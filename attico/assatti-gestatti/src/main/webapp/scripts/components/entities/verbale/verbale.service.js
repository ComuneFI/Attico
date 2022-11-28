'use strict';

angular.module('cifra2gestattiApp')
    .factory('Verbale', function ($resource) {
        return $resource('api/verbales/:id/:action/:idAllegato', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'allegato': { method: 'GET', isArray: false, params:{action:'allegato' } },
            'generaDocPerFirma': { method: 'GET', isArray: false, params:{action:'generadocperfirma' } },
            'firmaDocumento': { method: 'POST', isArray: false, params:{action:'firmadocumento' } },
            'getNextSottoscrittore': { method: 'GET', isArray: false, params:{action:'nextsottoscrittore' } }
        });
    });
