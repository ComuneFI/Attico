'use strict';

angular.module('cifra2gestattiApp')
.controller('StoricoAttoGiuntaDetailController', function ($scope, $rootScope, $log, StoricoAtto, ParseLinks, $stateParams, $state) {
	
	$scope.load = function (id) {
		
		
		if(!$scope.atto)  {
			 StoricoAtto.attoGiunta({id: id}, function(result) {
				 $scope.atto = result;
				 $log.debug("LOAD STORICO ATTO GIUNTA",$scope.atto);
			 });
		}
	};
	
    $log.debug("load Storico Atto Giunta id=", $stateParams.id);
	$scope.load($stateParams.id);
	
	$scope.refresh = function ( ) {
		StoricoAtto.attoGiunta({id: id}, function(result) {
			 $scope.atto = result;
			 $log.debug("REFRESH STORICO ATTO GIUNTA",$scope.atto);
		 });
	};
});