'use strict';

angular.module('cifra2gestattiApp')
    .factory('Esito', function ($resource) {
        return $resource('api/esitos/:id/:action/', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'POST', params:{action:'update' }},
        });
    });

