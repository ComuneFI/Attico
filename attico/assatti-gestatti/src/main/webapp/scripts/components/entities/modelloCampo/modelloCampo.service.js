'use strict';

angular.module('cifra2gestattiApp')
    .factory('ModelloCampo', function ($resource) {
        return $resource('api/modelloCampos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'getAllGlobal': { method: 'GET', isArray: true, params:{action:'getAllGlobal' } },
            'getAllMixed': { method: 'GET', isArray: true, params:{action:'getAllMixed' } },
            'getGlobal': { method: 'GET', isArray: false, params:{action:'getGlobal' } },
            
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
