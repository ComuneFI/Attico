'use strict';

angular.module('cifra2gestattiApp')
    .controller('JobTrasparenzaController', function ($scope, JobTrasparenza, Aoo, ModelloHtml, ParseLinks, Principal, $rootScope) {
        $scope.jobTrasparenzas = [];
        
        $scope.isAdmin = null;
        var admin = [];
	  	admin.push("ROLE_ADMIN");
	  	admin.push("ROLE_AMMINISTRATORE_RP");
	  	if(Principal.isInAnyRole(admin)){
	  		$scope.isAdmin = true;
	  	}else{
	  		$scope.isAdmin = false;
	  	}

		$scope.statoInfos = [
			{
				'stato': 'NEW',
				'label':'In coda',
				'class':'glyphicon glyphicon-log-in',
				'color':'black'
			},
			{
				'stato': 'IN_PROGRESS',
				'label':'Invio/Agg. in corso',
				'class':'glyphicon glyphicon-new-window',
				'color':'orange'
			},
			{
				'stato': 'DONE',
				'label':'Invio/Agg. effettuato',
				'class':'glyphicon glyphicon-ok-circle',
				'color':'cadetblue'
			},
			{
				'stato': 'ERROR',
				'label':'Errore di invio/agg.',
				'class':'glyphicon glyphicon-remove-circle',
				'color':'brown'
			},
			{
				'stato': 'CANCELED',
				'label':'Invio/agg. annullato',
				'class':'glyphicon glyphicon-record',
				'color':'darkgray'
			}
		];
		
	  	 $scope.statiRelata = [
	  	       		{
	  	       			id: 0,
	  	       			denominazione:"Da Generare"
	  	       		},
	  	       		{
	  	       			id: 1,
	  	       			denominazione:"Generata"
	  	       		},
	  	       		{
	  	       			id: 2,
	  	       			denominazione:"Firmata"
	  	       		},
	  	       		{
	  	       			id: 3,
	  	       			denominazione:"Tutti"
	  	       		}
	  	           ];
	  	
	  	 

        if($scope.isAdmin){
	        $scope.aoos = Aoo.getMinimal();
        }else{
        	$scope.aoosFilter = [];
        	$scope.profilosFilter = [];
        	
        	if($rootScope.activeprofilos){
	        	for(var i = 0; i<$rootScope.activeprofilos.length; i++){
	        		$scope.aoosFilter.push($rootScope.activeprofilos[i].aoo.id);
	        		$scope.profilosFilter.push($rootScope.activeprofilos[i].id);
	        	}
        	}
        	if($scope.aoosFilter.length == 0){
        		$scope.aoosFilter.push(0);
        	}
        	if($scope.profilosFilter.length == 0){
        		$scope.profilosFilter.push(0);
        	}
        }
        
        ModelloHtml.query({tipoDocumento:'report_ricerca'}, function(result) {
	        $scope.modelloHtmls = result;
	    });
        
        $scope.page = 1;
        $scope.jobTrasparenzaSearch = {};
        $scope.tempSearch = {};
        $scope.tempSearch.aoosFilter = $scope.aoosFilter;
        $scope.tempSearch.profilosFilter = $scope.profilosFilter;
        $scope.jobTrasparenzaSearch.aoosFilter = $scope.aoosFilter;
        $scope.jobTrasparenzaSearch.profilosFilter = $scope.profilosFilter;
        
        /*
        $scope.tempSearch.statoRelata = $scope.statiRelata[3];
        */
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.jobTrasparenzaSearch = {};
        	$scope.tempSearch = {};
        	$scope.tempSearch.errori = "";
            $scope.tempSearch.aoosFilter = $scope.aoosFilter;
            $scope.jobTrasparenzaSearch.aoosFilter = $scope.aoosFilter;
            $scope.tempSearch.profilosFilter = $scope.profilosFilter;
            $scope.jobTrasparenzaSearch.profilosFilter = $scope.profilosFilter;
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in jobTrasparenzaSearch*/
        	$scope.jobTrasparenzaSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	
        	/*var statoRelata = 3;*/

    		if(searchObject!=undefined && searchObject!=null) {
    			/*
    			if(searchObject.statoRelata){
        			statoRelata = searchObject.statoRelata.id;
        		}
        		*/
    			var obj = {
						esito:searchObject.esito,
    					codiceCifra:searchObject.codiceCifra,
    					numeroAdozione:searchObject.numeroAdozione,
    					dataAdozioneStart:searchObject.dataAdozioneStart, 
    					dataAdozioneEnd: searchObject.dataAdozioneEnd, 
    					oggetto: searchObject.oggetto, 
    					statoPubblicazione: searchObject.statoPubblicazione, 
    					statoProceduraPubblicazione: searchObject.statoProceduraPubblicazione, 
    					errori: searchObject.errori, 
						dataInvioStart:searchObject.dataInvioStart, 
    					dataInvioEnd: searchObject.dataInvioEnd, 
    					dataInizioPubblicazioneStart:searchObject.dataInizioPubblicazioneStart, 
    					dataInizioPubblicazioneEnd: searchObject.dataInizioPubblicazioneEnd, 
    					dataFinePubblicazioneStart:searchObject.dataFinePubblicazioneStart, 
    					dataFinePubblicazioneEnd: searchObject.dataFinePubblicazioneEnd
    			};
    			/*,statoRelata};*/
    			//var obj = {jobTrasparenza:searchObject.jobTrasparenza, aoo: searchObject.aoo};
    			if(searchObject.aoosFilter && searchObject.aoosFilter.length > 0){
    				obj.aoos = searchObject.aoosFilter;
    			}
    			if(searchObject.profilosFilter && searchObject.profilosFilter.length > 0){
    				obj.profiloss = searchObject.profilosFilter;
    			}
    			JobTrasparenza.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.jobTrasparenzas = result;
					$scope.jobTrasparenzas.map( (t) => Object.assign(t, {info: $scope.statoInfos.filter((si) => si.stato == t.stato)[0]}));
	            });
    		}
    		else{
    			/*
    			if($scope.tempSearch && $scope.tempSearch.statoRelata){
    				statoRelata = $scope.tempSearch.statoRelata.id;
    			}
    			*/
    			
    			var obj = {
						esito:$scope.jobTrasparenzaSearch.esito,
    					codiceCifra:$scope.jobTrasparenzaSearch.codiceCifra, 
    					numeroAdozione:$scope.jobTrasparenzaSearch.numeroAdozione, 
    					dataAdozioneStart:$scope.jobTrasparenzaSearch.dataAdozioneStart, 
    					dataAdozioneEnd:$scope.jobTrasparenzaSearch.dataAdozioneEnd, 
    					oggetto:$scope.jobTrasparenzaSearch.oggetto, 
    					statoPubblicazione:$scope.jobTrasparenzaSearch.statoPubblicazione, 
    					statoProceduraPubblicazione:$scope.jobTrasparenzaSearch.statoProceduraPubblicazione,
    					errori:$scope.jobTrasparenzaSearch.errori, 
						dataInvioStart:$scope.jobTrasparenzaSearch.dataInvioStart, 
    					dataInvioEnd:$scope.jobTrasparenzaSearch.dataInvioEnd, 
    					dataInizioPubblicazioneStart:$scope.jobTrasparenzaSearch.dataInizioPubblicazioneStart, 
    					dataInizioPubblicazioneEnd:$scope.jobTrasparenzaSearch.dataInizioPubblicazioneEnd, 
    					dataFinePubblicazioneStart:$scope.jobTrasparenzaSearch.dataFinePubblicazioneStart, 
    					dataFinePubblicazioneEnd:$scope.jobTrasparenzaSearch.dataFinePubblicazioneEnd
    			};
    			/*,statoRelata:$scope.jobTrasparenzaSearch.statoRelata};*/
    			if($scope.jobTrasparenzaSearch.aoosFilter && $scope.jobTrasparenzaSearch.aoosFilter.length > 0){
    				obj.aoos = $scope.jobTrasparenzaSearch.aoosFilter;
    			}
    			if($scope.jobTrasparenzaSearch.profilosFilter && $scope.jobTrasparenzaSearch.profilosFilter.length > 0){
    				obj.profiloss = $scope.jobTrasparenzaSearch.profilosFilter;
    			}
    			JobTrasparenza.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.jobTrasparenzas = result;
					$scope.jobTrasparenzas.map( (t) => Object.assign(t, {info: $scope.statoInfos.filter((si) => si.stato == t.stato)[0]}));
                });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	JobTrasparenza.get({id: id}, function(result) {
                $scope.jobTrasparenza = result;
                $('#saveJobTrasparenzaModal').modal('show');
            });
        };
        
        $scope.show = function (idAtto) {
			window.location = "#/atto/" + idAtto + "/aggiornaPubblicazione/"; 
        };

        $scope.save = function () {
        	if ($scope.jobTrasparenza.aoo != null && $scope.jobTrasparenza.aoo.id == null) {
        		$scope.jobTrasparenza.aoo = null;
        	}
            if ($scope.jobTrasparenza.id != null && $scope.jobTrasparenza.atto.id != null) {
            	JobTrasparenza.updateDatiAnnullamento($scope.jobTrasparenza,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
        	JobTrasparenza.get({id: id}, function(result) {
                $scope.jobTrasparenza = result;
                $('#deleteJobTrasparenzaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	JobTrasparenza.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteJobTrasparenzaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveJobTrasparenzaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.jobTrasparenza = {jobTrasparenza: null, id: null, aoo:{id:null}};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.showUpdate = function (id) {
        	JobTrasparenza.get({id: id}, function(result) {
                $scope.jobTrasparenza = result;
                $('#saveJobTrasparenzaModal').modal('show');
            });
        };
        
        $scope.esportaRicercaPdf = function(){
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
    		// $scope.tempSearch.statoRelata = {};
    		$scope.tempSearch.profiloId = $rootScope.profiloattivo.id;
    		$scope.tempSearch.aoos = $scope.tempSearch.aoosFilter;
    		$scope.tempSearch.profilos = $scope.tempSearch.profilosFilter;
    		$scope.tempSearch.viewtype = 'jobTrasparenzas';
    		var json = jQuery.extend(true, {}, $scope.tempSearch);
    		json.colnames = colNames;
    		var res;
    		if(json){
    			res = JSON.stringify(json);
    			res = res.split("/").join("slash");
    		}else{
    			res = "{}";
    		}
    		window.open('api/jobTrasparenzas/reportpdf/'+$scope.modelloHtmls[0].id+'/' + window.encodeURIComponent(res) + '/endSearch?access_token='+ $rootScope.access_token,'_blank');
    	};
    });
