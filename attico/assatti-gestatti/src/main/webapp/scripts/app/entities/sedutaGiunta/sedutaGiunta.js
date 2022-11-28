'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sedutaGiunta', {
                parent: 'entity',
                url: '/sedutaGiunta',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.sedutaGiunta.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sedutaGiunta/sedutaGiuntas.html',
                        controller: 'SedutaGiuntaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sedutaGiunta');
                        return $translate.refresh();
                    }]
                }
            })
            .state('sedutaGiuntaDetail', {
                parent: 'entity',
                url: '/gestioneSeduta/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.sedutaGiunta.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sedutaGiunta/sedutaGiunta-detail.html',
                        controller: 'SedutaGiuntaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('atto');
                    	$translatePartialLoader.addPart('documentoInformatico');
                    	$translatePartialLoader.addPart('modelloCampo');
                    	$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
                    	$translatePartialLoader.addPart('sottoscrittoreAtto');
                        $translatePartialLoader.addPart('sedutaGiunta');

                        return $translate.refresh();
                    }],
                    seduta: function(SedutaGiunta,$stateParams,$state) {
                        sessionStorage.setItem("activeTab", 'atti-trattati');
                        sessionStorage.setItem("activeTab", 'atti-inseribili');
                        /*
                        SedutaGiunta.getPrimaConvocazioneFine({sedutaId:$stateParams.id},function(result){
                        	if(result.primaConvocazioneFine != undefined){
                        		$state.go('sedutaGiuntaConsolidatiDetail', {
                					id:$stateParams.id
                				});
                        	} else  $state.go('sedutaGiuntaDetail', {
            					id:$stateParams.id
            				});
                        });
                        */
                    }
                }
            })
            .state('sedutaGiuntaConsolidatiDetail', {
                parent: 'entity',
                url: '/gestioneSedutaConsolidati/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.sedutaGiunta.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sedutaGiunta/sedutaGiunta-detail.html',
                        controller: 'SedutaGiuntaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    	$translatePartialLoader.addPart('atto');
                    	$translatePartialLoader.addPart('documentoInformatico');
                    	$translatePartialLoader.addPart('modelloCampo');
                    	$translatePartialLoader.addPart('rubricaDestinatarioEsterno');
                    	$translatePartialLoader.addPart('sottoscrittoreAtto');
                        $translatePartialLoader.addPart('sedutaGiunta');

                        return $translate.refresh();
                    }]
                }
            });
    });

angular.module('cifra2gestattiApp')
.filter('returnOdgBase', function () {
    return function (item) {
    	var list = [];
    	for(var i = 0; i<item.length; i++){
    		if(item[i].tipoOdg.id == 1 || item[i].tipoOdg.id == 2) {
	    	   list.push(item[i]);
    		}
    	}
       return list;
    	}
	})
    .filter('returnOdgSuppletivo', function () {
        return function (item) {
        	var list = [];
        	for(var i = 0; i<item.length; i++){
        		if(item[i].tipoOdg.id == 3) {
    	    	   list.push(item[i]);
        		}
        	}
           return list;
        }
    })
    .filter('returnOdgFuoriSacco', function () {
        return function (item) {
        	var list = [];
        	for(var i = 0; i<item.length; i++){
        		if(item[i].tipoOdg.id == 4) {
    	    	   list.push(item[i]);
        		}
        	}
           return list;
        };
    })
    .filter('returnResoconto', function () {
        return function (item) {
        	var list = [];
        	for(var i = 0; i<item.length; i++){
        			if(item[i].tipoOdg.id == 1 || item[i].tipoOdg.id == 2 || 
                 item[i].tipoOdg.id == 3 || item[i].tipoOdg.id == 4) {
    	    	   list.push(item[i]);
        		}
        	}
           return list;
        	}
    	});


angular.module('cifra2gestattiApp')
.directive('showtabseduta',  function () {
	return {
		link: function (scope, element, attrs) {

			element.bind('click', function(e) {
				e.preventDefault();
				scope.clearRicerca();
				$(element).tab('show');
				scope.sedutatype = $(element).attr('name');
				scope.page = 1;
				scope.loadAll();
			});
		}
	};
});

