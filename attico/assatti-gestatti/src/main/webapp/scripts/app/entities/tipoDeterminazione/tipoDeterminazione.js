'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoDeterminazione', {
                parent: 'entity',
                url: '/tipoDeterminazione',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoDeterminazione.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDeterminazione/tipoDeterminaziones.html',
                        controller: 'TipoDeterminazioneController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDeterminazione');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoDeterminazioneDetail', {
                parent: 'entity',
                url: '/tipoDeterminazione/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoDeterminazione.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoDeterminazione/tipoDeterminazione-detail.html',
                        controller: 'TipoDeterminazioneDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoDeterminazione');
                        return $translate.refresh();
                    }]
                }
            });
    });
