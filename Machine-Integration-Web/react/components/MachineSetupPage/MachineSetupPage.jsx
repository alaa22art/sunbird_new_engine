import { useCallback, useEffect, useMemo, useState } from 'react';
import { AgGridReact } from 'ag-grid-react';
import { ModuleRegistry, ClientSideRowModelModule } from 'ag-grid-community';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import { apiRequest } from '../../lib/httpClient';
import { hasAnyAuthority } from '../../lib/authClient';
import { loadTranslator, DEFAULT_LOCALE } from '../../lib/i18n';
import { useServerGrid } from '../../lib/useServerGrid';
import './MachineSetupPage.css';

ModuleRegistry.registerModules([ClientSideRowModelModule]);

const PAGE_SIZE = 10;
const MACHINE_ASSIGN_TESTS_URL = '/miw/machine-assign-tests';
const HANDOFF_KEY = 'machineHandoff';

// React port of the AngularJS machineSetup module (component/machineSetup/*). Reachable
// at /miw/machine-setup -- see the 'machine-setup' state in routes.js.
//
// "Create" and the per-row "tests" gear icon both navigate to /miw/machine-assign-tests,
// which is NOT migrated yet and still reads its edit-vs-create machine from
// $rootScope.machine. Since this is a full page navigation (not an in-SPA ui-router
// transition), $rootScope doesn't survive it, so the selected row is handed off via
// sessionStorage instead -- see the matching read in machineAssignTestsController.js.
//
// Known gap: the original grid had a filterable dropdown on the machineType column;
// this port has server-side paging/sorting (see useServerGrid) but no column filter UI
// yet -- fine for now, worth adding if this table grows past a page or two in practice.
export default function MachineSetupPage() {
  const [translate, setTranslate] = useState(null);
  const [selectedMachine, setSelectedMachine] = useState(null);
  const [actionInFlight, setActionInFlight] = useState(false);

  useEffect(() => {
    loadTranslator(null, DEFAULT_LOCALE).then(setTranslate);
  }, []);

  const fetchPage = useCallback(
    (request) => apiRequest('getMachinePage.srvc', request),
    []
  );
  const { rows, total, page, setPage, loading, refresh, onSortChanged } = useServerGrid({
    fetchPage,
    pageSize: PAGE_SIZE,
  });

  const canCreate = hasAnyAuthority(['ADD_MACHINE_SETUP']);
  const canViewTests = hasAnyAuthority(['VIEW_MACHINE_TEST_MAPPING']);

  const columnDefs = useMemo(
    () => [
      { field: 'name', headerName: translate ? translate('MachineGridName') : 'name', sortable: true },
      {
        colId: 'machineType.name',
        headerName: translate ? translate('machineGridMachineType') : 'machineType',
        valueGetter: (params) => (params.data.machineType ? params.data.machineType.name : ''),
      },
      {
        field: 'machineIpAddress',
        headerName: translate ? translate('machineIP') : 'machineIpAddress',
        sortable: true,
      },
      {
        field: 'serverIpAddress',
        headerName: translate ? translate('machineGridServerIpAddress') : 'serverIpAddress',
        sortable: true,
      },
      {
        field: 'serverPort',
        headerName: translate ? translate('machineGridserverPort') : 'serverPort',
        sortable: true,
      },
      {
        colId: 'isPortOpen',
        headerName: translate ? translate('isPortOpen') : 'isPortOpen',
        cellRenderer: (params) => (
          <span className={params.data.isActive && params.data.isPortOpen ? 'port-status-active' : 'port-status-inactive'}>
            {params.data.isActive && params.data.isPortOpen ? '●' : '○'}
          </span>
        ),
      },
      canViewTests
        ? {
            colId: 'tests',
            headerName: translate ? translate('tests') : 'tests',
            cellRenderer: (params) => (
              <button
                type="button"
                className="machine-setup-icon-button"
                title={translate ? translate('EditDetails') : 'Edit details'}
                onClick={() => goToAssignTests(params.data)}
              >
                {'⚙'}
              </button>
            ),
          }
        : null,
    ].filter(Boolean),
    [translate, canViewTests]
  );

  function goToAssignTests(machine) {
    if (machine) {
      window.sessionStorage.setItem(HANDOFF_KEY, JSON.stringify(machine));
    } else {
      window.sessionStorage.removeItem(HANDOFF_KEY);
    }
    window.location.assign(MACHINE_ASSIGN_TESTS_URL);
  }

  async function handleSelectionChanged(event) {
    const selectedRows = event.api.getSelectedRows();
    if (selectedRows.length === 0) {
      setSelectedMachine(null);
      return;
    }
    const full = await apiRequest('getMachineById.srvc', selectedRows[0].rid);
    setSelectedMachine(full);
  }

  async function handleActivatePort() {
    setActionInFlight(true);
    try {
      await apiRequest('openConnection.srvc', selectedMachine);
      setSelectedMachine(null);
      refresh();
    } finally {
      setActionInFlight(false);
    }
  }

  async function handleDeactivatePort() {
    setActionInFlight(true);
    try {
      await apiRequest('closeConnection.srvc', selectedMachine);
      setSelectedMachine(null);
      refresh();
    } finally {
      setActionInFlight(false);
    }
  }

  // Same enable/disable rule as the original: with nothing selected both buttons stay
  // enabled (matching machine-setup-view.html's ng-disabled expressions verbatim).
  const startDisabled =
    !!selectedMachine && selectedMachine.rid != null && selectedMachine.isActive && selectedMachine.isPortOpen;
  const stopDisabled =
    !!selectedMachine && selectedMachine.rid != null && (!selectedMachine.isActive || !selectedMachine.isPortOpen);

  if (!translate) {
    return null;
  }

  return (
    <div className="machine-setup-wrapper">
      <div className="machine-setup-toolbar">
        {canCreate && (
          <button type="button" title={translate('create')} onClick={() => goToAssignTests(null)}>
            {translate('create')}
          </button>
        )}
        <button type="button" title={translate('refresh')} onClick={refresh}>
          {translate('refresh')}
        </button>
        <button
          type="button"
          title={translate('start')}
          disabled={startDisabled || actionInFlight}
          onClick={handleActivatePort}
        >
          {translate('start')}
        </button>
        <button
          type="button"
          title={translate('stop')}
          disabled={stopDisabled || actionInFlight}
          onClick={handleDeactivatePort}
        >
          {translate('stop')}
        </button>
      </div>

      <div className="ag-theme-quartz machine-setup-grid">
        <AgGridReact
          rowData={rows}
          columnDefs={columnDefs}
          rowSelection="single"
          getRowId={(params) => String(params.data.rid)}
          onSelectionChanged={handleSelectionChanged}
          onSortChanged={(e) => onSortChanged(e.api.getColumnState())}
          loading={loading}
          domLayout="autoHeight"
        />
      </div>

      <div className="machine-setup-pager">
        <button type="button" disabled={page === 0} onClick={() => setPage(page - 1)}>
          {'<'}
        </button>
        <span>
          {page + 1} / {Math.max(1, Math.ceil(total / PAGE_SIZE))}
        </span>
        <button type="button" disabled={(page + 1) * PAGE_SIZE >= total} onClick={() => setPage(page + 1)}>
          {'>'}
        </button>
      </div>
    </div>
  );
}
