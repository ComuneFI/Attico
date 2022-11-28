'use strict';

angular.module('cifra2gestattiApp')
    .controller('ConfigurazioneRiversamentoController', function ($scope, ConfigurazioneRiversamento, TipoDocumentoSerie, Diogene, ParseLinks, ngToast, Aoo, TipoDocumento, TipoAtto, $filter) {
    	$scope.configurazioneRiversamentos = [];
        TipoDocumentoSerie.query({}, function(result){
        	$scope.tipodocumentoseries = result;
        	$scope.tipodocumentoseries = $scope.tipodocumentoseries.filter(function(v){
        		return v.enable;
        	});
        	$scope.tipodocumentoseries = $filter('orderBy')($scope.tipodocumentoseries, '+descrizione');
        });
        TipoDocumento.query({}, function(result){
        	$scope.tipodocumentos = result;
        	$scope.tipodocumentos = $filter('orderBy')($scope.tipodocumentos, '+descrizione');
        });
        
        TipoAtto.query({}, function(result){
        	$scope.tipoattos = result;
        });
        $scope.loading = true;
        Aoo.getMinimal({}, function(result){
        	$scope.aoos = result;
        	$scope.aoos = $filter('orderBy')($scope.aoos, '+codice');
        	$scope.aoos.unshift({id:-1, codice:'-1', descrizione:'Nessuna'});
        	$scope.aoos.unshift({id:0, codice:'0', descrizione:'Tutte'});
        });
        
        $scope.page = 1;
        $scope.configurazioneSearch = {};
        $scope.tempSearch = {};
        
        $scope.loadAll = function(searchObject) {
        	$scope.loading = true;
        	if(searchObject!=undefined && searchObject!=null){
        		if(!searchObject.id){
        			searchObject.id = null;
        		}
        		ConfigurazioneRiversamento.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.configurazioneRiversamentos = result;
	                $scope.loading = false;
	            });
        	}else{
        		ConfigurazioneRiversamento.search({page: $scope.page, per_page: 10}, $scope.configurazioneSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.configurazioneRiversamentos = result;
	                $scope.loading = false;
	            });
        	}
        };
        
        $scope.loadAll();
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in configurazioneSearch*/
        	$scope.configurazioneSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.configurazioneSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };

        $scope.showUpdate = function (id) {
            ConfigurazioneRiversamento.get({id: id}, function(result) {
                $scope.configurazioneRiversamento = result;
                $('#saveSerieModal').modal('show');
            });
        };

        $scope.save = function () {
	        ConfigurazioneRiversamento.save($scope.configurazioneRiversamento, function () {
                $scope.refresh();
            }, function (error) {
            	ngToast.create(  { className: 'danger', content: error.headers().failure} );
            	$scope.refresh();
            });
        };

        $scope.disable = function(serie){
        	ConfigurazioneRiversamento.disable({ id: serie.id}, function(data){
        		serie = data;
        		$scope.refresh();
     	    });
        };
        
        $scope.enable = function(serie){
        	ConfigurazioneRiversamento.enable({ id: serie.id}, function(data){
        		serie = data;
        		$scope.refresh();
     	    }, function (error) {
            	ngToast.create(  { className: 'danger', content: error.headers().failure} );
            	$scope.refresh();
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveSerieModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.configurazioneRiversamento = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll($scope.configurazioneSearch);
        };
        
        $scope.generate = function (serie) {
        	$scope.generaSpin = true;            	
            Diogene.generaSerie(serie, function(result) {
            	$scope.generaSpin = false;
            }, function(error){
            	$scope.generaSpin = false;
            });
        };
    });
