define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
	'use strict';
	app.directive('lisHeader', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/header/header.html",
			controller: ['$scope', '$interval', '$state', '$rootScope', '$transitions', 'socketService',
				function ($scope, $interval, $state, $rootScope, $transitions, socketService) {
					if (util.token == null) { // user is not logged, this class will be removed after login is successful
						$("#header").addClass("ng-hide");
					}
					$scope.search = null;
					$scope.tooltipDirection = util.direction === 'ltr' ? "left" : "right";
					$scope.userGroups = "";
					$scope.fullName = "";
					$scope.modules = angular.copy(commonData.apexModules);
					$scope.showModules = false;
					$scope.selectedModule = $scope.modules[Object.keys($scope.modules)[0]];
					$scope.branchName = null;
					// This code, despite it ticks the clock for the timer ui, it also makes Angular $apply which will $apply for
					// every place that is outside of angular context.
					var tick = function () {
						$scope.clock = Date.now();
					}
					tick();
					$interval(tick, 500);

					function headerInfo() {
						if (!util.isLoggedIn()) {
							$scope.userGroups = "";
							$scope.fullName = "";
							$scope.branchName = null;
							return;
						}
						if (util.user.fullName != null) {
							if (util.user.fullName.hasOwnProperty(util.userLocale)) {
								$scope.fullName = util.user.fullName[util.userLocale];
							} else {
								$scope.fullName = util.user.fullName[Object.keys(util.user.fullName)[0]];
							}
						} else {
							$scope.fullName = "";
						}
						if (util.user.userGroups != null && util.user.userGroups.length > 0) {
							$scope.userGroups = util.user.userGroups.map(function (group) {
								return group.name[util.userLocale];
							}).join(",");
						} else {
							util.user.userGroups = [];
						}

						if (util.user.branch) {
							$scope.branchName = util.user.branch.name[util.userLocale];
						} else {
							$scope.branchName = null;
						}

					}
					headerInfo();

					$scope.$on("recompileHeader", function (event, data) {
						headerInfo();
					});


					$scope.onSearchSubmit = function (event) {
						if (event.which === 13) {
							$scope.searchSamples();
						}
					};

					$scope.searchSamples = function () {
						if (!$scope.search) {
							return;
						}
					};


					$scope.logout = function () {
						socketService.close();
						util.clearUtilData();
						$state.go("login");
					};

					$scope.userProfile = function () {
						$state.go("user-profile");
					};

					$scope.filterApex = function (module, broadcastEvent) {
						if (!module) {
							return;
						}
						$scope.selectedModule = module;
						if (broadcastEvent) {
							$rootScope.$broadcast("onApexRouteFilter", module.id);
						}
					};
					$scope.$on("onApexRender", function (event, data) {
						//after apex rendering
						$scope.filterApex($scope.selectedModule, true);
					});
					$scope.$on("onApexChangeModule", function (event, data) {
						//when we want to change the header selectdModule
						if (!data) {
							return;
						}
						for (var key in $scope.modules) {
							if ($scope.modules[key].id === data.moduleId) {
								$scope.filterApex($scope.modules[key], data.broadcastEvent);
								break;
							}
						}

					});

					$transitions.onFinish({}, function ($transition) {
						//to toggle showing modules or not based on route
						var toState = $transition.to();
						$scope.showModules = toState.name.startsWith("apex.");
						return true;//continue navigating
					});

				}
			]
		}
	});
});