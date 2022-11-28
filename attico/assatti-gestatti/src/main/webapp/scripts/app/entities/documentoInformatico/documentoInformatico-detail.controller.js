'use strict';

angular.module('cifra2gestattiApp')
    .controller('DocumentoInformaticoDetailController', function ($scope, $stateParams, DocumentoInformatico, Atto, TipoDocumento, Serie, Parere, Resoconto,  Lettera,  Verbale, Fascicolo, Avanzamento) {
        $scope.documentoInformatico = {};
        $scope.load = function (id) {
            DocumentoInformatico.get({id: id}, function(result) {
              $scope.documentoInformatico = result;
            });
        };
        $scope.load($stateParams.id);
    });
