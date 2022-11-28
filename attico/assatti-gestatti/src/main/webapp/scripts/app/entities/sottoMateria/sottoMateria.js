'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sottoMateria', {
                parent: 'entity',
                url: '/sottoMateria',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.sottoMateria.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sottoMateria/sottoMaterias.html',
                        controller: 'SottoMateriaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sottoMateria');
                        return $translate.refresh();
                    }]
                }
            })
            .state('sottoMateriaDetail', {
                parent: 'entity',
                url: '/sottoMateria/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.sottoMateria.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sottoMateria/sottoMateria-detail.html',
                        controller: 'SottoMateriaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sottoMateria');
                        return $translate.refresh();
                    }]
                }
            });
    });
