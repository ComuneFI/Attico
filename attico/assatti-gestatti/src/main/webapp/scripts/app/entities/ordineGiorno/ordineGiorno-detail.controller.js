'use strict';

angular.module('cifra2gestattiApp')
.controller('OrdineGiornoDetailController', function ($scope, $stateParams, OrdineGiorno, SedutaGiunta, TipoOdg, ArgumentsOdgService,
		Atto, $rootScope,ParseLinks, ModelloHtml,ngToast) {
	$scope.ordineGiorno = {};
	$scope.resultAttiOdg = [];
	$scope.resultAttiOdg.allItemsSelected = false;
	$scope.load = function (id) {
		OrdineGiorno.get({id: id}, function(result) {
			$scope.ordineGiorno = result;
		});
	};
	
	$scope.summernoteOptions = {
			  height: 300,
			  focus: false,
			  airMode: false,
			  lang: "it-IT",
			  toolbar: [
			          ['edit',['undo','redo']],
			          ['fontclr', ['color']],
			          ['headline', ['style']],
			          ['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
			          ['fontface', ['fontname']],
			          ['textsize', ['fontsize']],
			          
			          ['alignment', ['ul', 'ol', 'paragraph', 'lineheight']],
			          ['height', ['height']],
			          ['table', ['table']],
			          /*['insert', ['picture','hr','pagebreak']],*/
				  	  ['insert', ['hr','pagebreak']],
			          ['view', ['fullscreen', 'codeview']]
			          ]
			};

	$scope.modelloHtmls = ModelloHtml.query();
	
	$scope.sedutagiuntas = SedutaGiunta.query();

	$scope.save = function () {
		if ($scope.ordineGiorno.id != null) {
			OrdineGiorno.update($scope.ordineGiorno,
					function (result) {
				$scope.ordineGiorno=result;
				OrdineGiorno.saveArgumentsOdg({odgId:$scope.ordineGiorno.id},$scope.arguments,function(data){
				});
			});
		} else {
			OrdineGiorno.save($scope.ordineGiorno,
					function (result) {
				$scope.ordineGiorno=result;
				OrdineGiorno.saveArgumentsOdg({odgId:$scope.ordineGiorno.id},$scope.arguments,function(data){
				});
			});
		}
		
	};




	$scope.load = function (id,section) {
		$scope.tipoNuovo=id;
		$scope.arguments = ArgumentsOdgService.getArguments();
		if('nuovo' === id ){
			$scope.ordineGiorno = {numeroOdg: null, protocollo: null, dataPubblicazioneSito: null, idDiogene: null, id: null};
		}else if(id > 0){
			// $log.debug('load');
			OrdineGiorno.get({id: id}, function(result) {
				$scope.ordineGiorno = result;
			});
			if('view' === section ||$scope.arguments.lenght==0){
				for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
					 tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
				 }
				$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:tipiAttoIds, idOdg:id} ;
				Atto.search( $scope.criteria , $scope.criteria ,function (resultOdg, headers) {
					$scope.arguments = resultOdg;
				});
			}

		}
		
		TipoOdg.query(function(result) {
			$scope.tipoodgs =result;
			$scope.tipoOdgDisabled = false;
			if('suppletivo' === id|| 'fuorisacco' === id){
				OrdineGiorno.query(function(result, headers) {
					$scope.odglist = result;
				});
				$scope.tipoOdgDisabled = true;
				for (var i = 0; i < $scope.tipoodgs.length; i++) {
					if( $scope.tipoodgs[i].descrizione.toLowerCase().replace(/ /g,'') === id){
						$scope.ordineGiorno.tipoOdg={};
						$scope.ordineGiorno.tipoOdg.id = $scope.tipoodgs[i].id;	
						break;
					}
				}
			} else {
				var temptipoodgs =  $scope.tipoodgs;
				for (var i = 0; i < temptipoodgs.length; i++) {
					if( !(temptipoodgs[i].descrizione ==='Ordinario') && !(temptipoodgs[i].descrizione ==='Straordinario') ){
						 $scope.tipoodgs.splice($scope.tipoodgs.indexOf(temptipoodgs[i]), 1);
						 if(i===$scope.tipoodgs.length-1){
							 i--;
						 }
					}
				}
			}
		});
		
			/*for (var i = 0; i < $scope.resultAttiOdg.length; i++) {
				if('suppletivo' === section){
					$scope.tipoodgs[]	
				}
			}*/
	};
	$scope.load($stateParams.id, $stateParams.section);
});
