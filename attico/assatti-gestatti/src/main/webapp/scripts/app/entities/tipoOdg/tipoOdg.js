'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoOdg', {
                parent: 'entity',
                url: '/tipoOdg',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoOdg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoOdg/tipoOdgs.html',
                        controller: 'TipoOdgController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoOdg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoOdgDetail', {
                parent: 'entity',
                url: '/tipoOdg/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoOdg.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoOdg/tipoOdg-detail.html',
                        controller: 'TipoOdgDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoOdg');
                        return $translate.refresh();
                    }]
                }
            });
    });
