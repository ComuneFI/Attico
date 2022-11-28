'use strict';

angular.module('cifra2gestattiApp')
.controller('AooDetailController', function ($scope, $stateParams, Aoo, Profilo, Indirizzo, TipoAoo ) {
	$scope.aoo = {};
	$scope.load = function (id) {
		Aoo.get({id: id}, function(result) {
			$scope.aoo = result;
		});
	};
	$scope.load($stateParams.id);
});


angular.module('cifra2gestattiApp')
.controller('AooQualificaProfessionaleController', function ($scope,$stateParams,  Aoo,   $log, QualificaProfessionale , ParseLinks) {
		$scope.page = 1;
		$scope.load = function (id) {
			$scope.editing = false;
			delete $scope.qualificaProfessionaleEdit ;
			Aoo.get({id: id}, function(result) {
				$scope.aoo = result;
				$scope.aoos = [];
				$scope.aoos.push($scope.aoo);
			});
			$scope.aooId = id;
		 	$scope.loadAll( );

		};
        $scope.loadAll = function( ) {
            /*
             * INNOVCIFRA-187
        	QualificaProfessionale.getByAooId({  aooId:$scope.aooId }, function(result, headers) {
                $scope.qualificaProfessionales = result;
            });
            */
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        

	$scope.toggle = function(scope) {
		scope.toggle();
	};

	$scope.edit = function( entity ) {
		QualificaProfessionale.get({id:entity.id},function(result){
			$scope.qualificaProfessionaleEdit = result;
			$scope.qualificaProfessionaleEdit.edit=true;
			$scope.editing = true;
		});
		  
		$log.debug(entity);
		
	};

	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
		$scope.editing = false;
	};

	$scope.salvaQualificaProfessionale= function( qualificaProfessionaleEdit  ) {
		$log.debug(qualificaProfessionaleEdit);

		if(qualificaProfessionaleEdit.id !== null ){
			QualificaProfessionale.update( qualificaProfessionaleEdit, function(result) {
					$scope.load( $scope.aoo.id );
			});

		}else{
			RubricaDestinatarioEsterno.save( qualificaProfessionaleEdit, function(result) {
					$scope.load( $scope.aoo.id );
			});
		}
	};
	 
	$scope.clear = function () {
		$scope.qualificaProfessionaleEdit = {};
	};

	$scope.addQualificaProfessionale = function( ) {
		$scope.editing = true;
		$scope.qualificaProfessionaleEdit = { aoo:{ id: $scope.aoo.id } ,edit:true } ;
	 
	};
     
	$scope.delete = function (id) {
		QualificaProfessionale.get({id: id}, function(result) {
			$scope.qualificaProfessionaleEdit = result;
			$('#deleteQualificaProfessionaleConfirmation').modal('show');
		});
	};

	$scope.confirmDelete = function (id) {
		QualificaProfessionale.delete({id: id},
			function () {
				$scope.loadAll();
				$('#deleteQualificaProfessionaleConfirmation').modal('hide');
				$scope.clear();
			});
	};
	$scope.load($stateParams.id);

});

