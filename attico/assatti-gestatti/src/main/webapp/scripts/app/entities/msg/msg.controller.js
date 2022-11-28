'use strict';

angular.module('cifra2gestattiApp')
    .controller('MsgController', ['$scope', '$rootScope','Msg','ParseLinks', 'CategoriaMsg', 'Aoo',
        function ($scope, $rootScope, Msg, ParseLinks, CategoriaMsg, Aoo) {
        $scope.msgs = [];
        $scope.msg = {};
        $scope.msgSearch = {};
        $scope.tempSearch = {};
        $scope.page = 1;
        $scope.aooAttive = Aoo.getAllEnabled();
        $scope.prioritas = Msg.prioritas();
        $scope.categorieMsg = CategoriaMsg.getAllEnabled();
        $scope.now = new Date().toISOString().substring(0, 10);
        
        $scope.clear = function(){
        	$scope.msg = {};
        	$scope.msg.validodal = new Date();
        	$scope.msg.pubblicatoIntranet = true;
        	$scope.msg.pubblicatoInternet = false;
        };
        
        $scope.forceExpire = function(id){
        	Msg.forceExpire({id:id}, function(res){
        		$scope.load();
        	})
        };
        
        $scope.showUpdate = function(id){
        	Msg.get({id:id}, function(res){
        		$scope.msg = res;
        		$("#saveMsgModal").modal("show");
        	});
        };
        
        $scope.save = function () {
        	Msg.save($scope.msg, function () {
        		$("#saveMsgModal").modal("hide");
                $scope.load();
            });
        };

        $scope.load = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		Msg.search({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.msgs = result;
	                $scope.now = new Date().toISOString().substring(0, 10);
	            });
        	}else{
        		Msg.search({page: $scope.page, per_page: 10}, $scope.msgSearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.msgs = result;
	                $scope.now = new Date().toISOString().substring(0, 10);
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in msgSearch*/
        	$scope.msgSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.load($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.msgSearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.load();
        };
        
        $scope.load();
        
    }]);
