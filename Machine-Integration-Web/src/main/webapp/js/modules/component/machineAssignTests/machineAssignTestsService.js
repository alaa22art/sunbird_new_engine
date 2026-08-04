define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('machineAssignTestsService', ["$http", function ($http) {

        /*this.getMachineTestCatalogList = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMachineTestCatalogList.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };*/

        this.getMachineTestCatalogList = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getMachineTestCatalogList.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.getMachineTestsPage = function (e) {
            return $http({
                method: "POST",
                data: JSON.stringify(e.data),
                url: config.server + config.api_path + "getMachineTestsPage.srvc"
            }).then(function successCallback(response) {
                e.success(response.data);
            });
        };


        this.updateMachineTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "updateMachineTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.insertMachineTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "insertMachineTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        /*  this.insertMachineTests = function (e) {
              return $http({
                  method: "POST",
                  url: config.server + config.api_path + "insertMachineTests.srvc",
                  data: JSON.stringify(e)
              }).then(function successCallback(response) {
                  return response;
              });
          };*/


        this.deleteMachineTest = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "deleteMachineTest.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.deleteAllByTestId = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "deleteAllByTestId.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };


        this.filterMachineTests = function (searchValue) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "filterMachineTests.srvc",
                data: JSON.stringify(searchValue)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.filterMachineTestCatalogList = function (searchValue) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "filterMachineTestCatalogList.srvc",
                data: JSON.stringify(searchValue)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.activeAllTests = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "activeAllTests.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

        this.deactivateAllTests = function (e) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "deactivateAllTests.srvc",
                data: JSON.stringify(e)
            }).then(function successCallback(response) {
                return response;
            });
        };

    }]);
});