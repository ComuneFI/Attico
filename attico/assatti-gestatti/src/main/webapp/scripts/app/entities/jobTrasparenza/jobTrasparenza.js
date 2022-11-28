'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('jobTrasparenza', {
                parent: 'entity',
                url: '/jobTrasparenza',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'global.menu.monitoraggioPubblicazioni.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/jobTrasparenza/jobTrasparenzas.html',
                        controller: 'JobTrasparenzaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('jobTrasparenza');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('jobTrasparenzaDetail', {
                parent: 'entity',
                url: '/jobTrasparenza/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'global.menu.monitoraggioPubblicazioni.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/jobTrasparenza/jobTrasparenza-detail.html',
                        controller: 'JobTrasparenzaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('jobTrasparenza');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });

