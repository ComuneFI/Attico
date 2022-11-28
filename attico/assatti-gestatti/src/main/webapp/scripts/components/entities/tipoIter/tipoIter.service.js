'use strict';

angular.module('cifra2gestattiApp')
    .factory('TipoIter', function ($resource) {
        return $resource('api/tipoIters/:id/:action', {}, {
            'query': { method: 'GET', isArray: true},
            'getByTipoAtto': { method: 'GET', isArray: true, params:{'action':'getByTipoAtto'}},
            'getByCodiceTipoAtto': { method: 'GET', isArray: true, params:{'action':'getByCodiceTipoAtto'}},
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
    .factory('TipoIterService', function ($resource) {
        return { 
              listToMap: function (result) {
                 var tipoIters = {} ; 

                 for(var i = 0; i <  result.length; i++ ){
                  var tipoIter = result[i];

                  var tipiAdempimenti = {} ; 
                  for(var ii = 0; ii <  tipoIter.tipiAdempimenti.length; ii++ ){
                    var tipoAdempimento = tipoIter.tipiAdempimenti[ii];
                    
                    tipiAdempimenti[tipoAdempimento.id] = tipoAdempimento;
                  }

                  tipoIter.tipiAdempimenti = tipiAdempimenti;
                  tipoIters[tipoIter.id] = tipoIter ;

              }
            return tipoIters;
          }

        }

    });

