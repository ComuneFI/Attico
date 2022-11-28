'use strict';

angular.module('cifra2gestattiApp')
    .controller('MateriaController', function ($scope, Materia, TipoMateria, ParseLinks) {
        $scope.materias = [];
        $scope.tipomaterias = TipoMateria.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Materia.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.materias = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Materia.get({id: id}, function(result) {
                $scope.materia = result;
                $('#saveMateriaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.materia.id != null) {
                Materia.update($scope.materia,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Materia.save($scope.materia,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Materia.get({id: id}, function(result) {
                $scope.materia = result;
                $('#deleteMateriaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Materia.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMateriaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveMateriaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.materia = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
