'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('composizioneGiunta', {
                parent: 'entity',
                url: '/composizioneGiunta',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.composizioneGiunta.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/composizioneGiunta/composizioneGiuntas.html',
                        controller: 'ComposizioneGiuntaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('composizioneGiunta');
                    	$translatePartialLoader.addPart('profilo');
                        return $translate.refresh();
                    }]
                }
            });
    });
