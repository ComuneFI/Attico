'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('messaggio', {
                parent: 'entity',
                url: '/messaggio',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.messaggio.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/messaggio/messaggios.html',
                        controller: 'MessaggioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('messaggio');
                        return $translate.refresh();
                    }]
                }
            });
        
    });
