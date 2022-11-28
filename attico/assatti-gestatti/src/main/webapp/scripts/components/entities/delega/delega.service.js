'use strict';

angular.module('cifra2gestattiApp')
    .factory('Delega', function ($resource) {
        return $resource('api/delega/:id/:action/:idDelegato', {}, {
        	'search': { method: 'POST', isArray: true, params:{action:'search'}},
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'delete': { method:'DELETE' },
            'getDelegantiByDelegatoProfiloId': { method: 'GET', isArray: true, params:{action:'deleganti', idDelegato:'@idDelegato'}},
			'exists': { method: 'POST', isArray: false, params:{action:'exists'}}
        });
    });
