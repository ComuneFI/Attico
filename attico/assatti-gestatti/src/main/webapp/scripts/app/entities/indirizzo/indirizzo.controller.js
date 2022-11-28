'use strict';

angular.module('cifra2gestattiApp')
    .controller('IndirizzoController', function ($scope, Indirizzo, ParseLinks) {
        $scope.indirizzos = [];
        $scope.page = 1;
        $scope.indirizzoSearch = {};
        $scope.tempSearch = {};
        
        $scope.stati = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.indirizzo.filtro.stato.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.indirizzo.filtro.stato.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.indirizzo.filtro.stato.entrambi"
    		}
        ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.disable = function(indirizzo){
     	    Indirizzo.disable({ id: indirizzo.id}, function(data){
     	    	indirizzo.attivo = false;
     	    });
        };
        
        $scope.enable = function(indirizzo){
     	    Indirizzo.enable({ id: indirizzo.id}, function(data){
     	    	indirizzo.attivo = true;
     	    });
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.indirizzoSearch = {};
        	var lastStato = $scope.tempSearch.stato;
        	$scope.tempSearch = {
        			stato: lastStato
        	};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in indirizzoSearch*/
        	$scope.indirizzoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };        
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var stato = "";
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
	            Indirizzo.query({
		            	page: $scope.page,
		            	per_page: 5,
		            	idIndirizzo: searchObject.idIndirizzo,
		            	dug: searchObject.dug,
		            	toponimo: searchObject.toponimo,
		            	civico: searchObject.civico,
		            	cap: searchObject.cap,
		            	comune: searchObject.comune,
		            	provincia: searchObject.provincia,
		            	stato: stato
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.indirizzos = result;
	            });
        	}else{
        		Indirizzo.query({page: $scope.page, per_page: 5, idIndirizzo:$scope.indirizzoSearch.idIndirizzo, dug:$scope.indirizzoSearch.dug, toponimo:$scope.indirizzoSearch.toponimo, civico:$scope.indirizzoSearch.civico, cap:$scope.indirizzoSearch.cap, comune:$scope.indirizzoSearch.comune, provincia:$scope.indirizzoSearch.provincia}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.indirizzos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.indirizzoSearch);
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Indirizzo.get({id: id}, function(result) {
                $scope.indirizzo = result;
                $('#saveIndirizzoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.indirizzo.id != null) {
                Indirizzo.update($scope.indirizzo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Indirizzo.save($scope.indirizzo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Indirizzo.get({id: id}, function(result) {
                $scope.indirizzo = result;
                $('#deleteIndirizzoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Indirizzo.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteIndirizzoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveIndirizzoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.indirizzo = {dug: null, toponimo: null, civico: null, cap: null, comune: null, provincia: null, id: null, attivo: true};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
