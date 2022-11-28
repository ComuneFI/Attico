'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAooController', function ($scope, TipoAoo, ParseLinks) {
        $scope.tipoAoos = [];
        $scope.page = 1;
        $scope.tipoAoosSearch = {};
        $scope.tempSearch = {};
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
	            TipoAoo.query({page: $scope.page, per_page: 20, codice:searchObject.codice, descrizione:searchObject.descrizione, note:searchObject.note}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAoos = result;
	            });
        	}
        	else{
        		TipoAoo.query({page: $scope.page, per_page: 20, codice:$scope.tipoAoosSearch.codice, descrizione:$scope.tipoAoosSearch.descrizione, note:$scope.tipoAoosSearch.note}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoAoos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoAoo.get({id: id}, function(result) {
                $scope.tipoAoo = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveTipoAooModal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.tipoAoo.id != null) {
                TipoAoo.update($scope.tipoAoo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoAoo.save($scope.tipoAoo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoAoo.get({id: id}, function(result) {
                $scope.tipoAoo = result;
                $('#deleteTipoAooConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
        	TipoAoo.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteTipoAooConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteTipoAooConfirmation').modal('hide');
            			$('#deleteTipoAooDeletingError').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoAooModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoAoo = {codice: null, descrizione: null, note: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.erroreCancellazione = "";
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoAoosSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoAoosSearch*/
        	$scope.tipoAoosSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
    });
