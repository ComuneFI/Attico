'use strict';

angular.module('cifra2gestattiApp')
	.controller('SedutaGiuntaConsolidatiDetailController',
			function ($scope,$filter,$controller,moment,Esito,SedutaGiuntaConstants,ngToast,$rootScope,$state,$q,$stateParams,
					SedutaGiunta,$log,TipoOdg,Profilo,Atto,ModelloHtml,OrdineGiorno,$timeout,$sce) {

	$controller('SedutaGiuntaDetailController', { $scope: $scope });

  	$scope.id = $stateParams.id;
  	$scope.sedutaGiunta = {id:null};
  	$scope.decisioni = [];
  	$scope.profilos =[];
  	$scope.atti = [];


    $scope.nonTrattato = function (item) {
        let idOdg = item.id;
    	if(idOdg != null){
          let idOdgs = [];
          idOdgs.push(idOdg);
          $scope.param={};
          $scope.param.attoOdgId=idOdgs;
          //$scope.param.profiloId=$rootScope.profiloattivo.id;
          //attoOdgId:'@attoOdgId', profiloId:'@profiloId'
         // $scope.param.profiloId=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
           OrdineGiorno.nontrattati($scope.param,function(result){
             if(result){
							 	item.esito = 'non_trattato';
             }
           })
		}
		$log.debug("AttiOdg",$scope.sedutaGiunta.odgs[0].attos);
	};

	$scope.trattato = function (item) {
        let idOdg = item.id;
    		if(idOdg != null){
          let idOdgs = [];
          idOdgs.push(idOdg);
          $scope.param={};
          $scope.param.attoOdgId=idOdgs;
           OrdineGiorno.reimpostatrattati($scope.param,function(result){
             if(result){
							 	item.esito = null;
             }
           })
		}
		$log.debug("AttiOdg",$scope.sedutaGiunta.odgs[0].attos);
	  };

      $scope.filtraTrattati = function (item) {
          return (item.esito !== 'non_trattato' || item.esito == undefined);
      };

      $scope.filtraNonTrattati = function (item) {
          return item.esito === 'non_trattato';
      };
  
    $scope.init();
});
angular.module('cifra2gestattiApp')
.directive('showtabOdgBaseOrdinarioConsolidato',  function () {
	return {
		link: function (scope, element, attrs) {
				// Save active tab to localStorage
				scope.setActiveTab = function (activeTab) {
						sessionStorage.setItem("activeTab", activeTab);
				};
				// Get active tab from localStorage
				scope.getActiveTab = function () {
						return sessionStorage.getItem("activeTab");
				};

				scope.tasktype = scope.getActiveTab();
				if(scope.tasktype != undefined){
					if(scope.tasktype === 'atti-non-trattati'){
						$('#atti-non-trattati').show();
						$('#atti-trattati').hide();
						setTimeout(function(){
								$('#atti-non-trattati-tab').click();
							}, 1);
						scope.openTabOdgBaseOrdinario('atti-non-trattati');
					} else{
						$('#atti-non-trattati').hide();
						$('#atti-trattati').show();
					}
				} else{
					$('#atti-non-trattati').hide();
				}

				element.bind('click', function(e) {
				e.preventDefault();
				$(element).tab('show');
				scope.tasktype = $(element).attr('name');
				scope.page = 1;
				scope.setActiveTab(scope.tasktype);
				if(scope.tasktype == 'atti-trattati'){
					$('#atti-non-trattati').hide();
					//scope.searchAtto();
				}else if(scope.tasktype == 'atti-non-trattati'){
					$('#atti-trattati').hide();
			}
		 });
		}
	};
});


angular.module('cifra2gestattiApp')
.controller('DatiSedutaAttoController', function ($scope,Lavorazione, $filter,$controller,moment,Esito,SedutaGiuntaConstants,ngToast,$rootScope,$state,$q,$stateParams,
		SedutaGiunta,$log,TipoOdg,Profilo,Atto,ModelloHtml,OrdineGiorno,$timeout,$sce) {
  $controller('SedutaGiuntaDetailController', { $scope: $scope });
  $scope.profiloattivo = $rootScope.profiloattivo;
  $scope.visualizzaOggetto = $rootScope.visualizzaOggetto;
  $scope.inizializzaOdgAtto = function() {
  $scope.taskBpm = {};	  
  	  $rootScope.$on("loadDettaglioTask", function(){
			Lavorazione.dettaglioTask({taskBpmId: $stateParams.taskBpmId },function(data){
				$scope.taskBpm = data;
			});
	  });

	  Lavorazione.dettaglioTask({taskBpmId: $stateParams.taskBpmId },function(data){
		$scope.taskBpm = data;
	  });
	  if ($scope.sedutaGiunta.odgs) {
		  for (var i = 0; i < $scope.sedutaGiunta.odgs.length; i++) {
			  if ($scope.sedutaGiunta.odgs[i].attos) {
				  for (var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++) {
					 if ($scope.sedutaGiunta.odgs[i].attos[j].atto.id == $scope.attoSelId) {
						 $scope.attoOdgSel = $scope.sedutaGiunta.odgs[i].attos[j];
						 $scope.odgSedutaSel = $scope.sedutaGiunta.odgs[i];
					 } 
				  }
			  }
		  }
	  }
  }
})
.directive('showDatiSedutaAtto', function() {
  return {
	  restrict: 'E',
	  scope: {
		  sedutaAttoId: '=',
		  attoSelId: '='
	  },
	  controller: 'DatiSedutaAttoController',
	  templateUrl: 'scripts/app/entities/sedutaGiunta/atto-datiSeduta.html'
  };
});

