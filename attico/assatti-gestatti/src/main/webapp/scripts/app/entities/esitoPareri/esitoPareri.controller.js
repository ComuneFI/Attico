'use strict';

angular.module('cifra2gestattiApp')
    .controller('EsitoPareriController', function ($rootScope, $scope, EsitoPareri, ParseLinks, TipoAtto) {
        $scope.esitoPareris = [];
        $scope.page = 1;
        $scope.esitoPareriSearch = {};
        $scope.tempSearch = {};
        
        
        TipoAtto.query({consiglio:'1'}, function(result){
        	$scope.tipoAttos = result;
        });
        
        $scope.executeDisable = function(id){
        	EsitoPareri.disable({id:id}, function(res){
        		$scope.loadAll();
        	});
        };
        
        $scope.disable = function(esitoPareri){
        	$rootScope.showMessage({title:'Conferma disabilitazione', siButton:true, noButton:true, 
        	siFunction: function(){
        		EsitoPareri.disable( {id:esitoPareri.id} ,function(){
        			$('#genericMessage').modal('hide');
        			$scope.refresh();
        		});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Confermare l\'operazione?'});
        };
        
        $scope.enable = function(esitoPareri){
        	$rootScope.showMessage({title:'Conferma abilitazione', siButton:true, noButton:true, 
            	siFunction: function(){
            		EsitoPareri.enable( {id:esitoPareri.id} ,function(){
            			$('#genericMessage').modal('hide');
            			$scope.refresh();
            		});
            	},
            	noFunction:function(){
            		$('#genericMessage').modal('hide');
            	},
            	body:'Confermare l\'operazione?'});
        };

        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.esitoPareriSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in esitoPareriSearch*/
        	$scope.esitoPareriSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.compareFn = function(obj1, obj2){
            return obj1.id === obj2.id;
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		EsitoPareri.query({page: $scope.page, per_page: 10, idEsitoPareri:searchObject.id, tipoEsitoPareri:searchObject.tipo, tipoAtto:(searchObject.tipoAtto && searchObject.tipoAtto.descrizione ? searchObject.tipoAtto.descrizione : ''), valore:searchObject.valore}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.esitoPareris = result;
	            });
        	}else{
	            EsitoPareri.query({page: $scope.page, per_page: 10, idEsitoPareri:$scope.esitoPareriSearch.id, tipoEsitoPareri:$scope.esitoPareriSearch.tipo, tipoAtto:($scope.esitoPareriSearch.tipoAtto && $scope.esitoPareriSearch.tipoAtto.descrizione ? $scope.esitoPareriSearch.tipoAtto.descrizione : ''), valore:$scope.esitoPareriSearch.valore}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.esitoPareris = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            EsitoPareri.get({id: id}, function(result) {
                $scope.esitoPareri = result;
                $('#saveEsitoPareriModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.esitoPareri.id != null) {
            	EsitoPareri.update($scope.esitoPareri,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	EsitoPareri.save($scope.esitoPareri,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            EsitoPareri.get({id: id}, function(result) {
                $scope.esitoPareri = result;
                $('#deleteEsitoPareriConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            EsitoPareri.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteEsitoPareriConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveEsitoPareriModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.esitoPareri = {codice: null, id:null, valore:null, tipo:null, enabled:null, tipoAtto:null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
