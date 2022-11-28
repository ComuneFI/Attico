'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoProgressivo', {
                parent: 'entity',
                url: '/tipoProgressivo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoProgressivo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoProgressivo/tipoProgressivos.html',
                        controller: 'TipoProgressivoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoProgressivo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoProgressivoDetail', {
                parent: 'entity',
                url: '/tipoProgressivo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoProgressivo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoProgressivo/tipoProgressivo-detail.html',
                        controller: 'TipoProgressivoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoProgressivo');
                        return $translate.refresh();
                    }]
                }
            });
    });
