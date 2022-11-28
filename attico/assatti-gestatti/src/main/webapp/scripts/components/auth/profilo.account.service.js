'use strict';

angular.module('cifra2gestattiApp')
    .factory('ProfiloAccount', function Auth($rootScope,  localStorageService,$log, $q) {

        var _keyAccount = "cifra2account";
        var _keyProfilo = "cifra2profilo";
        var _keyTipoMaterie = "cifra2tipomaterie";
        var _keyProfiloOrigine = "cifra2profiloOrigine";
        var _keyProfiliAttiviOrigine = "cifra2profiliAttiviOrigine";

        return {
        	setProfiloOrigine: function ( profilo, profiliAttivi ) {
                localStorageService.set( _keyProfiloOrigine, profilo);
                localStorageService.set( _keyProfiliAttiviOrigine, profiliAttivi);
            },
            setAccount: function ( account ) {
                localStorageService.set( _keyAccount, account);
                $rootScope.account = account;
            },
             setProfilo: function ( profilo ) {
                localStorageService.set( _keyProfilo, profilo);
                $rootScope.profiloattivo = profilo;
                $rootScope.insertUpdateAppKey(true);
            },
             setTipoMaterie: function ( tipoMaterieActive ) {
                localStorageService.set( _keyTipoMaterie, tipoMaterieActive);
                $rootScope.tipoMaterieActive = tipoMaterieActive;
            },
            loadAccount: function( ) {
                var account =  localStorageService.get( _keyAccount );
                $rootScope.account = account;

                var profilo =  localStorageService.get( _keyProfilo );
                $rootScope.profiloattivo = profilo;
                
                var profiloOrigine =  localStorageService.get( _keyProfiloOrigine );
                $rootScope.profiloOrigine = profiloOrigine;
                
                if(profiloOrigine && profilo){
                	$rootScope.activeprofilos = [];
                	$rootScope.activeprofilos.push(profilo);
                	$rootScope.activeprofilos.$resolved = true;
                }
                
                var profiliAttiviOrigine =  localStorageService.get( _keyProfiliAttiviOrigine );
                $rootScope.profiliAttiviOrigine = profiliAttiviOrigine;

                var tipoMaterieActive =  localStorageService.get( _keyTipoMaterie );
                $rootScope.tipoMaterieActive = tipoMaterieActive;
            },
            isInAnyRole: function( roles ) {
                //$log.debug(roles);
                if(roles === null || roles.length === 0 )
                    return true;

                //$log.debug("isInAnyRole roles.length:"+roles.length);

                if ( angular.isDefined($rootScope.profiloattivo)   && $rootScope.profiloattivo !== null
                    &&   angular.isDefined($rootScope.profiloattivo.grupporuolo)  && $rootScope.profiloattivo.grupporuolo !== null
                     &&   angular.isDefined($rootScope.profiloattivo.grupporuolo.hasRuoli)  && $rootScope.profiloattivo.grupporuolo.hasRuoli !== null 
                        && $rootScope.profiloattivo.grupporuolo.hasRuoli.length > 0) {
                    for(var i=0;i<$rootScope.profiloattivo.grupporuolo.hasRuoli.length;i++){
                        var item = $rootScope.profiloattivo.grupporuolo.hasRuoli[i];
                        //$log.debug("isInAnyRole item.codice:"+item.codice  );
                        for(var ii=0;ii<roles.length;ii++){
                            var rolevo = roles[ii];
                            //$log.debug("isInAnyRole rolevo:"+ rolevo  );
                            var b = item.codice === rolevo ;
                            //$log.debug("isInAnyRole b :"+ b   );
                            if ( item.codice === rolevo ) {
                                return true;
                            }

                        }
                    }
                }

                return false;
                
            },
            isAnyProfilesInRole: function( roles ) {
            	var deferred = $q.defer();
            	var abilitato = false;
                if($rootScope.activeprofilos && $rootScope.activeprofilos.$resolved){
                	$q(function(){
                			abilitato = false;
		                	for(var x = 0; x < $rootScope.activeprofilos.length; x++){
		                		if ( angular.isDefined($rootScope.activeprofilos[x]) && $rootScope.activeprofilos[x] &&
    		                    $rootScope.activeprofilos[x].grupporuolo
    		                    && $rootScope.activeprofilos[x].grupporuolo.hasRuoli
    		                    && $rootScope.activeprofilos[x].grupporuolo.hasRuoli.length > 0) {
	    		                    for(var i=0;i<$rootScope.activeprofilos[x].grupporuolo.hasRuoli.length;i++){
	    		                        var item = $rootScope.activeprofilos[x].grupporuolo.hasRuoli[i];
	    		                        for(var ii=0;ii<roles.length;ii++){
	    		                            var rolevo = roles[ii];
	    		                            if ( item.codice == rolevo ) {
	    		                            	abilitato = true;
	    		                            	break;
	    		                            }
	    		                        }
	    		                        if(abilitato == true){
    		                            	break;
    		                            }
	    		                    }
		                		}
		                		if(abilitato == true){
	                            	break;
	                            }
			            	};
			            	deferred.resolve(abilitato);
                	});
                }else{
                	deferred.resolve(abilitato);
                }
                return deferred.promise;
            }
        };
    });
