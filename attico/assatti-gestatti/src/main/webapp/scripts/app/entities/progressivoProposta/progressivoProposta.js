'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('progressivoProposta', {
                parent: 'entity',
                url: '/progressivoProposta',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.progressivoProposta.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/progressivoProposta/progressivoPropostas.html',
                        controller: 'ProgressivoPropostaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('progressivoProposta');
                        return $translate.refresh();
                    }]
                }
            })
            .state('progressivoPropostaDetail', {
                parent: 'entity',
                url: '/progressivoProposta/:id',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.progressivoProposta.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/progressivoProposta/progressivoProposta-detail.html',
                        controller: 'ProgressivoPropostaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('progressivoProposta');
                        return $translate.refresh();
                    }]
                }
            });
    });
