'use strict';

angular.module('cifra2gestattiApp')
    .factory('Ufficio', function ($resource) {
        return $resource('api/ufficios/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            'getByAooId': { method: 'GET', isArray: true, params:{ action: 'getByAooId'}},
          
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.validodal != null){
                        var validodalFrom = data.validodal.split("-");
                        data.validodal = new Date(new Date(validodalFrom[0], validodalFrom[1] - 1, validodalFrom[2]));
                    }
                    if (data.validoal != null){
                        var validoalFrom = data.validoal.split("-");
                        data.validoal = new Date(new Date(validoalFrom[0], validoalFrom[1] - 1, validoalFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
