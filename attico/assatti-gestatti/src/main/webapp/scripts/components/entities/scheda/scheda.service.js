'use strict';

angular.module('cifra2gestattiApp')
    .factory('Scheda', function ($resource) {
        return $resource('api/schedas/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'SchedaDato': { method: 'GET', isArray: true,params:{'action':'SchedaDato'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'deleteForce':{method:'DELETE', params:{'action':'deleteForce'}}
        });
    });
