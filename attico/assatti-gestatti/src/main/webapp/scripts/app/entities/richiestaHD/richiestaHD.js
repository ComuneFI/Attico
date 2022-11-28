'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('richiestaHD', {
                parent: 'entity',
                url: '/richiestaHD',
                data: {
                	roles: ['ROLE_ADMIN'],
                    pageTitle: 'cifra2gestattiApp.richiestaHD.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/richiestaHD/richiestaHDs.html',
                        controller: 'RichiestaHDController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('richiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('richiestaHDDetail', {
                parent: 'entity',
                url: '/richiestaHDDetail/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.richiestaHD.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/richiestaHD/richiestaHD-detail.html',
                        controller: 'RichiestaHDDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('richiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('richiestaHDU', {
                parent: 'entity',
                url: '/richiestaHDU',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.richiestaHD.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/richiestaHD/richiestaHDs.html',
                        controller: 'RichiestaHDUserController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('richiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('richiesteDirigente', {
                parent: 'entity',
                url: '/richiesteDirigente',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.richiestaHD.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/richiestaHD/richiestaHDs.html',
                        controller: 'RichiestaDirigenteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('richiestaHD');
                        return $translate.refresh();
                    }]
                }
            })
            .state('richiestaDirigenteDetail', {
                parent: 'entity',
                url: '/richiestaDirigenteDetail/:id',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.richiestaHD.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/richiestaHD/richiestaHD-detail.html',
                        controller: 'RichiestaDirigenteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('richiestaHD');
                        return $translate.refresh();
                    }]
                }
            });
    });
