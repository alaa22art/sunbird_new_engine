define(['app', 'config', 'util'], function (app, config, util) {
  'use strict';
  app.service('commonMethods', ["$http", function ($http) {


    this.retrieveMetaData = function (className) {
      return util.createApiRequest("getClassMetaData.srvc", JSON.stringify(className));
    };

    this.retrieveMetaDataList = function (classNameList) {
      return util.createApiRequest("getClassMetaDataList.srvc", JSON.stringify(classNameList));
    };

  }]);
});