'use strict';

angular.module('cifra2gestattiApp')
    .controller('ParereController', function ($scope, Parere, Atto, Aoo, ParseLinks) {
        $scope.pareres = [];
        $scope.attos = Atto.query();
        $scope.aoos = Aoo.getMinimal();
        $scope.page = 1;
        $scope.loadAll = function() {
            Parere.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.pareres = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Parere.get({id: id}, function(result) {
                $scope.parere = result;
                $('#saveParereModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.parere.id != null) {
                Parere.update($scope.parere,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Parere.save($scope.parere,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Parere.get({id: id}, function(result) {
                $scope.parere = result;
                $('#deleteParereConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Parere.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteParereConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveParereModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.parere = {tipoParere: null, parere: null, idDiogene: null, data: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
