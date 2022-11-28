'use strict';

angular.module('cifra2gestattiApp')
    .controller('AssessoratoController', function ($scope, Assessorato, Profilo, ParseLinks) {
        $scope.assessoratos = [];
        $scope.profilos = [];
        Profilo.getByRole({ruolo:'ROLE_COMPONENTE_GIUNTA'}, function(result){
        	if(result && result.length && result.length > 0){
        		for(var i = 0; i < result.length; i++){
        			if(!result[i].validita || !result[i].validita.validoal){
        				$scope.profilos.push(result[i]);
        			}
        		}
        	}
        });
        
        $scope.updateNominativoResponsabile = function(profiloid){
        	var nome = null;
        	if(profiloid && $scope.profilos && $scope.profilos.length && $scope.profilos.length > 0){
        		for(var i = 0; i< $scope.profilos.length; i++){
        			if($scope.profilos[i].id == profiloid){
        				nome = $scope.profilos[i].utente.nome + " " + $scope.profilos[i].utente.cognome;
        				break;
        			}
        		}
        	}
        	if(nome != null){
        		$scope.assessorato.nominativoResponsabile = nome;
        	}
        };
        
        $scope.page = 1;
        $scope.assessoratoSearch = {};
        $scope.tempSearch = {};
        
        $scope.stati = [
    		{
    			id:0,
    			denominazione:"cifra2gestattiApp.assessorato.filtro.stato.attivi"
    		},
    		{
    			id:1,
    			denominazione:"cifra2gestattiApp.assessorato.filtro.stato.disattivati"
    		},
    		{
    			id:2,
    			denominazione:"cifra2gestattiApp.assessorato.filtro.stato.entrambi"
    		}
        ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.assessoratoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in assessoratoSearch*/
        	$scope.assessoratoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var stato = "";
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
	            Assessorato.query({
	            	page: $scope.page,
	            	per_page: 5,
	            	idAssessorato: searchObject.id,
	            	codice: searchObject.codice,
	            	responsabile: searchObject.responsabile,
	            	denominazione: searchObject.denominazione,
	            	qualifica: searchObject.qualifica,
	            	stato: stato
	            	}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.assessoratos = result;
	            });
        	}else{
        		Assessorato.query({page: $scope.page, per_page: 5, idAssessorato:$scope.assessoratoSearch.id, codice:$scope.assessoratoSearch.codice, responsabile:$scope.assessoratoSearch.responsabile, denominazione:$scope.assessoratoSearch.denominazione, qualifica:$scope.assessoratoSearch.qualifica}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.assessoratos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.assessoratoSearch);
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Assessorato.get({id: id}, function(result) {
                $scope.assessorato = result;
                $('#saveAssessoratoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.assessorato.id != null) {
                Assessorato.update($scope.assessorato,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Assessorato.save($scope.assessorato,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.disable = function (id) {
        	Assessorato.disable({id: id},
    			function () {
        			$scope.loadAll();
        			$scope.clear();
            });
        };
        
        $scope.enable = function (id) {
        	Assessorato.enable({id: id},
    			function () {
        			$scope.loadAll();
        			$scope.clear();
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveAssessoratoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.assessorato = {denominazione: null, qualifica: null, validodal: null, validoal: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
