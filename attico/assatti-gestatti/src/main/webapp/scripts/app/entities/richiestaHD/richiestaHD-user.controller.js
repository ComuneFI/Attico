'use strict';

angular.module('cifra2gestattiApp')
    .controller('RichiestaHDUserController', ['$scope', '$stateParams', 'RichiestaHD', 'Profilo', 'StatoRichiestaHD', 'TipoRichiestaHD', 'ParseLinks', '$state',
        function ($scope, $stateParams, RichiestaHD, Profilo, StatoRichiestaHD, TipoRichiestaHD, ParseLinks, $state) {
    	$scope.richiestaHDs = [];
        $scope.richiestaHDSearch = {};
        $scope.tiporichiestahds = [];
        $scope.tempSearch = {};
        $scope.page = 1;
        $scope.risposta = {};
        $scope.richiesta = {};

        $scope.clear = function(){
        	$scope.richiesta = {};
        };

        $scope.save = function () {
            RichiestaHD.save($scope.richiesta, function () {
        		$("#saveRichiestaHDModal").modal("hide");
                $scope.loadUtente();
            });
        };
        
        $scope.goToDetail = function(id){
        	$state.go("richiestaHDDetail", {id:id});
        };
        
        $scope.rispostaUtente = function(){
        	RichiestaHD.rispostaUtente({}, $scope.risposta, function(result){
        		//
        	});
        };
        
        $scope.loadUtente = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		RichiestaHD.searchUtente({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.richiestaHDs = result;
	            });
        	}else{
        		RichiestaHD.searchUtente({page: $scope.page, per_page: 10}, $scope.richiestaHDSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.richiestaHDs = result;
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in richiestaHDSearch*/
        	$scope.richiestaHDSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadUtente($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.richiestaHDSearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadUtente();
        };
        
        $scope.init = function(){
        	$scope.statorichiestahds = StatoRichiestaHD.query();
        	TipoRichiestaHD.query( function(result){
            	$scope.tiporichiestahds = [];
            	for(var i = 0; i<result.length; i++){
            		if(result[i].enabled){
            			$scope.tiporichiestahds.push(result[i]);
            		}
            	}
            });
        	$scope.loadUtente();
        };
    }]);
