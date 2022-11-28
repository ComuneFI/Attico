'use strict';

angular.module('cifra2gestattiApp')
    .controller('RuoloDetailController', function ($scope, $stateParams, Ruolo) {
        $scope.ruolo = {};
        $scope.load = function (id) {
            Ruolo.get({id: id}, function(result) {
              $scope.ruolo = result;
            });
        };
        $scope.load($stateParams.id);
    });
