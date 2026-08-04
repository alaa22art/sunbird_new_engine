define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('devicesCtrl', [
		'$scope',
		'$rootScope',
		'devicesService',
		'socketService',
		function (
			$scope,
			$rootScope,
			devicesService,
			socketService
		) {

			var pageSize = 30; //must be high number

			$scope.machinePage = { content: [], totalPages: 1000 };
			$scope.currentMachineFPR = util.generateFilterablePageRequest();
			$scope.currentMachineFPR.size = pageSize;
			$scope.onApexEntityPanelHide = true;

			$rootScope.$broadcast("onApexEntityPanelHide", false);

			$scope.machineSearchOptions = {
				service: devicesService.getMachinePage,
				callback: function (filters) {
					if ($scope.machineSearchOptions.selectedItem && $scope.machineSearchOptions.selectedItem.rid !== -1) {
						var fpr = util.generateFilterablePageRequest();
						fpr.filters.push({
							field: "rid",
							value: $scope.machineSearchOptions.selectedItem.rid,
							operator: "eq"
						});
						$scope.machinePage.content = [];
						getMachinePageData(fpr);
					} else if (filters) {
						var fpr = util.generateFilterablePageRequest();
						fpr.size = pageSize;
						fpr.filters = filters;
						$scope.machinePage.content = [];
						getMachinePageData(fpr);
					}
				},
				skeleton: { code: "name", description: "name" },
				filterList: ["name"],
				sortList: [{ property: "name", direction: "ASC" }]
			};

			$scope.$on("onSocketReceive", function (event, eventData) {
				if (!eventData || eventData.type !== socketService.types.MACHINE_CONNECTION ||
					util.isArrayEmpty($scope.machinePage.content)) {
					return;
				}
				//get only our currently viewed machine rids
				var machineRids = $scope.machinePage.content
					.filter(function (obj) { return eventData.data.indexOf(obj.rid) !== -1; })
					.map(function (obj) { return obj.rid; });
				if (util.isArrayEmpty(machineRids)) {
					return;
				}

				//fetch to update the changed machines
				devicesService.getMachineList({ filters: [{ field: "rid", value: machineRids, operator: "in" }] })
					.then(function (response) {
						for (var key in response.data) {
							var machine = $scope.machinePage.content
								.find(function (obj) { return obj.rid === response.data[key].rid; });
							if (machine) {
								machine.isConnected = response.data[key].isConnected;
							}
						}
					});

			});

			$scope.onMachineScroll = function () {
				$scope.currentMachineFPR.page = $scope.machinePage.number + 1;
				return getMachinePageData($scope.currentMachineFPR);
			};

			function getMachinePageData(fpr) {
				if (fpr.page > $scope.machinePage.totalPages) {
					return;
				}
				$scope.currentMachineFPR = angular.copy(fpr);
				return devicesService.getMachineDevicesPageData($scope.currentMachineFPR).then(function (response) {
					response.data.content = $scope.machinePage.content.concat(response.data.content);
					$scope.machinePage = response.data;
				});
			}


			$scope.togglePortConnection = function (machine, isOpen) {
				if (!machine.isPortOpen && isOpen) {
					devicesService.openConnection(machine).then(function () {
						util.createToast(util.systemMessages.openPort, "success");
						refreshSingleMachine(machine.rid);
					});
				} else if (machine.isPortOpen && !isOpen) {
					devicesService.closeConnection(machine).then(function () {
						util.createToast(util.systemMessages.closePort, "success");
						refreshSingleMachine(machine.rid);
					});
				};
			};


			function refreshSingleMachine(rid) {
				var fpr = util.generateFilterablePageRequest();
				fpr.filters = [{ field: "rid", value: rid, operator: "eq" }];
				devicesService.getMachineDevicesPageData(fpr).then(function (response) {
					var idx = $scope.machinePage.content.findIndex(function (obj) { return obj.rid === rid; });
					$scope.machinePage[idx] = response.data.content[0];
				});
			}

			$scope.deleteMachine = function (machine) {
				return devicesService.deleteMachine(machine.rid).then(function () {
					util.createToast(util.systemMessages.closePort, "success");
					$scope.machinePage.content = $scope.machinePage.content.filter(function (obj) { return obj.rid !== machine.rid; });
				});
			};

			$scope.goToMachineDetails = function (machine) {
				$rootScope.$broadcast("onApexNavigateTo", { path: "device-details", params: { machineRid: machine.rid } });
			};

			function refreshMachinePageData() {
				$scope.machinePage.concat = [];
				var fpr = util.generateFilterablePageRequest();
				fpr.size = pageSize;
				getMachinePageData(fpr);
			}

			//initial fetch
			angular.element(function () {
				refreshMachinePageData();
			});


		}
	]);
});
