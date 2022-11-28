'use strict';

angular.module('cifra2gestattiApp')
    .factory('ProgressivoAdozione', function ($resource) {
        return $resource('api/progressivoAdoziones/:id/:action', {}, {
        	'search': { method: 'POST', isArray: true, params: {action :'search'}},
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
