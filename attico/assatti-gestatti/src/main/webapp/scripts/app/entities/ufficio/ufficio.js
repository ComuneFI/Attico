'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ufficio', {
                parent: 'entity',
                url: '/ufficio',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.ufficio.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ufficio/ufficios.html',
                        controller: 'UfficioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ufficio');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ufficioDetail', {
                parent: 'entity',
                url: '/ufficio/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.ufficio.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ufficio/ufficio-detail.html',
                        controller: 'UfficioDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ufficio');
                        return $translate.refresh();
                    }]
                }
            });
    });



angular.module('cifra2gestattiApp').
  directive('ufficioForm', function() {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled : '=',
            disabledAoo : '=',
            listaAoo : '=',
            listaProfilo : '=',
        },
         templateUrl: './scripts/app/entities/ufficio/ufficio-form.html',
         link: function(scope) {

             scope.loadProfilos = function(aooId) {
            	 scope.loadFn(aooId);
             }
             
             scope.$watch("ngModel", function() {
                if(scope.ngModel && scope.ngModel.aoo) {
                	scope.loadFn(scope.ngModel.aoo.id);                	
                }
             });
             
             scope.loadFn = function(aooId) {
            	 scope.filterProfilo = [];            		 
            	 if(aooId && scope.listaProfilo) {
            		 for(var i=0; i<scope.listaProfilo.length; i++) {
            			 if(scope.listaProfilo[i].aoo && scope.listaProfilo[i].aoo.id == aooId)
            				 scope.filterProfilo.push(scope.listaProfilo[i]);
            		 }
            	 } 
             }
         }
       
    };
  });
