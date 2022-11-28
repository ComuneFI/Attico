'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoRichiestaHD', function ($resource) {
        return $resource('api/tipoRichiestaHDs/:id/:action', {}, {
        	'enable': { method: 'GET', isArray: false, params: {action :'enable'}},
        	'disable': { method: 'GET', isArray: false, params: {action :'disable'}},
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'dirigenti': { method: 'GET', isArray: true, params:{action: 'dirigente'}}
        });
    });
