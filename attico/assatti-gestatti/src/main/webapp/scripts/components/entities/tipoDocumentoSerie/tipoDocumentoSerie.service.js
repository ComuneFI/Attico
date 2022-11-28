'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoDocumentoSerie', function ($resource) {
        return $resource('api/tipoDocumentoSeries/:id/:action/:abilitato', {}, {
        	'search': { method: 'POST', isArray: true, params: {action :'search'}},   
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'setIsAbilitato': {method:'PUT', isArray: false, params: {action: 'abilitato', id: '@id', abilitato:'@abilitato'}}
        });
    });
