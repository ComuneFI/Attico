'use strict';

angular.module('cifra2gestattiApp')
    .factory('SistemaAccreditato', function ($resource) {
        return $resource('api/sistemaAccreditatos/:action', {}, {
            'search': { method: 'POST', isArray: true, params:{action:'search'}}
        });
    });
