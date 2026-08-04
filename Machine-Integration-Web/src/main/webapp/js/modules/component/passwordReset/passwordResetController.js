define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('passwordResetCtrl', [
        '$scope',
        'passwordResetService',
        '$location',
        '$state',
        'commonMethods',
        function (
            $scope,
            passwordResetService,
            $location,
            $state,
            commonMethods
        ) {
            util.fullWebsiteView($scope);
            util.clearUtilData();
            var forgotPassToken = $location.search().t;//token in url
            $scope.langSwitcherOptions = {};
            if (forgotPassToken) {
                var tokenData = util.decodeToken(forgotPassToken);
                $scope.username = tokenData.user.username;
            } else {
                $state.go('login');
            }

            $scope.changeForgottenPassword = function (valid) {
                if (!valid) {
                    return;
                }
                var map = {
                    password: $scope.password,
                    token: forgotPassToken
                };
                passwordResetService.changeForgottenPassword(map).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $state.go('login');
                });

            };

        }
    ]);
});
