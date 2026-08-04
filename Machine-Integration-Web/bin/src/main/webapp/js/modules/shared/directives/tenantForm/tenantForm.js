define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.directive('tenantForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                tenant: "=tenant",
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/tenantForm/tenant-form-view.html",
            controller: ['$scope', 'commonMethods', 'tenantMessagesService', 'lovService',
                function ($scope, commonMethods, tenantMessagesService, lovService) {
                    $scope.metaData = null;
                    $scope.countryCityOptions = null;
                    $scope.availableLanguages = null;
                    $scope.options.tenantLanguages = null;
                    $scope.dummyMobileNumber = null;
                    $scope.tenantTransfields = null;
                    $scope.options.fileDataWrapper = { //logo
                        oldFile: $scope.tenant.logo != null ? $scope.tenant.logo : null,
                        fileModel: $scope.tenant.logo != null ? $scope.tenant.logo : null,
                        types: ["jpg", "jpeg", "png"],
                        labelCode: "logo"
                    };
                    tenantMessagesService.getTenantLanguages([util.user.tenantId])
                        .then(function (response) {
                            $scope.options.tenantLanguages = response.data;
                            tenantMessagesService.getSupportedLanguages()
                                .then(function (response) {
                                    $scope.availableLanguages = [];
                                    OUTER: for (var idx = 0; idx < response.data.length; idx++) {
                                        for (var i = 0; i < $scope.options.tenantLanguages.length; i++) {
                                            if ($scope.options.tenantLanguages[i].comLanguage.rid == response.data[idx].rid) {
                                                continue OUTER;
                                            }
                                        }
                                        var obj = {
                                            isPrimary: false,
                                            comLanguage: response.data[idx]
                                        };
                                        $scope.availableLanguages.push(obj);
                                    }
                                });
                        });
                    $scope.onMobilePatternChange = function () {
                        $scope.dummyMobileNumber = null;
                    };

                    function onCountryChange(selectedCountry) {
                        if (selectedCountry == null || $scope.tenant == null) {
                            return;
                        }
                        if ($scope.tenant.mobilePattern == null || $scope.tenant.mobilePattern == "") {
                            //appending a hash so the ui-mask can work otherwise it wont recognize the phoneCode alone as a regex
                            $scope.tenant.mobilePattern = selectedCountry.phoneCode + "#";
                        }
                    }
                    commonMethods.retrieveMetaData("SecTenant").then(function (response) {
                        $scope.metaData = response.data;
                        $scope.countryCityOptions = {
                            country: {
                                name: $scope.metaData.country.name,
                                required: $scope.metaData.country.notNull,
                                onChange: onCountryChange
                            },
                            city: {
                                name: $scope.metaData.city.name,
                                required: $scope.metaData.city.notNull
                            }
                        };
                        $scope.tenantTransfields = {
                            name: util.getTransFieldLanguages("name", "name", null, $scope.metaData.name.notNull),
                            address: util.getTransFieldLanguages("address", "address", null, $scope.metaData.address.notNull)
                        };
                    });

                    $scope.tenantLanguagesListener = function (chip, field) {
                        for (var idx = 0; idx < $scope.options.tenantLanguages.length; idx++) {
                            if (chip.comLanguage.locale !== $scope.options.tenantLanguages[idx].comLanguage.locale) {
                                $scope.options.tenantLanguages[idx][field] = false;
                            }
                        }
                    };

                    $scope.transferTenantLanguages = function (chip) {
                        chip.isPrimary = false;
                        chip.isNamePrimary = false;
                        if (util.isArrayEmpty($scope.options.tenantLanguages)) {
                            chip.isPrimary = true;
                            chip.isNamePrimary = true;
                        }
                        $scope.options.tenantLanguages.push(chip);
                        for (var idx = 0; idx < $scope.availableLanguages.length; idx++) {
                            if (chip.comLanguage.rid == $scope.availableLanguages[idx].comLanguage.rid) {
                                $scope.availableLanguages.splice(idx, 1);
                                break;
                            }
                        }
                    };
                    $scope.onTenantLanguagesRemove = function (chip) {
                        $scope.availableLanguages.push(chip);
                        if ($scope.options.tenantLanguages.length > 0) {
                            if (chip.isPrimary) {
                                $scope.options.tenantLanguages[0].isPrimary = true;
                            }
                            if (chip.isNamePrimary) {
                                $scope.options.tenantLanguages[0].isNamePrimary = true;
                            }
                        }
                    };


                    $scope.options["clear"] = function () {
                        $scope.tenant = null;
                        $scope.options.tenantLanguages = [];
                        $scope.options.tenantForm.$setPristine();
                        $scope.options.tenantForm.$setUntouched();
                    };
                    $scope.options["isFormInvalid"] = function () {
                        return $scope.options.tenantForm.$invalid || util.isArrayEmpty($scope.options.tenantLanguages);
                    };


                    $scope.options["getTenant"] = function () {
                        return $scope.tenant;
                    };

                }
            ]
        }
    });
});