'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('rubricaBeneficiario', {
                parent: 'entity',
                url: '/rubricaBeneficiario',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.rubricaBeneficiario.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rubricaBeneficiario/rubricaBeneficiarios.html',
                        controller: 'RubricaBeneficiarioController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('rubricaBeneficiario');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('rubricaBeneficiarioDetail', {
                parent: 'entity',
                url: '/rubricaBeneficiario/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.rubricaBeneficiario.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/rubricaBeneficiario/rubricaBeneficiario-detail.html',
                        controller: 'RubricaBeneficiarioDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('rubricaBeneficiario');
                        $translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            });
    });


angular.module('cifra2gestattiApp').
  directive('rubricaBeneficiarioForm', function() {
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
         templateUrl: './scripts/app/entities/rubricaBeneficiario/rubrica-beneficiario-form.html'
    };
  });

angular.module('cifra2gestattiApp').
directive('rubricaBeneficiarioImportazione', function() {
      return {
       restrict: 'E',
       replace: true,
       transclude: true,
      scope: {
          ngModel : '=',
          ngDisabled : '=',
          aoo : '=',
      },
       templateUrl: './scripts/app/entities/rubricaBeneficiario/rubrica-beneficiario-importazione.html',
       controller:'RubricaBeneficiarioController'
  };
});
