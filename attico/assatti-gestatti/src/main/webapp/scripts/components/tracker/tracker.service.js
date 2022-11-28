'use strict';

angular.module('cifra2gestattiApp')
    .factory('Tracker', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        function sendActivity() {
            if ($rootScope.toState && $rootScope.toState.name && stompClient != null && stompClient.connected) {
                stompClient
                    .send('/topic/activity',
                    {},
                    JSON.stringify({'page': $rootScope.toState.name}));
            }
        }
        return {
            connect: function () {
            	if (!$rootScope.trackerAlreadyConnectedOnce) {
	                var socket = new SockJS('/websocket/tracker');
	                stompClient = Stomp.over(socket);
	                var headers = {};
	                headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
		                stompClient.connect(headers, function(frame) {
		                    connected.resolve("success");
		                    sendActivity();
		                    if (!$rootScope.trackerAlreadyConnectedOnce) {
		                        $rootScope.$on('$stateChangeStart', function (event) {
		                            sendActivity();
		                        });
		                        $rootScope.trackerAlreadyConnectedOnce = true;
		                    }
		                });
            	}
            },
            subscribe: function() {
                connected.promise.then(function() {
                    subscriber = stompClient.subscribe("/topic/tracker", function(data) {
                        listener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
            },
            receive: function() {
                return listener.promise;
            },
            sendActivity: function () {
                if (stompClient != null) {
                    sendActivity();
                }
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    $rootScope.trackerAlreadyConnectedOnce = false;
                    stompClient = null;
                }
            }
        };
    });
