define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('pcrResultPicker', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/pcrResultPicker/pcr-result-picker.html",
            scope: {
                options: "=options"
            },
            controller: ['$scope', function ($scope) {
                console.log($scope.pcrResult);
                // $scope.pcrResults = ["All","Positive", "Negative", "Undetermined"];
                $scope.pcrResults = $scope.options.selections ;
                $scope.onPcrResultChange = function () {
                    if ($scope.pcrResult === "All") {
                        $scope.options.onClick(null);
                    } else {
                        $scope.options.onClick($scope.pcrResult);
                    }
                };
            }]
        }
    })
})