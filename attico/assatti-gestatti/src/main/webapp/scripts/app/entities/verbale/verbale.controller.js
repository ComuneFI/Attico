'use strict';

angular.module('cifra2gestattiApp')
    .controller('VerbaleController', function ($scope, Verbale, SedutaGiunta, ParseLinks) {
        $scope.verbales = [];
        $scope.sedutagiuntas = SedutaGiunta.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Verbale.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.verbales = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Verbale.get({id: id}, function(result) {
                $scope.verbale = result;
                $('#saveVerbaleModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.verbale.id != null) {
                Verbale.update($scope.verbale,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Verbale.save($scope.verbale,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Verbale.get({id: id}, function(result) {
                $scope.verbale = result;
                $('#deleteVerbaleConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Verbale.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteVerbaleConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveVerbaleModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.verbale = {organoDeliberante: null, idDiogene: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
