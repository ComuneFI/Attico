'use strict';

angular.module('cifra2gestattiApp')
    .controller('RubricaDestinatarioEsternoController', function ($scope, RubricaDestinatarioEsterno, Aoo, Indirizzo, ParseLinks, Principal, $rootScope) {
        $scope.rubricaDestinatarioEsternos = [];
        $scope.destinatariSearch = {notificaGiuntaAutomatica:'-'};
        $scope.tempSearch = {notificaGiuntaAutomatica:'-'};
        $scope.ricercaImport = false;
        $scope.checkEsistenza = false;
        $scope.stati = [
    		{
    			id: 0,
    			denominazione: "cifra2gestattiApp.rubricaDestinatarioEsterno.filtro.stato.attivi"
    		},
    		{
    			id: 1,
    			denominazione: "cifra2gestattiApp.rubricaDestinatarioEsterno.filtro.stato.disattivati"
    		},
    		{
    			id: 2,
    			denominazione: "cifra2gestattiApp.rubricaDestinatarioEsterno.filtro.stato.entrambi"
    		}
        ];
        $scope.aoos = [];
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.indirizzos = Indirizzo.query();
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.destinatariSearch = {notificaGiuntaAutomatica:'-'};
        	$scope.tempSearch = {notificaGiuntaAutomatica:'-'};
        	
        	if($scope.ricercaImport == true){
        		$scope.tempSearch.stato = $scope.stati[0];
        		$scope.tempSearch.notificagiunta = false;
        	}
        	else{
        		$scope.tempSearch.stato = $scope.stati[2];
        	}
        	if(!$scope.isAdmin){
        		$scope.tempSearch.aooId = $rootScope.profiloattivo.aoo.id;
        	}
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in destinatariSearch*/
        	$scope.destinatariSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.onSelected = function(){
        	$scope.ricerca();
        };
        
        $scope.page = 1;
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(!$scope.isAdmin){
            		searchObject.aooId = $rootScope.profiloattivo.aoo.id;
            	}
        		RubricaDestinatarioEsterno.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.rubricaDestinatarioEsternos = result;
	            });
        	}else{
        		if(!$scope.isAdmin){
            		$scope.destinatariSearch.aooId = $rootScope.profiloattivo.aoo.id;
            	}
        		RubricaDestinatarioEsterno.search({page: $scope.page, per_page: 10}, $scope.destinatariSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.rubricaDestinatarioEsternos = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.loadPageImport = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.tempSearch);
        };
        
        $scope.showUpdate = function (id) {
        	$scope.checkEsistenza = false;
            RubricaDestinatarioEsterno.get({id: id}, function(result) {
                $scope.rubricaDestinatarioEsterno = result;
                $('#saveRubricaDestinatarioEsternoModal').modal('show');
            });
        };
        
        $scope.save = function () {
        	if($scope.rubricaDestinatarioEsterno.notificaGiuntaAutomatica != true){
        		$scope.rubricaDestinatarioEsterno.notificaGiuntaAutomatica = false;
        	}
        	RubricaDestinatarioEsterno.checkIfAlreadyexist($scope.rubricaDestinatarioEsterno, function(data){
	        	$scope.checkEsistenza = data.alreadyExists;
	    		if($scope.checkEsistenza == false){
	    			RubricaDestinatarioEsterno.save($scope.rubricaDestinatarioEsterno, function () {
	                    $scope.refresh();
	                });
	    		}
        	});
        };

        $scope.disable = function (id) {
            RubricaDestinatarioEsterno.get({id: id}, function(result) {
                $scope.rubricaDestinatarioEsterno = result;
                $('#deleteRubricaDestinatarioEsternoConfirmation').modal('show');
            });
        };
        
        $scope.enable = function(rubricaDestinatario){
        	RubricaDestinatarioEsterno.enable({ id: rubricaDestinatario.id}, function(data){
     	    	if(rubricaDestinatario.validita == null || rubricaDestinatario.validita == undefined){
     	    		rubricaDestinatario.validita = {};
     	    	}
     	    	rubricaDestinatario.validita.validoal = undefined;
     	    });
        };

        $scope.confirmDisable = function (id) {
            RubricaDestinatarioEsterno.disable({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRubricaDestinatarioEsternoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
        	$scope.loadAll();
            $('#saveRubricaDestinatarioEsternoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.checkEsistenza = false;
        	if($scope.isAdmin){
        		$scope.rubricaDestinatarioEsterno = {denominazione: null, nome: null, cognome: null, titolo: null, email: null, pec: null, tipo: null, id: null, aoo:{id:null}};
        	}else{
        		$scope.rubricaDestinatarioEsterno = {denominazione: null, nome: null, cognome: null, titolo: null, email: null, pec: null, tipo: null, id: null, aoo:{id:$rootScope.profiloattivo.aoo.id}};
        	}
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
		
		$scope.isAdmin = false;
        var admin = [];
	  	admin.push("ROLE_ADMIN");
	  	admin.push("ROLE_AMMINISTRATORE_RP");
	  	if(Principal.isInAnyRole(admin)){
	  		Aoo.getMinimal({}, function(results){
	  			$scope.aoos = results;
	  			$scope.aoos.unshift({'id':'0', 'descrizione':'Tutte', 'validita':{'validoal':null}});
	  		});
	  		$scope.isAdmin = true;
	  		$scope.ricerca();
	  		$scope.loadAll();
	  	}else{
	  		$scope.loadAll();
	  	}
    });
