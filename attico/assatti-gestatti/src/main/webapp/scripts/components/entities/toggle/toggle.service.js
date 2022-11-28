'use strict';

angular.module('cifra2gestattiApp')
    .factory('Toggle', function ($resource) {
        return $resource('api/toggle', {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });