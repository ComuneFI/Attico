'use strict';

angular.module('cifra2gestattiApp')
    .controller('RichiestaHDDetailController', ['$scope', '$rootScope','RichiestaHD', 'StatoRichiestaHD', '$stateParams', 'ngToast', 'Principal','$state',
        function ($scope, $rootScope, RichiestaHD, StatoRichiestaHD, $stateParams, ngToast, Principal, $state) {
    	$scope.richiestaHD = {};
    	$scope.rispostaHD = {richiesta:{}};
    	$scope.statoUpdate = {};
    	$scope.statorichiestahds = StatoRichiestaHD.query();
        $scope.load = function (id) {
        	RichiestaHD.get({id: id}, function(result) {
              $scope.richiestaHD = result;
              $scope.statoUpdate = jQuery.extend(true, {}, $scope.richiestaHD.stato);
            });
        };
        
        $scope.downloadModulo = function(){
        	window.open(
        			$rootScope.buildDownloadUrl("api/richiestaHDs/" + $scope.richiestaHD.id + "/modulo") + "&access_token=" + $scope.access_token,
        			  '_blank'
        			);
        };
        
        $scope.updateStato = function(idRichiesta, statoid){
        	RichiestaHD.updateStato({id:idRichiesta, statoId:statoid}, function(result){
        		ngToast.create(  { className: 'success', content:  'Stato richiesta salvato con successo'  } );
        		$scope.load($stateParams.id);
        	});
        };
        
	  	$scope.chiudi = function(richiesta){
	  		if($scope.isAdmin || $rootScope.account.utente.id == richiesta.autore.id){
		  		if(confirm('Per confermare la chiusura della richiesta preme OK')){
			  		var statoid = null;
			  		for(var i = 0; i<$scope.statorichiestahds.length; i++){
			  			if($scope.statorichiestahds[i].descrizione == 'CHIUSA'){
			  				statoid = $scope.statorichiestahds[i].id;
			  				break;
			  			}
			  		}
			  		if(statoid!=null){
				  		RichiestaHD.updateStato({id:richiesta.id, statoId:statoid}, function(result){
			        		ngToast.create(  { className: 'success', content:  'Richiesta chiusa con successo'  } );
			        		$scope.load($stateParams.id);
			        	});
			  		}else{
			  			ngToast.create(  { className: 'error', content:  'Impossibile chiudere la richiesta'  } );
			  		}
		  		}
	  		}
	  	};
	  	
	  	$scope.editTestoRichiesta = function (richiesta) {
            RichiestaHD.editTestoRichiesta(richiesta, function () {
            	ngToast.create(  { className: 'success', content:  'Testo della richiesta modificato con successo'  } );
            });
        };
        
        $scope.editTestoRisposta = function (risposta) {
            RichiestaHD.editTestoRisposta(risposta, function () {
            	ngToast.create(  { className: 'success', content:  'Testo della risposta modificato con successo'  } );
            });
        };
	  	
	  	$scope.rispondi = function(idRichiesta){
	  		$scope.rispostaHD.richiesta = {id:idRichiesta};
	  		if($scope.isAdmin === false){
	  			$scope.rispostaUtente();
	  		}else if($scope.isAdmin === true){
	  			$scope.rispostaOperatore();
	  		}else{
	  			alert("Si è verificato un problema. Si consiglia di effettuare il logout, riaccedere con le proprie credenziali e provare nuovamente.");
	  		}
	  	};
	  	
        $scope.rispostaOperatore = function(){
        	RichiestaHD.rispostaOperatore({}, $scope.rispostaHD, function(result) {
        		ngToast.create(  { className: 'success', content:  'Risposta salvata con successo'  } );
        		$scope.load($stateParams.id);
        		$scope.rispostaHD = {richiesta:{}};
            });
        };
        
        $scope.rispostaUtente = function(){
        	RichiestaHD.rispostaUtente({}, $scope.rispostaHD, function(result) {
        		ngToast.create(  { className: 'success', content:  'Risposta salvata con successo'  } );
        		$scope.load($stateParams.id);
        		$scope.rispostaHD = {richiesta:{}};
            });
        };
        
        $scope.goBack = function(){
        	if($scope.isAdmin){
        		$state.go("richiestaHD");
        	}else{
        		$state.go("richiestaHDU");
        	}
        };
        
        $scope.init = function(){
        	$scope.isAdmin = null;
            var admin = [];
    	  	admin.push("ROLE_ADMIN");
    	  	if(Principal.isInAnyRole(admin)){
    	  		$scope.isAdmin = true;
    	  	}else{
    	  		$scope.isAdmin = false;
    	  	}
    	  	$scope.load($stateParams.id);
        };
    }]);
