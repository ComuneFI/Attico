'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ambitoDl33', {
                parent: 'entity',
                url: '/ambitoDl33',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.ambitoDl33.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ambitoDl33/ambitoDl33s.html',
                        controller: 'AmbitoDl33Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ambitoDl33');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ambitoDl33Detail', {
                parent: 'entity',
                url: '/ambitoDl33/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.ambitoDl33.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ambitoDl33/ambitoDl33-detail.html',
                        controller: 'AmbitoDl33DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ambitoDl33');
                        return $translate.refresh();
                    }]
                }
            });
    });
