'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('modelloCampoAdmin', {
                parent: 'entity',
                url: '/modelloCampo/:flag',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_ISTRUTTORE_PROPOSTA', 'ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.modelloCampo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/modelloCampo/modelloCamposAdmin.html',
                        controller: 'ModelloCampoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('modelloCampo');
                        $translatePartialLoader.addPart('atto');
                        return $translate.refresh();
                    }]
                }
            })
            .state('modelloCampoDetailAdmin', {
                parent: 'entity',
                url: '/modelloCampo/:flag/:id',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_ISTRUTTORE_PROPOSTA', 'ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.modelloCampo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/modelloCampo/modelloCampoAdmin-detail.html',
                        controller: 'ModelloCampoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('modelloCampo');
                        return $translate.refresh();
                    }]
                }
            });
    });
