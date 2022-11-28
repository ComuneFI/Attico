'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoDocumento', {
                parent: 'entity',
                url: '/tipoDocumento',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoDocumento.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDocumento/tipoDocumentos.html',
                        controller: 'TipoDocumentoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDocumento');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoDocumentoDetail', {
                parent: 'entity',
                url: '/tipoDocumento/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoDocumento.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDocumento/tipoDocumento-detail.html',
                        controller: 'TipoDocumentoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDocumento');
                        return $translate.refresh();
                    }]
                }
            });
    });
