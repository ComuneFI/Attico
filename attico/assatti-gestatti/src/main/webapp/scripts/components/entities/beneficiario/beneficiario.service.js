'use strict';

angular.module('cifra2gestattiApp')
    .factory('Beneficiario', function ($resource) {
        return $resource('api/beneficiarios/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'},
            'htmlFromBeneficiari': {method:'POST', params:{action:'htmlFromBeneficiari'}}
        });
    });
