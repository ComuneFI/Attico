'use strict';

angular.module('cifra2gestattiApp')
.factory('Ldap', function ($q, $http) {
	return {
		getUsers: function() {
    		return $http.get('api/ldap/users').then(function (response) {
                return response.data;
            });
    	}
	}
})

