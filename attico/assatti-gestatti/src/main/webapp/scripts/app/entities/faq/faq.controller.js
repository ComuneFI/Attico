'use strict';

angular.module('cifra2gestattiApp')
    .controller('FaqController', function ($scope, $rootScope, Faq, CategoriaFaq, Aoo, ParseLinks, $log) {
    	$scope.faqs = [];
        $scope.categoriafaqs = CategoriaFaq.query();
        $scope.aoos = Aoo.getMinimal();
        $scope.faqSearch = {};
    	$scope.tempSearch = {};
        
        var indiceCategorie = [];
        
        $scope.faqsToFilter = function() {
        	indiceCategorie = [];
            return $scope.faqs;
        }
        
        $scope.filterCategories = function(faq) {
        	
        	var faqIsNew = indiceCategorie.indexOf(faq.categoria.id) == -1;
        	if (faqIsNew) {
        		indiceCategorie.push(faq.categoria.id);
            }
        	
            return faqIsNew;
        }

        $scope.page = 1;
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject != undefined && searchObject != null){
        		$log.log('searchObject', searchObject);
        		var aoo = "";
        		var categoria = "";
        		if(searchObject.aoo){
        			aoo = searchObject.aoo.id;
        		}
        		if(searchObject.categoria){
        			categoria = searchObject.categoria.id;
        		}
        		Faq.query({
        			page: $scope.page,
        			per_page: 20,
        			domanda: searchObject.domanda,
        			risposta: searchObject.risposta,
        			categoria: categoria,
        			aoo: aoo
        			}, function(result, headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.faqs = result;
                });
        	} else {
        		Faq.query({page: $scope.page, per_page: 20},function(result,headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.faqs = result;
                });
        	}
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.faqSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/* copio tempSearch e suoi elementi in faqSearch */
        	$scope.faqSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };

        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Faq.get({id: id}, function(result) {
                $scope.faq = result;
                $('#saveFaqModal').modal('show');
            });
        };

        $scope.save = function () {
        	
            if ($scope.faq.id != null) {
                Faq.update($scope.faq,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Faq.save($scope.faq,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Faq.get({id: id}, function(result) {
                $scope.faq = result;
                $('#deleteFaqConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Faq.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFaqConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveFaqModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.faq = {domanda: null, risposta: null, pubblica: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
