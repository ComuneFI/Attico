'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('delega', {
                parent: 'entity',
                url: '/delega',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_OPERATORE_DELEGA', 'ROLE_OPERATORE_GESTIONE_UFFICI', 'ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.delega.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/delega/deleghe.html',
                        controller: 'DelegaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('delega');
                        return $translate.refresh();
                    }]
                }
            });
    });