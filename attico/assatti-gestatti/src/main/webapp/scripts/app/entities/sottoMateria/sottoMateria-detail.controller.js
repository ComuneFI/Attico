'use strict';

angular.module('cifra2gestattiApp')
    .controller('SottoMateriaDetailController', function ($scope, $stateParams, SottoMateria, Materia) {
        $scope.sottoMateria = {};
        $scope.load = function (id) {
            SottoMateria.get({id: id}, function(result) {
              $scope.sottoMateria = result;
            });
        };
        $scope.load($stateParams.id);
    });