angular.module('cifra2gestattiApp').factory('sharedSedutaFactory', function($rootScope, ProfiloAccount) {
	var isOperatoreOdgGiunta = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_ODG_GIUNTA"]);
		return bool;
    };

    var isOperatoreOdgConsiglio = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_ODG_CONSIGLIO"]);
		return bool;
    };

    var isOperatoreResocontoGiunta = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_RESOCONTO_GIUNTA"]);

		return bool;
	};

	var isOperatoreResocontoConsiglio = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_RESOCONTO_CONSIGLIO"]);

		return bool;
	};

	var isSegretarioGiunta = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_SEGRETARIO_SEDUTA_GIUNTA"]);

		return bool;
	};

	var isSegretarioConsiglio = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_SEGRETARIO_SEDUTA_CONSIGLIO"]);

		return bool;
	};
	
	var isOperatoreSegreteriaGiunta = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_SEGRETERIA_GIUNTA"]);

		return bool;
	};

	var isOperatoreSegreteriaConsiglio = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_OPERATORE_SEGRETERIA_CONSIGLIO"]);

		return bool;
	};
	
	var isPubblicatoreGiunta = function(){
		var bool = false;

    	bool = hasRuolo(["ROLE_RESPONSABILE_PUBBLICAZIONE_GIUNTA"]);

		return bool;
	};
	
	var isPubblicatoreConsiglio = function(){
		var bool = false;

    	bool = hasRuolo(["ROLE_RESPONSABILE_PUBBLICAZIONE_CONSIGLIO"]);

		return bool;
	};
	
	var isPubblicatoreAD = function(){
		var bool = false;

    	bool = hasRuolo(["ROLE_RESPONSABILE_PUBBLICAZIONE_AD"]);

		return bool;
	};
	
	var isPubblicatoreOrdinanze = function(){
		var bool = false;

    	bool = hasRuolo(["ROLE_RESPONSABILE_PUBBLICAZIONE_ORD"]);

		return bool;
	};
	
	var isPubblicatoreDecreti = function(){
		var bool = false;

    	bool = hasRuolo(["ROLE_RESPONSABILE_PUBBLICAZIONE_DEC"]);

		return bool;
	};

	var isSegretarioGenerale = function(){
    	var bool = false;

    	bool = hasRuolo(["ROLE_SEGRETARIO_GENERALE"]);

		return bool;
	};

	var hasRuolo = function(ruolo) {
		var bool = false;

		if ($rootScope.profiloattivo.id != 0){
			for(var i = 0; i < $rootScope.profiloattivo.grupporuolo.hasRuoli.length; i++){
				var item = $rootScope.profiloattivo.grupporuolo.hasRuoli[i];

				for(var ii=0;ii<ruolo.length;ii++){
					var rolevo = ruolo[ii];
					if ( item.codice === rolevo ) {
						bool = true;
						break;
					}
				}
				if(bool == true){
					break;
				}
			}
		} else {
			angular.forEach($rootScope.activeprofilos, function(profilo, key){
				if ( angular.isDefined(profilo) && profilo !== null
						&&   angular.isDefined(profilo.grupporuolo)  && profilo.grupporuolo !== null
						&&   angular.isDefined(profilo.grupporuolo.hasRuoli)  && profilo.grupporuolo.hasRuoli !== null
						&& profilo.grupporuolo.hasRuoli.length > 0) {
					for(var i=0;i<profilo.grupporuolo.hasRuoli.length;i++){
						var item = profilo.grupporuolo.hasRuoli[i];

						for(var ii=0;ii<ruolo.length;ii++){
							var rolevo = ruolo[ii];
							if ( item.codice === rolevo ) {
								bool = true;
								break;
							}
						}
						if(bool == true){
							break;
						}
					}
				}
			});
		}

		return bool;
	};




	 /**
     * restituisce l'odg base della seduta selezionata
     */
    var getOdgBase = function (seduta){
    	if(seduta && seduta.odgs && seduta.odgs.length > 0){
	    	for(var i = 0; i<seduta.odgs.length; i++){
	    		if(seduta.odgs[i].tipoOdg.id == 1 || seduta.odgs[i].tipoOdg.id == 2)
	    			return seduta.odgs[i];
	    	}
    	}else{
    		return null;
    	}
    };

    return {
    		isOperatoreOdgGiunta: isOperatoreOdgGiunta,
    		isOperatoreOdgConsiglio: isOperatoreOdgConsiglio,
    		isOperatoreResocontoGiunta: isOperatoreResocontoGiunta,
    		isOperatoreResocontoConsiglio: isOperatoreResocontoConsiglio,
    		isSegretarioGiunta: isSegretarioGiunta,
    		isSegretarioConsiglio: isSegretarioConsiglio,
    		isSegretarioGenerale: isSegretarioGenerale,
    		isOperatoreSegreteriaGiunta: isOperatoreSegreteriaGiunta,
    		isOperatoreSegreteriaConsiglio: isOperatoreSegreteriaConsiglio,
    		isPubblicatoreGiunta:isPubblicatoreGiunta,
			isPubblicatoreConsiglio:isPubblicatoreConsiglio,
			isPubblicatoreAD:isPubblicatoreAD,
			isPubblicatoreOrdinanze:isPubblicatoreOrdinanze,
			isPubblicatoreDecreti:isPubblicatoreDecreti,
			hasRuolo: hasRuolo,
    		getOdgBase: getOdgBase
    }
});
