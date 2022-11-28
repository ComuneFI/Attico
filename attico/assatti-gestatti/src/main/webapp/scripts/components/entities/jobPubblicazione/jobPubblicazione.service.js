'use strict';

angular.module('cifra2gestattiApp')
    .factory('JobPubblicazione', function ($resource) {
        return $resource('api/jobPubblicaziones/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'search':{method:'POST', isArray:true, params:{action:'search'}},
            'updateDatiAnnullamento': { method:'PUT' }
        });
    });
