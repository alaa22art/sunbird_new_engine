define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.controller('apexCtrl', [
        '$scope',
        '$state',
        '$mdDialog',
        '$rootScope',
        '$timeout',
        '$transitions',
        'socketService',
        'devicesService',
        'lovService',
        'driversService',
        function (
            $scope,
            $state,
            $mdDialog,
            $rootScope,
            $timeout,
            $transitions,
            socketService,
            devicesService,
            lovService,
            driversService
        ) {
            $scope.user = util.user;
            var pageSize = 20;

            $scope.isEventPanelOpened = false;
            $scope.eventsPanelIcon = "fas fa-angle-double-left";
            $scope.eventsPage = { content: [], totalPages: 1000 }; //dummy totalPages
            $scope.currentEventsFPR = util.generateFilterablePageRequest();
            $scope.currentEventsFPR.size = pageSize;
            $scope.eventStatuses = [];
            $scope.eventSeverities = [];
            $scope.eventCategories = [];
            $scope.newEvents = false;

            $scope.showEventStatuses = false;
            $scope.showEventSeverities = false;
            $scope.showEventCategories = false;

            $scope.apexRoutes = [];
            var allApexRoutes = [];
            $scope.selectedRoute = null;
            $scope.selectedSubroute = null;

            $scope.apexEntities = [];
            $rootScope.apexEntities = $scope.apexEntities;
            $scope.apexSelectionType = "MACHINE";//MACHINE, MACHINE_TYPE
            $scope.apexSelectionMode = "MULTI";//MULTI, SINGLE
            $scope.apexSelectionLabel = "devices";//devices,drivers
            $scope.isUserInfoPanelShown = false;
            $scope.isShownUserName = true;
            $scope.showIconName = "angle-double-down";
            $scope.showApexEntities = true;
            $scope.showUserPanel = function () {
                $scope.isUserInfoPanelShown = !$scope.isUserInfoPanelShown;
                if ($scope.isUserInfoPanelShown) {
                    $scope.isShownUserName = false;
                }
                else {
                    setTimeout(function () {
                        $scope.isShownUserName = true;
                    }, 250);
                }
            }

            function sortByRank(a, b) {
                if (a.views.main.data.rank > b.views.main.data.rank) {
                    return 1;
                } else if (a.views.main.data.rank < b.views.main.data.rank) {
                    return -1;
                } else {
                    return 0;
                }
            }
            function prepareRoutes() {
                //return an object with name of route is the key
                //get only apex's routes
                //labels must be unique from each other so parentName can work correctly, maybe change this later and use fake id
                //we only draw tabs for routes and subroutes(1 level of children)

                //exclude non-routes, apex itself
                $scope.apexRoutes = $state.get().filter(function (obj) {
                    return obj.name && obj.name.startsWith("apex.");
                });

                var parentRoutes = [];//store all parent routes
                //populate some data for all routes
                for (var i in $scope.apexRoutes) {
                    var route = $scope.apexRoutes[i];
                    route["label"] = route.views.main.data.pageName ? route.views.main.data.pageName : "";
                    var parentRoute = route.views.main.data.parentName;
                    if (parentRoute) {
                        var parent = parentRoutes.find(function (obj) { return obj.label === parentRoute; });
                        if (!parent) {//parent does'nt exist, create one
                            parent = {
                                label: parentRoute,
                                children: []
                            };
                            parentRoutes.push(parent);
                        }
                    }
                }

                $scope.apexRoutes = $scope.apexRoutes.concat(parentRoutes);
                //populate parent-child relationship
                var routesLength = $scope.apexRoutes.length;
                for (var idx = routesLength - 1; idx >= 0; idx--) {
                    var route = $scope.apexRoutes[idx];
                    if (route.children) {//skip parent routes
                        continue;
                    }
                    var parentName = route.views.main.data.parentName;//get parent name
                    var parentRoute = parentName ?
                        $scope.apexRoutes.find(function (obj) { return obj.label === parentName; }) : null;
                    if (parentRoute) {
                        route["parent"] = parentRoute;
                        parentRoute.children.push(route);
                        $scope.apexRoutes.splice(idx, 1);
                    }
                }
                //sort all routes and subroutes by Rank
                $scope.apexRoutes.sort(sortByRank);
                for (var i in $scope.apexRoutes) {
                    if ($scope.apexRoutes[i].children) {
                        $scope.apexRoutes[i].children.sort(sortByRank);
                    }
                }
                allApexRoutes = angular.copy($scope.apexRoutes);
                //console.log($scope.apexRoutes);
            }

            prepareRoutes();

            $scope.onEventsScroll = function () {
                $scope.currentEventsFPR.page = $scope.eventsPage.number + 1;
                return $scope.getEventDetailsPage($scope.currentEventsFPR);
            };

            $scope.getEventDetailsPage = function (fpr) {

            };

            $scope.refreshEventDetailsPage = function () {
                $scope.eventsPage.content = [];
                var fpr = util.generateFilterablePageRequest();
                fpr.size = pageSize;
                $scope.getEventDetailsPage(fpr);
            };

            $scope.onEventsCheckChange = function (values, changedValue) {
                for (var key in values) {
                    if (values[key] !== changedValue) {
                        values[key].checked = false;
                    }
                }
                changedValue.checked = !changedValue.checked;
                $scope.refreshEventDetailsPage();
            };

            $scope.toggleEventsPanel = function () {
                $scope.newEvents = false;
                $scope.isEventsPanelOpened = !$scope.isEventsPanelOpened;
                $scope.eventsPanelIcon = $scope.isEventsPanelOpened ? "fas fa-angle-double-right" : "fas fa-angle-double-left";
            };

            $scope.closeQCEventDetails = function (eventDetail) {

            };

            function findRoute(routeName) {
                var route = allApexRoutes.find(function (route) {
                    if (route.children) {
                        return route.children.find(function (subroute) { return subroute.name === routeName; });
                    } else {
                        return route.name === routeName;
                    }
                });
                if (route.children) {
                    return route.children.find(function (subroute) { return subroute.name === routeName; });
                }
                return route;
            }

            $scope.onRouteClick = function (route, params) {
                if (!route) {
                    return;
                }
                //if it has children then navigate to first one
                if (route.children) {
                    $state.go(route.children[0].name, { params: params });
                    $scope.selectedSubroute = route.children[0];
                } else {
                    $state.go(route.name, { params: params });
                }
                //set the route in the correct object
                if (route.parent) {
                    $scope.selectedSubroute = route;
                } else {
                    $scope.selectedRoute = route;
                }
            };

            function getApexSelectionData(fpr) {
                //To be used when fetching apex entities either in dialog or any other internal process
                var wrapper = fpr ? angular.copy(fpr) : { filters: [] };
                var api = null;
                if ($scope.apexSelectionType === "MACHINE") {
                    api = devicesService.getMachinePage(wrapper);
                } else if ($scope.apexSelectionType === "MACHINE_TYPE") {
                    api = driversService.getMachineTypePage(wrapper);
                }
                return api.then(function (response) {
                    //add custom fields 
                    if (response.data.content && response.data.content.length > 0) {
                        for (var key in response.data.content) {
                            var obj = response.data.content[key];
                            obj["label"] = "";
                            if ($scope.apexSelectionType === "MACHINE") {
                                obj.label = obj.name;
                            } else if ($scope.apexSelectionType === "MACHINE_TYPE") {
                                obj.label = obj.name;
                            } else if ($scope.apexSelectionType === "CONTROL_ITEM") {
                                obj.label = obj.description;
                            }
                        }
                    }

                    return response.data;
                });
            }

            $scope.apexSelection = function (ev) {
                $mdDialog.show({
                    controller: [
                        "$scope",
                        "$mdDialog",
                        "currentlySelected",
                        "apexSelectionType",
                        "apexSelectionMode",
                        "apexSelectionLabel",
                        function (
                            $scope,
                            $mdDialog,
                            currentlySelected,
                            apexSelectionType,
                            apexSelectionMode,
                            apexSelectionLabel) {

                            var runGridSelectionOnChange = { value: true };
                            var gridId = "#apexSelectionGrid";//html element id
                            $scope.apexSelectionType = apexSelectionType;
                            $scope.apexSelectionMode = apexSelectionMode;
                            $scope.apexSelectionLabel = apexSelectionLabel;
                            $scope.newSelectedEntities = [];

                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };

                            $scope.add = function () {
                                $mdDialog.hide($scope.newSelectedEntities);
                            };

                            $scope.refreshGrid = function () {
                                apexDataSource.read();
                            };

                            var gridColumns = [];
                            var schemaFields = {};

                            //set custom columns
                            if ($scope.apexSelectionType === "MACHINE") {
                                gridColumns = [
                                    {
                                        field: "name",
                                        title: util.systemMessages.name
                                    },
                                    {
                                        field: "isActive",
                                        title: util.systemMessages.active,
                                        template: function (dataItem) {
                                            return '<activation class="text-center" value="' + dataItem.isActive + '"></activation>';
                                        }
                                    },
                                ];
                                schemaFields = {
                                    "rid": {
                                        type: "number"
                                    },
                                    "name": {
                                        type: "string"
                                    },
                                    "isActive": {
                                        type: "boolean"
                                    }
                                };
                            } else if ($scope.apexSelectionType === "MACHINE_TYPE") {
                                gridColumns = [
                                    {
                                        field: "name",
                                        title: util.systemMessages.name
                                    },
                                    {
                                        field: "isActive",
                                        title: util.systemMessages.active,
                                        template: function (dataItem) {
                                            return '<activation class="text-center" value="' + dataItem.isActive + '"></activation>';
                                        }
                                    }
                                ];
                                schemaFields = {
                                    "rid": {
                                        type: "number"
                                    },
                                    "name": {
                                        type: "string"
                                    },
                                    "isActive": {
                                        type: "boolean"
                                    }
                                };
                            } else if ($scope.apexSelectionType === "CONTROL_ITEM") {
                                gridColumns = [
                                    {
                                        field: "description",
                                        title: util.systemMessages.description
                                    }
                                ];
                                schemaFields = {
                                    "rid": {
                                        type: "number"
                                    },
                                    "description": {
                                        type: "string"
                                    }
                                };
                            }

                            //custom behavior depending on current mode
                            if ($scope.apexSelectionMode === "SINGLE") {
                                gridColumns.splice(0, 0,
                                    {
                                        selectable: true,
                                        width: "50px",
                                        headerTemplate: ' '
                                    });
                                //wait for grid to init
                                $timeout(function () {
                                    //to support single selection
                                    var grid = $(gridId).data("kendoGrid");
                                    grid.tbody.on("click", ".k-checkbox", function onClick(e) {
                                        var grid = $(gridId).data("kendoGrid");
                                        var row = $(e.target).closest("tr");
                                        if (row.hasClass("k-state-selected")) {
                                            setTimeout(function (e) {
                                                var grid = $(gridId).data("kendoGrid");
                                                grid.clearSelection();
                                            })
                                        } else {
                                            grid.clearSelection();
                                        };
                                    });
                                });
                            } else {
                                gridColumns.splice(0, 0,
                                    {
                                        selectable: true,
                                        width: "50px",
                                    });
                            }

                            var apexDataSource = new kendo.data.DataSource({
                                pageSize: config.gridPageSizes[0],
                                page: 1,
                                serverPaging: true,
                                serverFiltering: true,
                                transport: {
                                    read: function (e) {
                                        e.data = util.createFilterablePageRequest($scope.apexSelectionGridOptions.dataSource, undefined);
                                        //exclude currently selected
                                        if (currentlySelected && currentlySelected.length > 0) {
                                            e.data.filters.push({
                                                field: "rid",
                                                value: currentlySelected,
                                                operator: "notin"
                                            });
                                        }

                                        getApexSelectionData(e.data).then(function (data) {
                                            e.success(data);
                                        });

                                    }
                                },
                                schema: {
                                    parse: function (data) {
                                        return data;
                                    },
                                    data: "content",
                                    total: "totalElements",
                                    model: { id: "rid", fields: schemaFields }
                                }
                            });

                            $scope.apexSelectionGridOptions = {
                                columns: gridColumns,
                                selectable: false,
                                persistSelection: false,
                                dataSource: apexDataSource,
                                dataBound: function (e) {
                                    util.gridSelectionDataBound(e.sender, $scope.newSelectedEntities, runGridSelectionOnChange, undefined);
                                },
                                change: function onChange(e) {
                                    util.gridSelectionChange(e.sender, $scope.newSelectedEntities, runGridSelectionOnChange);
                                    //If going to next page and we already selected a row then we can select more than one
                                    //row, so we make sure that we put last selected in the array only for SINGLE mode
                                    if ($scope.apexSelectionMode === "SINGLE" && $scope.newSelectedEntities.length > 1) {
                                        $scope.newSelectedEntities = [$scope.newSelectedEntities[$scope.newSelectedEntities.length - 1]];
                                    }
                                }
                            };
                        }],
                    templateUrl: "./" + config.lisDir + "/modules/dialogs/apex-selection-grid-dialog.html",
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    locals: {
                        currentlySelected: $scope.apexEntities.map(function (obj) { return obj.rid; }),
                        apexSelectionType: $scope.apexSelectionType,
                        apexSelectionMode: $scope.apexSelectionMode,
                        apexSelectionLabel: $scope.apexSelectionLabel
                    }
                }).then(function (data) {
                    if (data) {
                        addToApexEntity(data);
                        onApexSelection(data, true);
                    }
                }, function () { });
            };

            function addToApexEntity(data) {
                if (!data) {
                    return;
                }
                //dont use concat so rootScope variable keep pointing to same variable
                for (var key in data) {
                    $scope.apexEntities.push(data[key]);
                }
            };

            $scope.removeApexEntity = function (entity, broadcastEvent) {
                var index = $scope.apexEntities.map(function (obj) {
                    return obj.rid
                }).indexOf(entity.rid);
                if (index !== -1) {
                    $scope.apexEntities.splice(index, 1);
                    if (broadcastEvent) {
                        onApexRemove([entity]);
                    }
                }
            };

            $scope.removeAllApexEntities = function (broadcastEvent) {
                //dont use $scope.apexEntities = [] so rootScope variable keep pointing to same variable
                var removedEntities = [];
                var length = $scope.apexEntities.length;
                for (var i = length - 1; i >= 0; i--) {
                    removedEntities.push($scope.apexEntities[i]);
                    $scope.apexEntities.splice(i, 1);
                }
                if (broadcastEvent) {
                    onApexRemove(removedEntities);
                }
            };

            function onApexSelection(apexEntities, broadcastEvent) {
                if (util.isArrayEmpty(apexEntities) || !broadcastEvent) {
                    return;
                }
                $rootScope.$broadcast("onApexSelection", apexEntities);
            }

            function onApexRemove(apexEntities) {
                if (util.isArrayEmpty(apexEntities)) {
                    return;
                }
                $rootScope.$broadcast("onApexRemove", apexEntities);
            }

            $scope.$on("onApexModeChange", function (event, data) {
                if (!data || !data.type || !data.mode) {
                    return;
                }


                if ($scope.apexSelectionType === data.type && $scope.apexSelectionMode === data.mode) {
                    //dont remove anything just call selection so caller page can work
                    //must use $timeout to make it run late
                    $timeout(function () {
                        onApexSelection($scope.apexEntities, true);
                    });
                    return;
                }
                $scope.removeAllApexEntities(true);
                $scope.apexSelectionType = data.type;
                $scope.apexSelectionMode = data.mode;

                if ($scope.apexSelectionType === "MACHINE") {
                    $scope.apexSelectionLabel = "devices";
                } else if ($scope.apexSelectionType === "MACHINE_TYPE") {
                    $scope.apexSelectionLabel = "drivers";
                }

            });

            $scope.$on("onApexEntityAddition", function (event, data) {
                //Set/Update apexEntities by using data parameter.
                //Must be a list with rids to refetch apexEntities.
                if (util.isArrayEmpty(data.rids)) {
                    return;
                }
                var broadcastEvent = data.broadcastEvent;
                var wrapper = {
                    filters: [{ field: "rid", value: data.rids, operator: "in" }]
                };
                getApexSelectionData(wrapper).then(function (data) {
                    $scope.removeAllApexEntities(false);
                    addToApexEntity(data.content);
                    if (broadcastEvent) {
                        onApexSelection(data.content, broadcastEvent);
                    }
                });

            });

            $scope.$on("onApexEntityRemoveAll", function (event, data) {
                $scope.removeAllApexEntities(data);
            });

            $scope.$on("onApexRouteFilter", function (event, data) {
                //filter routes using module id
                if (!data) {
                    return;
                }
                refreshApexRoutes(data);
                navigateToRoute($scope.apexRoutes[0], null);
            });

            $scope.$on("onApexNavigateTo", function (event, data) {
                if (!data) {
                    return;
                }
                var route = findRoute("apex." + data.path);
                var moduleId = route.views.main.data.module;
                refreshApexRoutes(moduleId);
                navigateToRoute(route, data.params);
                //update header selected module
                $rootScope.$broadcast("onApexChangeModule", { moduleId: moduleId, broadcastEvent: false });
            });

            $scope.$on("onApexEntityPanelHide", function (event, data) {
                $scope.showApexEntities = data;
            });

            function refreshApexRoutes(moduleId) {
                $scope.apexRoutes = angular.copy(allApexRoutes).filter(function (route) {
                    if (route.children) {
                        route.children = route.children.filter(function (subroute) {
                            return subroute.views.main.data.module === moduleId;
                        });
                        return route.children.length > 0;
                    } else {
                        return route.views.main.data.module === moduleId;
                    }
                });
                $scope.apexRoutes.sort(sortByRank);
            }

            $transitions.onFinish({}, function ($transition) {
                //to toggle showing apex entities 
                var toState = $transition.to();
                $scope.showApexEntities = true;
                return true;//continue navigating
            });

            function navigateToRoute(route, params) {
                //navigate to a specific route using a route object.
                //its different from onRouteClick(...) because onRouteClick(...) handles clicking a route from UI
                //and not setting it manually through code
                if (!route) {
                    return;
                }
                $scope.onRouteClick(route, params);
                if (route.parent) {//set parent object
                    $scope.selectedRoute = route.parent;
                }
            }
            //init some data after dom renders
            angular.element(function () {
                $rootScope.$broadcast("onApexRender", {});
                $scope.refreshEventDetailsPage();
            });

            $scope.$on("$destroy", function () {
                delete $rootScope["apexEntities"];
            });

        }
    ]);
});