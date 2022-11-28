'use strict';

angular.module('cifra2gestattiApp')
    .controller('LogoutController', function (Auth) {
    	localStorageService.clearAll();
        Auth.logout("logoutcontroller");
    });
