'use strict';

angular.module('cifra2gestattiApp')
    .controller('CategoriaFaqDetailController', function ($scope, $stateParams, CategoriaFaq) {
        $scope.categoriaFaq = {};
        $scope.load = function (id) {
            CategoriaFaq.get({id: id}, function(result) {
              $scope.categoriaFaq = result;
            });
        };
        $scope.load($stateParams.id);
    });