angular.module('cifra2gestattiApp')
.controller('AooAssociaRubricaDestinatarioEsternoController', function ($scope,$stateParams,  Aoo,   $log, RubricaDestinatarioEsterno , ParseLinks) {
		$scope.page = 1;
		$scope.load = function (id) {
			$scope.editing = false;
			Aoo.get({id: id}, function(result) {
				$scope.aoo = result;
				$scope.aoos = [];
				$scope.aoos.push($scope.aoo);
			});
			$scope.aooId = id;
		 	$scope.loadAll( );

		};
        $scope.loadAll = function( ) {
            RubricaDestinatarioEsterno.getByAooId({page: $scope.page, per_page: 20, aooId:$scope.aooId }, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.rubricaDestinatarioEsternos = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        

	$scope.toggle = function(scope) {
		scope.toggle();
	};

	$scope.edit = function( entity ) {
		RubricaDestinatarioEsterno.get({id:entity.id},function(result){
			$scope.rubricaDestinatarioEsternoEdit = result;
			$('#saveRubricaDestinatarioEsternoModal').modal('show');
			entity.edit=true;
			$scope.editing = true;
		});
		  
		$log.debug(entity);
		
	};

	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
		$scope.editing = false;
	};

	$scope.salvaRubricaDestinatarioEsterno= function( rubricaDestinatarioEsterno  ) {
		$log.debug(rubricaDestinatarioEsterno);
		if(rubricaDestinatarioEsterno.id !== null ){
			RubricaDestinatarioEsterno.update( rubricaDestinatarioEsterno, function(result) {
					$scope.load( $scope.aoo.id );
			});
			$('#saveRubricaDestinatarioEsternoModal').modal('hide');

		}else{
			RubricaDestinatarioEsterno.save( rubricaDestinatarioEsterno, function(result) {
					$scope.load( $scope.aoo.id );
			});
			$('#saveRubricaDestinatarioEsternoModal').modal('hide');
		}
	};
	/*$scope.refresh = function () {
		$scope.load($scope.aoo.id);
		$('#saveRubricaDestinatarioEsternoModal').modal('hide');
		$scope.clear();
	};*/
	$scope.clear = function () {
		$scope.rubricaDestinatarioEsternoEdit = {denominazione: null, nome: null, cognome: null, titolo: null, email: null, pec: null, tipo: null, id: null};
		$scope.editForm.$setPristine();
		$scope.editForm.$setUntouched();
	};
	$scope.addRubricaDestinatarioEsterno = function( ) {
		$scope.editing = true;
		$scope.rubricaDestinatarioEsternoEdit = { aoo:{ id: $scope.aoo.id } ,edit:true } ;
		$('#saveRubricaDestinatarioEsternoModal').modal('show');
		$scope.editing = true;
	};
     
	$scope.delete = function (id) {
		RubricaDestinatarioEsterno.get({id: id}, function(result) {
			$scope.rubricaDestinatarioEsternoEdit = result;
			$('#deleteRubricaDestinatarioEsternoConfirmation').modal('show');
		});
	};

	$scope.confirmDelete = function (id) {
		RubricaDestinatarioEsterno.delete({id: id},
			function () {
				$scope.loadAll();
				$('#deleteRubricaDestinatarioEsternoConfirmation').modal('hide');
				$scope.clear();
			});
	};
	$scope.load($stateParams.id);

});

angular.module('cifra2gestattiApp')
.controller('AooAssociaGruppoRuoloController', function ($scope,$stateParams,  Aoo,   $log, GruppoRuolo, Ruolo ) {

	$scope.toggle = function(scope) {
		scope.toggle();
	};

	$scope.edit = function( entity ) {
		GruppoRuolo.get({id:entity.id},function(result){
			$scope.gruppoRuoloEdit = result;
			entity.edit=true;
			$scope.gruppoRuoloEdit.aoo = {id:$scope.aoo.id };
			$scope.editing = true;
		});

		$log.debug(entity);
		
	};

	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
		$scope.editing = false;
	};

	$scope.salvaGruppoRuolo= function( gruppoRuolo  ) {
		$log.debug(gruppoRuolo);
		if(gruppoRuolo.id !== null ){
			GruppoRuolo.update( gruppoRuolo, function(result) {
					$scope.load( $scope.aoo.id );
			});

		}else{
			GruppoRuolo.save( gruppoRuolo, function(result) {
					$scope.load( $scope.aoo.id );
			});
		}
	};
	
	$scope.addGruppoRuolo= function( ) {
		$scope.gruppoRuoloEdit ={ dataType:'gruppoRuolo', aoo:{id:$scope.aoo.id } ,edit:true };
		$scope.editing = true;
	};
 


	$scope.load = function (id) {
		$scope.editing = false;
		Aoo.get({id: id}, function(result) {
			$scope.aoo = result;
			$scope.aoos = [];
			$scope.aoos.push($scope.aoo);
		});
		$scope.gruppoRuolos = GruppoRuolo.getByAooId({aooId:id });
	};

	$scope.load($stateParams.id);
	$scope.ruolos = Ruolo.query();

});

