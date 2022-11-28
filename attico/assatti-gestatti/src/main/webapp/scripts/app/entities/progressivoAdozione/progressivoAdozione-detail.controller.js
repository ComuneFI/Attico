'use strict';

angular.module('cifra2gestattiApp')
    .controller('ProgressivoAdozioneDetailController', function ($scope, $stateParams, ProgressivoAdozione, TipoAtto, Aoo) {
        $scope.progressivoAdozione = {};
        $scope.load = function (id) {
            ProgressivoAdozione.get({id: id}, function(result) {
              $scope.progressivoAdozione = result;
            });
        };
        $scope.load($stateParams.id);
    });
