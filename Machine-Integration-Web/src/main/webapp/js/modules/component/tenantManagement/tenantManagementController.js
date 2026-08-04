define(['app', 'util'], function (app, util) {
	'use strict';
	app.controller('tenantManagementCtrl', ['$scope', 'tenantFormService',
		function ($scope, tenantFormService) {
			$scope.tenant = null;
			$scope.tenantFormOptions = {};
			getTenant();

			function getTenant() {
				tenantFormService.getTenantData(util.user.tenantId).then(function (response) {
					$scope.tenant = response.data;
				});
			}
			$scope.submitTenant = function () {
				var tenant = $scope.tenantFormOptions.getTenant();
				var data = {
					logo: $scope.tenantFormOptions.fileDataWrapper.fileModel,
					tenant: tenant,
					tenantLanguages: $scope.tenantFormOptions.tenantLanguages
				};
				if (tenant.rid != null) {
					tenantFormService.updateTenant(data).then(function () {
						util.createToast(util.systemMessages.success, "success");
						getTenant();
					});
				} else {
					tenantFormService.createTenant(data).then(function () {
						util.createToast(util.systemMessages.success, "success");
						getTenant();
					});
				}
			};
		}
	]);
});