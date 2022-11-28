'use strict';

angular.module('cifra2gestattiApp')
    .controller('AmbitoDl33DetailController', function ($scope, $stateParams, AmbitoDl33) {
        $scope.ambitoDl33 = {};
        $scope.load = function (id) {
            AmbitoDl33.get({id: id}, function(result) {
              $scope.ambitoDl33 = result;
            });
        };
        $scope.load($stateParams.id);
    });
