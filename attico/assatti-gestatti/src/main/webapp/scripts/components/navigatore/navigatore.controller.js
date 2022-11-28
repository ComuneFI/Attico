'use strict';

angular.module('cifra2gestattiApp')
    .controller('NavigatoreController', function ($scope, $rootScope, $location, $state, Auth, Principal, ProfiloAccount, Utente, localStorageService,$log,InstantMessage, ngToast) {
        
    	$scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        $scope.tokenUpdating = false;
        
        $scope.aooAbilitata = function(){
        	var abilitata = true;
        	if($rootScope.profiloattivo && $rootScope.profiloattivo.id && $rootScope.activeprofilos){
        		for(var i = 0; i< $rootScope.activeprofilos.length; i++){
        			if($rootScope.activeprofilos[i].id == $rootScope.profiloattivo.id){
        				if($rootScope.activeprofilos[i].aoo.validita.validoal){
        					abilitata = false;
        					break;
        				}
        			}
        		}
        	}
        	return abilitata;
        };
        
        $scope.impersonificazioneAttiva = function () {
        	return ($rootScope.profiloOrigine != null);
        };
        
        $scope.stopImpersonifica = function () {
        	if ($rootScope.profiloOrigine) {
        		$rootScope.activeprofilos = $rootScope.profiliAttiviOrigine;
        		$rootScope.activeprofilos.$resolved = true;
        		var tempProfilo = $rootScope.profiloOrigine;
        		$rootScope.profiloOrigine = null;
        		$rootScope.profiliAttiviOrigine = null;
        		ProfiloAccount.setProfiloOrigine(null, null);
        		
        		var account = $rootScope.account;
            	account.email = tempProfilo.utente.email;
            	account.login = tempProfilo.utente.username;
            	account.utente = tempProfilo.utente;
            	
            	ProfiloAccount.setAccount(account);
        		
        		if($rootScope.activeprofilos){
    	        	for(var i = 0; i<$rootScope.activeprofilos.length; i++){
    	        		if ($rootScope.activeprofilos[i].id == $rootScope.profiloattivo.id) {
    	        			$rootScope.activeprofilos.splice(i,1);
    	        		}
    	        	}
            	}

            	ProfiloAccount.setProfilo(tempProfilo);
                Utente.getAoosDirigente({id:tempProfilo.utente.id}, function(res){
            		$rootScope.aoosDirigente = res;
            	});
                Principal.identity(true).then(function(){
            		$state.go("selezionaprofilo", {}, {reload: true});
            	});
        	}
        };
        
        $scope.clientTimeoutSession = 36000000; //millisecondi (10 ore)
        if($rootScope.getCookie("myloggin") == ""){
//    		Auth.logout();
        	Principal.authenticate(null);
            delete $rootScope.profiloattivo;
            $rootScope.$broadcast('LogoutEvent');
    	}
        
        $scope.refreshLogin = function () {
            Auth.refresh(function () {
                $scope.authenticationError = false;
                $rootScope.$broadcast('refresh-token');
                /*$scope.tokenUpdating = false;*/
            }).catch(function () {
                $scope.authenticationError = true;
                /*$scope.tokenUpdating = false;*/
                $scope.refreshLogin();
            });
        };
        
        $scope.initTipiAttoTab = function(tipiAttoCodici){
        	var tipiAttoIds = [];
            if(angular.isDefined($rootScope.profiloattivo) && $rootScope.profiloattivo != null && $rootScope.profiloattivo.id != '0' && $rootScope.profiloattivo.id != 0){
        		for(var i = 0; i<$rootScope.profiloattivo.tipiAtto.length; i++){
        			if(tipiAttoCodici != 'sedute' && tipiAttoCodici.indexOf($rootScope.profiloattivo.tipiAtto[i].codice) > -1){
        				tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
        			}else if(tipiAttoCodici == 'sedute'){
        				tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
        			}
        		}
            }
            if( tipiAttoIds.length > 0)
            	return true;
            else
            	return false;
        };
        
        document.body.addEventListener("click", function(){
        	$rootScope.forceCountdown = false;
        	if($scope.tokenUpdating == false && $rootScope.counterTimeAuth < $scope.clientTimeoutSession-1000 ){
        		$scope.tokenUpdating = true;
	        	setTimeout(function(){
		        	var token = localStorageService.get('token');
		        	if(token != undefined){
			        	token.expires_at = new Date().getTime() + $scope.clientTimeoutSession;
			        	localStorageService.set('token', token);
		        	}
		        	$scope.tokenUpdating = false;
	        	}, 1000);
        	}
        }, false);
        
        $scope.checkMessage = function(){
        	if($scope.isAuthenticated() && $rootScope.configurationParams.component_notifiche_popup_enabled){
        		InstantMessage.read({}, function(messaggi){
        			if(messaggi.length > 0){
        				for(var i = 0; i<messaggi.length; i++){
        					ngToast.create(  {
    	                		className: messaggi[i].colore,
    	                		content: messaggi[i].testo,
    	                		dismissButton: true,
    	                		animation: 'fade',
    	                		dismissOnTimeout: false
    	                	});
        				}
        			}
        			setTimeout($scope.checkMessage, 10000);
        		});
        	}else{
        		setTimeout($scope.checkMessage, 10000);
        	}
    	};
    	
    	$scope.checkToken = function(){
        	if(Principal.isAuthenticated() && !localStorageService.get('token')){
				console.log("Effettuo il logout");
        		delete $rootScope.profiloattivo;
                localStorageService.clearAll();
				Auth.logout("checkToken");
				$state.go('home');
        	}
        	setTimeout($scope.checkToken, 30000);
        };
    	
    	setTimeout($scope.checkMessage, 10000);
    	setTimeout($scope.checkToken, 30000);
    });