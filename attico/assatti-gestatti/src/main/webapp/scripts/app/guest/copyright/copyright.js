'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('copyright', {
                parent: 'site',
                url: '/guest/copyright',
                data: {
                    roles: [],
                    pageTitle: 'copyright.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/copyright/copyright.html'
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
