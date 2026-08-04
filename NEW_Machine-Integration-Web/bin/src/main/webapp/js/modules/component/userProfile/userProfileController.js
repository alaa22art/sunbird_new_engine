define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('userProfileCtrl', [
        '$scope',
        'userProfileService',
        'commonMethods',
        '$window',
        'tenantMessagesService',
        'branchFormService',
        '$rootScope',
        function (
            $scope,
            userProfileService,
            commonMethods,
            $window,
            tenantMessagesService,
            branchFormService,
            $rootScope
        ) {
            $scope.newUserInfo = {};
            $scope.userMetaData = [];
            $scope.userLKPs = [];
            $scope.userTransFields = {};
            $scope.username = util.user.username;
            $scope.email = util.user.email;
            $scope.newPassword = null;
            $scope.confirmPassword = null;
            $scope.branchLkp = null;
            commonMethods.retrieveMetaData("SecUser").then(function (response) {
                $scope.userMetaData = response.data;
                $scope.userLKPs = [{
                    className: "LkpGender",
                    name: $scope.userMetaData.lkpGender.name,
                    labelText: "sex",
                    valueField: "name." + util.userLocale,
                    selectedValue: null,
                    required: $scope.userMetaData.lkpGender.notNull
                }];
                $scope.userTransFields = {
                    address: util.getTransFieldLanguages("address", "address", null, $scope.userMetaData.address.notNull)
                };
                tenantMessagesService.getTenantLanguages().then(function (response) {
                    var tenantLanguages = [];
                    for (var idx = 0; idx < response.data.length; idx++) {
                        tenantLanguages.push(response.data[idx].comLanguage);
                    }
                    $scope.userLKPs.push({
                        className: "ComLanguage",
                        name: $scope.userMetaData.comLanguage.name,
                        labelText: "language",
                        valueField: "name",
                        selectedValue: null,
                        required: $scope.userMetaData.comLanguage.notNull,
                        data: tenantLanguages
                    });
                    getUserProfileData();
                });


            });
            if (util.user.branchId == null) {
                $scope.onBranchChange = function (selectedBranch) {
                    commonData.selectedTokenBranch = selectedBranch;
                    if (selectedBranch == null) {
                        util.token = angular.copy(util.originalToken);
                        util.user.branch = null;
                        $rootScope.$broadcast("recompileHeader");
                        return;
                    }
                    userProfileService.generateBranchedToken(selectedBranch.rid).then(function (response) {
                        util.createToast(util.systemMessages.successTransaction, "success");
                        util.token = response.data;
                        util.user.branch = selectedBranch;
                        $rootScope.$broadcast("recompileHeader");
                    });
                }
                branchFormService.getLabBranchList({ filters: [] }).then(function (response) {
                    $scope.branchLkp = {
                        className: "Branch",
                        name: "branch",
                        labelText: util.systemMessages.branch,
                        valueField: "name." + util.userLocale,
                        selectedValue: commonData.selectedTokenBranch != null ? commonData.selectedTokenBranch : null,
                        required: false,
                        data: response.data
                    };
                });
            }

            $scope.updatePassword = function (valid) {
                if (!valid) {
                    return;
                }
                var map = {
                    currentPassword: $scope.currentPassword,
                    newPassword: $scope.newPassword,
                    newEmail: $scope.email
                };
                userProfileService.updateEmailPassword(map).then(function (response) {
                    util.doRefreshToken().then(function (refreshResponse) {
                        util.createToast(util.systemMessages.successTransaction, "success");
                        $scope.currentPassword = null;
                        $scope.newPassword = null;
                        $scope.confirmPassword = null;
                        $scope.loginForm.$setPristine();
                        $scope.loginForm.$setUntouched();
                        util.setUserData(refreshResponse.data, util.getIsRememberMe());
                        getUserProfileData();
                    });
                });
            };

            $scope.updateProfile = function (valid) {
                if (!valid) {
                    return;
                }
                $scope.userLKPs[0].assignValues($scope.newUserInfo, $scope.userLKPs);

                userProfileService.updateUserProfile($scope.newUserInfo).then(function (response) {
                    util.doRefreshToken().then(function (refreshResponse) {
                        util.setUserData(refreshResponse.data, util.getIsRememberMe());
                        $window.location.reload();
                    });
                });
            };

            function getUserProfileData() {
                userProfileService.getUserProfileData().then(function (response) {
                    $scope.newUserInfo = response.data;
                    //we cant use setValues since we calling this without waiting or clicking on any record
                    for (var i in $scope.userLKPs) {
                        var lkpValue = $scope.userLKPs[i];
                        lkpValue.selectedValue = $scope.newUserInfo[lkpValue.name];
                    }
                });
            }

        }
    ]);
});