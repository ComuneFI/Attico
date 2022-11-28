'use strict';

angular.module('cifra2gestattiApp')
    .controller('MateriaDl33Controller', function ($scope, MateriaDl33, AmbitoDl33, ParseLinks) {
        $scope.materiaDl33s = [];
        $scope.page = 1;
        $scope.tempSearch = {};
        $scope.materiaDl33Search = {};
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.materiaDl33Search = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in materiaDl33Search*/
        	$scope.materiaDl33Search = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.attivo) || searchObject.attivo==undefined || searchObject.attivo == null){
        			searchObject.attivo = {};
        		}
        		if(angular.isUndefined(searchObject.ambitoDl33) || searchObject.ambitoDl33==undefined || searchObject.ambitoDl33 == null){
        			searchObject.ambitoDl33 = {};
        		}
        		MateriaDl33.query({page: $scope.page, per_page: 5, denominazione:searchObject.denominazione, attivo:searchObject.attivo.id, ambitoDl33:searchObject.ambitoDl33.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.materiaDl33s = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.materiaDl33Search.attivo) || $scope.materiaDl33Search.attivo==undefined || $scope.materiaDl33Search.attivo == null){
        			$scope.materiaDl33Search.attivo = {};
        		}
        		if(angular.isUndefined($scope.materiaDl33Search.ambitoDl33) || $scope.materiaDl33Search.ambitoDl33==undefined || $scope.materiaDl33Search.ambitoDl33 == null){
        			$scope.materiaDl33Search.ambitoDl33 = {};
        		}
        		MateriaDl33.query({page: $scope.page, per_page: 5, denominazione:$scope.materiaDl33Search.denominazione, attivo:$scope.materiaDl33Search.attivo.id, ambitoDl33:$scope.materiaDl33Search.ambitoDl33.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.materiaDl33s = result;
	            });
        	}
        };
        
        $scope.ambitodl33s = AmbitoDl33.query();
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            MateriaDl33.get({id: id}, function(result) {
                $scope.materiaDl33 = result;
                $('#saveMateriaDl33Modal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.materiaDl33.id != null) {
                MateriaDl33.update($scope.materiaDl33,
                    function () {
                        $scope.refresh();
                    });
            } else {
                MateriaDl33.save($scope.materiaDl33,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            MateriaDl33.get({id: id}, function(result) {
                $scope.materiaDl33 = result;
                $('#deleteMateriaDl33Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            MateriaDl33.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMateriaDl33Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveMateriaDl33Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.materiaDl33 = {denominazione: null, attivo: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
