'use strict';

angular.module('cifra2gestattiApp')
    .factory('Msg', function ($resource) {
        return $resource('api/msgs/:id/:action', {}, {
        	'forceExpire': { method: 'GET', isArray: false, params: {action :'forceExpire'}},
            'search': { method: 'POST', isArray: true, params: {action :'search'}},
            'searchUtente': { method: 'POST', isArray: true, params: {action :'searchUtente'}},
            'prioritas': { method: 'GET', isArray: true, params: {action :'prioritas'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
