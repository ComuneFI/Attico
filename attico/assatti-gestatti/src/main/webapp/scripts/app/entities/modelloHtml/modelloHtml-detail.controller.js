'use strict';

angular.module('cifra2gestattiApp')
    .controller('ModelloHtmlDetailController', function ($scope, $stateParams, ModelloHtml, TipoAtto) {
        $scope.modelloHtml = {};
        $scope.load = function (id) {
            ModelloHtml.get({id: id}, function(result) {
              $scope.modelloHtml = result;
            });
        };
        $scope.load($stateParams.id);
    });
