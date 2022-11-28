'use strict';

angular.module('cifra2gestattiApp')
    .factory('Auth', function Auth($rootScope, $state, $q, $translate, Principal, AuthServerProvider, Account
                                    , Register, Activate, Password, PasswordResetInit, PasswordResetFinish
                                    , Tracker, $log, ngToast, $window, ProfiloAccount) {
        return {
            login: function (credentials, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();
                $rootScope.lastMsgIdentifier = null;
                AuthServerProvider.login(credentials).then(function (data) {
                    // retrieve the logged account information
                    Principal.identity(true).then(function(account) {
                        // After the login the language will be changed to
                        // the language selected by the user during his registration
                    	if(account != undefined && account != null){
	                        $translate.use(account.langKey);
	                        /*Tracker.sendActivity();*/
	                        deferred.resolve(data);
	                        $rootScope.$broadcast('okLoginEvent', account);
                    	}else{
                            deferred.reject(data);
                            return cb(data);
                    	}
                    });
                    return cb();
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

                return deferred.promise;
            },

            refresh: function (callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();

                AuthServerProvider.refresh().then(function (data) {   
                    return cb();
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

                return deferred.promise;
            },
            logoutLS: function(){
            	var deferred = $q.defer();
            	AuthServerProvider.logoutLS().then(function(){
            		deferred.resolve();
            	});
            	return deferred.promise;
            },
            logout: function (calledBy) {
				console.log("CalledBy", calledBy);
        		AuthServerProvider.logout();
                Principal.authenticate(null);
                delete $rootScope.profiloattivo;
                delete $rootScope.account;
                
                $rootScope.$broadcast('LogoutEvent');
                if( $rootScope.configurationParams.logout_url_enabled){
                	console.log("REDIRECT URL LOGOUT: ", $rootScope.configurationParams.logout_url);                	
                	$window.location.href = $rootScope.configurationParams.logout_url;

                	// $state.go('accessdenied');
                }
                /*Tracker.disconnect();*/
            },

            authorize: function(force) {
                return Principal.identity(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();

                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles) && (!$rootScope.toState.data.customRoles || !ProfiloAccount.isInAnyRole($rootScope.toState.data.customRoles))) {
                            if (isAuthenticated) {
                                // user is signed in but not authorized for desired state
                                $state.go('accessdenied');
                            }
                            else {
                                // user is not authenticated. stow the state they wanted before you
                                // send them to the signin state, so you can return them when you're done
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;

                                // now, send them to the signin state so they can log in
                                $state.go('login');
                            }
                        }
                    });
            },
            createAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Register.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            updateAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Account.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            activateAccount: function (key, callback) {
                var cb = callback || angular.noop;

                return Activate.get(key,
                    function (response) {
                        return cb(response);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            changePassword: function (newPassword, callback) {
                var cb = callback || angular.noop;

                return Password.save(newPassword, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            },
            
            resetPasswordInit: function (mail, callback) {
                var cb = callback || angular.noop;

                return PasswordResetInit.save(mail, function() {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;

            },

            resetPasswordFinish: function(key, newPassword, callback) {
                var cb = callback || angular.noop;

                return PasswordResetFinish.save(key, newPassword, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            }
        };
    });
