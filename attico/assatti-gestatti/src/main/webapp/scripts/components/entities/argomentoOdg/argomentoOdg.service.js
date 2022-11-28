'use strict';

angular.module('cifra2gestattiApp')
    .factory('ArgomentoOdg', function ($resource) {
        return $resource('api/argomentoOdgs/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'getAll': { method: 'GET', isArray: true},
            'update': { method:'PUT' }
        });
    });
