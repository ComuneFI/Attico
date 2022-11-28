'use strict';

angular.module('cifra2gestattiApp')
    .controller('ProfiloController', function ($scope, $rootScope, TaskDesktop, DelegaTask, Delega, Profilo, TipoAtto, Utente,GruppoRuolo,Aoo, QualificaProfessionale, ParseLinks, Ruolo, Principal, ProfiloAccount, $state ) {
        $scope.profilos = [];

        $scope.userDisabled = false;
        $scope.profiloSearch = {};
        $scope.tempSearch = {};
        $scope.checkEsistenza = "";
        $scope.profiloRiassegnazioneId = "";
        $scope.qualificaRiassegna = {};
		$scope.riassegnazione = {reassigneeProf : {profiloOrigineId : null}, rTask : null};
        
        $scope.stati = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.profilo.filtro.stato.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.profilo.filtro.stato.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.profilo.filtro.stato.entrambi"
    		}
        ];

		$scope.statiFuture = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.profilo.filtro.statoFuture.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.profilo.filtro.statoFuture.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.profilo.filtro.statoFuture.entrambi"
    		}
        ];     

        $scope.tempSearch.stato = $scope.stati[2];
		$scope.tempSearch.statoFuture = $scope.statiFuture[2];
        
        $scope.ruolos = Ruolo.query(function(result) {
        	$scope.listAllRuoli = $scope.ruolos.map( function (ruoloCur) {
                return ruoloCur.descrizione;
        	});
        });
        
        $scope.gruppoRuolos = GruppoRuolo.query({idUser: $rootScope.profiloattivo.id});
        $scope.tipoAttos = TipoAtto.query(function(result) {
        	$scope.listTipoAtto = result.map( function (tipoCur) {
                return tipoCur.descrizione;
            });
        });
        $scope.aoos = [];
        
        if(!Principal.isInAnyRole($state.current.data.roles) && $rootScope.profiloattivo && $rootScope.profiloattivo.aoo && $rootScope.profiloattivo.aoo.id &&  ProfiloAccount.isInAnyRole($state.current.data.customRoles)){
    		Aoo.queryRicorsiva({id: $rootScope.profiloattivo.aoo.id}, function(result){
    			if(result && result.length > 0){
    				var aoo = null;
	        		for(var i = 0; i<result.length; i++){
	        			aoo = result[i];
	        			$scope.aoos.push({id:aoo.id, codice:aoo.codice, descrizione:aoo.descrizione});
	        		}
    			}
    		});
    	}else if(Principal.isInAnyRole($state.current.data.roles)){
	        Aoo.getMinimal(function(result) {
	        	$scope.listAllAoo = result.map( function (aooCur) {
	                return aooCur.descrizione;
	            });
	        	if(result && result.length > 0){
	        		var aoo = null;
	        		for(var i = 0; i<result.length; i++){
	        			aoo = result[i];
	        			$scope.aoos.push({id:aoo.id, codice:aoo.codice, descrizione:aoo.descrizione});
	        		}
	        	}
	        });
    	}
        
        $scope.qualificaprofessionales = QualificaProfessionale.query(function(result) {
        	$scope.listAllQualifiche = result.map( function (qualificaCur) {
                return qualificaCur.denominazione;
            });
        });

        $scope.impersonifica = function(profilo){
        	if (profilo) {
        		Profilo.checkImpersonifica(profilo.utente.username, function(response) {
        			if (response) {
        				if (!response.isAmministratore) {        				
	        				$rootScope.profiloOrigine = $rootScope.profiloattivo;
	        				ProfiloAccount.setProfiloOrigine($rootScope.profiloOrigine, $rootScope.activeprofilos);
	        				
	                    	var account = $rootScope.account;
	                    	account.email = profilo.utente.email;
	                    	account.login = profilo.utente.username;
	                    	account.utente = profilo.utente;
	                    	
	                    	ProfiloAccount.setAccount(account);
	                    	$rootScope.activeprofilos = [];
	                    	$rootScope.activeprofilos.push(profilo);
	                    	$rootScope.activeprofilos.$resolved = true;
	                    	ProfiloAccount.setProfilo(profilo);
	                        Utente.getAoosDirigente({id:profilo.utente.id}, function(res){
	                    		$rootScope.aoosDirigente = res;
	                    	});
	                        $state.go("selezionaprofilo", {}, {reload: true});
        				}
		        		else {
		        			alert('ATTENZIONE! Utenti amministratori non possono essere impersonificati.');
		        		}
        			}
    			});
        	}
        }
        
        $scope.getProfiloTipiAtto = function(profilo){
        	var profiloTipiAtto = [];
        	for(var i = 0; $scope.tipoAttos.length; i++){
        		var profiloTipoAtto = {};
        		profiloTipoAtto.tipoAtto = $scope.tipoAttos[i];
        		profiloTipoAtto.profilo = {};
        		profiloTipoAtto.profilo.id = profilo.id;
        		$scope.profiloTipiAtto.push(profiloTipoAtto);
        	}
        	return profiloTipiAtto;
        }
        
        $scope.page = 1;
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.profiloSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.profiloSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	var profiloAooId = null;
        	if(!Principal.isInAnyRole($state.current.data.roles) && ProfiloAccount.isInAnyRole($state.current.data.customRoles)){
        		profiloAooId = $rootScope.profiloattivo.aoo.id;
        	}else if(!Principal.isInAnyRole($state.current.data.roles)){
        		return;
        	}
        	if(searchObject==undefined || searchObject==null && $rootScope && $rootScope.fromUtente){
        		searchObject = {};
        		searchObject.utente = $rootScope.fromUtente;
        		$scope.tempSearch = {};
        		$scope.tempSearch.utente = searchObject.utente;
        		$rootScope.fromUtente = null;
        	}
        	
        	if(searchObject!=undefined && searchObject!=null){
        		var sRuoli = null;
        		if (searchObject.ruolo) {
        			sRuoli = searchObject.ruolo.codice;
        		}
        		let stato = null;
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
				let statoFuture = null;
        		if(searchObject.statoFuture){
        			statoFuture = searchObject.statoFuture.id;
        		}
	            Profilo.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	idProfilo: searchObject.id,
	            	descrizione: searchObject.descrizione,
	            	delega: searchObject.delega,
	            	tipoAtto: searchObject.tipoAtto,
	            	utente: searchObject.utente,
	            	aoo: searchObject.aoo,
	            	qualificaProfessionale: searchObject.qualificaProfessionale,
	            	ruoli: sRuoli,
	            	stato: stato,
					statoFuture:statoFuture,
	            	profiloAooId:profiloAooId
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.profilos = result;
	            });
        	}else{
        		Profilo.query({stato:($scope.profiloSearch.stato ? $scope.profiloSearch.stato.id : null), statoFuture:($scope.profiloSearch.statoFuture ? $scope.profiloSearch.statoFuture.id : null), profiloAooId:profiloAooId, page: $scope.page, per_page: 5, idProfilo:$scope.profiloSearch.id, descrizione:$scope.profiloSearch.descrizione, delega:$scope.profiloSearch.delega, tipoAtto:$scope.profiloSearch.tipoAtto, utente:$scope.profiloSearch.utente, aoo:$scope.profiloSearch.aoo, qualificaProfessionale:$scope.profiloSearch.qualificaProfessionale,ruoli:$scope.profiloSearch.ruolo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.profilos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            if(!$scope.profiloSearch.utente && $scope.tempSearch && $scope.tempSearch.utente){
            	$scope.profiloSearch = jQuery.extend(true, {}, $scope.tempSearch);
            }
            $scope.loadAll($scope.profiloSearch);
        };
        $scope.loadAll();
        
		$scope.confirmFutureDisable = function(profilo) { 
			$rootScope.showMessage({title:'Conferma disabilitazione', siButton:true, noButton:true, 
        	siFunction: function(){
				Profilo.existsAsDelegato({id:profilo.id}, function(resp){
					if(resp && resp.exists){
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Il profilo non pu\u00F2 essere disabilitato in quanto esiste una delega, in corso o futura, di tipo temporale oppure una delega per intero iter in cui questo profilo risulta come delegato.'});
					}else{
		        		Profilo.futureDisable({id:profilo.id}, function(result){
							profilo.futureEnabled = false;
		        			$('#genericMessage').modal('hide');
		        		});
					}
				});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Attenzione! Con la presente operazione il profilo non sar\u00E0 pi\u00F9 selezionabile, nella sezione Visti Pareri e Firme (per la lavorazione di nuovi atti), in Carichi di Lavoro, in Deleghe Temporali e Delega Singola Lavorazione.<br />Prima della conferma, si prega di verificare se, per l\'ufficio associato al profilo, esiste un altro profilo con uguale Gruppo Ruolo.<br/>Confermi di voler procedere con la disabilitazione?'});
        };

		$scope.futureEnable = function(profilo) { 
			$rootScope.showMessage({title:'Conferma abilitazione', siButton:true, noButton:true, 
        	siFunction: function(){
        		Profilo.futureEnable({id:profilo.id}, function(result){
					profilo.futureEnabled = true;
        			$('#genericMessage').modal('hide');
        		});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Confermi di voler procedere con la riabilitazione?'});
        };
        
        $scope.confirmDisable = function(profilo) { 
        	Profilo.checkIfCanDisableWithoutReassegnee({id:profilo.id}, function(res){
        		if(!res.lockTypes || !res.lockTypes.length){
        			Profilo.disabilitazioneDiretta({id:profilo.id}, function(result){
        				if(result.stato == 'ok'){
        					if(profilo.validita == null || profilo.validita == undefined){
             	    			profilo.validita = {};
             	    		}
             	    		profilo.validita.validoal = "-";
        				}
        			});
        		}else{
					/*
					if(res.lockTypes.length == 1 && res.lockTypes[0] == 'TASK_IN_CARICO'){
	        			$scope.profiloRiassegnazioneId = "";
	        			$scope.qualificaRiassegna = {};
	                	$scope.profilo = profilo;
	                	
	                	Profilo.getProfilosRiassegnazione({profiloDisabilitazioneId:profilo.id}, function(result){
	                		$scope.profiliAssegnaTask = result;
	                	});
	                	
	                	$('#disableProfiloConfirmation').modal('show');
	        		}else{*/
						/*let motivo = "";
						if(res.lockTypes.indexOf('TASK_ISTRUTTORE') > -1){
							motivo = "\u00E8 istruttore di atti ancora in itenere";
						}
						if(res.lockTypes.indexOf('TASK_RIASSIGNEE') > -1){
							if(motivo){
								motivo = motivo + " ed ";
							}
							motivo = motivo + "\u00E8 assegnatario di task che occorre riassegnare"
						}
						if(res.lockTypes.indexOf('TASK_RILASCIARE') > -1){
							if(motivo){
								motivo = motivo + " ed ";
							}
							motivo = motivo + "\u00E8 assegnatario di task provenienti da una coda"
						}
						if(res.lockTypes.indexOf('DELEGA_TASK') > -1){
							if(motivo){
								motivo = motivo + " ed ";
							}
							motivo = motivo + "ha delle deleghe su intero iter attive su atti ancora in itinere"
						}
						if(res.lockTypes.indexOf('DELEGA_TEMPORALE') > -1){
							if(motivo){
								motivo = motivo + " ed ";
							}
							motivo = motivo + "\u00E8 censito come delegato in deleghe temporali attive o future"
						}
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Il profilo non pu\u00F2 essere disabilitato in quanto ' + motivo + ".<br>Per valutare la situazione delle lavorazioni e delle deleghe associate al profilo utilizzare il pulsante <b>'RIASSEGNA LAVORAZIONI'</b>"});
						*/
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Il profilo non pu\u00F2 essere disabilitato in quanto vi sono lavorazioni da riassegnare a suo carico o deleghe attive a suo favore.<br>Per valutare la situazione di lavorazioni e deleghe associate al profilo utilizzare il pulsante <b>\'RIASSEGNA LAVORAZIONI\'</b>'});
					/*}*/
				}
        	});
        }

        $scope.$watch($scope.profiloRiassegnazioneId, function(newValue){
        	console.log("$scope.profiloRiassegnazioneId -----> ", $scope.profiloRiassegnazioneId);
        });
        
        $scope.setRiassegnazione = function(idRiassegnazione){
        	$scope.profiloRiassegnazioneId = idRiassegnazione;
        	$scope.qualificaRiassegna = {};
        }
        
        $scope.setQualificaRiassegnazione = function(qualifica){
    		$scope.qualificaRiassegna = qualifica;
        };
        
        $scope.disable = function(profilo){
     	    Profilo.disable({id: profilo.id, idRias:$scope.profiloRiassegnazioneId, qualificaId:$scope.qualificaRiassegna.id}, function(data){
     	    	if(data.stato == 'ok'){
     	    		if(profilo.validita == null || profilo.validita == undefined){
     	    			profilo.validita = {};
     	    		}
     	    		profilo.validita.validoal = "-";
     	    	}
     	    	$('#disableProfiloConfirmation').modal('hide');
     	    });
        };
        
        $scope.enable = function(profilo){
     	    Profilo.enable({ id: profilo.id}, function(data){
     	    	if(data.stato == 'ok'){
	     	    	if(profilo.validita == null ||profilo.validita == undefined){
	     	    		profilo.validita = {};
	     	    	}
	     	    	profilo.validita.validoal = undefined;
     	    	}else if (data.stato == 'aooDisabled'){
     	    		$('#aooDisabledError').modal('show');
     	    	}
     	    });
        };

        $scope.showUpdate = function (id) {
        	$scope.userDisabled = true;
            $scope.checkEsistenza = false;
            $('#saveProfiloModal').modal('show');
            $scope.saveButtonDisabled = false;
            Profilo.getToUpt({id: id}, function(result) {
				$scope.profilo = result.profilo;
				if(result.locks){
					if(result.locks.filter((p) => p.GRUPPO_RUOLO).length){
						$scope.profilo.lockGR = result.locks.filter((p) => p.GRUPPO_RUOLO)[0].GRUPPO_RUOLO;
					}
				}
				if(!$scope.gruppoRuolos || !$scope.gruppoRuolos.some( gr => {
					return gr.id == $scope.profilo.grupporuolo.id;
				})){
					$scope.profilo.lockGR = "Il seguente Gruppo Ruolo può essere cambiato solo da un amministratore";
				}
				delete $scope.profilo.locks;
            });
        };
        
        $scope.verificaAbilitazioneSalvataggio = function(){
        	var saveButtonDisabled = true;
        	if ($scope.profilo && $scope.profilo.hasQualifica && $scope.profilo.tipiAtto && $scope.profilo.aoo) {
        		saveButtonDisabled = $scope.profilo.hasQualifica.length < 1 || $scope.profilo.tipiAtto.length < 1;
        	}
        	return saveButtonDisabled;
        }
        
        $scope.showCreate = function () {
        	$scope.utentes = Utente.getAllActive();
        	$scope.clear();
            $('#saveProfiloModal').modal('show');
        };

        $scope.save = function () {
        	//Disable the save button
        	$scope.saveButtonDisabled = true;
        	if ($scope.profilo.id != null) {
                Profilo.update($scope.profilo, function () {
                	$scope.refresh();
                });
            } else {
    			Profilo.save($scope.profilo, function () {
                    $scope.refresh();
                }, function(err){
                	$scope.saveButtonDisabled = false;
                });
            }
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveProfiloModal').modal('hide');
            $scope.clear();
            
            //Enable save button
            $scope.saveButtonDisabled = false;
        };

        $scope.clear = function () {
            $scope.userDisabled = false;
            $scope.checkEsistenza = false;
            $scope.profilo = {descrizione: null, validodal: null, validoal: null, delega: null, id: null, aoo: null};
            //$scope.editForm.$setPristine();
            //$scope.editForm.$setUntouched();
        };
        
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
		
		$scope.viewRiassegnazioneTask = function(task){
			$scope.riassegnazione.rTask = task;
			$('#futureReassignee').modal('show');
		}
		
		$scope.futureReassignee = function(profilo){
			$rootScope.showMessage({title:'Conferma operazione', siButton:true, noButton:true, 
        	siFunction: function(){
				let riassegnazione = {
					attoId:$scope.riassegnazione.rTask.attoId, 
					qualificaNuova:profilo.qualificaNuova.id,
					confIncaricoId:$scope.riassegnazione.rTask.confIncaricoId,
					profiloNuovo:profilo.id,
					profiloOrigine:$scope.riassegnazione.reassigneeProf.profiloOrigineId
				};
        		Profilo.futureReassignee(riassegnazione, function (res) {
	                if(res.stato == 'ok'){
						$('#futureReassignee').modal('hide');
						$rootScope.showMessage({title:'Risultato operazione', okButton:true, body:'Operazione eseguita con successo'});
						$scope.loadPageReassignee(1);
					}else{
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'A causa di un errore il sistema non ha eseguito l\'operazione. Si prega di riprovare.'});
						$scope.loadPageReassignee(1);
					}
	            });
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Confermare l\'operazione?'});
		};
		
		$scope.rilascia = function(taskRiassegnazione){
			$rootScope.showMessage({title:'Conferma operazione', siButton:true, noButton:true, 
        	siFunction: function(){
				TaskDesktop.unclaim({taskId:taskRiassegnazione.taskId, verifyOriginalProfId:$scope.riassegnazione.reassigneeProf.profiloOrigineId}, function(result){
					$rootScope.showMessage({title:'Risultato operazione', okButton:true, body:'Operazione eseguita con successo'});
					$scope.loadPageReassignee(1);
				}, function(){
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'A causa di un errore il sistema non ha eseguito l\'operazione. Si prega di riprovare.'});
					$scope.loadPageReassignee(1);
				});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Confermare l\'operazione?'});
		};
		
		$scope.loadRiassegnazioneView = function(view){
			if(view && (!$scope.riassegnazione.sezView || $scope.riassegnazione.sezView!=view)){
				if($scope.riassegnazione.sezView){
					delete $scope.riassegnazione[$scope.riassegnazione.sezView];
				}
				let service = null;
				switch (view) {
				  case 'sezIstr':
				    service = Profilo.findTaskIstruttore;
				    break;
				  case 'sezRiass':
		 			service = Profilo.findTaskRiassignee;
				    break;
				  case 'sezRil':
				    service = Profilo.findTaskDaRilasciare;
				    break;
				  case 'sezDel':
					service = DelegaTask.search;
				    break;
				}
				if(service){
					$scope.riassegnazione = {...$scope.riassegnazione, rTask : null, riassignazioneTasks:[], sezView : view, service : service};
					$scope.riassegnazione[$scope.riassegnazione.sezView] = {loading:false};
					$scope.loadPageReassignee(1);
				} 
			}
		};
		
		$scope.configFutureReassignee = function(profilo){
			$scope.riassegnazione = {profilo: profilo, reassigneeProf : {profiloOrigineId : profilo.id}, rTask : null, riassignazioneTasks:[], sezView : 'sezIstr', service : Profilo.findTaskIstruttore, sezIstr : {loading:true}};
			Profilo.findTaskIstruttore({id : profilo.id, per_page: 5, page: 1}, function(result, headers) {
	            $scope.riassegnazione[$scope.riassegnazione.sezView] = {links:ParseLinks.parse(headers('link')), page:1, loading:false};
				$scope.riassegnazione.riassignazioneTasks = result;
				$('#riassegneeModal').modal('show');
			}, function(){
				$scope.riassegnazione = {reassigneeProf : {profiloOrigineId : null}, rTask : null};
				$rootScope.showMessage({title:'Attenzione', okButton:true, body:'A causa di un errore il sistema non ha restituito la lista dei task da riassegnare. Si prega di riprovare.'});
			});
		};
		
		$scope.loadPageReassignee = function(page){
			$scope.riassegnazione.riassignazioneTasks = [];
			$scope.riassegnazione[$scope.riassegnazione.sezView].loading = true;
			if($scope.riassegnazione.sezView != 'sezDel'){
				$scope.riassegnazione.service({
					 		id : $scope.riassegnazione.reassigneeProf.profiloOrigineId, per_page: 5, page: page}, function(result, headers) {
				            $scope.riassegnazione[$scope.riassegnazione.sezView] = {links:ParseLinks.parse(headers('link')), page:page, loading:false};
							$scope.riassegnazione.riassignazioneTasks = result;
							$('#riassegneeModal').modal('show');
				}, function(){
					$scope.riassegnazione = {reassigneeProf : {profiloOrigineId : null}, rTask : null};
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'A causa di un errore il sistema non ha restituito la lista dei task da riassegnare. Si prega di riprovare.'});
				});
			}else{
				$scope.riassegnazione.service({per_page: 5, page: page}, {itinereOnly:true, delegatoProfId: $scope.riassegnazione.reassigneeProf.profiloOrigineId}, function(result, headers) {
			            $scope.riassegnazione[$scope.riassegnazione.sezView] = {links:ParseLinks.parse(headers('link')), page:page, loading:false};
						$scope.riassegnazione.riassignazioneTasks = result;
						if($scope.riassegnazione.warningDelegheTemporali === undefined || $scope.riassegnazione.warningDelegheTemporali === null){
							Delega.exists({}, {inCorsoOFutura:true, delegatoProfId:$scope.riassegnazione.reassigneeProf.profiloOrigineId}, function(result) {
				                $scope.riassegnazione.warningDelegheTemporali = result.exists;
								$('#riassegneeModal').modal('show');
				            });
						}else{
							$('#riassegneeModal').modal('show');
						}
				}, function(){
					$scope.riassegnazione = {reassigneeProf : {profiloOrigineId : null}, rTask : null};
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'A causa di un errore il sistema non ha restituito la lista dei task da riassegnare. Si prega di riprovare.'});
				});
			}
		};
		
		//inizio modifica link to profilo
        $scope.ricercaUtente = function(id, username){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.tempSearch = {};
        	$scope.tempSearch.utente = username;
        	$scope.tempSearch.id = id;
        	$scope.loadUtente($scope.tempSearch);
        	
        };
        
        $scope.loadUtente = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Utente.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	utente: searchObject.utente
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.utentes = result;
	                $rootScope.fromUtenteProfilo = searchObject.utente;
	                $rootScope.fromUtenteProfiloId = searchObject.id;
	                $state.go("utente", {}, {reload: false});
	            });
        	}
        };
        //fine modifica link to profilo
		
		
    });
