'use strict';

angular.module('cifra2gestattiApp')
    .controller('QualificaProfessionaleController', function ($scope, QualificaProfessionale, Aoo, ParseLinks) {
        $scope.qualificaProfessionales = [];
        $scope.tempSearch = {};
        $scope.qualificaProfessionaleSearch = {};
        $scope.page = 1;
        $scope.aoos = Aoo.getMinimal();
        
        $scope.stati = [
    		{
    			id: 0,
    			denominazione:"cifra2gestattiApp.qualificaProfessionale.filtro.stato.attive"
    		},
    		{
    			id: 1,
    			denominazione:"cifra2gestattiApp.qualificaProfessionale.filtro.stato.disattivate"
    		},
    		{
    			id: 2,
    			denominazione:"cifra2gestattiApp.qualificaProfessionale.filtro.stato.entrambe"
    		}
        ];
        
        $scope.tempSearch.stato = $scope.stati[2];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.qualificaProfessionaleSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in qualificaProfessionaleSearch*/
        	$scope.qualificaProfessionaleSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		var stato = null;
        		if(searchObject.stato){
        			stato = searchObject.stato.id;
        		}
        		QualificaProfessionale.query({
        			page: $scope.page,
        			per_page: 5,
        			denominazione: searchObject.denominazione,
        			stato: stato
        			}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.qualificaProfessionales = result;
	            });
        	}else{
        		QualificaProfessionale.query({page: $scope.page, per_page: 5, denominazione:$scope.qualificaProfessionaleSearch.denominazione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.qualificaProfessionales = result;
	            });
        	}
        };
        
        $scope.disable = function(qualificaprofessionale){
        	QualificaProfessionale.disable({ id: qualificaprofessionale.id}, function(data){
        		qualificaprofessionale.enabled = false;
     	    	console.log("stato disabilitazione: " + data.stato);
     	    });
        };
        
        $scope.enable = function(qualificaprofessionale){
        	QualificaProfessionale.enable({ id: qualificaprofessionale.id}, function(data){
        		qualificaprofessionale.enabled = true;
     	    	console.log("stato abilitazione: " + data.stato);
     	    });
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.qualificaProfessionaleSearch);
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            QualificaProfessionale.get({id: id}, function(result) {
                $scope.qualificaProfessionale = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#saveQualificaProfessionaleModal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.qualificaProfessionale.id != null) {
                QualificaProfessionale.update($scope.qualificaProfessionale,
                    function () {
                        $scope.refresh();
                    });
            } else {
                QualificaProfessionale.save($scope.qualificaProfessionale,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            QualificaProfessionale.get({id: id}, function(result) {
                $scope.qualificaProfessionale = result;
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

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveQualificaProfessionaleModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.qualificaProfessionale = {denominazione: null, id: null, enabled:true };
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
