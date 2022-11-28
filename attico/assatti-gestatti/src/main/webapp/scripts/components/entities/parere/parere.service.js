'use strict';

angular.module('cifra2gestattiApp')
    .factory('Parere', function ($resource) {
        return $resource('api/pareres/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.data != null){
                        var dataFrom = data.data.split("-");
                        data.data = new Date(new Date(dataFrom[0], dataFrom[1] - 1, dataFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'save': { method:'POST', isArray: false},
            'nonEspresso': { method: 'PUT', isArray: false, params:{action:'nonEspresso', id:'@id'}}
        });
    });
