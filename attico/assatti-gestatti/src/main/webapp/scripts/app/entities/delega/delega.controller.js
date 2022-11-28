'use strict';

angular.module('cifra2gestattiApp')
    .controller('DelegaController', function ($scope, Delega, Profilo, ParseLinks, $rootScope, Principal, ProfiloAccount, $state) {
    	$scope.delegaSearch = {};
        $scope.tempSearch = {};
        $scope.deleghe = [];
        $scope.delega = {};
        $scope.deleganti = [];
        $scope.delegati = [];
        $scope.page = 1;
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.delegaSearch = {};
        	$scope.tempSearch = {};
        	$scope.setDefaultFilters(undefined, true);
        	$scope.loadAll();
        };
        
        $scope.setDefaultFilters = function(filterName, isInit){
        	if(isInit || filterName == 'validoFilter' || !$scope.tempSearch.validoFilter){
        		for(var i = 0; i < $scope.filterValues.length; i++){
        			if($scope.filterValues[i].defaultValue){
        				if(isInit){
        					$scope.delegaSearch.validoFilter = $scope.filterValues[i].key;
        				}
        				$scope.tempSearch.validoFilter = $scope.filterValues[i].key;
        				break;
        			}
        		}
        	}
        	if(isInit || filterName == 'enabledFilter' || !$scope.tempSearch.enabledFilter){
        		for(var i = 0; i < $scope.filterValues.length; i++){
        			if($scope.filterValues[i].defaultValue){
        				if(isInit){
        					$scope.delegaSearch.enabledFilter = $scope.filterValues[i].key;
        				}
        				$scope.tempSearch.enabledFilter = $scope.filterValues[i].key;
        				break;
        			}
        		}
        	}
        };
              
        $scope.filterValues = [
    		{
    			'key': 'yes',
    			'descrizione':'SI',
    			'defaultValue': true
    		},
    		{
    			'key': 'no',
    			'descrizione':'NO',
    			'defaultValue': false
    		},
    		{
    			'key': 'all',
    			'descrizione':'Entrambi',
    			'defaultValue': false
    		},
    	];
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.delegaSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	$scope.setDefaultFilters();
        	var profiloAooId = null;
        	if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
        		profiloAooId = $rootScope.profiloattivo.aoo.id;
        	}
        	/*$scope.delegati = [];*/
        	$scope.delega.delegati = null;
        	if(searchObject!=undefined && searchObject!=null){
        		searchObject.profiloAooId = profiloAooId;
	        	Delega.search({page: $scope.page, per_page: 5}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.deleghe = result;
	            });
        	}else{
        		$scope.delegaSearch.profiloAooId = profiloAooId;
        		Delega.search({page: $scope.page, per_page: 5}, $scope.delegaSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.deleghe = result;
	            });
        	}
        };
        
        $scope.getDeleganteLabel = function(item){
        	return item.utente.cognome +' '+ item.utente.nome +' (' + item.descrizione + ')';
        } 
        
        
        $scope.getDelegatiLabel = function(items){
        	var result="";
        	for ( var i in items) {
        		result+=items[i].utente.cognome +' '+ items[i].utente.nome +' (' + items[i].descrizione + ')<br />';
			}
        	return result;
        }

        $scope.findDelegati = function(reset){
        	if(reset){
        		$scope.delega.delegati = null;
        	}

        	var profiloAooId = null;
        	if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
        		profiloAooId = $rootScope.profiloattivo.aoo.id;
        	}
        	$scope.delegati = [];
        	Profilo.getAllActiveBasic({profiloAooId:profiloAooId, includiAoo:true}, function(result){
        		$scope.delegati = result;
				$scope.deleganti = result;
				$('#saveDelegaModal').modal('show');
        	});
        }
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.setDefaultFilters(undefined, true);
        $scope.loadAll();
        
        $scope.save = function () {
        	if(!Array.isArray($scope.delega.delegati)){
        		$scope.delega.delegati = [$scope.delega.delegati];
        	}
            if ($scope.delega.id != null) {
                Delega.update($scope.delega,
                    function () {
                        $scope.refresh();
                    }, function(){
                    	if($scope.delega.delegati && Array.isArray($scope.delega.delegati)){
                        	if($scope.delega.delegati.length > 0){
                        		$scope.delega.delegati = $scope.delega.delegati[0];
                        	}else{
                        		$scope.delega.delegati = null;
                        	}
                    	}
                    });
            } else {
                Delega.save($scope.delega,
                    function () {
                        $scope.refresh();
                    }, function(){
                    	if($scope.delega.delegati && Array.isArray($scope.delega.delegati)){
                        	if($scope.delega.delegati.length > 0){
                        		$scope.delega.delegati = $scope.delega.delegati[0];
                        	}else{
                        		$scope.delega.delegati = null;
                        	}
                    	}
                    });
            }
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveDelegaModal').modal('hide');
            $scope.clear();
        };
        
        $scope.showUpdate = function (id) {
        	$scope.findDelegati();
            Delega.get({id: id}, function(result) {
                $scope.delega = result;
                if($scope.delega.delegati && Array.isArray($scope.delega.delegati)){
                	if($scope.delega.delegati.length > 0){
                		$scope.delega.delegati = $scope.delega.delegati[0];
                	}else{
                		$scope.delega.delegati = null;
                	}
            	}
            });
        };
        
        $scope.delete = function (id) {
            Delega.get({id: id}, function(result) {
                $scope.delega = result;
                $('#deleteDelegaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            Delega.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteDelegaConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteDelegaConfirmation').modal('hide');
            			$('#deleteDelegaDeletingError').modal('show');
            		}
                });
        };
        
        $scope.clear = function () {
            $scope.delega = {id: null, dataValiditaFine: null, dataValiditaInizio: null, profiloDelegante: null, delegati: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.checkWarnDateDelega = function(){
    		$scope.warnDateDelega = $scope.delega.dataValiditaInizio && $scope.delega.dataValiditaInizio && new Date($scope.delega.dataValiditaInizio).getTime()>new Date($scope.delega.dataValiditaFine).getTime();
    		return $scope.warnDateDelega;
    	};
    });