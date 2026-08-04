define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('realTimePCRPatientsService', ["$http", function ($http) {

        this.getPcrOrdersPage = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getPcrOrdersPage.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

    }]);
});