'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('lettera', {
                parent: 'entity',
                url: '/lettera',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.lettera.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/lettera/letteras.html',
                        controller: 'LetteraController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('lettera');
                        return $translate.refresh();
                    }]
                }
            })
            .state('letteraDetail', {
                parent: 'entity',
                url: '/lettera/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.lettera.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/lettera/lettera-detail.html',
                        controller: 'LetteraDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('lettera');
                        return $translate.refresh();
                    }]
                }
            });
    });
