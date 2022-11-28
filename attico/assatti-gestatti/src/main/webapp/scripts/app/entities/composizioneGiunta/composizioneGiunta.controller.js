'use strict';

angular.module('cifra2gestattiApp')
    .controller('ComposizioneGiuntaController', function ($scope, $rootScope,  $filter, Profilo, ComposizioneGiunta,Aoo, QualificaProfessionale, ParseLinks, Ruolo, Principal, ProfiloAccount,RoleCodes, $state ) {
        $scope.profilos = [];

        $scope.profiloSearch = {};
        $scope.tempSearch = {};
        $scope.page = 1;
        
        $scope.loadAll = function() {
        	
        	var ruoliSer = RoleCodes.ROLE_COMPONENTE_GIUNTA;

        	ComposizioneGiunta.getComponentiGiuntaConsiglio({stato:'0',ruolo: ruoliSer}, function(result){
    			//$log.debug("Assessorati:",result);
        		$scope.profilos = [];
        		$scope.composizioneGiunta = {componenti:[]};
        		
    			for(var i = 0; i < result.length; i++){
    				if($scope.filterDuplicatiAssessori($scope.profilos, result[i].id) == false){
    					if(result[i].qualificaProfessionaleGiunta==null){
    						result[i].qualificaProfessionaleGiunta =  {id:0}
    					}
    					
    					$scope.profilos.push(result[i]);
       				}
    			}
    			$scope.composizioneGiunta.componenti = $scope.profilos;
    			$scope.composizioneGiunta.componenti = $filter('orderBy')($scope.composizioneGiunta.componenti, ['ordineGiunta']);
    			//$scope.aooDestinatari = $filter('orderBy')($scope.aooDestinatari, ['codice', 'name']);
    		});
        };
        $scope.filterDuplicatiAssessori = function(lista, id){
        	for(var i = 0; i< lista.length; i++){
        		if(lista[i].id == id){
        			return true;
        		}
        	}

        	return false;
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.profiloSearch);
        };
        $scope.loadAll();
 
        $scope.resetOrdine = function() {
        	if($scope.composizioneGiunta.componenti!=null){
        		for(var i = 0; i< $scope.composizioneGiunta.componenti.length; i++){
        			$scope.composizioneGiunta.componenti[i].ordineGiunta="";
            	}
        	}
        };
        
        $scope.save = function () {
        	//Disable the save button
        	$scope.saveButtonDisabled = true;
        	let checkIdx = [];
        	if($scope.composizioneGiunta.componenti!=null){
        		
        				var aggiorna = true;
        				//var numbers = new RegExp(/^[0-9]+$/);
        				for(var i = 0; i< $scope.composizioneGiunta.componenti.length; i++){
        					if(isNaN(+$scope.composizioneGiunta.componenti[i].ordineGiunta)){
        						aggiorna = false;
        						break;
        					}
        					if(checkIdx.includes(Number($scope.composizioneGiunta.componenti[i].ordineGiunta))) {
								aggiorna = false;
								break;
							}
							checkIdx.push(Number($scope.composizioneGiunta.componenti[i].ordineGiunta));
        					if($scope.composizioneGiunta.componenti[i].validoSedutaGiunta &&
								(!$scope.composizioneGiunta.componenti[i].qualificaProfessionaleGiunta ||
								!$scope.composizioneGiunta.componenti[i].qualificaProfessionaleGiunta.id)) {
								aggiorna = false;
								break;
							}
        				}
        				if(aggiorna == true){
        					ComposizioneGiunta.update($scope.composizioneGiunta.componenti, function () {
        						$rootScope.showMessage({title:'Composizione Giunta', okButton:true, body:'Salvataggio della Composizione Giunta avvenuto correttamente'});
                            	$scope.refresh();
                            });
        				}else{
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Controllare la validità dei valori' +
									' inseriti: sono ammessi solo valori numerici non duplicati per il campo Ordine Presenza/Votazione ed è obbligatoria la qualifica per i profili validi'});
        				}
                        
        	}
        };

        $scope.refresh = function () {
            $scope.loadAll();
            //Enable save button
            $scope.saveButtonDisabled = false;
        };

        
		/**
		 * Init the tooltip.
		 */
		$scope.initTooltip = function () {
			$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
		};
    });
