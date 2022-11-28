'use strict';

angular.module('cifra2gestattiApp')
    .factory('InstantMessage', function ($resource) {
        return $resource('api/instantMessage', {}, {
        	'read': { method: 'GET', isArray: true}
        });
    });
