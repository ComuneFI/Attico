'use strict';

angular.module('cifra2gestattiApp')
    .controller('UfficioController', function ($scope, Ufficio, Profilo, Indirizzo, Aoo, ParseLinks) {
        $scope.ufficios = [];
        $scope.profilos = Profilo.getAllActive();
        $scope.indirizzos = Indirizzo.query();
        $scope.aoos = Aoo.getMinimal();
        $scope.page = 1;
        $scope.ufficioSearch = {};
        $scope.tempSearch = {};
        
        $scope.stati = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.ufficio.filtro.stato.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.ufficio.filtro.stato.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.ufficio.filtro.stato.entrambi"
    		}
        ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.ufficioSearch = {};
        	var lastStato = $scope.tempSearch.stato;
        	$scope.tempSearch = {
        			stato: lastStato
        	};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in grupporuoloSearch*/
        	$scope.grupporuoloSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var stato = null;
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
	            Ufficio.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	idUfficio: searchObject.idUfficio,
	            	codice: searchObject.codice,
	            	descrizione: searchObject.descrizione,
	            	email: searchObject.email,
	            	pec: searchObject.pec,
	            	responsabile: searchObject.responsabile,
	            	aoo: searchObject.aoo,
	            	stato: stato
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ufficios = result;
	            });
        	}else{
        		Ufficio.query({page: $scope.page, per_page: 5, idUfficio:$scope.ufficioSearch.idUfficio, codice:$scope.ufficioSearch.codice, descrizione:$scope.ufficioSearch.descrizione, email:$scope.ufficioSearch.email, pec:$scope.ufficioSearch.pec, responsabile:$scope.ufficioSearch.responsabile, aoo:$scope.ufficioSearch.aoo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ufficios = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.grupporuoloSearch);
        };
        $scope.loadAll();
        
        $scope.disable = function(ufficio){
        	Ufficio.disable({ id: ufficio.id}, function(data){
     	    	if(ufficio.validita == null ||ufficio.validita == undefined){
     	    		ufficio.validita = {};
     	    	}
     	    	ufficio.validita.validoal = "-";
     	    });
        };
        
        $scope.enable = function(ufficio){
        	Ufficio.enable({ id: ufficio.id}, function(data){
     	    	if(ufficio.validita == null ||ufficio.validita == undefined){
     	    		ufficio.validita = {};
     	    	}
     	    	ufficio.validita.validoal = undefined;
     	    });
        };

        $scope.showUpdate = function (id) {
            Ufficio.get({id: id}, function(result) {
                $scope.ufficio = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveUfficioModal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.ufficio.id != null) {
                Ufficio.update($scope.ufficio,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Ufficio.save($scope.ufficio,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Ufficio.get({id: id}, function(result) {
                $scope.ufficio = result;
                $('#deleteUfficioConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Ufficio.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUfficioConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveUfficioModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ufficio = {codice: null, descrizione: null, validodal: null, validoal: null, fax: null, email: null, pec: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
    });
