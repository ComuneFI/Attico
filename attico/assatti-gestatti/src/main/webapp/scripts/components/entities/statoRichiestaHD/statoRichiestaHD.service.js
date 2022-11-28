'use strict';

angular.module('cifra2gestattiApp')
    .factory('StatoRichiestaHD', function ($resource) {
        return $resource('api/statoRichiestaHDs/:id', {}, {
            'query': { method: 'GET', isArray: true},
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
