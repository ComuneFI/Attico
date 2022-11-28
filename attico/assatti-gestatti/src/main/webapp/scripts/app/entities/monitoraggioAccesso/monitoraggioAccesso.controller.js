'use strict';

angular.module('cifra2gestattiApp')
    .controller('MonitoraggioAccessoController', function ($scope, MonitoraggioAccesso, ParseLinks, Principal, $rootScope) {
        $scope.accessi = [];
        
        $scope.page = 1;
        $scope.accessiSearch = {};
        $scope.tempSearch = {};
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.accessiSearch = {};
            $scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in accessiSearch*/
        	$scope.accessiSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.sort = function(sortField){
        	if(!$scope.tempSearch.sortField || !$scope.tempSearch.sortType){
        		$scope.tempSearch.sortType = 'asc';
        	}else if($scope.tempSearch.sortField == sortField && $scope.tempSearch.sortType=='asc' ){
        		$scope.tempSearch.sortType = 'desc';
        	}else{
        		$scope.tempSearch.sortType = 'asc';
        	}
        	$scope.tempSearch.sortField = sortField;
        	$scope.ricerca();
        };
        
        $scope.loadAll = function(searchObject) {
    		if(searchObject!=undefined && searchObject!=null){
    			var obj = {sortType:searchObject.sortType,sortField:searchObject.sortField, dataA:searchObject.dataA, dataDa:searchObject.dataDa, username:searchObject.username, hostname: searchObject.hostname, ipAddress: searchObject.ipAddress, status:searchObject.status, dettaglio:searchObject.dettaglio};
	            MonitoraggioAccesso.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.accessi = result;
	            });
    		}else{
    			var obj = {sortType:$scope.accessiSearch.sortType,sortField:$scope.accessiSearch.sortField, dataA:$scope.accessiSearch.dataA, dataDa:$scope.accessiSearch.dataDa, username:$scope.accessiSearch.username, hostname: $scope.accessiSearch.hostname, ipAddress: $scope.accessiSearch.ipAddress, status:$scope.accessiSearch.status, dettaglio:$scope.accessiSearch.dettaglio};
    			MonitoraggioAccesso.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.accessi = result;
                });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
    });
