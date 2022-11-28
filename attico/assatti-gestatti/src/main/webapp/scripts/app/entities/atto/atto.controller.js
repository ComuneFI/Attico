'use strict';

angular.module('cifra2gestattiApp')
.controller('AttoController', function ($scope,$log, localStorageService, Atto, Profilo, SottoMateria, Materia, TipoMateria, 
    TipoAtto, ArgomentoOdg, TipoAdempimento, Aoo, Ufficio, TipoIter, SedutaGiunta, Esito, ParseLinks , $filter,$rootScope,$stateParams, $state, ModelloHtml,ProfiloAccount, $timeout) {
	$scope.loading = false;
	
	$scope.tipoRicerca = $stateParams.tipo;
	$scope.tipoAtto = $stateParams.atto;
	$scope.idAtto = $stateParams.id;
	$scope.hiddenCols = [];
	
	$scope.tipoattos = [];
	
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
	
	if($state.current.name == "attoList" && $.inArray('_attoSearch', localStorageService.keys()) >= 0 && $scope.tipoRicerca == localStorageService.get('_attoSearch_tipoRicerca') && $scope.tipoAtto == localStorageService.get('_attoSearch_tipoAtto')){
		$scope.criteria = localStorageService.get('_attoSearch');
		if($scope.criteria){
			$scope.criteria.oldS = true;
		}
	}else{
		$scope.criteria = {};
		$scope.criteria.oldS = false;
	}
	
	ModelloHtml.query({tipoDocumento:'report_ricerca'}, function(result) {
        $scope.modelloHtmls = result;
    });
	
	if(angular.isDefined($scope.ngModel)){
		$scope.isCollegamentoAtto = true;
	}else if($state.current.name == "clonaAtto"){
		$scope.isClonaAtto = true;
	}
	
	$timeout(function(){
		if(angular.isDefined($scope.ngModel)){
			$scope.isCollegamentoAtto = true;
		}else if($state.current.name == "clonaAtto"){
			$scope.isClonaAtto = true;
		}
	}, 1000);
	
	$scope.annoList = [];
	$scope.annoCorrente = ( new Date() ).getFullYear();
	$scope.regolamenti=[{valore:'no',label:'NO'},{valore:'provvisorio',label:'Regolamento Provvisorio'},{valore:'definitivo',label:'Regolamento Definitivo'}];
	
	for(var i=1; i <5; i++){
		$scope.annoList.push($scope.annoCorrente - i);
	}
	
	$scope.esitiSeduta = [];
	$scope.tipiSeduta = [
	                     {id: 1, label:'Seduta Ordinaria'},
	                     {id: 2, label:'Seduta Straordinaria'}
	                     ];
	$scope.assentiList = [
	                     {id: 1, label:'Si'},
	                     {id: 0, label:'No'}
	                     ];
	$scope.esitiSedutaAll = [];
	Esito.query(function(result){
		$scope.esitiSedutaAll = result;
		$log.debug("esitiSeduta:",result);
		var keys = [];
	    angular.forEach(result, function(item) {
	    	var id = item['id'];
	        var key = item['label'];
	        if(keys.indexOf(key) === -1 && item['tipiAtto']) {
	        	//for(var i=0; i < $scope.tipiAttoIds.length ; i++){
	        		if(item['tipiAtto'].indexOf($scope.tipoAtto) != -1 || $scope.tipoAtto=='sedute'){
	        			keys.push(key);
	        		}
	        	//}
	            
	        }
	    });
	    $scope.esitiSeduta = keys;
		
	});
	$scope.getEsitoLabel = function(id) {
		for(var i= 0; i<$scope.esitiSedutaAll.length; i++) {
			if($scope.esitiSedutaAll[i].id == id){
				return $scope.esitiSedutaAll[i].label;
			}
		}
	};
	
	
	$log.debug("tipoRicerca=",$scope.tipoRicerca);
	$log.debug("tipoAtto=",$scope.tipoAtto);
	
	$scope.statoAttoFisso = "";
	
	$scope.attos = [];
	$scope.sedutas=[];
    $scope.page = 1;
    
    $scope.attoRevocato = null;
    
    $scope.selezionaAttoRevocato = function(atto){
    	$scope.attoRevocato = atto;
    };
    
    $scope.salvaAttoRevocato = function(){
    	if($scope.attoRevocato != null){
    		if(!$scope.provvisorio){
	    		$scope.ngModel.codicecifraAttoRevocato = $scope.attoRevocato.codiceCifra;
	    		$scope.ngModel.numeroAdozioneAttoRevocato = $scope.attoRevocato.numeroAdozione;
	    		$scope.ngModel.dataAdozioneAttoRevocato = $scope.attoRevocato.dataAdozione;
	    		$scope.ngModel.attoRevocatoId = $scope.attoRevocato.id;
    		}
    		else{
    			$scope.ngModel.codicecifraAttoProvvisorio = $scope.attoRevocato.codiceCifra;
	    		$scope.ngModel.numeroAdozioneAttoProvvisorio = $scope.attoRevocato.numeroAdozione;
	    		$scope.ngModel.dataAdozioneAttoProvvisorio = $scope.attoRevocato.dataAdozione;
    		}
    	}
    	else {
    		if(!$scope.provvisorio){
	    		$scope.ngModel.codicecifraAttoRevocato = null;
	    		$scope.ngModel.numeroAdozioneAttoRevocato = null;
	    		$scope.ngModel.dataAdozioneAttoRevocato = null;
	    		$scope.ngModel.attoRevocatoId = null;
    		}
    		else{
    			$scope.ngModel.codicecifraAttoProvvisorio = null;
	    		$scope.ngModel.numeroAdozioneAttoProvvisorio = null;
	    		$scope.ngModel.dataAdozioneAttoProvvisorio = null;
    		}
    	}
    	if(!$scope.provvisorio)
    		$("#ricercaAttoRevocato").modal('hide');
    	else
    		$("#ricercaAttoProvvisorio").modal('hide');
    };
    
    $scope.rimuoviAttoRevocato = function(){
    	if(!$scope.provvisorio){
	    	$scope.ngModel.codicecifraAttoRevocato = null;
			$scope.ngModel.numeroAdozioneAttoRevocato = null;
			$scope.ngModel.dataAdozioneAttoRevocato = null;
	    	$("#ricercaAttoRevocato").modal('hide');
    	}else{
    		$scope.ngModel.codicecifraAttoProvvisorio = null;
    		$scope.ngModel.numeroAdozioneAttoProvvisorio = null;
    		$scope.ngModel.dataAdozioneAttoProvvisorio = null;
    		$("#ricercaAttoProvvisorio").modal('hide');
    	}
    };
    
    $scope.annullaAttoRevocato = function(){
    	$("#ricercaAttoRevocato").modal('hide');
    	$("#ricercaAttoProvvisorio").modal('hide');
    };
    
    
    TipoAtto.query(function(collection){
		var output = [], keys = [];
		angular.forEach(collection, function(item) {
			var key = item['descrizione'];
			if(keys.indexOf(key) === -1) {
				keys.push(key);
			}
		});
		$scope.tipiAtto = keys;
	});
    
    $scope.tipiAttoIds = [];
    $scope.initTipiAttoTab = function(tipiAttoCodici){
    	var tipiAttoIds = [];
        /*if(angular.isDefined($rootScope.profiloattivo) && $rootScope.profiloattivo.id != '0' && $rootScope.profiloattivo.id != 0){
    		for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
    			if(tipiAttoCodici != 'sedute' && tipiAttoCodici.indexOf($rootScope.profiloattivo.tipiAtto[i].codice) > -1){
    				tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
    			}else if(tipiAttoCodici == 'sedute'){
    				tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
    			}
    		}
        }*/
    	$scope.elencoTipiAtto=[];
    	TipoAtto.query(function(collection){
    		$scope.elencoTipiAtto = collection;
    		
    		if(ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && tipiAttoCodici == 'DIR'){
        		for(var i = 0; i < $scope.elencoTipiAtto.length; i++){
        			if(tipiAttoCodici.indexOf($scope.elencoTipiAtto[i].codice) > -1){
        				tipiAttoIds.push($scope.elencoTipiAtto[i].id);
        			}
        		}
        		//tipiAttoIds.push(tipiAttoCodici);    		
        	}
        	if(ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_GIUNTA']) && (tipiAttoCodici == 'DEL' || tipiAttoCodici == 'SDL' || tipiAttoCodici == 'COM' || tipiAttoCodici == 'sedute')){
        		if(tipiAttoCodici != 'sedute'){
        			for(var i = 0; i < $scope.elencoTipiAtto.length; i++){
            			if(tipiAttoCodici.indexOf($scope.elencoTipiAtto[i].codice) > -1){
            				tipiAttoIds.push($scope.elencoTipiAtto[i].id);
            			}
            			if(tipiAttoCodici == 'SDL' && 'DDL'.indexOf($scope.elencoTipiAtto[i].codice) > -1){
            				tipiAttoIds.push($scope.elencoTipiAtto[i].id);
            			}
            		}
        		}
        		else {
        			//tipiAttoIds = ['DEL','SDL','DDL','COM'];
        			for(var i = 0; i < $scope.elencoTipiAtto.length; i++){
            			if('DEL'.indexOf($scope.elencoTipiAtto[i].codice) > -1 || 'SDL'.indexOf($scope.elencoTipiAtto[i].codice) > -1 || 'DDL'.indexOf($scope.elencoTipiAtto[i].codice) > -1 || 'COM'.indexOf($scope.elencoTipiAtto[i].codice) > -1){
            				tipiAttoIds.push($scope.elencoTipiAtto[i].id);
            			}
            		}
        		}
        	}
        	$log.debug("$scope.tipiAttoIds:",tipiAttoIds);
        	$scope.tipiAttoIds = tipiAttoIds;   
        	if(!$scope.tipoAtto){
	        	TipoIter.query(function(collection){
	        		var output = [], keys = [];
	        	    angular.forEach(collection, function(item) {
	        	        var key = item['descrizione'];
	        	        if(keys.indexOf(key) === -1) {
	        	        	// for(var i=0; i < $scope.tipiAttoIds.length ; i++){
	        	        	//	if(item['tipoAtto']['id'] == $scope.tipiAttoIds[i]){
	        	        			keys.push(key);
	        	        	//	}
	        	        	// }
	        	            
	        	        }
	        	    });
	        	    $scope.tipiIter = keys;
	        	});
        	}else{
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
        	}
        	$scope.loadAll();
    	});
    };
	
//	TODO: codici dei tipi atto non coincidono in ATTICO
//	if(!$scope.criteria.oldS && ((ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && $scope.tipoAtto == 'DIR') ||
//			(ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_GIUNTA']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_GIUNTA']) && ($scope.tipoAtto  == 'DEL' || $scope.tipoAtto  == 'SDL' || $scope.tipoAtto  == 'COM' || $scope.tipoAtto  == 'sedute')) )){
//		$scope.criteria = {aooId:null, tipiAttoIds:$scope.tipiAttoIds} ;
//	}
//	else if(!$scope.criteria.oldS){
		$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:$scope.tipiAttoIds} ;
//	}
	
	$log.debug("$scope.attoRevocato",$scope.attoRevocato);
	if($scope.attoRevocato != null){
		delete $scope.criteria.aooId;
	}
	if(!$scope.criteria.oldS){
		$scope.criteria.page = $scope.page;
		$scope.criteria.per_page = 10;
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		$scope.criteria.viewtype = 'conclusi';
	}
	Aoo.padri(function(collection){
		$log.debug("AOOS:",collection);
	    
	    $scope.listaaoo = collection;
	});
	
	$scope.stati = [];
	if(!$scope.isClonaAtto){
		Atto.caricastati({tipoAtto:$scope.tipoAtto},function(collection){
			$scope.stati = collection;
		});
	}else{
		Atto.caricastati({},function(collection){
			$scope.stati = collection;
		});
	}
	

 
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
    	$scope.page = 1;
    	$scope.criteria.page = $scope.page;
    	$scope.loadAll();
	};
	
	$scope.loadAll = function(isRevocaSearch) {
		//evita di effettuare la query al load del controller
		if($state.current.name == 'attoDetailSection' && !isRevocaSearch){
			$scope.attos = [];
			return;
		}
		//  && $state.current.name != "attoDetailSection"
		if($state.current.name != "attoDetail"){
			if(angular.isDefined($scope.ngModel)){
				$scope.isCollegamentoAtto = true;
			}else if($state.current.name == "clonaAtto"){
				$scope.isClonaAtto = true;
			}
			
			$scope.loading = true;
			$scope.attos = [];
			if((angular.isDefined($scope.isCollegamentoAtto) && $scope.isCollegamentoAtto == true) || (angular.isDefined($scope.isClonaAtto) && $scope.isClonaAtto == true)){
				/*Nessun filtro su anno*/
			}
			else if(angular.isUndefined($scope.tipoRicerca) || $scope.tipoRicerca == 'anno' ){
				$scope.criteria.anno = $scope.annoCorrente;
				//$scope.criteria.oggetto = 'test';
				
			}
			else if(angular.isUndefined($scope.criteria.anno) || $scope.criteria.anno == null || $scope.criteria.anno ==''){
				$scope.criteria.anno = $scope.annoCorrente - 1;
			}
			if(!$scope.isClonaAtto){
				if(!$scope.criteria.oldS && $scope.tipoAtto == 'sedute' && !((angular.isDefined($scope.isCollegamentoAtto) && $scope.isCollegamentoAtto == true) || (angular.isDefined($scope.isClonaAtto) && $scope.isClonaAtto == true))){
					$scope.criteria.viewtype='elencopersedute';
					$scope.criteria.page=1;
					$scope.criteria.per_page=1000000;
					//$scope.criteria.aooId = null;
				}
				if( (!$scope.criteria.oldS) && ($state.current.name != "attoDetailSection")){
					$scope.criteria.tipiAttoIds = $scope.tipiAttoIds;
				}
				
				$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
		//		if(angular.isDefined($scope.tempSearch.area)){
		//			$scope.tempSearch.area = $scope.tempSearch.area.codice;
		//		}
				if(!$scope.criteria.oldS && angular.isUndefined($scope.isCollegamentoAtto) || !$scope.isCollegamentoAtto){
					$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
				}else if(!$scope.criteria.oldS){
					//$scope.tempSearch.stato = "Atto Dirigenziale Esecutivo";
					if($scope.provvisorio){
						$scope.tempSearch.regolamento = 'provvisorio';
					}
				}
			}
			else {
				$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
			}
			if(isRevocaSearch && isRevocaSearch === true){
				$scope.tempSearch.idAttoFilterType = $scope.idAtto;
				if(!$scope.tempSearch.dataAdozione){
					$scope.tempSearch.dataAdozione = "2000-01-01";
				}
				$scope.tempSearch.escludiRevocati = true;
			}
			if ($scope.tipoAtto && $scope.tipoAtto != '' && $scope.tipoAtto != 'sedute') {
				$scope.tempSearch.codiceTipoAtto = $scope.tipoAtto;
			}
			if(!isRevocaSearch && !$scope.tipoAttoObj){
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
					$scope.doSearch();
				});
			}else{
				$scope.doSearch();
			}
		}
	};
	
	$scope.doSearch = function(){
		Atto.search( $scope.tempSearch, $scope.tempSearch , function(result, headers) {
			if($state.current.name == "attoList"){
				localStorageService.set('_attoSearch', $scope.tempSearch);
				localStorageService.set('_attoSearch_tipoRicerca', $scope.tipoRicerca);
				localStorageService.set('_attoSearch_tipoAtto', $scope.tipoAtto);
			}
			$scope.attos = result;

			if($scope.attos && $scope.attos.length) {
	        	for(var i = 0; i< $scope.attos.length; i++){
		        	if($scope.attos[i].numeroAdozione) {
		        		$scope.attos[i].numeroAdozione = $scope.attos[i].numeroAdozione.substr($scope.attos[i].numeroAdozione.length - 5);
		        	}
		    	}
	        }
	        
	        
	        $scope.links = ParseLinks.parse(headers('link'));
	        $scope.totalResultAtti=headers('x-total-count') ;
	        if($scope.criteria.viewtype == 'elencopersedute'){
	        	$scope.sedutas = $scope.getSedutas();
	        }
	        $scope.loading = false;
	    });
	};
	
	$scope.getSedutas = function(){
		 var groupArray = [];
		 var seduta = null;
	    angular.forEach($scope.attos, function (item, idx) {
	    	if(seduta == null){
	    		seduta = item.sedutaGiunta;
	    		groupArray.push(item.sedutaGiunta);
	    	}
	    	else if (item.sedutaGiunta.id != seduta.id) {
	    		seduta = item.sedutaGiunta;
	            groupArray.push(item.sedutaGiunta);
	        }
	    });
	    return groupArray;
	}
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
		var res;
		if(json){
			res = JSON.stringify(json);
		}else{
			res = "{}";
		}
		window.open($rootScope.buildDownloadUrl('api/attos/avcanac') + '&attoCriteriaStr=' + window.encodeURIComponent(res) + '&access_token='+ $rootScope.access_token,'_blank');
	};
	
	$("#ricercaAttoRevocato").on('shown.bs.modal', function(){
		$scope.attos = [];
		$scope.resetRicerca(true);
	});
	
	$scope.cercaAttoRevocato = function(){
		$scope.page = 1;
		$scope.criteria.page = $scope.page;
		$scope.loadAll(true);
	};
	
	$scope.resetRicerca = function(isAttoRevocato){
		$scope.attos = [];
		$scope.page = 1;
		var temp = $scope.criteria.viewtype;
		
//		TODO: i codici per tipo atto non coincidono in Attico
//		if( (ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_DIRIGENZIALI']) && $scope.tipoAtto == 'DIR') ||
//				(ProfiloAccount.isInAnyRole(['ROLE_SUPERVISORE_CONSULTAZIONE_ATTI_GIUNTA']) && ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_CONSULTAZIONE_ATTI_GIUNTA']) && ($scope.tipoAtto  == 'DEL' || $scope.tipoAtto  == 'SDL' || $scope.tipoAtto  == 'COM' || $scope.tipoAtto  == 'sedute')) ){
//			$scope.criteria = {aooId:null, tipiAttoIds:$scope.tipiAttoIds,viewtype:temp} ;
//		}else{
			$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipiAttoIds:$scope.tipiAttoIds,viewtype:temp} ;
