'use strict';

angular.module('cifra2gestattiApp')
    .controller('RichiestaHDController', ['$scope', '$rootScope','RichiestaHD', 'StatoRichiestaHD', 'TipoRichiestaHD','ParseLinks', '$state','Utente','Principal',
        function ($scope, $rootScope, RichiestaHD, StatoRichiestaHD, TipoRichiestaHD, ParseLinks, $state, Utente, Principal) {
        $scope.richiestaHDs = [];
        $scope.richiestaHDSearch = {};
        $scope.tiporichiestahds = [];
        $scope.utenti = [];
        $scope.tempSearch = {};
        $scope.page = 1;
        $scope.risposta = {};
        $scope.statorichiestahds = StatoRichiestaHD.query();
        
        $scope.rispostaOperatore = function(statoRichiesta){
        	RichiestaHD.rispostaOperatore({statoRichiesta:statoRichiesta}, $scope.risposta, function(result){
        		//
        	});
        };
        
        $scope.goToDetail = function(id){
        	RichiestaHD.presaVisione({id:id}, function(res){
        		$state.go("richiestaHDDetail", {id:id});
        	});
        };

        $scope.loadOperatore = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		RichiestaHD.searchOperatore({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.richiestaHDs = result;
	            });
        	}else{
        		RichiestaHD.searchOperatore({page: $scope.page, per_page: 10}, $scope.richiestaHDSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.richiestaHDs = result;
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in richiestaHDSearch*/
        	$scope.richiestaHDSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadOperatore($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.richiestaHDSearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadOperatore();
        };
        
        $scope.init = function(){
        	$scope.isAdmin = null;
            var admin = [];
    	  	admin.push("ROLE_ADMIN");
    	  	if(Principal.isInAnyRole(admin)){
    	  		$scope.isAdmin = true;
    	  	}else{
    	  		$scope.isAdmin = false;
    	  	}
    	  	Utente.getAllActive(function(result){
            	$scope.utenti = result;
            });
    	  	
    	  	TipoRichiestaHD.query( function(result){
                $scope.tiporichiestahds = result;
            }); 
        	$scope.loadOperatore();
        };
    }]);
