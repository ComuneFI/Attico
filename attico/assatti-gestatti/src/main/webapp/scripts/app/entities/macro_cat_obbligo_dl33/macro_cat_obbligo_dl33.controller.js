'use strict';

angular.module('cifra2gestattiApp')
    .controller('Macro_cat_obbligo_dl33Controller', function ($scope, Macro_cat_obbligo_dl33, ParseLinks, $log) {
        $scope.macro_cat_obbligo_dl33s = [];
        $scope.page = 1;
        $scope.tempSearch = {};
        $scope.checkEsistenza = "";
        $scope.macroCatObbligoDl33Search = {};
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.macroCatObbligoDl33Search = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in macroCatObbligoDl33Search*/
        	$scope.macroCatObbligoDl33Search = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.attiva) || searchObject.attiva==undefined || searchObject.attiva == null){
        			searchObject.attiva = {};
        		}
        		Macro_cat_obbligo_dl33.query({page: $scope.page, per_page: 5, codice:searchObject.codice, descrizione:searchObject.descrizione, attiva:searchObject.attiva.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.macro_cat_obbligo_dl33s = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.macroCatObbligoDl33Search.attiva) || $scope.macroCatObbligoDl33Search.attiva==undefined || $scope.macroCatObbligoDl33Search.attiva == null){
        			$scope.macroCatObbligoDl33Search.attiva = {};
        		}
        		Macro_cat_obbligo_dl33.query({page: $scope.page, per_page: 5, codice:$scope.macroCatObbligoDl33Search.codice, descrizione:$scope.macroCatObbligoDl33Search.descrizione, attiva:$scope.macroCatObbligoDl33Search.attiva.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.macro_cat_obbligo_dl33s = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	$scope.checkEsistenza = false;
            Macro_cat_obbligo_dl33.get({id: id}, function(result) {
                $scope.macro_cat_obbligo_dl33 = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveMacro_cat_obbligo_dl33Modal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.macro_cat_obbligo_dl33.id != null) {
            	Macro_cat_obbligo_dl33.checkIfAlreadyexist({id:$scope.macro_cat_obbligo_dl33.id, codice:$scope.macro_cat_obbligo_dl33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Macro_cat_obbligo_dl33.save($scope.macro_cat_obbligo_dl33, function () {
                            $scope.refresh();
                        }, function(err){
                        	$scope.saveButtonDisabled = false;
                        });
            		} else {
            			$scope.saveButtonDisabled = false;
            		}
            	}, function (err) {
            		$scope.saveButtonDisabled = false;
                });            } else {
               Macro_cat_obbligo_dl33.checkIfAlreadyexist({id:'-1',codice:$scope.macro_cat_obbligo_dl33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Macro_cat_obbligo_dl33.save($scope.macro_cat_obbligo_dl33, function () {
                            $scope.refresh();
                        }, function(err){
                        	$scope.saveButtonDisabled = false;
                        });
            		} else {
            			$scope.saveButtonDisabled = false;
            		}
            	}, function (err) {
            		$scope.saveButtonDisabled = false;
                });
                
                
                
                
            }
        };

        $scope.delete = function (id) {
            Macro_cat_obbligo_dl33.get({id: id}, function(result) {
                $scope.macro_cat_obbligo_dl33 = result;
                $('#deleteMacro_cat_obbligo_dl33Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Macro_cat_obbligo_dl33.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMacro_cat_obbligo_dl33Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveMacro_cat_obbligo_dl33Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.macro_cat_obbligo_dl33 = {codice: null, descrizione: null, attiva: true, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.checkEsistenza = false;
        };
        
        $scope.disabilita=function(entity){
        	$scope.daDisabilitare = {};
        	Macro_cat_obbligo_dl33.checkAlert({id:entity.id}, function(risultato){
        		$log.debug(risultato);
        		if(risultato.alert == "yes" && risultato.categorie && risultato.categorie.length > 0){
        			angular.copy(entity, $scope.daDisabilitare);
        			risultato.categorie.map(function(categoria){
        				if(!angular.isDefined($scope.daDisabilitare.categorie) || $scope.daDisabilitare.categorie == null){
        					$scope.daDisabilitare.categorie = categoria['descrizione'] + " (ID " + categoria['id'] + ")"; 
        				}else{
        					$scope.daDisabilitare.categorie = $scope.daDisabilitare.categorie + "<br/>" + categoria['descrizione'] + " (ID " + categoria['id'] + ")"; 
        				}        				
        			});
        			if(risultato.obblighi && risultato.obblighi.length > 0){
        				risultato.obblighi.map(function(obbligo){
        					if(!angular.isDefined($scope.daDisabilitare.obblighi) || $scope.daDisabilitare.obblighi == null){
            					$scope.daDisabilitare.obblighi = obbligo['descrizione'] + " (ID " + obbligo['id'] + ")"; 
            				}else{
            					$scope.daDisabilitare.obblighi = $scope.daDisabilitare.obblighi + "<br/>" + obbligo['descrizione'] + " (ID " + obbligo['id'] + ")"; 
            				}  
        				});
        			}
        			$('#alertDisabilitazione').modal('show');
        		}else{
        			Macro_cat_obbligo_dl33.disabilita({id:entity.id}, function(res){
        				$log.debug(res);
        				if(res && res.disabilitazione == "ok"){
        					entity.attiva = false;
                		}
        			});
        		}
        	});
        };
        
        $scope.confermaDisabilitazione = function(id){
        	Macro_cat_obbligo_dl33.disabilita({id:id}, function(res){
				$log.debug(res);
				if(res && res.disabilitazione == "ok"){
					for(var i = 0; i<$scope.macro_cat_obbligo_dl33s.length; i++){
						if($scope.macro_cat_obbligo_dl33s[i].id == id){
							$scope.macro_cat_obbligo_dl33s[i].attiva = false;
							$('#alertDisabilitazione').modal('hide');
							break;
						}
					}
        		}
			});
        };
        
        $scope.abilita=function(macroCategoria){
        	Macro_cat_obbligo_dl33.abilita({id:macroCategoria.id}, function(risultato){
        		$log.debug(risultato);
        		if(risultato && risultato.abilitazione == "ok"){
        			macroCategoria.attiva = true;
        		}
        	});
        };
    });
