'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('materiaDl33', {
                parent: 'entity',
                url: '/materiaDl33',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.materiaDl33.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/materiaDl33/materiaDl33s.html',
                        controller: 'MateriaDl33Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('materiaDl33');
                        return $translate.refresh();
                    }]
                }
            })
            .state('materiaDl33Detail', {
                parent: 'entity',
                url: '/materiaDl33/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.materiaDl33.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/materiaDl33/materiaDl33-detail.html',
                        controller: 'MateriaDl33DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('materiaDl33');
                        return $translate.refresh();
                    }]
                }
            });
    });
