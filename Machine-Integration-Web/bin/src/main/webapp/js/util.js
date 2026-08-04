define("util", ["config", "commonData", "jwt_decode"], function (config, commonData, jwt_decode) {
    'use strict';
    var util = {
        systemMessages: {},
        token: null,
        originalToken: null,
        user: {},
        userLocale: null,
        userPrimary: null,
        authorities: [],
        languages: [], //tenant languages
        direction: null,
        $rootScope: null,
        $http: null,
        $mdToast: null,
        $window: null,
        $timeout: null,
        $filter: null,
        decodeToken: function (token) {
            return jwt_decode(token);
        },
        doRefreshToken: function () {
            var payload = {
                grant_type: "refresh_token",
                client_id: "ACCULINK",
                client_secret: "acculink-secret",
                refresh_token: util.refreshToken
            }
            return util.createApiRequest(null, $.param(payload), {
                url: config.contextRoot + "oauth/token",
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            });
        },
        generateAgeString: function (agePeriod) {
            /**
             * Used to read Period java object.
             */
            var ageArray = [];

            for (var i = 0; i < agePeriod.values.length; i++) {
                if (agePeriod.values[i] > 0) {
                    ageArray.push(agePeriod.values[i] + util.$filter("translate")(agePeriod.fieldTypes[i].name.substr(0, 1).toUpperCase()));
                }
            }

            if (ageArray.length === 0) {
                return "0 " + util.$filter("translate")("D");
            } else {
                return ageArray.join(" ");
            }
        },
        prepareAppDirection: function () {
            /**
             * Set application direction to rtl or ltr(default).
             * 
             */
            var minVar = config.lisDir === "js" ? "" : ".min";
            var pathVar = config.lisDir === "js" ? "./assets/css/" : config.lisDir + "/styles/";
            if (util.user.comLanguage !== undefined) {
                util.$rootScope.direction = util.user.comLanguage.direction;
            } else {
                util.$rootScope.direction = 'ltr';
            }
            util.$rootScope.directionCss = pathVar + util.$rootScope.direction + '_app' + minVar + '.css';
            util.direction = util.$rootScope.direction;
        },
        prepareSystemMessages: function (messages) {
            /**
             * Take the tenant messages and reformat them, also update page title.
             * messages: the tenant messages array.
             */
            commonData.tenantMessages = messages; // store all the tenant messages objects
            for (var i in messages) {
                // get the value depending on user language
                util.systemMessages[messages[i].code] = messages[i].description[util.userLocale];
            }
            util.updatePageTitle(null);
        },
        gridSelectionDataBound: function (grid, selectedItems, runOnChange, preSelectFunc) {
            /**
             * Get the current page data in the grid and check/uncheck items if the item is in the chip array or not.
             * (Use inside Kendo's dataBound event)
             * grid : kendo grid.
             * selectedItems : the array which the chips read from, also it will contian all the selections.
             * runOnChange : an object that has "value" key which is a flag, to stop the onChange from running while we triggering the checkboxes.
             * preSelectFunc: a function to pre-select an item if it is in the chips array.(i dont think this is needed anymore, since if not pagination then the grid will persist selection )
             */
            runOnChange.value = false; // so we dont trigger the change event of the grid
            var gridData = grid.dataSource.data();
            for (var i = 0; i < gridData.length; i++) {
                var item = gridData[i];
                var row = grid.element.find("tr[data-uid='" + item.uid + "']");
                var checkBox = row.find("input[type=checkbox]");
                var notExist = true; // notExist as in " user didnt select it"

                if (preSelectFunc) {
                    preSelectFunc(item); // pass parameters to pre-select
                }

                for (var idx = 0; idx < selectedItems.length; idx++) {
                    if (selectedItems[idx].rid == item.rid) {
                        notExist = false;
                        if (checkBox.attr("aria-checked") == "false") {
                            checkBox.trigger("click");
                        }
                    }
                }
                if (checkBox.attr("aria-checked") == "true" && notExist) {
                    checkBox.trigger("click");
                }

                if (i + 1 == gridData.length) {
                    runOnChange.value = true;
                }
            }
        },
        gridSelectionChange: function (grid, selectedItems, runOnChange) {
            /**
             * Get the current page data in the grid remove all the current page records from the selectedItems then insert the selected.
             * (Use inside Kendo's dataBound event)
             * grid : kendo grid.
             * selectedItems : the array which the chips read from, also it will contian all the selections.
             * runOnChange : an object that has "value" key which is a flag, to stop the onChange from running while we triggering the checkboxes.
             */
            if (!runOnChange.value) {
                return;
            }
            // remove all current page records from the selected items then add the selected ones in the same page
            var currentPageRows = grid.dataSource.data(); // current page records
            for (var idx = 0; idx < currentPageRows.length; idx++) {
                for (var i = selectedItems.length - 1; i >= 0; i--) {
                    if (selectedItems[i].rid == currentPageRows[idx].rid) {
                        selectedItems.splice(i, 1);
                    }
                }
            }
            // get the selected rows and put them in the selected items
            var selectedRows = grid.select();
            for (var i = 0; i < selectedRows.length; i++) {
                var dataItem = grid.dataItem(selectedRows[i]);
                selectedItems.push(dataItem);
            }
        },
        removeGridChip: function (chip, grid) {
            /**
             * To trigger the checkbox of the item that we removed from the chip
             * (called from the function that listen's to md-on-remove's event of the chip)
             * chip: angular chip
             * grid: kendo grid
             */
            var gridData = grid.dataSource.data();
            for (var i = 0; i < gridData.length; i++) {
                var item = gridData[i];
                if (item.rid == chip.rid) {
                    var row = grid.element.find("tr[data-uid='" + item.uid + "']");
                    var checkBox = row.find("input[type=checkbox]");
                    checkBox.trigger("click");
                }
            }
        },
        prepareLanguages: function (languages) {
            /**
             * Modify the tenant languages so it can be used for the transfield directive
             * languages : the languages array, since we will use it when we dont have a user then we need
             * to inject the data to the function
             */
            util.languages = [];
            var langs = languages != null && languages.length > 0 ? languages : util.user.tenantLanguages;
            for (var objKey in langs) {
                var obj = langs[objKey];
                if (obj.isPrimary) {
                    util.userPrimary = obj.comLanguage.locale;
                }
                var language = {
                    language: obj.comLanguage.locale,
                    placeholder: "(" + obj.comLanguage.shortcutName + ")",
                    primary: obj.isPrimary,
                    value: null,
                    direction: obj.comLanguage.direction
                }
                util.languages.push(language);
            }
        },
        getTransFieldLanguages: function (key, labelCode, object, required) {
            /**
             * A prepare function to be called when declaring trans-fields so they can be used in the trans-field directive.
             * key: trans-field name
             * labelCode
             * object: the object which has a key to pre set the transfield by language(on transfield creation)
             * required
             */
            var langArray = angular.copy(util.languages);
            for (var i in langArray) {
                if (object != null && object[key] != null) {
                    langArray[i].value = object[key][langArray[i].language];
                }
                langArray[i].labelCode = labelCode;
                langArray[i].placeholder = langArray[i].placeholder;
                langArray[i].required = required;
                langArray[i].fieldName = key;
            }
            return langArray;
        },
        createLovFilter: function (element, payload, api) {
            /**
             * A function to be called when declaring the filter for LOVs in grids
             * element: The element passed from column.filterable.ui function(element)
             * payload: [nullable] The data to send to the API, for LKPs: { className: "LkpDependencyType" }
             * api: The API function which returns a promise, for LKPs: lovService.getLkpByClass
             * 
             * Extra per grid settings:
             * 1. Must pass a filterMap to createFilterablePageRequest, ex: { "lkpDependencyTypeRid": "lkpDependencyType.rid" }
             * 2. Must pass specific fields to the schema.model.fields, ex:
             *    lkpDependencyTypeRid: { from: "lkpDependencyType.rid", type: "lov" },
             *    lkpDependencyType: { defaultValue: {} },
             * 3. Filter should be under new column name, ex: "lkpDependencyTypeRid"
             * 4. Column should contain the filterable.ui function, ex:
             *      filterable: {
                        ui: function (element) {
                            util.createLovFilter(element, { className: "LkpDependencyType" }, lovService.getLkpByClass);
                        }
                    },
             * 5. [No longer needed, added in global settings instead] Must pass the following operators in "filterable" grid settings: 
             *    operators: {
             *        lov: {
             *            eq: util.systemMessages.eq,
             *            neq: util.systemMessages.neq,
             *            isnull: util.systemMessages.isnull",
             *            isnotnull: util.systemMessages.isnotnull
             *        }
             *    }
             */
            var lovDataSource = new kendo.data.DataSource({
                transport: {
                    read: function (e) {
                        api(payload)
                            .then(function (response) {
                                var data = response;
                                if (response.hasOwnProperty("data")) {
                                    data = response.data;
                                }
                                e.success(data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                },
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: {
                                type: "number"
                            },
                            name: {
                                type: "object"
                            }
                        }
                    }
                }
            });

            var primaryLanguage = "en_us";
            for (var i = 0; i < util.languages.length; i++) {
                if (util.languages[i].primary) {
                    primaryLanguage = util.languages[i].language;
                    break;
                }
            }

            element.kendoDropDownList({
                dataSource: lovDataSource,
                dataValueField: "rid",
                template: function (dataItem) {
                    if (dataItem.name[util.userLocale] != null) {
                        return dataItem.name[util.userLocale];
                    } else if (dataItem.name[primaryLanguage] != null) {
                        return dataItem.name[primaryLanguage];
                    } else {
                        return "";
                    }
                },
                valueTemplate: function (dataItem) {
                    if (dataItem.name[util.userLocale] != null) {
                        return dataItem.name[util.userLocale];
                    } else if (dataItem.name[primaryLanguage] != null) {
                        return dataItem.name[primaryLanguage];
                    } else {
                        return "";
                    }
                },
                optionLabel: {
                    name: util.systemMessages.selectValue
                },
                optionLabelTemplate: function (item) {
                    return item.name;
                }
            });
        },
        isArrayEmpty: function (array) {
            return !array || !Array.isArray(array) || array.length === 0;
        },
        addParenthesis: function (parenthesesValue, mainValue) {
            if (!parenthesesValue && !mainValue) {
                return "";
            }
            return "(" + parenthesesValue + ") " + mainValue;
        },
        createTransFieldEditor: function (container, options, fieldName) {
            /**
             * Create a custom component to handle trans-field column.
             * container: editor's container
             * options: editor's options
             * fieldName: the trans-field key in the object
             */
            var dataItem = options.model;
            if (dataItem[fieldName] == null) { // in case the object does not have the field name populated
                dataItem[fieldName] = {};
            }
            var uk = fieldName + "_" + dataItem.rid;
            var languages = angular.copy(util.languages);
            languages.sort(function (a, b) {
                return (a.primary === b.primary) ? 0 : a.primary ? -1 : 1
            }); //primary first
            var selectedLanguage = languages[0]; //default value
            $(container).addClass("inline-trans-editor");
            $(container).attr("flex", "");
            // $(container).attr("layout", "row");
            $(container).attr("layout-wrap", "");
            $('<input class="lang-list" />')
                .appendTo(container)
                .kendoDropDownList({
                    dataTextField: "placeholder",
                    dataValueField: "language",
                    change: function (e) {
                        var selectedRows = this.select();
                        selectedLanguage = this.dataItem(selectedRows[0]);
                        $("#" + uk).val(dataItem[fieldName][selectedLanguage.language]); //put the value in the input
                    },
                    dataSource: languages,
                    value: selectedLanguage.language
                });
            $('<input flex-offset-gt-md="5" dynamic-flex="100|100|100|75|75" type="text" class="k-input k-textbox lang-list-value" id="' + uk + '" />')
                .appendTo(container);
            $("#" + uk).val(dataItem[fieldName][selectedLanguage.language]); //default value

            $(container).find("span.lang-list").attr("dynamic-flex", "100|100|100|20|20");

            $("#" + uk).bind("change paste keyup", function (e) {
                var value = $(this).val();
                dataItem[fieldName][selectedLanguage.language] = value;
                dataItem.dirty = true;
            });
        },
        createFilterablePageRequest: function (dataSource, filterMap, junctionMap) {
            /**
             * For pagination, parameters:
             * -dataSource: kendo dataSource
             * -filterMap: if you want to change the field that you are filtering on. i.e. ("lkpUserStatus", "lkpUserStatus.name")
             *  so if the filters from the grid has "lkpUserStatus" then change it to "lkpUserStatus.name" [optional]
             * -junctionMap: if you want to change the junctionOperator of each field, default is "And" [optional]
             */

            // Pageable object starts counting from 1, however the first page for the Pageable is index = 0
            // and since Kendo Grid sends the first page in the map as 1 then we decreased by 1
            var filterablePageRequest = {
                filters: [],
                page: dataSource._page - 1,
                size: dataSource._pageSize,
                sortList: []
            };

            if (dataSource.filter() != null) {
                var gridFilters = dataSource.filter().filters;
                for (var idx = 0; idx < gridFilters.length; idx++) {
                    var obj = {
                        field: gridFilters[idx].field,
                        operator: gridFilters[idx].operator,
                        value: gridFilters[idx].value,
                        junctionOperator: "And"
                    };
                    if (filterMap !== undefined) {
                        var value = filterMap[gridFilters[idx].field];
                        if (value !== undefined) {
                            obj.field = value;
                        }
                    }
                    if (junctionMap !== undefined) {
                        obj.junctionOperator = junctionMap[gridFilters[idx].field];
                    }
                    filterablePageRequest.filters.push(obj);
                }
            }
            if (dataSource.sort() != null) {
                //filterablePageRequest.sortList = data.sort();
                var gridSortList = dataSource.sort();
                for (var idx = 0; idx < gridSortList.length; idx++) {
                    var fixedEnum = String(gridSortList[idx].dir).toUpperCase();
                    var field = gridSortList[idx].field;
                    if (filterMap !== undefined) {
                        var value = filterMap[field];
                        if (value !== undefined) {
                            field = value;
                        }
                    }
                    filterablePageRequest.sortList[idx] = {
                        direction: fixedEnum,
                        property: field
                    };
                }
            }
            return filterablePageRequest;
        },
        createToast: function (message, type, duration) {
            /**
             * Create a custom toast depending on the type.
             * message : the message to be displayed
             * type : success,info,warning,error,errorfatal
             * duration : display time of the toast[optional]
             */
            if (message == null || type == null) {
                return;
            }

            var toastDir = util.direction == "ltr" ? "right" : "left";
            type = type.toLowerCase();
            if (type == "errorfatal") {
                type = "error-fatal";
            }
            var toastColor = type + "-toast";
            var toastType = util.systemMessages[type];
            var toastIcon;
            switch (type) {
                case "success":
                    toastIcon = "fas fa-check-circle";
                    toastType = util.systemMessages["successToast"]; // success toast msg is different than the normal success
                    break;
                case "info":
                    toastIcon = "fas fa-info-circle";
                    break;
                case "warning":
                    toastIcon = "fas fa-exclamation-triangle";
                    break;
                case "error":
                    toastIcon = "fas fa-exclamation-circle";
                    break;
                case "error-fatal":
                    toastIcon = "lis-access-denied";
                    break;
            }
            var toastData = {
                color: toastColor,
                message: message,
                type: toastType,
                icon: toastIcon
            };
            if (duration === undefined) {
                duration = 3000;
            }
            util.$mdToast.show({
                hideDelay: duration,
                position: ('top ' + toastDir),
                controller: ["$scope", "toastData", function ($scope, toastData) {
                    $scope.content = toastData;
                    $scope.closeToast = function () {
                        util.$mdToast.hide();
                    };
                }],
                templateUrl: "./" + config.lisDir + "/modules/dialogs/toast.html",
                locals: {
                    toastData: toastData
                }
            });

        },
        selectShuttleItems: function (selectedShuttleId, shuttleOptionsId, shuttleOptionsContainerId, callbackFunction) {
            /**
             * Pre-select the items in the shuttle using callBack's logic.
             * selectedShuttleId: second select Id (Note: Angular Kendo Shuttle uses two select tags)
             * shuttleOptionsId: first select Id 
             * shuttleOptionsContainerId: the container id (i.e. <div>) of the shuttleOptionsId element
             * callbackFunction: function to return True or False to select the current item or not, the data object will be passed to the function
             */
            var shuttleOptions = $(shuttleOptionsId).data("kendoListBox");
            var data = shuttleOptions.dataItems();
            var domData = shuttleOptions.items();
            var transferTo = $(shuttleOptionsContainerId).find("a[data-command='transferTo']");
            for (var idx = 0; idx < data.length; idx++) {
                if (callbackFunction(data[idx])) {
                    shuttleOptions.select(domData[idx]);
                    transferTo.trigger("click");
                }
            }

            // sometimes the ui transfers an extra item for some reason, so we transfer back any false data from the selected list
            shuttleOptions = $(selectedShuttleId).data("kendoListBox");
            data = shuttleOptions.dataItems();
            domData = shuttleOptions.items();
            var transferBack = $(shuttleOptionsContainerId).find("a[data-command='transferFrom']");
            for (var idx = 0; idx < data.length; idx++) {
                if (!callbackFunction(data[idx])) {
                    shuttleOptions.select(domData[idx]);
                    transferBack.trigger("click");
                }
            }

        },
        clearSelectedShuttleItems: function (selectedShuttleId, shuttleOptionsContainerId) {
            /**
             * Clears the selected items in the shuttle.
             */
            var shuttleOptions = $(selectedShuttleId).data("kendoListBox"); //dont run if the selected list is empty(bug $apply in progress)
            if (shuttleOptions.dataItems().length < 1) {
                return;
            }
            var transferBack = $(shuttleOptionsContainerId).find("a[data-command='transferAllFrom']");
            transferBack.trigger("click");
        },
        searchShuttleItems: function (selectedShuttleId, shuttleOptionsId, searchValue, searchField, originalData) {
            /**
             * Search Inside the Shuttle Data and display the results.
             * selectedShuttleId: Second select Id (Note: Angular Kendo Shuttle uses two select tags)
             * shuttleOptionsId: First select Id 
             * searchValue : User's search input.
             * searchField : The field which the Regex will use to test. i.e name or name.en_us
             * originalData : All the data which the search will run on
             */
            var currentData = [];
            if (searchValue == null) {
                currentData = originalData;
            } else {
                var regex = new RegExp("^.*(" + searchValue + ").*$", "i"); //"contains" regex
                originalDataLoop: for (var idx = 0; idx < originalData.length; idx++) {
                    var obj = originalData[idx];
                    var searchFieldArray = searchField.split(".");
                    var objValue = obj[searchFieldArray[0]];
                    for (var i = 1; i < searchFieldArray.length; i++) {
                        objValue = objValue[searchFieldArray[i]];
                    }
                    var selectedShuttleData = $(selectedShuttleId).data("kendoListBox").dataItems();
                    for (var k = 0; k < selectedShuttleData.length; k++) {
                        if (selectedShuttleData[k].rid == obj.rid) {
                            continue originalDataLoop;
                        }
                    }
                    if (regex.test(objValue)) {
                        currentData.push(obj);
                    }
                }
            }
            var searchedData = new kendo.data.DataSource({
                serverFiltering: true,
                data: currentData,
            });
            searchedData.read(currentData);
            $(shuttleOptionsId).data("kendoListBox").setDataSource(searchedData);
        },
        isJsonString: function (str) {
            /**
             * Check whether the str can be parsed into an oject
             * str: the string object
             */
            if (typeof str != "string") {
                return false;
            }
            try {
                JSON.parse(str);
            } catch (e) {
                return false;
            }
            return true;
        },
        round: function (number, scale) {
            if (!scale) {
                scale = config.numberFraction;
            }
            return +(Math.round(number + ("e+" + scale)) + ("e-" + scale));
        },
        getUserData: function () {
            if (util.getItemFromStorage("session", "token") && util.getItemFromStorage("session", "user")) {
                var user = util.getItemFromStorage("session", "user");
                var token = util.getItemFromStorage("session", "token");
                var refreshToken = util.getItemFromStorage("session", "refreshToken");
                var authorities = util.getItemFromStorage("session", "authorities");
                util.setUtilData(user, token, refreshToken, authorities);
                util.prepareLanguages();
            } else if (util.getItemFromStorage("local", "token") && util.getItemFromStorage("local", "user")) {
                var user = util.getItemFromStorage("local", "user");
                var token = util.getItemFromStorage("local", "token");
                var refreshToken = util.getItemFromStorage("local", "refreshToken");
                var authorities = util.getItemFromStorage("local", "authorities");
                util.setUtilData(user, token, refreshToken, authorities);
                util.prepareLanguages();
            } else {
                //defaults when no user is logged in
                util.userLocale = commonData.defaultLocale;
            }
            util.$rootScope.userLocale = util.userLocale;
        },
        setUserData: function (loginResponse, rememberMe) {
            /**
             * Set/Update user's data.
             * loginResponse: the data from the server containing user,token,tenant languages.
             * rememberMe: store in locale storage or not
             */

            util.clearUtilData();
            var token = loginResponse.access_token;
            var refreshToken = loginResponse.refresh_token;
            var user = loginResponse.user;
            var authorities = user.privileges;

            //https://auth0.com/blog/blacklist-json-web-token-api-keys/
            var storageToUse = "session";
            if (rememberMe) {
                storageToUse = "local";
            }
            util.setItemInStorage(storageToUse, "token", token);
            util.setItemInStorage(storageToUse, "refreshToken", refreshToken);
            util.setItemInStorage(storageToUse, "user", user);
            util.setItemInStorage(storageToUse, "authorities", authorities);
            util.setUtilData(user, token, refreshToken, authorities);

            util.prepareLanguages();
            util.prepareAppDirection();

        },
        setUtilData: function (user, token, refreshToken, authorities) {
            util.token = token;
            util.refreshToken = refreshToken;
            util.originalToken = token;
            util.user = user;
            util.userLocale = user.comLanguage.locale;
            util.authorities = authorities;
        },
        updatePageTitle: function (toState) {
            /**
             * Update the page title in header file and in the window tab, except login we are handling it inisde the loginController
             * toState: ui-router $transition.to() [optional]
             */
            if (toState != null && toState.views != null) { // in case we called this outside the route listener 
                util.$rootScope.pageTitleName = toState.views.main.data ? toState.views.main.data.pageName : toState.name;
            }
            if (util.systemMessages.hasOwnProperty(util.$rootScope.pageTitleName)) {
                util.$rootScope.pageTitleName = util.systemMessages[util.$rootScope.pageTitleName];
                util.$window.document.title = util.$rootScope.pageTitleName;
            }

        },
        fullWebsiteView: function ($scope) {
            /**
             * Hide Header, stretch body to full view. And back to default when destroying the controller
             * $scope: the controller's scope
             */
            $("#header").hasClass("ng-hide") ? null : $("#header").addClass("ng-hide");
            $("#navMenu").hasClass("ng-hide") ? null : $("#navMenu").addClass("ng-hide");
            $("div.main").addClass("no-padding");
            $("#mainContent").removeClass("content");
            $scope.$on("$destroy", function () {
                $("#header").removeClass("ng-hide");
                $("#navMenu").removeClass("ng-hide");
                $("div.main").removeClass("no-padding");
                $("#mainContent").addClass("content");
            });
        },
        getDeepValueInObj: function (obj, nestedKeys) {
            /**
             * Get the deep value in the object even in case of list keys.
             * obj: the object to search for value in it.
             * nestedKeys: the key inside of the object to be fetched. i.e. obj.groups[3].name.en_us,obj.age.DoB
             */
            nestedKeys = nestedKeys.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
            nestedKeys = nestedKeys.replace(/^\./, ''); // strip a leading dot
            var a = nestedKeys.split('.');
            for (var i = 0, n = a.length; i < n; ++i) {
                var k = a[i];
                if (k in obj) {
                    obj = obj[k];
                } else {
                    return;
                }
            }
            return obj;
        },
        reportHandler: function (data, file) {
            /**
             * For now the type when downloading is pdf.
             * data: the blob data.
             * file:
             *  1- type: type pf file i.e. "application/pdf"
             *  2- name: if this value is specified then the file will be downloaded with the given name.
             */
            var blob = new Blob([data], {
                type: file.type
            });
            var url = util.$window.URL.createObjectURL(blob);
            if (file.name) {
                var link = document.createElement('a');
                link.href = url;
                link.download = file.name + ".pdf";
                document.body.appendChild(link);
                link.click();
                util.$window.URL.revokeObjectURL(url);
                link.remove();
            } else {
                util.$window.open(url, "_blank");
            }
        },
        createApiRequest: function (requestMapping, payload, options) {
            /**
             * Create a Request to the server.
             * requestMapping: the api mapping on the back-end
             * payload: the request body [optional]
             * options: any options to add/override the requestObj [optional]
             */
            var requestObj = {
                method: "POST", //default
                url: config.server + config.api_path + requestMapping //default
            }
            if (payload != null) {
                requestObj["data"] = payload;
            }

            for (var key in options) {
                if (options.hasOwnProperty(key)) {
                    requestObj[key] = options[key];
                }
            }

            return util.$http(requestObj).then(function (response) {
                return response;
            });

        },
        waitForDirective: function (readyKey, fireEvent, scope, payload) {
            //readyKey: name of key in commonData.events object
            //fireEvent: the event to fire [String]
            //scope: scope of the original controller
            //payload: data to send to the directive

            var count = 0;
            waitForDir();

            function waitForDir() {
                if (commonData.events[readyKey]) {
                    scope.$broadcast(fireEvent, payload);
                } else if (count < 200) {
                    //try 200 times with 1 ms delay between trials
                    util.$timeout(function () {
                        waitForDir();
                    }, 1);
                }
            }
        },
        clearUtilData: function () {
            /**
             * Clear some util data.
             */
            util.clearAllStorage("locale");
            util.clearAllStorage("session");
            util.token = null;
            util.originalToken = null;
            util.user = {};
            util.userPrimary = null;
            util.authorities = [];
            util.languages = [];
            util.direction = null;
        },
        getStorageByName: function (name) {
            /**
             * "locale" for localeStorage otherwise sessionStorage
             */
            return name == "locale" ? util.$window.localStorage : util.$window.sessionStorage;
        },
        clearAllStorage: function (storageName) {
            var storage = util.getStorageByName(storageName);
            storage.clear();
        },
        clearStorageProperty: function (storageName, prop) {
            var storage = util.getStorageByName(storageName);
            storage.removeItem(prop);
        },
        getAllStorage: function (storageName) {
            var storage = util.getStorageByName(storageName);
            var data = {};
            for (var i = 0; i < localStorage.length; ++i) {
                data[localStorage.key(i)] = localStorage.getItem(localStorage.key(i));
            }
            return data;
        },
        getStorageProperty: function (storageName, prop) {
            var storage = util.getStorageByName(storageName);
            var item = storage.getItem(prop);
            try {
                return JSON.parse(item); //If able to parse then its an stringified object
            } catch (e) {
                return item; //If not return plain text value
            }
        },
        setStorageProperty: function (storageName, prop, value) {
            try {
                var storage = util.getStorageByName(storageName);
                if (typeof value == 'object') {
                    value = JSON.stringify(value);
                }
                storage.setItem(prop, value);
            } catch (e) { // might be a full storage
                console.warn(e);
            }
        },
        createListFilter: function (element, data, label, dataSourceOptions) {
            /**
             * Create a list filter for any objects.
             * element: The element passed from column.filterable.ui function(element)
             * data: datasource's data
             * dataSourceOptions: to add options to datasource [optional]
             */
            var dataSource = new kendo.data.DataSource({
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: {
                                type: "number"
                            }
                        }
                    }
                },
                data: data
            });
            for (var key in dataSourceOptions) {
                if (dataSourceOptions.hasOwnProperty(key)) {
                    dataSource[key] = dataSourceOptions[key];
                }
            }
            element.kendoDropDownList({
                dataSource: dataSource,
                dataValueField: "rid",
                valueTemplate: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                template: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                optionLabel: {
                    name: util.systemMessages.selectValue
                },
                optionLabelTemplate: function (item) {
                    return item.name;
                }
            });
        },
        isLoggedIn: function () {
            return util.user != null && Object.keys(util.user).length > 0;
        },
        getItemFromStorage: function (storageName, prop) {
            var storage = util.getStorageByName(storageName);
            var item = storage.getItem(prop);
            try {
                return JSON.parse(item); //If able to parse then it is a stringified object
            } catch (e) {
                return item; //If not return plain text value
            }
        },
        setItemInStorage: function (storageName, prop, value) {
            try {
                var storage = util.getStorageByName(storageName);
                if (typeof value === 'object') {
                    value = JSON.stringify(value);
                }
                storage.setItem(prop, value);
            } catch (e) { // storage might be full
                console.warn(e);
            }
        },
        createListEditor: function (container, options, data, label, dataSourceOptions) {
            /**
             * Create a List of values for editing.
             * container : kendo's container
             * options : kendo's container
             * data : datasource's data
             * label : label to display, accepts inner values e.g. user.name.en_us
             * dataSourceOptions: to add options to datasource [optional]
             */
            var dataSource = new kendo.data.DataSource({
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: {
                                type: "number",
                                nullable: true
                            }
                        }
                    }
                },
                data: data
            });
            for (var key in dataSourceOptions) {
                if (dataSourceOptions.hasOwnProperty(key)) {
                    dataSource[key] = dataSourceOptions[key];
                }
            }
            $('<input name="' + options.field + '"/>')
                .appendTo(container)
                .kendoDropDownList({
                    dataValueField: "rid",
                    valueTemplate: function (dataItem) {
                        return util.getDeepValueInObj(dataItem, label) || "";
                    },
                    template: function (dataItem) {
                        return util.getDeepValueInObj(dataItem, label) || "";
                    },
                    dataSource: dataSource,
                    dataBound: function (e) {
                        var selectedIndex = e.sender.select();
                        e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                        e.sender.trigger("change");
                    }
                });
        },
        generateFilterablePageRequest: function () {
            return {
                filters: [],
                sortList: [],
                page: 0,
                size: 10
            };
        },
        addOrReplaceToFilters: function (filters, newFilter) {
            if (!filters || !newFilter) {
                return;
            }

            //replace if it exists otherwise add it to filters array
            var index = filters.findIndex(function (obj) { return obj.field === newFilter.field; });
            if (index !== -1) {
                filters[index] = newFilter;
            } else {
                filters.push(newFilter);
            }
        },
        getContentDispositionFileName: function (response) {
            /**
             * Get file name from content-disposition.
             * 
             * response: http response
             */
            var headers = response.headers();
            var fileName = headers["content-disposition"].substring(headers["content-disposition"].indexOf("=") + 1);
            return fileName.replace(/\"/g, "");
        },
        getHttpUploadOptions: function () {
            return {
                transformRequest: angular.identity,
                headers: { 'Content-Type': undefined },
                responseType: "json"
            };
        },
        getIsRememberMe: function () {
            return util.getItemFromStorage("local", "token") ? true : false;
        }
    };
    return util;
});