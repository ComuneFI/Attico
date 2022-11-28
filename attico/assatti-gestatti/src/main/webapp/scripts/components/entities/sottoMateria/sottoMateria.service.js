'use strict';

angular.module('cifra2gestattiApp')
    .factory('SottoMateria', function ($resource) {
        return $resource('api/sottoMaterias/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
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
