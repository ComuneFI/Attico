'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('composizioneConsiglio', {
                parent: 'entity',
                url: '/composizioneConsiglio',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.composizioneConsiglio.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/composizioneConsiglio/composizioneConsiglios.html',
                        controller: 'ComposizioneConsiglioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('composizioneConsiglio');
                    	$translatePartialLoader.addPart('profilo');
                        return $translate.refresh();
                    }]
                }
            });
    });
