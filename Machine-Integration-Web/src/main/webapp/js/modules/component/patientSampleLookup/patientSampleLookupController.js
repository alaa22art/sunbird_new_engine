define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('patientSampleLookupCtrl', [
		'$scope',
		'$rootScope',
		'$timeout',
		'$filter',
		'$stateParams',
		'branchFormService',
		'messegesTransactionLogService',
		'machineResultsService',
		function ($scope,
			$rootScope,
			$timeout,
			$filter,
			$stateParams,
			branchFormService,
			messegesTransactionLogService,
			machineResultsService) {

			var ordersData = [];
			var expandedObj = null;
			var orderGridPager = null;

			$scope.sampleBarcode = $rootScope.patientBarcode ? $rootScope.patientBarcode : null;
			$scope.selectedMachineOrder = null;
			$scope.currentView = "ORDER";//ORDER,RESULT

			$scope.branchLkp = {
				className: 'LabBranch',
				name: 'labBranch',
				labelText: 'branch',
				valueField: 'label',
				selectedValue: null,
				required: true,
				data: []
			};

			branchFormService.getBranchesWithAllOption({ filters: [] }).then(function (data) {
				$scope.branchLkp.selectedValue = data.find(function (obj) { return obj.rid === -1; });
				if ($scope.branchLkp.updateData) {
					$scope.branchLkp.updateData(data);
				} else {
					$scope.branchLkp.data = data;
				}
				var branchRid = $stateParams.params ? $stateParams.params.branchRid : null;
				if (branchRid) {
					$scope.branchLkp.selectedValue = data.find(function (obj) { return obj.rid === branchRid; });
				}

			});

			$scope.onTabSelection = function (viewName) {
				$scope.currentView = viewName;
			};

			//custom datasource for order grid
			var orderDataSource = new kendo.data.DataSource({
				transport: {
					read: function (e) {

						e.data = util.createFilterablePageRequest(orderDataSource);

						if ($scope.sampleBarcode) {
							e.data.filters.push({ field: "barcode", value: $scope.sampleBarcode, operator: "eq" });
						}

						if ($scope.branchLkp && $scope.branchLkp.selectedValue && $scope.branchLkp.selectedValue.rid !== -1) {
							e.data.filters.push({ field: "branchId", value: $scope.branchLkp.selectedValue.rid, operator: "eq" });
						}

						e.data.page = orderGridPager.page() == 0 ? orderGridPager.page() : orderGridPager.page() - 1;
						e.data.size = orderGridPager.pageSize();
						messegesTransactionLogService.getMachineOrderPage(e.data).then(function (response) {
							ordersData = response.data.content;
							treeOrderDataSource.read();
							e.success(response.data);
						});
					}
				},
				pageSize: config.gridPageSizes[0],
				page: 1,
				serverPaging: true,
				serverFiltering: true,
				schema: {
					data: "content",
					total: "totalElements",
					model: { id: "rid" }
				}
			});

			var treeOrderDataSource = new kendo.data.TreeListDataSource({
				transport: {
					read: function (e) {
						if (util.isArrayEmpty(ordersData)) {
							e.success([]);
							return;
						}
						//fetch results
						if (e.data.id && expandedObj) {
							var fpr = {
								filters: [{
									field: "machineOrder.rid",
									value: expandedObj.rid,
									operator: "eq"
								}],
								sortList: [{
									direction: "DESC",
									property: "rid"
								}]
							};
							//exclude latest result
							if (expandedObj.transients.latestResult) {
								fpr.filters.push({
									field: "rid",
									value: expandedObj.transients.latestResult.rid,
									operator: "neq"
								});
							}
							machineResultsService.getMachineResultList(fpr).then(function (response) {
								e.success(response.data);
							});
						} else {
							expandedObj = null;
							e.success(ordersData);
						}

					}
				},
				schema: {
					parse: function (data) {
						var results = [];
						if (util.isArrayEmpty(data)) {
							return results;
						}

						//expanded a machine order
						if (expandedObj) {
							for (var key in data) {
								var result = data[key];
								result["parentRid"] = expandedObj.rid;
								results.push(result);
							}
						} else {
							for (var key in data) {
								var order = data[key];
								order["parentRid"] = null;
								order["hasChildren"] = order.transients.resultsCount != null && order.transients.resultsCount > 1;
								results.push(order);
							}
						}

						return results;
					},
					model: {
						id: "rid",
						parentId: "parentRid",
						expanded: false,
						fields: {
							"rid": { type: "number" },
							"parentRid": { type: "number", nullable: true }
						}
					}
				}
			});

			$scope.treeOrderGridOptions = {
				columns: [
					{
						expandable: true,
						width: 200,
						field: "branch",
						title: util.systemMessages.branch,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							return dataItem.transients.branch ? dataItem.transients.branch.name[util.userLocale] : "";
						}
					},
					{
						field: "name",
						width: 200,
						title: util.systemMessages.name,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							var result = "";
							if (dataItem.patientFirstName) {
								result += " " + dataItem.patientFirstName;
							}
							if (dataItem.patientLastName) {
								result += " " + dataItem.patientLastName;
							}
							if (dataItem.gender) {
								result += " " + dataItem.gender;
							}
							if (dataItem.transients.patientAge != null) {
								result += " " + util.generateAgeString(dataItem.transients.patientAge);
							}
							return result;
						}
					},
					{
						field: "specimenCollectionDateAndTime",
						width: 200,
						title: util.systemMessages.collected,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							return dataItem.specimenCollectionDateAndTime ? $filter("dateTimeFormat")(dataItem.specimenCollectionDateAndTime) : "";
						}
					},
					{
						field: "order",
						width: 200,
						title: util.systemMessages.order,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							return dataItem.orderId ? dataItem.orderId : "";
						}
					},
					{
						field: "creationDate",
						width: 200,
						title: util.systemMessages.ordered,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							return dataItem.creationDate ? $filter("dateTimeFormat")(dataItem.creationDate) : "";
						}
					},
					{
						field: "barcode",
						width: 200,
						title: util.systemMessages.barcode,
						template: function (dataItem) {
							if (dataItem.parentRid) {
								return "";
							}
							return dataItem.barcode ? dataItem.barcode : "";
						}
					},
					{
						field: "code",
						width: 200,
						title: util.systemMessages.code,
						template: function (dataItem) {
							var result = "";
							var obj = dataItem.parentRid ? dataItem : dataItem.transients.latestResult;
							if (obj) {
								result = obj.resultCode ? obj.resultCode : (obj.testCode ? obj.testCode : "");
							}
							return result;
						}
					},
					{
						field: "result",
						width: 200,
						title: util.systemMessages.result,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.dataOrMeasurementValue ? dataItem.dataOrMeasurementValue : "";
							} else {
								result = dataItem.transients.latestResult && dataItem.transients.latestResult.dataOrMeasurementValue ?
									dataItem.transients.latestResult.dataOrMeasurementValue : "";
							}
							return result;
						}
					},
					{
						field: "unit",
						width: 200,
						title: util.systemMessages.unit,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.unit ? dataItem.unit : "";
							} else {
								result = dataItem.transients.latestResult && dataItem.transients.latestResult.unit ? dataItem.transients.latestResult.unit : "";
							}
							return result;
						}
					},
					{
						field: "completed",
						width: 200,
						title: util.systemMessages.completed,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.testCompletedDateTime ? $filter("dateTimeFormat")(dataItem.testCompletedDateTime) : "";
							} else {
								result = dataItem.transients.latestResult && dataItem.transients.latestResult.testCompletedDateTime ?
									$filter("dateTimeFormat")(dataItem.transients.latestResult.testCompletedDateTime) : "";
							}
							return result;
						}
					},
					{
						field: "device",
						width: 200,
						title: util.systemMessages.device,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.machineName;
							} else {
								result = dataItem.transients.latestResult && dataItem.transients.latestResult.machine ? dataItem.transients.latestResult.machine.name : "";
							}
							return result;
						}
					}
				],
				dataSource: treeOrderDataSource,
				autoBind: false,
				change: function (e) {
					var selectedRow = e.sender.dataItem(e.sender.select());
					if (!selectedRow.parentRid) {
						$scope.selectedMachineOrder = selectedRow;
					} else {
						$scope.selectedMachineOrder = null;
					}
				},
				expand: function (e) {
					expandedObj = angular.copy(e.model);//get the expanded object
				}
			};

			$scope.refreshOrdersGrid = function () {
				$scope.selectedMachineOrder = null;
				orderDataSource.read();
			};

			var resultsDataSource = new kendo.data.TreeListDataSource({
				transport: {
					read: function (e) {
						e.success([]);
						return;
						e.data = util.createFilterablePageRequest(resultsDataSource);
						e.data.filters.push({ field: "machineOrder", value: null, operator: "isnull" });
						machineResultsService.getResultPatientSampleLookupData(e.data).then(function (response) {
							e.success(response.data);
						});
					}
				},
				schema: {
					data: "content",
					total: "totalElements",
					model: {
						id: "rid",
						fields: {
							"rid": { type: "number" }
						}
					}
				}
			});

			$scope.resultsGridOptions = {
				columns: [
					{

						field: "branch",
						title: util.systemMessages.branch,
						template: function (dataItem) {
							return dataItem.transients.branch ? dataItem.transients.branch.name[util.userLocale] : "";
						}
					},
					{
						field: "name",
						title: util.systemMessages.name,
						template: function (dataItem) {
							var result = "";
							if (dataItem.patientFirstName) {
								result += " " + dataItem.patientFirstName;
							}
							if (dataItem.patientLastName) {
								result += " " + dataItem.patientLastName;
							}
							if (dataItem.gender) {
								result += " " + dataItem.gender;
							}
							if (dataItem.transients.patientAge != null) {
								result += " " + dataItem.transients.patientAge.age + dataItem.transients.patientAge.unit;
							}
							return result;
						}
					},
					{
						field: "specimenCollectionDateAndTime",
						title: util.systemMessages.collected,
						template: function (dataItem) {
							return dataItem.specimenCollectionDateAndTime ? $filter("dateTimeFormat")(dataItem.specimenCollectionDateAndTime) : "";
						}
					},
					{
						field: "warnings",
						title: util.systemMessages.warnings,
						template: function (dataItem) {
							if (dataItem.parentRid) {

							}
							return dataItem.transients.warningsCount != null ? dataItem.transients.warningsCount : "";
						}
					},
					{
						field: "code",
						title: util.systemMessages.code,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.testCode ? dataItem.testCode : "";
							}
							return result;
						}
					},
					{
						field: "result",
						title: util.systemMessages.result,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.dataOrMeasurementValue ? dataItem.dataOrMeasurementValue : "";
							}
							return result;
						}
					},
					{
						field: "unit",
						title: util.systemMessages.unit,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.unit ? dataItem.unit : "";
							}
							return result;
						}
					},
					{
						field: "completed",
						title: util.systemMessages.completed,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.testCompletedDateTime ? $filter("dateTimeFormat")(dataItem.testCompletedDateTime) : "";
							}
							return result;
						}
					},
					{
						field: "device",
						title: util.systemMessages.device,
						template: function (dataItem) {
							var result = "";
							if (dataItem.parentRid) {
								result = dataItem.machineName;
							}
							return result;
						}
					}
				],
				dataSource: resultsDataSource,
			};

			$scope.refreshResultsGrid = function () {
				resultsDataSource.read();
			};

			//initial fetch
			angular.element(function () {
				$timeout(function () {
					//make sure element exists
					orderGridPager = $("#orderGridPager").kendoPager({
						dataSource: orderDataSource,
						pageSizes: config.gridPageSizes,
						buttonCount: config.gridPageButtonCount
					}).data("kendoPager");

					$scope.refreshOrdersGrid();
					$scope.refreshResultsGrid();
				});

			});

		}
	]);
});
