/**
 * Ricerca Controller
 */
'use strict';

angular.module('cifra2gestattiApp')
.controller('AttoRicercaController', function ($scope,$log,$http, Atto, Profilo, SottoMateria, Materia, TipoMateria, 
    TipoAtto, ArgomentoOdg, TipoAdempimento, Aoo, Ufficio, TipoIter, SedutaGiunta,  ParseLinks ,$rootScope,ProfiloAccount) {
	
	$log.debug("Ricerca libera");
	$log.debug("Profilo:",$rootScope.profiloattivo);
	$scope.operatori = [
	                    {id:"like",descrizione:"Contiene"},
	                    {id:"eq",descrizione:"Uguale a"},
	                    {id:"neq",descrizione:"Diverso da"},
	                    {id:"maggiore",descrizione:"Maggiore di"},
	                    {id:"maggioreuguale",descrizione:"Maggiore uguale di"},
	                    {id:"minore",descrizione:"Minore di"},
	                    {id:"minoreuguale",descrizione:"Minore uguale di"}
	                    ];
	
	$scope.relazioni = [
	                    {id:"and",descrizione:"AND"},
	                    {id:"or",descrizione:"OR"},
	                    ];
	
	$scope.ordinamenti = [
	                    {id:"asc",descrizione:"Ascendente"},
	                    {id:"desc",descrizione:"Discendente"},
	                    ];
	
	
	
	$scope.campi = [];
	$scope.campiCriteria = [];

	$http.get("/scripts/components/summernote/plugin/atto.json")
	.then(function(response) {
		
		angular.forEach(response.data, function(campo, key) {
			if(typeof campo.show == "undefined" || campo.show == true){
				
				if( key.indexOf('atto.aoo') < 0 || $scope.disableAoo() == false){
					if(campo.template == "text"){
						$scope.campiCriteria.push({id:campo.key,descrizione:campo.titolo});
					}
					else if(campo.template == "list"){
						angular.forEach(campo.fields, function(item, key2) {
							if(item.template == "text"){
								$scope.campiCriteria.push({id:campo.key + "." + item.key,descrizione:campo.titolo + " - " + item.titolo});
							}
						});
					}
				}
				
				if(campo.template == "text"){
					$scope.campi.push({id:campo.key,descrizione:campo.titolo});
				}
				else if(campo.template == "list"){
					angular.forEach(campo.fields, function(item, key2) {
						if(item.template == "text"){
							$scope.campi.push({id:campo.key + "." + item.key,descrizione:campo.titolo + " - " + item.titolo});
						}
					});
				}
			}
		});
		
    });
	
	$scope.disableAoo = function() {
//		TODO: verificare per esigenze di ATTICO
//		if( (ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_DIRIGENZIALI'])) ||
//				(ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_GIUNTA']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_GIUNTA'])) ){
//			return true;
//		}else{
			return false;
//		}
	};
	
	$scope.criteri = [];
	$scope.colonnericerca = [];
	
	$scope.addCriterio = function (  ) {
		$scope.criterio = {campo: "",condizione:"",valore:"",relazioneAltroCampo:{id:"and",descrizione:"AND"}};
		$scope.criteri.push( $scope.criterio ) ;
	};
	
	$scope.deleteCriterio = function ( index ) {
		$scope.criteri.splice(index ,1);
	};
	$scope.attos = [];
	$scope.page = 1;
	$scope.criteria = {} ;
	$scope.criteria.page = $scope.page;
	$scope.criteria.per_page = 10;
	
	$scope.ricerca = function(){
		$scope.page = 1;
		$scope.criteria.page = $scope.page;
		$scope.criteria.campiWhere =  [];
		if($scope.disableAoo() == false){
			$scope.criteria.campiWhere.push({
				campo: 'atto.aoo.codice',
				condizione:"eq",
				relazioneAltroCampo:"and",
				tipoCampo:"String",
				valore: $rootScope.profiloattivo.aoo.codice
			});
		}
		angular.forEach($scope.criteri, function(item,key){
			$log.debug("key:",key);
			$log.debug("item:",item);
			var temp = jQuery.extend(true, {}, item);
			temp.campo = item.campo.id;
			temp.condizione = item.condizione.id;
			if(angular.isDefined(item.relazioneAltroCampo) && item.relazioneAltroCampo != null){
				temp.relazioneAltroCampo = item.relazioneAltroCampo.id;
			}
			
			/*----Test----*/
			temp.tipoCampo = "String";
			/*------------*/
			$scope.criteria.campiWhere.push(temp);
			
		});
		
		//$scope.criteria.campiWhere=$scope.criteri;
		//$scope.criteria.campiSelect = $scope.colonnericerca;
		$scope.criteria.campiSelect =  [];
		angular.forEach($scope.colonnericerca, function(item,key){
			$log.debug("key:",key);
			$log.debug("item:",item);
			$scope.criteria.campiSelect.push(item.id);
		});
		
		var tempSearch = jQuery.extend(true, {}, $scope.criteria);
		
		tempSearch.tipoOrdinamento = tempSearch.tipoOrdinamento.id;
		tempSearch.ordinamento = tempSearch.ordinamento.id;
		tempSearch.gruppoRuolo = $rootScope.profiloattivo.grupporuolo.id;
		tempSearch.profiloId = $rootScope.profiloattivo.id;
//		tempSearch.aooId = $rootScope.profiloattivo.aoo.id;
		
		$log.debug("Criteri:",$scope.criteria);
		Atto.searchlibera(tempSearch, tempSearch , function(result, headers) {
	        $scope.attos = result;
	        $log.debug("atto.search:",$scope.attos);
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	    });
	};
	
	$scope.resetRicerca = function(){
		$scope.page = 1;
		$scope.criteri = [];
		$scope.colonnericerca = [];
		$scope.criteria = {};
	};
	
	$scope.loadPage = function(page) {
	    $scope.page = page;
	    $scope.criteria.page =  $scope.page;
	    $scope.ricerca();
	};
	
	$scope.isCampoData = function(campo) {
		if(angular.isDefined(campo.id)){
			var r = new RegExp('data'); 
			return r.test(campo.id);
		}
		
		return false;
		
	};
});