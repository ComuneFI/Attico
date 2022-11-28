'use strict';

angular.module('cifra2gestattiApp')
    .controller('SchedaDetailController', function ($scope, $stateParams, Scheda) {
        $scope.scheda = {};
        $scope.load = function (id) {
            Scheda.get({id: id}, function(result) {
              $scope.scheda = result;
            });
        };
        $scope.load($stateParams.id);
    });
