'use strict';

angular.module('cifra2gestattiApp')
    .controller('OrdineGiornoController', function ($scope, OrdineGiorno, SedutaGiunta, TipoOdg, ParseLinks,$location) {
        $scope.ordineGiornos = [];
        $scope.sedutagiuntas = SedutaGiunta.query();
        $scope.tipoodgs = TipoOdg.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            OrdineGiorno.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.ordineGiornos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

       

        /*$scope.save = function () {
            if ($scope.ordineGiorno.id != null) {
                OrdineGiorno.update($scope.ordineGiorno,
                    function () {
                        $scope.refresh();
                    });
            } else {
                OrdineGiorno.save($scope.ordineGiorno,
                    function () {
                        $scope.refresh();
                    });
            }
        };*/

        $scope.delete = function (id) {
            OrdineGiorno.get({id: id}, function(result) {
                $scope.ordineGiorno = result;
                $('#deleteOrdineGiornoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OrdineGiorno.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOrdineGiornoConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        
        $scope.editOdg = function (id) {
            OrdineGiorno.delete({id: id},
                function () {
            		$location.path("/giacenza/"+id);
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveOrdineGiornoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ordineGiorno = {numeroOdg: null, protocollo: null, dataPubblicazioneSito: null, idDiogene: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
