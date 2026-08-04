/* global window, requirejs */
(function (document) {
    'use strict';
    requirejs.config({
        baseUrl: lisDir,
        waitSeconds: 20,
        paths: {
            //angular derivatives
            'angular': '../libs/angular/angular.min',
            'angularMessages': '../libs/angular-messages/angular-messages.min',
            'angularWizard': '../libs/angular-wizard/angular-wizard.min',
            'angularAnimate': '../libs/angular-animate/angular-animate.min',
            'angularAria': '../libs/angular-aria/angular-aria.min',
            'angularSanitize': '../libs/angular-sanitize/angular-sanitize.min',
            'angularTranslate': '../libs/angular-translate/angular-translate.min',
            'angularTranslateLoaderStaticFiles': '../libs/angular-translate-loader-static-files/angular-translate-loader-static-files.min',
            'angularMaterial': '../libs/angular-material/angular-material.min',
            'angularCookies': '../libs/angular-cookies/angular-cookies.min',
            'angularLoadingBar': '../libs/angular-loading-bar/loading-bar.min',
            'angularUiMask': '../libs/angular-ui-mask/mask.min',
            'ngStomp': '../libs/ng-stomp/ng-stomp.standalone.min',
            //ui-router
            'uiRouter': '../libs/@uirouter/angular-ui-router.min',
            //jquery
            'jquery': '../libs/jquery/jquery.min',
            'jwt_decode': '../libs/jwt-decode/jwt-decode.min',
            //kendo (NOTE: do not change the naming style of kendo.all.min as it will break loading internal modules)
            'kendo.all.min': '../libs/kendoui/js/kendo.all.min',
            'kendoOptions': 'kendoOptions',
            //particles (this file should be loaded by requireJS as dep for login only)
            'particles': '../libs/particles.js/particles.min',
            'd3': '../libs/d3/d3.min',
            'c3': '../libs/c3/c3.min',
            //modules
            'APIInterceptor': 'modules/shared/services/APIInterceptorService',
            'footer': 'modules/shared/directives/footer/footer',
            'header': 'modules/shared/directives/header/header',
            'dynamicFlex': 'modules/shared/directives/dynamicFlex/dynamicFlex',
            'inputMessages': 'modules/shared/directives/inputMessages/inputMessages',
            'authorityChecker': 'modules/shared/directives/authorityChecker',
            'httpDisableClick': 'modules/shared/directives/httpDisableClick',
            'socketService': "modules/shared/services/socketService"
        },
        shim: {
            'angular': {
                exports: 'angular',
                deps: ['jquery']
            },
            'angularWizard': {
                deps: ['angular']
            },
            'angularAria': {
                deps: ['angular']
            },
            'uibootstrap': {
                deps: ['angular']
            },
            'jquery': {
                exports: 'jquery'
            },
            'ngStomp': {
                deps: ['angular']
            },
            'angularMaterial': {
                deps: ['angularAnimate', 'angularAria']
            },
            'uiRouter': {
                deps: ['angular']
            },
            'angularSanitize': {
                deps: ['angular']
            },
            'angularAnimate': {
                deps: ['angular']
            },
            'angularTranslate': {
                deps: ['angular']
            },
            'angularTranslateLoaderStaticFiles': {
                deps: ['angularTranslate']
            },
            'angularMessages': {
                deps: ['angular']
            },
            'angularCookies': {
                deps: ['angular']
            },
            'angularLoadingBar': {
                deps: ['angular']
            },
            'angularUiMask': {
                deps: ['angular']
            },
            'd3': {
                deps: ['angular']
            },
            'c3': {
                deps: ['d3']
            },
            'app': {
                deps: ['angularUiMask', 'angularLoadingBar', 'angularMessages', 'angularSanitize',
                    'angularTranslate', 'angularMaterial', 'angularWizard', 'jquery', 'uiRouter',
                    'ngStomp', 'kendo.all.min', 'angularCookies', 'c3', 'jwt_decode']
            }
        },
        onNodeCreated: function (node, config, moduleName, url) {

            node.addEventListener('load', function () {
                if (moduleName === "angular") {
                    //The rest of the app starts here
                    requirejs(['angular', 'app', 'APIInterceptor', 'particles', 'header', 'footer', 'dynamicFlex', 'inputMessages', 'authorityChecker', 'httpDisableClick', 'socketService'], function (angular, app) {
                        angular.bootstrap(document, [app.name]);
                    });
                }
            });

            node.addEventListener('error', function () {
                console.log('module ' + moduleName + ' could not be loaded');
            });
        }
    });
    // Angular starts here!
    requirejs(['angular'], function (angular) {

    });

}(window.document));
