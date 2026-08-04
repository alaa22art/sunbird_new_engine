define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('driverAssaysCtrl', [
		'$scope',
		'$rootScope',
		'commonMethods',
		'lovService',
		'driverAssaysService',
		'$q',
		'$filter',
		'$stateParams',
		function ($scope,
			$rootScope,
			commonMethods,
			lovService,
			driverAssaysService,
			$q,
			$filter,
			$stateParams) {

			$rootScope.$broadcast('onApexModeChange', { type: "MACHINE_TYPE", mode: "SINGLE" });
			var preSelectedMachineTypeRid = $stateParams.params ? $stateParams.params.machineTypeRid : null;
			var pageSize = 30; //must be high number

			$scope.currentMachineTypeTestPage = { content: [], totalPages: 1000 }; //dummy totalPages
			$scope.currentMachineTypeTestFPR = util.generateFilterablePageRequest();
			$scope.currentMachineTypeTestFPR.size = pageSize;
			$scope.selectedMachineTypeTest = {
				isActive: true
			};
			$scope.selectedTestAssay = {
				isAutoVerifiable: true
			};
			$scope.selectedMachineType = null;
			$scope.selectedControlLotAssay = null;

			$scope.machineTypeTestMetaData = null;
			$scope.testAssayMetaData = null;

			$scope.machineTypeTestSearchOptions = {
				service: function (fpr) {
					addMachineTypeFilter(fpr);
					return driverAssaysService.getMachineTypeTestPage(fpr).then(function (response) {
						return response;
					});
				},
				callback: function (filters) {
					if ($scope.machineTypeTestSearchOptions.selectedItem && $scope.machineTypeTestSearchOptions.selectedItem.rid !== -1) {
						var fpr = util.generateFilterablePageRequest();
						fpr.filters.push({
							field: "rid",
							value: $scope.machineTypeTestSearchOptions.selectedItem.rid,
							operator: "eq"
						});
						$scope.currentMachineTypeTestPage.content = [];
						getMachineTypeTestData(fpr);
					} else if (filters) {
						var fpr = util.generateFilterablePageRequest();
						fpr.size = pageSize;
						fpr.filters = filters;
						$scope.currentMachineTypeTestPage.content = [];
						getMachineTypeTestData(fpr);
					}
				},
				skeleton: { code: "defaultHostCode", description: "name" },
				filterList: ["name", "defaultHostCode", "description"],
				sortList: [
					{ direction: "ASC", property: "name" },
					{ direction: "ASC", property: "defaultHostCode" },
					{ direction: "ASC", property: "description" }
				],
				label: "driverAssay"
			};

			$scope.$on('onApexSelection', function (event, data) {
				$scope.selectedMachineType = data[0];
				$scope.clearForm();
				refreshMachineTypeTestData();
			});

			$scope.$on('onApexRemove', function (event, data) {
				$scope.selectedMachineType = null;
				$scope.clearForm();
			});

			commonMethods.retrieveMetaDataList(["MachineTypeTest"]).then(function (response) {
				$scope.machineTypeTestMetaData = response.data["MachineTypeTest"];
			});

			$scope.onMachineTypeTestScroll = function () {
				$scope.currentMachineTypeTestFPR.page = $scope.currentMachineTypeTestPage.number + 1;
				return getMachineTypeTestData($scope.currentMachineTypeTestFPR);
			};

			function getMachineTypeTestData(fpr) {
				if (fpr.page > $scope.currentMachineTypeTestPage.totalPages) {
					return;
				}
				addMachineTypeFilter(fpr);
				$scope.currentMachineTypeTestFPR = angular.copy(fpr);
				return driverAssaysService.getMachineTypeTestPage($scope.currentMachineTypeTestFPR)
					.then(function (response) {
						response.data.content = $scope.currentMachineTypeTestPage.content.concat(response.data.content);
						$scope.currentMachineTypeTestPage = response.data;
					});
			}

			function refreshMachineTypeTestData() {
				$scope.currentMachineTypeTestPage.content = [];
				var fpr = util.generateFilterablePageRequest();
				fpr.size = pageSize;
				getMachineTypeTestData(fpr);
			}

			$scope.onMachineTypeTestClick = function (machineTypeTest) {
				$scope.selectedMachineTypeTest = machineTypeTest;
				$scope.selectedTestAssay = machineTypeTest.testAssay;
			};

			$scope.clearForm = function () {

				$scope.selectedMachineTypeTest = {
					isActive: true
				};
				$scope.selectedTestAssay = {
					isAutoVerifiable: false
				};

				if ($scope.machineTypeTestForm) {
					$scope.machineTypeTestForm.$setPristine();
					$scope.machineTypeTestForm.$setUntouched();
				}
			};

			function addMachineTypeFilter(fpr) {
				if ($scope.selectedMachineType) {
					if (!fpr.filters) {
						fpr.filters = [];
					}
					fpr.filters.push({ field: "machineType.rid", value: $scope.selectedMachineType.rid, operator: "eq" });
				}
			}

			$scope.submitMachineTypeTest = function () {

				$scope.selectedMachineTypeTest.machineType = $scope.selectedMachineType;

				var api = null;
				if ($scope.selectedMachineTypeTest.rid) {
					api = driverAssaysService.updateMachineTypeTest;
				} else {
					api = driverAssaysService.createMachineTypeTest;
				}
				api($scope.selectedMachineTypeTest).then(function () {
					util.createToast(util.systemMessages.successTransaction, 'success');
					$scope.clearForm();
					refreshMachineTypeTestData();
				});

			};

			//initial fetch
			angular.element(function () {
				if (preSelectedMachineTypeRid) {
					$rootScope.$broadcast("onApexEntityAddition", { rids: [preSelectedMachineTypeRid], broadcastEvent: true });
				}

			});


		}
	]);
});
