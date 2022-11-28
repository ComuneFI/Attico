'use strict';

angular.module('cifra2gestattiApp')
    .controller('SottoMateriaController', function ($scope, SottoMateria, Materia, ParseLinks) {
        $scope.sottoMaterias = [];
        $scope.materias = Materia.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            SottoMateria.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.sottoMaterias = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            SottoMateria.get({id: id}, function(result) {
                $scope.sottoMateria = result;
                $('#saveSottoMateriaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.sottoMateria.id != null) {
                SottoMateria.update($scope.sottoMateria,
                    function () {
                        $scope.refresh();
                    });
            } else {
                SottoMateria.save($scope.sottoMateria,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            SottoMateria.get({id: id}, function(result) {
                $scope.sottoMateria = result;
                $('#deleteSottoMateriaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SottoMateria.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSottoMateriaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSottoMateriaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.sottoMateria = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
