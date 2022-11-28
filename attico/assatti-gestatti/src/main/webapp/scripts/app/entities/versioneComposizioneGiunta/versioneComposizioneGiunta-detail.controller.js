'use strict';

angular.module('cifra2gestattiApp')
    .controller('VersioneComposizioneGiuntaDetailController', function ($scope, $stateParams, VersioneComposizioneGiunta) {
        $scope.versioneComposizioneGiunta = {};
        $scope.load = function (id) {
        	VersioneComposizioneGiunta.get({id: id}, function(result) {
              $scope.versioneComposizioneGiunta = result;
            });
        };
        $scope.load($stateParams.id);
    });
