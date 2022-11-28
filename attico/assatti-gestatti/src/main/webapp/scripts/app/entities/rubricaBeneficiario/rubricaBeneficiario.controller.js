'use strict';

angular.module('cifra2gestattiApp')
    .controller('RubricaBeneficiarioController', function ($scope, RubricaBeneficiario, Principal, Aoo, ParseLinks, $filter, $rootScope) {
    	
        $scope.rubricaBeneficiarios = [];
        $scope.beneficiariSearch = {};
        $scope.tempSearch = {};
        $scope.rubricaBeneficiario = {};
        $scope.page = 1;
        $scope.aoos = [];
        
        $scope.soloAttivi = false;
        
        $scope.stati = [
                		{
                			id: 0,
                			denominazione: "cifra2gestattiApp.rubricaBeneficiario.filtro.stato.attivi"
                		},
                		{
                			id: 1,
                			denominazione: "cifra2gestattiApp.rubricaBeneficiario.filtro.stato.disattivati"
                		},
                		{
                			id: 2,
                			denominazione: "cifra2gestattiApp.rubricaBeneficiario.filtro.stato.entrambi"
                		}
                    ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in beneficiariSearch*/
        	$scope.beneficiariSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		RubricaBeneficiario.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.rubricaBeneficiarios = result;
	            });
        	}else{
        		RubricaBeneficiario.search({page: $scope.page, per_page: 10}, $scope.beneficiariSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.rubricaBeneficiarios = result;
	            });
        	}
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.beneficiariSearch = {};
        	$scope.tempSearch = {};
        	if(!$scope.isAdmin){
    	  		$scope.tempSearch.aoo = $rootScope.profiloattivo.aoo;
    	  	}
        	if(!$scope.soloAttivi){
        		$scope.tempSearch.stato = $scope.stati[2];
        	}else{
        		$scope.tempSearch.stato = $scope.stati[0];
        	}
        	$scope.ricerca();
        };
        
        $scope.onSelected = function(){
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.isNotValid = function(){
        	var res = false;
        	if(angular.isDefined($scope.rubricaBeneficiario.tipoSoggetto) && $scope.rubricaBeneficiario.tipoSoggetto != null){
        		if(($scope.rubricaBeneficiario.email == undefined || $scope.rubricaBeneficiario.email == null || $scope.rubricaBeneficiario.email.trim() == '') && ($scope.rubricaBeneficiario.pec == undefined || $scope.rubricaBeneficiario.pec == null || $scope.rubricaBeneficiario.pec.trim() == '')){
        			res = true;
        		}else if($scope.rubricaBeneficiario.tipoSoggetto == 'PRIVATO'){
					if($scope.rubricaBeneficiario.codiceFiscale == undefined || $scope.rubricaBeneficiario.codiceFiscale == null || $scope.rubricaBeneficiario.codiceFiscale.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.nome == undefined || $scope.rubricaBeneficiario.nome == null || $scope.rubricaBeneficiario.nome.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.cognome == undefined || $scope.rubricaBeneficiario.cognome == null || $scope.rubricaBeneficiario.cognome.trim() == ''){
						res = true;
					}
				}else if($scope.rubricaBeneficiario.tipoSoggetto == 'PUBBLICO'){
					if($scope.rubricaBeneficiario.codiceFiscale == undefined || $scope.rubricaBeneficiario.codiceFiscale == null || $scope.rubricaBeneficiario.codiceFiscale.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.denominazione == undefined || $scope.rubricaBeneficiario.denominazione == null || $scope.rubricaBeneficiario.denominazione.trim() == ''){
						res = true;
					}
				}else if($scope.rubricaBeneficiario.tipoSoggetto == 'IMPRESA_DITTA'){
					if($scope.rubricaBeneficiario.partitaIva == undefined || $scope.rubricaBeneficiario.partitaIva == null || $scope.rubricaBeneficiario.partitaIva.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.denominazione == undefined || $scope.rubricaBeneficiario.denominazione == null || $scope.rubricaBeneficiario.denominazione.trim() == ''){
						res = true;
					}
				}else if($scope.rubricaBeneficiario.tipoSoggetto == 'DITTA_INDIVIDUALE'){
					if($scope.rubricaBeneficiario.partitaIva == undefined || $scope.rubricaBeneficiario.partitaIva == null || $scope.rubricaBeneficiario.partitaIva.trim() == '' || $scope.rubricaBeneficiario.codiceFiscale == undefined || $scope.rubricaBeneficiario.codiceFiscale == null || $scope.rubricaBeneficiario.codiceFiscale.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.nome == undefined || $scope.rubricaBeneficiario.nome == null || $scope.rubricaBeneficiario.nome.trim() == ''){
						res = true;
					}else if($scope.rubricaBeneficiario.cognome == undefined || $scope.rubricaBeneficiario.cognome == null || $scope.rubricaBeneficiario.cognome.trim() == ''){
						res = true;
					}
				}
			}else{
				res = true;
			}
			return res;
        };

        $scope.showUpdate = function (id) {
            RubricaBeneficiario.get({id: id}, function(result) {
            	$(".toClear").val("");
                $scope.rubricaBeneficiario = result;
                $('#saveRubricaBeneficiarioModal').modal('show');
            });
        };

        $scope.save = function () {
        	$scope.adattaData();
        	if ($scope.rubricaBeneficiario.id != null) {
                RubricaBeneficiario.update($scope.rubricaBeneficiario,
                    function () {
                        $scope.refresh();
                    });
            } else {
                RubricaBeneficiario.save($scope.rubricaBeneficiario,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.disable = function (id) {
            RubricaBeneficiario.get({id: id}, function(result) {
                $scope.rubricaBeneficiario = result;
                $('#deleteRubricaBeneficiarioConfirmation').modal('show');
            });
        };
        
        $scope.adattaData = function(){
        	if($scope.rubricaBeneficiario && $scope.rubricaBeneficiario.dataNascita){
        		$scope.rubricaBeneficiario.dataNascita = $filter('date')($scope.rubricaBeneficiario.dataNascita, 'yyyy-MM-dd');
        	}
        };
        
        $scope.enable = function(rubricaBeneficiario){
        	RubricaBeneficiario.enable({ id: rubricaBeneficiario.id}, function(data){
     	    	rubricaBeneficiario.attivo = true;
     	    });
        };

        $scope.confirmDisable = function (id) {
            RubricaBeneficiario.disable({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRubricaBeneficiarioConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveRubricaBeneficiarioModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$(".toClear").val("");
        	if($scope.isAdmin){
        		$scope.rubricaBeneficiario = {attivo:true, id: null, aoo:{id:null}};
        	}else{
        		$scope.rubricaBeneficiario = {attivo:true, id: null, aoo:{id:$rootScope.profiloattivo.aoo.id}};
        	}
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.isAdmin = false;
        var admin = [];
	  	admin.push("ROLE_ADMIN");
	  	admin.push("ROLE_AMMINISTRATORE_RP");
	  	if(Principal.isInAnyRole(admin)){
	  		$scope.isAdmin = true;
	  		$scope.ricerca();
	  		Aoo.getMinimal({}, function(results){
	  			$scope.aoos = results;
	  			$scope.aoos.unshift({'id':'0', 'descrizione':'Tutte', 'validita':{'validoal':null}});
	  		});
	  	}else{
	  		$scope.aoos = [];
	  		if(angular.isDefined($rootScope.profiloattivo.aoo)){
	  			$scope.aoos.push($rootScope.profiloattivo.aoo);
	  		}
  			$scope.aoos.unshift({'id':'0', 'descrizione':'Tutte', 'validita':{'validoal':null}});
	  		$scope.tempSearch.aoo = $rootScope.profiloattivo.aoo;
	  		$scope.ricerca();
	  	}   
	  	
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
    });