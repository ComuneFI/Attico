'use strict';

angular.module('cifra2gestattiApp')
    .controller('SchedulerCheckingController',['$scope','SchedulerChecking', '$filter', '$rootScope', '$datepicker','$timepicker',
                                               function ($scope, SchedulerChecking,$filter, $rootScope, $datepicker, $timepicker) {
        $scope.lista = [];
        $scope.hostName = "";
        $scope.nuovoScheduler = {};
        $scope.viewNuovo = false;
        
        $scope.loadAll = function() {
        	SchedulerChecking.getAll({}, function(result) {
        		$scope.lista = result;
            });
        };
        
        $scope.salva = function(obj){
        	if(obj.executionTime){
        		if(!(obj.executionTime instanceof Date)){
        			obj.executionTime = new Date(obj.executionTime);
        		}
        		obj.executionTime = obj.executionTime.getTime();
        	}
	        SchedulerChecking.saveUpdate({}, obj, function(result) {
	        	$scope.nuovoScheduler = {};
	        	$scope.nuovoScheduler.enabled=false;
	        	$scope.viewNuovo = false;
	        	$scope.loadAll();
	        	alert("Salvaggio effettuato con successo");
	        });
        };
        
        $scope.getHostName = function(){
        	SchedulerChecking.getHostName({}, function(result){
        		$scope.hostName = result.hostname;
        	});
        };
        
        $scope.nuovo = function(){
        	$scope.nuovoScheduler = {};
        	$scope.nuovoScheduler.enabled=false;
        	$scope.viewNuovo = true;
        };
        
        $scope.delete = function(id){
        	if(confirm("Confermare l'eliminazione")){
	        	SchedulerChecking.remove({id:id}, function(){
	        		$scope.loadAll();
	        		alert("Eliminazione effettuata con successo");
	        	});
        	}
        };
        
        $scope.getHostName();
        $scope.loadAll();
}]);