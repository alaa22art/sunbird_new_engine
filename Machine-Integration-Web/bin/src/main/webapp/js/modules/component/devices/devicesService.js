define(['app', 'util'], function (app, util) {
	'use strict';
	app.service('devicesService', function () {
		this.getMachineList = function (data) {
			return util.createApiRequest("getMachineList.srvc", JSON.stringify(data));
		};
		this.getMachinePage = function (data) {
			return util.createApiRequest("getMachinePage.srvc", JSON.stringify(data));
		};
		this.getMachineDevicesPageData = function (data) {
			return util.createApiRequest("getMachineDevicesPageData.srvc", JSON.stringify(data));
		};
		this.closeConnection = function (machine) {
			return util.createApiRequest("closeConnection.srvc", JSON.stringify(machine));
		};
		this.openConnection = function (machine) {
			return util.createApiRequest("openConnection.srvc", JSON.stringify(machine));
		};
		this.testConnection = function (machine) {
			return util.createApiRequest("testConnection.srvc", JSON.stringify(machine));
		};
		this.createMachine = function (data) {
			return util.createApiRequest("createMachine.srvc", JSON.stringify(data));
		};
		this.updateMachine = function (data) {
			return util.createApiRequest("updateMachine.srvc", JSON.stringify(data));
		};
		this.deleteMachine = function (e) {
			return util.createApiRequest("deleteMachine.srvc", JSON.stringify(e));
		};
		this.getMachineData = function (e) {
			return util.createApiRequest("getMachineData.srvc", JSON.stringify(e));
		};
	});
});