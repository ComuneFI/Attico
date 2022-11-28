'use strict';

angular.module('cifra2gestattiApp')
    .controller('CategoriaMsgDetailController', ['$scope','CategoriaMsg', '$stateParams',
        function ($scope, CategoriaMsg, $stateParams) {
    	$scope.categoriaMsg = {};
        $scope.load = function (id) {
        	CategoriaMsg.get({id: id}, function(result) {
              $scope.categoriaMsg = result;
            });
        };
        $scope.load($stateParams.id);
    }]);
