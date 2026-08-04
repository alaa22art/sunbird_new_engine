define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.service('machineResultsService', ["$http", function ($http) {

        this.getMachineResultPage = function (data) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMachineResultPage.srvc", data: JSON.stringify(data)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.resendLabResults = function (data) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "resendMachineResults.srvc", data: JSON.stringify(data)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getMachineResultList = function (data) {
            return util.createApiRequest("getMachineResultList.srvc", JSON.stringify(data));
        };



    }]);
});
