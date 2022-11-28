'use strict';

angular.module('cifra2gestattiApp')
	.controller('SedutaGiuntaDetailController',
		function ($scope,$filter,moment,Esito,VersioneComposizioneGiunta,VersioneComposizioneConsiglio, sharedSedutaFactory,SedutaGiuntaConstants,RoleCodes,ValVotazioni,ngToast,$rootScope,$state,$q,$stateParams,
				  SedutaGiunta,$log,TipoOdg,Profilo,Assessorato,Atto,ModelloHtml,OrdineGiorno,QualificaProfessionale,Upload,TaskDesktop,ArgumentsOdgService,
				  $timeout,ModelloCampo,ConvertService,$sce,Verbale,ParseLinks,DocumentoInformatico,Aoo,Utente,ProfiloAccount, TipoAtto) {

			var $translate = $filter('translate');

			$scope.todayMidnight = new moment();
			$scope.todayMidnight = $scope.todayMidnight.set({hour:0,minute:0,second:0,millisecond:0});
			$scope.endOfToday = new moment();
			$scope.endOfToday = $scope.endOfToday.set({hour:23,minute:59,second:59,millisecond:999});
			$scope.isOperatoreSegreteriaConsiglio = sharedSedutaFactory.isOperatoreSegreteriaConsiglio();
			$scope.isOperatoreSegreteriaGiunta = sharedSedutaFactory.isOperatoreSegreteriaGiunta();
			// Non mostra lo spinner nella gestione scenari di disabilitazione
			$scope.disableSpinner=true;

			if ($scope.sedutaAttoId) {
				$scope.id = $scope.sedutaAttoId;
			}
			else {
				$scope.id = $stateParams.id;
			}

			// Ordine atti ODG
			$scope.tipiAtto = ['QT','VERB','COM','DAT','DC-DPC-DIC','INT','ODG','MZ','RIS'];
			$scope.tipiAttoGiuntaOrder = ['DG','DIG','DPC'];

			$scope.existsDocVerbali = null;
			$scope.existsDocDefEsito = null;
			$scope.allAttos = [];
			$scope.maxNArg = 0;
			$scope.sedutaGiunta = {id:null};
			$scope.decisioni = [];
			$scope.profilos =[];
			$scope.atti = [];
			// $scope.attiodg = [];
			$scope.tipiAttoIds = [];
			$scope.rubricaToImport = {};
			$scope.modelloHtmls = ModelloHtml.query();
			$scope.modelloHtmlsOdg=[];
			$scope.solaLettura = false;
			$scope.solaLetturaVerbale = true;
			$scope.dtoWorkflow= {};
			$scope.dtoWorkflow.campi=[];
			$scope.modificaEstremi = false;
			$scope.annullaEstremi = false;
			$scope.variazioneDefault = false;
			$scope.odgSelected = null;
			$scope.sedutaConstants = SedutaGiuntaConstants;
			$scope.documentiFirmatiDaCaricare = new Map();
			$scope.relatori = ["presidente","vicepresidente","assessore"];
			$scope.esiti = [];
			$scope.esitiAll = [];
			$scope.versioniComposizioneGiunta = [];
			$scope.versioniComposizioneConsiglio = [];
			$scope.versioneComposizioneSelezionata = {};
			$scope.idProfiloSottoscrittoreVerbale = null;
			$scope.showSezioneVerbale = null;
			$scope.showSezioneResoconto = 'sottoscrittoriResoconto';
			$scope.resocontoPubblicato = false;
			$scope.minDataChiusura = null;
			$scope.minDataFine = null;
			$scope.erroreCongruenzaPresentiNumVoti = false;

			$scope.profilosPresidente = [];
			$scope.profilosVicePresidente = [];
			$scope.profilosComponentiSeduta = [];

			$scope.scenariDisabilitazione = [];

			$scope.lovSiNo = [
				{"id":true, "label":"SI"},
				{"id":false, "label":"NO"}
			];
			$scope.valVotazioni = ValVotazioni;

			$scope.sottoscrittoreResoconto = null;
			$scope.sottoscrittorePresenze = null;
			$scope.listaAnnulla = null;
			$scope.generazionemassiva = false;
			$scope.dettResocontoVisibile = false;
			$scope.ammetteImmediataEseguibilita = false;
			$scope.visibilitaPresenza = false;
			$scope.visibilitaVotazione = false;

			$scope.viewImmediataEseguibilita = false;

			$scope.attiInseribiliList = [];
			$scope.attiInseritiList = [];
			//	$scope.sezioneIdx;

			$scope.attoNewPosition;

			$scope.nuovoNumeroSeduta;

			var selectedAttoOrder;
			$scope.positonError = false;
			var taskDescrizionePrefix = 'Mancanza ';

			$scope.verificaPresentiNumVoti = function(){
				if($scope.resoconto && $scope.resoconto.votazioneSegreta){
					let presenti = ($scope.resoconto && $scope.resoconto.voto && $scope.resoconto.voto.segreto && $scope.resoconto.voto.segreto.presenti && !isNaN($scope.resoconto.voto.segreto.presenti) ? Number($scope.resoconto.voto.segreto.presenti) : 0);
					let favorevoli = ($scope.resoconto && $scope.resoconto.voto && $scope.resoconto.voto.segreto && $scope.resoconto.voto.segreto.favorevoli && !isNaN($scope.resoconto.voto.segreto.favorevoli) ? Number($scope.resoconto.voto.segreto.favorevoli) : 0);
					let contrari = ($scope.resoconto && $scope.resoconto.voto && $scope.resoconto.voto.segreto && $scope.resoconto.voto.segreto.contrari && !isNaN($scope.resoconto.voto.segreto.contrari) ? Number($scope.resoconto.voto.segreto.contrari) : 0);
					let astenuti = ($scope.resoconto && $scope.resoconto.voto && $scope.resoconto.voto.segreto && $scope.resoconto.voto.segreto.astenuti && !isNaN($scope.resoconto.voto.segreto.astenuti) ? Number($scope.resoconto.voto.segreto.astenuti) : 0);
					let presentiNonVotanti = ($scope.resoconto && $scope.resoconto.voto && $scope.resoconto.voto.segreto && $scope.resoconto.voto.segreto.presentiNonVotanti && !isNaN($scope.resoconto.voto.segreto.presentiNonVotanti) ? Number($scope.resoconto.voto.segreto.presentiNonVotanti) : 0);

					if(presenti !== (favorevoli + contrari + astenuti + presentiNonVotanti)){
						$scope.erroreCongruenzaPresentiNumVoti = true;
					}else{
						$scope.erroreCongruenzaPresentiNumVoti = false;
					}
				}else{
					$scope.erroreCongruenzaPresentiNumVoti = false;
				}
			};
			$scope.eaNumeriProgressivi = function(){
				$scope.taskLoading = true;
				var nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
				var attiSelezionati = [];
				for(var i = 0; i < nodes.length; i++){
					if(nodes[i].azioneNumera){
						attiSelezionati.push(nodes[i].id);
					}
					nodes[i].azioneNumera = false;
				}
				if(attiSelezionati.length < 1){
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Nessun atto selezionato tramite il checkbox \"Numera\".'});
				}else{
					$('#genericMessage').modal('hide');
					SedutaGiunta.setArgomentiProgressivi({'sedutaId': $scope.sedutaGiunta.id}, attiSelezionati, function(obj){
						ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
						$scope.refresh();
						$scope.taskLoading = false;
					}, function(err){
						$scope.taskLoading = false;
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Si è verificato un problema, si prega di riprovare.'});
					});
				}
			};

			$scope.eaStessoNumero  = function(){
				$scope.taskLoading = true;
				var nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
				var attiSelezionati = [];
				for(var i = 0; i < nodes.length; i++){
					if(nodes[i].azioneNumera){
						attiSelezionati.push(nodes[i].id);
					}
					nodes[i].azioneNumera = false;
				}
				if(attiSelezionati.length < 1){
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Nessun atto selezionato tramite il checkbox \"Numera\".'});
				}else{
					$('#genericMessage').modal('hide');
					SedutaGiunta.setStessoNumeroArgomento({'sedutaId': $scope.sedutaGiunta.id}, attiSelezionati, function(r){
						ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
						$scope.refresh();
						$scope.taskLoading = false;
					}, function(err){
						$scope.taskLoading = false;
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Si è verificato un problema, si prega di riprovare.'});
					});
				}
			};

			$scope.eaReset = function(confermed){
				var nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
				var attiSelezionati = [];
				for(var i = 0; i < nodes.length; i++){
					if(nodes[i].azioneResetNumerazione){
						attiSelezionati.push(nodes[i].id);
					}
				}
				if(attiSelezionati.length < 1){
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Nessun atto selezionato tramite il checkbox \"Reset num. argomento\".'});
				}else{
					$scope.taskLoading = true;
					var updateArray = [];
					for(var i = 0; i < nodes.length; i++){
						var toDelete = attiSelezionati.indexOf(nodes[i].id) > -1;
						var obj = {'numArg':nodes[i].numeroArgomento, 'id': nodes[i].id, 'toDelete': toDelete};
						updateArray.push(obj);
					}
					SedutaGiunta.resetNumeriArgomento({'sedutaId': $scope.sedutaGiunta.id, 'confermed' : confermed}, updateArray, function(r){
						if(r && r.done){
							for(var j = 0; j < nodes.length; j++){
								nodes[j].azioneResetNumerazione = false;
							}
							$('#genericMessage').modal('hide');
							$scope.taskLoading = false;
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$scope.refresh();
						}else if(r && r.toConferm){
							var noFunction = function(){
								$('#genericMessage').modal('hide');
							}
							var message = "Attenzione il sistema ha rilevato che il reset dei numeri di argomento " + r.toConferm + " può provocare dei buchi nella numerazione.";
							$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true, siFunction: $scope.eaConfirmReset, noFunction:noFunction, body: message + "<br/> Procedere comunque?"});
						}else{
							$rootScope.showMessage({title:'Attenzione', okButton:true, body: (r && r.error ? r.error : 'Si è presentato un errore')});
						}
						$scope.taskLoading = false;
					}, function(err){
						$scope.taskLoading = false;
						$('#genericMessage').modal('hide');
					});
				}
			};

			$scope.eaConfirmReset = function(){
				$scope.eaReset(true);
			};

			$scope.eaConfirmSalva = function(){
				$scope.eaSalva(true);
			};

			$scope.rimuoviNumArg = function(node){
				$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true,
					siFunction:function(){
						$scope.attoDaAggiornare = node;
						if(!node.argomentoExSeduta && $scope.isOperatoreResoconto && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneNumeroArgomento.label){
							$scope.attoNewPosition = null;
							$scope.ode = null;
						}
						$scope.eaSalva(true);
					},
					noFunction:function(){
						$('#genericMessage').modal('hide');
					},
					body: "Se si conferma l\u0027operazione il numero di argomento sar\u00E0 rimosso e dovr\u00E0 essere necessariamente reinserito in seguito.<br/>Procedere?"
				});
			}

			$scope.eaSalva = function(confermed){
				confermed = true;
				var nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
				$('#ordinamentoElencoArgomentoModal').modal('hide');
				var updateArray = [];
				if($scope.attoDaAggiornare){
					updateArray.push({'newNumArg': $scope.attoNewPosition, 'ode':($scope.ode ? true : false), 'numArg': $scope.attoDaAggiornare.numeroArgomento, 'id': $scope.attoDaAggiornare.id});
				}else{
					for(var i = 0; i < nodes.length; i++){
						if(nodes[i].azioneNumera){
							updateArray.push({'ode':(nodes[i].nargOde ? true : false), 'numArg':nodes[i].numeroArgomento, 'id': nodes[i].id});
						}
					}
				}
				if(updateArray && updateArray.length > 0){
					$scope.taskLoading = true;
					SedutaGiunta.salvaNumeriArgomento({'sedutaId': $scope.sedutaGiunta.id, 'confermed': confermed}, updateArray, function(res){
						if(res && res.done){
							$('#genericMessage').modal('hide');
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$scope.refresh();
						}else if(res && res.toConferm){
							var noFunction = function(){
								$('#genericMessage').modal('hide');
							}
							var message = "Attenzione il sistema ha rilevato che l\'aggiornamento del numero di argomento " + res.toConferm + " può provocare dei buchi nella numerazione.";
							$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true, siFunction: $scope.eaConfirmSalva, noFunction:noFunction, body: message + "<br/> Procedere comunque?"});
						}else if(res && res.errMax){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Non è possibile inserire un numero di argomento superiore a " + res.nextNum});
						}else{
							var errorMessage = "Impossibile assegnare la numerazione inserita.";
							if(res && res.err && res.err.length > 0){
								errorMessage = res.err;
							}
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:errorMessage});
						}
						$scope.taskLoading = false;
					}, function(err){
						$scope.taskLoading = false;
						/*$('#genericMessage').modal('hide');*/
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Si è verificato un problema, si prega di riprovare.'});
					});
				}else{
					$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Nessun atto selezionato'});
					$scope.taskLoading = false;
				}
			};

			$scope.azioniEA = [
				/*
			CDFATTICOASM-14
			{
				"codice" : "numeri_progressivi",
				"descrizione" : "Attribuisci num. argomento progressivo",
				"confirm":"Il sistema assegnerà un numero di argomento progressivo a tutti gli atti selezionati tramite il checkbox \"Numera\".",
				"operazione": $scope.eaNumeriProgressivi
			},
			{
				"codice" : "stesso_numero",
				"descrizione" : "Attribuisci stesso num. argomento",
				"confirm":"Il sistema assegnerà lo stesso numero di argomento, il primo progressivo disponibile, a tutti gli atti selezionati tramite il checkbox \"Numera\".",
				"operazione": $scope.eaStessoNumero
			},

			{
				"codice" : "reset",
				"descrizione" : "Reset num. argomento",
				"confirm":"Il sistema rimuoverà il numero di argomento a tutti gli atti selezionati tramite il checkbox \"Reset num.\".",
				"operazione": $scope.eaReset
			}
			*/

				/*
			{
				"codice" : "salva",
				"descrizione" : "Salva bozza numerazione",
				"confirm":"Il sistema memorizzerà la numerazione al momento assegnata in modo tale che, in un secondo momento, possa essere recuperata, modificata e completata.<br />N.B. L'assegnazione definitiva del numero di argomento avverrà in modo irreversibile soltanto al momento del passaggio alla registrazione degli esiti.",
				"operazione": $scope.eaSalva
			}
			*/
			];

			$scope.azioneElencoArgomenti = function(codiceAzione){
				$('#genericMessage').modal('hide');
				if(codiceAzione){
					var azioneEA = null;
					for(var i = 0; i < $scope.azioniEA.length; i++){
						if($scope.azioniEA[i].codice == codiceAzione){
							azioneEA = $scope.azioniEA[i];
							break;
						}
					}
					if(azioneEA){
						var noFunction = function(){
							$('#genericMessage').modal('hide');
						}
						$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true, siFunction: azioneEA.operazione, noFunction:noFunction, body: azioneEA.confirm + "<br/> Procedere?"});
					}else{
						$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Nessuna azione selezionata.'});
					}
				}
			};

			$scope.copiaGestioneSedutaToIE = function(){
				if(!$scope.qualifiche){
					$scope.qualifiche = {};
				}

				$scope.qualifiche.presidenteIE = $scope.qualifiche.presidenteFine ? [...$scope.qualifiche.presidenteFine] : [];
				$scope.qualifiche.segretarioIE = $scope.qualifiche.segretarioFine ? [...$scope.qualifiche.segretarioFine] : [];
				$scope.qualifiche.scrutatoreIE01 = $scope.qualifiche.scrutatore01 ? [...$scope.qualifiche.scrutatore01] : [];
				$scope.qualifiche.scrutatoreIE02 = $scope.qualifiche.scrutatore02 ? [...$scope.qualifiche.scrutatore02] : [];
				$scope.qualifiche.scrutatoreIE03 = $scope.qualifiche.scrutatore03 ? [...$scope.qualifiche.scrutatore03] : [];

				if($scope.resoconto.segretarioFine){
					if(!$scope.resoconto.segretarioIE){
						$scope.resoconto.segretarioIE = {};
					}
					$scope.resoconto.segretarioIE.profilo = $scope.resoconto.segretarioFine.profilo;
					$scope.resoconto.segretarioIE.qualifica = $scope.resoconto.segretarioFine.qualifica;
				}else{
					delete $scope.resoconto.segretarioIE;
				}

				if($scope.resoconto.presidenteFine){
					if(!$scope.resoconto.presidenteIE){
						$scope.resoconto.presidenteIE = {};
					}
					$scope.resoconto.presidenteIE.profilo = $scope.resoconto.presidenteFine.profilo;
					$scope.resoconto.presidenteIE.qualifica = $scope.resoconto.presidenteFine.qualifica;
				}else{
					delete $scope.resoconto.presidenteIE;
				}

				if($scope.resoconto.scrutatore01){
					if(!$scope.resoconto.scrutatoreIE01){
						$scope.resoconto.scrutatoreIE01 = {};
					}
					$scope.resoconto.scrutatoreIE01.profilo = $scope.resoconto.scrutatore01.profilo;
					$scope.resoconto.scrutatoreIE01.qualifica = $scope.resoconto.scrutatore01.qualifica;
				}else{
					delete $scope.resoconto.scrutatoreIE01;
				}

				if($scope.resoconto.scrutatore02){
					if(!$scope.resoconto.scrutatoreIE02){
						$scope.resoconto.scrutatoreIE02 = {};
					}
					$scope.resoconto.scrutatoreIE02.profilo = $scope.resoconto.scrutatore02.profilo;
					$scope.resoconto.scrutatoreIE02.qualifica = $scope.resoconto.scrutatore02.qualifica;
				}else{
					delete $scope.resoconto.scrutatoreIE02;
				}

				if($scope.resoconto.scrutatore03){
					if(!$scope.resoconto.scrutatoreIE03){
						$scope.resoconto.scrutatoreIE03 = {};
					}
					$scope.resoconto.scrutatoreIE03.profilo = $scope.resoconto.scrutatore03.profilo;
					$scope.resoconto.scrutatoreIE03.qualifica = $scope.resoconto.scrutatore03.qualifica;
				}else{
					delete $scope.resoconto.scrutatoreIE03;
				}
			};

			$scope.nomeFile = function(seduta) {
				if(seduta){
					return ($scope.sedutaGiunta.organo == 'G' ? "ODG" : "ODL")+"_Seduta_"+seduta.numero+".xls"
				}
			}

			$scope.loadRuoli = function (isSedutaGiunta) {
				var ruoliSer = "";
				if (isSedutaGiunta) {
					ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO;
				}
				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					$scope.profilos = result;
				});

				ruoliSer = "";
				if (isSedutaGiunta) {
					ruoliSer = RoleCodes.ROLE_COMPONENTE_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_COMPONENTE_CONSIGLIO;
				}

				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					//$log.debug("Assessorati:",result);
					$scope.profilosComponentiSeduta = [];

					// TODO: in questo caso serve ?
					for(var i = 0; i < result.length; i++){
						if(isSedutaGiunta && result[i].validoSedutaGiunta && result[i].validoSedutaGiunta == true &&
							$scope.filterDuplicatiAssessori($scope.profilosComponentiSeduta, result[i].id) == false){
							$scope.profilosComponentiSeduta.push(result[i]);
						}
						else if(!isSedutaGiunta && result[i].validoSedutaConsiglio && result[i].validoSedutaConsiglio == true &&
							$scope.filterDuplicatiAssessori($scope.profilosComponentiSeduta, result[i].id) == false){
							$scope.profilosComponentiSeduta.push(result[i]);
						}
					}

					if (isSedutaGiunta) {
						$scope.profilosComponentiSeduta = $filter('orderBy')($scope.profilosComponentiSeduta, ['ordineGiunta']);
					}else{
						$scope.profilosComponentiSeduta = $filter('orderBy')($scope.profilosComponentiSeduta, ['ordineConsiglio']);
					}
				});

				if (isSedutaGiunta) {
					ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO;
				}
				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					//$scope.profilosPresidente = result;
					$scope.profilosPresidente = [];
					if(result && result.length && result.length > 0){
						for(var i = 0; i < result.length; i++){
							if(isSedutaGiunta && result[i].validoSedutaGiunta && result[i].validoSedutaGiunta == true){
								$scope.profilosPresidente.push(result[i]);
							}
							else if(!isSedutaGiunta && result[i].validoSedutaConsiglio && result[i].validoSedutaConsiglio == true){
								$scope.profilosPresidente.push(result[i]);
							}
						}
					}
					/*
    			for(var i = 0; i < $scope.profilosPresidente.length; i++){
    				if($scope.sedutaGiunta.presidente == null && $scope.isCapoEnte($scope.profilosPresidente[i].grupporuolo.hasRuoli) == true){
    					$scope.sedutaGiunta.presidente = $scope.profilosPresidente[i];
    				}
    			}
    			*/
				});

				if (isSedutaGiunta) {
					ruoliSer = RoleCodes.ROLE_VICE_PRESIDENTE_SEDUTA_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_VICE_PRESIDENTE_SEDUTA_CONSIGLIO;
				}

				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					//$scope.profilosVicePresidente = result;
					$scope.profilosVicePresidente = [];
					if(result && result.length && result.length > 0){
					    
						for(var i = 0; i < result.length; i++){
							if (isSedutaGiunta && result[i].validoSedutaGiunta && result[i].validoSedutaGiunta == true) {
								$scope.profilosVicePresidente.push(result[i]);
							}
							else if(!isSedutaGiunta && result[i].validoSedutaConsiglio && result[i].validoSedutaConsiglio == true){
								$scope.profilosVicePresidente.push(result[i]);
							}
						}
					}

				});

				// Query per cercare i sottoscrittori possibili di un odg, ovvero
				// sia i presidenti che i segretari...
				if (isSedutaGiunta) {
					ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA + ',' + RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO + ',' + RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO;
				}

				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					$scope.profilosSottoscrittoriOdg = result;
				});

				if (isSedutaGiunta) {
					$scope.isOperatoreOdg = sharedSedutaFactory.isOperatoreOdgGiunta();
					$scope.isOperatoreResoconto = sharedSedutaFactory.isOperatoreResocontoGiunta();
				}
				else {
					$scope.isOperatoreOdg = sharedSedutaFactory.isOperatoreOdgConsiglio();
					$scope.isOperatoreResoconto = sharedSedutaFactory.isOperatoreResocontoConsiglio();
				}
			}
			if ($scope.id == 'nuova-giunta') {
				$scope.loadRuoli(true);
			}
			if ($scope.id == 'nuova-consiglio') {
				$scope.loadRuoli(false);
			}


