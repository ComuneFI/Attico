'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('macro_cat_obbligo_dl33', {
                parent: 'entity',
                url: '/macro_cat_obbligo_dl33',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.macro_cat_obbligo_dl33.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/macro_cat_obbligo_dl33/macro_cat_obbligo_dl33s.html',
                        controller: 'Macro_cat_obbligo_dl33Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('macro_cat_obbligo_dl33');
                        return $translate.refresh();
                    }]
                }
            })
            .state('macro_cat_obbligo_dl33Detail', {
                parent: 'entity',
                url: '/macro_cat_obbligo_dl33/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.macro_cat_obbligo_dl33.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/macro_cat_obbligo_dl33/macro_cat_obbligo_dl33-detail.html',
                        controller: 'Macro_cat_obbligo_dl33DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('macro_cat_obbligo_dl33');
                        return $translate.refresh();
                    }]
                }
            });
    });
