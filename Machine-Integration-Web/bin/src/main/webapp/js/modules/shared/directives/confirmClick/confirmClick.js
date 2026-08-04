define(['app', 'config'], function (app, config) {
    'use strict';
    /**
     * To show a confirmation popup to the user after clicking a button.
     * 
     * 1. confirm-click : Put your "ok" function call here, support a promise and non-promise functions
     * 2. cancel-click : Put your "cancel" function call here [optional]
     * 
     */
    app.directive('confirmClick', ["$mdDialog", "$q", "$parse", function ($mdDialog, $q, $parse) {
        return {
            restrict: 'A',
            scope: {
                confirmClick: "&",
                cancelClick: "&?"
            },
            link: function ($scope, $element, attributes) {

                $element.bind('click', function (event) {
                    $mdDialog.show({
                        controller: ["$scope", "directiveScope", function ($scope, directiveScope) {
                            $scope.message = "areYouSure";
                            $scope.ok = function () {
                                $q.when($parse(attributes.confirmClick)(directiveScope.$parent)).then(function () {
                                    $mdDialog.cancel();
                                }).catch(function () {
                                    $mdDialog.cancel();
                                });
                            };

                            $scope.cancel = function () {
                                if (attributes.cancelClick) {
                                    $q.when($parse(attributes.cancelClick)(directiveScope.$parent)).then(function () {
                                        $mdDialog.cancel();
                                    }).catch(function () {
                                        $mdDialog.cancel();
                                    });
                                } else {
                                    $mdDialog.cancel();
                                }

                            };
                        }],
                        templateUrl: './' + config.lisDir + '/modules/shared/directives/confirmClick/confirm-click-view.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        locals: {
                            directiveScope: $scope
                        },
                        clickOutsideToClose: true,
                        fullscreen: true
                    }).then(function (e) {

                    }, function (e) {

                    });

                });
            }
        };
    }]);
});