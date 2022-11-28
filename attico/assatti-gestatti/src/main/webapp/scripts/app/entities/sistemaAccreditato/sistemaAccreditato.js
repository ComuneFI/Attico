'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sistemaAccreditato', {
                parent: 'entity',
                url: '/sistemaAccreditato',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.sistemaAccreditato.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sistemaAccreditato/sistemaAccreditatos.html',
                        controller: 'SistemaAccreditatoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sistemaAccreditato');
                        return $translate.refresh();
                    }]
                }
            })
    });
