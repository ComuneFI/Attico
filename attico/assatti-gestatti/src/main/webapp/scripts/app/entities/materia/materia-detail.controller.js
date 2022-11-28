'use strict';

angular.module('cifra2gestattiApp')
    .controller('MateriaDetailController', function ($scope, $stateParams, Materia, TipoMateria) {
        $scope.materia = {};
        $scope.load = function (id) {
            Materia.get({id: id}, function(result) {
              $scope.materia = result;
            });
        };
        $scope.load($stateParams.id);
    });
