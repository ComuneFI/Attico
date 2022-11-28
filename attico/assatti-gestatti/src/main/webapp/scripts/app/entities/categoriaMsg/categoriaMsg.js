'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('categoriaMsg', {
                parent: 'entity',
                url: '/categoriaMsg',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.categoriaMsg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/categoriaMsg/categoriaMsgs.html',
                        controller: 'CategoriaMsgController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('categoriaMsg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('categoriaMsgDetail', {
                parent: 'entity',
                url: '/categoriaMsgDetail/:id',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.categoriaMsg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/categoriaMsg/categoriaMsg-detail.html',
                        controller: 'CategoriaMsgDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('categoriaMsg');
                        return $translate.refresh();
                    }]
                }
            });
    });
