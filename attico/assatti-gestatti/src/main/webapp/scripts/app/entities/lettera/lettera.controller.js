'use strict';

angular.module('cifra2gestattiApp')
    .controller('LetteraController', function ($scope, Lettera, ParseLinks) {
        $scope.letteras = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Lettera.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.letteras = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Lettera.get({id: id}, function(result) {
                $scope.lettera = result;
                $('#saveLetteraModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.lettera.id != null) {
                Lettera.update($scope.lettera,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Lettera.save($scope.lettera,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Lettera.get({id: id}, function(result) {
                $scope.lettera = result;
                $('#deleteLetteraConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Lettera.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteLetteraConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveLetteraModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.lettera = {tipoLettera: null, dataPubblicazioneSito: null, idDiogene: null, testo: null, data: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
