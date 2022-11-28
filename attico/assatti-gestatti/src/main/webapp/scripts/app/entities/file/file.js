'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('file', {
                parent: 'entity',
                url: '/file',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.file.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/file/files.html',
                        controller: 'FileController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('file');
                        return $translate.refresh();
                    }]
                }
            })
            .state('fileDetail', {
                parent: 'entity',
                url: '/file/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.file.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/file/file-detail.html',
                        controller: 'FileDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('file');
                        return $translate.refresh();
                    }]
                }
            });
    });
