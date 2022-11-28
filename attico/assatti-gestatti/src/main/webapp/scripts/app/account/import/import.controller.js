'use strict';

angular.module('cifra2gestattiApp')
    .controller('ImportController', function ($scope, $translate, $timeout, Auth, $http, Upload, $rootScope, Principal, Ldap, $log) {
    	Principal.identity().then(function(account) {
            $scope.isAuthenticated = Principal.isAuthenticated();
        });
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.importAccount = {};
        $scope.ldapSearch = [];
        $scope.tempfilename = undefined;
        $timeout(function (){angular.element('[ng-model="importAccount.login"]').focus();});
        
        

        $scope.setTempfile = function(){
        	if($("#file").val() != ""){
        		$scope.tempfilename = $("#file").val();
        	}else{
        		$scope.tempfilename = undefined;
        	}
        };
        
        $scope.ldapSearch = function(){
        	$scope.ldapSearch = [];
        	Ldap.getUsers().then(function (data) {
            	$scope.ldapUsers = data;
            });
        }
        
        $scope.import = function () {
            if ($scope.importAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.importAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.error = null;
                $scope.errorUserExists = null;
                $scope.errorEmailExists = null;
                $scope.errorCfExists = null;
                
//                $log.debug('Uploading file', $scope.registerAccount.moduloregistrazione);
//                if($scope.registerAccount.moduloregistrazione.size > $rootScope.ngfMaxSize){
//                	var rejectedFiles = [$scope.registerAccount.moduloregistrazione];
//                	$rootScope.$broadcast('rejectedFilesEvent', [], {}, rejectedFiles);
//            	    return;
//                }
                
//                Upload.upload({
//                    url: 'api/uploadmoduloregistrazione',
//                    headers : {
//                      'Authorization': 'Bearer '+ $rootScope.accessToken
//                    },
//                    file: $scope.registerAccount.moduloregistrazione
//                }).success(function (data, status, headers, config) {
//                  console.log("upload ok, fileid: ", data.fileid);
//                  /*Effettuato l'upload del file si inviano gli altri dati*/
//                  $scope.registerAccount.fileid = data.fileid;
//                  Auth.createAccount($scope.registerAccount).then(function () {
//                	  var admin = [];
//                	  admin.push("ROLE_AMMINISTRATORE_RP");
//                	  admin.push("ROLE_ADMIN");
//                	  if(Principal.isInAnyRole(admin)){
//                		  $scope.successAdmin = 'OK';
//                	  }else{
//                		  $scope.success = 'OK';
//                	  }
//                  }).catch(function (response) {
//                      $scope.success = null;
//                      if (response.status === 400 && response.data === 'login already in use') {
//                          $scope.errorUserExists = 'ERROR';
//                      } else if (response.status === 400 && response.data === 'e-mail address already in use') {
//                          $scope.errorEmailExists = 'ERROR';
//                      }else if (response.status === 400 && response.data === 'codice fiscale already in use') {
//                    	  $scope.errorCfExists = 'ERROR';
//                      } else {
//                          $scope.error = 'ERROR';
//                      }
//                  });
//                });
            }
        };
    });
//    .directive("fileread", [function () {
//            return {
//            	restrict: 'A',
//                link: function (scope, element, attributes) {
//                    element.bind("change", function (changeEvent) {
//                      scope.$apply( function() {
//                    	  scope.registerAccount.moduloregistrazione = changeEvent.target.files[0];
//                      });
//                    });
//                }
//            }
//     }]);
