'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('entity', {
                abstract: true,
                parent: 'site',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.entity.home.title'
                }
            });
    })
    .directive('a', function($rootScope) {
	    return {
	        restrict: 'E',
	        link: function(scope, elm, attr) {
	            elm.on('click', function($event) {
	            	if(attr && attr.target && attr.target == '_blank'){
		            	$event.preventDefault();
		            	window.open($rootScope.buildDownloadUrl(attr.href));
		            	return false;
	            	}else if(attr.href && !attr.href.startsWith("#") && attr.href.toLowerCase().indexOf('javascript') < 0){
	            		window.location.replace($rootScope.buildDownloadUrl(attr.href));
	            		return false;
	            	}else if(attr.href && attr.href == "#"){
	            		return false;
	            	}else{
	            		return true;
	            	}
	            });
	        }
	    }
	})
    .directive('scenario', function(SCENARIO_RULES, $timeout, $rootScope) {
    	
    	function link(scope, element, attrs) {
    		
    		var scenari = [];

    		$rootScope.$on('verificaDisabilitazioni',  function(event) {
    			refresh();
    		});
    		
    		scope.$watch('scenariDisabilitazione', function(newValue, oldValue) {
			  if(newValue) {
				  if(typeof newValue == 'string')
					  scenari.push(newValue);
				  else if(Array.isArray(newValue))
					  scenari = newValue;
				  refresh();					  
			  }
			});
    		
    		scope.$watch('sezioneCorrente', function(newValue, oldValue) {
  			  if(scope.scenariDisabilitazione) {
  				  if(typeof scope.scenariDisabilitazione == 'string')
  					  scenari.push(scope.scenariDisabilitazione);
  				  else if(Array.isArray(scope.scenariDisabilitazione))
  					  scenari = scope.scenariDisabilitazione;
  				  refresh();
  			  }
  			});
    		
    		// FIX PROBLEMA CAMBIO TAB GESTIONE SEDUTE
    		scope.$watch('infoTab', function(newValue, oldValue) {
    			refresh();
    		});
    		
    		var refresh = function () {
    			if (!scope.disableSpinner) {
    				scope._onSpinner=true;
    			}

    			$timeout(function () {
				  updateDOM(scenari);
				  
				  if (!scope.disableSpinner) {
	    			scope._onSpinner=false;
				  }
				  
				  // FIX PROBLEMA ABILITAZIONE CAMPI PUBBLICAZIONE
				  if (scope.atto && (scope.atto.riservato != null)) {
					  scope.atto.riservato = !scope.atto.riservato;
					  scope.$apply();
					  scope.atto.riservato = !scope.atto.riservato;
					  scope.$apply();
				  }
				  
				}, 500);
    		}
    		
    		var updateDOM = function (scenari) {
    			for(var i=0; i<scenari.length; i++) {
    				for(var y=0; y<SCENARIO_RULES.length; y++) {
    					if(scenari[i] == SCENARIO_RULES[y].code && SCENARIO_RULES[y].fields.length > 0) {
    						
    						for(var z=0; z<SCENARIO_RULES[y].fields.length; z++) {
    							var input = SCENARIO_RULES[y].fields[z];
    							if(input.not == undefined){
    								angular.element(input.id).attr(input.action,input.value);
    							}else{
    								if(!angular.element(input.id).attr('editTesto'))
    									angular.element(input.id).not(input.not).attr(input.action,input.value);
    							}
    						}
    					}
    				}
    			}
    		};
    	}
    	
    	return {
    		link: link
		};
  });
