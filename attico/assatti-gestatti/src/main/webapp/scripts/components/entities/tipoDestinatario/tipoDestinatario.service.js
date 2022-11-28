'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoDestinatario', function ($resource) {
        return $resource('api/tipoDestinatarios', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });