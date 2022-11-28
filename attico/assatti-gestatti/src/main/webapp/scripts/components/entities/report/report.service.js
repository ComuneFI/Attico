angular.module('cifra2gestattiApp')
    .factory('Preview', function ($resource) {
        return $resource('api/preview/:action/:taskBpmId', {}, {
        	'elencotipodocumento': { method: 'GET', isArray: true, params:{action:'elencotipodocumento', 'taskBpmId':'@taskBpmId'}},
        	'pcf': { method: 'GET', isArray: true, params:{action:'pcf', 'taskBpmId':'@taskBpmId'}}
        });
    });