define(['app', 'config'], function (app, config) {
    /**
     * This directive is to upload any 'single' type of files.
     * 
     * 1-options:
     * a. types: the accepted types without dots.
     * b. oldFile :  
     * c. fileModel : 
     * d. labelCode : labelCode to appear on the upload button.
     */
    'use strict';
    app.directive('uploadFile', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/uploadFile/uploadFile.html",
            scope: {
                options: "=options"
            },
            link: function ($scope, $element, attributes) {
                $($element).find('.file-input').bind("change", function (changeEvent) {
                    $scope.options.fileModel = undefined;
                    $scope.options.base64 = undefined;
                    $scope.newFile = undefined;
                    if (changeEvent.target.files.length > 0) {
                        $scope.options.fileModel = changeEvent.target.files[0];
                        var reader = new FileReader();
                        reader.onload = function (loadEvent) {
                            $scope.$apply(function () {
                                $scope.options.oldFile = undefined;
                                $scope.newFile = loadEvent.target.result; //base64
                                $scope.options.base64 = angular.copy($scope.newFile);
                            });
                        }
                        reader.readAsDataURL(changeEvent.target.files[0]);
                    }
                });
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                $scope.isImage = false;
                var typeStringsArray = [];
                $scope.options.types.forEach(function (item) {
                    typeStringsArray.push("." + item);
                });
                $scope.typeStrings = typeStringsArray.join(", ");
                if ($scope.options.types.indexOf("jpg") != -1 ||
                    $scope.options.types.indexOf("jpeg") != -1 ||
                    $scope.options.types.indexOf("png") != -1 ||
                    $scope.options.types.indexOf("gif") != -1) {
                    $scope.isImage = true;
                }

                $scope.openFileDialog = function () {
                    $($element).find('.file-input').trigger('click');
                };

                $scope.options["reset"] = function () {
                    $scope.options.oldFile = undefined;
                    $scope.options.fileModel = undefined;
                    $scope.options.base64 = undefined;
                    $scope.newFile = undefined;
                    $($element).find('.file-input').val('');
                };

                $scope.options["setFile"] = function (file) {
                    $scope.options.oldFile = file;
                    $scope.options.fileModel = file;
                };

            }]
        }
    });
});