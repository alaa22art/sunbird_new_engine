define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('driversCtrl', [
		'$scope',
		'$rootScope',
		'$q',
		'driversService',
		'commonMethods',
		'lovService',
		function ($scope,
			$rootScope,
			$q,
			driversService,
			commonMethods,
			lovService) {

			var pageSize = 30; //must be high number

			$rootScope.$broadcast("onApexEntityPanelHide", false);


			$scope.currentMachineTypePage = { content: [], totalPages: 1000 }; //dummy totalPages
			$scope.currentMachineTypeFPR = util.generateFilterablePageRequest();
			$scope.currentMachineTypeFPR.size = pageSize;
			$scope.selectedMachineType = {
				isActive: true,
				isMachineNameRequired: false,
				isAutoVerify: false
			};

			$scope.lowLevelProtocolLkp = null;
			$scope.highLevelProtocolLkp = null;
			$scope.machineTypeMetaData = null;
			$scope.driverMetaData = null;
			var allProtocolLevels = [];

			commonMethods.retrieveMetaDataList(["MachineType", "Driver"]).then(function (response) {
				$scope.machineTypeMetaData = response.data["MachineType"];
				$scope.driverMetaData = response.data["Driver"];

				$q.all([lovService.getLkpByClass({ className: "LkpProtocol" })]).then(function (response) {

					$scope.lowLevelProtocolLkp = {
						className: 'LkpProtocolLevel',
						name: 'lkpProtocolLevel',
						labelText: 'lowLevelProtocol',
						valueField: 'name.' + util.userLocale,
						selectedValue: null,
						required: $scope.machineTypeMetaData.lowLevelProtocol.notNull,
						data: angular.copy(response[0])
					};

					$scope.highLevelProtocolLkp = {
						className: 'LkpProtocolLevel',
						name: 'lkpProtocolLevel',
						labelText: 'highLevelProtocol',
						valueField: 'name.' + util.userLocale,
						selectedValue: null,
						required: $scope.machineTypeMetaData.highLevelProtocol.notNull,
						data: angular.copy(response[0])
					};

				});


			});

			$scope.onProtocolChange = function (selectedValue) {
				$scope.highLevelProtocolLkp.selectedValue = null;
				$scope.lowLevelProtocolLkp.selectedValue = null;
				if (!selectedValue) {
					$scope.lowLevelProtocolLkp.data = [];
					$scope.highLevelProtocolLkp.data = [];
					return;
				}

				var filteredProtocols = allProtocolLevels.filter(function (obj) { return obj.protocol.rid === selectedValue.rid });
				$scope.lowLevelProtocolLkp.updateData(angular.copy(filteredProtocols));
				$scope.highLevelProtocolLkp.updateData(angular.copy(filteredProtocols));
			};

			$scope.machineTypeSearchOptions = {
				service: function (fpr) {
					return driversService.getMachineTypePage(fpr).then(function (response) {
						for (var key in response.data.content) {
							var obj = response.data.content[key];
							obj["customLabel"] = util.addParenthesis(obj.driver.name, obj.name);
						}
						return response;
					});
				},
				callback: function (filters) {
					if ($scope.machineTypeSearchOptions.selectedItem && $scope.machineTypeSearchOptions.selectedItem.rid !== -1) {
						var fpr = util.generateFilterablePageRequest();
						fpr.filters.push({
							field: "rid",
							value: $scope.machineTypeSearchOptions.selectedItem.rid,
							operator: "eq"
						});
						$scope.currentMachineTypePage.content = [];
						getMachineTypeData(fpr);
					} else if (filters) {
						var fpr = util.generateFilterablePageRequest();
						fpr.size = pageSize;
						fpr.filters = filters;
						$scope.currentMachineTypePage.content = [];
						getMachineTypeData(fpr);
					}
				},
				skeleton: { code: "customLabel", description: "customLabel" },
				filterList: ["name", "code"],
				sortList: [{ direction: "ASC", property: "name" }]
			};

			$scope.onMachineTypeScroll = function () {
				$scope.currentMachineTypeFPR.page = $scope.currentMachineTypePage.number + 1;
				return getMachineTypeData($scope.currentMachineTypeFPR);
			};

			function getMachineTypeData(fpr) {
				if (fpr.page > $scope.currentMachineTypePage.totalPages) {
					return;
				}
				$scope.currentMachineTypeFPR = angular.copy(fpr);
				return driversService.getMachineTypePage($scope.currentMachineTypeFPR)
					.then(function (response) {
						response.data.content = $scope.currentMachineTypePage.content.concat(response.data.content);
						$scope.currentMachineTypePage = response.data;
					});
			}

			$scope.onMachineTypeClick = function (machineType) {
				$scope.selectedMachineType = machineType;
				$scope.selectedMachineType["driverConfig"] = machineType.driver.configTxt;
				$scope.onProtocolChange(machineType.protocol);
				$scope.lowLevelProtocolLkp.selectedValue = machineType.lowLevelProtocol;
				$scope.highLevelProtocolLkp.selectedValue = machineType.highLevelProtocol;
			};

			$scope.clearForm = function () {
				$scope.machineTypeForm.$setPristine();
				$scope.machineTypeForm.$setUntouched();
				$scope.selectedMachineType = {
					isActive: true,
					isMachineNameRequired: false,
					isAutoVerify: false
				};
				$scope.onProtocolChange(null);
				$scope.lowLevelProtocolLkp.selectedValue = null;
				$scope.highLevelProtocolLkp.selectedValue = null;
			};

			$scope.submit = function () {
				var wrapper = {
					rid: $scope.selectedMachineType.rid ? $scope.selectedMachineType.rid : null,
					name: $scope.selectedMachineType.name,
					code: $scope.selectedMachineType.code,
					isActive: $scope.selectedMachineType.isActive,
					isMachineNameRequired: $scope.selectedMachineType.isMachineNameRequired,
					isAutoVerify: $scope.selectedMachineType.isAutoVerify,
					lowLevelProtocolRid: $scope.lowLevelProtocolLkp.selectedValue ? $scope.lowLevelProtocolLkp.selectedValue.rid : null,
					highLevelProtocolRid: $scope.highLevelProtocolLkp.selectedValue ? $scope.highLevelProtocolLkp.selectedValue.rid : null,
					driverRid: $scope.selectedMachineType.driver ? $scope.selectedMachineType.driver.rid : null,
					driverConfig: $scope.selectedMachineType.driverConfig
				};
				var apiRequest = null;
				if ($scope.selectedMachineType.rid) {
					apiRequest = driversService.updateMachineType;
				} else {
					apiRequest = driversService.createMachineType;
				}

				apiRequest(wrapper).then(function (response) {
					util.createToast(util.systemMessages.successTransaction, "success");
					$scope.clearForm();
					//update value in ui
					var mtRid = response.data.rid;
					driversService.getMachineTypePage({ filters: [{ field: "rid", value: mtRid, operator: "eq" }] })
						.then(function (response) {
							var prevIdx = $scope.currentMachineTypePage.content.map(function (obj) { return obj.rid; }).indexOf(mtRid);
							if (prevIdx !== -1) {
								$scope.currentMachineTypePage.content[prevIdx] = response.data.content[0];
							} else {
								$scope.currentMachineTypePage.content.push(response.data.content[0]);
							}
						});

				});

			};

			$scope.driverAssaysPage = function (machineType) {
				$rootScope.$broadcast("onApexNavigateTo", { path: "driver-assays", params: { machineTypeRid: machineType.rid } });
			};

			//initial fetch
			angular.element(function () {
				var fpr = util.generateFilterablePageRequest();
				fpr.size = pageSize;
				getMachineTypeData(fpr);
			});

		}
	]);
});
