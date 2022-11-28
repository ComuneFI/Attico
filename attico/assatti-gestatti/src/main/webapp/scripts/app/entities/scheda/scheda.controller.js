'use strict';

angular.module('cifra2gestattiApp')
    .controller('SchedaController', function ($scope, $filter, Scheda, Dato,SchedaDato, $log, ParseLinks) {
        $scope.schedas = [];
        $scope.schedaDatoa = [];
        $scope.schedaDatoToDelete = [];
        $scope.page = 1;
        $scope.listTrueFalse = [{"descrizione":"Si", "id":"true"}, {"descrizione":"No", "id":"false"}];
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.tempSearch = {};
        $scope.schedaSearch = {};
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.schedaSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in schedaSearch*/
        	$scope.schedaSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if(angular.isUndefined(searchObject.ripetitiva) || searchObject.ripetitiva==undefined || searchObject.ripetitiva == null){
        			searchObject.ripetitiva = {};
        		}
        		if(angular.isDefined(searchObject.ordine) && searchObject.ordine != undefined && searchObject.ordine!=null){
        			if(!Number.isInteger(Number(searchObject.ordine))){
        				searchObject.ordine = "";
        			}
        		}
        		Scheda.query({page: $scope.page, per_page: 5, etichetta:searchObject.etichetta, ripetitiva:searchObject.ripetitiva.id, ordine:searchObject.ordine}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.schedas = result;
	            });
        	}else{
        		if(angular.isUndefined($scope.schedaSearch.ripetitiva) || $scope.schedaSearch.ripetitiva==undefined || $scope.schedaSearch.ripetitiva == null){
        			$scope.schedaSearch.ripetitiva = {};
        		}
        		if(angular.isDefined($scope.schedaSearch.ordine) && $scope.schedaSearch.ordine != undefined && $scope.schedaSearch.ordine!=null){
        			if(!Number.isInteger(Number($scope.schedaSearch.ordine))){
        				$scope.schedaSearch.ordine = "";
        			}
        		}
        		Scheda.query({page: $scope.page, per_page: 5, etichetta:$scope.schedaSearch.etichetta, ripetitiva:$scope.schedaSearch.ripetitiva.id, ordine:$scope.schedaSearch.ordine}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.schedas = result;
	            });
        	}
        };
        
        $scope.initConditions=function(schedaDato){
                $log.debug(schedaDato);
                if( angular.isDefined( schedaDato.hideExpression) 
                    && schedaDato.hideExpression !== null 
                        &&  schedaDato.hideExpression.length > 0
                            &&  schedaDato.hideExpression.trim().length > 0 ){
                    try {
                        schedaDato.conditions = angular.fromJson( schedaDato.hideExpression);
                    }
                    catch(err) {
                        schedaDato.hideExpression = '[]';
                        schedaDato.conditions = [];  
                    }
                
                }else{
                        schedaDato.hideExpression = '[]';
                        schedaDato.conditions = [];  

                }
        }
     
         $scope.addExpression = function (operatore,schedaDato) {
            $log.debug("operatore:"+operatore);
           $log.debug(schedaDato );
           
               if(schedaDato.hideExpression ===null ){
                schedaDato.hideExpression =   operatore ;
               }else{
                   schedaDato.hideExpression = schedaDato.hideExpression + operatore ;
                } 
           
           
         };
        

        $scope.row={};
        $scope.obbligatorio="";
        $scope.datos = Dato.query();
        
        $scope.loadAll();
        $scope.loadSchedaDato = function(scheda) {
        	Scheda.SchedaDato({id: scheda.id}, function(result) {
                $scope.schedaDatoa = result;
                
            	});
               $scope.scheda = scheda;
//               $scope.schedaDatoa=SchedaDato.query();
            
        };
        $scope.showUpdate = function (id) {
        	$scope.schedaDatoToDelete = [];
            Scheda.get({id: id}, function(result) {
                $scope.scheda = result;
                $('#saveSchedaModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.scheda.id != null) {
                Scheda.update($scope.scheda,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Scheda.save($scope.scheda,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Scheda.get({id: id}, function(result) {
                $scope.scheda = result;
                $('#deleteSchedaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Scheda.delete({id: id},
                function (result) {
            		if(result.stato == "rif_obblighi"){
            			$scope.scheda.question = "question_rif_obblighi";
            			$scope.scheda.obblighiThatUseScheda = result.obblighiThatUseScheda;
            			$('#deleteSchedaConfirmation').modal('hide');
            			$('#deleteSchedaInformation').modal('show');
            		}else if(result.stato == "rif_atti"){
            			$scope.scheda.question = "question_rif_atti";
            			$scope.scheda.obblighiThatUseScheda = result.obblighiThatUseScheda;
            			$scope.scheda.attiThatUseScheda = result.attiThatUseScheda;
            			$scope.scheda.obblighiIdsStr = result.obblighiIdsStr;
            			$('#deleteSchedaConfirmation').modal('hide');
            			$('#deleteSchedaInformation').modal('show');
            		}else{
            			$scope.loadAll();
	                    $('#deleteSchedaConfirmation').modal('hide');
	                    $scope.clear();
            		}
                });
        };
        
        $scope.confirmForceDelete = function(id){
        	Scheda.deleteForce({id: id}, function (){
        		$scope.loadAll();
        		$('#deleteSchedaInformation').modal('hide');
        		$scope.clear();
        	});
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSchedaModal').modal('hide');
            $scope.schedaDatoToDelete = [];
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.scheda = {etichetta: null, ripetitiva: null, ordine: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.addRow = function(){
        	var dato = $filter('filter')($scope.datos, {id: $scope.row.dato})[0];
        	$scope.schedaDatoa.push({'scheda':$scope.scheda,'dato':dato, 'obbligatorio': $scope.row.obbligatorio, 'ordine':$scope.row.ordine});
        	$scope.row.dato='';
        	$scope.row.obbligatorio='';
        	$scope.row.ordine='';
        	
        };
       
        $scope.removeRow = function(index){
        		if($scope.schedaDatoa[index].id==undefined || $scope.schedaDatoa[index].id==null){
        			$scope.schedaDatoa.splice( index, 1 );
        		}else{
        			if(confirm("Confermare la cancellazione? Scegliendo OK avviene la cancellazione senza necessità di premere 'Salva'.")){
			        	SchedaDato.delete({id: $scope.schedaDatoa[index].id}, function(risultato){
			        		if(risultato.stato == "ok"){
			        			$scope.schedaDatoa.splice( index, 1 );
			        		}else{
			        			alert("Il dato che si sta tentando di cancellare è utilizzato da altre entità del sistema. Impossibile cancellare.");
			        		}
			        	});
        			}
        		}
    	};
    	
        $scope.saveSchedaDato = function (Rigo) {
   
            if (Rigo.id != null) {
            	
                SchedaDato.update(Rigo,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	
                SchedaDato.save(Rigo,
                    function () {
                        $scope.refresh();
                    });
            }
            $scope.update=false;
        };
        $scope.SalvaRighi=function () {
        	if($("select.ng-invalid").length==0){
        		$("#requiredDiv").hide();
	        	var comArr = eval( $scope.schedaDatoa );
	        	for( var i = 0; i < comArr.length; i++ ) {
	        		
	        		$scope.saveSchedaDato(comArr[i]);
	    		}
	        	$('#saveSchedaDatoModal').modal('hide');
	//        	delete $scope.schedaDatoa;
        	}else{
        		$("#requiredDiv").show();
        	}
        };
    });
