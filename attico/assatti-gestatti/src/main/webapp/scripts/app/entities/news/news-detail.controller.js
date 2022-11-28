'use strict';

angular.module('cifra2gestattiApp')
    .controller('NewsDetailController', function ($scope, $rootScope, $stateParams, News, Profilo) {

        $scope.back = {};
        	
        News.get({id: $stateParams.id}, function(result) {
        	$scope.news = result;
        });
        
        $scope.back = $rootScope.previousStateName;
        
        
    });
