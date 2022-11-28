'use strict';

angular.module('cifra2gestattiApp')
    .controller('JobTrasparenzaDetailController', function ($scope, $log,$stateParams, JobTrasparenza, Atto) {
        $scope.jobTrasparenza = {};
        $scope.load = function (id) {
            JobTrasparenza.get({id: id}, function(result) {
              $scope.jobTrasparenza = result;
              $log.debug("JobTrasparenza:",result);
            });
        };
        $scope.load($stateParams.id);
    });
