'use strict';

angular.module('cifra2gestattiApp')
    .controller('SedutaGiuntaController', function ($scope,$rootScope,ModelloHtml,sharedSedutaFactory, SedutaGiuntaConstants,SedutaGiunta, ParseLinks, Profilo,ngToast,$log,$filter) {
        $scope.sedutaGiuntas = [];
        $scope.page = 1;
        $scope.param=[];
        $scope.sedutatype = 'sedutafirma';
        $scope.profilos =[];
        $scope.profilos = Profilo.searchquery({'q':''});
        $scope.tempSearch = {};
        $scope.modelloHtmlsOdg = [];
        $scope.dtoWorkflow= {};
        $scope.dtoWorkflow.campi=[];
        $scope.sedutaConstants = SedutaGiuntaConstants;
        $scope.refreshProfilo = function(search) {
            $log.debug("search:"+search);
            $scope.profilos = Profilo.searchquery({'q':search});
        };

        $scope.isOperatoreOdg = sharedSedutaFactory.isOperatoreOdgGiunta();
        $scope.getOdgBase = function(seduta) {
    		return sharedSedutaFactory.getOdgBase(seduta);
    	}
    	
    	$scope.openSeduta = function(seduta){
    		if(seduta.primaConvocazioneFine){
        		$state.go('sedutaGiuntaConsolidatiDetail', {
					id:seduta.id
				});
        	} else  $state.go('sedutaGiuntaDetail', {
        		id:seduta.id
			});
    	};
    	

        $scope.clearRicerca = function(){
        	$scope.page = 1;
        };

        $scope.loadAll = function() {
        	$log.debug("LOAD ALL");
        	$scope.modelloHtmls = ModelloHtml.query();
        	switch($scope.sedutatype){
        		case 'sedutafirma':
        			$scope.criteria = {type: 'sedutafirma',stato:SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgBase}
        			if(!$scope.isOperatoreOdg)
        				$scope.criteria.profiloId = $rootScope.profiloattivo.id;
        			break;
        		case 'sedutaconsolidata':
        			$scope.criteria = {stato:SedutaGiuntaConstants.statiSeduta.sedutaConsolidata};
        			break;
        		case 'sedutaconclusa':
        			$scope.criteria = {stato:SedutaGiuntaConstants.statiSeduta.sedutaConclusa};
        			break;
        		case 'sedutaannulata':
        			$scope.criteria = {stato:SedutaGiuntaConstants.statiSeduta.sedutaAnnullata};
        			break;
        		case 'sedutavariazione':
        			$scope.criteria = {stato:SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione};
        			break;
        	}


    		$scope.tempSearch = jQuery.extend(true, {}, $scope.criteria);
    		$scope.tempSearch.page = 1;
    		$scope.tempSearch.per_page = 20;

    		SedutaGiunta.search( $scope.tempSearch,  function(result, headers) {
    			$scope.sedutaGiuntas = result;
    	        $log.debug("sedutas:",$scope.sedutaGiuntas);
    	        $scope.links = ParseLinks.parse(headers('link'));
    	        $scope.totalResultSedute=headers('x-total-count') ;
    	    });

//            SedutaGiunta.query({page: $scope.page, per_page: 20}, function(result, headers) {
//                $scope.links = ParseLinks.parse(headers('link'));
//                $scope.sedutaGiuntas = result;
//            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.showUpdate = function (id) {


            SedutaGiunta.get({id: id}, function(result) {
                $scope.sedutaGiunta = result;
                $scope.sedutaGiuntasFilter = SedutaGiunta.riferimentoconsentiti({id: $scope.sedutaGiunta.id});

                $('#saveSedutaGiuntaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.sedutaGiunta.id != null) {
                SedutaGiunta.update($scope.sedutaGiunta,
                    function (result, headers) {
                        $scope.refresh();
                    });
            } else {
                SedutaGiunta.save($scope.sedutaGiunta,
                    function (result, headers) {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            SedutaGiunta.get({id: id}, function(result) {
                $scope.sedutaGiunta = result;
                $('#deleteSedutaGiuntaConfirmation').modal('show');
            });
        };

        /*
         * Le sedute vanno Annullate, non semplicemente eliminate
         *
        $scope.confirmDelete = function (id) {
            SedutaGiunta.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSedutaGiuntaConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        */
        /**
         * annulla seduta giunta
         */
        $scope.annullaSedutaGiunta = function(){
        	if($scope.sedutaGiunta != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
        		$scope.taskLoading = true;
        		$scope.param = {};
        	    $scope.param.sedutaId = $scope.sedutaGiunta.id;
        	    $scope.param.modelloId = $scope.dtoWorkflow.campi['modelloHtmlId'].id;
        		$log.debug("param",$scope.param);

        		SedutaGiunta.annulla($scope.param, function (result, headers) {
        			$log.debug("Annullato");
        			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        			$scope.taskLoading = false;
            		$('#mascheraWorkflow').modal('hide');
            		$scope.loadAll();
                });
        	}
        };

        $scope.salvaDecisione = function(decisioneCorrente) {
			$log.debug(decisioneCorrente);
        	switch(decisioneCorrente.codice){
	        	case 'seduta-annulla':
	        		$scope.annullaSedutaGiunta();
	        		break;
        	}

		};

        $scope.callDecisioneOdg  = function (decisione,seduta) {
			$log.debug("callDecisione",decisione);
			$log.debug("seduta:",seduta);
			$scope.sedutaGiunta = {id: seduta};
			$log.debug("seduta:",$scope.sedutaGiunta);
			var valid = true;

			switch(decisione.codice){
				case 'seduta-annulla':
					$scope.modelloDaCodice('sedutaannulla');
					break;

				default:

					break;
			}


			if(valid){
				if(decisione.mostraMaschera ){
					$scope.decisioneCorrente = decisione;
					$log.debug("Decisione Corrente:",$scope.decisioneCorrente);
					$scope.documentiFirmatiDaCaricare = new Map();
					$('#mascheraWorkflow').modal('show');
				}else{
					$scope.salvaDecisione(decisione);
				}
			}
		};

		/**
		 * Prende i modelli in base al codice
		 */
		$scope.modelloDaCodice = function(codice){
			var modelliList = [];

			for(var i = 0; i <  $scope.modelloHtmls.length; i++){
				if($scope.modelloHtmls[i].tipoDocumento.codice == codice){
					//$log.debug("modelloTrovato",$scope.modelloHtmls[i]);
					modelliList.push($scope.modelloHtmls[i]);
				}
			}
			$scope.modelloHtmlsOdg = modelliList;
			if($scope.modelloHtmlsOdg.length == 1){
				$scope.dtoWorkflow.campi['modelloHtmlId']=$scope.modelloHtmlsOdg[0];
			}
		}

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSedutaGiuntaModal').modal('hide');
            $scope.clear();
        };

        $scope.clearAnnulla = function() {
        	$('#annullaSedutaGiuntaConfirmation').modal('hide');
        };

        $scope.clear = function () {
            $scope.sedutaGiunta = {luogo: null, dataOra: null, stato: null, tipoSeduta: null, id: null, componentiGiunta:[],protocollo: null };
            $scope.sedutaGiuntasFilter = SedutaGiunta.riferimentoconsentiti( );
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
