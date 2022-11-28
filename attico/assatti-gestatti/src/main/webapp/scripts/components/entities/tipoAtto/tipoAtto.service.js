'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoAtto', function ($resource) {
        return $resource('api/tipoAttos/:id/:action/:codiceParam', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'getStatiByTipoAttoId': { method: 'GET', isArray: true, params:{action:'getStatiByTipoAttoId', id:'@id'}},
            'getByCodice': { method: 'GET', isArray: false, params:{action:'byCodice', codiceParam:'@codiceParam'}},
            'disable': { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
            'enable': { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}}
        });
    });
