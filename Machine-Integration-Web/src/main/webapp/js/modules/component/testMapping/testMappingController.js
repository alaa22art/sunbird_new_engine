define(['app', 'config', 'util'], function (app, config, util) {
	'use strict';
	app.controller('testMappingController', [
		'$scope',
		'testMappingService',
		'machineTypeSetupService',
		'$rootScope',
		function (
			$scope,
			testMappingService,
			machineTypeSetupService,
			$rootScope
		) {
			$scope.metaData = {};
			$scope.lkps = [];
			$scope.createMode = true;
			$scope.imageName = "no_machine";

			$scope.lowLevelProtocol = {
				className: "Protocol",
				name: "lowLevelProtocolId",
				labelText: util.systemMessages.lowLevelProtocol,
				valueField: "name." + util.userLocale,
				selectedValue: null,
				required: false,
				data: null
			};

			$scope.highLevelProtocol = {
				className: "Protocol",
				name: "highLevelProtocolId",
				labelText: util.systemMessages.highLevelProtocol,
				valueField: "name." + util.userLocale,
				selectedValue: null,
				required: false,
				data: null
			};

			$scope.driver = {
				className: "Driver",
				name: "driver",
				labelText: util.systemMessages.driver,
				valueField: "name",
				selectedValue: null,
				required: true,
				data: null
			};

			$scope.selectedValue = {
				code: null,
				name: null,
				isActive: true
			};
			$scope.selectedTest = null;
			$scope.selectedTestChanged = false;
			$scope.isEditModeAllow = false;
			$scope.metaData = {};
			$scope.lkps = [];
			$scope.createMode = true;
			$scope.selectedMachineType = ($rootScope.machineTypeId == null || $rootScope.machineTypeId == undefined) ? null : $rootScope.machineTypeId;

			machineTypeSetupService.getProtocolList().then(function (response) {
				$scope.lowLevelProtocol.data = response.data;
				$scope.highLevelProtocol.data = response.data;
			});

			machineTypeSetupService.getDriverList().then(function (response) {
				$scope.driver.data = response.data;
			});

			if ($scope.selectedMachineType != null) {

				machineTypeSetupService.getMachineTypeById($scope.selectedMachineType).then(function (response) {
					$scope.selectedMachineType = response.data;
					$scope.imageName = response.data.name;
					$scope.refreshGrid();
					$scope.lowLevelProtocol.selectedValue = $scope.selectedMachineType[$scope.lowLevelProtocol.name];
					$scope.highLevelProtocol.selectedValue = $scope.selectedMachineType[$scope.highLevelProtocol.name];
					$scope.driver.selectedValue = $scope.selectedMachineType[$scope.driver.name];
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
							"machineType": "machineTypeId.name",
							"testId": "testId.name",
							"requesterTestCode": "testId.requesterTestCode"
						}
						e.data = util.createFilterablePageRequest($scope.TestCatalogGridOptions.dataSource, filterMap);
						var obj = {
							field: "machineTypeId.rid",
							operator: "eq",
							value: $scope.selectedMachineType.rid,
							junctionOperator: "And"
						};


						e.data.filters.push(obj);

						testMappingService.getMachineTypeTestsPage(e.data).then(function (response) {
							e.success(response.data);
						}).catch(function (error) {
							e.error(error);
						});
					},

					create: function (e) {

						testMappingService.insertMachineTypeTest(e.data).then(function (response) {
							util.createToast(util.systemMessages.success, "success");
							$scope.refreshGrid();
						}).catch(function (error) {
							e.error(error);
						});
					},
					update: function (e) {

						testMappingService.updateMachineTypeTest(e.data).then(function (response) {
							util.createToast(util.systemMessages.success, "success");
							$scope.refreshGrid();
						}).catch(function (error) {
							e.error(error);
						});
					},
					destroy: function (e) {
						testMappingService.deleteMachineTypeTest(e.data).then(function (response) {
							util.createToast(util.systemMessages.success, "success");
							$scope.refreshGrid();
						}).catch(function () {
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
							defultHostCode: {
								type: "string",
								editable: true
							},
							name: {
								type: "string",
								editable: true
							},
							"testId": {
								type: "object",
								editable: true
							},
							"requesterTestCode": {
								type: "object",
								editable: false
							},
							description: {
								type: "string",
								editable: true
							},
							isActive: {
								type: "boolean",
								editable: true
							},
							resultCode: {
								type: "string",
								editable: true
							}
						}
					}
				}
			});

			var allTestCatalogs = [];

			$scope.clearFields = function () {
				$scope.selectedMachineType = null;
				$scope.testForm.$setPristine();
				$scope.testForm.$setUntouched();
				$scope.lowLevelProtocol.selectedValue = null;
				$scope.highLevelProtocol.selectedValue = null;
				$scope.driver.selectedValue = null;
				$scope.TestCatalogGrid.dataSource.data = [];
				$scope.imageName = 'no_machine';
				$rootScope.machineType = null;
			}

			function testCatalogDropDownList(container, options) {
				$('<input required name="' + options.field + '"/>')
					.appendTo(container)
					.kendoDropDownList({
						dataValueField: "rid",
						valueTemplate: function (dataItem) {
							return dataItem.name;
						},
						template: function (dataItem) {
							return dataItem.name;
						},
						filter: "contains",
						dataSource: {
							serverFiltering: true,
							schema: {
								model: {
									id: "rid"
								}
							},
							transport: {
								read: function (e) {

									var searchValue = "";
									if (e.data.filter) {
										var filters = e.data.filter.filters;
										for (var filterKey in filters) {
											// get the searched value in the drop down list
											if (filters[filterKey].operator == "contains") {
												searchValue = filters[filterKey].value;
											}
										}
									}

									var obj = {
										searchValue: searchValue,
										machineTypeId: $scope.selectedMachineType.rid
									};

									testMappingService.getTestCatalogListByName(obj).then(function (response) {
										if (searchValue == "") {
											allTestCatalogs = response.data;
										}
										e.success(response.data);
									}).catch(function () {});

								}
							}
						},
						dataBound: function (e) {
							var selectedIndex = e.sender.select();
							e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
							e.sender.trigger("change");
						}
					});
			}

			$scope.TestCatalogGridOptions = {
				columns: [
					/*{
						title: util.systemMessages.Status,
						columns: [{
								field: "isActive",
								title: util.systemMessages.isActive,
								width: 200,
								template: function (Data) {
									//$scope.foo.isPortOpen = Data.isPortOpen;

									if (Data.isActive) {
										return '<div class="mapIcon"><md-icon md-font-icon="fas fa-check-circle" class="mapActive"></md-icon></div>';
									} else {
										return '<div class="mapIcon"><md-icon md-font-icon="fas fa-ban" class="mapInactive"></md-icon></div>';
									}

								}
							}



						]
					},*/
					{
						title: util.systemMessages.Machine_Tests_Details,
						columns: [{
								field: "name",
								title: util.systemMessages.testName,
								width: 200
							}, {
								field: "description",
								title: util.systemMessages.description,
								width: 200
							},
							{
								field: "defultHostCode",
								title: util.systemMessages.defultHostCode,
								width: 200
							}
						]
					},
					{
						title: util.systemMessages.External_Tests_Details,
						columns: [{
								field: "testId",
								title: util.systemMessages.name,
								editor: testCatalogDropDownList,
								template: function (data) {
									if (data.testId == null) {
										return "";
									} else
										return data.testId.name;
								}
							},
							{
								field: "requesterTestCode",
								title: util.systemMessages.requesterTestCode,
								template: function (data) {
									if (data.testId == null) {
										return "";
									} else
										return data.testId.requesterTestCode;
								}
							}
						]
					},
				],
				editable: "inline",
				dataSource: dataSource,
				autoBind: false,
				reorderable: true,
				groupable: false,
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
					$scope.selectedTest = $scope.TestCatalogGrid.dataItem($scope.TestCatalogGrid.select());
				}
			}

			$scope.refreshGrid = function () {
				$scope.TestCatalogGrid.dataSource.read();
				$scope.TestCatalogGrid.refresh();
			};

			$scope.saveSelectedTest = function () {
				dataSource.sync();
				$scope.selectedTest = null;
				$scope.selectedTestChanged = false;
			};

			$scope.editMachineTypeDetails = function () {
				$scope.isEditModeAllow = true;
			};

			$scope.addMachineType = function () {
				$scope.selectedMachineType[$scope.lowLevelProtocol.name] = $scope.lowLevelProtocol.selectedValue;
				$scope.selectedMachineType[$scope.highLevelProtocol.name] = $scope.highLevelProtocol.selectedValue;
				$scope.selectedMachineType[$scope.driver.name] = $scope.driver.selectedValue;

				machineTypeSetupService.addMachineType($scope.selectedMachineType).then(function (response) {
					$scope.selectedMachineType = response.data;
					util.createToast(util.systemMessages.success, "success");
					$scope.refreshGrid();
				});
			}

			$scope.updateMachineType = function () {
				$scope.selectedMachineType[$scope.lowLevelProtocol.name] = $scope.lowLevelProtocol.selectedValue;
				$scope.selectedMachineType[$scope.highLevelProtocol.name] = $scope.highLevelProtocol.selectedValue;
				$scope.selectedMachineType[$scope.driver.name] = $scope.driver.selectedValue;

				machineTypeSetupService.updateMachineType($scope.selectedMachineType).then(function (response) {
					$scope.selectedMachineType = response.data;
					util.createToast(util.systemMessages.success, "success");
					$scope.refreshGrid();
				}).catch(function (error) {
					e.error(error);
				});
			}

			$scope.addSelectedTest = function () {
				var newSelectedTest = {
					description: "",
					name: "",
					testId: null,
					isActive: true,
					machineTypeId: $scope.selectedMachineType
				};
				dataSource.add(newSelectedTest);
				var data = dataSource.data();
				var dataItem = data[data.length - 1];
				$scope.editSelectedTest(dataItem);
			};

			$scope.editSelectedTest = function (dataItem) {
				$("#TestCatalogGrid").data("kendoGrid").editRow(dataItem);
				$("#TestCatalogGrid").data("kendoGrid").select("tr[data-uid=" + dataItem.uid + "]");
				$scope.selectedTestChanged = true;
			};

			$scope.deleteSelectedTest = function () {
				dataSource.remove($scope.selectedTest);
				dataSource.sync();
				$scope.selectedTest = null;
				$scope.selectedTestChanged = false;
			};

			$scope.cancelSelectedTests = function () {
				dataSource.cancelChanges();
				$scope.selectedTest = null;
				$scope.selectedTestChanged = false;
			};

			$scope.back = function () {
				window.history.back();
			};

		}
	]);
});