/*
 * Routes configrations Define all routes and there options/dependencies here
 * Dependencies will be loading in the system using requireJS
 */
define(
    ['config'],
    function (config) {
        'use strict';
        var prefix = "/";
        var componentPath = "modules/component/";
        var directivePath = "modules/shared/directives/";
        return {
            directives: {
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
                    dependencies: [directivePath + "lov/lovService"]
                },
                name: {
                    path: directivePath + "name/nameDirective",
                    dependencies: [directivePath + "name/nameService"]
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
                    dependencies: [componentPath + "tenantMessages/tenantMessagesService"]
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
                pcrResultPicker: {
                    path: directivePath + "pcrResultPicker/pcrResultPicker"
                },
                tenantForm: {
                    path: directivePath + "tenantForm/tenantForm",
                    dependencies: [
                        directivePath + "tenantForm/tenantFormService",
                        componentPath + "tenantMessages/tenantMessagesService",
                        "modules/shared/services/commonMethods"
                    ],
                    directives: [
                        "countryCity",
                        "transField",
                        "uploadFile"
                    ]
                },
                branchForm: {
                    path: directivePath + "branchForm/branchForm",
                    dependencies: ['modules/shared/services/commonMethods'],
                    directives: [
                        "lov",
                        "countryCity",
                        "transField"
                    ]
                },
                reactMount: {
                    path: directivePath + "reactMount/reactMount"
                }
            },
            routes: {
                'home': {
                    url: prefix,
                    dependencies: [componentPath + 'home/homeController', componentPath + 'home/homeService'],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'home/home-view.html',
                            controller: 'homeCtrl',
                            data: {
                                pageName: "Home"
                            }
                        }
                    }
                },
                "groups-management": {
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
                        componentPath + "groupsManagement/groupsManagementService",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "rolesManagement/rolesManagementService",
                        "modules/shared/services/commonMethods"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "groupsManagement/groups-management-view.html",
                            controller: "groupsManagementCtrl",
                            data: {
                                pageName: "groupManagement"
                            }
                        }
                    }
                },
                "roles-management": {
                    url: prefix + "roles-management",
                    directives: [
                        "transField",
                        "shuttleBox",
                        "confirmClick",
                        "chipsWrapper"
                    ],
                    dependencies: [
                        componentPath + "rolesManagement/rolesManagementController",
                        componentPath + "rolesManagement/rolesManagementService",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "groupsManagement/groupsManagementService",
                        "modules/shared/services/commonMethods"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "rolesManagement/roles-management-view.html",
                            controller: "rolesManagementCtrl",
                            data: {
                                pageName: "roleManagement"
                            }
                        }
                    }
                },
                "users-management": {
                    url: prefix + "users-management",
                    directives: [
                        "lov",
                        "shuttleBox",
                        "transField",
                        "confirmClick",
                        "name",
                        "autocompleteSearch"
                    ],
                    dependencies: [
                        componentPath + "usersManagement/usersManagementController",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "groupsManagement/groupsManagementService",
                        componentPath + "rolesManagement/rolesManagementService",
                        "modules/shared/services/commonMethods",
                        componentPath + "userProfile/userProfileService",
                        directivePath + "branchForm/branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "usersManagement/users-management-view.html",
                            controller: "usersManagementCtrl",
                            data: {
                                pageName: "userManagement"
                            }
                        }
                    }
                },
                'machine-setup': {
                    url: prefix + 'machine-setup',
                    directives: ["lov"],
                    dependencies: [
                        componentPath + 'machineSetup/machineSetupController',
                        componentPath + 'machineSetup/machineSetupService',
                        componentPath + 'machineAssignTests/machineAssignTestsService',
                        'modules/shared/services/commonMethods',
                        directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'machineSetup/machine-setup-view.html',
                            controller: 'machineSetupCtrl',
                            data: {
                                pageName: "machineSetup"
                            }
                        }
                    }
                },
                'login': {
                    url: prefix + 'login',
                    directives: ["lov", "particles"],
                    dependencies: [
                        componentPath + 'login/loginController',
                        componentPath + 'login/loginService'
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
                        componentPath + 'passwordReset/passwordResetController',
                        componentPath + 'passwordReset/passwordResetService', 'modules/shared/services/commonMethods'
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
                'tenant-messages': {
                    url: prefix + 'tenant-messages',
                    directives: ["lov", "transField", "confirmClick"],
                    dependencies: [componentPath + 'tenantMessages/tenantMessagesController',
                    componentPath + 'tenantMessages/tenantMessagesService',
                    directivePath + 'lov/lovService',
                        'modules/shared/services/commonMethods'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'tenantMessages/tenant-messages-view.html',
                            controller: 'tenantMessagesCtrl',
                            data: {
                                pageName: "tenantMessages"
                            }
                        }
                    }
                },
                'transaction-history': {
                    url: prefix + 'transaction-history',
                    directives: ["lov"],
                    dependencies: [componentPath + 'transactionHistory/transactionHistoryController',
                    componentPath + 'transactionHistory/transactionHistoryService',
                    componentPath + 'machineSetup/machineSetupService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'transactionHistory/transaction-history-view.html',
                            controller: 'transactionHistoryCtrl',
                            data: {
                                pageName: "transactionHistory"
                            }
                        }
                    }
                },
                'machine-type-setup': {
                    url: prefix + 'machine-type-setup',
                    directives: ["lov"],
                    dependencies: [componentPath + 'machineTypeSetup/machineTypeSetupController', componentPath + 'machineTypeSetup/machineTypeSetupService', 'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'machineTypeSetup/machine-type-setup-view.html',
                            controller: 'machineTypeSetupCtrl',
                            data: {
                                pageName: "machineTypeSetup"
                            }
                        }
                    }
                },
                'test-mapping': {
                    url: prefix + 'test-mapping',
                    directives: ["lov"],
                    dependencies: [
                        componentPath + 'testMapping/testMappingController',
                        componentPath + 'testMapping/testMappingService',
                        'modules/shared/services/commonMethods',
                        directivePath + 'lov/lovService',
                        componentPath + 'machineTypeSetup/machineTypeSetupService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'testMapping/test-mapping-view.html',
                            controller: 'testMappingController',
                            data: {
                                pageName: "testMapping"
                            }
                        }
                    }
                },
                'machine-assign-tests': {
                    url: prefix + 'machine-assign-tests',
                    directives: ["lov"],
                    dependencies: [componentPath + 'machineAssignTests/machineAssignTestsController',
                    componentPath + 'machineAssignTests/machineAssignTestsService',
                    componentPath + 'machineSetup/machineSetupService',
                    componentPath + 'testMapping/testMappingService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'machineAssignTests/machine-assign-tests-view.html',
                            controller: 'machineAssignTestsController',
                            data: {
                                pageName: "machineTestMapping"
                            }
                        }
                    }
                },
                'test-catalog': {
                    url: prefix + 'test-catalog',
                    directives: ["lov"],
                    dependencies: [componentPath + 'testCatalog/testCatalogController',
                    componentPath + 'testCatalog/testCatalogService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'testCatalog/test-catalog-view.html',
                            controller: 'testCatalogCtrl',
                            data: {
                                pageName: "testCatalog"
                            }
                        }
                    }
                }, 'mapping-codes': {
                    url: prefix + 'mapping-codes',
                    directives: ["lov"],
                    dependencies: [componentPath + 'mappingCodes/mappingCodesController',
                    componentPath + 'mappingCodes/mappingCodesService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'mappingCodes/mapping-codes-view.html',
                            controller: 'mappingCodesCtrl',
                            data: {
                                pageName: "mappingCodes"
                            }
                        }
                    }
                },
                'machine-results': {
                    url: prefix + 'machine-results',
                    directives: ["lov"],
                    dependencies: [componentPath + 'machineResults/machineResultsController',
                    componentPath + 'machineResults/machineResultsService', componentPath + 'machineSetup/machineSetupService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'machineResults/machine-results-view.html',
                            controller: 'machineResultController',
                            data: {
                                pageName: "machineResults"
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
                        componentPath + "userProfile/userProfileController",
                        componentPath + "userProfile/userProfileService",
                        "modules/shared/services/commonMethods",
                        componentPath + "tenantMessages/tenantMessagesService",
                        directivePath + "branchForm/branchFormService"
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
                'transaction-log': {
                    url: prefix + 'transaction-log',
                    dependencies: [componentPath + 'transactionLog/transactionLogController',
                        'modules/shared/services/commonMethods',
                    componentPath + 'transactionLog/transactionLogService'
                    ],
                    directives: [],
                    views: {
                        main: {
                            templateUrl: 'js/' + componentPath + 'transactionLog/transaction-log-view.html',
                            controller: 'transactionLogCtrl',
                            data: {
                                pageName: "transactionLog"
                            }
                        }
                    }
                },
                'messeges-transaction-log': {
                    url: prefix + 'messeges-transaction-log',
                    dependencies: [
                        componentPath + 'messegesTransactionLog/messegesTransactionLogController',
                        componentPath + 'messegesTransactionLog/messegesTransactionLogService',
                        directivePath + 'branchForm/branchFormService'
                    ],
                    directives: ["lov"],
                    views: {
                        main: {
                            templateUrl: 'js/' + componentPath + 'messegesTransactionLog/messeges-transaction-log-view.html',
                            controller: 'messegesTransactionLogCtrl',
                            data: {
                                pageName: "messagesTransactionLog"
                            }
                        }
                    }
                },
                'patient-sample-lookup': {
                    url: prefix + 'patient-sample-lookup',
                    dependencies: [
                        componentPath + 'patientSampleLookup/patientSampleLookupController',
                        directivePath + 'branchForm/branchFormService',
                        componentPath + 'messegesTransactionLog/messegesTransactionLogService',
                        componentPath + 'machineResults/machineResultsService'
                    ],
                    directives: ["lov"],
                    views: {
                        main: {
                            templateUrl: 'js/' + componentPath + 'patientSampleLookup/patient-sample-lookup-view.html',
                            controller: 'patientSampleLookupCtrl',
                            data: {
                                pageName: "patientSampleLookup"
                            }
                        }
                    }
                },
                "lkp-management": {
                    url: prefix + "lkp-management",
                    directives: ["lov", "confirmClick"],
                    dependencies: [
                        componentPath + "lkpManagement/lkpManagementController",
                        componentPath + "lkpManagement/lkpManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "lkpManagement/lkp-management-view.html",
                            controller: "lkpManagementCtrl",
                            data: {
                                pageName: "lkpManagement"
                            }
                        }
                    }
                },
                "tenant-management": {
                    url: prefix + "tenant-management",
                    directives: [
                        "transField",
                        "tenantForm",
                        "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "tenantManagement/tenantManagementController",
                        componentPath + "tenantManagement/tenantManagementService",
                        directivePath + "tenantForm/tenantFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "tenantManagement/tenant-management-view.html",
                            controller: "tenantManagementCtrl",
                            data: {
                                pageName: "tenantManagement"
                            }
                        }
                    }
                },
                "branches": {
                    url: prefix + "branches",
                    dependencies: [
                        componentPath + "branch/branchController",
                        directivePath + "branchForm/branchFormService"
                    ],
                    directives: [
                        "branchForm",
                        "lov",
                        "countryCity",
                        "transField",
                        "confirmClick"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "branch/branch-view.html",
                            controller: "branchCtrl",
                            data: {
                                pageName: "branches"
                            }
                        }
                    }
                },
                'realtime-pcr': {
                    url: prefix + 'realtime-pcr',
                    directives: [
                        "lov",
                        "confirmClick",
                        "loadOnScroll",
                        "autocompleteSearch"
                    ],
                    dependencies: [componentPath + 'realTimePCR/realTimePCRController',
                    componentPath + 'realTimePCR/realTimePCRService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'realTimePCR/real-time-pcr-view.html',
                            controller: 'realTimePCRCtrl',
                            data: {
                                pageName: "Real Time PCR"
                            }
                        }
                    }
                }
                ,
                'realtime-pcr-patients': {
                    url: prefix + 'realtime-pcr-patients',
                    directives: [
                        "lov",
                        "confirmClick",
                        "loadOnScroll",
                        "autocompleteSearch",
                        "pcrResultPicker",
                        "transField"
                    ],
                    dependencies: [componentPath + 'pcrRealTimePatients/pcrRealTimePatientsController',
                    componentPath + 'pcrRealTimePatients/pcrRealTimePatientsService',
                        'modules/shared/services/commonMethods',
                    directivePath + 'lov/lovService'
                    ],
                    views: {
                        'main': {
                            templateUrl: 'js/' + componentPath + 'pcrRealTimePatients/pcr-real-time-patients.html',
                            controller: 'realTimePCRPatientsCtrl',
                            data: {
                                pageName: "Real Time PCR Patients"
                            }
                        }
                    }
                } //Don't add anything under this line, add your routes before it and Don't REMOVE
            },
            otherwise: prefix + 'login'
        };
    })