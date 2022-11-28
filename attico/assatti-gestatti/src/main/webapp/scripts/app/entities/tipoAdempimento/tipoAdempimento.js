'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoAdempimento', {
                parent: 'entity',
                url: '/tipoAdempimento',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAdempimento.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAdempimento/tipoAdempimentos.html',
                        controller: 'TipoAdempimentoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAdempimento');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoAdempimentoDetail', {
                parent: 'entity',
                url: '/tipoAdempimento/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAdempimento.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAdempimento/tipoAdempimento-detail.html',
                        controller: 'TipoAdempimentoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAdempimento');
                        return $translate.refresh();
                    }]
                }
            });
    });
