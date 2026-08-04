define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('nameService', function () {

        this.getTransliteration = function (data) {
            return util.createApiRequest("getTransliteration.srvc", JSON.stringify(data));
        };

        this.getLocalTransliteration = function (data) {
            return util.createApiRequest("getLocalTransliteration.srvc", JSON.stringify(data));
        };

    });
});