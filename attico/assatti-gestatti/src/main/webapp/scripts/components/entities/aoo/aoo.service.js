'use strict';

angular.module('cifra2gestattiApp')
    .factory('Aoo', function ($resource) {
        return $resource('api/aoos/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'queryRicorsiva': { method: 'GET', isArray: true,params:{'action':'queryRicorsiva', id:'@id'}},
            'organigramma': { method: 'GET', isArray: true,params:{'action':'organigramma'}},
            'padri': { method: 'GET', isArray: true, params:{'action':'padri'}},
            'nodipadre': { method: 'GET', isArray: true, params:{'action':'nodipadre'}},
            'sezionibyaoo': { method: 'GET', isArray: true, params:{'action':'sezioni', id:'@id'}},
            'assessoratibyaoo': { method: 'GET', isArray: true, params:{'action':'assessorati', id:'@id'}},
            'abilita': { method:'PUT', isArray: false, params:{'action':'abilita', id:'@id'} },
            'disabilita': { method:'PUT', isArray: false, params:{action:'disabilita', id:'@id'}},
            'codiceExists':{method:'GET', isArray:false, params:{action:'codiceExists'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'updateParent': { method:'PUT', params:{'action':'parentUpd', id:'@id'} },
            'annullaParent': { method:'PUT', isArray: false, params:{action:'parentAnnulla', id:'@id'}},
            'getAllEnabled':{method:'GET', isArray:true, params:{action:'getAllEnabled'}},
            'getMinimal':{method:'GET', isArray:true, params:{action:'minimal'}},
            'getDirezioneOfAoo':{method:'GET', isArray:false, params:{action:'getDirezioneOfAoo'}},
            'getDirezioni':{method:'GET', isArray:true, params:{action:'getDirezioni'}},
            'getAllDirezioni':{method:'GET', isArray:true, params:{action:'getAllDirezioni'}},
        });
    });
