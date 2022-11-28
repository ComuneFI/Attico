'use strict';

angular.module('cifra2gestattiApp')
    .factory('Atto', function ($resource) {
        return $resource('api/attos/:id/:action/:idAllegato/:actionAllegato/:idOmissis/:taskBpmId', {}, {
            'searchRevocato':{method:'POST', isArray:true, params:{action:'searchRevocato' }},
        	'query': { method: 'GET', isArray: true},
            // IN ATTICO esclusa la gestione dei sottoscrittori
        	// 'sottoscrittoreAtto': { method: 'post', isArray: false, params:{action:'sottoscrittoreatto' } },
            // IN ATTICO L'INTEGRAZIONE CON MOTORE WORKFLOW GESTITA DIVERSAMENTE
            // 'preworkflow': { method: 'get', isArray: false, params:{action:'preworkflow' } },
            'search': { method: 'post', isArray: true, params:{action:'search' } },
            'searchGrouped': { method: 'post', isArray: false, params:{action:'searchGrouped' } },
            'searchlibera': { method: 'post', isArray: true, params:{action:'searchlibera' } },
            'copia': { method: 'post', isArray: false, params:{action:'copia' } },
            'schedatrasparenza': { method: 'post', isArray: false, params:{action:'schedatrasparenza' } },
            'caricaschedatrasparenza': { method: 'get', isArray: false, params:{action:'schedatrasparenza' } },
            'caricastati': { method: 'get', isArray: true, params:{action:'stati' } },           
            'allegati': { method: 'get', isArray: true, params:{action:'allegati' } },           
            'firma': { method: 'post', isArray: false, params:{action:'firma' } },
            'sendOTP': { method: 'post', isArray: false, params:{action:'sendOTP' } }, 
            'generadocumentifirmati': { method: 'post', isArray: false, params:{action:'generadocumentifirmati' } },           
            'eventi': { method: 'get', isArray: true, params:{action:'eventi' } },  
            'ultimostato': { method: 'get', isArray: true, params:{action:'ultimostato' } },  
            'revocato': { method: 'get', isArray: false, params:{action:'revocato' } },
            'relataGestibile':{method:'get', isArray:false, params:{action:'relataGestibile', id:'@id'}},
            'copiaNonConforme':{method:'get', isArray:false, params:{action:'copiaNonConforme', id:'@id',idModelloHtml:'@idModelloHtml'}},
            'elencotipodocumento': { method: 'GET', isArray: true, params:{action:'elencotipodocumento', 'taskBpmId':'@taskBpmId'}},
            'pcf': { method: 'GET', isArray: true, params:{action:'pcf', 'taskBpmId':'@taskBpmId'}},
            'elencodocumentidafirmare': { method: 'GET', isArray: true, params:{action:'elencodocumentidafirmare', 'taskBpmId':'@taskBpmId'}},
			'getCodiciTipiDocumentoDaFirmare': { method: 'GET', isArray: true, params:{action:'getCodiciTipiDocumentoDaFirmare', 'taskBpmId':'@taskBpmId'}},
			'getAttoConTaskAttivi':{method: 'GET', isArray: false, params:{action:'getAttoConTaskAttivi', 'id':'@id'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'POST', params:{action:'update' }},
            'deleteAllegatoOmissis': { method: 'DELETE', isArray: false}
        });
    });
angular.module('cifra2gestattiApp') 
.directive('showtabatti',  function ($rootScope,ProfiloAccount) {
	return {
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				
				scope.attos = [];
				scope.page = 1;
				
				var annoSelected = scope.criteria.anno;
				
//				TODO: in ATTICO i codici per i tipi di atto non coincidono
//				if( (ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && scope.tipoAtto == 'DIR') ||
//						(ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_GIUNTA']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_GIUNTA']) && (scope.tipoAtto  == 'DEL' || scope.tipoAtto  == 'SDL' || scope.tipoAtto  == 'COM' || scope.tipoAtto  == 'sedute')) ){
//					scope.criteria = {aooId:null, tipiAttoIds:scope.tipiAttoIds,anno:annoSelected};
//				}else{
					scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:scope.tipiAttoIds,anno:annoSelected};
//				}
				scope.criteria.page = scope.page;
				scope.criteria.per_page = 10;
				scope.criteria.viewtype = $(element).attr('name');
				scope.criteria.ordinamento = "codiceCifra";
				scope.criteria.tipoOrinamento = "desc";

				scope.loadAll();
			});
		}
	};
});

angular.module('cifra2gestattiApp') 
.directive('tabgroupedattosearch',  function ($rootScope,ProfiloAccount) {
	return {
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				scope.init($(element).attr('name'));
			});
		}
	};
});

angular.module('cifra2gestattiApp') 
.directive('showtabcontabile',  function ($rootScope) {
	return {
		scope: {
            updateFn: '&'
        },
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				
				scope.$apply(function(){				
					scope.showSezioneContabile = $(element).attr('name');
					scope.updateFn({value: $(element).attr('name')});
				});
			});
		}
	};
});