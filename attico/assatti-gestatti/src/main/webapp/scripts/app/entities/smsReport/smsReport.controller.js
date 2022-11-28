'use strict';

angular.module('cifra2gestattiApp')
    .controller('SmsReportController', function ($scope, $rootScope, $state, Sms) {
        $scope.smsReports = [];
        $scope.history = [];
        $scope.marker = 0;
        $scope.lastSearchObject = {};
        $scope.tempSearch = {};
        
        $scope.detail = function(invio){
        	$rootScope.currentSmsIdInvio = invio.idInvio;
        	$rootScope.currentSmsNome = invio.nome;
        	$rootScope.currentSmsTesto = invio.testo;
        	$state.go('smsReportDetail');
        };
        
        $scope.loadAll = function(searchObject) {
        	$scope.waitSmsConnection = true;
        	if(searchObject!=undefined && searchObject!=null){
        		$scope.history = [];
        		$scope.marker = 0;
        		Sms.search({marker: $scope.marker, page: $scope.page, per_page: 50}, searchObject, function(result) {
	                $scope.smsReports = result;
	                setMarker();
	                statusFromCode();
	                $scope.waitSmsConnection = false;
	            }, function(error){
	            	$scope.waitSmsConnection = false;
	            });
        	}else{
        		Sms.search({marker: $scope.marker, page: $scope.page, per_page: 50}, $scope.lastSearchObject, function(result) {
	                $scope.smsReports = result;
	                setMarker();
	                statusFromCode();
	                $scope.waitSmsConnection = false;
        		}, function(error){
	            	$scope.waitSmsConnection = false;
	            });
        	}
        };
        
        var setMarker = function() {
        	if($scope.smsReports) {
        		$scope.marker = $scope.smsReports.length>0 ? $scope.smsReports[$scope.smsReports.length-1].idInvio : 0;
        		$scope.empty = $scope.smsReports.length==0 ;        		
        	}
        }
        
        var statusFromCode = function() {
        	if($scope.smsReports) {
            	  
          	    for(var i=0; i<$scope.smsReports.length; i++) {
          	  	  
          	  	    if($scope.smsReports[i].invioStato == 0) {
          	  	  	    $scope.smsReports[i].invioStatoDesc = 'Da inviare';
          	  	    }
          	  	    if($scope.smsReports[i].invioStato == 1) {
          	  	  	    $scope.smsReports[i].invioStatoDesc = 'Invio Parziale';
          	  	    }
          	  	    if($scope.smsReports[i].invioStato == 2) {
          	  	  	    $scope.smsReports[i].invioStatoDesc = 'Invio Fallito';
          	  	    }
          	  	    if($scope.smsReports[i].invioStato == 4) {
          	  	  	    $scope.smsReports[i].invioStatoDesc = 'Inviato';
          	  	    }
          	  	    if($scope.smsReports[i].invioStato == 5) {
          	  	  	    $scope.smsReports[i].invioStatoDesc = 'In corso';
          	  	    }
          	    }
            }
        }
//        0=Da inviare                   Non è iniziata l’attività
//        1=Invio Parziale              Alcuni invii sono falliti
//        2=Invio Fallito                 L’invio è completamente fallito
//        4=Inviato                        L’invio è avvenuto completamente
//        5=In corso                      L’invio è in corso
//        Gli stati 1, 2 e 4 sono stati definitivi e l’invio dei messaggi si è concluso.
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastSearchObject = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.aggiorna = function(){
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastSearchObject = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.lastSearchObject = {};
        	$scope.tempSearch = {};
        	$scope.defaultDate();
        	$scope.ricerca();
        };
        
        $scope.loadPrev = function(page) {
        	$scope.history.pop();
        	$scope.marker = $scope.history[$scope.history.length-1]
        	$scope.loadAll();
        };
        
        $scope.loadNext = function(page) {
        	$scope.history.push($scope.marker);
        	$scope.loadAll();
        };
        
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
        
        $scope.defaultDate = function() {
        	var d = new Date();
    		d.setDate(d.getDate() - 30);
    		
        	if ($scope.tempSearch) {
        		$scope.tempSearch.dataStart = d.toISOString().substring(0, 10);
            	$scope.tempSearch.dataEnd = new Date().toISOString().substring(0, 10);
        	}
        	
        	if ($scope.lastSearchObject) {
        		$scope.lastSearchObject.dataStart = d.toISOString().substring(0, 10);
            	$scope.lastSearchObject.dataEnd = new Date().toISOString().substring(0, 10);
        	}
        }
        
        $scope.defaultDate();
        $scope.loadAll();
    });
