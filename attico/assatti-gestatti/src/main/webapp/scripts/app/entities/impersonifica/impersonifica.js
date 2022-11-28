'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('impersonifica', {
                parent: 'entity',
                url: '/impersonifica',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_SUPPORT_MANAGER'],
                    pageTitle: 'cifra2gestattiApp.impersonifica.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/impersonifica/impersonifica.html',
                        controller: 'ImpersonificaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('impersonifica');
                        return $translate.refresh();
                    }]
                }
            });
    });