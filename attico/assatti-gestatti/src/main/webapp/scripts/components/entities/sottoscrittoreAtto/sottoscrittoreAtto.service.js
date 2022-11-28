'use strict';

angular.module('cifra2gestattiApp')
    .factory('SottoscrittoreAtto', function ($resource) {
        return $resource('api/sottoscrittoreAttos/:action/:aooid/:attoid/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'deletebyattoaoo': { method: 'DELETE', isArray: true, params:{'action':'deletebyattoaoo', attoid:'@attoid',aooid:'@aooid'}},
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
