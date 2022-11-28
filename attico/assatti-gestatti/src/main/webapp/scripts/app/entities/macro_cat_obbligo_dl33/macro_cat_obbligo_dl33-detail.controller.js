'use strict';

angular.module('cifra2gestattiApp')
    .controller('Macro_cat_obbligo_dl33DetailController', function ($scope, $stateParams, Macro_cat_obbligo_dl33) {
        $scope.macro_cat_obbligo_dl33 = {};
        $scope.load = function (id) {
            Macro_cat_obbligo_dl33.get({id: id}, function(result) {
              $scope.macro_cat_obbligo_dl33 = result;
            });
        };
        $scope.load($stateParams.id);
    });
