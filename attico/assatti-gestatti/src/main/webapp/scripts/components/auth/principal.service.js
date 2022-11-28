'use strict';

angular.module('cifra2gestattiApp')
    .factory('Principal', function Principal($q, Account, Tracker, $rootScope) {
        var _identity,
            _authenticated = false;

        return {
            isIdentityResolved: function () {
                return angular.isDefined(_identity);
            },
            isAuthenticated: function () {
                return _authenticated;
            },
            isInRole: function (role) {
                if (!_authenticated || !_identity || !_identity.roles || ($rootScope.profiloOrigine && (role == 'ROLE_ADMIN' || role == 'ROLE_AMMINISTRATORE_RP')) ) {
                    return false;
                }

                return _identity.roles.indexOf(role) !== -1;
            },
            isInAnyRole: function (roles) {
                if (!_authenticated || !_identity.roles) {
                    return false;
                }

                for (var i = 0; i < roles.length; i++) {
                    if (this.isInRole(roles[i])) {
                        return true;
                    }
                }

                return false;
            },
            authenticate: function (identity) {
                _identity = identity;
                _authenticated = identity !== null;
            },
            identity: function (force) {
                var deferred = $q.defer();

                if (force === true) {
                    _identity = undefined;
                }

                // check and see if we have retrieved the identity data from the server.
                // if we have, reuse it by immediately resolving
                if (angular.isDefined(_identity)) {
                    deferred.resolve(_identity);

                    return deferred.promise;
                }

                // retrieve the identity data from the server, update the identity object, and then resolve.
                Account.get().$promise
                    .then(function (account) {
                        _identity = account.data;
                        _authenticated = true;
                        deferred.resolve(_identity);
                        /*Tracker.connect();*/
                    })
                    .catch(function(response) {
                    	if (response.status == 423 ) {
       	           		 $rootScope.$broadcast('Login423Event');
                    	}
                        _identity = null;
                        _authenticated = false;
                        deferred.resolve(_identity);
                    });
                return deferred.promise;
            },
            removeAdminRoles: function(){
            	if(_identity && _identity.roles){
	            	if(_identity.roles.indexOf('ROLE_AMMINISTRATORE_RP') >= 0){
	            		_identity.roles.splice(_identity.roles.indexOf('ROLE_AMMINISTRATORE_RP'), 1);
	            	}
	            	if(_identity.roles.indexOf('ROLE_ADMIN') >= 0){
	            		_identity.roles.splice(_identity.roles.indexOf('ROLE_ADMIN'), 1);
	            	}
            	}
            }
        };
    });
