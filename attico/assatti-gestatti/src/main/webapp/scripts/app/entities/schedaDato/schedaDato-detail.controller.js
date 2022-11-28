'use strict';

angular.module('cifra2gestattiApp')
    .controller('SchedaDatoDetailController', function ($scope, $stateParams, SchedaDato, Scheda, Dato) {
        $scope.schedaDato = {};
        $scope.load = function (id) {
            SchedaDato.get({id: id}, function(result) {
              $scope.schedaDato = result;
            });
        };
        $scope.load($stateParams.id);
    });
