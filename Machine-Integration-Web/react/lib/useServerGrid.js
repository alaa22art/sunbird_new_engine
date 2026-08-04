import { useCallback, useEffect, useState } from 'react';

// AG Grid Community doesn't include the server-side row model (that's an Enterprise
// feature), so paging/sorting here is done by hand: each state change re-POSTs a page
// request and the grid just renders whatever rows come back. Request/response shape
// matches util.js#createFilterablePageRequest, which every *Page.srvc endpoint in this
// app already speaks:
//   request:  { filters: [{field, operator, value, junctionOperator}], page, size, sortList: [{direction, property}] }
//   response: { data: [...], total }
//
// `fetchPage` is `(request) => Promise<{data, total}>`. Filtering isn't wired up yet
// (no column filter UI in the pilot module that introduced this hook) -- `filters`
// is always sent empty for now.
export function useServerGrid({ fetchPage, pageSize }) {
  const [page, setPage] = useState(0);
  const [sortList, setSortList] = useState([]);
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  const refresh = useCallback(() => {
    setLoading(true);
    return fetchPage({ filters: [], page, size: pageSize, sortList })
      .then((result) => {
        setRows(result.data || []);
        setTotal(result.total || 0);
      })
      .finally(() => setLoading(false));
  }, [fetchPage, page, pageSize, sortList]);

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, pageSize, sortList]);

  function onSortChanged(columnState) {
    setSortList(
      columnState
        .filter((col) => col.sort)
        .map((col) => ({ direction: col.sort.toUpperCase(), property: col.colId }))
    );
    setPage(0);
  }

  return { rows, total, page, setPage, loading, onSortChanged, refresh };
}
