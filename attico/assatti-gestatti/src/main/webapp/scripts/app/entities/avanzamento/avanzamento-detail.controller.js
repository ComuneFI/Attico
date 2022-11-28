'use strict';

angular.module('cifra2gestattiApp')
    .controller('AvanzamentoDetailController', function ($scope, $stateParams, Avanzamento, Atto, Profilo, Resoconto, Lettera,  Parere, Verbale, OrdineGiorno) {
        $scope.avanzamento = {};
        $scope.load = function (id) {
            Avanzamento.get({id: id}, function(result) {
              $scope.avanzamento = result;
            });
        };
        $scope.load($stateParams.id);
    });
