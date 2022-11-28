'use strict';

angular.module('cifra2gestattiApp')
    .factory('Avanzamento', function ($resource) {
        return $resource('api/avanzamentos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'getByAtto': { method: 'GET', isArray: true, params:{'action':'getByAtto'}},
            'search': { method: 'POST', isArray: true, params:{'action':'search'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.dataAttivita != null) data.dataAttivita = new Date(data.dataAttivita);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
