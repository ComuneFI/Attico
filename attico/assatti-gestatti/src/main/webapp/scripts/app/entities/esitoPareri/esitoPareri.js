'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('esitoPareri', {
                parent: 'entity',
                url: '/esitoPareri',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.esitoPareri.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/esitoPareri/esitoPareris.html',
                        controller: 'EsitoPareriController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('esitoPareri');
                        return $translate.refresh();
                    }]
                }
            })
            .state('esitoPareriDetail', {
                parent: 'entity',
                url: '/esitoPareri/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.esitoPareri.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/esitoPareri/esitoPareri-detail.html',
                        controller: 'EsitoPareriDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('esitoPareri');

                        return $translate.refresh();
                    }]
                }
            });
    });
