'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoAoo', {
                parent: 'entity',
                url: '/tipoAoo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAoo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAoo/tipoAoos.html',
                        controller: 'TipoAooController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoAooDetail', {
                parent: 'entity',
                url: '/tipoAoo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.tipoAoo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoAoo/tipoAoo-detail.html',
                        controller: 'TipoAooDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoAoo');
                        return $translate.refresh();
                    }]
                }
            });
    });
