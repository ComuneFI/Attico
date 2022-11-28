'use strict';

angular.module('cifra2gestattiApp')
    .controller('ResocontoController', function ($scope, Resoconto, SedutaGiunta, ParseLinks) {
        $scope.resocontos = [];
        $scope.sedutagiuntas = SedutaGiunta.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Resoconto.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.resocontos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Resoconto.get({id: id}, function(result) {
                $scope.resoconto = result;
                $('#saveResocontoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.resoconto.id != null) {
                Resoconto.update($scope.resoconto,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Resoconto.save($scope.resoconto,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Resoconto.get({id: id}, function(result) {
                $scope.resoconto = result;
                $('#deleteResocontoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Resoconto.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteResocontoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveResocontoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.resoconto = {organoDeliberante: null, tipo: null, idDiogene: null, dataPubblicazioneSito: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
