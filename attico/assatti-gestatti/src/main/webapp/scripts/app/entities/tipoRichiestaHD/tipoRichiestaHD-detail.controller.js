'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoRichiestaHDDetailController', function ($scope, $stateParams, TipoRichiestaHD) {
        $scope.tipoRichiestaHD = {};
        $scope.load = function (id) {
            TipoRichiestaHD.get({id: id}, function(result) {
              $scope.tipoRichiestaHD = result;
            });
        };
        $scope.load($stateParams.id);
    });
