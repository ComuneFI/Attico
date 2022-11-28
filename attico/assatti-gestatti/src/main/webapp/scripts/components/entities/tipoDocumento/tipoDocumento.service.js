'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoDocumento', function ($resource) {
        return $resource('api/tipoDocumentos/:action/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'checkIfAlreadyExists':{method:'GET', params:{action:'checkIfAlreadyExists'}}
        });
    });
