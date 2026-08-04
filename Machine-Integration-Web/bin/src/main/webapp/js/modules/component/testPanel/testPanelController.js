define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('testPanelCtrl', [
        '$scope',
        'testPanelService',
        'commonMethods',
        function (
            $scope,
            testPanelService,
            commonMethods
        ) {
            var pageSize = 30;
            $scope.metaData = null;
            $scope.panelForm = null;
            $scope.currentFilterablePageRequest = util.generateFilterablePageRequest();
            $scope.currentFilterablePageRequest.size = pageSize;
            $scope.panelPage = { content: [], totalPages: 1000 };//dummy totalPages
            $scope.selectedPanel = { externalCode: "", name: "" };
            $scope.testCatalogOptions = { filters: [], hideLetters: true, panelFilters: [] };
            $scope.letterPickerOptions = {
                onClick: function (letter) {
                    $scope.panelPage.content = [];
                    if ($scope.autoSearchOptions.reset) {
                        $scope.autoSearchOptions.reset();
                    }
                    var filter = util.generateFilterablePageRequest();
                    filter.size = pageSize;
                    if (letter) {
                        filter.filters.push({
                            field: "name",
                            value: letter,
                            operator: "startswith"
                        });
                    }
                    $scope.getPanelPage(filter);
                }
            };
            $scope.autoSearchOptions = {
                service: testPanelService.getPanelPage,
                callback: function (filters) {
                    if ($scope.autoSearchOptions.selectedItem && $scope.autoSearchOptions.selectedItem.rid !== -1) {
                        var filter = util.generateFilterablePageRequest();
                        filter.page = 0;
                        filter.size = 1;
                        filter.filters.push({
                            field: "rid",
                            value: $scope.autoSearchOptions.selectedItem.rid,
                            operator: "eq"
                        });
                        $scope.panelPage.content = [];
                        $scope.letterPickerOptions.reset();
                        $scope.getPanelPage(filter);
                    } else if (filters) {
                        var filter = util.generateFilterablePageRequest();
                        filter.page = 0;
                        filter.size = pageSize;
                        filter.filters = filters;
                        $scope.panelPage.content = [];
                        $scope.letterPickerOptions.reset();
                        $scope.getPanelPage(filter);
                    }
                },
                skeleton: {
                    code: "externalCode",
                    description: "name"
                },
                filterList: ["externalCode", "name"],
                sortList: [{ property: "externalCode", direction: "ASC" }]
            };

            commonMethods.retrieveMetaData("Panel").then(function (response) {
                $scope.metaData = response.data;
            });


            $scope.submit = function () {
                var apiRequest = null;
                //pick the api to use
                if ($scope.selectedPanel.rid) {
                    var apiRequest = testPanelService.updatePanel;
                } else {
                    var apiRequest = testPanelService.createPanel;
                }
                apiRequest($scope.selectedPanel).then(function (response) {
                    util.createToast(util.systemMessages.successTransaction, "success");
                    replaceUpdatedPanel(response.data);
                });
            };

            function replaceUpdatedPanel(panel) {
                //set the new version in the list, we are not refetching here bcz panel is a simple entity with no joins
                $scope.onPanelClick(panel);
                var prevPanelIdx = $scope.panelPage.content.map(function (obj) { return obj.rid; }).indexOf(panel.rid);
                if (prevPanelIdx !== -1) {
                    $scope.panelPage.content[prevPanelIdx] = panel;
                } else {
                    $scope.panelPage.content.unshift(panel);
                }
            }

            $scope.onPanelScroll = function () {
                $scope.currentFilterablePageRequest.page = $scope.panelPage.number + 1;
                return $scope.getPanelPage($scope.currentFilterablePageRequest);
            };

            $scope.getPanelPage = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.panelPage.totalPages) {
                    return;
                }
                $scope.currentFilterablePageRequest = angular.copy(filterablePageRequest);
                return testPanelService.getPanelPage(filterablePageRequest).then(function (response) {
                    response.data.content = $scope.panelPage.content.concat(response.data.content);
                    $scope.panelPage = response.data;
                });
            };

            $scope.clearForm = function () {
                $scope.panelForm.$setPristine();
                $scope.panelForm.$setUntouched();
                $scope.selectedPanel = { externalCode: "", name: "" };
                $scope.testCatalogOptions.filters = [];
                $scope.testCatalogOptions.panelFilters = [];
                $scope.testCatalogOptions.refresh();
                $scope.testCatalogOptions.refreshPanels();
            };

            $scope.deletePanel = function (panel) {
                testPanelService.deletePanel(panel.rid).then(function () {
                    util.createToast(util.systemMessages.successTransaction, "success");
                    $scope.testCatalogOptions.refreshPanels();
                    $scope.clearForm();
                    for (var i in $scope.panelPage.content) {
                        var p = $scope.panelPage.content[i];
                        if (p.rid === panel.rid) {
                            $scope.panelPage.content.splice(i, 1);
                            break;
                        }
                    }
                });
            };

            $scope.onPanelClick = function (panel) {
                $scope.testCatalogOptions.filters = [{ field: "panel.rid", value: panel.rid, operator: "eq" }];
                $scope.testCatalogOptions.panelFilters = [{ field: "rid", value: panel.rid, operator: "eq" }];
                $scope.testCatalogOptions.refresh();
                $scope.testCatalogOptions.refreshPanels();
                $scope.selectedPanel = panel;
            };

            //initial fetch
            angular.element(function () {
                var fpr = util.generateFilterablePageRequest();
                fpr.size = pageSize;
                //$scope.getPanelPage(fpr);
            });

        }
    ]);
});