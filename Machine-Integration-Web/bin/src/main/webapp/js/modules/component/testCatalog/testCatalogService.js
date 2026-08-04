define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testCatalogService', function () {

        this.getTestCatalogPage = function (data) {
            return util.createApiRequest("getTestCatalogPage.srvc", JSON.stringify(data));
        };

        this.getTestCatalogList = function (data) {
            return util.createApiRequest("getTestCatalogList.srvc", JSON.stringify(data));
        };

        this.createTestCatalog = function (data) {
            return util.createApiRequest("createTestCatalog.srvc", JSON.stringify(data));
        };

        this.updateTestCatalog = function (data) {
            return util.createApiRequest("updateTestCatalog.srvc", JSON.stringify(data));
        };

        this.deleteTestCatalog = function (data) {
            return util.createApiRequest("deleteTestCatalog.srvc", JSON.stringify(data));
        };

        this.getTestCatalogByRid = function (data) {
            return util.createApiRequest("getTestCatalogByRid.srvc", JSON.stringify(data));
        };

        this.triggerTestActivation = function (data) {
            return util.createApiRequest("triggerTestActivation.srvc", JSON.stringify(data));
        };

        this.importTestCatalogFromExternalSource = function (data) {
            return util.createApiRequest("importTestCatalogFromExternalSource.srvc", JSON.stringify(data));
        };

    });
});