'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('consQuartRev', {
                parent: 'entity',
                url: '/consQuartRev',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.pareriQuartRev.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/pareri_quart_rev/pareriquartrev.html',
                        controller: 'PareriQuartRevController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('pareriQuartRev');
                        return $translate.refresh();
                    }]
                }
            });
    });
