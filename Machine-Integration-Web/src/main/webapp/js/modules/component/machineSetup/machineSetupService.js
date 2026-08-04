define(['app', 'config'], function (app, config) {
	'use strict';
	app.service('machineSetupService', ["$http", function ($http) {
		this.getMachineList = function () {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getMachineList.srvc"
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getMachineTypeList = function () {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getMachineTypeList.srvc"
			}).then(function successCallback(response) {
				return response;
			});
		};
		this.getMachinePage = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getMachinePage.srvc",
				data: JSON.stringify(e.data)
			}).then(function successCallback(response) {
				e.success(response.data);
				return response.data;
			});
		};

		this.getMachineQueryPage = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getMachineQueryPage.srvc",
				data: JSON.stringify(e.data)
			}).then(function successCallback(response) {
				e.success(response.data);
			});
		};

		this.closeConnection = function (machine) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "closeConnection.srvc",
				data: JSON.stringify(machine)
			}).then(function successCallback(response) {
				//e.success(response.data);
				return response.data;
			});
		};

		this.openConnection = function (machine) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "openConnection.srvc",
				data: JSON.stringify(machine)
			}).then(function successCallback(response) {
				//e.success(response.data);
				return response.data;
			});
		};



		this.createMachine = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "addMachine.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.getMachineById = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "getMachineById.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

		this.updateMachine = function (e) {
			return $http({
				method: "POST",
				url: config.server + config.api_path + "updateMachine.srvc",
				data: JSON.stringify(e)
			}).then(function successCallback(response) {
				return response;
			});
		};

	}]);
});