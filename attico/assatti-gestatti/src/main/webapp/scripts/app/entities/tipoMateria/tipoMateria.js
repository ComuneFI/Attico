'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tipoMateria', {
                parent: 'entity',
                url: '/tipoMateria',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoMateria.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoMateria/tipoMaterias.html',
                        controller: 'TipoMateriaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoMateria');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tipoMateriaDetail', {
                parent: 'entity',
                url: '/tipoMateria/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.tipoMateria.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tipoMateria/tipoMateria-detail.html',
                        controller: 'TipoMateriaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tipoMateria');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });


angular.module('cifra2gestattiApp').
  directive('tipoMateriaFormGroup', function( $log ) {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
         scope: {
            ngModel : '=',
            tipomaterias: '=',
            ngChange: '&',
         },
         templateUrl: './scripts/app/entities/tipoMateria/tipoMateria-formgroup.html',
         link: function postLink(scope, element, attrs){
            scope.updateTipoMateria = function(item){
                $log.debug(item);
                 //scope.materias = item.materie;
                 //scope.ngChange();
                }
         }
       
    };
  });
