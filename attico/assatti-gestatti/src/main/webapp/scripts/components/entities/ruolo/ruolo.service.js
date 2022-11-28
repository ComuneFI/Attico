'use strict';

angular.module('cifra2gestattiApp')
    .factory('Ruolo', function ($resource) {
        return $resource('api/ruolos/:id/:action/:codice', {}, {
        	'enable': { method: 'GET', isArray: false, params: {action :'enable'}},
        	'disable': { method: 'GET', isArray: false, params: {action :'disable'}},
            'query': { method: 'GET', isArray: true},
            'getDescriptionByCode' : { method: 'GET', isArray: false, params: {action :'getDescriptionByCode'}},
            'getAllEnabled': { method: 'GET', isArray: true, params: {action :'getAllEnabled'}},
            'isRoleUsedInGruppoRuolo': { method: 'GET', isArray: false, params: {action :'isRoleUsedInGruppoRuolo'}},
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
