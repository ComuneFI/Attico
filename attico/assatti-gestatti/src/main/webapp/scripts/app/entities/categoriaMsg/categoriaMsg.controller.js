'use strict';

angular.module('cifra2gestattiApp')
    .controller('CategoriaMsgController', ['$scope', '$rootScope','CategoriaMsg','ParseLinks',
        function ($scope, $rootScope, CategoriaMsg, ParseLinks) {
        $scope.categoriaMsgs = [];
        $scope.categoriaMsg = {};
        $scope.categoriaMsgSearch = {};
        $scope.tempSearch = {};
        $scope.page = 1;
        
        $scope.clear = function(){
        	$scope.categoriaMsg = {};
        };
        
        $scope.disable = function(id){
        	CategoriaMsg.disable({id:id}, function(res){
        		$scope.load();
        	})
        };
        
        $scope.enable = function(id){
        	CategoriaMsg.enable({id:id}, function(res){
        		$scope.load();
        	})
        };
        
        $scope.showUpdate = function(id){
        	CategoriaMsg.get({id:id}, function(res){
        		$scope.categoriaMsg = res;
        		$("#saveCategoriamsgModal").modal("show");
        	});
        };
        
        $scope.update = function () {
        	CategoriaMsg.update($scope.categoriaMsg, function () {
        		$("#saveCategoriamsgModal").modal("hide");
                $scope.load();
            });
        };
        
        $scope.save = function () {
        	CategoriaMsg.save($scope.categoriaMsg, function () {
        		$("#newCategoriamsgModal").modal("hide");
                $scope.load();
            });
        };

        $scope.load = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		CategoriaMsg.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.categoriaMsgs = result;
	            });
        	}else{
        		CategoriaMsg.search({page: $scope.page, per_page: 10}, $scope.categoriaMsgSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.categoriaMsgs = result;
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in categoriaMsgSearch*/
        	$scope.categoriaMsgSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.load($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.categoriaMsgSearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.load();
        };
        
        $scope.load();
        
    }]);