//    	$scope.esclusi = ["rinviato","non_trattato","ritirato"];

			// Gestione Tab votazioni seduta
			$scope.switchInfo = function(val){
				$scope.infoTab = val;
			}

			$scope.qualifiche = {
				//"presidenteInizio": [],
				"presidenteFine": [],
				"presidenteIE": [],
				//"segretarioInizio": [],
				"segretarioFine": [],
				"segretarioIE": [],
				"scrutatore01": [],
				"scrutatore02": [],
				"scrutatore03": [],
				"scrutatoreIE01": [],
				"scrutatoreIE02": [],
				"scrutatoreIE03": []
			};

			$scope.setQualificheProfilo = function(membroSeduta, profiloId, isChanged){

				if (membroSeduta==null || $scope.resoconto[membroSeduta]==null) {
					return;
				}

				$scope.qualifiche[membroSeduta]=[];
				if($scope.resoconto[membroSeduta].qualifica==null){
					$scope.resoconto[membroSeduta].qualifica={};
					$scope.resoconto[membroSeduta].qualifica.id=null;
				}
				if(isChanged){
					if($scope.resoconto[membroSeduta].qualifica!=null){
						$scope.resoconto[membroSeduta].qualifica.id=null;
					}
				}

				if(profiloId!=undefined && profiloId!=null){
					QualificaProfessionale.getEnabledByProfiloId({profiloId: profiloId}, function(result) {
						$scope.qualifiche[membroSeduta]=result;
						if(
							$scope.qualifiche[membroSeduta]!=null &&
							$scope.qualifiche[membroSeduta].length==1
						){
							$scope.resoconto[membroSeduta].qualifica.id=$scope.qualifiche[membroSeduta][0].id;
						}
					});
				}
			};

			$scope.setListaScrutatori = function(){
				
				$scope.listaScrutatori = [];
				if($scope.versioneComposizioneSelezionata && $scope.versioneComposizioneSelezionata.profiliComposizione){
					for(var componente in $scope.versioneComposizioneSelezionata.profiliComposizione){
						var isSelected = false;
						if( !isSelected && $scope.resoconto.scrutatore01!=null && $scope.resoconto.scrutatore01.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatore01.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatore02!=null && $scope.resoconto.scrutatore02.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatore02.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatore03!=null &&  $scope.resoconto.scrutatore03.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatore03.profilo.id ){
							isSelected = true;
						}
						if(!isSelected && $scope.versioneComposizioneSelezionata.profiliComposizione[componente].valido
							&& $scope.versioneComposizioneSelezionata.profiliComposizione[componente].valido == true){
							$scope.listaScrutatori.push($scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo);
						}
					}
				}else{
					for ( var componente in $scope.resoconto.componenti) {
						
						var isSelected = false;
						if( !isSelected && $scope.resoconto.scrutatore01!=null && $scope.resoconto.scrutatore01.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatore01.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatore02!=null && $scope.resoconto.scrutatore02.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatore02.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatore03!=null &&  $scope.resoconto.scrutatore03.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatore03.profilo.id ){
							isSelected = true;
						}
						if($scope.isOdg() && !isSelected && $scope.resoconto.componenti[componente].profilo.validoSedutaGiunta
							&& $scope.resoconto.componenti[componente].profilo.validoSedutaGiunta == true){
							$scope.listaScrutatori.push($scope.resoconto.componenti[componente].profilo);
						}
						else if(!isSelected && $scope.resoconto.componenti[componente].profilo.validoSedutaConsiglio
							&& $scope.resoconto.componenti[componente].profilo.validoSedutaConsiglio == true){
							$scope.listaScrutatori.push($scope.resoconto.componenti[componente].profilo);
						}
					}
				}
			};

			$scope.setListaScrutatoriIE = function(){
				$scope.listaScrutatoriIE = [];
				
				if($scope.versioneComposizioneSelezionata && $scope.versioneComposizioneSelezionata.profiliComposizione){
					for(var componente in $scope.versioneComposizioneSelezionata.profiliComposizione){
						var isSelected = false;
						if( !isSelected && $scope.resoconto.scrutatoreIE01!=null && $scope.resoconto.scrutatoreIE01.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatoreIE01.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatoreIE02!=null && $scope.resoconto.scrutatoreIE02.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatoreIE02.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatoreIE03!=null &&  $scope.resoconto.scrutatoreIE03.profilo!=null &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo.id == $scope.resoconto.scrutatoreIE03.profilo.id ){
							isSelected = true;
						}
						if(!isSelected && $scope.versioneComposizioneSelezionata.profiliComposizione[componente].valido &&
							$scope.versioneComposizioneSelezionata.profiliComposizione[componente].valido == true){
							$scope.listaScrutatoriIE.push($scope.versioneComposizioneSelezionata.profiliComposizione[componente].profilo);

						}
					}
				}else{
					for ( var componente in $scope.resoconto.componenti) {
						var isSelected = false;
						if( !isSelected && $scope.resoconto.scrutatoreIE01!=null && $scope.resoconto.scrutatoreIE01.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatoreIE01.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatoreIE02!=null && $scope.resoconto.scrutatoreIE02.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatoreIE02.profilo.id ){
							isSelected = true;
						}
						if( !isSelected && $scope.resoconto.scrutatoreIE03!=null &&  $scope.resoconto.scrutatoreIE03.profilo!=null &&
							$scope.resoconto.componenti[componente].profilo.id == $scope.resoconto.scrutatoreIE03.profilo.id ){
							isSelected = true;
						}
						if($scope.isOdg() && !isSelected && $scope.resoconto.componenti[componente].profilo.validoSedutaGiunta &&
							$scope.resoconto.componenti[componente].profilo.validoSedutaGiunta == true){
							$scope.listaScrutatoriIE.push($scope.resoconto.componenti[componente].profilo);

						}else if(!isSelected && $scope.resoconto.componenti[componente].profilo.validoSedutaConsiglio &&
							$scope.resoconto.componenti[componente].profilo.validoSedutaConsiglio == true){
							$scope.listaScrutatoriIE.push($scope.resoconto.componenti[componente].profilo);
						}
					}
				}
				
				
				
			};

			$scope.verificaVotazione = function(indice, componente, isIE){
				for ( var index in $scope.resoconto.componenti) {
					if(componente.profilo.id==$scope.resoconto.componenti[index].profilo.id){
						if(!isIE && !componente.presente){
							$scope.resoconto.componenti[index].votazione=null;
							$scope.updateTotals();
						}else if(!isIE && !$scope.resoconto.componenti[index].votazione && !$scope.resoconto.votazioneSegreta){
							$scope.resoconto.componenti[index].votazione='FAV';
							$scope.updateTotals();
						}
						if(isIE && !componente.presenteIE){
							$scope.resoconto.componenti[index].votazioneIE=null;
							$scope.updateTotals();
						}else if(isIE && !$scope.resoconto.componenti[index].votazioneIE){
							$scope.resoconto.componenti[index].votazioneIE='FAV';
							$scope.updateTotals();
						}
						break;
					}
				}
			}
			$scope.impostaTutti = function(param){
				let operazione = param ? param : $scope.resoconto.impostaTutti;
				if(operazione=="P"){
					$scope.tuttiPresenti();
				}else if(operazione=="A"){
					$scope.tuttiAssenti();
				}else if(operazione=="PTC"){
					$scope.tuttiPresentiContrari();
				}
				else{
					$scope.svuotaVotazione();
				}
				$scope.verificaPresentiNumVoti();
				$scope.resoconto.impostaTutti = operazione;
			}

			$scope.impostaTuttiIE = function(){
				if($scope.resoconto.impostaTuttiIE=="P"){
					$scope.tuttiPresentiIE();
				}else if($scope.resoconto.impostaTuttiIE=="A"){
					$scope.tuttiAssentiIE();
				}else if($scope.resoconto.impostaTuttiIE=="PTC"){
					$scope.tuttiPresentiContrariIE();
				}
				else if($scope.resoconto.impostaTuttiIE=="C"){
					$scope.copiaVotazione();
				}else{
					$scope.svuotaVotazioneIE();
				}
			}

			$scope.tuttiPresenti = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presente = true;
					if(!$scope.resoconto || !$scope.resoconto.votazioneSegreta){
						$scope.resoconto.componenti[index].votazione = "FAV";
					}
				}
				if($scope.resoconto && $scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.segreto.presenti=$scope.resoconto.componenti.filter((c) => !c.isSegretarioFine && ! c.isSegretarioIE).length;
					$scope.resoconto.voto.segreto.favorevoli=$scope.resoconto.voto.segreto.presenti;
					$scope.resoconto.voto.segreto.assenti=0;
					$scope.resoconto.voto.segreto.contrari=0;
					$scope.resoconto.voto.segreto.astenuti=0;
					$scope.resoconto.voto.segreto.presentiNonVotanti=0;
				}else{
					$scope.updateTotals();
				}
				return false;
			}

			$scope.tuttiPresentiIE = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presenteIE = true;
					$scope.resoconto.componenti[index].votazioneIE = "FAV";
				}
				$scope.updateTotals();

				return false;
			}

			$scope.tuttiPresentiContrari = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presente = true;
					if(!$scope.resoconto || !$scope.resoconto.votazioneSegreta){
						$scope.resoconto.componenti[index].votazione = "CON";
					}
				}
				if($scope.resoconto && $scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.segreto.presenti=$scope.resoconto.componenti.filter((c) => !c.isSegretarioFine && ! c.isSegretarioIE).length;
					$scope.resoconto.voto.segreto.favorevoli=0;
					$scope.resoconto.voto.segreto.assenti=0;
					$scope.resoconto.voto.segreto.contrari=$scope.resoconto.voto.segreto.presenti;
					$scope.resoconto.voto.segreto.astenuti=0;
					$scope.resoconto.voto.segreto.presentiNonVotanti=0;
				}else{
					$scope.updateTotals();
				}

				return false;
			}

			$scope.tuttiPresentiContrariIE = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presenteIE = true;
					$scope.resoconto.componenti[index].votazioneIE = "CON";
				}
				$scope.updateTotals();

				return false;
			}

			$scope.tuttiAssenti = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presente = false;
					if(!$scope.resoconto || !$scope.resoconto.votazioneSegreta){
						$scope.resoconto.componenti[index].votazione = null
					}
				}

				if($scope.resoconto && $scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.segreto.presenti=0;
					$scope.resoconto.voto.segreto.favorevoli=0;
					$scope.resoconto.voto.segreto.assenti=$scope.resoconto.componenti.filter((c) => !c.isSegretarioFine && ! c.isSegretarioIE).length;
					$scope.resoconto.voto.segreto.contrari=0;
					$scope.resoconto.voto.segreto.astenuti=0;
					$scope.resoconto.voto.segreto.presentiNonVotanti=0;
				}else{
					$scope.updateTotals();
				}
				return false;
			}

			$scope.tuttiAssentiIE = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presenteIE = false;
					$scope.resoconto.componenti[index].votazioneIE = null
				}

				$scope.updateTotals();
				return false;
			}

			$scope.svuotaVotazione = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presente = null;
					$scope.resoconto.componenti[index].votazione = null
				}

				if($scope.resoconto && $scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.segreto.presenti=0;
					$scope.resoconto.voto.segreto.favorevoli=0;
					$scope.resoconto.voto.segreto.assenti=0;
					$scope.resoconto.voto.segreto.contrari=0;
					$scope.resoconto.voto.segreto.astenuti=0;
					$scope.resoconto.voto.segreto.presentiNonVotanti=0;
				}else{
					$scope.updateTotals();
				}
				return false;
			}

			$scope.svuotaVotazioneIE = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presenteIE = null;
					$scope.resoconto.componenti[index].votazioneIE = null
				}

				$scope.updateTotals();
				return false;
			}

			$scope.copiaVotazione = function(){
				for ( var index in $scope.resoconto.componenti) {
					$scope.resoconto.componenti[index].presenteIE = $scope.resoconto.componenti[index].presente;
					$scope.resoconto.componenti[index].votazioneIE = $scope.resoconto.componenti[index].votazione;
				}

				$scope.updateTotals();
				return false;
			}

			$scope.updateTotals = function(){

				if(!$scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.segreto = {};
					$scope.resoconto.voto.palese.presenti=0;
					$scope.resoconto.voto.palese.assenti=0;
					$scope.resoconto.voto.palese.favorevoli=0;
					$scope.resoconto.voto.palese.contrari=0;
					$scope.resoconto.voto.palese.astenuti=0;
					$scope.resoconto.voto.palese.presentiNonVotanti=0;
				}else{
					$scope.resoconto.voto.palese = {};
					$scope.resoconto.voto.segreto.presenti=0;
					$scope.resoconto.voto.segreto.assenti=0;
				}

				$scope.resoconto.voto.immediataEseguibilita.presenti=0;
				$scope.resoconto.voto.immediataEseguibilita.assenti=0;
				$scope.resoconto.voto.immediataEseguibilita.favorevoli=0;
				$scope.resoconto.voto.immediataEseguibilita.contrari=0;
				$scope.resoconto.voto.immediataEseguibilita.astenuti=0;
				$scope.resoconto.voto.immediataEseguibilita.presentiNonVotanti=0;

				var presenti = 0;
				var assenti = 0;
				var favorevoli = 0;
				var contrari = 0;
				var astenuti = 0;
				var presentiNonVotanti = 0;

				var presentiIE = 0;
				var assentiIE = 0;
				var favorevoliIE = 0;
				var contrariIE = 0;
				var astenutiIE = 0;
				var presentiNonVotantiIE = 0;

				for ( var index in $scope.resoconto.componenti) {

					if(
						//!$scope.resoconto.componenti[index].isSegretarioInizio &&
						!$scope.resoconto.componenti[index].isSegretarioFine  &&
						!$scope.resoconto.componenti[index].isSegretarioIE
					) {
						if($scope.resoconto.componenti[index].presente!=null){
							if($scope.resoconto.componenti[index].presente){
								++presenti;
							} else if(!$scope.resoconto.componenti[index].presente){
								++assenti;
							}
						}
						if($scope.resoconto.componenti[index].presenteIE!=null){
							if($scope.resoconto.componenti[index].presenteIE){
								++presentiIE;
							} else if(!$scope.resoconto.componenti[index].presenteIE){
								++assentiIE;
							}
						}
						switch ($scope.resoconto.componenti[index].votazione) {
							case "FAV":
								++favorevoli;
								break;
							case "CON":
								++contrari;
								break;
							case "AST":
								++astenuti;
								break;
							case "NPV":
								++presentiNonVotanti;
								break;
							default:
								break;
						}
						switch ($scope.resoconto.componenti[index].votazioneIE) {
							case "FAV":
								++favorevoliIE;
								break;
							case "CON":
								++contrariIE;
								break;
							case "AST":
								++astenutiIE;
								break;
							case "NPV":
								++presentiNonVotantiIE;
								break;
							default:
								break;
						}
					}
				}

				if(!$scope.resoconto.votazioneSegreta){
					$scope.resoconto.voto.palese.presenti=angular.copy(presenti);
					$scope.resoconto.voto.palese.assenti=angular.copy(assenti);
					$scope.resoconto.voto.palese.favorevoli=angular.copy(favorevoli);
					$scope.resoconto.voto.palese.contrari=angular.copy(contrari);
					$scope.resoconto.voto.palese.astenuti=angular.copy(astenuti);
					$scope.resoconto.voto.palese.presentiNonVotanti=angular.copy(presentiNonVotanti);
				}else{
					$scope.resoconto.voto.segreto.presenti=angular.copy(presenti);
					$scope.resoconto.voto.segreto.assenti=angular.copy(assenti);
					$scope.verificaPresentiNumVoti();
				}

				$scope.resoconto.voto.immediataEseguibilita.presenti=angular.copy(presentiIE);
				$scope.resoconto.voto.immediataEseguibilita.assenti=angular.copy(assentiIE);
				$scope.resoconto.voto.immediataEseguibilita.favorevoli=angular.copy(favorevoliIE);
				$scope.resoconto.voto.immediataEseguibilita.contrari=angular.copy(contrariIE);
				$scope.resoconto.voto.immediataEseguibilita.astenuti=angular.copy(astenutiIE);
				$scope.resoconto.voto.immediataEseguibilita.presentiNonVotanti=angular.copy(presentiNonVotantiIE);
			};

			$scope.stringEmpty = function(valore){
				if(angular.isDefined( valore ) &&
					valore!== null &&
					valore.trim().length > 0 ) {

					return false;
				}
				else{
					return true;
				}
			};

			Esito.query(function(result){
				$scope.esitiAll = result;
				//$log.debug($scope.esitiAll);
			});
			
			VersioneComposizioneGiunta.caricaComposizioni(function(result){
				$scope.versioniComposizioneGiunta = result;
			});
			
			VersioneComposizioneConsiglio.caricaComposizioni(function(result){
				$scope.versioniComposizioneConsiglio = result;
			});

			$scope.getEsitoLabel = function(id) {
				for(var i= 0; i<$scope.esitiAll.length; i++) {
					if($scope.esitiAll[i].id == id){
						return $scope.esitiAll[i].label;
					}
				}
				return "";
			};

			$scope.getEsitoObj = function(id) {
				for(var i= 0; i<$scope.esitiAll.length; i++) {
					if($scope.esitiAll[i].id == id){
						return $scope.esitiAll[i];
					}
				}
				return null;
			}

			$scope.checkVisResoconto = function() {
				$scope.dettResocontoVisibile = false;
				$scope.ammetteImmediataEseguibilita = false;
				$scope.visibilitaPresenza = false;
				$scope.visibilitaVotazione = false;
				if ($scope.resoconto && $scope.resoconto.esito) {
					$scope.dettResocontoVisibile = $scope.getEsitoObj($scope.resoconto.esito).registraVotazione;
					$scope.visibilitaPresenza = $scope.getEsitoObj($scope.resoconto.esito).visibilitaPresenza;
					$scope.ammetteImmediataEseguibilita = $scope.getEsitoObj($scope.resoconto.esito).ammetteIE;
					$scope.visibilitaVotazione = $scope.getEsitoObj($scope.resoconto.esito).visibilitaVotazione;
				}
			};

			$scope.checkAllAtti = function(sedutaOdg,filter){
				//$log.debug(typeof odg.selected != 'undefined' && odg.selected.length == odg.attos.length);
				let attos = null;
				let checkFunction = null;
				let checkParam = null;

				if(sedutaOdg.odgs){
					//si tratta della seduta
					attos = $scope.allAttos;
					checkFunction = $scope.checkAtto;
					checkParam = attos;
				}else{
					//si tratta di odg
					attos = sedutaOdg.attos;
					checkFunction = $scope.checkAttoOdg;
					checkParam = sedutaOdg;
				}

				let attiLength = attos.length;
				if(filter != undefined || filter == null){
					let attiFiltered = $filter('filter')(attos, {esito : filter});
					attiFiltered = $filter('filter')(attiFiltered, function(item,index,value){
						return !item.bloccoModifica;
					});
					attiLength = attiFiltered.length;
				}
				if(typeof sedutaOdg.selected != 'undefined' && sedutaOdg.selected.length && sedutaOdg.selected.length == attiLength){
					// selected
					sedutaOdg.selected = [];
				}
				else{
					// not selected
					sedutaOdg.selected = [];
					for(var i = 0; i < attos.length; i++){
						if(filter != undefined || filter == null){
							if(attos[i].esito == filter && !attos[i].bloccoModifica){
								checkFunction(checkParam, attos[i]);
							}
						}else{
							checkFunction(checkParam, attos[i]);
						}
					}
				}
				/*
				angular.forEach(odg.selected, function(item){
						$log.debug('selected',item.atto.codiceCifra);
				})
				*/

			};

			$scope.checkAttoOdg = function(odg, atto){
				// Verifico se può essere abilitato
				var checkEnable =($scope.abilitaRegistraResoconto(odg.attos) && atto.ultimoOdg);
				if (!checkEnable)  {
					return;
				}

				if(typeof odg.selected != 'undefined'){
					// var index = odg.selected.indexOf(id);
					var index = -1;
					for(var i = 0; i < odg.selected.length; i++){
						if(odg.selected[i].id == atto.id){
							index = i;
						}
					}

					if(index > -1){
						odg.selected.splice(index, 1);
					} else {
						odg.selected.push(atto);
					}
				} else{
					odg.selected = [atto];
				}
			};

			$scope.checkAtto = function(attos, atto){

				// Verifico se può essere abilitato
				var checkEnable =($scope.abilitaRegistraResoconto(attos) && atto.ultimoOdg);
				if (!checkEnable)  {
					return;
				}

				if(typeof $scope.sedutaGiunta.selected != 'undefined'){
					// var index = odg.selected.indexOf(id);
					var index = -1;
					for(var i = 0; i < $scope.sedutaGiunta.selected.length; i++){
						if($scope.sedutaGiunta.selected[i].id == atto.id){
							index = i;
						}
					}

					if(index > -1){
						$scope.sedutaGiunta.selected.splice(index, 1);
					} else {
						$scope.sedutaGiunta.selected.push(atto);
					}
				} else{
					$scope.sedutaGiunta.selected = [atto];
				}
			};

			/**
			 * creazione nuovo odg suppletivo o fuori sacco
			 */
			$scope.creaNuovoOdg = function(idtipoOdg){
				var odg = {id:null,
					oggetto: $scope.sedutaGiunta.organo == 'G' ? SedutaGiuntaConstants.statiOdg.odgInPredisposizione : SedutaGiuntaConstants.statiOdg.odlInPredisposizione,
					protocollo: null,
					dataPubblicazioneSito: null,
					idDiogene: null,
					sedutaGiunta:{id:$scope.sedutaGiunta.id},
					tipoOdg:{id:idtipoOdg},
					sottoscrittore: $scope.sedutaGiunta.presidente};

				if(idtipoOdg == 3 || idtipoOdg == 4 ){
					odg['progressivoOdgSeduta'] = $scope.generaProgressivoOdg($scope.sedutaGiunta,idtipoOdg) + 1;
				}
				OrdineGiorno.save(odg, function (result) {
					// $scope.ordineGiorno=result;
					ngToast.create(  { className: 'success', content: 'Creazione effettuata con successo' } );
					$scope.refresh();
				});
			};

			/**
			 * elimina odg suppletivo o fuori sacco
			 */
			$scope.eliminaOdg = function(odgId){

				OrdineGiorno.delete({id:odgId}, function (result) {
					// $scope.ordineGiorno=result;
					ngToast.create(  { className: 'success', content: 'Eliminazione effettuata con successo' } );
					$scope.refresh();
				});
			};

			/**
			 * aggiorna odg suppletivo o fuori sacco
			 */
			$scope.aggiornaOdg = function(odg){

				OrdineGiorno.update(odg, function (result) {
					// $scope.ordineGiorno=result;
					ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
					$scope.refresh();
				});

			};

			/**
			 * genera il progressivo per suppletivi e fuori sacco relativo ad ogni
			 * seduta
			 */
			$scope.generaProgressivoOdg= function(seduta,idtipoOdg){
				var num = 0;

				var odg = $filter('orderBy')(seduta.odgs, 'progressivoOdgSeduta', true,function(a,b){
					return (parseInt(a) > parseInt(b)) ? -1 : 1;
				});

				for(var i = 0; i < odg.length ; i++){
					if(odg[i].tipoOdg.id == idtipoOdg ){
						num = parseInt(odg[i].progressivoOdgSeduta);
						break;
					}
				}
				return num;
			}

			$scope.filterDuplicatiAssessori = function(lista, id){


				for(var i = 0; i< lista.length; i++){
					if(lista[i].id == id){
						return true;
					}
				}

				return false;
			};
			/**
			 * Seduta Init
			 */
			$scope.init = function() {
				$scope.resocontoPubblicato = false;
				$scope.tipiseduta = [{id:1,descrizione:"Ordinaria"},{id:2,descrizione:"Straordinaria"}];
				$scope.inizializzaSezioni();
				$scope.sezioneCorrente={};
				$scope.initSelezionaSezione(0);


				for(var i = 0; angular.isDefined($rootScope.profiloattivo) && angular.isDefined($rootScope.profiloattivo.tipiAtto) && i<$rootScope.profiloattivo.tipiAtto.length; i++){
					$scope.tipiAttoIds.push($rootScope.profiloattivo.tipiAtto[i].id);
				}

				$scope.importingSection = {};
				$scope.summernoteOptions = {
					height: 300,
					// width: 580,
					focus: false,
					airMode: false,
					lang: "it-IT",
					toolbar: [
						['edit',['undo','redo']],
						['fontclr', ['color']],
						['headline', ['style']],
						['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
						['fontface', ['fontname']],
						['textsize', ['fontsize']],

						['alignment', ['ul', 'ol', 'paragraph', 'lineheight']],
						['height', ['height']],
						['table', ['table']],
						/*['insert', ['picture','hr','pagebreak']],*/
						['insert', ['hr','pagebreak']],
						['view', ['fullscreen', 'codeview']],
						['cifra2', ['omissis','proprieta']]
					]
				};



			};

			$scope.isCapoEnte = function(ruoli) {
				for(var i = 0; i< ruoli.length; i++){
					if(ruoli[i].codice == RoleCodes.ROLE_SINDACO){
						return true;
					}
				}

				return false;
			}

			/* *** NON PIU' USATA ***
    	$scope.isSegretarioEnte = function(ruoli) {
    		var checkRole = RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO;
    		if ($scope.id == 'nuova-giunta' || $scope.sedutaGiunta.organo == 'G') {
    			checkRole = RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA;
    		}
    		for(var i = 0; i< ruoli.length; i++) {
    			if(ruoli[i].codice == checkRole){
    				return true;
    			}
    		}
    		return false;
    	}
    	*/

			$scope.abilitaModificaEstremi = function(){
				$scope.modificaEstremi = true;
				$scope.annullaEstremi = true;

				if ($scope.sedutaGiunta.secondaConvocazioneInizio == null && $scope.sedutaGiunta.secondaConvocazioneLuogo == null) {
					$scope.variazioneDefault = true;
				}

				if ($scope.sedutaGiunta.secondaConvocazioneInizio == null) {
					$scope.sedutaGiunta.secondaConvocazioneInizio = moment($scope.sedutaGiunta.primaConvocazioneInizio);
				}
				if ($scope.sedutaGiunta.secondaConvocazioneLuogo == null) {
					$scope.sedutaGiunta.secondaConvocazioneLuogo = $scope.sedutaGiunta.luogo + "";
				}

				$scope.loadSection(0);
			}

			$scope.annullaModificaEstremi = function(){
				// $scope.sedutaGiunta.secondaConvocazioneInizio = null;
				// $scope.sedutaGiunta.secondaConvocazioneLuogo = null;

				if ($scope.variazioneDefault) {
					$scope.sedutaGiunta.secondaConvocazioneInizio = null;
					$scope.sedutaGiunta.secondaConvocazioneLuogo = null;
				}

				$scope.variazioneDefault = false;
				$scope.modificaEstremi = false;
				$scope.annullaEstremi = false;
			}

			$scope.showSottoscrittoreOdg = function(odg) {
				var retValue = false;

				if (odg.oggetto != SedutaGiuntaConstants.statiOdg.odgInPredisposizione &&
					odg.oggetto != SedutaGiuntaConstants.statiOdg.odlInPredisposizione)
					retValue = true;

				return retValue;
			}
			$scope.showSottoscrittoreAnnull = function() {
				var retValue = false;

				if (angular.isDefined($scope.sedutaGiunta.stato) &&
					($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.ANNULLATA))
					retValue = true;

				// $log.debug("showSottoscrittoreAnnull:",retValue);
				return retValue;
			}


			$scope.loadProposteInseribiliInOdg = function(){
				// TODO: sistemare in base a giunta/consiglio
				$scope.criteria = {aooId:null, tipiAttoIds:$scope.tipiAttoIds} ;
				// $scope.criteria.page = 1;
				// $scope.criteria.per_page = 10;
				$scope.criteria.ordinamento = "";
				$scope.criteria.tipoOrinamento = "";
				// $scope.criteria.viewtype = 'tutti-nonpaginati';
				// $scope.criteria.profiloId = $rootScope.profiloattivo.id;
				// $scope.criteria.stato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg;


				TaskDesktop.searchOdg({page: 1, per_page: 10000, organo: $scope.sedutaGiunta.organo},
					$scope.criteria , function(result, headers) {
						$scope.atti = $filter('filter')(result, {stato : '!'+SedutaGiuntaConstants.statiAtto.propostaSospesa});
						$scope.atti = $filter('orderBy')($scope.atti, 'codiceCifra');
						//$log.debug("Atti inseribili:",$scope.atti);
						if($scope.isOdl()){
							$scope.filtraAttiIns($scope.atti,'','','',true, true,true, false);
						}
					});

			};

			$scope.initTree = function(){
				if( $scope.sedutaGiunta.fase != SedutaGiuntaConstants.fasiSeduta.CONCLUSA &&
					$scope.sedutaGiunta.fase != SedutaGiuntaConstants.fasiSeduta.ANNULLATA){
					$scope.loadProposteInseribiliInOdg();
				}
			};

			/**
			 * Carica la seduta
			 */
			$scope.load = function (id) {
				//$log.debug("Load seduta giunta");
				SedutaGiunta.get({id: id}, function(result) {

					$scope.resocontoPubblicato = false;
					if(result.resoconto != null && result.resoconto.length > 1){
						for(var i = 0; i < result.resoconto.length; i++){
							if((result.resoconto[i].tipo == 1 || result.resoconto[i].tipo == 0) &&
								result.resoconto[i].dataPubblicazioneSito != null){
								$scope.resocontoPubblicato = true;
							}
						}
					}

					$scope.sedutaGiunta = result;
					//$log.debug("SedutaGiunta:",$scope.sedutaGiunta);

					if ($scope.sedutaAttoId) {
						$scope.inizializzaOdgAtto();
					}

					$scope.inizializzaSezioni();
					$scope.sezioneCorrente={};
					//let sezioneCorrente = $scope.sezioneIdx != undefined ? $scope.sezioneIdx : 0;
					$scope.initSelezionaSezione($scope.indexSezioneCorrente);

					if( $scope.sedutaGiunta.primaConvocazioneInizio != null){
						$scope.sedutaGiunta.primaConvocazioneInizio = moment($scope.sedutaGiunta.primaConvocazioneInizio);
					}

					if( $scope.sedutaGiunta.primaConvocazioneFine != null){
						$scope.sedutaGiunta.primaConvocazioneFine = moment($scope.sedutaGiunta.primaConvocazioneFine);
					}

					if( $scope.sedutaGiunta.inizioLavoriEffettiva != null){
						$scope.sedutaGiunta.inizioLavoriEffettiva = moment($scope.sedutaGiunta.inizioLavoriEffettiva);
					}

					if( $scope.sedutaGiunta.secondaConvocazioneInizio != null){
						$scope.sedutaGiunta.secondaConvocazioneInizio = moment($scope.sedutaGiunta.secondaConvocazioneInizio);
					}

					if( $scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA ||
						$scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.CONCLUSA ||
						$scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.ANNULLATA) {
						$scope.abilitaSezioneOdg("datiOdgSuppletivo");
						$scope.abilitaSezioneOdg("datiOdgFuoriSacco");
					}
					else{
						$scope.disabilitaSezioneOdg("datiOdgSuppletivo");
						$scope.disabilitaSezioneOdg("datiOdgFuoriSacco");
					}

					$scope.loadRuoli($scope.sedutaGiunta.organo == 'G');

					$scope.updateMinChiusura();

// // TODO chiamata per controllo atto collegato
// for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
// for(var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++){
// var id = $scope.sedutaGiunta.odgs[i].attos[j].atto.id;
// $log.debug("Codice Atto Collegato:", id);
// $scope.indicei = i;
// $scope.indicej = j;
//
// Atto.collegato( { id: id}, function(data){
// $log.debug("SedutaGiunta Atto collegato:",data);
// $log.debug("test",$scope.sedutaGiunta.odgs[$scope.indicei]);
// $scope.sedutaGiunta.odgs[$scope.indicei].attos[$scope.indicej].atto.codicecifraAttoCollegato
// = data.id;
// $log.debug("SedutaGiunta COLL:",$scope.sedutaGiunta);
// });
// }
// }
					$scope.initTree();

					$scope.verificaSezioneResoconto();
					$scope.showSezioneResoconto = 'sottoscrittoriResoconto';

					$scope.verificaSezioneVerbale();
					if (!$scope.showSezioneVerbale){
						$scope.showSezioneVerbale = 'narrativaVerbale';
					}

					$scope.loadRubricaDestinatarioInterno();
				});
			};

			$scope.$watch('sedutaGiunta.primaConvocazioneInizio', function() {
				$scope.updateMinChiusura();
			});

			$scope.$watch('sedutaGiunta.secondaConvocazioneInizio', function() {
				$scope.updateMinChiusura();
			});
			$scope.$watch('sedutaGiunta.inizioLavoriEffettiva', function() {
				$scope.updateMinChiusura();
				$scope.updateMinFine();
			});

			$scope.updateMinChiusura = function(){
				if ($scope.sedutaGiunta.secondaConvocazioneInizio != null){
					$scope.minDataChiusura = $scope.sedutaGiunta.secondaConvocazioneInizio;
				}
				else{
					$scope.minDataChiusura = $scope.sedutaGiunta.primaConvocazioneInizio;
				}
			};


			$scope.updateMinFine = function(){

				$scope.minDataFine = $scope.minDataChiusura;
				if ($scope.sedutaGiunta.inizioLavoriEffettiva != null){
					$scope.minDataFine = $scope.sedutaGiunta.inizioLavoriEffettiva;
				}
			};


			$scope.verifyDataChiusura = function(inputDate){

				if($scope.resoconto.esito!=null) {
					if(! $scope.getEsitoObj($scope.resoconto.esito).registraVotazione) {
						return false;
					}
				}

				var inputClone = new Date();
				var minClone = new Date();
				if(angular.isDefined(inputDate)) {
					var dateVal = inputDate.toDate();

					inputClone.setFullYear(dateVal.getFullYear());
					inputClone.setMonth(dateVal.getMonth());
					inputClone.setDate(dateVal.getDate());
				}
				if(angular.isDefined($scope.minDataChiusura)){
					var minVal = $scope.minDataChiusura.toDate();

					minClone.setFullYear(minVal.getFullYear());
					minClone.setMonth(minVal.getMonth());
					minClone.setDate(minVal.getDate());
				}
				var retValue = false;

				if( angular.isDefined($scope.sedutaGiunta) &&
					angular.isDefined($scope.sedutaGiunta.primaConvocazioneFine) &&
					angular.isDefined($scope.minDataChiusura) &&
					angular.isDefined(inputClone)){

					inputClone.setHours(0);
					inputClone.setMinutes(0);
					inputClone.setSeconds(0);
					inputClone.setMilliseconds(0);

//        		$log.debug("inputDate ::", inputDate);
//        		$log.debug("inputClone ::", inputClone);
//        		$log.debug("minDataChiusura ::", $scope.minDataChiusura);
//        		$log.debug("maxDataChiusura ::", $scope.sedutaGiunta.primaConvocazioneFine);

					if (inputClone > $scope.sedutaGiunta.primaConvocazioneFine){
						retValue = true;
					}
					else{
						inputClone.setHours(1);
						inputClone.setMinutes(0);
						inputClone.setSeconds(0);
						inputClone.setMilliseconds(0);

						minClone.setHours(0);
						minClone.setMinutes(0);
						minClone.setSeconds(0);
						minClone.setMilliseconds(0);

						if (inputClone < minClone){
							retValue = true;
						}
					}
				}

				if(angular.isDefined($scope.sedutaGiunta) &&
					angular.isDefined($scope.sedutaGiunta.inizioLavoriEffettiva) &&
					angular.isDefined(inputClone)){
					inputClone.setHours(24);
					inputClone.setMinutes(0);
					inputClone.setSeconds(0);
					inputClone.setMilliseconds(0);
				 	if(inputClone < $scope.sedutaGiunta.inizioLavoriEffettiva){
						retValue = true;
					}
				}

				return retValue;
			}

			/**
			 * Salvataggio Seduta
			 */
			$scope.save = function () {
				if ($scope.sedutaGiunta.id != null) {

					for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
						$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
					}

					SedutaGiunta.update($scope.sedutaGiunta,
						function (result, headers) {
							ngToast.dismiss();
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$scope.refresh();
							$scope.taskLoading = false;
						});
				}
				else {
					if ($scope.id == 'nuova-giunta') {
						$scope.sedutaGiunta.organo = 'G';
					}
					if ($scope.id == 'nuova-consiglio') {
						$scope.sedutaGiunta.organo = 'C';
					}
					SedutaGiunta.save($scope.sedutaGiunta,
						function (result, headers) {
							var id = headers('id') ;
							$scope.sedutaGiunta.id = id;
							//$log.debug(  "id:"+id);
							ngToast.create(  { className: 'success', content: 'Creazione effettuata con successo' } );
							$state.go('sedutaGiuntaDetail', {id:id});
							$scope.taskLoading = false;
						});
				}
			};

			/**
			 * restituisce l'odg base della seduta selezionata
			 */
			$scope.getOdgBase=function (seduta){
				for(var i = 0; i<seduta.odgs.length; i++){
					if(seduta.odgs[i].tipoOdg.id == 1 || seduta.odgs[i].tipoOdg.id == 2)
						return seduta.odgs[i];
				}
			};

			/**
			 * controlla se la seduta o il singolo odg è in sola lettura
			 */
			$scope.isSolaLettura = function (odg) {
				if ($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.ANNULLATA) {
					return true;
				}
				if ($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA && angular.isUndefined(odg)) {
					return true;
				}

				var solaLettura = false;
				if($scope.sedutaGiunta.odgs != null && $scope.sedutaGiunta.odgs.length > 0){
					var odgBase =  $scope.getOdgBase($scope.sedutaGiunta);
					if(angular.isUndefined(odg) && odgBase.documentiPdf!= null && odgBase.documentiPdf.length > 0  &&
						!(odgBase.oggetto == SedutaGiuntaConstants.statiOdg.odgInPredisposizione || odgBase.oggetto == SedutaGiuntaConstants.statiOdg.odlInPredisposizione)){
						solaLettura = true;
					}
					if(angular.isDefined(odg) && odg != null && odg.documentiPdf!= null && odg.documentiPdf.length > 0 &&
						!(odg.oggetto == SedutaGiuntaConstants.statiOdg.odgInPredisposizione || odg.oggetto == SedutaGiuntaConstants.statiOdg.odlInPredisposizione)){
						solaLettura = true;
					}
				}
				return solaLettura;
			};

			/**
			 * controlla se il resoconto è in sola lettura
			 */

			$scope.isSolaLetturaResoconto = function (){
				return $scope.confermaEsito;
			}


			/**
			 * verifica se siamo nello stato in attesa di firma
			 */
			$scope.verificaShowButtonFirma = function (odg){
				var show = false;

				/*
        	 * FIRMA NON PREVISTA
        	 *
        	var idProfilo = angular.isDefined($rootScope.profiloattivo) ? $rootScope.profiloattivo.id : 0;

        	// var sottoscrittore = $scope.sedutaGiunta.presidente.id;
        	var sottoscrittore = 0;
        	if (angular.isDefined(odg.sottoscrittore) && odg.sottoscrittore != null)
        		sottoscrittore = odg.sottoscrittore.id;

        	if( $scope.isSolaLettura(odg) && (idProfilo == sottoscrittore) &&
        		( $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgBase ||
        		  $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdlBase ||
        		  odg.oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
        		  odg.oggetto == SedutaGiuntaConstants.statiOdg.odlInAttesaDiFirma)){
        		show = true;
        	}
        	*/
				return show;
			};

			/**
			 * verifica se siamo nello stato in attesa di firma doc variazione
			 */
			$scope.verificaShowButtonFirmaSeduta = function (odg,variazione){
				var show = false;

				/*
        	 * FIRMA NON PREVISTA
        	 *
        	var idProfilo = angular.isDefined($rootScope.profiloattivo) ? $rootScope.profiloattivo.id : 0;

        	var sottoscrittore = 0;
        	if(variazione == true){
        		sottoscrittore = (angular.isDefined($scope.sedutaGiunta.sottoscrittoreDocVariazione) &&	$scope.sedutaGiunta.sottoscrittoreDocVariazione != null) ?$scope.sedutaGiunta.sottoscrittoreDocVariazione.id:0;
        	} else {
        		sottoscrittore = (angular.isDefined($scope.sedutaGiunta.sottoscrittoreDocAnnullamento) && $scope.sedutaGiunta.sottoscrittoreDocAnnullamento != null) ?$scope.sedutaGiunta.sottoscrittoreDocAnnullamento.id:0;
        	}

        	if(variazione == true && sottoscrittore > 0 && $scope.isSolaLettura(odg) && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione && idProfilo == sottoscrittore ){
        		show = true;
        	}
        	if(variazione == false && sottoscrittore > 0 && $scope.isSolaLettura(odg) && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento && idProfilo == sottoscrittore ){
        		show = true;
        	}
        	*/
				return show;
			};

			/**
			 * verifica se può essere mostrato il button "Numera Atto"
			 */
			$scope.verificaShowButtonNumeraAtto = function (attos){
				var show = false;
				if(typeof attos != 'undefined'){
					for(var i = 0; i < attos.length; i++){
						if(attos[i].numerabile){
							show = true;
							break;
						}
					}
				}

				return show;
			};

			/**
			 * verifica se può essere mostrato il button "Numera Atto"
			 */
			/* In attico non previsto il pulsante per numerazione singola
        $scope.verificaShowButtonNumeraAttoSingolo = function (attoOdg){
        	var show = false;
        	if(typeof attoOdg != 'undefined'){
	        	if(attoOdg.numerabile){
	        		show = true;
	        	}
        	}

        	return show;
        };
        */

			$scope.verificaEnableButtonNumeraAtto = function (attos){
				var show = 0;
				for(var i = 0; i < attos.length; i++){
					if(attos[i].numerabile){
						show ++;
					}
				}

				if(show == attos.length){
					return true;
				} else {
					return false;
				}
			};

			/**
			 * verifica se può essere mostrato il button "Annulla Numerazione"
			 */
			/* Il processo attico non consente annullamento numerazione
        $scope.verificaShowButtonAnnullaNumeraAtto = function (odg){
        	var show = false;

        	for(var i = 0; i < odg.attos.length; i++){
        		if(odg.attos[i].esito != null && odg.attos[i].esito != '' && (odg.attos[i].esito == 'approvato' || odg.attos[i].esito == 'adottato')
        				&& odg.attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaNumerata ){
        			show = true;
        			break;
        		}
        	}
        	return show;
        };

        $scope.verificaShowButtonAnnullaNumeraAttoSingolo = function (attoOdg){
        	var show = false;
    		if(attoOdg.esito != null && attoOdg.esito != '' && (attoOdg.esito == 'approvato' || attoOdg.esito == 'adottato')
    				&& attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaNumerata ){
    			show = true;
    		}
        	return show;
        };

        $scope.verificaEnableButtonAnnullaNumeraAtto = function (attos){
        	var show = 0;
        	for(var i = 0; i < attos.length; i++){
        		if(attos[i].esito != null && attos[i].esito != '' && (attos[i].esito == 'approvato' || attos[i].esito == 'adottato')
        				&& attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaNumerata ){
        			show ++;
        		}
        	}

        	if(show == attos.length){
        		return true;
        	} else {
        		return false;
        	}
        };
        */

			/**
			 * verifica se può essere mostrato il button "Visualizza Atto"
			 */
			// Per Attico - dovrebbe essere sempre possibile visualizzare il dettaglio
			/*
        $scope.verificaShowButtonVisualizzaAtto = function (attoOdg){
        	var show = false;
        	// if(attoOdg.atto.codicecifraAttoCollegato != null){
        	if( (attoOdg.esito != null && attoOdg.esito != '' && (attoOdg.esito == 'verbalizzato' || attoOdg.esito == 'presa_d_atto') && (attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiGenerazione || attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaConEsito) ) ||
        		(attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaNumerata || attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiGenerazione)	){
    			show = true;
    		}
        	return show;
        };
        */

			/**
			 * verifica se può essere mostrato il button "Genera doc Atto"
			 */
			// Il processo attico prevede la generazione dei documenti in una fase successiva
			/*
        $scope.verificaShowButtonGeneraDoc = function (odg){
        	var show = false;
        	for(var i = 0; i < odg.attos.length; i++){
	        	if( odg.attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiGenerazione	){
	    			show = true;
	    			break;
	    		}
        	}

        	if(show == true){
        		$scope.sedutaGiunta.resoconto.forEach(function(res){
    				if(res.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma || res.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma){
    					show = false
    				}
    			});

        	}

        	if(show == true){
        		show = !($scope.isOperatoreResoconto == true && $scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0 && $scope.sedutaGiunta.resoconto.length < 3 && $scope.abilitaPulsantePresenzeAssenze() == false);
        	}


        	return show;
        };
        */

			/**
			 * verifica se può essere mostrato il button "Genera doc Atto"
			 */
			// Il processo attico prevede la generazione dei documenti in una fase successiva
			/*
        $scope.verificaShowButtonGeneraDocSingolo = function (attoOdg){
        	var show = false;
        	if( attoOdg.atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiGenerazione	){
    			show = true;
    		}
        	return show;
        };

        $scope.verificaEnableButtonGeneraDoc = function (attos){
        	var show = 0;
        	for(var i = 0; i < attos.length; i++){
	        	if( attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiGenerazione	){
	    			show ++;
	    		}
        	}

        	if(show == attos.length){
        		return true;
        	} else {
        		return false;
        	}
        };
        */

			$scope.disabledCampiVariazione = function() {
				var disabled = true;

				if($scope.modificaEstremi == true ||
					($scope.modificaEstremi == false && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.label)){
					disabled = false;
				}
				/*
			 * if($scope.modificaEstremi == false && $scope.sedutaGiunta.stato ==
			 * SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione){
			 * $log.debug("Disabilita"); return true; } else{ return false; }
			 */

				return disabled;
			}

			/**
			 * verifica se la seduta è nello stato in attesa gen doc variazione
			 */
			$scope.verificaGenDocVariazione = function() {
				if($scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.label){
					return true;
				}
				else{
					return false;
				}
			}

			/**
			 * verifica se la seduta è nello stato in consolidata
			 */
			$scope.verificaAnteprimaDocAnnull = function() {
				if($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA){
					return true;
				}
				else{
					return false;
				}
			}

			/**
			 * verifica se posso visualizzare il crea odg suppletivo - PER ATTICO COME FUORI SACCO
			 *
			 $scope.verificaCreaSuppletivo = function() {
        	if($scope.isOperatoreOdg == true && $scope.controlloOrario(3) &&
        			$scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConsolidata){
        		for(var i=0; i < $scope.sedutaGiunta.odgs.length; i++) {
        			if($scope.sedutaGiunta.odgs[i].tipoOdg.id == 3 &&
        					($scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
               				 $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInAttesaDiFirma ||
        					 $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInPredisposizione ||
        					 $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInPredisposizione))
        				return false;
        		}
        		return true;
        	}
        	else{
        		return false;
        	}
        }
			 */

			/**
			 * verifica se posso visualizzare il crea odg fuori sacco
			 */
			$scope.verificaCreaFuoriSaccoOrSuppletivo = function(idtipoOdg) {

				// Come concorato, possono essere creati un solo OdG/OdL Suppletivo e uno solo Fuori Sacco
				if (idtipoOdg != 3 && idtipoOdg != 4) {
					return false;
				}

				if ($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.ANNULLATA ||
					$scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.CONCLUSA) {
					return false;
				}

				// var filtered = $filter('filter')($scope.sedutaGiunta.odgs, {tipoOdg: {id: 4}});

				// if(filtered.length > 0){
				// Controllo Orario non utilizzato in ATTICO
				// && $scope.controlloOrario(4)
				if($scope.isOperatoreOdg == true) {
					// &&
					// In attico consentito prima della Firma
					// ($scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConsolidata ||
					// $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgBase)) {

					// Come concorato, possono essere creati un solo OdG/OdL Suppletivo e uno solo Fuori Sacco
					for(var i=0; i < $scope.sedutaGiunta.odgs.length; i++){
						if( $scope.sedutaGiunta.odgs[i].tipoOdg.id == idtipoOdg ) {
							$scope.odgSelected = $scope.sedutaGiunta.odgs[i];
							// $scope.sedutaGiunta.odgs[i].tipoOdg.id == 3 && (
							// $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInPredisposizione ||
							// $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInPredisposizione)) {
							// In CIFRA dopo la firma
							// ($scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
							return false;
						}
						/*
            			if( $scope.sedutaGiunta.odgs[i].tipoOdg.id == 4 && (
            				$scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInPredisposizione ||
        					$scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInPredisposizione)) {
            				// In CIFRA dopo la firma
            				// ($scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
            				return false;
            			}
            			*/
					}
					return true;
					// }
					// else{
					//	return false;
					// }
				}
				// else {
				//	return true;
				// }

				return false;
			}

			/*
		 * $scope.abilitaRubrica = function() { if($scope.sedutaGiunta.stato !=
		 * SedutaGiuntaConstants.statiSeduta.sedutaConsolidata &&
		 * $scope.sedutaGiunta.stato !=
		 * SedutaGiuntaConstants.statiSeduta.sedutaConclusa){ return true; }
		 * else{ return false; } }
		 */


			/*
         * CONTROLLO ORARIO NON ABILITATO IN ATTICO
         *
    	$scope.controlloOrario = function(tipoOdg,stato){

    		var inizio = angular.copy($scope.sedutaGiunta.primaConvocazioneInizio);
    		var adesso = new Date();
    		if($scope.sedutaGiunta.secondaConvocazioneInizio != null){
    			inizio = angular.copy($scope.sedutaGiunta.secondaConvocazioneInizio);
    		}

// inizio.setHours(inizio.getHours() - 1);

    		var differenza = ((adesso - inizio) / (1000 * 60 * 60));

    		if(tipoOdg == 3){
    			if(angular.isDefined(stato)){
    				if(stato != SedutaGiuntaConstants.statiOdg.odgInPredisposizione &&
    				   stato != SedutaGiuntaConstants.statiOdg.odlInPredisposizione){
    					return false;
    				}
    				else{
    					return true;
    				}
    			}
    			else if(differenza <= -1){
    				return true;
    			}
    			else{
    				return false;
    			}

    		}
    		else if((tipoOdg == 4) && (differenza >= 0) && ($scope.sedutaGiunta.primaConvocazioneFine == null)){
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
        */

			/**
			 * genera documento odg
			 */
			$scope.generaDocumentoOdg = function(){
				if($scope.odgSelected != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
					$scope.uploadok = true;
					$scope.param=[];
					$scope.param['odgId']=$scope.odgSelected.id;
					$scope.param['modelloId']=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
					if($scope.dtoWorkflow.campi['profiloSottoscrittore']){
						$scope.param['sottoscrittoreId']=$scope.dtoWorkflow.campi['profiloSottoscrittore'].id;
					}
					OrdineGiorno.generaDocOdg($scope.param,function (result, headers) {
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.uploadok = false;
						$('#mascheraWorkflow').modal('hide');
						$scope.refresh();
					});
				}
			};


			/**
			 * genera documento resoconto
			 */
			$scope.chiusuraResoconto = function() {
				SedutaGiunta.chiusuraResoconto({'sedutaId': $scope.sedutaGiunta.id}, {}, function (result, headers) {
					ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
					$('#mascheraWorkflow').modal('hide');
					$scope.refresh();
				});
			};


			/**
			 * genera documento resoconto
			 */
			$scope.generaDocumentoResoconto = function(tipo) {
				// Documento resoconto su tutti gli ODG
				// $scope.odgSelected != null &&
				if($scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
					$scope.uploadok = true;
					$scope.param=[];
					$scope.param['sedutaId']=$scope.sedutaGiunta.id;
					$scope.param['tipo']=tipo;
					$scope.param['modelloId']=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
					$scope.param['profiloId']=$rootScope.profiloattivo.id;
//        		$scope.param['sottoscrittoreId']=$scope.dtoWorkflow.campi['profiloSottoscrittore'].id;
					SedutaGiunta.generaDocResoconto($scope.param,function (result, headers) {
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.uploadok = false;
						$('#mascheraWorkflow').modal('hide');
						$scope.refresh();
					});
				}
			};

			/**
			 * pubblica documento resoconto
			 *
			 * NON PREVISTO IN ATTICO
			 *
			 $scope.pubblicaDocumentoResoconto = function(){
        	$scope.taskLoading = true;
    		$scope.param=[];
    		$scope.param['odgId']=$scope.odgSelected.id;
    		OrdineGiorno.pubblicaDocResoconto($scope.param,function (result, headers) {
        		ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$scope.resocontoPubblicato = true;
        		$scope.refresh();
            });
        };
			 */

			/**
			 * annulla seduta giunta
			 */
			$scope.annullaSedutaGiunta = function(){

				if($scope.sedutaGiunta != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
					$scope.uploadok = true;
					$scope.param={};
					$scope.param.sedutaId=$scope.sedutaGiunta.id;
					$scope.param.modelloId=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
					$scope.param.profiloId=$rootScope.profiloattivo.id;
					$scope.param.profiloSottoscrittoreId=$rootScope.profiloattivo.id;
					SedutaGiunta.annulla($scope.param, function () {
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.uploadok = false;
						$('#mascheraWorkflow').modal('hide');
						$scope.refresh();
						$scope.taskLoading = false;
					});
				}
			};

			/**
			 * annulla seduta giunta provvisoria
			 */
			$scope.annullaSedutaProvvisoria = function(){

				if($scope.sedutaGiunta != null){
					$scope.uploadok = true;
					$scope.param={};
					$scope.param.sedutaId=$scope.sedutaGiunta.id;
					$scope.param.profiloId=$rootScope.profiloattivo.id;
					$scope.param.profiloSottoscrittoreId=$rootScope.profiloattivo.id;
					SedutaGiunta.annulla($scope.param, function () {
						ngToast.create(  { className: 'success', content: 'Annullamento effettuato con successo' } );
						$scope.uploadok = false;
						$('#mascheraWorkflow').modal('hide');
						$scope.refresh();
						$scope.taskLoading = false;
					});
				}
			};

			$scope.generaVariazioneSeduta = function(){

				if($scope.sedutaGiunta != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
					$scope.uploadok = true;
					$scope.param={};
					$scope.param.sedutaId=$scope.sedutaGiunta.id;
					$scope.param.modelloId=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
					$scope.param.profiloId=$rootScope.profiloattivo.id;
					$scope.param.profiloSottoscrittoreId=$rootScope.profiloattivo.id;
					SedutaGiunta.variazione($scope.param,function () {
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.uploadok = false;
						$('#mascheraWorkflow').modal('hide');
						$scope.refresh();
						$scope.taskLoading = false;
					});
				}
			};

			$scope.firmaDocumentoResoconto = function(tipo){
				if($scope.firma) {
					//$log.debug("Firma documento resoconto - firma remota :start");
					//$log.debug('id del profilo ' + $rootScope.profiloattivo.id);
					//$log.debug('tipoDocumento ' + $scope.tipoDocumento);
					$scope.dtoFdr.codiceFiscale = $rootScope.profiloattivo.utente.codicefiscale;
					$scope.dtoFdr.errorMessage='';
					$scope.dtoFdr.errorCode='';
					// chiama ws di firma remota
					OrdineGiorno.firmaDocumentoResoconto({id: $scope.odgSelected.id,idResoconto:$scope.resocontoSelected.id,tipo: tipo, idProfilo:$rootScope.profiloattivo.id}, $scope.dtoFdr, function (result, headers) {
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							// $state.go('sedutaGiunta');
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
							$scope.taskLoading = false;
						},
						function(error) {
							$scope.uploadok = false;
							$scope.dtoFdr.password='';
							$scope.dtoFdr.otp='';
							$scope.dtoFdr.errorMessage=error.data.errorMessage;
							$scope.dtoFdr.errorCode=error.data.errorCode;
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
						});
				}
				// chiama il ws di upload file firmato
				else {

					//$log.debug($scope.documentiFirmatiDaCaricare);

					$scope.uploadDocumenti($scope.documentiFirmatiDaCaricare, "documenti_firmati")
						.then(function() {
							$scope.documentiFirmatiDaCaricare = new Map();
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
							$scope.taskLoading = false;
						});
				}
			};

			/**
			 * firma documento odg
			 */
			$scope.firmaDocumentoOdg = function(){
				if($scope.firma) {
					//$log.debug("Firma documento odg - firma remota :start");
					//$log.debug('id del profilo ' + $rootScope.profiloattivo.id);
					//$log.debug('tipoDocumento ' + $scope.tipoDocumento);
					$scope.uploadok = true;
					$scope.dtoFdr.codiceFiscale = $rootScope.profiloattivo.utente.codicefiscale;
					$scope.dtoFdr.errorMessage='';
					$scope.dtoFdr.errorCode='';

					// chiama ws di firma remota
					OrdineGiorno.firmaDocumento({id:$scope.odgSelected.id, idProfilo:$rootScope.profiloattivo.id}, $scope.dtoFdr, function (result, headers) {
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$scope.uploadok = false;
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
						},
						function(error) {
							$scope.uploadok = false;
							$scope.dtoFdr.password='';
							$scope.dtoFdr.otp='';
							$scope.dtoFdr.errorCode=error.data.errorCode;
							$scope.dtoFdr.errorMessage=error.data.errorMessage;
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
						});

				}

				// chiama il ws di upload file firmato
				else {
					$scope.uploadDocumenti($scope.documentiFirmatiDaCaricare, "documenti_firmati")
						.then(function() {
							$scope.documentiFirmatiDaCaricare = new Map();
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
							$scope.uploadok = false;
							$scope.taskLoading = false;
						});

				}

			}

			$scope.salvaDecisione = function(indexSezioneCorrente,decisioneCorrente) {
				//	$log.debug("decisioneCorrente:",decisioneCorrente);
				//$log.debug("indexSezioneCorrente:",indexSezioneCorrente);
				switch(decisioneCorrente.codice){
					case 'seduta-generadocodg':
						for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
							$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						}
						SedutaGiunta.update($scope.sedutaGiunta,
							function () {
								$scope.generaDocumentoOdg();
								$state.go('sedutaGiuntaDetail', {
									id:$stateParams.id
								});
							});

						break;
					case 'sedutavariazione-generadocodg':
						$scope.generaVariazioneSeduta();
						break;
					case 'seduta-firmadocodg':
						$scope.firmaDocumentoOdg();
						break;
					case 'sedutavariazione-firmadocodg':
						$scope.firmaDocumentoOdg();
						break;
					case 'seduta-firmadocann':
						$scope.firmaDocumentoOdg();
						break;
					case 'seduta-annulla':
						$scope.annullaSedutaGiunta();
						break;
					case 'seduta-provvisoria-annulla':
						$scope.annullaSedutaProvvisoria();
						break;
					case 'conferma-ordine-discussione':{
						let nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
						if(!nodes || !nodes.length || nodes.filter((n) => n.numeroDiscussione).length < nodes.length){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Prima di procedere occorre accedere nella sezione "Resoconto" alla scheda "Elenco Discussione" ed inserire l\'ordine di discussione su tutti gli atti.'});
						}else{
							for(let i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
								$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
							}
							if($scope.sedutaGiunta.organo == 'C'){
								$scope.aggiornaStatoSeduta(SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneNumeroArgomento.codice, 'up');
							}else if($scope.sedutaGiunta.organo == 'G'){
								$scope.aggiornaStatoSeduta(SedutaGiuntaConstants.statiSeduta.sedutaConclusaRegistrazioneEsiti.codice, 'up');
							}
						}
					}
						break;
					case 'modifica-ordine-discussione':
						$scope.aggiornaStatoSeduta(SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneOrdineDiscussione.codice, 'down');
						break;
					case 'assegna-numeri-adozione':
						$scope.chiusuraResoconto();
						break;
					case 'passto-registrazioni-esiti':{
						let nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
						if(!nodes || !nodes.length || nodes.filter((n) => n.numeroArgomento).length < nodes.length){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Prima di procedere occorre accedere nella sezione "Resoconto" alla scheda "Elenco Argomenti" ed inserire il numero di argomento su tutti gli atti.'});
						}else{
							$scope.aggiornaStatoSeduta(SedutaGiuntaConstants.statiSeduta.sedutaConclusaRegistrazioneEsiti.codice, 'up');
						}
					}
						break;
					case 'seduta-generadocresint':
						for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
							$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						}
						SedutaGiunta.update($scope.sedutaGiunta,
							function () {
								$scope.generaDocumentoResoconto("doc-definitivo-esito");
							});
						break;
					case 'seduta-generadocelencoverbali':
						for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
							$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						}
						SedutaGiunta.update($scope.sedutaGiunta,
							function () {
								$scope.generaDocumentoResoconto("doc-definitivo-elenco-verbali");
							});
						break;
