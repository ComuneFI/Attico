'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('smsReport', {
                parent: 'entity',
                url: '/smsReport',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.smsReport.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/smsReport/smsReports.html',
                        controller: 'SmsReportController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('smsReport');
                        return $translate.refresh();
                    }]
                }
            })
            .state('smsReportDetail', {
                parent: 'entity',
                url: '/smsReport/:id',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.smsReport.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/smsReport/smsReport-detail.html',
                        controller: 'SmsReportDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('smsReport');
                        return $translate.refresh();
                    }]
                }
            });
    });