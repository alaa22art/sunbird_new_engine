define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('machineResultController', [
        '$scope',
        '$timeout',
        'machineResultsService',
        'machineSetupService',
        '$location',
        '$filter',
        'commonMethods',
        '$rootScope',
        "$mdToast",
        function (
            $scope,
            $timeout,
            machineResultsService,
            machineSetupService,
            $location,
            $filter,
            commonMethods,
            $rootScope,
            $mdToast
        ) {
            $scope.selectedMachineResult = null;
            $scope.metaData = {};
            $scope.machineLookup = null;



            commonMethods.retrieveMetaData("MachineResult").then(function (response) {

                $scope.metaData = response.data;
                // to get any lkp without the need to use an LoV directive
                machineSetupService.getMachineList().then(function (response) {
                    $scope.machineLookup = {
                        className: "Machine",
                        name: "machine",
                        labelText: $filter('translate')('machine'),
                        valueField: "name",
                        selectedValue: null,
                        required: $scope.metaData.machine.notNull,
                        data: response.data,
                        onChange: $scope.refreshGrid
                    };
                })
            }).catch(function (error) {

            });

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.MachineResultGridOptions.dataSource);
                        var obj = {
                            field: "machine.rid",
                            nullable: false,
                            operator: "eq",
                            value: $scope.machineLookup != null && $scope.machineLookup.selectedValue ? $scope.machineLookup.selectedValue.rid : null,
                            junctionOperator: "And"
                        };
                        if (obj.value) {
                            e.data.filters.push(obj);
                        }
                        machineResultsService.getMachineResultPage(e.data).then(function (response) {
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
                            sampleNo: {
                                type: "string"
                            },
                            machineName: {
                                type: "string"
                            },
                            testCode: {
                                type: "string",
                            },
                            dataOrMeasurementValue: {
                                type: "string",
                            },
                            units: {
                                type: "string",
                            },
                            isSentToLIS: {
                                type: "string",
                            }
                        }
                    }
                }

            });

            $scope.foo = {
                isSentToLIS: false
            };
            $scope.MachineResultGridOptions = {

                columns: [{
                        selectable: true,
                        width: "50px"
                    },
                    {
                        field: "sampleNo",
                        title: $filter('translate')('sampleNo')
                    },
                    {
                        field: "machineName",
                        title: $filter('translate')('machineName')

                    },
                    {
                        field: "testCode",
                        title: $filter('translate')('testCode')
                    },
                    {
                        field: "dataOrMeasurementValue",
                        title: $filter('translate')('dataOrMeasurementValue')
                    },
                    {
                        field: "units",
                        title: $filter('translate')('units')
                    },
                    {
                        field: "isSentToLIS",
                        title: $filter('translate')('isSentToLIS'),
                        width: "10%",
                        template: function (Data) {
                            return "<md-input-container>" +
                                "<md-switch class='md-primary' ng-model='" + Data.isSentToLIS + "' ng-disabled='true' aria-label='{{'isSentToLIS' | translate}}>" +
                                "</md-switch>" +
                                "</md-input-container>"
                        }
                    }

                ],
                dataSource: dataSource,
                autoBind: true,
                reorderable: true,
                groupable: false,
                persistSelection: true,
                selectable: false,
                change: onChange,
                filterable: {
                    extra: false,
                    operators: {
                        object: {
                            eq: "Equal",
                            neq: "Not Equal",
                        }
                    }
                },

            }



            $scope.resultsList = [];
            var selectedItem = [];
            $scope.finalSelectedItem = [];

            function onChange(e) {
                var rows = e.sender.select();
                selectedItem = this.selectedKeyNames()
                $scope.selectedMachine = $scope.machineResultsGrid.dataItem($scope.machineResultsGrid.select());
                if (selectedItem != "") {
                    rows.each(function (e) {
                        var grid = $("#machineResultsGrid").data("kendoGrid");
                        var dataItem = grid.dataItem(this);
                        $scope.resultsList.push(dataItem);
                        var findedItem = $filter('filter')($scope.finalSelectedItem, {
                            'id': dataItem.rid
                        })
                        if (findedItem.length == 0) {
                            $scope.finalSelectedItem.push(dataItem)
                        }
                    })
                }
            };

            $scope.resendLabResults = function () {
                var log = [];
                $scope.finalResultsList = [];
                angular.forEach(selectedItem, function (value, key) {
                    var findedSlectedItem = $filter('filter')($scope.finalSelectedItem, {
                        'id': value
                    })
                    if (findedSlectedItem.length > 0) {
                        $scope.finalResultsList.push(findedSlectedItem[0]);
                    }
                }, log);

                machineResultsService.resendLabResults($scope.finalResultsList).then(function successCallBack(response) {
                    util.createToast($mdToast, util.systemMessages.success, "success");
                    $scope.clearFields();
                    $scope.refreshGrid();
                }).catch(function () {

                });
            }

            function getDimensionsByFind(id) {
                return $scope.resultsList.find(function (x) {
                    return x.rid === id;
                });
            }

            $scope.refreshGrid = function () {
                $scope.machineResultsGrid.dataSource.read();
                $scope.machineResultsGrid.refresh();
            };
        }
    ]);
});