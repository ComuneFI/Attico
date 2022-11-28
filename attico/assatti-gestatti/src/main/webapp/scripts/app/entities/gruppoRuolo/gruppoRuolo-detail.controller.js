'use strict';

angular.module('cifra2gestattiApp')
    .controller('GruppoRuoloDetailController', function ($scope, $stateParams, GruppoRuolo, Aoo) {
        $scope.gruppoRuolo = {};
        $scope.load = function (id) {
            GruppoRuolo.get({id: id}, function(result) {
              $scope.gruppoRuolo = result;
            });
        };
        $scope.load($stateParams.id);
    });
