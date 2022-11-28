'use strict';

angular.module('cifra2gestattiApp')
    .factory('TimeAuth', function ($rootScope, localStorageService, $state, Auth, $timeout, $interval, $window, $log) {
        
       
        $rootScope.stopped;

        var getToken = function(){
        	return localStorageService.get('token');
        };

        var timeClientMaxSeconds = 1800; /* Mezz'ora */ 
        $rootScope.counterTimeAuth = timeClientMaxSeconds*1000;
        $rootScope.forceCountdown = false;
        var countdown = function(){
        	var token = getToken();
        	$interval.cancel( $rootScope.stopped); 
        	$timeout.cancel(  $rootScope.stopped);
        	$rootScope.stopped = $timeout(function() {
        					if($rootScope.counterTimeAuth > 0){
        						if($rootScope.forceCountdown == false){
    								$rootScope.forceCountdown = 1000;
    							}else{
    								$rootScope.forceCountdown = $rootScope.forceCountdown + 1000;
    							}
    							$rootScope.counterTimeAuth = (timeClientMaxSeconds * 1000) - $rootScope.forceCountdown;
								
								var restante = $rootScope.counterTimeAuth /1000;
								var restante = ~~restante;
								

								if(restante === 60){
									$rootScope.$broadcast('auth-expire', 60);
								}
								if(restante === 30){
									$rootScope.$broadcast('auth-expire', 30);
									$rootScope.firstTime=false;
								}
								if(restante <= 0){
									stop();
                                    delete $rootScope.profiloattivo;
                                    localStorageService.clearAll();
									alert('Il sistema ha rilevato un mancato utilizzo del sistema per un tempo troppo lungo e quindi la sessione sta per essere chiusa.');
									Auth.logout("timeauth");
									$state.go('home');
								}
							}
     						countdown();
    					}, 1000);
        };

        return {
            start: function() {
				countdown();
            },
            stop: function() {
				$interval.cancel( $rootScope.stopped); 
        		$timeout.cancel(  $rootScope.stopped);	
            },
            refresh: function() {
            	$interval.cancel( $rootScope.stopped); 
        		$timeout.cancel(  $rootScope.stopped);
            }
         };
	});