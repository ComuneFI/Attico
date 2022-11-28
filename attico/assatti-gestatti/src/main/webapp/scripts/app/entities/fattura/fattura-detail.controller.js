'use strict';

angular.module('cifra2gestattiApp')
    .controller('FatturaDetailController', function ($scope, $stateParams, Fattura, Atto, RubricaDestinatarioEsterno) {
        $scope.fattura = {};
        $scope.load = function (id) {
            Fattura.get({id: id}, function(result) {
              $scope.fattura = result;
            });
        };
        $scope.load($stateParams.id);
    });
