'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoOdg', function ($resource) {
        return $resource('api/tipoOdgs/:id', {}, {
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
