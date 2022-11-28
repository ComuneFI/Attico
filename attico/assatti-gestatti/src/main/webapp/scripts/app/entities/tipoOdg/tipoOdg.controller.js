'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoOdgController', function ($scope, TipoOdg, ParseLinks) {
        $scope.tipoOdgs = [];
        $scope.tempSearch = {};
        $scope.tipoOdgSearch = {};
        $scope.page = 1;
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoOdgSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoOdgSearch*/
        	$scope.tipoOdgSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		TipoOdg.query({page: $scope.page, per_page: 5, descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoOdgs = result;
	            });
        	}else{
        		TipoOdg.query({page: $scope.page, per_page: 5, descrizione:$scope.tipoOdgSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoOdgs = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoOdg.get({id: id}, function(result) {
                $scope.tipoOdg = result;
                $('#saveTipoOdgModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.tipoOdg.id != null) {
                TipoOdg.update($scope.tipoOdg,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoOdg.save($scope.tipoOdg,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoOdg.get({id: id}, function(result) {
                $scope.tipoOdg = result;
                $('#deleteTipoOdgConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TipoOdg.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTipoOdgConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoOdgModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoOdg = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
