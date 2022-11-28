'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDocumentoController', function ($scope, TipoDocumento, ParseLinks) {
    	$scope.showAlreadyExists = false;
        $scope.tipoDocumentos = [];
        $scope.tempSearch = {};
        $scope.tipoDocumentoSearch = {};
        $scope.page = 1;
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoDocumentoSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoDocumentoSearch*/
        	$scope.tipoDocumentoSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		TipoDocumento.query({page: $scope.page, per_page: 5, descrizione:searchObject.descrizione, codice:searchObject.codice, riversamentoTipoatto:searchObject.riversamentoTipoatto}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDocumentos = result;
	            });
        	}else{
        		TipoDocumento.query({page: $scope.page, per_page: 5, descrizione:$scope.tipoDocumentoSearch.descrizione, codice:$scope.tipoDocumentoSearch.codice, riversamentoTipoatto: $scope.tipoDocumentoSearch.riversamentoTipoatto}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDocumentos = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            TipoDocumento.get({id: id}, function(result) {
                $scope.tipoDocumento = result;
                $('#saveTipoDocumentoModal').modal('show');
            });
        };
        
        
        $scope.save = function () {
        		$scope.counteItem = undefined;
        		var checkId = 0;
        		if($scope.tipoDocumento.id != undefined || $scope.tipoDocumento.id != null){
        			checkId = $scope.tipoDocumento.id;
        		}
            	TipoDocumento.checkIfAlreadyExists({id:checkId, codice:$scope.tipoDocumento.codice}, function(data){
            		$scope.showAlreadyExists = data.alreadyExists;
	              		if($scope.showAlreadyExists==false){
	            			if ($scope.tipoDocumento.id != null) {
		            			TipoDocumento.update($scope.tipoDocumento,
		                            function () {
		                                $scope.refresh();
		                        });
	            			} else {
	                            TipoDocumento.save($scope.tipoDocumento,
	                                function () {
	                                    $scope.refresh();
	                            });
	                        }	            			
	            		}
            	});
            	
        };

        $scope.delete = function (id) {
            TipoDocumento.get({id: id}, function(result) {
                $scope.tipoDocumento = result;
                $('#deleteTipoDocumentoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            TipoDocumento.delete({id: id},
                function (result) {
            		if(result.success){
	                    $scope.loadAll();
	                    $('#deleteTipoDocumentoConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = result.message;
            			$('#deleteTipoDocumentoConfirmation').modal('hide');
            			$('#deleteTipoDocumentoDeletingError').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoDocumentoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
        	$scope.erroreCancellazione = "";
        	$scope.showAlreadyExists = false;
            $scope.tipoDocumento = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
