'use strict';

angular.module('cifra2gestattiApp')
    .factory('Sezione', function ($resource) {
        return $resource('api/sezioni/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'riferimentoconsentiti': { method: 'GET', isArray: true, params:{'action':'riferimentoconsentiti'} }
        });
    });