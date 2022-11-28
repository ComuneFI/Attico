'use strict';

angular.module('cifra2gestattiApp')
    .factory('ProgressivoProposta', function ($resource) {
        return $resource('api/progressivoPropostas/:id/:action', {}, {
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
