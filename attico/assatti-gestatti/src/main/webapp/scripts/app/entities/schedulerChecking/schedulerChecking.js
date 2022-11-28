'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('schedulerChecking', {
                parent: 'entity',
                url: '/schedulerChecking',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'Checking Scheduler'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/schedulerChecking/schedulerChecking.html',
                        controller: 'SchedulerCheckingController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        return $translate.refresh();
                    }]
                }
    });
});