define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('deviceTestsService', function () {

        this.getMachineTestCatalogList = function (e) {
            return util.createApiRequest("getMachineTestCatalogList.srvc", JSON.stringify(e));
        };

        this.getMachineTestPage = function (data) {
            return util.createApiRequest("getMachineTestPage.srvc", JSON.stringify(data));
        };

        this.getMachineTestSelectionPage = function (data) {
            return util.createApiRequest("getMachineTestSelectionPage.srvc", JSON.stringify(data));
        };

        this.updateMachineTest = function (e) {
            return util.createApiRequest("updateMachineTest.srvc", JSON.stringify(e));
        };

        this.createMachineTest = function (e) {
            return util.createApiRequest("createMachineTest.srvc", JSON.stringify(e));
        };

        this.deleteMachineTest = function (e) {
            return util.createApiRequest("deleteMachineTest.srvc", JSON.stringify(e));
        };

        this.deleteAllByTestId = function (e) {
            return util.createApiRequest("deleteAllByTestId.srvc", JSON.stringify(e));
        };


        this.filterMachineTests = function (searchValue) {
            return util.createApiRequest("filterMachineTests.srvc", JSON.stringify(searchValue));
        };

        this.filterMachineTestCatalogList = function (searchValue) {
            return util.createApiRequest("filterMachineTestCatalogList.srvc", JSON.stringify(searchValue));
        };

        this.activeAllTests = function (e) {
            return util.createApiRequest("activeAllTests.srvc", JSON.stringify(e));
        };

        this.deactivateAllTests = function (e) {
            return util.createApiRequest("deactivateAllTests.srvc", JSON.stringify(e));
        };



    });
});