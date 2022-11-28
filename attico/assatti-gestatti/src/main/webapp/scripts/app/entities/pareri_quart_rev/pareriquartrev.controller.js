'use strict';

angular.module('cifra2gestattiApp')
    .controller('PareriQuartRevController', function ($scope, localStorageService,ngToast, SedutaGiuntaConstants,TaskDesktop,
    		$log, Atto, TipoAtto, Upload, DocumentoInformatico, Parere, ConfigurazioneIncarico, ParseLinks, $rootScope, Lavorazione, EsitoPareri) {
    	
    	$scope.page = 1;
    	$scope.tempSearch = {};
        $scope.loading = false;
        
        $scope.attos = [];
        $scope.attoSel = null;
        $scope.visualizzaAllegati = 0;
        $scope.listUffici = [];
        $scope.listUfficiFiltrata = [];
        $scope.ufficiCaricati = false;
        $scope.esitiParere = [];

        
        $scope.parereNonEspresso = function(parere){
        	$('#gestionePareri').modal('hide');
        	var siFunction = function(){
        		Parere.nonEspresso({id: parere.id}, function (retParere) {
        			var idx = -1;
        			for(var i = 0; i < $scope.pareri.length; i++){
						if($scope.pareri[i].id == retParere.id){
							idx = i;
							break;
						}
					}
        			if(idx > -1){
        				$scope.pareri[idx] = retParere;
        			}
        			idx = -1;
        			for(var i = 0; i < $scope.attoSel.pareri.length; i++){
						if($scope.attoSel.pareri[i].id == retParere.id){
							idx = i;
							break;
						}
					}
        			if(idx > -1){
        				$scope.attoSel.pareri[idx] = retParere;
        			}
        			$('#genericMessage').modal('hide');
            		$('#gestionePareri').modal('show');
                });
        	}
        	var noFunction = function(){
        		$('#genericMessage').modal('hide');
        		$('#gestionePareri').modal('show');
        	}
        	$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true, siFunction: siFunction, noFunction:noFunction, body:'Conferma che il parere sia indicato come non espresso? Eventuali allegati del parere inseriti in precedenza saranno rimossi.'});
        };
        
        $scope.cancellaParere = function(parere){
        	$('#gestionePareri').modal('hide');
        	
        	var siFunction = function(){
        		Parere.delete({id: parere.id}, function () {
        			var idx = -1;
        			for(var i = 0; i < $scope.pareri.length; i++){
						if($scope.pareri[i].id == parere.id){
							idx = i;
							break;
						}
					}
        			if(idx > -1){
        				$scope.pareri.splice(idx, 1);
        			}
        			idx = -1;
        			for(var i = 0; i < $scope.attoSel.pareri.length; i++){
						if($scope.attoSel.pareri[i].id == parere.id){
							idx = i;
							break;
						}
					}
        			if(idx > -1){
        				$scope.attoSel.pareri.splice(idx, 1);
        			}
        			$scope.calcolaListUfficiFiltrata($scope.listUffici);
        			ngToast.create(  { className: 'success', content:  'Parere eliminato con successo'  } );
        			$('#genericMessage').modal('hide');
            		$('#gestionePareri').modal('show');
                });
        	}
        	var noFunction = function(){
        		$('#genericMessage').modal('hide');
        		$('#gestionePareri').modal('show');
        	}
        	$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true, siFunction: siFunction, noFunction:noFunction, body:'Conferma la cancellazione del parere?'});
        };
        
        $scope.loadUffici = function(){
        	if(!$scope.ufficiCaricati){
        		$scope.ufficiCaricati = true;
        	   	Lavorazione.confTaskAoos({codiceConfigurazioneTask: $rootScope.configurationParams.codice_configurazione_parere_quartrev}, function(list){
        	   		$scope.listUffici = list;
        	   		$scope.calcolaListUfficiFiltrata(list);
        	   	});
        	}else{
        		$scope.calcolaListUfficiFiltrata($scope.listUffici);
        	}
        }
        
        $scope.calcolaListUfficiFiltrata = function(listUffici){
        	$scope.listUfficiFiltrata = [];
        	if($scope.pareri && listUffici){
        		var aoo = null;
        		var exists = false;
        		for(var i = 0; i < listUffici.length; i++){
        			aoo = listUffici[i];
        			exists = false;
        			for(var j = 0; j < $scope.pareri.length; j++){
        				if(aoo.id == $scope.pareri[j].aoo.id){
        					exists = true;
        					break;
        				}
        			}
        			if(!exists){
        				$scope.listUfficiFiltrata.push(aoo);
        			}
        		}
        	}else{
        		 $scope.listUfficiFiltrata = [];
        	}
        };
        
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
    	$scope.criteria.per_page = 10;
    	$scope.criteria.ordinamento = "codiceCifra";
    	$scope.criteria.tipoOrinamento = "desc";
    	
    	$scope.ricercaAtti = function() {
    		$scope.loading = true;
    		$scope.attos = [];
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
    		
    		TaskDesktop.searchAttiCommissioni({page: $scope.page, per_page: 10, organo: 'C', tipoRicerca: 'QUART_REV'},
    				 $scope.tempSearch, function(result, headers) {	
    			$scope.loading = false;
    	        $scope.attos = result;
    	        $log.debug("atto.search:",$scope.attos);
    	        
    	        $scope.links = ParseLinks.parse(headers('link'));
    	        $scope.totalResultAtti=headers('x-total-count') ;
    	        if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
    				localStorageService.set('_pareriQuartRev_taskSearch', $scope.tempSearch);
    			}
    	    });
    	};
    	
    	$scope.resetRicerca = function(){
	    	$scope.page = 1;
	    	$scope.tempSearch = {};
	    	
	    	$scope.ricercaAtti();
    	}
    	
    	$scope.loadPageAtto = function(page) {
    		$scope.page = page;
    		$scope.ricercaAtti();
    	};
    	
    	$scope.mostraMotivazione = function(pareri) {
    		var output = "";
    		if (pareri) {
    			for (var pc = 0; pc < pareri.length; pc++) {
    				if (pareri[pc].annullato) {
    					continue;
    				}
    				if ($rootScope.configurationParams.codice_tipo_parere_quartieri_revisori == pareri[pc].tipoAzione.codice) {
    					output += pareri[pc].parere + "<br/>";
    				}
    			}
    		}
    		return output;
    	}
    	
    	$scope.aggiungiParere = function(commissione, dataInvio, dataScadenza) {
			$scope.parere = {
					atto: {id: $scope.attoSel.id},
					aoo: commissione,
					profilo: $rootScope.profiloattivo,
					dataScadenza: dataScadenza,
    				tipoAzione: {
    					codice: $rootScope.configurationParams.codice_tipo_parere_quartieri_revisori
    				},
    				origine:"Q",
					dataInvio: dataInvio
    			};
    		Parere.save($scope.parere, function (json) {
				$scope.parere.id = json.parereId;
				$scope.parere.createdBy = $rootScope.account.login;
				if(!$scope.attoSel.pareri){
					$scope.attoSel.pareri = [];
				}
				$scope.attoSel.nuovoParere = null;
				$scope.attoSel.dataInvioNew = null;
				$scope.attoSel.dataScadenzaNew = null;
				$scope.attoSel.pareri.push($scope.parere);
				$scope.gestisciPareri($scope.attoSel);
			});
    	};
    	
    	$scope.inizializzaDataEffettivaRichiesta = function() {
    		if(!$scope.attoSel.dataInvioNew){
    			$scope.attoSel.dataInvioNew = new Date();
    		}
    	}
    	
    	$scope.gestisciPareri = function(atto) {
    		$scope.attoSel = atto;
    		$scope.parere = null;
    		$scope.pareri = [];

			EsitoPareri.query({tipo:'Quartieri e Rev. Contabili'}, function(result){
				$scope.listaEsitoPareriQuartieri =  [];
				for(var i = 0; i<result.length; i ++){
					var app = result[i];
					if(app.tipo=='Quartieri e Rev. Contabili' && $scope.attoSel.tipoAtto.id==app.tipoAtto.id  && app.enabled == true){
						$scope.listaEsitoPareriQuartieri.push(app);
					}

				}
			});
    		
    		if ($scope.attoSel.pareri) {
    			for (var pq = 0; pq < $scope.attoSel.pareri.length; pq++) {
    				var curParere = $scope.attoSel.pareri[pq];
    				if (curParere.annullato) {
    					continue;
    				}
    				if (curParere.origine == 'Q' && curParere.tipoAzione.codice ==
    						$rootScope.configurationParams.codice_tipo_parere_quartieri_revisori) {
    					$scope.pareri.push(curParere);
    				}
    			}
    		}
    		$scope.loadUffici();
    		$('#gestionePareri').modal('show');
    	    
    	};
    	
    	$scope.modificaParere = function(parere) {
    		
    		$scope.parere  = jQuery.extend(true, {}, parere);
    		
    		$scope.visualizzaAllegati = parere.id;
    		$scope.listaEsitoPareriQuartieri  = [];
    		EsitoPareri.query({tipo:'Quartieri e Rev. Contabili'}, function(result){
    			$scope.listaEsitoPareriQuartieri =  [];
    			for(var i = 0; i<result.length; i ++){
    				var app = result[i];
    				if(app.tipo=='Quartieri e Rev. Contabili' && $scope.attoSel.tipoAtto.id==app.tipoAtto.id  && app.enabled == true){
    					$scope.listaEsitoPareriQuartieri.push(app);
    				}
    				
    			}
    		});
    		
    		$('#gestionePareri').modal('hide');
    		$('#mascheraParere').modal('show');
    	};
    	
    	$scope.closeMascheraParere = function(){
    		$scope.parere = null;
    		$scope.visualizzaAllegati = 0;
    		$('#mascheraParere').modal('hide');
    		$('#gestionePareri').modal('show');
    	}
    	
    	$scope.fileDroppedParere = function (files,event,rejectedFiles, parere ) {
    	    $log.debug(event);

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
    	        	$log.debug( data );
    	        	$scope.parere.allegati = data;
    	        	for(var i = 0; i < $scope.pareri.length; i++){
						if($scope.pareri[i].id == $scope.parere.id){
							$scope.pareri[i]  = $scope.parere;
							break;
						}
					}
    	        });
    	      }
    	    };
    	    
	    $scope.deleteAllegatoParere = function (allegati, index) {
	    	if(allegati!=undefined && allegati!=null && allegati[index]!=undefined && allegati[index]!=null && allegati[index].id!=undefined && allegati[index].id!=null){
	    		DocumentoInformatico.delete({id: allegati[index].id}, function () {
	    			allegati.splice(index,1);
	    			$scope.parere.allegati = allegati;
    	        	for(var i = 0; i < $scope.pareri.length; i++){
						if($scope.pareri[i].id == $scope.parere.id){
							$scope.pareri[i]  = $scope.parere;
							break;
						}
					}
	    	    });
	    	}
	    };
	    
	    $scope.saveParereQuartRev = function() {
	    	$scope.parere.atto = {id: $scope.parere.atto.id}
	    	ConfigurazioneIncarico.saveParere($scope.parere,  
		    	function (parere) {
	    			ngToast.create(  { className: 'success', content:  'Parere salvato con successo'  } );
	    			for(var i = 0; i < $scope.pareri.length; i++){
						if($scope.pareri[i].id == $scope.parere.id){
							$scope.pareri[i]  = parere;
							$scope.parere = parere;
							break;
						}
					}
		        }, 
		        function(err) {
		        	ngToast.create(  { className: 'error', content: 'Errore di elaborazione. Si prega di riprovare, se l\'errore persiste contattare l\'assistenza.'  } );
		        });
	    	$('#mascheraParere').modal('hide');
	    	$('#gestionePareri').modal('show');
	    }
	    
	    /**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
	    
	    if($.inArray('_pareriQuartRev_taskSearch', localStorageService.keys()) >= 0){
        	$scope.tempSearch = localStorageService.get('_pareriQuartRev_taskSearch');
        }
	    $scope.ricercaAtti();
	    
	    $scope.updateParereQR = function(){
			if (angular.isDefined($scope.parere) && angular.isDefined($scope.parere.parereSintetico)  ) {
				
				if($scope.parere.parereSintetico && $scope.parere.parereSintetico.toLowerCase().indexOf('personalizzato')<0){
					$scope.parere.parerePersonalizzato = "";
				}
			}
		};
		
		EsitoPareri.query({tipo:'Quartieri e Rev. Contabili'}, function(result){
    		$scope.esitiParere = result;
		});

		$scope.getParereLeggibile = function(parSint){
			var res = "";
			if(parSint){
				res = parSint;
				if($scope.listaEsitoPareriQuartieri && $scope.listaEsitoPareriQuartieri.length > 0){
					for(var i = 0; i<$scope.listaEsitoPareriQuartieri.length; i++){
						if($scope.listaEsitoPareriQuartieri[i].codice == parSint){
							res = $scope.listaEsitoPareriQuartieri[i].valore;
							break;
						}
					}
				}
			}
			return res;
		}
		
		$scope.mostraPareriQ = function(pareri) {
    		var output = "";
    		if (pareri) {
    			for (var pc = 0; pc < pareri.length; pc++) {
    				if (pareri[pc].annullato) {
    					continue;
    				}
    				if ($rootScope.configurationParams.codice_tipo_parere_quartieri_revisori == pareri[pc].tipoAzione.codice) {
    					if($scope.esitiParere && $scope.esitiParere.length > 0){
    	    				var inserito = false;
    						if(pareri[pc].parereSintetico && pareri[pc].parereSintetico.length > 0){
	    	    				for(var i = 0; i<$scope.esitiParere.length; i++){
	    	    					if($scope.esitiParere[i].codice == pareri[pc].parereSintetico){
	    	    						inserito = true;
	    	    						if(output!==""){
	    	    	    					output += "<br/>"
	    	    	    				}
	    	    						output += pareri[pc].aoo.descrizione + " ("+ $scope.esitiParere[i].valore+")";
	    	    						break;
	    	    					}
	    	    				}
    						}else{
    							if(output!==""){
    		    					output += "<br/>"
    		    				}
    							output += pareri[pc].aoo.descrizione;
    							inserito = true;
    						}
    						if(!inserito){
    							if(output!==""){
    		    					output += "<br/>"
    		    				}
    							output += pareri[pc].aoo.descrizione + " ("+ pareri[pc].parereSintetico+")";
    						}
    	    			}
    				}
    			}
    		}
    		return output;
    	}
		
		
	    
});
