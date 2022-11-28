'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoIterDetailController', function ($scope, $stateParams, TipoIter) {
        $scope.tipoIter = {};
        $scope.load = function (id) {
            TipoIter.get({id: id}, function(result) {
              $scope.tipoIter = result;
            });
        };
        $scope.load($stateParams.id);
    });
