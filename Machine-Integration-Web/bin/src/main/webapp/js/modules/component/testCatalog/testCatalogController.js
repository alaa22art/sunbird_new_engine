define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('testCatalogCtrl', [
        '$scope',
        'commonMethods',
        'testCatalogService',
        function (
            $scope,
            commonMethods,
            testCatalogService
        ) {
            $scope.testCatalogOptions = {};

            $scope.url = null;
            $scope.uploadedFile = null;
            $scope.validationResult = null;
            var uploadElement = $("#import_test_catalog");//fileupload element

            uploadElement.on("change", function (event) {//listen to fileupload event
                $scope.uploadedFile = this.files[0];
            });

            $scope.downloadTemplate = function () {
                var wrapper = {
                    target: "TestCatalog"
                };
                commonMethods.downloadImportTemplate(wrapper).then(function (response) {
                    util.fileHandler(response.data, {
                        type: commonData.fileTypes.excel,
                        name: util.getContentDispositionFileName(response)
                    });
                });
            };

            $scope.uploadTemplate = function () {
                uploadElement.val('');//reset
                uploadElement.trigger("click");
                $scope.validationResult = null;
            };

            $scope.validateData = function () {
                if (!$scope.uploadedFile) {
                    return;
                }
                var wrapper = {
                    target: "TestCatalog",
                    file: $scope.uploadedFile
                };
                commonMethods.validateImportData(wrapper).then(function (response) {
                    $scope.validationResult = response.data;
                });
            };

            $scope.importDataFile = function () {
                if (!$scope.uploadedFile) {
                    return;
                }

                var wrapper = {
                    target: "TestCatalog",
                    file: $scope.uploadedFile
                };
                commonMethods.uploadImportData(wrapper).then(function (response) {
                    util.createToast(util.systemMessages.successTransaction, "success");
                });

            };

            $scope.importUsingTestUrl = function () {
                testCatalogService.importTestCatalogFromExternalSource().then(function (response) {
                    util.createToast(util.systemMessages.successTransaction, "success");
                });
            };

            $scope.switchGridDataSource = function (type) {
                switch (type) {
                    case "success":
                        $scope.testCatalogDataSource.data($scope.validationResult.success);
                        break;
                    case "error":
                        $scope.testCatalogDataSource.data($scope.validationResult.error);
                        break;
                }
            };

            $scope.testCatalogDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                data: [],
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            "name": { type: "string" },
                            "requesterTestCode": { type: "string" },
                            "specimenType": { type: "object" }
                        }
                    }
                }
            });

            $scope.testCatalogGridOptions = {
                columns: [
                    {
                        field: "name",
                        title: util.systemMessages.name
                    },
                    {
                        field: "requesterTestCode",
                        title: util.systemMessages.requesterTestCode
                    },
                    {
                        field: "specimenType",
                        title: util.systemMessages.specimenType,
                        template: function (dataItem) {
                            return dataItem.specimenType ? dataItem.specimenType.name[util.userLocale] : "";
                        }
                    }
                ],
                dataSource: $scope.testCatalogDataSource
            };
        }
    ]);
});