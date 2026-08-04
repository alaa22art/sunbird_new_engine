define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('machineTypeSetupCtrl', [
        '$scope',
        'machineTypeSetupService',
        '$filter',
        '$rootScope',
        '$state',
        function (
            $scope,
            machineTypeSetupService,
            $filter,
            $rootScope,
            $state
        ) {

            $scope.metaData = {};
            $scope.lkps = [];
            $scope.createMode = true;
            $scope.selectedMachineType = {
                code: null,
                name: null,
                isActive: false,
                driver: null,
                lowLevelProtocolId: null,
                highLevelProtocolId: null
            }
            var driverList = [];
            var protocolList = [];

            $scope.refreshGrid = function () {
                $scope.machinesTypeGrid.dataSource.read();
                $scope.machinesTypeGrid.refresh();
            };
            $scope.clearFields = function () {
                $scope.machineTypeForm.$setPristine();
                $scope.machineTypeForm.$setUntouched();
                $scope.selectedMachineType = {
                    isActive: false,
                    name: null,
                    code: null,
                    driver: null,
                    lowLevelProtocolId: null,
                    highLevelProtocolId: null,
                }

                for (var i in $scope.lkps) {
                    var lkpValue = $scope.lkps[i];
                    lkpValue.selectedValue = null;
                }
            }

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        var map = {
                            "driver": "driver.rid",
                            "highLevelProtocolId": "highLevelProtocolId.rid",
                            "lowLevelProtocolId": "lowLevelProtocolId.rid"
                        }
                        e.data = util.createFilterablePageRequest($scope.machineTypeGridOptions.dataSource, map);

                        for (var i = 0; i < e.data.filters; i++) {

                            if (e.data.filters[i].field == "driver.rid" || e.data.filters[i].field == "highLevelProtocolId.rid" || e.data.filters[i].field == "lowLevelProtocolId.rid") {
                                e.data.filters[i].value = parseInt(e.data.filters[i].value);
                            }
                        }

                        machineTypeSetupService.getMachineTypePage(e).then(function (data) {

                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                schema: {
                    data: "data",
                    total: "total",
                    model: {
                        id: "rid",
                        fields: {
                            name: {
                                type: "string"
                            },
                            code: {
                                type: "string"
                            },
                            lowLevelProtocolId: {
                                type: "object"
                            },
                            highLevelProtocolId: {
                                type: "object"
                            },
                            driver: {
                                type: "object"
                            },
                            isActive: {
                                type: "boolean"
                            }
                        }
                    }
                }
            });


            $scope.foo = {
                isPortOpen: false
            };

            $scope.machineTypeGridOptions = {
                columns: [{
                        field: "name",
                        title: util.systemMessages.machineTypeGridName
                    },
                    {
                        field: "code",
                        title: util.systemMessages.code
                    },
                    {
                        field: "lowLevelProtocolId",
                        title: util.systemMessages.machineTypeGridlowLevelProtocol,
                        template: function (Data) {
                            if (Data.lowLevelProtocolId == null) {
                                return "";
                            } else {
                                return Data.lowLevelProtocolId.name[util.userLocale];
                            }
                        },
                        filterable: {
                            ui: lowLevelProtocolTypesFilter
                        }
                    },
                    {
                        field: "highLevelProtocolId",
                        title: util.systemMessages.machineGridTypehighLevelProtocol,
                        template: function (Data) {
                            if (Data.highLevelProtocolId == null) {
                                return "";
                            } else {
                                return Data.highLevelProtocolId.name[util.userLocale];
                            }
                        },
                        filterable: {
                            ui: highLevelProtocolTypesFilter
                        }
                    },
                    {
                        field: "driver",
                        title: util.systemMessages.driver,
                        template: function (Data) {
                            if (Data.driver == null) {
                                return "";
                            } else {
                                return Data.driver.name;
                            }
                        },
                        filterable: {
                            ui: driverTypesFilter
                        }
                    },
                    /* {
                         field: "isActive",
                         title: $filter('translate')('isActive'),
                         width: "10%",
                         template: function (Data) {
                             //$scope.foo.isPortOpen = Data.isPortOpen;
                             return "<md-input-container>" +
                                 "<md-switch class='md-primary' ng-model='" + Data.isActive + "' ng-disabled='true' aria-label='{{'isActive' | translate}}>" +
                                 "</md-switch>" +
                                 "</md-input-container>"
                         }

                     },*/
                    {
                        field: "tests",
                        title: util.systemMessages.testsDetails,
                        template: function (Data) {
                            return '<div class="mapIcon">' +
                                '<md-tooltip md-direction="top">{{"EditDetails" | translate}}</md-tooltip>' +
                                '<md-icon class="clickableCursor" md-font-icon="fas fa-cog" ng-click="storeMachineTypeId(' + Data.rid + ')" authority-checker = "VIEW_MACHINE_TYPE_TEST_MAPPING"></md-icon>' +
                                '</div>';
                        }
                    }
                ],
                dataSource: dataSource,
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

                    $scope.selectedMachineType = $scope.machinesTypeGrid.dataItem($scope.machinesTypeGrid.select());
                    machineTypeSetupService.getMachineTypeById($scope.selectedMachineType.rid).then(function (response) {
                        $scope.selectedMachineType = response.data;
                        for (var i in $scope.lkps) {
                            var lkpValue = $scope.lkps[i];
                            lkpValue.selectedValue = $scope.selectedMachineType[lkpValue.name];
                        }

                    });
                }
            };


            $scope.storeMachineTypeId = function (rid) {
                $rootScope.machineTypeId = rid;
                $state.go("test-mapping");
            };

            $scope.redirectToTestManagmentScreen = function () {
                $rootScope.machineTypeId = null;
                $state.go("test-mapping");
            };


            function driverTypesFilter(element) {
                element.kendoDropDownList({
                    dataTextField: "name",
                    dataValueField: "rid",
                    optionLabel: $filter('translate')('selectedDrive'),
                    dataSource: driverList,
                    autoBind: false
                });
            }

            function highLevelProtocolTypesFilter(element) {
                element.kendoDropDownList({
                    dataTextField: "name",
                    dataValueField: "rid",
                    optionLabel: $filter('translate')('selectedProtocol'),
                    dataSource: protocolList,
                    autoBind: false
                });
            }

            function lowLevelProtocolTypesFilter(element) {

                element.kendoDropDownList({
                    dataTextField: "name",
                    dataValueField: "rid",
                    optionLabel: $filter('translate')('selectedProtocol'),
                    dataSource: protocolList,
                    autoBind: false
                });
            }


            $scope.activateMachineType = function () {
                $scope.selectedMachineType.isActive = 1;
                machineTypeSetupService.updateMachineType($scope.selectedMachineType).then(function (response) {
                    util.createToast(util.systemMessages.updateInfo, "success");
                    $scope.refreshGrid();
                    $scope.selectedMachineType = null;
                });
            }


            $scope.deactivateMachineType = function () {
                $scope.selectedMachineType.isActive = 0;
                machineTypeSetupService.updateMachineType($scope.selectedMachineType).then(function (response) {
                    util.createToast(util.systemMessages.updateInfo, "success");
                    $scope.refreshGrid();
                    $scope.selectedMachineType = null;
                });
            }

        }
    ]);
});