'use strict';

angular.module('cifra2gestattiApp')
    .factory('StoricoAtto', function ($resource) {
        return $resource('api/storicoattos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'attoDirigenziale': { method: 'get', isArray: false, params:{action:'attoDirigenziale' } },
            'attoGiunta': { method: 'get', isArray: false, params:{action:'attoGiunta' } },
            'getAllAoo': { method: 'GET', isArray: true, params:{action:'getAllAoo' } },
            'getAllSedute': { method: 'GET', isArray: true, params:{action:'getAllSedute' } },
            'searchAttiDir_groupByAoo': { method: 'post', isArray: true, params:{action:'searchAttiDir_groupByAoo' } },
            'searchAttiGiunta_groupByAoo': { method: 'post', isArray: true, params:{action:'searchAttiGiunta_groupByAoo' } },
            'searchAttiGiunta_groupBySeduta': { method: 'post', isArray: true, params:{action:'searchAttiGiunta_groupBySeduta' } },
            'searchAttiDirigenziali': { method: 'post', isArray: true, params:{action:'searchAttiDirigenziali' } },
            'searchAttiGiunta': { method: 'post', isArray: true, params:{action:'searchAttiGiunta' } },
            'getLavorazioniAttoDirigenziale': { method: 'GET', isArray: true, params:{action:'getLavorazioniAttoDirigenziale' } },
            'getLavorazioniAttoGiunta': { method: 'GET', isArray: true, params:{action:'getLavorazioniAttoGiunta' } },
            'tipiIter': { method: 'GET', isArray: true, params:{action:'tipiIter' } },
            'tipiFinanziamento': { method: 'GET', isArray: true, params:{action:'tipiFinanziamento' } },
            'tipiAdem': { method: 'GET', isArray: true, params:{action:'tipiAdem' } },
            'tipiRiunioneGiunta': { method: 'GET', isArray: true, params:{action:'tipiRiunioneGiunta' } },
            'min_maxAnnoAttiStorici': { method: 'GET', isArray: false, params:{action:'min_maxAnnoAttiStorici' } }
        });
    });