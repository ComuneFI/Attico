'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDocumentoSerieDetailController', function ($scope, $stateParams, TipoDocumentoSerie) {
        $scope.tipoDocumentoSerie = {};
        $scope.load = function (id) {
            TipoDocumentoSerie.get({id: id}, function(result) {
              $scope.tipoDocumentoSerie = result;
            });
        };
        $scope.load($stateParams.id);
    });
