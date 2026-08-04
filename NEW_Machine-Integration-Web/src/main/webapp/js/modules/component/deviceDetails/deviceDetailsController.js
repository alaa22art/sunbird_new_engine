define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('deviceDetailsCtrl', [
		'$scope',
		'$rootScope',
		'$q',
		'$stateParams',
		'devicesService',
		'commonMethods',
		'lovService',
		'driversService',
		function (
			$scope,
			$rootScope,
			$q,
			$stateParams,
			devicesService,
			commonMethods,
			lovService,
			driversService
		) {

			$rootScope.$broadcast('onApexModeChange', { type: "MACHINE", mode: "SINGLE" });
			var preSelectedMachineRid = $stateParams.params ? $stateParams.params.machineRid : null;
			var sslCertificateValue = null;

			$scope.isDeviceReachable = null;
			$scope.machineMetaData = null;
			$scope.selectedMachineType = null;
			$scope.selectedMachine = {
				isActive: false,
				isPanelOrder: false,
				requestTestWithNoResultOnly: false,
				requestNewTestOnly: false,
				isPortOpen: false,
				isConnected: false,
				isReceiveResult: false,
				isSendOrder: false
			};

			$scope.$on('onApexSelection', function (event, data) {
				$scope.getMachineData(data[0].rid);
			});

			$scope.$on('onApexRemove', function (event, data) {
				$scope.clearForm();
			});

			commonMethods.retrieveMetaData("Machine").then(function (response) {
				$scope.machineMetaData = response.data;
				$scope.machineTypeSearchOptions.required = $scope.machineMetaData.machineType.notNull;
			});

			$("#sslCertificate").on("change", function (event) {
				var file = event.target.files[0];
				var reader = new FileReader();
				reader.onload = function (loadEvent) {
					sslCertificateValue = file.name + "|" + loadEvent.target.result;//base64
				};
				reader.readAsDataURL(file);
			});

			$scope.resetSSLCertificate = function () {
				$("#sslCertificate").val("");
				sslCertificateValue = null;
				if ($scope.selectedMachine) {
					$scope.selectedMachine.sslCertificate = null;
				}
			};

			$scope.machineTypeSearchOptions = {
				service: driversService.getMachineTypePage,
				callback: function () {
					if ($scope.machineTypeSearchOptions.selectedItem && $scope.machineTypeSearchOptions.selectedItem.rid !== -1) {
						$scope.selectedMachineType = $scope.machineTypeSearchOptions.selectedItem;
					} else {
						$scope.selectedMachineType = null;
					}

					$scope.selectedMachine.machineType = $scope.selectedMachineType;
				},
				skeleton: { code: "name", description: "name" },
				filterList: ["name"],
				sortList: [{ direction: "ASC", property: "name" }],
				label: "driver",
				required: true
			};

			$scope.submit = function () {
				var api = null;
				if ($scope.selectedMachine.rid) {
					api = devicesService.updateMachine;
				} else {
					api = devicesService.createMachine;
				}
				$scope.selectedMachine.sslCertificate = sslCertificateValue;
				api($scope.selectedMachine).then(function (response) {
					util.createToast(util.systemMessages.successTransaction, "success");
					$scope.getMachineData(response.data.rid);
				});
			};


			$scope.getMachineData = function (rid) {
				$scope.clearForm();
				if (!rid) {
					return;
				}
				devicesService.getMachineData(rid).then(function (response) {
					$scope.selectedMachine = response.data;
					$scope.machineTypeSearchOptions.required = false;
					sslCertificateValue = $scope.selectedMachine.sslCertificate;
				});
			};

			$scope.clearForm = function (userEvent) {

				$scope.selectedMachine = {
					isActive: false,
					isPanelOrder: false,
					requestTestWithNoResultOnly: false,
					requestNewTestOnly: false,
					isPortOpen: false,
					isConnected: false,
					isReceiveResult: false,
					isSendOrder: false
				};

				if (userEvent) {
					$rootScope.$broadcast("onApexEntityRemoveAll", true);
				}

				$scope.resetSSLCertificate();

				if ($scope.machineTypeSearchOptions && $scope.machineTypeSearchOptions.reset) {
					$scope.machineTypeSearchOptions.reset();
				}

				if ($scope.form) {
					$scope.form.$setPristine();
					$scope.form.$setUntouched();
				}

			};

			$scope.testConnection = function () {
				$scope.isDeviceReachable = null;//reset
				var wrapper = {
					port: $scope.selectedMachine.serverPort,
					ip: $scope.selectedMachine.machineIpAddress
				};
				devicesService.testConnection(wrapper).then(function (response) {
					$scope.isDeviceReachable = response.data;
				});
			};

			$scope.onMachineTypeRemove = function () {
				$scope.selectedMachine.machineType = null;
				$scope.machineTypeSearchOptions.required = $scope.machineMetaData.machineType.notNull;
				$scope.machineTypeSearchOptions.reset();
			};

			//initial fetch
			angular.element(function () {
				if (preSelectedMachineRid) {
					$scope.getMachineData(preSelectedMachineRid);
					$rootScope.$broadcast("onApexEntityAddition", { rids: [preSelectedMachineRid], broadcastEvent: false });
				}

			});

		}
	]);
});
