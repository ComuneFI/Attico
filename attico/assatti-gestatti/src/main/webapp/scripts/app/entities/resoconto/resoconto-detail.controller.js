'use strict';

angular.module('cifra2gestattiApp')
    .controller('ResocontoDetailController', function ($scope, $stateParams, Resoconto, SedutaGiunta) {
        $scope.resoconto = {};
        $scope.load = function (id) {
            Resoconto.get({id: id}, function(result) {
              $scope.resoconto = result;
            });
        };
        $scope.load($stateParams.id);
    });
