'use strict';

angular.module('cifra2gestattiApp')
    .controller('MsgDetailController', ['$scope','Msg', '$stateParams',
        function ($scope, Msg, $stateParams) {
    	$scope.msg = {};
        $scope.load = function (id) {
        	Msg.get({id: id}, function(result) {
              $scope.msg = result;
            });
        };
        $scope.load($stateParams.id);
    }]);
