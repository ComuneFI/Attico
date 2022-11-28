'use strict';

angular.module('cifra2gestattiApp')
    .controller('ImpersonificaController', function ($scope, $rootScope, Profilo, TipoAtto, Utente, GruppoRuolo,Aoo, QualificaProfessionale, ParseLinks, Ruolo, Principal, ProfiloAccount, $state ) {
    	
    	$scope.page = 1;
    	$scope.profilos = [];
        
        $scope.profiloSearch = {};
        $scope.tempSearch = {};
        
        $scope.tipoAttos = TipoAtto.query(function(result) {
        	$scope.listTipoAtto = result.map( function (tipoCur) {
                return tipoCur.descrizione;
            });
        });
        
        $scope.stati = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.impersonifica.filtro.stato.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.impersonifica.filtro.stato.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.impersonifica.filtro.stato.entrambi"
    		}
        ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.ruolos = Ruolo.query(function(result) {
        	$scope.listAllRuoli = $scope.ruolos.map( function (ruoloCur) {
                return ruoloCur.descrizione;
        	});
        });
        
        $scope.qualificaprofessionales = QualificaProfessionale.query(function(result) {
        	$scope.listAllQualifiche = result.map( function (qualificaCur) {
                return qualificaCur.denominazione;
            });
        });
        
        $scope.impersonifica = function(profilo){
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
        	}
        }
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.profiloSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.profiloSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	var profiloAooId = null;
        	if(!Principal.isInAnyRole($state.current.data.roles) && ProfiloAccount.isInAnyRole($state.current.data.customRoles)){
        		profiloAooId = $rootScope.profiloattivo.aoo.id;
        	}else if(!Principal.isInAnyRole($state.current.data.roles)){
        		return;
        	}
        	if(searchObject!=undefined && searchObject!=null){
        		var sRuoli = null;
        		if (searchObject.ruolo) {
        			sRuoli = searchObject.ruolo.codice;
        		}
        		var stato = null;
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
	            Profilo.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	idProfilo: searchObject.id,
	            	descrizione: searchObject.descrizione,
	            	delega: searchObject.delega,
	            	tipoAtto: searchObject.tipoAtto,
	            	utente: searchObject.utente,
	            	aoo: searchObject.aoo,
	            	qualificaProfessionale: searchObject.qualificaProfessionale,
	            	ruoli: sRuoli,
	            	stato: stato,
	            	profiloAooId:profiloAooId
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.profilos = result;
	            });
        	}else{
        		Profilo.query({profiloAooId:profiloAooId, page: $scope.page, per_page: 5, idProfilo:$scope.profiloSearch.id, descrizione:$scope.profiloSearch.descrizione, delega:$scope.profiloSearch.delega, tipoAtto:$scope.profiloSearch.tipoAtto, utente:$scope.profiloSearch.utente, aoo:$scope.profiloSearch.aoo, qualificaProfessionale:$scope.profiloSearch.qualificaProfessionale,ruoli:$scope.profiloSearch.ruolo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.profilos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.profiloSearch);
        };
        $scope.loadAll();
        
    });