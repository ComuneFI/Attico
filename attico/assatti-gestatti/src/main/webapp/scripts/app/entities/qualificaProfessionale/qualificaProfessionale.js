'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('qualificaProfessionale', {
                parent: 'entity',
                url: '/qualificaProfessionale',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.qualificaProfessionale.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/qualificaProfessionale/qualificaProfessionales.html',
                        controller: 'QualificaProfessionaleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('qualificaProfessionale');
                        return $translate.refresh();
                    }]
                }
            })
            .state('qualificaProfessionaleDetail', {
                parent: 'entity',
                url: '/qualificaProfessionale/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.qualificaProfessionale.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/qualificaProfessionale/qualificaProfessionale-detail.html',
                        controller: 'QualificaProfessionaleDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('qualificaProfessionale');
                        return $translate.refresh();
                    }]
                }
            });
    });
