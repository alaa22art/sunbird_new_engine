/*
 * Routes configrations Define all routes and there options/dependencies here
 * Dependencies will be loading in the system using requireJS
 */
define(['config'],
    function (config) {
        'use strict';
        var prefix = "/";
        var componentPath = "modules/component/";
        var directivePath = "modules/shared/directives/";
        var sharedServicesPath = "modules/shared/services/";
        return {
            services: {
                mappingDriverTestService: {
                    path: componentPath + "mappingDriverTests/mappingDriverTestService"
                },
                lovService: {
                    path: directivePath + "lov/lovService"
                },
                nameService: {
                    path: directivePath + "name/nameService"
                },
                tenantMessagesService: {
                    path: componentPath + "tenantMessages/tenantMessagesService"
                },
                tenantFormService: {
                    path: directivePath + "tenantForm/tenantFormService"
                },
                commonMethods: {
                    path: sharedServicesPath + "commonMethods"
                },
                homeService: {
                    path: componentPath + 'home/homeService'
                },
                groupsManagementService: {
                    path: componentPath + "groupsManagement/groupsManagementService"
                },
                usersManagementService: {
                    path: componentPath + "usersManagement/usersManagementService"
                },
                rolesManagementService: {
                    path: componentPath + "rolesManagement/rolesManagementService"
                },
                userProfileService: {
                    path: componentPath + "userProfile/userProfileService"
                },
                branchFormService: {
                    path: directivePath + "branchForm/branchFormService"
                },
                devicesService: {
                    path: componentPath + 'devices/devicesService'
                },
                deviceTestsService: {
                    path: componentPath + 'deviceTestsMapping/deviceTestsService'
                },
                loginService: {
                    path: componentPath + 'login/loginService'
                },
                passwordResetService: {
                    path: componentPath + 'passwordReset/passwordResetService'
                },
                transactionHistoryService: {
                    path: componentPath + 'transactionHistory/transactionHistoryService'
                },
                driversService: {
                    path: componentPath + 'drivers/driversService'
                },
                driverAssaysService: {
                    path: componentPath + 'driverAssays/driverAssaysService'
                },
                testCatalogService: {
                    path: componentPath + 'testCatalog/testCatalogService'
                },
                machineResultsService: {
                    path: componentPath + 'patientSampleLookup/machineResultsService'
                },
                transactionLogService: {
                    path: componentPath + 'transactionLog/transactionLogService'
                },
                messegesTransactionLogService: {
                    path: componentPath + 'messegesTransactionLog/messegesTransactionLogService'
                },
                lkpManagementService: {
                    path: componentPath + "lkpManagement/lkpManagementService"
                },
                tenantManagementService: {
                    path: componentPath + "tenantManagement/tenantManagementService"
                },
                testPanelService: {
                    path: componentPath + "testPanel/testPanelService"
                }
            },
            directives: {
                accordion: {
                    path: directivePath + "accordion/accordion"
                },
                testCatalog: {
                    path: directivePath + 'testCatalog/testCatalogDirective',
                    services: ["commonMethods", "lovService", "testCatalogService", "testPanelService"],
                    directives: ["lov", "letterPicker", "autocompleteSearch", "loadOnScroll"]
                },
                testSelection: {
                    path: directivePath + 'testSelection/testSelection',
                    services: ["deviceTestsService"],
                    directives: ["autocompleteSearch", "loadOnScroll"]
                },
                loadOnScroll: {
                    path: directivePath + 'loadOnScroll'
                },
                confirmClick: {
                    path: directivePath + 'confirmClick/confirmClick'
                },
                chipsWrapper: {
                    path: directivePath + "chipsWrapper/chipsWrapper"
                },
                lov: {
                    path: directivePath + "lov/lovDirective",
                    services: ['lovService']
                },
                name: {
                    path: directivePath + "name/nameDirective",
                    services: ['nameService']
                },
                passwordVerify: {
                    path: directivePath + "passwordVerify"
                },
                shuttleBox: {
                    path: directivePath + "shuttleBox/shuttleBox"
                },
                transField: {
                    path: directivePath + "transField/transField"
                },
                countryCity: {
                    path: directivePath + "countryCity/countryCity",
                    directives: ["lov"]
                },
                languageSwitcher: {
                    path: directivePath + "languageSwitcher/languageSwitcher",
                    services: ["tenantMessagesService"]
                },
                particles: {
                    path: directivePath + "particles/particles"
                },
                uploadFile: {
                    path: directivePath + "uploadFile/uploadFile"
                },
                circularMenu: {
                    path: directivePath + "circularMenu/circularMenuDirective",
                    directives: ["confirmClick"]
                },
                autocompleteSearch: {
                    path: directivePath + "autocompleteSearch/autocompleteSearch"
                },
                letterPicker: {
                    path: directivePath + "letterPicker/letterPicker"
                },
                tenantForm: {
                    path: directivePath + "tenantForm/tenantForm",
                    services: [
                        "commonMethods",
                        "tenantFormService",
                        "tenantMessagesService"
                    ],
                    directives: [
                        "countryCity",
                        "transField",
                        "uploadFile"
                    ]
                },
                branchForm: {
                    path: directivePath + "branchForm/branchForm",
                    services: ["commonMethods"],
                    directives: [
                        "lov",
                        "countryCity",
                        "transField"
                    ]
                },
                activation: {
                    path: directivePath + "activation/activationDirective"
                }
            },
            routes: {
                "apex.groups-management": {
                    url: prefix + "groups-management",
                    directives: [
                        "lov",
                        "transField",
                        "shuttleBox",
                        "confirmClick",
                        "chipsWrapper"
                    ],
                    dependencies: [
                        componentPath + "groupsManagement/groupsManagementController",
                    ],
                    services: [
                        "commonMethods",
                        "groupsManagementService",
                        "usersManagementService",
                        "rolesManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "groupsManagement/groups-management-view.html",
                            controller: "groupsManagementCtrl",
                            data: {
                                pageName: "groupManagement",
                                icon: "fas fa-users",
                                authority: "VIEW_GROUPS_MANAGEMENT",
                                module: 6,
                                rank: 1
                            }
                        }
                    }
                },
                "apex.roles-management": {
                    url: prefix + "roles-management",
                    directives: [
                        "transField",
                        "shuttleBox",
                        "confirmClick",
                        "chipsWrapper"
                    ],
                    dependencies: [
                        componentPath + "rolesManagement/rolesManagementController"
                    ],
                    services: [
                        "commonMethods",
                        "groupsManagementService",
                        "usersManagementService",
                        "rolesManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "rolesManagement/roles-management-view.html",
                            controller: "rolesManagementCtrl",
                            data: {
                                pageName: "roleManagement",
                                icon: "lis-role",
                                authority: "VIEW_ROLES_MANAGEMENT",
                                module: 6,
                                rank: 2
                            }
                        }
                    }
                },
                "apex.users-management": {
                    url: prefix + "users-management",
                    directives: [
                        "lov",
                        "shuttleBox",
                        "transField",
                        "confirmClick",
                        "name",
                        "autocompleteSearch",
                        "activation"
                    ],
                    dependencies: [
                        componentPath + "usersManagement/usersManagementController"
                    ],
                    services: [
                        "userProfileService",
                        "branchFormService",
                        "commonMethods",
                        "groupsManagementService",
                        "usersManagementService",
                        "rolesManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "usersManagement/users-management-view.html",
                            controller: "usersManagementCtrl",
                            data: {
                                pageName: "userManagement",
                                icon: "fas fa-user",
                                authority: "VIEW_USERS_MANAGEMENT",
                                module: 6,
                                rank: 3
                            }
                        }
                    }
                },
                'login': {
                    url: prefix + 'login',
                    directives: ["lov", "particles"],
                    dependencies: [
                        componentPath + 'login/loginController'
                    ],
                    services: [
                        "loginService"
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'login/loginView.html',
                            controller: 'loginCtrl',
                            data: {
                                pageName: "login"
                            }
                        }
                    }
                },
                'password-reset': {
                    url: prefix + 'password-reset',
                    directives: ["lov"],
                    dependencies: [
                        componentPath + 'passwordReset/passwordResetController'
                    ],
                    services: [
                        "commonMethods",
                        "passwordResetService"
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'passwordReset/password-reset-view.html',
                            controller: 'passwordResetCtrl',
                            data: {
                                pageName: "passwordReset"
                            }
                        }
                    }
                },
                'apex.tenant-messages': {
                    url: prefix + 'tenant-messages',
                    directives: [
                        "lov",
                        "transField",
                        "confirmClick"],
                    dependencies: [
                        componentPath + 'tenantMessages/tenantMessagesController'
                    ],
                    services: [
                        "commonMethods",
                        "lovService",
                        "tenantMessagesService"
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'tenantMessages/tenant-messages-view.html',
                            controller: 'tenantMessagesCtrl',
                            data: {
                                pageName: "systemLabels",
                                icon: "fas fa-language",
                                authority: "VIEW_TENANT_MESSAGES",
                                module: 7,
                                rank: 1
                            }
                        }
                    }
                },
                'apex.test-catalog': {
                    url: prefix + 'test-catalog',
                    directives: ["testCatalog"],
                    dependencies: [
                        componentPath + 'testCatalog/testCatalogController'
                    ],
                    services: ["commonMethods", "testCatalogService"],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'testCatalog/test-catalog-view.html',
                            controller: 'testCatalogCtrl',
                            data: {
                                pageName: "test",
                                icon: "fas fa-syringe",
                                module: 5,
                                rank: 1
                            }
                        }
                    }
                },
                'apex.test-panel': {
                    url: prefix + 'test-panel',
                    directives: ["lov", "letterPicker", "autocompleteSearch", "loadOnScroll", "testCatalog", "accordion"],
                    services: ["testPanelService", "commonMethods"],
                    dependencies: [
                        componentPath + 'testPanel/testPanelController'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'testPanel/test-panel-view.html',
                            controller: 'testPanelCtrl',
                            data: {
                                pageName: "panel",
                                icon: "fas fa-syringe",
                                module: 5,
                                rank: 2
                            }
                        }
                    }
                },
                "user-profile": {
                    url: prefix + "user-profile",
                    directives: [
                        "lov",
                        "passwordVerify",
                        "transField",
                        "name"
                    ],
                    dependencies: [
                        componentPath + "userProfile/userProfileController"
                    ],
                    services: [
                        "commonMethods",
                        "branchFormService",
                        "tenantMessagesService",
                        "userProfileService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "userProfile/user-profile-view.html",
                            controller: "userProfileCtrl",
                            data: {
                                pageName: "userProfile"
                            }
                        }
                    }
                },
                'apex.transaction-log': {
                    url: prefix + 'transaction-log',
                    dependencies: [
                        componentPath + 'transactionLog/transactionLogController'
                    ],
                    services: [
                        "commonMethods",
                        "transactionLogService"
                    ],
                    views: {
                        main: {
                            templateUrl: 'js/' + componentPath + 'transactionLog/transaction-log-view.html',
                            controller: 'transactionLogCtrl',
                            data: {
                                pageName: "transactionLog",
                                icon: "fas fa-cogs",
                                module: 1,
                                rank: 2
                            }
                        }
                    }
                },
                'apex.messeges-transaction-log': {
                    url: prefix + 'messeges-transaction-log',
                    dependencies: [
                        componentPath + 'messegesTransactionLog/messegesTransactionLogController',
                    ],
                    directives: ["lov"],
                    services: [
                        "messegesTransactionLogService", "branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: 'js/' + componentPath + 'messegesTransactionLog/messeges-transaction-log-view.html',
                            controller: 'messegesTransactionLogCtrl',
                            data: {
                                pageName: "messagesTransactionLog",
                                icon: "fas fa-cogs",
                                module: 1,
                                rank: 1
                            }
                        }
                    }
                },
                "apex.lkp-management": {
                    url: prefix + "lkp-management",
                    directives: ["lov", "confirmClick"],
                    dependencies: [
                        componentPath + "lkpManagement/lkpManagementController"
                    ],
                    services: [
                        "lkpManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "lkpManagement/lkp-management-view.html",
                            controller: "lkpManagementCtrl",
                            data: {
                                pageName: "lkpManagement",
                                icon: "lis-lkp-management-alt",
                                authority: "VIEW_LKP_MANAGEMENT",
                                module: 7,
                                rank: 4
                            }
                        }
                    }
                },
                "apex.tenant-management": {
                    url: prefix + "tenant-management",
                    directives: [
                        "transField",
                        "tenantForm",
                        "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "tenantManagement/tenantManagementController"
                    ],
                    services: [
                        "tenantFormService",
                        "tenantManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "tenantManagement/tenant-management-view.html",
                            controller: "tenantManagementCtrl",
                            data: {
                                pageName: "tenantManagement",
                                icon: "fas fa-user-tie",
                                authority: "VIEW_TENANT_MANAGEMENT",
                                module: 7,
                                rank: 5
                            }
                        }
                    }
                },
                "apex.branches": {
                    url: prefix + "branches",
                    dependencies: [
                        componentPath + "branch/branchController"
                    ],
                    directives: [
                        "branchForm",
                        "lov",
                        "countryCity",
                        "transField",
                        "confirmClick",
                        "activation"
                    ],
                    services: [
                        "branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "branch/branch-view.html",
                            controller: "branchCtrl",
                            data: {
                                pageName: "branches",
                                icon: "fas fa-code-branch",
                                authority: "VIEW_BRANCH",
                                module: 7,
                                rank: 6
                            }
                        }
                    }
                },
                "apex.devices": {
                    url: prefix + "devices",
                    dependencies: [
                        componentPath + "devices/devicesController"
                    ],
                    directives: ["autocompleteSearch", "loadOnScroll", "activation", "confirmClick"],
                    services: ["devicesService"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "devices/devices-view.html",
                            controller: "devicesCtrl",
                            data: {
                                pageName: "devices",
                                icon: "fas fa-shield-alt",
                                module: 2,
                                rank: 1
                            }
                        }
                    }
                },
                "apex.device-details": {
                    url: prefix + "device-details",
                    params: true,
                    dependencies: [
                        componentPath + "deviceDetails/deviceDetailsController"
                    ],
                    directives: ["autocompleteSearch", "lov"],
                    services: ["devicesService", "lovService", "commonMethods", "driversService"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "deviceDetails/device-details-view.html",
                            controller: "deviceDetailsCtrl",
                            data: {
                                pageName: "deviceDetails",
                                icon: "fas fa-shield-alt",
                                module: 2,
                                rank: 2
                            }
                        }
                    }
                },
                "apex.device-tests-mapping": {
                    url: prefix + "device-tests-mapping",
                    dependencies: [
                        componentPath + "deviceTestsMapping/deviceTestsMappingController"
                    ],
                    directives: ["autocompleteSearch", "lov", "activation", "confirmClick"],
                    services: ["deviceTestsService", "lovService", "commonMethods", "driverAssaysService"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "deviceTestsMapping/device-tests-mapping-view.html",
                            controller: "deviceTestsMappingCtrl",
                            data: {
                                pageName: "deviceTestsMapping",
                                icon: "fas fa-shield-alt",
                                module: 2,
                                rank: 3
                            }
                        }
                    }
                },
                "apex.drivers": {
                    url: prefix + "drivers",
                    dependencies: [
                        componentPath + "drivers/driversController"
                    ],
                    directives: [
                        "lov",
                        "loadOnScroll",
                        "autocompleteSearch",
                        "activation"
                    ],
                    services: [
                        "driversService",
                        "commonMethods",
                        "lovService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "drivers/drivers-view.html",
                            controller: "driversCtrl",
                            data: {
                                pageName: "drivers",
                                icon: "fas fa-shield-alt",
                                module: 3,
                                rank: 1


                            }
                        }
                    }
                },
                "apex.driver-assays": {
                    url: prefix + "driver-assays",
                    params: true,
                    dependencies: [
                        componentPath + "driverAssays/driverAssaysController"
                    ],
                    directives: [
                        "lov",
                        "loadOnScroll",
                        "autocompleteSearch",
                        "activation"
                    ],
                    services: [
                        "commonMethods",
                        "lovService",
                        "driverAssaysService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "driverAssays/driver-assays-view.html",
                            controller: "driverAssaysCtrl",
                            data: {
                                pageName: "driverAssays",
                                icon: "fas fa-shield-alt",
                                module: 3,
                                rank: 2
                            }
                        }
                    }
                },
                "apex.mapping-driver-test": {
                    url: prefix + "mapping-driver-test",
                    dependencies: [
                        componentPath + "mappingDriverTests/mappingDriverTestController"
                    ],
                    directives: [
                        "lov",
                        "loadOnScroll",
                        "activation",
                        "autocompleteSearch",
                        "accordion"
                    ],
                    services: [
                        "driverAssaysService",
                        "mappingDriverTestService",
                        "testCatalogService",
                        "testPanelService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "mappingDriverTests/mapping-driver-test-view.html",
                            controller: "mappingDriverTestCtrl",
                            data: {
                                pageName: "driverAssaysMapping",
                                icon: "fas fa-shield-alt",
                                module: 3,
                                rank: 3
                            }
                        }
                    }
                },
                "apex.patient-sample-lookup": {
                    url: prefix + "patient-sample-lookup",
                    params: true,
                    dependencies: [
                        componentPath + "patientSampleLookup/patientSampleLookupController"
                    ],
                    directives: ["lov"],
                    services: [
                        "branchFormService", "messegesTransactionLogService", "machineResultsService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "patientSampleLookup/patient-sample-lookup-view.html",
                            controller: "patientSampleLookupCtrl",
                            data: {
                                pageName: "patientSampleLookup",
                                icon: "fas fa-shield-alt",
                                module: 1,
                                rank: 3
                            }
                        }
                    }
                },
                "apex": {
                    url: prefix + "apex",
                    dependencies: [
                        componentPath + "apex/apexController"
                    ],
                    directives: [
                        "loadOnScroll",
                        "activation"
                    ],
                    services: [
                        "devicesService",
                        "lovService",
                        "driversService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "apex/apex-view.html",
                            controller: "apexCtrl",
                            data: {
                                pageName: "apex"
                            }
                        }
                    }
                }
            },
            otherwise: prefix + 'apex'
        };
    })