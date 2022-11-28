'use strict';

angular.module('cifra2gestattiApp')
    .factory('Macro_cat_obbligo_dl33', function ($resource) {
        return $resource('api/macro_cat_obbligo_dl33s/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'attivo': { method: 'GET', isArray: true,params:{'action':'attivo'}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'checkAlert':{method:'GET',params:{'action':'checkAlert'}},
            'checkIfAlreadyexist':{method:'GET', isArray:false, params:{action:'alreadyexist', id:'@id', codice:'@codice'}},
            'abilita':{method:'PUT',params:{'action':'abilita', 'id':'@id'}},
            'disabilita':{method:'PUT',params:{'action':'disabilita', 'id':'@id'}}
        });
    });


angular.module('cifra2gestattiApp')
    .factory('AnagraficaObbligoDlService', function ($resource) {
        return { 
              listToMap: function (result) {
                 var macros = {} ; 

                 for(var i = 0; i <  result.length; i++ ){
                  var macro = result[i];

                  var categorie = {} ; 
                  for(var ii = 0; ii <  macro.categorie.length; ii++ ){
                    var categoria = macro.categorie[ii];
                    var obblighi = {} ; 
                    for(var iii = 0; iii <  categoria.obblighi.length; iii++ ){
                      var obbligo = categoria.obblighi[iii];
                      var schede = {} ; 
                      for(var iiii = 0; iiii <  obbligo.schedas.length; iiii++ ){
                        var scheda = obbligo.schedas[iiii];
                        schede[scheda.id] = scheda;
                      }
                      obbligo.schede = schede;
                      obblighi[obbligo.id] = obbligo;
                    }
                    categoria.obblighi = obblighi;
                    categorie[categoria.id] = categoria;
                  }

                  macro.categorie = categorie;
                  macros[macro.id] = macro ;

              }
            return macros;
          }

        }

    });
