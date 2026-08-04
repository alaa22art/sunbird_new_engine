define(['app', 'config'], function (app, config) {
	'use strict';
	app.service('realTimePCRService', ["$http", function ($http) {
		this.addPcrWorkList = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrWorkList.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getAllPcrWorkLists = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getAllPcrWorkLists.srvc",
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getPcrWorkListPage = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getPcrWorkListPage.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.changeWorkListStatus = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "changeWorkListStatus.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getPcrWorkListOrders = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getPcrRealTimeWorkListOrdersById.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getPcrTemplateLines = function () {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getResultTemplateLines.srvc"
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getPcrRealTimeActualResultValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getPcrRealTimeActualResultValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addPcrOrderToWorkList = function (e, rid) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + `addPcrOrderToWorkList.srvc?rid=${rid}`,
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addPcrWorkListOrder = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrWorkListOrder.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.updateWorkListOrder = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "updateWorkListOrder.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addPcrRealTimeOrder = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrOrder.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};


		this.unloadPcrWorkListOrder = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "unloadPcrWorkListOrder.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addPcrRealTimeActualResultValue = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrRealTimeActualResultValue.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addActualResultValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrRealTimeActualResultValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.addPcrActualResultValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addPcrRealTimeActualResultValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.updatePcrActualResultValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "updatePcrActualResultValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};


		this.savePcrActualResultValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "savePcrActualResultValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.deletePcrRealTimeWorkListById = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "deletePcrRealTimeWorkListById.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.sendPcrRealTimeActualValues = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "sendPcrRealTimeActualValues.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

	}]);
});