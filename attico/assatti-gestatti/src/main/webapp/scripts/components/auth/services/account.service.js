'use strict';

angular.module('cifra2gestattiApp')
    .factory('Account', function Account($resource) {
        return $resource('api/account', {}, {
            'get': { method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        return response;
                    }
                }
            }
        });
    });

angular.module('cifra2gestattiApp')
.factory('AccountInfo', function ($resource) {
    return $resource('api/account/logged', {}, {
    	'isLogged': { method: 'GET', isArray: false,
            interceptor: {
                response: function(response) {
                    return response;
                }
            }
    	}
    });
});
