'use strict';

angular.module('cifra2gestattiApp')
.controller('TaskDesktopController', function ($scope, localStorageService,ngToast,SedutaGiuntaConstants,RoleCodes,BpmSeparator, sharedSedutaFactory, $window, $timeout, $state,TaskDesktop,
		$log,Atto, Aoo,ParseLinks, Profilo, $rootScope,ProfiloAccount, ConfigurationParams,TipoMateria,OrdineGiorno,ArgumentsOdgService,SedutaGiunta,ModelloHtml,Esito, TipoAtto, Delega, $filter, Ruolo, TypeFirmaMassiva, Utente, Lavorazione) {
	
	$scope.taskDesktops = [];
	$scope.taskLoading = false;
	$scope.taskDesktopsByDelegante = [];
	$scope.page = 1;
	$scope.tasktype = 'carico';
	$scope.taskDesktops.allItemsSelected = false;
	$scope.taskDesktopsByDelegante.allItemsSelected = false;
	$scope.taskSearch = {};
    $scope.tempSearch = {};
    $scope.loading = false;
    $scope.taskManaging = {};
    $scope.incarico = {};
    $scope.profiliRiassegnazione = [];
    $scope.profiloRiassegnazione = {};
    $scope.codaRiassegnazione = {};
    $scope.qualificaRiassegna = {};
    $scope.tipoattos = [];
    $scope.deleganti = [];
    $scope.listaLavorazioni = [];
    $scope.profilosSottoscrittoriOdg = [];
    $scope.statiDocumentiSeduta = [];
    $scope.attoAppoggio = {};
    $scope.abilitaRiassegnazione=false;
    
    $scope.documentiDaFirmare = [];
    $scope.documentiDaGenerareFirmare = [];
    $scope.utenti = [];
    
    if(!$scope.utenti || $scope.utenti.length == 0){
    	Utente.getAllBasic({}, function(result){
    		$scope.utenti = result;
    	});
    };
    
    $scope.openSeduta = function(seduta){
		if(seduta.primaConvocazioneFine){
    		$state.go('sedutaGiuntaConsolidatiDetail', {
				id:seduta.id
			});
    	} else  $state.go('sedutaGiuntaDetail', {
    		id:seduta.id
		});
	};
    
    $scope.setDefaultUfficioFilter = function(forceReset){
    	if(!$scope.tempSearch){
    		searchObj = {};
    	}
    	if(forceReset || !$scope.tempSearch.ufficioFilter){
    		for(var i = 0; i < $scope.ufficioFilterValues.length; i++){
    			if($scope.ufficioFilterValues[i].defaultValue){
    				$scope.tempSearch.ufficioFilter = $scope.ufficioFilterValues[i].key;
    				break;
    			}
    		}
    		
    	}
    };
    
    $scope.setDefaultLavRagioneriaFilter = function(forceReset){
    	if(!$scope.tempSearch){
    		searchObj = {};
    	}
    	if(forceReset || !$scope.tempSearch.lavRagioneriaFilter){
	    	for(var i = 0; i < $scope.lavRagioneriaValues.length; i++){
				if($scope.lavRagioneriaValues[i].defaultValue){
					$scope.tempSearch.lavRagioneriaFilter = $scope.lavRagioneriaValues[i].key;
					break;
				}
	    	}
    	}
    };

    $scope.ufficioFilterValues = [
    		{
    			'key': 'all',
    			'descrizione':'Nessun filtro',
    			'defaultValue': true
    		},
    		{
    			'key': 'myown',
    			'descrizione':'Proprio ufficio',
    			'defaultValue': false
    		},
    		{
    			'key': 'others',
    			'descrizione':'Uffici diversi dal proprio',
    			'defaultValue': false
    		},
    		{
    			'key': 'notexpected',
    			'descrizione':'Non previsto',
    			'defaultValue': false
    		}
    ];
    
    $scope.lavRagioneriaValues = [
			{
				'key': 'all',
				'descrizione':'Nessun filtro',
				'defaultValue': true
			},
			{
				'key': 'eq1',
				'descrizione':'Si',
				'defaultValue': false
			},
			{
				'key': 'gt1',
				'descrizione':'No',
				'defaultValue': false
			}
	];
    
    $scope.aoos = [];
    $scope.selectedAoo = {};
    Aoo.queryRicorsiva(
    	{ id:$rootScope.profiloattivo.aoo.id }, function(result) {
    	if(result && result.length > 0 && $scope.aoos.length < 1){
    		var aoo = null;
    		for(var i = 0; i<result.length; i++){
    			aoo = result[i];
    			$scope.aoos.push({id:aoo.id, codice:aoo.codice, descrizione:aoo.descrizione});
    		}
    	}
    });
    
    $scope.descriptionRoleReassignee = "";
    $scope.codeRoleReassignee = "";
    $scope.setRuoloDescription = function(bpmGroupName){
    	if($scope.codeRoleReassignee != $scope.onlyRoles(bpmGroupName)){
	    	$scope.codeRoleReassignee = $scope.onlyRoles(bpmGroupName);
	    	Ruolo.getDescriptionByCode({codice: $scope.codeRoleReassignee}, function(data){
	    		$scope.descriptionRoleReassignee = data.descrizione;
	    	}, function (error){
	    		$scope.descriptionRoleReassignee = '-';
	    	});
    	}
    };
    
    $scope.bpmSeparator = BpmSeparator;
    
    Delega.getDelegantiByDelegatoProfiloId({idDelegato:$rootScope.profiloattivo.id}, function(result){
    	$scope.deleganti = result;
    });
	
    TipoAtto.query({}, function(result){
    	$scope.tipoattos = result;
    });
    
    SedutaGiunta.statidocumenti({}, function(result){
    	$scope.statiDocumentiSeduta = result;
    });
    
    TaskDesktop.listaLavorazioni({}, function(result){
    	$scope.listaLavorazioni = result;
    });
    
    $scope.carichiDiLavoroClick = function () {
		$scope.page = 1;
		$scope.carichiDiLavoroClicked = true;
	};
	
	$scope.nonCarichiDiLavoroClick = function () {
		$scope.page = 1;
		$scope.carichiDiLavoroClicked = false;
	};
    
    $scope.getDescrizioneTipoattoByCodice = function(codice){
    	var ris = "";
    	if($scope.tipoattos && $scope.tipoattos.length && $scope.tipoattos.length > 0){
    		for(var i = 0; i < $scope.tipoattos.length; i++){
    			if($scope.tipoattos[i].codice == codice){
    				ris = $scope.tipoattos[i].descrizione;
    				break;
    			}
    		}
    	}
    	return ris;
    };
    
    $scope.sedutaConstants = SedutaGiuntaConstants;
    $scope.sedutaGiuntas = [];
    $scope.proposteInSeduta = [];
    $scope.attiPostSeduta = [];
    $scope.openTabActivities = function(tabName) {
        var i;
        var x = document.getElementsByClassName("activitiesTab");
        for (i = 0; i < x.length; i++) {
        	x[i].classList.remove("active");
            x[i].style.display = "none"; 
        }
        document.getElementById(tabName).style.display = "block"; 
        document.getElementById(tabName).classList.add("active");
    }
    
    $scope.statiSedutaGiunta = [
    	SedutaGiuntaConstants.statiSeduta.odgBaseInPredisposizione.label,
    	SedutaGiuntaConstants.statiSeduta.odgBaseConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.odgSuppletivoConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.odgFuoriSaccoConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.odlBaseInPredisposizione.label,
    	SedutaGiuntaConstants.statiSeduta.odlBaseConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.odlSuppletivoConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.odlFuoriSaccoConsolidato.label,
    	SedutaGiuntaConstants.statiSeduta.sedutaConclusaRegistrazioneEsiti.label,
    	SedutaGiuntaConstants.statiSeduta.sedutaConclusaEsitiConfermati.label,
    	SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.label,
    	SedutaGiuntaConstants.statiSeduta.sedutaConsolidata.label,
    	SedutaGiuntaConstants.statiSeduta.sedutaVerbalizzata.label
    ];
    
    $scope.tipiSedutaGiunta = [
    	{"id":1, "value":"Ordinaria"},
    	{"id":2, "value":"Straordinaria"}
    ];
    $scope.tipiOdg = [
    	{"id":1, "value":"Base"},
    	{"id":3, "value":"Suppletivo"},
    	{"id":4, "value":"Fuorisacco"}
    ];
    $scope.lovSiNo = [
    	{"id":true, "value":"SI"},
    	{"id":false, "value":"NO"}
    ];
    $scope.esiti = [];
	Esito.query(function(result){
		$scope.esiti = result;
		$log.debug("esiti:",result);
	});
	$scope.getEsitoLabel = function(id) {
		for(var i= 0; i<$scope.esiti.length; i++) {
			if($scope.esiti[i].id == id){
				return $scope.esiti[i].label;
			}
		}
	};
	Atto.caricastati(function(collection){
		$scope.statiProposta = collection;
	});
    
    $scope.clearRicerca = function(){
    	$scope.page = 1;
    	$scope.taskSearch = {};
    	$scope.tempSearch = {};
    };
    
    $scope.resetRicerca = function(){
    	$scope.page = 1;
    	$scope.taskSearch = {};
    	$scope.tempSearch = {};
    	
    	if($scope.tasktype == 'odg-giunta' || $scope.tasktype == 'odg-consiglio'){
    		$scope.searchAtto();
    	}
    	else if($scope.tasktype == 'sedute-giunta' || $scope.tasktype == 'sedute-consiglio'){
    		$scope.searchSeduta();
    	}
    	else if($scope.tasktype == 'propInSeduta-giunta' || $scope.tasktype == 'propInSeduta-consiglio'){
    		$scope.searchAttiOdgs();
    	}
    	else if($scope.tasktype == 'postSeduta-giunta' || $scope.tasktype == 'postSeduta-consiglio'){
    		$scope.searchAttoPostSeduta();
    	}
    	//else if($scope.tasktype == 'attiInCoordinamentoTesto-giunta' || $scope.tasktype == 'attiInCoordinamentoTesto-consiglio'){
    		//$scope.searchAttiOdgsCoordinamentoTesto();
    	//}
    	else
    		$scope.loadAll();
	};
	
	$scope.treeOptions = {
        accept: function(sourceNode, destNodes, destIndex) {
            return false;  
        },
        dropped: function(event) {
           return false;
	    } 
	   };
    
    $scope.ricerca = function(){
    	$scope.page = 1;
    	/*copio tempSearch e suoi elementi in taskSearch*/
    	$scope.taskSearch = jQuery.extend(true, {}, $scope.tempSearch);
    	
    	$scope.loadAll($scope.tempSearch);
	};
	
	$scope.groupVisibility = {};

	$scope.showHide = function(ufficioId) {
        $scope.groupVisibility[ufficioId] = !$scope.groupVisibility[ufficioId];
	};
	
	$scope.groupSearch = {};
	$scope.groups = {};
	$scope.linksGroup = [];
	$scope.groupsPage = [];
	
	$scope.ricercaGroup = function(){
    	$scope.groupSearch= jQuery.extend(true, {}, $scope.tempSearch);
    	
    	$scope.loadRagioneria($scope.tempSearch);
	};
	
	$scope.loadPageGroup = function(ufficioId, page){
		$scope.groupsPage[ufficioId] = page;
		$scope.loadGroup(ufficioId);
	};

	$scope.loadGroup = function(ufficioId, searchObject){
		$scope.loading = true;
		$scope.groups[ufficioId].page.content = [];
		
		var filterObject = {page: $scope.groupsPage[ufficioId], per_page: 10,tasktype: $scope.tasktype};
		if($rootScope.profiloattivo && $rootScope.profiloattivo.id == 0) {
			filterObject.userId = $rootScope.profiloattivo.utente;
		}
		
		var filterParams = $scope.groupSearch;
		if (searchObject!=undefined && searchObject!=null) {
			filterParams = searchObject;
		}
		if(!filterParams){
			filterParams = {};
		}
		filterParams.idUfficioGiacenza = ufficioId;
		$scope.checkImportiEU(filterParams);
			
		TaskDesktop.search(filterObject, filterParams, function(result, headers) {
			$scope.linksGroup[ufficioId] = ParseLinks.parse(headers('link'));
			$scope.groups[ufficioId].page.content = result;
			$scope.loading = false;
		});
	};
	
	$scope.ordinamentoRagioneria = function(ordinamento){
		
		if($scope.groupSearch.ordinamento == ordinamento){
			if($scope.groupSearch.tipoOrinamento == "asc"){
				$scope.groupSearch.tipoOrinamento = "desc";
			}else{
				$scope.groupSearch.tipoOrinamento = "asc";
			}
		}else{	
			$scope.groupSearch.tipoOrinamento = "asc"; 
		}
		$scope.groupSearch.ordinamento = ordinamento;
		$scope.loadRagioneria($scope.groupSearch);
	};

	$scope.resetRicercaGroup = function(){
    	$scope.groupSearch = {};
    	$scope.tempSearch = {};
    	$scope.loadRagioneria();
    	$scope.linksGroup = [];
		$scope.groupsPage = [];
	};
	
	$scope.loadRagioneria = function(searchObject){
		
		if(searchObject!=undefined && searchObject!=null){
			localStorageService.set('_taskDesktop_taskSearch', searchObject);
		}
		else {
			localStorageService.set('_taskDesktop_taskSearch', $scope.groupSearch);
		}
    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
		
		$scope.groups = {};
		$scope.loading = true;
		var params = {per_page: 10, tasktype: $scope.tasktype};
		if($rootScope.profiloattivo && $rootScope.profiloattivo.id == 0) {
			params.userId = $rootScope.profiloattivo.utente;
		}
		$scope.setDefaultUfficioFilter();
		var filterParams = $scope.groupSearch;
		if (searchObject!=undefined && searchObject!=null) {
			filterParams = searchObject;
		}
		
		if ($scope.selectedAoo && $scope.selectedAoo.value && $scope.selectedAoo.value.id) {
			filterParams.filterAooId = $scope.selectedAoo.value.id;
		}
		else {
			delete filterParams.filterAooId;
		}
		
		$scope.checkImportiEU(filterParams);
		
		TaskDesktop.searchGroups(params, filterParams, function(result, headers) {
			for(var i = 0; i<result.length; i++){
				$scope.linksGroup[result[i].ufficio.id] = ParseLinks.parse(headers('link' + result[i].ufficio.id));
				$scope.groups[result[i].ufficio.id] = result[i];
				$scope.groupsPage[result[i].ufficio.id] = 1;
				$scope.groupVisibility[result[i].ufficio.id] = false;
			}
			$scope.loading = false;
		});
    };
	
    $scope.ricercaAtti = function() {
    	$scope.page = 1;
    	$scope.searchAtto();
	};
	
	$scope.ricercaAttiPostSeduta = function() {
    	$scope.page = 1;
    	$scope.searchAttoPostSeduta();
	};
	
	$scope.ricercaSedute = function() {
		$scope.page = 1;
		$scope.searchSeduta();
	};
	
	$scope.ricercaAttiOdg = function() {
		$scope.page = 1;
		$scope.searchAttiOdgs();
	};
	
	$scope.isDirigente = false;
	
	if($rootScope.profiloattivo && $rootScope.profiloattivo.id != 0){
		Profilo.checkIfIsDirigente($rootScope.profiloattivo,function(result){
			$scope.isDirigente = result.isDirigente;
		});		
	}	
	$scope.unclaim = function(task){
		$scope.taskManaging = task;
		//if($scope.isDirigente){
		$scope.setRuoloDescription(task.taskBpm.candidateGroups);
		$('#unclameConfirm').modal('show');
		//}
	};
	
	$scope.dounclaim = function(taskId, currentAssignement){
//		if($scope.isDirigente){
			let currentAssigneeProfId = null;
			if(currentAssignement){
				try{
					currentAssigneeProfId = currentAssignement.split('_!U#_')[1].split('_')[0];
				}catch(err){}
			}
			TaskDesktop.unclaim({taskId:taskId, verifyOriginalProfId:currentAssigneeProfId}, function(result){
				$('#unclameConfirm').modal('hide');
				$scope.taskManaging = {};
				$scope.loadAll();
			});
//		}
	};
	$scope.prelevaTaskAttivi = function(task) {
		Atto.getAttoConTaskAttivi({id: task.taskBpm.businessKey}, function(result) {
	        $scope.attoAppoggio = result;
	    });
		
	}
	$scope.cambiaCoda = function(task) {
		$scope.incarico = null;
		$scope.taskManaging = task;
		Lavorazione.cambioCoda({taskBpmId: task.taskBpm.id, idAtto: task.taskBpm.businessKey, idAooProfilo: $rootScope.profiloattivo.aoo.id}, function(data) {

			let idConfIncarico = 0;
			let pos = task.taskBpm.candidateGroups.indexOf($scope.bpmSeparator.BPM_INCARICO_SEPARATOR);
			if (pos > -1) {
				idConfIncarico = task.taskBpm.candidateGroups.substring(pos+$scope.bpmSeparator.BPM_INCARICO_SEPARATOR.length,task.taskBpm.candidateGroups.length);
			}

			let incarico;

			if(data.listAssegnazioneIncaricoDettaglio!=null && data.listAssegnazioneIncaricoDettaglio.length > 0){
				incarico = data.listAssegnazioneIncaricoDettaglio[0];
				
			}

			if(incarico && incarico.listAoo && incarico.listAoo.length > 0){
				$scope.incarico = incarico;
			}
		});
		$('#cambiaCodaModal').modal('show');
	}
	
	$scope.riassegna = function(task) {
		$scope.profiloRiassegnazione = null;
		$scope.qualificaRiassegna = {};
		if(!$scope.attoAppoggio){
			$scope.prelevaTaskAttivi(task);
		}
		//if ($scope.tasktype == 'attiInCaricoAssessori' && $scope.attoAppoggio && $scope.attoAppoggio.taskAttivi && $scope.attoAppoggio.taskAttivi.length>0) {
		if ($scope.tasktype == 'attiInCaricoAssessori') {
			$scope.profiliRiassegnazione = [];
			$scope.taskManaging = task;
			
			
			
			Profilo.getByRole({ruolo:'ROLE_COMPONENTE_GIUNTA'}, function(profili) {
				
				if(profili && profili.length && profili.length > 0){
	        		for(var i = 0; i < profili.length; i++){
	        			if(!profili[i].validita || !profili[i].validita.validoal){
	        				$scope.profiliRiassegnazione.push(profili[i]);
	        			}
	        		}
	        	}
				
				
//				if($scope.attoAppoggio!=null && $scope.attoAppoggio.taskAttivi!=null && $scope.attoAppoggio.taskAttivi.length>0){
//					for(var i = 0; i<profili.length; i++){
//						var componenteGiunta = profili[i];
//						var nominativoComponenteGiunta=componenteGiunta.utente.nome + " " + componenteGiunta.utente.cognome;
//						var aggiungi = true;
//						for(var j = 0; j<$scope.attoAppoggio.taskAttivi.length; j++){
//							var taskAttivo = $scope.attoAppoggio.taskAttivi[j];
//							if(taskAttivo && taskAttivo.descrizione == 'Visto Assessore' && taskAttivo.nominativo == nominativoComponenteGiunta){
//								aggiungi = false;
//								break;
//							}
//						}
//						if(aggiungi){
//							$scope.profiliRiassegnazione.push(componenteGiunta);
//						}
//					}
//				}else {
//				}
				
					
				
				
				
				
	        });
			$('#riassegnazioneModal').modal('show');
		}
		else if($scope.isDirigente && task.taskBpm.idAssegnatario!=null){
			$scope.profiliRiassegnazione = [];
			$scope.taskManaging = task;
			
			var username = $scope.onlyUsername(task.taskBpm.idAssegnatario);
			var profiloId = task.taskBpm.idAssegnatario.split(username+$scope.bpmSeparator.BPM_USERNAME_SEPARATOR)[1];
			
			var nQual = profiloId.indexOf($scope.bpmSeparator.BPM_INCARICO_SEPARATOR);
			if (nQual > -1) {
				profiloId = profiloId.substring(0, nQual);
			}
			
			Profilo.getProfilosRiassegnazioneTaskConfTaskBased({taskId: task.taskBpm.id}, function(profili){
				$scope.profiliRiassegnazione = profili;
				$('#riassegnazioneModal').modal('show');
			});
			
//			Profilo.getProfilosRiassegnazione({profiloDisabilitazioneId:profiloId}, function(profili){
//				$scope.profiliRiassegnazione = profili;
//				$('#riassegnazioneModal').modal('show');
//        	});
		}else if($scope.isDirigente && task.taskBpm.idAssegnatario==null && task.taskBpm.candidateGroups!=null){
			$scope.profiliRiassegnazione = [];
			$scope.taskManaging = task;
			
			Profilo.getByCandidateGroup({candidateGroup:task.taskBpm.candidateGroups}, function(profili) {
				
				if(profili && profili.length && profili.length > 0){
	        		for(var i = 0; i < profili.length; i++){
	        			if(!profili[i].validita || !profili[i].validita.validoal){
	        				$scope.profiliRiassegnazione.push(profili[i]);
	        			}
	        		}
	        	}
			});
			$('#riassegnazioneModal').modal('show');
		}
		
	};
	$scope.mostraRiassegna = function(task) {
		$('#riassegnazioneModal').modal('show');
	}
	
	$scope.setRiassegnazione = function(profilo){
		
		var settaRiassegnazione = true;
		if($scope.tasktype=='attiInCaricoAssessori' && $scope.attoAppoggio!=null && $scope.attoAppoggio.taskAttivi!=null && $scope.attoAppoggio.taskAttivi.length>0){
			var nominativoComponenteGiunta=profilo.utente.nome + " " + profilo.utente.cognome;
			var aggiungi = true;
			for(var j = 0; j<$scope.attoAppoggio.taskAttivi.length; j++){
				var taskAttivo = $scope.attoAppoggio.taskAttivi[j];
				//if(taskAttivo && taskAttivo.descrizione == 'Visto Assessore' && taskAttivo.nominativo == nominativoComponenteGiunta){
				if(taskAttivo && taskAttivo.nominativo == nominativoComponenteGiunta){
					settaRiassegnazione = false;
					break;
				}
			}
		}
		//imposto questa variabile a true per disabilitare il controllo client side in quanto è stato replicato server side.
		settaRiassegnazione = true;
		if(settaRiassegnazione==true){
			$scope.profiloRiassegnazione = profilo;
			$scope.qualificaRiassegna = {};
			$scope.abilitaRiassegnazione=true;
		}else
		{
			alert("L'assessore selezionato ha già in carico l'atto selezionato");
			$scope.abilitaRiassegnazione=false;
		}
		
    };
    
    $scope.setQualificaRiassegnazione = function(qualifica){
		$scope.qualificaRiassegna = qualifica;
    };
    
    $scope.setRiassegnazioneCoda = function(codaRiassegnazioneSel){
		$scope.codaRiassegnazione = codaRiassegnazioneSel;
    };
	
	$scope.effettuaRiassegnazione = function(){
		if( ($scope.isDirigente || $scope.tasktype == 'attiInCaricoAssessori') && 
			$scope.profiloRiassegnazione && $scope.taskManaging && 
			$scope.taskManaging.taskBpm && $scope.taskManaging.taskBpm.id && 
			$scope.qualificaRiassegna && $scope.qualificaRiassegna.id){
				TaskDesktop.reassignee({
					taskId: $scope.taskManaging.taskBpm.id, 
					profiloIdDirigente: $rootScope.profiloattivo.id, 
					attoId: $scope.taskManaging.taskBpm.businessKey,
					profiloIdAssegna: $scope.profiloRiassegnazione.id,
					idQualificaAssegna: $scope.qualificaRiassegna.id,
					tasktype:$scope.tasktype
				}, function(result){
					$('#riassegnazioneModal').modal('hide');
					
					if(!result.warnCodice){
		                $scope.loadAll();
		    		}else{
		    			$('#warnEffettuaRiassegnazione').modal('show');
		    		}
				}, function(result){
					if(result && result.data && result.data.error){
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:result.data.error});
					}else{
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Si \u00E8 verificato un errore, si prega di riprovare.'});
					}				
				});
				$scope.profiloRiassegnazione = {};
				$scope.qualificaRiassegna = {};
			
		}
	};
	
	$scope.effettuaCambiaCoda = function(){
		if( $scope.isDirigente && 
			$scope.codaRiassegnazione && $scope.taskManaging && 
			$scope.taskManaging.taskBpm && $scope.taskManaging.taskBpm.id){
				TaskDesktop.cambiaCoda({
					taskId: $scope.taskManaging.taskBpm.id, 
					profiloIdDirigente: $rootScope.profiloattivo.id, 
					attoId: $scope.taskManaging.taskBpm.businessKey,
					aooIdAssegna: $scope.codaRiassegnazione
				}, function(result){
				
				$('#cambiaCodaModal').modal('hide');
				
				if(!result.warnCodice){
                    $scope.loadAll();
        		}else{
        			$('#warnEffettuaCambioCoda').modal('show');
        		}
			}, function(result){
				if(result && result.data && result.data.error){
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:result.data.error});
				}else{
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Si \u00E8 verificato un errore, si prega di riprovare.'});
				}				
			});
			
			
			$scope.codaRiassegnazione = {};
		}
	};
	
	// Query per cercare i sottoscrittori possibili di un odg, ovvero sia i presidenti che i segretari...
	var ruoliSer = "";
	if ($scope.tasktype == 'odg-giunta') {
		ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA + ',' + RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA;
	}
	else {
		ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO + ',' + RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO;
	}
	
		
	Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
		$scope.profilosSottoscrittoriOdg = result;
	});
	
	
	$scope.checkImportiEU = function(searchObject) {
		
		if (searchObject.differenzaImportoA && 
				!$scope.checkNumberImporto(searchObject.differenzaImportoA)) {
			delete searchObject.differenzaImportoA;
		}
		if (searchObject.differenzaImportoDa && 
				!$scope.checkNumberImporto(searchObject.differenzaImportoDa)) {
			delete searchObject.differenzaImportoDa;
		}
		
		if (searchObject.importoEntrataA && 
				!$scope.checkNumberImporto(searchObject.importoEntrataA)) {
			delete searchObject.importoEntrataA;
		}
		if (searchObject.importoEntrataDa && 
				!$scope.checkNumberImporto(searchObject.importoEntrataDa)) {
			delete searchObject.importoEntrataDa;
		}
		
		if (searchObject.importoUscitaA && 
				!$scope.checkNumberImporto(searchObject.importoUscitaA)) {
			delete searchObject.importoUscitaA;
		}
		if (searchObject.importoUscitaDa && 
				!$scope.checkNumberImporto(searchObject.importoUscitaDa)) {
			delete searchObject.importoUscitaDa;
		}
	}
	
	$scope.checkImpersonificaFirma = function () {
		if ($rootScope.profiloOrigine && $rootScope.profiloOrigine.id > 0) {
			return false;
		}
		return true;
	};
	
	$scope.checkNumberImporto = function(valNumber) {
		var cleanVal = valNumber.replace(/\./g, "").replace(/,/g, ".");
		return !isNaN(cleanVal - parseFloat(cleanVal));
	}
	
	$scope.loadAll = function(searchObject) {
		
		ConfigurationParams.get().then(function (data) {
	    	$rootScope.configurationParams.msgManutenzione = data.msgManutenzione;
	    });
		
		
		var profiloId = $scope.tasktype.split("_")[1];
		var tasktype = $scope.tasktype;
		if(profiloId==undefined) {
			profiloId=null;
		}
		else {
			tasktype = $scope.tasktype.split("_")[0];
		}
		
		if(searchObject!=undefined && searchObject!=null){
			localStorageService.set('_taskDesktop_taskSearch', searchObject);
		}
		else {
			localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
		}
    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
		
		$scope.loading = true;
		$scope.taskDesktops = [];
		
		var filterObject = {page: $scope.page, per_page: 10,tasktype: tasktype, profiloId: profiloId};
		if($rootScope.profiloattivo && $rootScope.profiloattivo.id == 0) {
			filterObject.userId = $rootScope.profiloattivo.utente;
		}
		$scope.setDefaultUfficioFilter();
		$scope.setDefaultLavRagioneriaFilter();
		if (tasktype != 'arrivo' && tasktype != 'carico') {
			$scope.setDefaultLavRagioneriaFilter();
		}
		
		var filterParams = $scope.taskSearch;
		if (searchObject!=undefined && searchObject!=null) {
			filterParams = searchObject;
		}
		
		if ($scope.selectedAoo && $scope.selectedAoo.value && $scope.selectedAoo.value.id) {
			filterParams.filterAooId = $scope.selectedAoo.value.id;
		}
		else {
			delete filterParams.filterAooId;
		}
		
		$scope.checkImportiEU(filterParams);
			
		if(tasktype == "postSeduta-consiglio"|| tasktype == "postSeduta-giunta"){
			TaskDesktop.searchPostSeduta({page: $scope.page, per_page: 10, organo: $scope.tasktype == 'postSeduta-giunta' ? 'G' : 'C'},
					 $scope.tempSearch, function(result, headers) {	
				$scope.loading = false;
		        $scope.attos = result;
		        $scope.links = ParseLinks.parse(headers('link'));
		        $scope.totalResultAtti=headers('x-total-count') ;
		        if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
					localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
				}
		        else{
					localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
				}
		    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
		    });		
		}else if(tasktype == "monitoraggioGroup"){
			$scope.loadRagioneria();
		}
		else if(profiloId==null){
			TaskDesktop.search(filterObject, filterParams, function(result, headers) {
				$scope.links = ParseLinks.parse(headers('link'));
				$scope.taskDesktops = result;
				$scope.loading = false;
				$log.debug("Tasks:",$scope.taskDesktops);
			});
		}
		else {
			TaskDesktop.searchByDelegante(filterObject, filterParams, function(result, headers) {
				$scope.links = ParseLinks.parse(headers('link'));
				$scope.taskDesktopsByDelegante = result;
				$scope.loading = false;
				$log.debug("Tasks:",$scope.taskDesktopsByDelegante);
			});
		}
	};
	
	$scope.loadPage = function(page) {
		$scope.page = page;
		$scope.loadAll();
	};
	
	$scope.attos = [];
	$scope.attos.allItemsSelected = false;
	
	var tipiAttoIds = [];
	for(var i = 0; angular.isDefined($rootScope.profiloattivo) && angular.isDefined($rootScope.profiloattivo.tipiAtto) && i<$rootScope.profiloattivo.tipiAtto.length; i++){
		tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
	}
	$scope.tipiAttoIds = tipiAttoIds;
	$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:tipiAttoIds} ;
	$scope.criteria.page = $scope.page;
	$scope.criteria.per_page = 10;
	$scope.criteria.ordinamento = "codiceCifra";
	$scope.criteria.tipoOrinamento = "desc";
	
	$scope.isAttoInseribileInOdg = function (entity){
		return entity.stato != SedutaGiuntaConstants.statiAtto.propostaSospesa;
	}

	$scope.isAttoInseribileInOdgNotDpc = function (entity){
		return entity.stato != SedutaGiuntaConstants.statiAtto.propostaSospesa && entity.tipoAtto.codice.toLowerCase() != 'dpc';
	}
	
	$scope.esportaRicercaOdgXls = function(){
		var json = jQuery.extend(true, {}, $scope.tempSearch);
		var res;
		if(json){
			res = JSON.stringify(json);
		}else{
			res = "{}";
		}
		
		window.open($rootScope.buildDownloadUrl('api/taskBpms/searchOdgXls') + '&searchStr=' + window.encodeURIComponent(res) + '&access_token='+ $rootScope.access_token,'_blank');
	}
	
	$scope.isAttoSospeso = function (entity){
		return entity.stato == SedutaGiuntaConstants.statiAtto.propostaSospesa;
	}
	
	$scope.isDecorsiTermini = function (sedutaGiunta){
		var retValue = false;
		
		var now = new Date();
		var dataConvocazione;
		
		if (sedutaGiunta.secondaConvocazioneInizio != null && sedutaGiunta.secondaConvocazioneInizio != "")
			dataConvocazione = new Date(sedutaGiunta.secondaConvocazioneInizio);
		else 
			dataConvocazione = new Date(sedutaGiunta.primaConvocazioneInizio);
		
		if ((now > dataConvocazione) && sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA)
			retValue = true;
		
		return retValue;
	}
	
	$scope.isModificatoInGiunta = function (attoOdg){
		var retValue = false;
		
		if (attoOdg.atto.isModificatoInGiunta != null)
			retValue = attoOdg.atto.isModificatoInGiunta;
		
		return retValue;
	}
	
	$scope.getSottoscrittoriAttoOdg = function (attoOdg){
		var sottoscrittori = [];
		
		if(attoOdg.componenti!=undefined && attoOdg.componenti!=null){
			for (var i = 0; i < attoOdg.componenti.length; i++) {
				if (attoOdg.componenti[i].isPresidenteFine || attoOdg.componenti[i].isSegretarioFine)
					sottoscrittori.push(attoOdg.componenti[i].profilo.utente.username);
			}
		}
		
		return sottoscrittori;
	}
	
	$scope.formatDate = function (data) {
		var d = new Date(data.replace('Z',''));
		var dateString = ("0" + d.getDate()).slice(-2) + "-" + 
		                 ("0"+(d.getMonth()+1)).slice(-2) + "-" +
	                     d.getFullYear() + " " + 
	                     ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
		
		return dateString;
	}
	
	$scope.getVariazioneEstremi = function (sedutaGiunta){
		var nuovaData = "";
		if (sedutaGiunta.secondaConvocazioneInizio != null && sedutaGiunta.secondaConvocazioneInizio != ""){
			var primaConvocazione = new Date(sedutaGiunta.primaConvocazioneInizio);
			var secondaConvocazione = new Date(sedutaGiunta.secondaConvocazioneInizio);
			if (primaConvocazione < secondaConvocazione){
				nuovaData = "Posticipata al: " + $filter('date')(sedutaGiunta.secondaConvocazioneInizio, "dd-MM-yyyy HH:mm",'Europe/Berlin');
			}else if (primaConvocazione > secondaConvocazione)
				nuovaData = "Anticipata al: " + $filter('date')(sedutaGiunta.secondaConvocazioneInizio, "dd-MM-yyyy HH:mm",'Europe/Berlin');
		}
		
		var nuovaSede = "";
		if (sedutaGiunta.secondaConvocazioneLuogo != null && 
				sedutaGiunta.secondaConvocazioneLuogo != "" &&
				sedutaGiunta.secondaConvocazioneLuogo.toLowerCase() != sedutaGiunta.luogo.toLowerCase()){
			nuovaSede = "Nuova Sede: " + sedutaGiunta.secondaConvocazioneLuogo;
		} 
		
		var retValue = [];
		if (nuovaData != "")
			retValue.push(nuovaData);
		if (nuovaSede != "")
			retValue.push(nuovaSede);
			
		return retValue;
	}

	$scope.presetParCom = function() {
		if(!($scope.tempSearch)){
			$scope.tempSearch = {};
		}
		$scope.tempSearch.parComNotReq = true;
		$scope.tempSearch.parComAll = true;
		$scope.tempSearch.parComExp = true;
		$scope.tempSearch.parAll = false;
	}

	
	$scope.searchAtto = function() {	
		$scope.loading = true;
		$scope.attos = [];
		
		if($scope.tempSearch==undefined || $scope.tempSearch==null){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		}
		$scope.setDefaultUfficioFilter();
		$scope.setDefaultLavRagioneriaFilter();
		if($scope.tempSearch.tipiAttoIds==undefined || $scope.tempSearch.tipiAttoIds==null){
			$scope.tempSearch.tipiAttoIds = $scope.tipiAttoIds;
		}
		if($scope.tempSearch.tipoOrinamento==undefined || $scope.tempSearch.tipoOrinamento==null){
			$scope.tempSearch.ordinamento = "codiceCifra";
			$scope.tempSearch.tipoOrinamento = "desc";
		}
		$scope.tempSearch.viewtype = 'tutti';
		
		if ($rootScope.profiloattivo.id > 0)
			$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
		
		if(($scope.tasktype == 'odg-giunta') || ($scope.tasktype == 'odg-consiglio')) {
			$scope.tempSearch.ordinamento = "dataAttivita";
			$scope.tempSearch.tipoOrinamento = "asc";
			
			if ($scope.tempSearch.tipoAttoSel !=null){
				$scope.tempSearch.tipoAtto = $scope.tempSearch.tipoAttoSel.codice;
			}
			else {
				$scope.tempSearch.tipoAtto = null;
			}
		}
		
		if($scope.tempSearch.cercaSospesi != null) {
			if($scope.tempSearch.cercaSospesi.id == true) {
				$scope.tempSearch.stato = SedutaGiuntaConstants.statiAtto.propostaSospesa;
			}
			if($scope.tempSearch.cercaSospesi.id == false) {
				$scope.tempSearch.stato = $scope.tasktype == 'odg-giunta' ? SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg : SedutaGiuntaConstants.statiAtto.propostaInseribileInOdl;
			}
		}
		
		TaskDesktop.searchOdg({page: $scope.page, per_page: 10, organo: $scope.tasktype == 'odg-giunta' ? 'G' : 'C'},
				 $scope.tempSearch, function(result, headers) {	
			$scope.loading = false;
	        $scope.attos = result;
	        $log.debug("atto.search:",$scope.attos);
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	        if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
				localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
			}
	        else{
				localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
			}
	    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
	    });		
	};
	
	
	$scope.searchAttoPostSeduta = function() {	
		$scope.loading = true;
		$scope.attos = [];
		
		if($scope.tempSearch==undefined || $scope.tempSearch==null){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		}
		$scope.setDefaultUfficioFilter();
		$scope.setDefaultLavRagioneriaFilter();
		if($scope.tempSearch.tipiAttoIds==undefined || $scope.tempSearch.tipiAttoIds==null){
			$scope.tempSearch.tipiAttoIds = $scope.tipiAttoIds;
		}
		if($scope.tempSearch.tipoOrinamento==undefined || $scope.tempSearch.tipoOrinamento==null){
			$scope.tempSearch.ordinamento = "codiceCifra";
			$scope.tempSearch.tipoOrinamento = "desc";
		}
		$scope.tempSearch.viewtype = 'tutti';
		
		if ($rootScope.profiloattivo.id > 0)
			$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
		
		if(($scope.tasktype == 'postSeduta-consiglio') || ($scope.tasktype == 'postSeduta-giunta')) {
			$scope.tempSearch.ordinamento = "dataAttivita";
			$scope.tempSearch.tipoOrinamento = "asc";
			
			if ($scope.tempSearch.tipoAttoSel !=null){
				$scope.tempSearch.tipoAtto = $scope.tempSearch.tipoAttoSel.codice;
			}
			else {
				$scope.tempSearch.tipoAtto = null;
			}
		}

		TaskDesktop.searchPostSeduta({page: $scope.page, per_page: 10, organo: $scope.tasktype == 'postSeduta-giunta' ? 'G' : 'C'},
				 $scope.tempSearch, function(result, headers) {	
			$scope.loading = false;
	        $scope.attos = result;
	        $log.debug("atto.search:",$scope.attos);
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	        if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
				localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
			}
	        else{
				localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
			}
	    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
	    });		
	};
	
	$scope.isOperatoreOdgGiunta = sharedSedutaFactory.isOperatoreOdgGiunta();
	$scope.isOperatoreOdgConsiglio = sharedSedutaFactory.isOperatoreOdgConsiglio();
	$scope.isSegretarioGiunta = sharedSedutaFactory.isSegretarioGiunta();
	$scope.isSegretarioConsiglio = sharedSedutaFactory.isSegretarioConsiglio();
	$scope.isSegretarioGenerale = sharedSedutaFactory.isSegretarioGenerale();
	$scope.isOperatoreSegreteriaConsiglio = sharedSedutaFactory.isOperatoreSegreteriaConsiglio();
	$scope.isOperatoreSegreteriaGiunta = sharedSedutaFactory.isOperatoreSegreteriaGiunta();
	
	
	$scope.isAbilitatoTabsSedutaGiunta = sharedSedutaFactory.hasRuolo([
	                                                             RoleCodes.ROLE_OPERATORE_ODG_GIUNTA,
	                                                             RoleCodes.ROLE_OPERATORE_RESOCONTO_GIUNTA,
	                                                             RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA,
	                                                             RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA,
	                                                             RoleCodes.ROLE_SINDACO]);
	
	$scope.isAbilitatoTabsPostSedutaGiunta = sharedSedutaFactory.hasRuolo([
        RoleCodes.ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_G]);
	
	$scope.isAbilitatoPostTabsSedutaConsiglio = sharedSedutaFactory.hasRuolo([
        RoleCodes.ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_C]);
	
	$scope.isAbilitatoTabsSedutaConsiglio = sharedSedutaFactory.hasRuolo([
														        RoleCodes.ROLE_OPERATORE_ODG_CONSIGLIO,
														        RoleCodes.ROLE_OPERATORE_RESOCONTO_CONSIGLIO,
														        RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO,
														        RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO,
														        RoleCodes.ROLE_SINDACO]);
														        
	$scope.isAbilitatoTabsAttiCoordinamentoTestoConsiglio = sharedSedutaFactory.hasRuolo([
														        RoleCodes.ROLE_OPERATORE_SEGRETERIA_CONSIGLIO,
														        RoleCodes.ROLE_SEGRETARIO_GENERALE]);
														        
	$scope.isAbilitatoTabsAttiCoordinamentoTestoGiunta = sharedSedutaFactory.hasRuolo([
														        RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA,
														        RoleCodes.ROLE_SEGRETARIO_GENERALE]);
	
	// TODO: differenziare in base a tab ???
	$scope.isSottoscrittorePossibileSeduta = (
			 sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA]) || 
			(sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA]) && !sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_SINDACO])) ||
			 sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO]) || 
			(sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO]) && !sharedSedutaFactory.hasRuolo([RoleCodes.ROLE_SINDACO])));
	
	// TODO: Chiedere conferma se vanno disabilitati in caso di selezione TUTTI I Profili...
	$scope.isAbilitatoTabsSedutaGiunta = $scope.isAbilitatoTabsSedutaGiunta && ($rootScope.profiloattivo.id != 0);
	$scope.isAbilitatoTabsSedutaConsiglio = $scope.isAbilitatoTabsSedutaConsiglio && ($rootScope.profiloattivo.id != 0);
	$scope.isAbilitatoTabsPostSedutaGiunta = $scope.isAbilitatoTabsPostSedutaGiunta && ($rootScope.profiloattivo.id != 0);
	$scope.isAbilitatoPostTabsSedutaConsiglio = $scope.isAbilitatoPostTabsSedutaConsiglio && ($rootScope.profiloattivo.id != 0);
	$scope.isSottoscrittorePossibileSeduta = $scope.isSottoscrittorePossibileSeduta && ($rootScope.profiloattivo.id != 0);
	$scope.isAbilitatoTabsAttiCoordinamentoTestoConsiglio = $scope.isAbilitatoTabsAttiCoordinamentoTestoConsiglio && ($rootScope.profiloattivo.id != 0);
	$scope.isAbilitatoTabsAttiCoordinamentoTestoGiunta = $scope.isAbilitatoTabsAttiCoordinamentoTestoGiunta && ($rootScope.profiloattivo.id != 0);
	
	$scope.getOdgBase = function(seduta) {
		return sharedSedutaFactory.getOdgBase(seduta);
	}
	
	/**
	 * 
	 */
	$scope.searchSeduta = function() {
     	$log.debug("Search Seduta");
     	$scope.loading = true;
     	
     	if($scope.tempSearch==undefined || $scope.tempSearch==null){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		}
     	$scope.setDefaultUfficioFilter();
     	// if ($scope.isSottoscrittorePossibileSeduta)
     	//	$scope.tempSearch.sottoscrittoreProfiloId = $rootScope.profiloattivo.id;
     	// else
     		$scope.tempSearch.sottoscrittoreProfiloId = null;
     	
     	$log.debug("sedutaGiunta search dto:",$scope.tempSearch);
     	     	
     	SedutaGiunta.search( 
     			{ 
     				page: $scope.page, 
     				per_page: $scope.criteria.per_page 
     			},
     			{ 
     				stato : $scope.tempSearch.stato,
     				numOdg : $scope.tempSearch.numOdg,
     				tipoSeduta : $scope.tempSearch.tipoSeduta!=null ? $scope.tempSearch.tipoSeduta.id : null,
     				presidente : $scope.tempSearch.presidente,
     				estremiVariati : $scope.tempSearch.estremiVariati!=null ? $scope.tempSearch.estremiVariati.id : null,
     				decorsiTermini : $scope.tempSearch.decorsiTermini!=null ? $scope.tempSearch.decorsiTermini.id : null,
     				primaConvInizioDa : $scope.tempSearch.primaConvInizioDa,
     				primaConvInizioA : $scope.tempSearch.primaConvInizioA,
     				sottoscrittoreProfiloId : $scope.tempSearch.sottoscrittoreProfiloId,
     				statoDocumento: $scope.tempSearch.statodoc,
     				sottoscrittoreDocumento: $scope.tempSearch.sottoscrittoredoc,
     				organo: $scope.tasktype == 'sedute-giunta' ? 'G' : 'C',
     				ecludiInfoAggiuntive: true
     			},
     			function(result, headers) {
     				$scope.loading = false;
     				$scope.sedutaGiuntas = result;
     				$log.debug("sedutaGiuntas:",$scope.sedutaGiuntas);
     				$scope.links = ParseLinks.parse(headers('link'));
     				$scope.totalResultSedute=headers('x-total-count') ;
     				if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
     					localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
     				}else{
     					localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
     				}
     		    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
     			});
     	
    };
    
    /**
	 * 
	 */
	$scope.searchAttiOdgs = function() {
		$log.debug("Search Atti Odgs");
     	$scope.loading = true;
     	
     	if($scope.tempSearch==undefined || $scope.tempSearch==null){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		}
     	$scope.setDefaultUfficioFilter();
     	// if ($scope.isSottoscrittorePossibileSeduta)
     	//	$scope.tempSearch.sottoscrittoreProfiloId = $rootScope.profiloattivo.id;
     	// else
     		$scope.tempSearch.sottoscrittoreProfiloId = null;
     	
     	$log.debug("AttiOdg search dto:",$scope.tempSearch);
     	
     	OrdineGiorno.searchAttiOdgs( 
     			{ 
     				page: $scope.page, 
     				per_page: $scope.criteria.per_page 
     			},
     			{ 
     				statoProposta : $scope.tempSearch.statoProposta,
     				codiceCifra : $scope.tempSearch.codiceCifra,
     				numSeduta : $scope.tempSearch.numSeduta,
     				tipoSeduta : $scope.tempSearch.tipoSeduta!=null ? $scope.tempSearch.tipoSeduta.id : null,
     				organo: $scope.tasktype == 'propInSeduta-giunta' ? 'G' : 'C',
     				tipoOdg : $scope.tempSearch.tipoOdg!=null ? $scope.tempSearch.tipoOdg.id : null,
     				oggetto : $scope.tempSearch.oggetto,
     				esito : $scope.tempSearch.esito!=null ? $scope.tempSearch.esito.id : null,
     				estremiVariati : $scope.tempSearch.estremiVariati!=null ? $scope.tempSearch.estremiVariati.id : null,
     				modificatoInGiunta : $scope.tempSearch.modificatoInGiunta!=null ? $scope.tempSearch.modificatoInGiunta.id : null,
     				dataOraSedutaDa : $scope.tempSearch.dataOraSedutaDa,
     				dataOraSedutaA : $scope.tempSearch.dataOraSedutaA,
     				dataEsecutivitaDa : $scope.tempSearch.dataEsecutivitaDa,
     				dataOraSedutaA : $scope.tempSearch.dataEsecutivitaA,
     				dataEsecutivitaA : $scope.tempSearch.dataAdozione,
     				numeroAdozione : $scope.tempSearch.numeroAdozione,
     				sottoscrittoreProfiloId : $scope.tempSearch.sottoscrittoreProfiloId,
     				sottoscrittoreNome : $scope.tempSearch.sottoscrittoreNome
     			},
     			function(result, headers) {
     				$scope.loading = false;
     				$scope.proposteInSeduta = result;
     				$log.debug("proposteInSeduta:",$scope.proposteInSeduta);
     				$scope.links = ParseLinks.parse(headers('link'));
     				$scope.totalResultSedute=headers('x-total-count') ;
     				if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
     					localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
     				}else{
     					localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
     				}
     		    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
     			});
     	
    };
    
    $scope.searchAttiOdgsCoordinamentoTesto = function() {
		$log.debug("Search Atti Odgs Coordinameto Testo");
     	$scope.loading = true;
     	
     	if($scope.tempSearch==undefined || $scope.tempSearch==null){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		}
     	
     	// if ($scope.isSottoscrittorePossibileSeduta)
     	//	$scope.tempSearch.sottoscrittoreProfiloId = $rootScope.profiloattivo.id;
     	//else
     		$scope.tempSearch.sottoscrittoreProfiloId = null;
     	
     	$log.debug("AttiOdg search dto Coordinameto Testo:",$scope.tempSearch);
     	
     	OrdineGiorno.searchAttiOdgs( 
     			{ 
     				page: $scope.page, 
     				per_page: $scope.criteria.per_page 
     			},
     			{ 
     				statoProposta : $scope.tempSearch.statoProposta,
     				codiceCifra : $scope.tempSearch.codiceCifra,
     				numSeduta : $scope.tempSearch.numSeduta,
     				tipoSeduta : $scope.tempSearch.tipoSeduta!=null ? $scope.tempSearch.tipoSeduta.id : null,
     				organo: $scope.tasktype == 'propInSeduta-giunta' ? 'G' : 'C',
     				tipoOdg : $scope.tempSearch.tipoOdg!=null ? $scope.tempSearch.tipoOdg.id : null,
     				oggetto : $scope.tempSearch.oggetto,
     				esito : $scope.tempSearch.esito!=null ? $scope.tempSearch.esito.id : null,
     				estremiVariati : $scope.tempSearch.estremiVariati!=null ? $scope.tempSearch.estremiVariati.id : null,
     				modificatoInGiunta : $scope.tempSearch.modificatoInGiunta!=null ? $scope.tempSearch.modificatoInGiunta.id : null,
     				dataOraSedutaDa : $scope.tempSearch.dataOraSedutaDa,
     				dataOraSedutaA : $scope.tempSearch.dataOraSedutaA,
     				dataAdozione : $scope.tempSearch.dataAdozione,
     				numeroAdozione : $scope.tempSearch.numeroAdozione,
     				sottoscrittoreProfiloId : $scope.tempSearch.sottoscrittoreProfiloId,
     				sottoscrittoreNome : $scope.tempSearch.sottoscrittoreNome
     			},
     			function(result, headers) {
     				$scope.loading = false;
     				$scope.proposteInSeduta = result;
     				$log.debug("proposteInSeduta:",$scope.proposteInSeduta);
     				$scope.links = ParseLinks.parse(headers('link'));
     				$scope.totalResultSedute=headers('x-total-count') ;
     				if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
     					localStorageService.set('_taskDesktop_taskSearch', $scope.tempSearch);
     				}else{
     					localStorageService.set('_taskDesktop_taskSearch', $scope.taskSearch);
     				}
     		    	localStorageService.set('_taskDesktop_tasktype', $scope.tasktype);
     			});
     	
    };
    
	$scope.modelloHtmls = ModelloHtml.query();
	$scope.modelloHtmlId = null;
	$scope.modelloHtmlsOdg = [];
	$scope.selectIncludiSospeseList = [];

	$scope.hideMascheraWorkflow = function(){
		$scope.decisioneCorrente = null;
		$scope.taskLoading = true;
		$('#mascheraWorkflow').modal('hide');
	}
	
	$scope.filterModelli = function(array){
		var ret = [];
		if($scope.modelloHtmls && $scope.modelloHtmls.$resolved && $scope.modelloHtmls.length > 0 && array && array.length > 0){
			for(var i = 0; i < $scope.modelloHtmls.length; i++){
				for(var j = 0; j < array.length; j++){
					if($scope.modelloHtmls[i].id == array[j]){
						ret.push($scope.modelloHtmls[i]);
						break;
					}
				}
			}
		}
		return ret;
	};
	
	$scope.sedutas = [];
	$scope.searchOdg = function() {	
		//cerco gli odg non ancora consolidati, cioè quelli appartenenti ad una seduta
		//e che hanno stato proposta inserita all'odg
		// $scope.criteria = {stato:SedutaGiuntaConstants.statiSeduta.sedutaInPredisposizione} ;
		
		$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		$scope.tempSearch.page = 1;
		$scope.tempSearch.per_page = 20;
		
		SedutaGiunta.search( $scope.tempSearch,  function(result, headers) {
	        $scope.sedutas = result;
	        $log.debug("atto.sedutas:",$scope.sedutas);
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	    });
	}

	$scope.loadPageAtto = function(page) {
		$scope.page = page;
		$scope.searchAtto();
	};
	$scope.loadPageAttoPostSeduta = function(page) {
		$scope.page = page;
		$scope.searchAttoPostSeduta();
	};
	$scope.loadPageSeduta = function(page) {
        $scope.page = page;
        $scope.searchSeduta();
    };
    $scope.loadPageAttoOdg = function(page) {
        $scope.page = page;
        $scope.searchAttiOdgs();
    };

    if($.inArray('_taskDesktop_taskSearch', localStorageService.keys()) >= 0 && $.inArray('_taskDesktop_tasktype', localStorageService.keys()) >= 0){
    	$scope.tempSearch = localStorageService.get('_taskDesktop_taskSearch');
    	$scope.tasktype = localStorageService.get('_taskDesktop_tasktype');
    	if($scope.tasktype == 'monitoraggio' || $scope.tasktype == 'monitoraggioGroup'){
    		$scope.carichiDiLavoroClick();
    	}else{
    		$scope.nonCarichiDiLavoroClick();
    	}
    	if($scope.tasktype == 'odg-giunta' || $scope.tasktype == 'odg-consiglio'){
    		$scope.searchAtto();
    	}
    	else if($scope.tasktype == 'sedute-giunta' || $scope.tasktype == 'sedute-consiglio') {
    		$scope.searchSeduta();
    	} 
    	else if($scope.tasktype == 'propInSeduta-giunta' || $scope.tasktype == 'propInSeduta-consiglio') {
    		$scope.searchAttiOdgs();
    	}
    	else if($scope.tasktype == 'postSeduta-giunta' || $scope.tasktype == 'postSeduta-consiglio') {
    		$scope.searchAttoPostSeduta();
    	}
    	else if($scope.tasktype.startsWith('attiInRagioneria') || $scope.tasktype == 'monitoraggioGroup') {
    		$scope.loadRagioneria();
    	}
    	//else if($scope.tasktype == 'attiInCoordinamentoTesto-giunta' || $scope.tasktype == 'attiInCoordinamentoTesto-consiglio') {
    		//$scope.searchAttiOdgsCoordinamentoTesto();
    	//}
    	else {
    		$scope.ricerca();
    	}
    	$timeout(function() {
		   angular.element('#navTab_'+$scope.tasktype).triggerHandler('click');
		});
    }else{
    	$scope.loadAll();
    }
    
	$scope.showUpdate = function (id) {
		TaskDesktop.get({id: id}, function(result) {
			$scope.taskDesktop = result;
			$('#saveTaskDesktopModal').modal('show');
		});
	};


	$scope.refresh = function () {
		$scope.loadAll();
		$('#saveTaskDesktopModal').modal('hide');
	};
	
	$scope.operazioneMassiva = function(action) { 
		let idTasks = [];
		for (var i = 0; i < $scope.taskDesktops.length; i++) {
			if ($scope.taskDesktops[i].isChecked) {
				var task =$scope.taskDesktops[i];
				idTasks.push(task.taskBpm.id);
			}
		}
		
		if (idTasks.length < 1) {
			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Occorre selezionare almeno un Atto.'});
			$scope.hideMascheraWorkflow();
			$scope.taskLoading = false;
			$rootScope.$broadcast('globalLoadingEnd');
			return;
		}
		
		var param={};
		param.idTasks=idTasks;
		$scope.taskLoading = true;
		
		if (action == 'visto-massivo') {
			TaskDesktop.vistoMassivo( param, function(data) {
				$scope.taskLoading = false;
				$scope.hideMascheraWorkflow();
				$scope.refresh();
			}, function(error) {
				$scope.taskLoading = false;
				$scope.hideMascheraWorkflow();
				$scope.refresh();
			});
		}
		if (action == 'firma-massiva') {
			if($scope.documentiDaGenerareFirmare && $scope.documentiDaGenerareFirmare.length > 0){
				param.documentiDaGenerareFirmare = [];
				for(var i = 0; i<$scope.documentiDaGenerareFirmare.length; i++){
					param.documentiDaGenerareFirmare.push({
						attoId : $scope.documentiDaGenerareFirmare[i].attoId,
						modelloId : $scope.documentiDaGenerareFirmare[i].modelloId,
						taskId : $scope.documentiDaGenerareFirmare[i].taskId,
						typeOperazione : $scope.documentiDaGenerareFirmare[i].typeOperazione,
						modelliSolaGenerazioneIds : $scope.documentiDaGenerareFirmare[i].modelliSolaGenerazioneIds
					});
				}
			}
			param.codiceFiscale = $scope.profiloattivo.utente.codicefiscale;
			param.password = $scope.dtoFdr.password;
			param.otp = $scope.dtoFdr.otp;
			
			TaskDesktop.firmaMassiva( param, function(data) {
				$scope.taskLoading = false;
				$scope.hideMascheraWorkflow();
				$scope.refresh();
			}, function(error) {
				$scope.taskLoading = false;
				if (error && error.data && error.data.errorMessage && error.data.errorMessage != '') {
					$scope.dtoFdr.password='';
					$scope.dtoFdr.otp='';
					$scope.dtoFdr.errorMessage=error.data.errorMessage;
					$scope.dtoFdr.errorCode=error.data.errorCode;
				}
				else {
					$scope.hideMascheraWorkflow();
					$scope.refresh();
				}
			});
		}
	};
	
	$scope.richiediOtp = function() {
		$scope.dtoFdr.codiceFiscale = $scope.profiloattivo.utente.codicefiscale;
		$scope.dtoFdr.errorMessage='';
		$scope.dtoFdr.errorCode='';
		$scope.dtoFdr.filesId=[];
		
		Atto.sendOTP({id:0},$scope.dtoFdr, function(result) {
			ngToast.create(  { className: 'success', content:  'Richiesta invio OTP effettuata con successo'  } );
		}, function(error) {
			$scope.dtoFdr.password='';
			$scope.dtoFdr.otp='';
			$scope.dtoFdr.errorMessage=error.data.errorMessage;
			$scope.dtoFdr.errorCode=error.data.errorCode;
		});
	}

	$scope.prendiInCaricoTask = function() {
		for (var i = 0; i < $scope.taskDesktops.length; i++) {
			if ($scope.taskDesktops[i].isChecked) {
				var task =$scope.taskDesktops[i]; 
				TaskDesktop.prendiInCaricoTask(task.taskBpm, function(){
					//TaskDesktop.prendiInCaricoTask({prendiInCaricoTask:$scope.taskDesktops[i]}, function() {
						$scope.refresh();
					});
					/*TaskDesktop.save($scope.taskDesktops[i],
    	                    function () {
    	                        $scope.refresh();
    	                    });*/
			}
		}
	};
	
	$scope.prendiInCaricoTaskContent = function(entity) {

				var task =entity; 
				TaskDesktop.prendiInCaricoTask(task.taskBpm, function(){
						$scope.ricercaGroup();
					});


	};
	
	$scope.riassegnazioneTaskMyOwn = function() {
		$rootScope.showMessage({title:'Conferma Operazione', siButton:true, noButton:true, siFunction: function(){
			var done = 0;
			var checked = $scope.taskDesktops.filter((t) => t.isChecked).length;
			for (var i = 0; i < $scope.taskDesktops.length; i++) {
				if ($scope.taskDesktops[i].isChecked) {
					var task =$scope.taskDesktops[i]; 
					TaskDesktop.riassegnazioneTaskMyOwn(task.taskBpm, function(){
						done = done + 1;
						if(done == checked){
							$scope.refresh();
							$('#genericMessage').modal('hide');
						}
					}, function(errore){
						$scope.refresh();
						$('#genericMessage').modal('hide');
					});
				}
			}
		}, noFunction: function(){
    		$('#genericMessage').modal('hide');
    	}, body:'Desideri confermare la presa in carico dell\'operazione?'});
		
	};
	
	$scope.prendiInCaricoTaskDelegante = function() {
		
		for (var i = 0; i < $scope.taskDesktopsByDelegante.length; i++) {
			if ($scope.taskDesktopsByDelegante[i].isChecked) {
				var task =$scope.taskDesktopsByDelegante[i]; 
				TaskDesktop.prendiInCaricoTask({isPresaInCaricoPerDelega:true}, task.taskBpm, function(){
					//TaskDesktop.prendiInCaricoTask({prendiInCaricoTask:$scope.taskDesktops[i]}, function() {
						$scope.refresh();
					});
					/*TaskDesktop.save($scope.taskDesktops[i],
    	                    function () {
    	                        $scope.refresh();
    	                    });*/
			}
		}
	};
	
	/**
	 * funzione che si occupa di ritirareun atto che si trova nello stato Atto Inseribile In Odg
	 */
	$scope.ritiraAttoBtn = function(){
		
		for (var i = 0; i < $scope.attos.length; i++) {
			if ($scope.attos[i].isChecked) {
				var atto =$scope.attos[i];
				atto.note = $scope.motivoSospensione;
				
				if ($scope.tasktype == 'odg-giunta') {
					TaskDesktop.ritiraAttoG(atto, function(data){
						$scope.resetRitiro();
					});
				}
				else {
					TaskDesktop.ritiraAttoC(atto, function(data){
						$scope.resetRitiro();
					});
				}
			}
		}
	};
	
	$scope.resetRitiro = function() {
		$scope.attos=[];
		$scope.attos.allItemsSelected = false;
		ArgumentsOdgService.setArguments([]);
		$scope.motivoSospensione="";
		$scope.searchAtto();
		$('#ritiraAttoModal').modal('hide');
	};
	
	$scope.motivoSospensione="";
	$scope.sospendiAtto = function() {
		
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		
		for (var i = 0; i < $scope.attos.length; i++) {
			if ($scope.attos[i].isChecked) {
				if($scope.motivoSospensione!=null){
					var atto =$scope.attos[i]; 
					$scope.attos[i].stato = SedutaGiuntaConstants.statiAtto.propostaSospesa;
					$scope.attos[i].note = $scope.motivoSospensione;
					ArgumentsOdgService.addArgument($scope.attos[i]); 
		       /* Atto.save({},$scope.attos[i],
		         function (result, headers) {
		          ngToast.create(  { className: 'success', content: decisione.descrizione+' effettuata con successo' } );
		          $scope.refresh();
		        });*/
				
				
				}
			}
		}
		
		OrdineGiorno.suspendAtti(ArgumentsOdgService.getArguments(),function(data){
			$scope.attos=[];
			$scope.attos.allItemsSelected = false;
			ArgumentsOdgService.setArguments([]);
			$scope.motivoSospensione="";
			$scope.searchAtto();
			$('#sospendiAttoModal').modal('hide');
		});
	};
	
	$scope.riattivaAtto = function() {
		
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		
		for (var i = 0; i < $scope.attos.length; i++) {
			if ($scope.attos[i].isChecked) {
				var atto =$scope.attos[i]; 
				// $scope.attos[i].stato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg;
				// $scope.attos[i].note = "";
				ArgumentsOdgService.addArgument($scope.attos[i]); 
		        /*Atto.save({},$scope.attos[i],
		         function (result, headers) {
		          ngToast.create(  { className: 'success', content: decisione.descrizione+' effettuata con successo' } );
		          $scope.refresh();
		        });*/
			}
		}
		OrdineGiorno.activateAtti(ArgumentsOdgService.getArguments(),function(data){
			$scope.attos=[];
			$scope.attos.allItemsSelected = false;
			ArgumentsOdgService.setArguments([]);
		
			$scope.searchAtto();
			$('#sospendiAttoModal').modal('hide');
		});
	};

