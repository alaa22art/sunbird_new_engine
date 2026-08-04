define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('driversService', function () {
        this.getMachineTypePage = function (data) {
            return util.createApiRequest("getMachineTypePage.srvc", JSON.stringify(data));
        };
        this.createMachineType = function (e) {
            return util.createApiRequest("createMachineType.srvc", JSON.stringify(e));
        };
        this.updateMachineType = function (e) {
            return util.createApiRequest("updateMachineType.srvc", JSON.stringify(e));
        };

    });
});