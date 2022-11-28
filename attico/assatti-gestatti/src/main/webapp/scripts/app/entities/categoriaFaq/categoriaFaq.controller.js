'use strict';

angular.module('cifra2gestattiApp')
    .controller('CategoriaFaqController', function ($scope, CategoriaFaq, ParseLinks) {
        $scope.categoriaFaqs = [];
        $scope.tempSearch = {};
        $scope.categoriaFaqSearch = {};
        $scope.page = 1;
        $scope.loadAll = function(searchObject) {
        	if(searchObject != undefined && searchObject != null){
        		CategoriaFaq.query({
        			page: $scope.page,
        			per_page: 20,
        			idRicerca: searchObject.id,
        			descrizione: searchObject.descrizione
        			}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.categoriaFaqs = result;
	            });
        	} else {
        		CategoriaFaq.query({page: $scope.page, per_page: 20}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.categoriaFaqs = result;
	            });
        	}
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.categoriaFaqSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in categoriaFaqSearch*/
        	$scope.categoriaFaqSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.showUpdate = function (id) {
            CategoriaFaq.get({id: id}, function(result) {
                $scope.categoriaFaq = result;
                $('#saveCategoriaFaqModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.categoriaFaq.id != null) {
                CategoriaFaq.update($scope.categoriaFaq,
                    function () {
                        $scope.refresh();
                    });
            } else {
                CategoriaFaq.save($scope.categoriaFaq,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            CategoriaFaq.get({id: id}, function(result) {
                $scope.categoriaFaq = result;
                $('#deleteCategoriaFaqConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            CategoriaFaq.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCategoriaFaqConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveCategoriaFaqModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.categoriaFaq = {descrizione: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
