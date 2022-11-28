'use strict';

angular.module('cifra2gestattiApp')
    .factory('Faq', function ($resource) {
        return $resource('api/faqs/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'searchByProfiloId': { method: 'GET', isArray: true, params:{action:'searchByProfiloId'}},
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
