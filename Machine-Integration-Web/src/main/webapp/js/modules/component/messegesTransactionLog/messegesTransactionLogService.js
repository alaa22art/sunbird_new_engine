define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('messegesTransactionLogService', ["$filter", function ($filter) {
        this.getMessageTransactionPage = function (data) {
            return util.createApiRequest("getMessageTransactionPage.srvc", JSON.stringify(data));
        };
        this.getOrderInfo = function (data) {
            return util.createApiRequest("getOrderInfo.srvc", JSON.stringify(data));
        };
        this.getOrderQueryResponse = function (data) {
            return util.createApiRequest("getQueryResultResponse.srvc", JSON.stringify(data));
        };
        this.getQueryResult = function (data) {
            return util.createApiRequest("getQueryResult.srvc", JSON.stringify(data));
        };
        this.getMachineOrderPage = function (data) {
            return util.createApiRequest("getMachineOrderPage.srvc", JSON.stringify(data));
        };
    }]);
});