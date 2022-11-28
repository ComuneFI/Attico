'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDocumentoSerieController', function ($scope, TipoDocumentoSerie, ParseLinks) {
        $scope.tipoDocumentoSeries = [];
        $scope.page = 1;
        $scope.tipoDocumentoSeriesSearch = {};
        $scope.tempSearch = {};
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
	            TipoDocumentoSerie.search({page: $scope.page, per_page: 20},searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDocumentoSeries = result;
	            });
        	}
        	else{
        		TipoDocumentoSerie.search({page: $scope.page, per_page: 20},$scope.tipoDocumentoSeriesSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDocumentoSeries = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoDocumentoSerie.get({id: id}, function(result) {
                $scope.tipoDocumentoSerie = result;
                $('#saveTipoDocumentoSerieModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.tipoDocumentoSerie.id != null) {
                TipoDocumentoSerie.update($scope.tipoDocumentoSerie,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoDocumentoSerie.save($scope.tipoDocumentoSerie,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.setIsAbilitato = function (id, abilitato) {
            TipoDocumentoSerie.setIsAbilitato({id: id, abilitato: abilitato},
                function () {
                    $scope.loadAll();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoDocumentoSerieModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoDocumentoSerie = {descrizione: null, codice: null, id: null, enable: true};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoDocumentoSeriesSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoDocumentoSeriesSearch*/
        	$scope.tipoDocumentoSeriesSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
    });
