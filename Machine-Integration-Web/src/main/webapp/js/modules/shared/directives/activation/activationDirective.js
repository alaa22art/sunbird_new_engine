define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
  'use strict';
  app.directive('activation', function () {
    return {
      replace: true,
      restrict: 'E',
      scope: {
        value: '=value'
      },
      templateUrl: "./" + config.lisDir + "/modules/shared/directives/activation/activation-view.html",
      controller: ['$scope', function ($scope) {

        $scope.isActive = $scope.value;
        var valueWatcher = $scope.$watch("value", function (newValue, oldValue) {
          $scope.isActive = newValue;
        });

        $scope.$on("$destroy", function () {
          valueWatcher();
        });


      }]
    }
  });
});