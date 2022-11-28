'use strict';

angular.module('cifra2gestattiApp')
    .controller('Cat_obbligo_DL33DetailController', function ($scope, $stateParams, Cat_obbligo_DL33, Macro_cat_obbligo_dl33) {
        $scope.cat_obbligo_DL33 = {};
        $scope.load = function (id) {
            Cat_obbligo_DL33.get({id: id}, function(result) {
              $scope.cat_obbligo_DL33 = result;
            });
        };
        $scope.load($stateParams.id);
    });
