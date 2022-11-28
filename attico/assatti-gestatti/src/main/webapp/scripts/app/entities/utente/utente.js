'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('utente', {
                parent: 'entity',
                url: '/utente',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.utente.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/utente/utentes.html',
                        controller: 'UtenteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('utente');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('utenteDetail', {
                parent: 'entity',
                url: '/utente/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.utente.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/utente/utente-detail.html',
                        controller: 'UtenteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('utente');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('utenteMySelf', {
                parent: 'entity',
                url: '/utente/:id/my',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.utente.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/utente/utente-myself-edit.html',
                        controller: 'UtenteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('utente');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });
 
angular.module('cifra2gestattiApp').
  directive('utenteForm', function() {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled: '=',
        },
         templateUrl: './scripts/app/entities/utente/utente-form.html'
       
    };
  });