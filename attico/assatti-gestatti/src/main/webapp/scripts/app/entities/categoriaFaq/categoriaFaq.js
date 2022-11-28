'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('categoriaFaq', {
                parent: 'entity',
                url: '/categoriaFaq',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.categoriaFaq.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/categoriaFaq/categoriaFaqs.html',
                        controller: 'CategoriaFaqController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('categoriaFaq');
                        return $translate.refresh();
                    }]
                }
            })
            .state('categoriaFaqDetail', {
                parent: 'entity',
                url: '/categoriaFaq/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.categoriaFaq.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/categoriaFaq/categoriaFaq-detail.html',
                        controller: 'CategoriaFaqDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('categoriaFaq');
                        return $translate.refresh();
                    }]
                }
            });
    });
