'use strict';

angular.module('cifra2gestattiApp')
    .factory('News', function ($resource) {
        return $resource('api/newss/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'pubblica': { method: 'GET', isArray: true, params:{action:'pubblica'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'newsReportLastState': { method: 'POST', isArray: true, params:{id:'newsReport', action:'lastState'}},
            'loadStoricoTentativi':{method:'GET', isArray:true, params:{action:'loadStoricoTentativi'}},
            'retryInvio':{method:'GET', isArray:false, params:{action:'retryInvio'}},
            'annullaTentativo':{method:'GET', isArray:false, params:{action:'annullaTentativo'}}
        });
    });
