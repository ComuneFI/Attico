'use strict';

angular.module('cifra2gestattiApp')
    .controller('TraduciController', function ($scope, $rootScope ,$http) {

    	
        $scope.isObject = function(value){
            return angular.isObject(value)    
        };
        
         $scope.isArray = function(value){
            return angular.isArray(value)    
        };
        

        $http.get('/en.json').success(function(data, status, headers, config) {
           $scope.en = data; 
        });
    	
        
    });