// case 'seduta-generadocrespar':
// $scope.generaDocumentoResoconto("resoconto-parziale");
// break;
// case 'seduta-generadocpresenze':
//	$scope.generaDocumentoResoconto("presenze-assenze");
//	break;
					case 'seduta-generadocverbale':
						$scope.generaDocumentoVerbale();
						break;
					case 'seduta-firmadocverbale':
						$scope.taskLoading = true;
						$scope.firmaDocumentoVerbale();
						break;
					case 'seduta-firmadocres':
						$scope.firmaDocumentoResoconto('resoconto');
						break;
					case 'seduta-firmadocpresenze':
						$scope.firmaDocumentoResoconto('presenze-assenze');
						break;
				}

			};

			$scope.notEmptyDestinatariNotifiche = function (){
				var esito = false;

				if ($scope.sedutaGiunta.notificaTuttiAssessori != null && $scope.sedutaGiunta.notificaTuttiAssessori == true)
					esito = true;
				else if ($scope.sedutaGiunta.notificaTuttiConsiglieri != null && $scope.sedutaGiunta.notificaTuttiConsiglieri == true)
					esito = true;
				else if ($scope.sedutaGiunta.notificaTuttiAltreStrutture != null && $scope.sedutaGiunta.notificaTuttiAltreStrutture == true)
					esito = true;
				else if ($scope.sedutaGiunta.destinatariInterni != null && $scope.sedutaGiunta.destinatariInterni.length > 0)
					esito = true;
				else if ($scope.sedutaGiunta.rubricaSeduta != null && $scope.sedutaGiunta.rubricaSeduta.length > 0)
					esito = true;

				return esito;
			};

			$scope.callDecisioneOdg  = function (indexSezione,decisione,odg) {
				$scope.odgSelected = odg;
				$scope.dtoFdr = {codiceFiscale:'', password:'' , otp: '',
					filesId: [], filesOmissis: [], filesAdozione: [],
					filesParereId: [], filesScheda: [], filesAttoInesistente: [], filesRelataPubblicazione: []};

				var valid = true;
				$scope.emptyMascheraWorkflow = false;

				switch(decisione.codice){
					case "modifica-sottoscrittori":
						//$log.debug("Seduta di Giunta:",$scope.sedutaGiunta);
						SedutaGiunta.sottoscrittoriresoconto($scope.sedutaGiunta,function () {
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$scope.refresh();
							$scope.taskLoading = false;
						});
						break;
					case 'seduta-generadocodg':
						if($scope.sedutaGiunta.organo == 'G') {
							$scope.modelloDaCodice('ordinegiornogiunta');
						}
						else {
							$scope.modelloDaCodice('ordinegiornoconsiglio');
						}
						$scope.dtoWorkflow.campi['profiloSottoscrittore']=$scope.sedutaGiunta.presidente;
						break;
					case 'sedutavariazione-generadocodg':
						if($scope.sedutaGiunta.organo == 'G') {
							$scope.modelloDaCodice('variazione_estremi_seduta_giunta');
						}
						else {
							$scope.modelloDaCodice('variazione_estremi_seduta_consiglio');
						}
						$scope.sedutaGiunta.sottoscrittoreDocVariazione = $scope.sedutaGiunta.presidente;
						break;
					case 'seduta-annulla':
						if ('C' == $scope.sedutaGiunta.organo) {
							$scope.modelloDaCodice('annullamento_seduta_consiglio');
						}
						else {
							$scope.modelloDaCodice('annullamento_seduta_giunta');
						}
						$scope.sedutaGiunta.sottoscrittoreDocAnnullamento = $scope.sedutaGiunta.presidente;
						break;
					case 'seduta-generadocresint':
						$scope.modelloDaCodice('documento_definitivo_esito');
						// Modificati in Attico
						// $scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
						// $scope.dtoWorkflow.campi['profiloSottoscrittore']=$scope.sedutaGiunta.presidente;
						break;
					case 'seduta-generadocelencoverbali':
						var nodes = $scope.attiResoconto($scope.sedutaGiunta.odgs);
						for(var i = 0; i < nodes.length; i++){
							if(!nodes[i].numeroArgomento){
								valid = false;
							}
						}
						if(!valid){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Prima di procedere occorre accedere nella sezione "Resoconto" alla scheda "Elenco Argomenti" ed inserire il numero di argomento su tutti gli atti.'});
						}else{
							$scope.modelloDaCodice('documento_definitivo_elenco_verbali');
						}
						break;
					case 'seduta-pubblicadocres':
						$scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
						$scope.pubblicaDocumentoResoconto();
						break;
// case 'seduta-generadocrespar':
// $scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
// $scope.modelloDaCodice('resoconto');
						break;
					case 'seduta-generadocpresenze':
						$scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
						$scope.modelloDaCodice('foglio_assenti_riunione_giunta');
						$scope.dtoWorkflow.campi['profiloSottoscrittore']=$scope.sedutaGiunta.presidente;
						break;
					case 'seduta-provvisoria-annulla':
						$scope.sedutaGiunta.sottoscrittoreDocAnnullamento = $scope.sedutaGiunta.presidente;
						$scope.emptyMascheraWorkflow = true;
						break;
					case 'seduta-firmadocres':
						$scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
						$scope.sedutaGiunta.resoconto.forEach(function(res){
							if(res.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma){
								$scope.resocontoSelected = res;
							}
						});
						break;
					case 'seduta-firmadocpresenze':
						$scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
						$scope.sedutaGiunta.resoconto.forEach(function(res){
							if(res.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma){
								$scope.resocontoSelected = res;
							}
						});
						break;

					default:

						break;
				}

				if(valid){
					if(decisione.mostraMaschera ){
						if("seduta-firmadocodg" === decisione.codice || 'seduta-firmadocres' === decisione.codice){
							$scope.firma = true;
						}

						$scope.decisioneCorrente = decisione;
						//$log.debug("Decisione Corrente:",$scope.decisioneCorrente);
						$scope.documentiFirmatiDaCaricare = new Map();
						$('#mascheraWorkflow').modal('show');
					}else{
						$scope.salvaDecisione(indexSezione,decisione);
					}
				}
			};


			$scope.terminaRegistrazioni = function() {
				if ($scope.sedutaGiunta.selected.length < 2 || confirm($translate('cifra2gestattiApp.sedutaGiunta.alertRegistrazioni'))) {
					$('#resocontoAtto').modal('hide');
				}
			}

			$('#resocontoAtto').on('hide.bs.modal', function () {
				if($scope.resoconto){
					delete $scope.resoconto.massivo;
					delete $scope.resoconto.selected;
				}
				if($scope.appoggioResoconto){
					delete $scope.appoggioResoconto;
				}

				$scope.scenariDisabilitazione = [];
				$scope.refresh();
			});

			/**
			 * Prende i modelli in base al codice
			 */
			$scope.modelloDaCodice = function(codice){
				var modelliList = [];

				for(var i = 0; i <  $scope.modelloHtmls.length; i++){
					if($scope.modelloHtmls[i].tipoDocumento.codice == codice){
						// $log.debug("modelloTrovato",$scope.modelloHtmls[i]);
						modelliList.push($scope.modelloHtmls[i]);
					}
				}
				$scope.modelloHtmlsOdg = modelliList;
				if($scope.modelloHtmlsOdg.length == 1){
					$scope.dtoWorkflow.campi['modelloHtmlId']=$scope.modelloHtmlsOdg[0];
				}
			}

			$scope.refresh = function () {
				//	$log.debug("Refresh page");
				$scope.modificaEstremi = false;
				$scope.annullaEstremi = false;
				$scope.load($scope.sedutaGiunta.id);
			};

			/**
			 *
			 */
			$scope.selezionaSezione = function(index){

				var sezione = $scope.sedutaSezioni[index];
				$scope.indexSezioneCorrente=index;
				sezione.active=true;
				sezione.activeCss="active";
				$scope.sezioneCorrente = sezione;
				$scope.modelloCampo={};
				$scope.modelloCampo.tipoCampo=$scope.sezioneCorrente.target;

				if($scope.sedutaGiunta && $scope.sedutaGiunta.resoconto && $scope.sedutaGiunta.resoconto.length){
					$scope.existsDocVerbali = $scope.sedutaGiunta.resoconto.map((res) => res.documentiPdf).flat().filter((doc) => doc.tipoDocumento.codice == 'documento_definitivo_elenco_verbali').length > 0;
					$scope.existsDocDefEsito = $scope.sedutaGiunta.resoconto.map((res) => res.documentiPdf).flat().filter((doc) => doc.tipoDocumento.codice == 'documento_definitivo_esito').length > 0;
				}else{
					$scope.existsDocVerbali = false;
					$scope.existsDocDefEsito = false;
				}

				if (sezione.target == 'resoconto'){
					$scope.allAttos = $scope.attiResoconto($scope.sedutaGiunta.odgs);
					SedutaGiunta.getNextNumeroArgomento({sedutaId: $scope.sedutaGiunta.id}, function(res){
						if(res && res.nextNumArg && !isNaN(res.nextNumArg)){
							$scope.maxNArg = res.nextNumArg -1;
						}else{
							$scope.maxNArg = 0;
						}
					});
				}

				if (sezione.target == 'datiOdgBase' || sezione.target == 'resoconto'){
					angular.forEach($scope.sedutaGiunta.odgs,function(odg){
						if(odg.tipoOdg.id == 1 || odg.tipoOdg.id == 2){
							$scope.odgSelected = odg;
						}
					})
				}

				if (sezione.target == 'datiOdgSuppletivo'){
					angular.forEach($scope.sedutaGiunta.odgs,function(odg){
						if(odg.tipoOdg.id == 3){
							$scope.odgSelected = odg;
						}
					})
				}

				if (sezione.target == 'datiOdgFuoriSacco'){
					angular.forEach($scope.sedutaGiunta.odgs,function(odg){
						if(odg.tipoOdg.id == 4){
							$scope.odgSelected = odg;
						}
					})
				}

				if (sezione.target == 'verbale'){
					// console.log('Ci sono ' + $scope.profilos.length + ' profili
					// utente... silaLetturaVerbale = ' + $scope.solaLetturaVerbale)
					/*
				 * SedutaGiunta.getSottoscrittoriVerbalePossibili({id:$scope.sedutaGiunta.id},function(result){
				 * $scope.profilosVerbali = result; });
				 */



					if (!$scope.sedutaGiunta.verbale){
						$scope.idProfiloSottoscrittoreVerbale = null;
						$scope.sedutaGiunta.verbale = { id: null,
							sedutaGiunta : { id: $scope.sedutaGiunta.id },
							narrativa : { id: null, testo: '' },
							noteFinali : { id: null, testo: '' },
							allegati: [],
							documentiPdf: [],
							sottoscrittori: [ { id: null,
								firmato: false,
								ordineFirma: 1,
								profilo: $scope.sedutaGiunta.segretario
							}] };
					} else {
						if (!$scope.sedutaGiunta.verbale.stato){
							$scope.sedutaGiunta.verbale.stato = SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione;
						}
						if($scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione){
							if (!$scope.sedutaGiunta.verbale.allegati){
								$scope.sedutaGiunta.verbale.allegati = [];
							}
							if (!$scope.sedutaGiunta.verbale.documentiPdf){
								$scope.sedutaGiunta.verbale.documentiPdf = [];
							}
							if (!$scope.sedutaGiunta.verbale.sottoscrittori){
								$scope.sedutaGiunta.verbale.sottoscrittori = [];
							}
						}



					}

					$scope.showSezioneVerbale = 'narrativaVerbale';
					$scope.showSezioneResoconto = 'sottoscrittoriResoconto';
				}

				//$log.log('PINGAS SEZIONE CORRENTE', $scope.sezioneCorrente);
			};

			$scope.sottoscrittoriResocontoValid = function(){
				var valid = true;
				$scope.sedutaGiunta.sottoscrittoriresoconto.forEach(function(sottoscrittore){
					if(sottoscrittore.profilo == null || sottoscrittore.qualificaProfessionale == null){
						valid = false;
					}
				});

				return valid;
			};

			/**
			 * 1- Controllo che tutti gli atti degli OdG della seduta abbiano un
			 * esito 2- Controllo che tutti gli atti con esito positivo siano stati
			 * numerati
			 */
			$scope.abilitaPulsantiResoconto = function() {

				// Giorgio: in attico dopo la conferma degli esiti
				return $scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.CONCLUSA &&
					$scope.isOperatoreResoconto && ($scope.sedutaGiunta.resoconto == null || $scope.sedutaGiunta.resoconto.length < 1);

				/* ----------- VECCHIA VERSIONE NON USATA  -------------
    		if($scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.CONCLUSA){
	    		if(angular.isDefined($scope.sedutaGiunta.odgs)){
	    			for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
	        			for(var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++){

	        				if($scope.stringEmpty($scope.sedutaGiunta.odgs[i].attos[j].esito)) {
	        					return false;
	        				}
	        				if ( (!$scope.stringEmpty($scope.sedutaGiunta.odgs[i].attos[j].tipoProvvedimento)) &&
	        					   $scope.stringEmpty($scope.sedutaGiunta.odgs[i].attos[j].atto.numeroAdozione)) {
		        				return false;
	        				}
	        			}
	        		}
	    			return true;
	    		}
	    		else{
	    			return false;
	    		}
	    		return true;
    		}
    		else{
    			return false;
    		}
			*/
			};

			$scope.checkConfermaEsito = function() {
				return $scope.sedutaGiunta.statoDocumento == SedutaGiuntaConstants.statiDocSeduta.docResInAttesaDiPredisposizione;
			};

			/**
			 * Controllo se esiste un resoconto da firmare
			 *
			 * NON UTILIZZATI IN ATTICO
			 *
			 *
			 $scope.abilitaPulsanteFirmaResoconto = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma
    						&& element.sottoscrittore != null && element.sottoscrittore.id === $rootScope.profiloattivo.id){
    					check = true;
    				}
    			});
    		}
    		return check;
    	};
			 */

			/**
			 * Controllo se esiste un resoconto da firmare
			 *
			 * NON UTILIZZATI IN ATTICO
			 *
			 $scope.abilitaPulsanteFirmaResocontoControlloSezione = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma){
    					check = true;
    				}
    			});
    		}
    		return check;
    	};
			 */

			/**
			 * Controllo se il documento di presenze assenze è da firmare
			 *
			 * NON UTILIZZATI IN ATTICO
			 *
			 $scope.abilitaPulsanteFirmaPresenze = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma
    						&& element.sottoscrittore != null && element.sottoscrittore.id === $rootScope.profiloattivo.id){
    					check = true;
    				}
    			});
    		}
    		return check;
    	};

			 $scope.abilitaPulsanteFirmaPresenzeControlloSezione = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma){
    					check = true;
    				}
    			});
    		}
    		return check;
    	};

			 $scope.showSottoscrittoreResoconto = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma ||
    						element.stato === SedutaGiuntaConstants.statiResoconto.resocontoConsolidato){
    					check = true;
    					$scope.sottoscrittoreResoconto = element.sottoscrittore;
    				}
    			});
    		}
    		return check;
    	};

			 $scope.showSottoscrittorePresenze = function(){
    		var check = false;
    		if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
    			$scope.sedutaGiunta.resoconto.forEach(function (element) {
    				if(element.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma ||
    						element.stato === SedutaGiuntaConstants.statiPresenze.presenzeConsolidato){
    					check = true;
    					$scope.sottoscrittorePresenze = element.sottoscrittore;
    				}
    			});
    		}
    		return check;
    	};
			 */

			/**
			 * Controllo se esiste un resoconto da firmare
			 *
			 * NON UTILIZZATI IN ATTICO
			 *
			 $scope.abilitaPulsantePresenzeAssenze = function(){
    		var check = false;
    		if($scope.verificaEsitiDiscussi() == true){
    			if($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0){
        			$scope.sedutaGiunta.resoconto.forEach(function (element) {
        				if(element.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma){
        					check = true;
        				}
        			});
        		}
    		} else{
    			check = true;
    		}

    		return check;
    	};

			 $scope.verificaEsitiDiscussi = function(){
    		var check = false;
//    		var odgBase = $scope.getOdgBase($scope.sedutaGiunta);
    		$scope.sedutaGiunta.odgs.forEach(function(odg){
    			odg.attos.forEach(function(atto){
        			if($scope.getEsitoObj(atto.esito).registraVotazione){
        				check = true;
        			}
    			});
    		});


    		return check;
    	};
			 */

			$scope.abilitaPerTipo = function(tipo){
				var bool = true;
				for(var i = 0; i < $scope.sedutaGiunta.resoconto.length; i++){
					if($scope.sedutaGiunta.resoconto[i].tipo == tipo){
						bool = false;
					}
				}

				return bool;
			};

			/**
			 *
			 */
			$scope.initSelezionaSezione = function( section){

				$scope.indexSezioneCorrente=section;

				for(var i = Number(section) + 1; i< $scope.sedutaSezioni.length; i++){
					if($scope.sedutaSezioni[i].abilitato){
						$scope.nextSection = i;
						break;
					}
				}

				for(var i = Number(section) -1; i>-1; i--){
					if($scope.sedutaSezioni[i].abilitato){
						$scope.previusSection = i;
						break;
					}
				}

				/*$rootScope.navigatoretitle = [];*/
				$scope.selezionaSezione($scope.indexSezioneCorrente);
			};

			/**
			 * Load section data
			 */
			$scope.loadSection = function (section) {
				//$log.debug("Sezione:",section);
				$scope.sedutaSezioni[$scope.indexSezioneCorrente].activeCss="";
				$scope.selezionaSezione(section);
				sessionStorage.setItem("activeTab", null);
				$scope.resetFiltraAttiResoconto($scope.sedutaGiunta.odgs,null,null);
				$scope.filtroCodiceCifra = null;
				$scope.filtroOggetto = null;
				$scope.filtroProponente = null;
			};

			$scope.isOdl = function() {
				if ('nuova-consiglio' == $scope.id || 'C' == $scope.sedutaGiunta.organo) {
					return true;
				}
				else {
					return false;
				}
			}

			$scope.isOdg = function() {
				if ('nuova-giunta' == $scope.id || 'G' == $scope.sedutaGiunta.organo) {
					return true;
				}
				else {
					return false;
				}
			}
			/**
			 * Inizializzazione delle sezioni della seduta
			 */
			$scope.inizializzaSezioni = function() {

				$scope.odgStr = "OdG";
				if ('nuova-consiglio' == $scope.id || 'C' == $scope.sedutaGiunta.organo) {
					$scope.odgStr = "OdL";
				}

				$scope.sedutaSezioni = [
					{ target:"datiIdentificativi" , icon:"fa fa-qrcode", title:"Dati Identificativi", description: "Dati Identificativi", abilitato: true  },
					// { target:"datiRubrica" , icon:"fa fa-book", title:"Rubrica destinatari", description: "Rubrica destinatari", abilitato: true  },
					{ target:"datiOdgBase" , icon:"fa fa-paperclip", title: $scope.odgStr+" Base", description: $scope.odgStr+" Base", abilitato: true  },
					// Non Prevista in attico
					{ target:"datiOdgSuppletivo" , icon:"fa fa-list", title: $scope.odgStr+" Suppletivo",  description: $scope.odgStr+" Suppletivo",  abilitato: false  },
					{ target:"datiOdgFuoriSacco" , icon:"fa fa-list", title: $scope.odgStr+" Fuori Sacco", description: $scope.odgStr+" Fuori Sacco", abilitato: false  },
					{ target:"documentiPdf" , icon:"fa fa-file-pdf-o", title:"Documenti", description: "Documenti", abilitato: true  },
					{ target:"resoconto" , icon:"fa fa-file-text-o", title:"Resoconto", description: "Resoconto", abilitato: false  },
					{ target:"verbale" , icon:"fa fa-file-text-o", title:"Verbale", description: "Verbale", abilitato: false  }
				];

				$scope.decisioni = [
					{codice: "modifica-seduta",descrizione:"Salva Seduta",tipo:"globale",mostraMaschera:false},
					{codice: "seduta-generadocodg",descrizione: "Genera documento " + $scope.odgStr, tipo:"odg", mostraMaschera:true},
					// FIRMA NON PREVISTA
					// {codice: "seduta-firmadocodg",descrizione:"Firma documento " + $scope.odgStr,tipo:"odg",mostraMaschera:true},
					// {codice: "seduta-firmadocann",descrizione:"Firma documento Annullamento",tipo:"globale",mostraMaschera:true},
					{codice: "sedutavariazione-generadocodg",descrizione:"Genera documento variazione estremi seduta",tipo:"odgvariazione",mostraMaschera:true},
					// {codice: "sedutavariazione-firmadocodg",descrizione:"Firma documento variazione estremi seduta",tipo:"odgvariazione",mostraMaschera:true},
					{codice: "seduta-generadocresint",descrizione:"Genera documento definitivo Esito",tipo:"sedutadocres",mostraMaschera:true},
					{codice: "conferma-ordine-discussione",descrizione:"Conferma Ordine Discussione",tipo:"confermadiscussione",mostraMaschera:false},
					{codice: "modifica-ordine-discussione",descrizione:"Modifica Ordinamento Discussione",tipo:"modificadiscussione",mostraMaschera:false},
					{codice: "passto-registrazioni-esiti",descrizione:"Registrazione Esiti",tipo:"passtoregistrazioneesiti",mostraMaschera:false},
					{codice: "assegna-numeri-adozione",descrizione:"Assegna Numeri di Adozione",tipo:"adozione",mostraMaschera:true},
					{codice: "passto-ordine-discussione",descrizione:"Imposta ordine di discussione",tipo:"pass-to",mostraMaschera:false},
					// SOTTOSCRITTORI NON PREVISTI
					// {codice: "modifica-sottoscrittori",descrizione:"Salva Sottoscrittori",tipo:"sedutadocres",mostraMaschera:false},
					// {codice: "seduta-firmadocres",descrizione:"Firma documento Resoconto Integrale",tipo:"sedutaresdocfirma",mostraMaschera:true},
					// {codice: "seduta-pubblicadocres",descrizione:"Pubblica documento Resoconto Integrale/Parziale",tipo:"sedutapubdocres",mostraMaschera:false},
					// {codice:
					// "seduta-generadocrespar",descrizione:"Genera
					// documento Resoconto
					// Parziale",tipo:"sedutadocres",mostraMaschera:true},
					// DOC PRESENZE NON PREVISTO
					// {codice: "seduta-generadocpresenze",descrizione:"Genera documento Presenze/Assenze",tipo:"sedutapreass",mostraMaschera:true},
					// {codice: "seduta-firmadocpresenze",descrizione:"Firma documento Presenze/Assenze",tipo:"sedutadocpreassfirma",mostraMaschera:true},
					{codice: "seduta-generadocelencoverbali",descrizione:"Genera documento definitivo Elenco Verbali",tipo:"sedutadocverbali",mostraMaschera:true},
					{codice: "modifica-verbale",descrizione:"Salva Verbale",tipo:"verbale",mostraMaschera:false},
					{codice: "rinuncia-verbale",descrizione:"Rinuncia al Verbale",tipo:"verbale",mostraMaschera:false},
					{codice: "seduta-generadocverbale",descrizione:"Genera documento del Verbale",tipo:"verbale",mostraMaschera:true},
					// {codice: "seduta-firmadocverbale",descrizione:"Firma documento del Verbale",tipo:"verbale",mostraMaschera:true}
				];
			};

			/**
			 * Abilita le sezioni ODG
			 */
			$scope.abilitaSezioniOdg = function(){
				for(var i = 0; i < $scope.sedutaSezioni.length; i++){
					if($scope.sedutaSezioni[i].odg == true){
						$scope.sedutaSezioni[i].abilitato = true;
					}
				}
			};

			/**
			 * abilita la sezione identificata da target
			 */
			$scope.abilitaSezioneOdg = function(target){
				for(var i = 0; i < $scope.sedutaSezioni.length; i++){
					if($scope.sedutaSezioni[i].target == target){
						$scope.sedutaSezioni[i].abilitato = true;
					}
				}
			};
			/**
			 * disabilita la sezione identificata da target
			 */
			$scope.disabilitaSezioneOdg = function(target){
				for(var i = 0; i < $scope.sedutaSezioni.length; i++){
					if($scope.sedutaSezioni[i].target == target){
						$scope.sedutaSezioni[i].abilitato = false;
					}
				}
			};

			/**
			 * Disabilita le sezioni ODG
			 */
			$scope.disabilitaSezioniOdg = function(){
				for(var i = 0; i < $scope.sedutaSezioni.length; i++){
					if($scope.sedutaSezioni[i].odg == true){
						$scope.sedutaSezioni[i].abilitato = false;
					}
				}
			};

			$scope.modificaShow = function() {
// $log.debug("Stato:",$scope.sedutaGiunta.stato);
// $log.debug(SedutaGiuntaConstants.statiSeduta.sedutaInPredisposizione);
// $log.debug(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgBase);
				if( $scope.modificaEstremi == false && $scope.sedutaGiunta.id != null &&
					$scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA) {
					return true;
				}
				return false;
			}

			$scope.callDecisione  = function (indexSezione,decisione) {
				//$log.debug(decisione);
				//$scope.sezioneIdx = indexSezione;
				$scope.dtoFdr = {codiceFiscale:'', password:'' , otp: '',
					filesId: [], filesOmissis: [], filesAdozione: [],
					filesParereId: [], filesScheda: [], filesAttoInesistente: [], filesRelataPubblicazione: []};
				$scope.taskLoading = true;
				switch(decisione.codice){
					case "crea-seduta":
						//$log.debug("Seduta di Giunta:",$scope.sedutaGiunta);
						$scope.save();
						break;
					case "modifica-seduta":
						if($scope.modificaEstremi == true){
							$scope.sedutaGiunta.stato = SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.label;
						}

						//$log.debug("Seduta di Giunta:",$scope.sedutaGiunta);

						var dtFineEl = angular.element("#dtPrimaConvocazioneFine");
						if (dtFineEl[0] && (!dtFineEl[0].disabled)) {

							// Controllo che non vi siano OdG non consolidati
							if(angular.isDefined($scope.sedutaGiunta.odgs)){
								for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
									if(!$scope.isSolaLettura($scope.sedutaGiunta.odgs[i])) {
										alert("Attenzione! Per proseguire con la chiusura della Seduta occorre consolidare l'" + $scope.odgStr
											+ " Suppletivo e\o Fuori Sacco oppure, rimuovere gli atti al momento in essi inseriti.");
										$scope.sedutaGiunta.primaConvocazioneFine = null;
										$scope.sedutaGiunta.inizioLavoriEffettiva = null;
										$scope.taskLoading = false;
										return;
									}
								}
							}

							$('#confermaFineLavori').modal('show');
						}
						else {
							$scope.save();
						}
						break;
					case 'seduta-firmadocann':
						$scope.taskLoading = false;
						if(decisione.mostraMaschera ){
							$scope.odgSelected = $scope.getOdgBase($scope.sedutaGiunta);
							$scope.firma = true;
							$scope.decisioneCorrente = decisione;
							//$log.debug("Decisione Corrente:",$scope.decisioneCorrente);
							$scope.documentiFirmatiDaCaricare = new Map();
							$('#mascheraWorkflow').modal('show');
						}
						break;
					case 'passto-ordine-discussione':{
						for(let i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
							$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						}
						$scope.aggiornaStatoSeduta(SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneOrdineDiscussione.codice, 'up');
						break;
					}
				}

			};

			$scope.selectAllAtti = function(odg){
				var idOdg = odg.id;
				var newAtti = [];
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg){
						for(var j = 0; j < $scope.atti.length; j++){
							if ($scope.atti[j].escludiDaVisualizzazione) {
								newAtti.push($scope.atti[j]);
							}
							else {
								var attoOdg = {id:null,ordineGiorno:{id:idOdg},atto: $scope.atti[j],ordineOdg:0,sezione:0,parte:0,bloccoModifica:0};
								$scope.sedutaGiunta.odgs[i].attos.push(attoOdg);
								// $scope.atti.splice(i,1);
								//$log.debug("eliminato indice:",j);
							}
						}
						if($scope.isOdl()){
							riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
						}
						else {
							riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
						}

					}
				}
				$scope.atti = newAtti;
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};

			$scope.deselectAllAtti = function(idOdg){
				var isOdl = $scope.isOdl();
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg){
						var newAttiOdg = [];
						for(var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++){
							if ($scope.sedutaGiunta.odgs[i].attos[j].atto.escludiDaVisualizzazione) {
								newAttiOdg.push($scope.sedutaGiunta.odgs[i].attos[j]);
							}
							else {
								$scope.atti.push($scope.sedutaGiunta.odgs[i].attos[j].atto);
								// $scope.attiodg.splice(i,1);
								//$log.debug("aggiunto indice:",j);
							}
						}
						$scope.sedutaGiunta.odgs[i].attos = newAttiOdg;
						if(isOdl){
							riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
						}
						else {
							riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
						}
					}
				}
				$scope.atti = $filter('orderBy')($scope.atti, 'codiceCifra');
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};

			$scope.selectAtti = function(odg){
				var idOdg = odg.id;
				var isOdl = $scope.isOdl();
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg){
						for(var j = 0; j < $scope.attiInseribiliList.length; j++){
							var atto = getAttoInseribileById($scope.attiInseribiliList[j]);
							if(atto && atto.id){
								var attoOdg = {id:null,ordineGiorno:{id:idOdg},atto: atto,ordineOdg:0,sezione:0,parte:0,bloccoModifica:0};
								$scope.sedutaGiunta.odgs[i].attos.push(attoOdg);
								var index = $scope.atti.indexOf(atto);
								$scope.atti.splice(index,1);
							}
						}
						if(isOdl){
							riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
						}
						else {
							riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
						}
					}
				}
				$scope.attiInseribiliList = [];
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};

			$scope.deselectAtti = function(idOdg){
				var isOdl = $scope.isOdl();
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg){
						for(var j = 0; j < $scope.attiInseritiList.length; j++){
							var item = getAttoInseritoById(i,$scope.attiInseritiList[j]);
							$scope.atti.push(item.atto);
							var index = $scope.sedutaGiunta.odgs[i].attos.indexOf(item);
							$scope.sedutaGiunta.odgs[i].attos.splice(index,1);
							//$log.debug("aggiunto indice:",j);
						}
						$scope.attiInseritiList = [];
					}
					if(isOdl){
						riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
					}
					else {
						riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
					}
				}
				$scope.atti = $filter('orderBy')($scope.atti, 'codiceCifra');
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};


			function getAttoInseribileById(idAtto){
				return $filter('filter')($scope.atti, function (d) {return d.id === idAtto;})[0];
			}

			function getAttoInseritoById(i,idAtto){
				return $filter('filter')($scope.sedutaGiunta.odgs[i].attos, function (d) {return d.id === idAtto;})[0];
			}

			$scope.removeItem = function (item,index,idOdg) {
				$scope.atti.push(item.atto);
				var isOdl = $scope.isOdl();
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg){
						$scope.sedutaGiunta.odgs[i].attos.splice(index,1);
						let idx = 1;
						if($scope.odgSelected != undefined && $scope.odgSelected.tipoOdg.id == '3'){
							idx = $scope.getOdgBase($scope.sedutaGiunta).attos.length+1;
						}
						angular.forEach($scope.sedutaGiunta.odgs[i].attos, function(atto) {
							atto.ordineOdg = idx;
							idx++;
						});
					}
					// if(!isOdl){
					// 	riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
					//
					// }
					// else {
					// 	riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
					// 	}
				}

				$scope.atti = $filter('orderBy')($scope.atti, 'codiceCifra');
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};

			$scope.removeRubricaItem = function (item,index,idSeduta) {

				if(angular.isDefined(item.removable) && item.removable == true){
					$scope.sedutaGiunta.rubricaSeduta.splice(index,1);
				}
				else{
					$scope.rubricaDel = {idRubrica: item.id, index: index};
					$('#deleteRubricaConfirmation').modal('show');
				}

			};

			$scope.confirmDeleteRubrica = function(item) {
				$('#deleteRubricaConfirmation').modal('hide');
				$scope.sedutaGiunta.rubricaSeduta.splice($scope.rubricaDel.index,1);
				for(var i = 0; i<$scope.sedutaGiunta.odgs.length; i++){
					$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
				}
				//$log.debug("SedutaGiunta PreSave:",$scope.sedutaGiunta);
				SedutaGiunta.update($scope.sedutaGiunta,
					function () {
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.refresh();
						$scope.taskLoading = false;
					});

			};



			$scope.abilitaRegistraResoconto = function(attos){
				if(attos == undefined || attos == null){
					attos = $scope.attiResoconto($scope.sedutaGiunta.odgs);
				}

				var abilita = false;
				for(var i = 0; i < attos.length; i++){
					if( (attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito ||
						attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg ||
						attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInseribileInOdl)
						/*&& attos[i].ultimoOdg*/
					){
						abilita = true;
						break;
					}
				}

				return abilita;
			};

			/* Nel processo di Attico, funzione prevista su singolo Atto
    	$scope.abilitaAnnullaResoconto = function(odg){
    		var abilita = false;
    		for(var i = 0; i < odg.attos.length; i++){
    			if(odg.attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaConEsito ||
    					(odg.attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInseribileInOdg && !($scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0 )) ||
    					odg.attos[i].atto.stato == SedutaGiuntaConstants.statiAtto.propostaInAttesaDiRitiro){
    				abilita = true;
    				break;
    			}
    		}

    		return abilita;
    	};
    	*/

			$scope.abilitaDaStato = function(stati, sedutaOdg){
				var check = 0;
				if(sedutaOdg.selected && sedutaOdg.selected.length){
					for(var i = 0; i < sedutaOdg.selected.length; i++){
						if(stati.indexOf(sedutaOdg.selected[i].atto.stato) > -1){
							check ++;
						}
					}
					if(check == sedutaOdg.selected.length){
						return true;
					}
				}
				return false;
			};

			$scope.resetResoconto = function(){
				if($scope.resoconto!=null){
					if($scope.resoconto.componenti!=null){
						for ( var componenteIndex in $scope.resoconto.componenti) {
							$scope.resoconto.componenti[componenteIndex].presente = null;
							$scope.resoconto.componenti[componenteIndex].votazione = null;
						}
					}

					if($scope.resoconto.dataDiscussione==null){
						$scope.resoconto.dataDiscussione = angular.copy($scope.dataDiscussione);
					}
					if($scope.resoconto.isAttoModificatoInGiunta==null){
						$scope.resoconto.isAttoModificatoInGiunta=false;
					}
					$scope.resoconto.scrutatori = [];
					$scope.resoconto.scrutatoriIE = [];

					$scope.resoconto.voto = {
						segreto: {
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						},
						palese:{
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						},
						immediataEseguibilita:{
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						}
					};
					$scope.resoconto.numPresenti=0;
					$scope.resoconto.numAssenti=0;
					$scope.resoconto.numFavorevoli=0;
					$scope.resoconto.numContrari=0;
					$scope.resoconto.numAstenuti=0;
					$scope.resoconto.numNpv=0;
					$scope.resoconto.votazioneSegreta=false;
					$scope.resoconto.votazioneIE=false;
					$scope.resoconto.approvataIE=null;
					$scope.resoconto.sedutaConvocata='';
					$scope.resoconto.attoPresentato='';
					$scope.resoconto.dichiarazioniVoto='';

					$scope.infoTab = 0;
				}
			}

			$scope.initResoconto = function(odg, massivo, modificaComponenti, idResoconto) {
				var resoconto = {
					id:idResoconto,
					esito: null,
					dataDiscussione:angular.copy($scope.dataDiscussione),
					componenti: [],
					scrutatori: [],
					scrutatoriIE: [],
					voto: {
						segreto: {
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						},
						palese:{
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						},
						immediataEseguibilita:{
							presenti: 0,
							assenti: 0,
							favorevoli: 0,
							contrari: 0,
							astenuti: 0,
							presentiNonVotanti: 0
						}
					},
					presidenteFine: {
						profilo: {}
					},
					//presidente : {
					//	profilo: {}
					//},
					presidenteIE: {
						profilo: {}
					},
					segretarioFine: {
						profilo: {}
					},
					//segretarioInizio: {
					//	profilo: {}
					//},
					segretarioIE: {
						profilo: {}
					},
					numPresenti: 0,
					numAssenti: 0,
					numFavorevoli: 0,
					numContrari: 0,
					numAstenuti: 0,
					numNpv: 0,
					votazioneSegreta: false,
					votazioneIE: false,
					approvataIE: null,
					sedutaConvocata: '',
					attoPresentato: '',
					dichiarazioniVoto: '',
					annulla: false,
					modifica: true,
					modificaComponenti: modificaComponenti,
					isAttoModificatoInGiunta: false,
					massivo: massivo,
					selected: $scope.sedutaGiunta.selected
				};
				return resoconto;
			}

			$scope.filtraAtti = function(odg, codiceCifra, oggetto, proponente, parComNotReq, parComAll, parComExp, parAll){
				for(var i=0; i< odg.attos.length; i++){
					var escludiDaVis = 0;
					if(codiceCifra != undefined && codiceCifra!==""){
						if(odg.attos[i].atto.codiceCifra.toLowerCase().indexOf(codiceCifra.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && oggetto != undefined && oggetto!==""){
						if(odg.attos[i].atto.oggetto.toLowerCase().indexOf(oggetto.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && proponente != undefined && proponente!==""){
						if(odg.attos[i].atto.assessoreProponente.toLowerCase().indexOf(proponente.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && !parAll){
						escludiDaVis = 1;
						if(escludiDaVis == 1 && parComNotReq && odg.attos[i].atto.parComNotReq){
							escludiDaVis = 0;
						}
						if(escludiDaVis == 1 && parComAll && odg.attos[i].atto.parComAll){
							escludiDaVis = 0;
						}
						if(escludiDaVis == 1 && parComExp && odg.attos[i].atto.parComExpired){
							escludiDaVis = 0;
						}
					}
					odg.attos[i].atto.escludiDaVisualizzazione = escludiDaVis;
				}
			}


			$scope.resetFiltraAtti = function(odg,codiceCifra,oggetto,proponente){
				codiceCifra = "";
				oggetto = "";
				proponente = "";
				for(var i=0; i< odg.attos.length; i++){
					odg.attos[i].atto.escludiDaVisualizzazione = 0;
				}
			}


			$scope.filtraAttiIns = function(atti, codiceCifraIns, oggettoIns, proponenteIns, parComNotReq,
											parComAll, parComExp, parAll ){
				for(var i=0; i< atti.length; i++){
					var escludiDaVis = 0;
					if(codiceCifraIns != undefined && codiceCifraIns!==""){
						if(atti[i].codiceCifra.toLowerCase().indexOf(codiceCifraIns.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && oggettoIns != undefined && oggettoIns!==""){
						if(atti[i].oggetto.toLowerCase().indexOf(oggettoIns.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && proponenteIns != undefined && proponenteIns!==""){
						if(atti[i].assessoreProponente.toLowerCase().indexOf(proponenteIns.toLowerCase()) === -1)
						{
							escludiDaVis = 1;
						}
					}
					if(escludiDaVis === 0 && !parAll){
						escludiDaVis = 1;
						if(escludiDaVis == 1 && parComNotReq && atti[i].parComNotReq){
							escludiDaVis = 0;
						}
						if(escludiDaVis == 1 && parComAll && atti[i].parComAll){
							escludiDaVis = 0;
						}
						if(escludiDaVis == 1 && parComExp && atti[i].parComExpired){
							escludiDaVis = 0;
						}
					}
					atti[i].escludiDaVisualizzazione = escludiDaVis;
				}
			}


			$scope.resetFiltraAttiIns = function(atti,codiceCifraIns,oggettoIns,proponenteIns){
				codiceCifraIns = "";
				oggettoIns = "";
				proponenteIns = "";
				for(var i=0; i< atti.length; i++){
					atti[i].escludiDaVisualizzazione = 0;
				}
			}


			$scope.filtraAttiResoconto = function(odgs, codiceCifra, oggetto){
				for(var j=0; j< odgs.length; j++){
					for(var i=0; i< odgs[j].attos.length; i++){
						var escludiDaVis = 0;
						if(codiceCifra != undefined && codiceCifra!==""){
							if(odgs[j].attos[i].atto.codiceCifra.toLowerCase().indexOf(codiceCifra.toLowerCase()) === -1)
							{
								escludiDaVis = 1;
							}
						}
						if(escludiDaVis === 0 && oggetto != undefined && oggetto!==""){
							if(odgs[j].attos[i].atto.oggetto.toLowerCase().indexOf(oggetto.toLowerCase()) === -1)
							{
								escludiDaVis = 1;
							}
						}
						odgs[j].attos[i].atto.escludiDaVisualizzazione = escludiDaVis;
					}
				}
			}


			$scope.resetFiltraAttiResoconto = function(odgs, codiceCifra, oggetto){
				for(var j=0; j< odgs.length; j++){
					codiceCifra = "";
					oggetto = "";
					for(var i=0; i< odgs[j].attos.length; i++){
						odgs[j].attos[i].atto.escludiDaVisualizzazione = 0;
					}
				}
			}

			$scope.getOdgFromAttoId = function(odgs, aOdgId){
				let odg = null;
				if(odgs && odgs.length && aOdgId){
					for(let i = 0; i < odgs.length; i++){
						if(odgs[i] && odgs[i].attos && odgs[i].attos.length){
							for(let j = 0; j < odgs[i].attos.length; j++){
								if(odgs[i].attos[j] && odgs[i].attos[j].id && odgs[i].attos[j].id == aOdgId){
									odg = odgs[i];
									break;
								}
							}
							if(odg){
								break;
							}
						}
					}
				}
				return odg;
			};

			$scope.apriRegistraEsitoMassivo = function(seduta, curIdx){

				$scope.resoconto = {};

				//$log.debug("Odg:",odg);

				//$log.debug("Resoconto - assessori:",$scope.profilosComponentiSeduta);
				$scope.dataDiscussione = $scope.sedutaGiunta.primaConvocazioneInizio;
				if($scope.sedutaGiunta.secondaConvocazioneInizio != null && typeof $scope.sedutaGiunta.secondaConvocazioneInizio != "undefined"){
					$scope.dataDiscussione = $scope.sedutaGiunta.secondaConvocazioneInizio;
				}
				if($scope.sedutaGiunta.inizioLavoriEffettiva != null && typeof $scope.sedutaGiunta.inizioLavoriEffettiva != "undefined"){
					$scope.dataDiscussione = $scope.sedutaGiunta.inizioLavoriEffettiva;
				}

				$scope.infoTab = 0;
				seduta.selected = $filter('orderBy')(seduta.selected, ['numeroDiscussione']);
				$scope.resoconto = $scope.initResoconto(seduta, true, false, null);

				$scope.esiti = [];

				if (! (curIdx && curIdx > 0)) {
					curIdx = 0;
				}
				$scope.resoconto.curIdx = curIdx;

				let odg = $scope.getOdgFromAttoId(seduta.odgs, seduta.selected[curIdx].id);

				// IMMEDIATA ESEGUIBILITA'
				$scope.resoconto.ie = seduta.selected[curIdx].atto.ie;
				$scope.viewImmediataEseguibilita = false;
				TipoAtto.get({id: seduta.selected[curIdx].atto.tipoAtto.id}, function(result) {
					if (result && result.campi && result.campi.length && result.campi.length > 0) {
						for (var i = 0; i < result.campi.length; i++) {
							if(result.campi[i].codice == 'ie'){
								$scope.viewImmediataEseguibilita = result.campi[i].visibile;
							}
							// 		$scope.visibilitaCampi[result.campi[i].codice] = result.campi[i].visibile;
						}
					}
				});

				// In questa fase , se == null, non gestita neanche in fase di esito
				// if($scope.resoconto.ie === undefined || $scope.resoconto.ie === null){
				// 	$scope.resoconto.ie = true;
				// 	$scope.viewImmediataEseguibilita =
				// }

				$scope.resoconto.labelTitle = "(" + seduta.selected[curIdx].atto.codiceCifra + (seduta.selected[curIdx].atto.numeroAdozione ? ' - ' + seduta.selected[curIdx].atto.numeroAdozione : '') + ") " + seduta.selected[curIdx].atto.oggetto;
				$scope.resoconto.labelTitleShort = ": " + $scope.resoconto.labelTitle;
				if ($scope.resoconto.labelTitleShort.length > 150) {
					$scope.resoconto.labelTitleShort = $scope.resoconto.labelTitleShort.substring(0, 150);
				}

				for(var i= 0; i<$scope.esitiAll.length; i++) {
					// Giorgio CDFATTICOMEV-65: registrazione esito su ogni atto, non necessita della logica sugli esiti comuni
					//var common = 0;
					// for(var j = 0; j < odg.selected.length; j++){
					// if($scope.esitiAll[i].tipiAtto!=null && $scope.esitiAll[i].tipiAtto.split(',').indexOf(odg.selected[j].atto.tipoAtto.codice) !== -1 && $scope.sedutaGiunta.organo == $scope.esitiAll[i].organo){
					if( $scope.esitiAll[i].tipiAtto!=null && $scope.esitiAll[i].tipiAtto.split(',').indexOf(seduta.selected[curIdx].atto.tipoAtto.codice) !== -1 &&
						$scope.sedutaGiunta.organo == $scope.esitiAll[i].organo) {
						//		common ++;
						//	}
						// }

						// if(common == odg.selected.length){
						$scope.esiti.push($scope.esitiAll[i]);
					}
				}
				//$log.debug($scope.esiti);

//				for(var i=0; i< $scope.profilosComponentiSeduta.length; i++){
//					$scope.resoconto.componenti.push($scope.getComponenteVuoto($scope.profilosComponentiSeduta[i],null,odg));
//				}
				$scope.caricaComposizionePredefinita(odg);
				
				$scope.setListaScrutatori();
				$scope.setListaScrutatoriIE();

				// Init del presidente di questo resoconto con il presidente della
				// giunta...
				//if ($scope.resoconto.presidenteInizio == null){
				//var compPres = $scope.getPresidenteVuoto(null,odg,true);
				//$scope.resoconto.presidenteInizio = compPres;
				//}

				if (!$scope.appoggioResoconto) {
					$scope.appoggioResoconto = {};
				}

				if ($scope.appoggioResoconto.esito) {

					if($scope.esiti) {

						var stessaLabelCodiceDiverso = 1;

						for(var i= 0; i<$scope.esiti.length; i++) {
							if($scope.esiti[i].id == $scope.appoggioResoconto.esito){
								$scope.resoconto.esito = $scope.appoggioResoconto.esito;

								$scope.checkVisResoconto();
								$scope.infoTab = 0;
								stessaLabelCodiceDiverso=0;
							}
						}
						var labelEsito = "";
						if(stessaLabelCodiceDiverso==1){
							for(var i= 0; i<$scope.esitiAll.length; i++) {
								if($scope.esitiAll[i].id == $scope.appoggioResoconto.esito){
									labelEsito = $scope.esitiAll[i].label;
									break;
								}
							}

							for(var i= 0; i<$scope.esiti.length; i++) {
								if($scope.esiti[i].label == labelEsito){
									$scope.appoggioResoconto.esito = $scope.esiti[i].id;
									break;
								}

							}

						}


					}

					$scope.resoconto.esito = $scope.appoggioResoconto.esito;

					$scope.checkVisResoconto();
					$scope.infoTab = 0;
				}

				if ($scope.resoconto.presidenteFine == null || $scope.resoconto.presidenteFine.profilo == null || !$scope.resoconto.presidenteFine.profilo.id) {
					if ($scope.appoggioResoconto.presidenteFine) {
						$scope.resoconto.presidenteFine = $scope.appoggioResoconto.presidenteFine;
						if ($scope.resoconto.presidenteFine.profilo) {
							$scope.setQualificheProfilo('presidenteFine',$scope.resoconto.presidenteFine.profilo.id, false);
						}
					}
					else {
						var compPres = $scope.getPresidenteVuoto(null,odg,false);
						$scope.resoconto.presidenteFine = compPres;
					}
				}
				// Init del segretario di questo resoconto con il segretario della
				// giunta...
				//if ($scope.resoconto.segretarioInizio == null){
				//var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,true,true);
				//$scope.resoconto.segretarioInizio = compSegr;
				//}
				if ($scope.resoconto.segretarioFine == null || $scope.resoconto.segretarioFine.profilo == null || !$scope.resoconto.segretarioFine.profilo.id) {
					if ($scope.appoggioResoconto.segretarioFine) {
						$scope.resoconto.segretarioFine = $scope.appoggioResoconto.segretarioFine;
						if ($scope.resoconto.segretarioFine.profilo) {
							$scope.setQualificheProfilo('segretarioFine', $scope.resoconto.segretarioFine.profilo.id, false);
						}
					}
					else {
						var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,false,true);
						$scope.resoconto.segretarioFine = compSegr;
					}
				}
				// Init presidente e segretario per immediata esecutiv.
				if ($scope.resoconto.presidenteIE == null || $scope.resoconto.presidenteIE.profilo == null || !$scope.resoconto.presidenteIE.profilo.id){
					if ($scope.appoggioResoconto.presidenteIE) {
						$scope.resoconto.presidenteIE = $scope.appoggioResoconto.presidenteIE;
						if ($scope.resoconto.presidenteIE.profilo) {
							$scope.setQualificheProfilo('presidenteIE', $scope.resoconto.presidenteIE.profilo.id, false);
						}
					}
					else {
						var compPres = $scope.getPresidenteVuoto(null,odg,true);
						$scope.resoconto.presidenteIE = compPres;
					}
				}
				if ($scope.resoconto.segretarioIE == null || $scope.resoconto.segretarioIE.profilo == null || !$scope.resoconto.segretarioIE.profilo.id){
					if ($scope.appoggioResoconto.segretarioIE) {
						$scope.resoconto.segretarioIE = $scope.appoggioResoconto.segretarioIE;
						if ($scope.resoconto.segretarioIE.profilo) {
							$scope.setQualificheProfilo('segretarioIE', $scope.resoconto.segretarioIE.profilo.id, false);
						}
					}
					else {
						var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,true,true);
						$scope.resoconto.segretarioIE = compSegr;
					}
				}
				// Init degli scrutatori di questo resoconto con gli scrutatori della
				// giunta...
				if ($scope.resoconto.scrutatore01 == null || $scope.resoconto.scrutatore01.profilo == null || !$scope.resoconto.scrutatore01.profilo.id){
					if ($scope.appoggioResoconto.scrutatore01) {
						$scope.resoconto.scrutatore01 = $scope.appoggioResoconto.scrutatore01;
						if ($scope.resoconto.scrutatore01.profilo) {
							$scope.setQualificheProfilo('scrutatore01', $scope.resoconto.scrutatore01.profilo.id, false);
						}
					}
					else {
						var compScr01 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatore01 = compScr01;
					}
				}
				if ($scope.resoconto.scrutatore02 == null || $scope.resoconto.scrutatore02.profilo == null || !$scope.resoconto.scrutatore02.profilo.id){
					if ($scope.appoggioResoconto.scrutatore02) {
						$scope.resoconto.scrutatore02 = $scope.appoggioResoconto.scrutatore02;
						if ($scope.resoconto.scrutatore02.profilo) {
							$scope.setQualificheProfilo('scrutatore02', $scope.resoconto.scrutatore02.profilo.id, false);
						}
					}
					else {
						var compScr02 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatore02 = compScr02;
					}
				}
				if ($scope.resoconto.scrutatore03 == null || $scope.resoconto.scrutatore03.profilo == null || !$scope.resoconto.scrutatore03.profilo.id){
					if ($scope.appoggioResoconto.scrutatore03) {
						$scope.resoconto.scrutatore03 = $scope.appoggioResoconto.scrutatore03;
						if ($scope.resoconto.scrutatore03.profilo) {
							$scope.setQualificheProfilo('scrutatore03', $scope.resoconto.scrutatore03.profilo.id, false);
						}
					}
					else {
						var compScr03 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatore03 = compScr03;
					}
				}
				if ($scope.resoconto.scrutatoreIE01 == null || $scope.resoconto.scrutatoreIE01.profilo == null || !$scope.resoconto.scrutatoreIE01.profilo.id){
					if ($scope.appoggioResoconto.scrutatoreIE01) {
						$scope.resoconto.scrutatoreIE01 = $scope.appoggioResoconto.scrutatoreIE01;
						if ($scope.resoconto.scrutatoreIE01.profilo) {
							$scope.setQualificheProfilo('scrutatoreIE01', $scope.resoconto.scrutatoreIE01.profilo.id, false);
						}
					}
					else {
						var compScr01 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatoreIE01 = compScr01;
					}
				}
				if ($scope.resoconto.scrutatoreIE02 == null || $scope.resoconto.scrutatoreIE02.profilo == null || !$scope.resoconto.scrutatoreIE02.profilo.id){
					if ($scope.appoggioResoconto.scrutatoreIE02) {
						$scope.resoconto.scrutatoreIE02 = $scope.appoggioResoconto.scrutatoreIE02;
						if ($scope.resoconto.scrutatoreIE02.profilo) {
							$scope.setQualificheProfilo('scrutatoreIE02', $scope.resoconto.scrutatoreIE02.profilo.id, false);
						}
					}
					else {
						var compScr02 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatoreIE02 = compScr02;
					}
				}
				if ($scope.resoconto.scrutatoreIE03 == null || $scope.resoconto.scrutatoreIE03.profilo == null || !$scope.resoconto.scrutatoreIE03.profilo.id){
					if ($scope.appoggioResoconto.scrutatoreIE03) {
						$scope.resoconto.scrutatoreIE03 = $scope.appoggioResoconto.scrutatoreIE03;
						if ($scope.resoconto.scrutatoreIE03.profilo) {
							$scope.setQualificheProfilo('scrutatoreIE03', $scope.resoconto.scrutatoreIE03.profilo.id, false);
						}
					}
					else {
						var compScr03 = $scope.getScrutatoreVuoto(null,odg,true);
						$scope.resoconto.scrutatoreIE03 = compScr03;
					}
				}

				delete $scope.appoggioResoconto;
				$scope.verificaPresentiNumVoti();
				//$log.debug("apriRegistraEsitoMassivo:", $scope.resoconto);
				if (!($('#resocontoAtto').is(':visible'))) {
					$('#resocontoAtto').modal('show');
				}
				else {
					$('body').addClass('modal-open');
				}
			};


			$scope.resocontoMassivoBack = function() {
				var nextIdx = $scope.resoconto.curIdx -1;

				if ($scope.sedutaGiunta.selected[nextIdx].esito) {
					$scope.apriResoconto($scope.sedutaGiunta.selected[nextIdx],$scope.sedutaGiunta, false, nextIdx);
				}
				else {
					$scope.appoggioResoconto = $scope.resoconto;
					$scope.apriRegistraEsitoMassivo($scope.sedutaGiunta, nextIdx);
				}
			};

			$scope.setTabLabel = function() {
				if($scope.visibilitaPresenza && $scope.visibilitaVotazione){
					return 'Presenze/Votazioni';
				} else if($scope.visibilitaPresenza){
					return 'Presenze';
				} else if($scope.visibilitaVotazione){
					return 'Votazioni';
				} else {
					return 'Presenze/Votazioni';
				}
			};

			/* Il processo attico non consente annullamento massivo
    	$scope.annullaResocontoMassivo = function(odg){
    		$log.debug("Odg:",odg);
    		$scope.listaAnnulla = odg;
    		$('#annullaResocontoAtto').modal('show');
    	};
    	*/


			/**
			 * Apre il modal del resoconto
			 */
			$scope.apriResoconto = function(atto,seduta,readonly,curIdx) {
				let odg = $scope.getOdgFromAttoId(seduta.odgs, atto.id);
				if(readonly){
					let today = new Date().setHours(0,0,0,0);
					let checkParereObbligatorio = false;
					let hasTaskAttivi = false;
					let quartRevDaInserire = false;
					
					for(var i = 0; i < atto.atto.taskAttivi.length; i++){
						var infos = $scope.getInfoCommissioni(atto.atto);
						
						let descrizioneTask = atto.atto.taskAttivi[i].descrizione;
						let almenoUnTaskCheDeveAncoraScadere = false;
						if(descrizioneTask != null){
							for(var j = 0; j< infos.length; j++){
								var infoAoo = infos[j].aoo;
								var scadenza = null;
								if(atto.atto.taskAttivi[i].aoo!= null && infoAoo === atto.atto.taskAttivi[i].aoo.descrizione){
									if(infos[j].conf && infos[j].conf.dataManuale && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
										scadenza = new Date(infos[j].conf.dataManuale);
										scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
									}
					    			else if(infos[j].conf && infos[j].conf.dataCreazione && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
					    				scadenza = new Date(infos[j].conf.dataCreazione);
					    				scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
					    			}
									var adesso = new Date();
									if(scadenza){
					    				if(scadenza.getTime() > adesso.getTime()){
					    					hasTaskAttivi = true;
					    					break;
					    				}
					    			}
								}
							}
						}
					}
					
					
					if($scope.sedutaGiunta.organo === 'C' && $rootScope.configurationParams.parere_cons_quart_rev_cont_obbligatorio == 'true'){
						if(atto.atto.pareri && atto.atto.pareri.length > 0 ){
							atto.atto.pareri.forEach(p => {
		                        if(p && !p.annullato && p.tipoAzione && p.tipoAzione.codice== 'parere_quartiere_revisori' && !p.data){
		                        	quartRevDaInserire = true;
		                        }
		                    })
		                }
					}
					
					
					if($scope.sedutaGiunta.organo === 'G' && hasTaskAttivi){
						checkParereObbligatorio = $rootScope.configurationParams.parere_assessore_obbligatorio;
					}
					if($scope.sedutaGiunta.organo === 'C' && (hasTaskAttivi || quartRevDaInserire)){
						checkParereObbligatorio = $rootScope.configurationParams.parere_commissione_consigliare_obbligatorio;
					}

					$scope.listTaskAttivi = listTaskAttiviICuiTerminiNonSonoScaduti(atto.atto);
					

					if(checkParereObbligatorio && $scope.listTaskAttivi.length>0){
						$('#checkParereObbligatorioDialog').modal('show');
						return;
					}
				}

				var tmpMassivo = false;
				if ($scope.resoconto) {
					tmpMassivo = $scope.resoconto.massivo;
				}

				var tempSelected = null;
				if (tmpMassivo) {
					tempSelected = $scope.resoconto.selected;
				}

				$scope.resoconto = {};
				$scope.confermaEsito = readonly;
				$scope.atto = atto;
				//$log.debug(atto);
				//$log.debug("Resoconto - assessori:",$scope.profilosComponentiSeduta);
				$scope.dataDiscussione = $scope.sedutaGiunta.primaConvocazioneInizio;
				if($scope.sedutaGiunta.secondaConvocazioneInizio != null && typeof $scope.sedutaGiunta.secondaConvocazioneInizio != "undefined"){
					$scope.dataDiscussione = $scope.sedutaGiunta.secondaConvocazioneInizio;
				}
				if($scope.sedutaGiunta.inizioLavoriEffettiva != null && typeof $scope.sedutaGiunta.inizioLavoriEffettiva != "undefined"){
					$scope.dataDiscussione = $scope.sedutaGiunta.inizioLavoriEffettiva;
				}

				$scope.esiti = [];
				for(var i= 0; i<$scope.esitiAll.length; i++) {
					var insertTipoAtto = false;

					if($scope.esitiAll[i].tipiAtto!=null && $scope.esitiAll[i].tipiAtto.split(',').indexOf(atto.atto.tipoAtto.codice) !== -1){
						insertTipoAtto = true;
					}

					if(insertTipoAtto){
						$scope.esiti.push($scope.esitiAll[i]);
					}
				}

				if (! (curIdx && curIdx > 0)) {
					curIdx = 0;
				}
				$scope.resoconto.curIdx = curIdx;
				$scope.resoconto.massivo = tmpMassivo;

				if (tempSelected) {
					$scope.resoconto.selected = tempSelected;
				}

				OrdineGiorno.esito({attoodg: atto.id},function(result){
					///$log.debug(result);

					if(result.esito != null) {
						// TODO !!!!
						// if(atto.atto.stato != SedutaGiuntaConstants.statiAtto.propostaNumerata && atto.atto.stato != SedutaGiuntaConstants.statiAtto.propostaNotificata){
						//	$scope.resoconto.annulla = true;
						// }
						// else{
						//	$scope.resoconto.modifica = false;$scope.resetResoconto
						// }
						$scope.modificaComponenti = true;

						var tmpIdx = 0;
						if ($scope.resoconto.curIdx) {
							tmpIdx = $scope.resoconto.curIdx;
						}

						var tmpMassivo = $scope.resoconto.massivo;
						var tempSelected = null;

						if (tmpMassivo) {
							tempSelected = $scope.resoconto.selected;
						}

						$scope.resoconto = $scope.initResoconto(result, tmpMassivo, readonly, result.id);
						$scope.resoconto.composizione = result.composizione;
						$scope.caricaComposizioneSelezionata();
						$scope.resoconto.curIdx = tmpIdx;
						let idTipoAtto = 0;
						if ($scope.resoconto.massivo) {
							$scope.resoconto.labelTitle = "(" + seduta.selected[$scope.resoconto.curIdx].atto.codiceCifra + (seduta.selected[$scope.resoconto.curIdx].atto.numeroAdozione ? ' - ' + seduta.selected[$scope.resoconto.curIdx].atto.numeroAdozione : '') + ") " + seduta.selected[$scope.resoconto.curIdx].atto.oggetto;
							if (tempSelected) {
								$scope.resoconto.selected = tempSelected;
							}
							$scope.resoconto.ie = seduta.selected[curIdx].atto.ie;
							idTipoAtto = seduta.selected[curIdx].atto.tipoAtto.id;
						}else{
							$scope.resoconto.labelTitle = "(" + atto.atto.codiceCifra + (atto.atto.numeroAdozione ? ' - ' + atto.atto.numeroAdozione : '') + ") " + atto.atto.oggetto;
							$scope.resoconto.ie = atto.atto.ie;
							idTipoAtto = atto.atto.tipoAtto.id;
						}

						$scope.viewImmediataEseguibilita = false;
						TipoAtto.get({id: idTipoAtto}, function(result) {
							if (result && result.campi && result.campi.length && result.campi.length > 0) {
								for (var i = 0; i < result.campi.length; i++) {
									if(result.campi[i].codice == 'ie'){
										$scope.viewImmediataEseguibilita = result.campi[i].visibile;
									}
									// 		$scope.visibilitaCampi[result.campi[i].codice] = result.campi[i].visibile;
								}
							}
						});

						// if($scope.resoconto.ie === undefined || $scope.resoconto.ie === null){
						// 	$scope.resoconto.ie = true;
						// }
						$scope.resoconto.labelTitleShort = ": " + $scope.resoconto.labelTitle;
						if ($scope.resoconto.labelTitleShort.length > 150) {
							$scope.resoconto.labelTitleShort = $scope.resoconto.labelTitleShort.substring(0, 150);
						}

						$scope.resoconto.esito = result.esito;
						$scope.resoconto.dataDiscussione = moment(result.dataDiscussione);
						$scope.resoconto.isAttoModificatoInGiunta = result.isAttoModificatoInGiunta;

						//if (result.presidenteInizio) {
						//$scope.resoconto.presidenteInizio = result.presidenteInizio;
						//$scope.resoconto.presidenteInizio.qualifica = result.presidenteInizio.qualificaProfessionale;
						//}

						if (result.presidenteFine) {
							$scope.resoconto.presidenteFine = result.presidenteFine;
							$scope.resoconto.presidenteFine.qualifica = result.presidenteFine.qualificaProfessionale;
						}

						if (result.presidenteIE) {
							$scope.resoconto.presidenteIE = result.presidenteIE;
							if (result.presidenteIE.qualificaProfessionaleIE) {
								$scope.resoconto.presidenteIE.qualifica = result.presidenteIE.qualificaProfessionaleIE;
							}
						}
						if (result.segretarioIE) {
							$scope.resoconto.segretarioIE = result.segretarioIE;
							if (result.segretarioIE.qualificaProfessionaleIE) {
								$scope.resoconto.segretarioIE.qualifica = result.segretarioIE.qualificaProfessionaleIE;
							}
						}

						$scope.resoconto.votazioneSegreta = result.votazioneSegreta;
						$scope.resoconto.votazioneIE = result.votazioneIE;
						$scope.resoconto.approvataIE = result.approvataIE;
						$scope.resoconto.sedutaConvocata = result.sedutaConvocata;
						$scope.resoconto.attoPresentato = result.attoPresentato;
						$scope.resoconto.dichiarazioniVoto = result.dichiarazioniVoto;

						//if (result.segretarioInizio) {
						//$scope.resoconto.segretarioInizio = result.segretarioInizio;
						//$scope.resoconto.segretarioInizio.qualifica = result.segretarioInizio.qualificaProfessionale;
						//}

						if (result.segretarioFine) {
							$scope.resoconto.segretarioFine = result.segretarioFine;
							$scope.resoconto.segretarioFine.qualifica = result.segretarioFine.qualificaProfessionale;
						}

						if(result.scrutatori!=null){
							for (var i = 0; i < result.scrutatori.length; i++) {
								$scope.resoconto['scrutatore0'+(i+1)]=result.scrutatori[i];
								$scope.resoconto['scrutatore0'+(i+1)].qualifica=result.scrutatori[i].qualificaProfessionale;
							}
						}
						if(result.scrutatoriIE!=null){
							for (var i = 0; i < result.scrutatoriIE.length; i++) {
								$scope.resoconto['scrutatoreIE0'+(i+1)]=result.scrutatoriIE[i];
								$scope.resoconto['scrutatoreIE0'+(i+1)].qualifica=result.scrutatoriIE[i].qualificaProfessionaleIE;
							}
						}

						$scope.resoconto.numPresenti = result.numPresenti;
						$scope.resoconto.numAssenti = result.numAssenti;
						$scope.resoconto.numFavorevoli = result.numFavorevoli;
						$scope.resoconto.numContrari = result.numContrari;
						$scope.resoconto.numAstenuti = result.numAstenuti;
						$scope.resoconto.numNpv = result.numNpv;

						if($scope.resoconto.votazioneSegreta){
							$scope.resoconto.voto.segreto.presenti = result.numPresenti;
							$scope.resoconto.voto.segreto.assenti = result.numAssenti;
							$scope.resoconto.voto.segreto.favorevoli = result.numFavorevoli;
							$scope.resoconto.voto.segreto.contrari = result.numContrari;
							$scope.resoconto.voto.segreto.astenuti = result.numAstenuti;
							$scope.resoconto.voto.segreto.presentiNonVotanti = result.numNpv;
						} else {
							$scope.resoconto.voto.palese.presenti = result.numPresenti;
							$scope.resoconto.voto.palese.assenti = result.numAssenti;
							$scope.resoconto.voto.palese.favorevoli = result.numFavorevoli;
							$scope.resoconto.voto.palese.contrari = result.numContrari;
							$scope.resoconto.voto.palese.astenuti = result.numAstenuti;
							$scope.resoconto.voto.palese.presentiNonVotanti = result.numNpv;
						}
						$scope.resoconto.voto.immediataEseguibilita.presenti = result.numPresentiIE;
						$scope.resoconto.voto.immediataEseguibilita.assenti = result.numAssentiIE;
						$scope.resoconto.voto.immediataEseguibilita.favorevoli = result.numFavorevoliIE;
						$scope.resoconto.voto.immediataEseguibilita.contrari = result.numContrariIE;
						$scope.resoconto.voto.immediataEseguibilita.astenuti = result.numAstenutiIE;
						$scope.resoconto.voto.immediataEseguibilita.presentiNonVotanti = result.numNpvIE;


						$scope.resoconto.componenti = result.componenti;
//						for(var i=0; i< $scope.profilosComponentiSeduta.length; i++){
//							if($scope.componenteExist($scope.profilosComponentiSeduta[i]) == false) {
//								$scope.resoconto.componenti.push($scope.getComponenteVuoto($scope.profilosComponentiSeduta[i],atto,odg));
//							}
//						}
					}
					/*
    			 *  IN ATTICO NON PREVISTO
    			 *
    			else if(angular.isDefined(previous)) {

    				// Presentare resoconto atto precedente
    				OrdineGiorno.esito({id: odg.id,attoodg: previous.id},function(result){
    					$log.debug(result);
    	    			if(result.esito != null) {
    	    				$scope.resoconto.componenti = result.componenti;
    	    				for(var i=0; i< $scope.profilosComponentiSeduta.length; i++){
    	    					if($scope.componenteExist($scope.profilosComponentiSeduta[i]) == false) {
    	    						$scope.resoconto.componenti.push($scope.getComponenteVuoto($scope.profilosComponentiSeduta[i],atto,odg));
    	    					}
    	    				}
    	    			}
    	    			else{
    	    				for(var i=0; i< $scope.profilosComponentiSeduta.length; i++){
    	    					$scope.resoconto.componenti.push($scope.getComponenteVuoto($scope.profilosComponentiSeduta[i],atto,odg));
    	    	    		}
    	    			}
    				});
    			}
    			*/
					else {
//						$scope.resoconto.componenti = [];
//						for(var i=0; i< $scope.profilosComponentiSeduta.length; i++){
//							$scope.resoconto.componenti.push($scope.getComponenteVuoto($scope.profilosComponentiSeduta[i],atto,odg));
//						}
						
						$scope.caricaComposizionePredefinita();
					}

					// Init del presidente di questo resoconto con il presidente della
					// giunta...
					//if ($scope.resoconto.presidenteInizio == null){
					//var compPres = $scope.getPresidenteVuoto(null,odg,true);
					//$scope.resoconto.presidenteInizio = compPres;
					//}
					if ($scope.resoconto.presidenteFine == null || $scope.resoconto.presidenteFine.profilo == null || !$scope.resoconto.presidenteFine.profilo.id){
						var compPres = $scope.getPresidenteVuoto(null,odg,false);
						$scope.resoconto.presidenteFine = compPres;
					}
					if ($scope.resoconto.presidenteIE == null || $scope.resoconto.presidenteIE.profilo == null || !$scope.resoconto.presidenteIE.profilo.id){
						var compPres = $scope.getPresidenteVuoto(null,odg,false);
						$scope.resoconto.presidenteIE = compPres;
					}
					// Init del segretario di questo resoconto con il segretario della
					// giunta...
					//if ($scope.resoconto.segretarioInizio == null){
					//var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,true,true);
					//$scope.resoconto.segretarioInizio = compSegr;
					//}
					if ($scope.resoconto.segretarioFine == null || $scope.resoconto.segretarioFine.profilo == null || !$scope.resoconto.segretarioFine.profilo.id){
						var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,false,true);
						$scope.resoconto.segretarioFine = compSegr;
					}
					if ($scope.resoconto.segretarioIE == null || $scope.resoconto.segretarioIE.profilo == null || !$scope.resoconto.segretarioIE.profilo.id){
						var compSegr = $scope.getSegretarioVuoto($scope.sedutaGiunta.segretario,null,odg,false,true);
						$scope.resoconto.segretarioIE = compSegr;
					}
					$scope.verificaPresentiNumVoti();
					if (!($('#resocontoAtto').is(':visible'))) {
						$('#resocontoAtto').modal('show');
					}
					else {
						$('body').addClass('modal-open');
					}

					$scope.scenariDisabilitazione = [];
					if ($scope.sedutaAttoId) {
						$scope.scenariDisabilitazione.push("ModificheCoordinamentoTesto");
					}
					else if(atto.bloccoModifica || (
						!ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_GIUNTA']) &&
						!ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_CONSIGLIO'])
					)) {
						$scope.scenariDisabilitazione.push("VotazioniPresenzeNonModificabili");
					}

					$scope.checkVisResoconto();
					$scope.infoTab = 0;
				});

				//$log.debug($scope.profilosComponentiSeduta);
			}
			
			
			$scope.caricaComposizioneSelezionata = function() {
				if($scope.sedutaGiunta.organo === 'G'){
					$scope.versioneComposizioneSelezionata = {};
					for(var i=0; i< $scope.versioniComposizioneGiunta.length; i++){
						if($scope.resoconto.composizione && $scope.resoconto.composizione.id== $scope.versioniComposizioneGiunta[i].id){
							$scope.versioneComposizioneSelezionata =$scope.versioniComposizioneGiunta[i];
							break;
						}
						
					}
					

				}else if($scope.sedutaGiunta.organo === 'C'){
					$scope.versioneComposizioneSelezionata = {};
					
					for(var i=0; i< $scope.versioniComposizioneConsiglio.length; i++){
						if($scope.resoconto.composizione && $scope.resoconto.composizione.id== $scope.versioniComposizioneConsiglio[i].id){
							$scope.versioneComposizioneSelezionata =$scope.versioniComposizioneConsiglio[i];
							break;
						}
						
					}
				}
				
				$scope.resoconto.componenti = [];
				let odg;
				if($scope.odgSelected && $scope.odgSelected.id > 0){
					odg = $scope.odgSelected;
				}else if($scope.atto && $scope.atto.id > 0){
					odg = $scope.getOdgFromAttoId($scope.sedutaGiunta.odgs, $scope.atto.id);
				}
				if(odg && $scope.versioneComposizioneSelezionata && $scope.versioneComposizioneSelezionata.profiliComposizione){
					for(var i=0; i< $scope.versioneComposizioneSelezionata.profiliComposizione.length; i++){
						if($scope.versioneComposizioneSelezionata.profiliComposizione[i].valido && $scope.versioneComposizioneSelezionata.profiliComposizione[i].valido == true){
							$scope.resoconto.componenti.push($scope.getComponenteVuotoDaComposizione($scope.versioneComposizioneSelezionata.profiliComposizione[i],$scope.atto,odg));
						}
					}
				}
				
				if ($scope.resoconto.votazioneIE){
					for ( var index in $scope.resoconto.componenti) {
						$scope.resoconto.componenti[index].presenteIE = true;
						$scope.resoconto.componenti[index].votazioneIE = "FAV";
					}
					$scope.updateTotals();
				}
				
				
			}
			
			$scope.caricaComposizionePredefinita = function(odg) {
				if($scope.sedutaGiunta.organo === 'G'){
					$scope.versioneComposizioneSelezionata = {};
					for(var i=0; i< $scope.versioniComposizioneGiunta.length; i++){
						if($scope.versioniComposizioneGiunta[i].predefinita && $scope.versioniComposizioneGiunta[i].predefinita==true){
							$scope.versioneComposizioneSelezionata =$scope.versioniComposizioneGiunta[i];
							$scope.resoconto.composizione = {
									'id': $scope.versioneComposizioneSelezionata.id
									};
							break;
						}
					}
				}
				if($scope.sedutaGiunta.organo === 'C'){
					$scope.versioneComposizioneSelezionata = {};
					for(var i=0; i< $scope.versioniComposizioneConsiglio.length; i++){
						if($scope.versioniComposizioneConsiglio[i].predefinita && $scope.versioniComposizioneConsiglio[i].predefinita==true){
							$scope.versioneComposizioneSelezionata =$scope.versioniComposizioneConsiglio[i];
							$scope.resoconto.composizione = {
									'id': $scope.versioneComposizioneSelezionata.id
									};
							break;
						}
					}
				}
				if(odg==null){
					odg = $scope.getOdgFromAttoId($scope.sedutaGiunta.odgs, $scope.atto.id);
				}
				
				for(var i=0; i< $scope.versioneComposizioneSelezionata.profiliComposizione.length; i++){
					if($scope.versioneComposizioneSelezionata.profiliComposizione[i].valido && $scope.versioneComposizioneSelezionata.profiliComposizione[i].valido == true){
						$scope.resoconto.componenti.push($scope.getComponenteVuotoDaComposizione($scope.versioneComposizioneSelezionata.profiliComposizione[i],null,odg));
					}
				}
			}
			
			
			
			$scope.componenteExistInComposizione = function(componenteResoconto) {
				for(var i = 0; i < $scope.resoconto.componenti.length; i++) {
					if($scope.resoconto.componenti[i].profilo.id == profilo.id) {
						return true;
					}
				}

				return false;
			}
			
			

			function listTaskAttiviICuiTerminiNonSonoScaduti(atto){
				let taskAttivi = [];
				angular.forEach(atto.taskAttivi,function(item){
					let nominativoDescrizione = false;
					let task = {'nominativo': null, 'descrizione': null};
					var infos = $scope.getInfoCommissioni(atto);
					
					if(item.nominativo != null){
						task.nominativo = item.nominativo;
						nominativoDescrizione = true;
					}else if(item.aoo){
						task.nominativo = item.aoo.descrizione;
						nominativoDescrizione = true;
					}
					if(item.descrizione != null){
						task.descrizione = taskDescrizionePrefix + item.descrizione;
					}
					
					for(var j = 0; j< infos.length; j++){
						var infoAoo = infos[j].aoo;
						var scadenza = null;
						if(item.aoo!= null && infoAoo === item.aoo.descrizione){
							if(infos[j].conf && infos[j].conf.dataManuale && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
								scadenza = new Date(infos[j].conf.dataManuale);
								scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
							}
			    			else if(infos[j].conf && infos[j].conf.dataCreazione && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
			    				scadenza = new Date(infos[j].conf.dataCreazione);
			    				scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
			    			}
							var adesso = new Date();
							if(scadenza){
			    				if(scadenza.getTime() > adesso.getTime()){
			    					taskAttivi.push(task);
			    					break;
			    				}
			    			}
						}
					}
					
					
					
				})

				if($scope.sedutaGiunta.organo === 'C' && $rootScope.configurationParams.parere_cons_quart_rev_cont_obbligatorio == 'true'){
					if(atto.pareri && atto.pareri.length > 0 ){
						atto.pareri.forEach(p => {
	                        if(p && !p.annullato && p.tipoAzione && p.tipoAzione.codice== 'parere_quartiere_revisori' && !p.parereSintetico){
                            	var adesso = new Date();
                            	if(p.dataScadenza){
            	    				if(new Date(p.dataScadenza) > adesso){
            	    					let task = {'descrizione': 'Mancanza Parere Cons. Quart. / Revisori Contabili', 'nominativo': p.aoo.descrizione};
            	    					taskAttivi.push(task);
            	    				}
            	    			}else{
            	    				let task = {'descrizione': 'Mancanza Parere Cons. Quart. / Revisori Contabili', 'nominativo': p.aoo.descrizione};
        	    					taskAttivi.push(task);
            	    			}
	                        }
	                    })
	                }
				}
				return taskAttivi;
			}

			/**
			 * Controlla se un assessore è presente nei componenti di giunta
			 */
			$scope.componenteExist = function(profilo) {
				for(var i = 0; i < $scope.resoconto.componenti.length; i++) {
					if($scope.resoconto.componenti[i].profilo.id == profilo.id) {
						return true;
					}
				}

				return false;
			}

			/**
			 * Crea un componente vuoto
			 */
			$scope.getComponenteVuoto = function(profilo,atto,odg) {
				var componente = {
					id: null,
					profilo: profilo,
					qualifica: null,
					qualificaProfessionale: null,
					qualificaProfessionaleIE: null,
					atto: atto,
					ordineGiorno: odg,
					presente: true,
					presenteIE: true,
					isSindaco: false,
					//isPresidenteInizio: false,
					//isSegretarioInizio: false,
					isPresidenteFine: false,
					isSegretarioFine: false,
					isScrutatore: false,
					isPresidenteIE: false,
					isSegretarioIE: false,
					isScrutatoreIE: false
				};
				return componente;
			}
			
			$scope.getComponenteVuotoDaComposizione = function(componente,atto,odg) {
				var componente = {
					id: null,
					profilo: componente.profilo,
					qualifica: null,
					qualificaProfessionale: componente.qualificaProfessionale,
					qualificaProfessionaleIE: componente.qualificaProfessionale,
					atto: atto,
					ordineGiorno: odg,
					presente: null,
					presenteIE: null,
					isSindaco: false,
					//isPresidenteInizio: false,
					//isSegretarioInizio: false,
					isPresidenteFine: false,
					isSegretarioFine: false,
					isScrutatore: false,
					isPresidenteIE: false,
					isSegretarioIE: false,
					isScrutatoreIE: false
				};
				return componente;
			}

			/**
			 * Crea un componente vuoto da usare come presidente, inizializzato con
			 * il presidente della Seduta di Giunta
			 */
			$scope.getPresidenteVuoto = function(atto,odg,inizio) {




				var componente = {
					id: null,
					//profilo: $scope.sedutaGiunta.presidente,
					qualifica: null,
					qualificaProfessionale: null,
					qualificaProfessionaleIE: null,
					atto: atto,
					ordineGiorno: odg,
					presente: true,
					presenteIE: true,
					isSindaco: false,
					//isPresidenteInizio: inizio,
					//isSegretarioInizio: false,
					isPresidenteIE: false,
					isPresidenteFine: !inizio,
					isSegretarioFine: false,
					isSegretarioIE: false,
					isScrutatore: false,
					isScrutatoreIE: false
				};

				if($scope.isOdg() && $scope.sedutaGiunta && $scope.sedutaGiunta.presidente &&
					$scope.sedutaGiunta.presidente.validoSedutaGiunta && $scope.sedutaGiunta.presidente.validoSedutaGiunta == true){
					componente.profilo = $scope.sedutaGiunta.presidente;
				}
				else if($scope.sedutaGiunta && $scope.sedutaGiunta.presidente &&
					$scope.sedutaGiunta.presidente.validoSedutaConsiglio && $scope.sedutaGiunta.presidente.validoSedutaConsiglio == true){
					componente.profilo = $scope.sedutaGiunta.presidente;
				}

				return componente;
			}

			/**
			 * Crea un componente vuoto da usare come segretario, inizializzato con
			 * il segretario della Seduta di Giunta
			 */
			$scope.getSegretarioVuoto = function(profilo, atto,odg,inizio,presente) {
				var componente = {
					id: null,
					profilo: profilo,
					qualifica: null,
					qualificaProfessionale: null,
					qualificaProfessionaleIE: null,
					atto: atto,
					ordineGiorno: odg,
					presente: presente,
					presenteIE: presente,
					isSindaco: false,
					//isPresidenteInizio: false,
					//isSegretarioInizio: inizio,
					isPresidenteIE: false,
					isPresidenteFine: false,
					isSegretarioFine: !inizio,
					isSegretarioIE: false,
					isScrutatore: false,
					isScrutatoreIE: false
				};

				return componente;
			}

			/**
			 * Crea un componente vuoto da usare come scrutatore, inizializzato con
			 * lo scrutatore della Seduta di Giunta
			 */
			$scope.getScrutatoreVuoto = function(atto,odg,inizio) {
				var componente = {
					id: null,
					profilo: null,
					qualifica: null,
					qualificaProfessionale: null,
					qualificaProfessionaleIE: null,
					atto: atto,
					ordineGiorno: odg,
					presente: true,
					presenteIE: true,
					isSindaco: false,
					//isPresidenteInizio: false,
					//isSegretarioInizio: false,
					isPresidenteIE: false,
					isPresidenteFine: false,
					isSegretarioFine: false,
					isSegretarioIE: false,
					isScrutatore: false,
					isScrutatoreIE: false
				};

				return componente;
			}

			/**
			 * funzione che si occupa di ritirare un atto che si trova nello stato
			 * Atto Inseribile In Odg
			 */
			$scope.ritiraAtto = function(atto){
				TaskDesktop.ritiraAtto(atto,function(data){
					// $('#ritiraAttoModal').modal('hide');
					ngToast.create(  { className: 'success', content: 'Ritiro atto effettuato con successo' } );
					$scope.refresh();
				});
			};

			$scope.validaResocontoForm = function(){
				var isValidTabGestioneSeduta = false;
				var isValidTabPresenzeVotazioni = true;
				var isValidTabImmediataEseguibilita = true;
				var isValidCongruenzaPresentiNumVoti = ($scope.erroreCongruenzaPresentiNumVoti ? false : true);
				if(isValidCongruenzaPresentiNumVoti && $scope.resoconto && $scope.resoconto.esito!=null){
					if($scope.getEsitoObj($scope.resoconto.esito).registraVotazione){
						if(
							$scope.resoconto.dataDiscussione!=null &&
							$scope.resoconto.isAttoModificatoInGiunta!=null &&
							//$scope.resoconto.presidenteInizio.profilo!=null && $scope.resoconto.presidenteInizio.profilo.id!=null &&
							//$scope.resoconto.presidenteInizio.qualifica!=null && $scope.resoconto.presidenteInizio.qualifica.id!=null &&
							$scope.resoconto.presidenteFine.profilo!=null && $scope.resoconto.presidenteFine.profilo.id!=null &&
							$scope.resoconto.presidenteFine.qualifica!=null && $scope.resoconto.presidenteFine.qualifica.id!=null &&
							//$scope.resoconto.segretarioInizio.profilo!=null && $scope.resoconto.segretarioInizio.profilo.id!=null &&
							//$scope.resoconto.segretarioInizio.qualifica!=null && $scope.resoconto.segretarioInizio.qualifica.id!=null &&
							$scope.resoconto.segretarioFine.profilo!=null && $scope.resoconto.segretarioFine.profilo.id!=null &&
							$scope.resoconto.segretarioFine.qualifica!=null && $scope.resoconto.segretarioFine.qualifica.id!=null
						){
							isValidTabGestioneSeduta = true;
						}

						if ($scope.resoconto.scrutatore01 != null && $scope.resoconto.scrutatore01.profilo != null && $scope.resoconto.scrutatore01.profilo.id != null) {
							if ($scope.resoconto.scrutatore01.qualifica == null || $scope.resoconto.scrutatore01.qualifica.id == null) {
								isValidTabGestioneSeduta = false;
							}
						}
						if ($scope.resoconto.scrutatore02 != null && $scope.resoconto.scrutatore02.profilo != null && $scope.resoconto.scrutatore02.profilo.id != null) {
							if ($scope.resoconto.scrutatore02.qualifica == null || $scope.resoconto.scrutatore02.qualifica.id == null) {
								isValidTabGestioneSeduta = false;
							}
						}
						if ($scope.resoconto.scrutatore03 != null && $scope.resoconto.scrutatore03.profilo != null && $scope.resoconto.scrutatore03.profilo.id != null) {
							if ($scope.resoconto.scrutatore03.qualifica == null || $scope.resoconto.scrutatore03.qualifica.id == null) {
								isValidTabGestioneSeduta = false;
							}
						}

						if($scope.visibilitaPresenza){
							for ( var index in $scope.resoconto.componenti) {
								if($scope.resoconto.componenti[index].presente==null){
									isValidTabPresenzeVotazioni = false;
									break;
								} else {
									if(!$scope.resoconto.votazioneSegreta && $scope.resoconto.componenti[index].presente==true && ($scope.resoconto.componenti[index].votazione==null) &&
										//(!$scope.resoconto.componenti[index].isSegretarioInizio) &&
										(!$scope.resoconto.componenti[index].isSegretarioFine) ){
										isValidTabPresenzeVotazioni = false;
										break;
									}
								}
							}
						} else {
							if($scope.visibilitaVotazione){
								for ( var index in $scope.resoconto.componenti) {
									if($scope.resoconto.componenti[index].votazione==null &&
										//(!$scope.resoconto.componenti[index].isSegretarioInizio) &&
										(!$scope.resoconto.componenti[index].isSegretarioFine) ){
										isValidTabPresenzeVotazioni = false;
										break;
									}
								}
							}
						}

						if($scope.resoconto.votazioneIE){
							if($scope.resoconto.approvataIE==null){
								isValidTabImmediataEseguibilita = false;
							}
							if($scope.resoconto.presidenteIE.profilo==null	 || $scope.resoconto.presidenteIE.profilo.id==null ||
								$scope.resoconto.presidenteIE.qualifica==null || $scope.resoconto.presidenteIE.qualifica.id==null ||
								$scope.resoconto.segretarioIE.profilo==null	 || $scope.resoconto.segretarioIE.profilo.id==null ||
								$scope.resoconto.segretarioIE.qualifica==null || $scope.resoconto.segretarioIE.qualifica.id==null
							) {
								isValidTabImmediataEseguibilita = false;
							}

							if ($scope.resoconto.scrutatoreIE01 != null && $scope.resoconto.scrutatoreIE01.profilo != null && $scope.resoconto.scrutatoreIE01.profilo.id != null) {
								if ($scope.resoconto.scrutatoreIE01.qualifica == null || $scope.resoconto.scrutatoreIE01.qualifica.id == null) {
									isValidTabImmediataEseguibilita = false;
								}
							}
							if ($scope.resoconto.scrutatoreIE02 != null && $scope.resoconto.scrutatoreIE02.profilo != null && $scope.resoconto.scrutatoreIE02.profilo.id != null) {
								if ($scope.resoconto.scrutatoreIE02.qualifica == null || $scope.resoconto.scrutatoreIE02.qualifica.id == null) {
									isValidTabImmediataEseguibilita = false;
								}
							}
							if ($scope.resoconto.scrutatoreIE03 != null && $scope.resoconto.scrutatoreIE03.profilo != null && $scope.resoconto.scrutatoreIE03.profilo.id != null) {
								if ($scope.resoconto.scrutatoreIE03.qualifica == null || $scope.resoconto.scrutatoreIE03.qualifica.id == null) {
									isValidTabImmediataEseguibilita = false;
								}
							}

							if(!isValidTabImmediataEseguibilita){
								for ( var index in $scope.resoconto.componenti) {
									if($scope.resoconto.componenti[index].presenteIE==null){
										isValidTabPresenzeVotazioni = false;
										break;
									} else {
										if($scope.resoconto.componenti[index].presenteIE==true && ($scope.resoconto.componenti[index].votazioneIE==null)){
											isValidTabImmediataEseguibilita = false;
											break;
										}
									}
								}
							}
						}
					} else {
						isValidTabGestioneSeduta = true;
					}
				}

				return (isValidCongruenzaPresentiNumVoti && isValidTabGestioneSeduta && isValidTabPresenzeVotazioni && isValidTabImmediataEseguibilita);

			}

			/**
			 * Salva il resoconto
			 */
			$scope.confermaResoconto = function() {
				$('#formConfirmation').modal('show');
			}

			/**
			 * Salva il resoconto
			 */
			$scope.saveResoconto = function() {

				$('#formConfirmation').modal('hide');

				//$log.debug("saveResoconto:",$scope.resoconto);
				$scope.taskLoading = true;

				var odg = null;
				var indexToRemoveSegretario = -1;
				var compBk;
				if($scope.sedutaGiunta.organo == 'G'){
					var compBk = $scope.resoconto.componenti;
				}

				if(!$scope.getEsitoObj($scope.resoconto.esito).registraVotazione) {
					$scope.resoconto.componenti = [];
				}
				else {
					// Ciclo sui componenti e aggiorno il profilo del presidente e
					// del segretario...

					for(var i = 0; i < $scope.resoconto.componenti.length; i++) {
						var componente = $scope.resoconto.componenti[i];
						if(odg==null){
							odg=componente.ordineGiorno;
						}

						//componente.isPresidenteInizio = false;
						componente.isPresidenteFine = false;
						componente.isPresidenteIE = false;
						componente.isSindaco = false;
						//componente.isSegretarioInizio = false;
						componente.isSegretarioIE = false;
						componente.isScrutatore = false;
						componente.isScrutatoreIE = false;

						//var segretarioInizio = null;
						var segretarioFine = null;
						var segretarioIE = null;

						//if(componente.profilo.id==$scope.resoconto.presidenteInizio.profilo.id) {
						//componente.isPresidenteInizio = true;
						//componente.profilo = $scope.resoconto.presidenteInizio.profilo;
						//componente.qualificaProfessionale = $scope.resoconto.presidenteInizio.qualifica;
						//}

						if(componente.profilo && componente.profilo.grupporuolo && componente.profilo.grupporuolo.hasRuoli){
							if($scope.isCapoEnte(componente.profilo.grupporuolo.hasRuoli) == true){
								componente.isSindaco = true;
							}
						}
						
						



						if(componente.profilo.id==$scope.resoconto.presidenteFine.profilo.id) {
							componente.isPresidenteFine = true;
							var gruppoRuolo = componente.profilo.grupporuolo;
							componente.profilo = $scope.resoconto.presidenteFine.profilo;
							if(gruppoRuolo && gruppoRuolo != null && componente.profilo && (!componente.profilo.grupporuolo || componente.profilo.grupporuolo == null) ){
								componente.profilo.grupporuolo = gruppoRuolo;
							}
							componente.qualificaProfessionale = $scope.resoconto.presidenteFine.qualifica;
						}

						//if(componente.profilo.id==$scope.resoconto.segretarioInizio.profilo.id) {
						//componente.isSegretarioInizio = true;
						//componente.profilo = $scope.resoconto.segretarioInizio.profilo;
						//componente.qualificaProfessionale = $scope.resoconto.segretarioInizio.qualifica;
						//segretarioInizio = componente;
						//}
						if(componente.profilo.id==$scope.resoconto.segretarioFine.profilo.id) {
							componente.isSegretarioFine = true;
							var gruppoRuolo = componente.profilo.grupporuolo;
							componente.profilo = $scope.resoconto.segretarioFine.profilo;
							if(gruppoRuolo && gruppoRuolo != null && componente.profilo && (!componente.profilo.grupporuolo || componente.profilo.grupporuolo == null) ){
								componente.profilo.grupporuolo = gruppoRuolo;
							}
							componente.qualificaProfessionale = $scope.resoconto.segretarioFine.qualifica;
							segretarioFine = componente;
						}else if (componente.isSegretarioFine == true){
							indexToRemoveSegretario = i;

						}else {
							componente.isSegretarioFine = false;
						}

						if (
							$scope.resoconto.scrutatore01!=null && $scope.resoconto.scrutatore01.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatore01.profilo.id
						){
							componente.isScrutatore = true;
							componente.qualificaProfessionale = $scope.resoconto.scrutatore01.qualifica;
							$scope.resoconto.scrutatori.push(componente);
						}
						if (
							$scope.resoconto.scrutatore02!=null && $scope.resoconto.scrutatore02.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatore02.profilo.id
						){
							componente.isScrutatore = true;
							componente.qualificaProfessionale = $scope.resoconto.scrutatore02.qualifica;
							$scope.resoconto.scrutatori.push(componente);
						}

						if (
							$scope.resoconto.scrutatore03!=null && $scope.resoconto.scrutatore03.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatore03.profilo.id
						){
							componente.isScrutatore = true;
							componente.qualificaProfessionale = $scope.resoconto.scrutatore03.qualifica;
							$scope.resoconto.scrutatori.push(componente);
						}

						if($scope.resoconto.votazioneIE){
							if(componente.profilo.id==$scope.resoconto.presidenteIE.profilo.id) {
								componente.isPresidenteIE = true;
								var gruppoRuolo = componente.profilo.grupporuolo;
								componente.profilo = $scope.resoconto.presidenteIE.profilo;
								if(gruppoRuolo && gruppoRuolo != null && componente.profilo && (!componente.profilo.grupporuolo || componente.profilo.grupporuolo == null) ){
									componente.profilo.grupporuolo = gruppoRuolo;
								}
								componente.qualificaProfessionaleIE = $scope.resoconto.presidenteIE.qualifica;
							}

							if(componente.profilo.id==$scope.resoconto.segretarioIE.profilo.id) {
								componente.isSegretarioIE = true;
								var gruppoRuolo = componente.profilo.grupporuolo;
								componente.profilo = $scope.resoconto.segretarioIE.profilo;
								if(gruppoRuolo && gruppoRuolo != null && componente.profilo && (!componente.profilo.grupporuolo || componente.profilo.grupporuolo == null) ){
									componente.profilo.grupporuolo = gruppoRuolo;
								}
								componente.qualificaProfessionaleIE = $scope.resoconto.segretarioIE.qualifica;
								segretarioIE = componente;
							}

							if (
								$scope.resoconto.scrutatoreIE01!=null && $scope.resoconto.scrutatoreIE01.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatoreIE01.profilo.id
							){
								componente.isScrutatoreIE = true;
								componente.qualificaProfessionaleIE = $scope.resoconto.scrutatoreIE01.qualifica;
								$scope.resoconto.scrutatoriIE.push(componente);
							}
							if (
								$scope.resoconto.scrutatoreIE02!=null && $scope.resoconto.scrutatoreIE02.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatoreIE02.profilo.id
							){
								componente.isScrutatoreIE = true;
								componente.qualificaProfessionaleIE = $scope.resoconto.scrutatoreIE02.qualifica;
								$scope.resoconto.scrutatoriIE.push(componente);
							}

							if (
								$scope.resoconto.scrutatoreIE03!=null && $scope.resoconto.scrutatoreIE03.profilo!=null && componente.profilo.id==$scope.resoconto.scrutatoreIE03.profilo.id
							){
								componente.isScrutatoreIE = true;
								componente.qualificaProfessionaleIE = $scope.resoconto.scrutatoreIE03.qualifica;
								$scope.resoconto.scrutatoriIE.push(componente);
							}
						}
					}

					//if (segretarioInizio == null) {
					//segretarioInizio = $scope.getSegretarioVuoto($scope.resoconto.segretarioInizio.profilo,null,odg,true,true);
					//segretarioInizio.qualificaProfessionale = $scope.resoconto.segretarioInizio.qualifica;
					//$scope.resoconto.componenti.push(segretarioInizio);
					//}
					if (segretarioFine == null) {

						if(indexToRemoveSegretario>-1){
							$scope.resoconto.componenti.splice(indexToRemoveSegretario,1);
						}

						segretarioFine = $scope.getSegretarioVuoto($scope.resoconto.segretarioFine.profilo,null,odg,false,true);
						segretarioFine.qualificaProfessionale = $scope.resoconto.segretarioFine.qualifica;
						$scope.resoconto.componenti.push(segretarioFine);
						//}
					}
					if ($scope.resoconto.votazioneIE && (segretarioIE == null)) {
						//if(segretarioInizio.profilo.id==$scope.resoconto.segretarioIE.profilo.id) {
						//segretarioInizio.isSegretarioIE=true;
						//}
						//else
						if((segretarioFine != null) && (segretarioFine.profilo.id==$scope.resoconto.segretarioIE.profilo.id)) {
							segretarioFine.isSegretarioIE=true;
							segretarioFine.qualificaProfessionaleIE = $scope.resoconto.segretarioIE.qualifica;
						}
						else {
							segretarioIE = $scope.getSegretarioVuoto($scope.resoconto.segretarioIE.profilo,null,odg,true,true);
							segretarioIE.qualificaProfessionaleIE = $scope.resoconto.segretarioIE.qualifica;
							segretarioFine.qualificaProfessionaleIE=null;
							segretarioIE.isSegretarioFine=false;
							segretarioIE.isSegretarioIE=true;
							$scope.resoconto.componenti.push(segretarioIE);
						}
					}

					if($scope.resoconto.votazioneSegreta){
						$scope.resoconto.numPresenti = $scope.resoconto.voto.segreto.presenti;
						$scope.resoconto.numAssenti = $scope.resoconto.voto.segreto.assenti;
						$scope.resoconto.numFavorevoli = $scope.resoconto.voto.segreto.favorevoli;
						$scope.resoconto.numContrari = $scope.resoconto.voto.segreto.contrari;
						$scope.resoconto.numAstenuti = $scope.resoconto.voto.segreto.astenuti;
						$scope.resoconto.numNpv = $scope.resoconto.voto.segreto.presentiNonVotanti;
					}
					else {
						$scope.resoconto.numPresenti = $scope.resoconto.voto.palese.presenti;
						$scope.resoconto.numAssenti = $scope.resoconto.voto.palese.assenti;
						$scope.resoconto.numFavorevoli = $scope.resoconto.voto.palese.favorevoli;
						$scope.resoconto.numContrari = $scope.resoconto.voto.palese.contrari;
						$scope.resoconto.numAstenuti = $scope.resoconto.voto.palese.astenuti;
						$scope.resoconto.numNpv = $scope.resoconto.voto.palese.presentiNonVotanti;
					}

					$scope.resoconto.numPresentiIE = $scope.resoconto.voto.immediataEseguibilita.presenti;
					$scope.resoconto.numAssentiIE = $scope.resoconto.voto.immediataEseguibilita.assenti;
					$scope.resoconto.numFavorevoliIE = $scope.resoconto.voto.immediataEseguibilita.favorevoli;
					$scope.resoconto.numContrariIE = $scope.resoconto.voto.immediataEseguibilita.contrari;
					$scope.resoconto.numAstenutiIE = $scope.resoconto.voto.immediataEseguibilita.astenuti;
					$scope.resoconto.numNpvIE = $scope.resoconto.voto.immediataEseguibilita.presentiNonVotanti;
				}

				if($scope.resoconto.massivo){
					// Giorgio CDFATTICOMEV-65: procedere su ogni atto selezionato
					// delete $scope.resoconto.massivo;
					var selected = [];
					selected.push($scope.resoconto.selected[$scope.resoconto.curIdx].id);

					// for(var i = 0; i < $scope.resoconto.selected.length; i++){
					//	selected.push($scope.resoconto.selected[i].id);
					// }
					// delete $scope.resoconto.selected;
					//$log.debug("RESOCONTO MASSIVO:",$scope.resoconto);
					//$log.debug("RESOCONTO MASSIVO - Atti:", selected);

					var resocontoMassivo = {
						resoconto: $scope.resoconto,
						atti: selected
					};

					$scope.appoggioResoconto = $scope.resoconto;
					// Giorgio: TODO - gestione disabilitazione interfaccia
					// $scope.appoggioScenari = $scope.scenariDisabilitazione;

					// $scope.scenariDisabilitazione = [];
					// $scope.scenariDisabilitazione.push("VotazioniPresenzeNonModificabili");

					// Giorgio CDFATTICOMEV-65: gestione per passare all'atto selezionato successivo
					OrdineGiorno.salvaesiti(resocontoMassivo,function(result){
						$log.debug("Salvataggio: ", result);
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );

						SedutaGiunta.get({id: $scope.sedutaGiunta.id}, function(result) {
							$log.debug("Seduta: ", result);
							for(var i = 0; i < result.odgs.length; i++) {
								if (result.odgs[i].id == $scope.odgSelected.id) {
									$scope.odgSelected.attos = result.odgs[i].attos;
								}
								for (var j = 0; j < $scope.sedutaGiunta.selected.length; j++) {
									for(var k=0; k < result.odgs[i].attos.length; k++) {
										if (result.odgs[i].attos[k].id == $scope.sedutaGiunta.selected[j].id) {
											$scope.sedutaGiunta.selected[j] = result.odgs[i].attos[k];
										}
									}
								}
							}

							$scope.taskLoading = false;

							// Giorgio: TODO - gestione disabilitazione interfaccia
							// if ($scope.appoggioScenari || $scope.appoggioScenari.length > 0) {
							//	$scope.scenariDisabilitazione = $scope.appoggioScenari;
							//	delete $scope.appoggioScenari;
							// }

							if (!$scope.resoconto.stepMass) {
								$scope.resoconto.stepMass = 0;
							}

							var nextIdx = $scope.resoconto.curIdx + $scope.resoconto.stepMass;

							if ($scope.sedutaGiunta.selected[nextIdx].esito) {
								$scope.apriResoconto($scope.sedutaGiunta.selected[nextIdx],$scope.sedutaGiunta,false,nextIdx);
							}
							else {
								$scope.apriRegistraEsitoMassivo($scope.sedutaGiunta, nextIdx);
								if($scope.sedutaGiunta.organo == 'C'){
									$scope.svuotaVotazione();
									$scope.svuotaVotazioneIE();
								}else if(compBk && $scope.sedutaGiunta.organo == 'G'){
									$scope.resoconto.componenti = compBk;
								}
							}
						});

						//	$('#resocontoAtto').modal('hide');
						//	$scope.refresh();
					});
				}
				else {
					if($scope.confermaEsito){
						let attoOdgId = {'attoOdgId': $scope.resoconto.id};
						OrdineGiorno.confermaesito(attoOdgId,function(result){
							//$log.debug(result);
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$('#resocontoAtto').modal('hide');
							$scope.confermaEsito = null;
							$scope.refresh();
							$scope.taskLoading = false;
						});
					}
					else{

						OrdineGiorno.modificacomponenti($scope.resoconto,function(result){
							//$log.debug(result);
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$('#resocontoAtto').modal('hide');
							if($scope.$parent.$parent){
								$scope.$parent.refresh();
							}else{
								$scope.refresh();
							}
							
							$scope.taskLoading = false;
						});
					}
				}
			}

			/**
			 * Annulla il resoconto - Modal di conferma
			 */
			$scope.annullaResoconto = function(atto) {
				$('#resocontoAtto').modal('hide');
				$('#annullaResocontoAtto').modal('show');
				$scope.listaAnnulla = [atto];
			}

			/**
			 * Annulla il resoconto - Annullamento
			 */
			$scope.confirmAnnullaResoconto = function() {
				//alert('Funzione non consentita!');
				let attoOdgId = { 'attoOdgId' : $scope.listaAnnulla[0].id };
				OrdineGiorno.annullaesito(attoOdgId,function(result){
					//$log.debug(result);
					ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
					$('#annullaResocontoAtto').modal('hide');
					$scope.refresh();
					$scope.taskLoading = false;
				});

				/*
    		$scope.taskLoading = true;
    		$log.debug($scope.listaAnnulla);

    		$scope.resoconto = {
    				id:null,
    				esito: null,
    				dataDiscussione:$scope.dataDiscussione,
    				componenti: [],
    				annulla: false,
    				modifica: true,
    				isAttoModificatoInGiunta: false,
    				};


			var selected = [];
			for(var i = 0; i < $scope.listaAnnulla.selected.length; i++){
				selected.push($scope.listaAnnulla.selected[i].id);
			}
			$log.debug("RESOCONTO ANNULLA MASSIVO:",$scope.resoconto);
			$log.debug("RESOCONTO ANNULLA MASSIVO - Atti:", selected);

			var resocontoMassivo = {
					resoconto: $scope.resoconto,
					atti: selected
			};

			OrdineGiorno.cancellaesiti(resocontoMassivo,function(result){
    			$log.debug(result);
    			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$('#annullaResocontoAtto').modal('hide');
        		$scope.refresh();
    		});
    		*/
			}

			/**
			 * Numera atto
			 */
			/* Numerazione singola non consentita
    	$scope.numeraAtto = function(atto) {
    		$log.debug("numero atto.id",atto);
    		$scope.taskLoading = true;

    		var emptylist = [];
    		ArgumentsOdgService.setArguments(emptylist);
    		ArgumentsOdgService.addArgument(atto);


    		OrdineGiorno.numeraAtti(ArgumentsOdgService.getArguments(),function(result){
    			$log.debug(result);
    			// $scope.attos=[];
    			// $scope.attos.allItemsSelected = false;
    			ArgumentsOdgService.setArguments([]);
    			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$scope.refresh();
    		});
    	}
    	*/

			/**
			 * Numera atti massivo
			 */
			$scope.numeraAtti = function(odg) {
				//$log.debug("numera atti",odg.selected);
				$scope.taskLoading = true;

				var attilist = [];
				for (var i = 0; i < odg.selected.length; i++) {
					attilist.push(odg.selected[i].atto);
				}

				attilist = $filter('orderBy')(attilist, 'numeroAdozione', true,function(a,b){
					return (parseInt(a) < parseInt(b)) ? -1 : 1;
				});

				OrdineGiorno.numeraAtti(attilist,function(result){
					//	$log.debug(result);
					ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
					$scope.refresh();
					$scope.taskLoading = false;
				});
			};

			$scope.messaggioButtonAnnullaNumeraAtto = function (odg){
				var attilist = [];
				var allattilist = [];
				var numeriSel = [];
				var numeriAll = [];

				for (var i = 0; i < odg.attos.length; i++) {
					allattilist.push(odg.attos[i].atto);
					if(odg.attos[i].atto.numeroAdozione != null){
						numeriAll.push(parseInt(odg.attos[i].atto.numeroAdozione));
					}

				}

				allattilist = $filter('orderBy')(allattilist, 'numeroAdozione', true,function(a,b){
					return (parseInt(a) < parseInt(b)) ? -1 : 1;
				});

				if(typeof odg.selected != 'undefined'){
					for (var i = 0; i < odg.selected.length; i++) {
						attilist.push(odg.selected[i].atto);
						if(odg.selected[i].atto.numeroAdozione != null){
							numeriSel.push(parseInt(odg.selected[i].atto.numeroAdozione));
						}
					}

					attilist = $filter('orderBy')(attilist, 'numeroAdozione', true,function(a,b){
						return (parseInt(a) < parseInt(b)) ? -1 : 1;
					});

				}

				var check = false;
				var last = 0;

				if(attilist.length > 0){
					for(var i=0; i < allattilist.length; i++){
						if(allattilist[i].numeroAdozione != null){
							last = allattilist[i].numeroAdozione;
							if(parseInt(allattilist[i].numeroAdozione) > parseInt(attilist[0].numeroAdozione)){
								check = true;
								break;
							}

						}
					}

// if(check == false){
//
// var diff = numeriAll.filter(function(item1) {
// for (var i in numeriSel) {
// if (item1 === numeriSel[i]) { return false; }
// };
// return true;
// });
//
// $log.debug("Difference:",diff);
// }

				}


				return check;
			};

			$scope.trovaNumeroAdozione = function(numero, lista){
				var check = false;
				for(var i=0; i < lista.length; i++){
					if(parseInt(lista[i].numeroAdozione) == numero){
						check = true;
						break;
					}
				}

				return check;
			};

			/**
			 * generaDoc di provvedimento
			 */
			/*
         * Il processo attico consente la generazione del documento in una fase successiva
    	 *
    	$scope.generaDocAtto = function(atto) {
    		$log.debug("generaDocAtto atto.id",atto);
    		$scope.taskLoading = true;

    		var emptylist = [];
    		ArgumentsOdgService.setArguments(emptylist);
    		ArgumentsOdgService.addArgument(atto);

    		OrdineGiorno.generaDocProvv(ArgumentsOdgService.getArguments(),function(result){
    			$log.debug(result);
    			// $scope.attos=[];
    			// $scope.attos.allItemsSelected = false;
    			ArgumentsOdgService.setArguments([]);
    			ngToast.create(  { className: 'success', content: 'Generazione effettuata con successo' } );
        		$scope.taskLoading = false;
        		$scope.refresh();
    		});
    	}
    	*/

			/**
			 * generaDoc di provvedimento massivo
			 */
			/*Il processo attico non consente la generazione massiva
    	 *
    	 *
    	$scope.generaDocAtti = function(odg) {
    		$log.debug("numera atti",odg.selected);
    		$scope.taskLoading = true;
    		$scope.generazionemassiva = true;

    		for(var i = 0; i < odg.selected.length; i++){
    			odg.selected[i].loading = true;
    		}

    		$scope.generaDocumentoSingolo(0, odg.selected);
    	};

    	$scope.generaDocumentoSingolo = function(index, atti){
    		if(typeof atti[index] !== 'undefined') {
    		    var atto = atti[index];
    			OrdineGiorno.generaDocProvv([atto.atto],function(result){
        			$log.debug(result);
        			delete atto.loading;
        			atto.atto.stato = SedutaGiuntaConstants.statiAtto.propostaInAttesaDiFirmaSegretario;
        			ngToast.create(  { className: 'success', content: 'Generazione effettuata con successo' } );
            		// $scope.taskLoading = false;
            		// $scope.refresh();
        			var succ = index + 1;
        			if($scope.generazionemassiva == true){
        				$scope.generaDocumentoSingolo(succ, atti);
        			} else {
        				$scope.generazionemassiva = false;
            			$scope.taskLoading = false;
            			$scope.refresh();
        			}

        		});
    		} else{
    			$scope.generazionemassiva = false;
    			$scope.taskLoading = false;
    			$scope.refresh();
    		}
    	};

    	$scope.annullaGeneraDocAtti = function() {
    		$scope.generazionemassiva = false;
    		$scope.taskLoading = false;
    		$scope.refresh();
    	};
    	*/

			/**
			 * Annulla Numerazione atto
			 */
			/*Il processo attico non consente l'annullamento della numerazione
    	$scope.annullaNumerazione = function(atto) {
    		$log.debug("numero atto.id",atto);
    		$scope.taskLoading = true;

    		var emptylist = [];
    		ArgumentsOdgService.setArguments(emptylist);
    		ArgumentsOdgService.addArgument(atto);

    		OrdineGiorno.annullaNumerazione(ArgumentsOdgService.getArguments(),function(result){
    			$log.debug(result);
    			// $scope.attos=[];
    			// $scope.attos.allItemsSelected = false;
    			ArgumentsOdgService.setArguments([]);
    			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$scope.refresh();
    		});
    	}
    	*/

			/**
			 * Annulla Numerazione atto massiva
			 */
			/*Il processo attico non consente l'annullamento della numerazione
    	$scope.annullaNumerazioni = function(odg) {

    		$log.debug("annulla numera atti",odg.selected);
    		$scope.taskLoading = true;

    		var attilist = [];
    		for (var i = 0; i < odg.selected.length; i++) {
    			attilist.push(odg.selected[i].atto);
    		}

    		attilist = $filter('orderBy')(attilist, 'atto.numeroAdozione');
    		attilist = attilist.reverse();
    		$log.debug("orderedList:",attilist);


    		OrdineGiorno.annullaNumerazione(attilist,function(result){
    			$log.debug(result);
    			ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
        		$scope.taskLoading = false;
        		$scope.refresh();
    		}, function(error){
    			$log.debug("Errore:",error);
    			$scope.taskLoading = false;
    		});
    	}
    	*/

			/**
			 * Dismiss dell'annullamento
			 */
			$scope.clearAnnullaResoconto = function() {
				$('#annullaResocontoAtto').modal('hide');
			}

			$scope.moveItem = function (item,index,idOdg) {
				var odg = null;
				var isOdl = $scope.isOdl();
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.odgs[i].id == idOdg && !$scope.isSolaLettura($scope.sedutaGiunta.odgs[i])){
						odg = $scope.sedutaGiunta.odgs[i];
						var attoOdg = {id:null,ordineGiorno:{id:idOdg},atto: item,ordineOdg:0,sezione:0,parte:0,bloccoModifica:0};
						$scope.sedutaGiunta.odgs[i].attos.push(attoOdg);
						if(isOdl){
							riordinaAttiOdl($scope.sedutaGiunta.odgs[i].attos);
						}
						else {
							riordinaAttiOdg($scope.sedutaGiunta.odgs[i].attos);
						}
					}
				}
				if(odg != null && !$scope.isSolaLettura(odg)){
					$scope.atti.splice(index,1);
				}

				// TODO: funzione di ordinamento ATTI !!!!!
				// $scope.ordinaAtti(odg);
				//$log.debug("AttiOdg",$scope.sedutaGiunta.odgs[0].attos);
				$scope.callDecisione($scope.sezioneCorrente,$scope.decisioni[0]);
			};



			/**
			 * Funzione di ordinamento degli atti dell'odg - NON USATA in ATTICO
			 *
			 $scope.ordinaAtti = function(odg) {
    		var attiCloned = angular.copy(odg.attos);
    		//log.debug("Filter:");
    		attiCloned = $filter('orderBy')(attiCloned, 'atto.codiceArea');
    		///$log.debug(attiCloned);

    		var sezioneUno_uno = [];
    		var sezioneUno_due = [];
    		var sezioneDue_uno = [];
    		var sezioneDue_due = [];

    		// Divisione degli atti nelle due sezioni
    		for(var i =0; i < attiCloned.length; i++) {
    			if($scope.stringEmpty(attiCloned[i].atto.tipoProvvedimento)){
    				attiCloned[i].sezione = 1;
    				if(attiCloned[i].atto.usoEsclusivo == 'sanita'){
    					attiCloned[i].parte = 2;
    					sezioneUno_due.push(attiCloned[i]);
    				}
    				else{
    					attiCloned[i].parte = 1;
    					sezioneUno_uno.push(attiCloned[i]);
    				}
    			}
    			else{
    				attiCloned[i].sezione = 2;
    				if(attiCloned[i].atto.usoEsclusivo == 'sanita'){
    					attiCloned[i].parte = 2;
    					sezioneDue_due.push(attiCloned[i]);
    				}
    				else{
    					attiCloned[i].parte = 1;
    					sezioneDue_uno.push(attiCloned[i]);
    				}
    			}
    		}
    		// ---------Ordinamento sezione uno - atti
			// rinviati------------------------------
    		sezioneUno_uno = $scope.ordinaParti(sezioneUno_uno); // atti non
																	// sanitari
    		sezioneUno_due = $scope.ordinaParti(sezioneUno_due); // atti
																	// sanitari

    		// ---------Ordinamento sezione due - atti
			// nuovi---------------------------------
    		sezioneDue_uno = $scope.ordinaParti(sezioneDue_uno); // atti non
																	// sanitari
    		sezioneDue_due = $scope.ordinaParti(sezioneDue_due); // atti
																	// sanitari

    		attiCloned = sezioneUno_uno.concat(sezioneUno_due);
    		attiCloned = attiCloned.concat(sezioneDue_uno);
    		attiCloned = attiCloned.concat(sezioneDue_due);

    		for(var i = 0; i < attiCloned.length; i++) {
    			attiCloned[i].ordineOdg = i;
    		}


    		odg.attos = attiCloned;
    		$log.debug("Atti Ordinati:",odg.attos);

    	}
			 */

			/**
			 * Ordinamento prima per tipologia di atto poi per relatore - NON USATA in ATTICO
			 *
			 $scope.ordinaParti = function(sezione) {
    		var sezione_divisa = [];
    		sezione_divisa['SDL'] = [];
    		sezione_divisa['DDL'] = [];
    		sezione_divisa['COM'] = [];
    		// sezione_divisa['DEL'] = [];
    		// sezione_divisa['DELA'] = [];
    		for(var i = 0; i < sezione.length; i++){
    			for(var j = 0; j < $scope.relatori.length; j++) {
    				if(sezione[i].atto.denominazioneRelatore == $scope.relatori[j]){
    					if(sezione[i].atto.tipoAtto.codice == "DEL"){
    						sezione_divisa["COM"].push(sezione[i]);
    					}
    					else{
    						sezione_divisa[sezione[i].atto.tipoAtto.codice].push(sezione[i]);
    					}
    				}
    			}

    		}


// sezione_divisa['DELA'] = $filter('orderBy')(sezione_divisa['DELA'],
// 'atto.argomentoOdg.id');

// for(var i = 0; i < sezione_divisa['DEL'].length; i++){
// for(var j = 0; j < sezione_divisa['DELA'].length; j++){
// if(sezione_divisa['DEL'][i].atto.codiceArea ==
// sezione_divisa['DELA'][j].atto.codiceArea){
// sezione_divisa['DEL'].splice(i-1, 0, sezione_divisa['DELA'][j]);
// sezione_divisa['DELA'].splice(j, 1);
// }
// }
// }

// sezione_divisa['DELA'] = $filter('orderBy')(sezione_divisa['DELA'],
// 'atto.argomentoOdg.id');

    		sezione = [];
			sezione = sezione.concat(sezione_divisa['SDL']);
			sezione = sezione.concat(sezione_divisa['DDL']);
			sezione = sezione.concat(sezione_divisa['COM']);
// sezione = sezione.concat(sezione_divisa['DEL']);
// sezione = sezione.concat(sezione_divisa['DELA']);

    		return sezione;
    	}
			 */

			$scope.viewImportFromRubrica = function(){
				$scope.rubricaToImport = {};
				$('#rubricaModal').modal('show');
			};

			$scope.importaRubrica = function(item){
				//$log.debug("importaRubrica");
				$scope.aggiungiARubrica(item);
				$('#rubricaModal').modal('hide');
			};

			$scope.aggiungiARubrica = function (importFromRubrica) {
				//$log.debug(importFromRubrica);
				if($scope.sedutaGiunta.rubricaSeduta == null){
					$scope.sedutaGiunta.rubricaSeduta = [];
				}

				importFromRubrica.removable = true;
				$scope.sedutaGiunta.rubricaSeduta.push(importFromRubrica);
			};

			$scope.switchFirma = function (isFirma){
				$scope.firma = isFirma;
			}

			$scope.fileDropperDocumentoFirmato = function (
				files, event, rejectedFiles,
				/* omissis, adozione, dtoWorkflow, parereId, schedaAnagraficoContabile, */
				idTempDownload, signingDocId) {

				//$log.debug("sono in fileDropperDocumentoFirmato event.type: ", event.type);
				if(event.type !== "click"){
					if(files!=undefined && files[0]!=undefined){
						$scope.dtoWorkflow.campi['firmato'] = true;
						var restUrl = 'api/ordineGiornos/'+$scope.odgSelected.id +'/firmato/allegato?idProfilo='+$rootScope.profiloattivo.id+'&signingDocId='+signingDocId;

						var fileTmp = {};
						fileTmp.type = files[0].type;
						fileTmp.name = files[0].name;
						$("#" + idTempDownload).html("<a style=\"cursor:default;\" class=\"list-group-item ng-binding\" href=\"#\" onclick=\"return false;\"><span class=\"fa fa-upload\"\"> "+fileTmp.name+" (Atteso click su conferma per il caricamento)</span></a>");
						$scope.documentiFirmatiDaCaricare.set(restUrl, files[0]);
					}else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
						alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");
					}
				}
			};

			$scope.fileDropperDocumentoResocontoFirmato = function (files, event, rejectedFiles, idTempDownload,signingDocId) {
				//$log.debug("sono in fileDropperDocumentoFirmato event.type: ", event.type);
				if(event.type !== "click"){
					if(files!=undefined && files[0]!=undefined){
						$scope.dtoWorkflow.campi['firmato'] = true;
						var restUrl = 'api/ordineGiornos/'+$scope.odgSelected.id +'/firmato/allegato?idProfilo='+$rootScope.profiloattivo.id+'&signingDocId='+signingDocId + '&idResoconto=' + $scope.resocontoSelected.id;
						var fileTmp = {};
						fileTmp.type = files[0].type;
						fileTmp.name = files[0].name;
						$("#" + idTempDownload).html("<a style=\"cursor:default;\" class=\"list-group-item ng-binding\" href=\"#\" onclick=\"return false;\"><span class=\"fa fa-upload\"\"> "+fileTmp.name+" (Atteso click su conferma per il caricamento)</span></a>");
						$scope.documentiFirmatiDaCaricare.set(restUrl, files[0]);
					}else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
						alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");
					}
				}
			};

			$scope.uploadDocumenti = function(mappaDocumenti, tipoDocumenti){
				var arrayOfFunction = [];
				if(mappaDocumenti.size == 0){
					var deferred = $q.defer();
					deferred.resolve();
					arrayOfFunction.push(deferred.promise);
				}else{
					mappaDocumenti.forEach(function (file, restUrl) {
						arrayOfFunction.push($scope.doUploadDoc(mappaDocumenti, restUrl, tipoDocumenti));
					});
				}
				return $q.all(arrayOfFunction);
			}
			$scope.doUploadDoc = function(mappaDocumenti, restUrl, tipoDocumenti){
				var deferred = $q.defer();
				$scope.uploadok = true;
				var file;
				if(tipoDocumenti==undefined || tipoDocumenti != "documenti_trasparenza"){
					file = mappaDocumenti.get(restUrl);
				}else{
					file = mappaDocumenti.get(restUrl).file;
				}
				//$log.debug('Uploading file', file);
				if(file.size && file.size > $rootScope.ngfMaxSize){
					var rejectedFiles = [$scope.registerAccount.moduloregistrazione];
					$rootScope.$broadcast('rejectedFilesEvent', [], {}, rejectedFiles, 'application/*');
					return;
				}

				Upload.upload({
					url: restUrl,
					headers : {
						'Authorization': 'Bearer '+ $rootScope.accessToken
					},
// fields: {'idProfilo': $rootScope.profiloattivo.id},
					file: file
				}).progress(function (evt) {
					$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
				}).success(function (data, status, headers, config) {
					if(tipoDocumenti != undefined && tipoDocumenti == "documenti_trasparenza"){
						$scope.valoriSchedeDati.elementiSchede[mappaDocumenti.get(restUrl).schedaId][mappaDocumenti.get(restUrl).elementoIndex][mappaDocumenti.get(restUrl).schemaDatoId] = data;
					}
					$scope.uploadok = false;
					deferred.resolve();
				});
				return deferred.promise;
			}

			$scope.firmare = function(sezione) {
				/*
    		 * firma non prevista in ATTICO
    		 *
    		if(sezione.target == 'datiOdgBase') { // odg base
    			if($scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgBase ||
    			   $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdlBase ||
    			   $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione){
    				return true;
    			}
    			else{
    				return false;
    			}
    		}
    		else if(sezione.target == 'datiOdgSuppletivo') {
    			for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
    				if($scope.sedutaGiunta.odgs[i].tipoOdg.id == 3 &&
    					( $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
    					  $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInAttesaDiFirma ) ) {
    					return true;
    				}
    			}

    			return false;
    		}
    		else if(sezione.target == 'datiOdgFuoriSacco') {
    			for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
    				if($scope.sedutaGiunta.odgs[i].tipoOdg.id == 4 &&
    					( $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma ||
    					  $scope.sedutaGiunta.odgs[i].oggetto == SedutaGiuntaConstants.statiOdg.odlInAttesaDiFirma ) ) {
    					return true;
    				}
    			}

    			return false;
    		}
    		else if(sezione.target == 'verbale') {
    			return $scope.isAbilitatoFirmaVerbale();
    		}
    		else if(sezione.target == 'resoconto') {
    			return $scope.isResocontoDaFirmare();
    		}
    		else{
    			return false;
    		}
			*/
				return false;
			}


			/**
			 * Sezione funzioni per il Verbale
			 */

			/**
			 * Set the contenteditable attribute utilizzato per i SummerNote.
			 */
			$scope.setContentVerbaleEditable = function () {

				if($scope.solaLetturaVerbale){
					$(".note-editable").attr("contenteditable","false");
					$(".note-toolbar").attr("style","display: none;");
				} else {
					$(".note-editable").attr("contenteditable","true");
				}
			};

			/**
			 * Modelli Campo
			 */
			$scope.cercaModelloCampo = function (tipoCampo){
				//$log.debug("cercaModelloCampo:tipoCampo:", tipoCampo);
				$scope.pageModelloCampo = 1;
				$scope.modelloCampos =[];
				delete $scope.modelloCampoLinks;
				delete $scope.totalModelloCampos;

				$scope.tipoModelloCampo = {};
				$scope.tipoModelloCampo = tipoCampo;
				$scope.confirmCercaModelloCampo($scope.tipoModelloCampo, 1);
				$('#cercaModelloCampoConfirmation').modal('show');

			};

			$scope.selezionaModelloCampo = function(modelloCampo) {
				if(modelloCampo.tipoCampo ==='verbale_narrativa'){
					$scope.sedutaGiunta.verbale.narrativa.testo = modelloCampo.testo;
				} else if(modelloCampo.tipoCampo ==='verbale_note'){
					$scope.sedutaGiunta.verbale.noteFinali.testo = modelloCampo.testo;
				} else if(modelloCampo.tipoCampo ==='preambolo_odl' || modelloCampo.tipoCampo ==='preambolo_odg'){
					if(!$scope.odgSelected.preambolo){
						$scope.odgSelected.preambolo = {};
					}
					$scope.odgSelected.preambolo.testo = modelloCampo.testo;
				}
				$('#cercaModelloCampoConfirmation').modal('hide');
			};

			$scope.confirmCercaModelloCampo = function(tipoModelloCampo, page) {
				//$log.debug("tipoModelloCampo: " + tipoModelloCampo);
				if(angular.isUndefined($scope.modelloCampoCriteria)){
					$scope.modelloCampoCriteria = {};
				}
				$scope.modelloCampoCriteria.tipoCampo = tipoModelloCampo;
				if(page){
					$scope.modelloCampoCriteria.page = page;
				}else if(angular.isUndefined($scope.modelloCampoCriteria.page)){
					$scope.modelloCampoCriteria.page = 1;
				}
				$scope.modelloCampoCriteria.per_page = 5,
					ModelloCampo.getAllMixed($scope.modelloCampoCriteria,
						function (result, headers) {
							$scope.modelloCampoLinks = ParseLinks.parse(headers('link'));
							$scope.totalModelloCampos=headers('x-total-count') ;
							$scope.modelloCampos = result;
						});
			};

			$scope.loadPageModelloCampo = function(page, modello) {
				$scope.pageModelloCampo = page;
				$scope.confirmCercaModelloCampo(modello, page);
			};

			$scope.salvaModelloCampo = function (tipoCampo) {
				//$log.debug("salvaModelloCampo");
				$scope.modelloCampo={};
				$scope.modelloCampo.tipoCampo=tipoCampo;

				if(tipoCampo ==='verbale_narrativa'){
					$scope.modelloCampo.testo = $scope.sedutaGiunta.verbale.narrativa.testo;
				} else if(tipoCampo ==='verbale_note'){
					$scope.modelloCampo.testo = $scope.sedutaGiunta.verbale.noteFinali.testo;
				}else if(tipoCampo ==='preambolo_odl' || tipoCampo ==='preambolo_odg'){
					$scope.modelloCampo.testo = $scope.odgSelected.preambolo.testo;
				}

				$('#salvaModelloCampoConfirmation').modal('show');

			};

			$scope.confirmSalvaModelloCampo = function (modelloCampo) {
				ModelloCampo.save(modelloCampo,
					function (data) {
						//$log.debug(data);
						$('#salvaModelloCampoConfirmation').modal('hide');
					});
			};

			$scope.pulisciTesto = function (tipoCampo) {
				//$log.debug("pulisciTesto :: tipoCampo:" + tipoCampo);
				if(tipoCampo ==='verbale_narrativa'){
					$scope.sedutaGiunta.verbale.narrativa.testo = "";
				} else if(tipoCampo ==='verbale_note'){
					$scope.sedutaGiunta.verbale.noteFinali.testo = "";
				} else if(tipoCampo ==='preambolo_odl' || tipoCampo ==='preambolo_odg'){
					$scope.odgSelected.preambolo.testo = "";
				}
			};

			/**
			 * Genera le estensioni accettate in modo leggibile.
			 */
			$scope.generaEstensioniAccettateLeggibili = function (){
				var estensioniAccettateLeggibile = "";
				$scope.estensioniAccettate.forEach(function(item) {
					if(estensioniAccettateLeggibile){
						estensioniAccettateLeggibile += ", ";
					}
					estensioniAccettateLeggibile += item;
				});
				return estensioniAccettateLeggibile;
			};

			$scope.fileImportDropped = function (files,event,rejectedFiles,importing, controllaEstensione) {
				//$log.debug(event);

				if(event.type !== "click"){
					$scope.importingSection[importing] = true;
					var eseguiOperazione = true;
					if(controllaEstensione != undefined && controllaEstensione){
						var estensione = files[0].name.split(".")[files[0].name.split(".").length - 1];
						if($scope.estensioniAccettate.indexOf(estensione) == -1){
							eseguiOperazione = false;
							var estensioniAccettateLeggibile = $scope.generaEstensioniAccettateLeggibili();
							$scope.importingSection[importing] = false;
							alert("Estensione del file non valida. Il sistema accetta solo file con le seguenti estensioni: " + estensioniAccettateLeggibile)
						}
					}
					if(eseguiOperazione){
						if(rejectedFiles.length > 0){
							$rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, $scope.estensioniAccettate);
							return;
						}
						Upload.upload({
							url: ConvertService.extract,
							headers : {
								'Authorization': 'Bearer '+ $rootScope.accessToken
							},
							file: files[0]
						}).progress(function (evt) {

						}).success(function (data, status, headers, config) {
							if(importing ==='verbale_narrativa'){
								$scope.sedutaGiunta.verbale.narrativa.testo = data.body;
							} else if(importing ==='verbale_note'){
								$scope.sedutaGiunta.verbale.noteFinali.testo = data.body;
							} else if(importing ==='preambolo_odl' || importing ==='preambolo_odg'){
								$scope.odgSelected.preambolo.testo = data.body;
							}

							$scope.modelloCampo.tipoCampo=importing;
							$scope.modelloCampo.testo = data.body;
							$scope.importingSection[importing] = false;
							//$log.debug("import ok");
						}).error(function (error) {
							$scope.importingSection[importing] = false;
							//$log.debug("import ko");
						});
					}
				}
			};

			/**
			 * Restituisce true se ciascuno degli allegati al verbale presenti ha i
			 * campi obbligatori compilati....
			 */
			$scope.isListaAllegatiVerbaleValida = function(){
				var retValue = true;

				if ($scope.sedutaGiunta.verbale.allegati && $scope.sedutaGiunta.verbale.allegati.length && $scope.sedutaGiunta.verbale.allegati.length > 0){
					for(var i = 0; i<$scope.sedutaGiunta.verbale.allegati.length; i++){
						if($scope.sedutaGiunta.verbale.allegati[i].parteIntegrante == null ||
							!$scope.sedutaGiunta.verbale.allegati[i].titolo ||
							$scope.sedutaGiunta.verbale.allegati[i].titolo.length == 0){
							retValue = false;
							break;
						}
					}
				}

				return retValue;
			}

			/**
			 * Restituisce true se le parti obbligatorie del verbale sono compilate.
			 */
			$scope.isVerbaleValido = function(){
				var retValue = $scope.sedutaGiunta.verbale &&
					($scope.sedutaGiunta.verbale.narrativa && $scope.sedutaGiunta.verbale.narrativa.testo && $scope.sedutaGiunta.verbale.narrativa.testo.length > 0) &&
					$scope.isListaAllegatiVerbaleValida();

				if (retValue && $scope.sedutaGiunta.verbale.sottoscrittori && $scope.sedutaGiunta.verbale.sottoscrittori.length && $scope.sedutaGiunta.verbale.sottoscrittori.length > 0){
					var ssg = null;
					for(var i = 0; i < $scope.sedutaGiunta.verbale.sottoscrittori.length; i++){
						ssg = $scope.sedutaGiunta.verbale.sottoscrittori[i];
						if (ssg.profilo == null || ssg.qualificaProfessionale == null){
							retValue = false;
							break;
						}
					}
				}

				return retValue;
			}

			/**
			 * Restituisce TRUE se la seduta è conclusa e tutti gli atti dei vari
			 * odg riportano un esito, ovvero non sono in stato proposta in attesa
			 * di esito.
			 *
			 * IN ATTICO NON NECESSARIO
			 *
			 $scope.isTuttiProvvedimentiFirmatiOrEsitiRitiratiNonTrattati = function() {
    		var retValue = false;

    		var atto = null;
    		var attoOdg = null;

    		if(angular.isDefined($scope.sedutaGiunta.odgs)){
    			for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					for(var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++) {
						retValue = true;

						attoOdg = $scope.sedutaGiunta.odgs[i].attos[j];
						atto = $scope.sedutaGiunta.odgs[i].attos[j].atto;

        				if ($scope.stringEmpty(attoOdg.esito)) {
	        				return false;
        				}

        				// VECCHIA VERSIONE CIFRA
						// if( atto.stato != SedutaGiuntaConstants.statiAtto.provvedimentoEsecutivo &&
						//		atto.stato != SedutaGiuntaConstants.statiAtto.provvedimentoPresoDatto &&
						//		atto.stato != SedutaGiuntaConstants.statiAtto.provvedimentoVerbalizzato){

							// Se l'atto in esame non è arrivato allo stato
							// del provvedimento firmato verifico se
							// è stato registrato un esito e questi coincide
							// con RITIRATO, NON TRATTATO o RINVIATO
						//	if (attoOdg.esito == null || (attoOdg.esito != null && attoOdg.esito == '')){
						//		retValue = false;
						//		break;
						//	} else if (attoOdg.esito != 'ritirato' &&
						//			attoOdg.esito != SedutaGiuntaConstants.esitoAttoOdg.nonTrattato &&
						//			attoOdg.esito != 'rinviato'){
						//		retValue = false;
						//		break;
						//	}
						// }
					}
				}
    		}


    		return retValue;
    	}
			 */

			/**
			 * Restituisce TRUE se la seduta è conclusa e sono stati generati i
			 * documenti di resoconto integrale e parziale.
			 */
			// Non previsto in ATTICO
			/*
		$scope.isSedutaConclusaConResoconto = function(){
    		var retValue = false;

    		if ($scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusa &&
    				$scope.sedutaGiunta.resoconto != null &&
    				$scope.sedutaGiunta.resoconto.length > 0 ) {
    			retValue = true;
    		}

    		$log.debug("isSedutaConclusaConResoconto: ", retValue);

    		return retValue;
    	}
    	*/

			/**
			 * Restituisce true se la seduta è in condizioni per generare il verbale
			 * e l'utente loggato è il segretario della giunta.
			 */
			$scope.isAbilitatoRedazioneVerbale = function(){
				var retValue = false;

				// Non previsto in ATTICO
				// $scope.isSedutaConclusaConResoconto() &&

				// retValue = $scope.isTuttiProvvedimentiFirmatiOrEsitiRitiratiNonTrattati() &&
				//		   ($scope.sedutaGiunta.verbale &&
				//				   ($scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione) &&
				//				   ($rootScope.profiloattivo) &&
				//				   (ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_GIUNTA']) || ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_CONSIGLIO']))
				//			);

				if ($scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusaNumerazioneConfermata.label &&
					$rootScope.profiloattivo && (ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_GIUNTA']) || ProfiloAccount.isInAnyRole(['ROLE_OPERATORE_SEGRETERIA_CONSIGLIO']))) {

					if ($scope.sedutaGiunta.organo == 'C') {
						if ($scope.existsDocVerbali && $scope.existsDocDefEsito) {
							return true;
						}
					}
					else if ($scope.existsDocDefEsito) {
						return true;
					}
				}

				//$log.debug("isAbilitatoRedazioneVerbale: ", retValue);

				return retValue;
			};

			/**
			 La direzione assume valore 'up' se la seduta sta andando avanti (avanzamento di stato)
			 La direzione assume valore 'down' se la seduta sta andando indietro (regressione di stato)
			 */
			$scope.aggiornaStatoSeduta = function(codiceStatoSeduta, direzione, notConfirm){
				let postFunct = null;
				let message = "";
				if(codiceStatoSeduta == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneOrdineDiscussione.codice){
					if(direzione == 'up'){
						if($scope.sedutaGiunta.odgs.map((odg) => odg.attos).flat().filter((attoOdg) => attoOdg.esito == SedutaGiuntaConstants.esitoAttoOdg.nonTrattato && !attoOdg.dataConfermaEsito).length){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Prima di procedere occorre terminare la gestione degli atti non trattati, posticipandoli ad una futura seduta o indicandoli nuovamente come atti trattati nella seduta corrente.'});
							$scope.taskLoading = false;
							return;
						}
						message = 'Verificare la correttezza degli elenchi degli atti trattati\\non trattati poich\u00E8, dopo la conferma dell\u0027operazione, non sar\u00E0 pi\u00F9 possibile modificare tali elenchi e la seduta passer\u00E0 alla fase di definizione dell\u0027ordine di discussione.';
					}else if(direzione == 'down'){
						message = 'Dopo la conferma dell\u0027operazione la seduta passer\u00E0 nuovamente alla fase di attribuzione dell\u0027ordine di discussione.<br/>';
						if($scope.sedutaGiunta.organo == 'C'){
							message += 'Attenzione&#33; Le eventuali attribuzioni di numeri di argomento al momento inserite saranno annullate.';
							postFunct = function(){
								$scope.openTabOdgBaseOrdinario('tab-0')
							};
						}else if($scope.sedutaGiunta.organo == 'G'){
							message += 'Gli eventuali esiti al momento inseriti non saranno persi.';
						}
					}
				}else if(codiceStatoSeduta == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneNumeroArgomento.codice){
					message = 'Verificare la correttezza dell\u0027ordine di discussione poich\u00E8, dopo la conferma dell\u0027operazione, la seduta passer\u00E0 alla fase di attribuzione dei numeri di argomento.';
				}else if(codiceStatoSeduta == SedutaGiuntaConstants.statiSeduta.sedutaConclusaRegistrazioneEsiti.codice){
					if($scope.sedutaGiunta.organo == 'G'){
						message = 'Verificare la correttezza dell\u0027ordine di discussione poich\u00E8, dopo la conferma dell\u0027operazione, la seduta passer\u00E0 alla fase di registrazione degli esiti.';
					}else if($scope.sedutaGiunta.organo == 'C'){
						message = 'Verificare la correttezza dell\u0027attribuzione dei numeri di argomento poich\u00E8, dopo la conferma dell\u0027operazione, non sar\u00E0 pi\u00F9 possibile modificarli e la seduta passer\u00E0 alla fase di registrazione degli esiti.';
					}
				}

				if(message && !notConfirm){
					$rootScope.showMessage({title:'Attenzione', siButton:true, noButton:true,
						siFunction:function(){
							$scope.taskLoading = true;
							SedutaGiunta.aggiornaStatoSeduta({'id': $scope.sedutaGiunta.id, 'stato': codiceStatoSeduta, 'direzione' : direzione}, $scope.sedutaGiunta, function(res){
								$scope.refresh();
								ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
								$('#genericMessage').modal('hide');
								if(postFunct){
									postFunct();
								}
								$scope.taskLoading = false;
							}, function(err){
								$('#genericMessage').modal('hide');
								$scope.refresh();
								$scope.taskLoading = false;
							});
						},
						noFunction:function(){
							$scope.taskLoading = false;
							$('#genericMessage').modal('hide');
						},
						body: message + "<br/> Procedere?"
					});
				}else{
					$scope.taskLoading = true;
					SedutaGiunta.aggiornaStatoSeduta({'id': $scope.sedutaGiunta.id, 'stato': codiceStatoSeduta, 'direzione' : direzione}, {}, function(res){
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$scope.refresh();
						$scope.taskLoading = false;
					}, function(err){
						$scope.refresh();
						$scope.taskLoading = false;
					});
				}
			};

			/**
			 * Restituisce true se la seduta è in condizioni per generare il documento resoconto
			 * e l'utente loggato è il segretario della giunta.
			 */
			$scope.isAbilitatoSezioneResoconto = function(){
				var retValue = $scope.sedutaGiunta.fase == SedutaGiuntaConstants.fasiSeduta.CONCLUSA &&
					$scope.sedutaGiunta.stato != SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneAttiTrattati.label;

//    		retValue = ($scope.abilitaPulsantiResoconto() && $scope.isOperatoreResoconto == true && ($scope.sedutaGiunta.resoconto == null || $scope.sedutaGiunta.resoconto.length < 1)) ||
//    					$scope.abilitaPulsanteFirmaResocontoControlloSezione() || $scope.abilitaPulsanteFirmaPresenzeControlloSezione() ||
//    					$scope.resocontoFirmato() == true ||
//    					($scope.isOperatoreResoconto == true && $scope.sedutaGiunta.resoconto != null && $scope.sedutaGiunta.resoconto.length > 0 && $scope.sedutaGiunta.resoconto.length < 3 && $scope.abilitaPulsantePresenzeAssenze() == false);

				//	$log.debug("isAbilitatoRedazioneResoconto: ", retValue);

				return retValue;
			};

			$scope.resocontoFirmato = function(){
				var check = false;
				$scope.sedutaGiunta.resoconto.forEach(function(res){
					if(res.stato === SedutaGiuntaConstants.statiResoconto.resocontoConsolidato){
						check = true;
					}
				});

				return check;
			};

			/*
    	 * In ATTICO NON UTILIZZATA
    	 *
    	$scope.isAbilitatoScritturaSezioneResoconto = function(){
    		$log.debug("******isAbilitatoScritturaSezioneResoconto*******");
    		$log.debug($scope.abilitaPulsantiResoconto());
    		$log.debug($scope.isOperatoreResoconto);
    		$log.debug($scope.sedutaGiunta.resoconto);

    		return ($scope.abilitaPulsantiResoconto() && $scope.isOperatoreResoconto == true && ($scope.sedutaGiunta.resoconto == null || $scope.sedutaGiunta.resoconto.length < 1));
    	};
    	*/

			/**
			 * Restituisce true se la seduta è in condizioni per accedere il verbale
			 * e l'utente loggato è il segretario della giunta.
			 */
			$scope.isAbilitatoLetturaVerbale = function(){
				if ($scope.sedutaGiunta.organo == 'C') {
					if ($scope.sedutaGiunta.resoconto.length > 1) {
						return true;
					}
				}
				else if ($scope.sedutaGiunta.resoconto.length > 0) {
					return true;
				}

				return  false;

				// Vecchia Versione
				//
				// return ($scope.sedutaGiunta.verbale &&
				//		($scope.sonoSottoscrittoreVerbale() || ($rootScope.profiloattivo && $scope.sedutaGiunta.segretario &&
				//												$scope.sedutaGiunta.segretario.id == $rootScope.profiloattivo.id)) &&
				//		($scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleInAttesaFirma ||
				//				$scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleConsolidato ||
				//				$scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleRifiutato));
			};

			/**
			 * Restituisce true se il verbale è in uno stato tale da poter generare
			 * il documento del Verbale.
			 */
			$scope.isAbilitatoGeneraDocVerbale = function(){
				return ($scope.isAbilitatoRedazioneVerbale() && !$scope.solaLetturaVerbale);
			}

			/**
			 * Restituisce true se l'utente è abilitato alla visualizzazione dei
			 * documenti del Verbale.
			 */
			$scope.isAbilitatoVisDocsVerbale = function(){
				var retValue = (($scope.sedutaGiunta.verbale && $scope.sedutaGiunta.verbale.documentiPdf &&
					$scope.sedutaGiunta.verbale.documentiPdf.length > 0) &&
					($scope.isAbilitatoRedazioneVerbale() || $scope.isAbilitatoLetturaVerbale()));

				return retValue;
			}

			/**
			 * Restituisce true se l'utente loggato risulta essere nella lista dei
			 * sottoscrittori del verbale della seguta in esame.
			 */
			$scope.sonoSottoscrittoreVerbale = function(){
				var found = false;

				if ($scope.sedutaGiunta.verbale && $scope.sedutaGiunta.verbale.sottoscrittori){
					for(var i = 0; i<$scope.sedutaGiunta.verbale.sottoscrittori.length; i++){
						if ($rootScope.profiloattivo && $rootScope.profiloattivo.id == $scope.sedutaGiunta.verbale.sottoscrittori[i].profilo.id) {
							found = true;
							break;
						}
					}
				}

				return found;
			};

			$scope.verificaSezioneResoconto = function(){
				//$log.debug("********verificaSezioneResoconto**********");

				var ruoliSer = "";
				if($scope.sedutaGiunta.organo == 'G') {
					ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA + ',' + RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA;
				}
				else {
					ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO + ',' + RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO;
				}

				Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
					$scope.profilosResoconto = result;
					if ($scope.sedutaGiunta.sottoscrittoriresoconto){
						for (var int = 0; int < $scope.sedutaGiunta.sottoscrittoriresoconto.length; int++) {
							for (var j = 0; j < $scope.profilosResoconto.length; j++){
								if( $scope.sedutaGiunta.sottoscrittoriresoconto[int].profilo.id == $scope.profilosResoconto[j].id){
									$scope.sedutaGiunta.sottoscrittoriresoconto[int].profilo = $scope.profilosResoconto[j];
								}
							}
						}
					}

				});
				if ($scope.isAbilitatoSezioneResoconto()){
					$scope.abilitaSezioneOdg('resoconto');
				} else {
					$scope.disabilitaSezioneOdg('resoconto');
				}
			};

			$scope.verificaSezioneVerbale = function(){
				if($scope.sedutaGiunta.verbale != null) {
					var ruoliSer = "";
					if($scope.sedutaGiunta.organo == 'G') {
						ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_GIUNTA + ',' + RoleCodes.ROLE_PRESIDENTE_SEDUTA_GIUNTA;
					}
					else {
						ruoliSer = RoleCodes.ROLE_SEGRETARIO_SEDUTA_CONSIGLIO + ',' + RoleCodes.ROLE_PRESIDENTE_SEDUTA_CONSIGLIO;
					}

					Profilo.query({stato:'0',ruoli: ruoliSer}, function(result){
						$scope.profilosVerbali = result;
						if ($scope.sedutaGiunta.verbale.sottoscrittori){
							for (var int = 0; int < $scope.sedutaGiunta.verbale.sottoscrittori.length; int++) {
								for (var j = 0; j < $scope.profilosVerbali.length; j++){
									if( $scope.sedutaGiunta.verbale.sottoscrittori[int].profilo.id == $scope.profilosVerbali[j].id){
										$scope.sedutaGiunta.verbale.sottoscrittori[int].profilo = $scope.profilosVerbali[j];
									}
								}
							}
						}

					});
				}

				if ($scope.isAbilitatoRedazioneVerbale()){
					$scope.abilitaSezioneOdg('verbale');
					$scope.solaLetturaVerbale = false;

					/*
    			 * FIRMA NON PREVISTA IN ATTICO
    			 *
    			Verbale.getNextSottoscrittore({id:$scope.sedutaGiunta.verbale.id},function(result){
        			$scope.idProfiloSottoscrittoreVerbale = result.value;
        		},function(error) {
        			$scope.idProfiloSottoscrittoreVerbale = null;
        		});
        		*/
				}
				else if ($scope.isAbilitatoLetturaVerbale()){
					$scope.abilitaSezioneOdg('verbale');
					$scope.solaLetturaVerbale = true;

					/*
    			 * FIRMA NON PREVISTA IN ATTICO
    			 *
    			Verbale.getNextSottoscrittore({id:$scope.sedutaGiunta.verbale.id},function(result){
        			$scope.idProfiloSottoscrittoreVerbale = result.value;
        		},function(error) {
        			$scope.idProfiloSottoscrittoreVerbale = null;
        		});
        		*/
				}
				else {
					$scope.disabilitaSezioneOdg('verbale');
					$scope.idProfiloSottoscrittoreVerbale = null;
				}
			};

			/**
			 * Verifica se la funzionalità di firma del verbale è abilitata.
			 */
