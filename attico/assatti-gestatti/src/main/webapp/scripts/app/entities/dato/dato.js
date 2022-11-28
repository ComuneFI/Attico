'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dato', {
                parent: 'entity',
                url: '/dato',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.dato.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dato/datos.html',
                        controller: 'DatoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dato');
                        return $translate.refresh();
                    }]
                }
            })
            .state('datoDetail', {
                parent: 'entity',
                url: '/dato/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.dato.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dato/dato-detail.html',
                        controller: 'DatoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dato');
                        return $translate.refresh();
                    }]
                }
            });
    });
