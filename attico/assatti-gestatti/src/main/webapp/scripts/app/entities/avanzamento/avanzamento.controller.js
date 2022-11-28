'use strict';

angular.module('cifra2gestattiApp')
    .controller('AvanzamentoController', function ($scope, $rootScope, $state, Atto, Avanzamento, ParseLinks, Profilo, ProfiloAccount, Utente) {
        $scope.avanzamentos = [];
        $scope.page = 1;

		$scope.impersonifica = function(avanzamento){
	        if(avanzamento && avanzamento.profilo && avanzamento.profilo.id){
				Profilo.get({id: avanzamento.profilo.id}, function(profilo) {
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
		        	}else{
						alert('Impossibile impersonificare.');
					}
	            });
			}else{
				alert('ATTENZIONE! Impossibile impersonificare.');
			}
        };

        $scope.loadAll = function() {
        	$scope.criteria.page = $scope.page;
        	$scope.criteria.per_page = 20;
        	$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
            Avanzamento.search($scope.tempSearch, $scope.tempSearch, function(result, headers) {
            	$scope.links = ParseLinks.parse(headers('link'));
                $scope.avanzamentos = result;
            });
        };
        
        Atto.caricastati(function(collection){
    		$scope.stati = collection;
    	});
        
        $scope.criteria = {};
        $scope.criteria.page = $scope.page;
    	$scope.criteria.per_page = 20;
    	$scope.criteria.ordinamento = "dataAttivita";
    	$scope.criteria.tipoOrinamento = "desc";
        
    	$scope.ordinamento = function(ordinamento){
    		
    		if($scope.criteria.ordinamento == ordinamento){
    			if($scope.criteria.tipoOrinamento == "asc"){
    				$scope.criteria.tipoOrinamento = "desc";
    			}else{
    				$scope.criteria.tipoOrinamento = "asc";
    			}
    		}else{	
    			$scope.criteria.tipoOrinamento = "asc"; 
    		}
    		$scope.criteria.ordinamento = ordinamento;
    		$scope.loadAll();
    	};
    	
    	$scope.resetRicerca = function(){
    		$scope.avanzamentos = [];
    		$scope.page = 1;
    		$scope.criteria = {};
            $scope.criteria.page = $scope.page;
        	$scope.criteria.per_page = 20;
        	$scope.criteria.ordinamento = "dataAttivita";
        	$scope.criteria.tipoOrinamento = "desc";
        	
    		$scope.loadAll();
    	};
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Avanzamento.get({id: id}, function(result) {
                $scope.avanzamento = result;
                $('#saveAvanzamentoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.avanzamento.id != null) {
                Avanzamento.update($scope.avanzamento,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Avanzamento.save($scope.avanzamento,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Avanzamento.get({id: id}, function(result) {
                $scope.avanzamento = result;
                $('#deleteAvanzamentoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Avanzamento.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAvanzamentoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveAvanzamentoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.avanzamento = {dataAttivita: null, note: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
