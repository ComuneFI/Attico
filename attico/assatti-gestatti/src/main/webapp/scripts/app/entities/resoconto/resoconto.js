'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('resoconto', {
                parent: 'entity',
                url: '/resoconto',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.resoconto.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/resoconto/resocontos.html',
                        controller: 'ResocontoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('resoconto');
                        return $translate.refresh();
                    }]
                }
            })
            .state('resocontoDetail', {
                parent: 'entity',
                url: '/resoconto/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.resoconto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/resoconto/resoconto-detail.html',
                        controller: 'ResocontoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('resoconto');
                        return $translate.refresh();
                    }]
                }
            });
    });
