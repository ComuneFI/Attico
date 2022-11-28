'use strict';

angular.module('cifra2gestattiApp')
    .factory('RubricaDestinatarioEsterno', function ($resource) {
        return $resource('api/rubricaDestinatarioEsternos/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            'getByAooId': { method: 'GET', isArray: true, params:{ action: 'getByAooId'}},
            
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
