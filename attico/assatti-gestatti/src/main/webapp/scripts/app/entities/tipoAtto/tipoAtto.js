'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoAtto', {
                parent: 'entity',
                url: '/tipoAtto',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAtto.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAtto/tipoAttos.html',
                        controller: 'TipoAttoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAtto');
                        $translatePartialLoader.addPart('tipoIter');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoAttoDetail', {
                parent: 'entity',
                url: '/tipoAtto/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAtto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAtto/tipoAtto-detail.html',
                        controller: 'TipoAttoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAtto');
                        return $translate.refresh();
                    }]
                }
            });
    });
