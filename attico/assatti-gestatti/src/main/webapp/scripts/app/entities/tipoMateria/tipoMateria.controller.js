'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoMateriaController', function ($scope,$stateParams,  Aoo,   $log, TipoMateria, Materia, SottoMateria) {

    	$scope.aoos = Aoo.getMinimal();
    	$log.debug("Aoos:",$scope.aoos);

    $scope.toggle = function(scope) {
        scope.toggle();
    };
    
    $scope.loading = true;
    
    $scope.tipiRicerca = [{'value':'tipomateria', 'label':'Tipo Materia'}, {'value':'materia', 'label':'Materia'}, {'value':'sottomateria', 'label':'Sotto Materia'}];
    
    $scope.search = {};
    
    $scope.stati = [
		{
			id:0,
			denominazione:"cifra2gestattiApp.tipoMateria.filtro.stato.attivi"
		},
		{
			id:1,
			denominazione:"cifra2gestattiApp.tipoMateria.filtro.stato.disattivati"
		},
		{
			id:2,
			denominazione:"cifra2gestattiApp.tipoMateria.filtro.stato.entrambi"
		}
    ];
    
    $scope.search.stato = $scope.stati[2];
    
    $scope.ricerca = function(){
    	var stato = null;
		if($scope.search.stato){
			stato = $scope.search.stato.id;
		}
    	$scope.tipoMaterias = TipoMateria.search({
    		tipoRicerca: $scope.search.tipoRicerca.value,
    		descrizione: $scope.search.descrizione,
    		aoo: $scope.search.aoo,
    		stato: stato
    	});
    };
    
    $scope.resetRicerca = function(){
    	$scope.search = {};
    	$scope.load();
    };
    
    $scope.disableMateria = function(materia){
 	    Materia.disable({ id: materia.id}, function(data){
 	    	if(materia.validita == null || materia.validita == undefined){
 	    		materia.validita = {};
 	    	}
 	    	materia.validita.validoal = new Date();
 	    });
    };
    
    $scope.enableMateria = function(materia){
    	Materia.enable({ id: materia.id}, function(data){
 	    	if(materia.validita == null ||materia.validita == undefined){
 	    		materia.validita = {};
 	    	}
 	    	materia.validita.validoal = undefined;
 	    });
    };
    
    $scope.disableSottoMateria = function(sottomateria){
 	    SottoMateria.disable({ id: sottomateria.id}, function(data){
 	    	if(sottomateria.validita == null || sottomateria.validita == undefined){
 	    		sottomateria.validita = {};
 	    	}
 	    	sottomateria.validita.validoal = new Date();
 	    });
    };
    
    $scope.enableSottoMateria = function(sottomateria){
    	SottoMateria.enable({ id: sottomateria.id}, function(data){
 	    	if(sottomateria.validita == null ||sottomateria.validita == undefined){
 	    		sottomateria.validita = {};
 	    	}
 	    	sottomateria.validita.validoal = undefined;
 	    });
    };
    
    $scope.disableTipoMateria = function(tipoMateria){
 	    TipoMateria.disable({ id: tipoMateria.id}, function(data){
 	    	if(tipoMateria.validita == null || tipoMateria.validita == undefined){
 	    		tipoMateria.validita = {};
 	    	}
 	    	tipoMateria.validita.validoal = new Date();
 	    });
    };
    
    $scope.enableTipoMateria = function(tipoMateria){
    	TipoMateria.enable({ id: tipoMateria.id}, function(data){
 	    	if(tipoMateria.validita == null ||tipoMateria.validita == undefined){
 	    		tipoMateria.validita = {};
 	    	}
 	    	tipoMateria.validita.validoal = undefined;
 	    });
    };


    $scope.edit = function( entity ) {
    	
    	var editable = false;
        
    	// modifica voce 'tipoMateria'
        if( typeof entity.materie !== 'undefined'){
        	TipoMateria.get({id: entity.id}, function(result) {
	    		if(!result.atti){
	    			editable=true;
	            } 
	    		
	    		if(editable){
	            	entity.backup = jQuery.extend({}, entity);
	                entity.edit=true;
	            } else {
	            	$('#operationNotPermitted').modal('show');
	            }
	    	});
        }

        // modifica voce 'materia'
        if( (typeof entity.sottoMaterie !== 'undefined') && (typeof entity.tipoMateria !== 'undefined') ){
        	Materia.get({id: entity.id}, function(result) {
	    		if(!result.atti){
	    			editable=true;
	            } 
	    		
	    		if(editable){
	            	entity.backup = jQuery.extend({}, entity);
	                entity.edit=true;
	            } else {
	            	$('#operationNotPermitted').modal('show');
	            }
	    	});
        }
        
        // modifica voce 'sottoMateria'
        if( typeof entity.materia !== 'undefined' ){
        	SottoMateria.get({id: entity.id}, function(result) {
	    		if(!result.atti){
	    			editable=true;
	            } 
	    		
	    		if(editable){
	            	entity.backup = jQuery.extend({}, entity);
	                entity.edit=true;
	            } else {
	            	$('#operationNotPermitted').modal('show');
	            }
	    		
	    	});
        }

    };  
    $scope.cancelEdit = function( entity ) {
    	if(!entity || !entity.id){
    		if($scope.tipoMaterias){
	    		if($scope.tipoMaterias.indexOf(entity) !== -1){
	    			$scope.tipoMaterias.splice($scope.tipoMaterias.indexOf(entity));
	    		}else{
		    		for(var i = 0; i < $scope.tipoMaterias.length; i++){
		    			if($scope.tipoMaterias[i].materie){
		    				if($scope.tipoMaterias[i].materie.indexOf(entity) !== -1){
		    					$scope.tipoMaterias[i].materie.splice($scope.tipoMaterias[i].materie.indexOf(entity));
		    					break;
		    				}else{
			    				for(var j = 0; j < $scope.tipoMaterias[i].materie.length; j++){
			    					if($scope.tipoMaterias[i].materie[j].sottoMaterie){
			    						if($scope.tipoMaterias[i].materie[j].sottoMaterie.indexOf(entity) !== -1){
			    							$scope.tipoMaterias[i].materie[j].sottoMaterie.splice($scope.tipoMaterias[i].materie[j].sottoMaterie.indexOf(entity));
			    							break;
			    						}
			    					}
			    				}
		    				}
		    			}
		    		}
	    		}
    		}
    	}else{
	    	if(entity.backup){
	    		if(entity.backup.descrizione){
	    			entity.descrizione = entity.backup.descrizione;
	    		}
	    		if(entity.backup.aoo){
	    			entity.aoo = entity.backup.aoo;
	    		}
	    	}
	    	delete entity.backup;
	        entity.edit=false;
    	}
    };
    $scope.disable = function( entity ) {
        $log.debug("disable",entity);
        entity.validita.validoal=new Date();
    };
    $scope.enable = function( entity ) {
        $log.debug("enable",entity);
        entity.validita.validoal=null;
    };

    $scope.salvaTipoMateria = function( tipoMateria ) {
        $log.debug(tipoMateria);
       
        if(tipoMateria.aoo!= null && tipoMateria.aoo.id == ''){
        	tipoMateria.aoo = null;
        }
        
        if(tipoMateria.id!=undefined && tipoMateria.id !== null ){
            TipoMateria.update( tipoMateria, function(result) {
                    $scope.load(   );
            });

        }else{
            TipoMateria.save( tipoMateria, function(result) {
            	$scope.resetRicerca();
            });
        }
    };
    
    $scope.salvaMateria = function( materia  ) {
        $log.debug(materia);
        
        if(materia.aoo != null && (materia.aoo.id == 'null' || materia.aoo.id == ''))
        	materia.aoo = null;
        	
        
        if(materia.id!=undefined && materia.id !== null ){
            Materia.update( materia, function(result) {
                $scope.load(  );
            });
        }else{
            Materia.save( materia, function(result) {
            	$scope.resetRicerca();
            });
        }

        
    };

    $scope.salvaSottoMateria = function( sottoMateria  ) {
        $log.debug(sottoMateria);
        
        if(sottoMateria.aoo != null && (sottoMateria.aoo.id == 'null' || sottoMateria.aoo.id == ''))
        	sottoMateria.aoo = null;
        
        if(sottoMateria.id!=undefined && sottoMateria.id !== null ){
            SottoMateria.update( sottoMateria, function(result) {
                $scope.load(   );
            });
        }else{
            SottoMateria.save( sottoMateria, function(result) {
            	$scope.resetRicerca();
            });
        }
    };



    $scope.addTipomateria = function( ) {
        $log.debug("addTipomateria");

        if( !angular.isDefined( $scope.tipoMaterias) ||  $scope.tipoMaterias=== null ){
            $scope.tipoMaterias= [];
        }


        $scope.tipoMaterias.push( {descrizione:'', dataType:'tipoMateria', aoo:null  ,edit:true, add: true } );    
    };


    $scope.addMateria = function( tipoMateria ) {
        if(!angular.isDefined(tipoMateria.materie) || tipoMateria.materie === null ){
            tipoMateria.materie=[];
        }
        
        var settableAoo = (tipoMateria.aoo == null);

        tipoMateria.materie.push( {settableAoo: settableAoo, aoo: tipoMateria.aoo, descrizione:'', tipoMateria: {id: tipoMateria.id}, dataType:'materia', edit:true} );    

    };



    $scope.addSottoMateria = function( materia ) {
        $log.debug("addSottoMateria .materie:"+materia);
        if( !angular.isDefined( materia.sottoMaterie) ||  materia.sottoMaterie === null ){
            materia.sottoMaterie = [];
        }
        
        var settableAoo = (materia.aoo == null);

        materia.sottoMaterie.push( {settableAoo: settableAoo, aoo: materia.aoo, descrizione:'', materia: {id:materia.id}, dataType:'sottoMateria' ,edit:true  } );    
    };



    $scope.load = function ( ) {
    	$scope.loading = true;
        TipoMateria.aoovuota({}, function(res){
        	$scope.tipoMaterias = res;
        	$scope.loading = false;
        	
        });
        $log.debug("tipoMaterias:",$scope.tipoMaterias);
    };

    $scope.load( );
    
	/**
	 * Init the tooltip.
	 */
	$scope.initTooltip = function () {
		$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
	};



        // function ($scope, TipoMateria, Aoo, ParseLinks) {
        // $scope.tipoMaterias = [];
        // $scope.aoos = Aoo.query();
        // $scope.page = 1;
        // $scope.loadAll = function() {
        //     TipoMateria.query({page: $scope.page, per_page: 20}, function(result, headers) {
        //         $scope.links = ParseLinks.parse(headers('link'));
        //         $scope.tipoMaterias = result;
        //     });
        // };
        // $scope.loadPage = function(page) {
        //     $scope.page = page;
        //     $scope.loadAll();
        // };
        // $scope.loadAll();

        // $scope.showUpdate = function (id) {
        //     TipoMateria.get({id: id}, function(result) {
        //         $scope.tipoMateria = result;
        //         $('#saveTipoMateriaModal').modal('show');
        //     });
        // };

        // $scope.save = function () {
        //     if ($scope.tipoMateria.id != null) {
        //         TipoMateria.update($scope.tipoMateria,
        //             function () {
        //                 $scope.refresh();
        //             });
        //     } else {
        //         TipoMateria.save($scope.tipoMateria,
        //             function () {
        //                 $scope.refresh();
        //             });
        //     }
        // };

        // $scope.delete = function (id) {
        //     TipoMateria.get({id: id}, function(result) {
        //         $scope.tipoMateria = result;
        //         $('#deleteTipoMateriaConfirmation').modal('show');
        //     });
        // };

        // $scope.confirmDelete = function (id) {
        //     TipoMateria.delete({id: id},
        //         function () {
        //             $scope.loadAll();
        //             $('#deleteTipoMateriaConfirmation').modal('hide');
        //             $scope.clear();
        //         });
        // };

        // $scope.refresh = function () {
        //     $scope.loadAll();
        //     $('#saveTipoMateriaModal').modal('hide');
        //     $scope.clear();
        // };

        // $scope.clear = function () {
        //     $scope.tipoMateria = {descrizione: null, id: null};
        //     $scope.editForm.$setPristine();
        //     $scope.editForm.$setUntouched();
        // };
    });
