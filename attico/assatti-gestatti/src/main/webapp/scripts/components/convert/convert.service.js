'use strict';

angular.module('cifra2gestattiApp')
    .factory('ConvertService', function ($resource, URL_CONVERT, $rootScope) {
        return {'extract': $rootScope.URL_CONVERT + 'api/extract',
			'extractV2': $rootScope.URL_CONVERT + 'api/extractV2'  };
    });
