'use strict';

angular.module('cifra2gestattiApp')
    .factory('Cat_obbligo_DL33', function ($resource) {
        return $resource('api/cat_obbligo_DL33s/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'checkIfAlreadyexist':{method:'GET', isArray:false, params:{action:'alreadyexist', id:'@id', cat_obbligo_DL33id:'@cat_obbligo_DL33id', codice:'@codice'}},
            'checkIfAlreadyexist':{method:'GET', isArray:false, params:{action:'alreadyexist', id:'@id', macro_cat_obbligo_DL33id:'@macro_cat_obbligo_DL33id', codice:'@codice'}},
            'checkAlert':{method:'GET',params:{'action':'checkAlert'}},
            'abilita':{method:'PUT',params:{'action':'abilita', 'id':'@id'}},
            'disabilita':{method:'PUT',params:{'action':'disabilita', 'id':'@id'}}
        });
    });
