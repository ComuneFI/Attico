'use strict';

angular.module('cifra2gestattiApp')
    .controller('SistemaAccreditatoController', function ($scope, SistemaAccreditato, ParseLinks) {
        $scope.sistemaAccreditatos = [];
        $scope.page = 1;
        $scope.tempSearch = {};
        $scope.sistemaAccreditatoSearch = {};

        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.sistemaAccreditatoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in sistemaAccreditatoSearch*/
        	$scope.sistemaAccreditatoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		SistemaAccreditato.search({page: $scope.page, per_page: 5},searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.sistemaAccreditatos = result;
	            });
        	}else{
        		SistemaAccreditato.search({page: $scope.page, per_page: 5}, $scope.sistemaAccreditatoSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.sistemaAccreditatos = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (sistemaAccreditato) {
            $scope.sistemaAccreditato = sistemaAccreditato;
            $('#saveSistemaAccreditatoModal').modal('show');
        };

        

        $scope.save = function () {
        	SistemaAccreditato.save($scope.sistemaAccreditato,
                function () {
                    $scope.refresh();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSistemaAccreditatoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.sistemaAccreditato = {codice: null, descrizione: null, id: null,enabled:true};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
