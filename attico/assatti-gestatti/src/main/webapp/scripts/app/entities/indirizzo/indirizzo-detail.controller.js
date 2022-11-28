'use strict';

angular.module('cifra2gestattiApp')
    .controller('IndirizzoDetailController', function ($scope, $stateParams, Indirizzo) {
        $scope.indirizzo = {};
        $scope.load = function (id) {
            Indirizzo.get({id: id}, function(result) {
              $scope.indirizzo = result;
            });
        };
        $scope.load($stateParams.id);
    });
