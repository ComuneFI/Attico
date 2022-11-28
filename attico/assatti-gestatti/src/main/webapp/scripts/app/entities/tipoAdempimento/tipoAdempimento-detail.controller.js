'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAdempimentoDetailController', function ($scope, $stateParams, TipoAdempimento) {
        $scope.tipoAdempimento = {};
        $scope.load = function (id) {
            TipoAdempimento.get({id: id}, function(result) {
              $scope.tipoAdempimento = result;
            });
        };
        $scope.load($stateParams.id);
    });
