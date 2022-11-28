'use strict';

angular.module('cifra2gestattiApp')
    .factory('ComposizioneGiunta', function ($resource) {
        return $resource('api/composizioneGiuntas/:id/:action/:aooid/:utenteid/:idRias/:profiloId/:qualificaId', {}, {
            'getComponentiGiuntaConsiglio': { method: 'GET', isArray: true, params:{ action: 'getComponentiGiuntaConsiglio', ruolo:'@ruolo'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });