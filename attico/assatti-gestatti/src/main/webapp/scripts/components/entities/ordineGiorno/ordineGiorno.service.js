'use strict';

angular.module('cifra2gestattiApp')
.factory('OrdineGiorno', function ($resource) {
	return $resource('api/ordineGiornos/:id/:action/:attoodg', {}, {
		'query': { method: 'GET', isArray: true},
		'get': {
			method: 'GET',
			transformResponse: function (data) {
				data = angular.fromJson(data);
				if (data.dataPubblicazioneSito != null){
					var dataPubblicazioneSitoFrom = data.dataPubblicazioneSito.split("-");
					data.dataPubblicazioneSito = new Date(new Date(dataPubblicazioneSitoFrom[0], dataPubblicazioneSitoFrom[1] - 1, dataPubblicazioneSitoFrom[2]));
				}
				return data;
			}
		},
		// In attico non consentito -> da resoconto massivo
		// 'salvaesito': { method:'POST', isArray: false, params: {action:'salvaesito'} },
		'salvaesiti': { method:'POST', isArray: false, params: {action:'salvaesiti'} },
		'confermaesito': { method:'POST', isArray: false, params: {action:'confermaesito',attoOdgId:'@attoOdgId', profiloId:'@profiloId'} },
		'nontrattati': { method:'POST', isArray: false, params: {action:'nontrattati',attoOdgId:'@attoOdgId', profiloId:'@profiloId'} },
		'reimpostatrattati': { method:'POST', isArray: false, params: {action:'reimpostatrattati',attoOdgId:'@attoOdgId', profiloId:'@profiloId'} },
		'modificacomponenti': { method:'POST', isArray: false, params: {action:'modificacomponenti'} },
		'annullaesito': { method:'POST', isArray: false, params: {action:'annullaesito',attoOdgId:'@attoOdgId', profiloId:'@profiloId'} },
		 // Per ATTICO non consentita la cancellazione dell'esito
		 // 'cancellaesito': { method:'POST', isArray: false, params: {action:'cancellaesito'} },
		 //'cancellaesiti': { method:'POST', isArray: false, params: {action:'cancellaesiti'} },
		'esito': { method: 'GET', isArray: false, params:{action:'esito', attoodg:'@attoodg' } },
		'generaDocOdg': { method: 'GET', isArray: false, params:{action:'generadocodg' } },
		// CASISTICHE ELIMINATE
		// 'generaDocResoconto': { method: 'GET', isArray: false, params:{action:'generadocresoconto' } },
		// 'pubblicaDocResoconto': { method: 'GET', isArray: false, params:{action:'pubblicadocresoconto' } },
		// 'saveArgumentsOdg': { method: 'POST', isArray: false, params:{action:'saveargumentsodg' } },
		'suspendAtti': { method: 'POST', isArray: false, params:{action:'suspendatti' } },
		'activateAtti': { method: 'POST', isArray: false, params:{action:'activateatti' } },
		// CASISTICA ELIMINATA
		// 'numeraAtti': { method: 'POST', isArray: false, params:{action:'numeraatti' } },
		// Per ATTICO la generazione del documento avviene in una fase successiva
		// 'generaDocProvv':{ method: 'POST', isArray: false, params:{action:'generadocprovv' } },
		// Per ATTICO non consentito annullamento numerazione
		// 'annullaNumerazione': { method: 'POST', isArray: false, params:{action:'annullanumerazione' } },
		'update': { method:'PUT' },
		// CASISTICHE ELIMINATE
		// 'firmaDocumento': { method: 'POST', isArray: false, params:{action:'firmadocumento' } },
		// 'firmaDocumentoResoconto': { method: 'POST', isArray: false, params:{action:'firmadocumentoresoconto' } },
		'searchAttiOdgs': { method: 'POST', isArray: true, params:{action:'searchAttiOdgs' } }
	});
});

angular.module('cifra2gestattiApp').service('ArgumentsOdgService', function() {
	var argumentsList = [];

	 var addArgument= function(newObj) {
		 argumentsList.push(newObj);
	  };

	var setArguments = function(list) {
		argumentsList = list;
	};

	var getArguments = function(){
		return argumentsList;
	};

	return {
		addArgument: addArgument,
		setArguments: setArguments,
		getArguments: getArguments
	};

});
