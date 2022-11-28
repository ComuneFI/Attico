'use strict';

angular.module('cifra2gestattiApp')
    .controller('ParereDetailController', function ($scope, $stateParams, Parere, Atto, Aoo) {
        $scope.parere = {};
        $scope.load = function (id) {
            Parere.get({id: id}, function(result) {
              $scope.parere = result;
            });
        };
        $scope.load($stateParams.id);
    });
