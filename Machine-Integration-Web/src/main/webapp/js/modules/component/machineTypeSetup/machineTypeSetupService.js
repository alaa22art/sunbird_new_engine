define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('machineTypeSetupService', ["$http", function ($http) {

        this.getDriverList = function () {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getDriverList.srvc"
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getProtocolList = function () {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getProtocolList.srvc"
            }).then(function successCallback(response) {
                return response;
            });
        };
        this.getMachineTypePage = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMachineTypePage.srvc",
                data: JSON.stringify(e.data)
            }).then(function successCallback(response) {
                e.success(response.data);
                return response.data;
            });
        };

        this.addMachineType = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "addMachineType.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.updateMachineType = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateMachineType.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getMachineTypeById = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMachineTypeById.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.updateMachineType = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateMachineType.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };
    }]);
});