define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.controller('branchCtrl', [
        '$scope',
        'branchFormService',
        function (
            $scope,
            branchFormService
        ) {
            $scope.mainTemplate = true;
            $scope.branch = null;
            $scope.userLocale = util.userLocale;
            $scope.branchFormOptions = {};

            $scope.refreshGrid = function () {
                refresh();
                $scope.clearForm();
            };

            function refresh() {
                dataSource.read();
            }
            $scope.back = function () {
                $scope.toggleView();
                $scope.clearForm();
                $("#branchGrid").data("kendoGrid").clearSelection();
            };
            $scope.toggleView = function () {
                $scope.mainTemplate = !$scope.mainTemplate;
            };
            $scope.clearForm = function () {
                $scope.branchFormOptions.clear();
                // in case we called this function when we are inside the edit 
                if ($scope.mainTemplate == false) {
                    $scope.branch = {
                        isActive: true,
                        isIntegrationEnabled: false
                    };
                }
            };
            $scope.createMode = function () {
                $scope.toggleView();
                $scope.clearForm();
            };
            $scope.updateMode = function () {
                $scope.toggleView();
            };
            $scope.activateBranch = function () {
                branchFormService.activateBranch($scope.branch.rid).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });
            };
            $scope.deactivateBranch = function () {
                branchFormService.deactivateBranch($scope.branch.rid).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });
            };
            $scope.submitBranch = function () {
                if ($scope.branch.rid == null) {
                    branchFormService.createBranch($scope.branch).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.branch = response.data; //we set it here but we will get the fully joined and fetched data from refresh
                        refresh();
                    });
                } else {
                    branchFormService.updateBranch($scope.branch).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.branch = response.data; //we set it here but we will get the fully joined and fetched data from refresh
                        refresh();
                    });
                }
            };
            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        branchFormService.getBranches().then(function (response) {
                            e.success(response.data);
                            // get branch if it was set
                            if ($scope.branch != null && $scope.branch.rid != null) {
                                for (var idx = 0; idx < response.data.length; idx++) {
                                    if (response.data[idx].rid == $scope.branch.rid) {
                                        $scope.branch = response.data[idx];
                                        break;
                                    }
                                }
                            }

                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.length; idx++) {
                            data[idx]["nameLocale"] = data[idx].name[util.userLocale];
                            data[idx]["addressLocale"] = data[idx].address[util.userLocale];
                            data[idx]["cityLocale"] = data[idx].city.name[util.userLocale];
                            data[idx]["countryLocale"] = data[idx].country.name[util.userLocale];
                        }
                        return data;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "code": {
                                type: "string"
                            },
                            "nameLocale": {
                                type: "string"
                            },
                            "phoneNo": {
                                type: "string"
                            },
                            "addressLocale": {
                                type: "string"
                            },
                            "cityLocale": {
                                type: "string"
                            },
                            "countryLocale": {
                                type: "string"
                            },
                            "isActive": {
                                type: "boolean"
                            },
                            "mobilePattern": {
                                type: "string"
                            },
                            "integrationUrl": {
                                type: "string"
                            }
                        }
                    }
                }
            });
            $scope.branchGridOptions = {
                columns: [{
                        field: "code",
                        title: util.systemMessages.code
                    },
                    {
                        field: "nameLocale",
                        title: util.systemMessages.name
                    },
                    {
                        field: "isActive",
                        title: util.systemMessages.active,
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "phoneNo",
                        title: util.systemMessages.phone
                    },
                    {
                        field: "addressLocale",
                        title: util.systemMessages.address
                    },
                    {
                        field: "countryLocale",
                        title: util.systemMessages.country,
                        hidden: true
                    },
                    {
                        field: "cityLocale",
                        title: util.systemMessages.city,
                        hidden: true
                    },
                    {
                        field: "mobilePattern",
                        title: util.systemMessages.mobilePattern,
                        hidden: true
                    },
                    {
                        field: "integrationUrl",
                        title: util.systemMessages.integrationUrl,
                        hidden: true
                    }
                ],
                dataSource: dataSource,
                change: function () {
                    $scope.branch = $scope.branchGrid.dataItem($scope.branchGrid.select());
                }
            };

        }
    ]);
});