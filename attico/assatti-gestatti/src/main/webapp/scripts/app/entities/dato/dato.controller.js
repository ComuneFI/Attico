'use strict';

angular.module('cifra2gestattiApp')
    .controller('DatoController', function ($scope, Dato, ParseLinks) {
        $scope.datos = [];
        $scope.tempSearch = {};
        $scope.datoSearch = {};
        $scope.page = 1;
        $scope.tipodatos = ['text','number','date','datetime','file','select','beneficiario','valuta','url'];
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.datoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in datoSearch*/
        	$scope.datoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Dato.query({page: $scope.page, per_page: 5, etichetta:searchObject.etichetta, dato_tipdat_fk:searchObject.dato_tipdat_fk, multivalore:searchObject.multivalore}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.datos = result;
	            });
        	}else{
        		Dato.query({page: $scope.page, per_page: 5, etichetta:$scope.datoSearch.etichetta, dato_tipdat_fk:$scope.datoSearch.dato_tipdat_fk, multivalore:$scope.datoSearch.multivalore}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.datos = result;
	            });
        	}
        };
       
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Dato.get({id: id}, function(result) {
                $scope.dato = result;
                $('#saveDatoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.dato.id != null) {
                Dato.update($scope.dato,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Dato.save($scope.dato,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Dato.get({id: id}, function(result) {
                $scope.dato = result;
                $('#deleteDatoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Dato.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDatoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveDatoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.dato = {etichetta: null, multivalore: null, stile: null, valore_legame: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
