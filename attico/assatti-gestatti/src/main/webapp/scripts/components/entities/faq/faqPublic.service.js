'use strict';

angular.module('cifra2gestattiApp')
    .factory('FaqPublic', function ($resource) {
        return $resource('api/public/faqs', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
