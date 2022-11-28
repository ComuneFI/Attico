'use strict';

angular.module('cifra2gestattiApp')
    .controller('VersioneComposizioneConsiglioController', function ($rootScope, $scope, ngToast, VersioneComposizioneConsiglio, TipoProgressivo, ModelloHtml, Sezione, ParseLinks,TipoIter, Aoo, Campo) {
        $scope.versioneComposizioneConsiglios = [];
        $scope.statos = [];
        $scope.page = 1;
        
        $scope.tempSearch = {};
        $scope.versioneComposizioneConsiglioSearch = {};
        $scope.nuovaVersioneComposizioneConsiglio = {};
        $scope.aoos = [];
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.versioneComposizioneConsiglioSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	$scope.versioneComposizioneConsiglioSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		VersioneComposizioneConsiglio.query({page: $scope.page, per_page: 10, predefinita:searchObject.predefinita, version:searchObject.version, descrizione:searchObject.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.versioneComposizioneConsiglios = result;
	            });
        	}else{
        		VersioneComposizioneConsiglio.query({page: $scope.page, per_page: 10, predefinita:$scope.versioneComposizioneConsiglioSearch.predefinita, version:$scope.versioneComposizioneConsiglioSearch.version, descrizione:$scope.versioneComposizioneConsiglioSearch.descrizione}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.versioneComposizioneConsiglios = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        
        $scope.showUpdate = function (id) {
        	VersioneComposizioneConsiglio.get({id: id}, function(result) {
                $scope.versioneComposizioneConsiglio = result;
                $scope.editing = true;
            });
        };

        $scope.nuovaVersione = function () {
        	 $scope.nuovaVersioneComposizioneConsiglio.descrizione = "max 100 caratteri";
        	 
        	 $scope.nuovoNumeroVersione= 0;
        	 if($scope.versioneComposizioneConsiglios){
					for(var i = 0; i<$scope.versioneComposizioneConsiglios.length; i++){
						if($scope.versioneComposizioneConsiglios[i].version > $scope.nuovoNumeroVersione){
							$scope.nuovoNumeroVersione = $scope.versioneComposizioneConsiglios[i].version;
						}
					}
				}
        	 $scope.nuovaVersioneComposizioneConsiglio.version = $scope.nuovoNumeroVersione+1;
        	 $scope.nuovaVersioneComposizioneConsiglio.predefinita = false;
        };
        
        $scope.saveNuovaVersione = function () {
           
        	VersioneComposizioneConsiglio.save($scope.nuovaVersioneComposizioneConsiglio,
                function () {
                    $scope.refresh();
                });
        };

        $scope.save = function () {
            if ($scope.versioneComposizioneConsiglio.id != null) {
            	VersioneComposizioneConsiglio.update($scope.versioneComposizioneConsiglio,
                    function () {
                        $scope.refresh();
                    });
            } else {
            	VersioneComposizioneConsiglio.save($scope.versioneComposizioneConsiglio,
                    function () {
                        $scope.refresh();
                    });
            }
        };

       

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveVersioneComposizioneConsiglioModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
        	$scope.editing = false;
            $scope.versioneComposizioneConsiglio = {version: null, descrizione: null, id: null,predefinita:null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
