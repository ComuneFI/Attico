'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('import', {
                parent: 'account',
                url: '/import',
                data: {
                    roles: [],
                    pageTitle: 'import.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/import/import.html',
                        controller: 'ImportController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('import');
                        $translatePartialLoader.addPart('utente');
                        return $translate.refresh();
                    }]
                }
            });
    });
