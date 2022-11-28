'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('classificazione', {
                parent: 'entity',
                url: '/classificazione',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.classificazione.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/classificazione/classificaziones.html',
                        controller: 'ClassificazioneController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('classificazione');
                        return $translate.refresh();
                    }]
                }
            })
            .state('classificazioneDetail', {
                parent: 'entity',
                url: '/classificazione/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.classificazione.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/classificazione/classificazione-detail.html',
                        controller: 'ClassificazioneDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('classificazione');
                        return $translate.refresh();
                    }]
                }
            });
    });
