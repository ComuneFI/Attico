'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('avanzamento', {
                parent: 'entity',
                url: '/avanzamento',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.avanzamento.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/avanzamento/avanzamentos.html',
                        controller: 'AvanzamentoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('avanzamento');
                        return $translate.refresh();
                    }]
                }
            })
            .state('avanzamentoDetail', {
                parent: 'entity',
                url: '/avanzamento/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.avanzamento.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/avanzamento/avanzamento-detail.html',
                        controller: 'AvanzamentoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('avanzamento');
                        return $translate.refresh();
                    }]
                }
            });
    });
