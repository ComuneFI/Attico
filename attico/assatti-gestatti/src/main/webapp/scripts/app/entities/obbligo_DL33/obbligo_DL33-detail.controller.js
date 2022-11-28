'use strict';

angular.module('cifra2gestattiApp')
    .controller('Obbligo_DL33DetailController', function ($scope, $stateParams, Obbligo_DL33, Cat_obbligo_DL33, Scheda) {
        $scope.obbligo_DL33 = {};
        $scope.load = function (id) {
            Obbligo_DL33.get({id: id}, function(result) {
              $scope.obbligo_DL33 = result;
            });
        };
        $scope.load($stateParams.id);
    });
