define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('messegesTransactionLogCtrl', [
        '$scope',
        '$rootScope',
        'messegesTransactionLogService',
        '$filter',
        'branchFormService',
        function (
            $scope,
            $rootScope,
            messegesTransactionLogService,
            $filter,
            branchFormService
        ) {

            $rootScope.$broadcast('onApexModeChange', { type: "MACHINE", mode: "SINGLE" });

            $scope.selectedMachine = null;

            var fromDate = new Date();
            fromDate.setHours(0, 0, 0, 0);
            fromDate.setDate(fromDate.getDate() - 1);
            var toDate = new Date();
            toDate.setHours(23, 59, 59, 999);//last time in this date
            $scope.filters = {
                barcode: null,
                from: fromDate,
                to: toDate
            };

            $scope.branchLkp = {
                className: 'LabBranch',
                name: 'labBranch',
                labelText: 'branch',
                valueField: 'label',
                selectedValue: null,
                required: true,
                data: []
            };

            branchFormService.getBranchesWithAllOption({ filters: [] }).then(function (data) {
                $scope.branchLkp.selectedValue = data.find(function (obj) { return obj.rid === -1; });
                if ($scope.branchLkp.updateData) {
                    $scope.branchLkp.updateData(data);
                } else {
                    $scope.branchLkp.data = data;
                }
            });

            $scope.$on('onApexSelection', function (event, data) {
                $scope.selectedMachine = data[0];
                $scope.refreshGrid();
            });

            $scope.$on('onApexRemove', function (event, data) {
                $scope.selectedMachine = null;
                $scope.refreshGrid();
            });

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(dataSource);

                        //add form filters
                        for (var key in $scope.filters) {
                            var filterValue = $scope.filters[key];
                            if (filterValue) {
                                switch (key) {
                                    case "barcode":
                                        e.data.filters.push({
                                            field: key,
                                            value: filterValue,
                                            operator: "eq"
                                        });
                                        break;
                                    case "from":
                                        e.data.filters.push({
                                            field: "creationDate",
                                            value: filterValue,
                                            operator: "gte"
                                        });
                                        break;
                                    case "to":
                                        e.data.filters.push({
                                            field: "creationDate",
                                            value: filterValue,
                                            operator: "lte"
                                        });
                                        break;
                                }

                            }
                        }

                        if ($scope.selectedMachine) {
                            e.data.filters.push({
                                field: "machine.rid",
                                value: $scope.selectedMachine.rid,
                                operator: "eq"
                            });
                        }

                        if ($scope.branchLkp && $scope.branchLkp.selectedValue && $scope.branchLkp.selectedValue.rid !== -1) {
                            e.data.filters.push({
                                field: "branchId",
                                value: $scope.branchLkp.selectedValue.rid,
                                operator: "eq"
                            });
                        }

                        messegesTransactionLogService.getMessageTransactionPage(e.data).then(function (response) {
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
                            rid: { type: "number" },
                            barcode: { type: "string" },
                            messageType: { type: "object" },
                            creationDate: { type: "date" },
                            messageDirection: { type: "object" },
                            branch: { type: "object" },
                            notes: { type: "string" },
                            messageBody: { type: "string" },
                            status: { type: "boolean" },
                            machine: { type: "object" }
                        }
                    }
                }
            });

            $scope.gridOptions = {
                columns: [
                    {
                        field: "rid",
                        title: util.systemMessages.rid
                    },
                    {
                        field: "barcode",
                        title: util.systemMessages.barcode,
                        template: function (dataItem) {
                            return dataItem.barcode ? "<div><a href='' ng-click='goToOrder(" + dataItem.rid + ")'>" + dataItem.barcode + "</a></div>" : "";
                        }
                    },
                    {
                        field: "messageType",
                        title: util.systemMessages.type,
                        template: function (dataItem) {
                            var result = "";
                            if (dataItem.messageType) {
                                result = util.addParenthesis(dataItem.messageType.code,
                                    dataItem.messageType.name[util.userLocale])
                            }
                            return result;
                        }
                    },
                    {
                        field: "creationDate",
                        title: util.systemMessages.date,
                        template: function (dataItem) {
                            return $filter("dateTimeFormat")(dataItem.creationDate);
                        }
                    },
                    {
                        field: "messageDirection",
                        title: util.systemMessages.direction,
                        template: function (dataItem) {
                            var result = "";
                            if (dataItem.messageDirection) {
                                result = util.addParenthesis(dataItem.messageDirection.code,
                                    dataItem.messageDirection.name[util.userLocale]);
                            }
                            return result;
                        }
                    },
                    {
                        field: "branch",
                        title: util.systemMessages.branch,
                        template: function (dataItem) {
                            return dataItem.transients.branch ? dataItem.transients.branch.name[util.userLocale] : "";
                        }
                    },
                    {
                        field: "notes",
                        title: util.systemMessages.notes
                    },
                    {
                        field: "status",
                        title: util.systemMessages.status,
                        width: 100,
                        template: function (dataItem) {
                            var color = "";
                            if (dataItem.isProcessed) {
                                color = "warning-color";
                            } else {
                                if (dataItem.isSuccess) {
                                    color = "success-color";
                                } else {
                                    color = "error-color";
                                }
                            }

                            return "<div class='text-center'><md-icon class='" + color + "' md-font-icon='fas fa-circle'></md-icon></div>";
                        }
                    },
                    {
                        field: "messageBody",
                        title: util.systemMessages.data,
                        hidden: true
                    },
                    {
                        field: "machine",
                        title: util.systemMessages.machine,
                        hidden: true,
                        template: function (dataItem) {
                            return dataItem.machine ? dataItem.machine.name : "";
                        }
                    }
                ],
                autoBind: false,
                dataSource: dataSource
            };


            $scope.refreshGrid = function () {
                dataSource.read();
            };

            $scope.goToOrder = function (messageTransactionRid) {
                var data = dataSource.data().find(function (obj) { return obj.rid === messageTransactionRid; });
                $rootScope.$broadcast("onApexNavigateTo", {
                    path: "patient-sample-lookup",
                    params: { barcode: data.barcode, branchRid: data.branchId }
                });
            };

            //initial fetch
            angular.element(function () {
                $scope.refreshGrid();
            });

        }
    ]);
});
