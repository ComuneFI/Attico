'use strict';

angular.module('cifra2gestattiApp')
    .controller('DiogeneReportController', function ($scope, DiogeneReport, TipoDocumentoSerie,  ParseLinks) {
        $scope.diogeneReports = [];
        $scope.page = 1;
        $scope.lastSearchObject = {};
        $scope.tempSearch = {};
        $scope.loading = false;
        
        $scope.statos = ["INVIATO","ERRORE","ERRORE_CONFIGURAZIONESERIE", "ERRORE_CLASSIFICAZIONE"];
        $scope.destinazioni = ["SERIE","RUP"];
        $scope.tipodocumentoseries = TipoDocumentoSerie.query();
        
        $scope.loadAll = function(searchObject) {
        	$scope.loading = true;
        	if(searchObject!=undefined && searchObject!=null){
        		DiogeneReport.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.diogeneReports = result;
	                $("#updateList").removeClass("spinEffect");
	                $scope.loading = false;
	            });
        	}else{
        		DiogeneReport.search({page: $scope.page, per_page: 10}, $scope.lastSearchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.diogeneReports = result;
	                $scope.loading = false;
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastSearchObject = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.aggiorna = function(){
        	$("#updateList").addClass("spinEffect");
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastSearchObject = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.lastSearchObject = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
        
        $scope.loadAll();
    });
