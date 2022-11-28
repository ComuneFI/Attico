'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('parere', {
                parent: 'entity',
                url: '/parere',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.parere.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/parere/pareres.html',
                        controller: 'ParereController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('parere');
                        return $translate.refresh();
                    }]
                }
            })
            .state('parereDetail', {
                parent: 'entity',
                url: '/parere/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.parere.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/parere/parere-detail.html',
                        controller: 'ParereDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('parere');
                        return $translate.refresh();
                    }]
                }
            });
    });
