'use strict';

angular.module('cifra2gestattiApp')
    .controller('SottoscrittoreAttoDetailController', function ($scope, $stateParams, SottoscrittoreAtto, Atto, Profilo) {
        $scope.sottoscrittoreAtto = {};
        $scope.load = function (id) {
            SottoscrittoreAtto.get({id: id}, function(result) {
              $scope.sottoscrittoreAtto = result;
            });
        };
        $scope.load($stateParams.id);
    });
