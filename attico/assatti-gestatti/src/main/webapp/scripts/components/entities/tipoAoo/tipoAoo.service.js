'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoAoo', function ($resource) {
        return $resource('api/tipoAoos/:id', {}, {
            'query': { method: 'GET', isArray: true},
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
