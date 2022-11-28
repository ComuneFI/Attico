'use strict';

angular.module('cifra2gestattiApp')
    .factory('DateServer', function ($resource) {
        return $resource('api/dateServer/:action', {}, {
            'getCurrent': { method: 'GET', isArray: false, params:{action:'currentdate'}},
            'calcolaDataFinePubblicazione':{method:'POST', isArray:false, params:{action:'calcolaDataFinePubblicazione'}}
        });
    });
