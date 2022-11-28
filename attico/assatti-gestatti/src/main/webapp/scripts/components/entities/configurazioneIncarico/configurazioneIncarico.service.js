'use strict';

angular.module('cifra2gestattiApp')
    .factory('ConfigurazioneIncarico', function ($resource) {
    	return $resource('api/configurazioneincarico/:action/:id/', {}, {
        	'saveCommissioneConsiliare': { method:'POST', isArray: false, params: {action :'saveCommissioneConsiliare'} },
    		'insertParere': { method:'POST', isArray: false, params: {action :'insertParere'} },
        	'aggiornaScadenza': { method:'POST', isArray: false, params: {action :'aggiornaScadenza'} },
    		'saveParere': { method:'POST', isArray: false, params: {action :'saveParere'} }
        });
    });