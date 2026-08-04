define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.service('testMappingService', ["$http", function ($http) {

        this.getMachineTypeTestsPage = function (data) {
            return util.createApiRequest("getMachineTypeTestsPage.srvc", JSON.stringify(data));
        };

        this.updateMachineTypeTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateMachineTypeTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };


        this.insertMachineTypeTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "insertMachineTypeTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.deleteMachineTypeTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "deleteMachineTypeTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getTestCatalogListByName = function (searchValue) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getTestCatalogListByName.srvc",
                data: JSON.stringify(searchValue)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getTestCatalogListByMachineType = function (map) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getTestCatalogListByMachineType.srvc",
                data: JSON.stringify(map)
            }).then(function successCallback(response) {
                return response;
            });
        };




    }]);
});