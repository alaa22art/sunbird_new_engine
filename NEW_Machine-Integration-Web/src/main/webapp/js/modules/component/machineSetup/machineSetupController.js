define(['app', 'config', 'util'], function (app, config, util) {
	'use strict';
	app.controller('machineSetupCtrl', [
		'$scope',
		'machineSetupService',
		'machineAssignTestsService',
		'commonMethods',
		'$filter',
		'$rootScope',
		'$state',
		function (
			$scope,
			machineSetupService,
			machineAssignTestsService,
			commonMethods,
			$filter,
			$rootScope,
			$state
		) {
			$scope.metaData = {};
			$scope.lkps = [];
			$scope.createMode = true;

			$scope.selectedMachine = {
				isActive: false,
				isReceiveStateActive: false,
				isSendStateActive: false,
				isPortOpen: false,
				userName: null,
				password: null
			}
			var machineTypes = [];

			commonMethods.retrieveMetaData("Machine").then(function (response) {

				$scope.metaData = response.data;
				// to get any lkp without the need to use an LoV directive
				machineSetupService.getMachineTypeList().then(function (response) {
					machineTypes = response.data;
					$scope.lkps.push({
						className: "Machine",
						name: "machineType",
						labelText: util.systemMessages.machineType,
						valueField: "name", //util.userLocale,
						selectedValue: null,
						required: $scope.metaData.machineType.notNull,
						data: machineTypes
					});

				});
			});




			$scope.goToAssignTestPage = function () {

				$rootScope.machine = null;
				$state.go("machine-assign-tests");
			};

			$scope.refreshGrid = function () {
				$scope.machinesGrid.dataSource.read();
				$scope.machinesGrid.refresh();
			};

			$scope.saveMachine = function (isValid) {

				if (!isValid) {
					return;
				}
				for (var i in $scope.lkps) {
					var lkpValue = $scope.lkps[i];
					$scope.selectedMachine[lkpValue.name] = lkpValue.selectedValue;

				}
				machineSetupService.createMachine($scope.selectedMachine).then(function (response) {
					machineAssignTestsService.insertMachineTests($scope.selectedMachine).then(function (response) {
						util.createToast(util.systemMessages.success, "success");
						$scope.clearFields();
						$scope.refreshGrid();
					});
				});
			}

			$scope.updateMachine = function () {
				for (var i in $scope.lkps) {
					var lkpValue = $scope.lkps[i];
					$scope.selectedMachine[lkpValue.name] = lkpValue.selectedValue;
				}

				machineSetupService.updateMachine($scope.selectedMachine).then(function (response) {
					util.createToast($filter('translate')('updateInfo'), "success");
					$scope.clearFields();
					$scope.refreshGrid();
				});

			}

			$scope.clearFields = function () {
				$scope.machineForm.$setPristine();
				$scope.machineForm.$setUntouched();
				$scope.selectedMachine = {
					isActive: false,
					isReceiveStateActive: false,
					isSendStateActive: false,
					isPortOpen: false,
					userName: null,
					password: null
				}

				for (var i in $scope.lkps) {
					var lkpValue = $scope.lkps[i];
					lkpValue.selectedValue = null;
				}


			}

			$scope.activatePort = function () {
				machineSetupService.openConnection($scope.selectedMachine).then(function (response) {
					util.createToast($filter('translate')('openPort'), "success");
					$scope.refreshGrid();
					$scope.selectedMachine = null;
				});
			}


			$scope.deActivatePort = function () {
				machineSetupService.closeConnection($scope.selectedMachine).then(function (response) {
					util.createToast($filter('translate')('closePort'), "success");
					$scope.refreshGrid();
					$scope.selectedMachine = null;
				});
			}


			var dataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				serverPaging: true,
				serverFiltering: true,
				transport: {
					read: function (e) {
						var filterMap = {
							"machineType": "machineType.name"
						}
						e.data = util.createFilterablePageRequest($scope.machinesGridOptions.dataSource, filterMap);
						machineSetupService.getMachinePage(e).then(function (data) {

						}).catch(function (error) {
							e.error(error);
						});
					}
				},
				schema: {
					data: "data",
					total: "total",
					model: {
						id: "rid",
						fields: {
							name: {
								type: "string"
							},
							address: {
								type: "string"
							},
							machineIpAddress: {
								type: "string"
							},
							serverIpAddress: {
								type: "string"
							},
							machineType: {
								type: "object"
							},
							isPortOpen: {
								type: "boolean"
							}
						}
					}
				}
			});


			$scope.foo = {
				isPortOpen: false
			};
			$scope.machinesGridOptions = {
				columns: [{
						field: "name",
						title: util.systemMessages.MachineGridName
					},

					{
						field: "machineType",
						title: util.systemMessages.machineGridMachineType,
						template: function (Data) {
							if (Data.machineType == null) {
								return "";
							} else {
								return Data.machineType.name;
							}
						},
						filterable: {
							ui: machineTypesFilter
						}

					},
					{
						field: "machineIpAddress",
						title: util.systemMessages.machineIP
					},
					{
						field: "serverIpAddress",
						title: util.systemMessages.machineGridServerIpAddress
					},
					{
						field: "serverPort",
						title: util.systemMessages.machineGridserverPort
					},
					{
						field: "isPortOpen",
						title: util.systemMessages.isPortOpen,
						template: function (Data) {

							if (Data.isActive && Data.isPortOpen) {
								return "<md-input-container>" +
									'<div class="mapIcon"><md-icon md-font-icon="fas fa-check-circle" class="mapActive"></md-icon></div>' +
									"</md-input-container>";
							} else {
								return "<md-input-container>" +
									'<div class="mapIcon"><md-icon md-font-icon="fas fa-stop-circle" class="mapInactive"></md-icon></div>' +
									"</md-input-container>";
							}
						}
					},
					{
						field: "tests",
						title: util.systemMessages.tests,
						template: function (Data) {
							return '<div class="mapIcon" authority-checker="VIEW_MACHINE_TEST_MAPPING">' +
								'<md-tooltip md-direction="top">{{"EditDetails" | translate}}</md-tooltip>' +
								'<md-icon md-font-icon="fas fa-cog" ng-click="storeMachineId(' + Data.rid + ')"></md-icon>' +
								'</div>';
						}
					}
				],
				dataSource: dataSource,
				filterable: {
					extra: false,
					operators: {
						object: {
							eq: "Equal",
							neq: "Not Equal",
						}
					}
				},
				change: function () {

					$scope.selectedMachine = $scope.machinesGrid.dataItem($scope.machinesGrid.select());
					machineSetupService.getMachineById($scope.selectedMachine.rid).then(function (response) {
						$scope.selectedMachine = response.data;
						for (var i in $scope.lkps) {
							var lkpValue = $scope.lkps[i];
							lkpValue.selectedValue = $scope.selectedMachine[lkpValue.name];
						}

					});
				}
			};

			$scope.storeMachineId = function (rid) {
				var gridData = $scope.machinesGrid.dataSource.data();
				for (var idx = 0; idx < gridData.length; idx++) {
					if (rid == gridData[idx].rid) {
						$rootScope.machine = gridData[idx];
						$state.go("machine-assign-tests");
						break;
					}
				}

			};

			function machineTypesFilter(element) {

				element.kendoDropDownList({
					dataTextField: "name",
					dataValueField: "name",
					optionLabel: $filter('translate')('selectedType'),
					dataSource: machineTypes,
					autoBind: false,
					change: function (e) {}
				});
			}


		}
	]);
});