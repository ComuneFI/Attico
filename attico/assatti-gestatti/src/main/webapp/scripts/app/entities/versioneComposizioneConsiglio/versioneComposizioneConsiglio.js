'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('versioneComposizioneConsiglio', {
                parent: 'entity',
                url: '/versioneComposizioneConsiglio',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.versioneComposizioneConsiglio.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/versioneComposizioneConsiglio/versioneComposizioneConsiglios.html',
                        controller: 'VersioneComposizioneConsiglioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('versioneComposizioneConsiglio');
                        return $translate.refresh();
                    }]
                }
            })
            .state('versioneComposizioneConsiglioDetail', {
                parent: 'entity',
                url: '/versioneComposizioneConsiglio/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.versioneComposizioneConsiglio.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/versioneComposizioneConsiglio/versioneComposizioneConsiglio-detail.html',
                        controller: 'VersioneComposizioneConsiglioDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('versioneComposizioneConsiglio');
                        return $translate.refresh();
                    }]
                }
            });
    });
