'use strict';

angular.module('cifra2gestattiApp')
    .factory('CategoriaMsg', function ($resource) {
        return $resource('api/categoriaMsg/:id/:action', {}, {
        	'update': { method: 'POST', isArray: false, params: {action :'update'}},
        	'enable': { method: 'GET', isArray: false, params: {action :'enable'}},
        	'disable': { method: 'GET', isArray: false, params: {action :'disable'}},
            'search': { method: 'POST', isArray: true, params: {action :'search'}},
            'getAllEnabled': { method: 'GET', isArray: true, params: {action :'getAllEnabled'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
