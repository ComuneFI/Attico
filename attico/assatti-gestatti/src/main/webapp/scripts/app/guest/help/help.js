'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('help', {
                parent: 'site',
                url: '/guest/help',
                data: {
                    roles: [],
                    pageTitle: 'global.menu.help'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/help/help-public.html'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('faq');
                        return $translate.refresh();
                    }]
                }
            });
    });
