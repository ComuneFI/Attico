'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAdempimentoController', function ($scope, TipoAdempimento,TipoIter, ParseLinks) {
        $scope.tipoAdempimentos = [];
        $scope.tipoiters = TipoIter.query();
        $scope.tempSearch = {};
        $scope.tipoAdempimentoSearch = {};
        $scope.page = 1;
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoAdempimentoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoAdempimentoSearch*/
        	$scope.tipoAdempimentoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.tipoiter) || searchObject.tipoiter==undefined || searchObject.tipoiter == null){
        			searchObject.tipoiter = {};
        		}
        		TipoAdempimento.query({page: $scope.page, per_page: 5, descrizione:searchObject.descrizione, tipoiter:searchObject.tipoiter.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAdempimentos = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.tipoAdempimentoSearch.tipoiter) || $scope.tipoAdempimentoSearch.tipoiter==undefined || $scope.tipoAdempimentoSearch.tipoiter == null){
        			$scope.tipoAdempimentoSearch.tipoiter = {};
        		}
        		TipoAdempimento.query({page: $scope.page, per_page: 5, descrizione:$scope.tipoAdempimentoSearch.descrizione, tipoiter:$scope.tipoAdempimentoSearch.tipoiter.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAdempimentos = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoAdempimento.get({id: id}, function(result) {
                $scope.tipoAdempimento = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveTipoAdempimentoModal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.tipoAdempimento.id != null) {
                TipoAdempimento.update($scope.tipoAdempimento,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoAdempimento.save($scope.tipoAdempimento,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoAdempimento.get({id: id}, function(result) {
                $scope.tipoAdempimento = result;
                $('#deleteTipoAdempimentoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TipoAdempimento.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTipoAdempimentoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoAdempimentoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoAdempimento = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
