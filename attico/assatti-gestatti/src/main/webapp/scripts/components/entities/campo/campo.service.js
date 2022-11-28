'use strict';

angular.module('cifra2gestattiApp')
    .factory('Campo', function ($resource) {
        return $resource('api/campi/:id/:action', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });