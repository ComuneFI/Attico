'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoMateriaDetailController', function ($scope, $stateParams, TipoMateria, Aoo) {
        $scope.tipoMateria = {};
        $scope.load = function (id) {
            TipoMateria.get({id: id}, function(result) {
              $scope.tipoMateria = result;
            });
        };
        $scope.load($stateParams.id);
    });
