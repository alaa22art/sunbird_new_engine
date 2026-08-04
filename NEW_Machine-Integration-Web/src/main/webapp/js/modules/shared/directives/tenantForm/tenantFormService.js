define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('tenantFormService', function () {
        var options = {
            transformRequest: angular.identity,
            headers: {
                'Content-Type': undefined
            }
        };

        function formatTenantData(data) {
            var payLoad = new FormData();
            if (data.logo !== undefined) {
                payLoad.append("logo", data.logo);
            } else {
                data.tenant.logo = null; //remove logo from tenant if we didnt recieve it in the data.logo
            }

            payLoad.append("tenant", JSON.stringify(data.tenant));
            payLoad.append("tenantLanguages", JSON.stringify(data.tenantLanguages));
            return payLoad;
        }
        this.createTenant = function (data) {
            return util.createApiRequest("createTenant.srvc", formatTenantData(data), options);
        };
        this.updateTenant = function (data) {
            return util.createApiRequest("updateTenant.srvc", formatTenantData(data), options);
        };
        this.getTenantData = function (data) {
            return util.createApiRequest("getTenantData.srvc", JSON.stringify(data));
        };
        this.getTenants = function () {
            return util.createApiRequest("getTenants.srvc");
        };
    });
});