'use strict';

angular.module('cifra2gestattiApp')
    .factory('Profilo', function ($resource) {
        return $resource('api/profilos/:id/:action/:aooid/:utenteid/:idRias/:profiloId/:qualificaId', {}, {
			'futureEnable':{ method: 'PUT', isArray: false, params:{action:'futureEnable', id:'@id'}},
			'futureDisable':{ method: 'PUT', isArray: false, params:{action:'futureDisable', id:'@id'}},
        	'getAllActive':{ method: 'GET', isArray: true, params:{action:'getAllActive'}},
        	'getAllActiveMinimal':{ method: 'GET', isArray: true, params:{action:'getAllActiveMinimal'}},
        	'getAllActiveBasic':{ method: 'GET', isArray: true, params:{action:'getAllActiveBasic'}},
			'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id', idRias:'@idRias', qualificaId: '@qualificaId'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
        	'getSimple' : { method: 'GET', isArray: false, params:{action:'getSimple', id:'@id'}},
			'getToUpt' : { method: 'GET', isArray: false, params:{action:'getToUpt', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            'search': { method: 'POST', isArray: true, params:{action:'search'}},
            'searchquery': { method: 'GET', isArray: true, params:{action:'searchquery'}},
            'getEmananti': { method: 'GET', isArray: true, params:{ action: 'getEmananti',aooId:'@aooId', tipoAttoCodice:'@tipoAttoCodice'}},
            'getByAooId': { method: 'GET', isArray: true, params:{ action: 'getByAooId'}},
            'getByAooIdRicorsive': { method: 'GET', isArray: true, params:{ action: 'getByAooIdRicorsive'}},
            'getProfilosRiassegnazione': { method: 'GET', isArray: true, params:{ action: 'getProfilosRiassegnazione'}},
            'getProfilosRiassegnazioneTask': { method: 'GET', isArray: true, params:{ action: 'getProfilosRiassegnazioneTask'}},
            'getByAooIdAndTipoAtto': { method: 'GET', isArray: true, params:{ action: 'getByAooIdAndTipoAtto',aooId:'@aooId', codiceTipoAtto:'@codiceTipoAtto'}},
            'getByAooIdAndTipoAttoForGiunta': { method: 'GET', isArray: true, params:{ action: 'getByAooIdAndTipoAttoForGiunta',aooId:'@aooId', codiceTipoAtto:'@codiceTipoAtto'}},
            'getByAooIdAndTipoAttoForPareri': { method: 'GET', isArray: true, params:{ action: 'getByAooIdAndTipoAttoForPareri',aooId:'@aooId', codiceTipoAtto:'@codiceTipoAtto', tipoParere:'@tipoParere'}},
            'getForControdeduzioni': { method: 'GET', isArray: true, params:{ action: 'getForControdeduzioni',aooId:'@aoosId'}},
            'getProfiliOfUser': { method: 'GET', isArray: true, params:{ action: 'getProfiliOfUser', utenteid:'@utenteid'}},
            'getByRole': { method: 'GET', isArray: true, params:{ action: 'getByRole', ruolo:'@ruolo'}},
			'getByCandidateGroup': { method: 'GET', isArray: true, params:{ action: 'getByCandidateGroup', candidateGroup:'@candidateGroup'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'checkIfIsDirigente' : { method: 'POST', isArray: false, params:{action:'checkdirigente'}},
            'checkImpersonifica' : { method: 'POST', isArray: false, params:{action:'checkImpersonifica'}},
			'existsAsDelegato':{method:'GET', isArray:false, params:{action:'existsAsDelegato', id:'@id'}},
            'checkIfAlreadyexist':{method:'GET', isArray:false, params:{action:'alreadyexist', aooid:'@aooid', utenteid:'@utenteid'}},
            'checkIfCanDisableWithoutReassegnee':{method:'GET', isArray:false, params:{action:'candisablewithoutreassegnee', profiloid:'@id'}},
            'disabilitazioneDiretta':{method:'PUT', isArray:false, params:{action:'disabilitazioneDiretta', id:'@id'}},
            'findByRuoloAoo':{method:'GET', isArray:true, params:{action:'findByRuoloAoo', listIdRuolo:'@listIdRuolo', listIdRuolo:'@listIdRuolo'}},
			'findTaskIstruttore':{method:'GET', isArray:true, params:{action:'findTaskIstruttore'}},
			'findTaskDaRilasciare':{method:'GET', isArray:true, params:{action:'findTaskDaRilasciare'}},
			'findTaskRiassignee':{method:'GET', isArray:true, params:{action:'findTaskRiassignee'}},
			'futureReassignee' : { method: 'PUT', isArray: false, params:{action:'futureReassignee'}},
			'getProfilosRiassegnazioneTaskConfTaskBased': { method: 'GET', isArray: true, params:{ action: 'getProfilosRiassegnazioneTaskConfTaskBased'}}
        });
    });

angular.module('cifra2gestattiApp').service('ProfiloService', ['Profilo', 'ProfiloAccount', 'TipoMateria', '$state', '$rootScope', 'Utente',
                                                               function(Profilo, ProfiloAccount, TipoMateria, $state, $rootScope, Utente) {
	
	/**
	 * Seleziona il profilo passato come parametro.
	 * @param profilo Il nuovo profilo selezionato.
	 */
	var selezionaProfilo = function(profilo) {
		var utenteId = $rootScope.account.utente.id;
		if(angular.isDefined(profilo)){
			if(profilo.id == '0'){
				Profilo.getProfiliOfUser({utenteid:utenteId}, function(result){
					ProfiloAccount.setProfilo({	id:0,
												descrizione:'Tutti i profili dell\'utente',
												utente:utenteId,
												aoo:{id:0}});
					$state.go("selezionaprofilo", {}, {reload: true});
				});
			}else{
				Profilo.get({id:profilo.id}, function(result){
					ProfiloAccount.setProfilo(result);
					Utente.updateLastProfile({id:profilo.utente.id, profiloId:result.id});
					TipoMateria.active(function(data){
						ProfiloAccount.setTipoMaterie(data);
					});
					$state.go("selezionaprofilo", {}, {reload: true});
				});
			}
		}
		else{
			Profilo.getProfiliOfUser({utenteid:utenteId}, function(result){
				ProfiloAccount.setProfilo({	id:0,
											descrizione:'Tutti i profili dell\'utente',
											utente:utenteId,
											aoo:{id:0}});
				$state.go("selezionaprofilo", {}, {reload: true});
				Utente.updateLastProfile({id:utenteId, profiloId:0});
			});
		}
	};
	
	
	return {
		selezionaProfilo: selezionaProfilo
	};
}]);