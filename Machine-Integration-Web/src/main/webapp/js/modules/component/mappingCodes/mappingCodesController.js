define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('mappingCodesCtrl', [
        '$scope',
        'mappingCodesService',
        'commonMethods',
        '$filter',
        'lovService',
        function (
            $scope,
            mappingCodesService,
            commonMethods,
            $filter,
            lovService
        ) {
            $scope.selectedTestChanged = false;
            $scope.selectedTest = null;

            $scope.refreshGrid = function () {
                $scope.testCatalogeGrid.dataSource.read();
                $scope.testCatalogeGrid.refresh();
            };

            /* $scope.saveTest = function () {
                 mappingCodesService.saveTest($scope.selectedMachine).then(function (response) {
                     util.createToast(util.systemMessages.success, "success");
                     $scope.selectedTestChanged = false;
                     $scope.selectedTest = null;
                     $scope.clearFields();
                     $scope.refreshGrid();
                 });
             }*/

            var SpecimenTypes = []

            lovService.getLkpByClass({ className: "LkpSpecimenType" }).then(function (data) {
                SpecimenTypes = data;
            });


            $scope.saveTest = function () {

                if ($scope.selectedTest.id == "") {
                    mappingCodesService.addMappingCodes($scope.selectedTest).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.selectedTestChanged = false;
                        $scope.selectedTest = null;
                        $scope.clearFields();
                        $scope.refreshGrid();
                    });
                } else {
                    mappingCodesService.updateMappingCodes($scope.selectedTest).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.selectedTestChanged = false;
                        $scope.selectedTest = null;
                        $scope.clearFields();
                        $scope.refreshGrid();
                    });

                }


            };

            function filterHandler(e, fieldNames) {
                var currentFilters = [];
                if (e.sender.dataSource.filter() != null) {
                    //get current filters
                    currentFilters = e.sender.dataSource.filter().filters;
                }
                // we filtered this col
                if (e.filter != null) {
                    var filteredCol = e.filter.filters[0];
                    if (fieldNames[filteredCol.field]) {
                        e.preventDefault();
                        for (var idx = currentFilters.length - 1; idx >= 0; idx--) {
                            if (currentFilters[idx].field.indexOf(filteredCol.field) != -1) {
                                currentFilters.splice(idx, 1);
                                break;
                            }
                        }
                        filteredCol.field = fieldNames[filteredCol.field];
                        currentFilters.push(filteredCol);
                        e.sender.dataSource.filter(currentFilters);
                    }
                } else if (e.sender.dataSource.filter() != null && fieldNames[e.field] && e.filter == null) {
                    // we have filters and we cleared the col
                    e.preventDefault();
                    for (var idx = currentFilters.length - 1; idx >= 0; idx--) {
                        if (currentFilters[idx].field.indexOf(fieldNames[e.field]) != -1) {
                            currentFilters.splice(idx, 1);
                            break;
                        }
                    }
                    e.sender.dataSource.filter(currentFilters);
                }

            }



            commonMethods.retrieveMetaData("TestCatalog").then(function (response) {
                $scope.metaData = response.data;
            });

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.testCatalogeGridOptions.dataSource);
                        mappingCodesService.getMappingCodesPage(e).then(function (data) {
                            e.success(data);
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
                                type: "string",
                                editable: true
                            },
                            requesterTestCode: {
                                type: "string",
                                editable: true
                            },
                            specimenType: {
                                type: "object",
                                editable: true
                            },
                            section: {
                                type: "string",
                                editable: true
                            }
                        }
                    }
                }
            });

            $scope.testCatalogeGridOptions = {
                columns: [{
                    field: "name",
                    title: "Item Name"
                },
                {
                    field: "requesterTestCode",
                    title: "Vista item Code"
                }, {
                    field: "section",
                    title: "Section"
                }, {
                    field: "certa_item_code",
                    title: "certa item code"
                },
                    // {
                    //     field: "specimenType",
                    //     filterable: {
                    //         ui: function (element) {
                    //             util.createListFilter(element, SpecimenTypes, "name");
                    //         }
                    //     },
                    //     editor: function (container, options) {
                    //         util.createListEditor(container, options, SpecimenTypes, "name");
                    //     },
                    //     template: function (data) {
                    //         if (data.specimenType != null) {
                    //             return data.specimenType.code
                    //         } else {
                    //             return "";
                    //         }

                    //     },
                    //     title: util.systemMessages.specimenType
                    // }
                ],
                autoBind: true,
                editable: "inline",
                dataSource: dataSource,
                reorderable: true,
                groupable: false,
                filter: function (e) {
                    var filterMap = {
                        "specimenType": "specimenType.rid"
                    };
                    filterHandler(e, filterMap);
                },
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
                    $scope.selectedTest = $scope.testCatalogeGrid.dataItem($scope.testCatalogeGrid.select());
                }
            };

            $scope.clearFields = function () {
                $scope.testCatlogeForm.$setPristine();
                $scope.testCatlogeForm.$setUntouched();
                $scope.selectedTest = null;
                $scope.selectedTestChanged = false;
            }

            $scope.addSelectedTest = function () {
                var newSelectedTest = {
                    name: "",
                    requesterTestCode: "",
                    id: null
                };
                dataSource.add(newSelectedTest);
                var data = dataSource.data();
                var dataItem = data[data.length - 1];
                $scope.editSelectedTest(dataItem);
            };

            $scope.editSelectedTest = function (dataItem) {
                $("#testCatalogeGrid").data("kendoGrid").editRow(dataItem);
                $("#testCatalogeGrid").data("kendoGrid").select("tr[data-uid=" + dataItem.uid + "]");
                $scope.selectedTestChanged = true;
            };

            $scope.saveSelectedTest = function () {
                $scope.saveTest($scope.selectedTest).then(function (response) {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.clearFields();
                    $scope.refreshGrid();
                });
            }

            $scope.deleteTest = function () {
                if ($scope.selectedTest.rid != null) {
                    mappingCodesService.getMappingCodesById($scope.selectedTest.rid).then(function (responce) {
                        $scope.selectedTest = responce.data;
                        mappingCodesService.deleteAllTest($scope.selectedTest).then(function (responce) {
                            util.createToast(util.systemMessages.success, "success")
                            $scope.clearFields();
                            $scope.refreshGrid();
                        });
                    });
                }
            }

        }
    ]);
});