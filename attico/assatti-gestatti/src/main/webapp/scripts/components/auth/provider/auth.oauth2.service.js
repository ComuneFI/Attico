'use strict';

angular.module('cifra2gestattiApp')
    .factory('AuthServerProvider', function loginService($http, localStorageService, Base64, $log, $q) {
        return {
            login: function(credentials) {
                var data = "username=" + credentials.username + "&password="
                    + credentials.password + "&grant_type=password&scope=read%20write&" +
                    "client_secret=alsdkjfa09dsfausdfijasdclasi82937482f8a9s7df&client_id=cifra2gestattiapp";
                return $http.post('oauth/token', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        "Accept": "application/json",
                        "Authorization": "Basic " + Base64.encode("cifra2gestattiapp" + ':' + "alsdkjfa09dsfausdfijasdclasi82937482f8a9s7df")
                    }
                }).success(function (response) {
                    //$log.debug("token response: ", response);
                    var expiredAt = new Date();
                    expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
                    response.expires_at = expiredAt.getTime();
                    localStorageService.set('token', response);                 
                    return response;
                });
            },
            // aggiunto metodo che esegue refresh del token oauth nel caso in cui sta per scadere...
            refresh: function() {
                var token = this.getToken();
                //$log.debug("token : ", token);
                if( token === null || angular.isUndefined(token) ||  angular.isUndefined(token.refresh_token)){
                    
                }

                var data = "grant_type=refresh_token&scope=read%20write&" +
                    "client_secret=alsdkjfa09dsfausdfijasdclasi82937482f8a9s7df&client_id=cifra2gestattiapp";
                data = data + "&refresh_token=" + token.refresh_token;
                //$log.debug("token data: ", data);
                
                return $http.post('oauth/token', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        "Accept": "application/json",
                        "Authorization": "Basic " + Base64.encode("cifra2gestattiapp" + ':' + "alsdkjfa09dsfausdfijasdclasi82937482f8a9s7df") 
                    }
                }).success(function (response) {
                    //$log.debug("token response: ", response);
                    var expiredAt = new Date();
                    expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
                    response.expires_at = expiredAt.getTime();
                    localStorageService.set('token', response);
                    return response;
                });
            },
            logout: function() {
                // logout from the server
            	// la seconda chiamata (account/logout/custom) permette ad un eventuale filtro custom (specifico per il dominio) di eseguire del codice relativo al logout specifico per il dominio            	
                $http.post('api/logout').then(function() {
                    localStorageService.clearAll();
                    $http.post('account/logout/custom').then(function() {
                    })
                });
            	
            },
            logoutLS : function(){
            	var deferred = $q.defer();
            	var currentAccessToExpire = localStorageService.get("token") ? 'Bearer ' + localStorageService.get("token").access_token : '';
            	$http.post('api/logout', {}, 
	    			{headers : {
	    			"Content-Type": "application/x-www-form-urlencoded",
	                "Accept": "application/json",
	                "Authorization":currentAccessToExpire}}).success(function() {
	                	localStorageService.clearAll();
	                	deferred.resolve();
                }).error(function(){
                	deferred.resolve();
                });
            	return deferred.promise;
            },
            getToken: function () {
                return localStorageService.get('token');
            },
            hasValidToken: function () {
                var token = this.getToken();
                $log.debug("hasValidToken:",token && token.expires_at && token.expires_at > new Date().getTime());
                return token && token.expires_at && token.expires_at > new Date().getTime();
            }
        };
    });
