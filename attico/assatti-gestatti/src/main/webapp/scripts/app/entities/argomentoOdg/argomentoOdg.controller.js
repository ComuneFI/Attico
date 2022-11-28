'use strict';

angular.module('cifra2gestattiApp')
    .controller('ArgomentoOdgController', function ($scope, ArgomentoOdg) {
        $scope.argomentoOdgs = [];
        $scope.loadAll = function() {
            ArgomentoOdg.query(function(result) {
               $scope.argomentoOdgs = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            ArgomentoOdg.get({id: id}, function(result) {
                $scope.argomentoOdg = result;
                $('#saveArgomentoOdgModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.argomentoOdg.id != null) {
                ArgomentoOdg.update($scope.argomentoOdg,
                    function () {
                        $scope.refresh();
                    });
            } else {
                ArgomentoOdg.save($scope.argomentoOdg,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            ArgomentoOdg.get({id: id}, function(result) {
                $scope.argomentoOdg = result;
                $('#deleteArgomentoOdgConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ArgomentoOdg.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteArgomentoOdgConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveArgomentoOdgModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.argomentoOdg = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
