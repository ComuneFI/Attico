'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news', {
                parent: 'entity',
                url: '/news',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.news.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/news/newss.html',
                        controller: 'NewsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            })
            .state('newsDetail', {
                parent: 'entity',
                url: '/news/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'News'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/news/news-detail.html',
                        controller: 'NewsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            })
            .state('newsReport', {
                parent: 'entity',
                url: '/newsReport',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'Report Notifiche'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/news/newsReport.html',
                        controller: 'NewsReportController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            });
        
    });
