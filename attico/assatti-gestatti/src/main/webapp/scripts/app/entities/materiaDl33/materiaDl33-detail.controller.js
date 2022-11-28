'use strict';

angular.module('cifra2gestattiApp')
    .controller('MateriaDl33DetailController', function ($scope, $stateParams, MateriaDl33, AmbitoDl33) {
        $scope.materiaDl33 = {};
        $scope.load = function (id) {
            MateriaDl33.get({id: id}, function(result) {
              $scope.materiaDl33 = result;
            });
        };
        $scope.load($stateParams.id);
    });
