define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
  'use strict';
  app.directive('name', function () {
    return {
      require: '^form',
      replace: true,
      restrict: 'E',
      scope: {
        nameOption: '=nameOption',
        metaData: '=metaData',
        form: '=form'
      },
      templateUrl: "./" + config.lisDir + "/modules/shared/directives/name/name-view.html",
      controller: ['$scope', 'nameService', function ($scope, nameService) {

        //wait for meta data
        var metaDataWatcher = $scope.$watch("metaData", function (newValue, oldValue) {
          if (newValue != null && Object.keys(newValue).length > 0) {
            prepareDirective();
            metaDataWatcher();
          }
        });

        function prepareDirective() {
          var result = angular.copy(util.languages);
          for (var idx = 0; idx < result.length; idx++) {
            var lang = result[idx];
            lang["names"] =
              [
                { type: "firstName" },
                { type: "secondName" },
                { type: "thirdName" },
                { type: "lastName" }
              ];
          }

          $scope.languages = result;
        }
        function getNames(transliterationName, originLang) {
          var names = {
            firstName: transliterationName.firstName != null && transliterationName.firstName[originLang] != "" ?
              transliterationName.firstName[originLang] : null,
            secondName: transliterationName.secondName != null && transliterationName.secondName[originLang] != "" ?
              transliterationName.secondName[originLang] : null,
            thirdName: transliterationName.thirdName != null && transliterationName.thirdName[originLang] != "" ?
              transliterationName.thirdName[originLang] : null,
            lastName: transliterationName.lastName != null && transliterationName.lastName[originLang] != "" ?
              transliterationName.lastName[originLang] : null
          };
          return names;
        }
        $scope.nameTransliteration = function (transliterationName, languageObj) {
          var originLang = languageObj.language;
          var targetLang = null;
          for (var i = 0; i < util.languages.length; i++) {
            if (util.languages[i].language != languageObj.language) {
              targetLang = util.languages[i].language;
              break;
            }
          }
          var names = getNames(transliterationName, originLang);

          var firstName = names.firstName;
          firstName = firstName == null ? "" : firstName;
          var secondName = names.secondName;
          secondName = secondName == null ? "" : secondName;
          var thirdName = names.thirdName;
          thirdName = thirdName == null ? "" : thirdName;
          var lastName = names.lastName;
          lastName = lastName == null ? "" : lastName;
          var toTranslate = firstName + "||" + secondName + "||" + thirdName + "||" + lastName;
          var transliterationObj = {
            name: toTranslate,
            entityType: "PERSON",
            sourceLanguageOfOrigin: originLang.substr(0, 2),
            sourceLanguageOfUse: originLang.substr(0, 2),
            targetLanguage: targetLang.substr(0, 2)
          };
          nameService.getLocalTransliteration(transliterationObj)
            .then(function (response) {
              var arraySplit = response.data["translation"].split("||");
              transliterationName.firstName[targetLang] = arraySplit[0];
              transliterationName.secondName[targetLang] = arraySplit[1];
              transliterationName.thirdName[targetLang] = arraySplit[2];
              transliterationName.lastName[targetLang] = arraySplit[3];
            });
        };
        // disable or enable the translation button, fields are required depending on metaData whether 
        // its a primary language or not
        $scope.disableTranslate = function (transliterationName, languageObj) {
          if (transliterationName == null || languageObj == null || $scope.metaData == null || Object.keys($scope.metaData).length < 1) {
            return true;
          }
          var originLang = languageObj.language;
          var names = getNames(transliterationName, originLang);
          var isFirstNameRequired = $scope.metaData["firstName"].notNull;
          var isSecondNameRequired = $scope.metaData["secondName"].notNull;
          var isThirdNameRequired = $scope.metaData["thirdName"].notNull;
          var isLastNameRequired = $scope.metaData["lastName"].notNull;

          return (names.firstName == null && isFirstNameRequired) || (names.secondName == null && isSecondNameRequired)
            || (names.thirdName == null && isThirdNameRequired) || (names.lastName == null && isLastNameRequired);
        }

        //NOT USED
        $scope.onTabTransliteration = function (transliterationName, languageObj) {
          //ng-keydown="($event.keyCode == 13 || $event.keyCode == 9) && onTabTransliteration(nameOption,language)"
          if ($scope.disableTranslate(transliterationName, languageObj)) {
            return;
          }
          $scope.nameTransliteration(transliterationName, languageObj);
        };

      }]
    }
  });
});