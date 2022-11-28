'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('verbale', {
                parent: 'entity',
                url: '/verbale',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.verbale.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/verbale/verbales.html',
                        controller: 'VerbaleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('verbale');
                        return $translate.refresh();
                    }]
                }
            })
            .state('verbaleDetail', {
                parent: 'entity',
                url: '/verbale/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.verbale.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/verbale/verbale-detail.html',
                        controller: 'VerbaleDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('verbale');
                        return $translate.refresh();
                    }]
                }
            });
    });
