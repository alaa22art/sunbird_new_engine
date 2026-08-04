define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('mappingCodesService', ["$http", function ($http) {

        this.getMappingCodesPage = function (e) {
            return $http({
                method: "POST",
                data: JSON.stringify(e.data),
                url: config.server + config.api_path + "getMappingCodesPage.srvc"
            }).then(function successCallback(response) {
                return response.data;
            });

        };

        this.addMappingCodes = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "addMappingCodes.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.updateMappingCodes = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateMappingCodes.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.deleteAllTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "deleteAllTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };


        this.getMappingCodesById = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMappingCodesById.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };




    }]);
});