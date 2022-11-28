'use strict';

angular.module('cifra2gestattiApp')
    .controller('RubricaBeneficiarioDetailController', function ($scope, $stateParams, RubricaBeneficiario, Aoo) {
    	
        $scope.rubricaBeneficiario = {};
        
        $scope.load = function (id) {
            RubricaBeneficiario.get({id: id}, function(result) {
              $scope.rubricaBeneficiario = result;
            });
        };
        $scope.load($stateParams.id);
    });
