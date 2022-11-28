'use strict';

angular.module('cifra2gestattiApp')
.controller('StoricoAttoDirDetailController', function ($scope, $rootScope, $log, StoricoAtto, ParseLinks, $stateParams, $state) {
	
	$scope.load = function (id) {
		if(!$scope.atto)  {
			 StoricoAtto.attoDirigenziale({id: id}, function(result) {
				 $scope.atto = result;
				 $log.debug("LOAD STORICO ATTO DIRIGENZIALE",$scope.atto);
			 });
		}
	};
	
    $log.debug("load Storico Atto Dirigenziale id=", $stateParams.id);
	$scope.load($stateParams.id);
	
	$scope.refresh = function ( ) {
		StoricoAtto.attoDirigenziale({id: id}, function(result) {
			 $scope.atto = result;
			 $log.debug("REFRESH STORICO ATTO DIRIGENZIALE",$scope.atto);
		 });
	};
	
});