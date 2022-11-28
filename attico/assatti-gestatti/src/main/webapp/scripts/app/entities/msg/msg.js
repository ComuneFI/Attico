'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('msg', {
                parent: 'entity',
                url: '/msg',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.msg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/msg/msgs.html',
                        controller: 'MsgController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('msg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('msgu', {
                parent: 'entity',
                url: '/msgu',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.msg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/msg/msgs-user.html',
                        controller: 'MsgUserController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('msg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('msgDetail', {
                parent: 'entity',
                url: '/msgDetail/:id',
                data: {
                	roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.msg.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/msg/msg-detail.html',
                        controller: 'MsgDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('msg');
                        return $translate.refresh();
                    }]
                }
            });
    });
