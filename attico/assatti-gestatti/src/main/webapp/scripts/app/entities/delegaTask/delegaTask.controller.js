'use strict';

angular.module('cifra2gestattiApp')
    .controller('DelegaTaskController', function ($scope, DelegaTask, Profilo, ParseLinks, $rootScope, Principal, ProfiloAccount, $state, TaskDesktop, BpmSeparator, TipoAtto) {
    	$scope.delegaSearch = {};
        $scope.tempSearch = {};
        $scope.deleghe = [];
        $scope.delega = {};
        $scope.deleganti = [];
        $scope.delegati = [];
        $scope.page = 1;
        $scope.delegantiLoaded = false;
        $scope.delegatiLoaded = false;
        $scope.bpmSeparator = BpmSeparator;
        $scope.taskLoading = false;
        $scope.tipiAtto = [];
        
        $scope.pageTask = 1;
        $scope.tasks = [];
        $scope.taskSearch = {};
        $scope.tempTaskSearch = {};
        
        $scope.loadTipiAtto = function(){
        	TipoAtto.query({}, function(tipiAtto){
        		$scope.tipiAtto = tipiAtto;
        	});
        };
        
        $scope.onlyUsername = function(bpmUserName){
    		if(bpmUserName){
    			return bpmUserName.split($scope.bpmSeparator.BPM_USERNAME_SEPARATOR)[0];
    		}
    		
    		return "";
    	};
        
        $scope.findAllTaskInCarico = function(searchObject){
        	$scope.taskLoading = true;
        	$scope.tasks = [];
        	if(!$scope.delega || !$scope.delega.profiloDelegante || !$scope.delega.profiloDelegante.id){
        		$scope.taskLoading = false;
        		$scope.linksTask = null;
        		return;
        	}
    		if(searchObject!=undefined && searchObject!=null){
    			searchObject.deleganteId = $scope.delega.profiloDelegante.id;
    			TaskDesktop.findAllInCarico({page: $scope.pageTask, per_page: 10}, searchObject, function(result, headers) {
	                $scope.linksTask = ParseLinks.parse(headers('link'));
	                $scope.taskLoading = false;
	                $scope.tasks = result;
	            });
        	}else{
        		$scope.taskSearch.deleganteId = $scope.delega.profiloDelegante.id;
        		TaskDesktop.findAllInCarico({page: $scope.pageTask, per_page: 10}, $scope.taskSearch, function(result, headers) {
	                $scope.linksTask = ParseLinks.parse(headers('link'));
	                $scope.taskLoading = false;
	                $scope.tasks = result;
	            });
        	}
        };
        
        $scope.deleganteSelected = function(){
        	$scope.delegati = [];
        	$scope.tasks = [];
        	$scope.delega.taskBpmId = null;
        	$scope.delega.atto = null;
        	$scope.delega.lavorazione = null;
        	$scope.delega.assigneeOriginario = null;
        	$scope.delega.profiloDelegato = null;
        };
        
        $scope.taskSelected = function(task, delega){
        	$scope.delegati = [];
        	delega.taskBpmId = null;
    		delega.atto = null;
    		delega.lavorazione = null;
    		delega.assigneeOriginario = null;
            delega.profiloDelegato = null;
        	if($scope.delega && $scope.delega.profiloDelegante && $scope.delega.profiloDelegante.id && task && task.taskBpm && task.atto){
        		var profiloAooId = null;
            	if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
            		profiloAooId = $rootScope.profiloattivo.aoo.id;
            	}
    			Profilo.getProfilosRiassegnazioneTask({profiloRiassegnazioneId: $scope.delega.profiloDelegante.id, tipoAttoCodice: task.atto.tipoAtto.codice, profiloAooId:profiloAooId}, function(delegatiList){
    				$scope.delegati = delegatiList;
    				delega.taskBpmId = task.taskBpm.id;
            		delega.atto = task.atto;
            		delega.lavorazione = task.taskBpm.nomeVisualizzato;
            		delega.assigneeOriginario = task.taskBpm.idAssegnatario;
    			});
        	}
        };
        
        $scope.resetRicercaTask = function(){
        	$scope.pageTask = 1;
        	$scope.taskSearch = {};
        	$scope.tempTaskSearch = {};
        	$scope.findAllTaskInCarico();
        };
        
        $scope.ricercaTask = function(){
        	$scope.pageTask = 1;
        	$scope.taskSearch = jQuery.extend(true, {}, $scope.tempTaskSearch);
        	$scope.findAllTaskInCarico($scope.tempTaskSearch);
        };
        
        $scope.loadPageTask = function(page) {
            $scope.pageTask = page;
            $scope.findAllTaskInCarico();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.delegaSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in profiloSearch*/
        	$scope.delegaSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	var profiloAooId = null;
        	if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
        		profiloAooId = $rootScope.profiloattivo.aoo.id;
        	}
        	if(searchObject!=undefined && searchObject!=null){
        		searchObject.profiloAooId = profiloAooId;
        		DelegaTask.search({page: $scope.page, per_page: 5}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.deleghe = result;
	            });
        	}else{
        		$scope.delegaSearch.profiloAooId = profiloAooId;
        		DelegaTask.search({page: $scope.page, per_page: 5}, $scope.delegaSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.deleghe = result;
	            });
        	}
        };

        $scope.getDeleganteDelegatoLabel = function(item){
        	return item.utente.cognome +' '+ item.utente.nome +' (' + item.descrizione + ')';
        } 
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();
        
        $scope.save = function () {
            if ($scope.delega.id != null) {
            	DelegaTask.update($scope.delega,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	DelegaTask.save($scope.delega,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveDelegaModal').modal('hide');
            $scope.clear();
        };
        
        $scope.showCreate = function(){
        	$scope.clear();
        	if(!$scope.delegantiLoaded){
        		$scope.delegantiLoaded = true;
        		$scope.deleganti = [];
        		if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
        			var profiloAooId = $rootScope.profiloattivo.aoo.id;
        			Profilo.getByAooIdRicorsive({aooId:profiloAooId}, function(profili){
        				$scope.deleganti = profili;
        			});
        		}else{
	        		Profilo.getAllActiveBasic({includiAoo:true}, function(profili){
	        			$scope.deleganti = profili;
	        		});
        		}
        	}
        	/*$scope.resetRicercaTask();*/
        	$('#saveDelegaModal').modal('show');
        };
        
        $scope.showUpdate = function (delega) {
        	$scope.delega = null;
        	var profiloAooId = null;
			if(!Principal.isInAnyRole($state.current.data.roles) && !ProfiloAccount.isInAnyRole(["ROLE_OPERATORE_DELEGA"]) && ProfiloAccount.isInAnyRole(["ROLE_REFERENTE_TECNICO"])){
				profiloAooId = $rootScope.profiloattivo.aoo.id;
			}
        	if(delega.tipo == 'ONE_TASK_ONLY'){
	        	TaskDesktop.existsActiveTask({taskId:delega.taskBpmId}, function(ris){
	        		if(ris && ris.taskExists){
	        			Profilo.getProfilosRiassegnazioneTask({profiloAooId: profiloAooId, profiloRiassegnazioneId: delega.profiloDelegante.id, tipoAttoCodice: delega.atto.tipoAtto.codice}, function(delegatiList){
	        				$scope.delegati = delegatiList;
	        				$scope.delega = jQuery.extend(true, {}, delega);
	        				/*
	    	        		DelegaTask.get({id: delega.id}, function(result) {
	    	                    $scope.delega = result;
	    	                });
	    	                */
	        				$('#saveDelegaModal').modal('show');
	        			});
	        		}else{
	        			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Il task selezionato risulta gi\u00E0 lavorato per cui la delega non pu\u00F2 essere modificata.'});
	        		}
	        	});
        	}else if(delega.tipo == 'FULL_ITER'){
        		Profilo.getProfilosRiassegnazioneTask({profiloAooId: profiloAooId, profiloRiassegnazioneId: delega.profiloDelegante.id, tipoAttoCodice: delega.atto.tipoAtto.codice}, function(delegatiList){
    				$scope.delegati = delegatiList;
    				$scope.delega = jQuery.extend(true, {}, delega);
    				$('#saveDelegaModal').modal('show');
    			});
        	}
        };
        
        $scope.delete = function (delega) {
        	if(delega.tipo == 'ONE_TASK_ONLY'){
	        	TaskDesktop.existsActiveTask({taskId:delega.taskBpmId}, function(ris){
	        		if(ris && ris.taskExists){
			        	DelegaTask.get({id: delega.id}, function(result) {
			                $scope.delega = result;
			                $('#deleteDelegaConfirmation').modal('show');
			            });
	        		}else{
	        			$rootScope.showMessage({title:'Attenzione', okButton:true, body:'Il task selezionato risulta gi\u00E0 lavorato per cui la delega non pu\u00F2 essere cancellata.'});
	        		}
	        	});
        	}else if(delega.tipo == 'FULL_ITER'){
        		DelegaTask.get({id: delega.id}, function(result) {
	                $scope.delega = result;
	                $('#deleteDelegaConfirmation').modal('show');
	            });
        	}
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
        	DelegaTask.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteDelegaConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteDelegaConfirmation').modal('hide');
            			$('#deleteDelegaDeletingError').modal('show');
            		}
                });
        };
        
        $scope.clear = function () {
            $scope.delega = {id: null, enabled: true, profiloDelegante: null, profiloDelegato: null, taskBpmId: null, lavorazione: null, codiceCifra:null, assigneeOriginario:null, assigneeDelegato:null, atto:null};
            $scope.tasks = [];
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.loadTipiAtto();
    });