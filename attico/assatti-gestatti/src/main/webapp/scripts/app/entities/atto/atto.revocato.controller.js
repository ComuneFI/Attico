/**
 * Ricerca Controller
 */
'use strict';

angular.module('cifra2gestattiApp')
.controller('AttoRevocatoController', function ($scope,$log,$http, Atto, Profilo, ParseLinks ,$rootScope) {
			$scope.atto = $scope.ngModel;
			$scope.attos = [];
		 	$scope.page = 1;
	        $scope.attoRevocatoSearch = {ordinamento: 'codiceCifra', direction : 'asc', aooId : $scope.atto.aoo.id};
	        $scope.tempSearch = {ordinamento: 'codiceCifra', direction : 'asc', aooId : $scope.atto.aoo.id};
	        
	        if($scope.atto.attoRevocatoId){
	        	$scope.attoSelezionatoId = $scope.atto.attoRevocatoId;
	        }
	        
	        $scope.selezionaAttoRevocato = function(atto){
	        	$scope.attoSelezionatoId = atto.id;
	        	$scope.atto.attoRevocatoId = atto.id;
	        	$scope.atto.codicecifraAttoRevocato = atto.codiceCifra;
	        	$scope.atto.numeroAdozioneAttoRevocato = atto.numeroAdozione;
	        	$scope.atto.dataAdozioneAttoRevocato = atto.dataAdozione;
	        };
	        
	        $scope.ordinamento = function(ordinamento){
	        	if($scope.tempSearch.ordinamento == ordinamento){
	        		if($scope.tempSearch.direction == 'asc'){
	        			$scope.tempSearch.direction = 'desc';
	        		}else{
	        			$scope.tempSearch.direction = 'asc';
	        		}
	        	}else{
	        		$scope.tempSearch.ordinamento = ordinamento;
	        		$scope.tempSearch.direction = 'asc';
	        	}
	        	$scope.ricerca();
	        };
	        
	        $scope.loadAll = function(searchObject) {
	        	$scope.loading = true;
	        	if(searchObject!=undefined && searchObject!=null){
	        		if(!searchObject.id){
	        			searchObject.id = null;
	        		}
	        		Atto.searchRevocato({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.attos = result;
		                $scope.loading = false;
		            });
	        	}else{
	        		Atto.searchRevocato({page: $scope.page, per_page: 10}, $scope.attoRevocatoSearch, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.attos = result;
		                $scope.loading = false;
		            });
	        	}
	        };
	        
	        $scope.loadAll();
	        
	        $scope.ricerca = function(){
	        	$scope.page = 1;
	        	/*copio tempSearch e suoi elementi in attoRevocatoSearch*/
	        	$scope.attoRevocatoSearch = jQuery.extend(true, {}, $scope.tempSearch);
	        	$scope.loadAll($scope.tempSearch);
	        };
	        
	        $scope.resetRicerca = function(){
	        	$scope.page = 1;
	        	$scope.attoRevocatoSearch = {ordinamento: 'codiceCifra', direction : 'asc', aooId : $scope.atto.aoo.id};
	        	$scope.tempSearch = {ordinamento: 'codiceCifra', direction : 'asc', aooId : $scope.atto.aoo.id};
	        	$scope.loadAll();
	        };
	        
	        $scope.loadPage = function(page) {
	            $scope.page = page;
	            $scope.loadAll($scope.attoRevocatoSearch);
	        };
});