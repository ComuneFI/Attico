'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('configurazioneTask', {
                parent: 'entity',
                url: '/configurazioneTask',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.configurazioneTask.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/configurazioneTask/configurazioneTasks.html',
                        controller: 'ConfigurazioneTaskController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('configurazioneTask');
                        return $translate.refresh();
                    }]
                }
            });
    });
