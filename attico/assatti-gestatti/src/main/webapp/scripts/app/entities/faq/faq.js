'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('faq', {
                parent: 'entity',
                url: '/faq',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.faq.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/faq/faqs.html',
                        controller: 'FaqController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('faq');
                        return $translate.refresh();
                    }]
                }
            });
    });
