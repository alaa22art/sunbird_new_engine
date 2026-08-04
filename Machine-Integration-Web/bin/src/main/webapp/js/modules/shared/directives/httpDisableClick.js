define(['app'], function (app) {
    'use strict';
    /**
     * Disable the button that requested the server till the response is back. Supports button/form.
     * Just put http-disable-click on the element.
     * TODO: the click handler does not work if there is a ng-disabled={{expression}}
     */
    app.directive('httpDisableClick', ["$http", "APIInterceptor", "$rootScope", "$timeout", function ($http, APIInterceptor, $rootScope, $timeout) {
        var globalRequestIds = [];
        return {
            restrict: 'A',
            link: function (scope, element) {

                var requestId = null;
                var loadingIconDelay = 2000;// the delay before we add/remove the appended loading icon
                var isForm = element.is('form');
                var elementToDisable = element;//the same element OR the submit element of the form
                if (isForm) {
                    element.bind('submit', function () {
                        elementToDisable = element.find(":submit");
                        httpDisableClick();
                    });
                } else {
                    element.bind('click', function () {
                        httpDisableClick();
                    });
                }

                function httpDisableClick() {
                    var lastIdx = $http.pendingRequests.length - 1;
                    if (lastIdx < 0) {
                        return;
                    }
                    var requestObj = $http.pendingRequests[lastIdx];
                    elementToDisable.attr("disabled", "disabled");

                    requestId = Math.floor(Math.random() * 10000);//0 - 10000
                    while (globalRequestIds.indexOf(requestId) >= 0) {
                        requestId = Math.floor(Math.random() * 10000);
                    }
                    requestObj["id"] = requestId;
                    globalRequestIds.push(requestId);
                    APIInterceptor.insertServerRequest(requestObj);

                    $timeout(function () {
                        if (globalRequestIds.indexOf(requestId) > -1) {
                            elementToDisable.children().hide();
                            elementToDisable.append("<i id='hdc_" + requestId + "' class='fas fa-spinner fa-spin' style='color:white;' aria-hidden='true'></i>");
                            //elementToDisable.append("<md-progress-circular id='hdc_" + requestId + "' md-mode='indeterminate' md-diameter='20'></md-progress-circular>");
                        }
                    }, loadingIconDelay);

                }

                //Broadcasted from APIInterceptor
                $rootScope.$on("httpEnableClick", function (event, data) {
                    if (data == requestId) {
                        var serverRequestsArray = APIInterceptor.getServerRequests();
                        var idx = serverRequestsArray.map(function (req) {
                            return req.id;
                        }).indexOf(requestId);
                        if (idx >= 0) {
                            serverRequestsArray.splice(idx, 1);
                        }

                        globalRequestIds.splice(globalRequestIds.indexOf(requestId), 1);
                        $("#hdc_" + requestId).remove();
                        elementToDisable.children().show();
                        elementToDisable.removeAttr("disabled");

                    }
                });

            }
        };
    }]);
});