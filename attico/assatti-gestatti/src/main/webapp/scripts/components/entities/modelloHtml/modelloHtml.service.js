'use strict';

angular.module('cifra2gestattiApp')
    .factory('ModelloHtml', function ($resource) {
        return $resource('api/modelloHtmls/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'attotest': { method: 'GET', isArray: false,params:{'action':'attotest'}},
            'populate': { method: 'GET', isArray: false,params:{'action':'populate'}},
			'popDIR': { method: 'GET', isArray: false,params:{'action':'populate'}},
			'popDEL': { method: 'GET', isArray: false,params:{'action':'popDEL'}},
			'popOdgGiunta': { method: 'GET', isArray: false,params:{'action':'popOdgGiunta'}},
			'popOdgConsiglio': { method: 'GET', isArray: false,params:{'action':'popOdgConsiglio'}},
			'popSDL': { method: 'GET', isArray: false,params:{'action':'popSDL'}},
			'popCOM': { method: 'GET', isArray: false,params:{'action':'popCOM'}},
			'popDDL': { method: 'GET', isArray: false,params:{'action':'popDDL'}},
			'popRFT': { method: 'GET', isArray: false,params:{'action':'popRFT'}},
			'popORD': { method: 'GET', isArray: false,params:{'action':'popORD'}},
			'popDPR': { method: 'GET', isArray: false,params:{'action':'popDPR'}},
			'popParere': { method: 'GET', isArray: false,params:{'action':'popParere'}},
			'popLettera': { method: 'GET', isArray: false,params:{'action':'popLettera'}},
			'popVerbale': { method: 'GET', isArray: false,params:{'action':'popVerbale'}},
			'popResoconto': { method: 'GET', isArray: false,params:{'action':'popResoconto'}},
			'popSchedaAnagraficoContabile': { method: 'GET', isArray: false,params:{'action':'popSchedaAnagraficoContabile'}},
			'popReportRicerca': { method: 'GET', isArray: false,params:{'action':'popReportRicerca'}},
			'popAttoInesistente': { method: 'GET', isArray: false,params:{'action':'popAttoInesistente'}},
			'popRestituzioneSuIstanzaUfficioProponente': { method: 'GET', isArray: false,params:{'action':'popRestituzioneSuIstanzaUfficioProponente'}},
			'popRelataPubblicazione': { method: 'GET', isArray: false,params:{'action':'popRelataPubblicazione'}},
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
