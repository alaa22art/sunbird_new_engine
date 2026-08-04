define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('userProfileCtrl', [
        '$scope',
        'userProfileService',
        'commonMethods',
        '$window',
        'tenantMessagesService',
        'branchFormService',
        function (
            $scope,
            userProfileService,
            commonMethods,
            $window,
            tenantMessagesService,
            branchFormService
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
                },
                {
                    className: "LkpUserStatus",
                    name: $scope.userMetaData.lkpUserStatus.name,
                    labelText: "userStatus",
                    valueField: "name." + util.userLocale,
                    selectedValue: null,
                    required: $scope.userMetaData.lkpUserStatus.notNull
                }];
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
                    assignValuesToFields();
                });
                $scope.userTransFields = {
                    address: util.getTransFieldLanguages("address", "address", null, $scope.userMetaData.address.notNull)
                };

            });
            if (util.user.branchId == null) {
                $scope.onBranchChange = function (selectedBranch) {
                    commonData.selectedTokenBranch = selectedBranch;
                    if (selectedBranch == null) {
                        util.token = angular.copy(util.originalToken);
                        return;
                    }
                    userProfileService.generateBranchedToken(selectedBranch.rid).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        util.token = response.data;
                    });
                }
                branchFormService.getLabBranchList({
                    filters: []
                }).then(function (response) {
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

            function assignValuesToFields() {
                $scope.newUserInfo = angular.copy(util.user);

                //we cant use setValues since we calling this without waiting or clicking on any record
                for (var i in $scope.userLKPs) {
                    var lkpValue = $scope.userLKPs[i];
                    lkpValue.selectedValue = $scope.newUserInfo[lkpValue.name];
                }
            }

            $scope.updatePassword = function (valid) {
                if (!valid) {
                    return;
                }
                var map = {
                    secUser: JSON.stringify(util.user),
                    currentPassword: $scope.currentPassword,
                    newPassword: $scope.newPassword,
                    newEmail: $scope.email
                };
                userProfileService.updateEmailPassword(map).then(function () {
                    util.doRefreshToken().then(function (refreshResponse) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.currentPassword = null;
                        $scope.newPassword = null;
                        $scope.confirmPassword = null;
                        $scope.loginForm.$setPristine();
                        $scope.loginForm.$setUntouched();
                        applyChanges(refreshResponse.data);
                    });
                });
            };

            $scope.updateProfile = function (valid) {
                if (!valid) {
                    return;
                }
                $scope.userLKPs[0].assignValues($scope.newUserInfo, $scope.userLKPs);

                userProfileService.updateUserProfile($scope.newUserInfo).then(function () {
                    util.doRefreshToken().then(function (refreshResponse) {
                        applyChanges(refreshResponse.data);
                        $window.location.reload();
                    });
                });
            };

            function applyChanges(data) {
                var isRememberMe = window.localStorage.getItem('token') ? true : false;
                util.setUserData(data, isRememberMe);
                assignValuesToFields(); // get the new user from util and re assign, also to avoid optimistic lock
            }

            //Undo changes depending on the form name
            $scope.undoChanges = function (form) {
                if (form.$name === "loginForm") {
                    $scope.currentPassword = null;
                    $scope.newPassword = null;
                    $scope.confirmPassword = null;
                    $scope.email = util.user.email;
                    form.newPassword.$setValidity('passwordMatch', true);
                    form.confirmPassword.$setValidity('passwordMatch', true);
                } else {
                    assignValuesToFields();
                }
                form.$setPristine();
                form.$setUntouched();
            };

        }
    ]);
});