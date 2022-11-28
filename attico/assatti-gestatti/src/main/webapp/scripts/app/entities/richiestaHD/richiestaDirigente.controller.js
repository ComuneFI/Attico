'use strict';

angular.module('cifra2gestattiApp')
    .controller('RichiestaDirigenteController', ['$scope', '$rootScope','RichiestaHD', 'StatoRichiestaHD', 'TipoRichiestaHD','ParseLinks', '$state','Utente','Principal',
       '$controller','Upload',
        function ($scope, $rootScope, RichiestaHD, StatoRichiestaHD, TipoRichiestaHD, ParseLinks, $state, Utente, Principal, $controller, Upload) {
    	// instantiate base controller
        $controller('RichiestaHDController', { $scope: $scope });
        
        $scope.goToDetail = function(id){
        	if($scope.isAdmin){
	        	RichiestaHD.presaVisione({id:id}, function(res){
	        		$state.go("richiestaDirigenteDetail", {id:id});
	        	});
        	}else{
        		$state.go("richiestaDirigenteDetail", {id:id});
        	}
        };

        $scope.load = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		if($scope.isAdmin){
	        		RichiestaHD.searchRichiesteDirigente({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.richiestaHDs = result;
		            });
        		}else{
        			RichiestaHD.searchDirigente({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.richiestaHDs = result;
		            });
        		}
        	}else{
        		if($scope.isAdmin){
	        		RichiestaHD.searchRichiesteDirigente({page: $scope.page, per_page: 10}, $scope.richiestaHDSearch, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.richiestaHDs = result;
		            });
        		}else{
        			RichiestaHD.searchDirigente({page: $scope.page, per_page: 10}, $scope.richiestaHDSearch, function(result, headers) {
		                $scope.links = ParseLinks.parse(headers('link'));
		                $scope.richiestaHDs = result;
		            });
        		}
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in richiestaHDSearch*/
        	$scope.richiestaHDSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.load($scope.tempSearch);
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.load();
        };
        
        $scope.save = function () {
        	Upload.upload({
                url: 'api/richiestaHDs/uploadmodulo',
                headers : {
                  'Authorization': 'Bearer '+ $rootScope.accessToken
                },
                file: $scope.richiesta.modulo
            }).success(function (data, status, headers, config) {
              console.log("upload ok, fileid: ", data.fileid);
              /*Effettuato l'upload del file si inviano gli altri dati*/
              $scope.richiesta.allegatoId = data.fileid;
              if($scope.richiesta.allegatoId){
	              RichiestaHD.save($scope.richiesta, function () {
	          		$("#saveRichiestaHDModal").modal("hide");
	          		$scope.load();
	              });
              }else{
            	  alert("Errore di connessione. Riprovare.");
              }
            }).error(function(){
            	alert("Errore di connessione. Riprovare.");
            });
        };
        
        $scope.setTempfile = function(){
        	if($("#file").val() != ""){
        		$scope.tempfilename = $("#file").val();
        	}else{
        		$scope.tempfilename = undefined;
        	}
        };
        
        $scope.init = function(){
        	$scope.isDirigente = true;
        	$scope.isAdmin = null;
            var admin = [];
    	  	admin.push("ROLE_AMMINISTRATORE_RP");
    	  	if(Principal.isInAnyRole(admin)){
    	  		$scope.isAdmin = true;
    	  	}else{
    	  		$scope.isAdmin = false;
    	  	}

    	  	if($scope.isAdmin){
    	        Utente.getAllDirigenti(function(result){
    	        	$scope.utenti = result;
    	        });
    	  	}else{
    	  		$scope.richiesta = {};
    	  		TipoRichiestaHD.dirigenti( function(result){
                    $scope.tiporichiestahds = result;
                    $scope.richiesta.tipo = $scope.tiporichiestahds[0];
                });
    	  	}
    	  	
    	  	$scope.load();
        };
    }])
    .directive("richiestafileread", [function () {
            return {
            	restrict: 'A',
                link: function (scope, element, attributes) {
                    element.bind("change", function (changeEvent) {
                      scope.$apply( function() {
                    	  scope.richiesta.modulo = changeEvent.target.files[0];
                      });
                    });
                }
            }
     }]);
