'use strict';

angular.module('cifra2gestattiApp')
    .controller('StatoRichiestaHDDetailController', function ($scope, $stateParams, StatoRichiestaHD) {
        $scope.statoRichiestaHD = {};
        $scope.load = function (id) {
            StatoRichiestaHD.get({id: id}, function(result) {
              $scope.statoRichiestaHD = result;
            });
        };
        $scope.load($stateParams.id);
    });
