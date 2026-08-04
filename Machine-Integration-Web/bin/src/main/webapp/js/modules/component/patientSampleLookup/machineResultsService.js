define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('machineResultsService', function () {

        this.getMachineResultPage = function (data) {
            return util.createApiRequest("getMachineResultPage.srvc", JSON.stringify(data));
        };

        this.getMachineResultList = function (data) {
            return util.createApiRequest("getMachineResultList.srvc", JSON.stringify(data));
        };

        this.getResultPatientSampleLookupData = function (data) {
            return util.createApiRequest("getResultPatientSampleLookupData.srvc", JSON.stringify(data));
        };

        this.resendLabResults = function (data) {
            return util.createApiRequest("resendMachineResults.srvc", JSON.stringify(data));
        };

    });
});
