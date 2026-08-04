define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('transactionLogService', function () {

        this.getEventLogPage = function (e) {
            return util.createApiRequest("getEventLogPage.srvc", JSON.stringify(e.data));
        };
    });
});
