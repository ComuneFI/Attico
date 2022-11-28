'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('aoo', {
                parent: 'entity',
                url: '/aoo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoos.html',
                        controller: 'AooController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('aooDetail', {
                parent: 'entity',
                url: '/aoo/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-detail.html',
                        controller: 'AooDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('aooAssociaTipoMateria', {
                parent: 'entity',
                url: '/aoo/:id/associaTipoMateria',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaTipoMateria.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-tipo-materia.html',
                        controller: 'AooAssociaTipoMateriaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('tipoMateria');
                        $translatePartialLoader.addPart('materia');
                        $translatePartialLoader.addPart('sottoMateria');
                        return $translate.refresh();
                    }]
                }
            })
            .state('aooQualificaProfessionale', {
                parent: 'entity',
                url: '/aoo/:id/associaQualificaProfessionale',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaQualificaProfessionale.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-qualificaprofessionale.html',
                        controller: 'AooQualificaProfessionaleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('qualificaProfessionale');
                        return $translate.refresh();
                    }]
                }
            })
            .state('aooAssociaUfficio', {
                parent: 'entity',
                url: '/aoo/:id/associaUfficio',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaUfficio.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-ufficio.html',
                        controller: 'AooAssociaUfficioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('ufficio');

                        return $translate.refresh();
                    }]
                }
            })
            .state('aooAssociaProfilo', {
                parent: 'entity',
                url: '/aoo/:id/associaProfilo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaProfilo.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-profilo.html',
                        controller: 'AooAssociaProfiloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('profilo');
                        $translatePartialLoader.addPart('utente');

                        return $translate.refresh();
                    }]
                }
            })
             .state('aooAssociaProfiloUtente', {
                parent: 'entity',
                url: '/aoo/:id/associaProfilo/:utenteId',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaProfilo.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-profilo.html',
                        controller: 'AooAssociaProfiloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('profilo');
                        $translatePartialLoader.addPart('utente');

                        return $translate.refresh();
                    }]
                }
            })
            .state('aooAssociaGruppoRuolo', {
                parent: 'entity',
                url: '/aoo/:id/associaGruppoRuolo',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaGruppoRuolo.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-gruppo-ruolo.html',
                        controller: 'AooAssociaGruppoRuoloController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('gruppoRuolo');

                        return $translate.refresh();
                    }]
                }
            })
            .state('associaRubricaDestinatarioEsterno', {
                parent: 'entity',
                url: '/aoo/:id/associaRubricaDestinatarioEsterno',
                data: {
                    roles: ['ROLE_ADMIN'],
                    customRoles:['ROLE_OPERATORE_GESTIONE_UFFICI'],
                    pageTitle: 'cifra2gestattiApp.aoo.associaRubricaDestinatarioEsterno.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/aoo/aoo-associa-rubrica-destinatario-esterno.html',
                        controller: 'AooAssociaRubricaDestinatarioEsternoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('aoo');
                        $translatePartialLoader.addPart('rubricaDestinatarioEsterno');

                        return $translate.refresh();
                    }]
                }
            });

    });

angular.module('cifra2gestattiApp').
  directive('aooInfo', function() {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
        },
         templateUrl: './scripts/app/entities/aoo/aoo-info.html'
       
    };
  });

angular.module('cifra2gestattiApp').
  directive('aooForm', function() {
        return {
         restrict: 'E',
         replace: true,
          transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled: '='
        },
         templateUrl: './scripts/app/entities/aoo/aoo-form.html'
       
    };
  });


angular.module('cifra2gestattiApp').
  directive('aooSelect', function( $log ) {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            listaAoo : '=',
            ngChange: '&',
            aooVar: '&',
            newEntity: '=',
            notRequired: '@',
            isUnlabeled:'=',
            widthoutDiv:'=',
            aooSelectInherit:"="
        },
         templateUrl: './scripts/app/entities/aoo/aoo-select.html' 
       
    };
  });
