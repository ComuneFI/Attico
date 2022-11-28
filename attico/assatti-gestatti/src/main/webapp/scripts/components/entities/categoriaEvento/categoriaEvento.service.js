'use strict';

angular.module('cifra2gestattiApp')
    .factory('CategoriaEvento', function ($resource) {
        return $resource('api/categoriaEvento', {}, {
            'query': { method: 'GET', isArray: true}        
        });
    });
