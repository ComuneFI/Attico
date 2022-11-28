'use strict';

angular.module('cifra2gestattiApp')
    .controller('ComposizioneGiuntaDetailController', function ($scope, $stateParams, Profilo, TipoAtto, Utente) {
        $scope.profilo = {};
        $scope.load = function (id) {
            Profilo.get({id: id}, function(result) {
              $scope.profilo = result;
            });
        };
        $scope.load($stateParams.id);
    });
