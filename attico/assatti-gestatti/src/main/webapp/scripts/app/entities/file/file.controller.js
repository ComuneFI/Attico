'use strict';

angular.module('cifra2gestattiApp')
    .controller('FileController', function ($scope, File, ParseLinks) {
        $scope.files = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            File.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.files = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            File.get({id: id}, function(result) {
                $scope.file = result;
                $('#saveFileModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.file.id != null) {
                File.update($scope.file,
                    function () {
                        $scope.refresh();
                    });
            } else {
                File.save($scope.file,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            File.get({id: id}, function(result) {
                $scope.file = result;
                $('#deleteFileConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            File.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFileConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveFileModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.file = {nomeFile: null, contentType: null, size: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
