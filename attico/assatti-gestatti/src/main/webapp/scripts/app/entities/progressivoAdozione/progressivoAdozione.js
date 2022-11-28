'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('progressivoAdozione', {
                parent: 'entity',
                url: '/progressivoAdozione',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.progressivoAdozione.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/progressivoAdozione/progressivoAdoziones.html',
                        controller: 'ProgressivoAdozioneController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('progressivoAdozione');
                        return $translate.refresh();
                    }]
                }
            })
            .state('progressivoAdozioneDetail', {
                parent: 'entity',
                url: '/progressivoAdozione/:id',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.progressivoAdozione.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/progressivoAdozione/progressivoAdozione-detail.html',
                        controller: 'ProgressivoAdozioneDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('progressivoAdozione');
                        return $translate.refresh();
                    }]
                }
            });
    });
