'use strict';

angular.module('cifra2gestattiApp')
    .controller('DatoDetailController', function ($scope, $stateParams, Dato) {
        $scope.dato = {};
        $scope.load = function (id) {
            Dato.get({id: id}, function(result) {
              $scope.dato = result;
            });
        };
        $scope.load($stateParams.id);
    });
