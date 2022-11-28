'use strict';

angular.module('cifra2gestattiApp')
    .controller('FatturaController', function ($scope, Fattura, Atto, RubricaDestinatarioEsterno, ParseLinks) {
        $scope.fatturas = [];
        $scope.attos = Atto.query();
        $scope.rubricadestinatarioesternos = RubricaDestinatarioEsterno.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Fattura.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.fatturas = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Fattura.get({id: id}, function(result) {
                $scope.fattura = result;
                $('#saveFatturaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.fattura.id != null) {
                Fattura.update($scope.fattura,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Fattura.save($scope.fattura,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Fattura.get({id: id}, function(result) {
                $scope.fattura = result;
                $('#deleteFatturaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Fattura.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFatturaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveFatturaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.fattura = {numeroRegistro: null, note: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
