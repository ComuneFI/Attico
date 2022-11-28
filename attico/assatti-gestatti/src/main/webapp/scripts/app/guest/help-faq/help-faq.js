'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('help-faq', {
                parent: 'site',
                url: '/guest/help-faq',
                data: {
                    roles: [],
                    pageTitle: 'global.menu.faq'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/guest/help-faq/help-faq-public.html',
                        controller: 'Help-FaqController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('faq');
                        $translatePartialLoader.addPart('categoriaFaq');
                        return $translate.refresh();
                    }]
                }
            });
    });
