'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('gruppoRuolo', {
                parent: 'entity',
                url: '/gruppoRuolo',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.gruppoRuolo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gruppoRuolo/gruppoRuolos.html',
                        controller: 'GruppoRuoloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('gruppoRuolo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('gruppoRuoloDetail', {
                parent: 'entity',
                url: '/gruppoRuolo/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.gruppoRuolo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gruppoRuolo/gruppoRuolo-detail.html',
                        controller: 'GruppoRuoloDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('gruppoRuolo');
                        return $translate.refresh();
                    }]
                }
            });
    });