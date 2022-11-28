'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoMateria', function ($resource) {
        return $resource('api/tipoMaterias/:id/:action', {}, {
        	'disable' : { method: 'PUT', isArray: false, params:{action:'disable', id:'@id'}},
        	'enable' : { method: 'PUT', isArray: false, params:{action:'enable', id:'@id'}},
            'query': { method: 'GET', isArray: true},
            'aoovuota': { method: 'GET', isArray: true,params:{"action":"aoovuota"}},
            'gestione': { method: 'GET', isArray: true,params:{"action":"gestione"}},
            'active': { method: 'GET', isArray: true,params:{"action":"active"}},
            'activeByAoo': { method: 'GET', isArray: true,params:{"action":"activeByAoo"}},

            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'search':{method:'GET', isArray:true, params:{"action":"search"}}
        });
    });


angular.module('cifra2gestattiApp')
    .factory('TipoMateriaService', function ($resource) {
        return { 
              listToMap: function (result) {
                 var tipomaterias = {} ; 

                 for(var i = 0; i <  result.length; i++ ){
                  var tipo = result[i];

                  var materias = {} ; 
                  for(var ii = 0; ii <  tipo.materie.length; ii++ ){
                    var materia = tipo.materie[ii];
                    var sottoMaterie = {} ; 
                    for(var iii = 0; iii <  materia.sottoMaterie.length; iii++ ){
                      var sottoMateria = materia.sottoMaterie[iii];
                      sottoMaterie[sottoMateria.id] = sottoMateria;
                    }

                    materia.sottoMaterie = sottoMaterie;
                    materias[materia.id] = materia;
                  }

                  tipo.materie = materias;
                  tipomaterias[tipo.id] = tipo ;

              }
            return tipomaterias;
          }

        }

    });