angular.module('cifra2gestattiApp')
.controller('AooAssociaProfiloController', function ($scope, $stateParams,  Aoo,   $log, Profilo, TipoAtto,Utente ,GruppoRuolo, QualificaProfessionale) {



    $scope.attiva = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#attivaUtenteConfirmation').modal('show');
            });
     };

     $scope.confirmAttiva = function (utente) {
            Utente.attiva( $scope.utente ,
                function () {
                    $scope.search();
                    $('#attivaUtenteConfirmation').modal('hide');
                    $scope.clear();
                });
        };


	$scope.search = function( ) {
		$scope.utentes = Profilo.search({}, $scope.criteriaUtente );
	};


	$scope.toggle = function(scope) {
		scope.toggle();
	};

	$scope.editing = false;
	$scope.edit = function( entity ) {
		Profilo.get({id:entity.id},function(result){
			$scope.profiloEdit = result;
			entity.edit=true;
			$scope.editing = true;
		});

		$log.debug(entity);
		
	};	

	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
		$scope.editing=false;
		$scope.profiloEdit = null;
	};

	$scope.salvaProfilo= function( profiloEdit   ) {
		$log.debug( profiloEdit);

		if(profiloEdit.id !== null ){
			Profilo.update(profiloEdit, function(result) {
					$scope.gestioneProfilo( profiloEdit.utente.id );
			});

		}else{
			Profilo.save( profiloEdit, function(result) {
					$scope.gestioneProfilo( profiloEdit.utente.id );
			});
		}
	};
	
	$scope.addProfilo = function( ) {
		$scope.editing = true;
		$log.debug("addProfilo");
		$scope.profiloEdit = {descrizione:'', dataType:'profilo', aoo:{id:$scope.aoo.id }, utente: {id:$scope.utente.id, username:$scope.utente.username } ,  edit:true } ;
	};
 
   

	 $scope.showUpdate = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#saveUtenteModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.utente.id != null) {
                Utente.update($scope.utente,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Utente.save($scope.utente,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $('#deleteUtenteConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Utente.delete({id: id},
                function () {
                    $scope.search();
                    $('#deleteUtenteConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        
        $scope.refresh = function () {
            $scope.search();
            $scope.editing = false;
            $('#saveUtenteModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.utente = {cognome: null, nome: null, username: null, codicefiscale: null, validodal: null, validoal: null, telefono: null, altrorecapito: null, fax: null, email: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };

	 $scope.gestioneProfilo = function (id) {
	 		$scope.editing = false;
	 		
            Utente.get({id: id}, function(result) {
                $scope.utente = result;
                $scope.gestioneProfiloEdit=true;

                $scope.utentes =[];
                $scope.utentes.push($scope.utente);
               });

			Utente.hasProfili({id: id,aooId:  $stateParams.id }, function(result) {
                $scope.hasProfili = result;
               });

            $scope.tipoattos = TipoAtto.query();
			$scope.qualificaprofessionales = QualificaProfessionale.getOnlyEnabled();
    };


	$scope.load = function (id) {
		$scope.editing = false;
		Aoo.get({id: id}, function(result) {
			$scope.aoo = result;
			$scope.aoos = [];
			$scope.aoos.push($scope.aoo);
		});

		$scope.criteriaUtente= {aooId:$stateParams.id};
		$scope.search();
	
		$scope.gruppoRuolos = GruppoRuolo.getByAooId({aooId:id });
	};

	$scope.load($stateParams.id);
	
	$log.debug("$stateParams.utenteId:"+$stateParams.utenteId);
 	if( angular.isDefined( $stateParams.utenteId ) ){
 		$scope.gestioneProfilo($stateParams.utenteId);
 	}


});

angular.module('cifra2gestattiApp')
.controller('AooAssociaUfficioController', function ($scope,$stateParams,  Aoo,   $log, Ufficio,Profilo ) {



	$scope.toggle = function(scope) {
		scope.toggle();
	};


	$scope.editing = false;
	$scope.edit = function( entity ) {
		Ufficio.get({id:entity.id},function(result){
			$scope.ufficioEdit = result;
			entity.edit=true;
			$scope.editing = true;
		});

		$log.debug(entity);
		
	};	

	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
		$scope.editing=false;
		$scope.ufficioEdit = null;
	};


	$scope.salvaUfficio= function( ufficio ) {
		$log.debug(ufficio);
		if(ufficio.id !== null ){
			Ufficio.update( ufficio, function(result) {
					$scope.load( $scope.aoo.id );
			});

		}else{
			Ufficio.save( ufficio, function(result) {
					$scope.load( $scope.aoo.id );
			});
		}
	};
	
	$scope.addUfficio = function( ) {
		$scope.ufficioEdit =  {descrizione:'', dataType:'uffici', aoo: $scope.aoo ,edit:true } ;    
		$scope.editing = true;
	};
 


$scope.load = function (id) {
		$scope.editing = false;
		Aoo.get({id: id}, function(result) {
			$scope.aoo = result;
			$scope.aoos = [];
			$scope.aoos.push($scope.aoo);
		});

		$scope.ufficios = Ufficio.getByAooId({aooId:id });
		$scope.profilos = Profilo.getByAooId({aooId:id });

	};

 
	$scope.load($stateParams.id);
});





angular.module('cifra2gestattiApp')
.controller('AooAssociaTipoMateriaController', function ($scope,$stateParams,  Aoo,   $log, TipoMateria, Materia, SottoMateria) {


	$scope.toggle = function(scope) {
		scope.toggle();
	};


	$scope.edit = function( entity ) {
		$log.debug(entity);
		entity.edit=true;
	};	
	$scope.cancelEdit = function( entity ) {
		$log.debug(entity);
		entity.edit=false;
	};

	$scope.salvaTipoMateria = function( tipoMateria ) {
		$log.debug(tipoMateria);
		if(tipoMateria.id !== null ){
			TipoMateria.update( tipoMateria, function(result) {
					$scope.load( $scope.aoo.id );
			});

		}else{
			TipoMateria.save( tipoMateria, function(result) {
					$scope.load( $scope.aoo.id );
			});
		}
	};
	
	$scope.salvaMateria = function( materia  ) {
		$log.debug(materia);
		if(materia.id !== null ){
			Materia.update( materia, function(result) {
				$scope.load( $scope.aoo.id );
			});
		}else{
			Materia.save( materia, function(result) {
				$scope.load( $scope.aoo.id );

			});
		}

		
	};

	$scope.salvaSottoMateria = function( sottoMateria  ) {
		$log.debug(sottoMateria);
		if(sottoMateria.id !== null ){
			SottoMateria.update( sottoMateria, function(result) {
				$scope.load( $scope.aoo.id );
			});
		}else{
			SottoMateria.save( materia, function(result) {
				$scope.load( $scope.aoo.id );

			});
		}
	};



	$scope.addTipomateria = function( ) {
		if( !angular.isDefined( $scope.tipoMaterie) ||  $scope.tipoMaterie === null ){
			$scope.tipoMaterie = [];
		}

		$scope.tipoMaterie.push( {descrizione:'', dataType:'tipoMateria', aoo:{id:$scope.aoo.id } ,edit:true } );    
	};


	$scope.addMateria = function( tipoMateria ) {
		if(!angular.isDefined(tipoMateria.materie) || tipoMateria.materie === null ){
			tipoMateria.materie=[];
		}

		tipoMateria.materie.push( {descrizione:'', tipoMateria: { id: tipoMateria.id}   , aoo:{id:$scope.aoo.id } , dataType:'materia',edit:true  } );    

	};



	$scope.addSottoMateria = function( materia ) {
		$log.debug("addSottoMateria .materie:"+materia);
		if( !angular.isDefined( materia.sottoMaterie) ||  materia.sottoMaterie === null ){
			materia.sottoMaterie = [];
		}

		materia.sottoMaterie.push( {descrizione:'', materia: {id:materia.id}  , aoo:{id:$scope.aoo.id } , dataType:'sottoMateria' ,edit:true  } );    
	};






	$scope.load = function (id) {
		Aoo.get({id: id}, function(result) {
			$scope.aoo = result;
			$scope.tipoMaterie = TipoMateria.gestione({aooId:$scope.aoo.id});
		});
	};
	$scope.load($stateParams.id);









	$scope.options = {
		accept: function(sourceNode, destNodes, destIndex) {
			var data = sourceNode.$modelValue;
			var destType = destNodes.$element.attr('data-type');

			return (data.dataType == destType);  
		},
		dropped: function(event) {

			var sourceNode = event.source.nodeScope;
			var destNodes = event.dest.nodesScope;

			$log.debug(event);

        // //update changes to server
        // if (destNodes.isParent(sourceNode)
        //   && destNodes.$element.attr('data-type') == 'category') { // If it moves in the same group, then only update group
	var group = destNodes.$nodeScope.$modelValue;

	$log.debug( group.dataType );

        //   group.save();
        // } else { // save all
        //   $scope.saveGroups();
        // }


    },
    beforeDrop: function(event) {
        // if (!window.confirm('Are you sure you want to drop it here?')) {
        //   event.source.nodeScope.$$apply = false;
        // }
    }
};

});


