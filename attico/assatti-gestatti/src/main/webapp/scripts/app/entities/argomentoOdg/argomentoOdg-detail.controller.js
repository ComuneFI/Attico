'use strict';

angular.module('cifra2gestattiApp')
    .controller('ArgomentoOdgDetailController', function ($scope, $stateParams, ArgomentoOdg) {
        $scope.argomentoOdg = {};
        $scope.load = function (id) {
            ArgomentoOdg.get({id: id}, function(result) {
              $scope.argomentoOdg = result;
            });
        };
        $scope.load($stateParams.id);
    });
