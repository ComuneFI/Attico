'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('modelloHtml', {
                parent: 'entity',
                url: '/modelloHtml',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.modelloHtml.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/modelloHtml/modelloHtmls.html',
                        controller: 'ModelloHtmlController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('modelloHtml');
                        return $translate.refresh();
                    }]
                }
            })
            .state('modelloHtmlDetail', {
                parent: 'entity',
                url: '/modelloHtml/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.modelloHtml.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/modelloHtml/modelloHtml-detail.html',
                        controller: 'ModelloHtmlDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('modelloHtml');
                        return $translate.refresh();
                    }]
                }
            });
    });
