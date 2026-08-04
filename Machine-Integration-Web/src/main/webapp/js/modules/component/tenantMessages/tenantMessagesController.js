define(['app', 'config', 'util'], function (app, config, util) {
	'use strict';
	app.controller('tenantMessagesCtrl', [
		'$scope',
		'tenantMessagesService',
		'commonMethods',
		'lovService',
		function (
			$scope,
			tenantMessagesService,
			commonMethods,
			lovService
		) {
			$scope.tenantMessageCode = null;
			$scope.tenantMessage = null;
			$scope.metaData = {};
			$scope.lkps = [];
			$scope.transFields = {};
			$scope.createMode = true;
			var originalMessageCode = null;

			commonMethods.retrieveMetaData("ComTenantMessage").then(function (response) {
				$scope.metaData = response.data;
				$scope.lkps = [{
					className: "LkpMessagesType",
					name: $scope.metaData.lkpMessagesType.name,
					labelText: "messageType",
					valueField: "name." + util.userLocale,
					selectedValue: null,
					required: $scope.metaData.lkpMessagesType.notNull
				}];

				$scope.transFields = {
					description: util.getTransFieldLanguages("description", "description", null, $scope.metaData.description.notNull)
				};
			});

			$scope.submitTenantMessage = function (isValid) {
				if (!isValid) {
					return;
				}
				$scope.tenantMessage.code = $scope.tenantMessageCode;
				$scope.lkps[0].assignValues($scope.tenantMessage, $scope.lkps, null);

				tenantMessagesService.createTenantMessage($scope.tenantMessage).then(function () {
					util.createToast(util.systemMessages.success, "success");
					$scope.clearFields();
					$scope.refreshGrid();
				}).catch(function () {

				});
			};

			$scope.refreshGrid = function () {
				$scope.tenantMessagesGrid.dataSource.read();
			};

			$scope.updateTenantMessage = function (isValid) {
				if (!isValid) {
					return;
				}
				//incase selecting a message then changing its code then delete rid to create new one
				if (originalMessageCode !== $scope.tenantMessageCode) {
					delete $scope.tenantMessage.rid;
				}
				$scope.tenantMessage.code = $scope.tenantMessageCode;

				$scope.lkps[0].assignValues($scope.tenantMessage, $scope.lkps, null);

				tenantMessagesService.updateTenantMessage($scope.tenantMessage).then(function () {
					util.createToast(util.systemMessages.success, "success");
					$scope.clearFields();
					$scope.refreshGrid();
				}).catch(function () {

				});

			};
			$scope.clearFields = function () {
				$scope.tenantMessagesForm.$setPristine();
				$scope.tenantMessagesForm.$setUntouched();
				$scope.tenantMessage = null;
				$scope.tenantMessageCode = null;
				$scope.createMode = true;
				$scope.lkps[0].clearLkps($scope.lkps);
				for (var i in util.languages) {
					var descriptionLang = $scope.transFields.description[i];
					descriptionLang.value = null;
				}
			};

			$scope.deleteTenantMessage = function () {
				return tenantMessagesService.deleteTenantMessage($scope.tenantMessage).then(function () {
					util.createToast(util.systemMessages.success, "success");
					$scope.clearFields();
					$scope.refreshGrid();
				});
			}

			var tenantMessagesColumns = [{
					field: "code",
					title: util.systemMessages.code,
					width: "15%"
				},
				{
					field: "lkpMessagesTypeRid",
					title: util.systemMessages.messageType,
					width: "15%",
					template: function (dataItem) {
						return dataItem.lkpMessagesType ? dataItem.lkpMessagesType.name[util.userLocale] : "";
					},
					filterable: {
						ui: function (element) {
							util.createLovFilter(element, {
								className: "LkpMessagesType"
							}, lovService.getLkpByClass);
						}
					}
				}
			];
			var tenantMessagesModels = {
				id: "rid",
				fields: {
					"code": {
						type: "string"
					},
					"description": {
						defaultValue: {}
					},
					"lkpMessagesType": {
						defaultValue: {}
					},
					"lkpMessagesTypeRid": {
						from: "lkpMessagesType.rid",
						type: "lov"
					}
				}
			};

			var languages = angular.copy(util.user.tenantLanguages);
			languages.sort(function (a, b) {
				return (a.isPrimary === b.isPrimary) ? 0 : a.isPrimary ? -1 : 1
			}); //primary first
			for (var objKey in languages) {
				var obj = languages[objKey];
				var column = {
					field: ("description_" + obj.comLanguage.locale),
					title: util.systemMessages.description + " (" + obj.comLanguage.shortcutName + ")"
				};

				tenantMessagesModels.fields[("description_" + obj.comLanguage.locale)] = {
					from: "description." + obj.comLanguage.locale,
					type: "string"
				};
				tenantMessagesColumns.splice(1 + parseInt(objKey), 0, column);
			}

			var dataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				transport: {
					read: function (e) {
						tenantMessagesService.getTenantMessagesList().then(function (response) {
							e.success(response.data);
							util.prepareSystemMessages(response.data);
						}).catch(function () {});
					}
				},
				schema: {
					parse: function (response) {
						var descriptionKeys = [];
						for (var idx = 1; idx < tenantMessagesColumns.length - 1; idx++) {
							descriptionKeys.push(tenantMessagesColumns[idx].field);
						}
						for (var i = 0; i < response.length; i++) {
							var obj = response[i];
							for (var j = 0; j < descriptionKeys.length; j++) {
								var languageKey = descriptionKeys[j].substring(descriptionKeys[j].indexOf("_") + 1);
								if (obj.description[languageKey] == null) {
									obj.description[languageKey] = "";
								}
								obj[descriptionKeys[j]] = obj.description[languageKey];
							}
						}
						return response;
					},
					model: tenantMessagesModels
				}
			});

			$scope.tenantMessagesGridOptions = {
				columns: tenantMessagesColumns,
				dataSource: dataSource,
				change: function () {
					$scope.tenantMessage = $scope.tenantMessagesGrid.dataItem($scope.tenantMessagesGrid.select());
					$scope.tenantMessageCode = $scope.tenantMessage.code;
					originalMessageCode = angular.copy($scope.tenantMessage.code);

					$scope.createMode = false;

					$scope.lkps[0].setValues($scope.tenantMessage, $scope.lkps, null);
				}
			};
		}
	]);
});