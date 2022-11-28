'use strict';

angular.module('cifra2gestattiApp')
    .controller('VersioneComposizioneGiuntaController', function ($rootScope, $scope, ngToast, VersioneComposizioneGiunta, TipoProgressivo, ModelloHtml, Sezione, ParseLinks,TipoIter, Aoo, Campo) {
        $scope.versioneComposizioneGiuntas = [];
        $scope.statos = [];
        $scope.page = 1;
        
        $scope.tempSearch = {};
        $scope.versioneComposizioneGiuntaSearch = {};
        $scope.nuovaVersioneComposizioneGiunta = {};
        $scope.aoos = [];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.versioneComposizioneGiuntaSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	$scope.versioneComposizioneGiuntaSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		VersioneComposizioneGiunta.query({page: $scope.page, per_page: 10, predefinita:searchObject.predefinita, version:searchObject.version, descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.versioneComposizioneGiuntas = result;
	            });
        	}else{
        		VersioneComposizioneGiunta.query({page: $scope.page, per_page: 10, predefinita:$scope.versioneComposizioneGiuntaSearch.predefinita, version:$scope.versioneComposizioneGiuntaSearch.version, descrizione:$scope.versioneComposizioneGiuntaSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.versioneComposizioneGiuntas = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        
        $scope.showUpdate = function (id) {
        	VersioneComposizioneGiunta.get({id: id}, function(result) {
                $scope.versioneComposizioneGiunta = result;
                $scope.editing = true;
            });
        };

        $scope.nuovaVersione = function () {
        	 $scope.nuovaVersioneComposizioneGiunta.descrizione = "max 100 caratteri";
        	 
        	 $scope.nuovoNumeroVersione= 0;
        	 if($scope.versioneComposizioneGiuntas){
					for(var i = 0; i<$scope.versioneComposizioneGiuntas.length; i++){
						if($scope.versioneComposizioneGiuntas[i].version > $scope.nuovoNumeroVersione){
							$scope.nuovoNumeroVersione = $scope.versioneComposizioneGiuntas[i].version;
						}
					}
				}
        	 $scope.nuovaVersioneComposizioneGiunta.version = $scope.nuovoNumeroVersione+1;
        	 $scope.nuovaVersioneComposizioneGiunta.predefinita = false;
        };
        
        $scope.saveNuovaVersione = function () {
           
        	VersioneComposizioneGiunta.save($scope.nuovaVersioneComposizioneGiunta,
                function () {
                    $scope.refresh();
                });
        };

        $scope.save = function () {
            if ($scope.versioneComposizioneGiunta.id != null) {
            	VersioneComposizioneGiunta.update($scope.versioneComposizioneGiunta,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	VersioneComposizioneGiunta.save($scope.versioneComposizioneGiunta,
                    function () {
                        $scope.refresh();
                    });
            }
        };

       

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveVersioneComposizioneGiuntaModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.editing = false;
            $scope.versioneComposizioneGiunta = {version: null, descrizione: null, id: null,predefinita:null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
