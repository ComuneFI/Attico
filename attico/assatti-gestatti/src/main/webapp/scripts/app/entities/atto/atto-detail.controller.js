'use strict';

angular.module('cifra2gestattiApp')
	.controller('AttoDetailController',
		['$scope', '$controller', 'moment', 'Atto', 'Parere', 'EsitoPareri','TipoFinanziamento','Profilo', 'SottoMateria', 'Materia', 'DateServer',
			'TipoMateria', 'TipoAtto', 'ArgomentoOdg', 'TipoAdempimento', 'Aoo', 'Assessorato', 'Utente', 'Ufficio', 'TipoIter', 'SedutaGiunta',
			'ParseLinks','$log','$anchorScroll',  'SottoscrittoreAtto', '$stateParams',
			'$state' , 'Upload', '$http', '$rootScope','TipoMateriaService', 'ConvertService',
			'ModelloCampo', 'ModelloHtml','DocumentoInformatico', 'Diogene',
			'ngToast','TipoDeterminazione', 'TipoIterService', 'RubricaDestinatarioEsterno','AmbitoDl33','MateriaDl33',
			'Macro_cat_obbligo_dl33', 'AnagraficaObbligoDlService','TaskDesktop', 'AmbitoDl33Service' ,'Lavorazione', 'ConfigurazioneTask', 'QualificaProfessionale',
			'Avanzamento', 'CategoriaEvento', 'Movimento', 'Preview', 'RoleCodes', 'sharedSedutaFactory', 'StatiRispostaCodes', 'TerminiRispostaCodes', 'BpmSeparator', '$timeout','$filter', '$location', '$q',
			function ($scope, $controller, moment, Atto, Parere, EsitoPareri,TipoFinanziamento, Profilo, SottoMateria, Materia, DateServer,
					  TipoMateria, TipoAtto, ArgomentoOdg, TipoAdempimento, Aoo, Assessorato, Utente, Ufficio, TipoIter, SedutaGiunta,
					  ParseLinks,$log,$anchorScroll,  SottoscrittoreAtto, $stateParams,
					  $state, Upload, $http, $rootScope,TipoMateriaService, ConvertService,
					  ModelloCampo, ModelloHtml,DocumentoInformatico, Diogene,
					  ngToast,TipoDeterminazione, TipoIterService, RubricaDestinatarioEsterno,AmbitoDl33,MateriaDl33,
					  Macro_cat_obbligo_dl33, AnagraficaObbligoDlService,TaskDesktop, AmbitoDl33Service ,Lavorazione, ConfigurazioneTask, QualificaProfessionale,
					  Avanzamento, CategoriaEvento, Movimento, Preview, RoleCodes, sharedSedutaFactory, StatiRispostaCodes, TerminiRispostaCodes, BpmSeparator, $timeout,$filter, $location, $q) {

				$scope.newEditorVersion = false;

				// instantiate base controller
				$controller('AttoBaseDetailController', { $scope: $scope });

				$scope.viewImportBeneficiarioFromRubrica = function(){
					$scope.rubricaBeneficiarioToImport = {};
					$('#rubricaBeneficiariModal').modal('show');
				};

				$scope.tuttiEsitoPareriCommissioni =  [];
				$scope.tuttiEsitoPareriRespTecnico =  [];
				$scope.tuttiEsitoPareriRespContabile = [] ;
				$scope.tuttiEsitoPareriQuartieri =  [];

				EsitoPareri.query({tipo:'Commissione'}, function(result){

					for(var i = 0; i<result.length; i ++){
						var app = result[i];
						if(app.tipo=='Commissione'){
							$scope.tuttiEsitoPareriCommissioni.push(app);
						}

					}
				});
				
				$scope.tipoFinanziamentos = TipoFinanziamento.getAllEnabled(function(result) {
		        	
		        });

				EsitoPareri.query({tipo:'Resp. Contabile'}, function(result){

					for(var i = 0; i<result.length; i ++){
						var app = result[i];
						if(app.tipo.startsWith('Resp. Contabile')){
							$scope.tuttiEsitoPareriRespContabile.push(app);
						}

					}
				});

				EsitoPareri.query({tipo:'Resp. Tecnico'}, function(result){

					for(var i = 0; i<result.length; i ++){
						var app = result[i];
						if(app.tipo.startsWith('Resp. Tecnico')){
							$scope.tuttiEsitoPareriRespTecnico.push(app);
						}

					}
				});

				EsitoPareri.query({tipo:'Quartieri e Rev. Contabili'}, function(result){

					for(var i = 0; i<result.length; i ++){
						var app = result[i];
						if(app.tipo=='Quartieri e Rev. Contabili'){
							$scope.tuttiEsitoPareriQuartieri.push(app);
						}

					}
				});

				$scope.getDescrizioneStatoRisposta = function(codice){
					var r = "";
					if(StatiRispostaCodes!=null && StatiRispostaCodes.length > 0){
						for(var i = 0; i < StatiRispostaCodes.length; i++){
							if(StatiRispostaCodes[i] && StatiRispostaCodes[i].codice == codice){
								r = StatiRispostaCodes[i].descrizione;
								break;
							}
						}
					}
					return r;
				};

				$scope.getEsitoParereLeggibile = function(codice){

					var ret = "";

					if($scope.tuttiEsitoPareriCommissioni){
						for(var i = 0; i<$scope.tuttiEsitoPareriCommissioni.length; i++){
							if($scope.tuttiEsitoPareriCommissioni[i].codice == codice){
								ret = $scope.tuttiEsitoPareriCommissioni[i].valore;
								break;
							}
						}
					}
					if (!ret){
						if($scope.tuttiEsitoPareriQuartieri){
							for(var i = 0; i<$scope.tuttiEsitoPareriQuartieri.length; i++){
								if($scope.tuttiEsitoPareriQuartieri[i].codice == codice){
									ret = $scope.tuttiEsitoPareriQuartieri[i].valore;
									break;
								}
							}
						}
					}
					if(!ret){
						if($scope.tuttiEsitoPareriRespContabile){
							for(var i = 0; i<$scope.tuttiEsitoPareriRespContabile.length; i++){
								if($scope.tuttiEsitoPareriRespContabile[i].codice == codice){
									ret = $scope.tuttiEsitoPareriRespContabile[i].valore;
									break;
								}
							}
						}
					}
					if(!ret){
						if($scope.tuttiEsitoPareriRespTecnico){
							for(var i = 0; i<$scope.tuttiEsitoPareriRespTecnico.length; i++){
								if($scope.tuttiEsitoPareriRespTecnico[i].codice == codice){
									ret = $scope.tuttiEsitoPareriRespTecnico[i].valore;
									break;
								}
							}
						}
					}
					return ret;
				}

				$scope.listaAoo = [];
				$scope.profilosComponentiConsiglio = [];
				$scope.profilosComponentiGiunta = [];
				$scope.visibilitaCampi = {};

				/*
	$scope.settingListAoo = function(){
		$scope.listaAoo = [];
		 if($scope.atto && $scope.atto.aoo){
			 for(var i = 0; i<$scope.tutteLeAoo.length; i++ ){
				 if($scope.tutteLeAoo[i].id != $scope.atto.aoo.id){
					 $scope.listaAoo.push($scope.tutteLeAoo[i]);
				 }
			 }
		 }
	};
	*/

				//$scope.aooAttive = Aoo.getAllEnabled();
				$scope.aooAttive = [];

				/*
	if($scope.tutteLeAoo == undefined){
		 var deferred = $q.defer();
		 $scope.tutteLeAoo = deferred.promise;
		 Aoo.getMinimal({}, function(aoos){
			 deferred.resolve();
			 $scope.tutteLeAoo = aoos;
			 $scope.aooAttive = $scope.tutteLeAoo.filter(function(aoo){
				 return aoo && (!aoo.validita || !aoo.validita.validoal);
			 });
		 });
	 }else if (typeof $scope.tutteLeAoo.then === 'function') {
		 $scope.tutteLeAoo.then(function() {
			 $scope.aooAttive = $scope.tutteLeAoo.filter(function(aoo){
				 return aoo && (!aoo.validita || !aoo.validita.validoal);
			 });
		 });
	 }
	*/

				$scope.$watch(function(scope) { return angular.isDefined(scope.atto) ? scope.atto.tipoDeterminazione : undefined; }, function(newValue,oldValue,scope){
					if($scope.atto!=undefined && $scope.atto.motivoClonazione!=undefined){
						if($scope.atto.motivoClonazione.toLowerCase() != 'revoca' && oldValue && oldValue.id && oldValue.descrizione.toLowerCase() != 'ordinario' && (!newValue || !newValue.id || newValue.descrizione.toLowerCase() == 'ordinario')){
							$scope.atto.attoRevocatoId = null;
							$scope.atto.codicecifraAttoRevocato = null;
							$scope.atto.numeroAdozioneAttoRevocato = null;
							$scope.atto.dataAdozioneAttoRevocato = null;
						}
					}
				},true);

				$scope.aooSottoscrittore = {};
				$scope.uploadok = false;
				$scope.isPubblicazione = false;
				$scope.visibilitaSezioni = {};
				$scope.incarichi = [];
				$scope.risponste = [];
				$scope.incarichiAssegnati = [];
				$scope.assegnazioneIncarichi = null;
				$scope.configurazioneIncarichi = null;

				$scope.firma = true;
				$scope.visErrMovimenti = false;

				$scope.statiRispostaCodes = StatiRispostaCodes;
				$scope.terminiRispostaCodes = TerminiRispostaCodes;
				$scope.bpmSeparator = BpmSeparator;

				$scope.updateDataScadenzaRisposta = function(){
					if (angular.isDefined($scope.dtoWorkflow) && angular.isDefined($scope.dtoWorkflow.parere) &&
						angular.isDefined($scope.dtoWorkflow.parere.terminiPresentazione)  &&
						angular.isDefined($scope.atto.dataRicevimento)) {

						for (var cntr = 0; cntr < $scope.terminiRispostaCodes.length; cntr++) {
							var termCur = $scope.terminiRispostaCodes[cntr];
							if ($scope.dtoWorkflow.parere.terminiPresentazione === termCur.codice) {
								var parts = $scope.atto.dataRicevimento.split('-');
								var dateScad = new Date(parts[0], parts[1]-1, parts[2]);
								$scope.dtoWorkflow.parere.dataScadenza = new Date(dateScad.getTime() + (termCur.ggScadenza * 24 *60 * 60 * 1000));
							}
						}
					}
				};
				$scope.updateParereCommissione = function(){
					if (angular.isDefined($scope.dtoWorkflow) && angular.isDefined($scope.dtoWorkflow.parere) &&
						angular.isDefined($scope.dtoWorkflow.parere.parereSintetico)  ) {

						if($scope.dtoWorkflow.parere.parereSintetico && $scope.dtoWorkflow.parere.parereSintetico.toLowerCase().indexOf('personalizzato')<0){
							$scope.dtoWorkflow.parere.parerePersonalizzato = "";
						}
					}
				};

				$scope.updateParereRespTecnico = function(){
					if (angular.isDefined($scope.dtoWorkflow) && angular.isDefined($scope.dtoWorkflow.parere) &&
						angular.isDefined($scope.dtoWorkflow.parere.parereSintetico)  ) {

						if($scope.dtoWorkflow.parere.parereSintetico && $scope.dtoWorkflow.parere.parereSintetico.toLowerCase().indexOf('articolato')<0){
							$scope.dtoWorkflow.parere.parerePersonalizzato = "";
						}
					}
				};

				$scope.updateParereRespContabile = function(){
					if (angular.isDefined($scope.dtoWorkflow) && angular.isDefined($scope.dtoWorkflow.parere) &&
						angular.isDefined($scope.dtoWorkflow.parere.parereSintetico)  ) {

						if($scope.dtoWorkflow.parere.parereSintetico && $scope.dtoWorkflow.parere.parereSintetico.toLowerCase().indexOf('articolato')<0){
							$scope.dtoWorkflow.parere.parerePersonalizzato = "";
						}
					}
				};

				var qualificaProfessionaleDto = {
					"id": null,
					"denominazione": '',
					"enabled": true
				};
				var configurazioneIncaricoAooDto = {
					"idConfigurazioneIncarico":null,
					"idAoo": null,
					"giorniScadenza": null,
					"ordineFirma": 0,
					"qualificaProfessionaleDto": angular.copy(qualificaProfessionaleDto)
				};
				var configurazioneIncaricoProfiloDto = {
					"idConfigurazioneIncarico":null,
					"idProfilo": null,
					"ordineFirma": 0,
					"qualificaProfessionaleDto": angular.copy(qualificaProfessionaleDto)
				};

				$scope.aggiungiProfiloIncarico = function(incarico){
					incarico.listConfigurazioneIncaricoProfiloDto.push(angular.copy(configurazioneIncaricoProfiloDto));
				}

				$scope.aggiungiUfficioIncarico = function(incarico){
					incarico.listConfigurazioneIncaricoAooDto.push(angular.copy(configurazioneIncaricoAooDto));
				}

				$scope.rimuoviProfiloIncarico = function(indexIncarico, indexProfilo){
					$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto.splice(indexProfilo, 1);
					if($scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto.length==0){
						$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto.push(angular.copy(configurazioneIncaricoProfiloDto));
					}
				}

				$scope.rimuoviUfficioIncarico = function(indexIncarico, indexUfficio){
					$scope.incarichi[indexIncarico].listConfigurazioneIncaricoAooDto.splice(indexUfficio, 1);
					if($scope.incarichi[indexIncarico].listConfigurazioneIncaricoAooDto.length==0){
						$scope.incarichi[indexIncarico].listConfigurazioneIncaricoAooDto.push(angular.copy(configurazioneIncaricoAooDto));
					}
				}

				$scope.onlyUsername = function(string){
					if(string){
						return string.split($scope.bpmSeparator.BPM_USERNAME_SEPARATOR)[0];
					}else{
						return "";
					}
				};

				$scope.onlyAooCode = function(bpmGroupName){
					if(bpmGroupName){
						var splitted = bpmGroupName.split($scope.bpmSeparator.BPM_ROLE_SEPARATOR);
						if (splitted.length > 1) {
							return splitted[1].split($scope.bpmSeparator.BPM_INCARICO_SEPARATOR)[0];
						}
					}

					return "";
				};

				$scope.onlyRoles = function(bpmGroupName){
					if(bpmGroupName){
						return bpmGroupName.split($scope.bpmSeparator.BPM_ROLE_SEPARATOR)[0];
					}

					return "";
				};

				$scope.unclaim = function(task){
					$scope.taskManaging = task;
					//if($scope.isDirigente){
					$('#unclameConfirm').modal('show');
					//}
				};

				$scope.dounclaim = function(taskId, attoId, indexSezioneCorrente){
					$rootScope.$broadcast('globalLoadingStart');
//		if($scope.isDirigente){
					TaskDesktop.unclaim({taskId:taskId}, function(result){
						$scope.incarichi = [];
						$scope.assegnazioneIncarichi = null;
						$('#unclameConfirm').modal('hide');
						$scope.taskManaging = {};
//				$scope.loadAll();
						$scope.taskLoading = false;
						if ($scope.taskBpm) {
							$scope.taskBpm.idAssegnatario = null;
						}
						$scope.load( $scope.atto.id, indexSezioneCorrente, $scope.taskBpmId);
						$rootScope.$emit("loadDettaglioTask");
					});
//		}
				};


				var inizializzaIncarico = function(incaricoAssegnato, i){
					var incarico = {};

					incarico.id = (incaricoAssegnato!=null)?incaricoAssegnato.id:null;
					incarico.dettaglio = $scope.assegnazioneIncarichi[i];
					incarico.idAtto = $scope.atto.id;
					incarico.isValid = true;
					incarico.qualifiche = [];
					incarico.idConfigurazioneTask = $scope.assegnazioneIncarichi[i].configurazioneTaskDto.idConfigurazioneTask;
					incarico.listConfigurazioneIncaricoAooDto=[];
					incarico.listConfigurazioneIncaricoProfiloDto=[];

					if(incarico.dettaglio.configurazioneTaskDto.tipoConfigurazioneTaskId==$rootScope.configurationParams.tipo_conf_task_profilo_id){
						if(incaricoAssegnato!=null){
							if(incaricoAssegnato.listConfigurazioneIncaricoProfiloDto!=null && incaricoAssegnato.listConfigurazioneIncaricoProfiloDto.length>0){
								incarico.listConfigurazioneIncaricoProfiloDto=incaricoAssegnato.listConfigurazioneIncaricoProfiloDto;
							} else {
								incarico.listConfigurazioneIncaricoProfiloDto.push(angular.copy(configurazioneIncaricoProfiloDto));
							}
						} else {
							$scope.aggiungiProfiloIncarico(incarico);
						}
					}

					if(incarico.dettaglio.configurazioneTaskDto.tipoConfigurazioneTaskId==$rootScope.configurationParams.tipo_conf_task_ufficio_id){
						if(incaricoAssegnato!=null){
							if(incaricoAssegnato.listConfigurazioneIncaricoAooDto!=null && incaricoAssegnato.listConfigurazioneIncaricoAooDto.length>0){
								incarico.listConfigurazioneIncaricoAooDto=incaricoAssegnato.listConfigurazioneIncaricoAooDto;
							} else {
								incarico.listConfigurazioneIncaricoAooDto.push(angular.copy(configurazioneIncaricoAooDto));
							}
						} else {
							$scope.aggiungiUfficioIncarico(incarico);
						}
					}
					return incarico;
				}



				$scope.checkWarnDatePubblicazione = function(){
					return $scope.atto.dataInizioPubblicazionePresunta && $scope.atto.dataFinePubblicazionePresunta && new Date($scope.atto.dataInizioPubblicazionePresunta).getTime()>new Date($scope.atto.dataFinePubblicazionePresunta).getTime();
				};

				$scope.safeApply = function(fn) {
					var phase = this.$root.$$phase;
					if(phase == '$apply' || phase == '$digest') {
						if(fn && (typeof(fn) === 'function')) {
							fn();
						}
					} else {
						this.$apply(fn);
					}
				};

				$scope.usoesclusivo = [{valore: 'nessuno',label:'Nessuno'},{valore:'sanita',label:'Controllo della giunta ad uso esclusivo della Sanità'},{valore:'avvocatura',label:'Argomento dell\'ODG ad uso esclusivo dell\'Avvocatura'}];
				$scope.canChangeSDL = false;
				$scope.tipoattos = [];
				$scope.argomentiodg = [];

				ArgomentoOdg.getAll(function(result){
					$log.debug("Argomenti ODG:",result);

					$scope.argomentiodg = result;
				});

				$scope.thereisNext = function(currenteSectionIndex){
					var thereIs = false;
					if(Number(currenteSectionIndex) + 1 == $scope.attoDirigenzialeSezioni.length){
						thereIs = false;
					}else{
						for(var i = Number(currenteSectionIndex) + 1; i<$scope.attoDirigenzialeSezioni.length; i++){
							if($scope.attoDirigenzialeSezioni[i].abilitato){
								thereIs = true;
							}
						}
						if(thereIs != true){
							thereIs = false;
						}
					}
					return thereIs;
				};

				$scope.resetDatiArgomenti = function(entity){
					$log.debug("resetDatiArgomenti",entity);
					if(entity.usoEsclusivo != 'avvocatura'){
						$log.debug(entity.usoEsclusivo);
						entity.argomentoOdg = null;
					}

				}

				$scope.thereisPrevious = function(currenteSectionIndex){
					var thereIs = false;
					if(Number(currenteSectionIndex) == 0){
						thereIs = false;
					}else{
						for(var i = Number(currenteSectionIndex) - 1; i>-1; i--){
							if($scope.attoDirigenzialeSezioni[i].abilitato){
								thereIs = true;
							}
						}
						if(thereIs != true){
							thereIs = false;
						}
					}
					return thereIs;
				};

//	$scope.isSezioneVisibile = function(sezioneAtto){
//		var result = false;
//		for(var x in $scope.sezioniAtto){
//			if($scope.sezioniAtto[x].codice.toLowerCase()===sezioneAtto.target.toLowerCase()){
//				return (sezioneAtto.visibile && $scope.sezioniAtto[x].visibile);
//			}
//		}
//		return result;
//	}

				$scope.isSezioneVisibile = function(sezioneAtto){
					var result = false;
					for(var x in $scope.sezioniAtto){
						if($scope.sezioniAtto[x].codice.toLowerCase()===sezioneAtto.target.toLowerCase()){
							result = sezioneAtto.visibile && $scope.sezioniAtto[x].visibile;
							$scope.visibilitaSezioni[sezioneAtto.target.toLowerCase()]=result;
							return result;
						}
					}
					return result;
				}

				$scope.isVisibleRiferimentinormativi = function(){
					for(var x in $scope.sezioniAtto){
						if($scope.sezioniAtto[x].codice.toLowerCase()==='riferimentinormativi'){
							if( $rootScope.configurationParams.section_riferimentinormativi_enabled && $scope.sezioniAtto[x].visibile){
								return true;
							} else {
								return false;
							}
						}
					}
				}

				var convertIncarichiDto = function(){
					var incarichiDto = angular.copy($scope.incarichi);
					for ( var n in incarichiDto) {
						delete incarichiDto[n].dettaglio;
						delete incarichiDto[n].qualifiche;
						if(incarichiDto[n].listConfigurazioneIncaricoProfiloDto!=undefined || incarichiDto[n].listConfigurazioneIncaricoProfiloDto!=null || incarichiDto[n].listConfigurazioneIncaricoProfiloDto.length>0){
							for ( var i in incarichiDto[n].listConfigurazioneIncaricoProfiloDto) {
								if( incarichiDto[n].listConfigurazioneIncaricoProfiloDto[i].idProfilo==null ){
									incarichiDto[n].listConfigurazioneIncaricoProfiloDto.splice(i,1);
								}
							}
							for ( var i in incarichiDto[n].listConfigurazioneIncaricoProfiloDto) {
								incarichiDto[n].listConfigurazioneIncaricoProfiloDto[i].ordineFirma=i;
							}
						}
						if(incarichiDto[n].listConfigurazioneIncaricoAooDto!=undefined || incarichiDto[n].listConfigurazioneIncaricoAooDto!=null || incarichiDto[n].listConfigurazioneIncaricoAooDto.length>0){
							for ( var i in incarichiDto[n].listConfigurazioneIncaricoAooDto) {
								if( incarichiDto[n].listConfigurazioneIncaricoAooDto[i].idAoo==null ){
									incarichiDto[n].listConfigurazioneIncaricoAooDto.splice(i,1);
								}
							}
							for ( var i in incarichiDto[n].listConfigurazioneIncaricoAooDto) {
								incarichiDto[n].listConfigurazioneIncaricoAooDto[i].ordineFirma=i;
							}
						}
					}
					return incarichiDto;
				}


				var convertModelloHtmlDtoToIds = function(){
					var modelliHTMLIds = [];

					for(var j = 0; j<$scope.modelloHtml.length; j ++){
						var appModello = $scope.modelloHtml[j];
						modelliHTMLIds.push(appModello.idModelloHtml);

					}


					return modelliHTMLIds;
				}


				$scope.inizializzaParere = function(decisione){
					$scope.dtoWorkflow.parere = {
						atto: $scope.atto,
						aoo: $rootScope.profiloattivo.aoo,
						profilo: $rootScope.profiloattivo,
						data: new Date(),
						tipoAzione: {codice:decisione.tipoParere}
					}
				}
				$scope.visualizzaAllegati = 0;

				$scope.saveParerePerInserimentoAllegati  = function(){
					Parere.save($scope.dtoWorkflow.parere,
						function (json) {
							$scope.dtoWorkflow.parere.id = json.parereId;
							$scope.dtoWorkflow.parere.createdBy = $rootScope.account.login;
							$scope.visualizzaAllegati = json.parereId;
							$scope.refresh();
						});
					$scope.inizializzaParereCommissione();
				}

				$scope.inizializzaRispostaAttiDiConsiglio = function(decisione){

					//se non valorizzato
					if(!$scope.dtoWorkflow.parere){

						//lo riprendo da db
						if($scope.atto.pareri){
							for(var i=0; !$scope.dtoWorkflow.parere &&  i<$scope.atto.pareri.length; i++ ) {
								var p = $scope.atto.pareri[i];
								if (p.annullato) {
									continue;
								}

								if(p.origine == "R"){
									$scope.dtoWorkflow.parere = p;
									$scope.dtoWorkflow.parere.data = new Date(p.data);
								}
							}
						}

						//se non c'è su db lo inizializzo
						if(!$scope.dtoWorkflow.parere){

							if(decisione){
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									//aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:decisione.tipoParere},
									origine:"R"}
							} else {
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									//aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:'inserimento_risposta'},
									origine:"R"}
							}

//				Parere.save($scope.dtoWorkflow.parere,
//		                 function (result) {
//							$scope.dtoWorkflow.parere = result;
//		                     $scope.refresh();
//		                 });

						}
					}

				}



				$scope.initListaEsitoPareriCommissioni = function(){
					if(!$scope.listaEsitoPareriCommissioniReq && !$scope.listaEsitoPareriCommissioni || $scope.listaEsitoPareriCommissioni.length < 1){
						$scope.listaEsitoPareriCommissioniReq = true;
						EsitoPareri.query({tipo:'Commissione'}, function(result){
							$scope.listaEsitoPareriCommissioni =  [{codice: '', valore:'Selezionare un parere'}];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Commissione' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriCommissioni.push(app);
								}

							}
						});
					}
				}

				$scope.initListaEsitoPareriRespTecnico = function(){
					if(!$scope.listaEsitoPareriRespTecnicoReq && !$scope.listaEsitoPareriRespTecnico || $scope.listaEsitoPareriRespTecnico.length < 1){
						$scope.listaEsitoPareriRespTecnicoReq = true;
						EsitoPareri.query({tipo:'Resp. Tecnico'}, function(result){
							$scope.listaEsitoPareriRespTecnico =  [{codice: '', valore:'Selezionare un parere'}];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Resp. Tecnico' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriRespTecnico.push(app);
								}

							}
						});
					}
				}
				
				$scope.initListaEsitoPareriRespTecnicoRegMod = function(){
					if(!$scope.listaEsitoPareriRespTecnicoReq && !$scope.listaEsitoPareriRespTecnico || $scope.listaEsitoPareriRespTecnico.length < 1){
						$scope.listaEsitoPareriRespTecnicoReq = true;
						EsitoPareri.query({tipo:'Resp. Tecnico Reg. Mod.'}, function(result){
							$scope.listaEsitoPareriRespTecnico =  [{codice: '', valore:'Selezionare un parere'}];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Resp. Tecnico Reg. Mod.' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriRespTecnico.push(app);
								}

							}
						});
					}
				}

				$scope.initListaEsitoPareriRespContabile = function(){
					if(!$scope.listaEsitoPareriRespContReq && !$scope.listaEsitoPareriRespContabile || $scope.listaEsitoPareriRespContabile.length < 1){
						$scope.listaEsitoPareriRespContReq = true;
						EsitoPareri.query({tipo:'Resp. Contabile'}, function(result){
							$scope.listaEsitoPareriRespContabile =  [{codice: '', valore:'Selezionare un parere'}];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Resp. Contabile' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriRespContabile.push(app);
								}

							}
						});
					}
				}
				
				$scope.initListaEsitoPareriRespContabileRegMod = function(){
					if(!$scope.listaEsitoPareriRespContReq && !$scope.listaEsitoPareriRespContabile || $scope.listaEsitoPareriRespContabile.length < 1){
						$scope.listaEsitoPareriRespContReq = true;
						EsitoPareri.query({tipo:'Resp. Contabile Reg. Mod.'}, function(result){
							$scope.listaEsitoPareriRespContabile =  [{codice: '', valore:'Selezionare un parere'}];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Resp. Contabile Reg. Mod.' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriRespContabile.push(app);
								}

							}
						});
					}
				}

				$scope.initListaEsitoPareriQuartieri = function(){
					if(!$scope.listaEsitoPareriQuartieriReq && !$scope.listaEsitoPareriQuartieri || $scope.listaEsitoPareriQuartieri.length < 1){
						$scope.listaEsitoPareriQuartieriReq = true;
						EsitoPareri.query({tipo:'Quartieri e Rev. Contabili'}, function(result){
							$scope.listaEsitoPareriQuartieri =  [];
							for(var i = 0; i<result.length; i ++){
								var app = result[i];
								if(app.tipo=='Quartieri e Rev. Contabili' && $scope.atto.tipoAtto.id==app.tipoAtto.id && app.enabled == true){
									$scope.listaEsitoPareriQuartieri.push(app);
								}

							}
						});
					}
				}

				$scope.inizializzaParereRespTecnico = function(decisione) {
					
					if("INSERIMENTO_PARERE_RESP_TECNICO" === decisione.codiceAzioneUtente){
						$scope.initListaEsitoPareriRespTecnico();
					}else{
						$scope.initListaEsitoPareriRespTecnicoRegMod();
					}
					


					//se non valorizzato
					if (!$scope.dtoWorkflow.parere || ($scope.dtoWorkflow.parere && $scope.dtoWorkflow.parere.tipoAzione
						&& $scope.dtoWorkflow.parere.tipoAzione.codice != decisione.tipoParere)) {
						$scope.dtoWorkflow.parere = null;
						//lo riprendo da db
						if ($scope.atto.pareri) {
							for (var i = 0; !$scope.dtoWorkflow.parere && i < $scope.atto.pareri.length; i++) {
								var p = $scope.atto.pareri[i];
								if (p.annullato || p.tipoAzione.codice != decisione.tipoParere || p.origine != decisione.origineParere) {
									continue;
								}
								if (p.profilo.id == $rootScope.profiloattivo.id) {
									$scope.dtoWorkflow.parere = p;
									$scope.dtoWorkflow.parere.id = p.id;
									$scope.dtoWorkflow.parere.data = new Date(p.data);
									$scope.dtoWorkflow.parere.dataScadenza = p.dataScadenza ? new Date(p.dataScadenza) : null;
								}
							}
						}
						//se non c'e' su db lo inizializzo
						if (!$scope.dtoWorkflow.parere) {
							$scope.dtoWorkflow.parere = {
								atto: $scope.atto,
								aoo: $rootScope.profiloattivo.aoo,
								profilo: $rootScope.profiloattivo,
								tipoAzione: {codice: decisione.tipoParere},
								origine: decisione.origineParere
							}
						}
					}
				}

				$scope.inizializzaParereRespContabile = function(decisione) {
					$scope.initListaEsitoPareriRespContabile();
					
					if("INSERIMENTO_PARERE_RESP_CONTABILE" === decisione.codiceAzioneUtente){
						$scope.initListaEsitoPareriRespContabile();
					}else{
						$scope.initListaEsitoPareriRespContabileRegMod();
					}

					//se non valorizzato
					if (!$scope.dtoWorkflow.parere || ($scope.dtoWorkflow.parere && $scope.dtoWorkflow.parere.tipoAzione
						&& $scope.dtoWorkflow.parere.tipoAzione.codice != decisione.tipoParere)) {
						$scope.dtoWorkflow.parere = null;
						//lo riprendo da db
						if ($scope.atto.pareri) {
							for (var i = 0; !$scope.dtoWorkflow.parere && i < $scope.atto.pareri.length; i++) {
								var p = $scope.atto.pareri[i];
								if (p.annullato || p.tipoAzione.codice != decisione.tipoParere || p.origine != decisione.origineParere) {
									continue;
								}
								if (p.profilo.id == $rootScope.profiloattivo.id) {
									$scope.dtoWorkflow.parere = p;
									$scope.dtoWorkflow.parere.id = p.id;
									$scope.dtoWorkflow.parere.data = new Date(p.data);
									$scope.dtoWorkflow.parere.dataScadenza = p.dataScadenza ? new Date(p.dataScadenza) : null;
								}
							}
						}
						//se non c'e' su db lo inizializzo
						if (!$scope.dtoWorkflow.parere) {
							$scope.dtoWorkflow.parere = {
								atto: $scope.atto,
								aoo: $rootScope.profiloattivo.aoo,
								profilo: $rootScope.profiloattivo,
								tipoAzione: {codice: decisione.tipoParere},
								origine: decisione.origineParere
							}
						}
					}
				}

				$scope.inizializzaParereCommissione = function(decisione){
					$scope.initListaEsitoPareriCommissioni();

					//se non valorizzato
					if(!$scope.dtoWorkflow.parere){

						//lo riprendo da db
						if($scope.atto.pareri){
							for(var i=0; !$scope.dtoWorkflow.parere &&  i<$scope.atto.pareri.length; i++ ) {
								var p = $scope.atto.pareri[i];
								if (p.annullato) {
									continue;
								}
								if(p.origine == "C" && p.profilo.id == $rootScope.profiloattivo.id){
									$scope.dtoWorkflow.parere = p;
									$scope.dtoWorkflow.parere.id = p.id;
									$scope.dtoWorkflow.parere.data = new Date(p.data);
									$scope.dtoWorkflow.parere.dataScadenza = new Date(p.dataScadenza);
									$scope.visualizzaAllegati = p.id;
								}
							}
						}

						//se non c'e' su db lo inizializzo
						if(!$scope.dtoWorkflow.parere){

							if(decisione){
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:decisione.tipoParere},
									origine:"C"}
							} else {
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:'parere_commissione'},
									origine:"C"}
							}

							if($scope.incarichiAssegnati){
								for (var indiceIncarichiAssegnati = 0; indiceIncarichiAssegnati<$scope.incarichiAssegnati.length ; indiceIncarichiAssegnati++) {
									var incaricoAssegnato = $scope.incarichiAssegnati[indiceIncarichiAssegnati];
									if(incaricoAssegnato.configurazioneTaskCodice=="DIR_SEGRETARIO_COMMISSIONE" && incaricoAssegnato.listConfigurazioneIncaricoAooDto){
										for (var indConfigurazioneIncaricoAooDto = 0; indConfigurazioneIncaricoAooDto<incaricoAssegnato.listConfigurazioneIncaricoAooDto.length ; indConfigurazioneIncaricoAooDto++) {

											var confIncaricoAooDto = incaricoAssegnato.listConfigurazioneIncaricoAooDto[indConfigurazioneIncaricoAooDto];
											if(confIncaricoAooDto.idAoo == $rootScope.profiloattivo.aoo.id && confIncaricoAooDto.dataScadenza){
												var parts = confIncaricoAooDto.dataScadenza.split('/');
												var dateScad = new Date(parts[2], parts[1]-1, parts[0]);
												$scope.dtoWorkflow.parere.dataScadenza = new Date(dateScad.getTime());
												if(confIncaricoAooDto.dataManuale){
													$scope.dtoWorkflow.parere.dataInvioACommissione = confIncaricoAooDto.dataManuale;
												}
												else{
													$scope.dtoWorkflow.parere.dataInvioACommissione = incaricoAssegnato.dataCreazione;
												}
											}
										}
									}
								}


							}


							$scope.visualizzaAllegati = 0;

						}	else{
							if($scope.incarichiAssegnati){
								for (var indiceIncarichiAssegnati = 0; indiceIncarichiAssegnati<$scope.incarichiAssegnati.length ; indiceIncarichiAssegnati++) {
									var incaricoAssegnato = $scope.incarichiAssegnati[indiceIncarichiAssegnati];
									if(incaricoAssegnato.configurazioneTaskCodice=="DIR_SEGRETARIO_COMMISSIONE" && incaricoAssegnato.listConfigurazioneIncaricoAooDto){
										for (var indConfigurazioneIncaricoAooDto = 0; indConfigurazioneIncaricoAooDto<incaricoAssegnato.listConfigurazioneIncaricoAooDto.length ; indConfigurazioneIncaricoAooDto++) {

											var confIncaricoAooDto = incaricoAssegnato.listConfigurazioneIncaricoAooDto[indConfigurazioneIncaricoAooDto];
											if(confIncaricoAooDto.idAoo == $rootScope.profiloattivo.aoo.id && confIncaricoAooDto.dataScadenza){
												var parts = confIncaricoAooDto.dataScadenza.split('/');
												var dateScad = new Date(parts[2], parts[1]-1, parts[0]);
												$scope.dtoWorkflow.parere.dataScadenza = new Date(dateScad.getTime());
												if(confIncaricoAooDto.dataManuale){
													$scope.dtoWorkflow.parere.dataInvioACommissione = confIncaricoAooDto.dataManuale;
												}
												else {
													$scope.dtoWorkflow.parere.dataInvioACommissione = incaricoAssegnato.dataCreazione;
												}
											}
										}
									}
								}


							}
						}
					}

					if (decisione && "PARERE_NON_ESPRESSO" === decisione.codiceAzioneUtente) {
						$scope.dtoWorkflow.parere.origine = "C";
						$scope.dtoWorkflow.parere.parereSintetico = 'non_espresso';
						$scope.dtoWorkflow.parere.parere = null;

					}
				}


				$scope.inizializzaParereConsQuartRevCont = function(decisione){

					//se non valorizzato
					if(!$scope.dtoWorkflow.parere){

						//lo riprendo da db
						if($scope.atto.pareri){
							for(var i=0; !$scope.dtoWorkflow.parere &&  i<$scope.atto.pareri.length; i++ ) {
								var p = $scope.atto.pareri[i];
								if (p.annullato) {
									continue;
								}
								if(p.origine == "Q" && p.profilo.id == $rootScope.profiloattivo.id){
									$scope.dtoWorkflow.parere = p;
									$scope.dtoWorkflow.parere.data = new Date();
									$scope.visualizzaAllegati = p.id;
								}
							}
						}

						//se non c'e' su db lo inizializzo
						if(!$scope.dtoWorkflow.parere){

							if(decisione){
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:decisione.tipoParere},
									origine:"Q",
									data: new Date()}
							} else {
								$scope.dtoWorkflow.parere = {
									atto: $scope.atto,
									aoo: $rootScope.profiloattivo.aoo,
									profilo: $rootScope.profiloattivo,
									tipoAzione: {codice:'parere_quartiere_revisori'},
									origine:"Q",
									data: new Date()}
							}
							$scope.visualizzaAllegati = 0;
//				Parere.save($scope.dtoWorkflow.parere,
//		                 function (result) {
//							$scope.dtoWorkflow.parere = result;
//		                     $scope.refresh();
//		                 });

						}
					}

				}

				$scope.inizializzaParereConSoloRelatore = function(decisione){

					//se non valorizzato
					if(!$scope.dtoWorkflow.parere){

						//lo riprendo da db

						if($scope.atto.pareri){
							for(var i=0; !$scope.dtoWorkflow.parere &&  i<$scope.atto.pareri.length; i++ ) {
								var p = $scope.atto.pareri[i];
								if (p.annullato) {
									continue;
								}
								if(p.origine == "REL"){
									$scope.dtoWorkflow.parere = p;
								}
							}
						}

						//se non c'è su db lo inizializzo
						if(!$scope.dtoWorkflow.parere){

							$scope.dtoWorkflow.parere = {
								atto: $scope.atto,
								aoo: $rootScope.profiloattivo.aoo,
								profilo: $rootScope.profiloattivo,
								data: new Date(),
								tipoAzione: {codice:decisione.tipoParere},
								origine:"REL"}

//				Parere.save($scope.dtoWorkflow.parere,
//		                 function (result) {
//							$scope.dtoWorkflow.parere = result;
//		                     $scope.refresh();
//		                 });

						}
					}
				}

				$scope.inializzaSezioni = function(){
					$scope.attoDirigenzialeSezioni = [
						{
							target: "datiIdentificativi",
							icon: "fa fa-qrcode",
							title: "Dati Identificativi",
							description: "Dati Identificativi",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_datiidentificativi_enabled,
							error: false
						},
						{
							target: "datiIdentificativiConsiglio",
							icon: "fa fa-qrcode",
							title: "Dati Identificativi",
							description: "Dati Identificativi",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_datiidentificativiconsiglio_enabled,
							error: false
						},
						{
							target: "datiIdentificativiVerbale",
							icon: "fa fa-qrcode",
							title: "Dati Identificativi",
							description: "Dati Identificativi",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_datiidentificativiverbale_enabled,
							error: false
						},
						{
							target: "assegnazioneIncarichi",
							icon: "fa fa-users",
							title: "Visti, Pareri e Firme",
							description: "Visti, Pareri e Firme",
							abilitato: false,
							visibile: $rootScope.configurationParams.section_assegnazioneincarichi_enabled,
							error: false
						},
						{
							target: "risposta",
							icon: "fa fa-users",
							title: "Risposta",
							description: "Risposta",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_risposta_enabled,
							error: false
						},
						{
							target: "sottoscrizioni",
							icon: "fa fa-question",
							title: "Sottoscrizioni",
							description: "Sottoscrizioni",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_sottoscrizioni_enabled,
							error: false
						},
						{
							target: "riferimentiNormativi",
							icon: "fa fa-gavel",
							title: "L.R.15/08",
							description: "L.R.15/08",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_riferimentinormativi_enabled,
							error: false
						},
						{
							target: "preambolo",
							icon: "fa fa-flag",
							title: "Preambolo",
							description: "Preambolo",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_preambolo_enabled,
							error: false
						},
						{
							target: "motivazione",
							icon: "fa fa-question",
							title: "Motivazione",
							description: "Motivazione",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_motivazione_enabled,
							error: false
						},
						{
							target: "garanzieRiservatezza",
							icon: "fa fa-lock",
							title: "Garanzie alla Riservatezza",
							description: "Garanzia alla Riservatezza",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_garanzieriservatezza_enabled,
							error: false
						},
						{
							target: "schede",
							icon: "fa fa-credit-card",
							title: "Beneficiari",
							description: "Beneficiari",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_schede_enabled,
							error: false
						},
						{
							target: "dichiarazioni",
							icon: "fa fa-money",
							title: "Sezione Contabile",
							description: "Sezione Contabile",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_dichiarazioni_enabled,
							error: false
						},
						{
							target: "dispositivo",
							icon: "fa fa-gear",
							title: "Dispositivo",
							description: "Dispositivo",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_dispositivo_enabled,
							error: false
						},
						{
							target: "domanda",
							icon: "fa fa-flag",
							title: "Testo",
							description: "Testo",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_domanda_enabled,
							error: false
						},
						{
							target: "allegati",
							icon: "fa fa-newspaper-o",
							title: "Allegati",
							description: "Allegati",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_allegati_enabled,
							error: false
						},
						{
							target: "trasparenza",
							icon: "fa fa-eraser",
							title: "DLGS33/2013",
							description: "DlGs 33/13",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_trasparenza_enabled,
							error: false
						},
						{
							target: "notifica",
							icon: "fa fa-envelope",
							title: "Notifica",
							description: "Notifica",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_notifica_enabled,
							error: false
						},
						{
							target: "archivio",
							icon: "fa fa-archive",
							title: "Archivistici",
							description: "Archivistici",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_archivio_enabled,
							error: false
						},
						{
							target: "sistemiRegionali",
							icon: "fa fa-connectdevelop",
							title: "Altri sistemi regionali",
							description: "Altri sistemi regionali",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_sistemiregionali_enabled,
							error: false
						},
						{
							target: "note",
							icon: "fa fa-comments-o ",
							title: "Note",
							description: "Note",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_note_enabled,
							error: false
						},
						{
							target: "documentiPdf",
							icon: "fa fa-file-pdf-o",
							title: "Documenti",
							description: "Documenti",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_documentipdf_enabled,
							error: false
						},
						{
							target: "documentiPdfConsiglio",
							icon: "fa fa-file-pdf-o",
							title: "Documenti",
							description: "Documenti",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_documentipdfconsiglio_enabled,
							error: false
						},
						{
							target: "parere",
							icon: "fa fa-file-text-o",
							title: "Pareri",
							description: "Parere",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_parere_enabled,
							error: false
						},
						{
							target: "divulgazione",
							icon: "fa fa-exchange",
							title: "Pubblicazione",
							description: "Pubblicazione",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_divulgazione_enabled,
							error: false
						},
						{
							target: "divulgazioneSemplificata",
							icon: "fa fa-exchange",
							title: "Pubblicazione",
							description: "Pubblicazione",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_divulgazionesemplificata_enabled,
							error: false
						},
						{
							target:"dettagliAtto",
							icon:"fa fa-list-alt",
							title:"Dettaglio Stato Atto",
							description: "Dettaglio Atto",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_dettagliatto_enabled
						},
						{
							target:"listaTask",
							icon:"fa fa-list",
							title:"Lista Task Operatore",
							description: "Lista Task",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_listatask_enabled
						},
						{
							target:"eventiAtto",
							icon:"fa fa-list-alt",
							title:"Lista Task Automatici",
							description: "Eventi Atto",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_eventiatto_enabled
						},
						{
							target:"seduteAtto",
							icon:"fa fa-check",
							title:"Informazioni Seduta",
							description: "Informazioni Seduta",
							abilitato: true,
							visibile: $rootScope.configurationParams.section_eventiatto_enabled
						}

					];

					for( var x in $scope.attoDirigenzialeSezioni ){
						$scope.visibilitaSezioni[$scope.attoDirigenzialeSezioni[x].target.toLowerCase()]=$scope.attoDirigenzialeSezioni[x].visibile;
					}
				}

				$scope.init = function() {
					$scope.permettiCambiaOrdineSottoscrittori = true;
					//init on startup
					$scope.documentiFirmatiDaCaricare = new Map();
					$scope.documentiTrasparenzaDaCaricare = new Map();
					$scope.tipoAtto = {};
					$scope.runtimeHideExpression = {};
					$scope.decisioni = null;
					$scope.decisioniPostPubblicazioni = null;
					$scope.configurazioniTask = {};
					$scope.valoriSchedeDati= {  activeTab : ""  };
					$scope.sezioneCorrente={};
					$scope.indexSezioneCorrente = 0;

					$scope.getCodiceTipoIterById = function(){
						var codice = null;
						angular.forEach( $scope.tipoiters , function(value, key) {
							if($scope.tipoiters[key].id == $scope.atto.tipoIter.id){
								codice = $scope.tipoiters[key].codice;
							}
						});
						return codice;
					};

					if($rootScope.profiloattivo !== null){
						$log.debug('id del profilo ' + $rootScope.profiloattivo.id);
						$log.debug('id della aoo ' + $rootScope.profiloattivo.aoo.id);

						//Dipendenti
						if($stateParams.id === "nuovo" && $stateParams.section){
							$scope.getTipoAttoFromProfiloAttivo($stateParams.section);
						}
						$log.debug("TipoAtto:",$scope.tipoAtto);
						if(angular.isDefined($scope.tipoAtto.id) && $scope.tipoAtto.id != null){
							TipoIter.getByTipoAtto({tipoAttoId: $scope.tipoAtto.id},function(result){
								$scope.tipoiters = TipoIterService.listToMap(result);
							});
						}

						/*
			 * TipoDeterminazione.query(function(result) {
				$scope.tipoDeterminaziones = result;
				$scope.tipoDeterminaziones.unshift({});
			});
			*/
						Ufficio.getByAooId( {aooId:$rootScope.profiloattivo.aoo.id},function(result){
							$scope.ufficios = result;
							$scope.ufficios.push(null);
						});
					}

					$scope.importingSection = {};
					$scope.summernoteOptions = {
						height: 300,
						//width: 580,
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

					$scope.inializzaSezioni();
//		$scope.initListaEsitoPareriCommissioni();
//		$scope.initListaEsitoPareriQuartieri();

					$scope.attoHasAmbitoMateria = {  };
					$scope.idDoc = null;
					$scope.mostraAnteprimaParere = false;
					$scope.isCreazioneParere = false;
					$scope.isFirmaProposta = false;
					$scope.isFirmaAtto = false;
					$scope.isFirmaAttoInesistente = false;
					$scope.isAnteprimaRelataPubblicazione = false;
					$scope.isFirmaRelata = false;
					$scope.isFirmaParere = false;
					$scope.profiloResponsabile=null;

				}

				$scope.dataStato = null;

				$scope.loadWorflow = function(atto){

					var nomeStato = null;

					if( angular.isDefined( atto.avanzamento) && atto.avanzamento.length >0 ){
						nomeStato = atto.avanzamento[0].stato;
						$scope.dataStato = atto.avanzamento[0].dataAttivita;
						
						for (var i = 1; i < atto.avanzamento.length; i++) {
							if(nomeStato==atto.avanzamento[i].stato){
								$scope.dataStato = atto.avanzamento[i].dataAttivita;
							}else{
								break;
							}
						}
					}
					
					
					if(!$scope.categorieEvento) {
						CategoriaEvento.query(function(result){
							$scope.categorieEvento = result;
							$scope.eventiMap = {};

							for(var i=0; i<$scope.categorieEvento.length; i++ ) {
								$scope.eventiMap[$scope.categorieEvento[i].id] = [];
							}

							if(!$scope.eventi) {
								Atto.eventi({id: $scope.atto.id}, function(result) {
									$scope.eventi = result;

									for(var j=0; j<$scope.eventi.length; j++ ) {
										var evento = result[j];
										$scope.eventiMap[evento.tipoEvento.categoriaEvento.id].push(evento);
										$scope.dataStato = evento.dataCreazione;
										$scope.ultimoStato = evento.tipoEvento.descrizione;
									}
								});
							}
						});
					}

					if(angular.isDefined( atto.tipoIter ) &&  angular.isDefined( atto.tipoIter.id ) ){
						if(atto.tipoIter.id == 1){
							var contatore = 0;
							for(var j = 0; j<$scope.attoDirigenzialeSezioni.length; j ++){
//			  if($scope.attoDirigenzialeSezioni[j].target == "dichiarazioni"){
//				  contatore = contatore + 1;
//				  $scope.attoDirigenzialeSezioni[j].abilitato = false;
//			  }else
//			  if($scope.attoDirigenzialeSezioni[j].target == "assegnazioneIncarichi"){
//				  contatore = contatore + 1;
//				  $scope.attoDirigenzialeSezioni[j].abilitato = false;
//			  }
								/*ottimizzazione col contatore per fare il break quando entrambe le situazioni considerate sono state gestite*/
								if(contatore == 0){
									break;
								}
							}
						}
					}

					/*
  if('effettuaClonazione' !== $stateParams.id && 'nuovo' !== $stateParams.id && angular.isDefined( atto.tipoIter ) &&  angular.isDefined( atto.tipoIter.id ) && ($scope.taskBpmId > 0)
		  && $rootScope.profiloattivo && $rootScope.profiloattivo.id != 0){

	  Lavorazione.scenariDisabilitazione({taskBpmId: $scope.taskBpmId},
		function(data){
		  $log.debug("Scenari disabilitazione:", data );
		  for(var i = 0; i<data.length; i++){
			  if(data[i] == "NascondiParere"){
				  for(var j = 0; j<$scope.attoDirigenzialeSezioni.length; j++){
					  if($scope.attoDirigenzialeSezioni[j].target == "parere"){
						  $scope.attoDirigenzialeSezioni[j].abilitato = false;
						  break;
					  }
				  }
				  break;
			  }
		  }
	  });
  }
  */

					if(angular.isDefined( atto.tipoIter ) &&  angular.isDefined( atto.tipoIter.id ) && $rootScope.profiloattivo && $rootScope.profiloattivo.id != 0){
						if($scope.taskBpmId){
							Lavorazione.dettaglioTask({taskBpmId: $scope.taskBpmId },
								function(data){
									$scope.taskBpm = data;

									if($scope.incarichi==undefined || $scope.incarichi==null || $scope.incarichi.length==0){
										var aooId = null;
										if($scope.atto.aoo) {
											aooId = $scope.atto.aoo.id;
										}
										Lavorazione.assegnazioniincarichidettaglio({taskBpmId: $scope.taskBpmId, proponenteAooId: aooId, profiloAooId: $scope.profiloattivo.aoo.id, idAtto: $scope.atto.id }, function(data) {

											// lista incarichi da gestire
											if($scope.assegnazioneIncarichi==null){
												$scope.assegnazioneIncarichi = data.listAssegnazioneIncaricoDettaglio;
											}

											// lista incarichi già configurati
											$scope.configurazioneIncarichi = data.listConfigurazioneIncaricoDto;

											if( 	($scope.assegnazioneIncarichi!=null && $scope.assegnazioneIncarichi.length > 0) ||
												($scope.configurazioneIncarichi!=null && $scope.configurazioneIncarichi.length > 0) ||
												($scope.pareriSottoscrittori!=null && $scope.pareriSottoscrittori.length > 0)
											){
												for( var n in $scope.attoDirigenzialeSezioni){
													if($scope.attoDirigenzialeSezioni[n].target==='assegnazioneIncarichi'){
														$scope.attoDirigenzialeSezioni[n].abilitato = true;
														break;
													}
												}
											}

											for(var i in $scope.assegnazioneIncarichi){
												if(!isNaN(i)){

													// verifico se è già stato assegnato l'incarico
													var incaricoAssegnato = null;

													for(var t in $scope.configurazioneIncarichi){
														if( $scope.configurazioneIncarichi[t].idConfigurazioneTask==$scope.assegnazioneIncarichi[i].configurazioneTaskDto.idConfigurazioneTask ){
															incaricoAssegnato = $scope.configurazioneIncarichi[t];
															$scope.configurazioneIncarichi.splice(t,1);
															break;
														}
													}

													var incarico = inizializzaIncarico(incaricoAssegnato, i);
													incarico.isValid = true;

													if(!$scope.incarichi.includes(incarico)){
														$scope.incarichi[i]=incarico;
													}
												}
											}

											$scope.incarichiAssegnati = $scope.configurazioneIncarichi;

										});
									}


									if (data.idAssegnatario && (data.idAssegnatario.length > 0) ) {
										Lavorazione.listadecisioni({taskBpmId: $scope.taskBpmId},
											function(data){
												$scope.decisioni =  data;
												for (var i = 0; i<$scope.decisioni.length ; i++) {
													var dec = $scope.decisioni[i];

													/*
						        * IN ATTICO NON PRESENTE
						        *
						       if(   dec.codiceAzioneUtente == 'UC_CIFRA2_021'){
						           $scope.mostraAnteprimaParere = true;
						           $log.debug("mostraAnteprimaParere", $scope.mostraAnteprimaParere );
						       }
						       if(dec.codiceAzioneUtente == 'UC_CIFRA2_020'){
						    	   $scope.isCreazioneParere = true;
						       }else if(dec.codiceAzioneUtente == 'UC_CIFRA2_012'){
						    	   $scope.isFirmaProposta = true;
						       }

						       // FIRMA ATTICO - RESP. TECNICO E RESP: CONTABILE
						       else if(dec.codiceAzioneUtente == 'FIRMA_CONTABILE' || dec.codiceAzioneUtente == 'FIRMA'){
						    	   $scope.isFirmaAtto = true;
						       }

						       else if(dec.codiceAzioneUtente == 'UC_CIFRA2_014'){
						    	   $scope.isFirmaAttoInesistente = true;
						       }else if(dec.codiceAzioneUtente == 'GENERA_DOC_RELATA'){
						    	   $scope.isAnteprimaRelataPubblicazione = true;
						       }else if(dec.codiceAzioneUtente == 'UC_CIFRA2_022'){
						    	   $scope.isFirmaParere = true;
						       }else if(dec.codiceAzioneUtente == 'FIRMA_RELATA'){
						    	   $scope.isFirmaRelata = true;
						       }
						       */
												}
											});
									}
								});
						}
						else {
							if($scope.incarichi==undefined || $scope.incarichi==null || $scope.incarichi.length==0){
								Lavorazione.getincarichi({idAtto: $scope.atto.id }, function(data) {

									// lista incarichi da gestire
									if($scope.assegnazioneIncarichi==null){
										$scope.assegnazioneIncarichi = data.listAssegnazioneIncaricoDettaglio;
									}

									// lista incarichi già configurati
									$scope.configurazioneIncarichi = data.listConfigurazioneIncaricoDto;

									if( ($scope.assegnazioneIncarichi!=null && $scope.assegnazioneIncarichi.length > 0) ||
										($scope.configurazioneIncarichi!=null && $scope.configurazioneIncarichi.length > 0) ||
										($scope.pareriSottoscrittori!=null && $scope.pareriSottoscrittori.length > 0)
									){
										for( var n in $scope.attoDirigenzialeSezioni){
											if($scope.attoDirigenzialeSezioni[n].target==='assegnazioneIncarichi'){
												$scope.attoDirigenzialeSezioni[n].abilitato = true;
												break;
											}
										}
									}

									for(var i in $scope.assegnazioneIncarichi){
										if(!isNaN(i)){

											// verifico se è già stato assegnato l'incarico
											var incaricoAssegnato = null;

											for(var t in $scope.configurazioneIncarichi){
												if( $scope.configurazioneIncarichi[t].idConfigurazioneTask==$scope.assegnazioneIncarichi[i].configurazioneTaskDto.idConfigurazioneTask ){
													incaricoAssegnato = $scope.configurazioneIncarichi[t];
													$scope.configurazioneIncarichi.splice(t,1);
													break;
												}
											}

											var incarico = inizializzaIncarico(incaricoAssegnato, i);
											incarico.isValid = true;

											if(!$scope.incarichi.includes(incarico)){
												$scope.incarichi[i]=incarico;
											}
										}
									}

									$scope.incarichiAssegnati = $scope.configurazioneIncarichi;

								});
							}
						}

					}
				};

				$scope.concatenaCigCup = function( entity ) {
					$log.debug("concatenaCigCup",entity);
					entity.oggetto = ((angular.isDefined(entity.codiceCig) && entity.codiceCig != null && entity.codiceCig != '') ?'CIG: ' + entity.codiceCig:'') +((angular.isDefined(entity.codiceCig) && entity.codiceCig!=null && entity.codiceCig != '') && (angular.isDefined(entity.codiceCup) && entity.codiceCup!=null && entity.codiceCup != '')?' - ':'')+((angular.isDefined(entity.codiceCup) && entity.codiceCup!=null && entity.codiceCup != '')?'CUP: '+entity.codiceCup : '')+' '+(angular.isUndefined(entity.oggetto)?'':entity.oggetto);
				};

				$scope.createRuntimeExpression = function(elementoScheda, campoName, hideExpression){
//  $log.debug( "createRuntimeExpression" + campoName);
					var expression = 'true';


					if(hideExpression != null  && hideExpression.length > 0 ){
						// $log.debug("hideExpression:"+hideExpression);

						var conditions = angular.fromJson( hideExpression);

						$log.debug("qqqqconditions:"+conditions);
						if(angular.isDefined(conditions) ){
							if(conditions.length > 0){
								expression = "";
								angular.forEach( conditions , function( condition , key) {
									if(!$scope.stringEmpty(condition.bool)){
										expression += condition.bool +" " ;
									}

									expression += elementoScheda+'['+condition.leftOperand+']' + condition.comparison;

									if(!$scope.stringEmpty(condition.rightOperand)){
										expression += "'"+condition.rightOperand+"' ";
									}
								});
							}

						}

// $log.debug("AAAA expression:"+expression);

						//alert(runtimeE);

						//var runtimeE = hideExpression.replace("VAL",elementoScheda);
					}

					var value  = $scope.$eval(expression);
					$scope.runtimeHideExpression[campoName] = {expression:expression,  value: value};

				}

				$scope.evalRuntimeHideExpression = function( ){
					$log.debug( "evalRuntimeHideExpression");
					angular.forEach( $scope.runtimeHideExpression , function(value, key) {
						value.value = $scope.$eval(value.expression);
					});

				};

				$scope.setBeneficiario = function(field, index) {

					$scope.atto.beneficiari[index][field] = $filter('date')($scope.atto.beneficiari[index][field], 'yyyy-MM-dd');
				};

				$scope.deleteAllegatoParere = function (allegati, index) {
					if(allegati!=undefined && allegati!=null && allegati[index]!=undefined && allegati[index]!=null && allegati[index].id!=undefined && allegati[index].id!=null){
						DocumentoInformatico.delete({id: allegati[index].id}, function () {
							allegati.splice(index,1);
						});
					}
				};

				$scope.preparaInserimentoAllegatiParere = function(parere){
					Parere.save(parere,
						function (json) {
							$scope.dtoWorkflow.parere.id = json.parereId;
							$scope.dtoWorkflow.parere.createdBy = $rootScope.account.login;
						});
				};


				$scope.fileDroppedParere = function (files,event,rejectedFiles, parere ) {
					var allegatiInMaschera  = [];
					if(parere && parere.allegati && parere.allegati.length > 0){
						for(var i=0; i<parere.allegati.length; i++ ) {
							var allegato = {id:parere.allegati[i].id, pubblicabile:parere.allegati[i].pubblicabile};
							allegatiInMaschera.push(allegato);
						}
					}

					if(event.type !== "click"){
						if(rejectedFiles.length > 0){
							$rootScope.$broadcast('rejectedFilesEvent', files, event, rejectedFiles, 'application/*');
							return;
						}
						Upload.upload({
							url: 'api/pareres/'+parere.id +'/allegato',
							headers : {
								'Authorization': 'Bearer '+ $rootScope.accessToken
							},
							file: files
						}).progress(function (evt) {
							$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
						}).success(function (data, status, headers, config) {
							if(data.length > 0 && allegatiInMaschera.length>0){
								for(var i=0; i<data.length; i++ ) {
									for(var j=0; j<allegatiInMaschera.length; j++ ) {
										if(data[i].id == allegatiInMaschera[j].id){
											data[i].pubblicabile = allegatiInMaschera[j].pubblicabile;
											break;
										}
									}
								}
							}
							$scope.dtoWorkflow.parere.allegati = data;

						});

					}
				};

				$scope.cercaModelloCampo = function (tipoCampo){
					$log.debug("cercaModelloCampo:tipoCampo:", tipoCampo);
					$scope.pageModelloCampo = 1;
					$scope.modelloCampos =[];
					delete $scope.modelloCampoLinks;
					delete $scope.totalModelloCampos;

					$scope.tipoModelloCampo = {};
					if($scope.sezioneCorrente.target == 'dichiarazioni'){
						$scope.tipoModelloCampo = $scope.showSezioneContabile;
					}else{
						$scope.tipoModelloCampo = tipoCampo;
					}
					$scope.confirmCercaModelloCampo($scope.tipoModelloCampo, 1);
					$('#cercaModelloCampoConfirmation').modal('show');

				};

				$scope.selezionaModelloCampo = function(modelloCampo) {
					if(modelloCampo.tipoCampo ==='divulgazione'){
						$scope.atto['noteMotivazione'].testo =modelloCampo.testo;
					}else{
						$scope.atto[modelloCampo.tipoCampo].testo = modelloCampo.testo;
					}
					$('#cercaModelloCampoConfirmation').modal('hide');
				};


				$scope.confirmCercaModelloCampo = function(tipoModelloCampo, page) {
					$scope.pageModelloCampo = page;
					if(angular.isUndefined($scope.modelloCampoCriteria)){
						$scope.modelloCampoCriteria = {};
					}
					$scope.modelloCampoCriteria.tipoCampo = tipoModelloCampo;
					if(page){
						$scope.modelloCampoCriteria.page = page;
					}else if(angular.isUndefined($scope.modelloCampoCriteria.page)){
						$scope.modelloCampoCriteria.page = 1;
					}
					$scope.modelloCampoCriteria.tipoAttoId = $scope.atto.tipoAtto.id;
					$scope.modelloCampoCriteria.per_page = 5,
						ModelloCampo.getAllMixed($scope.modelloCampoCriteria,
							function (result, headers) {
								$scope.modelloCampoLinks = ParseLinks.parse(headers('link'));
								$scope.totalModelloCampos=headers('x-total-count') ;
								$scope.modelloCampos = result;
								if(result && result.length == 0 && $scope.pageModelloCampo != 1){
									$scope.confirmCercaModelloCampo(tipoModelloCampo, 1);
								}
								//$('#cercaModelloCampoConfirmation').modal('hide');
							});
				};

				$scope.loadPageModelloCampo = function(page, modello) {
					$scope.confirmCercaModelloCampo(modello, page);
				};

				$scope.salvaModelloCampo = function (tipoCampo) {
					$log.debug("salvaModelloCampo", tipoCampo, $scope.showSezioneContabile);
					$scope.modelloCampo = {};
					if($scope.sezioneCorrente.target == 'dichiarazioni'){
						$scope.modelloCampo.testo = $scope.atto[$scope.showSezioneContabile].testo;
						$scope.modelloCampo.tipoCampo = $scope.showSezioneContabile;
					}else{
						$scope.modelloCampo.testo = $scope.atto[tipoCampo].testo;
						if(angular.isDefined(tipoCampo) && tipoCampo!=''){
							$scope.modelloCampo.tipoCampo = tipoCampo;
						}
					}


					$('#salvaModelloCampoConfirmation').modal('show');

				};

				$scope.updateSezioneContabile = function (value){
					$scope.showSezioneContabile = value;
				};

				$scope.confirmSalvaModelloCampo = function (modelloCampo) {
					modelloCampo.tipoAtto = $scope.atto.tipoAtto;
					ModelloCampo.save(modelloCampo,
						function (data) {
							$log.debug(data);
							$('#salvaModelloCampoConfirmation').modal('hide');
						});
				};


				$scope.selezionaSezione = function(index){
					var sezione = $scope.attoDirigenzialeSezioni[index];
					$scope.indexSezioneCorrente=index;

					sezione.active=true;
					sezione.activeCss="active";
					$scope.sezioneCorrente = sezione;

					$scope.modelloCampo={};
					$scope.modelloCampo.tipoCampo=$scope.sezioneCorrente.target;
					if(!$scope.modelloHtmls || !angular.isDefined($scope.modelloHtmls.length)){
						$scope.modelloHtmls = ModelloHtml.query();
					}
					$scope.modelliSchedeAnagraficheContabili = [];

					// if(  $scope.sezioneCorrente.target === 'riferimentiNormativi' ){
					if(!$scope.tipomaterias && $scope.atto.aoo){
						TipoMateria.activeByAoo({aooId: $scope.atto.aoo.id}, function(result){
							$scope.tipomaterias = TipoMateriaService.listToMap(result);
						});
					}

					//}

					if(  $scope.sezioneCorrente.target === 'trasparenza' || $scope.sezioneCorrente.target === 'schede' ){
						angular.element(document).find("[name = 'traspBeneficiario'] option[value='?']").remove();
						if(!$scope.ambiti || !$scope.ambitoDl33s){
							$scope.ambitoDl33s = AmbitoDl33.attivo( function(results){
								$scope.ambiti =  AmbitoDl33Service.listToMap(results);
							});
						}
						if(!$scope.macros){
							Macro_cat_obbligo_dl33.attivo(  function(results){
								$scope.macros =  AnagraficaObbligoDlService.listToMap(results);
								if(!$scope.valoriSchedeDati.attoId)
									$scope.caricaSchedeTrasparenza( $scope.valoriSchedeDati );
							}) ;
						}else if(!$scope.valoriSchedeDati.attoId){
							$scope.caricaSchedeTrasparenza( $scope.valoriSchedeDati );
						}
					}

					if($scope.sezioneCorrente.target === 'dettagliAtto'){
						$log.debug("Ultimo Stato");
						$scope.statos = [];
						Atto.ultimostato({id: $scope.atto.id}, function(result) {
							$scope.statos = result;
						});
					}

					if(  $scope.sezioneCorrente.target === 'eventiAtto' || $scope.sezioneCorrente.target === 'dettagliAtto'){
						$log.debug("Eventi");
						if(!$scope.categorieEvento) {
							CategoriaEvento.query(function(result){
								$scope.categorieEvento = result;
								$scope.eventiMap = {};

								for(var i=0; i<$scope.categorieEvento.length; i++ ) {
									$scope.eventiMap[$scope.categorieEvento[i].id] = [];
								}

								if(!$scope.eventi) {
									Atto.eventi({id: $scope.atto.id}, function(result) {
										$scope.eventi = result;

										for(var j=0; j<$scope.eventi.length; j++ ) {
											var evento = result[j];
											$scope.eventiMap[evento.tipoEvento.categoriaEvento.id].push(evento);
										}
									});
								}
							});
						}
					}

					$rootScope.navigatoretitle.push({title: $scope.tipoAtto.descrizione , link:'#/atto'});
					if($scope.atto.id > 0){
						var titolo = $scope.atto.codiceCifra;
						if($scope.atto.numeroAdozione != null){
							titolo = titolo + " - " + $scope.atto.numeroAdozione;
						}
						$rootScope.navigatoretitle.push({title:titolo, link:'#/atto/'+$scope.atto.id});
						$rootScope.navigatoretitle.push({title:$scope.sezioneCorrente.title, link:'#/atto/'+$scope.atto.id+'/'+$scope.indexSezioneCorrente});
					}


				};

				$scope.aggiornaUltimoStato = function(){
					$scope.aggiornamentoStato = true;
					$scope.statos = [];
					Atto.ultimostato({id: $scope.atto.id}, function(result) {
						$scope.statos = result;
						$scope.aggiornamentoStato = false;
					});
				};

				$scope.aggiornaEventi = function(){

					$scope.aggiornamentoEventi = true;

					for(var i=0; i<$scope.categorieEvento.length; i++ ) {
						$scope.eventiMap[$scope.categorieEvento[i].id] = [];
					}

					Atto.eventi({id: $scope.atto.id}, function(result) {
						$scope.eventi = result;

						for(var j=0; j<$scope.eventi.length; j++ ) {
							var evento = result[j];
							$scope.eventiMap[evento.tipoEvento.categoriaEvento.id].push(evento);
						}

						$scope.aggiornamentoEventi = false;
					});
				}

				$scope.initIE = function(){
					if($scope.atto && ($scope.atto.ie === undefined || $scope.atto.ie === null)){
						$scope.atto.ie = true;
					}
				};

				$scope.initSelezionaSezione = function( section, tipoattoid){
					if(section == 'aggiornaPubblicazione' && $scope.atto.fullAccess){
						$scope.solaLettura = true;
						$scope.scenariDisabilitazione = [];
						$scope.scenariDisabilitazione.push('PostPubblicazioneTrasparenza');
						var pulsanteVuoto = {"descrizione":"Aggiorna Trasparenza", codiceAzioneUtente:"AGGIORNA_TRASPARENZA"};
						$scope.decisioniPostPubblicazione = [];
						$scope.decisioniPostPubblicazione.push(pulsanteVuoto);
						section = 0;
					}

					$scope.loadWorflow($scope.atto);

					$scope.sectionInitIndex = 0;

					TipoAtto.get({id: tipoattoid}, function(result) {
						var sezioniatto = result.sezioni;

						$scope.visibilitaCampi = {};
						if(result && result.campi && result.campi.length && result.campi.length > 0){
							for(var i = 0; i < result.campi.length; i++){
								$scope.visibilitaCampi[result.campi[i].codice] = result.campi[i].visibile;
							}
						}

						for(var i = $scope.attoDirigenzialeSezioni.length - 1; i >= 0; i--) {

							var sezioneAtto = sezioniatto.find(item => item.codice.toLowerCase() === $scope.attoDirigenzialeSezioni[i].target.toLowerCase());

							if(
								!$scope.attoDirigenzialeSezioni[i].visibile || sezioneAtto==null ||
								(sezioneAtto!=null && !sezioneAtto.visibile)
							){
								$scope.attoDirigenzialeSezioni.splice(i, 1);
							}
						}

						for(var i = Number(section); i< $scope.attoDirigenzialeSezioni.length; i++){
							if($scope.attoDirigenzialeSezioni[i].abilitato && $scope.attoDirigenzialeSezioni[i].visibile){
								$scope.indexSezioneCorrente=i;
								$scope.sectionInitIndex=section;
								break;
							}
						}


						for(var i = Number($scope.sectionInitIndex) + 1; i< $scope.attoDirigenzialeSezioni.length; i++){
							if($scope.attoDirigenzialeSezioni[i].abilitato && $scope.attoDirigenzialeSezioni[i].visibile){
								$scope.nextSection = i;
								break;
							}
						}

						for(var i = Number($scope.sectionInitIndex) -1; i>-1; i--){
							if($scope.attoDirigenzialeSezioni[i].abilitato && $scope.attoDirigenzialeSezioni[i].visibile){
								$scope.previusSection = i;
								break;
							}
						}

						$rootScope.navigatoretitle = [];
						$scope.selezionaSezione($scope.indexSezioneCorrente);
					});


				};

				$scope.verificaCampiObbligatoriAssegnazioneIncarichi = function(isError){
					var res = false;
					var target = 'assegnazioneIncarichi';
					if(isError!=undefined && isError!=null && isError){
						res = isError;
					} else {
						if( $scope.visibilitaSezioni[target.toLowerCase()] ){
							for( var i in $scope.incarichi ){

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.tipoConfigurazioneTaskId==$rootScope.configurationParams.tipo_conf_task_profilo_id){

									if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.obbligatoria && $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto.length == 0 ){
										res = true;
										break;
									}

									for ( var p in $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto ) {

										if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.obbligatoria ){
											if( $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].idProfilo == null ||
												$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].qualificaProfessionaleDto == null ||
												$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].qualificaProfessionaleDto.id == null ){
												res = true;
												break;
											}
										}

										if( $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].profilo &&
											$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].profilo.id == $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].idProfilo &&
											$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].profilo.validita &&
											$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].profilo.validita.validoal){
											res = true;
											break;
										}

										/*Se viene inserito il profilo deve esserci anche la qualifica*/
										if( $scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].idProfilo &&
											(
												!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].qualificaProfessionaleDto ||
												!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[p].qualificaProfessionaleDto.id
											)
										){
											res = true;
											break;
										}

										if( $scope.incarichi[i].isValid!=undefined && !$scope.incarichi[i].isValid ){
											res = true;
											break;
										}
									}

								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.tipoConfigurazioneTaskId==$rootScope.configurationParams.tipo_conf_task_ufficio_id){

									if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.obbligatoria && $scope.incarichi[i].listConfigurazioneIncaricoAooDto.length == 0 ){
										res = true;
										break;
									}else if($scope.incarichi[i].dettaglio.configurazioneTaskDto.obbligatoria && $scope.incarichi[i].listConfigurazioneIncaricoAooDto.length){
										for(let u = 0; u < $scope.incarichi[i].listConfigurazioneIncaricoAooDto.length; u++){
											 if(!$scope.incarichi[i].dettaglio.listAoo.filter((aoo) => aoo.id == $scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].idAoo).length){
												res = true;
												break;
											}
										}
										if(res == true){
											break;
										}
									}

									for ( var u in $scope.incarichi[i].listConfigurazioneIncaricoAooDto ) {

										if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.obbligatoria ){
											if( $scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].idAoo == null ){
												res = true;
												break;
											}
										}

										if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.impostaScadenza &&
											$scope.incarichi[i].dettaglio.configurazioneTaskDto.scadenzaObbligatoria &&
											$scope.incarichi[i].listConfigurazioneIncaricoAooDto[u] &&
											$scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].idAoo){
											if( !($scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].giorniScadenza) ){
												res = true;
												break;
											}
										}

										if( $scope.incarichi[i].dettaglio.configurazioneTaskDto.dataManuale &&
											$scope.incarichi[i].listConfigurazioneIncaricoAooDto[u] &&
											$scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].idAoo){
											if( !($scope.incarichi[i].listConfigurazioneIncaricoAooDto[u].dataManuale) ){
												res = true;
												break;
											}
										}

										if( $scope.incarichi[i].isValid!=undefined && !$scope.incarichi[i].isValid ){
											res = true;
											break;
										}

									}
								}
							}
						}
					}

					return res;
				}

				$scope.verificaCampiObbligatoriSottoscrizioni = function(){
					var res = false;
					var target = 'sottoscrizioni';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						for(var i = 0; i <  $scope.atto.sottoscrittori.length; i++){
							if(angular.isUndefined($scope.atto.sottoscrittori[i].qualificaProfessionale) || $scope.atto.sottoscrittori[i].qualificaProfessionale == null || $scope.atto.sottoscrittori[i].qualificaProfessionale == ''){
								res = true;
								break;
							}
							if(angular.isUndefined($scope.atto.emananteProfilo) || $scope.atto.emananteProfilo == null || !$scope.atto.emananteProfilo || $scope.atto.emananteProfilo == ''){
								res = true;
								break;
							}
							if(angular.isUndefined($scope.atto.qualificaEmanante) || $scope.atto.qualificaEmanante == null || !$scope.atto.qualificaEmanante || $scope.atto.qualificaEmanante == ''){
								res = true;
								break;
							}
						}
						if(!res && $scope.atto.sottoscrittori.length == 0){
							res = true;
						}else if(!res){
							var almenoUnoAttivo = false;
							for(var s = 0; s<$scope.atto.sottoscrittori.length; s++){
								if($scope.atto.sottoscrittori[s].enabled){
									almenoUnoAttivo = true;
									break;
								}
							}
							if(!almenoUnoAttivo){
								res = true;
							}
						}
						if(!res && $scope.isNotValidSottoscrittori || $scope.isNotValidSottoscrittoriNonProponente) {
							res = true;
						}
						if(!res && $scope.atto.congiunto){
							var almenoUnSottoscrittoreAltraAoo = false;
							for(var s = 0; s<$scope.atto.sottoscrittori.length; s++){
								if($scope.atto.sottoscrittori[s].aooNonProponente && $scope.atto.sottoscrittori[s].aooNonProponente.id != $scope.atto.aoo.id){
									almenoUnSottoscrittoreAltraAoo = true;
									break;
								}
							}
							if(!almenoUnSottoscrittoreAltraAoo){
								res = true;
							}
						}
					}

					return res;
				};
				$scope.verificaCampiObbligatoriDatiIdentificativi = function(){
					var res = false;
					var target = 'datiIdentificativi';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.oggetto == null){
							res = true;
							$log.debug('manca oggetto');
						}
						if(!$scope.atto.tipoDeterminazione){
							res = true;
							$log.debug('manca la tipoDeterminazione');
						}else if($scope.atto.tipoDeterminazione.codice && $scope.atto.tipoDeterminazione.codice.toUpperCase() == 'REVOCA' && (!$scope.atto.codicecifraAttoRevocato || !$scope.atto.codicecifraAttoRevocato.trim())){
							res = true;
						}
						// TODO: tipo adempimento non previsto in ATTICO
						// if($scope.atto.tipoAdempimento == null || $scope.atto.tipoAdempimento.id== null || $scope.atto.tipoAdempimento.id == ""){
						//	res = true;
						//	$log.debug('manca tipoAdempimento');
						// }
						if($scope.atto.tipoIter == null ){
							res = true;
							$log.debug('manca la tipoIter');
						}
						/*if ($scope.atto.codiceCig && ($scope.atto.codiceCig.trim().length < 10 )) {
			res = true;
			$log.debug('codice CIG non valido');
		}*/
						if ($scope.atto.codiceCup && ($scope.atto.codiceCup.trim().length != 15 )) {
							res = true;
							$log.debug('codice CUP non valido');
						}
						if($scope.atto.id > 0 && !$scope.atto.tipoDeterminazione){
							res = true;
						}
						if($scope.atto.id > 0 && $scope.atto.tipoDeterminazione && $scope.atto.tipoDeterminazione.descrizione.toLowerCase() != 'ordinario' &&
							!$scope.atto.attoRevocatoId) {
							// && $scope.atto.motivoClonazione.toLowerCase() != 'revoca'
							res = true;
							$log.debug('manca il riferimento all atto da revocare/rettificare (atto revocato)');
						}
						if($scope.visibilitaCampi && $scope.visibilitaCampi.tipo_finanziamento && $scope.visibilitaCampi.tipo_finanziamento == true && ($scope.atto.hasTipoFinanziamenti == null || ($scope.atto.hasTipoFinanziamenti != null && $scope.atto.hasTipoFinanziamenti.length == 0))){
							if($scope.atto.tipoAtto.codice == 'DD' || $scope.atto.tipoAtto.codice == 'DL'){
								if($scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0 && 
										(
												$scope.scenariDisabilitazione.indexOf('TestoPropostaModificabile') > -1 ||
												$scope.scenariDisabilitazione.indexOf('DatiPubblicazioneModificabili') > -1 ||
												$scope.scenariDisabilitazione.indexOf('TipoIterNonModificabile') > -1
												)
									){
									res = true;
									$log.debug('mancano finanziamenti');
								}
									
							}
							
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriDatiIdentificativiConsiglio = function(){
					var res = false;
					var target = 'datiIdentificativiConsiglio';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.oggetto == null){
							res = true;
							$log.debug('manca oggetto');
						}
						//if($scope.atto.tipoDeterminazione == null ){
						//	res = true;
						//	$log.debug('manca la tipoDeterminazione');
						//}
						if($scope.atto.tipoIter == null ){
							res = true;
							$log.debug('manca la tipoIter');
						}
						if($scope.atto.proponenti == null || ($scope.atto.proponenti != null && $scope.atto.proponenti.length == 0)){
							res = true;
							$log.debug('mancano proponenti');
						}
						if($scope.visibilitaCampi && $scope.visibilitaCampi.tipo_finanziamento && $scope.visibilitaCampi.tipo_finanziamento == true && ($scope.atto.hasTipoFinanziamenti == null || ($scope.atto.hasTipoFinanziamenti != null && $scope.atto.hasTipoFinanziamenti.length == 0))){
							if($scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0 && 
									(
											$scope.scenariDisabilitazione.indexOf('TestoPropostaModificabile') > -1 ||
											$scope.scenariDisabilitazione.indexOf('DatiPubblicazioneModificabili') > -1 ||
											$scope.scenariDisabilitazione.indexOf('TipoIterNonModificabile') > -1
											)
								){
								res = true;
								$log.debug('mancano finanziamenti');
							}
						}
						if($scope.atto.id > 0 && !$scope.atto.tipoDeterminazione){
							res = true;
						}
						if($scope.atto.id > 0 && $scope.atto.tipoDeterminazione && $scope.atto.tipoDeterminazione.descrizione.toLowerCase() != 'ordinario' &&
							!$scope.atto.attoRevocatoId) {
							// && $scope.atto.motivoClonazione.toLowerCase() != 'revoca'
							res = true;
							$log.debug('manca il riferimento all atto da revocare/rettificare (atto revocato)');
						}
						if(!$scope.atto.dataRicevimento ){
							res = true;
							$log.debug('manca la data di ricevimento');
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriDatiIdentificativiVerbale = function(){
					var res = false;
					var target = 'datiIdentificativiVerbale';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.oggetto == null){
							res = true;
							$log.debug('manca oggetto');
						}
						//if($scope.atto.tipoDeterminazione == null ){
						//	res = true;
						//	$log.debug('manca la tipoDeterminazione');
						//}
						if($scope.atto.tipoIter == null ){
							res = true;
							$log.debug('manca la tipoIter');
						}
						if($scope.atto.id > 0 && !$scope.atto.tipoDeterminazione){
							res = true;
						}
						if($scope.atto.id > 0 && $scope.atto.tipoDeterminazione && $scope.atto.tipoDeterminazione.descrizione.toLowerCase() != 'ordinario' &&
							!$scope.atto.attoRevocatoId) {
							// && $scope.atto.motivoClonazione.toLowerCase() != 'revoca'
							res = true;
							$log.debug('manca il riferimento all atto da revocare/rettificare (atto revocato)');
						}
						if($scope.visibilitaCampi && $scope.visibilitaCampi.tipo_finanziamento && $scope.visibilitaCampi.tipo_finanziamento == true && ($scope.atto.hasTipoFinanziamenti == null || ($scope.atto.hasTipoFinanziamenti != null && $scope.atto.hasTipoFinanziamenti.length == 0))){
							if($scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0 && 
									(
											$scope.scenariDisabilitazione.indexOf('TestoPropostaModificabile') > -1 ||
											$scope.scenariDisabilitazione.indexOf('DatiPubblicazioneModificabili') > -1 ||
											$scope.scenariDisabilitazione.indexOf('TipoIterNonModificabile') > -1
											)
								){
								res = true;
								$log.debug('mancano finanziamenti');
							}
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriLR1508 = function(){
					var res = false;
					var target = 'riferimentiNormativi';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.tipoMateria == null){
							res = true;
						}
						if($scope.atto.tipoMateria != null && $scope.verificaMateria()){
							res = true;
						}
						if($scope.verificaSottoMateria() && $scope.atto.sottoMateria == null ){
							res = true;
						}
						if($scope.atto.riservato == null ){
							res = true;
						}
						if(!$scope.atto.riservato && $scope.atto.pubblicazioneIntegrale == null ){
							res = true;
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriAllegato = function (allegato){
					return  (allegato != null && allegato.file != null &&
						(allegato.titolo == null || allegato.titolo == '' ||
							allegato.tipoAllegato == null ||
							allegato.pubblicabile == null ||
							($scope.atto.riservato == true && allegato.pubblicabile && allegato.pubblicabile === true) ||
							(allegato.pubblicabile && allegato.omissis == null) ||
							(allegato.omissis == true && !allegato.fileomissis))); /* && $scope.isGoodAllegatoMatrix(allegato) non dovrebbe servire */
				};
				$scope.verificaCampiObbligatoriAllegati = function() {
					let res = false;
					let target = 'allegati';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						for(let i = 0; $scope.atto.allegati != null && i < $scope.atto.allegati.length ; i++){
							let allegato = $scope.atto.allegati[i];
							let thisRes = $scope.verificaCampiObbligatoriAllegato(allegato);
							if(thisRes){
								allegato.$invalid = true;
								res = true;
							}else{
								delete allegato.$invalid;
							}
						}
						if($scope.atto.allegatoBeneficiari === true && $scope.atto.allegati && $scope.atto.allegati.length == 0) {
							res = true;
						}
					}
					return res;
				};

				$scope.verificaAllegatiRiservato = function(){
					$scope.showErrAllPubblicabile = false;
					for(var i = 0; $scope.atto.allegati != null && i < $scope.atto.allegati.length ; i++){
						var allegato = $scope.atto.allegati[i];
						if($scope.atto.riservato && $scope.atto.riservato === true && allegato.pubblicabile && allegato.pubblicabile === true){
							$scope.showErrAllPubblicabile = true;
						}
					}
					$log.debug("showRiservato",$scope.showRiservato);
				};

				$scope.setCampiObbligatoriSez2 = function() {
					var pubIntegraleEl = angular.element(".pubblicazioneIntegrale");
					if( ($scope.atto.riservato == true) && (pubIntegraleEl.length >0) && (pubIntegraleEl.attr('disabled') != 'disabled')) {
						$scope.atto.pubblicazioneIntegrale=null;
					}
					if($scope.atto.riservato === false){
						$scope.atto.pubblicazioneIntegrale = true;
					}else if($scope.atto.riservato === true){
						$scope.atto.pubblicazioneIntegrale = null;
					}
					$scope.verificaAllegatiRiservato();
				};

				$scope.verificaCampiObbligatoriDLGS33 = function(){
					var res = false;
					var target = 'trasparenza';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if(angular.isDefined($scope.atto)) {
							if($scope.atto.obbligodlgs332013 == null){
								res = true;
							}else if($scope.atto.obbligodlgs332013 == true){
//				if($scope.first($scope.atto.hasAmbitoMateriaDl) == null ){
//					res = true;
//				} else
								if($scope.atto.macroCategoriaObbligoDl33 == null ){
									res = true;
								} else if($scope.atto.macroCategoriaObbligoDl33 != null && $scope.atto.categoriaObbligoDl33 == null && angular.isDefined($scope.macros) && Object.keys($scope.macros).length > 0 &&
									$scope.first($scope.macros[$scope.atto.macroCategoriaObbligoDl33.id].categorie) != null){
									res = true;
								} else if($scope.atto.macroCategoriaObbligoDl33 != null && $scope.atto.categoriaObbligoDl33 != null && $scope.atto.obbligoDl33 == null && angular.isDefined($scope.macros) && Object.keys($scope.macros).length > 0 &&
									$scope.first($scope.macros[$scope.atto.macroCategoriaObbligoDl33.id].categorie[$scope.atto.categoriaObbligoDl33.id].obblighi) != null){
									res = true;
								}
								if(!$scope.schede) {
									Macro_cat_obbligo_dl33.attivo(  function(results){
										$scope.macros =  AnagraficaObbligoDlService.listToMap(results);
										if(!$scope.valoriSchedeDati.attoId)
											$scope.caricaSchedeTrasparenza( $scope.valoriSchedeDati );
									}) ;
								}
								if($scope.schede) {
									for (var i = 0; i < $scope.schede.length; i++) {
										var scheda = $scope.schede[i];
										if(angular.isDefined(scheda)) {
											for (var y = 0; y < scheda.campi.length; y++) {
												var campo = scheda.campi[y];
												if(angular.isDefined(campo) && campo.obbligatorio===true) {
													var campoScheda = $scope.valoriSchedeDati.elementiSchede[scheda.id][0][campo.id];
													if(campoScheda == null || (campoScheda != null && campoScheda.id == -1)) {
														res = true;
													}
												}
											}
										}
									}
								}
							}
						}
					}
					return res;
				};

				$scope.$watch(function(scope) { return scope.valoriSchedeDati; }, function(newValue,oldValue,scope){
					$log.debug("sono nel watch scheda");
					$scope.setAttoDirigenzialeError('trasparenza', $scope.verificaCampiObbligatoriDLGS33());
					$scope.disabledButton = $scope.calculateDisabledButton();
				},true);

				$scope.verificaCampiObbligatoriPreambolo = function(){
					var res = false;
					var target = 'preambolo';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.preambolo.testo == null || $scope.atto.preambolo.testo == ""){
							res = true;
						}
					}
					return res;
				};
				$scope.verificaCampiObbligatoriMotivazione = function(){
					var res = false;
					var target = 'motivazione';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.motivazione.testo == null || $scope.atto.motivazione.testo == ""){
							res = true;
						}
					}
					return res;
				};
				$scope.verificaCampiObbligatoriGaranzieAllaRiservatezza = function(){
					var res = false;
					var target = 'garanzieRiservatezza';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.garanzieRiservatezza.testo == null || $scope.atto.garanzieRiservatezza.testo == ""){
							res = true;
						}
					}
					return res;
				};
				$scope.verificaCampiObbligatoriDispositivo = function(){
					var res = false;
					var target = 'dispositivo';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.dispositivo.testo == null || $scope.atto.dispositivo.testo == ""){
							res = true;
						}
					}
					return res;
				};
				$scope.verificaCampiObbligatoriDomanda = function(){
					var res = false;
					var target = 'domanda';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if($scope.atto.domanda.testo == null || $scope.atto.domanda.testo == ""){
							res = true;
						}
					}
					return res;
				};

				$scope.setTomorrowDate = function(){
					if(!$scope.atto.dataInizioPubblicazionePresunta){
						DateServer.getCurrent({}, function(obj){
							if(obj && obj.milliseconds){
								var adesso = new Date(obj.milliseconds);
								$scope.atto.dataInizioPubblicazionePresunta = moment(adesso).add('days', 1).toDate();
								$scope.setPublicationEndDate();
							}
						});
					}
				}

				$scope.setPublicationEndDate = function(){
					if(($scope.isPubblicazione || ($scope.scenariDisabilitazione && $scope.scenariDisabilitazione.indexOf('PostPubblicazioneTrasparenza') > -1)) && $scope.atto.dataInizioPubblicazionePresunta && $scope.atto.durataGiorni){
						DateServer.calcolaDataFinePubblicazione({start: $scope.atto.dataInizioPubblicazionePresunta, days : $scope.atto.durataGiorni}, function(obj){
							if(obj && obj.milliseconds){
								$scope.atto.dataFinePubblicazionePresunta = new Date(obj.milliseconds);
							}
						});
						/*$scope.atto.dataFinePubblicazionePresunta = moment($scope.atto.dataInizioPubblicazionePresunta).add('days', $scope.atto.durataGiorni).toDate();*/
					}
				}

				$scope.validateDays = function(){
					if($scope.atto.durataGiorni!=null){
						if($scope.atto.durataGiorni<5 || $scope.atto.durataGiorni > 60){
							return false;
						} else {
							return true;
						}
					}
				}

				$scope.verificaCampiObbligatoriPubblicazione = function(){
					var res = false;
					var target = 'divulgazione';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if(!$scope.atto.riservato && (!$scope.atto.durataGiorni || isNaN($scope.atto.durataGiorni) || !Number(Number.isInteger($scope.atto.durataGiorni)))){
							res = true;
						}else if($scope.scenariDisabilitazione && $scope.scenariDisabilitazione.indexOf('DatiPubblicazioneModificabili') > -1){
							$scope.isPubblicazione = true;
							if($scope.atto.durataGiorni && $scope.atto.durataGiorni < 5){
								res = true;
							}else if(!$scope.atto.dataInizioPubblicazionePresunta || !$scope.atto.dataFinePubblicazionePresunta){
								res = true;
							}else{
								res = $scope.checkWarnDatePubblicazione();
							}
						}
						if( !$scope.isVisibleRiferimentinormativi() ){
							if($scope.atto.riservato == null ){
								res = true;
							}
							if(!$scope.atto.riservato && $scope.atto.pubblicazioneIntegrale == null ){
								res = true;
							}
							if($scope.atto.pubblicazioneTrasparenzaNolimit === null){
								res = true;
							}
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriPubblicazioneSemplificata = function(){
					var res = false;
					var target = 'divulgazioneSemplificata';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						if( !$scope.isVisibleRiferimentinormativi() ){
							if($scope.atto.riservato == null ){
								res = true;
							}
							if(!$scope.atto.riservato && $scope.atto.pubblicazioneIntegrale == null ){
								res = true;
							}
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatoriBeneficiari = function(){
					var res = false;
					var target = 'schede';
					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						$log.debug('verificaCampiObbligatoriBeneficiari',$scope.atto.beneficiari);
						if($scope.atto.tipoAdempimento && $scope.atto.tipoAdempimento.beneficiarioRequired == true && (!$scope.atto.beneficiari || $scope.atto.beneficiari.length < 1) && (!$scope.atto.allegatoBeneficiari || $scope.atto.allegatoBeneficiari === false)){
							res = true;
						}
						if(res == false && $scope.atto.beneficiari && $scope.atto.beneficiari.length > 0){
							for (var i = 0; i < $scope.atto.beneficiari.length; i++) {
								if(angular.isDefined($scope.atto.beneficiari[i].tipoSoggetto) && $scope.atto.beneficiari[i].tipoSoggetto != null){
									if(($scope.atto.beneficiari[i].email == undefined || $scope.atto.beneficiari[i].email == null || $scope.atto.beneficiari[i].email.trim() == '') && ($scope.atto.beneficiari[i].pec == undefined || $scope.atto.beneficiari[i].pec == null || $scope.atto.beneficiari[i].pec.trim() == '')){
										res = true;
									}
									else if($scope.atto.beneficiari[i].tipoSoggetto == 'PRIVATO'){
										if($scope.atto.beneficiari[i].codiceFiscale == undefined || $scope.atto.beneficiari[i].codiceFiscale == null || $scope.atto.beneficiari[i].codiceFiscale.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].nome == undefined || $scope.atto.beneficiari[i].nome == null || $scope.atto.beneficiari[i].nome.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].cognome == undefined || $scope.atto.beneficiari[i].cognome == null || $scope.atto.beneficiari[i].cognome.trim() == ''){
											res = true;
										}
									}
									else if($scope.atto.beneficiari[i].tipoSoggetto == 'PUBBLICO'){
										if($scope.atto.beneficiari[i].codiceFiscale == undefined || $scope.atto.beneficiari[i].codiceFiscale == null || $scope.atto.beneficiari[i].codiceFiscale.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].denominazione == undefined || $scope.atto.beneficiari[i].denominazione == null || $scope.atto.beneficiari[i].denominazione.trim() == ''){
											res = true;
										}
									}
									else if($scope.atto.beneficiari[i].tipoSoggetto == 'IMPRESA_DITTA'){
										if($scope.atto.beneficiari[i].partitaIva == undefined || $scope.atto.beneficiari[i].partitaIva == null || $scope.atto.beneficiari[i].partitaIva.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].denominazione == undefined || $scope.atto.beneficiari[i].denominazione == null || $scope.atto.beneficiari[i].denominazione.trim() == ''){
											res = true;
										}
									}
									else if($scope.atto.beneficiari[i].tipoSoggetto == 'DITTA_INDIVIDUALE'){
										if($scope.atto.beneficiari[i].partitaIva == undefined || $scope.atto.beneficiari[i].partitaIva == null || $scope.atto.beneficiari[i].partitaIva.trim() == '' || $scope.atto.beneficiari[i].codiceFiscale == undefined || $scope.atto.beneficiari[i].codiceFiscale == null || $scope.atto.beneficiari[i].codiceFiscale.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].nome == undefined || $scope.atto.beneficiari[i].nome == null || $scope.atto.beneficiari[i].nome.trim() == ''){
											res = true;
										}else if($scope.atto.beneficiari[i].cognome == undefined || $scope.atto.beneficiari[i].cognome == null || $scope.atto.beneficiari[i].cognome.trim() == ''){
											res = true;
										}
									}

									if ($scope.atto.beneficiari[i].codiceFiscale) {
										if ($scope.atto.beneficiari[i].tipoSoggetto == 'PUBBLICO') {
											if ($scope.atto.beneficiari[i].codiceFiscale.trim().length != 11) {
												res = true;
											}
										}
										else if ($scope.atto.beneficiari[i].codiceFiscale.trim().length != 16 ) {
											res = true;
										}
									}
									if ($scope.atto.beneficiari[i].partitaIva && ($scope.atto.beneficiari[i].partitaIva.trim().length != 11 )) {
										res = true;
									}
								}
								else{
									res = true;
								}

								if(res == true){
									break;
								}
							}
						}
					}
					return res;
				};

				$scope.isDisabledDataScadenzaContabile = function() {
					if ($scope.taskBpm && $scope.taskBpm.idAssegnatario && $scope.scenariDisabilitazione &&
						$scope.scenariDisabilitazione.indexOf('DataScadenzaContabileEditabile') > -1) {
						return false;
					}
					return true;
				};
				
				$scope.areRequiredImportiEntrataUscita = function(){
					if ($scope.taskBpm && $scope.taskBpm.idAssegnatario && $scope.scenariDisabilitazione &&
						$scope.scenariDisabilitazione.indexOf('ImportiContabiliObbligatori') > -1) {
						return true;
					}
					return false;
				};

				$scope.isDisabledImportoEntrata = function(){
					if ($scope.taskBpm && $scope.taskBpm.idAssegnatario && $scope.scenariDisabilitazione &&
						$scope.scenariDisabilitazione.indexOf('ImportiEntrataUscitaEditabili') > -1 &&
						$scope.atto.tipoIter.codice != $rootScope.ITER_SENZA_VERIFICA_CONTABILE &&
						($scope.atto.tipoAtto.codice == 'DL' || !$scope.atto.datiContabili.datiRicevuti)) {
						return false;
					}
					return true;
				};
				
				$scope.resetNascondiBeneficiariMovimenti = function(){
                    if ($scope.atto && $scope.atto.datiContabili) {
                        $scope.atto.datiContabili.nascondiBeneficiariMovimentiAtto = false;
                    }
                    return false;
                };

				$scope.isDisabledImportoUscita = function(){
					if ($scope.taskBpm && $scope.taskBpm.idAssegnatario && $scope.scenariDisabilitazione &&
						$scope.scenariDisabilitazione.indexOf('ImportiEntrataUscitaEditabili') > -1 &&
						$scope.atto.tipoIter.codice != $rootScope.ITER_SENZA_VERIFICA_CONTABILE &&
						!$scope.atto.datiContabili.datiRicevuti) {
						return false;
					}
					return true;
				};

				$scope.isDisabledDatiContabili = function(){
					if ($scope.taskBpm && $scope.taskBpm.idAssegnatario && $scope.scenariDisabilitazione &&
						($scope.scenariDisabilitazione.indexOf('DatiContabiliEditabili') > -1 || $scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0) &&
						$scope.atto.tipoIter.codice != $rootScope.ITER_SENZA_VERIFICA_CONTABILE) {
						return false;
					}
					return true;
				};

				$scope.verificaCampiObbligatoriSezioneContabile = function(){
					var res = false;
					var target = 'dichiarazioni';

					if($scope.atto && $scope.atto.contabileOggetto && $scope.atto.contabileOggetto.length > 500){
						res = true;
					}
					
					if($scope.atto && $scope.areRequiredImportiEntrataUscita() && 
						(!$scope.atto.datiContabili || ((!$scope.atto.datiContabili.importoEntrata && $scope.atto.datiContabili.importoEntrata!==0) && (!$scope.atto.datiContabili.importoUscita && $scope.atto.datiContabili.importoUscita!==0))
						)
					){
						res = true;
					}

					if( $scope.visibilitaSezioni[target.toLowerCase()] ){
						/**
						 if($scope.atto.tipoIter != null && $scope.atto.tipoIter.id > 0){
			if($scope.atto.dichiarazioni.testo == null || $scope.atto.dichiarazioni.testo == ""){
				res = true;
				$log.debug('manca dichiarazioni');
			}
		}
						 **/
						if($scope.atto.tipoIter != null && $scope.atto.tipoIter.codice != $rootScope.ITER_SENZA_VERIFICA_CONTABILE ){
							if(!$scope.atto.contabileOggetto || !$scope.atto.contabileOggetto.trim()){
								res = true;
								$log.debug('manca contabileOggetto');
							}

							/*
			if($scope.atto.contabileImporto == null || $scope.atto.contabileImporto == ""){
				res = true;
				$log.debug('manca contabileImporto');
			}
			if(($scope.movimentiCont == null) && ($scope.liquidazioniCont == null)) {
				res = true;
				$scope.visErrMovimenti = true;
				$log.debug('mancano i movimeti contabili');
			}
			else {
				$scope.visErrMovimenti = false;
			}*/
							/**
							 if($scope.movimentiCont == null || $scope.movimentiCont.length == 0 ){
				res = true;
				$log.debug('mancano i movimeti contabili');
			}
							 if($scope.atto.informazioniAnagraficoContabili.testo == null || $scope.atto.informazioniAnagraficoContabili.testo == ""){
				res = true;
				$log.debug('manca informazioniAnagraficoContabili');
			}
							 if($scope.atto.adempimentiContabili.testo == null || $scope.atto.adempimentiContabili.testo == ""){
				res = true;
				$log.debug('manca adempimentiContabili');
			}
							 **/
						}
					}
					return res;
				};

				$scope.verificaCampiObbligatori = function(){
					var res = false;

					if($scope.verificaCampiObbligatoriDatiIdentificativi() || $scope.verificaCampiObbligatoriAssegnazioneIncarichi()
						|| $scope.verificaCampiObbligatoriSottoscrizioni()
						|| $scope.verificaCampiObbligatoriLR1508() || $scope.verificaCampiObbligatoriDLGS33()
						|| $scope.verificaCampiObbligatoriPreambolo() || $scope.verificaCampiObbligatoriMotivazione()
						|| $scope.verificaCampiObbligatoriDispositivo() || $scope.verificaCampiObbligatoriDomanda() || $scope.verificaCampiObbligatoriPubblicazione()
						|| $scope.verificaCampiObbligatoriDatiIdentificativiConsiglio() || $scope.verificaCampiObbligatoriPubblicazioneSemplificata()
						|| $scope.verificaCampiObbligatoriDatiIdentificativiVerbale()) {
						res = true;
					}
					$log.debug('res='+res);
					return res;
				};

				$scope.verificaEmanante = function(){
					var res = false;
					if($scope.atto.emananteProfilo && $scope.profiloResponsabile && $scope.profiloResponsabile.id != $scope.atto.emananteProfilo.id )
						res = true;

					return res;

				};

				$scope.verificaSottoMateria = function(){
					var res = false;
					//$log.debug('$scope.atto.tipoMateria.id', $scope.atto.tipoMateria.id);
					//$log.debug("$scope.tipomaterias",$scope.tipomaterias);
					//$log.debug('sottoMaterie.length > 0', ($scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie[1]));
					if($scope.atto.tipoMateria != undefined && $scope.atto.tipoMateria != null && $scope.atto.materia!=undefined && $scope.atto.materia != null && $scope.tipomaterias!=undefined && $scope.tipomaterias!=null && $scope.tipomaterias && $scope.tipomaterias[$scope.atto.tipoMateria.id]!=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie != undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie != null && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id] != undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie != undefined && $scope.first($scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie) != null){
						res = true;
					}

					return res;

				};

				$scope.verificaMateria = function(){
					var res = false;
					if($scope.atto.materia == null && $scope.atto.tipoMateria != null && $scope.tipomaterias && $scope.tipomaterias[$scope.atto.tipoMateria.id]!=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie!=undefined && $scope.first($scope.tipomaterias[$scope.atto.tipoMateria.id].materie) != null){
						res = true;
					}

					return res;

				};

				$scope.modelloHtmlsAtto = [];

				$scope.checkImpersonificaFirma = function (decisione) {
					if (decisione && decisione.codiceAzioneUtente &&
						( (decisione.codiceAzioneUtente.toLowerCase().indexOf('firma') > -1) ||
							(decisione.codiceAzioneUtente.toLowerCase().indexOf('visto') > -1) ) ) {
						if ($rootScope.profiloOrigine && $rootScope.profiloOrigine.id > 0) {
							return false;
						}
					}
					return true;
				};

				$scope.callDecisione = function (indexSezione,decisione) {
					
					TipoAtto.get({id: $scope.atto.tipoAtto.id}, function(result) {
						$scope.sezioniAtto = result.sezioni;
					});
					
					if(decisione && !decisione.sempreAttiva && decisione.completaTask){
						if($scope.verificaCampiObbligatori()){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Occorre inserire tutti i dati obbligatori prima di poter procedere con l&#39; operazione richiesta."});
							return;
						}
					}
					$scope.taskLoading = true;

					if (!decisione.completaTask && !(decisione.codiceAzioneUtente && decisione.codiceAzioneUtente.length > 1)) {
						$rootScope.$broadcast('globalLoadingStart');
					}
					
					if($scope.atto.datiContabili != null && $scope.atto.datiContabili.includiMovimentiAtto == undefined){
						$scope.atto.datiContabili.includiMovimentiAtto = !($scope.atto.tipoAtto.codice == 'DG' || $scope.atto.tipoAtto.codice == 'DC' || $scope.atto.tipoAtto.codice == 'DPC' || $scope.atto.tipoAtto.codice == 'DD' || $scope.atto.tipoAtto.codice == 'DL');
					}
					if($scope.atto.datiContabili != null && $scope.atto.datiContabili.nascondiBeneficiariMovimentiAtto == undefined){
                        $scope.atto.datiContabili.nascondiBeneficiariMovimentiAtto = false;
                    }

					try {
						/*
		 * Per ATTICO esclusa gestione sottoscrittori
		 *
	    if(angular.isDefined($scope.atto.sottoscrittori) && $scope.atto.sottoscrittori!=undefined){
		    for(var i = 0; i <  $scope.atto.sottoscrittori.length; i++){
			    $scope.atto.sottoscrittori[i].ordineFirma = Number(i +1);
			}
	    }
	    */

						if($scope.haveToReloadDtoWorkflow(decisione.codiceAzioneUtente)){
							$scope.dtoWorkflow = {campi:{} , atto: $scope.atto };
						}
						$scope.dtoFdr = {codiceFiscale:'', password:'' , otp: '',
							filesId: [], filesOmissis: [], filesAdozione: [],
							filesParereId: [], filesScheda: [], filesAttoInesistente: [], filesRelataPubblicazione: []};
						var valid = true;
						var alertMessage = '';
						$scope.messaggioPerStep = null;
						var nomeTask = '';
						if ($scope.taskBpm) {
							nomeTask = $scope.taskBpm.nomeVisualizzato;
						}
						if( decisione.completaTask && 
								($scope.atto.tipoAtto.codice == 'DG' || $scope.atto.tipoAtto.codice == 'DC' || $scope.atto.tipoAtto.codice == 'DPC')
								&& nomeTask == "Coordinamento testo"
								){
							$scope.messaggioPerStep = "L'esito della votazione non potrà più essere modificato a meno che l'atto non venga restituito al Coordinamento Testo.";
						}

						if("INSERIMENTO_PARERE" === decisione.codiceAzioneUtente ||
							"VISTINO_CONTABILE"  === decisione.codiceAzioneUtente ||
							"VISTINO_CONTABILE"  === decisione.codiceAzioneUtente ||
							"VISTO_POSITIVO_CON_NOTE"  === decisione.codiceAzioneUtente ||
							"VISTO_POSITIVO_VERIFICA_PROPOSTA"  === decisione.codiceAzioneUtente ||
							"RESTITUISCI_PROPONENTE"  === decisione.codiceAzioneUtente ||
							"RESTITUISCI_SEGRETERIA_CONSIGLIO"  === decisione.codiceAzioneUtente ||
							"RITIRO_CONTABILE"=== decisione.codiceAzioneUtente ||
							"RESTITUZIONE_COORDINAMENTO_TESTO"  === decisione.codiceAzioneUtente ||
							"RIMANDA_SCRITTURA"  === decisione.codiceAzioneUtente ||
							"INSERIMENTO_PARERE_CONTABILE" === decisione.codiceAzioneUtente ||
							"RITIRO"  === decisione.codiceAzioneUtente ||
							"RITIRO_ATTO"  === decisione.codiceAzioneUtente ||
							"ANNULLA_ATTO"  === decisione.codiceAzioneUtente ||
							"ANNULLA_ATTO_RESP_CONT"  === decisione.codiceAzioneUtente ||
							"RITIRO_CONTABILE_ISTRUTTORE"=== decisione.codiceAzioneUtente ||
							"RITIRO_FASE_ISTRUTTORIA"=== decisione.codiceAzioneUtente ||
							"GENERA_FIRMA_REG_TECN"=== decisione.codiceAzioneUtente ||
							"GENERA_PAR_REG"=== decisione.codiceAzioneUtente
						) {
							if(["GENERA_FIRMA_REG_TECN", "GENERA_PAR_REG"].indexOf(decisione.codiceAzioneUtente) > -1){
								$scope.modelloHtml = [];
							}
							$scope.inizializzaParere(decisione);
						}
						else if("GENERA_FIRMA_REG_CONT"=== decisione.codiceAzioneUtente || "VISTO_POSITIVO"  === decisione.codiceAzioneUtente || "VISTO_POSITIVO_GENERA_ATTO"  === decisione.codiceAzioneUtente || "VISTO_POSITIVO_GENERA_ATTO_DL"  === decisione.codiceAzioneUtente || "PRESA_VISIONE"  === decisione.codiceAzioneUtente){
							if( ($scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_TECNICO") && "VISTO_POSITIVO"  === decisione.codiceAzioneUtente)
								|| ($scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_TECNICO_REG_MOD") && "VISTO_POSITIVO"  === decisione.codiceAzioneUtente)
								|| ($scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_CONTABILE") && "GENERA_FIRMA_REG_CONT"  === decisione.codiceAzioneUtente)
								|| ($scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_CONTABILE_REG_MOD") && "GENERA_FIRMA_REG_CONT"  === decisione.codiceAzioneUtente)
								){
								// Controllo che il parere sia stato espresso
								valid = false;
								var regMod = $scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_TECNICO_REG_MOD") || $scope.decisioni.some(d => d.codiceAzioneUtente == "INSERIMENTO_PARERE_RESP_CONTABILE_REG_MOD");
								alertMessage = "Per proseguire, &#232; necessario inserire un parere.";
								if($scope.atto.pareri){
									for(var i=0; i<$scope.atto.pareri.length; i++ ) {
										var p = $scope.atto.pareri[i];
										if (p.annullato) {
											continue;
										}
										if(!regMod){
											if(( (p.tipoAzione.codice == 'parere_resp_contabile') && "GENERA_FIRMA_REG_CONT"=== decisione.codiceAzioneUtente)
													|| ( (p.tipoAzione.codice == 'parere_resp_tecnico') && "VISTO_POSITIVO"  === decisione.codiceAzioneUtente)){
													valid = p.parereSintetico && p.parereSintetico.trim().length > 0;
												}
										}else{
											if(( (p.tipoAzione.codice == 'parere_resp_contabile_regolarita_modifiche') && "GENERA_FIRMA_REG_CONT"=== decisione.codiceAzioneUtente)
													|| ( (p.tipoAzione.codice == 'parere_resp_tecnico_regolarita_modifiche') && "VISTO_POSITIVO"  === decisione.codiceAzioneUtente)){
													valid = p.parereSintetico && p.parereSintetico.trim().length > 0;
												}
										}
									}
								}
								if(!valid){
									$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
									/*alert(alertMessage);*/
								}
								else {
									if("GENERA_FIRMA_REG_CONT"=== decisione.codiceAzioneUtente){
										$scope.modelloHtml = [];
									}
									$scope.inizializzaParere(decisione);
								}
							}
							else {
								if("GENERA_FIRMA_REG_CONT"=== decisione.codiceAzioneUtente || "VISTO_POSITIVO_GENERA_ATTO"=== decisione.codiceAzioneUtente || "VISTO_POSITIVO_GENERA_ATTO_DL"=== decisione.codiceAzioneUtente){
									$scope.modelloHtml = [];
								}
								$scope.inizializzaParere(decisione);
							}
						}
						else if("AGGIORNA_TRASPARENZA" === decisione.codiceAzioneUtente){
							valid = $scope.atto.oggetto != null;
						}
						else if("ALTRO_UFFICIO_CONTABILE" === decisione.codiceAzioneUtente){
							for(var i = 0; i <  $scope.incarichi.length; i++){
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_ISTRUTTORE_CONTABILE_ALTRO_UFFICIO"){
									if(!$scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo){
										valid = false;
										$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Selezionare l'Istruttore Contabile Altro Ufficio nella sezione Visti, Pareri e Firme."});
										/*alert("Selezionare l'Istruttore Contabile Altro Ufficio nella sezione Visti, Pareri e Firme.");*/
									}
								}
							}
							if(valid){
								$scope.inizializzaParere(decisione);
							}else{

							}
						}else if("INVIA_A_ISTRUTTORE_CONTABILE_PROPONENTE" === decisione.codiceAzioneUtente){
							for(var i = 0; i <  $scope.incarichi.length; i++){
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "ISTRUTTORE_CONTABILE_PROPONENTE"){
									if(!$scope.incarichi[i].listConfigurazioneIncaricoAooDto || !$scope.incarichi[i].listConfigurazioneIncaricoAooDto.length > 0 || !$scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo){
										valid = false;
										$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Selezionare l'Ufficio Contabile Proponente nella sezione Visti, Pareri e Firme."});
										/*alert("Selezionare l'Istruttore Contabile Proponente nella sezione Visti, Pareri e Firme.");*/
									}
								}
							}
						}
						else if("INVIA_DATI_CONTABILI" === decisione.codiceAzioneUtente){
							if($scope.atto && $scope.areRequiredImportiEntrataUscita() && 
								(!$scope.atto.datiContabili || ((!$scope.atto.datiContabili.importoEntrata && $scope.atto.datiContabili.importoEntrata!==0) && (!$scope.atto.datiContabili.importoUscita && $scope.atto.datiContabili.importoUscita!==0))
								)
							){
								valid = false;
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Per effettuare l'invio dei dati al sistema contabile occorre specificare almeno uno tra l'importo di entrata e quello di uscita all'interno della Sezione Contabile."});
							}else if(!$scope.atto || !$scope.atto.contabileOggetto || !$scope.atto.contabileOggetto.trim()){
								valid = false;
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Per effettuare l'invio dei dati al sistema contabile occorre specificare l'oggetto all'interno della Sezione Contabile."});
							}else if($scope.atto && $scope.atto.contabileOggetto && $scope.atto.contabileOggetto.length > 500){
								valid = false;
								$rootScope.showMessage({title:'Attenzione!', okButton:true, body:"L\u0027oggetto contabile eccede la lunghezza massima consentita (500 caratteri). Si prega di modificare l\u0027oggetto e riprovare l\u0027invio dei dati."});
							}else if($scope.verificaCampiObbligatoriSezioneContabile()){
								valid = false;
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Prima di procedere occorre correggere gli errore di validazione presenti nella Sezione Contabile."});
							}
						}
						else if("VISTO_CONTABILE" === decisione.codiceAzioneUtente || "VISTO_CONTABILE_RISCONTRO" === decisione.codiceAzioneUtente){
							for(var i = 0; i <  $scope.incarichi.length; i++)
							{
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE" || $scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DC_VERIFICA_RESPONSABILE_CONTABILE")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										/*alert("Selezionare il Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.");*/
										$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Selezionare il Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme."});
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Selezionare la qualifica del Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme."});
										/*alert("Selezionare la qualifica del Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.");*/
									}
								}
							}
							if(valid){
								$scope.inizializzaParere(decisione);
							}

						}
						else if("VISTO_CONTABILE_ISTRUTTORE" === decisione.codiceAzioneUtente){
							alertMessage = "ATTENZIONE!!! Cliccando sul pulsante "+decisione.descrizione+" l'atto sarà inviato ESCLUSIVAMENTE al Responsabile Contabile.<br/>";
							for(var i = 0; i <  $scope.incarichi.length; i++)
							{
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE" || $scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DC_VERIFICA_RESPONSABILE_CONTABILE")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage += "\xC8 obbligatorio selezionare il Responsabile Contabile nella sezione Visti, Pareri e Firme.<br/>";
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										alertMessage += "\xC8 obbligatorio selezionare la qualifica del Responsabile Contabile nella sezione Visti, Pareri e Firme.<br/>";
									}
								}
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_ISTRUTTORE_CONTABILE_ALTRO_UFFICIO")
								{
									if($scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo)
									{
										valid = false;
										alertMessage += "Non deve essere valorizzato il campo \"Istruttore Contabile Altro Ufficio\" nella sezione Visti, Pareri e Firme.<br/>";
									}
								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_SECONDO_ISTRUTTORE_CONTABILE")
								{
									if($scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo)
									{
										valid = false;
										alertMessage += "Non deve essere valorizzato il campo \"Secondo Istruttore Contabile\" nella sezione Visti, Pareri e Firme.<br/>";
									}
								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE_VISTINO")
								{
									if($scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage += "Non deve essere valorizzato il campo \"Responsabile incaricato al Vistino Contabile\" nella sezione Visti, Pareri e Firme.<br/>";
									}
								}

							}
							if(valid){
								$scope.inizializzaParere(decisione);
							}else{
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
								/*alert(alertMessage);*/
							}
						}
						else if("VISTO_CONTABILE_SECONDO_ISTRUTTORE" === decisione.codiceAzioneUtente){
							for(var i = 0; i <  $scope.incarichi.length; i++)
							{
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE" || $scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DC_VERIFICA_RESPONSABILE_CONTABILE")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage += "Selezionare il Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.\n";
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										alertMessage += "Selezionare la qualifica del Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.\n";
									}
								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE_VISTINO")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage = alertMessage + "Selezionare il Responsabile incaricato al Vistino Contabile nella sezione Visti, Pareri e Firme.\n";
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										alertMessage = alertMessage + "Selezionare la qualifica del Responsabile incaricato al Vistino Contabile nella sezione Visti, Pareri e Firme.\n";
									}
								}
							}
							if(valid){
								$scope.inizializzaParere(decisione);
							}else{
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
								/*alert(alertMessage);*/
							}
						}
						else if("SECONDO_UFFICIO_CONTABILE" === decisione.codiceAzioneUtente){
							alertMessage = "ATTENZIONE!!! Cliccando sul pulsante \"Vista e inoltra Secondo Ufficio\" l'atto sarà inviato ad un secondo ufficio contabile per l'eventuale apposizione di un vistino.\n";
							for(var i = 0; i <  $scope.incarichi.length; i++)
							{
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE" || $scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DC_VERIFICA_RESPONSABILE_CONTABILE")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage = "\xC8 obbligatorio selezionare il Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.\n";
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										alertMessage = alertMessage + "\xC8 obbligatorio selezionare la qualifica del Responsabile Contabile Firmatario nella sezione Visti, Pareri e Firme.\n";
									}
								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_SECONDO_ISTRUTTORE_CONTABILE")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo)
									{
										valid = false;
										alertMessage = alertMessage + "\xC8 obbligatorio selezionare il Secondo Istruttore Contabile nella sezione Visti, Pareri e Firme.\n";
									}
								}

								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_VERIFICA_RESPONSABILE_CONTABILE_VISTINO")
								{
									if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].idProfilo)
									{
										valid = false;
										alertMessage = alertMessage + "\xC8 obbligatorio selezionare il Responsabile incaricato al Vistino Contabile nella sezione Visti, Pareri e Firme.\n";
									}else if(!$scope.incarichi[i].listConfigurazioneIncaricoProfiloDto[0].qualificaProfessionaleDto.id){
										valid = false;
										alertMessage = alertMessage + "\xC8 obbligatorio selezionare la qualifica del Responsabile incaricato al Vistino Contabile nella sezione Visti, Pareri e Firme.\n";
									}
								}
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "DIR_ISTRUTTORE_CONTABILE_ALTRO_UFFICIO"){
									if($scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo){
										valid = false;
										alertMessage = alertMessage + "Non deve essere valorizzato il campo \"Istruttore Contabile Altro Ufficio\" nella sezione Visti, Pareri e Firme.\n";
									}
								}
							}
							if(valid){
								$scope.inizializzaParere(decisione);
							}
							else{
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
								/*alert(alertMessage);*/
							}
						}
						else if("INSERIMENTO_RISPOSTA_ATTI_DI_CONSIGLIO" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_INTERROGAZIONI" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_QT" === decisione.codiceAzioneUtente
						){
							$scope.inizializzaRispostaAttiDiConsiglio(decisione);

						}
						else if("INSERIMENTO_PARERE_COMMISSIONE" === decisione.codiceAzioneUtente ||
							"PARERE_NON_ESPRESSO" === decisione.codiceAzioneUtente
						){
							$scope.inizializzaParereCommissione(decisione);
						}
						else if("INSERIMENTO_PARERE_RESP_TECNICO" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereRespTecnico(decisione);
						}
						else if("INSERIMENTO_PARERE_RESP_CONTABILE" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereRespContabile(decisione);
						}
						else if("INSERIMENTO_PARERE_RESP_TECNICO_REG_MOD" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereRespTecnico(decisione);
						}
						else if("INSERIMENTO_PARERE_RESP_CONTABILE_REG_MOD" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereRespContabile(decisione);
						}
						else if("INSERIMENTO_PARERE_QUARTIERE_REVISORI" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereConsQuartRevCont(decisione);

						}
						else if("INSERIMENTO_RELATORE" === decisione.codiceAzioneUtente){
							$scope.inizializzaParereConSoloRelatore(decisione);
						}
						else if("GENERA_ATTO" === decisione.codiceAzioneUtente ||
							"GENERA_ATTO_DL" === decisione.codiceAzioneUtente ||
							"NUMERA_E_GENERA_ATTO" === decisione.codiceAzioneUtente ||
							"FIRMA_ATTO" === decisione.codiceAzioneUtente ||
							"NUMERA_GENERA_FIRMA_ATTO" === decisione.codiceAzioneUtente ||
							"GENERA_FIRMA_ATTO" === decisione.codiceAzioneUtente ||
							"GENERA_PAR_REG" === decisione.codiceAzioneUtente){
							
							$scope.modelloHtml = [];
							$scope.visualizzaMsgPerRespContabile = false;
							if("FIRMA_ATTO" === decisione.codiceAzioneUtente && decisione.descrizioneAlternativa &&
									decisione.descrizioneAlternativa.startsWith('Resp. Contabile') && 
									$scope.atto.tipoIter.codice == $rootScope.ITER_CON_VERIFICA_CONTABILE &&
									($scope.atto.tipoAtto.codice == 'DG' || $scope.atto.tipoAtto.codice == 'DC' || $scope.atto.tipoAtto.codice == 'DPC')  ){
								$scope.visualizzaMsgPerRespContabile = true;
							}
						}
						else if ("CONFERMA_PARERE_COMMISSIONE" === decisione.codiceAzioneUtente) {
							// Controllo che il parere sia stato espresso
							valid = false;
							alertMessage = "Per proseguire, &#232; necessario inserire un parere. NB: Tra i pareri sintetici disponibili, &#232; incluso anche quello \"Non Espresso\"";
							if($scope.atto.pareri){
								for(var i=0; !$scope.dtoWorkflow.parere &&  i<$scope.atto.pareri.length; i++ ) {
									var p = $scope.atto.pareri[i];
									if (p.annullato) {
										continue;
									}
									if(p.origine == "C" && p.profilo.id == $rootScope.profiloattivo.id){
										valid = p.parereSintetico && p.parereSintetico.trim().length > 0;
									}
								}
							}

							if(!valid){
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
								/*alert(alertMessage);*/
							}
						}

						if("GENERA_ATTO_DL" === decisione.codiceAzioneUtente || "GENERA_ATTO" === decisione.codiceAzioneUtente || "INVIO_A_CONTABILE_ISTRUTTORE"  === decisione.codiceAzioneUtente || "VERIFICA_DATI_CONTABILI_NO_OBBL" === decisione.codiceAzioneUtente || "GENERA_PAR_REG" === decisione.codiceAzioneUtente){
							for(var i = 0; i <  $scope.incarichi.length; i++)
							{
								if($scope.incarichi[i].dettaglio.configurazioneTaskDto.codice === "ISTRUTTORE_CONTABILE_PROPONENTE")
								{
									if($scope.incarichi[i].listConfigurazioneIncaricoAooDto && $scope.incarichi[i].listConfigurazioneIncaricoAooDto[0] && $scope.incarichi[i].listConfigurazioneIncaricoAooDto[0].idAoo)
									{
										valid = false;
										alertMessage += "Nella sezione \"Visti, pareri e firme\" \u00E8 stato indicato un Ufficio Contabile Proponente: per inviare la proposta a tale ufficio \u00E8 necessario premere il pulsante \"Salva e Invia ad Ufficio Contabile Proponente\".<br />Se non si vuole inviare la proposta all\u0027Ufficio Contabile Proponente, \u00E8 necessario eliminare tale ufficio dalla sezione \"Visti, pareri e firme\".";
										break;
									}
								}
							}
							if(!valid){
								$rootScope.showMessage({title:'Attenzione', okButton:true, body:alertMessage});
							}
						}

						/*
	     * Per ATTICO cambiata convenzione su azioni utente
	     *
	    if("UC_CIFRA2_011" === decisione.codiceAzioneUtente || "UC_CIFRA2_012.a" === decisione.codiceAzioneUtente){
			$scope.atto.tipoMateria.descrizione = $scope.tipomaterias[$scope.atto.tipoMateria.id].descrizione;
			if($scope.atto.materia!=undefined && $scope.atto.materia != null && $scope.atto.materia.id != undefined && $scope.atto.materia.id!=null && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie!=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie!=null && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id]!=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id]!=null){
				$scope.atto.materia.descrizione = $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].descrizione;
			}else{
				$scope.atto.materia = null;
			}
			if($scope.atto.tipoMateria.id != null && $scope.atto.materia!=undefined && $scope.atto.materia.id != undefined && $scope.atto.materia.id != null && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie!=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie!=null && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id] !=undefined && $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id]!=null && $scope.first($scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie)!=undefined && $scope.first($scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie) != null){
				$scope.atto.sottoMateria.descrizione = $scope.tipomaterias[$scope.atto.tipoMateria.id].materie[$scope.atto.materia.id].sottoMaterie[$scope.atto.sottoMateria.id].descrizione;
			}else{
				$scope.atto.sottoMateria = null;
			}

	      if($scope.atto.sottoscrittori.length<1){
	        valid = false;
	        alert("Inserire almeno un sottoscrittore atto.");
	      }
	      var modelliList = [];
	      $scope.modelliSchedeAnagraficheContabili = [];
	      for(var i = 0; i <  $scope.modelloHtmls.length; i++){
	    	  if($scope.modelloHtmls[i].tipoDocumento.codice == 'tipo_provvedimento_determinazione' || $scope.modelloHtmls[i].tipoDocumento.codice == 'tipo_proposta_determinazione'){
	    		  modelliList.push($scope.modelloHtmls[i]);
	    	  }
	      }
	      $scope.modelloHtmlsAtto = modelliList;
	   }
	   */

						if(valid){
							if (decisione.codiceAzioneUtente && decisione.codiceAzioneUtente.length > 1) {
								$scope.taskLoading = false;
								$scope.decisioneCorrente = decisione;
								$scope.documentiFirmatiDaCaricare = new Map();
								$('#mascheraWorkflowAtto').modal('show');
							}
							else {
								if(decisione.completaTask){
									$scope.taskLoading = false;
									$scope.decisioneCorrente = decisione;
									$('#actionFormConfirmation').modal('show');;

								} else {
									$scope.salva(indexSezione,decisione);
								}
							}
						}
						else{
							$scope.taskLoading = false;
						}
					}
					catch(err){
						$log.debug("errore:"+ err.messages );
						$scope.taskLoading = false;
						$rootScope.$broadcast('globalLoadingEnd');
					}
				};

				$scope.haveToReloadDtoWorkflow = function(codiceDecisione){
					return !(
						(codiceDecisione == 'INSERIMENTO_RISPOSTA_ATTI_DI_CONSIGLIO' ||
							codiceDecisione == 'INSERIMENTO_RISPOSTA_INTERROGAZIONI' ||
							codiceDecisione == 'INSERIMENTO_RISPOSTA_QT' ||
							codiceDecisione == 'INSERIMENTO_RELATORE' ||
							codiceDecisione == 'INSERIMENTO_PARERE_COMMISSIONE' ||
							codiceDecisione == 'INSERIMENTO_PARERE_QUARTIERE_REVISORI' ||
							codiceDecisione == 'INSERIMENTO_PARERE_RESP_TECNICO' || 
							codiceDecisione == 'INSERIMENTO_PARERE_RESP_TECNICO_REG_MOD') &&
						$scope.dtoWorkflow!=undefined && $scope.dtoWorkflow!=null &&
						$scope.dtoWorkflow.parere != undefined && $scope.dtoWorkflow.parere!=null
					);
				};

				$scope.switchFirma = function (isFirma){
					$scope.firma = isFirma;
				}

				$scope.richiediOtp = function() {
					$scope.dtoFdr.codiceFiscale = $scope.profiloattivo.utente.codicefiscale;
					$scope.dtoFdr.errorCode='';
					$scope.dtoFdr.errorMessage='';
					$scope.dtoFdr.filesId=[];

					if (!$scope.dtoFdr.password || $scope.dtoFdr.password == '' ) {
						if ($scope.dtoWorkflow.dtoFdr && $scope.dtoWorkflow.dtoFdr.password) {
							$scope.dtoFdr.password = $scope.dtoWorkflow.dtoFdr.password;
						}
					}

					Atto.sendOTP({id:$scope.atto.id},$scope.dtoFdr, function(result) {
						ngToast.create(  { className: 'success', content:  'Richiesta invio OTP effettuata con successo'  } );
					}, function(error) {
						$scope.dtoFdr.password='';
						$scope.dtoFdr.otp='';
						$scope.dtoFdr.errorCode=error.data.errorCode;
						$scope.dtoFdr.errorMessage=error.data.errorMessage;
					});
				}

				$scope.salva = function (indexSezione, decisione){
					if(decisione && !decisione.sempreAttiva && decisione.completaTask){
						if($scope.verificaCampiObbligatori()){
							$rootScope.showMessage({title:'Attenzione', okButton:true, body:"Occorre inserire tutti i dati obbligatori prima di poter procedere con l&#39; operazione richiesta."});
							return;
						}
					}
					$rootScope.$broadcast('globalLoadingStart');
					try{
						//Check if final note exists
						if($scope.atto.finalNote){
							var atto = angular.copy($scope.atto);
							atto.listaNote = null;
							$scope.atto.listaNote.unshift({
								profilo: $scope.profiloattivo,
								atto: atto,
								taskId: $scope.taskBpmId,
								stato: $scope.atto.stato,
								data: new Date(),
								testo: angular.copy($scope.atto.finalNote),
							});
							$scope.atto.finalNote = null;
						}


						if("FIRMA_ATTO" === decisione.codiceAzioneUtente && $scope.firma) {
							//genera documenti e chiama ws di firma remota
							$scope.dtoFdr.codiceFiscale = $scope.profiloattivo.utente.codicefiscale;
							$scope.dtoFdr.errorMessage='';
							$scope.dtoFdr.errorCode='';
							$scope.dtoFdr.filesId=[];
							for(var i = 0; i <  $scope.generaElencoDocumentiDaFirmare.length; i++){
								$scope.dtoFdr.filesId.push($scope.generaElencoDocumentiDaFirmare[i].id);
							}
							//$scope.dtoFdr.modelloHtmlId=$scope.dtoWorkflow.campi['modelloHtmlId'].id;

							$scope.uploadok = true;

							Atto.firma({id:$scope.atto.id},$scope.dtoFdr, function(result) {
								//		Atto.generadocumentifirmati({id:$scope.atto.id},$scope.dtoFdr, function(result) {
								$scope.uploadok = false;
								//			$scope.atto.numeroAdozione = result.numeroAdozione;
								//			$scope.atto.dataAdozione = result.dataAdozione;
								$scope.executeSave(indexSezione,decisione);
							}, function(error) {
								$scope.taskLoading = false;
								$scope.uploadok = false;
								$scope.dtoFdr.password='';
								$scope.dtoFdr.otp='';
								$scope.dtoFdr.errorMessage=error.data.errorMessage;
								$scope.dtoFdr.errorCode=error.data.errorCode;
							});
						}
						else if("INSERIMENTO_RELATORE" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_ATTI_DI_CONSIGLIO" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_INTERROGAZIONI" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_QT" === decisione.codiceAzioneUtente){
							if($scope.dtoWorkflow.parere.profiloRelatore){
								if($scope.profilosComponentiGiunta && $scope.profilosComponentiGiunta.length > 0){
									for(var i = 0; i <  $scope.profilosComponentiGiunta.length; i++){
										if($scope.profilosComponentiGiunta[i].id == $scope.dtoWorkflow.parere.profiloRelatore.id){
											$scope.dtoWorkflow.parere.aoo = $scope.profilosComponentiGiunta[i].aoo;
											break;
										}
									}
								}
							}
							$scope.executeSave(indexSezione,decisione);
						}else if("AGGIORNA_TRASPARENZA" === decisione.codiceAzioneUtente){
							$scope.executeSave(indexSezione,decisione);
						}
						//carica file
						else {
							if($scope.schede && $scope.schede.length>0){
								$scope.uploadDocumenti($scope.documentiFirmatiDaCaricare, "documenti_firmati")
									.then($scope.uploadDocumenti($scope.documentiTrasparenzaDaCaricare, "documenti_trasparenza")
										.then(function() {
											$scope.documentiTrasparenzaDaCaricare = new Map();
											$scope.documentiFirmatiDaCaricare = new Map();
											Atto.schedatrasparenza({id:$scope.atto.id}, $scope.valoriSchedeDati,function(result){
												$scope.atto.valoriSchedeDati = result.valoriSchedeDati;
												$scope.executeSave(indexSezione,decisione);
											});
										}));
							}else{
								$scope.uploadDocumenti($scope.documentiFirmatiDaCaricare, "documenti_firmati")
									.then(function() {
										$scope.documentiFirmatiDaCaricare = new Map();
										$scope.documentiTrasparenzaDaCaricare = new Map();
										$scope.executeSave(indexSezione,decisione);
									});
							}
						}
					}catch(err){
						$rootScope.$broadcast('globalLoadingEnd');
					}
				};

				$scope.executeSave = function (indexSezione,decisione) {
					$rootScope.$broadcast('globalLoadingStart');
					try{
						var valid = true;
						$log.debug( "indexSezione_:"+ indexSezione );
						$log.debug("decisione:",decisione);

						if("INSERIMENTO_PARERE" === decisione.codiceAzioneUtente  || "INSERIMENTO_PARERE_CONTABILE" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_PARERE_COMMISSIONE" === decisione.codiceAzioneUtente || "INSERIMENTO_PARERE_QUARTIERE_REVISORI" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_PARERE_RESP_TECNICO" === decisione.codiceAzioneUtente || "INSERIMENTO_PARERE_RESP_TECNICO_REG_MOD" === decisione.codiceAzioneUtente){
							decisione.variableValue = $scope.dtoWorkflow.parere.parereSintetico;
						}

						if("PARERE_NON_ESPRESSO" === decisione.codiceAzioneUtente){
							decisione.variableValue = 'SI';
						}

						if("INSERIMENTO_RISPOSTA_ATTI_DI_CONSIGLIO" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_INTERROGAZIONI" === decisione.codiceAzioneUtente ||
							"INSERIMENTO_RISPOSTA_QT" === decisione.codiceAzioneUtente){
							decisione.variableValue = $scope.dtoWorkflow.parere.parereSintetico;
						}

						if(valid){

							var isNotePresent = false;

							for ( var indexNota in $scope.atto.listaNote) {
								var atto = angular.copy($scope.atto);
								if($scope.atto.listaNote[indexNota].taskId==$scope.taskBpmId){
									$scope.atto.listaNote[indexNota].profilo = $scope.profiloattivo;
									$scope.atto.listaNote[indexNota].data = new Date();
									$scope.atto.listaNote[indexNota].stato =  $scope.atto.stato;
									$scope.atto.listaNote[indexNota].testo = $scope.atto.nota;
									$scope.atto.listaNote[indexNota].atto = atto;
									isNotePresent = true;
									break;
								}
							}

							if(!isNotePresent && $scope.atto.nota!=null){
								var atto = angular.copy($scope.atto);
								var nomeTask = '';
								if ($scope.taskBpm) {
									nomeTask = $scope.taskBpm.nomeVisualizzato;
								}
								var nuovaNota = {
									taskId: $scope.taskBpmId,
									taskName: nomeTask,
									profilo: $scope.profiloattivo,
									data: new Date(),
									stato:  $scope.atto.stato,
									testo: $scope.atto.nota,
									atto: atto
								};
								$scope.atto.listaNote.push(nuovaNota);
							}

							$scope.taskLoading = true;
							if ($scope.atto.id != null && $scope.atto.id > 0) {
								$scope.dtoWorkflow.atto = $scope.atto;
								$scope.dtoWorkflow.decisione = decisione;
								$scope.dtoWorkflow.incarichi = convertIncarichiDto();

								if("GENERA_ATTO"===decisione.codiceAzioneUtente ||
									"VISTO_POSITIVO_GENERA_ATTO"===decisione.codiceAzioneUtente ||
									"GENERA_ATTO_DL"===decisione.codiceAzioneUtente ||
									"VISTO_POSITIVO_GENERA_ATTO_DL"===decisione.codiceAzioneUtente ||
									"NUMERA_E_GENERA_ATTO"===decisione.codiceAzioneUtente ||
									"NUMERA_GENERA_FIRMA_ATTO"===decisione.codiceAzioneUtente ||
									"GENERA_FIRMA_ATTO"===decisione.codiceAzioneUtente ||
									"GENERA_FIRMA_REG_CONT" === decisione.codiceAzioneUtente ||
									"GENERA_FIRMA_REG_TECN" === decisione.codiceAzioneUtente ||
									"FIRMA_REG_TECN" === decisione.codiceAzioneUtente ||
									"GENERA_PAR_REG" === decisione.codiceAzioneUtente){
									$scope.dtoWorkflow.modelliHtmlIds = convertModelloHtmlDtoToIds();
								}

								if("GENERA_ATTO_DL"===decisione.codiceAzioneUtente) {
									$('#mascheraWorkflowAtto').modal('hide');
								}

								Atto.update({codiceDecisione:decisione.codiceAzioneUtente, taskId:$scope.taskBpmId},  $scope.dtoWorkflow ,
									function (result) {
										ngToast.create(  { className: 'success', content:  decisione.descrizione+' effettuata con successo'  } );
										if(decisione.codiceAzioneUtente == 'AGGIORNA_TRASPARENZA'){
											$state.go('jobTrasparenza');
										}else if(result != undefined && result!=null && result.attorevoca != undefined && result.attorevoca!=null && result.attorevoca != ''){
											$scope.redirectToAttoRevoca(result.attorevoca);
										}else{
											$scope.onAfterTaskUpdate(result.id, result.taskId, indexSezione);
										}
										$('#mascheraWorkflowAtto').modal('hide');
									}, function(error) {
										$scope.taskLoading = false;
										$scope.uploadok = false;
										if($scope.dtoWorkflow && $scope.dtoWorkflow.dtoFdr){
											$scope.dtoWorkflow.dtoFdr.password='';
											$scope.dtoWorkflow.dtoFdr.otp='';
											$scope.dtoWorkflow.dtoFdr.errorCode=error.data.errorCode;
											$scope.dtoWorkflow.dtoFdr.errorMessage=error.data.errorMessage;
										}
										else if($scope.dtoFdr){
											$scope.dtoFdr.password='';
											$scope.dtoFdr.otp='';
											$scope.dtoFdr.errorCode=error.data.errorCode;
											$scope.dtoFdr.errorMessage=error.data.errorMessage;
										}
									});
							}
							else {
								$scope.atto.tipoAtto = $scope.tipoAtto;

								if ($scope.tipoAtto.proponente){
									$scope.atto.aoo = $rootScope.profiloattivo.aoo;
								}


								$scope.atto.stato = 'Proposta in Predisposizione';

								Atto.save({}, $scope.atto,
									function (result, headers) {
										var id = result.id ;
										$log.debug(  "id:"+id);
										ngToast.create(  { className: 'success', content: decisione.descrizione+' effettuata con successo' } );
										$scope.onAfterTaskUpdate(result.id, result.taskId, indexSezione);
										$('#mascheraWorkflowAtto').modal('hide');
									}, function(error) {
										$rootScope.activeExitUserConfirmation = null;
										$location.path( "/" );
										$scope.taskLoading = false;
									});
							}
						}else{
							$rootScope.$broadcast('globalLoadingEnd');
							$scope.taskLoading = false;
						}
					}catch(err){
						$rootScope.$broadcast('globalLoadingEnd');
					}
				};

				$scope.treeOptions = {
					accept: function(sourceNodeScope, destNodesScope, destIndex) {
						if(sourceNodeScope.$parent.$id === destNodesScope.$id){
							return true;
						}else{
							return false;
						}
					}
				};


				$scope.sottoscrittoreOptions = {
					dropped: function(event) {
						for(var i = 0; i <  $scope.atto.sottoscrittori.length; i++){
							$scope.atto.sottoscrittori[i].ordineFirma = Number(i +1);
						}
					},
					accept: function(sourceNodeScope, destNodesScope, destIndex) {
						return (sourceNodeScope.$modelValue.aooNonProponente && destNodesScope.$modelValue[destIndex].aooNonProponente)
							|| (!sourceNodeScope.$modelValue.aooNonProponente && !destNodesScope.$modelValue[destIndex].aooNonProponente);
					}
				};

				/*
 * Per ATTICO esclusa gestione sottoscrittori
 *
$scope.checkIfCanDisableCongiunto = function(){
	if(!$scope.attoCongiunto && $scope.countSottoscrittoriAooCongiunte() > 0){
		$('#disabilitaAttoCongiuntoModal').modal('show');
		$scope.atto.congiunto = true;
	}
};

$scope.addSottoscrittore = function (aooNonProponente) {
 $scope.sottoscrittoreAtto = {atto: { id: $scope.attoId} ,edit:true, ordineFirma:$scope.atto.sottoscrittori.length, enabled:true};
 if(aooNonProponente){
	 $scope.sottoscrittoreAtto.aooNonProponente = aooNonProponente;
	 if(aooNonProponente && !$scope.aooSottoscrittoriMap[aooNonProponente.id]){
		 $scope.aooSottoscrittoriMap[aooNonProponente.id] = true;
		 Profilo.getByAooIdAndTipoAtto({aooId:aooNonProponente.id,codiceTipoAtto:$scope.atto.tipoAtto.codice},function(result){
			 $scope.aooSottoscrittoriMap[aooNonProponente.id] = result;
		 });
	 }
	 $scope.atto.sottoscrittori.push( $scope.sottoscrittoreAtto ) ;
 }else{
	 var sp = $scope.countSottoscrittoriAooProponente();
	 $scope.atto.sottoscrittori.splice(sp, 0, $scope.sottoscrittoreAtto);
 }

};

$scope.addSottoscrittoreParere = function ( parere ) {
 parere.sottoscrittori.push(  {parere: { id: parere.id}  } ) ;
};

$scope.deleteSottoscrittoreParere = function (parere, index ) {
 parere.sottoscrittori.splice(index ,1);
};



$scope.saveSottoscrittoreAtto = function ( sottoscrittoreAtto ) {
 Atto.sottoscrittoreAtto({id:$scope.atto.id}, sottoscrittoreAtto,
   function () {
    $scope.refreshSottoscrittori();
  });
};

$scope.countSottoscrittoriAooProponente = function(){
	var count = 0;
	if($scope.atto.sottoscrittori){
		angular.forEach($scope.atto.sottoscrittori, function(sottoscrittore) {
	        if (sottoscrittore.aooNonProponente === undefined || sottoscrittore.aooNonProponente === null) {
	        	count++;
	        }
	    });
	}
    return count;
};

$scope.countSottoscrittoriAooCongiunte = function(){
	var count = 0;
	if($scope.atto.sottoscrittori){
		angular.forEach($scope.atto.sottoscrittori, function(sottoscrittore) {
	        if (sottoscrittore.aooNonProponente !== undefined && sottoscrittore.aooNonProponente !== null) {
	        	count++;
	        }
	    });
	}
    return count;
};
*/

				$scope.deleteSottoscrittore = function (id, edit, index) {
					$log.debug("deleteSottoscrittore");
					if(!edit) {
						SottoscrittoreAtto.get({id: id}, function(result) {
							$scope.sottoscrittoreAtto = result;
							$scope.sottoscrittoreAtto.index = index;
							$('#deleteSottoscrittoreAttoConfirmation').modal('show');
						});
					}
					else {
						$scope.atto.sottoscrittori.splice(index,1);
						for(var i = 0; i <  $scope.atto.sottoscrittori.length; i++){
							$scope.atto.sottoscrittori[i].ordineFirma = Number(i +1);
						}
						$scope.validateSottoscrittoriNonProponente($scope.atto.sottoscrittori);
						$scope.validateSottoscrittori($scope.atto.sottoscrittori);
					}
				};

				$scope.confirmDeleteSottoscrittore = function (id) {
					SottoscrittoreAtto.delete({id: id},
						function () {
							$scope.atto.sottoscrittori.splice($scope.sottoscrittoreAtto.index,1);
							for(var i = 0; i <  $scope.atto.sottoscrittori.length; i++){
								$scope.atto.sottoscrittori[i].ordineFirma = Number(i +1);
							}
							$scope.validateSottoscrittoriNonProponente($scope.atto.sottoscrittori);
							$scope.validateSottoscrittori($scope.atto.sottoscrittori);
							$('#deleteSottoscrittoreAttoConfirmation').modal('hide');
						});
				};


				/*
 * Per ATTICO esclusa gestione sottoscrittori
 *
$scope.settaQualificaEmanante = function(){
	if(angular.isDefined($scope.atto.emananteProfilo) && $scope.atto.emananteProfilo!=null && angular.isDefined($scope.atto.emananteProfilo.hasQualifica) && $scope.atto.emananteProfilo.hasQualifica!=null && $scope.atto.emananteProfilo.hasQualifica.length == 1){
		$scope.atto.qualificaEmanante = $scope.atto.emananteProfilo.hasQualifica[0];
	}
};
*/

				$scope.setProfiloDefault = function(configurazioneIncaricoProfilo, listaProfili, obbligatorio, incaricoIndex, $index){
					if(configurazioneIncaricoProfilo.idConfigurazioneIncarico==null && listaProfili!=null && obbligatorio && listaProfili.length==1){
						configurazioneIncaricoProfilo.idProfilo=listaProfili[0].id;
						$scope.setQualificheProfilo(incaricoIndex, $index, configurazioneIncaricoProfilo.idProfilo, false);
					}
				}

				$scope.setUfficioDefault = function(configurazioneIncaricoAoo, listaUffici, obbligatorio){
					if(configurazioneIncaricoAoo.idConfigurazioneIncarico==null && listaUffici!=null && obbligatorio && listaUffici.length==1){
						configurazioneIncaricoAoo.idAoo=listaUffici[0].id;
					}
				}

				$scope.setQualificheProfilo = function(indexIncarico, indexProfilo, profiloId, isChanged){
					$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualifiche=[];
					if(isChanged){
						$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualificaProfessionaleDto = {id:null};
					}
					if(profiloId!=undefined && profiloId!=null){
						QualificaProfessionale.getEnabledByProfiloId({profiloId: profiloId}, function(result) {
							$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualifiche=result;
							if(
								$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualifiche!=null &&
								$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualifiche.length==1
							){
								$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualificaProfessionaleDto.id=$scope.incarichi[indexIncarico].listConfigurazioneIncaricoProfiloDto[indexProfilo].qualifiche[0].id;
							}
							$scope.incarichi[indexIncarico].runValidate = true;
						});
					}else{
						$scope.incarichi[indexIncarico].runValidate = true;
					}
				};

				$scope.setTipoIterDefault = function(){
					if($scope.atto.tipoIter==null){
						if($scope.tipoiters!=null && Object.keys($scope.tipoiters).length==1){
							$scope.atto.tipoIter = {};
							$scope.atto.tipoIter.id = $scope.tipoiters[Object.keys($scope.tipoiters)[0]].id;
							$scope.atto.tipoAdempimento = null;
							$scope.atto.tipoIter.codice = $scope.getCodiceTipoIterById()
						}
					}
				}
				$scope.inizializzaTestoNota = function(){
					$scope.showNoteTable = true;
					$scope.atto.nota = null;
					for ( var indexNota in $scope.atto.listaNote) {
						if($scope.atto.listaNote[indexNota].taskId==$scope.taskBpmId){
							$scope.atto.nota = $scope.atto.listaNote[indexNota].testo;
							break;
						}
					}

					if( $scope.atto.listaNote.length == 0 || ($scope.atto.listaNote.length==1 && $scope.atto.nota!=null)){
						$scope.showNoteTable = false;
					}

				}

				$scope.refresh = function ( ) {
					Atto.get({id: $scope.atto.id}, function(result) {
						$scope.atto = result;
						$scope.inizializzaTestoNota();
						if(!$scope.atto.emananteProfilo) {
							Aoo.get({id: $scope.atto.aoo.id}, function(result) {
								$scope.profiloResponsabile = result.profiloResponsabile;
								if($scope.profiloResponsabile && !$scope.atto.emananteProfilo){
									Profilo.get({id:$scope.profiloResponsabile.id},function(result){
										$scope.atto.emananteProfilo = result;
									});
									/*$scope.atto.emananteProfilo  = jQuery.extend(true, {}, $scope.profiloResponsabile);*/
								}
							});
						}
						// Per attico esclusa gestione sottoscrittori
						// $scope.addFirstSubscriber();

					});
				};

				/*
 * Per ATTICO esclusa gestione sottoscrittori
 *
$scope.refreshSottoscrittori = function ( ) {
	Atto.get({id: $scope.atto.id}, function(result) {
		$scope.atto.sottoscrittori = result.sottoscrittori;
		if(!$scope.atto.emananteProfilo) {
			Aoo.get({id: $scope.atto.aoo.id}, function(result) {
				$scope.profiloResponsabile = result.profiloResponsabile;
				if($scope.profiloResponsabile && !$scope.atto.emananteProfilo){
					Profilo.get({id:$scope.profiloResponsabile.id},function(result){
						$scope.atto.emananteProfilo = result;
					});
				}
			});
		}
		$scope.addFirstSubscriber();
	});
};
*/

				$scope.confirmCercaAtto = function(criteria) {
					Atto.search( criteria,
						function (result, headers) {
							$scope.resultAttiLinks = ParseLinks.parse(headers('link'));
							$scope.totalResultAtti=headers('x-total-count') ;
							$scope.resultAtti = result;
						});
				};

				$scope.loadPageCercaAtto = function(page) {
					$scope.pageCercaAtto = page;
					$scope.modelloCampoCriteri.page = $scope.pageCercaAtto;
					$scope.confirmCercaAtto($scope.modelloCampoCriteri);
				};

				$scope.creaAtto = function (attoId, tipoCopia) {
					$log.debug("creaAtto tipoCopia "+tipoCopia);
					$log.debug("creaAtto attoId "+attoId);

					Atto.copia( {id:attoId ,  tipoCopia: tipoCopia },{} , function(data){
						$scope.atto = data;
						$scope.initSelezionaSezione(0,tipoCopia.id);
						ngToast.create(  { className: 'success', content: 'Atto '+tipoCopia+' creato con successo' });
						$scope.showCercaAtto = false;
					});


				};

				$scope.loadSection = function (section) {
					$scope.attoDirigenzialeSezioni[$scope.indexSezioneCorrente].activeCss="";
					$scope.taskLoading = false;
					$scope.load($scope.atto.id,section,$scope.taskBpmId);
				}

				if(!angular.isDefined($scope.avanzamenti)){
					$scope.avanzamenti = [];
				}
				if(!angular.isDefined($scope.risposte)){

					$scope.risposte = [];
				}
				if(!angular.isDefined($scope.previewElencoTipoDocumento)){
					$scope.previewElencoTipoDocumento = [];
				}
				if(!angular.isDefined($scope.generaElencoTipoDocumento)){
					$scope.generaElencoTipoDocumento = [];
				}
				if(!angular.isDefined($scope.tipiDocumentoDaFirmareCodes)){
					$scope.tipiDocumentoDaFirmareCodes = [];
				}

				if(!angular.isDefined($scope.generaElencoDocumentiDaFirmare)){
					$scope.generaElencoDocumentiDaFirmare = [];
				}
				$scope.loaded = 0;

				$scope.load = function (id,section,taskBpmId) {
					$log.debug("id:"+id);
					$log.debug("section:"+section);
					$log.debug("taskBpmId:"+taskBpmId);

					if(!angular.isDefined(taskBpmId) && $scope.loaded < 1){
						$scope.loaded = $scope.loaded + 1;
					}

					if(!$scope.macros){
						Macro_cat_obbligo_dl33.attivo(  function(results){
							$scope.macros =  AnagraficaObbligoDlService.listToMap(results);
							if(!$scope.valoriSchedeDati.attoId)
								$scope.caricaSchedeTrasparenza( $scope.valoriSchedeDati );
						}) ;
					}else if(!$scope.valoriSchedeDati.attoId){
						$scope.caricaSchedeTrasparenza( $scope.valoriSchedeDati );
					}

					$scope.tipoNuovo = id;
					$scope.showCercaAtto = false;
					$scope.taskBpmId = taskBpmId;

					$scope.solaLettura = $scope.solaLettura || false;

					$scope.criteria = {aooId:$rootScope.profiloattivo.aoo.id, tipoAttoId: $scope.tipoAtto.id} ;

					$scope.pageCercaAtto = 0;
					$scope.criteria.page = $scope.pageCercaAtto;

					var inizializzaParereSottoscrizione = function(
						nome, cognome, aoo, qualifica, data, dataParere, dataScadenza, tipologia, motivazione, allegati, parereSintetico, parerePersonalizzato, annullato, perContoDiAoo, perContodiUtente){
						return {
							utente: cognome + ' ' + nome,
							aoo: aoo,
							data: data,
							dataParere: dataParere,
							dataScadenza: dataScadenza,
							qualifica: qualifica,
							tipologia: tipologia.descrizione,
							codTipologia: tipologia.codice,
							motivazione: motivazione,
							allegati: allegati,
							parereSintetico: parereSintetico,
							parerePersonalizzato: parerePersonalizzato,
							annullato: annullato,
							perContoDiAoo: perContoDiAoo,
							perContodiUtente: perContodiUtente
						};
					}

					$scope.inizializzaPareriSottoscrittori = function(){
						$scope.pareriSottoscrittori = [];
						if($scope.atto!=null && $scope.atto.pareri!=null && $scope.atto.pareri.length>0){
							for ( var parere in $scope.atto.pareri) {
								var nomeSottoscrittore = $scope.atto.pareri[parere].profilo.utente.nome;
								var cognnomeSottoscrittore = $scope.atto.pareri[parere].profilo.utente.cognome;

								var perContodiUtente = $scope.atto.pareri[parere].profiloRelatore ? ($scope.atto.pareri[parere].profiloRelatore.utente.nome + " " + $scope.atto.pareri[parere].profiloRelatore.utente.cognome) : "";

								var parerePersonalizzato = ($scope.atto.pareri[parere].parerePersonalizzato!=null) ? $scope.atto.pareri[parere].parerePersonalizzato : "";
								var dataParere = null;
								if ($scope.atto.pareri[parere].parereSintetico && $scope.atto.pareri[parere].parereSintetico.length>0) {
									dataParere = $scope.atto.pareri[parere].data;
								}
								var dataScadenza = null;
								if ($scope.atto.pareri[parere].dataScadenza) {
									dataScadenza = $scope.atto.pareri[parere].dataScadenza;
								}
								
								var dataCreazioneOModifica = new Date($scope.atto.pareri[parere].createdDate);
								
								if($scope.atto.pareri[parere].lastModifiedDate && $scope.atto.pareri[parere].tipoAzione && $scope.atto.pareri[parere].tipoAzione.codice && 
										($scope.atto.pareri[parere].tipoAzione.codice=='parere_commissione' || $scope.atto.pareri[parere].tipoAzione.codice=='parere_quartiere_revisori')
								){
									dataCreazioneOModifica = new Date($scope.atto.pareri[parere].lastModifiedDate);
								}

								var parereSottoscrizione = inizializzaParereSottoscrizione(
									nomeSottoscrittore,
									cognnomeSottoscrittore,
									$scope.atto.pareri[parere].profilo.aoo.descrizione,
									$scope.atto.pareri[parere].descrizioneQualifica,
									dataCreazioneOModifica,
									dataParere,
									dataScadenza,
									$scope.atto.pareri[parere].tipoAzione,
									$scope.atto.pareri[parere].parere,
									$scope.atto.pareri[parere].allegati,
									$scope.atto.pareri[parere].parereSintetico,
									parerePersonalizzato,
									$scope.atto.pareri[parere].annullato,
									($scope.atto.pareri[parere].aoo && $scope.atto.pareri[parere].aoo.id && $scope.atto.pareri[parere].aoo.id != $scope.atto.pareri[parere].profilo.aoo.id ? $scope.atto.pareri[parere].aoo.descrizione : ''),
									perContodiUtente
								);
								$scope.pareriSottoscrittori.push( parereSottoscrizione );
							}
						}
						if($scope.atto!=null && $scope.atto.sottoscrittori!=null && $scope.atto.sottoscrittori.length>0){
							for ( var sottoscrittore in $scope.atto.sottoscrittori) {
								var parereSottoscrizione = inizializzaParereSottoscrizione(
									$scope.atto.sottoscrittori[sottoscrittore].profilo.utente.nome,
									$scope.atto.sottoscrittori[sottoscrittore].profilo.utente.cognome,
									$scope.atto.sottoscrittori[sottoscrittore].profilo.aoo.descrizione,
									$scope.atto.sottoscrittori[sottoscrittore].descrizioneQualifica,
									new Date($scope.atto.sottoscrittori[sottoscrittore].dataFirma), null, null,
									{descrizione:'Firma'},
									'',
									'',
									'','',
									!$scope.atto.sottoscrittori[sottoscrittore].enabled
								);
								$scope.pareriSottoscrittori.push( parereSottoscrizione );
							}
						}
					}

					// Lettura degli scenari di abilitazione definiti tramite variabile del task
					// TODO: La parte successiva andrebbe rivista in base ai valori acquisiti dal processso
					if(!$.isNumeric(id)){
						$scope.solaLettura = false;
						$scope.scenariDisabilitazione = [];
					}else if ( taskBpmId && $rootScope.profiloattivo && $rootScope.profiloattivo.id != 0){
//	  if(!$scope.scenariDisabilitazione || !angular.isDefined($scope.scenariDisabilitazione.length)){
						$scope.onSpinner = true;
//		  $scope.solaLettura = $scope.isSolaLettura(taskBpmId, $scope.scenariDisabilitazione);
//		  if($scope.solaLettura){
//				 if($scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0){
//	 				$scope.scenariDisabilitazione.push('TuttoNonModificabile');
//	 			 }
//		  }
						Lavorazione.scenariDisabilitazione({taskBpmId: $scope.taskBpmId},
							function(data){
								$scope.scenariDisabilitazione =  data;
								$scope.onSpinner = false;
								for(var i=0; i<$scope.scenariDisabilitazione.length; i++) {
									if($scope.scenariDisabilitazione[i] == "SezioneSottoscrittoriNonModificabile"){
										$scope.permettiCambiaOrdineSottoscrittori = false;
										break;
									}
								}
								$scope.solaLettura = $scope.isSolaLettura(taskBpmId, $scope.scenariDisabilitazione) || ($scope.taskBpm && !$scope.taskBpm.idAssegnatario );
								if($scope.solaLettura){
									if($scope.scenariDisabilitazione.indexOf('TuttoNonModificabile') < 0){
										$scope.scenariDisabilitazione.push('TuttoNonModificabile');
									}
								}
							});
//	  }
					}
					else if ( (!$scope.scenariDisabilitazione) || $scope.scenariDisabilitazione.indexOf('PostPubblicazioneTrasparenza') < 0 ) {
						$scope.solaLettura = true;
						$scope.scenariDisabilitazione = [];
						$scope.scenariDisabilitazione.push('TuttoNonModificabile');

						/*$scope.solaLettura = true;
	 $scope.scenariDisabilitazione = [];
	 if($scope.isPubblicatorePostPubblicazione()){
	 	$scope.scenariDisabilitazione.push('PostPubblicazioneTrasparenza');
	 	var pulsanteVuoto = {"descrizione":"Aggiorna Trasparenza", codiceAzioneUtente:"AGGIORNA_TRASPARENZA"};
	  	$scope.decisioniPostPubblicazione = [];
		$scope.decisioniPostPubblicazione.push(pulsanteVuoto);
	 }else{
	 	$scope.scenariDisabilitazione.push('TuttoNonModificabile');
	 }*/
					}
					if('nuovo' === id ){
						$log.debug('nuovo');

						if($scope.tipoAtto == undefined){
							$scope.getTipoAttoFromProfiloAttivo();
						}

						if($scope.tipoAtto && $scope.tipoAtto.proponente == true){
							$scope.atto = {aoo: $rootScope.profiloattivo.aoo , profilo: $rootScope.profiloattivo, tipoAtto: $scope.tipoAtto, durataGiorni:$scope.tipoAtto.giorniPubblicazioneAlbo, congiunto:false, pubblicazioneTrasparenzaNolimit : true};
						}else {
							$scope.atto = {profilo: $rootScope.profiloattivo, tipoAtto: $scope.tipoAtto, durataGiorni:$scope.tipoAtto.giorniPubblicazioneAlbo, congiunto:false, pubblicazioneTrasparenzaNolimit : true};
						}

						TipoAtto.get({id: $scope.tipoAtto.id}, function(result) {
							$scope.sezioniAtto = result.sezioni;
						});

						Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_CONSIGLIO}, function(result){
							$log.debug("Assessorati:",result);
							$scope.profilosComponentiConsiglio = result;
						});

						Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_GIUNTA}, function(result){
							$log.debug("Componenti Giunta:",result);
							$scope.profilosComponentiGiunta = result;
						});

						TipoDeterminazione.query(function(result) {
							$scope.tipoDeterminaziones = result;
							$log.debug("elenco tipo det",$scope.tipoDeterminaziones);
							$scope.atto.tipoDeterminazione = $scope.tipoDeterminaziones[0];
							$log.debug('tipoDetSel=',$scope.atto.tipoDeterminazione);
						});

						if(!$scope.atto.profilo.hasQualifica) {
							Profilo.get({id:$scope.atto.profilo.id},function(result){
								$scope.atto.profilo.hasQualifica = result.hasQualifica;
							});
						}

						$scope.initSelezionaSezione(0,$scope.tipoAtto.id);
						var pulsanteVuoto = {"descrizione":"Crea Proposta"};
						$scope.decisioni = [];
						$scope.decisioni.push(pulsanteVuoto);
						$rootScope.activeExitUserConfirmation = true;
					}else if('effettuaClonazione' === id && section != undefined && section!=''){
						Atto.get({id: $stateParams.section}, function(result) {

							/*
		 if(result.tipoAtto.codice == 'SDL'){
			 $log.debug("Profilo attivo:",$rootScope.profiloattivo);
			 for(var pta = 0; pta < $rootScope.profiloattivo.tipiAtto.length; pta ++){
				 if($rootScope.profiloattivo.tipiAtto[pta].codice == "DDL"){
					 $scope.canChangeSDL = true;
					 break;
				 }
			 }


			 TipoAtto.query(function(collection){
				 //$scope.tipoattos = collection;
				angular.forEach(collection, function(item) {
					if(item.codice == "SDL" || item.codice == "DDL"){
						$scope.tipoattos.push(item);
					}
				});
			 });
		 }
		 */

							Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_CONSIGLIO}, function(result){
								$log.debug("Assessorati:",result);
								$scope.profilosComponentiConsiglio = result;
							});
							Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_GIUNTA}, function(result){
								$log.debug("Componenti Giunta:",result);
								$scope.profilosComponentiGiunta = result;
							});

							TipoIter.getByTipoAtto({tipoAttoId: result.tipoAtto.id},function(iter){
								$scope.tipoiters = TipoIterService.listToMap(iter);
								$scope.atto = result;
								$scope.atto.attoclonatoid = $scope.atto.id;
								$scope.atto.id = -1;
								$scope.atto.dataInizioPubblicazionePresunta = null;
								delete $scope.atto.motivoClonazione;
								$scope.tipoAtto = $scope.atto.tipoAtto;
								$scope.atto.profilo = $rootScope.profiloattivo;

								if ($scope.tipoAtto.proponente){
									$scope.atto.aoo = $rootScope.profiloattivo.aoo;
								}

								/*INIZIO ATTO REVOCATO*/
								if(angular.isDefined($scope.atto.numeroAdozione)){
									$scope.atto.numeroAdozioneAttoRevocato = $scope.atto.numeroAdozione;
								}
								if(angular.isDefined($scope.atto.dataAdozione)){
									$scope.atto.dataAdozioneAttoRevocato = $scope.atto.dataAdozione;
								}
								if(angular.isDefined($scope.atto.codiceCifra)){
									$scope.atto.codicecifraAttoRevocato = $scope.atto.codiceCifra;
								}
								/*FINE ATTO REVOCATO*/

								$scope.initSelezionaSezione(0,$scope.tipoAtto.id);
								var pulsanteVuoto = {"descrizione":"Clona Atto"};
								$scope.decisioni = [];
								$scope.decisioni.push(pulsanteVuoto);
								$rootScope.activeExitUserConfirmation = true;

								TipoDeterminazione.query(function(tipiDeterminazione) {
									$scope.tipoDeterminaziones = tipiDeterminazione;
									$scope.atto.tipoDeterminazione = $scope.tipoDeterminaziones[0];
								});
							});
							$scope.inizializzaPareriSottoscrittori();
						});

					}else if('clona' === $scope.tipoNuovo ){
						$log.debug('clona');
						$scope.showCercaAtto = true;

					}else if('riferimento' === id ){
						$log.debug('riferimento');
						$scope.showCercaAtto = true;

					}else if(id > 0){
						$log.debug('load');

						if(!$scope.atto)  {
							Atto.get({id: id}, function(result) {
								$scope.atto = result;

								$scope.newEditorVersion = false;
								let enableNewConvertMode = $rootScope.configurationParams.enableNewConvertMode;
								if(enableNewConvertMode && enableNewConvertMode == "full") {
									$scope.newEditorVersion = true;
								}else if(enableNewConvertMode == "true") {
									let dateList = $rootScope.configurationParams.enableNewConvertModeFromDateList;
									let codiceAooPredisposizioneAtto = $scope.atto.aoo.codice;
									let timeAttoCreation = $scope.atto.createdDate;
									
									let enableNewConvertModeAooList = $rootScope.configurationParams.enableNewConvertModeAooList;
									for(let i = 0; i < enableNewConvertModeAooList.length; i++) {
										if(enableNewConvertModeAooList[i].indexOf(codiceAooPredisposizioneAtto) > -1 && timeAttoCreation > new Date(dateList[i]).getTime()) {
											$scope.newEditorVersion = true;
											break;
										}
									}
								}

								$scope.inizializzaTestoNota();
								$scope.inizializzaPareriSottoscrittori();
								TipoAtto.get({id: $scope.atto.tipoAtto.id}, function(result) {
									$scope.sezioniAtto = result.sezioni;
									$scope.resetAttoDirigenzialeError();
								});
								Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_CONSIGLIO}, function(result){
									$log.debug("Assessorati:",result);
									$scope.profilosComponentiConsiglio = result;
								});
								Profilo.query({stato:'0',ruoli: RoleCodes.ROLE_COMPONENTE_GIUNTA}, function(result){
									$log.debug("Componenti Giunta:",result);
									$scope.profilosComponentiGiunta = result;
								});

								/*
				if($scope.tutteLeAoo == undefined){
				 var deferred = $q.defer();
				 $scope.tutteLeAoo = deferred.promise;
				 Aoo.getMinimal({}, function(aoos){
					 deferred.resolve();
					 $scope.tutteLeAoo = aoos;
					 $scope.settingListAoo();
				 });
			 }else if (typeof $scope.tutteLeAoo.then === 'function') {
				 $scope.tutteLeAoo.then(function(data) {
					 $scope.settingListAoo();
				 });
			 }
			 */
								angular.copy($scope.atto.beneficiari, $scope.beneficiariTrasp);
								$scope.beneficiariTrasp.unshift({});
								if(angular.isUndefined($scope.atto.allegatoBeneficiari) || $scope.atto.allegatoBeneficiari == null){
									$scope.atto.allegatoBeneficiari = false;
								}
								Atto.relataGestibile({id:$scope.atto.id},function(risposta){
									$scope.relataGestibile = risposta.info;
								});
								Atto.elencotipodocumento({taskBpmId: $scope.taskBpmId}, function(result, headers) {
									$log.debug("Atto.elencotipodocumento taskBpmId:"+taskBpmId, result);
									$scope.generaElencoTipoDocumento = [];
									angular.forEach(result, function(tipoDocumento, key){
										$scope.generaElencoTipoDocumento.push(tipoDocumento);
									});

									$log.debug("generaElencoTipoDocumento MOD:", $scope.generaElencoTipoDocumento);
								});

								Atto.getCodiciTipiDocumentoDaFirmare({taskBpmId: $scope.taskBpmId}, function(result, headers) {
									$scope.tipiDocumentoDaFirmareCodes = result;
								});

								Atto.elencodocumentidafirmare({taskBpmId: $scope.taskBpmId}, function(result, headers) {
									$log.debug("Atto.elencodocumentidafirmare taskBpmId:"+taskBpmId, result);
									$scope.generaElencoDocumentiDaFirmare = [];
									angular.forEach(result, function(documentoPdf, key){
										$scope.generaElencoDocumentiDaFirmare.push(documentoPdf);
									});

									$log.debug("generaElencoDocumentiDaFirmare MOD:", $scope.generaElencoDocumentiDaFirmare);
								});


								Avanzamento.getByAtto({id: id}, function(result, headers) {
									$log.debug("Avanzamento.getByAtto id:"+id,result);
									$scope.avanzamenti = [];
									var tempStato = "";
									var tempEsecutore = "";
									var tempAttivita = "";
									var tempNote = "";
									angular.forEach(result,function(avanzamento,key){
										/* Versione senza raggruppamento
					    avanzamento.listaAttivita = [];
					  	var attivita = {'nome':avanzamento.attivita,'data':avanzamento.dataAttivita,'note':avanzamento.note};
					  	avanzamento.listaAttivita.push(attivita);
			  	  	 	$scope.avanzamenti.push(avanzamento);
			  	  	 	*/

										if(avanzamento.attivita == 'Generazione Copia Conforme' || avanzamento.attivita == 'Generazione Copia Conforme Con Omissis' || avanzamento.attivita == 'Generazione Copia Non Conforme' || avanzamento.attivita == 'Generazione Copia Non Conforme Con Omissis'){
											tempAttivita = avanzamento.attivita;
											tempStato = avanzamento.stato;
											tempEsecutore = avanzamento.createdBy;
											avanzamento.listaAttivita = [];
											var attivita = {'nome':avanzamento.attivita,'data':avanzamento.dataAttivita,'note':avanzamento.note};
											avanzamento.listaAttivita.push(attivita);
											$scope.avanzamenti.push(avanzamento);
										}
										else  if(avanzamento.stato == tempStato && avanzamento.createdBy == tempEsecutore) {
											$scope.avanzamenti[$scope.avanzamenti.length - 1].dataAttivita = avanzamento.dataAttivita;
											var attivita = {
												'nome': avanzamento.attivita,
												'data': avanzamento.dataAttivita,
												'note': avanzamento.note
											};
											if (avanzamento.attivita == tempAttivita && avanzamento.note == tempNote && avanzamento.attivita != 'Inserisci/Modifica Parere Commissione' && avanzamento.attivita !='Inserisci/Modifica Parere Cons. Quart. / Revisori Contabili') {
												$scope.avanzamenti[$scope.avanzamenti.length - 1].listaAttivita.pop();
											}
											$scope.avanzamenti[$scope.avanzamenti.length - 1].listaAttivita.push(attivita);
											tempAttivita = avanzamento.attivita;
											tempNote = avanzamento.note;
										}
										else{
											if (avanzamento.attivita == tempAttivita && avanzamento.createdBy == tempEsecutore) {
												var ultimoAvanzamento = $scope.avanzamenti.pop();
												if(ultimoAvanzamento && ultimoAvanzamento.listaAttivita && ultimoAvanzamento.listaAttivita.length > 0){
													ultimoAvanzamento.listaAttivita.pop();
													if(ultimoAvanzamento.listaAttivita && ultimoAvanzamento.listaAttivita.length > 0){
														$scope.avanzamenti.push(ultimoAvanzamento);
													}
												}
											}
											var note = "";
											if(tempAttivita==avanzamento.attivita){
												if(tempNote!="" && (avanzamento.note==null || avanzamento.note=="")){
													note = tempNote;
												}else{
													note = avanzamento.note;
												}
											}else{
												note = avanzamento.note;
											}
											tempNote = note;
											
											tempAttivita = avanzamento.attivita;
											tempStato = avanzamento.stato;
											tempEsecutore = avanzamento.createdBy;
											avanzamento.listaAttivita = [];
											var attivita = {'nome':avanzamento.attivita,'data':avanzamento.dataAttivita,'note':note};
											avanzamento.listaAttivita.push(attivita);
											$scope.avanzamenti.push(avanzamento);
										}

									});

									$log.debug("avanzamenti MOD:",$scope.avanzamenti);
								});

								Movimento.elencoMovimenti({id: id}, function(result, headers) {
									$log.debug("Movimento.elencoMovimenti id:"+id, result);
									if (angular.isDefined(result)) {
										$scope.movimentiCont = [];
										$scope.liquidazioniCont = [];
									}

									angular.forEach(result, function(movimento, key){
										if (angular.isDefined(movimento.movImpAcce)) {
											$scope.movimentiCont.push(movimento.movImpAcce);
										}
										if (angular.isDefined(movimento.liquidazione) && angular.isDefined(movimento.liquidazione.documento)) {
											angular.forEach(movimento.liquidazione.documento, function(mv, key) {
												var movCompl = mv;
												movCompl.annoLiq = movimento.liquidazione.anno;
												movCompl.numeroLiq = movimento.liquidazione.numero;
												if(movCompl.numeroLiq && !isNaN(movCompl.numeroLiq)){
													movCompl.numeroLiq = parseInt(movCompl.numeroLiq);
												}
												$scope.liquidazioniCont.push(movCompl);
											});
										}
									});

								});

								Preview.elencotipodocumento({taskBpmId: $scope.taskBpmId}, function(result, headers) {
									$log.debug("Preview.elencotipodocumento taskBpmId:"+taskBpmId, result);
									$scope.previewElencoTipoDocumento = [];
									angular.forEach(result, function(tipoDocumento, key){
										$scope.previewElencoTipoDocumento.push(tipoDocumento);
									});

									$log.debug("previewElencoTipoDocumento MOD:", $scope.previewElencoTipoDocumento);
								});
								
								if($scope.atto.numeroAdozione)
									$scope.atto.numeroAdozione = $scope.atto.numeroAdozione.substr($scope.atto.numeroAdozione.length - 5);
								$log.debug('ATTO',$scope.atto);
								TipoIter.getByTipoAtto({tipoAttoId: $scope.atto.tipoAtto.id},function(result){
									$scope.tipoiters = TipoIterService.listToMap(result);
								});

								$scope.emananti = [];
								if($scope.atto.aoo){

									Profilo.getEmananti({aooId:$scope.atto.aoo.id, tipoAttoCodice:$scope.atto.tipoAtto.codice}, function(result) {
										$scope.emananti = result;
									});

								}

								/*
			  * TODO: per ATTICO l'emanante viene settato in base alle assegnazioni incarichi
			  *
				 Aoo.get({id: $scope.atto.aoo.id}, function(result) {
					 $log.debug("result",result);
					 $scope.profiloResponsabile = result.profiloResponsabile;
					 if($scope.profiloResponsabile && !$scope.atto.emananteProfilo){
						 Profilo.get({id:$scope.profiloResponsabile.id},function(pf){
							 $scope.atto.emananteProfilo = pf;
							 $scope.atto.qualificaEmanante = $scope.atto.emananteProfilo.hasQualifica[0];
						  });
						 // $scope.atto.emananteProfilo  = jQuery.extend(true, {}, $scope.profiloResponsabile);
					 }
					 else if((!$scope.atto.qualificaEmanante || !$scope.atto.qualificaEmanante.id) && $scope.atto.emananteProfilo && $scope.atto.emananteProfilo.hasQualifica){
						 $scope.atto.qualificaEmanante = $scope.atto.emananteProfilo.hasQualifica[0];
					 }
				 });
			 */

								if($scope.atto.pareri != null && $scope.atto.pareri.length >0 && $scope.atto.pareri[0].documentiPdf != null && $scope.atto.pareri[0].documentiPdf.length >0 && $scope.atto.pareri[0].documentiPdf[0].file != null){
									$scope.idDoc = $scope.atto.pareri[0].documentiPdf[0].id;
									$log.debug('trovato doc:',$scope.idDoc);
								}
								var aooIdAtto = null;
								if($scope.atto.aoo){
									aooIdAtto = $scope.atto.aoo.id;
								}
								Profilo.getByAooIdAndTipoAtto({aooId:aooIdAtto,codiceTipoAtto:$scope.atto.tipoAtto.codice},function(result){
									$scope.profilos = result;
									$scope.rupProfilos = result;
									$scope.rupProfilos.unshift({});

									/*
				 * Per ATTICO esclusa gestione sottoscrittori
				 *
				 for(var i = 0; i<$scope.atto.sottoscrittori.length; i++){
					 var found = false;
					 for(var y = 0; y < $scope.profilos.length; y++) {
						 if ($scope.profilos[y].id == $scope.atto.sottoscrittori[i].profilo.id) {
							 found = true;
							 break;
						 }
					 }
					 if(!found) $scope.profilos.push($scope.atto.sottoscrittori[i].profilo);
				 }
				 */
								});
								if(!angular.isDefined($scope.aooSottoscrittoriMap)){
									$scope.aooSottoscrittoriMap = {};
								}

								/*
			 * Per ATTICO esclusa gestione sottoscrittori
			 *
			 angular.forEach( $scope.atto.sottoscrittori , function(sottoscrittore) {
				 if(sottoscrittore.aooNonProponente && !$scope.aooSottoscrittoriMap[sottoscrittore.aooNonProponente.id]){
					 $scope.aooSottoscrittoriMap[sottoscrittore.aooNonProponente.id] = true;
					 Profilo.getByAooIdAndTipoAtto({aooId:sottoscrittore.aooNonProponente.id,codiceTipoAtto:$scope.atto.tipoAtto.codice},function(result){
						 $scope.aooSottoscrittoriMap[sottoscrittore.aooNonProponente.id] = result;
					 });
				 }
			  });
			  */

								TipoDeterminazione.query(function(result) {
									if(angular.isDefined($scope.atto.id) && $scope.atto.id!=null && $scope.atto.id > 0 && angular.isDefined($scope.atto.motivoClonazione) && $scope.atto.motivoClonazione!=null && ($scope.atto.motivoClonazione.toUpperCase() == 'REVOCA' || $scope.atto.motivoClonazione.toUpperCase() == 'RETTIFICA')){
										var toRemove = undefined;
										for(var i = 0; i<result.length; i++){
											if(result[i].id == 1){
												toRemove = i;
												break;
											}
										}
										if(toRemove!=undefined){
											result.splice(toRemove, 1);
										}
										if(angular.isDefined($scope.atto.tipoDeterminazione) && $scope.atto.tipoDeterminazione!=null && $scope.atto.tipoDeterminazione.id == 1){
											delete $scope.atto.tipoDeterminazione;
										}
									}
									$scope.tipoDeterminaziones = result;
									$scope.tipoDeterminaziones.unshift({});
								});

								$scope.verificaAllegatiRiservato();

								// Per attico esclusa gestione sottoscrittori
								// $scope.addFirstSubscriber();
								$scope.loadRubricaDestinatarioInterno();
								$scope.loadRubricaDestinatarioEsterno();
								$scope.filterSelected = true;
								if($scope.atto!=null && $scope.atto.pareri!=null && $scope.atto.pareri.length>0){
									$scope.risposte = [];
									for ( var i = 0; i<$scope.atto.pareri.length; i++) {
										var parere = $scope.atto.pareri[i];
										if(parere.origine == 'R'){
											$scope.risposte.push(parere);
										}

									}
								}
								$scope.initView(section, $scope.atto.tipoAtto.id);

							});

						}
						else {
							$scope.initView(section, $scope.atto.tipoAtto.id);
						}
					}
				};

				$scope.isPubblicatorePostPubblicazione = function(){

					return sharedSedutaFactory.isPubblicatoreGiunta();


				};

				$scope.changeTipoAttoIters = function(){
					$scope.atto.tipoIter = null;
					TipoIter.getByTipoAtto({tipoAttoId: $scope.atto.tipoAtto.id},function(iter){
						$scope.tipoiters = TipoIterService.listToMap(iter);
					});
				};

				/*
 * Per ATTICO esclusa gestione sottoscrittori
 *
$scope.addFirstSubscriber = function(){
	if($scope.checkIfToAddFirstSubscriber($scope.atto.sottoscrittori)){
		  var profilo = undefined;
		  Profilo.get({id:$scope.atto.profilo.id},function(result){
			  profilo = result;
			  if(profilo == undefined){
				  console.log("Profilo non trovato");
				  profilo =  $scope.atto.profilo;
			  }
			  $scope.sottoscrittoreAtto = {atto: { id: $scope.attoId} ,edit:true, profilo:profilo, fake:true};
			  if($scope.atto.sottoscrittori == undefined){
				  $scope.atto.sottoscrittori = [];
			  }
			  $scope.atto.sottoscrittori.unshift( $scope.sottoscrittoreAtto );
		  });
	  }
};

$scope.checkIfToAddFirstSubscriber = function(sottoscrittori){
	var result = false;
	if(sottoscrittori==undefined || sottoscrittori.length == 0){
		result = true;
	}else{
		if(sottoscrittori[0].profilo.id != $scope.atto.profilo.id){
			result = true;
		}
		var ids = [];
		for(var i = 1; i<sottoscrittori.length; i++){
			if(sottoscrittori[i].profilo.id == $scope.atto.profilo.id){
				ids.push(i);
			}
		}
		for(var i = 0; i<ids.length; i++){
			sottoscrittori.splice(ids[i], 1);
		}
	}
	return result;
};
*/

				$scope.redirectToAttoRevoca = function(attorevoca){
					TaskDesktop.getMyNextTask({id: attorevoca}, function(result, headers) {
						if ( angular.isDefined(result.taskId) && (result.taskId.length > 0) ) {
							window.location = "#/atto/" + attorevoca + "/0/" + result.taskId;
						}else{
							window.location = "#/atto/" + attorevoca + "/0/";
						}
					});
				};

				$scope.onAfterTaskUpdate = function(idAtto, nextTaskId, indexSezione) {
					$timeout(function () {

						if ( angular.isDefined(nextTaskId) && (nextTaskId.length > 0) ) {

							if (nextTaskId == $scope.taskBpmId) {
								if($stateParams.section == indexSezione)
									window.location.reload();
								else
									window.location = "#/atto/" + idAtto + "/"+indexSezione+"/" + nextTaskId;
							}
							else {
								window.location = "#/atto/" + idAtto + "/0/" + nextTaskId;
							}
						}
						else {
							window.location = "#/taskDesktop";
						}

					}, 0);
					$rootScope.activeExitUserConfirmation = null;
				};

				$scope.init();

				$scope.load($stateParams.id, $stateParams.section, $stateParams.taskBpmId);

				$scope.$watch(function() {return angular.element("form[name='editForm']").attr('class');}, function(newValue){
					$scope.checkDirtyForm();
				});

				$scope.checkDirtyForm = function() {
					if(!$scope.solaLettura)
						$rootScope.activeExitUserConfirmation = angular.element("form[name='editForm']").hasClass('ng-dirty');
				}

				/**
				 * Set the atto dirigenziale error.
				 * @param index The index of the atto dirigenziale.
				 * @param value The value of the error.
				 */
				$scope.setAttoDirigenzialeError = function(cod, value){
					if($scope.sezioniAtto!=undefined){
						var sezioneTipoAtto = $scope.sezioniAtto.find(item => item.codice.toLowerCase() === cod.toLowerCase());
						var sezione = $scope.attoDirigenzialeSezioni.find(item => item.target.toLowerCase() === cod.toLowerCase());
						if(( sezione!=null && sezione.visibile) && (sezioneTipoAtto!=null && sezioneTipoAtto.visibile)){
							sezione.error = value;
						}
					}
				};



				/**
				 * Calculate the disabled button.
				 * @returns Returns true if the buttons is disabled, false otherwise.
				 */
				$scope.calculateDisabledButton = function(){
					var ret = false;
					for(var i = 0; i<$scope.attoDirigenzialeSezioni.length; i++){
						if($scope.attoDirigenzialeSezioni[i].error){
							ret = true;
							break;
						}
					}
					return ret;
				};

				/**
				 * Reset all atto dirigenziale errors.
				 */
				$scope.resetAttoDirigenzialeError = function(){
					for ( var index in $scope.sezioniAtto) {
						$scope.setAttoDirigenzialeError($scope.sezioniAtto[index].codice, false);
						$scope.isSezioneVisibile({...$scope.sezioniAtto[index], target: $scope.sezioniAtto[index].codice});
					}
					$scope.verifyAllSections();
				};

				$scope.verifyAllSections = function(){
					$scope.setAttoDirigenzialeError('datiidentificativi', $scope.verificaCampiObbligatoriDatiIdentificativi());
					$scope.setAttoDirigenzialeError('assegnazioneincarichi', $scope.verificaCampiObbligatoriAssegnazioneIncarichi());
					$scope.setAttoDirigenzialeError('sottoscrizioni', $scope.verificaCampiObbligatoriSottoscrizioni());
					$scope.setAttoDirigenzialeError('riferimentinormativi', $scope.verificaCampiObbligatoriLR1508());
					$scope.setAttoDirigenzialeError('preambolo', $scope.verificaCampiObbligatoriPreambolo());
					$scope.setAttoDirigenzialeError('motivazione', $scope.verificaCampiObbligatoriMotivazione());
					$scope.setAttoDirigenzialeError('schede', $scope.verificaCampiObbligatoriBeneficiari());
					$scope.setAttoDirigenzialeError('dichiarazioni', $scope.verificaCampiObbligatoriSezioneContabile());
					$scope.setAttoDirigenzialeError('dispositivo', $scope.verificaCampiObbligatoriDispositivo());
					$scope.setAttoDirigenzialeError('domanda', $scope.verificaCampiObbligatoriDomanda());
					$scope.setAttoDirigenzialeError('allegati', $scope.verificaCampiObbligatoriAllegati());
					$scope.setAttoDirigenzialeError('trasparenza', $scope.verificaCampiObbligatoriDLGS33());
					$scope.setAttoDirigenzialeError('divulgazione', $scope.verificaCampiObbligatoriPubblicazione());
					$scope.setAttoDirigenzialeError('datiidentificativiconsiglio', $scope.verificaCampiObbligatoriDatiIdentificativiConsiglio());
					$scope.setAttoDirigenzialeError('divulgazionesemplificata', $scope.verificaCampiObbligatoriPubblicazioneSemplificata());
					$scope.setAttoDirigenzialeError('datiidentificativiverbale', $scope.verificaCampiObbligatoriDatiIdentificativiVerbale());


					$scope.disabledButton = $scope.calculateDisabledButton();
				}


				$scope.$watch("solaLettura", function(newValue){
					if(newValue) $rootScope.activeExitUserConfirmation = null;
				});
				$scope.disabledButton = false;
				$scope.$watch(function(scope) { return scope.atto; }, function(newValue,oldValue,scope){
					$log.debug("sono nel watch");
					if (angular.isDefined($scope.atto) && $scope.atto.id > 0 && newValue !== undefined) {
						$scope.verifyAllSections();
					}
					//var element = document.getElementById('UC_CIFRA2_011');
					//jQuery('#UC_CIFRA2_011').attr("disabled",(!angular.element("form[name='editForm']").$valid || $scope.taskLoading || $scope.verificaCampiObbligatori()));
				},true);

				$scope.$watch(function(scope) { return scope.incarichi; }, function(newValue,oldValue,scope){
					$log.debug("sono nel watch");
					if (angular.isDefined($scope.incarichi)) {
						$scope.setAttoDirigenzialeError('assegnazioneincarichi', $scope.verificaCampiObbligatoriAssegnazioneIncarichi());
						$scope.disabledButton = $scope.calculateDisabledButton();
					}
					//var element = document.getElementById('UC_CIFRA2_011');
					//jQuery('#UC_CIFRA2_011').attr("disabled",(!angular.element("form[name='editForm']").$valid || $scope.taskLoading || $scope.verificaCampiObbligatori()));
				},true);

				$scope.isEmpty = function (obj) {
					return angular.equals({}, obj);
				}

				$scope.isNumero = function (val) {
					return !isNaN(val % 1);
				}

				$scope.sortObject = function(hashmap){
					var array = [];
					for(var item in hashmap){
						array.push(hashmap[item])
					}
					return array;
				}


				$scope.elencoMovimentoContabileVisibile = true;
				$scope.handleElencoMovimentoContabileClick = function() {
					$scope.elencoMovimentoContabileVisibile = !$scope.elencoMovimentoContabileVisibile;
				}

				$scope.result = [];

				$('#mascheraWorkflowAtto').on('hide.bs.modal', function () {
					$scope.decisioneCorrente = null;
				});

			}])
	.filter('isNullOrUndefined', [function () {
		return function (items, property) {
			var arrayToReturn = [];
			for (var i = 0; i < items.length; i++) {
				var test = property !== undefined ? items[i][property] : items[i];
				if (test === undefined || test === null) {
					arrayToReturn.push(items[i]);
				}
			}
			return arrayToReturn;
		};
	}])
	.filter('notNullOrUndefined', [function () {
		return function (items, property) {
			var arrayToReturn = [];
			for (var i = 0; i < items.length; i++) {
				var test = property !== undefined ? items[i][property] : items[i];
				if (test !== undefined && test !== null) {
					arrayToReturn.push(items[i]);
				}
			}
			return arrayToReturn;
		};
	}]);


