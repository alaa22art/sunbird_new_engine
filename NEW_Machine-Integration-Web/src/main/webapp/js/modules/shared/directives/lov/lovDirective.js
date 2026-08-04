define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	/**
	 * Directive to display any listed data (mainly for Lookups, but can be used to display NON Lookup classes).
	 * 
	 * 1- options ->
	 * 	a. className: the fully Lkp class name.
	 * 	b. valueField: the key to be used to know what to display in the list,also used in the search functionality.
	 * 	c. name: name of the Lkp, will be used by the developer to put the name key inside the object to send to server.
	 * 	d. labelText: the label of the list.
	 * 	e. selectedValue: the user selected value.
	 * 	f. required: is the list required?
	 * 	g. joins: list of joins for the Lkp [optional].
	 *  h. filterablePageRequest: that has-> [optional].
	 * 		1. filters: list of SearchCriterion [optional].
	 *  	2. operator: a JunctionOperator enum [optional].
	 *  	3. sortList: list of OrderObject [optional].
	 * 	i. data: use this when the directive will display a NON Lkp class, so it will skip calling the getLkpByClass() [optional].
	 *  j. onParentChange: a callback function to fetch new data depending on the parent value [optional], must RETURN the $http object and the new options of the lov.
	 *  k. noneLabel: custom string to display instead of 'none'
	 * 
	 * 2- select -> selected value.
	 * 3- form -> the current form.
	 * 4- disableSelect -> to disable the selection [optional]
	 * 5- onChange -> a callback function to be executed on value change [optional]
	 * 6- parent -> the parent value of this lov [optional]
	 * 7- onChangeParams -> params to send with the onChange callback [optional]
	 */
	app.directive('lov', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/lov/lov-view.html",
			scope: {
				options: '=options',
				select: '=select',
				form: '=form',
				disableSelect: '=disableSelect',
				onChange: '=onChange',
				onChangeParams: '=onChangeParams',
				parent: '=parent'
			},
			controller: ['$scope', '$element', 'lovService', '$filter',
				function ($scope, $element, lovService, $filter) {

					var allValues = [];
					var valueField = $scope.options.valueField;
					var lkpWrapper = {
						"className": $scope.options.className,
						"joins": $scope.options.joins, //optional
						"filterablePageRequest": $scope.options.filterablePageRequest //optional
					};
					$scope.noneLabel = $scope.options.noneLabel;
					if (!$scope.noneLabel) {
						$scope.noneLabel = $filter('translate')('none');
					}

					function prepareLov() {
						allValues = [];
						if ($scope.options.required === undefined) {
							$scope.options.required = false;
						}
						//default field for Lkp classes, in this case the $scope.options.valueField will be the locale for Lkps(backward compatibility)
						valueField = $scope.options.valueField;
						if ($scope.options.data) {
							allValues = angular.copy($scope.options.data);
							valueField = $scope.options.valueField;
						} else {
							lkpWrapper = {
								"className": $scope.options.className,
								"joins": $scope.options.joins, //optional
								"filterablePageRequest": $scope.options.filterablePageRequest //optional
							};
							lovService.getLkpByClass(lkpWrapper).then(function (data) {
								$scope.options.data = data;
								allValues = angular.copy($scope.options.data);
							}).catch(function () {});
						}
					}
					prepareLov();
					$scope.searchValue;
					$scope.clearSearchTerm = function () {
						$scope.searchValue = '';
						$scope.options.data = allValues;
					};

					$scope.searchValues = function (event) {
						if ($scope.searchValue === undefined) {
							return;
						}
						$scope.options.data = [];
						var searchValue = ($scope.searchValue).toLowerCase();
						for (var idx = 0; idx < allValues.length; idx++) {
							var lkpValue = (getDeepValueInObj(allValues[idx], valueField)).toString();
							if (lkpValue.toLowerCase().indexOf(searchValue) != -1) {
								$scope.options.data.push(allValues[idx]);
							}
						}
					};

					var warningBool = false;

					function logWarning(msg) {
						if (!warningBool) {
							warningBool = true;
							console.warn(msg);
						}
					}

					$scope.highlightSearched = function (lkp) {
						if (getDeepValueInObj(lkp, valueField) == null) {
							logWarning("Can't find-> " + valueField + " in Lov entity");
							return;
						}
						var lkpValue = (getDeepValueInObj(lkp, valueField)).toString();
						if ($scope.searchValue == null || $scope.searchValue == "") {
							return lkpValue;
						}

						var searchedIndex = lkpValue.toLowerCase().indexOf(($scope.searchValue).toLowerCase());
						if (searchedIndex == -1) {
							return lkpValue;
						}

						var result = "<span>" + lkpValue.substr(0, searchedIndex) +
							"&zwj;<span class='text-search-highlight'>" + lkpValue.substr(searchedIndex, $scope.searchValue.length) + "&zwj;</span>" +
							lkpValue.substr(searchedIndex + $scope.searchValue.length) + "</span>";

						// to avoid zwj (zero-width-joiner) in the end of the text
						if ((searchedIndex + $scope.searchValue.length) == lkpValue.length || result.charAt(result.indexOf("</span>") + 7) == " ") {
							result = result.replace("&zwj;</span>", "</span>");
						}

						return result;
					};

					$element.find('input').on('keydown', function (ev) {
						ev.stopPropagation();
					});
					$scope.change = function (selectedValue) {
						if (!$scope.onChange) {
							return;
						}
						$scope.options.selectedValue = selectedValue; //the change event happens before applying the value in the model
						$scope.onChange(selectedValue, $scope.onChangeParams);
					};

					$scope.$watch("parent", function (newValue, oldValue) {
						if (!$scope.parent || !$scope.options.onParentChange) {
							return;
						}
						$scope.options.onParentChange($scope.parent).then(function () {
							prepareLov();
						});

					});

					$scope.options["clearLkps"] = function (lkpList) {
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkpValue = lkpList[idx];
							lkpValue.selectedValue = null;
						}
					};

					$scope.options["setValues"] = function (obj, lkpList, fieldNameMap) {
						/**
						 * Assign values from the obj to lkp.
						 * obj: the object to inject the value in.
						 * lkpList: all the lkps
						 * fieldNameMap: a map to assign a custom value from the lkp. the mapping should be className:customValue [optional]
						 */
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkpValue = lkpList[idx];
							lkpValue.selectedValue = null;
							var fieldName = fieldNameMap ? fieldNameMap[lkpValue.className] : null;
							if (fieldName == null) {
								lkpValue.selectedValue = obj[lkpValue.name];
							} else {
								for (var i = 0; i < lkpValue.data.length; i++) {
									if (lkpValue.data[i][fieldName] == obj[lkpValue.name]) {
										lkpValue.selectedValue = lkpValue.data[i];
										break;
									}
								}
							}
						}

					};


					$scope.options["assignValues"] = function (obj, lkpList, fieldNameMap) {
						/**
						 * Assign values from the lkps to the obj.
						 * obj: the object to inject the value in.
						 * lkpList: all the lkps
						 * fieldNameMap: a map to assign a custom value from the lkp. the mapping should be className:customValue [optional]
						 */
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkpValue = lkpList[idx];
							if (lkpValue.selectedValue == null) {
								obj[lkpValue.name] = null;
								continue;
							}
							var fieldName = fieldNameMap ? fieldNameMap[lkpValue.className] : null;
							if (fieldName == null) {
								obj[lkpValue.name] = lkpValue.selectedValue;
							} else {
								obj[lkpValue.name] = lkpValue.selectedValue[fieldName];
							}
						}
					};

					function getDeepValueInObj(obj, nestedKeys) {
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
								return obj[util.userPrimary];
							}
						}
						return obj;
					}

					$scope.options["updateData"] = function (newData) {
						$scope.options.data = newData;
						prepareLov();
					}

				}
			]
		}
	});
});