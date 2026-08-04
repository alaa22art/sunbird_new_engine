define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testPanelService', function () {

        this.getPanelPage = function (data) {
            return util.createApiRequest("getPanelPage.srvc", JSON.stringify(data));
        };

        this.createPanel = function (data) {
            return util.createApiRequest("createPanel.srvc", JSON.stringify(data));
        };

        this.updatePanel = function (data) {
            return util.createApiRequest("updatePanel.srvc", JSON.stringify(data));
        };

        this.deletePanel = function (data) {
            return util.createApiRequest("deletePanel.srvc", JSON.stringify(data));
        };

        this.getPanelList = function (data) {
            return util.createApiRequest("getPanelList.srvc", JSON.stringify(data));
        };


    });
});