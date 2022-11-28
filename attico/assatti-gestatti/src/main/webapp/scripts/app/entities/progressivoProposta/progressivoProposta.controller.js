'use strict';

angular.module('cifra2gestattiApp')
    .controller('ProgressivoPropostaController', function ($scope, ProgressivoProposta, TipoProgressivo, Aoo, ParseLinks, Principal, $rootScope) {
        $scope.progressivoPropostas = [];
        
        $scope.showAoo = false; 
        
        $scope.isAdmin = null;
        var admin = [];
	  	admin.push("ROLE_ADMIN");
	  	//admin.push("ROLE_AMMINISTRATORE_RP");
	  	if(Principal.isInAnyRole(admin)){
	  		$scope.isAdmin = true;
	  	}else{
	  		$scope.isAdmin = false;
	  	}

	  	$scope.tipoprogressivos = TipoProgressivo.query();
	  	if($scope.isAdmin){
	        $scope.aoos = Aoo.getMinimal();
        }else{
        	$scope.aoosFilter = [];
        	for(var i = 0; i<$rootScope.activeprofilos.length; i++){
        		$scope.aoosFilter.push($rootScope.activeprofilos[i].aoo.id);
        	}
        	if($scope.aoosFilter.length == 0){
        		$scope.aoosFilter.push(0);
        	}
        }
	  	
        $scope.page = 1;
        $scope.progressivoSearch = {};
        $scope.tempSearch = {};
        $scope.progressivoSearch.anno = new Date().getFullYear();
        $scope.tempSearch.anno = new Date().getFullYear();
        $scope.tempSearch.aoosFilter = $scope.aoosFilter;
        $scope.progressivoSearch.aoosFilter = $scope.aoosFilter;
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.progressivoSearch = {};
        	$scope.tempSearch = {};
        	$scope.progressivoSearch.anno = new Date().getFullYear();
            $scope.tempSearch.anno = new Date().getFullYear();
            $scope.tempSearch.aoosFilter = $scope.aoosFilter;
            $scope.progressivoSearch.aoosFilter = $scope.aoosFilter;
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in progressivoSearch*/
        	$scope.progressivoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
    		if(searchObject!=undefined && searchObject!=null){
    			var obj = {anno:searchObject.anno, progressivo:searchObject.progressivo, tipoProgressivo: searchObject.tipoProgressivo, aoo: searchObject.aoo};
    			if(searchObject.aoosFilter && searchObject.aoosFilter.length > 0){
    				obj.aoos = searchObject.aoosFilter;
    			}
    			ProgressivoProposta.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.progressivoPropostas = result;
	            });
    		}else{
    			var obj = {anno:$scope.progressivoSearch.anno, progressivo:$scope.progressivoSearch.progressivo, tipoProgressivo: $scope.progressivoSearch.tipoProgressivo, aoo: $scope.progressivoSearch.aoo};
    			if($scope.progressivoSearch.aoosFilter && $scope.progressivoSearch.aoosFilter.length > 0){
    				obj.aoos = $scope.progressivoSearch.aoosFilter;
    			}
    			ProgressivoProposta.search({page: $scope.page, per_page: 10}, obj, function(result, headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.progressivoPropostas = result;
                });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            ProgressivoProposta.get({id: id}, function(result) {
                $scope.progressivoProposta = result;
                $('#saveProgressivoPropostaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.progressivoProposta.id != null) {
                ProgressivoProposta.update($scope.progressivoProposta,
                    function () {
                        $scope.refresh();
                    });
            } else {
                ProgressivoProposta.save($scope.progressivoProposta,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            ProgressivoProposta.get({id: id}, function(result) {
                $scope.progressivoProposta = result;
                $('#deleteProgressivoPropostaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ProgressivoProposta.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteProgressivoPropostaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveProgressivoPropostaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.progressivoProposta = {anno: null, progressivo: null, id: null, aoo:{id:null}};
            if($scope.editForm!=null){
            	$scope.editForm.$setPristine();
                $scope.editForm.$setUntouched();
            }
        };
    });
