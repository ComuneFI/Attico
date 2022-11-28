'use strict';

angular.module('cifra2gestattiApp')
    .controller('FileDetailController', function ($scope, $stateParams, File) {
        $scope.file = {};
        $scope.load = function (id) {
            File.get({id: id}, function(result) {
              $scope.file = result;
            });
        };
        $scope.load($stateParams.id);
    });
