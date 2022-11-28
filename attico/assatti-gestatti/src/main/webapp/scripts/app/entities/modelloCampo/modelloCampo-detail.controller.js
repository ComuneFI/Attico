'use strict';

angular.module('cifra2gestattiApp')
    .controller('ModelloCampoDetailController', function ($scope, $stateParams, ModelloCampo, Profilo) {
        $scope.modelloCampo = {};
        $scope.load = function (id) {
        	if(angular.isDefined($stateParams.flag)){
        		ModelloCampo.getGlobal({id: id}, function(result) {
                    $scope.modelloCampo = result;
                  });
        	}
        	else{
        		ModelloCampo.get({id: id}, function(result) {
                    $scope.modelloCampo = result;
                  });
        	}
            
        };
        $scope.load($stateParams.id);
        $scope.$on('$viewContentLoaded', function(){
        	$(".note-editable").attr("contenteditable", false);
          });
    });
