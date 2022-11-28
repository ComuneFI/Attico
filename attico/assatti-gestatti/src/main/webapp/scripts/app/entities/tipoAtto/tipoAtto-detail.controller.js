'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAttoDetailController', function ($scope, $stateParams, TipoAtto) {
        $scope.tipoAtto = {};
        $scope.load = function (id) {
            TipoAtto.get({id: id}, function(result) {
              $scope.tipoAtto = result;
            });
        };
        $scope.load($stateParams.id);
    });