// $scope.isFirmaVerbaleAbilitata = function(){
// var retValue = ($scope.sedutaGiunta.verbale.stato ==
// SedutaGiuntaConstants.statiVerbale.verbaleInAttesaFirma) &&
// $scope.sonoSottoscrittoreVerbale();
//
// return retValue;
// };

			/**
			 * verifica se siamo nello stato in attesa di firma verbale
			 */
			$scope.isAbilitatoFirmaVerbale = function (){
				var show = false;

				/*
 	       	 * FIRMA NON PREVISTA
 	       	 *
 	       	if(angular.isDefined($scope.sedutaGiunta.verbale) && $scope.sedutaGiunta.verbale != null && $scope.sedutaGiunta.verbale.id != null &&
 	       			$scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusa &&
 	       			$scope.sedutaGiunta.verbale.stato == SedutaGiuntaConstants.statiVerbale.verbaleInAttesaFirma ){

 	       		var idProfilo = angular.isDefined($rootScope.profiloattivo) ? $rootScope.profiloattivo.id : 0;
 		       	if(idProfilo == $scope.idProfiloSottoscrittoreVerbale ){
 		       		show = true;
 		       	}
 	       	}
 	       	*/

				return show;
			};

			$scope.isResocontoDaFirmare = function (){
				var show = false;

				$scope.sedutaGiunta.resoconto.forEach(function(res){
					if(res.stato === SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma || res.stato === SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma){
						show = true;
					}
				});

				return show;
			};

			/**
			 * Upload del file del documento di Verbale Firmato
			 */
			$scope.fileDropperVerbaleFirmato = function (files, event, rejectedFiles, idTempDownload) {
				//$log.debug("sono in fileDropperVerbaleFirmato event.type: ", event.type);
				if(event.type !== "click"){
					if(files!=undefined && files[0]!=undefined){
						$scope.dtoWorkflow.campi['firmato'] = true;
						var restUrl = 'api/verbales/'+$scope.sedutaGiunta.verbale.id +'/firmato/allegato?idProfilo='+$rootScope.profiloattivo.id;
						var fileTmp = {};
						fileTmp.type = files[0].type;
						fileTmp.name = files[0].name;
						$("#" + idTempDownload).html("<a style=\"cursor:default;\" class=\"list-group-item ng-binding\" href=\"#\" onclick=\"return false;\"><span class=\"fa fa-upload\"\"> "+fileTmp.name+" (Atteso click su conferma per il caricamento)</span></a>");
						$scope.documentiFirmatiDaCaricare.set(restUrl, files[0]);
					}else if(rejectedFiles!=undefined && rejectedFiles[0]!=undefined){
						alert("La dimensione del file risulta superiore alla massima consentita di " + ($rootScope.ngfMaxSize/1024/1024) + " MB");
					}
				}
			};

			/**
			 * Init the tooltip.
			 */
			$scope.initTooltip = function () {
				$('[data-toggle="tooltip"]').tooltip('destroy').tooltip();
			};

			$scope.callDecisioneVerbale  = function (indexSezione,decisione) {

				if("modifica-verbale" === decisione.codice){
					$scope.taskLoading = true;
					$scope.salvaVerbale();
				}
				else if("rinuncia-verbale" === decisione.codice){
					$scope.taskLoading = true;
					$scope.sedutaGiunta.verbale.stato = SedutaGiuntaConstants.statiVerbale.verbaleRifiutato;
					$scope.salvaVerbale();
				} else {
					var valid = true;

					$scope.dtoFdr = {codiceFiscale:'', password:'' , otp: '',
						filesId: [], filesOmissis: [], filesAdozione: [],
						filesParereId: [], filesScheda: [], filesAttoInesistente: [], filesRelataPubblicazione: []};

					switch(decisione.codice) {
						case 'seduta-generadocverbale':
							if($scope.sedutaGiunta.organo == 'G') {
								$scope.modelloDaCodice('verbalegiunta');
							}
							else {
								$scope.modelloDaCodice('verbaleconsiglio');
							}

							break;
						default:
							break;
					}

					if (valid){
						if(decisione.mostraMaschera ){
							if("seduta-firmadocverbale" === decisione.codice){
								$scope.firma = true;
							}

							$scope.decisioneCorrente = decisione;
							//$log.debug("Decisione Corrente:",$scope.decisioneCorrente);
							$scope.documentiFirmatiDaCaricare = new Map();
							$('#mascheraWorkflow').modal('show');
						}else{
							$scope.salvaDecisione(indexSezione,decisione);
						}
					}
				}
			};

			/**
			 * Salvataggio Verbale
			 */
			$scope.salvaVerbale = function () {
				if ($scope.sedutaGiunta.verbale.id != null) {
					//$log.debug("Verbale PreSave:",$scope.sedutaGiunta.verbale);
					Verbale.update($scope.sedutaGiunta.verbale,
						function () {
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$scope.refresh();
							$scope.taskLoading = false;
						});
				} else {
					$scope.sedutaGiunta.verbale.stato = SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione;
					Verbale.save($scope.sedutaGiunta.verbale,
						function (result, headers) {
							var idSeduta = headers('idSeduta') ;
							$scope.sedutaGiunta.id = idSeduta;
							//$log.debug(  "idSeduta:"+idSeduta);
							ngToast.create(  { className: 'success', content: 'Creazione effettuata con successo' } );
							$state.go('sedutaGiuntaDetail', { id:idSeduta });
							$scope.taskLoading = false;
						});
				}
			};

			/**
			 * genera documento verbale
			 */
			$scope.generaDocumentoVerbale = function(){
				$scope.uploadok = true;
				if ($scope.sedutaGiunta.verbale.id != null) {
					//$log.debug("Verbale PreSave:",$scope.sedutaGiunta.verbale);
					Verbale.update($scope.sedutaGiunta.verbale,
						function () {
							if($scope.sedutaGiunta.verbale != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
								$scope.param=[];
								$scope.param['verbaleId']=$scope.sedutaGiunta.verbale.id;
								$scope.param['modelloId']=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
								Verbale.generaDocPerFirma($scope.param, function (result, headers) {
									ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
									$scope.uploadok = false;
									$('#mascheraWorkflow').modal('hide');
									$scope.refresh();
								});
							}
						});
				} else {
					$scope.sedutaGiunta.verbale.stato = SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione;
					Verbale.save($scope.sedutaGiunta.verbale,
						function (result, headers) {
							if($scope.sedutaGiunta.verbale != null && $scope.dtoWorkflow.campi['modelloHtmlId'] != null && $scope.dtoWorkflow.campi['modelloHtmlId'].id>=0){
								$scope.param=[];
								$scope.param['verbaleId']=$scope.sedutaGiunta.verbale.id;
								$scope.param['modelloId']=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
								Verbale.generaDocPerFirma($scope.param, function (result, headers) {
									var idSeduta = headers('idSeduta') ;
									$scope.sedutaGiunta.id = idSeduta;
									ngToast.create(  { className: 'success', content: 'Creazione effettuata con successo' } );
									$scope.uploadok = false;
									$('#mascheraWorkflow').modal('hide');
									$state.go('sedutaGiuntaDetail', { id:idSeduta });
								});
							}
						});
				}
			};


			/**
			 * firma documento verbale
			 */
			$scope.firmaDocumentoVerbale = function(){
				if($scope.firma) {
					//$log.debug("Firma documento verbale - firma remota :start");

					//$log.debug('id del profilo ' + $rootScope.profiloattivo.id);
					//$log.debug('cod fisc del profilo ' + $rootScope.profiloattivo.utente.codicefiscale);
					//$log.debug('tipoDocumento ' + $scope.tipoDocumento);
					$scope.uploadok = true;
					$scope.dtoFdr.codiceFiscale = $rootScope.profiloattivo.utente.codicefiscale;
					$scope.dtoFdr.errorMessage='';
					$scope.dtoFdr.errorCode='';

					// chiama ws di firma remota
					Verbale.firmaDocumento({id:$scope.sedutaGiunta.verbale.id, idProfilo:$rootScope.profiloattivo.id}, $scope.dtoFdr, function (result, headers) {
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$scope.uploadok = false;
// $state.go('sedutaGiunta');
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
						},
						function(error) {
							$scope.uploadok = false;
							$scope.dtoFdr.password='';
							$scope.dtoFdr.otp='';
							$scope.dtoFdr.errorMessage=error.data.errorMessage;
							$scope.dtoFdr.errorCode=error.data.errorCode;
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
						});

				}

				// chiama il ws di upload file firmato
				else {
					$scope.uploadDocumenti($scope.documentiFirmatiDaCaricare, "documenti_firmati")
						.then(function() {
							$scope.documentiFirmatiDaCaricare = new Map();
							ngToast.create(  { className: 'success', content: 'Operazione effettuata con successo' } );
							$('#mascheraWorkflow').modal('hide');
							$scope.refresh();
							$scope.taskLoading = false;
						});

				}

			}

			/**
			 * Allegati del Verbale
			 */
			$scope.fileDropped = function (files, event, rejectedFiles) {
				//$log.debug(event);

				if(event.type !== "click"){
					if(rejectedFiles.length > 0){
						$log.log("There are rejected files for the size");
						$rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, 'application/*');
						return;
					}

					Upload.upload({
						url: 'api/verbales/'+$scope.sedutaGiunta.verbale.id +'/allegato',
						headers : {
							'Authorization': 'Bearer '+ $rootScope.accessToken
						},
						file: files
					}).progress(function (evt) {
						$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
					}).success(function (data, status, headers, config) {
						//$log.debug("upload ok");
						$scope.refresh();
					});
				}
			};

			$scope.estensioniAccettate = ["doc", "docx", "rtf", "txt", "odt"];
			$scope.formatoFileParteIntegrante = [ ".odt", ".rtf", ".doc", ".docx", ".txt", ".pdf"];

			$scope.checkFileFormat = function() {

				if ($scope.allegatoEdit.parteIntegrante) {
					var allExt = $scope.allegatoEdit.file.nomeFile.split('.').pop().toLowerCase();

					if ($scope.formatoFileParteIntegrante.indexOf("." + allExt) < 0) {
						$('#warningFileType').modal('show');
						$scope.allegatoEdit.parteIntegrante = false;
					}
				}
			};

			$scope.modificaAllegato = function(allegato){
				/*
			 * DocumentoInformatico.get({id:allegato.id},function(result){
			 * $scope.allegatoEdit=result; $scope.allegatoEdit.edit=true; });
			 */
				$scope.backupAllegato = {};
				angular.copy(allegato, $scope.backupAllegato);
				$scope.allegatoEdit=allegato;
				$scope.allegatoEdit.edit=true;
			};

			$scope.$watch("allegatoEdit", function(newValue){
				if(newValue != null && newValue!=undefined && newValue!={} && newValue.id!=null && newValue.id != undefined){
					$scope.salvaAllegato(newValue);
				}
			});

			$scope.salvaAllegato = function(allegatoEdit){
				for(var i = 0; i<$scope.sedutaGiunta.verbale.allegati.length; i++){
					if($scope.sedutaGiunta.verbale.allegati[i].id == allegatoEdit.id){
						$scope.sedutaGiunta.verbale.allegati[i] = allegatoEdit;
						break;
					}
				}
				/*
			 * DocumentoInformatico.update( allegatoEdit, function(result) {
			 * $scope.allegatoEdit=result;
			 * $scope.allegatoEdit.edit=allegatoEdit.edit;
			 * Atto.allegati({id:$scope.atto.id},function(result){
			 * $scope.atto.allegati = result; }); });
			 */
			};

			$scope.allegatiOptions = {
				dropped: function(event) {
					//$log.debug(event);
					//$log.debug(event.dest.index);
					for(var i = event.dest.index; i <  $scope.sedutaGiunta.verbale.allegati.length; i++){
						$scope.sedutaGiunta.verbale.allegati[i].ordineInclusione = Number(i +1);
					}
				}
			};

			$scope.allegatoAnnullaEdit = function(allegato){
				allegato.edit = false;
				$scope.allegatoEdit = {};
				$scope.allegatoEdit.edit=false;
				angular.copy($scope.backupAllegato, allegato);
			};

			$scope.deleteAllegato = function (allegato) {
				$scope.allegatoDelete = null;
				$timeout(function () {
					$scope.allegatoDelete = allegato;
					$('#deleteAllegatoConfirmation').modal('show');
				}, 800);
			};

			$scope.confirmDeleteAllegato = function (id) {
				DocumentoInformatico.delete({id: id}, function () {
					for(var i = 0; i<$scope.sedutaGiunta.verbale.allegati.length; i++){
						if($scope.sedutaGiunta.verbale.allegati[i].id == id){
							$scope.sedutaGiunta.verbale.allegati.splice(i, 1);
						}
					}
					$('#deleteAllegatoConfirmation').modal('hide');
				});
			};

			$scope.purificaTestoPerJs = function(testoOriginale){
				return $sce.trustAsJs(testoOriginale);
			};
			/**
			 * Fine sezione Allegati del Verbale
			 */

			/**
			 * Sezione Sottoscrittori del Verbale
			 */
			$scope.sottoscrittoriOptions = {
				dropped: function(event) {
					//$log.debug(event);
					//$log.debug(event.dest.index);
					for(var i = event.dest.index; i <  $scope.sedutaGiunta.verbale.sottoscrittori.length; i++){
						$scope.sedutaGiunta.verbale.sottoscrittori[i].ordineFirma = Number(i +1);
					}
				}
			};

			/**
			 * Sezione Sottoscrittori del Resoconto
			 */
			$scope.sottoscrittoriResocontoOptions = {
				dropped: function(event) {
					//$log.debug(event);
					//$log.debug(event.dest.index);
					for(var i = event.dest.index; i <  $scope.sedutaGiunta.sottoscrittoriresoconto.length; i++){
						$scope.sedutaGiunta.sottoscrittoriresoconto[i].ordineFirma = Number(i +1);
					}
				}
			};

			$scope.addSottoscrittoreResoconto = function( sedutaGiunta){
				var sottoscrittore = {
					ordineFirma: sedutaGiunta.sottoscrittoriresoconto.length + 1,
					firmato: false,
					sedutaresoconto: {id: sedutaGiunta.id},
					profilo: null,
					qualifica: null,
					qualificaProfessionale: null
				}
				sedutaGiunta.sottoscrittoriresoconto.push(sottoscrittore);
			};

			$scope.deleteSottoscrittoreResoconto = function (sedutaGiunta, index, item) {
				if(typeof item.id != "undefined") {
					$scope.sottoscrittoreResocontoDelete = item;
					$scope.sottoscrittoreResocontoDeleteIndex = index;
					$('#deleteSottoscrittoreResocontoConfirmation').modal('show');
				}
				else {
					sedutaGiunta.sottoscrittoriresoconto.splice(index ,1);
				}
			};

			$scope.confirmDeleteSottoscrittoreResoconto = function(id){
				SedutaGiunta.deletesottoscrittore({id: id},{},function () {
					$scope.sottoscrittoreResocontoDelete = null;
					$scope.sedutaGiunta.sottoscrittoriresoconto.splice($scope.sottoscrittoreResocontoDeleteIndex ,1);
					$scope.sottoscrittoreResocontoDeleteIndex = null;
					$('#deleteSottoscrittoreResocontoConfirmation').modal('hide');
				});
			};

			$scope.addSottoscrittoreVerbale = function ( verbale ) {
				$scope.sottoscrittoreVerbale = {
					ordineFirma: verbale.sottoscrittori.length + 1,
					firmato: false,
					verbale: { id: verbale.id}
				}
				verbale.sottoscrittori.push($scope.sottoscrittoreVerbale);
			};

			$scope.deleteSottoscrittoreVerbale = function (verbale, index ) {
				verbale.sottoscrittori.splice(index ,1);
			};
			/**
			 * Fine sezione Sottoscrittori del Verbale
			 */
			$scope.updateSezioneVerbale = function (value){
				$scope.showSezioneVerbale = value;
			};

			$scope.updateSezioneResoconto = function (value){
				$scope.showSezioneResoconto = value;
			};

			$scope.rinunciaVerbaleConfirmation = function() {
				$('#rinunciaVerbaleConfirmation').modal('show');
			};

			$scope.confirmRinunciaVerbale = function() {
				$('#rinunciaVerbaleConfirmation').modal('hide');
				var decisione = {codice: "rinuncia-verbale",descrizione:"Rinuncia al Verbale",tipo:"verbale",mostraMaschera:false};
				$scope.callDecisioneVerbale($scope.indexSezioneCorrente, decisione);
			};

			/**
			 * Fine sezione Verbale
			 */

			/** ORDIAMENTO ATTI inseriti**/
			$scope.showOrdinamentoDialog = function(node){
				selectedAttoOrder = node;
				$scope.attoNewPosition = selectedAttoOrder.ordineOdg;
				let lastIdx = $scope.odgSelected.attos.length -1;
				$scope.maxPosition = $scope.odgSelected.attos[lastIdx].ordineOdg;
				$scope.minPosition = $scope.odgSelected.attos[0].ordineOdg;
				$('#ordinamentoAttiInseritiModal').modal('show');
			}

			$scope.checkPosition = function(attoNewPosition){
				$scope.positonError = false;
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.id == $scope.id && $scope.sedutaGiunta.odgs[i].tipoOdg == $scope.odgSelected.tipoOdg){
						selectedAttoOrder.ordineOdg = selectedAttoOrder.ordineOdg == 0 ? i+1 : selectedAttoOrder.ordineOdg;
						$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						//let attiOdgBaseSize = 0;
						/*
						if($scope.odgSelected.tipoOdg.id == '3'){
		 					attiOdgBaseSize += $scope.getOdgBase($scope.sedutaGiunta).attos.length;
		 			  }
						$scope.maxPosition = $scope.sedutaGiunta.odgs[i].attos.length + attiOdgBaseSize-1;
						$scope.minPosition = attiOdgBaseSize;
						*/
						let validPosition = (attoNewPosition > 0 && attoNewPosition <= $scope.maxPosition);
						let changed = true; //selectedAttoOrder.ordineOdg != attoNewPosition;
						if(validPosition){
							if(!changed){
								//nessun cambiamento
								$scope.positonError = true;
							}
						}else{
							//posizione non valida
							$scope.positonError = true;
						}
					}
				}
			}

			$scope.confirmOrdinamentoAtto = function(attoNewPosition){
				let errorCode = -1;
				for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
					if($scope.sedutaGiunta.id == $scope.id && $scope.sedutaGiunta.odgs[i].tipoOdg == $scope.odgSelected.tipoOdg){
						//se ordineOdg = 0 sul db setto la posizione corrente in base all'indice
						selectedAttoOrder.ordineOdg = selectedAttoOrder.ordineOdg == 0 ? i+1 : selectedAttoOrder.ordineOdg;
						$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
						let attiOdgBaseSize = 0;
						if($scope.odgSelected != undefined && $scope.odgSelected.tipoOdg.id == '3'){
							attiOdgBaseSize += $scope.getOdgBase($scope.sedutaGiunta).attos.length;
						}
						let validPosition = (attoNewPosition > 0 && attoNewPosition <= $scope.sedutaGiunta.odgs[i].attos.length+attiOdgBaseSize);
						let changed = true; //selectedAttoOrder.ordineOdg != attoNewPosition;
						if(validPosition){
							if(changed){
								let result = riordinaAtti($scope.sedutaGiunta.odgs[i].attos,attoNewPosition);
								if(result != undefined){
									$scope.sedutaGiunta.odgs[i].attos = result;
								}
							}else{
								//nessun cambiamento
								errorCode = 1;
							}
						}else{
							//posizione non valida
							errorCode = 2;
						}

					}
				}
				if(errorCode<0){
					//salvo le modifiche
					$scope.callDecisione(1,$scope.decisioni[0]);
					$('#ordinamentoAttiInseritiModal').modal('hide');
				}else{
					switch(errorCode){
						case 1:
							ngToast.create(  { className: 'error', content: 'Posizione identica' } );
							break;
						case 2:
							ngToast.create(  { className: 'error', content: 'Posizione non valida' } );
							break;
					}
				}
			}

			function riordinaAtti(atti,newPosition){
				let idx = 1;
				if($scope.odgSelected != undefined &&  $scope.odgSelected.tipoOdg.id == '3'){
					let attiOdgBaseSize = $scope.getOdgBase($scope.sedutaGiunta).attos.length;
					newPosition = newPosition - attiOdgBaseSize;
				}
				angular.forEach(atti, function(atto) {
					atto.ordineOdg = idx;
					idx++;
				});
				if(newPosition != undefined && newPosition >= 0 && atti.length >= newPosition){
					atti = move(atti,selectedAttoOrder.ordineOdg-1, newPosition-1);
				}
				idx = 1;
				if($scope.odgSelected != undefined && $scope.odgSelected.tipoOdg.id == '3'){
					idx = $scope.getOdgBase($scope.sedutaGiunta).attos.length+1;
				}
				angular.forEach(atti, function(atto) {
					atto.ordineOdg = idx;
					idx++;
				});
				return atti;
			};

			function riordinaAttiOdg(atti,newPosition){
				let idx = 1;
				if($scope.odgSelected != undefined &&  $scope.odgSelected.tipoOdg.id == '3'){
					let attiOdgBaseSize = $scope.getOdgBase($scope.sedutaGiunta).attos.length;
					newPosition = newPosition - attiOdgBaseSize;
				}
				if(newPosition != undefined && newPosition >= 0 && atti.length >= newPosition){
					atti = move(atti,selectedAttoOrder.ordineOdg-1, newPosition-1);
				}
				else if(atti){
					console.log("ORDINO NUOVO");
					atti.sort(function ( a, b ) {
						var aStrSpl = a.atto.codiceCifra.split('/');
						var bStrSpl = b.atto.codiceCifra.split('/');
						var aIdx = $scope.tipiAttoGiuntaOrder.indexOf(aStrSpl[0]);
						var bIdx = $scope.tipiAttoGiuntaOrder.indexOf(bStrSpl[0]);
						if((aIdx < 0 && bIdx > -1) || (aIdx > bIdx)){
							return 1;
						}
						else if((aIdx > -1 && bIdx < 0) || (aIdx < bIdx)){
							return -1;
						}
						else {
							if(Number(aStrSpl[1]) > Number(bStrSpl[1])){
								return 1;
							}
							else if(Number(aStrSpl[1]) < Number(bStrSpl[1])){
								return -1;
							}
							else {
								console.log("get assessore proponente ", a.atto);
								var aAssPrp = getAssessoreProponente(a.atto.objs);
								var bAssPrp = getAssessoreProponente(b.atto.objs);
								if(Number(aAssPrp.ordineGiunta) > Number(bAssPrp.ordineGiunta)){
									return 1;
								}
								else if(Number(aAssPrp.ordineGiunta) < Number(bAssPrp.ordineGiunta)){
									return -1;
								}
								else if(Number(aAssPrp.ordineGiunta) == Number(bAssPrp.ordineGiunta)){
									var aNum = aStrSpl[2];
									var bNum = bStrSpl[2];
									if(Number(aNum) > Number(bNum)){
										return 1;
									}
									else if(Number(aNum) < Number(bNum)){
										return -1;
									}
									return 0
								}
								return 0
							}
						}
						return 0;
					});
				}
				if($scope.odgSelected != undefined && $scope.odgSelected.tipoOdg.id == '3'){
					idx = $scope.getOdgBase($scope.sedutaGiunta).attos.length+1;
				}
				angular.forEach(atti, function(atto) {
					atto.ordineOdg = idx;
					idx++;
				});

				return atti;
			};

			function getAssessoreProponente(lstProponenti){
				if(lstProponenti){
					var assPrp;
					angular.forEach(lstProponenti, function(prop) {
						if(!assPrp || !assPrp.ordineGiunta ||  assPrp.ordineGiunta > prop.ordineGiunta) {
							console.log("Setto assessore proponente ", prop);
							assPrp = prop;
						}
					});
					return assPrp;
				}
				return null;
			}


			function riordinaAttiOdl(atti,newPosition){
				let idx = 1;
				if($scope.odgSelected != undefined &&  $scope.odgSelected.tipoOdg.id == '3'){
					let attiOdgBaseSize = $scope.getOdgBase($scope.sedutaGiunta).attos.length;
					newPosition = newPosition - attiOdgBaseSize;
				}
				if(newPosition != undefined && newPosition >= 0 && atti.length >= newPosition){
					atti = move(atti,selectedAttoOrder.ordineOdg-1, newPosition-1);
				}
				else if(atti){
					atti.sort(function ( a, b ) {
						var aStrSpl = a.atto.codiceCifra.split('/');
						var bStrSpl = b.atto.codiceCifra.split('/');
						var aIdx = $scope.tipiAtto.indexOf('DC-DPC-DIC'.includes(aStrSpl[0])? 'DC-DPC-DIC': aStrSpl[0]);
						var bIdx = $scope.tipiAtto.indexOf('DC-DPC-DIC'.includes(bStrSpl[0])? 'DC-DPC-DIC': bStrSpl[0]);
						if((aIdx < 0 && bIdx > -1) || (aIdx > bIdx)){
							return 1;
						}
						else if((aIdx > -1 && bIdx < 0) || (aIdx < bIdx)){
							return -1;
						}
						else {
							if(Number(aStrSpl[1]) > Number(bStrSpl[1])){
								return 1;
							}
							else if(Number(aStrSpl[1]) < Number(bStrSpl[1])){
								return -1;
							}
							else {
								var aNum = 'DC-DPC-DIC'.includes(aStrSpl[0]) ? aStrSpl[2] : a.atto.numeroAdozione;
								var bNum = 'DC-DPC-DIC'.includes(bStrSpl[0]) ? bStrSpl[2] : b.atto.numeroAdozione;
								if(Number(aNum) > Number(bNum)){
									return 1;
								}
								else if(Number(aNum) < Number(bNum)){
									return -1;
								}
							}
						}
						return 0;
					});
				}
				if($scope.odgSelected != undefined && $scope.odgSelected.tipoOdg.id == '3'){
					idx = $scope.getOdgBase($scope.sedutaGiunta).attos.length+1;
				}
				angular.forEach(atti, function(atto) {
					atto.ordineOdg = idx;
					idx++;
				});

				return atti;
			};

			function move(arr, old_index, new_index) {
				while (old_index < 0) {
					old_index += arr.length;
				}
				while (new_index < 0) {
					new_index += arr.length;
				}
				if (new_index >= arr.length) {
					var k = new_index - arr.length;
					while ((k--) + 1) {
						arr.push(undefined);
					}
				}
				arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
				return arr;
			}

			// ORDINAMENTO ATTI INSERITI FINE **/

			/** ORDINAMENTO Elenco Discussione **/
			$scope.showOrdinamentoElencoDiscussioneDialog = function(node){
				if($scope.isOperatoreResoconto && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneOrdineDiscussione.label){
					selectedAttoOrder = node;
					$('#ordinamentoElencoDiscussioneModal').modal('show');
				}
			}

			$scope.checkPositionElencoDiscussione = function(attoNewPosition){
				$scope.positonError = false;
				let atti = $scope.attiResocontoList;
				$scope.maxPosition = atti.length;
				//selectedAttoOrder.numeroDiscussione = 1;

				let validPosition = (attoNewPosition > 0 && attoNewPosition <= atti.length);
				let changed = true; //selectedAttoOrder.ordineOdg != attoNewPosition;
				if(validPosition){
					if(!changed){
						//nessun cambiamento
						$scope.positonError = true;
					}
				}else{
					//posizione non valida
					$scope.positonError = true;
				}
			}

			$scope.confirmOrdinamentoElencoDiscussione= function(attoNewPosition){
				let atti = $filter('orderBy')($scope.attiResocontoList, ['numeroDiscussione']);
				let errorCode = -1;
				let vecchioNumeroDiscussione = selectedAttoOrder.numeroDiscussione;

				let validPosition = (attoNewPosition > 0 && attoNewPosition <= atti.length);
				let changed = true; //selectedAttoOrder.ordineOdg != attoNewPosition;
				if(validPosition){
					if(changed){
						//atti = riordinaElencoDiscussione(atti,attoNewPosition);
						
						var idx = 1;
						
						atti = move(atti,vecchioNumeroDiscussione-1, attoNewPosition-1);
						angular.forEach(atti, function(atto) {
							atto.numeroDiscussione = idx;
							idx++;
						});
						$scope.attiResocontoList=atti;
						
					}else{
						//nessun cambiamento
						errorCode = 1;
					}
				}else{
					//posizione non valida
					errorCode = 2;
				}

				if(errorCode<0){
					//salvo le modifiche
					/*
			for(var j = 0; j < $scope.sedutaGiunta.odgs.length; j++){
			 if($scope.sedutaGiunta.id == $scope.id ){
				 for(var k = 0; j < $scope.sedutaGiunta.odgs[j].attos.length; k++){
					 for(var i=0; i < atti.length; i++){
						  if(atti[i].id == $scope.sedutaGiunta.odgs[j].attos[k].id){
								$scope.sedutaGiunta.odgs[j].attos[k].numeroDiscussione = atti[i].numeroDiscussione;
							}
					 }
				 }
			 }
		 }
		 */
					$scope.callDecisione(1,$scope.decisioni[0]);
					$('#ordinamentoElencoDiscussioneModal').modal('hide');
				}else{
					switch(errorCode){
						case 1:
							ngToast.create(  { className: 'error', content: 'Posizione identica' } );
							break;
						case 2:
							ngToast.create(  { className: 'error', content: 'Posizione non valida' } );
							break;
					}
					return false;
				}


			}

			function riordinaElencoDiscussione(atti,newPosition){
				var idx = 1;
				//angular.forEach(atti, function(atto) {
				//	atto.numeroDiscussione = idx;
				//	idx++;
				//});
				let attiChanged = move(atti,selectedAttoOrder.numeroDiscussione-1, newPosition-1);
				idx = 1;
				angular.forEach(attiChanged, function(atto) {
					atto.numeroDiscussione = idx;
					idx++;
				});
				return attiChanged;
			};
			/** ORDIAMENTO Elenco Discussione FINE**/


			/** ORDIAMENTO Elenco Argomento **/
			$scope.showOrdinamentoElencoArgomentoDialog = function(node){
				$scope.attoDaAggiornare = node;
				if(!node.argomentoExSeduta && $scope.isOperatoreResoconto && $scope.sedutaGiunta.stato == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneNumeroArgomento.label){
					$scope.attoNewPosition = node.numeroArgomento;
					$scope.ode = node.nargOde;
					$('#ordinamentoElencoArgomentoModal').modal('show');
				}
			}

			$scope.confirmOrdinamentoElencoArgomento= function(attoNewPosition){
				if(attoNewPosition != undefined){
					let atti = [];
					let errorCode = -1;
					for(var i = 0; i < $scope.sedutaGiunta.odgs.length; i++){
						if($scope.sedutaGiunta.id == $scope.id){
							$scope.sedutaGiunta.odgs[i].sedutaGiunta = {id:$scope.sedutaGiunta.id};
							for(var j = 0; j < $scope.sedutaGiunta.odgs[i].attos.length; j++){
								if($scope.sedutaGiunta.odgs[i].attos[j].id == selectedAttoOrder.id){
									$scope.sedutaGiunta.odgs[i].attos[j].numeroArgomento = attoNewPosition;
								}
							}
						}
					}

					$scope.callDecisione(1,$scope.decisioni[0]);
					$('#ordinamentoElencoArgomentoModal').modal('hide');

				}
			}
			/** ORDIAMENTO Elenco Argomento FINE**/




			/** rubrica destinatari interni * */
			// destinatari esterni ed interni
			$scope.loadRubricaDestinatarioInterno= function  () {

				if(!$scope.aooDestinatari)
					Aoo.getMinimal(function(aoos) {
						$scope.aooDestinatari = aoos.map(function(aoo){
							var attoHasDestinatario = {};
							attoHasDestinatario["sedutaId"] = $scope.sedutaGiunta.id;
							attoHasDestinatario["tipoDestinatario"] = {descrizione: "Aoo"};
							attoHasDestinatario["destinatario"] = aoo;
							attoHasDestinatario["name"] = aoo.descrizione;
							attoHasDestinatario["email"] = aoo.email;
							attoHasDestinatario["descrizione"] = aoo.descrizioneAsDestinatario;
							attoHasDestinatario["strippedDescrizione"] = aoo.descrizioneAsDestinatario;
							attoHasDestinatario["codice"] = aoo.codice;
							return attoHasDestinatario;
						});

						// Order by codice AOO e Descrizione
						$scope.aooDestinatari = $filter('orderBy')($scope.aooDestinatari, ['codice', 'name']);

						$scope.changeTipoDestinatario('aoo');
					});

				if(!$scope.assessoratoDestinatari)
					Assessorato.query(function(assessoratos) {
						$scope.assessoratoDestinatari = assessoratos.map(function(assessorato){
							var attoHasDestinatario = {};
							attoHasDestinatario["sedutaId"] = $scope.sedutaGiunta.id;
							attoHasDestinatario["tipoDestinatario"] = {descrizione: "Assessorato"};
							attoHasDestinatario["destinatario"] = assessorato;
							attoHasDestinatario["name"] = assessorato.denominazione;
							attoHasDestinatario["email"] = assessorato.email;
							attoHasDestinatario["descrizione"] = assessorato.descrizioneAsDestinatario;
							attoHasDestinatario["strippedDescrizione"] = assessorato.descrizioneAsDestinatario;
							return attoHasDestinatario;
						});

						// Order by Denominazione
						$scope.assessoratoDestinatari = $filter('orderBy')($scope.assessoratoDestinatari, 'name');
					});

				if(!$scope.utenteDestinatari)
					Utente.query(function(utentes) {
						$scope.utenteDestinatari = utentes.map(function(utente){
							var attoHasDestinatario = {};
							attoHasDestinatario["sedutaId"] = $scope.sedutaGiunta.id;
							attoHasDestinatario["tipoDestinatario"] = {descrizione: "Utente"};
							attoHasDestinatario["destinatario"] = utente;
							attoHasDestinatario["name"] = utente.cognome + ' ' + utente.nome;
							attoHasDestinatario["cognome"] = utente.cognome;
							attoHasDestinatario["nome"] = utente.nome;
							attoHasDestinatario["email"] = utente.email;
							attoHasDestinatario["descrizione"] = utente.descrizioneAsDestinatario;
							attoHasDestinatario["strippedDescrizione"] = $scope.stripHtml(utente.descrizioneAsDestinatario);
							return attoHasDestinatario;
						});

						// Order by Cognome e Nome
						$scope.utenteDestinatari = $filter('orderBy')($scope.utenteDestinatari, ['cognome', 'nome']);
					});

				if($scope.sedutaGiunta.destinatariInterni) {
					$scope.sedutaGiunta.destinatariInterni = $scope.sedutaGiunta.destinatariInterni.map(function(dest){

						if(dest.tipoDestinatario.descrizione=="Aoo") {
							dest["name"] = dest.destinatario.descrizione;
							dest["email"] = dest.destinatario.email;
							dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
							dest["strippedDescrizione"] = dest.destinatario.descrizioneAsDestinatario;
							return dest;
						}
						if(dest.tipoDestinatario.descrizione=="Assessorato") {
							dest["name"] = dest.destinatario.denominazione;
							dest["email"] = dest.destinatario.email;
							dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
							dest["strippedDescrizione"] = dest.destinatario.descrizioneAsDestinatario;
							return dest;
						}
						if(dest.tipoDestinatario.descrizione=="Utente") {
							dest["name"] = dest.destinatario.cognome+ ' ' +dest.destinatario.nome;
							dest["email"] = dest.destinatario.email;
							dest["descrizione"] = dest.destinatario.descrizioneAsDestinatario;
							dest["strippedDescrizione"] = $scope.stripHtml(dest.destinatario.descrizioneAsDestinatario);
							return dest;
						}
					});
					//$log.debug("destinatariInterni",$scope.sedutaGiunta.destinatariInterni);
				}
			}
			$scope.changeTipoDestinatario = function  (preset) {
				$scope.rubricaDestinatarioInternos = [];
				if(preset == 'aoo')
					$scope.rubricaDestinatarioInternos = $scope.aooDestinatari;

				if(preset == 'ass')
					$scope.rubricaDestinatarioInternos =$scope.assessoratoDestinatari;

				if(preset == 'ute')
					$scope.rubricaDestinatarioInternos = $scope.utenteDestinatari;

				//$log.debug("rubricaDestinatarioInternos",$scope.rubricaDestinatarioInternos);
			}

			$scope.querySearchInt= function  (query) {
				var results = query ?
					$scope.rubricaDestinatarioInternos.filter( $scope.createFilterForInt(query)) : [];
				return results;
			}
			$scope.createFilterForInt = function (query) {
				var lowercaseQuery = angular.lowercase(query);
				return function filterFn(contact) {
					contact.descrizione = contact.destinatario.descrizioneAsDestinatario;
					var lowercaseDest = angular.lowercase(contact.destinatario.descrizioneAsDestinatario);
					return (lowercaseDest.indexOf(lowercaseQuery) != -1);
				};
			}
			/**
			 * String the HTML from the given string.
			 *
			 * @param string
			 *            The string to strip.
			 */
			$scope.stripHtml = function(string){
				return jQuery('<div>' + string + '</div>').text();
			};
			/** fine destinatari interni * */

			/*-------------------------------------------------------------*/
			switch($scope.id){
				case "nuova-giunta":
					//$log.debug("Nuova seduta Giunta");
					$scope.decisioni = [
						{codice: "crea-seduta",descrizione:"Crea Seduta",tipo:"globale",mostraMaschera:false}
					];
					break;
				case "nuova-consiglio":
					//$log.debug("Nuova seduta Consiglio");
					$scope.decisioni = [
						{codice: "crea-seduta",descrizione:"Crea Seduta",tipo:"globale",mostraMaschera:false}
					];
					break;
				default:
					//$log.debug("Modifica Seduta");
					if ($scope.sedutaAttoId) {
						$scope.load($scope.sedutaAttoId);
					}
					else {
						$scope.load($stateParams.id);
					}
					break;
			}

			$scope.openTabOdgBaseOrdinario = function(tabName) {
				var i;
				var x = document.getElementsByClassName("odgBaseOrdinarioTab");
				for (i = 0; i < x.length; i++) {
					x[i].classList.remove("active");
					x[i].style.display = "none";
				}
				document.getElementById(tabName).style.display = "block";
				document.getElementById(tabName).classList.add("active");
				if($scope.isSolaLettura($scope.odgSelected) || (!$scope.isOperatoreSegreteriaConsiglio&&!$scope.isOperatoreSegreteriaGiunta&&!$scope.isOperatoreResoconto)){
					$('.note-editable').attr('contenteditable','false')
				}else{
					$('.note-editable').attr('contenteditable','true')
				}
			}

			$scope.getTooltip = function(item){
				//Tooltip task "paralleli": in "taskAttivi" se nominativo == null e aoo != null, mettere aoo.descrizione
				var textHtml;
				var li = '';
				var infos = $scope.getInfoCommissioni(item);
				if(item != undefined && item.taskAttivi != undefined && item.taskAttivi.length>0){
					for(var i = 0; i < item.taskAttivi.length; i++){
						li += '<li>';
						let nominativoDescrizione = false;
						let nominativo = item.taskAttivi[i].nominativo;
						if(nominativo != null){
							li += '<b>' + nominativo + '</b>';
							nominativoDescrizione = true;
						}else{
							let descrizione = item.taskAttivi[i].aoo != null ? item.taskAttivi[i].aoo.descrizione: '';
							if(descrizione != null){
								li += '<b>' + descrizione + '</b>';
							}
							nominativoDescrizione = true;
						}
						let descrizioneTask = item.taskAttivi[i].descrizione;
						var terminiScaduti = '';
						if(descrizioneTask != null){
							for(var j = 0; j< infos.length; j++){
								var infoAoo = infos[j].aoo;
								var scadenza = null;
								if(item.taskAttivi[i].aoo != null && infoAoo === item.taskAttivi[i].aoo.descrizione){
									if(infos[j].conf && infos[j].conf.dataManuale && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
										scadenza = new Date(infos[j].conf.dataManuale);
										scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
									}
					    			else if(infos[j].conf && infos[j].conf.dataCreazione && infos[j].conf.giorniScadenza!=undefined && infos[j].conf.giorniScadenza!=null){
					    				scadenza = new Date(infos[j].conf.dataCreazione);
					    				scadenza.setDate(scadenza.getDate() + infos[j].conf.giorniScadenza);
					    			}
									var adesso = new Date();
									if(scadenza){
					    				if(scadenza.getTime() > adesso.getTime()){
					    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(scadenza);
					    				}else{
					    					terminiScaduti += " - Termini scaduti"
					    				}
					    			}
								}
							}
							
							
							if(nominativoDescrizione){
								li += ' (Mancanza ' + descrizioneTask + terminiScaduti +')';
							} else li += 'Mancanza ' + descrizioneTask;

						}
						li += '</li>';
					}
				}
                if(item.pareri && item.pareri.length > 0 ){
                	item.pareri.forEach(p => {
                        if(p && !p.annullato && p.tipoAzione && p.tipoAzione.codice== 'parere_quartiere_revisori' && !p.data){
                        	var adesso = new Date();
                        	var terminiScaduti = '';
                        	if(p.dataScadenza){
        	    				if(new Date(p.dataScadenza) > adesso){
        	    					terminiScaduti = " - Scadenza Termini " + $scope.formattaData(new Date(p.dataScadenza));
        	    				}else{
        	    					terminiScaduti += " - Termini scaduti"
        	    				}
        	    			}
                        	li += '<li><b>' + p.aoo.descrizione  +'</b><br/>(Mancanza ' + p.tipoAzione.descrizione + terminiScaduti + ')</li>';
                        }
                    })
                }
				if(li){
					textHtml = '<ul class="taskTooltip">' + li + '</ul>';
				}
				else return null;

				return textHtml;
			}
			
			$scope.formattaData = function(data){
	    		if(data){
		    		var str = "";
		    		if(data.getDate() < 10){
		    			str += "0";
		    		}
		    		str += (data.getDate());
		    		str += "/";
		    		if((data.getMonth() + 1) < 10){
		    			str += "0";
		    		}
		    		str += (data.getMonth() + 1);
	    		    str += "/" + data.getFullYear();
	    		    return str;
	    		}else{
	    			return "";
	    		}
	    	};
			
			
			$scope.getInfoCommissioni = function(atto){
	    		var infos = [];
	    		if (atto.pareri) {
	    			for (var pc = 0; pc < atto.pareri.length; pc++) {
	    				if (atto.pareri[pc].annullato) {
	    					continue;
	    				}
	    				if ($rootScope.configurationParams.codice_tipo_parere_commissioni_consiliari == atto.pareri[pc].tipoAzione.codice) {
	    					var info = {
	    						'aoo': atto.pareri[pc].aoo.descrizione,
	    						'parere':atto.pareri[pc],
	    						'task': null
	    					};
	    					infos.push(info);
	    				}
	    			}
	    		}
	    		if (atto.taskAttivi) {
	    			for (var tc = 0; tc < atto.taskAttivi.length; tc++) {
	    				if (atto.taskAttivi[tc].aoo && atto.taskAttivi[tc].descrizione && atto.taskAttivi[tc].descrizione.toLowerCase().indexOf('commissione') > -1){
		    				var infoExists = false;
		    				for(var i = 0; i< infos.length; i++){
		    					if(infos[i].aoo == atto.taskAttivi[tc].aoo.descrizione){
		    						infos[i].task = atto.taskAttivi[tc];
		    						infoExists=true;
		    					}
		    				}
		    				if(!infoExists){
		    					var info = {
		    						'aoo': atto.taskAttivi[tc].aoo.descrizione,
		    						'parere':null,
		    						'task': atto.taskAttivi[tc]
		    					};
		        				infos.push(info);
		    				}
	    				}
	    			}
	    		}
	    		if (atto.objs) {
	    			for (var x = 0; x < atto.objs.length; x++) {
	    				if (atto.objs[x] && atto.objs[x].listConfigurazioneIncaricoAooDto && atto.objs[x].listConfigurazioneIncaricoAooDto.length > 0){
	    					for(var y = 0; y < atto.objs[x].listConfigurazioneIncaricoAooDto.length; y++){
			    				var conf = atto.objs[x].listConfigurazioneIncaricoAooDto[y];
			    				var existsinfo = false;
			    				for(var i = 0; i< infos.length; i++){
			    					if(infos[i].aoo == conf.descrizioneAoo){
			    						existsinfo = true;
			    						infos[i].conf = conf;
			    					}
			    				}
			    				if(!existsinfo){
			    					var info = {
			    						'aoo': conf.descrizioneAoo,
			    						'parere':null,
			    						'task': null,
			    						'conf':conf
			    					};
			        				infos.push(info);
			    				}
	    					}
	    				}
	    			}
	    		}
	    		return infos;
	    	}

			$scope.getTooltipPareri = function(item){
				var textHtml;
				var li = '';
				if(item.pareri && item.pareri.length > 0 ){
					item.pareri.forEach(p => {
						if(p && p.tipoAzione.codice== 'parere_resp_tecnico' && p.parerePersonalizzato && !p.annullato){
							li += '<li><b>Parere Tecnico Articolato: </b>'+ p.parerePersonalizzato+'</li>';
						}
						else if(p && p.tipoAzione.codice == 'parere_resp_contabile'  &&	p.parerePersonalizzato && !p.annullato){
							li += '<li><b>Parere Contabile Articolato: </b>'+ p.parerePersonalizzato+'</li>';
						}
					})
				}
				if(li){
					textHtml = '<ul class="taskTooltip">' + li + '</ul>';
				}
				else return null;

				return textHtml;
			}

			$scope.nontTrattatoMassivo = function(odg){
				let odgs = odg.selected;
				if(odgs != undefined && odgs.length>0){
					for(var i = 0; i < odgs.length; i++){
						$scope.nonTrattato(odgs[i]);
					}
				}
			}

			$scope.nonTrattato = function (item) {
				let idOdg = item.id;
				if(idOdg != null){
					let idOdgs = [];
					idOdgs.push(idOdg);
					$scope.param={};
					$scope.param.attoOdgId=idOdgs;
					//$scope.param.profiloId=$rootScope.profiloattivo.id;
					//attoOdgId:'@attoOdgId', profiloId:'@profiloId'
					// $scope.param.profiloId=$scope.dtoWorkflow.campi['modelloHtmlId'].id;
					OrdineGiorno.nontrattati($scope.param,function(result){
						if(result){
							item.esito = SedutaGiuntaConstants.esitoAttoOdg.nonTrattato;
							ngToast.dismiss();
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$('#ripristinaAttiNonTrattatiConfirmation').modal('hide');
							$( "input[name^='selectAll']" ).prop('checked', false);
							$scope.refresh();
							$scope.taskLoading = false;
						}
					})
				}
				//$log.debug("AttiOdg",$scope.sedutaGiunta.odgs[0].attos);
			};

			$scope.apriTrattatiMassivo = function(odg){
				$scope.attiNonTrattati = odg.selected;
				$('#ripristinaAttiNonTrattatiConfirmation').modal('show');
			}

			$scope.confirmTrattato = function(atto){
				$scope.attiNonTrattati = [atto];
				$('#ripristinaAttiNonTrattatiConfirmation').modal('show');
			}

			$scope.confirmTrattatiMassivo = function(){
				let odgs = $scope.attiNonTrattati;
				if(odgs != undefined && odgs.length>0){
					for(var i = 0; i < odgs.length; i++){
						$scope.trattato(odgs[i]);
					}
				}
			}

			$scope.trattato = function (item) {
				let idOdg = item.id;
				if(idOdg != null){
					let idOdgs = [];
					idOdgs.push(idOdg);
					$scope.param={};
					$scope.param.attoOdgId=idOdgs;
					OrdineGiorno.reimpostatrattati($scope.param,function(result){
						if(result){
							item.esito = null;
							$scope.attiNonTrattati = [];
							ngToast.dismiss();
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$('#ripristinaAttiNonTrattatiConfirmation').modal('hide');
							$( "input[name^='selectAll']" ).prop('checked', false);
							$scope.refresh();
							$scope.taskLoading = false;
						}
					})
				}
				//$log.debug("AttiOdg",$scope.sedutaGiunta.odgs[0].attos);
			};

			$scope.filtraTrattati = function (item) {
				return (item.esito !== SedutaGiuntaConstants.esitoAttoOdg.nonTrattato || item.esito == undefined);
			};

			$scope.filtraNonTrattati = function (item) {
				return item.esito === SedutaGiuntaConstants.esitoAttoOdg.nonTrattato && !item.dataConfermaEsito;
			};

			$scope.registraEsito = function(seduta,atto){
				seduta.selected = [atto];
				$scope.confermaEsito = null;
				$scope.apriRegistraEsitoMassivo(seduta, 0);
			}

			$scope.attiResoconto =  function(odgs){
				let atti = [];
				// add atti odg base
				angular.forEach(odgs,function(odg){
					let isAttoOdgBase = (odg.tipoOdg.id == 1 || odg.tipoOdg.id == 2) ? true:false;
					// add atti odg base
					if(isAttoOdgBase){
						angular.forEach(odg.attos,function(atto){
							if(atto.esito != SedutaGiuntaConstants.esitoAttoOdg.nonTrattato){
								atti.push(atto);
							}
						})
					}
				});
				// add atti odg suppletivo
				angular.forEach(odgs,function(odg){
					let isAttoOdgSuppletivo = odg.tipoOdg.id == 3 ? true:false;
					if(isAttoOdgSuppletivo){
						angular.forEach(odg.attos,function(atto){
							// Escludo dal resoconto gli atti non trattati
							if(atto.esito != SedutaGiuntaConstants.esitoAttoOdg.nonTrattato){
								atti.push(atto);
							}
						})
					}
				});
				// add atti odg fuorisacco
				angular.forEach(odgs,function(odg){
					let isAttoOdgFuoriSacco = odg.tipoOdg.id == 4 ? true:false;
					if(isAttoOdgFuoriSacco){
						angular.forEach(odg.attos,function(atto){
							// Escludo dal resoconto gli atti non trattati
							if(atto.esito != SedutaGiuntaConstants.esitoAttoOdg.nonTrattato){
								atti.push(atto);
							}
						})
					}
				})

				let attiOrdered = true;
				angular.forEach(atti,function(attoOdg){
					if(angular.isUndefined(attoOdg.numeroDiscussione) || attoOdg.numeroDiscussione === null ) {
						attiOrdered = false;
						return;
					}
				})

				if(!attiOrdered){
					let numeroDiscussioneIdx = 1;
					//assegno un numero discussione
					angular.forEach(atti,function(attoOdg){
						attoOdg.numeroDiscussione = numeroDiscussioneIdx;
						numeroDiscussioneIdx++;
					})
				}
				$scope.attiResocontoList = atti;
				return atti;
			}

			$scope.confirmSpostaAltraSeduta = function(atto,odg){
				$scope.attiNonTrattati = [atto];
				$('#spostaAltraSedutaConfirmation').modal('show');
			}

			$scope.confirmSpostaAltraSedutaMassivo = function(odg){
				$scope.attiNonTrattati = odg.selected;
				$('#spostaAltraSedutaConfirmation').modal('show');
			}

			$scope.spostaAltraSedutaMassivo = function(){
				let odgs = $scope.attiNonTrattati;
				if(odgs != undefined && odgs.length>0){
					for(var i = 0; i < odgs.length; i++){
						$scope.spostaAltraSeduta(odgs[i]);
					}
					$('#spostaAltraSedutaConfirmation').modal('hide');
					$( "input[name^='selectAll']" ).click();
					$( "input[name^='selectAll']" ).prop('checked', false);
				}
			}


			$scope.spostaAltraSeduta = function (item) {
				let idOdg = item.id;
				if(idOdg != null){
					let attoOdgId = {'attoOdgId': idOdg};
					OrdineGiorno.confermaesito(attoOdgId,function(result){
						item.dataConfermaEsito = result.data;
						$scope.attiNonTrattati = [];
						ngToast.dismiss();
						ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
						$('#resocontoAtto').modal('hide');
						$scope.refresh();
						$scope.taskLoading = false;
					});
				}
			};

			$scope.showEditNumeroSeduta = function(){
				$scope.nuovoNumeroSeduta = Number($scope.sedutaGiunta.numero);
				$('#editNumeroSedutaModal').modal('show');
			};

			$scope.confirmEditNumeroSeduta = function(){
				if($scope.nuovoNumeroSeduta && Number($scope.nuovoNumeroSeduta) > 0){
					SedutaGiunta.editNumeroSeduta({'id': $scope.sedutaGiunta.id, 'newNumeroSeduta': $scope.nuovoNumeroSeduta}, null, function(res){
							$scope.refresh();
							ngToast.create(  { className: 'success', content: 'Salvataggio effettuato con successo' } );
							$('#editNumeroSedutaModal').modal('hide');
							$scope.taskLoading = false;
						}, function(err){
							// $scope.refresh();
							$scope.taskLoading = false;
						}
					);
				}
				else {
					ngToast.create({className: 'error', content: 'Inserire un numero valido'});
				}
			}

			$scope.init();


		});
