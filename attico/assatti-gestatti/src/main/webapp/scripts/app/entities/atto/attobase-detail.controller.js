'use strict';

angular.module('cifra2gestattiApp')
.controller('AttoBaseDetailController', // base controller containing common functions for add/edit controllers
    ['$scope', 'Atto','RubricaDestinatarioEsterno','Aoo','Assessorato','Utente','$rootScope','$log','Upload','DocumentoInformatico','Diogene','Lavorazione','TaskDesktop',
     '$stateParams','$timeout','$state','$q','ConvertService','ngToast', '$sce','Beneficiario', '$filter', 'ProfiloService','SedutaGiuntaConstants','AllegatoConstants',
    function ($scope, Atto,RubricaDestinatarioEsterno,Aoo,Assessorato,Utente,$rootScope,$log,Upload,DocumentoInformatico,Diogene,Lavorazione,TaskDesktop, $stateParams,$timeout,
    		$state,$q,ConvertService,ngToast, $sce, Beneficiario, $filter, ProfiloService, SedutaGiuntaConstants, AllegatoConstants) {
    	
    	$scope.allegatoGoodMatrix = {
			GENERICO:
				{
				campi:['riservato'],
				ammissibili:
					[{
						riservato:true
					},
					{
						riservato:false
					}]
				}
			,
			PARTE_INTEGRANTE:
				{
				campi:['pubblicabile', 'omissis', 'riservato'],
				ammissibili:
					[{
						pubblicabile:true,
						omissis:true,
						riservato:true
					},
					{
						pubblicabile:true,
						omissis:true,
						riservato:false
					},
					{
						pubblicabile:true,
						omissis:false,
						riservato:false
					},
					{
						pubblicabile:false,
						omissis:false,
						riservato:true
					},
					{
						pubblicabile:false,
						omissis:false,
						riservato:false
					}]
				}
		};
		
		$scope.isGoodAllegatoMatrix = function(allegato){
			let isGood = false;
			if(allegato && allegato.tipoAllegato && allegato.tipoAllegato.codice && $scope.allegatoGoodMatrix[allegato.tipoAllegato.codice]){
				let matrix = $scope.allegatoGoodMatrix[allegato.tipoAllegato.codice];
				if(
					!(
					(matrix.campi.indexOf('pubblicabile') > -1 && (allegato.pubblicabile === undefined || allegato.pubblicabile === null)) ||
					(matrix.campi.indexOf('omissis') > -1 && (allegato.omissis === undefined || allegato.omissis === null)) ||
					(matrix.campi.indexOf('riservato') > -1 && (allegato.riservato === undefined || allegato.riservato === null))
					)
				){
					let goodChances = matrix.ammissibili;
					if(goodChances.length){
						for(let i = 0; i < goodChances.length; i++){
							if(goodChances[i] && 
								(matrix.campi.indexOf('pubblicabile') < 0 || goodChances[i].pubblicabile === allegato.pubblicabile) && 
								(matrix.campi.indexOf('omissis') < 0 || goodChances[i].omissis === allegato.omissis) &&
								(matrix.campi.indexOf('riservato') < 0 || goodChances[i].riservato === allegato.riservato)
							){
								isGood = true;
								break;
							}
						}
					}
				}else{
					//manca la compilazione di alcuni campi
					isGood = true;
				}
			}else{
				isGood = true;
			}
			return isGood;
		};
		
		$scope.selectAllegatoMatrixChanged = function(allegato, whatChange){
			if(whatChange == 'pubblicabile'){
				$scope.changeAllegatoPubblicabile(allegato);
			}else if(whatChange == 'tipoAllegato'){
				$scope.changeTipoAllegato(allegato);
			}else if(whatChange == 'omissis'){
				$scope.checkOmissisFlag(allegato);
			}
		};

		$scope.AllegatoConstants = AllegatoConstants;
    	
    	$scope.allegatoBeneficiariChoices = [
    	                                     {valore:true,label:'SI'},
    	                            	     {valore:false,label:'NO'}
    	                               ];
    	
    	$scope.isGoodNumber = function(valore){
    		return (new RegExp(/^[0-9]+(\,[0-9]{1,2})?$/)).test(valore);
    	};
    	
    	$scope.fileDroppati = []
    	$scope.salvaFileDroppati = function(files){
    		if(files!=null)
    		{
    			for(var i = 0; i<files.length; i++){
						$scope.fileDroppati.push(files[i]);
				}
    		}
    	};
    	
    	$scope.checkOmissisFlag = function(allegato){
    		var isOk = true;
    		if(!allegato.omissis && allegato.fileomissis){
    			allegato.omissis = true;
    			isOk = false;
    			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Per selezionare il valore \'NO\' occorre che non sia stato inserito un allegato omissis. Rimuoverlo e riprovare.'});
    		}
    		return isOk;
    	};
    	
    	var $translate = $filter('translate');
    	$scope.taskLoading = false;
    	$scope.rubricaBeneficiarioToImport = {};
    	$scope.infoAnagContDisabled = false;
    	
    	$scope.pubblicabileMassivo = {codice: false};
        $scope.tipoAllegatoMassivo = {codice: AllegatoConstants.tipoAllegato.GENERICO};
    	
    	$scope.stringEmpty =function(valore) {
        	  if(angular.isDefined( valore )  &&
        	     valore!== null &&
        	     valore.trim().length > 0 ){
        	    return false;
        	  }
        	  else{
        	    return true;
        	  }
        	};
    	$scope.first = function first(obj) {
    		//$log.debug('obj=',obj);
    	    for (var a in obj) return a;
    	};
    	
    	$scope.defaultQualifica = function(sottoscrittore){
    		if(sottoscrittore && sottoscrittore.profilo){
    			sottoscrittore.qualificaProfessionale = sottoscrittore.profilo.hasQualifica[0];
    		}
    	};
    	
    	$scope.defaultQualificaIncaricato = function(incaricato){
    		if(incaricato && incaricato.profilo){
    			incaricato.qualificaProfessionale = incaricato.profilo.hasQualifica[0];
    		}
    	};
    	
    	$scope.defaultQualificaPrimoSottoscrittore = function(sottoscrittori) {
    		
    		try {
    			var primoSottoscrittore = sottoscrittori[0];
    			if (primoSottoscrittore.profilo && !primoSottoscrittore.qualificaProfessionale ) {
    				primoSottoscrittore.qualificaProfessionale = primoSottoscrittore.profilo.hasQualifica[0];
        		}
    		}
    		catch (e) {
				console.log("Errore impostazione qualifica sottoscrittore.", e);
			}
    		
    		return true;
    	}
    	

    	$scope.validateSottoscrittori = function(sottoscrittori){
    		var seen = {};
    		var primoSottoscrittore = sottoscrittori[0];
    		    		
    		$scope.primoSottoscrittoreDuplicato = false;
    		var hasDuplicates = sottoscrittori.some(function (current) {
    			if(!current.aooNonProponente){
	    			if(!current || !current.profilo || !current.qualificaProfessionale){
	    				return false;
	    			}else if(primoSottoscrittore.profilo.id == current.profilo.id){
	    				if (seen.hasOwnProperty(current.profilo.id+""+current.qualificaProfessionale.id)){
	    					$scope.primoSottoscrittoreDuplicato = true;
	    					return true;
	    				}
	    			}else if (seen.hasOwnProperty(current.profilo.id+""+current.qualificaProfessionale.id)) {
	    		        return true;
	    		    }
	
	    		    return (seen[current.profilo.id+""+current.qualificaProfessionale.id] = false);
    			}else{
    				return false;
    			}
    		});
    		
    		$scope.isNotValidSottoscrittori = hasDuplicates;
    	};
    	
    	$scope.validateIncarichi = function(incarico, incaricoIndex, tipoTask){
    		$scope.incarichi[incaricoIndex].runValidate = false;
    		var seen = {};
    		var hasDuplicates = incarico.some(function (current) {
    			
    			if(tipoTask === $rootScope.configurationParams.tipo_conf_task_profilo_id){
    				if(!current || !current.qualificaProfessionaleDto){
        				return false;
        			}else if (seen.hasOwnProperty(current.idProfilo+"_"+current.qualificaProfessionaleDto.id)) {
        		        return true;
        		    }
    				
    				return (seen[current.idProfilo+"_"+current.qualificaProfessionaleDto.id] = false);
    			} else if(tipoTask === $rootScope.configurationParams.tipo_conf_task_ufficio_id){
    				if(!current){
        				return false;
        			}else if (seen.hasOwnProperty(current.idAoo)) {
        		        return true;
        		    }
    				
    				return (seen[current.idAoo] = false);
    			} else {
    				return true;
    			}
    			
    		});
    		$scope.incarichi[incaricoIndex].isValid=!hasDuplicates;
    	};
    	
    	$scope.validateSottoscrittoriNonProponente = function(sottoscrittori){
    		var seen = {};
    		var hasDuplicates = sottoscrittori.some(function (current) {
    			if(current.aooNonProponente){
    				if(!current || !current.profilo || !current.qualificaProfessionale || !current.qualificaProfessionale.enabled){
	    				return false;
	    			}else if (seen.hasOwnProperty(current.profilo.id+""+current.qualificaProfessionale.id)) {
	    		        return true;
	    		    }
	
	    		    return (seen[current.profilo.id+""+current.qualificaProfessionale.id] = false);
    			}else{
    				return false;
    			}
    		});
    		
    		$scope.isNotValidSottoscrittoriNonProponente = hasDuplicates;
    	};
		
    	$scope.trascriviBeneficiariToInformazioniAnagraficoContabili = function(){
    		Beneficiario.htmlFromBeneficiari($scope.atto.beneficiari, function(response){
    			if(response!=undefined && response!=null && response.html!=undefined && response.html!=null){
    				if($scope.atto.informazioniAnagraficoContabili.testo!=undefined && $scope.atto.informazioniAnagraficoContabili.testo!=null){
    					$scope.atto.informazioniAnagraficoContabili.testo += response.html;
    				}else{
    					$scope.atto.informazioniAnagraficoContabili.testo = response.html;
    				}
    			}else{
    				$log.debug("Si è verificato un errore durante la trascrizione dei beneficiari.");
    			}
    		});
    	}
    	
    	$scope.initTitoloParere = function(parere){
    		if($scope.dtoWorkflow.parere.tipoAzione != 'parere_contabile'){
    			if(!$scope.dtoWorkflow.parere.titolo){
    				$scope.dtoWorkflow.parere.titolo = $scope.dtoWorkflow.parere.tipoAzione.codice;
    			}
    		}else{
    			if(!$scope.dtoWorkflow.parere.titolo){
    				$scope.dtoWorkflow.parere.titolo = "Esito adempimenti contabili";
    			}
    		}
    	};
    	if(angular.isUndefined($scope.beneficiariTrasp)){
    		$scope.beneficiariTrasp = [];
    	}
    	$scope.beneficiariActive = 0;
    	$scope.fattura = {};
    	$scope.beneficiarioEdit = {};
    	
    	$scope.purificaTestoPerJs = function(testoOriginale){
    		return $sce.trustAsJs(testoOriginale);
    	};
    	
    	$scope.saveFattura = function(){
    		if($scope.fattura.id != undefined){
    			for(var i = 0; i<$scope.beneficiarioEdit.fatture.length; i++){
    				if($scope.beneficiarioEdit.fatture[i].id!=undefined){
	    				if($scope.fattura.id == $scope.beneficiarioEdit.fatture[i].id){
	    					$scope.beneficiarioEdit.fatture[i] = $scope.fattura;
	    					break;
	    				}
    				}
    			}
    		}else if($scope.fattura.index != undefined){
    			for(var i = 0; i<$scope.beneficiarioEdit.fatture.length; i++){
    				if($scope.beneficiarioEdit.fatture[i].index != undefined){
	    				if($scope.fattura.index == $scope.beneficiarioEdit.fatture[i].index){
	    					$scope.beneficiarioEdit.fatture[i] = $scope.fattura;
	    					break;
	    				}
    				}
    			}
    		}else{
    			if($scope.beneficiarioEdit.fatture!=undefined && $scope.beneficiarioEdit.fatture!=null && $scope.beneficiarioEdit.fatture.length!=undefined){
    				$scope.fattura.index = $scope.beneficiarioEdit.fatture.length + 1;
    			}else{
    				$scope.fattura.index = 1;
    			}
    			if($scope.beneficiarioEdit.fatture==undefined || $scope.beneficiarioEdit.fatture == null){
    				$scope.beneficiarioEdit.fatture = [];
    			}
    			$scope.fattura.beneficiario = {};
    			$scope.fattura.beneficiario.id = new Number($scope.beneficiarioEdit.id);
    			$scope.beneficiarioEdit.fatture.push($scope.fattura);
    		}
    		$('#fatturaBeneficiarioModal').modal('hide');
    	};
    	
    	$scope.removeFattura = function(beneficiario, fattura){
    		alert($translate('cifra2gestattiApp.fattura.alertCancellazione'));
    		var cancellato = false;
    		if(fattura.id != undefined){
    			for(var i = 0; i<beneficiario.fatture.length; i++){
    				if(beneficiario.fatture[i].id!=undefined){
	    				if(fattura.id == beneficiario.fatture[i].id){
	    					beneficiario.fatture.splice(i, 1);
	    					cancellato = true;
	    					break;
	    				}
    				}
    			}
    		}else if(fattura.index != undefined){
    			for(var i = 0; i<beneficiario.fatture.length; i++){
    				if(beneficiario.fatture[i].index != undefined){
	    				if(fattura.index == beneficiario.fatture[i].index){
	    					beneficiario.fatture.splice(i, 1);
	    					break;
	    				}
    				}
    			}
    		}
    		if(cancellato){
	    		for(var i = 0; i<beneficiario.fatture.length; i++){
	    			if(beneficiario.fatture[i].index != undefined){
	    				beneficiario.fatture[i].index = beneficiario.fatture[i].index-1;
	    			}
	    		}
    		}
    	};
    	
    	$scope.editBeneficiarioFattura = function(beneficiario, fattura){
    		$scope.addBeneficiarioFattura(beneficiario, fattura);
    	};
    	
    	$scope.addBeneficiarioFattura = function(beneficiario, fattura){
    		$scope.beneficiarioEdit = beneficiario;
    		if(fattura!=undefined){
    			$scope.fattura = fattura;
    		}else{
    			$scope.fattura = {};
    		}
    		$scope.fattura.edit = true;
			$('#fatturaBeneficiarioModal').modal('show');
    	};
    	
    	$scope.importaRubricaBeneficiario = function(atto){
    		$scope.aggiungiSchedaBeneficiario($scope.rubricaBeneficiarioToImport,atto);
    		atto.allegatoBeneficiari = false;
    		$('#rubricaBeneficiariModal').modal('hide');
    	};
    	
    	$scope.aggiungiSchedaBeneficiario = function (beneficiarioFromRubrica, paramAtto) {
    		var atto = null;
    		if(paramAtto){
    			atto = paramAtto;
    		}else{
    			atto = $scope.atto;
    		}
    		if(!beneficiarioFromRubrica){
    			atto.beneficiari.push({atto: { id: atto.id}});
    			angular.element(".nav-tabs .active").removeClass('active');
    			angular.element(".tab-content .active").removeClass('active');
    		}else{
    			atto.beneficiari.push({atto: { id: atto.id}});
    			angular.element(".nav-tabs .active").removeClass('active');
    			angular.element(".tab-content .active").removeClass('active');
    			
    			var newBeneficiario = atto.beneficiari[atto.beneficiari.length - 1];
    			angular.copy(beneficiarioFromRubrica, newBeneficiario);
    			newBeneficiario.id = null;
    			newBeneficiario.atto = {id : atto.id};
    		}
    		atto.allegatoBeneficiari = false;
			$scope.beneficiarioEdit = true;
			$scope.beneficiariActive = atto.beneficiari.length - 1;
    	};
    	
    	$scope.eliminaSchedaBeneficiari = function (beneficiario, selected) {
    		if(confirm($translate('cifra2gestattiApp.beneficiario.alertCancellazione'))){
	    		var index = 1;
				var idBenRimosso = $scope.atto.beneficiari.splice(selected, 1)[0].id;
				if(idBenRimosso!=undefined && idBenRimosso!=null){
					for(var i = 0; i< $scope.beneficiariTrasp.length; i++){
						if($scope.beneficiariTrasp[i].id == idBenRimosso){
							$scope.beneficiariTrasp.splice(i, 1);
							break;
						}
					}
				}
				if($scope.valoriSchedeDati!=undefined && $scope.valoriSchedeDati.elementiSchede!=undefined && $scope.valoriSchedeDati.elementiSchede!=undefined){
					angular.forEach($scope.valoriSchedeDati.elementiSchede, function(schede, j, schedeObj) {
						for(var y = 0; y<schedeObj[j].length; y++){
							angular.forEach(schedeObj[j][y], function(elemento, x, elementiObj) {
	    						if(elementiObj[x]!=null && elementiObj[x].descrizioneLeggibileBeneficiario != undefined && elementiObj[x].descrizioneLeggibileBeneficiario != null && elementiObj[x].id == idBenRimosso){
	    							elementiObj[x] = {id:-1};
	    						}
	        				});
						}
					});
				}
	
	    		if(selected>0)--selected;
	    		$scope.beneficiariActive = selected;
    		}
    	}
    	
    	$scope.getTipoAttoFromProfiloAttivo = function(idTipoAtto){
    		 for(var i = 0; $rootScope.profiloattivo.tipiAtto.length; i++){
    			 if($rootScope.profiloattivo.tipiAtto[i].id == idTipoAtto){
    				 $scope.tipoAtto = $rootScope.profiloattivo.tipiAtto[i];
    				 break;
    			 }
    		 }
    		 return $scope.tipoAtto;
    	};

        
        	$scope.loadaoo = function (id) {
        		$scope.editing = false;
        		Aoo.get({id: id}, function(result) {
        			$scope.aoo = result;
        			$scope.aoos = [];
        			$scope.aoos.push($scope.aoo);
        		});
        		$scope.aooId = id; 	
        	};
        	$scope.isSolaLettura = function (taskBpmId, scenari) {
        		var solaLettura = angular.isUndefined( taskBpmId ) || taskBpmId == null || taskBpmId == '' || taskBpmId == 0;
        		if(!solaLettura){
        			if(scenari!=undefined && scenari.length != undefined){
	        			for(var i=0; i<scenari.length; i++) {
	        				if(scenari[i] == "TuttoNonModificabile"){
	        					solaLettura = true;
	        					break;
	        				}
	        			}
        			}
        		}
        		return solaLettura;
        	}

        	$scope.initView = function(section, tipoattoid) {
        		if( angular.isDefined(section)) {
        			$scope.initSelezionaSezione(section, tipoattoid);
        		} else{
        			$scope.initSelezionaSezione(0, tipoattoid);
        		}
        		$scope.showSezioneContabile = 'informazioniAnagraficoContabili';
        		        		
        		if($scope.solaLettura) {
        			if($scope.scenariDisabilitazione!= null && $scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0 && $scope.scenariDisabilitazione.indexOf('PostPubblicazioneTrasparenza') < 0){
        				$scope.scenariDisabilitazione.push('TuttoNonModificabile');
        			}
        		} else{
        			$scope.checkDirtyForm();//$rootScope.activeExitUserConfirmation = true;
        		}
        		$timeout(function() { 
        			$scope.verifyAllSections();
        		}, 2000);
        		
        	}

        	$scope.exitFormConfirmation = function() {
        		$rootScope.activeExitUserConfirmation = null;//, $rootScope.navigation.toStateParams
        		$('#exitFormConfirmation').modal('hide');
        		if($rootScope.cambioProfilo){
        			ProfiloService.selezionaProfilo($rootScope.nuovoProfilo);
        		} else {
        			$timeout(function () {
            			$state.go($rootScope.navigation.toState.name, $rootScope.navigation.toStateParams);
            		}, 500);
        		}
        	};
        	
        	$scope.dismissModal = function() {
        		$log.debug('Dismiss modal');
        		$('#exitFormConfirmation').modal('hide');
        		$rootScope.$broadcast('globalLoadingEnd');
        		delete $rootScope.nuovoProfilo;
        	};
        	
        	/**
        	 * Evento che gestisce il cambiamento del profilo.
        	 */
        	$rootScope.$on('profileChange', function () {
        		if($rootScope.activeExitUserConfirmation){
        			$('#exitFormConfirmation').modal('show');
        		} else {
        			$scope.exitFormConfirmation();
        		}
        	});
        	
        	/*Dato un array di documentoPdf (atti/proposte/pareri) restituisce l'id del primo documentoPdf non firmato*/
        	$scope.getPrimoDocumentoNonFirmatoId = function(documentiPdf){
        		var id = null;
        		for(var i = 0; i<documentiPdf.length; i++){
        			if(!documentiPdf[i].firmato && documentiPdf[i].id){
        				id = documentiPdf[i].id;
        				break;
        			}
        		}
        		return id;
        	};
        	
        	/**
        	 * String the HTML from the given string.
        	 * @param string The string to strip.
        	 */
        	$scope.stripHtml = function(string){
        		return jQuery('<div>' + string + '</div>').text();
        	};
        	
        	$scope.settingAooDestinatari = function(){
        		if(!$scope.aooDestinatari && !isNaN($stateParams.id)){
	        		$scope.aooDestinatari = $scope.tutteLeAoo.map(function(aoo){ 
						   var attoHasDestinatario = {};
						   attoHasDestinatario["attoId"] = $stateParams.id;
						   attoHasDestinatario["tipoDestinatario"] = {descrizione: "Aoo"};
						   attoHasDestinatario["destinatario"] = aoo;
						   attoHasDestinatario["name"] = aoo.descrizione;
						   attoHasDestinatario["email"] = aoo.email;
						   attoHasDestinatario["descrizione"] = aoo.descrizioneAsDestinatario;
						   attoHasDestinatario["strippedDescrizione"] = aoo.descrizioneAsDestinatario;
						   attoHasDestinatario["codice"] = aoo.codice;
						   return attoHasDestinatario;
					});
					
					//Order by codice AOO e Descrizione
					$scope.aooDestinatari = $filter('orderBy')($scope.aooDestinatari, ['codice', 'name']);
					
					$scope.changeTipoDestinatario('aoo');
        		}
        	};
        	
        	//destinatari esterni ed interni
        	$scope.loadRubricaDestinatarioInterno= function  () {
						/*
						if($scope.tutteLeAoo == undefined){
	   				 var deferred = $q.defer();
	   				 $scope.tutteLeAoo = deferred.promise;
	   				 Aoo.getMinimal({}, function(aoos){
	   					 deferred.resolve();
	   					 $scope.tutteLeAoo = aoos;
	   					 $scope.settingAooDestinatari();
	   				 });
	   			 }else if (typeof $scope.tutteLeAoo.then === 'function') {
	   				 $scope.tutteLeAoo.then(function() {
	   					 $scope.settingAooDestinatari();
	   				 });
	   			 }
        		if(!$scope.assessoratoDestinatari)
        			Assessorato.query(function(assessoratos) {
        				$scope.assessoratoDestinatari = assessoratos.map(function(assessorato){ 
        					   var attoHasDestinatario = {};
        					   attoHasDestinatario["attoId"] = $scope.atto.id;
        					   attoHasDestinatario["tipoDestinatario"] = {descrizione: "Assessorato"};
        					   attoHasDestinatario["destinatario"] = assessorato;
        					   attoHasDestinatario["name"] = assessorato.denominazione;
        					   attoHasDestinatario["email"] = assessorato.email;
        					   attoHasDestinatario["descrizione"] = assessorato.descrizioneAsDestinatario;
        					   attoHasDestinatario["strippedDescrizione"] = assessorato.descrizioneAsDestinatario;
        					   return attoHasDestinatario;
        				});
        				
        				//Order by Denominazione
        				$scope.assessoratoDestinatari = $filter('orderBy')($scope.assessoratoDestinatari, 'name');
        			});	

        		if(!$scope.utenteDestinatari)
        			Utente.query(function(utentes) {
        				$scope.utenteDestinatari = utentes.map(function(utente){ 
        					   var attoHasDestinatario = {};
        					   attoHasDestinatario["attoId"] = $scope.atto.id;
        					   attoHasDestinatario["tipoDestinatario"] = {descrizione: "Utente"};
        					   attoHasDestinatario["destinatario"] = utente;
        					   attoHasDestinatario["name"] = utente.cognome + ' ' + utente.nome;
        					   attoHasDestinatario["cognome"] = utente.cognome;
        					   attoHasDestinatario["nome"] = utente.nome;
        					   attoHasDestinatario["email"] = utente.email;
        					   attoHasDestinatario["descrizione"] = utente.descrizioneAsDestinatario;
        					   attoHasDestinatario["strippedDescrizione"] = $scope.stripHtml(utente.descrizioneAsDestinatario);
        					   return attoHasDestinatario;
        				});
        				
        				//Order by Cognome e Nome
        				$scope.utenteDestinatari = $filter('orderBy')($scope.utenteDestinatari, ['cognome', 'nome']);
        			});	
        		
        		if($scope.atto.destinatariInterni) {
        			$scope.atto.destinatariInterni = $scope.atto.destinatariInterni.map(function(dest){ 
        				
        				if(dest.tipoDestinatario.descrizione=="Aoo") {
        					dest["name"] = dest.destinatario.descrizione;
        					dest["email"] = dest.destinatario.email;
        					dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
        					dest["strippedDescrizione"] = dest.destinatario.descrizioneAsDestinatario;
        					return dest;
        				}
        				if(dest.tipoDestinatario.descrizione=="Assessorato") {
        					dest["name"] = dest.destinatario.denominazione;
        					dest["email"] = dest.destinatario.email;
        					dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
        					dest["strippedDescrizione"] = dest.destinatario.descrizioneAsDestinatario;
        					return dest;
        				}
        				if(dest.tipoDestinatario.descrizione=="Utente") {
        					dest["name"] = dest.destinatario.cognome+ ' ' +dest.destinatario.nome;
        					dest["email"] = dest.destinatario.email;
        					dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
        					dest["strippedDescrizione"] = $scope.stripHtml(dest.destinatario.descrizioneAsDestinatario);
        					return dest;
        				}
        			});
						}
						*/
        	}

        	$scope.changeTipoDestinatario = function  (preset) {
        		$scope.rubricaDestinatarioInternos = [];
        		if(preset == 'aoo')
        			$scope.rubricaDestinatarioInternos = $scope.aooDestinatari;

        		if(preset == 'ass')
        			$scope.rubricaDestinatarioInternos =$scope.assessoratoDestinatari;
        		
        		if(preset == 'ute')
        			$scope.rubricaDestinatarioInternos = $scope.utenteDestinatari;
        	}
        	     
        	$scope.querySearchExt= function  (query) {
        		var results = query ?
        				$scope.rubricaDestinatarioEsternos.filter( $scope.createFilterForExt(query)) : [];
        				return results;	
        	}

        	$scope.querySearchInt= function  (query) {
        		var results = query ?
        				$scope.rubricaDestinatarioInternos.filter( $scope.createFilterForInt(query)) : [];
        				return results;	
        	}

        	$scope.createFilterForExt = function (query) {
        		var lowercaseQuery = angular.lowercase(query);
        		return function filterFn(contact) {
        			return (contact.nameCerca.indexOf(lowercaseQuery) != -1);
        		};
        	}

        	$scope.createFilterForInt = function (query) {
        	   var lowercaseQuery = angular.lowercase(query);
        	   return function filterFn(contact) {
        		   contact.descrizione = contact.destinatario.descrizioneAsDestinatario;
        		   var lowercaseDest = angular.lowercase(contact.destinatario.descrizioneAsDestinatario);
        		   return (lowercaseDest.indexOf(lowercaseQuery) != -1);
        	   };
        	}

        	$scope.loadRubricaDestinatarioEsterno = function () {
        	   RubricaDestinatarioEsterno.getByAooId({ aooId:$rootScope.profiloattivo.aoo.id}  , function(result, headers) {
        		   $scope.rubricaDestinatarioEsternos = result.filter($scope.filterDestinatariEsterniInit());
        		   $scope.rubricaDestinatariList = angular.copy($scope.rubricaDestinatarioEsternos.filter($scope.filterDestinatariEsterni()));
        	   });
        	};
        	
        	$scope.filterDestinatariEsterniInit = function () {
        		return function filterFn(dest) {
        			return dest.id && (dest.aoo || dest.aoo == null) && !dest.validita.validoal;
        		};
        	}
        	$scope.filterDestinatariEsterni = function () {
        		return function filterFn(dest) {
        			$log.debug("DESTINATARIO:",dest);
        			for(var i = 0; i<$scope.atto.destinatariEsterni.length; i++){
        				if($scope.atto.destinatariEsterni[i].id == dest.id) {
        					return false;
        				}
        			}
        			return dest.id && (dest.aoo || dest.aoo == null) && !dest.validita.validoal;
        		};
        	}
        	
        	$scope.disableDestinatarioEsterno = function (destinatario) {
        		destinatario.validita.validoal = new Date();
        		RubricaDestinatarioEsterno.update( destinatario, function(result) {
					for(var i = 0; i<$scope.atto.destinatariEsterni.length; i++){
        				if($scope.atto.destinatariEsterni[i].id == destinatario.id) {
        					$scope.rubricaDestinatarioEsternos = null;
        					$scope.atto.destinatariEsterni.splice(i, 1);
        					$scope.loadRubricaDestinatarioEsterno();
        					return;
        				}
        			}
				});
        	}
        	
        	$scope.$watch("atto.destinatariEsterni", function(value){
        		if($scope.rubricaDestinatarioEsternos) {
        			$scope.rubricaDestinatariList = $scope.rubricaDestinatarioEsternos.filter($scope.filterDestinatariEsterni());
        		}
        	});
        	
        	

        		$scope.addRubricaDestinatarioEsterno = function( ) {
        			$scope.loadaoo($scope.atto.aoo.id);
        			$scope.editing = true;
        			$scope.checkEsistenzaRDE = false;
        			$scope.rubricaDestinatarioEsterno = { aoo:{ id: $scope.atto.aoo.id } ,edit:true } ;
        			$('#saveRubricaDestinatarioEsternoModal').modal('show');
        			$scope.editing = true;
        		};

        		$scope.salvaRubricaDestinatarioEsterno= function( rubricaDestinatarioEsterno  ) {
        			$scope.appo = null;
        			RubricaDestinatarioEsterno.checkIfAlreadyexist($scope.rubricaDestinatarioEsterno, function(data){
        	        	$scope.checkEsistenzaRDE = data.alreadyExists;
        	    		if($scope.checkEsistenzaRDE == false){
		        			$scope.appo=RubricaDestinatarioEsterno.save( rubricaDestinatarioEsterno, function(result) {
		    						$scope.loadaoo( $scope.aoo.id );
		    						$scope.loadRubricaDestinatarioEsterno();
		    						$scope.insertDestinatariEsterni();        						
		    				});
		    				$('#saveRubricaDestinatarioEsternoModal').modal('hide');
		        			if($scope.appo && $scope.appo.tipo=="PRIVATO"){
		        				$scope.namecercaappo=$scope.appo.nome + ' ' + $scope.appo.cognome;
		        			}
		        			else if($scope.appo){
		        				$scope.namecercaappo=$scope.appo.denominazione;
		        			}
        	    		}
        			});
        		};
        		
        	$scope.insertDestinatariEsterni = function () {
        		$scope.atto.destinatariEsterni.push({
        			id : $scope.appo.id,
        			nameCerca : $scope.namecercaappo,
        			image : 'assets/images/'+ $scope.appo.tipo +'.png',
        			denominazione : $scope.appo.denominazione,
        			nome : $scope.appo.nome,
        			cognome : $scope.appo.cognome,
        			titolo : $scope.appo.titolo,
        			email : $scope.appo.email,
        			pec : $scope.appo.pec,
        			tipo : $scope.appo.tipo,
        			aoo:$scope.appo.aoo,
        			indirizzo : $scope.appo.indirizzo,
        		});
        	}
        		
        	
        	
        		//ambito-materia
        	$scope.loadAttoHasAmbitoMateria = function () {
        		$scope.AttoHasAmbitoMaterias=AttoHasAmbitoMateria.query();
        	};
        	$scope.addRowAmbitomateria = function( ){
        		 $scope.attoHasAmbitoMateria.atto =  {id: $scope.atto.id};

        		$scope.atto.hasAmbitoMateriaDl.push( $scope.attoHasAmbitoMateria ) ;

        	 
        		$log.debug($scope.atto.hasAmbitoMateriaDl);

        	  $scope.attoHasAmbitoMateria = {  };
        	};

        	$scope.removeRowAmbitoMateria = function(index){				
        		$scope.atto.hasAmbitoMateriaDl.splice( index, 1 );		
        	};

        	
        	//allegati
        	$scope.formatoFileParteIntegrante = [ ".odt", ".rtf", ".doc", ".docx", ".txt", ".pdf"];
        	
        	
        	$scope.checkPubblicabile = function(allegatoEdit) {
        		if (allegatoEdit.tipoAllegato.codice==AllegatoConstants.tipoAllegato.GENERICO) {
        			allegatoEdit.pubblicabile = false;
        		}
        	};
        	
        	$scope.checkPubblicabileMassiva= function() {
        		if ($scope.tipoAllegatoMassivo.codice==AllegatoConstants.tipoAllegato.GENERICO) {
        			$scope.pubblicabileMassivo.codice  = false;
        		}else{
        			$scope.pubblicabileMassivo.codice  = 'n';
        		}
        	};
        	
        	$scope.checkFileFormat = function(allegatoEdit) {
        		
        		if (allegatoEdit && allegatoEdit.tipoAllegato && allegatoEdit.tipoAllegato.codice==AllegatoConstants.tipoAllegato.ACCORPATO) {
        			var estensioneAllegato = allegatoEdit.file.nomeFile.split('.').pop().toLowerCase();
        			
        			if ($scope.formatoFileParteIntegrante.indexOf("." + estensioneAllegato) < 0) {
        				$('#warningFileType').modal('show');
        				allegatoEdit.tipoAllegato = null;
        			}
        		}
        	};
        	
        	$scope.checkCampiProtocollo = function(allegatoEdit) {
        		if (allegatoEdit.tipoAllegato!=null && allegatoEdit.tipoAllegato.codice!=null && (allegatoEdit.tipoAllegato.codice==AllegatoConstants.tipoAllegato.PARTE_INTEGRANTE || allegatoEdit.tipoAllegato.codice==AllegatoConstants.tipoAllegato.ACCORPATO) ) {
        			allegatoEdit.dataProtocollo = null;
        			allegatoEdit.numeroProtocollo = null;
        		}
        	};
        	
        	$scope.changeTipoAllegato = function(allegatoEdit){
        		if(!allegatoEdit || !allegatoEdit.tipoAllegato){
        			return;
        		}
        		$scope.checkFileFormat(allegatoEdit);
        		
        		if(allegatoEdit.tipoAllegato.codice == AllegatoConstants.tipoAllegato.GENERICO && (allegatoEdit.omissis || allegatoEdit.fileomissis) ){
        			allegatoEdit.tipoAllegato.codice=AllegatoConstants.tipoAllegato.PARTE_INTEGRANTE;
        			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Per selezionare la tipologia di allegato generico occorre che il campo omissis sia valorizzatto a \'NO\' e che non sia stato inserito un allegato omissis.'});
        			return;
        		}
        		$scope.checkCampiProtocollo(allegatoEdit);
        		$scope.checkPubblicabile(allegatoEdit);
        	};
        	
        	$scope.changeAllegatoPubblicabile = function(allegato){
        		if(allegato.pubblicabile && allegato.pubblicabile == true && allegato.omissis == null){
        			allegato.omissis = false;
        		}
       		
				if(!$scope.scenariDisabilitazione || ($scope.scenariDisabilitazione.indexOf('PostPubblicazioneTrasparenza') < 0 && $scope.scenariDisabilitazione.indexOf('DatiPubblicazioneModificabili') < 0)){
	        		if(!allegato.pubblicabile && (allegato.omissis || allegato.fileomissis)){
	        			allegato.pubblicabile = true;
	        			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Per selezionare il valore \'NO\' occorre che il campo omissis sia valorizzatto a \'NO\' e che non sia stato inserito un allegato omissis.'});
	        		}
				}
        	};
        	
        	$scope.fileDropperDocumentoFirmato = function (files,event,rejectedFiles,omissis, adozione ,dtoWorkflow,parereId, schedaAnagraficoContabile, idTempDownload, codiceTipoDocumento,attoInesistente,relataPubblicazione) {
        		if(event.type !== "click"){
        			if(files!=undefined && files[0]!=undefined){
	        			if(schedaAnagraficoContabile!=undefined && schedaAnagraficoContabile == true){
	        		    	dtoWorkflow.campi['firmatoSchedaAnagraficoContabile'] = true;
	        		    }else if( omissis ){
	        		    	dtoWorkflow.campi['firmatoOmissis'] = true;
	        		    }else{
	        		    	var indexFile="";
	        		    	if(idTempDownload!=undefined && idTempDownload!=null && idTempDownload.indexOf("_")>-1){
	        		    		var res = idTempDownload.split("_");
	        		    		indexFile=res[1];
	        		    	}
	        		    	dtoWorkflow.campi['firmato'+indexFile] = true;
	        		    }
	        			var parerePart="";
	        		    if(parereId != null){
	        		      parerePart = "&parereId="+parereId;
	        		    }
	        		    var restUrl = null;
	        		    if(schedaAnagraficoContabile!=undefined && schedaAnagraficoContabile == true){
	        		  		  restUrl = 'api/attos/'+$scope.atto.id +'/firmato/allegato?omissis='+omissis+"&adozione="+adozione+parerePart + '&schedaAnagraficoContabile=true';
	        		  	}else{
	        		  		  restUrl = 'api/attos/'+$scope.atto.id +'/firmato/allegato?omissis='+omissis+"&adozione="+adozione+parerePart + '&schedaAnagraficoContabile=false';
	        		  	}
	        		    if(attoInesistente!=undefined && attoInesistente == true){
	        		    	restUrl += '&attoInesistente=true';
	        		    }else{
	        		    	restUrl += '&attoInesistente=false';
	        		    }
	        		    if(relataPubblicazione!=undefined && relataPubblicazione == true){
	        		    	restUrl += '&relataPubblicazione=true';
	        		    }else{
	        		    	restUrl += '&relataPubblicazione=false';
	        		    }
	        		    restUrl+= '&codiceTipoDocumento='+codiceTipoDocumento;
	        		    var fileTmp = {};
	        		    fileTmp.type = files[0].type;
	        			fileTmp.name = files[0].name;
	        			$("#" + idTempDownload).html("<a style=\"cursor:default;\" class=\"list-group-item ng-binding\" href=\"#\" onclick=\"return false;\"><span class=\"fa fa-upload\"\"> "+fileTmp.name+" (Atteso click su conferma per il caricamento)</span></a>");
	        		    $scope.documentiFirmatiDaCaricare.set(restUrl, files[0]);
        			}else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
        				$rootScope.showMessage({title:'Attenzione', okButton:true, body:"La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB"});
        				/*alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");*/
        			}
        	    }
        	};
        	
        	$scope.fileDropperDocumentoFirmatoV2 = function (files,event,rejectedFiles,codiceTipoDocumento,idTempDownload) {
        		if(event.type !== "click"){
        			if(files!=undefined && files[0]!=undefined){
	        			
	        		    var restUrl = 'api/attos/'+$scope.atto.id +'/firmato/allegato?codiceTipoDocumento='+codiceTipoDocumento;
	        		  	
	        		    
	        		    var fileTmp = {};
	        		    fileTmp.type = files[0].type;
	        			fileTmp.name = files[0].name;
	        			$("#" + idTempDownload).html("<a style=\"cursor:default;\" class=\"list-group-item ng-binding\" href=\"#\" onclick=\"return false;\"><span class=\"fa fa-upload\"\"> "+fileTmp.name+" (Atteso click su conferma per il caricamento)</span></a>");
	        		    $scope.documentiFirmatiDaCaricare.set(restUrl, files[0]);
        			}else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
        				$rootScope.showMessage({title:'Attenzione', okButton:true, body:"La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB"});
        				/*alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");*/
        			}
        	    }
        	};
        	
        	
        	$scope.downloadFirmatoTmp = function(name, type, base64){
        		download(base64, name, type);
        	};
        	
        	$scope.estensioniAccettate = ["doc", "docx", "rtf", "txt", "odt"];
        	
        	
        	/**
        	 * Genera le estensioni accettate in modo leggibile.
        	 */
        	$scope.generaEstensioniAccettateLeggibili = function (){
        		var estensioniAccettateLeggibile = "";
        		$scope.estensioniAccettate.forEach(function(item) {
        			if(estensioniAccettateLeggibile){
        				estensioniAccettateLeggibile += ", ";
        			}
        			estensioniAccettateLeggibile += item;
    			});
        		return estensioniAccettateLeggibile;
        	};
        	
        	/**
        	 * Genera estensioni accettate ngf.
        	 */
        	$scope.generaEstensioniAccettateAllegatiNgf = function (){
        		var estensioniAccettateLeggibile = ".pdf";
        		$scope.estensioniAccettate.forEach(function(item) {
        			estensioniAccettateLeggibile += ",." + item;
    			});
        		return estensioniAccettateLeggibile;
        	};
        	
        	
        	$scope.fileImportDropped = function (files,event,rejectedFiles,importing, controllaEstensione) {
        	    $log.debug(event);

        	    if(event.type !== "click"){
        	    	$scope.importingSection[importing] = true;
        	    	var eseguiOperazione = true;
        	    	if(controllaEstensione != undefined && controllaEstensione){
        	    		var estensione = files[0].name.split(".")[files[0].name.split(".").length - 1];
        	    		
						if(estensione && estensione.toLowerCase() != 'docx' && $scope.newEditorVersion){
							//nuova versione editor
							eseguiOperazione = false;
							$scope.importingSection[importing] = false;
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Estensione del file non valida. Il sistema accetta solo file di tipo DOCX.<br />Per utilizzare file di tipologia differente consigliamo di effettuare la copia del contenuto e incollarlo direttamente all'interno dell'editor."});
						}else if($scope.estensioniAccettate.indexOf(estensione) == -1){
        	    		   eseguiOperazione = false;
        	    		   var estensioniAccettateLeggibile = $scope.generaEstensioniAccettateLeggibili();
        	    		   $scope.importingSection[importing] = false;
        	    		   $rootScope.showMessage({title:'Attenzione', okButton:true, body:"Estensione del file non valida. Il sistema accetta solo file con le seguenti estensioni: " + estensioniAccettateLeggibile});
        	    		   /*alert("Estensione del file non valida. Il sistema accetta solo file con le seguenti estensioni: " + estensioniAccettateLeggibile)*/
        	    		}
        	    	}
        	      if(eseguiOperazione){
        	    	  if(rejectedFiles.length > 0){
        	    		  $rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, $scope.estensioniAccettate);
        	    		  return;
	    	    	  }
	        	      Upload.upload({
	        	        url: $scope.newEditorVersion ? ConvertService.extractV2 : ConvertService.extract,
	        	        headers : {
	        	          'Authorization': 'Bearer '+ $rootScope.accessToken
	        	        },
	        	        file: files[0]
	        	      }).progress(function (evt) {
	        	         
	        	      }).success(function (data, status, headers, config) {
	        	          $scope.atto[importing].testo = data.body;
	        	          $scope.modelloCampo.tipoCampo=importing;
	        	          $scope.modelloCampo.testo = data.body;
	        	          $scope.importingSection[importing] = false;
	        	          $log.debug("import ok");
	        		  }).error(function (error) {
	        		      $scope.importingSection[importing] = false;
	        		      $log.debug("import ko");
	        		  });
        		  }
        	    }
        	  };
        	  
        	  $scope.fileDropped = function (files, event, rejectedFiles) {
        		  $log.debug(event);
        		  
        		  if(event.type !== "click"){
        			  if(rejectedFiles.length > 0){
        				  $log.log("There are rejected files for the size");
        				  $rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, 'application/*');
        				  return;
    				  }
    				  
        			if(files.length < 2){
        			  
        			  
        			  Upload.upload({
        				  url: 'api/attos/'+$scope.atto.id +'/allegato',
        				  headers : {
        					  'Authorization': 'Bearer '+ $rootScope.accessToken
        				  },
        				  file: files
    				  }).progress(function (evt) {
    					  $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
					  }).success(function (data, status, headers, config) {
						  $log.debug("upload ok");
						  $scope.progressPercentage = null;
						  if(!$scope.atto.allegati)
							  $scope.atto.allegati = [];
							  
						  for(var i = 0; i<data.length; i++){
						  	$scope.atto.allegati.push(data[i]);
						  }
						  $scope.initView($scope.indexSezioneCorrente, $scope.atto.tipoAtto.id);
				  	  });
				    }else{
				       $scope.fileDroppati = [];
				       $scope.salvaFileDroppati(files);
				       $scope.pubblicabileMassivo = {codice: false};
        			   $scope.tipoAllegatoMassivo = {codice: AllegatoConstants.tipoAllegato.GENERICO};
        			  $('#modificaAllegatiConfirmation').modal('show');
				    }
    		      }
        	   };
        		    
    		   $scope.importBeneficiari = function () {
    			   $log.log("Import beneficiari!");
    		   };

    		   $scope.fileOmissisDropped = function (files, event, rejectedFiles) {
    			   $log.debug(event);

        		   if(event.type !== "click"){
        			   if(rejectedFiles.length > 0){
        				   $rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, $scope.estensioniAccettate);
        				   return;
        		       }
        		       
        		       $scope.importingOmissis = true;

        		       var urlUpload = 'api/attos/'+$scope.atto.id +'/allegato/'+$scope.allegatoEdit.id+'/omissis';
        		       
        		       Upload.upload({
        		    	   url: urlUpload,
        		           headers : {
        		        	   'Authorization': 'Bearer '+ $rootScope.accessToken
        		           },
        		           fields: {'allegato': $scope.allegatoEdit},
        		           file: files[0]
        		        }).success(function (data, status, headers, config) {
        		        	$scope.importingOmissis = false;
        		            $log.debug("import omissis ok");
        		            delete $scope.allegatoEdit;
        		            Atto.allegati({id:$scope.atto.id},function(result){
        		            	$scope.atto.allegati = result;
        		            });
        		         });
        		      }
    		      };

    		        $scope.modificaAllegato = function(allegato){
    		        	/*
    		            DocumentoInformatico.get({id:allegato.id},function(result){
    		              $scope.allegatoEdit=result;
    		              $scope.allegatoEdit.edit=true;
    		            });
    		        	 */
    		        	  $scope.backupAllegato = {};
    		        	  angular.copy(allegato, $scope.backupAllegato);
      		              $scope.allegatoEdit=allegato;
      		              $scope.allegatoEdit.edit=true;
      		              $rootScope.$broadcast('verificaDisabilitazioni');
    		          };
    		          
    		          $scope.modificaAllegatiMassivamente = function(){
    		        	  $scope.backupAllegato = {};
    		        	  angular.copy(allegato, $scope.backupAllegato);
      		              $scope.allegatoEdit=allegato;
      		              $scope.allegatoEdit.edit=true;
    		          };
    		          
    		          
    		          $scope.modificaAllegatiMassivamente = function () {
        			 
        			  $timeout(function () {
        				  $scope.pubblicabileMassivo = {codice: false};
        				  $scope.tipoAllegatoMassivo = {codice: AllegatoConstants.tipoAllegato.GENERICO};
        				  $('#modificaAllegatiConfirmation').modal('show');
        			  }, 800);
        		  };
        			
        		  $scope.confirmModificaAllegatiMassivamente = function () {
        			  
					if($scope.fileDroppati){
						Upload.upload({
        				  url: 'api/attos/'+$scope.atto.id +'/allegato',
        				  headers : {
        					  'Authorization': 'Bearer '+ $rootScope.accessToken
        				  },
        				  file: $scope.fileDroppati,
        				  data:{
	        				  tipoAllegato: ($scope.tipoAllegatoMassivo ? $scope.tipoAllegatoMassivo.codice : null),
	        				  pubblicabile: ($scope.pubblicabileMassivo && $scope.pubblicabileMassivo.codice != 'n' ? $scope.pubblicabileMassivo.codice : null)
        				  }
    				  }).progress(function (evt) {
    					  $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
					  }).success(function (data, status, headers, config) {
						  $scope.progressPercentage = null;
						  if(!$scope.atto.allegati){
							  $scope.atto.allegati = [];
						  }	  
						  for(var i = 0; i<data.length; i++){
						  	data[i].tipoAllegato = {codice: $scope.tipoAllegatoMassivo.codice};
						  	if ($scope.tipoAllegatoMassivo.codice==AllegatoConstants.tipoAllegato.GENERICO) {
        						$scope.pubblicabileMassivo.codice  = false;
        					}
				  			if(data[i].pubblicabile==true){
				  				data[i].omissis = false;
				  			}
						  	$scope.atto.allegati.push(data[i]);
						  } 
						  
				  	  });
					}
        			$('#modificaAllegatiConfirmation').modal('hide');
        			  
        		  };
    		          
    		      $scope.$watch("allegatoEdit", function(newValue){
    		    	  if(newValue != null && newValue!=undefined && newValue!={} && newValue.id!=null && newValue.id != undefined){
    		    		  $scope.salvaAllegato(newValue);
    		    	  }
    		      });
    		       
		          $scope.salvaAllegato = function(allegatoEdit){
		        	    for(var i = 0; i<$scope.atto.allegati.length; i++){
		        	    	if($scope.atto.allegati[i].id == allegatoEdit.id){
		        	    		$scope.atto.allegati[i] = allegatoEdit;
		        	    		break;
		        	    	}
		        	    }
		        	    /*
		        	    DocumentoInformatico.update( allegatoEdit, function(result) {
		        	          $scope.allegatoEdit=result;
		        	          $scope.allegatoEdit.edit=allegatoEdit.edit;
		        	          Atto.allegati({id:$scope.atto.id},function(result){
		        	        	  $scope.atto.allegati = result;
		        	          });
		        	      });
		        	    */
		        	  };
		        	  
        		  $scope.allegatiOptions = {
					  dropped: function(event) {
					    $log.debug(event);
					    $log.debug(event.dest.index);
					    for(var i = event.dest.index; i <  $scope.atto.allegati.length; i++){
					     $scope.atto.allegati[i].ordineInclusione = Number(i +1);
					   }
					 }
        		  };
        		  
        		  $scope.allegatoAnnullaEdit = function(allegato){
        			  allegato.edit = false;
        			  $scope.allegatoEdit = {};
        			  $scope.allegatoEdit.edit=false;
        			  angular.copy($scope.backupAllegato, allegato);
        		  };
        			
        		  $scope.deleteAllegato = function (allegato) {
        			  $scope.allegatoDelete = null;
        			  $timeout(function () {
        				  $scope.allegatoDelete = allegato;
        				  $('#deleteAllegatoConfirmation').modal('show');
        			  }, 800);
        		  };
        			
        		  $scope.confirmDeleteAllegato = function (id) {
        			  DocumentoInformatico.delete({id: id}, function () {
        				  for(var i = 0; i<$scope.atto.allegati.length; i++){
        					  if($scope.atto.allegati[i].id == id){
        						  $scope.atto.allegati.splice(i, 1);
        					  }
        				  }
        				  $('#deleteAllegatoConfirmation').modal('hide');
        			  });
        		  };
        		  
        		  $scope.deleteAllegatoOmissis = function (allegato) {
        			var allegatoOmissis = allegato.fileomissis;
        			//Call the service to delete the omissis file
        			Atto.deleteAllegatoOmissis({
        				id: $scope.atto.id,
        				action: 'allegato',
        				idAllegato: allegato.id,
        				actionAllegato: 'omissis',
        				idOmissis: allegato.fileomissis.id
        			}, function(){
        				allegato.fileomissis = null;
        				$scope.verificaAllegatiRiservato();
        			});
        		  };
        			
        				$scope.doUploadDoc = function(mappaDocumenti, restUrl, tipoDocumenti){
        					var deferred = $q.defer();
        					$scope.uploadok = true;
        					var file;
        					if(tipoDocumenti==undefined || tipoDocumenti != "documenti_trasparenza"){
        						file = mappaDocumenti.get(restUrl);
        					}else{
        						file = mappaDocumenti.get(restUrl).file;
        					}
        					$log.debug('Uploading file', file);
        					if(file.size && file.size > $rootScope.ngfMaxSize){
        						var rejectedFiles = [$scope.registerAccount.moduloregistrazione];
        						$rootScope.$broadcast('rejectedFilesEvent', [], {}, rejectedFiles, 'application/*');
        						return;
      	    	    	    }
        					
        				    Upload.upload({
        				      url: restUrl,
        				      headers : {
        				        'Authorization': 'Bearer '+ $rootScope.accessToken
        				      },
        				      file: file
        				    }).progress(function (evt) {
        				    	$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
        				    }).success(function (data, status, headers, config) {
        				    	if(tipoDocumenti != undefined && tipoDocumenti == "documenti_trasparenza"){
        				    		$scope.valoriSchedeDati.elementiSchede[mappaDocumenti.get(restUrl).schedaId][mappaDocumenti.get(restUrl).elementoIndex][mappaDocumenti.get(restUrl).schemaDatoId] = data;
        				    	}
        				    	$scope.uploadok = false;
        				    	deferred.resolve();
        				    });
        				    return deferred.promise;
        				}
        				 
        				$scope.uploadDocumenti = function(mappaDocumenti, tipoDocumenti){
        					var arrayOfFunction = [];
        					if(mappaDocumenti.size == 0){
        						var deferred = $q.defer();
        						deferred.resolve();
        						arrayOfFunction.push(deferred.promise);
        					}else{
        						mappaDocumenti.forEach(function (file, restUrl) {
        							arrayOfFunction.push($scope.doUploadDoc(mappaDocumenti, restUrl, tipoDocumenti));
        						});
        					}
        					return $q.all(arrayOfFunction);
        				}
        	
        	//trasparenza
			$scope.addSchedaTrasparenza = function (valoriSchedeDati, scheda) {
			  var schedaId = new Number(valoriSchedeDati.activeTab);
			  /*
			  var nuovaScheda = angular.copy(scheda);
			  var elementiSchede = valoriSchedeDati.elementiSchede[scheda.id];
			  nuovaScheda.id = nuovaScheda.id + 1,
			  valoriSchedeDati.elementiSchede[nuovaScheda.id] = angular.copy(elementiSchede);
			  schedaId = scheda.id;
			  $scope.schede.push(nuovaScheda);
			  $log.log('schede:', $scope.schede);
			  */
			  valoriSchedeDati.elementiSchede[schedaId].push({});
			  $log.log('valoriSchedeDati', valoriSchedeDati);
			};
			
			/**
			 * Remove a scheda trasparenza.
			 * @param valoriSchedeDati
			 * @param index The index to remove from the array.
			 */
			$scope.removeSchedaTrasparenza = function (valoriSchedeDati, index) {
			  var schedaId = new Number(valoriSchedeDati.activeTab);
			  valoriSchedeDati.elementiSchede[schedaId].splice(index, 1);
			  $log.log('valoriSchedeDati', valoriSchedeDati);
			};

				$scope.salvaSchedaTrasparenza = function (valoriSchedeDati,scheda) {
				   Atto.schedatrasparenza({id:$scope.atto.id},valoriSchedeDati,function(result){
					   $scope.atto.valoriSchedeDati = result.valoriSchedeDati;
					   ngToast.create(  { className: 'success', content: 'Scheda  '+ scheda.etichetta +' salvata con successo' } );
				   } );
				};

				$scope.downloadTrasparenzaTmp = function(fileElement){
					download(fileElement.base64, fileElement.nomeFile, fileElement.type);
				};

				$scope.fileDropperTrasparenza = function (files,event,rejectedFiles,schedaId, schemaDatoId, elemento, elementoIndex) {
				  if(event.type !== "click"){
					  if(files!=undefined && files[0]!=undefined){
						  $scope.documentiTrasparenzaDaCaricare.set('api/attos/'+$scope.atto.id +'/trasparenza/allegato/'+schemaDatoId+'/'+elementoIndex, {"file": files[0], "schedaId":schedaId, "schemaDatoId":schemaDatoId, "elementoIndex":elementoIndex});
						  elemento[schemaDatoId] = {};
						  elemento[schemaDatoId].nomeFile = files[0].name;
						  elemento[schemaDatoId].createdBy = $rootScope.account.login;
						  elemento[schemaDatoId].temp = true;
						  elemento[schemaDatoId].type = files[0].type;
					  }else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
						  $rootScope.showMessage({title:'Attenzione', okButton:true, body:"La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB"});
						  /*alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");*/
	        		  }
				  }
				};
				
				$scope.removeAllegatoTrasparenza = function (schemaDatoId, elemento, elementoIndex) {
					$scope.documentiTrasparenzaDaCaricare.delete('api/attos/'+$scope.atto.id +'/trasparenza/allegato/'+schemaDatoId+'/'+elementoIndex);
					elemento[schemaDatoId]=null;
				};
    		
				$scope.ricaricaSchedeTrasparenza = function(){
					$log.debug("valoriSchedeDati Ricarica:");
					$log.debug( $scope.valoriSchedeDati );
					$scope.caricaSchedeTrasparenza($scope.valoriSchedeDati);
				}

				$scope.caricaSchedeTrasparenza = function (valoriSchedeDati) {
				  $log.debug("valoriSchedeDati:");
				  $log.debug( valoriSchedeDati );

				  if(angular.isDefined($scope.atto)){
					  valoriSchedeDati.attoId = $scope.atto.id;
				  }
				  
				  if($scope.atto != undefined && $scope.atto.macroCategoriaObbligoDl33 != undefined && $scope.atto.macroCategoriaObbligoDl33 != null){
				    valoriSchedeDati.macroId = $scope.atto.macroCategoriaObbligoDl33.id;  
				  } 
				  else valoriSchedeDati.macroId = null;
				  
				  if($scope.atto != undefined && $scope.atto.categoriaObbligoDl33!=undefined && $scope.atto.categoriaObbligoDl33.id!=null){
				    valoriSchedeDati.categoriaId = $scope.atto.categoriaObbligoDl33.id;
				  }
				  else valoriSchedeDati.categoriaId = null;
				  
				  if($scope.atto != undefined && $scope.atto.obbligoDl33!=undefined && $scope.atto.obbligoDl33.id!=null && $scope.atto.obbligoDl33.attivo==true){
				    valoriSchedeDati.obbligoId = $scope.atto.obbligoDl33.id;
				  }
				  else valoriSchedeDati.obbligoId = null;

				  if(valoriSchedeDati.macroId !== null && valoriSchedeDati.categoriaId!==null && valoriSchedeDati.obbligoId!=null){
					  $scope.schede = $scope.macros[valoriSchedeDati.macroId].categorie[valoriSchedeDati.categoriaId].obblighi[valoriSchedeDati.obbligoId].schedas;
				  }
				  else $scope.schede = null;
				  
				  if(angular.isDefined($scope.schede) && $scope.schede !== null && $scope.schede.length > 0){
				    valoriSchedeDati.elementiSchede ={};

				    for (var i = 0; i<$scope.schede.length ; i++) {
				       var scheda = $scope.schede[i];
				       valoriSchedeDati.elementiSchede[scheda.id] = [];

				        if(valoriSchedeDati.elementiSchede[ scheda.id].length === 0){
				           var elementi = {};
				           valoriSchedeDati.elementiSchede[ scheda.id ].push( elementi );
				        }
				    };

				    var scheda = $scope.schede[0];
				    valoriSchedeDati.activeTab = scheda.id + '' ;
				    $scope.caricaPopolaSchedeTrasparenza(valoriSchedeDati);
				    
				  }
				  else {
					  valoriSchedeDati.activeTab = null;
				  }
				  

				};

				$scope.caricaPopolaSchedeTrasparenza = function (valoriSchedeDati) {
					 Atto.caricaschedatrasparenza({id: $scope.atto.id},function(dtoResult){
					    $log.debug(dtoResult);
					    angular.forEach(dtoResult.elementiSchede, function(value, key) {
					      
					      valoriSchedeDati.elementiSchede[key]=[];
					      for (var i = 0; i<value.length ; i++) {
					        var elemento = value[i];
					        $log.debug(elemento)
					        valoriSchedeDati.elementiSchede[key].push(elemento);
					       }
					    });
					    
					  });
					};
    		
    		//presa in carico
    		$scope.callPrendiIncaricoTask  = function (indexSezione) {
    		    $scope.taskLoading = true;
    		    Lavorazione.tasksprendicarico({'taskBpmId':$scope.taskBpmId, 'attoId':$scope.atto.id}, function(data){
    		      ngToast.create(  { className: 'success', content:  'Task '+ $scope.taskBpmId + ' preso in carico con successo.'  } );
    		      $scope.taskLoading = false;
    		      if ($scope.taskBpm) {
    		       	$scope.taskBpm.idAssegnatario = $scope.profiloattivo.utente.username + $scope.bpmSeparator.BPM_USERNAME_SEPARATOR + $scope.profiloattivo.id;
    		      }
    		      $scope.load( $scope.atto.id ,indexSezione, $scope.taskBpmId);
				  $rootScope.$emit("loadDettaglioTask");
    		    });
    		}; 
    		
    		/**
    		 * Set the contenteditable attribute.
    		 */
    		$scope.setContentEditable = function (tabName) {
    			if($scope.solaLettura){
    				$(".note-editable").attr("contenteditable","false");
    				$scope.infoAnagContDisabled = true;
    			}else if(tabName !=undefined && tabName == 'informazioniAnagraficoContabili'){
    				if($scope.atto.tipoIter!=undefined && $scope.atto.tipoIter!=null && $scope.atto.tipoIter.codice == $rootScope.ITER_SENZA_VERIFICA_CONTABILE && $scope.atto.tipoAdempimento!=undefined && $scope.atto.tipoAdempimento!=null && $scope.atto.tipoAdempimento.generazioneSchedaAnagraficoContabile === false){
    					$scope.atto.informazioniAnagraficoContabili.testo = "";
    					$(".note-editable").attr("contenteditable","false");
    					$scope.infoAnagContDisabled = true;
    				}else{
    					$(".note-editable").attr("contenteditable","true");
    					$scope.infoAnagContDisabled = false;
    				}
    			}else{
    				$(".note-editable").attr("contenteditable","true");
    				$scope.infoAnagContDisabled = false;
    			}
    		};
    		
    		/**
    		 * Init the tooltip.
    		 */
    		$scope.initTooltip = function () {
    			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
    		};

    		$scope.sfogliaTitoli = function() {
    			$scope.history = [];
    			$scope.voce = null;
    			$scope.historyFascicoli = [];
    			$scope.fascicolo = null;
    			$scope.voceFascicolo = null;
    			
    			$scope.aggregati = [];
    			
    			if(!$scope.listTitolario) {
    				$scope.waitDiogeneConnection = true;
    				Diogene.titolari({username: $scope.diogeneUsername}, function(result) {
    					$scope.waitDiogeneConnection = false;
    					$scope.listTitolario = result.filter( function(titoli) {
    						return titoli.stato == 'A'; 
    					});
    				}, function(error) {
    					$scope.waitDiogeneConnection = false;
    					$scope.result = error;			
    				});		
    			} else {
    				$scope.titolario = null;
    				$scope.vociTitolario = null;
    			}
    		};

    		$scope.sfogliaVociTitolario = function(titolario, voce) {
    			
    			var idVoce = !voce ? null : voce.idVoce;		
    			
    			$scope.waitDiogeneConnection = true;
    			Diogene.vociTitolario({username: $scope.diogeneUsername, idTitolario: titolario.idTitolario, idVoce: idVoce}, function(result) {
    				$scope.waitDiogeneConnection = false;
    				$scope.vociTitolario = result;		
    				$scope.titolario = titolario;

    				$scope.historyFascicoli = [];
    				$scope.fascicolo = null;
    				$scope.voceFascicolo = null;
    				$scope.aggregati = [];
    				
    				if(voce) {
    					for(var i = 0; i < $scope.history.length; i++) {
    					    if ($scope.history[i].idVoce === voce.idVoce) {
    					        $scope.history = $scope.history.slice(0, i);
    					        break;
    					    }
    					}
    					$scope.history.push(voce);	
    					$scope.voce = voce;
    				} else {
    					$scope.history = [];
    					$scope.voce = null;
    				}
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    				$scope.result = error;			
    			});		
    		};
    		
    		$scope.sfogliaDefault = function() {
    			$scope.waitDiogeneConnection = true;
    			$scope.creaAggregato = false;
    			
    			Diogene.collocazioneBase({username: $scope.diogeneUsername}, function(preferiti) {
    				$scope.waitDiogeneConnection = false;
    				$scope.selezionaAggregato = true;
    				$scope.historyFascicoli = [];
    				$scope.fascicolo = null;
    				$scope.aggregati = preferiti;
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    				$scope.result = error;
    				//$scope.selezionaAggregato = false;
    			});		
    		};


    		$scope.listaFascicoli = function(titolario, voce) {
    			
    			$scope.waitDiogeneConnection = true;
    			Diogene.esploraTitolario({username: $scope.diogeneUsername, idTitolario: titolario.idTitolario, idVoce: voce.idVoce}, function(fascicoli) {
    				$scope.waitDiogeneConnection = false;
    				
    				$scope.historyFascicoli = [];
    				$scope.fascicolo = null;
    				
    				$scope.aggregati = [];
    				$scope.voceFascicolo = voce;
    				
    				$scope.aggregati = fascicoli;
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    				$scope.result = error;			
    			});		
    		};

    		$scope.contenutoFascicolo = function(fascicolo) {
    			
    			$scope.waitDiogeneConnection = true;
    			Diogene.contenutoAggregato({username: $scope.diogeneUsername, idAggregato: fascicolo.idAggregato}, function(fascicoli) {
    				$scope.waitDiogeneConnection = false;		
    				$scope.aggregati = [];
    				
    				for(var i = 0; i < $scope.historyFascicoli.length; i++) {
    				    if ($scope.historyFascicoli[i].idAggregato === fascicolo.idAggregato) {
    				    	$scope.historyFascicoli = $scope.historyFascicoli.slice(0, i);
    				        break;
    				    }
    				}
    				
    				$scope.historyFascicoli.push(fascicolo);	
    				$scope.fascicolo = fascicolo;
    				
    				$scope.aggregati = fascicoli;
    				$scope.creaAggregato = true;
    				$scope.selezionaAggregato = true;
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    				$scope.creaAggregato = false;
    				//$scope.selezionaAggregato = false;
    				$scope.result = error;			
    			});		
    		};
    		
    		$scope.selezionaPreferito = function(fascicolo) {
    			$scope.atto.fascicoloDiogene = fascicolo;
    			$scope.atto.fascicoloDiogeneSelection = 'id: '+fascicolo.idFascicolo + ' - denominazione: ' + fascicolo.nomeAggregato;
    		}
    		
    		$scope.selezionaFascicolo = function(fascicolo) {
    			$scope.atto.fascicoloDiogene = {
    					owner: $scope.diogeneUsername,
    					idFascicolo: fascicolo.idAggregato,
    					nomeAggregato: fascicolo.nomeAggregato ? fascicolo.nomeAggregato : fascicolo.nome,
    					idVoce: 32,//voce.idVoce,
    					idTitolario: 12//voce.idTitolario
    			};
    			$scope.atto.fascicoloDiogeneSelection = 'id: '+$scope.atto.fascicoloDiogene.idFascicolo + ' - denominazione: ' + $scope.atto.fascicoloDiogene.nomeAggregato;
    		};

    		$scope.creaFascicolo = function () {
    			var tipologia = parseInt($scope.nuovoFascicolo.numerazione.tipologiaNumerazione);
    			$scope.nuovoFascicolo['numerazione'].tipologiaNumerazione=tipologia;
    			$scope.nuovoFascicolo['idTitolario']= 12;//$scope.voceFascicolo.idTitolario;
    			$scope.nuovoFascicolo['idVoce']= 32;//$scope.voceFascicolo.idVoce;
    			$scope.nuovoFascicolo['owner']= $scope.diogeneUsername;
    			
    			$scope.waitDiogeneConnection = true;
    			Diogene.generaCollocazione($scope.nuovoFascicolo, function(result) {
    				
    				if($scope.nuovoFascicolo.idFascicolo) {
    					
    					Diogene.contenutoAggregato({username: $scope.diogeneUsername, idAggregato: $scope.nuovoFascicolo.idFascicolo}, function(fascicoli) {
    						$scope.waitDiogeneConnection = false;
    						$scope.aggregati = fascicoli;
    					}, function(error) {
    						$scope.waitDiogeneConnection = false;
    						$scope.result = error;			
    					});	

    				} else {
    					
    					Diogene.esploraTitolario({username: $scope.diogeneUsername, idTitolario: $scope.nuovoFascicolo.idTitolario, idVoce: $scope.nuovoFascicolo.idVoce}, function(fascicoli) {
    						$scope.waitDiogeneConnection = false;
    						$scope.aggregati = fascicoli;
    					}, function(error) {
    						$scope.waitDiogeneConnection = false;
    						$scope.result = error;			
    					});	
    					
    				}
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    			});	
    			
    			$('#creaFascicoloModal').modal('hide');
    		};

    		$scope.initNuovoFascicolo = function (idFascicolo) {
    			$scope.nuovoFascicolo = {};
    			$scope.nuovoFascicolo['idFascicolo']=idFascicolo;
    			$scope.nuovoFascicolo['numerazione'] = {'tipologiaNumerazione':'1'};
    		};



    		$scope.loginRup = function (login) {
    			$('#loginRupModal').modal('hide');
    			$scope.diogeneConnection = null;
    			$scope.waitDiogeneConnection = true;
    			
    			Diogene.init(login, function(result) {
    				$scope.waitDiogeneConnection = false;
    				$scope.diogeneConnection = result.rupUser!=null;
    				
    				if($scope.diogeneConnection){
    					$scope.diogeneUsername = result.rupUser;
    					Diogene.preferiti({username: $scope.diogeneUsername}, function(result) {
    						$scope.diogenePreferiti = result;
    					});
    					//$scope.sfogliaTitoli();
    					$scope.sfogliaDefault();
    				}
    				
    			}, function(error) {
    				$scope.waitDiogeneConnection = false;
    				$scope.diogeneConnection = false;
    				$scope.result = error;			
    			});
    		};
    		
    		
    		
    		/* 
			 * In attico i codici non corrispondono
			 */
    		$scope.isProvvedimentoAD = function(){
//    			if ($scope.atto.tipoAtto.codice == 'DIR' &&
//    				$scope.atto.stato == SedutaGiuntaConstants.statiAtto.provvedimentoEsecutivo)
//    				return true;
//    			else
    				return false;
    		};
    		
    		$scope.isProvvedimentoDG = function(){
//    			if ($scope.atto.tipoAtto.codice == 'DEL' &&
//    				$scope.atto.stato == SedutaGiuntaConstants.statiAtto.provvedimentoEsecutivo &&
//    				$scope.atto.esito != null && $scope.atto.esito == 'adottato')
//    				return true;
//    			else
    				return false;
    		};

    		$scope.isProvvedimentoDL = function(){
//    			if (($scope.atto.tipoAtto.codice == 'DDL' || $scope.atto.tipoAtto.codice == 'SDL') &&
//    				$scope.atto.stato == SedutaGiuntaConstants.statiAtto.provvedimentoEsecutivo &&
//    				$scope.atto.esito != null && $scope.atto.esito == 'approvato')
//    				return true;
//    			else
    				return false;
    		};


    		$scope.isProvvedimentoRepertoriato = function(){
//    			return ($scope.isProvvedimentoAD() || $scope.isProvvedimentoDL() || $scope.isProvvedimentoDG());
    			return false;
    		};
    		
    }]);
