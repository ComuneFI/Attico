'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('profilo', {
                parent: 'entity',
                url: '/profilo',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.profilo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/profilo/profilos.html',
                        controller: 'ProfiloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('profilo');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('profiloDetail', {
                parent: 'entity',
                url: '/profilo/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    customRoles:['ROLE_REFERENTE_TECNICO'],
                    pageTitle: 'cifra2gestattiApp.profilo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/profilo/profilo-detail.html',
                        controller: 'ProfiloDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('profilo');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });


angular.module('cifra2gestattiApp').
  directive('profiloForm', function( $log ) {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled : '=',
            disabledAoo : '=',
            listaAoo : '=',
            listaAtto : '=',
            listaQualificaprofessionale : '=',
            listaGrupporuolo : '=',
            listaUtente : '=',
            ngChange: '&',
        },
         templateUrl: './scripts/app/entities/profilo/profilo-form.html',
         link: function postLink(scope, element, attrs){
                scope.updateGruppoModel = function(item, utente)
                {
                     $log.debug(utente);
                     //$log.debug(scope.ngModel);
                     if(item != undefined && item.denominazione !=undefined){
                    	 scope.ngModel.descrizione = item.denominazione;
                     }
                     scope.ngChange();
                }
           }
       
    };
  });
