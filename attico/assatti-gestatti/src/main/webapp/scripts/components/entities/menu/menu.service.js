'use strict';

angular.module('cifra2gestattiApp')
    .factory('Menu', function ($resource) {
        return $resource('api/menus/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'menubar': { method: 'GET', isArray: true, params:{action:'bar'} },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
        // ,$resource('api/menubar/', {}, {
        //     'menubar': { method: 'GET', isArray: true}
        // });
    });