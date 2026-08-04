define(['app', 'config'], function (app, config) {
    'use strict';
    app.directive('accordion', function () {
        return {
            restrict: 'E',
            transclude: true,
            template: '<div class="accordion-wrapper">' +
                '<div ng-transclude></div>' +
                '</div>',
            replace: true
        };
    }).directive('accordionBlock', function () {
        return {
            restrict: 'E',
            transclude: true,
            template: '<div class="accordion-block" md-whiteframe="1">' +
                '<div ng-transclude></div>' +
                '</div>',
            replace: true,
            require: 'accordion'
        };
    }).directive('accordionHeader', function () {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/accordion/accordion-header.html",
            replace: true,
            require: 'accordionBlock',
            scope: {
                options: "=?"
            },
            controller: ["$scope", "$element", function ($scope, $element) {
                $scope.toggled = false;
                $scope.toggle = function (value) {
                    //get the accordion content element and manipulate height
                    var accordionContentElement = $element[0].nextElementSibling;
                    // if its null then it might has ng-if on it
                    if (!accordionContentElement) {
                        return;
                    }

                    if (value != null) {
                        $scope.toggled = value
                    } else {
                        $scope.toggled = !$scope.toggled;
                    }
                    if ($scope.toggled) {
                        accordionContentElement.style.padding = "8px";
                        accordionContentElement.style.height = "auto";//set content to its normal height
                        accordionContentElement.style.overflow = "initial";
                    } else {
                        accordionContentElement.style.height = null;
                        accordionContentElement.style.overflow = null;
                        accordionContentElement.style.padding = null;
                    }

                    if ($scope.options) {
                        $scope.options.expanded = $scope.toggled === true;
                        if ($scope.toggled === true && $scope.options.onExpand) {
                            $scope.options.onExpand();
                        } else if ($scope.toggled === false && $scope.options.onCollapse) {
                            $scope.options.onCollapse();
                        }
                    }

                };
                if ($scope.options) {
                    $scope.options["toggle"] = $scope.toggle;
                    $scope.options["expanded"] = $scope.toggled === true;
                }
            }]
        };
    }).directive('accordionContent', function () {
        return {
            restrict: 'E',
            transclude: true,
            template: '<div class="accordion-content">' +
                '<div ng-transclude></div>' +
                '</div>',
            replace: true,
            require: 'accordionBlock'
        };
    });
});