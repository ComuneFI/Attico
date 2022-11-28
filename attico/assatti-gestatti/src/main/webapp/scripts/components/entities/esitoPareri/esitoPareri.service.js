'use strict';

angular.module('cifra2gestattiApp')
    .factory('EsitoPareri', function ($resource) {
        return $resource('api/esitoPareris/:id/:action', {}, {
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
            'update': { method:'PUT' },
            'checkIfAlreadyexist':{method:'POST', isArray:false, params:{action:'checkIfAlreadyexist'}},
            'search':{method:'POST', isArray:true, params:{action:'search'}}
        });
    });
