'use strict';

angular.module('cifra2gestattiApp')
    .controller('MainController', function ($scope, $rootScope, News, ParseLinks, Principal, Auth, Utente) {
    	
    	$scope.page = 1;
    	$scope.newss = [];
    	$scope.destinataris = [];
    	$scope.codicefiscale = "";
    	$scope.statoregistrazione = "";
    	$scope.checkCodicefiscale = function(){
    		if($scope.codicefiscale){
	     	    Utente.checkstato({ codicefiscale: $scope.codicefiscale}, function(data){
	     		   $scope.statoregistrazione = data.stato;
	     		   if($scope.statoregistrazione == ''){
	     			   $scope.statoregistrazione = 'NONREGISTRATO';
	     		   }
	     	    });
    		}else{
    			$scope.codicefiscale = "";
    		}
        };
        
    	Principal.identity().then(function(account) {
    		$scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.loadAll();
        });
        
        $scope.loadAll = function() {
        	
        	if($rootScope.profiloattivo!=null){
        		News.query({page: $scope.page, per_page: 5, profiloid:$rootScope.profiloattivo.id}, function(result, headers) {
        			$scope.links = ParseLinks.parse(headers('link'));
                    $scope.newss = result;
                });
        	} 
        }
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };

        

        
    });