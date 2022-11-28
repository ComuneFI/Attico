'use strict';

angular.module('cifra2gestattiApp')
    .factory('ReportRuntime', function ($resource) {
        return $resource('api/reportRuntimes/:id', {}, {
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
