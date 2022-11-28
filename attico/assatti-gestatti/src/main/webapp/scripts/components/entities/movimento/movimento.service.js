'use strict';

angular.module('cifra2gestattiApp')
    .factory('Movimento', function ($resource) {
        return $resource('api/movimentos/:id', {}, {
            'elencoMovimenti': { method: 'GET', isArray: true, params:{id:'@id'}}
        });
    });
