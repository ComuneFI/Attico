'use strict';

angular.module('cifra2gestattiApp')
    .controller('MsgUserController', ['$scope', '$rootScope','Msg','ParseLinks', 'CategoriaMsg', 'Aoo',
        function ($scope, $rootScope, Msg, ParseLinks, CategoriaMsg, Aoo) {
        $scope.msgs = [];
        $scope.msgSearch = {};
        $scope.tempSearch = {};
        $scope.page = 1;
        $scope.aooAttive = [];
        $scope.prioritas = Msg.prioritas();
        $scope.categorieMsg = CategoriaMsg.getAllEnabled();
        $scope.now = new Date().toISOString().substring(0, 10);
        
        $scope.loadUserAoo = function(){
        	if($rootScope.activeprofilos && $rootScope.activeprofilos.length > 0){
	        	angular.forEach($rootScope.activeprofilos, function(profilo, key){
	        		$scope.aooAttive.push(profilo.aoo);
	    		});
        	}
        };
        
        $scope.load = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Msg.searchUtente({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.msgs = result;
	                $scope.now = new Date().toISOString().substring(0, 10);
	            });
        	}else{
        		Msg.searchUtente({page: $scope.page, per_page: 10}, $scope.msgSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.msgs = result;
	                $scope.now = new Date().toISOString().substring(0, 10);
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in msgSearch*/
        	$scope.msgSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.load($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.msgSearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.load();
        };
        
        $scope.load();
        $scope.loadUserAoo();
        
    }]);
