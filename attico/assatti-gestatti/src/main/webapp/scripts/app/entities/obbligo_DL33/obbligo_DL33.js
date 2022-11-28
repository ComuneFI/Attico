'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('obbligo_DL33', {
                parent: 'entity',
                url: '/obbligo_DL33',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.obbligo_DL33.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/obbligo_DL33/obbligo_DL33s.html',
                        controller: 'Obbligo_DL33Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('obbligo_DL33');
                        return $translate.refresh();
                    }]
                }
            })
            .state('obbligo_DL33Detail', {
                parent: 'entity',
                url: '/obbligo_DL33/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.obbligo_DL33.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/obbligo_DL33/obbligo_DL33-detail.html',
                        controller: 'Obbligo_DL33DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('obbligo_DL33');
                        return $translate.refresh();
                    }]
                }
            });
    });
