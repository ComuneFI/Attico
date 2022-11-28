'use strict';

angular.module('cifra2gestattiApp')
    .factory('RichiestaHD', function ($resource) {
        return $resource('api/richiestaHDs/:id/:action', {}, {
        	'editTestoRichiesta': { method: 'POST', isArray: false, params: {action :'editTestoRichiesta'}},
        	'editTestoRisposta': { method: 'POST', isArray: false, params: {action :'editTestoRisposta'}},
        	'searchUtente': { method: 'POST', isArray: true, params: {action :'searchUtente'}},
        	'searchDirigente': { method: 'POST', isArray: true, params: {action :'searchDirigente'}},
        	'searchOperatore': { method: 'POST', isArray: true, params: {action :'searchOperatore'}},
        	'searchRichiesteDirigente': { method: 'POST', isArray: true, params: {action :'searchRichiesteDirigente'}},
            'rispostaOperatore': { method: 'POST', isArray: false, params: {action :'rispostaOperatore'}},
            'rispostaUtente': { method: 'POST', isArray: false, params: {action :'rispostaUtente'}},
            'updateStato': { method: 'GET', isArray: false, params: {action :'updateStato'}},
            'presaVisione': { method: 'GET', isArray: false, params: {action :'presaVisione'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
