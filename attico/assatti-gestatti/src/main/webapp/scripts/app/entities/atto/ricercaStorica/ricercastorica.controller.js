'use strict';

angular.module('cifra2gestattiApp')
.controller('RicercaStoricaController', function ($scope, $log, $rootScope, $stateParams, $state, $window,
		StoricoAtto, Profilo,TipoAtto, Aoo, ParseLinks , ModelloHtml) {
	
	$log.debug("tipoRicercaStorica=", $scope.tipoRicercaStorica);
	
	$scope.tipoRicercaStorica = $stateParams.tipo;
	$scope.loading = false;
	$scope.tipiEsitiVerifica = ['Atto Esecutivo', 'Atto Non Esecutivo']
	$scope.storicoAttos = [];
	$scope.resultPageSize = 10;
	$scope.resultChildPageSize = 5;
    $scope.storicoPage = 1;
    $scope.storicoPageArray = [];
    $scope.tipiRegolamento= ["SI", "NO"];
    
    $scope.tipiAttoStoricoCodici = [];
    $scope.initTipiAttoStoricoTab = function(){
    	var tipiAttoCodici = [];
        if(angular.isDefined($rootScope.profiloattivo) && $rootScope.profiloattivo.id != '0' && $rootScope.profiloattivo.id != 0){
    		for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
    			tipiAttoCodici.push($rootScope.profiloattivo.tipiAtto[i].codice);
    		}
    		tipiAttoCodici.push("RFT");
    		tipiAttoCodici.push("DELFS");
        }
        $scope.tipiAttoStoricoCodici = tipiAttoCodici;
    };

    $scope.initTipiAttoStoricoTab();
    
    if(!angular.isDefined($scope.storicoAnnoList)){
    	$scope.storicoAnnoList = [];
    }

    if(angular.isDefined($rootScope.criteriaStorico) && $rootScope.criteriaStorico != null){
    	$scope.criteriaStorico = $rootScope.criteriaStorico;
    	delete $rootScope.criteriaStorico;
    	$rootScope.criteriPresiDaRoot = true;

    	if(angular.isDefined($scope.tipoRicercaStorica)){
    		if ($scope.tipoRicercaStorica == 'atti-dirigenziali'){
    			$scope.minAnno = '2004';
    			$scope.maxAnno = '2010';
    		} else {
    			$scope.minAnno = '1996';
    			$scope.maxAnno = '2016';
    		}

    		if ($scope.storicoAnnoList.length == 0){
    			for(var aa=$scope.maxAnno; aa >= $scope.minAnno; aa--){
    				$scope.storicoAnnoList.push(aa);
    			}
    		}
    	}
    } else if(!angular.isDefined($rootScope.criteriPresiDaRoot) || $rootScope.criteriPresiDaRoot == false ){
    	$scope.criteriaStorico = {tipiAttoCodici:$scope.tipiAttoStoricoCodici};
    	$scope.criteriaStorico.page = $scope.storicoPage;
    	$scope.criteriaStorico.per_page = $scope.resultPageSize;
    	$scope.criteriaStorico.ordinamento = "codiceCifra";
    	$scope.criteriaStorico.tipoOrdinamento = "desc";
    	$scope.criteriaStorico.viewtype = 'tutti';

    	if(angular.isDefined($scope.tipoRicercaStorica)){
    		if ($scope.tipoRicercaStorica == 'atti-dirigenziali'){
    			$scope.minAnno = '2004';
    			$scope.maxAnno = '2010';
    		} else {
    			$scope.minAnno = '1996';
    			$scope.maxAnno = '2016';
    		}
    		$scope.criteriaStorico.anno = $scope.maxAnno;

    		if ($scope.storicoAnnoList.length == 0){
    			for(var aa=$scope.maxAnno; aa >= $scope.minAnno; aa--){
    				$scope.storicoAnnoList.push(aa);
    			}
    		}
    	}
    }

    if(!angular.isDefined($scope.tipiIter)){
		StoricoAtto.tipiIter(function(collection){
			var output = [], keys = [];
			angular.forEach(collection, function(item) {
				var key = item;
				if(keys.indexOf(key) === -1) {
					keys.push(key);
				}
			});
			$scope.tipiIter = keys;
		});
	}
    
    if(!angular.isDefined($scope.tipiFinanziamento)){
		StoricoAtto.tipiFinanziamento(function(collection){
			var output = [], keys = [];
			angular.forEach(collection, function(item) {
				var key = item;
				if(keys.indexOf(key) === -1) {
					keys.push(key);
				}
			});
			$scope.tipiFinanziamento = keys;
		});
	}
	
	if(!angular.isDefined($scope.tipiAdem)){
		StoricoAtto.tipiAdem(function(collection){
			var output = [], keys = [];
			angular.forEach(collection, function(item) {
				var key = item;
				if(keys.indexOf(key) === -1) {
					keys.push(key);
				}
			});
			$scope.tipiAdem = keys;
		});
	}

	if(!angular.isDefined($scope.tipiRiunione)){
		StoricoAtto.tipiRiunioneGiunta(function(collection){
			var output = [], keys = [];
			angular.forEach(collection, function(item) {
				var key = item;
				if(keys.indexOf(key) === -1) {
					keys.push(key);
				}
			});
			$scope.tipiRiunione = keys;
		});
	}
	
	if(!angular.isDefined($scope.tipiAtto)){
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
	}
	if(!angular.isDefined($scope.listaaoo)){
		Aoo.padri(function(collection){
			$log.debug("AOOS:",collection);

			$scope.listaaoo = collection;
		});
	}
	
	$scope.ordinaStorico = function(ordinamento){
		if($scope.criteriaStorico.ordinamento == ordinamento){
			if($scope.criteriaStorico.tipoOrdinamento == "asc"){
				$scope.criteriaStorico.tipoOrdinamento = "desc";
			}else{
				$scope.criteriaStorico.tipoOrdinamento = "asc";
			}
		}else{	
			$scope.criteriaStorico.tipoOrdinamento = "asc"; 
		}
		$scope.criteriaStorico.ordinamento = ordinamento;
		$scope.loadAllStorico();
	};
	
	$scope.initStoricoPageArray = function() {
		$scope.storicoPageArray = [];
		$scope.linkAttos = [];
		$scope.totalResultAttos = [];
		for(var i=1; i < $scope.resultPageSize; i++){
			$scope.storicoPageArray.push(1);
			$scope.totalResultAttos.push(0);
		}
	};
	
	/**
	 * Caricamento degli Atti dallo Storico Atti Dirigenziali
	 */
	$scope.loadAllAttiDirigenziali = function() {
		$scope.storicoAttos = [];
		$scope.attosGroupByAoo = [];
		$scope.loading = true;
		if ($scope.criteriaStorico.viewtype !='tutti'){
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaStorico);
			StoricoAtto.searchAttiDirigenziali( $scope.tempSearch, $scope.tempSearch , function(result, headers) {
				$scope.storicoAttos = result;
				$scope.loading = false;
				$log.debug("storicoatto.searchAttiDirigenziali:",$scope.storicoAttos);
				$scope.links = ParseLinks.parse(headers('link'));
				$scope.totalResultAtti=headers('x-total-count') ;
			});
		} else {
			var params = {codTipoAtto:'DIR',
					  anno:$scope.criteriaStorico.anno,
					  page:$scope.storicoPage, 
				      per_page:$scope.resultPageSize};
			
			StoricoAtto.getAllAoo(params, function(result, headers) {
				$scope.attosGroupByAoo = result;
				$scope.loading = false;
				$log.debug("StoricoAtto.getAllAoo:",$scope.attosGroupByAoo);
				$scope.linkAoos = ParseLinks.parse(headers('link'));
				$scope.totalResultAoo=headers('x-total-count') ;
				$scope.initStoricoPageArray();
			});
		}
	};
	
	/**
	 * Caricamento degli Atti dallo Storico Atti di Giunta
	 */
	$scope.loadAllAttiGiunta = function() {
		$scope.storicoAttos = [];
		$scope.attosGroupByAoo = [];
		$scope.attosGroupBySeduta = [];
		$scope.loading = true;
		if ($scope.criteriaStorico.viewtype =='tutti'){
			var params = {codTipoAtto:'DEL',
 						  anno:$scope.criteriaStorico.anno,
 						  page:$scope.storicoPage, 
					      per_page:$scope.resultPageSize};
			
			StoricoAtto.getAllAoo(params, function(result, headers) {
				$scope.attosGroupByAoo = result;
				$scope.loading = false;
				$log.debug("StoricoAtto.getAllAoo:",$scope.attosGroupByAoo);
				$scope.linkAoos = ParseLinks.parse(headers('link'));
				$scope.totalResultAoo=headers('x-total-count') ;
				$scope.initStoricoPageArray();
			});
		} else if ($scope.criteriaStorico.viewtype =='sedute_giunta'){
			var params = {codTipiAtto:$scope.tipiAttoStoricoCodici, 
					      anno:$scope.criteriaStorico.anno, 
					      page:$scope.storicoPage, 
					      per_page:$scope.resultChildPageSize};
			
			StoricoAtto.getAllSedute(params, function(result, headers) {
				$scope.attosGroupBySeduta = result;
				$scope.loading = false;
				$log.debug("StoricoAtto.getAllSedute:",$scope.attosGroupBySeduta);
				$scope.linksSedute = ParseLinks.parse(headers('link'));
				$scope.totalResultSedute=headers('x-total-count');
				$scope.initStoricoPageArray();
			});
		}else {
			$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaStorico);
			StoricoAtto.searchAttiGiunta( $scope.tempSearch, $scope.tempSearch , function(result, headers) {
				$scope.storicoAttos = result;
				$scope.loading = false;
				$log.debug("storicoatto.searchAttiGiunta:",$scope.storicoAttos);
				$scope.links = ParseLinks.parse(headers('link'));
				$scope.totalResultAtti=headers('x-total-count') ;
			});
		} 
	};

	$scope.loadAllStorico = function() {
		$log.info("loadAllStorico:viewType=",$scope.criteriaStorico.viewtype);
		$log.debug("tipoRicercaStorica=",$scope.tipoRicercaStorica);
		$log.debug("criteriaStorico",$scope.criteriaStorico);
		
		$scope.initTipiAttoStoricoTab();
		$scope.criteriaStorico.tipiAttoCodici = $scope.tipiAttoStoricoCodici;
		
		if(angular.isUndefined($scope.criteriaStorico.anno) || $scope.criteriaStorico.anno == null || $scope.criteriaStorico.anno ==''){
			$scope.criteriaStorico.anno = $scope.maxAnno;
		}
		
		if ($scope.tipoRicercaStorica == 'atti-dirigenziali'){
			$scope.loadAllAttiDirigenziali();
		} else if ($scope.tipoRicercaStorica == 'atti-giunta'){
			$scope.loadAllAttiGiunta();
		}
	};
	
	
	$scope.resetRicercaStorico = function(){
		$scope.storicoAttos = [];
		$scope.storicoPage = 1;
		var temp = $scope.criteriaStorico.viewtype;
		$scope.criteriaStorico = {tipiAttoCodici:$scope.tipiAttoCodici,viewtype:temp,anno:$scope.criteriaStorico.anno};
		$scope.criteriaStorico.page = $scope.storicoPage;
		$scope.criteriaStorico.per_page = $scope.resultPageSize;
		$scope.criteriaStorico.ordinamento = "codiceCifra";
		$scope.criteriaStorico.tipoOrdinamento = "desc";
		$scope.loadAllStorico();
	};


	$scope.loadPageStorico = function(page) {
	    $scope.storicoPage = page;
	    $scope.criteriaStorico.page =  $scope.storicoPage;
	    $scope.loadAllStorico();
	};

	$scope.loadAllStorico();

	$scope.refresh = function () {
		$log.debug("refresh:",$scope.criteriaStorico.viewtype);
	    $scope.loadAllStorico();
	};
	
	/**
	 * Gestione del caricamento delle pagine di Atti Dirigenziali in una data Aoo..
	 */
	$scope.loadChildPageAttiDir = function(aoo, page) {
	    $scope.storicoPageArray[aoo.id] = page;
	    $scope.searchChildPageAttiDir(aoo);
	};
	$scope.searchChildPageAttiDir = function(aoo,elm,callback) {
		$scope.criteriaStorico = {anno:$scope.criteriaStorico.anno, viewtype:"tutti"} ;
		$scope.criteriaStorico.tipiAttoCodici = $scope.tipiAttoStoricoCodici;
		$scope.criteriaStorico.aooCodice = aoo.codice;
		$scope.criteriaStorico.aooDescr = aoo.descrizione;
		$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaStorico);
		
		aoo.loading = true;
		StoricoAtto.searchAttiDir_groupByAoo(
				{
					page:  $scope.storicoPageArray[aoo.id],
					per_page: $scope.resultChildPageSize				 
				},
				$scope.tempSearch,
				function(result, headers) {
			aoo.storicoAttiDir = result;
			$log.debug("carica caricaStoricoAttiDir groupByAoo - aoo:",aoo);
			$scope.linkAttos[aoo.id] = ParseLinks.parse(headers('link'));
			$scope.totalResultAttos[aoo.id]=headers('x-total-count') ;
			
			aoo.loading = false;
			if(angular.isDefined(callback)){
				callback(elm);
			}
		});
	}
	$scope.caricaStoricoAttiDir = function(aoo,elm,callback){
		if(aoo.storicoAttiDir && aoo.storicoAttiDir.length > 0){
			callback(elm);
			aoo.storicoAttiDir = [];
			aoo.loading = false;
			$scope.totalResultAttos[aoo.id]=0;
		} else {
			$scope.searchChildPageAttiDir(aoo,elm,callback);
		}
	};
	
	/**
	 * Gestione del caricamento delle pagine di Atti di Giunta in una data Aoo..
	 */
	$scope.loadChildPageAttiGiuntaByAoo = function(aoo, page) {
	    $scope.storicoPageArray[aoo.id] = page;
	    $scope.searchChildPageAttiGiuntaByAoo(aoo);
	};
	$scope.searchChildPageAttiGiuntaByAoo = function(aoo,elm,callback) {
		$scope.criteriaStorico = {anno:$scope.criteriaStorico.anno, viewtype:"tutti"} ;
		$scope.criteriaStorico.tipiAttoCodici = $scope.tipiAttoStoricoCodici;
		$scope.criteriaStorico.aooCodice = aoo.codice;
		$scope.criteriaStorico.aooDescr = aoo.descrizione;
		$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaStorico);
		
		aoo.loading = true;
		StoricoAtto.searchAttiGiunta_groupByAoo( 
				{
					page:  $scope.storicoPageArray[aoo.id],
					per_page: $scope.resultChildPageSize				 
				},
				$scope.tempSearch, 
				function(result, headers) {
			aoo.storicoAttiGiunta = result;
			$log.debug("carica StoricoAttiGiunta groupByAoo - aoo:",aoo);
			$scope.linkAttos[aoo.id] = ParseLinks.parse(headers('link'));
			$scope.totalResultAttos[aoo.id]=headers('x-total-count') ;
			
			aoo.loading = false;
			if(angular.isDefined(callback)){
				callback(elm);
			}
		});
	}
	$scope.caricaStoricoAttiGiuntaByAoo = function(aoo,elm,callback){
		
		if(aoo.storicoAttiGiunta && aoo.storicoAttiGiunta.length > 0){
			callback(elm);
			aoo.storicoAttiGiunta = [];
			aoo.loading = false;
			$scope.totalResultAttos[aoo.id]=0;
		} else {
			$scope.searchChildPageAttiGiuntaByAoo(aoo,elm,callback);
		}
	};
	
	/**
	 * Gestione del caricamento delle pagine di Atti di Giunta in una data Seduta..
	 */
	$scope.loadChildPageAttiGiuntaBySeduta = function(seduta, page) {
	    $scope.storicoPageArray[seduta.id] = page;
	    $scope.searchChildPageAttiGiuntaBySeduta(seduta);
	};
	$scope.searchChildPageAttiGiuntaBySeduta = function(seduta,elm,callback) {
		$scope.criteriaStorico = {anno:$scope.criteriaStorico.anno, viewtype:"sedute_giunta"} ;
		$scope.criteriaStorico.tipiAttoCodici = $scope.tipiAttoStoricoCodici;
		$scope.criteriaStorico.dataRiunioneGiunta = seduta.dataRiunioneDB;
		$scope.criteriaStorico.dataRiunioneGiuntaA = seduta.dataRiunioneDB;
		$scope.criteriaStorico.oraRiunioneGiunta = seduta.oraRiunione;
		$scope.criteriaStorico.tipoRiunioneGiunta = seduta.tipoRiunione;
		$scope.criteriaStorico.numRiunioneGiunta = seduta.numRiunione;
		
		$scope.tempSearch = jQuery.extend(true, {}, $scope.criteriaStorico);
		
		seduta.loading = true;
		StoricoAtto.searchAttiGiunta_groupBySeduta( 
				{
					page:  $scope.storicoPageArray[seduta.id],
					per_page: $scope.resultChildPageSize				 
				},
				$scope.tempSearch,
				function(result, headers) {
			seduta.attiGiunta = result;
			$log.debug("carica storicoAttiGiunta groupBySeduta - seduta:",seduta);
			$scope.linkAttos[seduta.id] = ParseLinks.parse(headers('link'));
			$scope.totalResultAttos[seduta.id]=headers('x-total-count') ;
			
			seduta.loading = false;
			if(angular.isDefined(callback)){
				callback(elm);
			}
		});
	}
	$scope.caricaStoricoAttiGiuntaBySeduta = function(seduta,elm,callback){
		if(seduta.attiGiunta && seduta.attiGiunta.length > 0){
			callback(elm);
			seduta.attiGiunta = [];
			seduta.loading = false;
			$scope.totalResultAttos[seduta.id]=0;
		} else {
			$scope.searchChildPageAttiGiuntaBySeduta(seduta,elm,callback);
		}
	};
	
	$scope.caricaLavorazioniAttoGiunta = function(atto,elm,callback){
		if(atto.listaLavorazioni && atto.listaLavorazioni.length > 0){
			callback(elm);
			atto.listaLavorazioni = [];
			atto.loading = false;
		} else {
			atto.loading = true;
			var params = { codiceCifra: atto.codiceCifra };
			
			StoricoAtto.getLavorazioniAttoGiunta(params, function(result, headers) {
				atto.listaLavorazioni = result;
				$log.debug("caricaLavorazioniAttoGiunta - atto:",atto);
				atto.loading = false;
				callback(elm);
			});
		}
	};
	
	$scope.caricaLavorazioniAttoDirigenziale = function(atto,elm,callback){
		if(atto.listaLavorazioni && atto.listaLavorazioni.length > 0){
			callback(elm);
			atto.listaLavorazioni = [];
			atto.loading = false;
		} else {
			atto.loading = true;
			var params = { codiceCifra: atto.codiceCifra };
			
			StoricoAtto.getLavorazioniAttoDirigenziale(params, function(result, headers) {
				atto.listaLavorazioni = result;
				$log.debug("caricaLavorazioniAttoDirigenziale - atto:",atto);
				atto.loading = false;
				callback(elm);
			});
		}
	};
	

	 /**
	 * Init the tooltip.
	 */
	$scope.initTooltip = function () {
		$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
	};
	
	$scope.storicoAttoDetail = function(attoId){
		$log.debug("storicoAttoGiuntaDetail - attoId:",attoId);
		// Salvo i filtri di ricerca nel rootScope...
		$rootScope.criteriaStorico = $scope.criteriaStorico;
		$rootScope.criteriPresiDaRoot = false
		
		var detailUrl = $window.location.href + '/' + attoId;
		$window.location.href = detailUrl;
	}
	
	$scope.$on("$destroy", function handler() {
		$log.debug("chiamato DESTROY...");
		if(!angular.isDefined($rootScope.criteriaStorico) || $rootScope.criteriaStorico == null){
			delete $rootScope.criteriaStorico;
			$rootScope.criteriPresiDaRoot = false
		}
    });

});
