'use strict';

angular.module('cifra2gestattiApp')
    .controller('NewsReportController', function ($scope, $rootScope, News, Profilo, ParseLinks) {

        $scope.lastNews = [];
        
        $scope.pageStorico = 1;
        
        $scope.page = 1;
        $scope.lastNewsearch = {};
        $scope.tempSearch = {};
        $scope.storicoTentativi = [];
        $scope.motivazioneMap = {};
        $scope.statoMap = {};
        $scope.statoTitles = {};
        
        $scope.statos = [
                         	 { target:"NEW-DANGER", viewInSelect:false, title:"La notifica è stata presa in carico e risulta in attesa di invio. Gli ultimi 3 tentativi di invio sono falliti.", description: "In attesa"  },
                             { target:"NEW", viewInSelect:true, title:"La notifica è stata presa in carico e risulta in attesa di invio.", description: "In attesa"  },
                             { target:"IN_PROGRESS" , viewInSelect:true, title:"L'invio della E-Mail o PEC risulta in corso.", description: "In fase di invio"  },
                             { target:"IN_PROGRESS-DANGER" , viewInSelect:false, title:"L'invio della E-Mail o PEC risulta in corso. Gli ultimi 3 tentativi di invio sono falliti.", description: "In fase di invio"  },
                             { target:"DONE" , viewInSelect:true, title:"La notifica risulta aver avuto esito positivo. La presenza di questo valore non significa necessariamente, nel caso di invio tramite PEC, che siano state recepite le ricevute di accettazione ed/o avvenuta consegna.", description: "Inviato"  },
                             { target:"RECEIVED" , viewInSelect:true, title:"E' stata recepita la ricevuta di accettazione.", description: "Ricevuta di accettazione"  },
                             { target:"DELIVERED" , viewInSelect:true, title:"E' stata recepita la ricevuta di consegna.", description: "Ricevuta di consegna"  },
                             { target:"SENDED_WITH_ERROR" , viewInSelect:true, title:"Ricevuto stato invio con errore. E' probabile che il mittente specificato non esista.", description: "Ricevuto stato invio con errore"  },
                             { target:"ERROR" , viewInSelect:true, title:"Il tentativo di invio si è concluso con un errore. In questi casi un nuovo invio viene tentato in automatico.", description: "Errore"  },
                             { target:"FAILED" , viewInSelect:true, title:"E' stato effettuato il numero massimo di tentativi di invio. L'amministratore potrà tentare un nuovo invio utilizzando l'apposito pulsante.", description: "Invio Fallito"  },
                             { target:"CANCELED", viewInSelect:true, title:"Il tentativo di invio è stato annullato dall\u0027amministratore. Non saranno tentati nuovi invii finchè l'amministratore non tenterà un nuovo invio utilizzando l'apposito pulsante.", description: "Annullato"  }
                       ];

        $scope.motivazioni = [
                         { target:"ATTO_NOTIFICATO", description:"Notifica atto", title: "L'atto viene notificato ai destinatari."  },
                         { target:"ANNULLAMENTO" , description:"Notifica per annullamento", title: "Notifica per annullamento"  },
                         { target:"VARIAZIONE" , description:"Notifica per variazione", title: "Notifica per variazione"  },
                         { target:"ODG_SUPPLETIVO", description:"Notifica ODG suppletivo", title: "Notifica ODG suppletivo"  },
                         { target:"ODG_BASE" , description:"Notifica ODG", title: "Notifica ODG"  }
                   ];
         
        for(var k in $scope.statos){
        	$scope.statoTitles[$scope.statos[k].target] = $scope.statos[k].title;
        }
        for(var k in $scope.statos){
        	$scope.statoMap[$scope.statos[k].target] = $scope.statos[k].description;
        }
        for(var k in $scope.motivazioni){
        	$scope.motivazioneMap[$scope.motivazioni[k].target] = $scope.motivazioni[k].description;
        }
        
        $scope.tipiInvio = ["E-Mail","Posta Elettronica Certificata","SMS"];
        
        $scope.annullaTentativo = function(newsid){
        	if(confirm("Premere Ok per annullare l'invio")){
        		News.annullaTentativo({newsid:newsid}, function(){
        			$scope.loadAll();
        		});
        	}
        };
        
        $scope.retryInvio = function(newsid){
        	if(confirm("Premere Ok per ritentare l'invio")){
        		News.retryInvio({newsid:newsid}, function(){
        			$scope.loadAll();
        		});
        	}
        }
        
        $scope.loadPageStorico = function(page) {
            $scope.pageStorico = page;
            News.loadStoricoTentativi({newsid:$scope.storicoNews.id, page:$scope.pageStorico, per_page:5}, function(result, headers){
        		$scope.storicoTentativi = result;
        		 $scope.linksStorico = ParseLinks.parse(headers('link'));
        	});
        };
        
        $scope.loadStoricoTentativi = function(news){
        	if(news.originalNews!=undefined && news.originalNews != null && news.originalNews.id > 0){
        		$scope.storicoNews = news.originalNews;
        	}else{
        		$scope.storicoNews = news;
        	}
        	$scope.storicoTentativi = [];
        	$scope.pageStorico = 1;
        	$("#storicoTentativi").modal("show");
        	News.loadStoricoTentativi({newsid:$scope.storicoNews.id, page:$scope.pageStorico, per_page:5}, function(result, headers){
        		$scope.storicoTentativi = result;
        		$scope.linksStorico = ParseLinks.parse(headers('link'));
        	});
        };
        
        $scope.loadAll = function(searchObject) {
        	if(searchObject!=undefined && searchObject!=null){
        		News.newsReportLastState({page: $scope.page, per_page: 10}, searchObject, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.lastNews = result;
	                $("#updateList").removeClass("spinEffect");
	            });
        	}else{
        		News.newsReportLastState({page: $scope.page, per_page: 10}, $scope.lastNewsearch, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.lastNews = result;
	            });
        	}
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastNewsearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.aggiorna = function(){
        	$("#updateList").addClass("spinEffect");
        	/*copio tempSearch e suoi elementi in lastNewsearch*/
        	$scope.lastNewsearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.lastNewsearch = {};
        	$scope.tempSearch = {};
        	$scope.ricerca();
        };
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
        
        $scope.loadAll();

    });
