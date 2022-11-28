'use strict';

angular.module('cifra2gestattiApp')
    .controller('NavbarController', function ($scope, $rootScope, $location, $state, ngToast, Auth, 
    		Principal, ParseLinks,localStorageService) {
    	
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        $scope.newNewss = 0;
        $scope.destinataris = [];
        
        $scope.logout = function () {
        	localStorageService.clearAll();
            Auth.logout("navbarlogout");
            $state.go('home');
        };
        
        $scope.titleTipiAtto = function (profilo) {
			var title1= "";
			var title2= "Nessun Tipo Documento";
            if (profilo != null && profilo.tipiAtto != null && profilo.tipiAtto.length > 0) {
               for(var i = 0; i<profilo.tipiAtto.length; i++){
               	title1 += profilo.tipiAtto[i].codice;
               	title1 += " ";
    			}
    			return title1;
            }
            else {
                return title2;
            }
        }
    });
