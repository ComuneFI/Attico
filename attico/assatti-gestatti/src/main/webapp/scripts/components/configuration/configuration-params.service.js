'use strict';

angular.module('cifra2gestattiApp')
    .factory('ConfigurationParams', function ($q, $http) {
    	return {
    		get: function() {
        		return $http.get('api/configurations').then(function (response) {
                    return response.data;
                });
        	}
    	}
    })





