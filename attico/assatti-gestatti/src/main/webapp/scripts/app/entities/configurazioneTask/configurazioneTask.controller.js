'use strict';

angular.module('cifra2gestattiApp')
    .controller('ConfigurazioneTaskController', function ($scope, ConfigurazioneTask, Aoo, Ruolo, ParseLinks, ngToast, $filter) {
    	$scope.configurazioneTasks = [];
        
		$scope.loading = true;
		
		$scope.deselezionaUffici = function(condizione){
			if(condizione){
				$scope.configurazioneTask.listAooObjList = [];
			}			
		};

		$scope.deselezionaCheckUffici = function(condizione){
			if(condizione){
				$scope.configurazioneTask.proponente = false;
				$scope.configurazioneTask.ufficioCorrente = false;
				$scope.configurazioneTask.ufficiLivelloSuperiore = false;
			}
		};

		$scope.deselezionaLivelloSuperiore = function(condizione){
			if(condizione){
				$scope.configurazioneTask.ufficiLivelloSuperiore = false;
			}
		};
		
		$scope.changeStessaDirezioneUffProponente = function(condizione){
			if(condizione){
				$scope.configurazioneTask.proponente = false;
				$scope.configurazioneTask.ufficioCorrente = false;
				$scope.configurazioneTask.ufficiLivelloSuperiore = false;
			}
		};
		
		
                
        $scope.loadAll = function() {
        	$scope.loading = true;
        	$scope.configurazioneTasks=[];
        	
        	Aoo.getMinimal({}, function(result){
            	$scope.aoos = result;
            	$scope.aoos = $filter('orderBy')($scope.aoos, '+codice');
            });
        	
        	Ruolo.query({}, function(result){
        		$scope.ruoli = result;
            	$scope.ruoli = $filter('orderBy')($scope.ruoli, '+codice');
        	});
        	
        	ConfigurazioneTask.findAll({}, function(result){
        		$scope.configurazioneTasks=result;
        		$scope.loading = false;
        	});

        };
        
        $scope.loadAll();
        
        $scope.getAooDescription = function(aooId){
        	for(var x in $scope.aoos){
        		if(aooId==$scope.aoos[x].id){
        			return $scope.aoos[x].descrizione;
        		}
        	}
        };
        
        $scope.getRuoloDescription = function(ruoloId){
        	for(var x in $scope.ruoli){
        		if(ruoloId==$scope.ruoli[x].id){
        			return $scope.ruoli[x].descrizione;
        		}
        	}
        };
        
        $scope.generaAooObjList = function(listAooIds, listAooObjList){
        	for(var i = 0; i < listAooIds.length; i++){
        		for(var j = 0; j < $scope.aoos.length; j++){
        			if($scope.aoos[j].id == listAooIds[i]){
        				listAooObjList.push($scope.aoos[j]);
        				break;
        			}
        		}
        	}
        };
        
        $scope.generaAooIdsList = function(listAooObjList){
        	$scope.configurazioneTask.listAoo = [];
        	for(var i = 0; i < listAooObjList.length; i++){
        		$scope.configurazioneTask.listAoo.push(listAooObjList[i].id);
        	}
        };
        
        $scope.generaRuoliObjList = function(listRuoliIds, listRuoliObjList){
        	for(var i = 0; i < listRuoliIds.length; i++){
        		for(var j = 0; j < $scope.ruoli.length; j++){
        			if($scope.ruoli[j].id == listRuoliIds[i]){
        				listRuoliObjList.push($scope.ruoli[j]);
        				break;
        			}
        		}
        	}
        };
        
        $scope.generaRuoliIdsList = function(listRuoliObjList){
        	$scope.configurazioneTask.listRuolo = [];
        	for(var i = 0; i < listRuoliObjList.length; i++){
        		$scope.configurazioneTask.listRuolo.push(listRuoliObjList[i].id);
        	}
        };
      
        $scope.showUpdate = function (id) {
        	$scope.configurazioneTask = {};
            ConfigurazioneTask.get({id: id}, function(result) {
                $scope.configurazioneTask = result;
                $scope.configurazioneTask.listAooObjList = [];
                $scope.configurazioneTask.listRuoliObjList = [];
                $scope.generaAooObjList($scope.configurazioneTask.listAoo, $scope.configurazioneTask.listAooObjList);
                $scope.generaRuoliObjList($scope.configurazioneTask.listRuolo, $scope.configurazioneTask.listRuoliObjList);
                $('#saveConfigurazioneTaskModal').modal('show');
            });
        };
        
        $scope.showDetails = function (id) {
            ConfigurazioneTask.get({id: id}, function(result) {
                $scope.configurazioneTask = result;
                $('#viewConfigurazioneTaskModal').modal('show');
            });
        };

        $scope.save = function () {
        	$scope.generaAooIdsList($scope.configurazioneTask.listAooObjList);
        	delete $scope.configurazioneTask.listAooObjList;
        	$scope.generaRuoliIdsList($scope.configurazioneTask.listRuoliObjList);
        	delete $scope.configurazioneTask.listRuoliObjList;
	        ConfigurazioneTask.save($scope.configurazioneTask, function () {
                $scope.refresh();
            }, function (error) {
            	ngToast.create(  { className: 'danger', content: error.headers().failure} );
            	$scope.refresh();
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveConfigurazioneTaskModal').modal('hide');
            $('#viewConfigurazioneTaskModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.configurazioneTask = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
    });
