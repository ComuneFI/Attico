'use strict';

angular.module('cifra2gestattiApp')
    .controller('VersioneComposizioneConsiglioDetailController', function ($scope, $stateParams, VersioneComposizioneConsiglio) {
        $scope.versioneComposizioneConsiglio = {};
        $scope.load = function (id) {
        	VersioneComposizioneConsiglio.get({id: id}, function(result) {
              $scope.versioneComposizioneConsiglio = result;
            });
        };
        $scope.load($stateParams.id);
    });
