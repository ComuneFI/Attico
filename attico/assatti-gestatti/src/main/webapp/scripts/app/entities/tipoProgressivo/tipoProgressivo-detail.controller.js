'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoProgressivoDetailController', function ($scope, $stateParams, TipoProgressivo) {
        $scope.tipoProgressivo = {};
        $scope.load = function (id) {
            TipoProgressivo.get({id: id}, function(result) {
              $scope.tipoProgressivo = result;
            });
        };
        $scope.load($stateParams.id);
    });
