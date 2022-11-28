'use strict';

angular.module('cifra2gestattiApp')
    .controller('RuoloController', function ($scope, Ruolo, ParseLinks) {
        $scope.ruolos = [];
        $scope.page = 1;
        $scope.ruoloSearch = {};
        $scope.tempSearch = {};
		$scope.tipologieRuolo = [{valore:'SISTEMA',label:'Di Sistema'},{valore:'OPERATIVO',label:'Operativo'}, {valore:'PROCESSO', label:'Di Processo'}];        

		$scope.getLabelByTipoVal = function(cod){
			if(cod){
				return $scope.tipologieRuolo.filter((o) => o.valore == cod)[0].label;
			}else{
				return "";
			}
		};

        $scope.executeDisable = function(id){
        	Ruolo.disable({id:id}, function(res){
        		$scope.loadAll();
        	});
        };
        
        $scope.disable = function(id){
        	Ruolo.isRoleUsedInGruppoRuolo({id:id}, function(res){
        		if(res.isUsed){
        			$('#disableRuoloDenied').modal('show');
        		}else{
        			$scope.executeDisable(id);
        		}
        	});
        };
        
        $scope.enable = function(id){
        	Ruolo.enable({id:id}, function(res){
        		$scope.loadAll();
        	})
        };

        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.ruoloSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in ruoloSearch*/
        	$scope.ruoloSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.compareFn = function(obj1, obj2){
            return obj1.id === obj2.id;
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Ruolo.query({page: $scope.page, per_page: 5, idRuolo:searchObject.id, codiceRuolo:searchObject.codice, descrizione:searchObject.descrizione, tipo:(searchObject.tipo ? searchObject.tipo.valore : null)}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ruolos = result;
	            });
        	}else{
	            Ruolo.query({page: $scope.page, per_page: 5, idRuolo:$scope.ruoloSearch.id, codiceRuolo:$scope.ruoloSearch.codice, descrizione:$scope.ruoloSearch.descrizione, tipo:($scope.ruoloSearch.tipo ? $scope.ruoloSearch.tipo.valore : null)}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.ruolos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Ruolo.get({id: id}, function(result) {
                $scope.ruolo = result;
                if(!$scope.ruolo){
                    $scope.ruolo = false;
                }
                $('#saveRuoloModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.ruolo.id != null) {
                Ruolo.update($scope.ruolo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Ruolo.save($scope.ruolo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Ruolo.get({id: id}, function(result) {
                $scope.ruolo = result;
                $('#deleteRuoloConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Ruolo.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRuoloConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveRuoloModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ruolo = {codice: null, descrizione: null, haqualifica: null, id: null, tipo:null, onlyAdmin: false};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
