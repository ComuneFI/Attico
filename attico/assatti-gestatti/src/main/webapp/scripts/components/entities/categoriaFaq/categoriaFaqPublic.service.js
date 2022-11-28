'use strict';

angular.module('cifra2gestattiApp')
    .factory('CategoriaFaqPublic', function ($resource) {
        return $resource('api/public/categoriaFaqs', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
