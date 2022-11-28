'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoOdgDetailController', function ($scope, $stateParams, TipoOdg) {
        $scope.tipoOdg = {};
        $scope.load = function (id) {
            TipoOdg.get({id: id}, function(result) {
              $scope.tipoOdg = result;
            });
        };
        $scope.load($stateParams.id);
    });
