'use strict';

angular.module('cifra2gestattiApp')
    .controller('DocumentoInformaticoController', function ($scope, DocumentoInformatico, Atto, TipoDocumento, Serie, Parere, Resoconto, Lettera, Verbale, Fascicolo, Avanzamento, ParseLinks) {
        $scope.documentoInformaticos = [];
        $scope.attos = Atto.query();
        $scope.tipodocumentos = TipoDocumento.query();
        $scope.series = Serie.query();
        $scope.pareres = Parere.query();
        $scope.resocontos = Resoconto.query();
        $scope.letteras = Lettera.query();
        $scope.verbales = Verbale.query();
        $scope.fascicolos = Fascicolo.query();
        $scope.avanzamentos = Avanzamento.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            DocumentoInformatico.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.documentoInformaticos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            DocumentoInformatico.get({id: id}, function(result) {
                $scope.documentoInformatico = result;
                $('#saveDocumentoInformaticoModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.documentoInformatico.id != null) {
                DocumentoInformatico.update($scope.documentoInformatico,
                    function () {
                        $scope.refresh();
                    });
            } else {
                DocumentoInformatico.save($scope.documentoInformatico,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            DocumentoInformatico.get({id: id}, function(result) {
                $scope.documentoInformatico = result;
                $('#deleteDocumentoInformaticoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            DocumentoInformatico.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDocumentoInformaticoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveDocumentoInformaticoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.documentoInformatico = {titolo: null, nomeFile: null, idDiogene: null, ordineInclusione: null, pubblicabile: null, oggetto: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
