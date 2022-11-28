'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('selezionaprofilo', {
                parent: 'site',
                url: '/selezionaprofilo',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'configuration.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/selezionaprofilo/selezionaprofilo.html',
                        controller: 'SelezionaProfiloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                       // $translatePartialLoader.addPart('selezionaprofilo');
                        return $translate.refresh();
                    }]
                }
            });
    });
