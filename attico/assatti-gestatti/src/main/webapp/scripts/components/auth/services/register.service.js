'use strict';

angular.module('cifra2gestattiApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


