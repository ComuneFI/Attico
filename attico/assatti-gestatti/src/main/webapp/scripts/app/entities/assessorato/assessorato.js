'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('assessorato', {
                parent: 'entity',
                url: '/assessorato',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'Assessoratos'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/assessorato/assessoratos.html',
                        controller: 'AssessoratoController'
                    }
                },
                resolve: {
						translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('assessorato');
                        return $translate.refresh();
                    }]
                }
            })
            .state('assessoratoDetail', {
                parent: 'entity',
                url: '/assessorato/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'Assessorato'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/assessorato/assessorato-detail.html',
                        controller: 'AssessoratoDetailController'
                    }
                },
                resolve: {
						translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('assessorato');
                        return $translate.refresh();
                    }]
                }
            });
    });
