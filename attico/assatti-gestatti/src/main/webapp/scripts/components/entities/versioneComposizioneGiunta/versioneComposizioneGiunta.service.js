'use strict';

angular.module('cifra2gestattiApp')
    .factory('VersioneComposizioneGiunta', function ($resource) {
        return $resource('api/versioneComposizioneGiuntas/:id/:action/:codiceParam', {}, {
            'query': { method: 'GET', isArray: true},
            'caricaComposizioni': { method: 'get', isArray: true, params:{action:'caricaComposizioni' } },    
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
        });
    });