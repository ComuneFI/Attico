'use strict';

angular.module('cifra2gestattiApp')
    .factory('MonitoraggioAccesso', function ($resource) {
        return $resource('api/monitoraggioAccessos/:action', {}, {
        	'search': { method: 'POST', isArray: true, params: {action :'search'}}
        });
    });
