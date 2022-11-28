'use strict';

angular.module('cifra2gestattiApp')
    .factory('Lavorazione', function ($resource) {
        return $resource('api/lavoraziones/:id/:action', {}, {
            'listadecisioni': { method: 'GET', isArray: true, params:{'action':'listadecisioni'}},
            'assegnazioniincarichi': { method: 'GET', isArray: true, params:{'action':'assegnazioniincarichi'}},
            'cambioCoda': { method: 'GET', isArray: false, params:{'action':'cambioCoda', 'idAtto':'@idAtto','taskBpmId':'@taskBpmId','idAooProfilo':'@idAooProfilo'}},
            'assegnazioniincarichidettaglio': { method: 'GET', isArray: false, params:{'action':'assegnazioniincarichidettaglio', 'proponenteAooId':'@proponenteAooId', 'profiloAooId':'@profiloAooId', 'idAtto':'@idAtto'}},
            'assegnazioneincaricodettaglio': { method: 'GET', isArray: false, params:{'action':'assegnazioneincaricodettaglio', 'proponenteAooId':'@proponenteAooId', 'profiloAooId':'@profiloAooId', 'idAtto':'@idAtto'}},
            'getincarichi': { method: 'GET', isArray: false, params:{'action':'getincarichi', 'idAtto':'@idAtto'}},
            'scenariDisabilitazione': { method: 'GET', isArray: true, params:{'action':'scenaridisabilitazione'}},
            'assegnati': { method: 'GET', isArray: false, params:{'action':'tasksassegnati'}},
            'tasksprendicarico': { method: 'PUT', isArray: false, params:{'action':'tasksprendicarico', 'taskBpmId':'@taskBpmId', 'attoId':'@attoId'}},
            'dettaglioTask' : { method: 'GET', isArray: false, params:{'action':'dettaglioTask'}},
            'arrivo': { method: 'GET', isArray: false, params:{'action':'tasksarrivo'}},
            'confTaskAoos': { method: 'GET', isArray: true, params:{'action':'confTaskAoos'}}
        });
    });
