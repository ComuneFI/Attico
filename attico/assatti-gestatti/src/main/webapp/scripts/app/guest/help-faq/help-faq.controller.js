'use strict';

angular.module('cifra2gestattiApp')
    .controller('Help-FaqController', function ($scope, $rootScope, Faq, FaqPublic, CategoriaFaqPublic, ParseLinks) {
        
    	$scope.faqs = [];
        $scope.categoriafaqs = CategoriaFaqPublic.query();
        $scope.categoriaID = null;
        
        //var indiceCategorie = [];
        $scope.indiceCategorie = [];

        $scope.faqsToFilter = function() {
        	$scope.indiceCategorie = [];
            return $scope.faqs;
        }
        
        $scope.filterCategories = function(faq) {
        	
        	var faqIsNew = $scope.indiceCategorie.indexOf(faq.categoria.id) == -1;
        	if (faqIsNew) {
        		$scope.indiceCategorie.push(faq.categoria.id);
            }
        	
            return faqIsNew;
        }

        $scope.page = 1;
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.loadAll = function() {

            if(!angular.isUndefined($rootScope.profiloattivo) && $rootScope.profiloattivo!==null){
                Faq.searchByProfiloId({categoriaID: $scope.categoriaID,
                                             profiloId: $rootScope.profiloattivo.id, 
                                             page: $scope.page, per_page: 20},function(result,headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.faqs = result;
                });
            } else {
                FaqPublic.query({categoriaID: $scope.categoriaID, page: $scope.page, per_page: 20},function(result,headers) {
                    $scope.links = ParseLinks.parse(headers('link'));
                    $scope.faqs = result;
                });
            }
        };

        $scope.loadOnChange = function(){
            $scope.indiceCategorie =[];
            $scope.loadAll();
        }

        $scope.loadAll();

        
    });
