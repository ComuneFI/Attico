'use strict';

angular.module('cifra2gestattiApp')
    .controller('SelezionaProfiloController', function ($scope,$rootScope, $location, $state, Principal, localStorageService, Utente,
    		$log, ProfiloAccount, Profilo, TipoMateria, News, ParseLinks, ProfiloService) {
    	
    	$scope.page = 1;
    	$scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        $scope.newss = [];
    	$scope.destinataris = [];

		if($scope.isAuthenticated && $scope.isAuthenticated() && angular.isDefined($rootScope.account) && angular.isDefined($rootScope.account.login) && (!$rootScope.activeprofilos || $rootScope.activeprofilos.length == 0)){
       		 Utente.activeprofilos( {id:$rootScope.account.login}, function(actvs){
       			$rootScope.activeprofilos = actvs;
       			if($rootScope.activeprofilos && $rootScope.activeprofilos.length > 0){
	       			Utente.getAoosDirigente({id:$rootScope.account.utente.id}, function(res){
	            		$rootScope.aoosDirigente = res;
	            	 });
       			}
       		 } );
        }

        $scope.selezionaProfilo = function( profilo ){
        	ProfiloService.selezionaProfilo(profilo);
        };
        
        //L'utente sta cambiando il profilo?
        $rootScope.cambioProfilo = false;
        
        /**
         * Seleziona il profilo mostrando un warning in base alla sezione visualizzata.
         * Questo è particolarmente utile quando si sta modificando un atto e si cambia
         * profilo. Fa riferimento diretto a INNOVCIFRA-638.
         * @param profilo Il profilo da utilizzare.
         */
        $scope.selezionaProfiloConWarning = function(profilo){
        	$log.log('$rootScope.activeExitUserConfirmation', $rootScope.activeExitUserConfirmation);
        	if($rootScope.activeExitUserConfirmation){
        		$log.debug("Bisogna confermare l'operazione prima di poter cambiare il profilo");
        		$rootScope.nuovoProfilo = profilo;
        		$rootScope.cambioProfilo = true;
        		$rootScope.$broadcast('profileChange');
        	} else {
        		localStorageService.remove('_attoSearch');
        		localStorageService.remove('_attoSearch_tipoRicerca');
        		localStorageService.remove('_attoSearch_tipoAtto');
        		
        		localStorageService.remove('_taskDesktop_taskSearch');
        		localStorageService.remove('_taskDesktop_tasktype');
        		$scope.selezionaProfilo(profilo);
        	}
        };
        
        
    	if($rootScope.profiloattivo!=null){
        	$rootScope.$on("news-"+$rootScope.profiloattivo.id, function (event, data) {
        		$scope.destinataris.unshift(data);
        	});
        }

    	$scope.decrement = function(letta){
        	if($rootScope.profiloattivo.id!=null){
	        	if(letta==0){//se non letta, decrementa di 1 
	        		$rootScope.$broadcast("news-"+$rootScope.profiloattivo.id+"-newNewss", -1);	
	        	}
        	}
        };
        $scope.loadAll = function() {
        	/*Carica notifiche in base al profilo*/
        }
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

    });
