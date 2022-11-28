'use strict';

angular.module('cifra2gestattiApp')
    .factory('Resoconto', function ($resource) {
        return $resource('api/resocontos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.dataPubblicazioneSito != null){
                        var dataPubblicazioneSitoFrom = data.dataPubblicazioneSito.split("-");
                        data.dataPubblicazioneSito = new Date(new Date(dataPubblicazioneSitoFrom[0], dataPubblicazioneSitoFrom[1] - 1, dataPubblicazioneSitoFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
