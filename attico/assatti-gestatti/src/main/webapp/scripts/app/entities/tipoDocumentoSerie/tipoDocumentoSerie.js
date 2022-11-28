'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoDocumentoSerie', {
                parent: 'entity',
                url: '/tipoDocumentoSerie',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoDocumentoSerie.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDocumentoSerie/tipoDocumentoSeries.html',
                        controller: 'TipoDocumentoSerieController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDocumentoSerie');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoDocumentoSerieDetail', {
                parent: 'entity',
                url: '/tipoDocumentoSerie/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoDocumentoSerie.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDocumentoSerie/tipoDocumentoSerie-detail.html',
                        controller: 'TipoDocumentoSerieDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDocumentoSerie');
                        return $translate.refresh();
                    }]
                }
            });
    });
