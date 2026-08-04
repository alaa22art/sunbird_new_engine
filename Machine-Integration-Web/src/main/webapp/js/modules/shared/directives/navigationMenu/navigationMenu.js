define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';

	//leave it as internal service temporarily, later on add it to a new file service as 'systemSettingService'
	app.service('systemSettingService', ["$http", function ($http) {
		this.getIsEnabledToViewPCR = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getIsEnabledToViewPCR.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};
	}]);

	app.directive('lisNavMenu', ['systemSettingService', function (systemSettingService) {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/navigationMenu/navigation-menu.html",
			controller: ['$scope', function ($scope) {
				if (util.token === null) { // user is not logged in, this class will be removed after login is successful
					$("#navMenu").addClass("ng-hide");
				}
				$scope.isMenuToggled = false;

				var veiwPcrPageType = "ENABLE_REALTIME_PCR_VIEW";

				var pcrNavItem =
				{
					authority: "VIEW_MACHINE_MANAGMENT",
					route: null,
					icon: "fa fa-lg fa-stethoscope",
					label: "RTPCR",
					isSubItemToggled: false,
					subItems: [{
						authority: "VIEW_MACHINE_TYPE_SETUP", // for now leave it as it is, later add new autthority for this page
						route: "realtime-pcr",
						icon: "fa fa-lg fa-stethoscope",
						label: "Real Time PCR"
					},
					{
						authority: "VIEW_MACHINE_TYPE_SETUP", // for now leave it as it is, later add new autthority for this page
						route: "realtime-pcr-patients",
						icon: "fas fa-lg fa-file-medical",
						label: "Real Time PCR Patients"
					}
					]
				};

				$scope.navMenuItems = [
				// 	{
				// 	authority: "VIEW_USERS_MANAGEMENT",
				// 	route: "test-catalog",
				// 	icon: "fas fa-lg fa-syringe",
				// 	label: "testCatalog",
				// 	//fas fa-vials
				// 	subItems: [{
				// 		route: "test-catalog",
				// 		//icon: "fas fa-lg fa-vials",
				// 		label: "testCatalog",
				// 	}]
				// },
				{
					authority: "VIEW_USERS_MANAGEMENT",
					route: "mapping-codes",
					icon: "fas fa-lg fa-syringe",
					label: "mappingCode",
					//fas fa-vials
					subItems: [{
						route: "mapping-codes",
						//icon: "fas fa-lg fa-vials",
						label: "mappingCode",
					}]
				},

				// {
				// 	route: "mapping-codes",
				// 	icon: "fas fa-lg fa-syringe",
				// 	label: "mappingCode"
				// },
				{
					route: "transaction-log",
					icon: "fas fa-lg fa-cogs",
					label: "transactionLog"
				},

				{
					route: "messeges-transaction-log",
					icon: "fas fa-lg fa-cogs",
					label: "messagesTransactionLog"
				},
				/*{
					route: "patient-sample-lookup",
					icon: "fas fa-lg fa-search",
					label: "patientSampleLookup"
				},*/
				/*{
					authority: "VIEW_MACHINE_MANAGMENT", //VIEW_TENANT_MANAGEMENT
					route: "tenant-management",
					icon: "fas fa-lg fa-user-tie",
					label: "tenantManagement"
				},*/
				/*{
					authority: "VIEW_BRANCH",
					route: "branches",
					icon: "fas fa-lg fa-code-branch",
					label: "branches"
				},*/
				{
					authority: "VIEW_MACHINE_MANAGMENT",
					route: null,
					icon: "fas fa-lg fa-screwdriver",
					label: "machineSetup",
					isSubItemToggled: false,
					subItems: [{
						authority: "VIEW_MACHINE_TYPE_SETUP",
						route: "machine-type-setup",
						icon: "fas fa-lg fa-screwdriver",
						label: "machineTypeSetup"
					},
					{
						authority: "VIEW_MACHINE_SETUP",
						route: "machine-setup",
						icon: "fas fa-lg fa-screwdriver",
						label: "machineSetup"
					}
					]
				},
					//pcrNavItem,

				{
					authority: "VIEW_SECURITY_MANAGEMENT",
					route: null,
					icon: "fas fa-lg fa-shield-alt",
					label: "securityControl",
					isSubItemToggled: false,
					subItems: [{
						authority: "VIEW_USERS_MANAGEMENT",
						route: "users-management",
						icon: "fas fa-lg fa-user",
						label: "userManagement"
					},
					{
						authority: "VIEW_GROUPS_MANAGEMENT",
						route: "groups-management",
						icon: "fas fa-lg fa-users",
						label: "groupManagement"
					},
					{
						authority: "VIEW_ROLES_MANAGEMENT",
						route: "roles-management",
						icon: "fa-lg lis-role",
						label: "roleManagement"
					}
					]
				},
				{
					route: null,
					icon: "fas fa-lg fa-cog",
					label: "settings",
					isSubItemToggled: false,
					subItems: [{
						authority: "VIEW_TENANT_MESSAGES",
						route: "tenant-messages",
						icon: "fas fa-lg fa-language",
						label: "tenantMessages"
					},
					{
						authority: "VIEW_LKP_MANAGEMENT",
						route: "lkp-management",
						icon: "fa-lg lis-lkp-management-alt",
						label: "lkpManagement"
					}
					]
				}
				];

				systemSettingService.getIsEnabledToViewPCR(veiwPcrPageType).then(function (response) {
					if (!response.data) {
						var removedItemIndex = $scope.navMenuItems.indexOf(pcrNavItem);
						$scope.navMenuItems.splice(removedItemIndex, 1);
					}
				})




				$scope.expandSubList = function (item) {
					// dont change value of it does not have sub list to expand or the nav menu is not exapnded
					if (item.isSubItemToggled == null || $scope.isMenuToggled == false) {
						return;
					}
					item.isSubItemToggled = !item.isSubItemToggled;
				};

				$scope.$on("toggleNavMenu", function (event, data) {
					$scope.isMenuToggled = !$scope.isMenuToggled;
					for (var idx = 0; idx < $scope.navMenuItems.length; idx++) {
						if ($scope.navMenuItems[idx].isSubItemToggled != null) {
							$scope.navMenuItems[idx].isSubItemToggled = false;
						}
					}
				});


			}]
		}
	}]);
});