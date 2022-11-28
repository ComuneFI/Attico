'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoAooDetailController', function ($scope, $stateParams, TipoAoo) {
        $scope.tipoAoo = {};
        $scope.load = function (id) {
            TipoAoo.get({id: id}, function(result) {
              $scope.tipoAoo = result;
            });
        };
        $scope.load($stateParams.id);
    });
