'use strict';

angular.module('cifra2gestattiApp')
    .factory('DiogeneReport', function ($resource) {
        return $resource('api/diogeneReports/', {}, {
            'search': { method: 'POST', isArray: true},
        });
    });
