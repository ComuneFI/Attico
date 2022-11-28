'use strict';

angular.module('cifra2gestattiApp')
    .factory('Lettera', function ($resource) {
        return $resource('api/letteras/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.dataPubblicazioneSito != null){
                        var dataPubblicazioneSitoFrom = data.dataPubblicazioneSito.split("-");
                        data.dataPubblicazioneSito = new Date(new Date(dataPubblicazioneSitoFrom[0], dataPubblicazioneSitoFrom[1] - 1, dataPubblicazioneSitoFrom[2]));
                    }
                    if (data.data != null){
                        var dataFrom = data.data.split("-");
                        data.data = new Date(new Date(dataFrom[0], dataFrom[1] - 1, dataFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
