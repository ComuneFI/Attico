'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('statoRichiestaHD', {
                parent: 'entity',
                url: '/statoRichiestaHD',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.statoRichiestaHD.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/statoRichiestaHD/statoRichiestaHDs.html',
                        controller: 'StatoRichiestaHDController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('statoRichiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('statoRichiestaHDDetail', {
                parent: 'entity',
                url: '/statoRichiestaHD/:id',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'StatoRichiestaHD'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/statoRichiestaHD/statoRichiestaHD-detail.html',
                        controller: 'StatoRichiestaHDDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('statoRichiestaHD');
                        return $translate.refresh();
                    }]
                }
            });
    });
