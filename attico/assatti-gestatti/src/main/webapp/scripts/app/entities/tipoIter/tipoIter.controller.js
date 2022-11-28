'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoIterController', function ($scope, TipoIter, ParseLinks,TipoAtto) {
        $scope.tipoIters = [];
        $scope.tipoAttos = TipoAtto.query();
        $scope.tempSearch = {};
        $scope.tipoIterSearch = {};
        
        $scope.page = 1;
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		TipoIter.query({page: $scope.page, per_page: 5, codice:searchObject.codice,  descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoIters = result;
	            });
        	}else{
        		TipoIter.query({page: $scope.page, per_page: 5, codice:$scope.tipoIterSearch.codice, descrizione:$scope.tipoIterSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoIters = result;
	            });
        	}
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoIterSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoIterSearch*/
        	$scope.tipoIterSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoIter.get({id: id}, function(result) {
                $scope.tipoIter = result;
                $('#saveTipoIterModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.tipoIter.id != null) {
                TipoIter.update($scope.tipoIter,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoIter.save($scope.tipoIter,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoIter.get({id: id}, function(result) {
                $scope.tipoIter = result;
                $('#deleteTipoIterConfirmation').modal('show');
            });
        };
      //  $scope.confirmDelete = function (id) {
      //      TipoIter.delete({id: id},
      //          function () {
      //              $scope.loadAll();
      //              $('#deleteTipoIterConfirmation').modal('hide');
      //              $scope.clear();
      //          });
      //  };

        
        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            TipoIter.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteTipoIterConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteTipoIterConfirmation').modal('hide');
            			$('#deleteTipoIterDeletingError').modal('show');
            		}
                });
        };
        
        

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoIterModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoIter = {codice: null, descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.erroreCancellazione = "";
        };
    });
