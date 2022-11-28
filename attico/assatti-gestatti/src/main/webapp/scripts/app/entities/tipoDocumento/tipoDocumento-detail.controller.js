'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDocumentoDetailController', function ($scope, $stateParams, TipoDocumento) {
        $scope.tipoDocumento = {};
        $scope.load = function (id) {
            TipoDocumento.get({id: id}, function(result) {
              $scope.tipoDocumento = result;
            });
        };
        $scope.load($stateParams.id);
    });
