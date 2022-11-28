'use strict';

angular.module('cifra2gestattiApp')
    .controller('RubricaDestinatarioEsternoDetailController', function ($scope, $log,$stateParams, RubricaDestinatarioEsterno, Aoo, Indirizzo) {
        $scope.rubricaDestinatarioEsterno = {};
        $scope.load = function (id) {
            RubricaDestinatarioEsterno.get({id: id}, function(result) {
              $scope.rubricaDestinatarioEsterno = result;
              $log.debug("Rubrica:",result);
            });
        };
        $scope.load($stateParams.id);
    });
