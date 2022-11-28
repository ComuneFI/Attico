'use strict';

angular.module('cifra2gestattiApp')
    .controller('NewsController', function ($scope, $rootScope, News, Profilo, ParseLinks) {
        $scope.newss = [];
        
        $scope.page = 1;
        $scope.loadAll = function() {
        	if($rootScope.profiloattivo !== null){
	            News.query({page: $scope.page, per_page: 20, profiloid:$rootScope.profiloattivo.id}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.newss = result;
	            });
        	}
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            News.get({id: id}, function(result) {
                $scope.news = result;
                $('#saveNewsModal').modal('show');
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveNewsModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.news = {titolo: null, testo: null, dataPubblicazione: null, pubblica: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
