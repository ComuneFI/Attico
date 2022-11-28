'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ruolo', {
                parent: 'entity',
                url: '/ruolo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.ruolo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ruolo/ruolos.html',
                        controller: 'RuoloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ruolo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ruoloDetail', {
                parent: 'entity',
                url: '/ruolo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.ruolo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ruolo/ruolo-detail.html',
                        controller: 'RuoloDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ruolo');

                        return $translate.refresh();
                    }]
                }
            });
    });
