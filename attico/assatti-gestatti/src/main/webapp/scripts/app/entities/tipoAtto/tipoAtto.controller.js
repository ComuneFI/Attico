'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAttoController', function ($rootScope, $scope, ngToast, TipoAtto, TipoProgressivo, ModelloHtml, Sezione, ParseLinks,TipoIter, Aoo, Profilo, Campo) {
        $scope.tipoAttos = [];
        $scope.statos = [];
        $scope.page = 1;
        $scope.tipiIter = TipoIter.query();
        $scope.tipiProgressivo = TipoProgressivo.query();
        $scope.modelliHtml = ModelloHtml.query();
        
        $scope.tempSearch = {};
        $scope.tipoAttoSearch = {};
        $scope.infoTab = 0;
        $scope.aoos = [];
        $scope.profilos = [];
        
        $scope.disable = function(tipoAtto){
        	$rootScope.showMessage({title:'Conferma disabilitazione', siButton:true, noButton:true, 
        	siFunction: function(){
        		TipoAtto.disable( {id:tipoAtto.id} ,function(){
        			$('#genericMessage').modal('hide');
        			$scope.refresh();
        		});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Disabilitando il tipo di atto ' + tipoAtto.codice + ' non sar\u00E0 pi\u00F9 possibile istruire atti di tale tipologia. Confermare l\'operazione?'});
        };
        
        $scope.enable = function(tipoAtto){
        	$rootScope.showMessage({title:'Conferma abilitazione', siButton:true, noButton:true, 
            	siFunction: function(){
            		TipoAtto.enable( {id:tipoAtto.id} ,function(){
            			$('#genericMessage').modal('hide');
            			$scope.refresh();
            		});
            	},
            	noFunction:function(){
            		$('#genericMessage').modal('hide');
            	},
            	body:'Abilitando il tipo di atto ' + tipoAtto.codice + ' sar\u00E0 possibile istruire atti di tale tipologia. Confermare l\'operazione?'});
        };
        
        $scope.saveTipoIter = function (tipoIter) {
            if (tipoIter.id != null) {
                TipoIter.update( tipoIter,function(){
                     $scope.showUpdate($scope.tipoAtto.id);
                });
            } else {
                TipoIter.save( tipoIter,
                    function(){
                     $scope.showUpdate($scope.tipoAtto.id);
                } 
               );
            }
        };
        
        $scope.getCriteriByType = function(criteri, type){
        	return criteri.filter(function(v){
        		return v.criterio.tipo == type;
        	});
        };
        
        $scope.criterioChanged = function(criterio){
        	if(criterio.criterio.aooBased && !criterio.value){
        		criterio.aoos = null;
        	}
        	if(criterio.criterio.profiloBased && !criterio.value){
        		criterio.profilos = null;
        	}
        };
        
        Sezione.query(null, function(result) {
        	$scope.tipoAttoSezioni = result;
        });
        
        Campo.query(null, function(result) {
        	$scope.tipoAttoCampi = result;
        });
        
        
        $scope.getConfigParam = function(codiceSezione){
        	return eval('$rootScope.configurationParams.section_'+codiceSezione+'_enabled');
        }
        
        $scope.switchInfo = function(tabActive){
        	$scope.infoTab = tabActive;
        }
        
        $scope.checkIfEnabled = function(idSezione){
        	for( var x in $scope.tipoAtto.sezioni ){
        		if( $scope.tipoAtto.sezioni[x].id === idSezione){
        			return $scope.tipoAtto.sezioni[x].visibile;
        		}
        	}
        	return false;
        }
        
        $scope.checkIfCampoEnabled = function(idCampo){
        	for( var x in $scope.tipoAtto.campi ){
        		if( $scope.tipoAtto.campi[x].id === idCampo){
        			return $scope.tipoAtto.campi[x].visibile;
        		}
        	}
        	return false;
        }
        
        $scope.fillStatiOfTipoAtto = function(tipoAtto){
        	TipoAtto.getStatiByTipoAttoId({id:tipoAtto.id}, function(result){
        		angular.forEach( result , function(value) {
        			$scope.statos.push({tipoAtto:{id:tipoAtto.id}, stato:value});
    			  });
        	});
        };

        $scope.addIter = function(){
           $scope.tipoAtto.tipiIter.push({id:null, tipoAtto: { id: $scope.tipoAtto.id}, codice: 'USERDEFINED_TIPOITER' , edit:true});
        };
        
        $scope.addSezione = function(tipoAttoSezione, index){
    		if(!$scope.tipoAtto.sezioni){
        		$scope.tipoAtto.sezioni = [];
        	}
            $scope.tipoAtto.sezioni[index] = { 'codice': tipoAttoSezione.id, 'descrizione': tipoAttoSezione.descrizione, 'id': tipoAttoSezione.id, 'idTipoAtto':null, 'visibile': false};
         };
         
         $scope.addCampo = function(tipoAttoCampo, index){
     		if(!$scope.tipoAtto.campi){
         		$scope.tipoAtto.campi = [];
         	}
             $scope.tipoAtto.campi[index] = { 'codice': tipoAttoCampo.id, 'descrizione': tipoAttoCampo.descrizione, 'id': tipoAttoCampo.id, 'idTipoAtto':null, 'visibile': false};
          };

     $scope.removeIter = function (tipoIter) {
            TipoIter.get({id: tipoIter.id}, function(result) {
                $scope.tipoIter = result;
                $('#deleteTipoIterConfirmation').modal('show');
            });
        };

        $scope.confirmDeleteIter = function (id) {
            TipoIter.delete({id: id},
                function () {
                    $scope.showUpdate($scope.tipoAtto.id);
                    $('#deleteTipoIterConfirmation').modal('hide');
                });
        };

        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoAttoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoAttoSearch*/
        	$scope.tipoAttoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var idTipoProgressivo = (searchObject.tipoProgressivo!=null?searchObject.tipoProgressivo.id:null);
        		TipoAtto.query({page: $scope.page, per_page: 5, enabled:searchObject.enabled, codice:searchObject.codice, descrizione:searchObject.descrizione, tipoProgressivo:idTipoProgressivo, proponente:searchObject.proponente, consiglio:searchObject.consiglio, giunta:searchObject.giunta }, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAttos = result;
	            });
        	}else{
        		var idTipoProgressivo = ($scope.tipoAttoSearch.tipoProgressivo!=null?$scope.tipoAttoSearch.tipoProgressivo.id:null);
        		TipoAtto.query({page: $scope.page, per_page: 5, enabled:$scope.tipoAttoSearch.enabled, codice:$scope.tipoAttoSearch.codice, descrizione:$scope.tipoAttoSearch.descrizione, tipoProgressivo:idTipoProgressivo, proponente: $scope.tipoAttoSearch.proponente , consiglio: $scope.tipoAttoSearch.consiglio, giunta: $scope.tipoAttoSearch.giunta}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAttos = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	$scope.tipoAtto = {};
        	$scope.statos = [];
            TipoAtto.get({id: id}, function(result) {
                $scope.tipoAtto = result;
                $scope.fillStatiOfTipoAtto($scope.tipoAtto);
//TODO Controllo in modifica da riattivare
//                if(result.atti){
//                	$('#operationNotPermitted').modal('show');
//                } else {
//                	$('#saveTipoAttoModal').modal('show');
//                }
                
//TODO Da eliminare dopo l'attivazione del controllo
                
                if(!$scope.profilos || $scope.profilos.length == 0){
                	Profilo.getAllActiveBasic({includiAoo:true}, function(profili){
	        			$scope.profilos = profili;
	        		});
                }
                
                if(!$scope.aoos || $scope.aoos.length == 0){
                	Aoo.getAllEnabled({}, function(data){
                		$('#saveTipoAttoModal').modal('show');
                		$scope.aoos = data;
                	});
                	
                	
                }else{
                	$('#saveTipoAttoModal').modal('show');
                }
            });
        };

        

        $scope.save = function () {
            if ($scope.tipoAtto.id != null) {
                TipoAtto.update($scope.tipoAtto,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoAtto.save($scope.tipoAtto,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoAtto.get({id: id}, function(result) {
                $scope.tipoAtto = result;
                $('#deleteTipoAttoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            TipoAtto.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteTipoAttoConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteTipoAttoConfirmation').modal('hide');
            			$('#deleteTipoAttoDeletingError').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoAttoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoAtto = {codice: null, descrizione: null, id: null,tipiIter:[]};
            $scope.infoTab = 0;
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
