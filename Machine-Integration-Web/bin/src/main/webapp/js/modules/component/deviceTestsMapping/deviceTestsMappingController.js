define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('deviceTestsMappingCtrl', [
		'$scope',
		'$rootScope',
		'$q',
		'$stateParams',
		'deviceTestsService',
		'commonMethods',
		'lovService',
		'driverAssaysService',
		function (
			$scope,
			$rootScope,
			$q,
			$stateParams,
			deviceTestsService,
			commonMethods,
			lovService,
			driverAssaysService
		) {

			$rootScope.$broadcast('onApexModeChange', { type: "MACHINE", mode: "SINGLE" });

			var pageSize = 30; //must be high number

			$scope.machineTestPage = { content: [], totalPages: 1000 }; //dummy totalPages
			$scope.currentMachineTestFPR = util.generateFilterablePageRequest();
			$scope.currentMachineTestFPR.size = pageSize;
			$scope.machineTestMetaData = null;
			$scope.selectedMachine = null;
			$scope.selectedMachineTest = {
				isActive: false,
				isMasking: false
			};

			$scope.machineTestSearchOptions = {
				service: function (fpr) {
					addMachineFilter(fpr);
					return deviceTestsService.getMachineTestPage(fpr).then(function (response) {
						for (var key in response.data.content) {
							var obj = response.data.content[key];
							obj["label"] = util.addParenthesis(obj.machine.name, obj.testCatalog.requesterTestCode);
						}
						return response;
					});
				},
				callback: function (filters) {
					if ($scope.machineTestSearchOptions.selectedItem && $scope.machineTestSearchOptions.selectedItem.rid !== -1) {
						var fpr = util.generateFilterablePageRequest();
						fpr.page = 0;
						fpr.size = 1;
						fpr.filters.push({
							field: "rid",
							value: $scope.machineTestSearchOptions.selectedItem.rid,
							operator: "eq"
						});
						$scope.machineTestPage.content = [];
						$scope.getMachineTestPageData(fpr);
					} else if (filters) {
						var fpr = util.generateFilterablePageRequest();
						fpr.page = 0;
						fpr.size = pageSize;
						fpr.filters = fpr.filters.concat(filters);
						$scope.machineTestPage.content = [];
						$scope.getMachineTestPageData(fpr);
					}
				},
				skeleton: {
					code: "label",
					description: "label"
				},
				filterList: ["testCatalog.requesterTestCode"],
				sortList: [{ property: "testCatalog.requesterTestCode", direction: "ASC" }],
				label: "tests"
			};

			commonMethods.retrieveMetaData("MachineTest").then(function (response) {
				$scope.machineTestMetaData = response.data;
			});

			$scope.$on('onApexSelection', function (event, data) {
				$scope.selectedMachine = data[0];
				refreshMachineTestPageData();
			});

			$scope.$on('onApexRemove', function (event, data) {
				$scope.selectedMachine = null;

				$scope.clearForm();
				$scope.machineTestPage.content = [];
			});

			function refreshMachineTestPageData() {
				$scope.clearForm();
				$scope.machineTestPage.content = [];

				var fpr = util.generateFilterablePageRequest();
				fpr.size = pageSize;
				$scope.getMachineTestPageData(fpr);
			}

			$scope.onMachineTestScroll = function () {
				$scope.currentMachineTestFPR.page = $scope.machineTestPage.number + 1;
				return $scope.getMachineTestPageData($scope.currentMachineTestFPR);
			};

			$scope.getMachineTestPageData = function (fpr) {
				if (fpr.page > $scope.machineTestPage.totalPages) {
					return;
				}
				$scope.currentMachineTestFPR = angular.copy(fpr);
				addMachineFilter($scope.currentMachineTestFPR);
				return deviceTestsService.getMachineTestPage($scope.currentMachineTestFPR).then(function (response) {
					response.data.content = $scope.machineTestPage.content.concat(response.data.content);
					$scope.machineTestPage = response.data;
				});
			};

			$scope.onMachineTestClick = function (machineTest) {
				$scope.selectedMachineTest = machineTest;
			};

			$scope.deleteMachineTest = function (machineTest) {
				return deviceTestsService.deleteMachineTest(machineTest).then(function () {
					util.createToast(util.systemMessages.successTransaction, "success");
					$scope.machineTestPage.content = $scope.machineTestPage.content.filter(function (obj) { return obj.rid !== machineTest.rid; });
					$scope.clearForm();
				});
			};

			$scope.submit = function () {
				$scope.selectedMachineTest.machine = $scope.selectedMachine;
				//$scope.selectedMachineTest.testCatalog = $scope.selectedMachineTest.machineTypeTest.testCatalog;
				var api = null;

				if ($scope.selectedMachineTest.rid) {
					api = deviceTestsService.updateMachineTest
				} else {
					api = deviceTestsService.createMachineTest
				}

				api($scope.selectedMachineTest).then(function (response) {
					util.createToast(util.systemMessages.successTransaction, "success");
					$scope.clearForm();
					var ridFilter = { field: "rid", value: response.data.rid, operator: "eq" };
					deviceTestsService.getMachineTestPage({ filters: [ridFilter] }).then(function (innerResponse) {
						var idx = $scope.machineTestPage.content.findIndex(function (obj) { return obj.rid === response.data.rid; });
						if (idx !== -1) {
							$scope.machineTestPage.content[idx] = innerResponse.data.content[0];
						} else {
							$scope.machineTestPage.content.push(innerResponse.data.content[0]);
						}

					});
				});
			};

			$scope.clearForm = function () {
				if ($scope.form) {
					$scope.form.$setPristine();
					$scope.form.$setUntouched();
				}

				$scope.selectedMachineTest = {
					isActive: false,
					isMasking: false
				};
			};

			function addMachineFilter(fpr) {
				if (!$scope.selectedMachine) {
					return;
				}

				util.addOrReplaceToFilters(fpr.filters, { field: "machine.rid", value: $scope.selectedMachine.rid, operator: "eq" });

			}

			//initial fetch
			angular.element(function () {
				refreshMachineTestPageData();
			});

		}
	]);
});