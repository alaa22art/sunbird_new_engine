define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.service('socketService', ["$rootScope", "$state", function ($rootScope, $state) {

        var stompClient;
        var _this = this;
        var connectionTimeout1 = 0;
        var connectionTimeout2 = 1000;

        this.openSocket = function () {
            if (!util.token) {
                return;
            }
            var socket = new SockJS(config.server + config.contextRoot + 'websocketApp/?access_token=' + util.token);
            stompClient = Stomp.over(socket);
            stompClient.debug = null; //this disables logging
            stompClient.connect({}, connectionSuccess, connectionError);

            function connectionSuccess() {
                connectionTimeout1 = 0;
                connectionTimeout2 = 1000;
                stompClient.subscribe('/user/topic/data', onMessageReceived);
            }

            function connectionError(e) {
                var timeout = connectionTimeout1 + connectionTimeout2;
                setTimeout(function () {
                    _this.openSocket();
                }, timeout);
                if (timeout < 300000) {
                    connectionTimeout2 += connectionTimeout1;
                    connectionTimeout1 = connectionTimeout2 - connectionTimeout1;
                }
            }
        }

        this.closeSocket = function () {
            if (stompClient && stompClient.connected) {
                stompClient.disconnect(function () {
                    //console.log("Disconnected successfully!");
                });
            }
        };

        // this.sendMessage = function (event) {
        //     stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({ "some": "data" }));
        // }

        function onMessageReceived(payload) {
            return;//disabled because it is not used
            payload = JSON.parse(payload.body);
            var type = payload.type;
            var data = payload.data;
            var title = "";
            var body = "";
            var onClick = function () { };
            switch (type) {
                default:
                    return;
            }

            if (!("Notification" in window)) {
                // Let's check if the browser supports notifications
                toast(title, body, onClick);
            } else if (Notification.permission === "granted") {
                // Let's check whether notification permissions have already been granted,If it's okay let's create a notification
                browerNotification(title, body, onClick);
            } else if (Notification.permission !== 'denied') {
                // Otherwise, we need to ask the user for permission
                Notification.requestPermission(function (permission) {
                    // If the user accepts, let's create a notification
                    if (permission === "granted") {
                        browerNotification(title, body, onClick);
                    } else {
                        toast(title, body, onClick);
                    }
                });
            } else if (Notification.permission === 'denied') {
                //Finally, if the user has denied notifications and you, want to be respectful there is no need to bother them any more.
                toast(title, body, onClick);
            }
        }

        function browerNotification(title, body, onClick) {
            var notification = new Notification(title, {
                body: body,
                icon: "./assets/images/tube.png"
            });
            notification.onclick = onClick;
        }


        function toast(title, body, onClick) {
            util.createToast(body, "info", 15000, { title: title, clickFunction: onClick });
        }

    }]);
});