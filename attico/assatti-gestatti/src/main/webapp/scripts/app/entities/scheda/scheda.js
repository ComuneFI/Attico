'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('scheda', {
                parent: 'entity',
                url: '/scheda',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.scheda.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/scheda/schedas.html',
                        controller: 'SchedaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('scheda');
                        $translatePartialLoader.addPart('schedaDato');
                        return $translate.refresh();
                    }]
                }
            })
            .state('schedaDetail', {
                parent: 'entity',
                url: '/scheda/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.scheda.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/scheda/scheda-detail.html',
                        controller: 'SchedaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('scheda');
                        $translatePartialLoader.addPart('schedaDato');
                        return $translate.refresh();
                    }]
                }
            });
    });
