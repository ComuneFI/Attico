'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('argomentoOdg', {
                parent: 'entity',
                url: '/argomentoOdg',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.argomentoOdg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/argomentoOdg/argomentoOdgs.html',
                        controller: 'ArgomentoOdgController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('argomentoOdg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('argomentoOdgDetail', {
                parent: 'entity',
                url: '/argomentoOdg/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.argomentoOdg.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/argomentoOdg/argomentoOdg-detail.html',
                        controller: 'ArgomentoOdgDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('argomentoOdg');
                        return $translate.refresh();
                    }]
                }
            });
    });
