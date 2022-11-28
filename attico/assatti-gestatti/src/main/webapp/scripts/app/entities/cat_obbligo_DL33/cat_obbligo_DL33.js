'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('cat_obbligo_DL33', {
                parent: 'entity',
                url: '/cat_obbligo_DL33',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.cat_obbligo_DL33.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cat_obbligo_DL33/cat_obbligo_DL33s.html',
                        controller: 'Cat_obbligo_DL33Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cat_obbligo_DL33');
                        return $translate.refresh();
                    }]
                }
            })
            .state('cat_obbligo_DL33Detail', {
                parent: 'entity',
                url: '/cat_obbligo_DL33/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.cat_obbligo_DL33.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cat_obbligo_DL33/cat_obbligo_DL33-detail.html',
                        controller: 'Cat_obbligo_DL33DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cat_obbligo_DL33');
                        return $translate.refresh();
                    }]
                }
            });
    });
