define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    /**
     * MachineTest entity.
     * 
     */
    app.directive('testSelection', function () {
        return {
            restrict: 'E',
            replace: false,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testSelection/test-selection.html",
            scope: {
                options: "=options"
            },
            controller: [
                '$scope',
                'deviceTestsService',
                '$rootScope',
                '$timeout',
                function (
                    $scope,
                    deviceTestsService,
                    $rootScope,
                    $timeout
                ) {
                    var isFetching = false;
                    var pageSize = 30;//must be high number
                    $scope.machineTestPage = { content: [], totalPages: 1000 };//dummy totalPages
                    $scope.currentFilterablePageRequest = generateFPR();
                    $scope.currentFilterablePageRequest.size = pageSize;
                    $scope.selectedMachineTest = null;
                    $scope.showMachineName = false;
                    $scope.typeFilter = "ALL";//default

                    $scope.autoSearchOptions = {
                        service: function (fitlerablePageRequest) {
                            if ($scope.options && $scope.options.filters) {
                                fitlerablePageRequest.filters = fitlerablePageRequest.filters.concat($scope.options.filters);
                            }
                            var wrapper = {
                                fpr: fitlerablePageRequest,
                                type: $scope.typeFilter
                            };
                            return deviceTestsService.getMachineTestSelectionPage(wrapper).then(function (response) {
                                generateLabels(response.data.content);
                                return response;
                            });
                        },
                        callback: function (filters) {
                            if ($scope.autoSearchOptions.selectedItem && $scope.autoSearchOptions.selectedItem.rid !== -1) {
                                var fpr = generateFPR();
                                fpr.page = 0;
                                fpr.size = 1;
                                fpr.filters.push({
                                    field: "rid",
                                    value: $scope.autoSearchOptions.selectedItem.rid,
                                    operator: "eq"
                                });
                                $scope.machineTestPage.content = [];
                                $scope.getMachineTestSelectionPage(fpr);
                            } else if (filters) {
                                var fpr = generateFPR();
                                fpr.page = 0;
                                fpr.size = pageSize;
                                fpr.filters = fpr.filters.concat(filters);
                                $scope.machineTestPage.content = [];
                                $scope.getMachineTestSelectionPage(fpr);
                            }
                        },
                        skeleton: {
                            code: "searchLabel",
                            description: "searchLabel"
                        },
                        filterList: ["testCatalog.requesterTestCode"],
                        sortList: [{ property: "testCatalog.requesterTestCode", direction: "ASC" }],
                        label: "tests"
                    };

                    $scope.onMachineTestScroll = function () {
                        $scope.currentFilterablePageRequest.page = $scope.machineTestPage.number + 1;
                        return $scope.getMachineTestSelectionPage($scope.currentFilterablePageRequest);
                    };

                    $scope.getMachineTestSelectionPage = function (filterablePageRequest) {
                        //exceeded the limit
                        if (filterablePageRequest.page > $scope.machineTestPage.totalPages || isFetching === true) {
                            return;
                        }
                        isFetching = true;
                        $scope.currentFilterablePageRequest = angular.copy(filterablePageRequest);
                        var wrapper = {
                            fpr: filterablePageRequest,
                            type: $scope.typeFilter
                        };
                        return deviceTestsService.getMachineTestSelectionPage(wrapper).then(function (response) {
                            isFetching = false;
                            generateLabels(response.data.content);
                            response.data.content = $scope.machineTestPage.content.concat(response.data.content);
                            $scope.machineTestPage = response.data;
                        });
                    };


                    $scope.onMachineTestClick = function (machineTest) {
                        //de-select
                        if ($scope.selectedMachineTest === machineTest) {
                            $scope.selectedMachineTest = null;
                            $scope.options.onClick(null);
                        } else {
                            $scope.selectedMachineTest = machineTest;
                            $scope.options.onClick(machineTest);
                        }

                    };

                    function generateFPR() {
                        //generate FilterablePageRequest and to do any custom behaviour
                        var fpr = util.generateFilterablePageRequest();
                        if ($scope.options && $scope.options.filters) {
                            fpr.filters = fpr.filters.concat($scope.options.filters);
                        }
                        return fpr;
                    }

                    function generateLabels(machineTests) {
                        if (util.isArrayEmpty(machineTests)) {
                            return;
                        }
                        for (var key in machineTests) {
                            var machineTest = machineTests[key];
                            machineTest["label"] = machineTest.testCatalog ? machineTest.testCatalog.requesterTestCode : "";
                            machineTest["machineName"] = machineTest.machine ? machineTest.machine.name : "";
                            //this for the autocomplete search
                            machineTest["searchLabel"] = util.addParenthesis(machineTest.machineName, machineTest.label);
                        }
                    }

                    $scope.$on('onApexSelection', function (event, data) {
                        toggleShowMachineName();
                    });

                    $scope.$on('onApexRemove', function (event, data) {
                        toggleShowMachineName();
                    });

                    function toggleShowMachineName() {
                        $scope.showMachineName = $rootScope.apexEntities && $rootScope.apexEntities.length !== 1;
                    }

                    $scope.onTypeFilterChange = function () {
                        $scope.onMachineTestClick(null);//reset
                        $scope.options.refresh();
                    };

                    $scope.options["refresh"] = function () {
                        //reset
                        $scope.machineTestPage.content = [];
                        var fpr = generateFPR();
                        fpr.size = pageSize;
                        $scope.getMachineTestSelectionPage(fpr);
                    };

                    //initial fetch
                    angular.element(function () {
                        toggleShowMachineName();
                        $scope.options.refresh();
                    });

                }]
        }
    });
});