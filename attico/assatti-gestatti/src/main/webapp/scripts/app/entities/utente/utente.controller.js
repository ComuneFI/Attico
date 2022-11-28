'use strict';

angular.module('cifra2gestattiApp')
    .controller('UtenteController', function ($scope, $rootScope, localStorageService, Utente, Indirizzo, ParseLinks, Aoo, Profilo,  $state) {
        $scope.utentes = [];
        $scope.indirizzos = Indirizzo.query();
        $scope.page = 1;
        $scope.utenteSearch = {};
        $scope.tempSearch = {};
        $scope.profiloSearch = {};
        
        if($.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0 && $.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0){
        	$scope.tempSearch = localStorageService.get('_utente_utenteSearch');
        }
        
        $scope.aoos = Aoo.getMinimal(function(result) {
        	$scope.listAllAoo = $scope.aoos.map( function (aooCur) {
                return aooCur.descrizione;
            });
        });
        $scope.statos = Utente.getAllUserStates();
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.utenteSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in utenteSearch*/
        	$scope.utenteSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	
        	if($rootScope && $rootScope.fromUtenteProfilo){
        		searchObject = {};
        		searchObject.username = $rootScope.fromUtenteProfilo;
        		searchObject.id = $rootScope.fromUtenteProfiloId;
        		$scope.tempSearch = {};
        		$scope.tempSearch.username = searchObject.username;
        		$scope.tempSearch.id = searchObject.id;
        		$rootScope.fromUtenteProfilo = null;
        	}
        	
        	
        	if(searchObject!=undefined && searchObject!=null){
	            Utente.query({page: $scope.page, per_page: 5, idUtente:searchObject.id, cognome:searchObject.cognome, nome:searchObject.nome, username:searchObject.username, stato:searchObject.stato, aoo:searchObject.aoo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.utentes = result;
	            });
        	}else{
        		Utente.query({page: $scope.page, per_page: 5, idUtente:$scope.utenteSearch.id, cognome:$scope.utenteSearch.cognome, nome:$scope.utenteSearch.nome, username:$scope.utenteSearch.username, stato:$scope.utenteSearch.stato, aoo:$scope.utenteSearch.aoo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.utentes = result;
	            });
        	}
        	
        	if($scope.tempSearch!=undefined && $scope.tempSearch!=null){
				localStorageService.set('_utente_utenteSearch', $scope.tempSearch);
			}else{
				localStorageService.set('_utente_utenteSearch', $scope.utenteSearch);
			}
        };
        $scope.downloadModuloreRegistrazione = function(id){
        	window.open(
        			window.open("api/utentes/" + id + "/moduloregistrazione") + "&access_token=" + $scope.access_token,
        			  '_blank'
        			);
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            if($.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0 && $.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0){
            	$scope.tempSearch = localStorageService.get('_utente_utenteSearch');
            	$scope.loadAll($scope.tempSearch);
            }else {
            	$scope.loadAll();
            }
        };
        

        $scope.showUpdate = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#saveUtenteModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.utente.id != null) {
                Utente.update($scope.utente,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Utente.save($scope.utente,
                    function () {
                        $scope.refresh();
                    });
            }
        };

 
    $scope.activeAmministratoreIP = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#activeAmministratoreIPConfirmation').modal('show');
            });
     };

     $scope.confirmActiveAmministratoreIP = function (utente) {
            Utente.activeAmministratoreIP( $scope.utente ,
                function () {
                    $scope.loadAll();
                    $('#activeAmministratoreIPConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.activeAmministratoreRP = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#activeAmministratoreRPConfirmation').modal('show');
            });
     };

     $scope.confirmActiveAmministratoreRP = function (utente) {
            Utente.activeAmministratoreRP( $scope.utente ,
                function () {
                    $scope.loadAll();
                    $('#activeAmministratoreRPConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        
    $scope.attiva = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#attivaUtenteConfirmation').modal('show');
            });
     };

     $scope.confirmAttiva = function (utente) {
            Utente.attiva( $scope.utente ,
                function () {
                    $scope.loadAll();
                    $('#attivaUtenteConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.delete = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#deleteUtenteConfirmation').modal('show');
            });
        };

        $scope.disable = function(utente){
     	    Utente.disable({ id: utente.id}, function(data){
     	    	if(data.stato != 'errore'){
     	    		utente.stato = data.stato;
     	    	}
     	    });
        };
        
        $scope.enable = function(utente){
     	    Utente.enable({ id: utente.id}, function(data){
     	    	if(data.stato != 'errore'){
     	    		utente.stato = data.stato;
     	    	}else{
     	    		alert('Si è verificato un errore: ' + data.dettaglioErrore);
     	    	}
     	    });
        };

        $scope.confirmDelete = function (id) {
            Utente.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUtenteConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveUtenteModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.utente = {cognome: null, nome: null, username: null, moduloregistrazione: null, codicefiscale: null, validodal: null, validoal: null, telefono: null, altrorecapito: null, fax: null, email: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        
        //inizio modifica link to profilo
        $scope.ricercaProfilo = function(utente){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.tempSearch = {};
        	$scope.tempSearch.utente = utente;
        	$scope.loadAllProfiliPerUtente($scope.tempSearch);
        	
        };
        
        $scope.loadAllProfiliPerUtente = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Profilo.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	utente: searchObject.utente
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.profilos = result;
	                $rootScope.fromUtente = searchObject.utente;
	                $state.go("profilo", {}, {reload: false});
	            });
        	}
        };
        //fine modifica link to profilo
        
        
        
        
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
		
		if($.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0 && $.inArray('_utente_utenteSearch', localStorageService.keys()) >= 0){
        	$scope.tempSearch = localStorageService.get('_utente_utenteSearch');
        	$scope.loadAll($scope.tempSearch);
        }else {
        	$scope.loadAll();
        }
		

    });
