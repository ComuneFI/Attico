'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('diogeneReport', {
                parent: 'entity',
                url: '/diogeneReport',
                data: {
                	roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.diogeneReport.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/diogeneReport/diogeneReports.html',
                        controller: 'DiogeneReportController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('diogeneReport');
                        return $translate.refresh();
                    }]
                }
            });
    });
