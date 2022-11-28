'use strict';

angular.module('cifra2gestattiApp')
    .controller('ClassificazioneController', function ($scope, Classificazione, Aoo, TipoDocumentoSerie) {
        $scope.classificaziones = [];
        $scope.aoos = Aoo.getMinimal();
        $scope.tipodocumentoseries = TipoDocumentoSerie.query();
        $scope.tempSearch = {};
        $scope.diogeneRun = false;
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var aoo = searchObject.aoo ? searchObject.aoo.id : null;
        		var tipoDoc = searchObject.tipoDocumentoSerie ? searchObject.tipoDocumentoSerie.id : null;
        		Classificazione.query({idTitolario:searchObject.idTitolario, idVoceTitolario:searchObject.idVoceTitolario, aoo:aoo, tipoDocumentoSerie:tipoDoc}, function(result) {
        			$scope.classificaziones = result;
	            });
        	}else{
        		Classificazione.query(function(result) {
        			$scope.classificaziones = result;
	            });
        	}
        };
        
        $scope.loadAll();
        
        $scope.ricerca = function(){
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.resetRicerca = function(){
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };

        $scope.showUpdate = function (id) {
            Classificazione.get({id: id}, function(result) {
                $scope.classificazione = result;
                $('#saveClassificazioneModal').modal('show');
            });
        };
        
        $scope.voceSelected = function(){
        	if($scope.voceSelezionata && $scope.voceSelezionata.idVoce && $scope.voceSelezionata.idTitolario){
        		$scope.classificazione.idTitolario = $scope.voceSelezionata.idTitolario;
        		$scope.classificazione.idVoceTitolario = $scope.voceSelezionata.idVoce;
        		
        		$scope.classificazione.titolario = $scope.voceSelezionata.codiceTitolario + " - " + $scope.voceSelezionata.descrizioneTitolario;
        		$scope.classificazione.voceTitolarioDescrizione = $scope.voceSelezionata.descrizione;
        		$scope.classificazione.voceTitolarioCodice = $scope.voceSelezionata.codice;
        	}
        	$('#diogeneModal').modal('hide');
        };
        
        $scope.viewDiogeneTree = function () {
        	$scope.diogeneRun = true;
        	$scope.voceSelezionata = null;
        	$('#diogeneModal').modal('show');
        };
        
        $scope.closeDiogeneModal = function(){
        	$('#diogeneModal').modal('hide');
        };

        $scope.save = function () {
            if ($scope.classificazione.id != null) {
                Classificazione.update($scope.classificazione,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Classificazione.save($scope.classificazione,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.disable = function(classificazione){
        	Classificazione.disable({ id: classificazione.id}, function(data){
     	    	classificazione.validoAl = "-";
     	    });
        };
        
        $scope.enable = function(classificazione){
        	Classificazione.enable({ id: classificazione.id}, function(data){
        		classificazione.validoAl = undefined;
     	    });
        };

        $scope.confirmDelete = function (id) {
            Classificazione.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteClassificazioneConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveClassificazioneModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.classificazione = {idTitolario: null, idVoceTitolario: null, validoDal: null, validoAl: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });