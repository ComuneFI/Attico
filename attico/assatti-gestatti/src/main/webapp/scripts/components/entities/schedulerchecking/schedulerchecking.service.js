'use strict';

angular.module('cifra2gestattiApp')
    .factory('SchedulerChecking', function ($resource) {
        return $resource('api/schedulerChecking/:id/:action', {}, {
        	'saveUpdate' : { method: 'POST', isArray: false, params:{action:'saveUpdate'}},
        	'getAll' : { method: 'GET', isArray: true, params:{action:'getAll'}},
        	'getHostName':{ method: 'GET', isArray: false, params:{action:'getHostName'}},
        	'remove':{ method: 'DELETE', isArray: false, params:{action:'remove', id:'@id'}}
        });
    });
