'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDeterminazioneDetailController', function ($scope, $stateParams, TipoDeterminazione) {
        $scope.tipoDeterminazione = {};
        $scope.load = function (id) {
            TipoDeterminazione.get({id: id}, function(result) {
              $scope.tipoDeterminazione = result;
            });
        };
        $scope.load($stateParams.id);
    });
