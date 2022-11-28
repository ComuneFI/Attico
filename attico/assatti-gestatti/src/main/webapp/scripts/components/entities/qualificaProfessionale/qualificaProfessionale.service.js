'use strict';

angular.module('cifra2gestattiApp')
    .factory('QualificaProfessionale', function ($resource) {
        return $resource('api/qualificaProfessionales/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            'getOnlyEnabled': { method: 'GET', isArray: true, params:{ action: 'getOnlyEnabled'}},
            'getByProfiloId': { method: 'GET', isArray: true, params:{ action: 'getByProfiloId', profiloId:'@profiloId'}},
            'getEnabledByProfiloId': { method: 'GET', isArray: true, params:{ action: 'getEnabledByProfiloId', profiloId:'@profiloId'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
