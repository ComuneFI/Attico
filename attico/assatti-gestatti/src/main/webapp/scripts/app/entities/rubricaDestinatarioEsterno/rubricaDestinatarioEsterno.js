'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rubricaDestinatarioEsterno', {
                parent: 'entity',
                url: '/rubricaDestinatarioEsterno',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.rubricaDestinatarioEsterno.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rubricaDestinatarioEsterno/rubricaDestinatarioEsternos.html',
                        controller: 'RubricaDestinatarioEsternoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('rubricaDestinatarioEsterno');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('rubricaDestinatarioEsternoDetail', {
                parent: 'entity',
                url: '/rubricaDestinatarioEsterno/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.rubricaDestinatarioEsterno.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rubricaDestinatarioEsterno/rubricaDestinatarioEsterno-detail.html',
                        controller: 'RubricaDestinatarioEsternoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('rubricaDestinatarioEsterno');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });


angular.module('cifra2gestattiApp').
  directive('rubricaDestinatarioEsternoForm', function() {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled : '=',
            listaAoo : '=',
            disabledAoo : '=',
            hideAoo : '=?',
        },
         templateUrl: './scripts/app/entities/rubricaDestinatarioEsterno/rubrica-destinatario-esterno-form.html'
       
    };
  });

angular.module('cifra2gestattiApp').
directive('rubricaDestinatarioEsternoImportazione', function() {
	return {
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			ngModel : '=',
			ngDisabled : '=',
			aoo : '=',
		},
		templateUrl: './scripts/app/entities/rubricaDestinatarioEsterno/rubrica-destinatario-esterno-importazione.html',
		controller:'RubricaDestinatarioEsternoController'
	};
});
