define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('testCatalogService', ["$http", function ($http) {

        this.getTestCatalogPage = function (e) {
            return $http({
                method: "POST",
                data: JSON.stringify(e.data),
                url: config.server + config.api_path + "getTestCatalogPage.srvc"
            }).then(function successCallback(response) {
                return response.data;
            });

        };

        this.addTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "addTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.updateTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateTest.srvc",
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


        this.getTestById = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getTestById.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };




    }]);
});