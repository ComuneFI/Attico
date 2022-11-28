'use strict';

angular.module('cifra2gestattiApp')
    .controller('SmsReportDetailController', function ($scope, $rootScope, $state, ngToast, Sms) {
        $scope.detailSmsReport = {};
        
        $scope.stati = [
        	{'descrizione':'Tutti gli sms','status':'!filter'},
        	{'descrizione':'Consegnati','status':5},
        	{'descrizione':'Inviati','status':1},
        	{'descrizione':'Da inviare','status':0},
        	{'descrizione':'Non consegnati','status':6},
        	{'descrizione':'Falliti','status':4}
        ];
        $scope.detailSmsReport.by = {'descrizione':'Tutti gli sms','status':'!filter'};
        
        
        $scope.loadAll = function () {
        	
        	var detailObj = {
        			'idInvio': $rootScope.currentSmsIdInvio,
    				'nome': $rootScope.currentSmsNome,
    				'testo': $rootScope.currentSmsTesto,
        	}
        	$scope.waitSmsConnection = true;
        	Sms.detail(detailObj.idInvio, function(result) {
        		
        		$scope.detailObj = detailObj;
                $scope.messages = result;
              
	            $scope.detailSmsReport.totaleConsegnato = 0;
	            $scope.detailSmsReport.totaleDaInviare = 0;
	            $scope.detailSmsReport.totaleFallito = 0;
	            $scope.detailSmsReport.totaleInviato = 0;
	            $scope.detailSmsReport.totaleNonConsegnato = 0;

                if($scope.messages) {
              	  
              	    for(var i=0; i<$scope.messages.length; i++) {
              	  	  
              	  	    if($scope.messages[i].status == 0) {
              	  	  	    $scope.messages[i].statusDesc = 'Da inviare';
              	  	  	    $scope.detailSmsReport.totaleDaInviare++;
              	  	    }
              	  	    if($scope.messages[i].status == 1) {
              	  	  	    $scope.messages[i].statusDesc = 'Inviato';
              	  	  	    $scope.detailSmsReport.totaleInviato++;
              	  	    }
              	  	    if($scope.messages[i].status == 4) {
              	  	  	    $scope.messages[i].statusDesc = 'Fallito';
              	  	  	    $scope.detailSmsReport.totaleFallito++;
              	  	    }
              	  	    if($scope.messages[i].status == 5) {
              	  	  	    $scope.messages[i].statusDesc = 'Consegnato';
              	  	  	    $scope.detailSmsReport.totaleConsegnato++;
              	  	    }
              	  	    if($scope.messages[i].status == 6) {
              	  	  	    $scope.messages[i].statusDesc = 'Non consegnato';
              	  	  	    $scope.detailSmsReport.totaleNonConsegnato++;
              	  	    }
              	    }
                }
                $scope.waitSmsConnection = false;
              
            }, function(error){
            	$scope.waitSmsConnection = false;
            });
        };
        $scope.loadAll();
        
        $scope.retry = function () {
        	if(!$scope.detailObj || !$scope.detailObj.idInvio || !$scope.detailObj.nome || !$scope.detailObj.testo) {
        		ngToast.create(  { className: 'warning', content:  'Torna alla lista degli sms e seleziona nuovamente il messaggio da reinviare.'  } );
        		
        	} else {
        		var retryObj = {
        				'idInvio': $scope.detailObj.idInvio,
        				'nome': $scope.detailObj.nome,
        				'testo': $scope.detailObj.testo
        		}
        		
        		$scope.waitSmsConnection = true;
        		Sms.retry(retryObj, function(result) {
        			
        			ngToast.create(  { className: 'success', content:  'Sms inviato.'  } );
        			$scope.waitSmsConnection = false;
        			$state.go('smsReport');

        		}, function(error){
                	$scope.waitSmsConnection = false;
                });        		
        	}
        }
    });
