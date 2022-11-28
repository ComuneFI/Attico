'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('versioneComposizioneGiunta', {
                parent: 'entity',
                url: '/versioneComposizioneGiunta',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.versioneComposizioneGiunta.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/versioneComposizioneGiunta/versioneComposizioneGiuntas.html',
                        controller: 'VersioneComposizioneGiuntaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('versioneComposizioneGiunta');
                        return $translate.refresh();
                    }]
                }
            })
            .state('versioneComposizioneGiuntaDetail', {
                parent: 'entity',
                url: '/versioneComposizioneGiunta/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.versioneComposizioneGiunta.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/versioneComposizioneGiunta/versioneComposizioneGiunta-detail.html',
                        controller: 'VersioneComposizioneGiuntaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('versioneComposizioneGiunta');
                        return $translate.refresh();
                    }]
                }
            });
    });
