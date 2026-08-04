define(['app', 'util'], function (app, util) {
	'use strict';
	app.controller('accessDeniedCtrl', ['$scope', '$location', '$rootScope', '$state',
		function ($scope, $location, $rootScope, $state) {

			util.fullWebsiteView($scope);


			$scope.goHome = function () {
				$location.path("/");
			};

			$scope.goBack = function () {
				if ($rootScope.fromState && $rootScope.fromState.name) {
					$state.go($rootScope.fromState.name);
				} else {
					$scope.goHome();
				}
			}

		}
	]);
});