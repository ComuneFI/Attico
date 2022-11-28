'use strict';

angular.module('cifra2gestattiApp')
    .controller('JobPubblicazioneDetailController', function ($scope, $log,$stateParams, JobPubblicazione, Atto) {
        $scope.jobPubblicazione = {};
        $scope.load = function (id) {
            JobPubblicazione.get({id: id}, function(result) {
              $scope.jobPubblicazione = result;
              $log.debug("JobPubblicazione:",result);
            });
        };
        $scope.load($stateParams.id);
    });
