'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('indirizzo', {
                parent: 'entity',
                url: '/indirizzo',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.indirizzo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/indirizzo/indirizzos.html',
                        controller: 'IndirizzoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('indirizzo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('indirizzoDetail', {
                parent: 'entity',
                url: '/indirizzo/:id',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_AMMINISTRATORE_RP'],
                    pageTitle: 'cifra2gestattiApp.indirizzo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/indirizzo/indirizzo-detail.html',
                        controller: 'IndirizzoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('indirizzo');
                        return $translate.refresh();
                    }]
                }
            });
    });


angular.module('cifra2gestattiApp').
  directive('indirizzoForm', function() {
        return {
         restrict: 'E',
         replace: true,
          transclude: true,
        scope: {
            ngModel : '=',
        },
         templateUrl: './scripts/app/entities/indirizzo/indirizzo-form.html'
       
    };
  })
  .directive('indirizzoSelect', function(Indirizzo) {
      return {
          restrict: 'E',
          replace: true,
           transclude: true,
         scope: {
        	 span: "=",
             ngModel : '=',
         },
          templateUrl: './scripts/app/entities/indirizzo/indirizzo-select.html',
          controller: function ($scope, $attrs, Indirizzo, $rootScope) {
        	  $scope.indirizzi = [];
        	  $scope.includiRef = $rootScope.includiRef;
              Indirizzo.query({onlyEnabled:true},function(result, headers) {
            	  for(var i=0; i<result.length; i++){
            		  $scope.indirizzi.push(result[i]);
            	  }
                  
              }, function(error){
                  console.log(error);
              });
              
              var contieneNull = function(indirizzi){
            	  for(var i=0; i<indirizzi.length; i++){
            		  if(!indirizzi[i]){
            			  return true;
            		  }
            	  }
            	  return false;
              };
              
              $scope.$watch("ngModel", function(newValue, oldValue){
            	  if($scope.indirizzi){
            		  if(newValue && newValue.id && !contieneNull($scope.indirizzi)){
            			  $scope.indirizzi.push(null);
            		  }
            	  }
        		 });
              
              
          }  
         
     };
   });
