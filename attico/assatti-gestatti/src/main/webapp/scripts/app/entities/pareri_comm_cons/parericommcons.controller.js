'use strict';

angular.module('cifra2gestattiApp')
    .controller('CommissioniConsController', function ($scope, localStorageService, ngToast, SedutaGiuntaConstants,
    		TaskDesktop, Lavorazione, ConfigurazioneIncarico, $log, Atto, TipoAtto, ParseLinks, $rootScope, EsitoPareri, Parere, Upload, DocumentoInformatico, DateServer) {
    	
    	$scope.page = 1;
    	$scope.tempSearch = {};
        $scope.loading = false;
        $scope.tipoattos = [];
        $scope.esitiParere = [];
        $scope.adesso = new Date();
        
        DateServer.getCurrent({}, function(obj){
			if(obj && obj.milliseconds){
				$scope.adesso = new Date(obj.milliseconds);
			}
		});
        
        $scope.attos = [];
        $scope.attoSel = null;
		$scope.giorniScadenzaNew= null;
        $scope.aggiungiCommissioneSel = {
        	value: null
        }
        
        $scope.idConfigurazioneTaskSel = null;
        $scope.ruoliCommissioniCons = [];
        $scope.listCommissioniCons = [];
        $scope.commissioniSel = [];
        $scope.confCommissioneSel = null;
        $scope.incarichi = null;
        
        var tipiAttoIds = [];
    	for(var i = 0; angular.isDefined($rootScope.profiloattivo) && angular.isDefined($rootScope.profiloattivo.tipiAtto) && i<$rootScope.profiloattivo.tipiAtto.length; i++){
    		tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
    	}
    	
    	Atto.caricastati(function(collection){
    		$scope.statiProposta = collection;
    	});
    	
    	TipoAtto.query({}, function(result){
        	$scope.tipoattos = result;
        });
    	
    	$scope.tipiAttoIds = tipiAttoIds;
    	$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id} ;
    	$scope.criteria.page = $scope.page;
    	$scope.criteria.per_page = 5;
    	$scope.criteria.ordinamento = "codiceCifra";
    	$scope.criteria.tipoOrinamento = "desc";
    	
    	$scope.deleteAllegatoParere = function (allegati, index) {
    		if(allegati!=undefined && allegati!=null && allegati[index]!=undefined && allegati[index]!=null && allegati[index].id!=undefined && allegati[index].id!=null){
    			DocumentoInformatico.delete({id: allegati[index].id}, function () {
    				allegati.splice(index,1);
    		    });
    		}
    	};
    	
    	$scope.formattaData = function(data){
    		if(data){
	    		var str = "";
	    		if(data.getDate() < 10){
	    			str += "0";
	    		}
	    		str += (data.getDate());
	    		str += "-";
	    		if((data.getMonth() + 1) < 10){
	    			str += "0";
	    		}
	    		str += (data.getMonth() + 1);
    		    str += "-" + data.getFullYear();
    		    return str;
    		}else{
    			return "";
    		}
    	};
    	
    	$scope.ricercaAtti = function(page) {
    		if(page){
    			$scope.page = page;
    		}else{
    			$scope.page = 1;
    		}
    		$scope.loading = true;
    		$scope.attos = [];
    		DateServer.getCurrent({}, function(obj){
    			if(obj && obj.milliseconds){
    				$scope.adesso = new Date(obj.milliseconds);
    			}
    		});
    		if($scope.tempSearch==undefined || $scope.tempSearch==null){
    			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
    		}
    		    		
    		$scope.tempSearch.ordinamento = "dataAttivita";
			$scope.tempSearch.tipoOrinamento = "asc";
    		
    		// if($scope.tempSearch.tipoOrinamento==undefined || $scope.tempSearch.tipoOrinamento==null){
    		//	$scope.tempSearch.ordinamento = "codiceCifra";
    		//	$scope.tempSearch.tipoOrinamento = "desc";
    		// }
    		
    		if ($rootScope.profiloattivo.id > 0)
    			$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
    		
    		if($scope.tempSearch.cercaSospesi != null) {
    			if($scope.tempSearch.cercaSospesi.id == true) {
    				$scope.tempSearch.stato = SedutaGiuntaConstants.statiAtto.propostaSospesa;
    			}
    			if($scope.tempSearch.cercaSospesi.id == false) {
    				$scope.tempSearch.stato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdl;
    			}
    		}
    		
    		TaskDesktop.searchAttiCommissioni({page: $scope.page, per_page: 10, organo: 'C', tipoRicerca: 'PAR_COMM'},
    				 $scope.tempSearch, function(result, headers) {	
    			$scope.loading = false;
    	        $scope.attos = result;
    	        
    	        $scope.links = ParseLinks.parse(headers('link'));
    	        $scope.totalResultAtti=headers('x-total-count') ;
    	        if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
    				localStorageService.set('_pareriCommiss_taskSearch', $scope.tempSearch);
    			}
    	    });
    	};

    	
    	$scope.resetRicerca = function(){
	    	$scope.page = 1;
	    	$scope.tempSearch = {};
	    	
	    	$scope.ricercaAtti();
    	}
    	
    	$scope.loadPageAtto = function(page) {
    		$scope.ricercaAtti(page);
    	};
    	
    	EsitoPareri.query({tipo:'Commissione'}, function(result){
    		$scope.esitiParere = result;
		});
    	
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
    	
    	$scope.mostraCommissioni = function(atto) {
    		if(!atto || atto.doneMostraComm){
    			return;
    		}
    		
    		atto.doneMostraComm = true;
    		
    		var output = ""
    		var checkEspresso = [];
    		
    		var infos = $scope.getInfoCommissioni(atto);
    		
    		for(var i = 0; i< infos.length; i++){
    			output += infos[i].aoo;
    			var innerStr = "";
    			var scadenza = null;
    			if(infos[i].parere && infos[i].parere.data){
    				var dataParere = new Date(infos[i].parere.data);
    				innerStr += $scope.formattaData(dataParere);
    			}
    			if($scope.esitiParere && $scope.esitiParere.length > 0){
    				var inserito = false;
					if(infos[i].parere && infos[i].parere.parereSintetico && infos[i].parere.parereSintetico.length > 0){
	    				for(var j = 0; j<$scope.esitiParere.length; j++){
	    					if($scope.esitiParere[j].codice == infos[i].parere.parereSintetico){
	    						inserito = true;
	    						innerStr += " "+ $scope.esitiParere[j].valore;
	    						break;
	    					}
	    				}
					}else{
						inserito = true;
					}
					if(!inserito && infos[i].parere.parereSintetico){
						innerStr += " - " + infos[i].parere.parereSintetico;
					}
    			}
    			if(infos[i].conf && infos[i].conf.dataManuale && infos[i].conf.giorniScadenza!=undefined && infos[i].conf.giorniScadenza!=null){
					scadenza = new Date(infos[i].conf.dataManuale);
					scadenza.setDate(scadenza.getDate() + infos[i].conf.giorniScadenza);
				}
    			else if(infos[i].conf && infos[i].conf.dataCreazione && infos[i].conf.giorniScadenza!=undefined && infos[i].conf.giorniScadenza!=null){
    				scadenza = new Date(infos[i].conf.dataCreazione);
    				scadenza.setDate(scadenza.getDate() + infos[i].conf.giorniScadenza);
    			}
    			if(scadenza){
    				if(innerStr){
	    				var lastChar = innerStr.substring(innerStr.length - 1);
	    				if(lastChar != "(" && lastChar != " "){
	    					innerStr += " - ";
	    				}
    				}
    				if(scadenza.getTime() > $scope.adesso.getTime()){
    					innerStr += "Scadenza " + $scope.formattaData(scadenza);
    				}else{
    					innerStr += "Termini scaduti"
    				}
    			}
    			if(innerStr){
    				output += " (" + innerStr + ")";
    			}
    			output += "<br/>";
    		}
    		
    		atto.stringaCommissioni = output;
    	}
    	
    	$scope.gestisciCommissioni = function(atto) {
    		$scope.listaEsitoPareriCommissione =  [];
    		$scope.listCommissioniCons = [];
    		$scope.ruoliCommissioniCons = [];
            $scope.commissioniSel = [];
            $scope.idUpdate = null;
    		$scope.attoSel = atto;
    		$scope.idConfigurazioneTaskSel = null;
    		$scope.aggiungiCommissioneSel.value = null;
			$scope.aggiungiCommissioneSel.ggScadenzaNew = null;
			$scope.giorniScadenzaNew= null;
    		var pareri = atto.pareri;
    		Lavorazione.assegnazioneincaricodettaglio({
    			codiceConfigurazioneTask: $rootScope.configurationParams.codice_configurazione_parere_consiliare, 
    			proponenteAooId: $scope.attoSel.aoo.id, 
    			profiloAooId: $scope.profiloattivo.aoo.id, 
    			idAtto: $scope.attoSel.id 
			}, 
			function(data) {
    			
    			if (data == null) {
    				ngToast.create(  { className: 'error', content: 'Errore di lettura dei dati.' } );
    				return;
    			}
    			
    			if (data.listConfigurazioneIncaricoDto.length > 1) {
    				ngToast.create(  { className: 'error', content: 'Errore di lettura dei dati: impossibile individuare in maniera univoca la configurazione delle assegnazioni per i Pareri Commissioni Consiliari' } );
    				return;
    			}
    			else if (data.listConfigurazioneIncaricoDto.length == 1) {
    				$scope.commissioniSel = data.listConfigurazioneIncaricoDto[0].listConfigurazioneIncaricoAooDto;
    				$log.debug("$scope.commissioniSel:",$scope.commissioniSel);
    				
    				
    				if (pareri) {
    	    			for (var pc = 0; pc < pareri.length; pc++) {
    	    				if (pareri[pc].annullato) {
    	    					continue;
    	    				}
    	    				
    	    				if ($rootScope.configurationParams.codice_tipo_parere_commissioni_consiliari == pareri[pc].tipoAzione.codice) {
    	    					for (var j = 0; j < $scope.commissioniSel.length; j++) {
    	    						if(pareri[pc].aoo.id == $scope.commissioniSel[j].idAoo){
    	    							$scope.commissioniSel[j].dataParere = pareri[pc].data;
    	    						}
    	    					}

    	    				}
    	    			}
    	    		}
    				
    				$scope.idUpdate = data.listConfigurazioneIncaricoDto[0].id;
    			}
    			
    			if (data.listAssegnazioneIncaricoDettaglio.length > 1) {
    				ngToast.create(  { className: 'error', content: 'Errore di lettura dei dati: impossibile individuare in maniera univoca la configurazione dei Pareri Commissioni Consiliari' } );
    				return;
    			}
    			else if (data.listAssegnazioneIncaricoDettaglio.length == 1) {
    				$scope.ruoliCommissioniCons = data.listAssegnazioneIncaricoDettaglio[0].listRuolo;
    				$scope.idConfigurazioneTaskSel = data.listAssegnazioneIncaricoDettaglio[0].configurazioneTaskDto.idConfigurazioneTask;
    				
    				if (data.listAssegnazioneIncaricoDettaglio[0].listAoo) {
    					for (var cc = 0; cc < data.listAssegnazioneIncaricoDettaglio[0].listAoo.length; cc++) {
        					if (!$scope.isAooSelected(data.listAssegnazioneIncaricoDettaglio[0].listAoo[cc].id)) {
        						$scope.listCommissioniCons.push(data.listAssegnazioneIncaricoDettaglio[0].listAoo[cc]);
        					}
        				}
    				}
    			}
    			
    			$('#mascheraPareri').modal('show');
    		});
    	};
    	
    	$scope.isAooSelected = function(idAoo) {
    		if ($scope.commissioniSel) {
    			for (var cs = 0; cs < $scope.commissioniSel.length; cs++) {
    				if ($scope.commissioniSel[cs].idAoo == idAoo) {
    					return true;
    				}
    			}
    		}
    		return false;
    	};
    	
    	$scope.aggiungiCommissione = function() {
    		
			var incarico = {};
			incarico.id = $scope.idUpdate;
			incarico.idAtto = $scope.attoSel.id;
			incarico.isValid = true;
			incarico.idConfigurazioneTask = $scope.idConfigurazioneTaskSel;
			incarico.listConfigurazioneIncaricoAooDto=[];
			incarico.listConfigurazioneIncaricoProfiloDto=[];
			
			if (incarico.id == null || incarico.id < 0) {
				alert('Errore: id configurazione da aggiornare non specificato.');
				return;
			}
			if ($scope.attoSel.id == null || $scope.attoSel.id < 0) {
				alert('Errore: id atto da aggiornare non specificato.');
				return;
			}
			
			var ordineFirma = 0;
			if ($scope.commissioniSel) {
				for (var cs = 0; cs < $scope.commissioniSel.length; cs++) {
					var configurazioneIncaricoAooDto = {
						"idAoo": $scope.commissioniSel[cs].idAoo,
						"ordineFirma": ordineFirma,
						"qualificaProfessionaleDto": null,
						"giorniScadenza": $scope.commissioniSel[cs].giorniScadenza,
						"dataManuale": $scope.commissioniSel[cs].dataManuale
					};
					
					incarico.listConfigurazioneIncaricoAooDto.push(configurazioneIncaricoAooDto);
					ordineFirma++;
				}
			}
    		
			if($scope.aggiungiCommissioneSel && $scope.aggiungiCommissioneSel.value != null
				&& $scope.aggiungiCommissioneSel.ggScadenzaNew) {
				var giorniScadenza = parseInt($scope.aggiungiCommissioneSel.ggScadenzaNew);
				var configurazioneIncaricoAooDto = {
						"idAoo": $scope.aggiungiCommissioneSel.value.id,
						"ordineFirma": ordineFirma,
						"qualificaProfessionaleDto": null,
						"giorniScadenza": giorniScadenza,
						"dataManuale": new Date()
					};
					
				incarico.listConfigurazioneIncaricoAooDto.push(configurazioneIncaricoAooDto);
			
				
    	
				ConfigurazioneIncarico.saveCommissioneConsiliare(incarico, function () {
					ngToast.create(  { className: 'success', content:  'Commissione Consiliare aggiunta con successo'  } );
					$scope.ricercaAtti($scope.page);
					$scope.gestisciCommissioni($scope.attoSel);
	            }, 
	            function(err) {
	            	ngToast.create(  { className: 'error', content: 'Errore di elaborazione. Si prega di riprovare, se l\'errore persiste contattare l\'assistenza.'  } );
					$scope.gestisciCommissioni($scope.attoSel);
	            });
			}
    	};
    	
    	
    	$scope.confermaElimina = function(configIncarico) {
    		$scope.confCommissioneSel = configIncarico;
    		$('#mascheraPareri').modal('hide');
    		$('#confermaElimina').modal('show');
    	};
    	
    	$scope.eliminaCommissione = function() {
    		var incarico = {};
			incarico.id = $scope.idUpdate;
			incarico.idAtto = $scope.attoSel.id;
			incarico.isValid = true;
			incarico.idConfigurazioneTask = $scope.idConfigurazioneTaskSel;
			incarico.listConfigurazioneIncaricoAooDto=[];
			incarico.listConfigurazioneIncaricoProfiloDto=[];
			
			if (incarico.id == null || incarico.id < 0) {
				alert('Errore: id configurazione da aggiornare non specificato.');
				return;
			}
			if ($scope.attoSel.id == null || $scope.attoSel.id < 0) {
				alert('Errore: id atto da aggiornare non specificato.');
				return;
			}
			
			var ordineFirma = 0;
			if ($scope.commissioniSel) {
				for (var cs = 0; cs < $scope.commissioniSel.length; cs++) {
					if ($scope.commissioniSel[cs].idAoo == $scope.confCommissioneSel.idAoo) {
						continue;
					}
										
					var configurazioneIncaricoAooDto = {
						"idAoo": $scope.commissioniSel[cs].idAoo,
						"ordineFirma": ordineFirma,
						"qualificaProfessionaleDto": null,
						"giorniScadenza": $scope.commissioniSel[cs].giorniScadenza
					};
					
					incarico.listConfigurazioneIncaricoAooDto.push(configurazioneIncaricoAooDto);
					ordineFirma++;
				}
			}
			
			ConfigurazioneIncarico.saveCommissioneConsiliare(incarico, function () {
				$('#confermaElimina').modal('hide');
				ngToast.create(  { className: 'success', content:  'Commissione Consiliare eliminata con successo'  } );
				$scope.ricercaAtti($scope.page);
				$scope.gestisciCommissioni($scope.attoSel);
            }, 
            function(err) {
            	ngToast.create(  { className: 'error', content: 'Errore di elaborazione. Si prega di riprovare, se l\'errore persiste contattare l\'assistenza.'  } );
				$scope.gestisciCommissioni($scope.attoSel);
            });
    	};
    	
    	$scope.fileDroppedParere = function (files,event,rejectedFiles, parere ) {
    		  $scope.saveParereCommissione();
    	      if(event.type !== "click"){
    	    	if(rejectedFiles.length > 0){
    	    		$rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, 'application/*');
    	    		return;
    	    	}
    	    	 
    	        Upload.upload({
    	          url: 'api/pareres/'+parere.id +'/allegato',
    	          headers : {
    	            'Authorization': 'Bearer '+ $rootScope.accessToken
    	          },
    	          file: files
    	        }).progress(function (evt) {
    	          $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    	        }).success(function (data, status, headers, config) {
    	        	$scope.parere.allegati = data;
    	        	for(var i = 0; i < $scope.attoSel.pareri.length; i++){
						if($scope.attoSel.pareri[i].id == $scope.parere.id){
							$scope.attoSel.pareri[i]  = $scope.parere;
							break;
						}
					}
    	        });
    	      }
    	    };
    	
    	$scope.saveParereCommissione = function() {
	    	ConfigurazioneIncarico.saveParere($scope.parere,  
		    	function (parere) {
	    			ngToast.create(  { className: 'success', content:  'Parere salvato con successo'  } );
	    			$scope.parere = parere;
	    			$scope.visualizzaAllegati = $scope.parere.id;
	    			if($scope.parere && $scope.parere.data){
	        			$scope.parere.data = new Date($scope.parere.data);
	        		}
	    			var inserito = false;
	    			for(var i = 0; i < $scope.attoSel.pareri.length; i++){
						if($scope.attoSel.pareri[i].id == $scope.parere.id){
							$scope.attoSel.pareri[i]  = parere;
							inserito = true;
							break;
						}
					}
	    			for(var i = 0; i < $scope.commissioniSel.length; i++){
	    				if($scope.commissioniSel[i] && $scope.commissioniSel[i].idAoo && $scope.parere && $scope.parere.aoo && $scope.parere.aoo.id && $scope.parere.aoo.id == $scope.commissioniSel[i].idAoo){
	    					$scope.commissioniSel[i].dataParere = $scope.parere.data;
	    					break;
	    				}
	    			}
	    			if(!inserito){
	    				$scope.attoSel.pareri.push(parere);
	    			}
	    			$scope.attoSel.doneMostraComm = false;
	    			$scope.ricercaAtti($scope.page);
	    			$scope.mostraCommissioni($scope.attoSel);
		        }, 
		        function(err) {
		        	ngToast.create(  { className: 'error', content: 'Errore di elaborazione. Si prega di riprovare, se l\'errore persiste contattare l\'assistenza.'  } );
		        });
	    }
    	
    	$scope.salvaEChiudiMascheraParere = function() {
    		$scope.saveParereCommissione();
    		$scope.closeMascheraParere();
    	}
    	
    	$scope.initParerePersonalizzato = function(){
    		if (angular.isDefined($scope.parere) && angular.isDefined($scope.parere.parereSintetico)  ) {
				if($scope.parere.parereSintetico == null || $scope.parere.parereSintetico.toLowerCase().indexOf('personalizzato')<0){
					$scope.parere.parerePersonalizzato = "";
				}
			}
    	};
    	
    	$scope.aggiungiParere = function(commissione) {
    		$scope.parere = {
					atto: {id: $scope.attoSel.id},
					aoo: {id: commissione.idAoo},
					profilo: $rootScope.profiloattivo,
    				tipoAzione: {
    					codice: $rootScope.configurationParams.codice_tipo_parere_commissioni_consiliari
    				},
    				origine:"C"
    			};
    		$scope.parere.createdBy = $rootScope.account.login;
			if(!$scope.attoSel.pareri){
				$scope.attoSel.pareri = [];
			}
			$scope.attoSel.nuovoParere = null;
			$scope.manageParere(commissione);
    	};
    	
    	$scope.inizializzaParereCommissione = function(parere){
    		if(!$scope.listaEsitoPareriCommissione || $scope.listaEsitoPareriCommissione.length < 1){
	    		$scope.listaEsitoPareriCommissione =  [];
	    		if($scope.esitiParere && $scope.esitiParere.length > 0){
	    			for(var i = 0; i<$scope.esitiParere.length; i ++){
	    				var app = $scope.esitiParere[i];
	    				if(app.tipo=='Commissione' && $scope.attoSel.tipoAtto.id==app.tipoAtto.id  && app.enabled == true){
	    					$scope.listaEsitoPareriCommissione.push(app);
	    				}
	    			}
	    		}
    		}
    	}
    	
    	$scope.getParereLeggibileByIdAooCommissione = function(idAoo){
    		var res = "";
    		if(idAoo){
	    		var p = $scope.getParereByIdAooCommissione(idAoo);
	    		if(p && p.parereSintetico){
	    			res = p.parereSintetico;
	    			$scope.inizializzaParereCommissione();
	    			if($scope.listaEsitoPareriCommissione && $scope.listaEsitoPareriCommissione.length > 0){
	    				for(var i = 0; i<$scope.listaEsitoPareriCommissione.length; i++){
	    					if($scope.listaEsitoPareriCommissione[i].codice == p.parereSintetico){
	    						res = $scope.listaEsitoPareriCommissione[i].valore;
	    						break;
	    					}
	    				}
	    			}
	    		}
    		}
    		return res;
    	}
    	
    	$scope.manageParere = function(commissione) {
    		$scope.confCommissioneSel = commissione;
    		if(!$scope.parere || $scope.confCommissioneSel.idAoo != $scope.parere.aoo.id){
    			var p = $scope.getParereByIdAooCommissione(commissione.idAoo);
        		$scope.parere  = jQuery.extend(true, {}, p);
    		}
    		if($scope.parere && $scope.parere.data){
    			$scope.parere.data = new Date($scope.parere.data);
    		}
    		$scope.inizializzaParereCommissione($scope.parere);
    		$scope.visualizzaAllegati = $scope.parere.id;
    		$('#mascheraPareri').modal('hide');
    		$('#mascheraParere').modal('show');
    	};
    	
    	$scope.closeMascheraParere = function(){
    		$scope.parere = null;
    		$scope.visualizzaAllegati = 0;
    		$('#mascheraParere').modal('hide');
    		$('#mascheraPareri').modal('show');
    	}
    	
    	$scope.getParereByIdAooCommissione = function(idAoo) {
    		if ($scope.attoSel.pareri) {
    			for (var cs = 0; cs < $scope.attoSel.pareri.length; cs++) {
    				var p = $scope.attoSel.pareri[cs];
    				if (p.annullato) {
    					continue;
    				}
    				if (idAoo == p.aoo.id && p.tipoAzione.codice ==
    					$rootScope.configurationParams.codice_tipo_parere_commissioni_consiliari) {
    					return p;
    				}
        		}	
    		}
    		return null; 
    	};
    	
    	$scope.parereAssente = function(idAoo) {
    		var res = $scope.getParereByIdAooCommissione(idAoo);
    		return res ? false : true;
    	};
    	
    	$scope.hasParereSintetico = function(idAoo) {
    		var res = $scope.getParereByIdAooCommissione(idAoo);
    		if(res && res.parereSintetico && res.parereSintetico.length > 0){
    			return true;
    		}else{
    			return false;
    		}
    	};
    	
    	$scope.confermaNonEspresso = function(configIncarico) {
    		$scope.confCommissioneSel = configIncarico;
    		$('#mascheraPareri').modal('hide');
    		$('#confermaNonEspresso').modal('show');
    	};
    	
    	$scope.modificaScadenza = function(configIncarico) {
    		$scope.confCommissioneSel = configIncarico;
    		$('#mascheraPareri').modal('hide');
    		$('#modificaScadenza').modal('show');
    	};
    	
    	$scope.inserisciScadenza = function() {
    		var configurazioneIncaricoAooDto = {
    				"idAoo": $scope.confCommissioneSel.idAoo,
    				"idConfigurazioneIncarico": $scope.idUpdate,
    			};
    		
    		configurazioneIncaricoAooDto.giorniScadenza = parseInt($scope.confCommissioneSel.giorniScadenza);
			configurazioneIncaricoAooDto.dataManuale = $scope.confCommissioneSel.dataManuale;
    		    		
    		ConfigurazioneIncarico.aggiornaScadenza(configurazioneIncaricoAooDto,  function () {
    			$('#modificaScadenza').modal('hide');
    			$scope.ricercaAtti($scope.page);
				$scope.gestisciCommissioni($scope.attoSel);
				ngToast.create(  { className: 'success', content:  'Scadenza aggiornata con successo'  } );
            }, 
            function(err) {
            	ngToast.create(  { className: 'error', content: 'Errore di elaborazione. Si prega di riprovare, se l\'errore persiste contattare l\'assistenza.'  } );
            	$scope.ricercaAtti($scope.page);
            });
    		
    	};
    	
    	if($.inArray('_pareriCommiss_taskSearch', localStorageService.keys()) >= 0 ) {
        	$scope.tempSearch = localStorageService.get('_pareriCommiss_taskSearch');
        }
    	$scope.ricercaAtti();

		$scope.isNumber= function (n) {
			return !isNaN(parseFloat(n)) && isFinite(n);
		}
    	
    	$scope.addDays = function(stringDate,days) {
    		if(!days){
    			days=0;
    		}
		   var date = new Date(stringDate);
		   date.setDate(date.getDate() + parseInt(days));
		  return date;
		}
});

