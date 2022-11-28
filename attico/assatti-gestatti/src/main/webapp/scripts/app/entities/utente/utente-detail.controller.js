'use strict';

angular.module('cifra2gestattiApp')
    .controller('UtenteDetailController', function ($scope, $stateParams, Utente, Indirizzo, $rootScope, Upload) {
        $scope.utente = {};
        $scope.showSuccessSave = false;
        $scope.load = function (id) {
            Utente.get({id: id}, function(result) {
              $scope.utente = result;
            });
        };
        $scope.load($stateParams.id);
        $scope.downloadModuloreRegistrazione = function(){
        	window.open(
        			$rootScope.buildDownloadUrl("api/utentes/" + $scope.utente.id + "/moduloregistrazione") + "&access_token=" + $scope.access_token,
        			  '_blank'
        			);
        };
        
        $scope.save = function () {
	        Utente.update($scope.utente,
	            function () {
	        		$scope.showSuccessSave = true;
	        		setTimeout(function() {
	        			$scope.showSuccessSave = false;
	        		}, 3000);
	        });
        };
        

		
		$scope.setTempfile = function(changeEvent){
        	if($("#file").val() != ""){
        		$scope.tempfilename = $("#file").val();
        		$scope.moduloregistrazioneNew = changeEvent.target.files[0];
        	}else{
        		$scope.tempfilename = undefined;
        		$scope.moduloregistrazioneNew = undefined;
        	}
        };
        
        $scope.updateFile = function () {
        	        	
            if($scope.moduloregistrazioneNew.size > $rootScope.ngfMaxSize){
            	var rejectedFiles = [$scope.moduloregistrazioneNew];
            	$rootScope.$broadcast('rejectedFilesEvent', [], {}, rejectedFiles);
        	    return;
            }
            
            Upload.upload({
                url: 'api/utentes/' + $scope.utente.id + '/updatemoduloregistrazione',
                headers : {
                  'Authorization': 'Bearer '+ $rootScope.accessToken
                },
                file: $scope.moduloregistrazioneNew
            }).success(function (data, status, headers, config) {
              console.log("upload ok, fileid: ", data.fileid);
            });
        }
    });
