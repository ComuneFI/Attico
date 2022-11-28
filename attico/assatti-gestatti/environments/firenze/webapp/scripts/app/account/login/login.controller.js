'use strict';

angular.module('cifra2gestattiApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth, Principal, AccountInfo, $window, $stateParams, localStorageService) {
    	
    	$scope.ssousername = $stateParams.ssousername;
    	$scope.ssotoken = $stateParams.ssotoken;
		$rootScope.loginTime = $rootScope.loginTime ? $rootScope.loginTime + 1 : 1;
    	
        $scope.user = {};
        $scope.errors = {};

        $scope.rememberMe = false;
		if($rootScope.loginTime < 2){
	        $timeout(function (){
	        	if($scope.ssousername) {
	        		$scope.username = $scope.ssousername;
	        		$scope.password = $scope.ssotoken;
	        		$scope.login();
	        	}
	        	
	        	angular.element('#username').focus();
	        });
	        
	        if(Principal.isAuthenticated())
	        	$state.go('home');
        }else{
			console.log('login_doubled ' + $rootScope.loginTime)
		}
        $scope.logoutLS = function(){
        	Auth.logoutLS().then(function(){
        		$scope.dismissModal();
        	});
        };
        
        $scope.login = function () {
        	if(localStorageService.get("token") || localStorageService.get("cifra2account")){
        		localStorageService.clearAll();
        	}
        	AccountInfo.isLogged({
                username: $scope.username,
                password: $scope.password,
                logout: false
            },function(response) {
            	if(response.data == true) {
            		$('#discardConcurrentSessionFormConfirmation').modal('show');
            	} else {
            		$scope.doLogin();            		
            	}
            },function(){
            	$rootScope.menuVerticaleShow = false;
                $scope.authenticationError = true;
            });
        };
        
        $scope.changeuser = function () {
        	$window.location.href = $rootScope.configurationParams.changeuser_url + $scope.ssotoken
        };
        
        $scope.doLogin = function() {
        	Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $scope.authenticationError = false;
                $rootScope.menuVerticaleShow = true;
                if ($rootScope.previousStateName === 'register') {
                    $state.go('home');
                } else {
                    $rootScope.back();
                    document.cookie = "myloggin=true";
                }
            }).catch(function () {
                $rootScope.menuVerticaleShow = false;
                $scope.authenticationError = true;
            });
        }
        
        $scope.discardConcurrentSessionConfirmation = function() {
    		$('#discardConcurrentSessionFormConfirmation').modal('hide');
    		AccountInfo.isLogged({
                username: $scope.username,
                password: $scope.password,
                logout: true
            }, function(){
            	$('#concurrentSessionDeleted').modal('show');
            });
    	};
    	
    	$scope.dismissModal = function() {
    		$('#discardConcurrentSessionFormConfirmation').modal('hide');
    		$('#loginGiaEffettuato').modal('hide');
    	};
    	
    	$scope.goHome = function() {
    		$state.go('home');
    	};
    });
