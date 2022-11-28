'use strict';

angular.module('cifra2gestattiApp')
    .controller('ReportRuntimeDetailController', function ($scope, $stateParams, ReportRuntime) {
        $scope.reportRuntime = {};
        $scope.load = function (id) {
            ReportRuntime.get({id: id}, function(result) {
              $scope.reportRuntime = result;
            });
        };
        $scope.load($stateParams.id);
    });
