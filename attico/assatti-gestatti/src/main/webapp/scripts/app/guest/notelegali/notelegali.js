'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('notelegali', {
                parent: 'site',
                url: '/guest/notelegali',
                data: {
                    roles: [],
                    pageTitle: 'notelegali.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/notelegali/notelegali.html'
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
