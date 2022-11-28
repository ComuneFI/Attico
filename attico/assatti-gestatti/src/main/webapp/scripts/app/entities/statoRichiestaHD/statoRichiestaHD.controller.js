'use strict';

angular.module('cifra2gestattiApp')
    .controller('StatoRichiestaHDController', function ($scope, StatoRichiestaHD, ParseLinks) {
        $scope.statoRichiestaHDs = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            StatoRichiestaHD.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.statoRichiestaHDs = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            StatoRichiestaHD.get({id: id}, function(result) {
                $scope.statoRichiestaHD = result;
                $('#saveStatoRichiestaHDModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.statoRichiestaHD.id != null) {
                StatoRichiestaHD.update($scope.statoRichiestaHD,
                    function () {
                        $scope.refresh();
                    });
            } else {
                StatoRichiestaHD.save($scope.statoRichiestaHD,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            StatoRichiestaHD.get({id: id}, function(result) {
                $scope.statoRichiestaHD = result;
                $('#deleteStatoRichiestaHDConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            StatoRichiestaHD.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteStatoRichiestaHDConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveStatoRichiestaHDModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.statoRichiestaHD = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
