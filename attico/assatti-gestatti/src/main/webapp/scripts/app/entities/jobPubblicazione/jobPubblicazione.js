'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('jobPubblicazione', {
                parent: 'entity',
                url: '/jobPubblicazione',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'global.menu.monitoraggioPubblicazioni.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/jobPubblicazione/jobPubblicaziones.html',
                        controller: 'JobPubblicazioneController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('jobPubblicazione');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('jobPubblicazioneDetail', {
                parent: 'entity',
                url: '/jobPubblicazione/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'global.menu.monitoraggioPubblicazioni.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/jobPubblicazione/jobPubblicazione-detail.html',
                        controller: 'JobPubblicazioneDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('jobPubblicazione');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });

