define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    /**
     * A- Options:
     *      1-filters: to add any custom filters when fetching test catalogs [Optional]
     *      2-panelFilters: to add any custom filter for panel lov, if this is set then we hide the lov [Optional]
     */
    app.directive('testCatalog', function () {
        return {
            restrict: 'E',
            replace: false,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testCatalog/test-catalog.html",
            scope: {
                options: "=options"
            },
            controller: [
                '$scope',
                'testCatalogService',
                'commonMethods',
                '$q',
                'lovService',
                'testPanelService',
                function (
                    $scope,
                    testCatalogService,
                    commonMethods,
                    $q,
                    lovService,
                    testPanelService
                ) {

                    var pageSize = 30;//must be high number
                    $scope.metaData = null;
                    $scope.testCatalogFilterablePageRequest = generateFPR();
                    $scope.testCatalogFilterablePageRequest.size = pageSize;
                    $scope.testCatalogPage = { content: [], totalPages: 1000 };//dummy totalPages
                    $scope.specimenTypeLov = null;
                    $scope.panelLov = null;
                    $scope.testCatalogForm = null;
                    $scope.selectedTestCatalog = {
                        requesterTestCode: "",
                        name: "",
                        isActive: true
                    };
                    $scope.letterPickerOptions = {
                        onClick: function (letter) {
                            $scope.testCatalogPage.content = [];
                            if ($scope.autoSearchOptions.reset) {
                                $scope.autoSearchOptions.reset();
                            }
                            var testCatalogFilter = generateFPR();
                            testCatalogFilter.size = pageSize;
                            if (letter) {
                                testCatalogFilter.filters.push({
                                    field: "requesterTestCode",
                                    value: letter,
                                    operator: "startswith"
                                });
                            }
                            $scope.getTestCatalogPage(testCatalogFilter);
                        }
                    };
                    $scope.autoSearchOptions = {
                        service: function (filterablePageRequest) {
                            if ($scope.options && $scope.options.filters) {
                                filterablePageRequest.filters = filterablePageRequest.filters.concat($scope.options.filters);
                            }
                            return testCatalogService.getTestCatalogPage(filterablePageRequest).then(function (response) {
                                return response;
                            });
                        },
                        callback: function (filters) {
                            if ($scope.autoSearchOptions.selectedItem && $scope.autoSearchOptions.selectedItem.rid !== -1) {
                                var testCatalogFilter = generateFPR();
                                testCatalogFilter.page = 0;
                                testCatalogFilter.size = 1;
                                testCatalogFilter.filters.push({
                                    field: "rid",
                                    value: $scope.autoSearchOptions.selectedItem.rid,
                                    operator: "eq"
                                });
                                $scope.testCatalogPage.content = [];
                                $scope.letterPickerOptions.reset();
                                $scope.getTestCatalogPage(testCatalogFilter);
                            } else if (filters) {
                                var testCatalogFilter = generateFPR();
                                testCatalogFilter.page = 0;
                                testCatalogFilter.size = pageSize;
                                testCatalogFilter.filters = testCatalogFilter.filters.concat(filters);
                                $scope.testCatalogPage.content = [];
                                $scope.letterPickerOptions.reset();
                                $scope.getTestCatalogPage(testCatalogFilter);
                            }
                        },
                        skeleton: {
                            code: "requesterTestCode",
                            description: "name"
                        },
                        filterList: ["requesterTestCode", "name"],
                        sortList: [{ property: "requesterTestCode", direction: "ASC" }]
                    };
                    function generateFPR() {
                        //generate FilterablePageRequest and to do any custom behaviour
                        var fpr = util.generateFilterablePageRequest();
                        if ($scope.options && $scope.options.filters) {
                            fpr.filters = fpr.filters.concat($scope.options.filters);
                        }
                        return fpr;
                    }

                    $q.all([commonMethods.retrieveMetaData("TestCatalog"),
                    lovService.getLkpByClass({ className: "LkpSpecimenType" })]).then(function (response) {
                        $scope.metaData = response[0].data;
                        $scope.specimenTypeLov = {
                            className: "LkpSpecimenType",
                            name: $scope.metaData.specimenType.name,
                            labelText: "specimenType",
                            valueField: "name." + util.userLocale,
                            selectedValue: null,
                            required: $scope.metaData.specimenType.notNull,
                            data: response[1]
                        };
                        // $scope.panelLov = {
                        //     className: "Panel",
                        //     name: $scope.metaData.panel.name,
                        //     labelText: "panel",
                        //     valueField: "name",
                        //     selectedValue: null,
                        //     required: $scope.metaData.panel.notNull,
                        //     data: []
                        // };
                        getPanels();
                    });


                    function getPanels() {
                        return;
                        var panelLovFilters = [];
                        //add custom panel filter
                        if ($scope.options && $scope.options.panelFilters) {
                            panelLovFilters = $scope.options.panelFilters;
                        }
                        testPanelService.getPanelList({ filters: panelLovFilters }).then(function (response) {
                            if ($scope.panelLov.updateData) {//sometimes we call this before lov render
                                $scope.panelLov.updateData(response.data);
                            } else {
                                $scope.panelLov.data = response.data
                            }
                            //since we are hiding the lov then set the selected value
                            if ($scope.options && $scope.options.panelFilters) {
                                $scope.panelLov.selectedValue = response.data[0];
                            }

                        });
                    }

                    $scope.onTestCatalogScroll = function () {
                        $scope.testCatalogFilterablePageRequest.page = $scope.testCatalogPage.number + 1;
                        return $scope.getTestCatalogPage($scope.testCatalogFilterablePageRequest);
                    };

                    $scope.getTestCatalogPage = function (filterablePageRequest) {
                        //exceeded the limit
                        if (filterablePageRequest.page > $scope.testCatalogPage.totalPages) {
                            return;
                        }
                        $scope.testCatalogFilterablePageRequest = angular.copy(filterablePageRequest);
                        return testCatalogService.getTestCatalogPage(filterablePageRequest).then(function (response) {
                            response.data.content = $scope.testCatalogPage.content.concat(response.data.content);
                            $scope.testCatalogPage = response.data;
                        });
                    };


                    $scope.submit = function () {
                        $scope.selectedTestCatalog.specimenType = $scope.specimenTypeLov.selectedValue;
                        //$scope.selectedTestCatalog.panel = $scope.panelLov.selectedValue;

                        var apiRequest = null;
                        //pick the api to use
                        if ($scope.selectedTestCatalog.rid) {
                            var apiRequest = testCatalogService.updateTestCatalog;
                        } else {
                            var apiRequest = testCatalogService.createTestCatalog;
                        }
                        apiRequest($scope.selectedTestCatalog).then(function (response) {
                            util.createToast(util.systemMessages.successTransaction, "success");
                            replaceUpdatedTestCatalog(response.data.rid);
                        });
                    };

                    function replaceUpdatedTestCatalog(rid) {
                        //refetch the updated/inserted entity and set the new version in the list
                        var fpr = generateFPR();
                        fpr.page = 0;
                        fpr.size = 1;
                        fpr.filters.push({
                            field: "rid",
                            value: rid,
                            operator: "eq"
                        });
                        testCatalogService.getTestCatalogPage(fpr).then(function (response) {
                            var fetchedTC = response.data.content[0];
                            $scope.onTestCatalogClick(fetchedTC);
                            var prevTCIdx = $scope.testCatalogPage.content.map(function (obj) { return obj.rid; }).indexOf(fetchedTC.rid);
                            if (prevTCIdx !== -1) {//set updated entity in list
                                $scope.testCatalogPage.content[prevTCIdx] = fetchedTC;
                            } else {
                                $scope.testCatalogPage.content.unshift(fetchedTC);
                            }
                        });
                    }

                    $scope.clearForm = function () {
                        $scope.testCatalogForm.$setPristine();
                        $scope.testCatalogForm.$setUntouched();
                        $scope.specimenTypeLov.selectedValue = null;
                        //if we have a penlFilter then dont remove value
                        if (!$scope.options || util.isArrayEmpty($scope.options.panelFilters)) {
                            //$scope.panelLov.selectedValue = null;
                        }
                        $scope.selectedTestCatalog = {
                            requesterTestCode: "",
                            section: "",
                            name: "",
                            isActive: true
                        };
                    };

                    $scope.deleteTest = function (testCatalog) {
                        testCatalogService.deleteTestCatalog(testCatalog).then(function () {
                            util.createToast(util.systemMessages.successTransaction, "success");
                            $scope.clearForm();
                            //remove from list
                            for (var i in $scope.testCatalogPage.content) {
                                var tc = $scope.testCatalogPage.content[i];
                                if (tc.rid === testCatalog.rid) {
                                    $scope.testCatalogPage.content.splice(i, 1);
                                    break;
                                }
                            }
                        });
                    };

                    $scope.triggerTestActivation = function (testCatalog) {
                        testCatalogService.triggerTestActivation(testCatalog.rid).then(function () {
                            util.createToast(util.systemMessages.successTransaction, "success");
                            replaceUpdatedTestCatalog(testCatalog.rid)
                        });
                    };

                    $scope.onTestCatalogClick = function (testCatalog) {
                        $scope.selectedTestCatalog = testCatalog;
                        if (testCatalog) {
                            $scope.specimenTypeLov.selectedValue = testCatalog.specimenType;
                            //$scope.panelLov.selectedValue = testCatalog.panel;
                        }
                    };

                    $scope.options["refresh"] = function () {
                        $scope.clearForm();
                        $scope.letterPickerOptions.pick("None");
                    };

                    $scope.options["refreshPanels"] = function () {
                        getPanels();
                    };

                    //initial fetch
                    angular.element(function () {
                        var fpr = generateFPR();
                        fpr.size = pageSize;
                        $scope.getTestCatalogPage(fpr);
                    });

                }]
        }
    });
});