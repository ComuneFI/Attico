'use strict';

angular.module('cifra2gestattiApp')
    .controller('NotificheController', function ($scope, $rootScope, $location, $state, Principal) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
       
    });