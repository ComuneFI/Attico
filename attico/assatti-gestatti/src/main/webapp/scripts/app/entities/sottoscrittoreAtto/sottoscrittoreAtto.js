'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sottoscrittoreAtto', {
                parent: 'entity',
                url: '/sottoscrittoreAtto',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.sottoscrittoreAtto.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sottoscrittoreAtto/sottoscrittoreAttos.html',
                        controller: 'SottoscrittoreAttoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sottoscrittoreAtto');
                        return $translate.refresh();
                    }]
                }
            })
            .state('sottoscrittoreAttoDetail', {
                parent: 'entity',
                url: '/sottoscrittoreAtto/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.sottoscrittoreAtto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sottoscrittoreAtto/sottoscrittoreAtto-detail.html',
                        controller: 'SottoscrittoreAttoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sottoscrittoreAtto');
                        return $translate.refresh();
                    }]
                }
            });
    });
