'use strict';

angular.module('cifra2gestattiApp')
    .factory('JobTrasparenza', function ($resource) {
        return $resource('api/jobTrasparenzas/:id/:action', {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'search':{method:'POST', isArray:true, params:{action:'search'}}
        });
    });
