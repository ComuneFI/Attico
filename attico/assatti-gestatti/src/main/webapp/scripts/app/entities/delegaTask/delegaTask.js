'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('delegaTask', {
                parent: 'entity',
                url: '/delegaTask',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_OPERATORE_DELEGA', 'ROLE_OPERATORE_GESTIONE_UFFICI', 'ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.delegaTask.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/delegaTask/delegheTask.html',
                        controller: 'DelegaTaskController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('delegaTask');
                        return $translate.refresh();
                    }]
                }
            });
    });