//	This executes when entity in table is checked
	$scope.selectEntity = function (tipo) {
		// If any entity is not checked, then uncheck the "allItemsSelected" checkbox
		
		if(tipo == 'task'){
			for (var i = 0; i < $scope.taskDesktops.length; i++) {
				if (!$scope.taskDesktops[i].isChecked) {
					$scope.taskDesktops.allItemsSelected = false;
					return;
				}
			}
			//If not the check the "allItemsSelected" checkbox
			$scope.taskDesktops.allItemsSelected = true;
		}
		if(tipo == 'atto'){
			for (var i = 0; i < $scope.attos.length; i++) {
				if (!$scope.attos[i].isChecked) {
					$scope.attos.allItemsSelected = false;
					return;
				}
			}
			//If not the check the "allItemsSelected" checkbox
			$scope.attos.allItemsSelected = true;
		}

		
	};
	
	$scope.selectSelectedEntity = function (entity){
		if($scope.tasktype == "attiInRagioneriaPerArrivo" || $scope.tasktype == 'monitoraggioGroup'){
			for (var i = 0; i < $scope.group.page.content.length; i++) {
				if (entity.taskBpm.id == $scope.group.page.content[i].taskBpm.id) {
					$scope.group.page.content[i].isChecked = true;
					return;
				}
			}
		}else{
			entity.isChecked = true;
		}
		
	}

