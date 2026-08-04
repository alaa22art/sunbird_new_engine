define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('transactionLogCtrl', [
        '$scope',
        'transactionLogService',
        '$filter',
        function (
            $scope,
            transactionLogService,
            $filter
        ) {

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                sort: { field: "sentDate", dir: "desc" },
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.transactionGridOptions.dataSource);
                        transactionLogService.getEventLogPage(e).then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            text: { type: "string" },
                            sentDate: { type: "date" },
                            machine: { type: "object" }
                        }
                    }
                }
            });

            $scope.transactionGridOptions = {
                columns: [
                    {
                        field: "text",
                        title: util.systemMessages.transaction
                    },
                    {
                        field: "sentDate",
                        title: util.systemMessages.transactionDatetime,
                        width: 250,
                        template: function (data) {
                            return $filter("dateTimeFormat")(data.sentDate);
                        }
                    },
                    {
                        field: "machine",
                        title: util.systemMessages.MachineGridName,
                        width: 350,
                        template: function (data) {
                            return data.machine ? data.machine.name : "";
                        }
                    }],
                dataSource: dataSource
            };



            $scope.refreshGrid = function () {
                dataSource.read();
            };

        }
    ]);
});
