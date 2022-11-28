'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('privacy', {
                parent: 'site',
                url: '/guest/privacy',
                data: {
                    roles: [],
                    pageTitle: 'privacy.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/privacy/privacy.html'
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
