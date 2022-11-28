'use strict';

angular.module('cifra2gestattiApp')
.controller('GiacenzaController', function ($scope,SedutaGiuntaConstants, Atto, OrdineGiorno, SedutaGiunta, $stateParams, ParseLinks,$rootScope,ArgumentsOdgService) {

	$scope.resultAttiOdg=[];
	$scope.allItemsSelected = false;
	$scope.motivoSospensione="";

	$scope.setArgumentsList = function(list){
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		for (var i = 0; i < $scope.resultAttiOdg.length; i++) {
			if ($scope.resultAttiOdg[i].isChecked) {
				if($scope.motivoSospensione!=null){
					$scope.resultAttiOdg[i].note = $scope.motivoSospensione;
				}
				ArgumentsOdgService.addArgument($scope.resultAttiOdg[i]); 
			}
		}
	};



//	This executes when entity in table is checked
	$scope.selectEntity = function () {
		// If any entity is not checked, then uncheck the "allItemsSelected" checkbox
		var numChecked = 0;
		for (var i = 0; i < $scope.resultAttiOdg.length; i++) {
			if (!$scope.resultAttiOdg[i].isChecked) {
				$scope.allItemsSelected = false;
			}else {
				numChecked++;
			}

		}
		if (!$scope.allItemsSelected) {
			if (numChecked==0){
				$scope.checkedButton = false;
			}else {
				$scope.checkedButton = true;
			}
			$scope.setArgumentsList();
			return;
		}

		//If not the check the "allItemsSelected" checkbox
		$scope.allItemsSelected = true;
		$scope.checkedButton = true;
		$scope.setArgumentsList();


	};

//	This executes when checkbox in table header is checked
	$scope.selectAll = function () {
		var numChecked = 0;
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		// Loop through all the entities and set their isChecked property
		for (var i = 0; i < $scope.resultAttiOdg.length; i++) {
			$scope.resultAttiOdg[i].isChecked = $scope.allItemsSelected;
			if ($scope.resultAttiOdg[i].isChecked) {
				numChecked++;
				ArgumentsOdgService.addArgument($scope.resultAttiOdg[i]); 
			}
		}
		if (numChecked==0){
			$scope.checkedButton = false;
		}else {
			$scope.checkedButton = true;
		}

	};

	$scope.load = function (id) {
		if (id>0){
			$scope.currentOdgId = id;
		}
		var tipiAttoIds = [];
		 for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
			 tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
		 }
		$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:tipiAttoIds,taskStato:SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg} ;
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		$scope.criteria.page = 1;
		$scope.criteria.per_page = 10;
		$scope.criteria.viewtype = 'tutti';
		Atto.search( $scope.criteria ,$scope.criteria ,function (result, headers) {
			$scope.resultAttiOdg = result;
			$scope.resultAttiLinks = ParseLinks.parse(headers('link'));
			$scope.totalResultAtti = headers('x-total-count') ;
			
		});
	};
	$scope.sospendi = function(){
		$scope.setArgumentsList();
		OrdineGiorno.suspendAtti(ArgumentsOdgService.getArguments(),function(data){
			$scope.resultAttiOdg=[];
			$scope.allItemsSelected = false;
			$scope.setArgumentsList([]);
			$scope.checkedButton = false;
			$scope.load($stateParams.id);
		});
		$('#sospendiAttoModal').modal('hide');
	};
	$scope.load($stateParams.id);
});

angular.module('cifra2gestattiApp')
.controller('SospesiController', function ($scope, Atto, OrdineGiorno, SedutaGiunta, $stateParams, ParseLinks,$rootScope,ArgumentsOdgService,$log) {

	$scope.resultAtti=[];
	$scope.allItemsSelected = false;
	ArgumentsOdgService.setArguments([]);
	$scope.setArgumentsList = function(list){
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		for (var i = 0; i < $scope.resultAtti.length; i++) {
			if ($scope.resultAtti[i].isChecked) {
				ArgumentsOdgService.addArgument($scope.resultAtti[i]); 
			}
		}
	};



//	This executes when entity in table is checked
	$scope.selectEntity = function () {
		// If any entity is not checked, then uncheck the "allItemsSelected" checkbox
		var numChecked = 0;
		for (var i = 0; i < $scope.resultAtti.length; i++) {
			if (!$scope.resultAtti[i].isChecked) {
				$scope.allItemsSelected = false;
			}else {
				numChecked++;
			}

		}
		if (!$scope.allItemsSelected) {
			if (numChecked==0){
				$scope.checkedButton = false;
			}else {
				$scope.checkedButton = true;
			}
			$scope.setArgumentsList();
			return;
		}

		//If not the check the "allItemsSelected" checkbox
		$scope.allItemsSelected = true;
		$scope.checkedButton = true;
		$scope.setArgumentsList();


	};

//	This executes when checkbox in table header is checked
	$scope.selectAll = function () {
		var numChecked = 0;
		var emptylist = [];
		ArgumentsOdgService.setArguments(emptylist);
		// Loop through all the entities and set their isChecked property
		for (var i = 0; i < $scope.resultAtti.length; i++) {
			$scope.resultAtti[i].isChecked = $scope.allItemsSelected;
			if ($scope.resultAtti[i].isChecked) {
				numChecked++;
				ArgumentsOdgService.addArgument($scope.resultAtti[i]); 
			}
		}
		if (numChecked==0){
			$scope.checkedButton = false;
		}else {
			$scope.checkedButton = true;
		}

	};

	$scope.load = function (id) {
		if (id>0){
			$scope.currentOdgId = id;
		}
		var tipiAttoIds = [];
		for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
			 tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
		 }
		$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:tipiAttoIds,taskStato:SedutaGiuntaConstants.statiAtto.propostaSospesa} ;
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		$scope.criteria.page = 1;
		$scope.criteria.per_page = 10;
		$scope.criteria.viewtype = 'tutti';
		Atto.search( $scope.criteria ,$scope.criteria ,function (result, headers) {
			$scope.resultAtti = result;
			$scope.resultAttiLinks = ParseLinks.parse(headers('link'));
			$scope.totalResultAtti = headers('x-total-count') ;
			
		});
	};
	$scope.attiva = function(){
		$scope.setArgumentsList();
		OrdineGiorno.activateAtti(ArgumentsOdgService.getArguments(),function(data){
			$scope.resultAtti=[];
			$scope.allItemsSelected = false;
			$scope.setArgumentsList([]);
			$scope.checkedButton = false;
			$scope.load($stateParams.id);
		});
		$('#sospendiAttoModal').modal('hide');
	};
	$scope.load($stateParams.id);
});