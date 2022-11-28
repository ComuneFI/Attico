'use strict';

angular.module('cifra2gestattiApp')
    .controller('Cat_obbligo_DL33Controller', function ($scope, Cat_obbligo_DL33, Macro_cat_obbligo_dl33, ParseLinks, $log) {
        $scope.cat_obbligo_DL33s = [];
        $scope.tempSearch = {};
        $scope.checkEsistenza = "";
        $scope.catObbligoDl33Search = {};
        $scope.macro_cat_obbligo_dl33s = Macro_cat_obbligo_dl33.query();
        $scope.page = 1;
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.catObbligoDl33Search = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in catObbligoDl33Search*/
        	$scope.catObbligoDl33Search = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.attiva) || searchObject.attiva==undefined || searchObject.attiva == null){
        			searchObject.attiva = {};
        		}
        		if(angular.isUndefined(searchObject.fk_cat_obbligo_macro_cat_obbligo_idx) || searchObject.fk_cat_obbligo_macro_cat_obbligo_idx==undefined || searchObject.fk_cat_obbligo_macro_cat_obbligo_idx == null){
        			searchObject.fk_cat_obbligo_macro_cat_obbligo_idx = {};
        		}
        		Cat_obbligo_DL33.query({page: $scope.page, per_page: 5, codice:searchObject.codice, descrizione:searchObject.descrizione, attiva:searchObject.attiva.id, fk_cat_obbligo_macro_cat_obbligo_idx:searchObject.fk_cat_obbligo_macro_cat_obbligo_idx.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.cat_obbligo_DL33s = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.catObbligoDl33Search.attiva) || $scope.catObbligoDl33Search.attiva==undefined || $scope.catObbligoDl33Search.attiva == null){
        			$scope.catObbligoDl33Search.attiva = {};
        		}
        		if(angular.isUndefined($scope.catObbligoDl33Search.fk_cat_obbligo_macro_cat_obbligo_idx) || $scope.catObbligoDl33Search.fk_cat_obbligo_macro_cat_obbligo_idx==undefined || $scope.catObbligoDl33Search.fk_cat_obbligo_macro_cat_obbligo_idx == null){
        			$scope.catObbligoDl33Search.fk_cat_obbligo_macro_cat_obbligo_idx = {};
        		}
        		Cat_obbligo_DL33.query({page: $scope.page, per_page: 5, codice:$scope.catObbligoDl33Search.codice, descrizione:$scope.catObbligoDl33Search.descrizione, attiva:$scope.catObbligoDl33Search.attiva.id, fk_cat_obbligo_macro_cat_obbligo_idx:$scope.catObbligoDl33Search.fk_cat_obbligo_macro_cat_obbligo_idx.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.cat_obbligo_DL33s = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	$scope.checkEsistenza = false;
            Cat_obbligo_DL33.get({id: id}, function(result) {
                $scope.cat_obbligo_DL33 = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveCat_obbligo_DL33Modal').modal('show');
                }
            });
        };

        $scope.save = function () {
        	if ($scope.cat_obbligo_DL33.id != null) {
        		Cat_obbligo_DL33.checkIfAlreadyexist({id:$scope.cat_obbligo_DL33.id,macro_cat_obbligo_DL33id:$scope.cat_obbligo_DL33.fk_cat_obbligo_macro_cat_obbligo_idx.id, codice:$scope.cat_obbligo_DL33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Cat_obbligo_DL33.save($scope.cat_obbligo_DL33, function () {
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
            } else {
            	Cat_obbligo_DL33.checkIfAlreadyexist({id:'-1',macro_cat_obbligo_DL33id:$scope.cat_obbligo_DL33.fk_cat_obbligo_macro_cat_obbligo_idx.id, codice:$scope.cat_obbligo_DL33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Cat_obbligo_DL33.save($scope.cat_obbligo_DL33, function () {
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
            Cat_obbligo_DL33.get({id: id}, function(result) {
                $scope.cat_obbligo_DL33 = result;
                $('#deleteCat_obbligo_DL33Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Cat_obbligo_DL33.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCat_obbligo_DL33Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveCat_obbligo_DL33Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.cat_obbligo_DL33 = {codice: null, descrizione: null, attiva: true, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.checkEsistenza = false;
        };
        
        $scope.disabilita=function(entity){
        	$scope.daDisabilitare = {};
        	Cat_obbligo_DL33.checkAlert({id:entity.id}, function(risultato){
	        	if(risultato.alert == "yes" && risultato.obblighi && risultato.obblighi.length > 0){
	        		angular.copy(entity, $scope.daDisabilitare);
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
        
        $scope.abilita=function(categoria){
        	Cat_obbligo_DL33.abilita({id:categoria.id}, function(risultato){
        		$log.debug(risultato);
        		if(risultato && risultato.abilitazione == "ok"){
        			categoria.attiva = true;
        		}
        	});
        };
        
        $scope.confermaDisabilitazione = function(id){
        	Cat_obbligo_DL33.disabilita({id:id}, function(res){
				$log.debug(res);
				if(res && res.disabilitazione == "ok"){
					for(var i = 0; i<$scope.cat_obbligo_DL33s.length; i++){
						if($scope.cat_obbligo_DL33s[i].id == id){
							$scope.cat_obbligo_DL33s[i].attiva = false;
							$('#alertDisabilitazione').modal('hide');
							break;
						}
					}
        		}
			});
        };
    });
