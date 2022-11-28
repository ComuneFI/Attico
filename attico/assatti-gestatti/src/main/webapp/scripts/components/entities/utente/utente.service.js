'use strict';

angular.module('cifra2gestattiApp')
    .factory('Utente', function ($resource) {
        return $resource('api/utentes/:id/:action/:codicefiscale/:profiloId', {}, {
            'query': { method: 'GET', isArray: true},
            'hasProfili': { method: 'GET', isArray: true, params:{action:'profilos'}},
            'getAllDirigenti':{ method: 'GET', isArray: true, params:{action:'getAllDirigenti'}},
            'lastProfile': { method: 'GET', isArray: false, params:{action:'lastprofile'}},
            'updateLastProfile': { method: 'PUT', isArray: false, params:{action:'lastprofile', profiloId:'@profiloId', id:'@id'}},
            'attiva': { method: 'POST', isArray: false, params:{action:'attiva'}},
            'activeAmministratoreRP': { method: 'POST', isArray: false, params:{action:'activeAmministratoreRP'}},
            'activeAmministratoreIP': { method: 'POST', isArray: false, params:{action:'activeAmministratoreIP'}},
            'downloadModuloreRegistrazione' : {method: 'GET', isArray:false, params:{action:'moduloregistrazione', id:'@id'}},
            'activeprofilos': { method: 'GET', isArray: true, params:{action:'activeprofilos'}},
            'getbyusername': { method: 'GET', isArray: false, params:{action:'getbyusername'}},
            'checkstato' : {method: 'GET', isArray:false, params:{action:'checkstato', codicefiscale: '@codicefiscale'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
            'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'getAllUserStates' : {method:'GET', isArray:true, params:{action:'statos'}},
            'getAllActive' : {method:'GET', isArray:true, params:{action:'getAllActive'}},
            'getAllBasic' : {method:'GET', isArray:true, params:{action:'getAllBasic'}},
            'utentiLoggati':{method:'GET', isArray:true, params:{action:'utentiLoggati'}},
            'getAoosDirigente':{method:'GET', isArray:true, params:{action:'getAoosDirigente', id:'@id'}}
        });
    });
