define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.service('socketService', ["$rootScope", "$state", "$window", function ($rootScope, $state, $window) {

        var self = this;
        self.types = {
            MACHINE_CONNECTION: "MACHINE_CONNECTION"
        };
        self.stompClient = null;

        self.open = function () {
            if (!util.token) {
                return;
            }
            var socket = new SockJS(config.server + config.contextRoot + 'websocketApp/?access_token=' + util.token);
            self.stompClient = Stomp.over(socket);
            self.stompClient.debug = null;// disable logging
            self.stompClient.connect({}, function () {
                self.stompClient.subscribe('/user/topic/data', self.onMessageReceived);
            });
        };

        self.close = function () {
            if (!self.stompClient || !self.stompClient.connected) {
                return;
            }

            self.stompClient.disconnect(function () {
                //console.log("Disconnected successfully!");
            });
        };

        self.onMessageReceived = function (payload) {
            if (!payload) {
                return;
            }
            //Some delay for queries/etc to take effect
            setTimeout(function () {
                $rootScope.$broadcast("onSocketReceive", JSON.parse(payload.body));
            }, 1000);
        };

        //try to reconnect every minute
        setInterval(function () {
            if (!self || !self.stompClient || self.stompClient.connected) {
                return;
            }
            self.open();

        }, 60000);


    }]);
});