//		}
		if($scope.attoRevocato != null){
			delete $scope.criteria.aooId;
		}
		$scope.criteria.page = $scope.page;
		$scope.criteria.per_page = 10;
		$scope.criteria.ordinamento = "codiceCifra";
		$scope.criteria.tipoOrinamento = "desc";
		//$scope.criteria.viewtype = 'tutti';
		$scope.loadAll(isAttoRevocato);
	};


	$scope.loadPage = function(page, isAttoRevocato) {
	    $scope.page = page;
	    $scope.criteria.page =  $scope.page;
	    $scope.loadAll(isAttoRevocato);
	};

	if($scope.tipoAtto != undefined ) $scope.initTipiAttoTab($scope.tipoAtto);
	else $scope.initTipiAttoTab('sedute');

	$scope.showUpdate = function (id) {
	    Atto.get({id: id}, function(result) {
	        $scope.atto = result;
	        $('#saveAttoModal').modal('show');
	    });
	};

	$scope.save = function () {
	    if ($scope.atto.id != null) {
	        Atto.update($scope.atto,
	            function () {
	                $scope.refresh();	
	            });
	    } else {
	        Atto.save($scope.atto,
	            function () {
	                $scope.refresh();
	            });
	    }
	};

	$scope.delete = function (id) {
	    Atto.get({id: id}, function(result) {
	        $scope.atto = result;
	        $('#deleteAttoConfirmation').modal('show');
	    });
	};
	
	$scope.confirmDelete = function (id) {
	    Atto.delete({id: id},
	        function () {
	            $scope.loadAll();
	            $('#deleteAttoConfirmation').modal('hide');
	            $scope.clear();
	        });
	};

	$scope.refresh = function () {
		$log.debug("refresh:",$scope.viewtype);
	    $scope.loadAll();
	    $('#saveAttoModal').modal('hide');
	    $scope.clear();
	};


	$scope.clear = function () {
	    $scope.atto = {sottoscrittori:[] };
	 };
	 
	 /**
	 * Init the tooltip.
	 */
	$scope.initTooltip = function () {
		$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
	};
	
	//Seduta {{atto.sedutaGiunta.tipoSeduta == 1 ? 'Ordinaria':'Straordinaria'}} n. {{atto.sedutaGiunta.numero}} del {{atto.sedutaGiunta.primaConvocazioneInizio  | date:  'dd-MM-yyyy HH:mm':'UTC'}}
	/**
	 * Costruisce la stringa degli estremi seduta.
	 */
	$scope.getEstremiSeduta = function (sedutaGiunta) {
		var tipoSeduta = '';
		if (sedutaGiunta.tipoSeduta == 1)
			tipoSeduta += 'Ordinaria';
		else
			tipoSeduta += 'Straordinaria';
		
		var dataOraSeduta = '';
		if (sedutaGiunta.secondaConvocazioneInizio != null)
			dataOraSeduta = $filter('date')(sedutaGiunta.secondaConvocazioneInizio, 'dd-MM-yyyy HH:mm', 'Europe/Berlin');
		else
			dataOraSeduta = $filter('date')(sedutaGiunta.primaConvocazioneInizio, 'dd-MM-yyyy HH:mm', 'Europe/Berlin');
		
		var retValue = 'Seduta ' + tipoSeduta + ' n. ' + sedutaGiunta.numero + ' del ' + dataOraSeduta;
		
		return retValue;
		 
	};


/*$rootScope.navigatoretitle.push({title: $rootScope.profiloattivo.tipoAtto.descrizione , link:'#/atto'});*/
});
