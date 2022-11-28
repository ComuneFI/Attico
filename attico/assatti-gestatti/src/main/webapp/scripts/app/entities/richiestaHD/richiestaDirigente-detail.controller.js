'use strict';

angular.module('cifra2gestattiApp')
    .controller('RichiestaDirigenteDetailController', ['$scope', '$rootScope','RichiestaHD', 'StatoRichiestaHD', '$stateParams', 'ngToast', 'Principal', '$controller','$state',
        function ($scope, $rootScope, RichiestaHD, StatoRichiestaHD, $stateParams, ngToast, Principal, $controller, $state) {
    	// instantiate base controller
        $controller('RichiestaHDDetailController', { $scope: $scope });
    	
        $scope.init=function(){
        	$scope.isAdmin = null;
            var admin = [];
    	  	admin.push("ROLE_AMMINISTRATORE_RP");
    	  	if(Principal.isInAnyRole(admin)){
    	  		$scope.isAdmin = true;
    	  	}else{
    	  		$scope.isAdmin = false;
    	  	}
    	  	$scope.load($stateParams.id);
        };
        
        $scope.goBack = function(){
        	$state.go("richiesteDirigente");
        };
        
    }]);
