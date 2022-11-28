'use strict';

angular.module('cifra2gestattiApp')
    .controller('LetteraDetailController', function ($scope, $stateParams, Lettera) {
        $scope.lettera = {};
        $scope.load = function (id) {
            Lettera.get({id: id}, function(result) {
              $scope.lettera = result;
            });
        };
        $scope.load($stateParams.id);
    });
