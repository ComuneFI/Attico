'use strict';

angular.module('cifra2gestattiApp')
    .factory('ConfigurazioneTask', function ($resource) {
    	return $resource('api/configurazionetask/:action/:id/:codice', {}, {
        	'get': { method: 'GET', isArray: false, params: {action :'get'} },
        	'findByCodice': { method: 'GET', isArray: false, params: {action :'findByCodice'} },
        	'findAll': { method: 'GET', isArray: true, params: {action :'findAll'} },
        	'save': { method:'POST', isArray: false, params: {action :'save'} }
        });
    });