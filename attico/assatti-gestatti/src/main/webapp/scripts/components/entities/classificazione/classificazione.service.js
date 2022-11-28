'use strict';

angular.module('cifra2gestattiApp')
    .factory('Classificazione', function ($resource) {
        return $resource('api/classificaziones/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            
            
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.validoDal != null){
                        var validoDalFrom = data.validoDal.split("-");
                        data.validoDal = new Date(new Date(validoDalFrom[0], validoDalFrom[1] - 1, validoDalFrom[2]));
                    }
                    if (data.validoAl != null){
                        var validoAlFrom = data.validoAl.split("-");
                        data.validoAl = new Date(new Date(validoAlFrom[0], validoAlFrom[1] - 1, validoAlFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
