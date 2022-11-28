'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('schedaDato', {
                parent: 'entity',
                url: '/schedaDato',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.schedaDato.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/schedaDato/schedaDatos.html',
                        controller: 'SchedaDatoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedaDato');
                        return $translate.refresh();
                    }]
                }
            })
            .state('schedaDatoDetail', {
                parent: 'entity',
                url: '/schedaDato/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.schedaDato.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/schedaDato/schedaDato-detail.html',
                        controller: 'SchedaDatoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedaDato');
                        return $translate.refresh();
                    }]
                }
            });
    });
