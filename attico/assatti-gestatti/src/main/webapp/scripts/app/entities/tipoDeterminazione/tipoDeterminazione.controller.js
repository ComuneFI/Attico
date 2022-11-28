'use strict';

angular.module('cifra2gestattiApp')
    .controller('TipoDeterminazioneController', function ($rootScope, $scope, TipoDeterminazione, TipoAtto, ParseLinks) {
        $scope.tipoDeterminaziones = [];
        $scope.tempSearch = {};
        $scope.tipoDeterminazioneSearch = {};
        $scope.page = 1;
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.tipoDeterminazioneSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in tipoDeterminazioneSearch*/
        	$scope.tipoDeterminazioneSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		TipoDeterminazione.query({page: $scope.page, per_page: 5, descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDeterminaziones = result;
	            });
        	}else{
        		TipoDeterminazione.query({page: $scope.page, per_page: 5, descrizione:$scope.tipoDeterminazioneSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.tipoDeterminaziones = result;
	            });
        	}
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	$scope.attiEsistenti = false;
            TipoDeterminazione.get({id: id}, function(result) {
                $scope.tipoDeterminazione = result;
                if(result.atti){
                	$scope.attiEsistenti = true;
                	//$('#operationNotPermitted').modal('show');
                	$('#saveTipoDeterminazioneModal').modal('show');
                } else {
                	$scope.attiEsistenti = false;
                	$('#saveTipoDeterminazioneModal').modal('show');
                }
            });
        };

        $scope.save = function () {
            if ($scope.tipoDeterminazione.id != null) {
                TipoDeterminazione.update($scope.tipoDeterminazione,
                    function () {
                        $scope.refresh();
                    });
            } else {
                TipoDeterminazione.save($scope.tipoDeterminazione,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
        	TipoDeterminazione.get({id: id}, function(result) {
                $scope.tipoDeterminazione = result;
                if(result.atti){
                	$('#operationNotPermitted').modal('show');
                } else {
                	$('#deleteTipoDeterminazioneConfirmation').modal('show');
                }
            });
        };

        $scope.confirmDelete = function (id) {
            TipoDeterminazione.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTipoDeterminazioneConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTipoDeterminazioneModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tipoDeterminazione = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.tipoAttos = TipoAtto.query(function(result) {
        	$scope.listTipoAtto = result.map( function (tipoCur) {
                return tipoCur.descrizione;
            });
        });
        
        $scope.disable = function(tipoDeterminazione){
        	$rootScope.showMessage({title:'Conferma disabilitazione', siButton:true, noButton:true, 
        	siFunction: function(){
        		TipoDeterminazione.disable( {id:tipoDeterminazione.id} ,function(){
        			$('#genericMessage').modal('hide');
        			$scope.refresh();
        		});
        	},
        	noFunction:function(){
        		$('#genericMessage').modal('hide');
        	},
        	body:'Disabilitando il tipo di atto ' + tipoDeterminazione.codice + ' non sar\u00E0 pi\u00F9 possibile istruire atti di tale tipologia. Confermare l\'operazione?'});
        };
        
        $scope.enable = function(tipoDeterminazione){
        	$rootScope.showMessage({title:'Conferma abilitazione', siButton:true, noButton:true, 
            	siFunction: function(){
            		TipoDeterminazione.enable( {id:tipoDeterminazione.id} ,function(){
            			$('#genericMessage').modal('hide');
            			$scope.refresh();
            		});
            	},
            	noFunction:function(){
            		$('#genericMessage').modal('hide');
            	},
            	body:'Abilitando il tipo di atto ' + tipoDeterminazione.codice + ' sar\u00E0 possibile istruire atti di tale tipologia. Confermare l\'operazione?'});
        };
    });
