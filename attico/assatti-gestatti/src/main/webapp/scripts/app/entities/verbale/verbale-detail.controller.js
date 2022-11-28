'use strict';

angular.module('cifra2gestattiApp')
    .controller('VerbaleDetailController', function ($scope, $stateParams, Verbale, SedutaGiunta) {
        $scope.verbale = {};
        $scope.load = function (id) {
            Verbale.get({id: id}, function(result) {
              $scope.verbale = result;
            });
        };
        $scope.load($stateParams.id);
    });
