define(['app', 'util', 'config'], function (app, util, config) {
    //options.service: The API promise
    //options.callback: The callback function to call with the filters as parameter callback(filters)
    //                  Use this 'filters' to perform your API call
    //options.filterList: List of field names to filter [ "fieldName1", "fieldName2" ]
    //options.skeleton: The object keys to use. Ex: { code: "standardCode", description: "description", image: "image" }
    //options.disabled: A boolean or an object of type boolean
    //options.pageSize: Default is config.gridPageSizes[0] [OPTIONAL]
    //options.sort: No sort is added by default. Ex: [{ direction: "ASC", property: "rid" }] [OPTIONAL]
    //options.staticFilters: List of SearchCriterion objects. Ex: [{ field: "", operator: "", value: "", junctionOperator: "And"}] [OPTIONAL]
    'use strict';
    app.directive('autocompleteSearch', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/autocompleteSearch/autocomplete-search.html",
            scope: {
                options: "=options"

            },
            controller: ['$scope', '$filter', function ($scope, $filter) {
                var quickSearchFilters = [];
                var quickSearchFiltersTemplate = function (query) {
                    var filters = [];
                    if (query) {
                        for (var i = 0; i < $scope.options.filterList.length; i++) {
                            filters.push({
                                field: $scope.options.filterList[i],
                                operator: "contains",
                                value: query,
                                junctionOperator: "Or"
                            });
                        }
                        if ($scope.options.staticFilters) {
                            for (var i = 0; i < $scope.options.staticFilters.length; i++) {
                                filters.push($scope.options.staticFilters[i]);
                            }
                        }
                    }
                    return filters;
                }

                $scope.selectedItemChange = function (item) {
                    if (item) {
                        if (item.rid === -1) {
                            quickSearchFilters = quickSearchFiltersTemplate(item.autocompleteCode);
                        } else {
                            quickSearchFilters = [{
                                field: "rid",
                                operator: "eq",
                                value: item.rid,
                                junctionOperator: "And"
                            }];
                        }
                    } else {
                        quickSearchFilters = [];
                    }
                    // send the filters to the request to fetch data
                    $scope.options.callback(quickSearchFilters);
                }

                var pageSize = $scope.options.pageSize ? $scope.options.pageSize : config.gridPageSizes[0];

                var lastQuickSearchText = "";
                var autocompleteData = [];

                $scope.autocompleteSearch = function (searchText) {
                    //use this to prevent extra calls when clicking on an item [or search]
                    if (searchText === lastQuickSearchText) {
                        return new Promise(function (resolve, reject) {
                            resolve(autocompleteData);
                        });
                    } else {
                        lastQuickSearchText = searchText;
                    }

                    var filterablePageRequest = {
                        filters: [],
                        page: 0,
                        size: pageSize,
                        sortList: []
                    };
                    var filters = quickSearchFiltersTemplate(searchText);
                    for (var i = 0; i < filters.length; i++) {
                        filterablePageRequest.filters.push(filters[i]);
                    }
                    if ($scope.options.sortList) {
                        for (var i = 0; i < $scope.options.sortList.length; i++) {
                            filterablePageRequest.sortList.push($scope.options.sortList[i]);
                        }
                    }
                    return $scope.options.service(filterablePageRequest)
                        .then(function (response) {
                            autocompleteData = response.data.content;
                            for (var i = 0; i < autocompleteData.length; i++) {
                                //flatten the data
                                var item = autocompleteData[i];
                                item.autocompleteCode = util.getDeepValueInObj(item, $scope.options.skeleton.code);
                                item.autocompleteDescription = util.getDeepValueInObj(item, $scope.options.skeleton.description);
                            }
                            var searchObj = { rid: -1 };
                            searchObj.searchLabel = $filter('translate')('search');
                            searchObj.autocompleteCode = searchText;
                            searchObj.autocompleteDescription = searchText;
                            autocompleteData.unshift(searchObj);
                            return autocompleteData;
                        }).catch(function (response) {
                            return response;
                        });
                };

                //this will clear the search input box also it will call the callback fn with no filters (selectedItemChange(null))
                $scope.options["reset"] = function () {
                    $scope.searchText = null;
                };

            }]
        }
    });
});