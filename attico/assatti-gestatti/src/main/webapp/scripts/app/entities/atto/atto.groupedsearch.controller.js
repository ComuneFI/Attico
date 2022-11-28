'use strict';

angular.module('cifra2gestattiApp')
.controller('AttoGroupedSearchController', function ($scope,$log, localStorageService, Atto,TipoIter, TipoFinanziamento,
    TipoAtto, Aoo, ParseLinks, $filter,$rootScope,$stateParams, $state, ModelloHtml, $timeout, $q) {
	$scope.loading = false;
	
	$scope.tipoRicerca = 'grouped-search';
	$scope.tipoAtto = $stateParams.atto;
	$scope.hiddenCols = [];
	$scope.criteria = {};
	$scope.tipoattos = [];
	$scope.tipiIter = [];
	
	TipoIter.getByCodiceTipoAtto({codiceTipoAtto: $scope.tipoAtto}, function(collection){
		var output = [], keys = [];
	    angular.forEach(collection, function(item) {
	        var key = item['descrizione'];
	        if(keys.indexOf(key) === -1) {
	        	keys.push(key);
	        }
	    });
	    $scope.tipiIter = keys;
	});
	
	TipoFinanziamento.getAll(function(collection){
		var output = [], keys = [];
	    angular.forEach(collection, function(item) {
	        var key = item['descrizione'];
	        if(keys.indexOf(key) === -1) {
	        	keys.push(key);
	        }
	    });
	    $scope.tipiFinanziamento = keys;
	});
	
	
	
	$scope.init = function(tab){
		$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id};
		$scope.criteria.viewtype = (tab ? tab : 'conclusi');
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		$scope.criteria.type = 'grouped-search';
		$scope.annoList = [];
		$scope.annoCorrente = ( new Date() ).getFullYear();
		
		if($stateParams.anno != 'current'){
			$scope.thisYear = false;
			for(var i=1; i <5; i++){
				$scope.annoList.push($scope.annoCorrente - i);
			}
			$scope.criteria.anno = $scope.annoCorrente - 1;
		}else{
			$scope.thisYear = true;
		}

		$scope.mainGroup = {};
		$scope.defaultDirezione = null;
		
		$scope.groupVisibility = {};
		$scope.direzioni = [];
		
		Aoo.getAllDirezioni({}, function(res){
			$scope.direzioni = res;
			Aoo.getDirezioneOfAoo({aooId:$rootScope.profiloattivo.aoo.id}, function(dir){
				$scope.mainGroup.ufficioId = dir.id;
				$scope.defaultDirezione = dir;
			});
		});
		
		if(!$scope.tipoAttoObj){
			$scope.hiddenCols = [];
			TipoAtto.getByCodice({codiceParam: $scope.tipoAtto}, function(tipo){
				if(tipo){
					$scope.tipoAttoObj = tipo;
					if($scope.tipoAttoObj && $scope.tipoAttoObj.attoRevocatoHidden){
						$scope.hiddenCols.push('codicecifraAttoRevocato');
					}
					if($scope.tipoAttoObj && $scope.tipoAttoObj.tipoIterHidden){
						$scope.hiddenCols.push('tipoIter');
					}
					if($scope.tipoAttoObj && $scope.tipoAttoObj.codiceCigHidden){
						$scope.hiddenCols.push('cig');
					}
					if($scope.tipoAttoObj && $scope.tipoAttoObj.codiceCupHidden){
						$scope.hiddenCols.push('cup');
					}
					if($scope.tipoAttoObj && $scope.tipoAttoObj.tipoFinanziamentoHidden){
						$scope.hiddenCols.push('tipoFinanziamento');
					}
				}
			});
		}
		
		
		
		
	};
	
	$scope.init();
	
	$scope.settaFlagRicercaUfficiDisattiva = function(){
		let flag = false;
		if($scope.direzioni && $scope.mainGroup.ufficioId){
			for(var i = 0; i < $scope.direzioni.length; i++){
				if($scope.direzioni[i].id == $scope.mainGroup.ufficioId){
					flag = $scope.direzioni[i].validoal!=null;
					break;
				}
			}
		}
		$scope.criteria.ufficiDisattivati = flag;
	};
	
	$scope.openCloseGroup = function(id){
		if($scope.groupVisibility[id]){
			$scope.groupVisibility[id] = false;
		}else{
			var aoo = $scope.getGroupByUfficioId($scope.mainGroup, id);
			if(aoo && aoo.group){
				$scope.groupVisibility[id] = true;
			}else{
				$scope.doSearchGrouped(id).then(function(){
					$scope.groupVisibility[id] = true;
				});
			}
		}
	};
	
	$scope.getGroupByUfficioId = function(group, aooIdGroup){
		if(group && aooIdGroup){
			if(group.ufficioId == aooIdGroup){
				return group;
			}else if(group.aoos && group.aoos.length > 0){
				for(var i = 0; i < group.aoos.length; i++){
					if(group.aoos[i].id == aooIdGroup){
						return group.aoos[i];
					}else if(group.aoos[i].group){
						var g = $scope.getGroupByUfficioId(group.aoos[i].group, aooIdGroup);
						if(g){
							return g;
						}
					}
				}
			}
		}
		return null;
	};
	
	$scope.doSearchGrouped = function(aooIdGroup){
		var deferred = $q.defer();
		
		if(aooIdGroup){
			$scope.tempSearch.aooIdGroup = aooIdGroup;
		}else{
			$scope.groupVisibility = {};
			$scope.tempSearch.aooIdGroup = $scope.mainGroup.ufficioId;
			$scope.mainGroup = {};
		}
		Atto.searchGrouped( $scope.tempSearch, $scope.tempSearch , function(result, headers) {
			if($state.current.name == "attoList"){
				localStorageService.set('_attoSearch', $scope.tempSearch);
				localStorageService.set('_attoSearch_tipoRicerca', $scope.tipoRicerca);
				localStorageService.set('_attoSearch_tipoAtto', $scope.tipoAtto);
			}
			if(aooIdGroup){
				var aoo = $scope.getGroupByUfficioId($scope.mainGroup, aooIdGroup);
				aoo.group = result;
				if(aoo.group.attos && aoo.group.attos.length) {
		        	for(var i = 0; i< aoo.group.attos.length; i++){
			        	if(aoo.group.attos[i].numeroAdozione) {
			        		aoo.group.attos[i].numeroAdozione = aoo.group.attos[i].numeroAdozione.substr(aoo.group.attos[i].numeroAdozione.length - 5);
			        	}
			    	}
		        }
			}else{
				$scope.mainGroup = result;
				if($scope.mainGroup.attos && $scope.mainGroup.attos.length) {
		        	for(var i = 0; i< $scope.mainGroup.attos.length; i++){
			        	if($scope.mainGroup.attos[i].numeroAdozione) {
			        		$scope.mainGroup.attos[i].numeroAdozione = $scope.mainGroup.attos[i].numeroAdozione.substr($scope.mainGroup.attos[i].numeroAdozione.length - 5);
			        	}
			    	}
		        }
			}
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	        $scope.loading = false;
	        deferred.resolve();
	    }, function(err){
	    	$scope.loading = false;
	    	deferred.resolve();
	    });
		return deferred.promise;
	};
	
	TipoAtto.query({}, function(result){
    	$scope.tipoattos = result;
    });
	
	$scope.isHiddenCol = function(col){
		return $scope.hiddenCols.indexOf(col) > -1;
	};
	
	$scope.getDescrizioneTipoattoByCodice = function(codice){
    	var ris = "";
    	if($scope.tipoattos && $scope.tipoattos.length && $scope.tipoattos.length > 0){
    		for(var i = 0; i < $scope.tipoattos.length; i++){
    			if($scope.tipoattos[i].codice == codice){
    				ris = $scope.tipoattos[i].descrizione;
    				break;
    			}
    		}
    	}
    	return ris;
    };
	
	ModelloHtml.query({tipoDocumento:'report_ricerca'}, function(result) {
        $scope.modelloHtmls = result;
    });
	
	$scope.stati = [];
	Atto.caricastati({tipoAtto:$scope.tipoAtto},function(collection){
		$scope.stati = collection;
	});
	
	$scope.ordinamento = function(ordinamento){
		
		if($scope.criteria.ordinamento == ordinamento){
			if($scope.criteria.tipoOrinamento == "asc"){
				$scope.criteria.tipoOrinamento = "desc";
			}else{
				$scope.criteria.tipoOrinamento = "asc";
			}
		}else{	
			$scope.criteria.tipoOrinamento = "asc"; 
		}
		$scope.criteria.ordinamento = ordinamento;
		$scope.loadAll();
	};
	
	$scope.ricerca = function() {
		if(!$scope.mainGroup){
			return;
		}
    	$scope.loadAll();
	};
	
	$scope.loadAll = function() {
			$scope.loading = true;
			$scope.attos = [];
			if($stateParams.anno == 'current'){
				$scope.criteria.anno = $scope.annoCorrente;
			}else if(!$scope.criteria.anno){
				return;
			}
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
			$scope.tempSearch.type = 'grouped-search';
			if(!$scope.criteria.oldS && angular.isUndefined($scope.isCollegamentoAtto) || !$scope.isCollegamentoAtto){
				$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
			}
			
			if ($scope.tipoAtto) {
				$scope.tempSearch.codiceTipoAtto = $scope.tipoAtto;
			}
			if(!$scope.tipoAttoObj){
				$scope.hiddenCols = [];
				TipoAtto.getByCodice({codiceParam: $scope.tipoAtto}, function(tipo){
					if(tipo){
						$scope.tipoAttoObj = tipo;
						if($scope.tipoAttoObj && $scope.tipoAttoObj.attoRevocatoHidden){
							$scope.hiddenCols.push('codicecifraAttoRevocato');
						}
						if($scope.tipoAttoObj && $scope.tipoAttoObj.tipoIterHidden){
							$scope.hiddenCols.push('tipoIter');
						}
						if($scope.tipoAttoObj && $scope.tipoAttoObj.codiceCigHidden){
							$scope.hiddenCols.push('cig');
						}
						if($scope.tipoAttoObj && $scope.tipoAttoObj.codiceCupHidden){
							$scope.hiddenCols.push('cup');
						}
						if($scope.tipoAttoObj && $scope.tipoAttoObj.tipoFinanziamentoHidden){
							$scope.hiddenCols.push('tipoFinanziamento');
						}
					}
					$scope.doSearchGrouped();
				});
			}else{
				$scope.doSearchGrouped();
			}
	};
	
	$scope.esportaRicercaXls = function(){
		var cols = document.querySelectorAll('[colname]');
		var colNames = [];
		for(var i = 0; i < cols.length; i++){
			for(var j = 0; j<cols[i].attributes.length; j++){
				if(cols[i].attributes[j].name == "colname"){
					colNames.push(cols[i].attributes[j].value);
					break;
				}
			}
		}
		var json = jQuery.extend(true, {}, $scope.tempSearch);
		json.colnames = colNames;
		json.aooIdGroup = $scope.mainGroup.ufficioId;
		var res;
		if(json){
			res = JSON.stringify(json);
		}else{
			res = "{}";
		}
		
		window.open($rootScope.buildDownloadUrl('api/attos/xls') + '&attoCriteriaStr=' + window.encodeURIComponent(res) + '&access_token='+ $rootScope.access_token,'_blank');
	};
	
	$scope.esportaRicercaXml = function(){
		var json = jQuery.extend(true, {}, $scope.tempSearch);
		json.aooIdGroup = $scope.mainGroup.ufficioId;
		var res;
		if(json){
			res = JSON.stringify(json);
		}else{
			res = "{}";
		}
		window.open($rootScope.buildDownloadUrl('api/attos/xml') + '&attoCriteriaStr=' + window.encodeURIComponent(res) + '&access_token='+ $rootScope.access_token,'_blank');
	};
	
	$scope.esportaRicercaPdf = function(idModello){
		var cols = document.querySelectorAll('[colname]');
		var colNames = [];
		for(var i = 0; i < cols.length; i++){
			for(var j = 0; j<cols[i].attributes.length; j++){
				if(cols[i].attributes[j].name == "colname"){
					colNames.push(cols[i].attributes[j].value);
					break;
				}
			}
		}
		var json = jQuery.extend(true, {}, $scope.tempSearch);
		json.aooIdGroup = $scope.mainGroup.ufficioId;
		json.colnames = colNames;
		var res;
		if(json){
			res = JSON.stringify(json);
			res = res.split("/").join("slash");
		}else{
			res = "{}";
		}
		window.open($rootScope.buildDownloadUrl('api/attos/reportpdf/'+idModello+'/' + window.encodeURIComponent(res) + '/endSearch') +'&access_token='+ $rootScope.access_token,'_blank');
	};

	$scope.reportAVCPANAC = function(){
		var colNames = [];
		colNames.push("1-codiceCig");
		colNames.push("2-tipoAtto");
		colNames.push("3-codiceAtto");
		colNames.push("4-strutturaProponente");
		colNames.push("5-oggetto");
		colNames.push("6-dataInizio");
		colNames.push("7-dataFine");
		colNames.push("8-tipoIter");
		colNames.push("9-dataEsecutivita");
		var json = jQuery.extend(true, {}, $scope.tempSearch);
		json.colnames = colNames;
		json.aooIdGroup = $scope.mainGroup.ufficioId;
		var res;
		if(json){
			res = JSON.stringify(json);
		}else{
			res = "{}";
		}
		window.open($rootScope.buildDownloadUrl('api/attos/avcanac') + '&attoCriteriaStr=' + window.encodeURIComponent(res) + '&access_token='+ $rootScope.access_token,'_blank');
	};
	
	
	$scope.resetRicerca = function(){
		if(!$scope.mainGroup){
			return;
		}
		var temp = $scope.criteria.viewtype;
		
		$scope.criteria = {viewtype:temp} ;
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		$scope.loadAll();
	};

	$scope.refresh = function () {
	    $scope.loadAll();
	    $scope.clear();
	};


	$scope.clear = function () {
	    $scope.atto = {sottoscrittori:[] };
	 };
	 
	$scope.initTooltip = function () {
		$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
	};
	
});
