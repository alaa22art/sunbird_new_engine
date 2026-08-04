define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('mappingDriverTestCtrl', [
        '$scope',
        'driverAssaysService',
        'testCatalogService',
        'testPanelService',
        '$rootScope',
        function (
            $scope,
            driverAssaysService,
            testCatalogService,
            testPanelService,
            $rootScope
        ) {

            $rootScope.$broadcast("onApexModeChange", { type: "MACHINE_TYPE", mode: "SINGLE" });

            var pageSize = 30;
            $scope.selectedTabIndex = 0;

            $scope.currentMachineTypeTestFPR = util.generateFilterablePageRequest();
            $scope.currentMachineTypeTestFPR.size = pageSize;
            $scope.machineTypeTestPage = { content: [], totalPages: 1000 };//dummy totalPages
            $scope.selectedMachineTypeTest = null;
            $scope.machineTypeTestMapping = "UNMAPPED";//UNMAPPED,MAPPED
            $scope.selectedMachineType = null;

            $scope.currentTestCatalogFPR = util.generateFilterablePageRequest();
            $scope.currentTestCatalogFPR.size = pageSize;
            $scope.testCatalogPage = { content: [], totalPages: 1000 };//dummy totalPages
            $scope.selectedTestCatalog = null;

            $scope.currentTestPanelFPR = util.generateFilterablePageRequest();
            $scope.currentTestPanelFPR.size = pageSize;
            $scope.testPanelPage = { content: [], totalPages: 1000 };//dummy totalPages
            $scope.selectedTestPanel = null;

            $scope.machineTypeTestSearchOptions = {
                service: function (fpr) {
                    addMappingFilter(fpr);
                    addMachineTypeFilter(fpr);
                    return driverAssaysService.getMachineTypeTestPage(fpr).then(function (response) {
                        for (var key in response.data.content) {
                            var obj = response.data.content[key];
                            obj.customLabel = util.addParenthesis(obj.machineType.name, obj.name);
                        }
                        return response;
                    })
                },
                callback: function (filters) {
                    if ($scope.machineTypeTestSearchOptions.selectedItem && $scope.machineTypeTestSearchOptions.selectedItem.rid !== -1) {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = 1;
                        fpr.filters.push({
                            field: "rid",
                            value: $scope.machineTypeTestSearchOptions.selectedItem.rid,
                            operator: "eq"
                        });
                        $scope.machineTypeTestPage.content = [];
                        $scope.getMachineTypeTestPageData(fpr);
                    } else if (filters && filters.length > 0) {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = pageSize;
                        fpr.filters = fpr.filters.concat(filters);
                        $scope.machineTypeTestPage.content = [];
                        $scope.getMachineTypeTestPageData(fpr);
                    }
                },
                skeleton: {
                    code: "customLabel",
                    description: "customLabel"
                },
                filterList: ["name", "description"],
                sortList: [{ property: "name", direction: "ASC" }]
            };
            $scope.testCatalogSearchOptions = {
                service: testCatalogService.getTestCatalogPage,
                callback: function (filters) {
                    if ($scope.testCatalogSearchOptions.selectedItem && $scope.testCatalogSearchOptions.selectedItem.rid !== -1) {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = 1;
                        fpr.filters.push({
                            field: "rid",
                            value: $scope.testCatalogSearchOptions.selectedItem.rid,
                            operator: "eq"
                        });
                        $scope.testCatalogPage.content = [];
                        $scope.getTestCatalogPageData(fpr);
                    } else {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = pageSize;
                        fpr.filters = fpr.filters.concat(filters);
                        $scope.testCatalogPage.content = [];
                        $scope.getTestCatalogPageData(fpr);
                    }
                },
                skeleton: {
                    code: "requesterTestCode",
                    description: "name"
                },
                filterList: ["requesterTestCode", "name"],
                sortList: [{ property: "requesterTestCode", direction: "ASC" }]
            };
            $scope.testPanelSearchOptions = {
                service: function (fpr) {
                    return testPanelService.getPanelPage(fpr).then(function (response) {
                        for (var key in response.data.content) {
                            var obj = response.data.content[key];
                            obj["label"] = util.addParenthesis(obj.externalCode, obj.name);
                        }
                        return response;
                    });
                },
                callback: function (filters) {
                    if ($scope.testPanelSearchOptions.selectedItem && $scope.testPanelSearchOptions.selectedItem.rid !== -1) {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = 1;
                        fpr.filters.push({
                            field: "rid",
                            value: $scope.testPanelSearchOptions.selectedItem.rid,
                            operator: "eq"
                        });
                        $scope.testPanelPage.content = [];
                        $scope.getTestPanelPageData(fpr);
                    } else {
                        var fpr = util.generateFilterablePageRequest();
                        fpr.page = 0;
                        fpr.size = pageSize;
                        fpr.filters = fpr.filters.concat(filters);
                        $scope.testPanelPage.content = [];
                        $scope.getTestPanelPageData(fpr);
                    }
                },
                skeleton: {
                    code: "label",
                    description: "label"
                },
                filterList: ["externalCode", "name"],
                sortList: [{ property: "externalCode", direction: "ASC" }, { property: "name", direction: "ASC" }]
            };

            $scope.$on("onApexSelection", function (event, data) {
                $scope.selectedMachineType = data[0];
                refreshMachineTypeTestPageData();
            });

            $scope.$on("onApexRemove", function (event, data) {
                $scope.selectedMachineType = null;
                refreshMachineTypeTestPageData();
            });

            $scope.onTabSelectionChange = function (type) {
                $scope.selectedTestCatalog = null;
                $scope.selectedTestPanel = null;
            };

            $scope.setMachineTypeTestMapping = function (isMapping) {
                var wrapper = {
                    machineTypeTestRid: $scope.selectedMachineTypeTest.rid,
                    testCatalogRid: isMapping ? $scope.selectedTestCatalog.rid : null
                };

                driverAssaysService.setMachineTypeTestMapping(wrapper).then(function () {
                    util.createToast(util.systemMessages.successTransaction, "success");
                    refreshMachineTypeTestPageData();
                });
            };

            $scope.onMachineTypeTestMappingChange = function () {
                refreshMachineTypeTestPageData();
            };

            function refreshMachineTypeTestPageData() {
                if ($scope.machineTypeTestSearchOptions.reset) {
                    $scope.machineTypeTestSearchOptions.reset();
                }
                $scope.selectedMachineTypeTest = null;
                $scope.machineTypeTestPage.content = [];
                var fpr = util.generateFilterablePageRequest();
                fpr.size = pageSize;
                $scope.getMachineTypeTestPageData(fpr);
            };

            $scope.onMachineTypeTestScroll = function () {
                $scope.currentMachineTypeTestFPR.page = $scope.machineTypeTestPage.number + 1;
                return $scope.getMachineTypeTestPageData($scope.currentMachineTypeTestFPR);
            };

            function addMappingFilter(fpr) {
                var operator = $scope.machineTypeTestMapping === "UNMAPPED" ? "isnull" : "isnotnull";
                util.addOrReplaceToFilters(fpr.filters, { field: "testCatalog", value: null, operator: operator });
            }

            function addMachineTypeFilter(fpr) {
                if (!$scope.selectedMachineType) {
                    return;
                }
                util.addOrReplaceToFilters(fpr.filters, { field: "machineType.rid", value: $scope.selectedMachineType.rid, operator: "eq" });
            }

            $scope.getMachineTypeTestPageData = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.machineTypeTestPage.totalPages) {
                    return;
                }
                addMappingFilter(filterablePageRequest);
                addMachineTypeFilter(filterablePageRequest);
                $scope.currentMachineTypeTestFPR = angular.copy(filterablePageRequest);
                return driverAssaysService.getMachineTypeTestPage(filterablePageRequest).then(function (response) {
                    response.data.content = $scope.machineTypeTestPage.content.concat(response.data.content);
                    $scope.machineTypeTestPage = response.data;
                });
            };

            $scope.onMachineTypeTestClick = function (machineTypeTest) {
                $scope.selectedMachineTypeTest = machineTypeTest;
            };


            $scope.onTestCatalogScroll = function () {
                $scope.currentTestCatalogFPR.page = $scope.testCatalogPage.number + 1;
                return $scope.getTestCatalogPageData($scope.currentTestCatalogFPR);
            };
            $scope.getTestCatalogPageData = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.testCatalogPage.totalPages) {
                    return;
                }
                $scope.currentTestCatalogFPR = angular.copy(filterablePageRequest);
                return testCatalogService.getTestCatalogPage(filterablePageRequest).then(function (response) {
                    response.data.content = $scope.testCatalogPage.content.concat(response.data.content);
                    $scope.testCatalogPage = response.data;
                });
            };

            $scope.onTestCatalogClick = function (testCatalog) {
                $scope.selectedTestCatalog = testCatalog;
                //apply only if test tab is visible
                if ($scope.selectedTabIndex === 0) {
                    $scope.selectedTestPanel = null;
                }
            };

            $scope.onTestPanelScroll = function () {
                $scope.currentTestPanelFPR.page = $scope.testPanelPage.number + 1;
                return $scope.getTestPanelPageData($scope.currentTestPanelFPR);
            };
            $scope.getTestPanelPageData = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.testPanelPage.totalPages) {
                    return;
                }
                $scope.currentTestPanelFPR = angular.copy(filterablePageRequest);
                return testPanelService.getPanelPage(filterablePageRequest).then(function (response) {
                    for (var key in response.data.content) {
                        var obj = response.data.content[key];
                        //var keeps reference to the last obj so every function will have the same obj,
                        //so this code to fix it
                        (function (data) {
                            data["accordionOptions"] = {
                                onExpand: function () { $scope.onTestPanelClick(data); }
                            };
                        })(obj);

                    }
                    response.data.content = $scope.testPanelPage.content.concat(response.data.content);
                    $scope.testPanelPage = response.data;
                });
            };

            $scope.onTestPanelClick = function (testPanel) {
                $scope.selectedTestPanel = testPanel;
                if (util.isArrayEmpty($scope.selectedTestPanel.tests)) {
                    var fpr = { filters: [{ field: "panel.rid", value: $scope.selectedTestPanel.rid, operator: "eq" }] };
                    testCatalogService.getTestCatalogList(fpr).then(function (response) {
                        $scope.selectedTestPanel["tests"] = response.data;
                    });
                }

            };


            //initial fetch for driver assays
            angular.element(function () {
                refreshMachineTypeTestPageData();

                var fpr2 = util.generateFilterablePageRequest();
                fpr2.size = pageSize;
                $scope.getTestCatalogPageData(fpr2);

                var fpr3 = util.generateFilterablePageRequest();
                fpr3.size = pageSize;
                $scope.getTestPanelPageData(fpr3);

            });

        }
    ]);
});