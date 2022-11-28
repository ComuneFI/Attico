'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoProgressivo', function ($resource) {
        return $resource('api/tipoProgressivos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'getByTipoAtto': { method: 'GET', isArray: true, params:{'action':'getByTipoAtto'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });

