'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('monitoraggioAccesso', {
                parent: 'entity',
                url: '/monitoraggioAccesso',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.monitoraggioAccesso.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/monitoraggioAccesso/monitoraggioAccessos.html',
                        controller: 'MonitoraggioAccessoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('monitoraggioAccesso');
                        return $translate.refresh();
                    }]
                }
            });
    });
