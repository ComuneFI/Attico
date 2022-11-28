'use strict';

angular.module('cifra2gestattiApp')
    .controller('UfficioDetailController', function ($scope, $stateParams, Ufficio, Profilo, Indirizzo, Aoo) {
        $scope.ufficio = {};
        $scope.load = function (id) {
            Ufficio.get({id: id}, function(result) {
              $scope.ufficio = result;
            });
        };
        $scope.load($stateParams.id);
    });
