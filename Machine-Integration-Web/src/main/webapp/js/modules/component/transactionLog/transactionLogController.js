define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('transactionLogCtrl', [
        '$scope',
        'transactionLogService',
        'commonMethods',
        '$filter',
        function (
            $scope,
            transactionLogService,
            commonMethods,
            $filter
        ) {
            $scope.selectedTestChanged = false;
            $scope.selectedTest = null;

            $scope.refreshGrid = function () {
                $scope.transactionLogGrid.dataSource.read();
                $scope.transactionLogGrid.refresh();
            };
            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                sort: { field: "sentDate", dir: "desc" },
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.transactionGridOptions.dataSource);
                        transactionLogService.getEventLogPage(e).then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    data: "data",
                    total: "total",
                    model: {
                        id: "rid",
                        fields: {
                            text: {
                                type: "string",
                                editable: true
                            },
                            sentDate: {
                                type: "string",
                                editable: true
                            },
                            machine: {
                                type: "object",
                                editable: true
                            }
                        }
                    }
                }
            });

            $scope.transactionGridOptions = {
                columns: [{
                    field: "text",
                    title: util.systemMessages.transaction
                },
                {
                    field: "sentDate",
                    title: util.systemMessages.transactionDatetime,
                    template: function (data) {
                        return $filter("dateTimeFormat")(data.sentDate);
                    }
                },
                {
                    field: "machine",
                    title: util.systemMessages.MachineGridName,
                    template: function (data) {
                        if (data.machine != null) {
                            return data.machine.name;
                        }
                        else {
                            return "";
                        }
                    }
                }
                ],
                autoBind: true,
                editable: "inline",
                dataSource: dataSource,
                reorderable: true,
                groupable: false,
                filterable: {
                    extra: false,
                    operators: {
                        object: {
                            eq: "Equal",
                            neq: "Not Equal",
                        }
                    }
                },
                change: function () {
                    $scope.selectedTest = $scope.transactionLogGrid.dataItem($scope.transactionLogGrid.select());
                }
            };
        }
    ]);
});
