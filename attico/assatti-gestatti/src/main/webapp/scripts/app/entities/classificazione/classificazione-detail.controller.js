'use strict';

angular.module('cifra2gestattiApp')
    .controller('ClassificazioneDetailController', function ($scope, $stateParams, Classificazione, TipoAtto, Aoo, TipoDocumentoSerie) {
        $scope.classificazione = {};
        $scope.load = function (id) {
            Classificazione.get({id: id}, function(result) {
              $scope.classificazione = result;
            });
        };
        $scope.load($stateParams.id);
    });
