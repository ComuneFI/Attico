'use strict';

angular.module('cifra2gestattiApp')
    .controller('MessaggioController', function ($scope, $rootScope, InstantMessage, Utente, Aoo) {
        $scope.utenti = [];
        $scope.aoos = [];
        
        $scope.loadAoos = function(){
        	Aoo.getAllEnabled({}, function(result){
        		$scope.aoos = result;
        	});
        };
        
        $scope.readUtentiLoggati = function(){
        	$scope.utenti = [];
        	$("#updateList").addClass("spinEffect");
	        Utente.utentiLoggati({}, function(result, headers) {
	    		angular.forEach(result, function(utente, key) {
	    			utente.descrizione = utente.nome + ' ' + utente.cognome + " (" + utente.username + ")"; 
	    			$scope.utenti.push(utente);
	    		});
	    		$("#updateList").removeClass("spinEffect");
	        });
        };
        $scope.level={ 
        		'success': 'Verde', 
        		'info': 'Celeste',
        		'warning':'Giallo',
        		'danger':'Rosso'
        			};

        $scope.save = function () {
        	if($scope.messaggio.destinatari && $scope.messaggio.destinatari.length > 0){
	        	angular.forEach($scope.messaggio.destinatari, function(destinatario, key) {
	        		$scope.messaggio.destinatari[key] = destinatario.username;
	    		});
        	}
        	if($scope.messaggio.aoos && $scope.messaggio.aoos.length > 0){
	        	angular.forEach($scope.messaggio.aoos, function(aoo, key) {
	        		$scope.messaggio.aoos[key] = aoo.id;
	    		});
        	}
        	InstantMessage.save($scope.messaggio, function () {
                $scope.refresh();
       		});
        };


        $scope.refresh = function () {
            $('#saveMessaggioModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.messaggio = {testo: null, level:'info'};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.readUtentiLoggati();
        };
        
        $scope.loadAoos();
    });
