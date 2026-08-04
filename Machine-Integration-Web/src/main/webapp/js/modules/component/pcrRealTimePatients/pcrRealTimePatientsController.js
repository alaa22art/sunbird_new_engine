define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('realTimePCRPatientsCtrl', [
        '$scope',
        'realTimePCRPatientsService',
        'systemSettingService',
        function (
            $scope,
            realTimePCRPatientsService,
            systemSettingService
        ) {

            //check if the user is enable to view pcr module
            systemSettingService.getIsEnabledToViewPCR(commonData.veiwPcrPageType).then(function (response) {
                $scope.isEnableToView = response.data;
            })


            var sort = {
                direction: "DESC",
                property: "creationDate"
            }

            $scope.currentPcrOrdersFPR = {
                filters: [],
                sortList: [sort],
                page: 0,
                size: 15
            };

            $scope.OPEN = commonData.workListStatus.OPEN;
            $scope.IN_PROGRESS = commonData.workListStatus.IN_PROGRESS;


            var patientNameSearchCriteria = {
                service: function (fpr) {
                    return realTimePCRPatientsService.getPcrOrdersPage(fpr).then(function (response) {
                        return response;
                    });
                },
                callback: function (filters) {
                    if ($scope.patientsSearchOptions.selectedItem && $scope.patientsSearchOptions.selectedItem.rid !== -1) {
                        var fpr = {
                            filters: [],
                            sortList: [sort],
                            page: 0
                            // size: 10
                        };
                        fpr.page = 0;
                        fpr.size = 10;
                        fpr.filters.push({
                            field: "machineOrder.patientFirstName",
                            value: $scope.patientsSearchOptions.selectedItem.machineOrder.patientFirstName,
                            operator: "eq"
                        });

                        $scope.pcrOrdersPage.content = [];
                        $scope.getPcrOrdersPageData(fpr);
                    } else if (filters) {
                        refreshPcrOrdersData();
                    }
                },
                skeleton: {
                    code: "machineOrder.patientFirstName",
                    description: "machineOrder.patientLastName"
                },
                filterList: ["machineOrder.patientFirstName", "machineOrder.patientLastName", "machineOrder.barcode"],
                sortList: [{ property: "creationDate", direction: "DESC" }],
                label: "pcr orders"
            };


            var barcodeSearchCriteria = {
                service: function (fpr) {
                    return realTimePCRPatientsService.getPcrOrdersPage(fpr).then(function (response) {
                        return response;
                    });
                },
                callback: function (filters) {
                    if ($scope.patientsSearchOptions.selectedItem && $scope.patientsSearchOptions.selectedItem.rid !== -1) {
                        var fpr = {
                            filters: [],
                            sortList: [sort],
                            page: 0
                            // size: 10
                        };
                        fpr.page = 0;
                        fpr.size = 10;
                        fpr.filters.push({
                            field: "machineOrder.barcode",
                            value: $scope.patientsSearchOptions.selectedItem.machineOrder.barcode,
                            operator: "eq"
                        });

                        $scope.pcrOrdersPage.content = [];
                        $scope.getPcrOrdersPageData(fpr);
                    } else if (filters) {
                        refreshPcrOrdersData();
                    }
                },
                skeleton: {
                    code: "machineOrder.barcode",
                    description: "machineOrder.barcode"
                },
                filterList: ["machineOrder.barcode"],
                sortList: [{ property: "creationDate", direction: "DESC" }],
                label: "pcr orders"
            };


            var worklistNameSearchCriteria = {
                service: function (fpr) {
                    return realTimePCRPatientsService.getPcrOrdersPage(fpr).then(function (response) {
                        return response;
                    });
                },
                callback: function (filters) {
                    if ($scope.patientsSearchOptions.selectedItem && $scope.patientsSearchOptions.selectedItem.rid !== -1) {
                        var fpr = {
                            filters: [],
                            sortList: [sort],
                            page: 0
                        };
                        fpr.page = 0;
                        fpr.size = 1000;
                        fpr.filters.push({
                            field: "workListOrders.pcrRealTimeWorkList.name",
                            value: $scope.patientsSearchOptions.selectedItem.workListOrders[0].pcrRealTimeWorkList.name,
                            operator: "eq"
                        });

                        $scope.currentPcrOrdersFPR = fpr;

                        $scope.pcrOrdersPage.content = [];
                        $scope.getPcrOrdersPageData(fpr);
                    } else if (filters) {
                        refreshPcrOrdersData();
                    }
                },
                skeleton: {
                    code: "workListOrders[0].pcrRealTimeWorkList.name",
                    description: "workListOrders[0].pcrRealTimeWorkList.name"
                },
                filterList: ["workListOrders.pcrRealTimeWorkList.name"],
                sortList: [{ property: "creationDate", direction: "DESC" }],
                label: "pcr orders"
            };

            $scope.pcrOrdersPage = { content: [], totalPages: 1000, number: -1 }

            $scope.getPcrOrdersPageData = function (filterablePageRequest) {
                //exceeded the limit
                if (filterablePageRequest.page > $scope.pcrOrdersPage.totalPages) {
                    return;
                }
                return realTimePCRPatientsService.getPcrOrdersPage(filterablePageRequest).then(function (response) {
                    response.data.content = $scope.pcrOrdersPage.content.concat(response.data.content);
                    $scope.pcrOrdersPage = response.data;
                    console.log(response.data);
                    $scope.assignClass();
                });
            };


            $scope.onPcrOrdersScroll = function () {
                $scope.currentPcrOrdersFPR.page = $scope.pcrOrdersPage.number + 1;
                return $scope.getPcrOrdersPageData($scope.currentPcrOrdersFPR);
            };

            $scope.onPcrOrdersScroll();

            function refreshPcrOrdersData() {
                $scope.pcrOrdersPage.content = [];
                $scope.currentPcrOrdersFPR = {
                    filters: [],
                    sortList: [sort],
                    page: 0,
                    size: 15
                };

                $scope.getPcrOrdersPageData($scope.currentPcrOrdersFPR);

            }

            //to get yyyymmdd from the timestamp 
            $scope.getSequence = function (date) {
                var seq = date.slice(0, date.indexOf("T"));
                // seq = seq.replaceAll("-", "");
                return seq;
            }


            //this is a temporary solution
            $scope.positiveTxt = commonData.pcrResults.positive;
            $scope.negativeTxt = commonData.pcrResults.negative;
            $scope.undeterminedTxt = commonData.pcrResults.undetermined;
            $scope.notStarted = "Not-Started";
            $scope.inProgress = "In-Progress";

            $scope.classes = {};
            $scope.assignClass = function () {

                for (var i = 0; i < $scope.pcrOrdersPage.content.length; i++) {
                    // $scope.classes[$scope.pcrOrdersPage.content[i].rid] = getClass($scope.pcrOrdersPage.content[i]);
                    getClass($scope.pcrOrdersPage.content[i]);

                }

            }


            function getClass(pcrOrder) {
                $scope.classes[pcrOrder.rid] = {};
                if (!pcrOrder) {
                    $scope.classes[pcrOrder.rid].isStarted = true;
                    return "not-started";
                }
                if (pcrOrder.workListOrders.length <= 0 || pcrOrder.workListOrders[0].pcrRealTimeWorkList.workListStatus.code === $scope.OPEN) {
                    $scope.classes[pcrOrder.rid].isStarted = true;
                    return "not-started";
                }
                if (pcrOrder.workListOrders[0].pcrRealTimeOrderResults.length <= 0 || pcrOrder.workListOrders[0].pcrRealTimeWorkList.workListStatus.code === $scope.IN_PROGRESS) {
                    $scope.classes[pcrOrder.rid].isInProgress = true;
                    return "in-progress";
                }

                if (pcrOrder.workListOrders[0].pcrRealTimeOrderResults[0].pcrRealTimeResult.isResultSent) {
                    $scope.classes[pcrOrder.rid].isResultSent = true;
                } else {
                    $scope.classes[pcrOrder.rid].isResultSent = false;
                }

                var temp = "";
                for (var i = 0; i < pcrOrder.workListOrders[0].pcrRealTimeOrderResults[0].pcrRealTimeResult.pcrRealTimeActualResultValues.length; i++) {
                    temp = pcrOrder.workListOrders[0].pcrRealTimeOrderResults[0].pcrRealTimeResult.pcrRealTimeActualResultValues[i];
                    if (temp.pcrRealTimeResultTemplateLine.code == "Result") {

                        if (temp.value == $scope.positiveTxt) {
                            $scope.classes[pcrOrder.rid].isPositive = true;
                            return "positive";
                        }
                        if (temp.value == $scope.negativeTxt) {
                            $scope.classes[pcrOrder.rid].isNegative = true;
                            return "negative";
                        }
                        if (temp.value == $scope.undeterminedTxt) {
                            $scope.classes[pcrOrder.rid].isUndetermined = true;
                            return "undetermined";
                        }
                    }
                }

                return "";
            }

            $scope.patientsSearchOptions = patientNameSearchCriteria;
            $scope.searchOptions = ["Patient", "Barcode", "Worklist"];
            $scope.selectedSearchCriteria = {};
            $scope.selectedSearchCriteria.value = "Patient";

            $scope.changeCriteria = function () {
                $scope.patientsSearchOptions.reset();
                refreshPcrOrdersData();
                var resetFn = $scope.patientsSearchOptions.reset;

                if ($scope.selectedSearchCriteria.value == "Patient")
                    $scope.patientsSearchOptions = patientNameSearchCriteria;

                if ($scope.selectedSearchCriteria.value == "Barcode")
                    $scope.patientsSearchOptions = barcodeSearchCriteria;

                if ($scope.selectedSearchCriteria.value == "Worklist")
                    $scope.patientsSearchOptions = worklistNameSearchCriteria;

                $scope.patientsSearchOptions.reset = resetFn;
            }

            $scope.pcrResultOptions = {
                onClick: function (pcrResult) {
                    var fpr = {
                        filters: [{
                            field: "workListOrders.pcrRealTimeOrderResults.pcrRealTimeResult.pcrRealTimeActualResultValues.value",
                            value: pcrResult,
                            operator: "eq"
                        }],
                        sortList: [sort],
                        page: 0,
                        size: 15
                    };
                    $scope.pcrOrdersPage.content = [];
                    $scope.currentPcrOrdersFPR = fpr;
                    $scope.getPcrOrdersPageData($scope.currentPcrOrdersFPR);
                },


                selections: ["All", $scope.positiveTxt, $scope.negativeTxt, $scope.undeterminedTxt]
            };


        }

    ]);



});





