'use strict';

angular.module('cifra2gestattiApp')
    .controller('Obbligo_DL33Controller', function ($scope, Obbligo_DL33, Cat_obbligo_DL33,Macro_cat_obbligo_dl33, Scheda, ParseLinks, $log) {
        $scope.obbligo_DL33s = [];
        $scope.tempSearch = {};
        $scope.checkEsistenza = "";
        $scope.obbligoDl33Search = {};
        $scope.cat_obbligo_dl33s = Cat_obbligo_DL33.query();
        $scope.macro_cat_obbligo_dl33s = Macro_cat_obbligo_dl33.query();
        $scope.schedas = Scheda.query();
        $scope.page = 1;
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        $scope.schedeSelect = [{"descrizione":"Entrambe", "id":"entrambe"},{"descrizione":"Si", "id":"si"}, {"descrizione":"No", "id":"no"}];
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.obbligoDl33Search = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in obbligoDl33Search*/
        	$scope.obbligoDl33Search = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        	if(angular.isDefined($scope.tempSearch.attiva.id)){
        		delete $scope.tempSearch.attiva;
        	}
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var emptyMacroCat = false;
        		var emptySchede = false;
        		var emptyAttiva = false;
        		var emptyCat = false;
        		if(angular.isUndefined(searchObject.macro_cat_obbligo_DL33) || searchObject.macro_cat_obbligo_DL33==undefined || searchObject.macro_cat_obbligo_DL33 == null){
        			emptyMacroCat = true;
        			searchObject.macro_cat_obbligo_DL33 = {};
        		}
        		if(angular.isUndefined(searchObject.schede) || searchObject.schede==undefined || searchObject.schede == null){
        			emptySchede = true;
        			searchObject.schede = {};
        		}
        		if(angular.isUndefined(searchObject.attiva) || searchObject.attiva==undefined || searchObject.attiva == null){
        			emptyAttiva = true;
        			searchObject.attiva = {};
        		}
        		if(angular.isUndefined(searchObject.cat_obbligo_DL33) || searchObject.cat_obbligo_DL33==undefined || searchObject.cat_obbligo_DL33 == null){
        			emptyCat = true;
        			searchObject.cat_obbligo_DL33 = {};
        		}
        		Obbligo_DL33.query({page: $scope.page, per_page: 5, macro_cat_obbligo_DL33:searchObject.macro_cat_obbligo_DL33.id, codice:searchObject.codice, descrizione:searchObject.descrizione,schede:searchObject.schede.id, attiva:searchObject.attiva.id, cat_obbligo_DL33:searchObject.cat_obbligo_DL33.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.obbligo_DL33s = result;
	                if(emptyMacroCat){
	                	delete searchObject.macro_cat_obbligo_DL33;
	                }
	                if(emptySchede){
	                	delete searchObject.schede;
	                }
	                if(emptyAttiva){
	                	delete searchObject.attiva;
	                }
	                if(emptyCat){
	                	delete searchObject.cat_obbligo_DL33;
	                }
	            });
        	}else{
        		var emptyMacroCat = false;
        		var emptySchede = false;
        		var emptyAttiva = false;
        		var emptyCat = false;
        		if(angular.isUndefined($scope.obbligoDl33Search.macro_cat_obbligo_DL33) || $scope.obbligoDl33Search.macro_cat_obbligo_DL33==undefined || $scope.obbligoDl33Search.macro_cat_obbligo_DL33 == null){
        			emptyMacroCat = true;
        			$scope.obbligoDl33Search.macro_cat_obbligo_DL33 = {};
        		}
        		if(angular.isUndefined($scope.obbligoDl33Search.schede) || $scope.obbligoDl33Search.schede==undefined || $scope.obbligoDl33Search.schede == null){
        			emptySchede = true;
        			$scope.obbligoDl33Search.schede = {};
        		}
        		if(angular.isUndefined($scope.obbligoDl33Search.attiva) || $scope.obbligoDl33Search.attiva==undefined || $scope.obbligoDl33Search.attiva == null){
        			emptyAttiva = true;
        			$scope.obbligoDl33Search.attiva = {};
        		}
        		if(angular.isUndefined($scope.obbligoDl33Search.cat_obbligo_DL33) || $scope.obbligoDl33Search.cat_obbligo_DL33==undefined || $scope.obbligoDl33Search.cat_obbligo_DL33 == null){
        			emptyCat = true;
        			$scope.obbligoDl33Search.cat_obbligo_DL33 = {};
        		}
        		Obbligo_DL33.query({page: $scope.page, per_page: 5, macro_cat_obbligo_DL33:$scope.obbligoDl33Search.macro_cat_obbligo_DL33.id, schede:$scope.obbligoDl33Search.schede.id, codice:$scope.obbligoDl33Search.codice, descrizione:$scope.obbligoDl33Search.descrizione, attiva:$scope.obbligoDl33Search.attiva.id, cat_obbligo_DL33:$scope.obbligoDl33Search.cat_obbligo_DL33.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.obbligo_DL33s = result;
	                if(emptyMacroCat){
	                	delete $scope.obbligoDl33Search.macro_cat_obbligo_DL33;
	                }
	                if(emptySchede){
	                	delete $scope.obbligoDl33Search.schede;
	                }
	                if(emptyAttiva){
	                	delete $scope.obbligoDl33Search.attiva;
	                }
	                if(emptyCat){
	                	delete $scope.obbligoDl33Search.cat_obbligo_DL33;
	                }
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.resetCatObbligo = function(){
        	if($scope.obbligo_DL33!=undefined && $scope.obbligo_DL33.macro_cat_obbligo_DL33!=undefined && $scope.obbligo_DL33.macro_cat_obbligo_DL33.id!=undefined && $scope.obbligo_DL33!=undefined && $scope.obbligo_DL33.cat_obbligo_DL33!=undefined && $scope.obbligo_DL33.cat_obbligo_DL33.id!=undefined){
        		$scope.obbligo_DL33.cat_obbligo_DL33 = {};
        	}
        }
        
        $scope.showUpdate = function (id) {
        	$scope.checkEsistenza = false;
            Obbligo_DL33.get({id: id}, function(result) {
            	
                $scope.obbligo_DL33 = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$scope.obbligo_DL33.macro_cat_obbligo_DL33 = result.cat_obbligo_DL33.fk_cat_obbligo_macro_cat_obbligo_idx;
                    
                    if(!$scope.obbligo_DL33.macro_cat_obbligo_DL33 || $scope.obbligo_DL33.macro_cat_obbligo_DL33.attiva == false){
                    	$scope.obbligo_DL33.macro_cat_obbligo_DL33 = null;
                    }
                    if(!$scope.obbligo_DL33.cat_obbligo_DL33 || $scope.obbligo_DL33.cat_obbligo_DL33.attiva == false){
                    	$scope.obbligo_DL33.cat_obbligo_DL33 = null;
                    }
                    $('#saveObbligo_DL33Modal').modal('show');
                }
            });
        };
        $scope.filtercat = function(a) {
            if(angular.isDefined($scope.obbligo_DL33) && $scope.obbligo_DL33!=undefined && $scope.obbligo_DL33!=null &&
            	angular.isDefined($scope.obbligo_DL33.macro_cat_obbligo_DL33) && $scope.obbligo_DL33.macro_cat_obbligo_DL33 != undefined && $scope.obbligo_DL33.macro_cat_obbligo_DL33!=null && 
            	a.fk_cat_obbligo_macro_cat_obbligo_idx.id === $scope.obbligo_DL33.macro_cat_obbligo_DL33.id ) {
               return true; }
             else { return false; }
       };

        $scope.save = function () {
            if ($scope.obbligo_DL33.id != null) {
            	Obbligo_DL33.checkIfAlreadyexist({id:$scope.obbligo_DL33.id,cat_obbligo_DL33id:$scope.obbligo_DL33.cat_obbligo_DL33.id, codice:$scope.obbligo_DL33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Obbligo_DL33.save($scope.obbligo_DL33, function () {
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
            }  else {
            	Obbligo_DL33.checkIfAlreadyexist({id:'-1',cat_obbligo_DL33id:$scope.obbligo_DL33.cat_obbligo_DL33.id, codice:$scope.obbligo_DL33.codice}, function(data){
            		$scope.checkEsistenza = data.alreadyExists;
            		if($scope.checkEsistenza == false){
            			Obbligo_DL33.save($scope.obbligo_DL33, function () {
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
            Obbligo_DL33.get({id: id}, function(result) {
                $scope.obbligo_DL33 = result;
                $('#deleteObbligo_DL33Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Obbligo_DL33.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteObbligo_DL33Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveObbligo_DL33Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.obbligo_DL33 = {descrizione: null, codice: null, attivo: true, id: null, macro_cat_obbligo_DL33:null , cat_obbligo_DL33:{} };
            $scope.obbligo_DL33.cat_obbligo_DL33.id='';
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.checkEsistenza = false;
        };
        
        $scope.abilita=function(obbligo){
        	Obbligo_DL33.abilita({id:obbligo.id}, function(risultato){
        		$log.debug(risultato);
        		if(risultato && risultato.abilitazione == "ok"){
        			obbligo.attivo = true;
        		}
        	});
        };
        
        $scope.disabilita=function(obbligo){
        	Obbligo_DL33.disabilita({id:obbligo.id}, function(risultato){
        		$log.debug(risultato);
        		if(risultato && risultato.disabilitazione == "ok"){
        			obbligo.attivo = false;
        		}
        	});
        };
    });
