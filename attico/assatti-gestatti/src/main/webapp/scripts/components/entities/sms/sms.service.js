'use strict';

angular.module('cifra2gestattiApp')
    .factory('Sms', function ($resource) {
        return $resource('api/sms/:id/:action/', {}, {
        	'search': { method: 'POST', isArray: true, params:{action:'search'}},
        	'detail':  { method: 'POST', isArray: true, params:{action:'detail'}},
        	'retry': { method:'POST', params:{action:'retry' }}
        });
    });
