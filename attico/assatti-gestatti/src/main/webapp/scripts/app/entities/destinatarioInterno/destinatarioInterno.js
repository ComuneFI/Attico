'use strict';

angular.module('cifra2gestattiApp').
  directive('destinatarioInternoForm', function() {
        return {
         restrict: 'E',
         replace: true,
         transclude: true,
        scope: {
            ngModel : '=',
            ngDisabled : '=',
        },
         templateUrl: './scripts/app/entities/destinatarioInterno/destinatario-interno-form.html',
         controller: function ($scope, $attrs, TipoDestinatario) {
        	 TipoDestinatario.query(function(result) {
                 $scope.tipiDestinatari = result;
             }, function(error){
                 console.log(error);
             });
        	 $scope.addDestinatario = function(destinatario){
        		$scope.ngModel.destinatariInterni.push(destinatario); 
        	 };
         }       
    };
  });
