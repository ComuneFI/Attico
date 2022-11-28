'use strict';

angular.module('cifra2gestattiApp')
    .controller('AmbitoDl33Controller', function ($scope, AmbitoDl33, ParseLinks) {
        $scope.ambitoDl33s = [];
        $scope.tempSearch = {};
        $scope.ambitoDl33Search = {};
        $scope.page = 1;
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.ambitoDl33Search = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in ambitoDl33Search*/
        	$scope.ambitoDl33Search = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.attivo) || searchObject.attivo==undefined || searchObject.attivo == null){
        			searchObject.attivo = {};
        		}
        		AmbitoDl33.query({page: $scope.page, per_page: 5, denominazione:searchObject.denominazione, attivo:searchObject.attivo.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ambitoDl33s = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.ambitoDl33Search.attivo) || $scope.ambitoDl33Search.attivo==undefined || $scope.ambitoDl33Search.attivo == null){
        			$scope.ambitoDl33Search.attivo = {};
        		}
        		AmbitoDl33.query({page: $scope.page, per_page: 5, denominazione:$scope.ambitoDl33Search.denominazione, attivo:$scope.ambitoDl33Search.attivo.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ambitoDl33s = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            AmbitoDl33.get({id: id}, function(result) {
                $scope.ambitoDl33 = result;
                $('#saveAmbitoDl33Modal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.ambitoDl33.id != null) {
                AmbitoDl33.update($scope.ambitoDl33,
                    function () {
                        $scope.refresh();
                    });
            } else {
                AmbitoDl33.save($scope.ambitoDl33,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            AmbitoDl33.get({id: id}, function(result) {
                $scope.ambitoDl33 = result;
                $('#deleteAmbitoDl33Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            AmbitoDl33.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAmbitoDl33Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveAmbitoDl33Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ambitoDl33 = {denominazione: null, attivo: true, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
