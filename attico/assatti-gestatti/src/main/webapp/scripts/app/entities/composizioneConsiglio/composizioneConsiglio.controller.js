'use strict';

angular.module('cifra2gestattiApp')
    .controller('ComposizioneConsiglioController', function ($scope, $rootScope,  $filter, Profilo, ComposizioneConsiglio,Aoo, QualificaProfessionale, ParseLinks, Ruolo, Principal, ProfiloAccount,RoleCodes, $state ) {
        $scope.profilos = [];

        $scope.profiloSearch = {};
        $scope.tempSearch = {};
        $scope.page = 1;
        
        $scope.loadAll = function() {
        	
        	var ruoliSer = RoleCodes.ROLE_COMPONENTE_CONSIGLIO;

        	ComposizioneConsiglio.getComponentiGiuntaConsiglio({stato:'0',ruolo: ruoliSer}, function(result){
    			//$log.debug("Assessorati:",result);
        		$scope.profilos = [];
        		$scope.composizioneConsiglio = {componenti:[]};
        		
    			for(var i = 0; i < result.length; i++){
    				if($scope.filterDuplicatiAssessori($scope.profilos, result[i].id) == false){
    					if(result[i].qualificaProfessionaleConsiglio==null){
    						//result[i].qualificaProfessionaleConsiglio =  {id:0}
    					}
    					
    					$scope.profilos.push(result[i]);
       				}
    			}
    			$scope.composizioneConsiglio.componenti = $scope.profilos;
    			$scope.composizioneConsiglio.componenti = $filter('orderBy')($scope.composizioneConsiglio.componenti, ['ordineConsiglio']);
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
        	if($scope.composizioneConsiglio.componenti!=null){
        		for(var i = 0; i< $scope.composizioneConsiglio.componenti.length; i++){
        			$scope.composizioneConsiglio.componenti[i].ordineConsiglio="";
            	}
        	}
        };
        
        
        $scope.save = function () {
        	//Disable the save button
        	$scope.saveButtonDisabled = true;
			let checkIdx = [];
        	if($scope.composizioneConsiglio.componenti!=null){
        		
        				var aggiorna = true;
        				for(var i = 0; i< $scope.composizioneConsiglio.componenti.length; i++){
        					if(isNaN(+$scope.composizioneConsiglio.componenti[i].ordineConsiglio)){
        						aggiorna = false;
        						break;
        					}
							if(checkIdx.includes(Number($scope.composizioneConsiglio.componenti[i].ordineConsiglio))) {
								aggiorna = false;
								break;
							}
							checkIdx.push(Number($scope.composizioneConsiglio.componenti[i].ordineConsiglio));
							if($scope.composizioneConsiglio.componenti[i].validoSedutaConsiglio &&
								(!$scope.composizioneConsiglio.componenti[i].qualificaProfessionaleConsiglio ||
									!$scope.composizioneConsiglio.componenti[i].qualificaProfessionaleConsiglio.id)) {
								aggiorna = false;
								break;
							}
        				}
        				if(aggiorna == true){
        					ComposizioneConsiglio.update($scope.composizioneConsiglio.componenti, function () {
        						rootScope.showMessage({title:'Composizione Consiglio', okButton:true, body:'Salvataggio della Composizione Consiglio avvenuto correttamente'});
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
