define(['app', 'util', 'config'], function (app, util, config) {
  'use strict';
  app.service('loginService', function () {

    this.loginUser = function (user) {
      user.grant_type = "password";
      user.client_id = "ACCULINK";
      user.client_secret = "acculink-secret";
      return util.createApiRequest(null, $.param(user), {
        url: config.contextRoot + "oauth/token",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
      });
    };

    this.forgotPassword = function (email) {
      return util.createApiRequest("forgotPassword.pub.srvc", JSON.stringify(email));
    };

    this.generateDummyToken = function () {
      return util.createApiRequest("generateDummyToken.pub.srvc");
    };

    this.generateTenantOnboardingToken = function (data) {
      return util.createApiRequest("generateTenantOnboardingToken.pub.srvc", JSON.stringify(data));
    };


  });
});
