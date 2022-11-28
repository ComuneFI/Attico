'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('credits', {
                parent: 'site',
                url: '/guest/credits',
                data: {
                    roles: [],
                    pageTitle: 'credits.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/credits/credits.html'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
