define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('realTimePCRCtrl', [
        '$scope',
        '$timeout',
        '$mdDialog',
        '$document',
        'realTimePCRService',
        'systemSettingService',
        function (
            $scope,
            $timeout,
            $mdDialog,
            $document,
            realTimePCRService,
            systemSettingService
        ) {

            //check if the user is enable to view pcr module
            systemSettingService.getIsEnabledToViewPCR(commonData.veiwPcrPageType).then(function (response) {
                $scope.isEnableToView = response.data;
            })

            $scope.rowsIndices = [];
            $scope.columnsIndeices = [];

            $scope.selectedRowIndex = -1;
            $scope.selectedColumnIndex = -1;

            $scope.pcrWorkListOrders = [];

            $scope.pcrOrders = {};

            var pcrWorkListOrderWrapper = {};

            $scope.inputIndex = "";


            $scope.barcode = '';
            $scope.alphabetLetters = 'abcdefghijklmnopqrstuvwxyz'.toUpperCase().split('');
            $scope.OPEN = commonData.workListStatus.OPEN;
            $scope.IN_PROGRESS = commonData.workListStatus.IN_PROGRESS;
            $scope.RESULTS_ENTRY = commonData.workListStatus.RESULTS_ENTRY;
            $scope.FINALIZED = commonData.workListStatus.FINALIZED;
            // $scope.CLOSED = commonData.workListStatus.CLOSED;


            //this is a temporary solution
            $scope.positiveTxt = commonData.pcrResults.positive;
            $scope.negativeTxt = commonData.pcrResults.negative;
            $scope.undeterminedTxt = commonData.pcrResults.undetermined;

            $scope.workListStatusIcons = {
                "OPEN": { "icon": "fas fa-lock-open", active: true },
                "IN_PROGRESS": { "icon": "fas fa-spinner", active: false },
                "RESULTS_ENTRY": { "icon": "fas fa-keyboard", active: false },
                "FINALIZED": { "icon": "fas fa-clipboard-check", active: false }
            }



            $scope.pcrWorkLists = [];

            $scope.selectedWorkListStatistics = {
                runningSamples: 0,
                finishedSamples: 0,
                positiveSamples: 0,
                negativeSamples: 0,
                undeterminedSamples: 0
            }

            $scope.worklistPage = { content: [], totalPages: 1000, number: -1 }
            var sortDescByDate = {
                direction: "DESC",
                property: "creationDate"
            }

            $scope.currentWorklistFPR = {
                filters: [],
                sortList: [sortDescByDate],
                page: 0,
                size: 10
            };

            //to check if the screen width as equal or bigger than the recommended
            if (screen.width < 1360)
                util.createToast("Screen Width Is Less Than Recommended", "Warning");
            // $(window).width()


            //////////// Util functions
            //to get yyyymmdd from the timestamp 
            $scope.getSequence = function (date) {
                var seq = date.slice(0, date.indexOf("T"));
                seq = seq.replaceAll("-", "");
                return seq;
            }

            //check on the pcr result
            $scope.getPcrResult = function (pcrWorklistOrder, condition) {
                if (pcrWorklistOrder.pcrRealTimeOrderResults <= 0)
                    return false;
                var temp = "";
                for (var i = 0; i < pcrWorklistOrder.pcrRealTimeOrderResults[0].pcrRealTimeResult.pcrRealTimeActualResultValues.length; i++) {
                    temp = pcrWorklistOrder.pcrRealTimeOrderResults[0].pcrRealTimeResult.pcrRealTimeActualResultValues[i];
                    if (temp.pcrRealTimeResultTemplateLine.code == "Result") {
                        return temp.value == condition;
                    }
                }

                return false;
            }
            ////////////


            ////////////worklist functions

            $scope.workListOptions = {
                service: function (fpr) {
                    return realTimePCRService.getPcrWorkListPage(fpr).then(function (response) {
                        return response;
                    });
                },
                callback: function (filters) {
                    if ($scope.workListOptions.selectedItem && $scope.workListOptions.selectedItem.rid !== -1) {
                        var fpr = {
                            filters: [],
                            sortList: [sortDescByDate],
                            page: 0
                            // size: 10
                        };
                        fpr.page = 0;
                        fpr.size = 10;
                        fpr.filters.push({
                            field: "name",
                            value: $scope.workListOptions.selectedItem.name,
                            operator: "eq"
                        });


                        $scope.worklistPage.content = [];
                        $scope.getWorklistPageData(fpr);
                    } else if (filters) {
                        refreshWorklistData();
                    }
                },
                skeleton: {
                    code: "name",
                    description: "name"
                },
                filterList: ["name"],
                sortList: [{ property: "creationDate", direction: "DESC" }],
                label: "WorkList"
            };

            $scope.getWorklistPageData = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.worklistPage.totalPages) {
                    return;
                }
                return realTimePCRService.getPcrWorkListPage(filterablePageRequest).then(function (response) {
                    response.data.content = $scope.worklistPage.content.concat(response.data.content);
                    $scope.worklistPage = response.data;
                });
            };


            $scope.onWorklistScroll = function () {
                $scope.currentWorklistFPR.page = $scope.worklistPage.number + 1;
                return $scope.getWorklistPageData($scope.currentWorklistFPR);
            };

            $scope.onWorklistScroll();



            $scope.selectWorkList = function (worklist) {
                $scope.isSelectAll = true;
                clearWorkListStatusIcons();
                $scope.rowsIndices = [];
                $scope.columnsIndeices = [];
                $scope.rows = worklist.maxRows;
                $scope.columns = worklist.maxColumns;
                $scope.selectedWorkList = worklist;
                $scope.pcrOrders = {};
                // $scope.pcrOrders = Array(worklist.maxRows).fill().map(function () { return Array(worklist.maxColumns) });
                activateWorkListStatusIcons($scope.selectedWorkList.workListStatus.code);
                refreshWorklistOrdersPlate();
            }

            function clearWorkListStatusIcons() {
                $scope.workListStatusIcons = {
                    "OPEN": { "icon": "fas fa-lock-open", active: true },
                    "IN_PROGRESS": { "icon": "fas fa-spinner", active: false },
                    "RESULTS_ENTRY": { "icon": "fas fa-keyboard", active: false },
                    "FINALIZED": { "icon": "fas fa-clipboard-check", active: false }
                }

            }

            function activateWorkListStatusIcons(status) {
                if ($scope.OPEN == status) {
                    $scope.workListStatusIcons[$scope.OPEN].active = true;
                    return;
                }

                if ($scope.IN_PROGRESS == status) {
                    $scope.workListStatusIcons[$scope.OPEN].active = true;
                    $scope.workListStatusIcons[$scope.IN_PROGRESS].active = true;
                    return;
                }

                if ($scope.RESULTS_ENTRY == status) {
                    $scope.workListStatusIcons[$scope.OPEN].active = true;
                    $scope.workListStatusIcons[$scope.IN_PROGRESS].active = true;
                    $scope.workListStatusIcons[$scope.RESULTS_ENTRY].active = true;
                    return;
                }

                if ($scope.FINALIZED == status) {
                    $scope.workListStatusIcons[$scope.OPEN].active = true;
                    $scope.workListStatusIcons[$scope.IN_PROGRESS].active = true;
                    $scope.workListStatusIcons[$scope.RESULTS_ENTRY].active = true;
                    $scope.workListStatusIcons[$scope.FINALIZED].active = true;
                    return;
                }

            }


            function getWorkListStatistics(pcrWorkListOrders) {
                var isPositive = false;
                var isNegative = false;
                var isUndetermined = false;
                $scope.selectedWorkListStatistics = {
                    runningSamples: 0,
                    finishedSamples: 0,
                    positiveSamples: 0,
                    negativeSamples: 0,
                    undeterminedSamples: 0
                }

                for (var i = 0; i < pcrWorkListOrders.length; i++) {
                    isPositive = $scope.getPcrResult(pcrWorkListOrders[i], commonData.pcrDialogResultsEntry.resultLine[0]);
                    isNegative = $scope.getPcrResult(pcrWorkListOrders[i], commonData.pcrDialogResultsEntry.resultLine[1]);
                    isUndetermined = $scope.getPcrResult(pcrWorkListOrders[i], commonData.pcrDialogResultsEntry.resultLine[2]);
                    if (isPositive)
                        $scope.selectedWorkListStatistics.positiveSamples++;
                    else if (isNegative)
                        $scope.selectedWorkListStatistics.negativeSamples++;
                    else if (isUndetermined)
                        $scope.selectedWorkListStatistics.undeterminedSamples++;
                    else
                        $scope.selectedWorkListStatistics.runningSamples++;
                }

                //get finished samples by summing up the positive and negative samples
                $scope.selectedWorkListStatistics.finishedSamples = $scope.selectedWorkListStatistics.positiveSamples + $scope.selectedWorkListStatistics.negativeSamples;

            }


            function refreshWorklistData() {
                $scope.worklistPage.content = [];
                $scope.currentWorklistFPR = {
                    filters: [],
                    sortList: [sortDescByDate],
                    page: 0,
                    size: 10
                };

                $scope.getWorklistPageData($scope.currentWorklistFPR);
            }


            $scope.deleteWorkList = function (rid) {
                realTimePCRService.deletePcrRealTimeWorkListById(rid).then(
                    function (response) {
                        $scope.selectedWorkList = 0; //to hide the deleted worklist if it is been shown
                        refreshWorklistData();
                    }
                )
            }

            $scope.changeWorkListStatus = function (statusCode) {
                var wrapper = {
                    workList: $scope.selectedWorkList,
                    statusCode: statusCode
                };


                realTimePCRService.changeWorkListStatus(wrapper).then(function (response) {
                    util.createToast(util.systemMessages.success, "success");
                    // activateWorkListStatusIcons(response.data.workListStatus.code);
                    $scope.selectWorkList(response.data);
                    $scope.worklistPage.content.forEach(workList => {
                        if (workList.rid === response.data.rid)
                            workList.workListStatus.code = response.data.workListStatus.code;
                    });
                });
            }


            function createWorkListPlate() {
                $scope.pcrOrders = {};
                //to make the shape of the screen samples looks like the actual plate
                //NOTE: it should be improved
                $scope.plateShape = (100 - 9 * $scope.columns) / 2 + '%';

                var rowLetter = "";
                for (var i = 0; i < $scope.rows; i++) {
                    rowLetter = $scope.alphabetLetters[i];
                    $scope.pcrOrders[rowLetter] = [];
                    for (var j = 0; j < $scope.columns; j++) {
                        $scope.pcrOrders[rowLetter][j] = 0;
                    }
                }
            }

            //worklist button toggle functionality
            $scope.show = true;
            $scope.showToggleBtn = false;
            $scope.toggle = function (show) {
                $scope.show = show;
                $scope.showToggleBtn = !show;
            }
            ////////////


            ////////////worklist-orders functions
            function refreshWorklistOrdersPlate() {
                $scope.pcrWorkListOrders = [];
                realTimePCRService.getPcrWorkListOrders($scope.selectedWorkList.rid).then(
                    function (response) {
                        $scope.pcrWorkListOrders = response.data;
                        assignCellType(response.data);
                        getWorkListStatistics($scope.pcrWorkListOrders);
                        //create a worklist plate with empty cells
                        createWorkListPlate();
                        //fill the cells 
                        fillOrders();
                    }
                )

            }

            // this function to fill the occupied cells 
            function fillOrders() {
                // $scope.pcrOrders = {};
                for (var i = 0; i < $scope.pcrWorkListOrders.length; i++) {
                    // var rowIndex = $scope.alphabetLetters.indexOf($scope.pcrWorkListOrders[i].orderRowIndex);
                    var rowIndex = $scope.pcrWorkListOrders[i].orderRowIndex;
                    var columnIndex = parseInt($scope.pcrWorkListOrders[i].orderColumnIndex) - 1;
                    // $scope.pcrOrders[rowIndex] = [];
                    $scope.pcrOrders[rowIndex][columnIndex] = $scope.pcrWorkListOrders[i];
                }

                var rowNum = Math.floor($scope.pcrWorkListOrders.length / $scope.columns);
                var columnNum = $scope.pcrWorkListOrders.length % $scope.columns;

                if ($scope.pcrWorkListOrders.length != 0) {
                    if (columnNum == 0)
                        $scope.inputIndex = $scope.alphabetLetters[rowNum] + "1"; // in case the row is full, move to a new row to the first column
                    else
                        $scope.inputIndex = $scope.alphabetLetters[rowNum] + (columnNum + 1); // activate the column after the last occupied cell
                }
                else {
                    $scope.inputIndex = "A1";
                }

            }

            $scope.cellsType = {};

            function assignCellType(workListOrders) {
                for (var i = 0; i < workListOrders.length; i++) {
                    $scope.cellsType[workListOrders[i].rid] = {};
                    $scope.cellsType[workListOrders[i].rid].isPositive = $scope.getPcrResult(workListOrders[i], $scope.positiveTxt);
                    if ($scope.cellsType[workListOrders[i].rid].isPositive)
                        continue;
                    $scope.cellsType[workListOrders[i].rid].isNegative = $scope.getPcrResult(workListOrders[i], $scope.negativeTxt);
                    if ($scope.cellsType[workListOrders[i].rid].isNegative)
                        continue;

                    $scope.cellsType[workListOrders[i].rid].isUndetermined = $scope.getPcrResult(workListOrders[i], $scope.undeterminedTxt);

                }
            }



            $scope.makeResultNegative = function (pcrRealTimeWorkListOrder) {
                if ($scope.selectedWorkList.workListStatus.code != $scope.RESULTS_ENTRY)
                    return;
                var wrapper = {
                    actualValue: $scope.negativeTxt,
                    lineCode: "Result",
                    pcrRealTimeWorkListOrder: pcrRealTimeWorkListOrder
                }
                realTimePCRService.addPcrRealTimeActualResultValue(wrapper).then(
                    function (response) {
                        refreshWorklistOrdersPlate();
                    }
                )
            }

            $scope.isValidToSendResult = function (pcrWorkListOrder) {

                if (!pcrWorkListOrder)
                    return false;
                if (!pcrWorkListOrder.pcrRealTimeOrderResults)
                    return false;
                if (pcrWorkListOrder.pcrRealTimeOrderResults.length > 0) {
                    for (var i = 0; i < pcrWorkListOrder.pcrRealTimeOrderResults.length; i++) {
                        if (pcrWorkListOrder.pcrRealTimeOrderResults[i].pcrRealTimeResult.pcrRealTimeActualResultValues[0].value == $scope.undeterminedTxt)
                            return false;
                    }
                    return true;
                }

                return false;
            }

            var selectedResultsToSend = [];
            $scope.sendResults = function () {
                selectedResultsToSend = [];
                var id = '';
                for (var i = 0; i < $scope.pcrWorkListOrders.length; i++) {
                    if (!$scope.pcrWorkListOrders[i].isConfirmed)
                        continue;
                    if ($scope.pcrWorkListOrders[i].pcrRealTimeOrderResults.length > 0) {
                        id = "#" + $scope.pcrWorkListOrders[i].orderRowIndex + $scope.pcrWorkListOrders[i].orderColumnIndex;
                        if (angular.element(document.querySelector(id))[0].checked) {
                            if ($scope.selectedWorkList.workListStatus.code == $scope.FINALIZED || $scope.pcrWorkListOrders[i].isConfirmed) {
                                if ($scope.isValidToSendResult($scope.pcrWorkListOrders[i]))
                                    selectedResultsToSend.push($scope.pcrWorkListOrders[i]);
                                else {
                                    util.createToast(util.systemMessages.resultNotValidToSend, "Warning");
                                    return;
                                }
                            }
                            else {
                                util.createToast(util.systemMessages.resultsNotConfirmed, "Warning");
                                return;
                            }
                        }
                    }
                }

                if (selectedResultsToSend.length > 0) {
                    realTimePCRService.sendPcrRealTimeActualValues(selectedResultsToSend).then(
                        function (response) {
                            util.createToast(util.systemMessages.sentSuccessfully, "Success");
                            $scope.selectWorkList($scope.selectedWorkList);
                        }
                    )
                } else {
                    util.createToast(util.systemMessages.pleaseSelectResults, "Warning");
                }
            }

            $scope.isSelectAll = true;
            $scope.selectTxt = "Select All";
            $scope.DeselectTxt = "Deselect All";
            $scope.selectAllResults = function () {
                var checkBoxes = angular.element(document.querySelectorAll(".select-box"));
                for (var i = 0; i < checkBoxes.length; i++) {
                    checkBoxes[i].checked = $scope.isSelectAll;
                }
                $scope.isSelectAll = !$scope.isSelectAll;
            }
            //to open the cell to enter the barcode 
            $scope.changeActiveCell = function (rowIndex, columnIndex) {
                $scope.inputIndex = rowIndex + columnIndex;
            }

            var barcodeInputTimer = null;
            var canAddOrder = true;
            $scope.onBarcodeChange = function (barcode, rowIndex, columnIndex) {

                if (barcodeInputTimer) {

                    $timeout.cancel(barcodeInputTimer);

                }

                barcodeInputTimer = $timeout(function () {

                    //check if the barcode length is complete
                    if (barcode.length == 12) {

                        $scope.pcrWorkListOrders.forEach(order => {
                            if (order.pcrRealTimeOrder.machineOrder.barcode == barcode)
                                canAddOrder = false;

                        })

                        if (!canAddOrder) {
                            util.createToast(util.systemMessages.alreadyExistsInTheWorklist, "error");
                            canAddOrder = true;
                            return;
                        }

                        pcrWorkListOrderWrapper = {
                            barcode: barcode,
                            pcrRealTimeWorkList: $scope.selectedWorkList,
                            orderRowIndex: rowIndex,
                            orderColumnIndex: columnIndex
                        }

                        realTimePCRService.addPcrWorkListOrder(pcrWorkListOrderWrapper).then(
                            function (response) {
                                //occupy the cell 
                                $scope.pcrOrders[rowIndex][columnIndex - 1] = response.data;
                                // createWorkListPlate();
                                fillOrders();
                                //in case 
                                if (columnIndex == $scope.columns) {
                                    var newRow = $scope.alphabetLetters[$scope.alphabetLetters.indexOf(rowIndex) + 1];
                                    $scope.inputIndex = newRow + "1";
                                }
                                else
                                    $scope.inputIndex = rowIndex + (parseInt(columnIndex) + 1);
                                util.createToast(util.systemMessages.success, "Success");
                                canAddOrder = true;
                            }
                        )
                    }

                }, 1000);

            };
            //////////



            //worklist-dialog
            $scope.workListAddition = function (ev) {
                var addedWorkList = {};
                var isRefreshNeeded = false;
                $mdDialog.show({
                    controller: [
                        "$scope",
                        "$mdDialog",
                        function (
                            $scope,
                            $mdDialog
                        ) {

                            $scope.worklist = {};
                            $scope.worklist.maxRows = 8;
                            $scope.worklist.maxColumns = 12;


                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };

                            //form data is linked with $scope.worklist
                            $scope.addWorklist = function (ev) {
                                realTimePCRService.addPcrWorkList($scope.worklist).then(
                                    function (response) {
                                        isRefreshNeeded = true;
                                        addedWorkList = response.data;
                                        util.createToast(util.systemMessages.worklistCreated, "success");
                                    }
                                )
                                $mdDialog.hide();
                            };

                        }],
                    templateUrl: "./" + config.lisDir + "/modules/dialogs/worklist-dialog-view.html",
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true,
                }).then(function (data) {
                    if (isRefreshNeeded) {
                        refreshWorklistData();
                        $scope.selectWorkList(addedWorkList);
                    }
                }, function () { });
            };


            //pcr order dialog, to show patient record data and it has the results and unloading samples functionality 
            $scope.showOrderData = function (pcrWorkListOrder) {
                if ($scope.selectedWorkList.workListStatus.code != $scope.RESULTS_ENTRY && $scope.selectedWorkList.workListStatus.code != $scope.FINALIZED)
                    return;
                var isRefreshNeeded = false;
                var selectedWorkList = $scope.selectedWorkList;
                var OPEN = $scope.OPEN;
                var RESULTS_ENTRY = $scope.RESULTS_ENTRY;
                $mdDialog.show({
                    controller: [
                        "$scope",
                        "$mdDialog",
                        function (
                            $scope,
                            $mdDialog
                        ) {

                            $scope.machineOrder = pcrWorkListOrder.pcrRealTimeOrder.machineOrder;
                            //to access it  in the dialog
                            $scope.workList = selectedWorkList;
                            $scope.RESULTS_ENTRY = RESULTS_ENTRY;

                            $scope.positiveResult = commonData.pcrResults.positive;

                            //temporary solution
                            $scope.resultOptions = {};
                            $scope.resultOptions["Result"] = commonData.pcrDialogResultsEntry.resultLine;
                            $scope.resultOptions["VAR"] = commonData.pcrDialogResultsEntry.varLine;
                            $scope.lovResults = ["VAR", "Result"];
                            $scope.resultCode = "Result";
                            $scope.var = "VAR";

                            //to show the unload button only in the open worklist status
                            $scope.showUnloadBtn = selectedWorkList.workListStatus.code == OPEN;
                            $scope.showConfirmBtn = false;

                            $scope.confirm = {};
                            $scope.confirm.box = pcrWorkListOrder.isConfirmed ? true : false;


                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };

                            $scope.templateLines = [];
                            $scope.tempResults = {};

                            var results = [];


                            $scope.cancel = function () {
                                $mdDialog.hide();
                            }




                            realTimePCRService.getPcrTemplateLines().then(
                                function (response) {
                                    $scope.templateLines = response.data;
                                    //to show the result lines in this arrange (CT, VAR, Result)
                                    //where the result should be the last line
                                    response.data.sort(function (a, b) {
                                        return b.code.length - a.code.length;
                                    });
                                }
                            )


                            realTimePCRService.getPcrRealTimeActualResultValues(pcrWorkListOrder.rid).then(
                                function (response) {
                                    results = JSON.parse(JSON.stringify(response.data)); //to compare later in the addActualResultValues function if there is an uodated value or it's the same                                 
                                    $scope.showConfirmBtn = response.data.length > 0 ? true : false;
                                    //to link the line code with the result
                                    //NOTE: this should be changed later
                                    for (var i = 0; i < response.data.length; i++) {
                                        $scope.tempResults[response.data[i].pcrRealTimeResultTemplateLine.code] = response.data[i];
                                    }
                                }
                            )

                            //to unload a sample from the worklist, but this sample shouldn't be runned and its results not entered yet
                            //otherwise it will not unload it
                            $scope.unloadSample = function () {
                                realTimePCRService.unloadPcrWorkListOrder(pcrWorkListOrder).then(
                                    function (response) {
                                        isRefreshNeeded = true;
                                        util.createToast(util.systemMessages.success, "Success");
                                    }
                                )
                                $mdDialog.hide();
                            }
                            /////////

                            $scope.saveActualValues = function () {
                                var resultsToSave = [];
                                var i = 0;
                                for (const lineCode in $scope.tempResults) {

                                    if ($scope.tempResults[lineCode]) {
                                        //check if the result is the first time has been entered(resultsToAdd) or it's just an update for the result(resultsToUpdate)
                                        //check also if there is a change in the result or it's the same result
                                        if (results.length === 0)
                                            resultsToSave.push($scope.tempResults[lineCode]);
                                        else if ($scope.tempResults[lineCode].value != results[i].value)
                                            resultsToSave.push($scope.tempResults[lineCode]);
                                    }
                                    i++;

                                }

                                var savedActualResultValueWrapper = {
                                    pcrRealTimeWorkListOrder: pcrWorkListOrder,
                                    pcrRealTimeActualValues: resultsToSave
                                };

                                if (resultsToSave.length > 0 || pcrWorkListOrder.isConfirmed != $scope.confirm.box) {
                                    pcrWorkListOrder.isConfirmed = $scope.confirm.box;
                                    realTimePCRService.savePcrActualResultValues(savedActualResultValueWrapper).then(
                                        function (response) {
                                            util.createToast(util.systemMessages.success, "Success");
                                            isRefreshNeeded = true;
                                            $mdDialog.hide();
                                        }
                                    )
                                }
                            }
                            /////////

                        }],
                    templateUrl: "./" + config.lisDir + "/modules/dialogs/pcr-order-dialog.html",
                    parent: angular.element(document.body),
                    targetEvent: pcrWorkListOrder,
                    clickOutsideToClose: true,
                }).then(function (data) {
                    if (isRefreshNeeded)
                        $scope.selectWorkList($scope.selectedWorkList);
                }, function () { });
            };

        }

    ]);

    app.directive('focusMe', function () {
        return {
            scope: { trigger: '=focusMe' },
            link: function (scope, element) {
                scope.$watch('trigger', function (value) {
                    if (value === true) {
                        element[0].focus();
                        scope.trigger = false;
                    }
                });
            }
        };
    });

});
