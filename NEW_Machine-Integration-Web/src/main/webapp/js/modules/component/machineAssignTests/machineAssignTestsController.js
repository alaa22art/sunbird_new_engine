define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('machineAssignTestsController', ['$scope', 'machineAssignTestsService', 'machineSetupService', 'testMappingService', '$filter', 'commonMethods', '$rootScope', "$mdDialog",
        function ($scope, machineAssignTestsService, machineSetupService, testMappingService, $filter, commonMethods, $rootScope, $mdDialog) {

            $scope.metaData = {};
            $scope.lkps = [];
            $scope.createMode = true;
            $scope.imageName = "no_machine";


            var machineTypes = [];


            $scope.branch = {
                className: "Branch",
                name: "branch",
                labelText: $filter('translate')('Branch'),
                valueField: "name",
                selectedValue: null,
                required: false,
                data: null
            }

            $scope.selectedMachine = null;
            $scope.selectedMachineType = null;

            if ($rootScope.machine == null) {

                $scope.selectedMachine = {
                    rid: null,
                    code: null,
                    name: null,
                    isActive: false,
                    isReceiveStateActive: false,
                    isSendStateActive: false,
                    isPortOpen: false,
                    machineType: null,
                    connected: false,
                    isPanelOrder: false
                };

            } else {
                $scope.selectedMachine = $rootScope.machine;
                $scope.selectedMachineType = $scope.selectedMachine.machineType
            }


            $scope.selectedTest = null;


            if ($scope.selectedMachine != null && $scope.selectedMachine.rid != null) {
                getMachineById($scope.selectedMachine.rid);
            }

            function getMachineById(rid) {
                machineSetupService.getMachineById(rid).then(function (response) {
                    $scope.selectedMachine = response.data;
                    $scope.imageName = response.data.machineType.name;
                    $scope.refreshGrid();
                });
            }
            commonMethods.retrieveMetaData("MachineTest").then(function (response) {

                $scope.metaData = response.data;

                machineSetupService.getMachineTypeList().then(function (response) {
                    machineTypes = response.data;


                    $scope.lkps.push({
                        className: "Machine",
                        name: "machineType",
                        labelText: $filter('translate')('machineType'),
                        valueField: "name", //util.userLocale,
                        selectedValue: $scope.selectedMachineType,
                        required: $scope.metaData.machine.notNull,
                        data: machineTypes
                    });

                }).catch(function (error) {

                });

            });

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {

                        if ($scope.selectedMachine == null || $scope.selectedMachine.rid == null) {
                            e.success([]);
                            return;
                        }

                        var filterMap = {
                            "name": "testId.testName"
                        }
                        e.data = util.createFilterablePageRequest($scope.TestCatalogGridOptions.dataSource, filterMap);
                        var obj = {
                            field: "machine.rid",
                            nullable: false,
                            operator: "eq",
                            value: $scope.selectedMachine.rid,
                            junctionOperator: "And"
                        };


                        e.data.filters.push(obj);

                        machineAssignTestsService.getMachineTestsPage(e).then(function (data) {
                            console.log(data);

                        }).catch(function (error) {
                            e.error(error);
                        });
                    },

                    create:

                        function (e) {

                            machineAssignTestsService.insertMachineTest(e.data).then(function successCallBack(response) {
                                util.createToast(util.systemMessages.success, "success");
                                $scope.refreshGrid();
                            }).catch(function (error) {
                                e.error(error);
                            });

                        },
                    update: function (e) {

                        machineAssignTestsService.updateMachineTest(e.data).then(function successCallBack(response) {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    destroy: function (e) {
                        machineAssignTestsService.deleteMachineTest(e.data).then(function successCallBack(response) {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
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
                            hostCode: {
                                type: "string"
                            },
                            "testCatalog": {
                                type: "object"
                            },
                            isActive: {
                                type: "boolean",


                            },
                            resultCode: {
                                type: "string",
                                editable: true

                            },
                            "testCatalog.requesterTestCode": {
                                type: "string",
                                editable: false
                            }


                        }
                    }
                }
            });


            $scope.TestCatalogGridOptions = {
                columns: [

                    {
                        title: "Status",
                        field: "isActive",
                        title: $filter('translate')('isActive'),
                        width: "8%",
                        template: function (Data) {

                            if (Data.isActive) {

                                return '<div class="mapGridIcon"><md-icon md-font-icon="fas fa-check-circle" class="mapActive"></md-icon></div>';
                            } else {
                                return '<div class="mapGridIcon"><md-icon md-font-icon="fas fa-times-circle" class="mapInactive"></md-icon></div>';
                            }

                        }
                    },
                    {

                        field: "testCatalog",
                        editor: machineTypeTestsDropDownList,
                        template: function (Data) {
                            var s = Data.testCatalog != null ? Data.testCatalog.name : "";
                            return s;
                        },
                        title: $filter('translate')('Test'),
                        width: "50%"

                    },
                    {

                        field: "hostCode",
                        title: $filter('translate')('HostCode'),
                        width: "10%"

                    },
                    {

                        field: "testCatalog.requesterTestCode",

                        template: function (Data) {
                            var s = Data.testCatalog != null ? Data.testCatalog.requesterTestCode : "";
                            return s;
                        },
                        title: $filter('translate')('External_Tests_Code'),
                        width: "20%"


                    },
                    {
                        field: "resultCode",
                        title: $filter('translate')('resultCode'),
                        width: "10%"
                    }

                ],
                editable: "inline",
                dataSource: dataSource,
                autoBind: false,
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
                    $scope.selectedTest = $scope.TestCatalogGrid.dataItem($scope.TestCatalogGrid.select());
                }


            }


            var allTestCatalogs = [];

            function machineTypeTestsDropDownList(container, options) {
                $('<input required name="' + options.field + '"/>')
                    .appendTo(container)
                    .kendoDropDownList({
                        dataValueField: "rid",
                        valueTemplate: function (dataItem) {
                            return dataItem.name;
                        },
                        template: function (dataItem) {
                            return dataItem.name;
                        },
                        filter: "contains",
                        dataSource: {
                            serverFiltering: true,
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {

                                    var searchValue = "";
                                    if (e.data.filter) {
                                        var filters = e.data.filter.filters;
                                        for (var filterKey in filters) {
                                            // get the searched value in the drop down list
                                            if (filters[filterKey].operator == "contains") {
                                                searchValue = filters[filterKey].value;
                                            }
                                        }
                                    }
                                    var map = {
                                        searchValue: searchValue,
                                        machineTypeId: $scope.selectedMachine.machineType.rid.toString()
                                    };
                                    testMappingService.getTestCatalogListByMachineType(map).then(function (response) {
                                        if (searchValue == "") {
                                            allTestCatalogs = response.data;
                                        }
                                        e.success(response.data);
                                    }).catch(function (error) {});

                                }
                            }
                        },
                        dataBound: function (e) {
                            var selectedIndex = e.sender.select();
                            e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                            e.sender.trigger("change");
                        }
                    });
            }




            $scope.refreshGrid = function () {
                $scope.TestCatalogGrid.dataSource.read();
                $scope.TestCatalogGrid.refresh();
                $scope.selectedTest = null;
            };


            $scope.saveSelectedTest = function () {
                dataSource.sync();
                $scope.selectedTest = null;
                $scope.selectedTestChanged = false;
            };

            $scope.activeAllTests = function () {

                machineAssignTestsService.activeAllTests($scope.selectedMachine).then(function (response) {
                    $scope.refreshGrid();
                    util.createToast(util.systemMessages.success, "success");
                }).catch(function () {


                });

            };

            $scope.deactivateAllTests = function () {

                machineAssignTestsService.deactivateAllTests($scope.selectedMachine).then(function (response) {
                    $scope.refreshGrid();
                    util.createToast(util.systemMessages.success, "success");
                }).catch(function () {


                });

            };

            $scope.addSelectedTest = function () {
                var newSelectedTest = {
                    description: "",
                    name: "",
                    testId: "",
                    machine: $scope.selectedMachine,
                };
                dataSource.add(newSelectedTest);
                var data = dataSource.data();
                var dataItem = data[data.length - 1];
                $scope.editSelectedTest(dataItem);
            };



            $scope.editSelectedTest = function (dataItem) {
                $("#TestCatalogGrid").data("kendoGrid").editRow(dataItem);
                $("#TestCatalogGrid").data("kendoGrid").select("tr[data-uid=" + dataItem.uid + "]");
                $scope.selectedTestChanged = true;

            };

            $scope.deleteSelectedTest = function () {
                dataSource.remove($scope.selectedTest);
                dataSource.sync();
                $scope.selectedTest = null;
            };

            $scope.cancelSelectedTests = function () {
                dataSource.cancelChanges();
                $scope.selectedTestChanged = false;
                $scope.selectedTest = null;
            };

            $scope.back = function () {
                window.history.back();
            };


            $scope.clearFields = function () {

                $scope.selectedMachine = {
                    rid: null,
                    code: null,
                    name: null,
                    isActive: false,
                    isReceiveStateActive: false,
                    isSendStateActive: false,
                    isPortOpen: false,
                    machineType: null,
                    connected: false,
                    isPanelOrder: false
                };
                $scope.machineAssignTest.$setPristine();
                $scope.machineAssignTest.$setUntouched();
                $scope.imageName = 'no_machine';
                $rootScope.machine = null;
                $scope.refreshGrid();
            };



            $scope.saveMachine = function () {
                if ($scope.selectedMachine.rid == null) {
                    machineSetupService.createMachine($scope.selectedMachine).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        getMachineById(response.data.rid);
                    });
                } else {
                    machineSetupService.updateMachine($scope.selectedMachine).then(function (response) {
                        util.createToast(util.systemMessages.updateInfo, "success");
                        getMachineById(response.data.rid);
                    });
                }
            };



            $scope.showManageTypeTests = function (ev) {
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog", "machine", "imageName", function ($scope, $mdDialog, machine, imageName) {
                        $scope.cancel = function () {
                            $mdDialog.cancel();
                        };

                        $scope.machine = machine;
                        $scope.imageName = imageName;

                        $scope.submit = function (invalid) {
                            if (invalid) {
                                return;
                            }



                            /* loginService.forgotPassword($scope.username).then(function () {
                                 util.createToast(util.systemMessages.success, "success");
                             });*/
                        };
                    }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/machineTypeTests.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    locals: {
                        machine: $scope.selectedMachine,
                        imageName: $scope.selectedMachine.machineType.name

                    }
                }).then(function () {}, function () {});
            };
        }
    ]);
});