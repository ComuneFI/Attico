'use strict';

angular.module('cifra2gestattiApp')
    .factory('Messaggio', function ($resource) {
        return $resource('api/messaggios', {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
