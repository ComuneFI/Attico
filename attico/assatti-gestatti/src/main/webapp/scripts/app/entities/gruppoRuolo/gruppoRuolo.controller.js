'use strict';

angular.module('cifra2gestattiApp')
    .controller('GruppoRuoloController', function ($scope, GruppoRuolo, Aoo, ParseLinks,Ruolo) {
        $scope.gruppoRuolos = [];
        $scope.aoos = Aoo.getMinimal(function(result) {
        	$scope.listAllAoo = $scope.aoos.map( function (aooCur) {
                return aooCur.descrizione;
            });
        });
        $scope.ruolos = Ruolo.getAllEnabled(function(result) {
        	$scope.listAllRuoli = $scope.ruolos.map( function (ruoloCur) {
                return ruoloCur.descrizione;
        	});
        });
        $scope.grupporuoloSearch = {};
        $scope.tempSearch = {};

        $scope.page = 1;
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.grupporuoloSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in grupporuoloSearch*/
        	$scope.grupporuoloSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
	            GruppoRuolo.query({page: $scope.page, per_page: 5, idGruppoRuolo:searchObject.idGruppoRuolo, denominazione:searchObject.denominazione, aoo:searchObject.aoo, ruolo:searchObject.ruolo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.gruppoRuolos = result;
	            });
        	}else{
        		GruppoRuolo.query({page: $scope.page, per_page: 5, idGruppoRuolo:$scope.grupporuoloSearch.idGruppoRuolo, denominazione:$scope.grupporuoloSearch.denominazione, aoo:$scope.grupporuoloSearch.aoo, ruolo:$scope.grupporuoloSearch.ruolo}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.gruppoRuolos = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            GruppoRuolo.get({id: id}, function(result) {
                $scope.gruppoRuolo = result;
                $('#saveGruppoRuoloModal').modal('show');
            });
        };

        $scope.save = function () {
        	
        	/*
        	 * eliminato il collegamento con l'AOO in quanto secondo Innova non ha senso.
        	 * ISSUE: UTE_006: Crud Gruppo Ruolo - eliminare legame AOO 
        	 * DATA: 31/05/2016
        	 */
        	$scope.gruppoRuolo.aoo = null;
        	
            if ($scope.gruppoRuolo.id != null) {
                GruppoRuolo.update($scope.gruppoRuolo,
                    function () {
                        $scope.refresh();
                    });
            } else {
                GruppoRuolo.save($scope.gruppoRuolo,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            GruppoRuolo.get({id: id}, function(result) {
                $scope.gruppoRuolo = result;
                $('#deleteGruppoRuoloConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            GruppoRuolo.delete({id: id},
                function (res) {
            		if(res.success){
	                    $scope.loadAll();
	                    $('#deleteGruppoRuoloConfirmation').modal('hide');
	                    $scope.clear();
            		}else{
            			$scope.erroreCancellazione = res.message;
            			$('#deleteGruppoRuoloConfirmation').modal('hide');
            			$('#deleteGruppoRuoloDeletingError').modal('show');
            		}
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveGruppoRuoloModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.erroreCancellazione = null;
        	$scope.showDetailErroreCancellazione = false;
            $scope.gruppoRuolo = {denominazione: null, id: null, aoo:{id:null}};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