angular.module('cifra2gestattiApp')
	.directive('showtabOdgBaseOrdinario',  function () {
		return {
			restrict: 'A',
			link: function (scope, element, attrs) {
				// Save active tab to localStorage
				scope.setActiveTab = function (activeTab) {
					sessionStorage.setItem("activeTab", activeTab);
				};
				// Get active tab from localStorage
				scope.getActiveTab = function () {
					return sessionStorage.getItem("activeTab");
				};
				scope.tabIndex = scope.getActiveTab() != undefined ? scope.getActiveTab() : 'tab-0';
				if(scope.tabIndex == 'tab-1'){
					$('#tab-1').show();
					setTimeout(function(){
						$('#tab-0').hide();
						$('#tab-1-header').click();
					}, 1);
				}else{
					$('#tab-0').show();
					setTimeout(function(){
						$('#tab-1').hide();
						$('#tab-0-header').click();
					}, 1);
				}

				//scope.setActiveTab(scope.tabIndex)
				element.bind('click', function(e) {
					e.preventDefault();
					$(element).tab('show');
					let currentTab = e.target.name;
					scope.tabIndex = currentTab != undefined ? currentTab : scope.getActiveTab();
					scope.setActiveTab(scope.tabIndex);
					//reset dei checkbox al cambio tab
					if(scope.odg == undefined){
						scope.$parent.odgSelected = [];
					}else{
						scope.odg.selected = [];
					}
					if(scope.seduta){
						scope.seduta.selected = [];
					}
					$( "input[name^='selectAll']" ).prop('checked', false);
					if(scope.tabIndex == 'tab-0'){
						$('#tab-1').hide();
					}else if(scope.tabIndex == 'tab-1'){
						$('#tab-0').hide();
					}
				});
			}
		};
	});
angular.module('cifra2gestattiApp')
	.filter('orderObjectBy', function() {
		return function(items, field, reverse) {
			var filtered = [];
			angular.forEach(items, function(item) {
				filtered.push(item);
			});
			filtered.sort(function (a, b) {
				return (a[field] > b[field] ? 1 : -1);
			});
			if(reverse) filtered.reverse();
			return filtered;
		};
	});