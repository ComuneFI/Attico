'use strict';

angular.module('cifra2gestattiApp')
.factory('TaskDesktop', function ($resource) {
	return $resource('api/taskBpms/:id/:action', {}, {
		'vistoMassivo': { method: 'post', isArray: false, params:{action:'vistoMassivo', idTasks:'@idTasks', profiloId:'@profiloId'} },
		'firmaMassiva': { method: 'post', isArray: false, params:{action:'firmaMassiva', documentiDaGenerareFirmare:'@documentiDaGenerareFirmare', idTasks:'@idTasks', profiloId:'@profiloId', codiceFiscale:'@codiceFiscale', password:'@password', otp:'@otp' } },
		'prendiInCaricoTask': { method: 'post', isArray: false, params:{action:'prendiInCaricoTask' } },
		'riassegnazioneTaskMyOwn':{ method: 'post', isArray: false, params:{action:'riassegnazioneTaskMyOwn' } },
		'ritiraAttoG': { method: 'post', isArray: false, params:{action:'ritiraAttoG'}},
		'ritiraAttoC': { method: 'post', isArray: false, params:{action:'ritiraAttoC'}},
		'listaLavorazioni': { method: 'GET', isArray: true, params:{action:'listaLavorazioni' } },
		'getMyNextTask': { method: 'GET', isArray: false},
		'getTask': { method: 'GET', isArray: false, params:{action:'getTask' }},
		'unclaim': { method: 'PUT', isArray: false, params:{action:'unclaim', taskId:'@taskId', verifyOriginalProfId:'@verifyOriginalProfId' }},
		'existsActiveTask':{ method: 'GET', isArray: false, params:{action:'existsActiveTask', taskId:'@taskId'}},
		'reassignee': { method: 'PUT', isArray: false, params:{
														action:'reassignee', 
														taskId:'@taskId', 
														profiloIdDirigente:'@profiloIdDirigente', 
														attoId:'@attoId',
														profiloIdAssegna: '@profiloIdAssegna',
														idQualificaAssegna: '@idQualificaAssegna',
														tasktype:'@tasktype'}},
		'cambiaCoda': { method: 'PUT', isArray: false, params:{action:'cambiaCoda',taskId:'@taskId',profiloIdDirigente:'@profiloIdDirigente',attoId:'@attoId',aooIdAssegna: '@aooIdAssegna'}},
        'get': {
            method: 'GET',
            transformResponse: function (data) {
                data = angular.fromJson(data);
                return data;
            }
        },
		'update': { method:'PUT' },
		'search':{method:'POST', isArray:true, params:{action:'search'}},
		'findAllInCarico':{method:'POST', isArray:true, params:{action:'findAllInCarico'}},
		'searchByDelegante':{method:'POST', isArray:true, params:{action:'searchByDelegante', profiloId:'@profiloId'}},
		'searchOdg':{method:'POST', isArray:true, params:{action:'searchOdg'}},
		'searchPostSeduta':{method:'POST', isArray:true, params:{action:'searchPostSeduta'}},
		'searchAttesaEsito':{method:'POST', isArray:true, params:{action:'searchAttesaEsito'}},
		'searchAttiCommissioni':{method:'POST', isArray:true, params:{action:'searchAttiCommissioni'}},
		'searchGroups':{method:'POST', isArray:true, params:{action:'searchGroups'}}
	});
});
angular.module('cifra2gestattiApp') 
.directive('showtab',  function () {
	return {
		link: function (scope, element, attrs) {
			
			element.bind('click', function(e) {
				e.preventDefault();
				if($(element).parent().hasClass('disabled')){
					return;
				}
				scope.clearRicerca();
				$(element).tab('show');
				scope.tasktype = $(element).attr('name');
				scope.page = 1;
				if(scope.tasktype == 'odg-giunta'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').show();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.searchAtto();
				}else if(scope.tasktype == 'odg-consiglio'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').show();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.presetParCom();
					scope.searchAtto();
				}else if(scope.tasktype == 'sedute-giunta'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').show();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasktype_"]').hide();
					scope.searchSeduta();
				}else if(scope.tasktype == 'sedute-consiglio'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').show();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasktype_"]').hide();
					scope.searchSeduta();
				}else if(scope.tasktype == 'propInSeduta-giunta'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').show();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.searchAttiOdgs();
				}else if(scope.tasktype == 'propInSeduta-consiglio'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').show();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.searchAttiOdgs();
				}
				else if(scope.tasktype == 'attiInCoordinamentoTesto-giunta'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').show();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.loadAll();
				}else if(scope.tasktype == 'attiInCoordinamentoTesto-consiglio'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').show();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					scope.loadAll();
				}else if(scope.tasktype == 'attiInCaricoAssessori'){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').show();
					$('[id^="tasklist_"]').hide();
					scope.loadAll();
				}else if(scope.tasktype.startsWith('carico_')){
					$('#attiRiassegnazione').hide();
					$('#groups').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					$('#'+scope.tasktype).show();
					scope.$parent.tasktype = $(element).attr('name');
					var tasklist = scope.tasktype.split("_");
					scope.loadAll();
				}else if(scope.tasktype.startsWith('attiInRagioneria')){
					$('#attiRiassegnazione').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					$('#groups').show();
					scope.groupSearch = {};
					scope.loadRagioneria();
				}else if(scope.tasktype == 'monitoraggioGroup'){
					$('#attiRiassegnazione').hide();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					$('#groups').show();
					scope.groupSearch = {};
					scope.loadRagioneria();
				}else if(scope.tasktype == 'attiRiassegnazione'){
					$('#attiRiassegnazione').show();
					$('#tasklist').hide();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					$('#groups').hide();
					scope.loadAll();
				}else{
					$('#attiRiassegnazione').hide();
					$('#tasklist').show();
					$('#odg-giunta').hide();
					$('#odg-consiglio').hide();
					$('#sedute-giunta').hide();
					$('#sedute-consiglio').hide();
					$('#propInSeduta-giunta').hide();
					$('#propInSeduta-consiglio').hide();
					$('#attiInCoordinamentoTesto-giunta').hide();
					$('#attiInCoordinamentoTesto-consiglio').hide();
					$('#attiInCaricoAssessori').hide();
					$('[id^="tasklist_"]').hide();
					$('#groups').hide();
					scope.loadAll();
				}
			});
		}
	};
});

