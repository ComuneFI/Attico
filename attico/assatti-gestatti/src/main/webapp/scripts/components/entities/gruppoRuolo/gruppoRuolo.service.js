'use strict';

angular.module('cifra2gestattiApp')
    .factory('GruppoRuolo', function ($resource) {
        return $resource('api/gruppoRuolos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'getByAooId': { method: 'GET', isArray: true, params:{'action':'getByAooId'} },
            
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
