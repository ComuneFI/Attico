'use strict';

angular.module('cifra2gestattiApp')
    .controller('ProgressivoPropostaDetailController', function ($scope, $stateParams, ProgressivoProposta, TipoAtto, Aoo) {
        $scope.progressivoProposta = {};
        $scope.load = function (id) {
            ProgressivoProposta.get({id: id}, function(result) {
              $scope.progressivoProposta = result;
            });
        };
        $scope.load($stateParams.id);
    });
