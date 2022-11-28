'use strict';

angular.module('cifra2gestattiApp')
    .controller('AooController', function ($rootScope,$scope, Aoo, Profilo, Assessorato, Indirizzo, TipoAoo, ParseLinks, $log,Upload,FileReader,TipoMateria) {
    	$scope.showButton = true;
    	$scope.searchable = true;
    	$scope.tipoAoos = [];
    	$scope.aoos = [];
    	$scope.profilosOrig = [];
    	$scope.profilos = [];
    	$scope.warnCodiceExists = false;
    	$scope.checkCodiceExists = function(aooid, codice){
    		Aoo.codiceExists({aooid:aooid, codice:codice}, function(risultato){
    			if(risultato.exists == 'y'){
    				$scope.warnCodiceExists = true;
    			}else{
    				$scope.warnCodiceExists = false;
    			}
    		});
    	}
    	
    	$scope.codiceAlertAndSave = function(aooCurrent){
    		if($scope.warnCodiceExists){
    			$('#codiceAlertAndSaveModal').modal('show');
    		}else{
    			$scope.save(aooCurrent);
    		}
    	};
    	
    	$log.debug("aooRoles:",$rootScope.aooRoles.toString());
        Profilo.getAllActiveMinimal({}, function(result, headers) {
    		angular.forEach(result, function(profilo, key) {
    			var find = false;
        		for (var i = 0, len = profilo.grupporuolo.hasRuoli.length; i < len; i++) {
        			if ($rootScope.aooRoles.indexOf(profilo.grupporuolo.hasRuoli[i].codice) !== -1){
        				find = true;
        				break;
        			}
    			}
        		
        		if(find == true){
        			
        			var res = result[key];
        			if(angular.isDefined(res.utente)){
        				res.descrizione = res.descrizione + " - " +  res.utente.cognome + " " + res.utente.nome;
        			}
        			
        			$scope.profilos.push(res)
        		}
        	});
        	
    		$scope.profilosOrig = angular.copy($scope.profilos);
    		
            $log.debug("Profili:",$scope.profilos);
        });
        $log.debug("Profili:",$scope.profilos);
        $scope.assessoratos = Assessorato.query({
        	stato: 0 //only enabled
        });
        $scope.tipoaoos = TipoAoo.query();
        
        $scope.specializzazioniAoo = [
    		{
    			codice:"Direzione Generale",
    			descrizione:"Direzione Generale"
    		},
    		{
    			codice:"",
    			descrizione:"Nessuna"
    		}
    	];
        
        $scope.tipomaterias = TipoMateria.query();
        $scope.criteriaAoo = { 
        		stato:{
        			id:0,
        			denominazione:"cifra2gestattiApp.aoo.filtro.stato.attive"
        		}
        };
        $scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaAoo);
        
        $scope.stati = [
        		{
        			id:0,
        			denominazione:"cifra2gestattiApp.aoo.filtro.stato.attive"
        		},
        		{
        			id:1,
        			denominazione:"cifra2gestattiApp.aoo.filtro.stato.disattivate"
        		},
        		{
        			id:2,
        			denominazione:"cifra2gestattiApp.aoo.filtro.stato.entrambe"
        		}
        ];
        
        TipoAoo.query({}, function(result, headers) {
            $scope.tipoAoos = result;
        });
    
    $scope.toggle = function(scope) {
        scope.toggle();
    };
    
    $scope.resetRicerca = function(){
    	$scope.showButton = true;
    	$scope.criteriaAoo = { 
        		stato:{
        			id:0,
        			denominazione:"cifra2gestattiApp.aoo.filtro.stato.attive"
        		}
        };
    	$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaAoo);
    	$scope.loadAll();
    };
    
    $scope.ricerca = function(){
    	$scope.showButton = true;
    	$scope.loadAll();
    };

        $scope.upload = function (files) {
            $log.debug(files);
            if (files && files.length) {
                for (var i = 0; i < files.length; i++) {
                   var file = files[i];
                     
                   FileReader.readAsDataURL(file, $scope).then(function (resp) {
                	   $scope.aooCurrent.logo=resp;
                	   $log.debug($scope.aooCurrent.logo);

                   }, function () {
                	   alert("Errore upload");
                   });
                }
            }
        };



    $scope.edit = function( entity ) {
    	$scope.searchable = false;
        $log.debug(entity);
          Aoo.get({id: entity.id}, function(result) {
        	  	$scope.profilos = [];
        	  	
                $scope.aooCurrent = result;
                if($scope.aooCurrent.profiloResponsabile && $scope.aooCurrent.profiloResponsabile.utente){
                	$scope.aooCurrent.profiloResponsabile.descrizione = $scope.aooCurrent.profiloResponsabile.descrizione + " - " +  $scope.aooCurrent.profiloResponsabile.utente.cognome + " " + $scope.aooCurrent.profiloResponsabile.utente.nome;
    			}
                $scope.aooCurrent.edit=true;
                entity.edit=true;
                
                angular.forEach($scope.profilosOrig, function(profilo, key) {
        	  		if(profilo.aoo.id == $scope.aooCurrent.id){
        	  			$scope.profilos.push(profilo);
        	  		}
        	  	});
                
            });
    }; 


    $scope.cancelEdit = function( entity ) {
        $log.debug(entity);
        entity.edit=false;
        $scope.aooCurrent.edit=false;
        $scope.refresh();
    };
    
    $scope.addAoo= function(   ) {
        if( !angular.isDefined( $scope.aoos  ) ||  $scope.aoos  === null ){
            $scope.aoos  = [];
        }

        $scope.profilos = [];
        $scope.aooCurrent = {denominazione:'', dataType:'aoo' , edit:true };
        $scope.aoos.push(  $scope.aooCurrent  );    

        
    };
    
   $scope.addSottoAoo= function( aoo ) {
        if( !angular.isDefined( aoo.sottoAoo  ) ||  aoo.sottoAoo  === null ){
            aoo.sottoAoo  = [];
        }
        $scope.profilos = $scope.profilosOrig;
        $scope.aooCurrent = {denominazione:'',identitavisiva:aoo.identitavisiva,logo:aoo.logo, dataType:'aoo' ,aooPadre:{id: aoo.id, descrizione: aoo.descrizione  } , edit:true, hasAssessorati:[] };
        
        aoo.sottoAoo.push(  $scope.aooCurrent  );    

        
    };
    
    $scope.addSottoUo= function( aoo ) {
        if( !angular.isDefined( aoo.sottoAoo  ) ||  aoo.sottoAoo  === null ){
            aoo.sottoAoo  = [];
        }
        $scope.profilos = $scope.profilosOrig;
        $scope.aooCurrent = {denominazione:'',uo:1, identitavisiva:aoo.identitavisiva,logo:aoo.logo, dataType:'aoo' ,aooPadre:{id: aoo.id, descrizione: aoo.descrizione  } , edit:true, hasAssessorati:[] };
        
        aoo.sottoAoo.push(  $scope.aooCurrent  );    

        
    };
    
    $scope.annullaParent = 

    $scope.options = {
        accept: function(sourceNode, destNodes, destIndex) {
            var dataType = sourceNode.$element.attr('data-type');
            var destType = destNodes.$element.attr('data-type');
            
            $log.debug("dataType:"+dataType);
            $log.debug("destType:"+destType);

            return (dataType == 'aoo-drag' && destType == 'aoo-drop');  
        },
        dropped: function(event) {
           var sourceNode = event.source.nodeScope;
           var destNodes = event.dest.nodesScope;
          
           $log.debug(sourceNode.$modelValue);

           var aooPadre = destNodes.$nodeScope.$modelValue;
           var aoo = sourceNode.$modelValue;

           if(angular.isDefined(aooPadre) && aooPadre.id!== null) {
        	   if ( (aoo.aooPadre != null) && (aoo.aooPadre.id == aooPadre.id)) {
        		   return;
        	   }
        	   
        	   if ( window.confirm('Sei sicuro di volere spostare la aoo/ufficio?')) {
        		   aoo.aooPadre = { id: aooPadre.id};
        		   $scope.saveSposta( aoo );
        	   }else{
        		   $scope.refresh();
        	   }
           }
           else {
        	   if (!angular.isDefined(aoo.aooPadre) || (aoo.aooPadre.id == null)) {
        		   return;
        	   }
        	   
        	   if ( window.confirm('Sei sicuro di volere spostare la aoo/ufficio?')) {
        		   delete aoo.aooPadre;
        		   $scope.saveSposta( aoo );
        	   }else{
        		   $scope.refresh();
        	   }
           }
	    } 
	   };

       $scope.loadAll = function() {
        	$scope.searchable = true;
        	$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaAoo);
        	$log.debug('search parameters:',$scope.tempSearch);
        	if(angular.isDefined($scope.tempSearch.tipologia)){
        		$scope.tempSearch.tipologia = $scope.tempSearch.tipologia.id;
        	}
        	if(angular.isDefined($scope.tempSearch.assessorato)){
        		$scope.tempSearch.assessorato = $scope.tempSearch.assessorato.id;
        	}
        	
        	if(angular.isDefined($scope.tempSearch.stato)){
        		
        		$scope.tempSearch.stato = $scope.tempSearch.stato.id;
//        		if($scope.showButton == true){
//        			$scope.tempSearch.stato = null;
//        		}
        	}
        	
        	Aoo.organigramma( $scope.tempSearch, function(result, headers) {
        		$scope.aoos = result;
        		$log.debug("AOOS:",result);
        		
        	});
        };

 

        $scope.loadPage = function(page) {
            $scope.loadAll();
        };

        $scope.loadAll();

		$scope.saveSposta = function(aoo) {
			Aoo.updateParent(aoo, function() {
				$scope.refresh();
			});
		};
		
		$scope.saveAnnullaParent = function(aoo) {
			if ( window.confirm('Sei sicuro di volere spostare la aoo/ufficio?')) {
			Aoo.annullaParent(aoo, function() {
				$scope.refresh();
			});
			}else{
     		   $scope.refresh();
     	   }
		};
		
        $scope.save = function ( ) {
        	$('#codiceAlertAndSaveModal').modal('hide');
        	if (!$scope.aooCurrent.indirizzo) {
        		$scope.aooCurrent.indirizzo = null;
        	}
            if ($scope.aooCurrent.id != null) {
                Aoo.update( $scope.aooCurrent,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Aoo.save( $scope.aooCurrent,
                    function () {
                        $scope.refresh();
                    });
            }
        };
        
        
        $scope.spostaARadice = function (id) {
        	Aoo.get({id: id}, function(result) {
                $scope.aoo = result;
                $('#spostaARadiceAooConfirmation').modal('show');
            });
        };

        $scope.disabilita = function (id) {
        	Aoo.get({id: id}, function(result) {
                $scope.aoo = result;
                $('#disabilitaAooConfirmation').modal('show');
            });
        };
        
        
        $scope.abilita = function (id) {
            Aoo.get({id: id}, function(result) {
                $scope.aoo = result;
                $('#abilitaAooConfirmation').modal('show');
            });
        };

        $scope.confirmSpostaARadice = function (id) {
            Aoo.annullaParent({id: id},
                function () {
                    $scope.loadAll();
                    $('#spostaARadiceAooConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        
        $scope.confirmDisabilita = function (id) {
            Aoo.disabilita({id: id},
                function (ris) {
                    $('#disabilitaAooConfirmation').modal('hide');
                    if(!ris.warnCodice){
	                    $scope.loadAll();
	                    $scope.clear();
            		}else{
            			$('#warnDisabilitazioneAoo').modal('show');
            		}
                });
            
        };
        
        $scope.confirmAbilita = function (id) {
            Aoo.abilita({id: id},
                function (ris) {
            		$('#abilitaAooConfirmation').modal('hide');
            		if(!ris.warnCodice){
	                    $scope.loadAll();
	                    $scope.clear();
            		}else{
            			$('#warnAbilitazioneCodice').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.files=[];
            $scope.aooCurrent = {codice: null, descrizione: null, telefono: null, fax: null, email: null, pec: null, identitavisiva: null,  id: null, indirizzo:{} };
        };
        
        /**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
    });
