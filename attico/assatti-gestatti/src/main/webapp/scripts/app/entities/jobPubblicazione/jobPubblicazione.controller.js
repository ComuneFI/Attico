'use strict';

angular.module('cifra2gestattiApp')
    .controller('JobPubblicazioneController', function ($scope, JobPubblicazione, Aoo, ModelloHtml, ParseLinks, Principal, $rootScope) {
        $scope.jobPubblicaziones = [];
        
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
	  	
        ModelloHtml.query({tipoDocumento:'report_ricerca'}, function(result) {
	        $scope.modelloHtmls = result;
	    });
        
        $scope.page = 1;
        $scope.jobPubblicazioneSearch = {};
        $scope.tempSearch = {};
        $scope.tempSearch.aoosFilter = $scope.aoosFilter;
        $scope.tempSearch.profilosFilter = $scope.profilosFilter;
        $scope.jobPubblicazioneSearch.aoosFilter = $scope.aoosFilter;
        $scope.jobPubblicazioneSearch.profilosFilter = $scope.profilosFilter;
        
        /*$scope.tempSearch.statoRelata = $scope.statiRelata[3];*/
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.jobPubblicazioneSearch = {};
        	$scope.tempSearch = {};
        	/*$scope.tempSearch.errori = "";*/
            $scope.tempSearch.aoosFilter = $scope.aoosFilter;
            $scope.jobPubblicazioneSearch.aoosFilter = $scope.aoosFilter;
            $scope.tempSearch.profilosFilter = $scope.profilosFilter;
            $scope.jobPubblicazioneSearch.profilosFilter = $scope.profilosFilter;
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in jobPubblicazioneSearch*/
        	$scope.jobPubblicazioneSearch = jQuery.extend(true, {}, $scope.tempSearch);
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
    					codiceCifra:searchObject.codiceCifra, 
    					numeroAdozione:searchObject.numeroAdozione,
    					dataAdozioneStart:searchObject.dataAdozioneStart, 
    					dataAdozioneEnd: searchObject.dataAdozioneEnd, 
    					oggetto: searchObject.oggetto, 
    					statoPubblicazione: searchObject.statoPubblicazione, 
    					statoProceduraPubblicazione: searchObject.statoProceduraPubblicazione, 
    					errori: searchObject.errori, 
    					dataInizioPubblicazioneStart:searchObject.dataInizioPubblicazioneStart, 
    					dataInizioPubblicazioneEnd: searchObject.dataInizioPubblicazioneEnd, 
    					dataFinePubblicazioneStart:searchObject.dataFinePubblicazioneStart, 
    					dataFinePubblicazioneEnd: searchObject.dataFinePubblicazioneEnd
    			};
    			/*,statoRelata};*/
    			//var obj = {jobPubblicazione:searchObject.jobPubblicazione, aoo: searchObject.aoo};
    			if(searchObject.aoosFilter && searchObject.aoosFilter.length > 0){
    				obj.aoos = searchObject.aoosFilter;
    			}
    			if(searchObject.profilosFilter && searchObject.profilosFilter.length > 0){
    				obj.profiloss = searchObject.profilosFilter;
    			}
    			JobPubblicazione.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.jobPubblicaziones = result;
	            });
    		}
    		else{
    			/*
    			if($scope.tempSearch && $scope.tempSearch.statoRelata){
    				statoRelata = $scope.tempSearch.statoRelata.id;
    			}
    			*/
    			
    			var obj = {
    					codiceCifra:$scope.jobPubblicazioneSearch.codiceCifra,
    					numeroAdozione:$scope.jobPubblicazioneSearch.numeroAdozione, 
    					dataAdozioneStart:$scope.jobPubblicazioneSearch.dataAdozioneStart, 
    					dataAdozioneEnd:$scope.jobPubblicazioneSearch.dataAdozioneEnd, 
    					oggetto:$scope.jobPubblicazioneSearch.oggetto, 
    					statoPubblicazione:$scope.jobPubblicazioneSearch.statoPubblicazione, 
    					statoProceduraPubblicazione:$scope.jobPubblicazioneSearch.statoProceduraPubblicazione,
    					errori:$scope.jobPubblicazioneSearch.errori, 
    					dataInizioPubblicazioneStart:$scope.jobPubblicazioneSearch.dataInizioPubblicazioneStart, 
    					dataInizioPubblicazioneEnd:$scope.jobPubblicazioneSearch.dataInizioPubblicazioneEnd, 
    					dataFinePubblicazioneStart:$scope.jobPubblicazioneSearch.dataFinePubblicazioneStart, 
    					dataFinePubblicazioneEnd:$scope.jobPubblicazioneSearch.dataFinePubblicazioneEnd
    			};
    			/*,statoRelata:$scope.jobPubblicazioneSearch.statoRelata};*/
    			if($scope.jobPubblicazioneSearch.aoosFilter && $scope.jobPubblicazioneSearch.aoosFilter.length > 0){
    				obj.aoos = $scope.jobPubblicazioneSearch.aoosFilter;
    			}
    			if($scope.jobPubblicazioneSearch.profilosFilter && $scope.jobPubblicazioneSearch.profilosFilter.length > 0){
    				obj.profiloss = $scope.jobPubblicazioneSearch.profilosFilter;
    			}
    			JobPubblicazione.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.jobPubblicaziones = result;
                });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	JobPubblicazione.get({id: id}, function(result) {
                $scope.jobPubblicazione = result;
                $('#saveJobPubblicazioneModal').modal('show');
            });
        };

        $scope.save = function () {
        	if ($scope.jobPubblicazione.aoo != null && $scope.jobPubblicazione.aoo.id == null) {
        		$scope.jobPubblicazione.aoo = null;
        	}
            if ($scope.jobPubblicazione.id != null && $scope.jobPubblicazione.atto.id != null) {
            	JobPubblicazione.updateDatiAnnullamento($scope.jobPubblicazione,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
        	JobPubblicazione.get({id: id}, function(result) {
                $scope.jobPubblicazione = result;
                $('#deleteJobPubblicazioneConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	JobPubblicazione.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteJobPubblicazioneConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveJobPubblicazioneModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.jobPubblicazione = {jobPubblicazione: null, id: null, aoo:{id:null}};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.showUpdate = function (id) {
        	JobPubblicazione.get({id: id}, function(result) {
                $scope.jobPubblicazione = result;
                $('#saveJobPubblicazioneModal').modal('show');
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
    		$scope.tempSearch.viewtype = 'jobPubblicaziones';
    		var json = jQuery.extend(true, {}, $scope.tempSearch);
    		json.colnames = colNames;
    		var res;
    		if(json){
    			res = JSON.stringify(json);
    			res = res.split("/").join("slash");
    		}else{
    			res = "{}";
    		}
    		
    		window.open($rootScope.buildDownloadUrl('api/jobPubblicaziones/reportpdf/'+$scope.modelloHtmls[0].id+'/' + window.encodeURIComponent(res) + '/endSearch') + '&access_token='+ $rootScope.access_token,'_blank');
    	};
    });
