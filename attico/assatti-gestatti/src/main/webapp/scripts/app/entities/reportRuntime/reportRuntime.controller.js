'use strict';

angular.module('cifra2gestattiApp')
    .controller('ReportRuntimeController', function ($scope, ReportRuntime) {
        $scope.reportRuntimes = [];
        $scope.loadAll = function() {
            ReportRuntime.query(function(result) {
               $scope.reportRuntimes = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            ReportRuntime.get({id: id}, function(result) {
                $scope.reportRuntime = result;
                $('#saveReportRuntimeModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.reportRuntime.id != null) {
                ReportRuntime.update($scope.reportRuntime,
                    function () {
                        $scope.refresh();
                    });
            } else {
                ReportRuntime.save($scope.reportRuntime,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            ReportRuntime.get({id: id}, function(result) {
                $scope.reportRuntime = result;
                $('#deleteReportRuntimeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ReportRuntime.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteReportRuntimeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveReportRuntimeModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.reportRuntime = {html: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
