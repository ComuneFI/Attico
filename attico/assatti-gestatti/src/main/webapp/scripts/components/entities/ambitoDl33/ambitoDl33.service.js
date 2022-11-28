'use strict';

angular.module('cifra2gestattiApp')
    .factory('AmbitoDl33', function ($resource) {
        return $resource('api/ambitoDl33s/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'attivo': { method: 'GET', isArray: true,params:{'action':'attivo'}},
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


angular.module('cifra2gestattiApp')
    .factory('AmbitoDl33Service', function ($resource) {
        return { 
              listToMap: function (result) {
                 var ambiti = {} ; 

                 for(var i = 0; i <  result.length; i++ ){
                  var ambito = result[i];

                  var materie = {} ; 
                  for(var ii = 0; ii <  ambito.materie.length; ii++ ){
                    var materia = ambito.materie[ii];
                    materie[materia.id] = materia;
                  }

                  ambito.materie = materie;

                  ambiti[ambito.id] = ambito ;
              }
            return ambiti;
          }

        }

    });

