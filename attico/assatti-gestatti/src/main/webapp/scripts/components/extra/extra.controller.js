'use strict';

angular.module('cifra2gestattiApp')
    .controller('ExtraController', function ($scope, $location, $state, Auth, Principal) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;

        // $scope.logout = function () {
        //     Auth.logout();
        //     $state.go('home');
        // };
    });