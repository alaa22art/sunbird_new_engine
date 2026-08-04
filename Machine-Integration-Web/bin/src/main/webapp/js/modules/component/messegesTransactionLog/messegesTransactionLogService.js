define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('messegesTransactionLogService', function () {

        this.getMessageTransactionPage = function (data) {
            return util.createApiRequest("getMessageTransactionPage.srvc", JSON.stringify(data));
        };

        this.getMachineOrderPage = function (data) {
            return util.createApiRequest("getMachineOrderPage.srvc", JSON.stringify(data));
        };

        this.reorderMachineOrder = function (data) {
            return util.createApiRequest("reorderMachineOrder.srvc", JSON.stringify(data));
        };

        this.cancelMachineOrder = function (data) {
            return util.createApiRequest("cancelMachineOrder.srvc", JSON.stringify(data));
        };

    });
});