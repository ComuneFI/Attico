'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('atto', {
                parent: 'entity',
                url: '/atto',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/attos.html',
                        controller: 'AttoController'
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
            .state('attoLibera', {
                parent: 'entity',
                url: '/atti/ricerca/libera',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricerca.libera'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/ricercalibera.html',
                        controller: 'AttoRicercaController'
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
            .state('attoList', {
                parent: 'entity',
                url: '/ricerca/:tipo/:atto',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricercatematica.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/attos.html',
                        controller: 'AttoController'
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
            .state('attoGroupedSearch', {
                parent: 'entity',
                url: '/grouped-search/:anno/:atto',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricercatematica.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/groupedsearch.html',
                        controller: 'AttoGroupedSearchController'
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
            .state('attoStoricaList', {
                parent: 'entity',
                url: '/atti/:tipo/:atto',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.ricercatematica.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/ricercaStorica/storicoattos.html',
                        controller: 'AttoController'
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
            .state('clonaAtto', {
                parent: 'entity',
                url: '/atto/clonazione/',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.clonazione'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/clonazione.html',
                        controller: 'AttoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('atto');
                        $translatePartialLoader.addPart('beneficiario');
						$translatePartialLoader.addPart('fattura');
                        return $translate.refresh();
                    }]
                }
            })
            .state('attoDetail', {
                parent: 'entity',
                url: '/atto/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/atto-detail.html',
                        controller: 'AttoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('atto');
                        $translatePartialLoader.addPart('sottoscrittoreAtto');
                        $translatePartialLoader.addPart('documentoInformatico');
                        $translatePartialLoader.addPart('modelloCampo');
						$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
						$translatePartialLoader.addPart('aoo');
						$translatePartialLoader.addPart('destinatarioInterno');
						$translatePartialLoader.addPart('beneficiario');
						$translatePartialLoader.addPart('fattura');
						$translatePartialLoader.addPart('rubricaBeneficiario');
						$translatePartialLoader.addPart('sedutaGiunta');
                        return $translate.refresh();
                    }]
                }
            })
            .state('attoDetailSection', {
                parent: 'entity',
                url: '/atto/:id/:section/:taskBpmId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/atto/atto-detail.html',
                        controller: 'AttoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('atto');
                        $translatePartialLoader.addPart('sottoscrittoreAtto');
                        $translatePartialLoader.addPart('documentoInformatico');
                        $translatePartialLoader.addPart('modelloCampo');
						$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
						$translatePartialLoader.addPart('aoo');
						$translatePartialLoader.addPart('destinatarioInterno');
						$translatePartialLoader.addPart('beneficiario');
						$translatePartialLoader.addPart('fattura');
						$translatePartialLoader.addPart('rubricaBeneficiario');
						$translatePartialLoader.addPart('sedutaGiunta');
                        return $translate.refresh();
                    }]
                }
            })
    });

/*
angular.module('cifra2gestattiApp').
  directive('attoDirigenzialeForm', function() {
        return {
         restrict: 'E',
         replace: true,
          transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled: '='
        },
         templateUrl: './scripts/app/entities/atto/atto-dirigenziale-form.html'
       
    };
  });
*/

angular.module('cifra2gestattiApp')
.directive('includeReplace', function () {
    return {
        require: 'ngInclude',
        restrict: 'A', /* optional */
        link: function (scope, el, attrs) {
            el.replaceWith(el.children());
        }
    };
});

angular.module('cifra2gestattiApp').
directive('ricercaAttoForm', function() {
      return {
       restrict: 'E',
       replace: true,
        transclude: true,
      scope: {
          ngModel : '='
      },
      controller: 'AttoController',
      templateUrl: 'scripts/app/entities/atto/ricerca-atto-modal.html'
  };
});
angular.module('cifra2gestattiApp').
directive('ricercaAttoProvvisorioForm', function() {
      return {
       restrict: 'E',
       replace: true,
        transclude: true,
      scope: {
          ngModel : '=',
          provvisorio : '=provvisorio'
      },
      controller: 'AttoController',
      templateUrl: 'scripts/app/entities/atto/ricerca-atto-modal.html'
  };
});

angular.module('cifra2gestattiApp').
directive('attoRevocatoForm', function() {
	
	function link(scope, element, attrs) {
	}
	
	return {
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
	          ngModel : '=',
	          ngDisabled: '='
	      },
		controller: 'AttoRevocatoController',
		link: link,
		templateUrl: 'scripts/app/entities/atto/atto-revocato.html'
	};
});

angular.module('cifra2gestattiApp').
directive('nuovoFascicoloForm', function() {
	
	function link(scope, element, attrs) {
		scope.nuovoFascicolo = {};
	}
	
	return {
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: false,
		/*controller: 'AttoDetailController', necessario? provoca doppio caricamento del controller. */
		link: link,
		templateUrl: 'scripts/app/entities/atto/nuovo-fascicolo-modal.html'
	};
});

angular.module('cifra2gestattiApp').
directive('nuovoFascicoloDeliberaForm', function() {
	
	function link(scope, element, attrs) {
		scope.nuovoFascicolo = {};
	}
	
	return {
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: false,
		controller: 'AttoDeliberaDetailController',
		link: link,
		templateUrl: 'scripts/app/entities/atto/nuovo-fascicolo-modal.html'
	};
});

angular.module('cifra2gestattiApp').
directive('fatturaForm', function() {
      return {
       restrict: 'E',
       replace: true,
       transclude: true,
      scope: {
          ngModel : '=',
          ngDisabled: '='
      },
       templateUrl: 'scripts/app/entities/atto/sezioniatto/fattura-form.html'     
  };
})


angular.module('cifra2gestattiApp').filter('getAttoValue', function($filter){
    
    return function(items, name){
    	const regex = /^\d{4}[.\/-]\d{2}[.\/-]\d{2}$/g;
    	const regexTime = /^\d{4}[.\/-]\d{2}[.\/-]\d{2}[T]\d{2}[:]\d{2}[:]\d{2}[Z]$/g;
    	var split = name.split(".");
    	var temp = "";
    	angular.forEach(split,function(id,key){
    		if(id != "atto" && temp == ""){
    			if(items != null){
    				temp = items[id];
    			}
    		}
    		else if(id != "atto"){
    			if(temp != null && temp != ""){
    				temp = temp[id];
    			}
    		}
    	});
        
    	try{
    		if(name == 'atto.sedutaGiunta.tipoSeduta'){
    			if(temp != null && temp == 1){
    				temp = 'Ordinaria';
    			} else if(temp != null){
    				temp = 'Straordinaria';
    			}
    			
    		} else {
    			if(temp != null && temp != true && temp != false && temp.match(regex) != null){
            		temp = $filter('date')(new Date(temp), 'dd/MM/yyyy'); 
            	}
        		
        		if(temp != null && temp != true && temp != false && temp.match(regexTime) != null){
            		temp = $filter('date')(new Date(temp), 'dd/MM/yyyy HH:mm:ss', 'Europe/Berlin'); 
            	}
        		
        		if(typeof(temp) === "boolean" && temp == true){
        			temp = 'Si';
        		}
        		
        		if(typeof(temp) === "boolean" && temp == false){
        			temp = 'No';
        		}
    		}
    		
    	} catch(err){
    		
    	}
    	
        return temp;
    };
});




