'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reportRuntime', {
                parent: 'entity',
                url: '/reportRuntime',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.reportRuntime.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reportRuntime/reportRuntimes.html',
                        controller: 'ReportRuntimeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reportRuntime');
                        return $translate.refresh();
                    }]
                }
            })
            .state('reportRuntimeDetail', {
                parent: 'entity',
                url: '/reportRuntime/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.reportRuntime.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reportRuntime/reportRuntime-detail.html',
                        controller: 'ReportRuntimeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reportRuntime');
                        return $translate.refresh();
                    }]
                }
            });
    });
