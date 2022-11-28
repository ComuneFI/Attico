'use strict';

angular.module('cifra2gestattiApp')
    .factory('SedutaGiunta', function ($resource) {
        return $resource('api/sedutaGiuntas/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'riferimentoconsentiti': { method: 'GET', isArray: true, params:{'action':'riferimentoconsentiti'} },
            'search': { method: 'POST', isArray: true, params:{action:'search' } },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.dataOra != null) data.dataOra = new Date(data.dataOra);
                    return data;
                }
            },
            'statidocumenti': { method: 'GET', isArray: true, params: {action:'statidocumenti'}},
            'update': { method:'PUT' },
            'annulla': { method:'POST', isArray: false, params: {action:'annulla'} },
            'variazione': { method:'POST', isArray: false, params: {action:'variazione'} },
            'chiusuraResoconto': { method: 'PUT', isArray: false, params:{action:'chiusuraResoconto' } },
            'generaDocResoconto': { method: 'GET', isArray: false, params:{action:'generadocresoconto' } },
            'deletesottoscrittore': { method:'POST', isArray: false, params: {action:'deletesottoscrittore'} },
            'sottoscrittoriresoconto':{ method:'PUT' , isArray: false, params: {action:'sottoscrittoriresoconto'} },
            'getSottoscrittoriVerbalePossibili': { method: 'GET', isArray: true, params:{action:'sottoscrittoriverbalepossibili' } },
            'getNextNumeroArgomento': { method: 'GET', isArray: false, params:{action:'getNextNumeroArgomento' } },
            'salvaNumeriArgomento': { method: 'PUT', isArray: false, params:{action:'salvaNumeriArgomento' } },
            'setStessoNumeroArgomento': { method: 'PUT', isArray: false, params:{action:'setStessoNumeroArgomento' } },
            'setArgomentiProgressivi': { method: 'PUT', isArray: false, params:{action:'setArgomentiProgressivi' } },
            'resetNumeriArgomento': { method: 'PUT', isArray: false, params:{action:'resetNumeriArgomento' } },
            'getPrimaConvocazioneFine': { method: 'GET', isArray: false, params:{action:'getPrimaConvocazioneFine' } },
			'aggiornaStatoSeduta':{ method: 'PUT', isArray: false, params:{action:'aggiornaStatoSeduta', id:'@id' } },
            'editNumeroSeduta':{ method: 'PUT', isArray: false, params:{action:'editNumeroSeduta'} },
        });
    });
angular.module('cifra2gestattiApp') 
.directive('showtabverbale',  function ($rootScope) {
	return {
		scope: {
            updateFn: '&'
        },
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				
				scope.$apply(function(){				
					scope.showSezioneVerbale = $(element).attr('name');
					scope.updateFn({value: $(element).attr('name')});
				});
			});
		}
	};
});

angular.module('cifra2gestattiApp') 
.directive('showtabresoconto',  function ($rootScope) {
	return {
		scope: {
            updateFn: '&'
        },
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				
				scope.$apply(function(){				
					scope.showSezioneResoconto = $(element).attr('name');
					scope.updateFn({value: $(element).attr('name')});
				});
			});
		}
	};
});