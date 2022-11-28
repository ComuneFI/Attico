'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('fattura', {
                parent: 'entity',
                url: '/fattura',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.fattura.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/fattura/fatturas.html',
                        controller: 'FatturaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('fattura');
                        return $translate.refresh();
                    }]
                }
            })
            .state('fatturaDetail', {
                parent: 'entity',
                url: '/fattura/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.fattura.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/fattura/fattura-detail.html',
                        controller: 'FatturaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('fattura');
                        return $translate.refresh();
                    }]
                }
            });
    });
