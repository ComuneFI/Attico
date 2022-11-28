'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoIter', {
                parent: 'entity',
                url: '/tipoIter',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoIter.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoIter/tipoIters.html',
                        controller: 'TipoIterController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoIter');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoIterDetail', {
                parent: 'entity',
                url: '/tipoIter/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoIter.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoIter/tipoIter-detail.html',
                        controller: 'TipoIterDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoIter');
                        return $translate.refresh();
                    }]
                }
            });
    });
