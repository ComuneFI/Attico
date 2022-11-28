'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('documentoInformatico', {
                parent: 'entity',
                url: '/documentoInformatico',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.documentoInformatico.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/documentoInformatico/documentoInformaticos.html',
                        controller: 'DocumentoInformaticoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('documentoInformatico');
                        return $translate.refresh();
                    }]
                }
            })
            .state('documentoInformaticoDetail', {
                parent: 'entity',
                url: '/documentoInformatico/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.documentoInformatico.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/documentoInformatico/documentoInformatico-detail.html',
                        controller: 'DocumentoInformaticoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('documentoInformatico');
                        return $translate.refresh();
                    }]
                }
            });
    });
