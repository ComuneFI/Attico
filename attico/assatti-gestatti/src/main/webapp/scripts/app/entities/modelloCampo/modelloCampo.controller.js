'use strict';

angular.module('cifra2gestattiApp')
    .controller('ModelloCampoController', function (TipoAtto, $scope, ModelloCampo,$stateParams, Profilo, ParseLinks, $rootScope,
    		ProfiloAccount, ConvertService, Upload, $log, Principal, $state, Aoo) {
    	
        $scope.modelloCampos = [];
        $scope.profiliOfUser=[];
        $scope.profilosSearch=[];
       
        $scope.page = 1;
        $scope.modelloCampoSearch = {};
        $scope.tempSearch = {};
        $scope.isAdmin = false;
        $scope.isReferenteTecnico = false;
        $scope.tipiAtto = [];
        $scope.uffici = [];
        $scope.modelloCampo = {};
        
        var admin = [];
	  	admin.push("ROLE_ADMIN");
	  	admin.push("ROLE_AMMINISTRATORE_RP");
	  	if(Principal.isInAnyRole(admin)){
	  		$scope.isAdmin = true;
	  	}else if(ProfiloAccount.isInAnyRole(['ROLE_REFERENTE_TECNICO'])){
	  		$scope.isReferenteTecnico = true;
	  	}
        
        if($scope.isAdmin){
        	Aoo.getMinimal(function(result) {
	        	if(result && result.length > 0){
	        		var aoo = null;
	        		for(var i = 0; i<result.length; i++){
	        			aoo = result[i];
	        			$scope.uffici.push({id:aoo.id, codice:aoo.codice, descrizione:aoo.descrizione});
	        		}
	        	}
	        });
    	}else if($scope.isReferenteTecnico){
    		Aoo.queryRicorsiva({id: $rootScope.profiloattivo.aoo.id}, function(result){
    			if(result && result.length > 0){
    				var aoo = null;
	        		for(var i = 0; i<result.length; i++){
	        			aoo = result[i];
	        			$scope.uffici.push({id:aoo.id, codice:aoo.codice, descrizione:aoo.descrizione});
	        		}
    			}
    		});
    	}
        
        $scope.initTipiAttoProfilo = function(profilo){
        	$scope.tipiAttoProfilo = [];
        	for(var i = 0; i<profilo.tipiAtto.length; i++){
        		$scope.tipiAttoProfilo.push(profilo.tipiAtto[i]);
        	}
        	$scope.tipiAttoProfilo.push({id:0, descrizione:'Non definito'});
        	if(!$scope.tipoVerbale){
        		$scope.modelloCampo.tipoAtto = null;
        	}
        };
        
        $scope.tipoVerbale = false;
        
        $scope.tipoCampoChanged = function(tipoCampo){
        	$scope.tipoVerbale = true;
        	var isVerbale = false;
        	$scope.tipoCampos.forEach(function(tipo){
        		if(tipo.target == tipoCampo){
        			if(tipo.isVerbale){
                		$scope.modelloCampo.tipoAtto={id:0, descrizione:'Non definito'};
                		isVerbale = true;
                	}
        		}
        	});
        	$scope.tipoVerbale = isVerbale;
        };
        
        TipoAtto.query({}, function(result){
        	$scope.tipiAtto = result;
        	$scope.tipiAtto.push({id:0, descrizione:'Non definito'});
        });
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.modelloCampoSearch = {};
        	$scope.tempSearch = {};
        	if($scope.isAdmin){
            	$scope.loadAll();
        	}else if($scope.isReferenteTecnico){
            	$scope.loadAll(undefined, null, $rootScope.profiloattivo.aoo.id);
            }else{
            	$scope.loadAll(undefined, $rootScope.account.utente.id);
            }
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/* copio tempSearch e suoi elementi in modelloCampoSearch */
        	$scope.modelloCampoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	if($scope.isAdmin){
        		$scope.loadAll($scope.tempSearch);
            }else if($scope.isReferenteTecnico){
            	$scope.loadAll($scope.tempSearch, null, $rootScope.profiloattivo.aoo.id);
            }else{
            	$scope.loadAll($scope.tempSearch, $rootScope.account.utente.id);
            }
        };
        
	  	if(Principal.isInAnyRole(admin)){
	        Profilo.query({},function(results){
	        	$scope.profilosSearch = results;
	        	$scope.profilosSearch.unshift({'id':'0', 'descrizione':'Tutti'});
	        });
	  	}else{
	  		/*Profilo.getProfiliOfUser({utenteid: $rootScope.account.utente.id},function(results){
	  			$scope.profiliOfUser = results;
	        	angular.copy(results, $scope.profilosSearch);
	        	$scope.profilosSearch.unshift({'id':'0', 'descrizione':'Tutti'});
	        });*/
	  		$scope.profiliOfUser = [];
  			$scope.profiliOfUser.push($rootScope.profiloattivo);
  			angular.copy($scope.profiliOfUser, $scope.profilosSearch);
        	$scope.profilosSearch.unshift({'id':'0', 'descrizione':'Tutti'});
	  	}
	  	
	  	
        if(ProfiloAccount.isInAnyRole(['ROLE_AMMINISTRATORE_RP'])){
            $log.debug("ROLE_AMMINISTRATORE_RP - id aoo:"+$rootScope.profiloattivo.aoo.id);
            $scope.profilos = Profilo.getByAooId({aooId:$rootScope.profiloattivo.aoo.id});
        }
        else {
            $scope.profilos = [$rootScope.profiloattivo];
        }

        $scope.tipoCampos = [
        	 { target:"dispositivo" , icon:"fa fa-gear", title:"Dispositivo", description: "Dispositivo", isVerbale:false  },
        	 { target:"garanzieRiservatezza" , icon:"fa fa-lock", title:"Garanzia alla Riservatezza", description: "Garanzia alla Riservatezza", isVerbale:false  },
        	 { target:"motivazione" , icon:"fa fa-question", title:"Motivazione", description: "Motivazione", isVerbale:false  },
        	 { target:"preambolo" , icon:"fa fa-flag", title:"Preambolo", description: "Preambolo", isVerbale:false  },
        	 { target:"domanda" , icon:"fa fa-question", title:"Testo", description: "Testo", isVerbale:false  },
        	 { target:"preambolo_odl" , icon:"fa fa-question", title:"Preambolo ODL", description: "Preambolo ODL", isVerbale:false  },
        	 { target:"preambolo_odg" , icon:"fa fa-question", title:"Preambolo ODG", description: "Preambolo ODG", isVerbale:false  }
             
           // { target:"domanda" , icon:"fa fa-gear", title:"Domanda",
			// description: "Domanda", isVerbale:false },
           // { target:"divulgazione" , icon:"fa fa-exchange",
			// title:"Divulgazione", description: "Divulgazione",
			// isVerbale:false },
           // { target:"informazioniAnagraficoContabili" , icon:"fa fa-lock",
			// title:"Informazioni Anagrafico Contabili", description:
			// "Informazioni Anagrafico Contabili", isVerbale:false },
           // { target:"adempimentiContabili" , icon:"fa fa-lock",
			// title:"Adempimenti Contabili", description: "Adempimenti
			// Contabili", isVerbale:false },
           // { target:"dichiarazioni" , icon:"fa fa-lock",
			// title:"Dichiarazioni", description: "Dichiarazioni",
			// isVerbale:false },
           // { target:"verbale_narrativa" , icon:"fa fa-lock", title:"Testo
			// del Verbale", description: "Testo del Verbale", isVerbale:true },
           // { target:"verbale_note" , icon:"fa fa-lock", title:"Note Finali
			// del Verbale", description: "Note Finali del Verbale",
			// isVerbale:true },
           // { target:"relazioneTecnica" , icon:"fa fa-flag", title:"Relazione
			// Tecnica", description: "Relazione Tecnica", isVerbale:false },
           // { target:"articolato" , icon:"fa fa-question",
			// title:"Articolato", description: "Articolato", isVerbale:false },
           // { target:"refertoTecnico" , icon:"fa fa-lock", title:"Referto
			// Tecnico", description: "Referto Tecnico", isVerbale:false }
        ];

        $scope.getTitleTipoCampo = function(target){
        	var risultato = "";
        	for(var i = 0; i<$scope.tipoCampos.length; i++){
        		if($scope.tipoCampos[i].target == target){
        			risultato = $scope.tipoCampos[i].title;
        			break;
        		}
        	}
        	return risultato;
        };
        
        $scope.summernoteOptions = {
            height: 300,
            focus: false,
            airMode: false,
            lang: "it-IT",
            toolbar: [
                    ['edit',['undo','redo']],
                    ['headline', ['style']],
                    ['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
                    ['fontface', ['fontname']],
                    ['textsize', ['fontsize']],
                    ['fontclr', ['color']],
                    ['alignment', ['ul', 'ol', 'paragraph', 'lineheight']],
                    ['height', ['height']],
                    ['table', ['table']],
                    /*['insert', ['picture','hr','pagebreak']],*/
				  	['insert', ['hr','pagebreak']],
                    ['view', ['fullscreen', 'codeview']],
                    ['cifra2', ['omissis','proprieta']]
                    ]       
        };

        $scope.importing = false;

        $scope.fileImportDropped = function (files,event,rejectedFiles) {
            $log.debug(event);

            if(event.type !== "click"){
              $scope.importing = true;
              
              if(rejectedFiles.length > 0){
		    	$rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, 'image/*,application/*');
  	    		return;
  	    	  }
              
              Upload.upload({
                url: ConvertService.extract,
                headers : {
                  'Authorization': 'Bearer '+ $rootScope.accessToken
                },
                file: files[0]
              }).progress(function (evt) {
                 
              }).success(function (data, status, headers, config) {
                 
                  $scope.modelloCampo.testo = data.body;
                  $scope.importing = false;
                  $log.debug("import ok");
              });
            }
        };

        $scope.loadAll = function(searchObject, utenteid, aooIdReferente) {
        	var profiloScelto = null;
        	var tipoCampoSelezionato = null;
        	var tipoAtto = null;
        	if(angular.isDefined($stateParams.flag)){
        		if(searchObject!=undefined && searchObject!=null){
        			if(searchObject.profilo != undefined && searchObject.profilo != null && searchObject.profilo.id != undefined && searchObject.profilo.id != null){
        				profiloScelto = searchObject.profilo.id;
        			}
        			if(searchObject.tipoAtto != undefined && searchObject.tipoAtto != null && searchObject.tipoAtto.id != undefined && searchObject.tipoAtto.id != null){
        				tipoAtto = searchObject.tipoAtto.id;
        			}
        			if(searchObject.tipoCampo != undefined && searchObject.tipoCampo != null && searchObject.tipoCampo.target != undefined && searchObject.tipoCampo.target != null){
        				tipoCampoSelezionato = searchObject.tipoCampo.target;
        			}
	        		ModelloCampo.getAllGlobal({page: $scope.page, per_page: 10, tipoAttoId:tipoAtto, codice: searchObject.codice, titolo:searchObject.titolo, tipoCampo:tipoCampoSelezionato, profilo:profiloScelto, aoo:searchObject.aoo, utenteid:utenteid, aooIdReferente: aooIdReferente}, function(result, headers) {
	                    $scope.links = ParseLinks.parse(headers('link'));
	                    $scope.modelloCampos = result;
	                });
        		}else{
        			if($scope.modelloCampoSearch.tipoAtto != undefined && $scope.modelloCampoSearch.tipoAtto != null && $scope.modelloCampoSearch.tipoAtto.id != undefined && $scope.modelloCampoSearch.tipoAtto.id != null){
        				tipoAtto = $scope.modelloCampoSearch.tipoAtto.id;
        			}
        			if($scope.modelloCampoSearch.profilo != undefined && $scope.modelloCampoSearch.profilo != null && $scope.modelloCampoSearch.profilo.id != undefined && $scope.modelloCampoSearch.profilo.id != null){
        				profiloScelto = $scope.modelloCampoSearch.profilo.id;
        			}
        			if($scope.modelloCampoSearch.tipoCampo != undefined && $scope.modelloCampoSearch.tipoCampo != null && $scope.modelloCampoSearch.tipoCampo.target != undefined && $scope.modelloCampoSearch.tipoCampo.target != null){
        				tipoCampoSelezionato = $scope.modelloCampoSearch.tipoCampo.target;
        			}
        			ModelloCampo.getAllGlobal({page: $scope.page, per_page: 10, aoo: $scope.modelloCampoSearch.aoo, tipoAttoId:tipoAtto, codice: $scope.modelloCampoSearch.codice, titolo:$scope.modelloCampoSearch.titolo, tipoCampo:tipoCampoSelezionato, profilo:profiloScelto, utenteid:utenteid, aooIdReferente: aooIdReferente}, function(result, headers) {
	                    $scope.links = ParseLinks.parse(headers('link'));
	                    $scope.modelloCampos = result;
	                });
        		}
        	}
        	else{
        		$log.debug("getAll");
        		if(searchObject!=undefined && searchObject!=null){
        			if(searchObject.profilo != undefined && searchObject.profilo != null && searchObject.profilo.id != undefined && searchObject.profilo.id != null){
        				profiloScelto = searchObject.profilo.id;
        			}
        			if(searchObject.tipoAtto != undefined && searchObject.tipoAtto != null && searchObject.tipoAtto.id != undefined && searchObject.tipoAtto.id != null){
        				tipoAtto = searchObject.tipoAtto.id;
        			}
        			if(searchObject.tipoCampo != undefined && searchObject.tipoCampo != null && searchObject.tipoCampo.target != undefined && searchObject.tipoCampo.target != null){
        				tipoCampoSelezionato = searchObject.tipoCampo.target;
        			}
	        		ModelloCampo.query({page: $scope.page, per_page: 10, tipoAttoId:tipoAtto, codice: searchObject.codice, titolo:searchObject.titolo, tipoCampo:tipoCampoSelezionato, profilo:profiloScelto}, function(result, headers) {
	                    $scope.links = ParseLinks.parse(headers('link'));
	                    $scope.modelloCampos = result;
	                });
        		}else{
        			if($scope.modelloCampoSearch.profilo != undefined && $scope.modelloCampoSearch.profilo != null && $scope.modelloCampoSearch.profilo.id != undefined && $scope.modelloCampoSearch.profilo.id != null){
        				profiloScelto = $scope.modelloCampoSearch.profilo.id;
        			}
        			if($scope.modelloCampoSearch.tipoAtto != undefined && $scope.modelloCampoSearch.tipoAtto != null && $scope.modelloCampoSearch.tipoAtto.id != undefined && $scope.modelloCampoSearch.tipoAtto.id != null){
        				tipoAtto = $scope.modelloCampoSearch.tipoAtto.id;
        			}
        			if($scope.modelloCampoSearch.tipoCampo != undefined && $scope.modelloCampoSearch.tipoCampo != null && $scope.modelloCampoSearch.tipoCampo.target != undefined && $scope.modelloCampoSearch.tipoCampo.target != null){
        				tipoCampoSelezionato = $scope.modelloCampoSearch.tipoCampo.target;
        			}
        			ModelloCampo.query({page: $scope.page, per_page: 10, tipoAttoId: tipoAtto, codice: $scope.modelloCampoSearch.codice, titolo:$scope.modelloCampoSearch.titolo, tipoCampo:tipoCampoSelezionato, profilo:profiloScelto}, function(result, headers) {
	                    $scope.links = ParseLinks.parse(headers('link'));
	                    $scope.modelloCampos = result;
	                });
        		}
        	}
            
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            if($scope.isAdmin){
        		$scope.loadAll();
            }else if($scope.isReferenteTecnico){
            	$scope.loadAll(undefined, null, $rootScope.profiloattivo.aoo.id);
            }else{
            	$scope.loadAll(undefined, $rootScope.account.utente.id);
            }
        };
        
        $scope.loadPage($scope.page);

        $scope.showUpdate = function (id) {
        	if(angular.isDefined($stateParams.flag)){
        		ModelloCampo.getGlobal({id: id}, function(result) {
                    $scope.modelloCampo = result;
                    if(!$scope.isAdmin && !$scope.isReferenteTecnico){
                    	$scope.initTipiAttoProfilo($scope.modelloCampo.profilo);
                    }
                    if(!$scope.modelloCampo.tipoAtto){
                    	$scope.modelloCampo.tipoAtto={id:0, descrizione:'Non definito'};
                    }
                    $scope.tipoCampoChanged($scope.modelloCampo.tipoCampo);
                    $('#saveModelloCampoModal').modal('show');
                });
        	}
        	else{
        		ModelloCampo.get({id: id}, function(result) {
                    $scope.modelloCampo = result;
                    if(!$scope.isAdmin && !$scope.isReferenteTecnico){
                    	$scope.initTipiAttoProfilo($scope.modelloCampo.profilo);
                    }
                    if(!scope.modelloCampo.tipoAtto){
                    	$scope.modelloCampo.tipoAtto={id:0, descrizione:'Non definito'};
                    }
                    $scope.tipoCampoChanged($scope.modelloCampo.tipoCampo);
                    $('#saveModelloCampoModal').modal('show');
                });
        	}
            
        };

        $scope.save = function () {
            if ($scope.modelloCampo.id != null) {
                ModelloCampo.update($scope.modelloCampo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                ModelloCampo.save($scope.modelloCampo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
        	if(angular.isDefined($stateParams.flag)){
        		ModelloCampo.getGlobal({id: id}, function(result) {
                    $scope.modelloCampo = result;
                    $('#deleteModelloCampoConfirmation').modal('show');
                });
        	}
        	else{
        		ModelloCampo.get({id: id}, function(result) {
                    $scope.modelloCampo = result;
                    $('#deleteModelloCampoConfirmation').modal('show');
                });
        	}
            
        };

        $scope.confirmDelete = function (id) {
            ModelloCampo.delete({id: id},
                function () {
	            	if($scope.isAdmin){
	            		$scope.loadAll();
	                }else if($scope.isReferenteTecnico){
	                	$scope.loadAll(undefined, null, $rootScope.profiloattivo.aoo.id);
	                }else{
	                	$scope.loadAll(undefined, $rootScope.account.utente.id);
	                }
                    $('#deleteModelloCampoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
        	if($scope.isAdmin){
        		$scope.loadAll();
            }else if($scope.isReferenteTecnico){
            	$scope.loadAll(undefined, null, $rootScope.profiloattivo.aoo.id);
            }else{
            	$scope.loadAll(undefined, $rootScope.account.utente.id);
            }
            $('#saveModelloCampoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.modelloCampo = {codice: null, titolo: null, testo: null, tipoCampo: null, id: null, profilo: {id:null}, aoo:null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
