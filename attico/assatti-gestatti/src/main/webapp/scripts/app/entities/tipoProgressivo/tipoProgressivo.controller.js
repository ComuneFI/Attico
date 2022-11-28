'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoProgressivoController', function ($scope, TipoProgressivo,TipoIter, ParseLinks) {
        $scope.tipoProgressivos = [];
        $scope.tempSearch = {};
        $scope.tipoProgressivoSearch = {};
        $scope.page = 1;
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoProgressivoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoProgressivoSearch*/
        	$scope.tipoProgressivoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		TipoProgressivo.query({page: $scope.page, per_page: 5, descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoProgressivos = result;
	            });
        	}else{
        		TipoProgressivo.query({page: $scope.page, per_page: 5, descrizione:$scope.tipoProgressivoSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoProgressivos = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoProgressivo.get({id: id}, function(result) {
                $scope.tipoProgressivo = result;
                $('#saveTipoProgressivoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.tipoProgressivo.id != null) {
                TipoProgressivo.update($scope.tipoProgressivo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoProgressivo.save($scope.tipoProgressivo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoProgressivo.get({id: id}, function(result) {
                $scope.tipoProgressivo = result;
                $('#deleteTipoProgressivoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            TipoProgressivo.delete({id: id},
            	function (res) {
	        		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteTipoProgressivoConfirmation').modal('hide');
	                    $scope.clear();
	        		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteTipoProgressivoConfirmation').modal('hide');
            			$('#deleteTipoProgressivoDeletingError').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoProgressivoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            $scope.tipoProgressivo = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
