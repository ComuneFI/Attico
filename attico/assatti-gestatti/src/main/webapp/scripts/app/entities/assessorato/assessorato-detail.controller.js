'use strict';

angular.module('cifra2gestattiApp')
    .controller('AssessoratoDetailController', function ($scope, $stateParams, Assessorato, Profilo) {
        $scope.assessorato = {};
        $scope.load = function (id) {
            Assessorato.get({id: id}, function(result) {
              $scope.assessorato = result;
            });
        };
        $scope.load($stateParams.id);
    });
