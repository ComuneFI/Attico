'use strict';

angular.module('cifra2gestattiApp')
    .controller('QualificaProfessionaleDetailController', function ($scope, $stateParams, QualificaProfessionale) {
        $scope.qualificaProfessionale = {};
        $scope.load = function (id) {
            QualificaProfessionale.get({id: id}, function(result) {
              $scope.qualificaProfessionale = result;
            });
        };
        $scope.load($stateParams.id);
    });
