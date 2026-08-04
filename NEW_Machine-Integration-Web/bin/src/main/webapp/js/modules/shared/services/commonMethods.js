define(['app', 'util'], function (app, util) {
  'use strict';
  app.service('commonMethods', function () {

    this.retrieveMetaData = function (className) {
      return util.createApiRequest("getClassMetaData.srvc", JSON.stringify(className));
    };

    this.retrieveMetaDataList = function (classNameList) {
      return util.createApiRequest("getClassMetaDataList.srvc", JSON.stringify(classNameList));
    };

    this.getCustomTokenData = function (data) {
      return util.createApiRequest("getCustomTokenData.pub.srvc", JSON.stringify(data));
    };

    this.downloadImportTemplate = function (data) {
      return util.createApiRequest("downloadImportTemplate.srvc", JSON.stringify(data), { responseType: "blob" });
    };

    this.validateImportData = function (data) {
      var formData = new FormData();
      formData.append("file", data.file);
      formData.append("target", data.target);
      return util.createApiRequest("validateImportData.srvc", formData, util.getHttpUploadOptions());
    };

    this.uploadImportData = function (data) {
      var formData = new FormData();
      formData.append("file", data.file);
      formData.append("target", data.target);
      return util.createApiRequest("uploadImportData.srvc", formData, util.getHttpUploadOptions());
    };

  });
});