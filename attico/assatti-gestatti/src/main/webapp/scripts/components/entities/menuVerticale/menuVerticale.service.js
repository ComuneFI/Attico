'use strict';

angular.module('cifra2gestattiApp')
    .factory('MenuVerticale', function ($resource) {
        return $resource('api/menuVerticales/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'fromMenu': { method: 'GET', isArray: true, params:{action:'frommenu'}},
            'update': { method:'PUT' }
        });
    });
