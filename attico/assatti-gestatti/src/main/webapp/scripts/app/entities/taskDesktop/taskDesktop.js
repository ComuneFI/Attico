'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('taskDesktop', {
                parent: 'entity',
                url: '/taskDesktop',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Scrivania'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/taskDesktop/taskDesktops.html',
                        controller: 'TaskDesktopController'
                    }
                },
                resolve: {
               	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sedutaGiunta');
                        return $translate.refresh();
                    }]
               }
            })
    });
