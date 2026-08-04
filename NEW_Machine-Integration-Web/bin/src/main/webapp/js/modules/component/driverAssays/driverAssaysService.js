define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('driverAssaysService', function () {

        this.getMachineTypeTestPage = function (data) {
            return util.createApiRequest("getMachineTypeTestPage.srvc", JSON.stringify(data));
        };

        this.getMachineTypeTestByControlItems = function (data) {
            return util.createApiRequest("getMachineTypeTestByControlItems.srvc", JSON.stringify(data));
        };

        this.getMachineTypeTestsByLotDetails = function (data) {
            return util.createApiRequest("getMachineTypeTestsByLotDetails.srvc", JSON.stringify(data));
        };

        this.updateMachineTypeTest = function (e) {
            return util.createApiRequest("updateMachineTypeTest.srvc", JSON.stringify(e));
        };

        this.createMachineTypeTest = function (e) {
            return util.createApiRequest("createMachineTypeTest.srvc", JSON.stringify(e));
        };

        this.deleteMachineTypeTest = function (e) {
            return util.createApiRequest("deleteMachineTypeTest.srvc", JSON.stringify(e));
        };

        this.getTestCatalogListByName = function (searchValue) {
            return util.createApiRequest("getTestCatalogListByName.srvc", JSON.stringify(searchValue));
        };

        this.getTestCatalogListByMachineType = function (map) {
            return util.createApiRequest("getTestCatalogListByMachineType.srvc", JSON.stringify(map));
        };

        this.setMachineTypeTestMapping = function (data) {
            return util.createApiRequest("setMachineTypeTestMapping.srvc", JSON.stringify(data));
        };

    });
});