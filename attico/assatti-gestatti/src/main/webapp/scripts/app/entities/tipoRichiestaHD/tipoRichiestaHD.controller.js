'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoRichiestaHDController', function ($scope, TipoRichiestaHD, ParseLinks) {
        $scope.tipoRichiestaHDs = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            TipoRichiestaHD.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.tipoRichiestaHDs = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();
        
        $scope.disable = function(id){
        	TipoRichiestaHD.disable({id:id}, function(res){
        		$scope.loadAll();
        	});
        };
        
        $scope.enable = function(id){
        	TipoRichiestaHD.enable({id:id}, function(res){
        		$scope.loadAll();
        	})
        };

        $scope.showUpdate = function (id) {
            TipoRichiestaHD.get({id: id}, function(result) {
                $scope.tipoRichiestaHD = result;
                $('#saveTipoRichiestaHDModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.tipoRichiestaHD.id != null) {
                TipoRichiestaHD.update($scope.tipoRichiestaHD,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	$scope.tipoRichiestaHD.enabled = true;
            	$scope.tipoRichiestaHD.dirigente = false;
                TipoRichiestaHD.save($scope.tipoRichiestaHD,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            TipoRichiestaHD.get({id: id}, function(result) {
                $scope.tipoRichiestaHD = result;
                $('#deleteTipoRichiestaHDConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TipoRichiestaHD.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTipoRichiestaHDConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoRichiestaHDModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoRichiestaHD = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
