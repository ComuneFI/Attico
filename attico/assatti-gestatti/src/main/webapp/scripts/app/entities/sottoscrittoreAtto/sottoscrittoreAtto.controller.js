'use strict';

angular.module('cifra2gestattiApp')
    .controller('SottoscrittoreAttoController', function ($scope, SottoscrittoreAtto, Atto, Profilo, ParseLinks) {
        $scope.sottoscrittoreAttos = [];
        $scope.attos = Atto.query();
        $scope.profilos = Profilo.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            SottoscrittoreAtto.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.sottoscrittoreAttos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            SottoscrittoreAtto.get({id: id}, function(result) {
                $scope.sottoscrittoreAtto = result;
                $('#saveSottoscrittoreAttoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.sottoscrittoreAtto.id != null) {
                SottoscrittoreAtto.update($scope.sottoscrittoreAtto,
                    function () {
                        $scope.refresh();
                    });
            } else {
                SottoscrittoreAtto.save($scope.sottoscrittoreAtto,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            SottoscrittoreAtto.get({id: id}, function(result) {
                $scope.sottoscrittoreAtto = result;
                $('#deleteSottoscrittoreAttoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SottoscrittoreAtto.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSottoscrittoreAttoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSottoscrittoreAttoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.sottoscrittoreAtto = {editor: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
