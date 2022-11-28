'use strict';

angular.module('cifra2gestattiApp')
    .controller('SchedaDatoController', function ($scope, SchedaDato, Scheda, Dato) {
        $scope.schedaDatos = [];
        $scope.schedaDatoa = [];
        
        $scope.counter=0;
        $scope.schedas = Scheda.query();
        $scope.datos = Dato.query();
        $scope.update=false;
        $scope.loadAll = function() {
            SchedaDato.query(function(result) {
               $scope.schedaDatos = result;
            });
        };
        $scope.loadAll();




        $scope.showUpdate = function (id) {
        	$scope.update=true;
            SchedaDato.get({id: id}, function(result) {
            	var appo = eval( $scope.schedaDatos );
        		for( var i = 0; i < appo.length; i++ ) {
        			if( appo[i].id === id ) {
        				$scope.schedaDato=appo[i];
        				break;
        			}
        		}
                $scope.schedaDato = result;
                $('#updateSchedaDatoModal').modal('show');
            });
        };
        
        $scope.addRow = function(){		
        	
        	$scope.schedaDatoa.push({'id':$scope.counter, 'Dato':$scope.datos[$scope.Dato-1], 'obbligatorio': $scope.obbligatorio, 'ordine':$scope.ordine});
        	$scope.Dato='';
        	$scope.obbligatorio='';
        	$scope.ordine='';
        	$scope.counter=$scope.counter+1;
        };
       
        $scope.removeRow = function(id){				
    		var index = -1;		
    		var comArr = eval( $scope.schedaDatoa );
    		for( var i = 0; i < comArr.length; i++ ) {
    			if( comArr[i].id === id ) {
    				index = i;
    				break;
    			}
    		}
    		if( index === -1 ) {
    			alert( "Something gone wrong" );
    		}
    		$scope.schedaDatoa.splice( index, 1 );		
    	};
    	
        $scope.save = function (Rigo) {
   
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
        	var comArr = eval( $scope.schedaDatoa );
        	for( var i = 0; i < comArr.length; i++ ) {
        		comArr[i].dato=comArr[i].Dato;
        		comArr[i].scheda=$scope.schedas[$scope.Scheda-1];
        		$scope.save(comArr[i]);
    		}
        	delete $scope.schedaDatoa;
        };
        $scope.delete = function (id) {
            SchedaDato.get({id: id}, function(result) {
                $scope.schedaDato = result;
                $('#deleteSchedaDatoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SchedaDato.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSchedaDatoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSchedaDatoModal').modal('hide');
            $('#updateSchedaDatoModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.schedaDato = {obbligatorio: null, ordine: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
