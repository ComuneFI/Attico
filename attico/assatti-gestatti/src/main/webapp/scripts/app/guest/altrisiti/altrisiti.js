'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('altrisiti', {
                parent: 'site',
                url: '/guest/altrisiti',
                data: {
                    roles: [],
                    pageTitle: 'altrisiti.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/altrisiti/altrisiti.html'
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
