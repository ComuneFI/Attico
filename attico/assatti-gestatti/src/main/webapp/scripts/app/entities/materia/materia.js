'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('materia', {
                parent: 'entity',
                url: '/materia',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.materia.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/materia/materias.html',
                        controller: 'MateriaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('materia');
                        return $translate.refresh();
                    }]
                }
            })
            .state('materiaDetail', {
                parent: 'entity',
                url: '/materia/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.materia.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/materia/materia-detail.html',
                        controller: 'MateriaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('materia');
                        return $translate.refresh();
                    }]
                }
            });
    });
