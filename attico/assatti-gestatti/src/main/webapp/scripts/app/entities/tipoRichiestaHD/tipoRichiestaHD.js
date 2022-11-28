'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoRichiestaHD', {
                parent: 'entity',
                url: '/tipoRichiestaHD',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoRichiestaHD.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoRichiestaHD/tipoRichiestaHDs.html',
                        controller: 'TipoRichiestaHDController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoRichiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoRichiestaHDDetail', {
                parent: 'entity',
                url: '/tipoRichiestaHD/:id',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.statoRichiestaHD.home.detail'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoRichiestaHD/tipoRichiestaHD-detail.html',
                        controller: 'TipoRichiestaHDDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoRichiestaHD');
                        return $translate.refresh();
                    }]
                }
            });
    });
