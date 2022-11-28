'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoFinanziamento', function ($resource) {
        return $resource('api/tipoFinanziamentos/:id/:action/:codice', {}, {
        	'enable': { method: 'GET', isArray: false, params: {action :'enable'}},
        	'disable': { method: 'GET', isArray: false, params: {action :'disable'}},
            'query': { method: 'GET', isArray: true},
            'getDescriptionByCode' : { method: 'GET', isArray: false, params: {action :'getDescriptionByCode'}},
            'getAllEnabled': { method: 'GET', isArray: true, params: {action :'getAllEnabled'}},
            'getAll': { method: 'GET', isArray: true, params: {action :'getAll'}},
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
