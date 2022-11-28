'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('configurazioneRiversamento', {
                parent: 'entity',
                url: '/configurazioneRiversamento',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.configurazioneRiversamento.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/configurazioneRiversamento/configurazioneRiversamentos.html',
                        controller: 'ConfigurazioneRiversamentoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('configurazioneRiversamento');
                        return $translate.refresh();
                    }]
                }
            });
    });
