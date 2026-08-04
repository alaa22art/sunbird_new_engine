define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('messegesTransactionLogCtrl', [
        '$scope',
        'messegesTransactionLogService',
        '$filter',
        '$state',
        '$rootScope',
        'branchFormService',
        function (
            $scope,
            messegesTransactionLogService,
            $filter,
            $state,
            $rootScope,
            branchFormService
        ) {
            $scope.selectedMachine = null;

            var fromDate = new Date();
            fromDate.setHours(0, 0, 0, 0);
            fromDate.setDate(fromDate.getDate() - 1);
            var toDate = new Date();
            toDate.setHours(23, 59, 59, 999);//last time in this date
            $scope.filters = {
                barcode: null,
                messageControlID: null,
                from: fromDate,
                to: toDate
            };

            $scope.branchLkp = {
                className: 'LabBranch',
                name: 'labBranch',
                labelText: 'branch',
                valueField: 'label',
                selectedValue: null,
                required: true,
                data: []
            };

            branchFormService.getBranchesWithAllOption({ filters: [] }).then(function (data) {
                $scope.branchLkp.selectedValue = data.find(function (obj) { return obj.rid === -1; });
                if ($scope.branchLkp.updateData) {
                    $scope.branchLkp.updateData(data);
                } else {
                    $scope.branchLkp.data = data;
                }
            });

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(dataSource);

                        //add form filters
                        for (var key in $scope.filters) {
                            var filterValue = $scope.filters[key];
                            if (filterValue) {
                                switch (key) {
                                    case "barcode":
                                        e.data.filters.push({
                                            field: key,
                                            value: filterValue,
                                            operator: "eq"
                                        });
                                        break;
                                    case "from":
                                        e.data.filters.push({
                                            field: "creationDate",
                                            value: filterValue,
                                            operator: "gte"
                                        });
                                        break;
                                    case "to":
                                        e.data.filters.push({
                                            field: "creationDate",
                                            value: filterValue,
                                            operator: "lte"
                                        });
                                        break;
                                    case "messageControlID":
                                        e.data.filters.push({
                                            field: key,
                                            value: filterValue,
                                            operator: "eq"
                                        });
                                        break;
                                    case "patientID":
                                        e.data.filters.push({
                                            field: key,
                                            value: filterValue,
                                            operator: "eq"
                                        });
                                        break;
                                  /*  case "nationalID":
                                        e.data.filters.push({
                                            field: key,
                                            value: filterValue,
                                            operator: "eq"
                                        });
                                        break;*/
                                        // case "messageType":
                                        //     e.data.filters.push({
                                        //         field: key,
                                        //         value: filterValue,
                                        //         operator: "eq"
                                        //     });
                                        //     break;
                                }

                            }
                        }

                        if ($scope.selectedMachine) {
                            e.data.filters.push({
                                field: "machine.rid",
                                value: $scope.selectedMachine.rid,
                                operator: "eq"
                            });
                        }

                        if ($scope.branchLkp && $scope.branchLkp.selectedValue && $scope.branchLkp.selectedValue.rid !== -1) {
                            e.data.filters.push({
                                field: "branchId",
                                value: $scope.branchLkp.selectedValue.rid,
                                operator: "eq"
                            });
                        }

                        messegesTransactionLogService.getMessageTransactionPage(e.data).then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            rid: { type: "number" },
                            barcode: { type: "string" },
                            messageType: { type: "object" },
                            creationDate: { type: "date" },
                            messageDirection: { type: "object" },
                            branch: { type: "object" },
                            notes: { type: "string" },
                            messageBody: { type: "string" },
                            status: { type: "boolean" },
                            machine: { type: "object" },
                            messageControlID: { type: "string" },
                            isSuccuss: { type: "boolean" },
                            isValidated: { type: "boolean" },
                            isSent: { type: "Boolean" },
                            ackmessageText: { type: "string" },
                            response: { type: "string" },
                            patientID: { type: "string" },
                            nationalID: { type: "string" },
                            adtOperationType: { type: "string" }
                        }
                    }
                }
            });

            $scope.gridOptions = {
                columns: [
                 /*   {
                        field: "messageControlID",
                        title: util.systemMessages.messageControlID


                    },*/
                    {
                        field: "barcode",
                        title: util.systemMessages.barcode,
                        template: function (dataItem) {
                            return dataItem.barcode ? "<div><a href='' ng-click='goToOrder(" + dataItem.rid + ")'>" + dataItem.barcode + "</a></div>" : "";
                        },
                        hidden: true

                    },
                   /* {
                        field: "adtOperationType",
                        title: util.systemMessages.adtOperationType
                    }
                    ,*/
                    {
                        field: "messageType",
                        title: util.systemMessages.type,
                        template: function (dataItem) {
                            var result = "";
                            if (dataItem.messageType) {
                                // QUERY_RECEIVED
                                // RESULT_RECEIVED
                                // TEST_SELECTION
                                // CONNECTION_OPEN
                                // CONNECTION_CLOSE
                                switch (dataItem.messageType.code) {
                                    case 'TEST_SELECTION':
                                        result = "O";
                                        break;
                                    case 'QUERY_RECEIVED':
                                        result = "Q";
                                        break;
                                    case 'RESULT_RECEIVED':
                                        result = "R";
                                        break;
                                    default:
                                        result = dataItem.messageType.code;
                                        break;
                                }
                            }
                            return "<div class='bold text-center'>" + result + "</div>";
                        }
                    },
                    /*{
                        field: "patientID",
                        title: util.systemMessages.patientID,
                        hidden: true


                    }
                    /*,
                    {
                        field: "nationalID",
                        title: util.systemMessages.nationalID,
                        hidden: true


                    }
                    ,*/
                    {
                        field: "creationDate",
                        title: util.systemMessages.date,
                        template: function (dataItem) {
                            return $filter("dateTimeFormat")(dataItem.creationDate);
                        }
                    },
                    {
                        field: "messageDirection",
                        title: util.systemMessages.direction,
                        width: 150,
                        template: function (dataItem) {
                            var result = "";
                            if (dataItem.messageDirection) {
                                var icon = dataItem.messageDirection.code === "IN" ?
                                    "fas fa-arrow-circle-right" : "fas fa-arrow-circle-left";
                                result = "<div class='text-center' style='font-size:1.8em'><md-icon md-font-icon='" + icon + "'></md-icon></div>";
                            }
                            return result;
                        }
                    },
                    {
                        field: "branch",
                        title: util.systemMessages.branch,
                        template: function (dataItem) {
                            return dataItem.transients.branch ? dataItem.transients.branch.name[util.userLocale] : "";
                        }
                        , hidden: true

                    },
                    {
                        field: "notes",
                        title: util.systemMessages.notes,
                        hidden: true

                    },
                    /* {
                         field: "status",
                         title: util.systemMessages.status,
                         width: 150,
                         template: function (dataItem) {
                             var color = "";
                             if (dataItem.isProcessed) {
                                 color = "warning-color";
                             } else {
                                 if (dataItem.isSuccess) {
                                     color = "success-color";
                                 } else {
                                     color = "error-color";
                                 }
                             }
 
                             return "<div class='text-center' style='font-size:1.5em'><md-icon class='" + color + "' md-font-icon='fas fa-circle'></md-icon></div>";
                         }
                     },*/



                    {
                        field: "messageBody",
                        title: util.systemMessages.data,
                        hidden: true
                    },
                    {
                        field: "response",
                        title: util.systemMessages.response,
                        hidden: true
                    }
                    ,
                    {
                        field: "ackmessageText",
                        title: util.systemMessages.ackmessageText,
                        hidden: true
                    },
                    {
                        field: "machine",
                        title: util.systemMessages.machine,
                        hidden: true,
                        template: function (dataItem) {
                            return dataItem.machine ? dataItem.machine.name : "";
                        }
                    }
                    
                 /*    ,
                   {
                        field: "isSuccuss",
                        title: util.systemMessages.isSuccess,
                        width: 150,
                        template: function (dataItem) {
                            var color = "";
                            if (!dataItem.isSuccuss) {
                                color = "error-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-times-circle'></md-icon></div>";
                            } else {
                                color = "success-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-check-circle'></md-icon></div>";
                            }
                        }
                    }, {
                        field: "isValidated",
                        title: util.systemMessages.isValidated,
                        width: 150,
                        template: function (dataItem) {
                            var color = "";
                            if (!dataItem.isValidated) {
                                color = "error-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-times-circle'></md-icon></div>";
                            } else {
                                color = "success-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-check-circle'></md-icon></div>";
                            }
                        }
                    },
                    {
                        field: "isSent",
                        title: util.systemMessages.isSent,
                        width: 150,
                        template: function (dataItem) {
                            var color = "";
                            if (!dataItem.isSent) {
                                color = "error-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-times-circle'></md-icon></div>";
                            } else {
                                color = "success-color";
                                return "<div class='text-center' style='font-size:1.8em'><md-icon class='" + color + "' md-font-icon='fas fa-check-circle'></md-icon></div>";
                            }
                        }
                    }*/

                ],
                autoBind: false,
                dataSource: dataSource
            };


            $scope.refreshGrid = function () {
                dataSource.read();
            };

            $scope.goToOrder = function (messageTransactionRid) {
                $rootScope.patientBarcode = dataSource.data().find(function (obj) { return obj.rid === messageTransactionRid; }).barcode;
                $state.go("patient-sample-lookup");
            };

            //initial fetch
            angular.element(function () {
                $scope.refreshGrid();
            });
        }
    ]);
});