//	This executes when checkbox in table header is checked
	$scope.selectAll = function (tipo) {
		// Loop through all the entities and set their isChecked property
		if(tipo == 'task'){
			for (var i = 0; i < $scope.taskDesktops.length; i++) {
				$scope.taskDesktops[i].isChecked = $scope.taskDesktops.allItemsSelected;
			}
		}
		if(tipo == 'atto'){
			for (var i = 0; i < $scope.attos.length; i++) {
				$scope.attos[i].isChecked = $scope.attos.allItemsSelected;
			}
		}
	};
	
	/**
	 * Retrieve the number.
	 * @param string The string from which retrieve the number.
	 * @return Returns the number from the given number.
	 */
	$scope.retrieveNumber = function(string){
		if(string){
			return string.match(/\d+/)[0];
		}else{
			return "";
		}
	};
	
	$scope.onlyUsername = function(bpmUserName){
		if(bpmUserName){
			return bpmUserName.split($scope.bpmSeparator.BPM_USERNAME_SEPARATOR)[0];
		}
		
		return "";
	};
	
	$scope.onlyProfiloId = function(bpmUserName){
		if(bpmUserName){
			return bpmUserName.split($scope.bpmSeparator.BPM_USERNAME_SEPARATOR)[1].split($scope.bpmSeparator.BPM_INCARICO_SEPARATOR)[0];
		}
		
		return "";
	};
	
	$scope.onlyAooCode = function(bpmGroupName){
		if(bpmGroupName){
			var splitted = bpmGroupName.split($scope.bpmSeparator.BPM_ROLE_SEPARATOR);
			if (splitted.length > 1) {
				return splitted[1].split($scope.bpmSeparator.BPM_INCARICO_SEPARATOR)[0];
			}
		}
		
		return "";
	};
	
	$scope.onlyRoles = function(bpmGroupName){
		if(bpmGroupName){
			return bpmGroupName.split($scope.bpmSeparator.BPM_ROLE_SEPARATOR)[0];
		}
		
		return "";
	};
	
	$scope.canClaimTask = function(bpmGroupName){
		var taskAoo  = $scope.onlyAooCode(bpmGroupName);
		var taskRole = $scope.onlyRoles(bpmGroupName);
		
		var profAoo = "";
		if ($rootScope.profiloattivo && $rootScope.profiloattivo.aoo) {
			profAoo = $rootScope.profiloattivo.aoo.codice;
		}
		
		if (! (taskAoo && taskRole && profAoo && 
				$rootScope.profiloattivo.grupporuolo && $rootScope.profiloattivo.grupporuolo.hasRuoli &&
				(taskAoo.length>0 && taskRole.length>0 && profAoo.length>0 && profAoo==taskAoo) )) {
			return false;
		}
		
		for(var i = 0; i <  $rootScope.profiloattivo.grupporuolo.hasRuoli.length; i++) {
			if (taskRole === $rootScope.profiloattivo.grupporuolo.hasRuoli[i].codice) {
				return true;
			}
		}
		return false;
	};
	
	$scope.selectProfiloAndGo = function(attoid,task, idAssegnatario){
		var profilo = null;
		
		var idProfilo = $scope.retrieveNumber(idAssegnatario);
		
		angular.forEach($rootScope.activeprofilos, function(campo, key){
			if(campo.id == idProfilo){
				profilo = campo;
			}
		});
		
		ProfiloAccount.setProfilo(profilo);

        TipoMateria.active(function(data){
            ProfiloAccount.setTipoMaterie(data);
        });
        
        $log.debug($rootScope.profiloattivo);
        
        
        
        $log.debug("#/atto/"+attoid);
//        $state.reload("navbar");
//        $state.reload("content");
//        $state.reload("richiestaHDFeedback");
//        $window.location.href = "#/atto/"+atto.id +"/0/"+task;
        
        $state.go("attoDetailSection", {id:attoid,section:0,taskBpmId:task}, {reload: true});
	};
	
	$scope.giacenzaTask = function (dataAssegnata) {
		//Get 1 day in milliseconds
		var one_day=1000*60*60*24;
		// Calculate the difference in milliseconds
		var difference_ms = new Date().getTime() - dataAssegnata;   
		// Convert back to days and return
		return Math.round(difference_ms/one_day);
	};
	
	$scope.callDecisioneSeduta  = function (decisione,seduta) {
		$log.debug("callDecisioneSeduta",decisione);
		$log.debug("seduta:",seduta);
		$scope.taskLoading = false;
		$scope.dtoFdr = {codiceFiscale:'', password:'' , otp: ''};
		switch(decisione.codice){
			case 'seduta-provvisoria-annulla':
				if ($rootScope.profiloattivo.id == 0){
					$log.debug("Profilo attivo : Tutti i Profili, operazione non consentita");
					decisione.codice = 'seduta-errortuttiprofili';
				} else {
					$scope.sedutaGiunta = seduta;
					$scope.sedutaGiunta.sottoscrittoreDocAnnullamento = seduta.presidente;
					$log.debug("seduta:",$scope.sedutaGiunta);
					
					if ('C' == $scope.sedutaGiunta.organo) {
						$scope.modelloDaCodice('annullamento_seduta_consiglio');
					}
					else {
						$scope.modelloDaCodice('annullamento_seduta_giunta');
					}
				}
				break;
			case 'seduta-stampagiacenza':
				var modelliList = [];

				$scope.selectIncludiSospeseList = [{"id":false,"label":"No"},{"id":true,"label":"Si"}];
				$scope.includiSospese = {"id":false, "label":"No"};
				$log.debug("selectIncludiSospeseList:",$scope.selectIncludiSospeseList);

				$scope.modelloDaCodice('giacenza');
				for(var i = 0; i <  $scope.modelloHtmls.length; i++){                
					if($scope.modelloHtmls[i].tipoDocumento.codice == 'giacenza'){
						modelliList.push($scope.modelloHtmls[i]);
					}
				}
				$scope.modelloHtmlsOdg = modelliList;
				if($scope.modelloHtmlsOdg.length == 1){
					$scope.modelloHtmlId=$scope.modelloHtmlsOdg[0];
				}
				break;
				
			case 'firma-massiva':	
				$scope.documentiDaFirmare = [];
				$scope.documentiDaGenerareFirmare = [];
				$scope.nDocDaFirmareResolved = 0;
				$scope.nDocDaFirmare = 0;
				for (var i = 0; i < $scope.taskDesktops.length; i++) {
					if ($scope.taskDesktops[i].isChecked && $scope.taskDesktops[i].taskBpm && $scope.taskDesktops[i].taskBpm.firmaDto && TypeFirmaMassiva.FIRMA.indexOf($scope.taskDesktops[i].taskBpm.firmaDto.type) > -1) {
						$scope.nDocDaFirmare++;
						Atto.elencodocumentidafirmare({taskBpmId: $scope.taskDesktops[i].taskBpm.id}, function(result, headers) {
							  $log.debug("Aggiunga elencodocumentidafirmare taskBpmId.", result);
							  $scope.nDocDaFirmareResolved++;
							  angular.forEach(result, function(documentoPdf, key){
								  $scope.documentiDaFirmare.push(documentoPdf);
							  });
							  
							  $log.debug("documentiDaFirmare MOD:", $scope.documentiDaFirmare);
				         });
					}else if($scope.taskDesktops[i].isChecked && $scope.taskDesktops[i].taskBpm && $scope.taskDesktops[i].taskBpm.firmaDto && TypeFirmaMassiva.GENERA_FIRMA.indexOf($scope.taskDesktops[i].taskBpm.firmaDto.type) > -1){
						$scope.documentiDaGenerareFirmare.push({
							attoId : $scope.taskDesktops[i].taskBpm.businessKey,
							modelloId : ($scope.taskDesktops[i].taskBpm.firmaDto.modelli && $scope.taskDesktops[i].taskBpm.firmaDto.modelli.length == 1 ? $scope.taskDesktops[i].taskBpm.firmaDto.modelli[0] : null),
							modelliDisponibili : $scope.taskDesktops[i].taskBpm.firmaDto.modelli,
							codiceCifra : $scope.taskDesktops[i].taskBpm.codiceCifra,
							oggetto : $scope.taskDesktops[i].taskBpm.oggetto,
							taskId : $scope.taskDesktops[i].taskBpm.id,
							typeOperazione : $scope.taskDesktops[i].taskBpm.firmaDto.type,
							modelliDisponibiliSolaGenerazione: $scope.taskDesktops[i].taskBpm.firmaDto.modelliSolaGenerazione,
							modelliSolaGenerazioneIds : ($scope.taskDesktops[i].taskBpm.firmaDto.modelliSolaGenerazione && $scope.taskDesktops[i].taskBpm.firmaDto.modelliSolaGenerazione.length == 1 ? $scope.taskDesktops[i].taskBpm.firmaDto.modelliSolaGenerazione : [])
						});
					}
				}
				break;
				
			default:
				break;
		}
		
		$scope.decisioneCorrente = decisione;
		$('#mascheraWorkflow').modal('show');
	};
	
	$scope.salvaDecisione = function(decisioneCorrente) {
		$rootScope.$broadcast('globalLoadingStart');
    	switch(decisioneCorrente.codice){
        	case 'seduta-provvisoria-annulla':
        		$scope.annullaSedutaProvvisoria();
        		break;
        	case 'firma-massiva':
        		$scope.operazioneMassiva('firma-massiva');
        		break;
        	case 'visto-massivo':
        		$scope.operazioneMassiva('visto-massivo');
        		break;
        	default:
				break;
    	}
    	
	};
	
	/**
     * annulla seduta giunta - CASISTICA NON PRESENTE IN ATTICO, annullamento solo se provvisoria
     *
    $scope.annullaSedutaGiunta = function(){
    	if($scope.sedutaGiunta != null && $scope.modelloHtmlId != null && $scope.modelloHtmlId.id >= 0){
    		$scope.taskLoading = true;
    		$scope.param = {};
    	    $scope.param.sedutaId = $scope.sedutaGiunta.id;
    	    $scope.param.modelloId = $scope.modelloHtmlId.id;
    	    $scope.param.profiloId=$rootScope.profiloattivo.id;
    	    $scope.param.profiloSottoscrittoreId=$scope.sedutaGiunta.sottoscrittoreDocAnnullamento.id;
    		$log.debug("param",$scope.param);
    		
    		SedutaGiunta.annulla($scope.param, function (result, headers) {
    			$log.debug("Annullato");
    			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$('#mascheraWorkflow').modal('hide');
        		$scope.searchSeduta();
        		$scope.refresh();
            });
    	}
    };
    */
    
    $scope.annullaSedutaProvvisoria = function(){
    	if($scope.sedutaGiunta != null){
    		$scope.taskLoading = true;
    	    $scope.param={};
    		$scope.param.sedutaId=$scope.sedutaGiunta.id;
    		$scope.param.profiloId=$rootScope.profiloattivo.id;
    		$scope.param.profiloSottoscrittoreId=$rootScope.profiloattivo.id;
    		SedutaGiunta.annulla($scope.param, function (result, headers) {
    			ngToast.create(  { className: 'success', content: 'Annullamento effettuato con successo' } );
        		$scope.taskLoading = false;
        		$('#mascheraWorkflow').modal('hide');
        		$scope.searchSeduta();
            });
    	}
    };
    
    
    /**
	 * Prende i modelli in base al codice
	 */
	$scope.modelloDaCodice = function(codice){
		var modelliList = [];
		
		for(var i = 0; i <  $scope.modelloHtmls.length; i++){                
			if($scope.modelloHtmls[i].tipoDocumento.codice == codice){
				modelliList.push($scope.modelloHtmls[i]);
			}
		}
		$scope.modelloHtmlsOdg = modelliList;
		if($scope.modelloHtmlsOdg.length == 1){
			$scope.modelloHtmlId=$scope.modelloHtmlsOdg[0];
		}
	}
	
	$scope.onChangeSelectGiacenza = function(includiSospese){
		$scope.includiSospese = includiSospese;
	};
	
	$scope.initTooltip = function () {
		$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
	};

	$scope.getTooltip = function(item){
		//Tooltip task "paralleli": in "taskAttivi" se nominativo == null e aoo != null, mettere aoo.descrizione
		var textHtml;
		var li = '';
		var infos = $scope.getInfoCommissioni(item);
		if(item != undefined && item.taskAttivi != undefined && item.taskAttivi.length>0){
			for(var i = 0; i < item.taskAttivi.length; i++){
				li += '<li>';
				let nominativoDescrizione = false;
				let nominativo = item.taskAttivi[i].nominativo;
				if(nominativo != null){
					li += '<b>' + nominativo + '</b>';
					nominativoDescrizione = true;
				}else{
					let descrizione = item.taskAttivi[i].aoo != null ? item.taskAttivi[i].aoo.descrizione: '';
					if(descrizione != null){
						li += '<b>' + descrizione + '</b>';
					}
					nominativoDescrizione = true;
				}
				let descrizioneTask = item.taskAttivi[i].descrizione;
				var terminiScaduti = '';
				if(descrizioneTask != null){
					for(var j = 0; j< infos.length; j++){
						var infoAoo = infos[j].aoo;
						var scadenza = null;
						if(item.taskAttivi[i].aoo!= null && infoAoo === item.taskAttivi[i].aoo.descrizione){
							if(infos[j].conf && infos[j].conf.dataManuale && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
								scadenza = new Date(infos[j].conf.dataManuale);
								scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
							}
			    			else if(infos[j].conf && infos[j].conf.dataCreazione && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
			    				scadenza = new Date(infos[j].conf.dataCreazione);
			    				scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
			    			}
							var adesso = new Date();
							if(scadenza){
			    				if(scadenza.getTime() > adesso.getTime()){
			    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(scadenza);
			    				}else{
			    					terminiScaduti += " - Termini scaduti"
			    				}
			    			}
						}
					}
					
					
					if(nominativoDescrizione){
						li += ' (Mancanza ' + descrizioneTask + terminiScaduti +')';
					} else li += 'Mancanza ' + descrizioneTask;

				}
				li += '</li>';
			}
		}
        if(item.pareri && item.pareri.length > 0 ){
            item.pareri.forEach(p => {
                if(p && !p.annullato && p.tipoAzione && p.tipoAzione.codice== 'parere_quartiere_revisori' && !p.parereSintetico){
                	var adesso = new Date();
                	var terminiScaduti = '';
                	if(p.dataScadenza){
	    				if(new Date(p.dataScadenza) > adesso){
	    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(new Date(p.dataScadenza));
	    				}else{
	    					terminiScaduti += " - Termini scaduti"
	    				}
	    			}
                	li += '<li><b>' + p.aoo.descrizione  +'</b><br/>(Mancanza ' + p.tipoAzione.descrizione + terminiScaduti + ')</li>';
                }
            })
        }
		if(li){
			textHtml = '<ul class="taskTooltip">' + li + '</ul>';
		}
		else return null;

		return textHtml;
	}
	
	$scope.getTooltipPost = function(item){
		//Tooltip task "paralleli": in "taskAttivi" se nominativo == null e aoo != null, mettere aoo.descrizione
		var textHtml;
		var li = '';
		var infos = $scope.getInfoCommissioni(item);
		if(item != undefined && item.taskAttivi != undefined && item.taskAttivi.length>0){
			for(var i = 0; i < item.taskAttivi.length; i++){
				li += '<li>';
				let nominativoDescrizione = false;
				let nominativo = item.taskAttivi[i].nominativo;
				if(nominativo != null){
					li += '<b>' + nominativo + '</b>';
					nominativoDescrizione = true;
				}else{
					let descrizione = item.taskAttivi[i].aoo != null ? item.taskAttivi[i].aoo.descrizione: '';
					if(descrizione != null){
						li += '<b>' + descrizione + '</b>';
					}
					nominativoDescrizione = true;
				}
				let descrizioneTask = item.taskAttivi[i].descrizione;
				var terminiScaduti = '';
				if(descrizioneTask != null){
					for(var j = 0; j< infos.length; j++){
						var infoAoo = infos[j].aoo;
						var scadenza = null;
						if(item.taskAttivi[i].aoo!= null && infoAoo === item.taskAttivi[i].aoo.descrizione){
							if(infos[j].conf && infos[j].conf.dataManuale && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
								scadenza = new Date(infos[j].conf.dataManuale);
								scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
							}
			    			else if(infos[j].conf && infos[j].conf.dataCreazione && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
			    				scadenza = new Date(infos[j].conf.dataCreazione);
			    				scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
			    			}
							var adesso = new Date();
							if(scadenza){
			    				if(scadenza.getTime() > adesso.getTime()){
			    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(scadenza);
			    				}else{
			    					terminiScaduti += " - Termini scaduti"
			    				}
			    			}
						}
					}
					
					
					if(nominativoDescrizione){
						li += ' ('+descrizioneTask + terminiScaduti +')';
					} else li += ' ' + descrizioneTask;

				}
				li += '</li>';
			}
		}
        if(item.pareri && item.pareri.length > 0 ){
            item.pareri.forEach(p => {
                if(p && !p.annullato && p.tipoAzione && p.tipoAzione.codice== 'parere_quartiere_revisori' && !p.parereSintetico){
                	var adesso = new Date();
                	var terminiScaduti = '';
                	if(p.dataScadenza){
	    				if(new Date(p.dataScadenza) > adesso){
	    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(new Date(p.dataScadenza));
	    				}else{
	    					terminiScaduti += " - Termini scaduti"
	    				}
	    			}
                	li += '<li><b>' + p.aoo.descrizione  +'</b><br/>(Mancanza ' + p.tipoAzione.descrizione + terminiScaduti + ')</li>';
                }
            })
        }
		if(li){
			textHtml = '<ul class="taskTooltip">' + li + '</ul>';
		}
		else return null;

		return textHtml;
	}
	
	$scope.formattaData = function(data){
		if(data){
    		var str = "";
    		if(data.getDate() < 10){
    			str += "0";
    		}
    		str += (data.getDate());
    		str += "/";
    		if((data.getMonth() + 1) < 10){
    			str += "0";
    		}
    		str += (data.getMonth() + 1);
		    str += "/" + data.getFullYear();
		    return str;
		}else{
			return "";
		}
	};
	
	$scope.getInfoCommissioni = function(atto){
		var infos = [];
		if (atto.pareri) {
			for (var pc = 0; pc < atto.pareri.length; pc++) {
				if (atto.pareri[pc].annullato) {
					continue;
				}
				if ($rootScope.configurationParams.codice_tipo_parere_commissioni_consiliari == atto.pareri[pc].tipoAzione.codice) {
					var info = {
						'aoo': atto.pareri[pc].aoo.descrizione,
						'parere':atto.pareri[pc],
						'task': null
					};
					infos.push(info);
				}
			}
		}
		if (atto.taskAttivi) {
			for (var tc = 0; tc < atto.taskAttivi.length; tc++) {
				if (atto.taskAttivi[tc].aoo && atto.taskAttivi[tc].descrizione && atto.taskAttivi[tc].descrizione.toLowerCase().indexOf('commissione') > -1){
    				var infoExists = false;
    				for(var i = 0; i< infos.length; i++){
    					if(infos[i].aoo == atto.taskAttivi[tc].aoo.descrizione){
    						infos[i].task = atto.taskAttivi[tc];
    						infoExists=true;
    					}
    				}
    				if(!infoExists){
    					var info = {
    						'aoo': atto.taskAttivi[tc].aoo.descrizione,
    						'parere':null,
    						'task': atto.taskAttivi[tc]
    					};
        				infos.push(info);
    				}
				}
			}
		}
		if (atto.objs) {
			for (var x = 0; x < atto.objs.length; x++) {
				if (atto.objs[x] && atto.objs[x].listConfigurazioneIncaricoAooDto && atto.objs[x].listConfigurazioneIncaricoAooDto.length > 0){
					for(var y = 0; y < atto.objs[x].listConfigurazioneIncaricoAooDto.length; y++){
	    				var conf = atto.objs[x].listConfigurazioneIncaricoAooDto[y];
	    				var existsinfo = false;
	    				for(var i = 0; i< infos.length; i++){
	    					if(infos[i].aoo == conf.descrizioneAoo){
	    						existsinfo = true;
	    						infos[i].conf = conf;
	    					}
	    				}
	    				if(!existsinfo){
	    					var info = {
	    						'aoo': conf.descrizioneAoo,
	    						'parere':null,
	    						'task': null,
	    						'conf':conf
	    					};
	        				infos.push(info);
	    				}
					}
				}
			}
		}
		return infos;
	}

	$scope.getTooltipPareri = function(item){
		var textHtml;
		var li = '';
		if(item.pareri && item.pareri.length > 0 ){
			item.pareri.forEach(p => {
				if(p && p.tipoAzione.codice== 'parere_resp_tecnico' && p.parerePersonalizzato && !p.annullato){
					li += '<li><b>Parere Tecnico Articolato: </b>'+ p.parerePersonalizzato+'</li>';
				}
				else if(p && p.tipoAzione.codice == 'parere_resp_contabile'  &&	p.parerePersonalizzato && !p.annullato){
					li += '<li><b>Parere Contabile Articolato: </b>'+ p.parerePersonalizzato+'</li>';
				}
			})
		}
		if(li){
			textHtml = '<ul class="taskTooltip">' + li + '</ul>';
		}
		else return null;

		return textHtml;
	}
	
});
