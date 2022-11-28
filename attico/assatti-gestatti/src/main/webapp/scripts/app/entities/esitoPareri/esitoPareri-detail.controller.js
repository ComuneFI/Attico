'use strict';

angular.module('cifra2gestattiApp')
    .controller('EsitoPareriDetailController', function ($scope, $stateParams, EsitoPareri) {
        $scope.esitoPareri = {};
        $scope.load = function (id) {
            EsitoPareri.get({id: id}, function(result) {
              $scope.esitoPareri = result;
            });
        };
        $scope.load($stateParams.id);
    });
