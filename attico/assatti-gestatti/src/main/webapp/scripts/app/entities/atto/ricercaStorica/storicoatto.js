'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('attoStorica', {
                parent: 'entity',
                url: '/atti/ricerca-storica/:tipo',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricerca.storica.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto//ricercaStorica/storicoattos.html',
                        controller: 'RicercaStoricaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('atto');
						$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
						$translatePartialLoader.addPart('beneficiario');
						$translatePartialLoader.addPart('fattura');
                        return $translate.refresh();
                    }]
                }
            })
           .state('storicoAttoDirDetail', {
                parent: 'entity',
                url: '/atti/ricerca-storica/atti-dirigenziali/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricerca.storica.detailAttoDirigenziale'
                },
                views: {
                    'content@': {
                    	templateUrl: 'scripts/app/entities/atto/ricercaStorica/storicoattodir-detail.html',
                        controller: 'StoricoAttoDirDetailController'
                    }
                },
                resolve: {
                	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                         $translatePartialLoader.addPart('atto');
 						$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
 						$translatePartialLoader.addPart('beneficiario');
                         return $translate.refresh();
                     }]
                }
            })
	        .state('storicoAttoGiuntaDetail', {
	            parent: 'entity',
	            url: '/atti/ricerca-storica/atti-giunta/:id',
	            data: {
	                roles: ['ROLE_USER'],
	                pageTitle: 'cifra2gestattiApp.atto.ricerca.storica.detailAttoGiunta'
	            },
	            views: {
	                'content@': {
	                	templateUrl: 'scripts/app/entities/atto/ricercaStorica/storicoattogiunta-detail.html',
	                    controller: 'StoricoAttoGiuntaDetailController'
	                }
	            },
	            resolve: {
	            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                     $translatePartialLoader.addPart('atto');
							$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
							$translatePartialLoader.addPart('beneficiario');
							$translatePartialLoader.addPart('fattura');
	                     return $translate.refresh();
	                 }]
	            }
	        });
    });

angular.module('cifra2gestattiApp') 
.directive('showtabstoricoatti',  function ($rootScope) {
	return {
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
				
				scope.storicoAttos = [];
				scope.storicoPage = 1;
				var annoSelected = scope.criteriaStorico.anno;
				scope.criteriaStorico = {tipiAttoCodici:scope.tipiAttoStoricoCodici,anno:annoSelected};
				scope.criteriaStorico.page = scope.storicoPage;
				scope.criteriaStorico.per_page = 10;
				scope.criteriaStorico.viewtype = $(element).attr('name');
				scope.criteriaStorico.ordinamento = "codiceCifra";
				scope.criteriaStorico.tipoOrdinamento = "desc";
				
				scope.loadAllStorico();
			});
		}
	};
});




