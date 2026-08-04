define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('transactionLogService', function () {

        this.getEventLogPage = function (e) {
            return util.createApiRequest("getEvenLogPage.srvc", JSON.stringify(e.data));
        };
    });
});
