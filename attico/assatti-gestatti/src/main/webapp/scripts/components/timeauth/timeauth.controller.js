'use strict';

angular.module('cifra2gestattiApp')
    .controller('TimeAuthController', function ($scope, $rootScope, $location, $state, Auth, Principal, TimeAuth, $log) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        $log.debug($scope.$state);



    	

    });