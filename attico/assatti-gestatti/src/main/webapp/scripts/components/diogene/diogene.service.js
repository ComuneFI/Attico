'use strict';

angular.module('cifra2gestattiApp')
	.factory('Diogene', function ($resource) {
		return $resource('api/diogene/:id/:username/:action/', {}, {
			'init': { method: 'post', isArray: false, params:{action:'init' } },
			'collocazioneBase': { method: 'get', isArray: true, params:{action:'default'} },
			'titolari': { method: 'get', isArray: true, params:{action:'titolari'} },
			'titolariTree': { method: 'get', isArray: false, params:{action:'titolariTree'} },
			'vociTitolario': { method: 'get', isArray: true, params:{action:'voci'} },
			'esploraTitolario': { method: 'get', isArray: true, params:{action:'esplora'} },
			'contenutoAggregato': { method: 'get', isArray: true, params:{action:'contenuto'} },
			'generaSerie': { method: 'post', isArray: false, params:{action:'serie'} },
			'generaCollocazione': { method: 'post', isArray: false, params:{action:'collocazione'} },
			'collocazione': { method: 'GET', isArray: false, params:{action:'collocazione'} },
			'preferiti': { method: 'get', isArray: true, params:{action:'preferiti'} },
			'get': {
				method: 'GET',
				transformResponse: function (data) {
					data = angular.fromJson(data);
					return data;
				}
			},
			'update': { method:'POST', params:{action:'update' }}
		});
	});

angular.module('cifra2gestattiApp').
directive('diogeneForm', function( $log, Diogene) {
      return {
       restrict: 'E',
       replace: true,
       transclude: true,
      scope: {
          ngModel : '=',
          ngDisabled : '=',
          run:'=',
          ngChange: '&',
      },
       templateUrl: './scripts/app/entities/directiveForms/diogene-form.html',
       link: function postLink(scope, element, attrs, $timeout){
    	    scope.list = [];
    	   	scope.numSelected = 0;
    	   	scope.options = {};
    	   	scope.erroreCaricamentoTitolari = false;
		   	scope.sfogliaTitoli = function() {
				Diogene.titolariTree({}, function(result) {
					scope.list = [];
					Object.keys(result).forEach(function(key) {
						if(typeof key == 'string' && key.indexOf('$') < 0){
							var voci = result[key];
							var obj = {};
							obj.titolo = key;
							obj.vociFiglie = voci;
							scope.list.push(obj);
						}
					});
				}, function(error) {
					scope.erroreCaricamentoTitolari = true;
				});		
	   		};
	   		
	   		scope.loadSottovoci = function(item){
	   			if(!item.loading){
		   			item.loading = true;
		   			Diogene.vociTitolario({idVoce: item.idVoce, idTitolario: item.idTitolario}, function(voci){
		   				item.sottovociLoaded = true;
		   				item.vociFiglie = voci;
		   				for(var i = 0; i<item.vociFiglie.length; i++){
		   					item.vociFiglie[i].codiceTitolario = item.codiceTitolario;
		   					item.vociFiglie[i].descrizioneTitolario = item.descrizioneTitolario;
		   				}
		   				item.loading = false;
		   			}, function(error){
		   				item.loading = false;
		   				alert("Attenzione! Il sistema documentale non risulta raggiungibile. Riprovare.");
		   			});
	   			}
	   		};
	   		
	   		scope.expandNode = function(n,$event) {
                $event.stopPropagation();
                n.toggle('slow');
            };
            
            scope.loadTitoli = function(){
            	scope.erroreCaricamentoTitolari = false;
            	scope.list = [];
   				scope.sfogliaTitoli();
   				scope.run = false;
            };
	   		
	   		scope.$watch("run", function(run){
	   			if(run){
	   				scope.loadTitoli();
	   			}
	   		});
	   		
	   		scope.voceChanged = function(voceSelezionata){
	   			scope.ngModel = voceSelezionata;
	   			scope.ngChange();
	   		};
	   		
		   	 scope.$watch('searchValue',function(nv) {
	             if(!nv && nv !== '') {
	                 return;
	             }
	             var previousDataset = angular.copy(scope.list);
	             var newData = (scope.searchValue === '') ? angular.copy(scope.list) : [scope.treeSearch(angular.copy(scope.list[0]),scope.searchValue)];
	             
	             if(newData.length === 1 && angular.equals({}, newData[0]) ) {
	               scope.emptyData = true;
	               return;
	             }
	             
	             scope.emptyData = false;
	             if(angular.equals(previousDataset,newData)) {
	               clearTimeout(inputTimeout);
	               return;
	             } 
	             
	             scope.list = newData;
	             
	             
	             $timeout.cancel(inputTimeout);
	             inputTimeout = $timeout(function() {
	                 
	                 var els = document.querySelectorAll('[ui-tree-node]');
	                 
	                 Array.prototype.forEach.call(els,function(el) {
	                     el = angular.element(el);
	                     var elScope = el.scope();
	                     if(elScope.$modelValue.match) {
	                         
	                         elScope.expand();
	                         
	                         //loop through all parents and keep expanding until no more parents are found
	                         var p = elScope.$parentNodeScope;
	                         while(p) {
	                           p.expand();
	                           p = p.$parentNodeScope;
	                           
	                         }
	                     }
	                 });
	             },500);
	         });
       }
  };
});

