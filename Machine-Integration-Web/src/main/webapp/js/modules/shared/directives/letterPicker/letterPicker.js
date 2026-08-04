define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('letterPicker', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/letterPicker/letter-picker.html",
            scope: {
                options: "=options"
            },
            controller: ['$scope', function ($scope) {

                var noLetter = angular.copy(util.systemMessages.none);
                $scope.selectedLetter = noLetter;
                $scope.letters = [];
                for (var i = 65; i <= 90; i++) {
                    $scope.letters.push(String.fromCharCode(i).toUpperCase());
                }
                $scope.letters.push(noLetter);

                $scope.onLetterClick = function (letter) {
                    $scope.selectedLetter = letter;
                    if (letter === noLetter) {
                        $scope.options.onClick(null);
                    } else {
                        $scope.options.onClick(letter);
                    }
                };

                $scope.options["reset"] = function () {
                    $scope.selectedLetter = noLetter;
                };
                $scope.options["pick"] = function (letter) {
                    $scope.onLetterClick(letter);
                };
            }]
        }
    });
});