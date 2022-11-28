'use strict';

angular.module('cifra2gestattiApp', ['LocalStorageModule', 'tmh.dynamicLocale',
    'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'infinite-scroll',
    'ui.grid', 'ui.grid.treeView','ui.grid.selection','ui.grid.pagination', 'mgcrea.ngStrap', 'mgcrea.ngStrap.modal', 'colorpicker.module',
    'ngSanitize', 'ui.select','ngFileUpload','filereader','ui.tree','checklist-model','ckeditor','ngToast', 'summernote',
    'ngMaterial','expression-builder', 'ui.utils.masks', 'angularMoment', 'moment-picker','angularScreenLoader'])
    .config(function($datepickerProvider) {
     angular.extend($datepickerProvider.defaults, {
      dateFormat: 'dd/MM/yyyy',
      modelDateFormat: 'yyyy-MM-dd',
      dateType: 'string',
      startWeek: 1,
      autoclose:true
    });
    })
    .config(['momentPickerProvider', function (momentPickerProvider) {
    	momentPickerProvider.options({
    		locale:        'it',
            format:        'DD/MM/YYYY, HH:mm',
            minView:       'decade',
            maxView:       'hour',
            startView:     'month',
            autoclose:     true,
            today:         true,
            keyboard:      false,

            /* Extra: Views properties */
            leftArrow:     '&larr;',
            rightArrow:    '&rarr;',
            yearsFormat:   'YYYY',
            monthsFormat:  'MMM',
            daysFormat:    'D',
            hoursFormat:   'HH:[00]',
            minutesFormat: 'HH:mm',
            secondsFormat: 'ss',
            minutesStep:   15,
            secondsStep:   1
    	});
    }])
    .factory('globalErrorInterceptor', function($q, ngToast, $rootScope) {
	  return {
	    'responseError': function(r) {
	      try{
		      if(r.status == 0 && (!$rootScope.lastTimeErrorMessage || new Date().getTime() - $rootScope.lastTimeErrorMessage > 1000)){
		    	  ngToast.create(  { className: 'danger', content: 'Attenzione! Si stanno verificando problemi di rete. Verificare la connessione e riprovare.'} );
		    	  $rootScope.lastTimeErrorMessage = new Date().getTime();
		      }
	      }catch(err){
	    	  console.log("errore", err);
	      }
	      return $q.reject(r)
	    }
	  }
	})
	.config(function($httpProvider) {
		$httpProvider.interceptors.push('globalErrorInterceptor')
	})
    .config(function($timepickerProvider) {
     angular.extend($timepickerProvider.defaults, {
        timeType: 'string',
        autoclose:true,
        minuteStep:15
     });
    })
    .run(function ($rootScope, $location, $window, $http, $state, $translate, Auth,AuthServerProvider, Principal,
                    Language, ENV, VERSION, SITE_TITLE, SITE_CSS, ITER_SENZA_VERIFICA_CONTABILE, ITER_CON_VERIFICA_CONTABILE, Navigatore, ProfiloAccount, $log,
                    localStorageService, Utente, Profilo, TipoMateria, ConfigurationParams, ngToast, TimeAuth, $sce, Upload, $timeout, Toggle) {

//TODO Chiamata non più utilizzata. Di seguito sono riportati i parametri restituiti lato server
//    	env0=note-v2,xls-ricerca,export-xml-ricerca,xls-avcpanac,export-ricerca-pdf
//    	env1=note-c,export-ricerca-pdf


//    	Toggle.get({}, function(res){
//    		$rootScope.environment = res;
//    	});

    	$rootScope.disableLabel = " (Disabilitato)";
        
        $rootScope.addDisableLabelToObj = function(obj, attribute){
        	if(attribute){
        		if(attribute.indexOf(".") < 0){
        			if(obj[attribute] && obj[attribute].indexOf($rootScope.disableLabel) < 0){
        				obj[attribute] = obj[attribute] + $rootScope.disableLabel;
        			}
        			return obj[attribute];
        		}else{
        			var leftAttr = attribute.split(".")[0];
        			var rigthAttr = attribute.substring(leftAttr.length + 1);
        			return $rootScope.addDisableLabelToObj(obj[leftAttr], rigthAttr);
        		}
        	}else{
        		return obj;
        	}
        }
        
        $rootScope.showMessage = function(genericMessage){
        	$rootScope.genericMessage = {};
        	if(genericMessage){
        		$rootScope.genericMessage = genericMessage;
        		$('#genericMessage').modal('show');
        	}
        }
        
        $rootScope.objectToArray = function(obj){
        	if(!obj){
        		return [];
        	}
        	var result = [];
        	Object.keys(obj).map(function(key) {
        		result.push(obj[key]);
    		});
        	return result;
        }
        
        $rootScope.resetDisableLabel = function(){
        	$rootScope.disableLabel = " (Disabilitato)";
        };

		$rootScope.copyInputElementToClipBoard = function(elementId){
			let messageElement = document.getElementById(elementId);;
			const elTA = document.createElement('textarea');
			elTA.style.opacity = 0;
			elTA.innerHTML = messageElement.value ? messageElement.value : messageElement.innerText;
			let mainDOM = null;
			let modalFadeIn = null;
			if($('body').find('.modal').is(':visible') ){
				modalFadeIn = $('.modal.fade.in')[0];
				$(modalFadeIn).removeClass('modal fade in');
				mainDOM = modalFadeIn;
			}else{
				mainDOM = document.body;
			}
			mainDOM.appendChild(elTA);
		    elTA.select();
		    elTA.setSelectionRange(0, 99999);
		    document.execCommand('copy');
		    if(modalFadeIn){
		    	$(modalFadeIn).addClass('modal fade in');
		    }
		    mainDOM.removeChild(elTA);
		};
        
        /*
         * collezione: collezione sulla quale aggiungere gli eventuali oggetti referenziati disabilitati
         * ref: oggetto referenziato, ottenuto dalla copia-server
         * label: stringa che identifica l'attributo al quale accodare $rootScope.disableLabel
         * refInPage: oggetto referenziato, ottenuto dall'oggetto in pagina
         * notEdit: booleano, se true la collection viene restituita senza alcuna modifica
         * */
        $rootScope.includiRef = function(collezione, ref, label, refInPage, notEdit){
        	  if(notEdit || !collezione){
        		  return collezione;
        	  }
    		  if(collezione instanceof Array){
    			   var items = collezione.slice();
         	       if(ref && items){
					     if(ref instanceof Array){
							 /*ref array e items array*/
							 if(ref.length > 0){
								var thereIs = false;
								ref.forEach(function(r){
									thereIs = false;
									items.forEach(function(v){
										 if(!thereIs && v && ref && r.id == v.id){
											thereIs = true;
										 }
									 });
									 if(!thereIs){
										 $rootScope.addDisableLabelToObj(r, label);
										 if(refInPage){
											 refInPage.forEach(function(vp){
												 if(r.id === vp.id){
													  vp.highlighted = true;
												 }else{
													 vp.highlighted = false;
												 }
											 });
										 }
										items.unshift(r);
									 }
								 });
								 
								return items;
							 }else{
								return items;
							 }
						 }else{
							 /*ref object e items array*/
							var thereIs = false;
							 Object.keys(items).forEach(function(v){
								 if(!thereIs && v && items[v] && ref && ref.id == items[v].id){
									thereIs = true;
								 }
							 });
							 if(!thereIs){
								$rootScope.addDisableLabelToObj(ref, label);
								if(refInPage){
									if(ref.id === refInPage.id){
										refInPage.highlighted = true;
									}else{
										refInPage.highlighted = false;
									}
								}
								items.unshift(ref);
							 }else{
								 if(refInPage){
									 refInPage.highlighted = false;
								 }
							 }
						 }
     	    	    	return items;
         	       }else{
         	    	   return items;
         	       }
    		  }else{
    			   var items = Object.assign({}, collezione);
         	       if(ref && items){
						if(ref instanceof Array){
							/*ref array e items map*/
							 if(ref.length > 0){
								var thereIs = false;
								Object.keys(ref).forEach(function(r){
									thereIs = false;
									Object.keys(items).forEach(function(v){
										 if(!thereIs && v && items[v] && ref && ref.id == items[v].id){
											thereIs = true;
										 }
									 });
									 if(!thereIs){
									    $rootScope.addDisableLabelToObj(r, label);
										if(refInPage){
											if(refInPage){
												 Object.keys(refInPage).forEach(function(vp){
													 if(r.id === vp.id){
														  vp.highlighted = true;
													 }else{
														 vp.highlighted = false;
													 }
												 });
											}
										}
										items[r.id] = r;
									 }
								 });
							 }else{
								return items;
							 }
						 }else{
							 /*ref object e items map*/
							var thereIs = false;
							 Object.keys(items).forEach(function(v){
								if(!thereIs && v && items[v] && ref && ref.id == items[v].id){
									thereIs = true;
								 }
							});
							if(!thereIs){
								$rootScope.addDisableLabelToObj(ref, label);
								if(refInPage){
									if(ref.id === refInPage.id){
										refInPage.highlighted = true;
									}else{
										refInPage.highlighted = false;
									}
								}
								items[ref.id] = ref;
							 }else if(refInPage){
								 refInPage.highlighted = false;
							 }
							return items;
						 }
         	       }else{
         	    	   return items;
         	       }
    		  } 
    	};
    	
    	$rootScope.ITER_SENZA_VERIFICA_CONTABILE = ITER_SENZA_VERIFICA_CONTABILE;
    	$rootScope.ITER_CON_VERIFICA_CONTABILE = ITER_CON_VERIFICA_CONTABILE;

    	$rootScope.ngfMaxSize = 311457280; //30 MB File size limit (DEFAULT)
        $rootScope.aooRoles = ['ROLE_SOTTOSCRITTORE_EMANANTE'];

		$rootScope.isLogged = function(){
			return Principal.isAuthenticated();
		}
        
    	ConfigurationParams.get().then(function (data) {
        	$rootScope.configurationParams = data;
        	$rootScope.ENV=$rootScope.configurationParams.global_env;
        	$rootScope.VERSION=$rootScope.configurationParams.global_version;
        	$rootScope.URL_CONVERT=$rootScope.configurationParams.global_url_convert;
        	$rootScope.SITE_TITLE=$rootScope.configurationParams.global_site_title;
        	$rootScope.SITE_CSS=$rootScope.configurationParams.global_site_css;
        	
        	var maxFileSizeVal = parseInt($rootScope.configurationParams.multipart_max_file_size) || 0;
        	if (maxFileSizeVal > 0) {
        		$rootScope.ngfMaxSize = maxFileSizeVal;
        	}
        });

        $rootScope.$on('okLoginEvent',  function(event, account) {
        	$rootScope.jk = true;
            ProfiloAccount.setAccount(account);
            $log.debug("okLoginEvent");
            if( angular.isDefined($rootScope.account) && angular.isDefined($rootScope.account.login) ){
                     Utente.activeprofilos( {id:$rootScope.account.login} , function(result){
                        $rootScope.activeprofilos = result;

                        //se ha solo un profilo lo seleziona in automatico
                        if( angular.isDefined($rootScope.activeprofilos) &&
                        	$rootScope.activeprofilos !== null  && $rootScope.activeprofilos.length === 1) {

                             Profilo.get({id:$rootScope.activeprofilos[0].id}, function(result){
                                ProfiloAccount.setProfilo(result);
                                Utente.getAoosDirigente({id:$rootScope.account.utente.id}, function(res){
            	            		$rootScope.aoosDirigente = res;
            	            		$state.go("selezionaprofilo");
            	            	});
                                TipoMateria.active(function(data){
                                    ProfiloAccount.setTipoMaterie(data);
                                });
                                $state.reload();
                                $state.reload("navbar");
                                $state.reload("content");
                                $state.reload("notifiche");
                            });
                       }
                       else {
                    	    var utenteId = $rootScope.account.utente.id;
               				Utente.lastProfile({id:utenteId}, function(p){
               					var profiloId = p.id;
	               				if(profiloId && profiloId != '0'){
	               					Profilo.get({id:profiloId}, function(result){
	               						ProfiloAccount.setProfilo(result);
	               						Utente.getAoosDirigente({id:$rootScope.account.utente.id}, function(res){
	               		            		$rootScope.aoosDirigente = res;
	               		            		$state.go("selezionaprofilo");
	               		            	});
	               						$state.reload();
	               					});
	               				}
	               				else{
               						/*ProfiloAccount.setProfilo({id:0,descrizione:'Tutti i profili dell\'utente',utente:utenteId,aoo:{id:0}});
               						Utente.getAoosDirigente({id:$rootScope.account.utente.id}, function(res){
               		            		$rootScope.aoosDirigente = res;
               		            		$state.go("selezionaprofilo");
               		            	});*/
	               					if($rootScope.activeprofilos && $rootScope.activeprofilos.length > 0){
		               					Profilo.get({id:$rootScope.activeprofilos[0].id}, function(result){
		                                    ProfiloAccount.setProfilo(result);
		                                    Utente.getAoosDirigente({id:$rootScope.account.utente.id}, function(res){
		                	            		$rootScope.aoosDirigente = res;
		                	            		$state.go("selezionaprofilo");
		                	            	});
		                                    TipoMateria.active(function(data){
		                                        ProfiloAccount.setTipoMaterie(data);
		                                    });
		                                    $state.reload();
		                                    $state.reload("navbar");
		                                    $state.reload("content");
		                                    $state.reload("notifiche");
		                                });
	               					}else{
	               						ProfiloAccount.setProfilo({id:0,descrizione:'',utente:utenteId,aoo:{id:0}});
	               						$rootScope.aoosDirigente = [];
	               						$state.go("selezionaprofilo");
	               					}
               						$state.reload();
	               				}
               				});
                   		}

                        $state.reload("navbar");
                        $state.reload("content");
                        $state.reload("notifiche");
                     });
                }
        });

        $rootScope.$on('auth-expire', function(event, remaining) {
            $log.debug("auth-expire:restano:", remaining );
            ngToast.create(  { className: 'danger', content: 'Restano '+remaining+' secondi allo scadere della sessione!'} );
        });

        $rootScope.$on('refresh-token', function(event) {
            var token = localStorageService.get('token');
            $rootScope.access_token = token.access_token;
            ngToast.create(  { className: 'success', content: 'Sessione aggiornata.'} );
        });

        $rootScope.$on('refresh-token-delay', function(event) {

        	ngToast.create(  { className: 'warning', content: 'Disconnessione in corso della sessione già attiva con l\'utenza corrente...'} );
        	$timeout(function (){
        		Auth.refresh(function () {
        			var token = localStorageService.get('token');
                    $rootScope.access_token = token.access_token;
                    ngToast.create(  { className: 'success', content: 'Sessione unica.'} );
                }).catch(function () {});
        	}, 5000);

        });
        /*
        $rootScope.$on('refresh-token-wm', function(event) {
            var token = localStorageService.get('token');
            $rootScope.access_token = token.access_token;
        });
		*/

        $rootScope.$on('profileChangeEvent',  function(event, account) {
            // Menuorizzontale.storeMenuBar();
        });

        $rootScope.$on('errorInternalEvent',  function(event, errorData) {
            $log.debug("errorInternalEvent" );
            $log.debug(errorData.data.message);
            ngToast.create(  { className: 'danger', content: $sce.trustAsHtml(errorData.data.message), timeout:10000 });
        });
        
        $rootScope.$on('invalidSecuritykeyEvent',  function(event) {
        	if($rootScope.profiloattivo){
        		$state.go('accessdenied');
	        	/*if($state.current.name.toLowerCase().indexOf('atto') >= 0){
	        		$state.go('attoAccessUnauthorized');
	        	}else{
	        		$state.go('accessdenied');
	        	}
	        	*/
	        	$rootScope.jk = true;
        	}
        });
        
        $rootScope.$on('rejectedFilesEvent',  function(event, files, sourceEvent, rejectedFiles, supportedExtensions) {
            $log.debug("rejectedFilesEvent" );
            var errorMessage = '';
            angular.forEach(rejectedFiles, function(file){
            	if(file.size > $rootScope.ngfMaxSize){
            		errorMessage = "Non è possibile allegare file di dimensione superiore a " + ($rootScope.ngfMaxSize/1024/1024) + " MB";
            	}
            });

            if(!errorMessage){
            	errorMessage = 'Non è possibile allegare file di questa tipologia.';
            	if(supportedExtensions && angular.isArray(supportedExtensions)){
            		errorMessage += " Le estesioni supportate sono: <ul>";
            		for(var i = 0; i < supportedExtensions.length; i++){
            			errorMessage += "<li>." + supportedExtensions[i];
            			if(i + 1 == supportedExtensions.length){
            				errorMessage += ".";
            			} else {
            				errorMessage += ";";
            			}
            			errorMessage += "</li>";
            		}
            		errorMessage += "</ul>";
            	}
            }

            ngToast.create(  { className: 'danger', content: errorMessage, timeout:10000 });
        });

        $rootScope.$on('LogoutEvent',  function(event) {
        	$rootScope.activeExitUserConfirmation = null;
            TimeAuth.stop();

        });

        $rootScope.$on('Logout403Event',  function(event) {
            ngToast.create(  { className: 'danger', content: 'Si è verificato un errore. Riprovare.' });
        });

        $rootScope.$on('Login423Event',  function(event) {
        	$rootScope.activeExitUserConfirmation = null;
        	localStorageService.clearAll();
        	Auth.logout("login423");
            TimeAuth.stop();
            $state.go('home');
            ngToast.create(  { className: 'danger', content: 'Accesso negato. Account in attesa che sia associato ad un profilo.' });
        });

        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {

        	$rootScope.navigation = null;

       		if ($rootScope.activeExitUserConfirmation) {
       			event.preventDefault();
       			$rootScope.navigation = {'toState':toState, 'toStateParams': toStateParams};
       			$rootScope.cambioProfilo = false;
       			$('#exitFormConfirmation').modal('show');

       		} else {
       			$rootScope.toState = toState;
       			$rootScope.toStateParams = toStateParams;
       			if (Principal.isIdentityResolved()) {
       				Auth.authorize();
       			}

       			// Update the language
       			Language.getCurrent().then(function (language) {
       				$translate.use(language);
       			});
       		}
        });
        
        $rootScope.getAppKey = function(baseUrl, isDownload){
        	var SECURITY_SEPARATOR = "#!@";
        	//var secret = btoa(CryptoJS.AES.encrypt($location.path() + SECURITY_SEPARATOR + $rootScope.profiloattivo.id, "kOtu9zDudS*ne51X").toString());
        	
        	var str = baseUrl + SECURITY_SEPARATOR;
        	
        	if(!isDownload){
        		str += ($rootScope.account && $rootScope.account.login ? $rootScope.account.login.toLowerCase() : '');
        		str += ($rootScope.profiloattivo && $rootScope.profiloattivo.id !== undefined ? $rootScope.profiloattivo.id : '');
        	}else{
        		var usrname = null;
        		if($rootScope.profiloOrigine && $rootScope.profiloOrigine.utente && $rootScope.profiloOrigine.utente.username){
            		usrname = $rootScope.profiloOrigine.utente.username.toLowerCase();
            		if(!isDownload){
            			usrname += ($rootScope.profiloOrigine.id ? $rootScope.profiloOrigine.id : '');
                	}
            	}else if($rootScope.account && $rootScope.account.login){
            		usrname = $rootScope.account.login.toLowerCase();
            		if(!isDownload){
            			usrname += ($rootScope.profiloattivo && $rootScope.profiloattivo.id !== undefined ? $rootScope.profiloattivo.id : '');
                	}
            	}else{
            		usrname = '';
            	}
        		str += usrname;
        	}
    		var secret = btoa(str);
        	var dupuredSecret = secret.split("/").join("").split("=").join("");
        	return window.encodeURIComponent(dupuredSecret);
        };
        
        $rootScope.insertUpdateAppKey = function(){
        	if($rootScope.profiloattivo){
        		var baseUrl = $location.url().split('?')[0];
       			var dupuredSecret = $rootScope.getAppKey(baseUrl);
       			var securityParam = "appkey=" + dupuredSecret;
            	if($rootScope.jk && $location.absUrl().indexOf("appkey") < 0){
                	$location.url(baseUrl + (baseUrl.indexOf("?") > 0 ? "&" : "?") + securityParam);
                	$rootScope.jk = true;
            	}else if($location.absUrl().indexOf("appkey") > -1){
            		$rootScope.jk = true;
            	}
            }
        };
        
        $rootScope.$on('$stateChangeSuccess', function (event, toState, toStateParams) {
        	$rootScope.insertUpdateAppKey();
        });

        $rootScope.hasBack = function(){
        	return $.inArray('secondLastState', localStorageService.keys()) >= 0 && localStorageService.get('secondLastState') != undefined && localStorageService.get('secondLastState') != null && localStorageService.get('secondLastState').state != undefined;
        };

        $rootScope.hasBackRicerca = function(){
        	return $.inArray('vistaProvenienzaAtto', localStorageService.keys()) >= 0 && localStorageService.get('vistaProvenienzaAtto') != undefined && localStorageService.get('vistaProvenienzaAtto') != null && localStorageService.get('vistaProvenienzaAtto').state != undefined && localStorageService.get('vistaProvenienzaAtto').state == 'attoList';
        };

        $rootScope.goBack = function(){
        	if($rootScope.hasBack()){
	        	var lastState = localStorageService.get('secondLastState');
	        	$state.go(lastState.state, lastState.params);
	        	localStorageService.set('lastState', lastState);
	        	localStorageService.remove('secondLastState');
        	}
        };

        $rootScope.getCookie = function(cname) {
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for(var i = 0; i <ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length,c.length);
                }
            }
            return "";
        };

        $rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
        	/*
        	var today = new Date();
        	if(AuthServerProvider.getToken() != null && typeof AuthServerProvider.getToken() != 'undefined' && (AuthServerProvider.getToken().expires_at - today.getTime()) < 1790000) {
        		Auth.refresh(function () {
        			$rootScope.$broadcast('refresh-token-wm');
        		}).catch(function () {
        		});
        	}
        	*/
            var titleKey = 'global.title';

            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;

            // Set the page title key to the one configured in state or use default one
            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }
            $translate(titleKey).then(function (title) {
                // Change window title with translated one
                $window.document.title = title;
            });

            if(Principal.isAuthenticated() ){
                TimeAuth.start();
            }
            // Menuorizzontale.menubar();
            // MenuV.menu_v();
            Navigatore.navigate(event, toState, toParams, fromState, fromParams);

        });
        
        $rootScope.buildDownloadUrl = function(basicUrl){
        	var goodUrl = basicUrl.startsWith("api/") ? "/" + basicUrl : basicUrl;
        	if(goodUrl.startsWith("/api/")){
	        	var securityParam = "&appkey=" + $rootScope.getAppKey(goodUrl.split('?')[0], true);
	        	return basicUrl + (basicUrl.indexOf("?") > 0 ? "&" : "?") + securityParam;
        	}else{
        		return basicUrl;
        	}
        };

        $rootScope.back = function() {
            if( $rootScope.profiloattivo === null ){
                $state.go('selezionaprofilo');
            }else
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };
        
        $rootScope.MAXCARATTERIOGGETTOINLISTA = 150;
        $rootScope.visualizzaOggetto = function(oggetto, customMaxSize) {
        	if(!oggetto){
        		return "";
        	}
        	let MAX_SIZE = customMaxSize ? customMaxSize : $rootScope.MAXCARATTERIOGGETTOINLISTA;
        	var retValue = oggetto;
        	if(oggetto.length > MAX_SIZE){

        	var blankPosition = oggetto.substring(MAX_SIZE).indexOf(" ");

        	retValue = oggetto.substring(0, MAX_SIZE+blankPosition)+"...";
        	}	
        	return retValue;
        };

        $(document).ready(function() {
        	var timeoutId;
        	$('body').on('mousemove',function() {
        	    if (!timeoutId) {
        	        timeoutId = window.setTimeout(function() {
        	            timeoutId = null;
        	            if(!$('body').find('.modal').is(':visible') ){
                      		$('body').removeClass('modal-open');
                        }
                        if($('.modal').length > 0){
                      	  var modali = [...$('.modal').toArray()];
        	                  for(var i = 0; i < modali.length; i++){
        	                	  if(!modali[i].binded){
        	                		  modali[i].binded = true;
        			                  $(modali[i]).on('shown.bs.modal', function () {
        			                	  $('body').addClass('modal-open');
        			              	  });
        			                  $(modali[i]).on('hidden.bs.modal', function () {
        			                	  if($('body').find('.modal').is(':visible') ){
        			                		  $('body').addClass('modal-open');
          		                          }
        			              	  });
        		                  }
        	                  }
                        }
        	       }, 1500);
        	    }
        	});       
        });
    })
    .factory('authInterceptor', function ($rootScope, $q, $location, localStorageService,ProfiloAccount, $log, $stateParams) {
        return {
            // Add authorization token to headers
            request: function (config) {
                config.headers = config.headers || {};
                var token = localStorageService.get('token');
                //$log.debug("config", config);

                /*if(token) {
                	var expiredAt = new Date();
                    expiredAt.setSeconds(expiredAt.getSeconds() + token.expires_in);
                    token.expires_at = expiredAt.getTime();
                    localStorageService.set('token', token);
                }*/


                if (token && token.expires_at && token.expires_at > new Date().getTime()) {
                    if(config.url !== 'oauth/token'){
                        config.headers.Authorization = 'Bearer ' + token.access_token;
                    }

                    $rootScope.access_token = token.access_token;

                    ProfiloAccount.loadAccount();
                    //$log.debug('profilo attivo:', $rootScope.profiloattivo);
                    if($rootScope.profiloattivo){
                        config.headers.profiloId = + ($rootScope.profiloattivo && $rootScope.profiloattivo.id !== undefined ? $rootScope.profiloattivo.id : '');
                        //config.headers.user = ($rootScope.account && $rootScope.account.login ? $rootScope.account.login : '');
                        config.headers.aooId = $rootScope.profiloattivo.aoo.id;
                    }
                    if ($rootScope.profiloOrigine) {
                    	config.headers.imp_profiloid = $rootScope.profiloOrigine.id;
                    }

                    if($stateParams && $stateParams.taskBpmId){
                    	config.headers.taskBpmId = $stateParams.taskBpmId;
                    }
                    if($stateParams && $stateParams.seduta){
                    	config.headers.sedutaId = $stateParams.seduta;
                    }
                    if($rootScope.profiloattivo){
                    	var baseUrl = $location.url();
                    	if(config.url.indexOf("api/") > -1 && $location.absUrl().indexOf("appkey=") > -1){
	                    	var securitykey = $location.absUrl().split("appkey=")[1].split("&")[0];
	                    	if(securitykey){
	                    		config.headers.appkey = decodeURIComponent(securitykey);
	                    		config.headers.originPath = $location.path();
	                    	}
                    	}
                    }
                }

                return config;
            }
        };
    })
    .factory('authExpiredInterceptor', function ($rootScope, $q, $injector,localStorageService) {
        return {
            responseError: function (response) {
                // token has expired
                if (response.status === 401 && (response.data.error == 'invalid_token' || response.data.error == 'Unauthorized') && (!response.data || !response.data.message || response.data.message != "invalid_appkey")) {
                    localStorageService.remove('token');
                    var Principal = $injector.get('Principal');
                    if (Principal.isAuthenticated()) {
                        var Auth = $injector.get('Auth');
                        Auth.authorize(true);
                    }
                }
                if(response.status === 401 && response.data && response.data.message && response.data.message == "invalid_appkey"){
                	$rootScope.$broadcast('invalidSecuritykeyEvent');
                }
                return $q.reject(response);
            }
        };
    })
    .factory('errorInternalServiceInterceptor', function ($rootScope, $q, $injector, localStorageService,$log) {
        return {
            responseError: function (response) {
                // Errore interni al server
            	if (response.status == 403 ) {
            		 $log.debug(response);
            		 $rootScope.$broadcast('Logout403Event');
            	}

                if (response.status >= 500 ) {
                    $log.debug(response);
                    $rootScope.$broadcast('errorInternalEvent', response);
                }
                return $q.reject(response);
            }
        };
    })
    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            views: {
                'navbar@': {
                    templateUrl: 'scripts/components/navbar/navbar.html',
                    controller: 'NavbarController'
                },
                 'navigatore@': {
                     templateUrl: 'scripts/components/navigatore/navigatore.html',
                     controller: 'NavigatoreController'
                },
                'notifiche@': {
                   templateUrl: 'scripts/app/entities/news/news-notifica.html',
                   controller: 'MainController'
               }
                //Occhi aggiungingedo state si devono aggingree anche nei test karma javascript
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('language');
                    return $translate.refresh();
                }]
            }
        });

        $httpProvider.interceptors.push('authInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');
        $httpProvider.interceptors.push('errorInternalServiceInterceptor');


        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json'
        });

        $translateProvider.preferredLanguage('it');
        $translateProvider.useCookieStorage();

        tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
    });


angular.module('cifra2gestattiApp').constant('AllegatoConstants', {
    tipoAllegato: {
        GENERICO: 'GENERICO',
        PARTE_INTEGRANTE: 'PARTE_INTEGRANTE',
        ACCORPATO: 'ACCORPATO'
    }
});

angular.module('cifra2gestattiApp').constant('SedutaGiuntaConstants', {

	fasiSeduta: {
		PREDISPOSIZIONE:'PREDISPOSIZIONE',
		PREDISPOSTA:'PREDISPOSTA',
		CONCLUSA:'CONCLUSA',
		ANNULLATA:'ANNULLATA'
	},
	statiSeduta: {
		odgBaseInPredisposizione: {
			codice: 'odgBaseInPredisposizione',
			fase:   'PREDISPOSIZIONE',
			label:  'Odg Base in predisposizione'
		},
		odgBaseConsolidato: {
			codice: 'odgBaseConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odg Base consolidato'
		},
		odgSuppletivoConsolidato: {
			codice: 'odgSuppletivoConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odg Suppletivo consolidato'
		},
		odgFuoriSaccoConsolidato: {
			codice: 'odgFuoriSaccoConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odg Fuori Sacco consolidato'
		},
		odlBaseInPredisposizione: {
			codice: 'odlBaseInPredisposizione',
			fase:   'PREDISPOSIZIONE',
			label:  'Odl Base in predisposizione'
		},
		odlBaseConsolidato: {
			codice: 'odlBaseConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odl Base consolidato'
		},
		odlSuppletivoConsolidato: {
			codice: 'odlSuppletivoConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odl Suppletivo consolidato'
		},
		odlFuoriSaccoConsolidato: {
			codice: 'odlFuoriSaccoConsolidato',
			fase:   'PREDISPOSTA',
			label:  'Odl Fuori Sacco consolidato'
		},
		sedutaInAttesaDocumentoVariazione: {
			codice: 'sedutaInAttesaDocumentoVariazione',
			fase:   'PREDISPOSTA',
			label:  'Seduta in attesa di generazione documento di variazione'
		},
		sedutaDocumentoVariazioneGenerato: {
			codice: 'sedutaDocumentoVariazioneGenerato',
			fase:   'PREDISPOSTA',
			label:  'Seduta con documento di variazione generato'
		},
		sedutaConclusaRegistrazioneEsiti: {
			codice: 'sedutaConclusaRegistrazioneEsiti',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Registrazione Esiti'
		},
		sedutaConclusaIndicazioneAttiTrattati:{
			codice: 'sedutaConclusaIndicazioneAttiTrattati',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Indicazione atti trattati'
		},
		sedutaConclusaIndicazioneOrdineDiscussione:{
			codice: 'sedutaConclusaIndicazioneOrdineDiscussione',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Indicazione ordine discussione'
		},
		sedutaConclusaIndicazioneNumeroArgomento:{
			codice: 'sedutaConclusaIndicazioneNumeroArgomento',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Indicazione numero argomento'
		},
		sedutaConclusaEsitiConfermati: {
			codice: 'sedutaConclusaEsitiConfermati',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Esiti Confermati'
		},
		sedutaConclusaNumerazioneConfermata: {
			codice: 'sedutaConclusaNumerazioneConfermata',
			fase:   'CONCLUSA',
			label:  'Seduta conclusa-Numerazione Confermata'
		},
		sedutaAnnullata: {
			codice: 'sedutaAnnullata',
			fase:   'ANNULLATA',
			label:  'Seduta annullata'
		},
		sedutaConsolidata: {
			codice: 'sedutaConsolidata',
			fase: 'CONCLUSA',
			label:  'Seduta Consolidata'
		},
		sedutaVerbalizzata: {
			codice: 'sedutaVerbalizzata',
			fase: 'CONCLUSA',
			label: 'Seduta Conclusa e Verbalizzata'
		},
	},

//  Modificata gestione stati seduta con introduzione
//    statiSeduta: {
//        sedutaInAttesaDiFirmaOdgBase: 'Seduta in attesa di firma OdG',
//        sedutaInAttesaDiFirmaOdlBase: 'Seduta in attesa di firma OdL',
//        sedutaInPredisposizione: 'Seduta in predisposizione',
//        sedutaConsolidata: 'Seduta consolidata',
//        sedutaConclusa: 'Seduta conclusa',
//        sedutaInAttesaDocumentoVariazione: 'Seduta in attesa di generazione documento di variazione',
//        sedutaInAttesaDiFirmaVariazione: 'Seduta in attesa di firma documento di variazione',
//        sedutaInAttesaDiFirmaAnnullamento: 'Seduta in attesa di firma documento di annullamento',
//        sedutaAnnullata: 'Seduta annullata',
//        sedutaProvvisoriaAnnullata: 'Seduta provvisoria annullata',
//        sedutaTerminata: 'Seduta terminata'
//    },
   statiAtto: {
    	propostaInAttesaDiEsito:'In attesa Consolidamento Seduta',
    	propostaInAttesaDiRitiro:'In attesa di ritiro',
    	propostaInseribileInOdg: 'Inseribile in OdG',
    	propostaInseribileInOdl: 'Inseribile in OdL',
    	propostaSospesa: 'Atto Sospeso',

    	// Stati non usati in ATTICO
//    	propostaConEsito: 'Proposta con esito registrato',
//    	propostaAdottata: 'Adottato',
//    	propostaAdottataEmendata: 'Adottato Emendato',
//    	propostaRespinta: 'Respinto',
//    	propostaRespintaEmendata: 'Respinto Emendato',
//    	propostaNumerata: 'Provvedimento numerato',
//    	propostaNotificata: 'Atto notificato',
//    	propostaDaRitirare: 'Predisposizione ritiro',
//    	ritiroInPredisposizione: 'Ritiro in Predisposizione',
//    	propostaInAttesaDiGenerazione: 'Provvedimento in attesa di generazione documento',
//    	propostaInAttesaDiFirmaSegretario: 'Provvedimento in attesa di firma del segretario',
//    	propostaInAttesaDiFirmaPresidente: 'Provvedimento in attesa di firma del presidente',
//    	provvedimentoEsecutivo: 'Provvedimento Esecutivo',
//    	provvedimentoPresoDatto: "Provvedimento Preso D’atto",
//    	provvedimentoVerbalizzato: 'Provvedimento Verbalizzato',
    },
    statiOdg: {
    	odgInPredisposizione: 'OdG in predisposizione',
    	odgInAttesaDiFirma: 'OdG in attesa di firma',
    	odlInPredisposizione: 'OdL in predisposizione',
    	odlInAttesaDiFirma: 'OdL in attesa di firma',
    },
    statiResoconto: {
    	resocontoInAttesaDiFirma: 'Resoconto in attesa di firma',
    	resocontoConsolidato: 'Resoconto firmato'
    },
    statiDocSeduta: {
    	docResInAttesaDiPredisposizione: "Resoconto in Attesa di Predisposizione",
    	docResInPredisposizione: "Resoconto in Predisposizione"
    },
    statiPresenze: {
    	presenzeInAttesaDiFirma: 'Doc presenze/assenze in attesa di firma',
    	presenzeConsolidato: 'Doc presenze/assenze firmato',
    },
    statiVerbale: {
    	verbaleInPredisposizione: 'Verbale in predisposizione',
    	verbaleInAttesaFirma: 'Verbale in attesa di firma',
    	verbaleConsolidato: 'Verbale consolidato',
    	verbaleRifiutato: 'Verbale non redatto'
    },
    esitoAttoOdg: {
    	nonTrattato : 'non_trattato'
    }
});


angular.module('cifra2gestattiApp').constant('RoleCodes', {
	ROLE_SINDACO: 'ROLE_SINDACO',
	ROLE_VICE_SINDACO: 'ROLE_VICE_SINDACO',

	ROLE_COMPONENTE_GIUNTA: 'ROLE_COMPONENTE_GIUNTA',
	ROLE_COMPONENTE_CONSIGLIO: 'ROLE_COMPONENTE_CONSIGLIO',

	ROLE_SEGRETARIO_SEDUTA_GIUNTA: 'ROLE_SEGRETARIO_SEDUTA_GIUNTA',
	ROLE_SEGRETARIO_SEDUTA_CONSIGLIO: 'ROLE_SEGRETARIO_SEDUTA_CONSIGLIO',
	ROLE_PRESIDENTE_SEDUTA_GIUNTA: 'ROLE_PRESIDENTE_SEDUTA_GIUNTA',
	ROLE_PRESIDENTE_SEDUTA_CONSIGLIO: 'ROLE_PRESIDENTE_SEDUTA_CONSIGLIO',
	ROLE_VICE_PRESIDENTE_SEDUTA_GIUNTA: 'ROLE_VICE_PRESIDENTE_SEDUTA_GIUNTA',
	ROLE_VICE_PRESIDENTE_SEDUTA_CONSIGLIO: 'ROLE_VICE_PRESIDENTE_SEDUTA_CONSIGLIO',
	ROLE_SCRUTATORE_SEDUTA_GIUNTA: 'ROLE_SCRUTATORE_SEDUTA_GIUNTA',
	ROLE_SCRUTATORE_SEDUTA_CONSIGLIO: 'ROLE_SCRUTATORE_SEDUTA_CONSIGLIO',

	ROLE_OPERATORE_ODG_GIUNTA: 'ROLE_OPERATORE_ODG_GIUNTA',
	ROLE_OPERATORE_ODG_CONSIGLIO: 'ROLE_OPERATORE_ODG_CONSIGLIO',
	ROLE_OPERATORE_RESOCONTO_GIUNTA: 'ROLE_OPERATORE_RESOCONTO_GIUNTA',
	ROLE_OPERATORE_RESOCONTO_CONSIGLIO: 'ROLE_OPERATORE_RESOCONTO_CONSIGLIO',
	ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_G: 'ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_G',
	ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_C: 'ROLE_VISUALIZZATORE_ATTI_POST_SEDUTA_C'
});

angular.module('cifra2gestattiApp').constant('StatiRispostaCodes', [
	{
		codice: "RISPOSTA_NEI_TERMINI",
		descrizione: "Risposta pervenuta nei termini"
	},
	{
		codice: "RISPOSTA_FUORI_TERMINI",
		descrizione: "Risposta pervenuta fuori dai termini"
	},
	{
		codice: "RISPOSTA_NON_PERVENUTA",
		descrizione: "Risposta non pervenuta"
	}
]);

angular.module('cifra2gestattiApp').constant('TerminiRispostaCodes', [
	{
		codice: "NON_URGENTE",
		label: "Non Urgente",
		ggScadenza: 30
	},
	{
		codice: "URGENTE",
		label: "Urgente",
		ggScadenza: 10
	}
]);


angular.module('cifra2gestattiApp').constant('ValVotazioni', [
	{ codice: "FAV",
	  descrizione: "Favorevole"
	},
	{ codice: "CON",
	  descrizione: "Contrario"
	},
	{ codice: "AST",
	  descrizione: "Astenuto"
	},
	{ codice: "NPV",
	  descrizione: "Presente non votante"
	}
]);

angular.module('cifra2gestattiApp').constant('BpmSeparator', {
	BPM_USERNAME_SEPARATOR: "_!U#_",
	BPM_INCARICO_SEPARATOR: "_!I#_",
	BPM_ROLE_SEPARATOR: "_!R#_"
});

angular.module('cifra2gestattiApp').constant('TypeFirmaMassiva', {
	FIRMA: ["FIRMA_ATTO", "FIRMA_REG_TECN"],
	GENERA_FIRMA: ["NUMERA_GENERA_FIRMA_ATTO", "GENERA_FIRMA_ATTO", "GENERA_FIRMA_REG_CONT", "GENERA_FIRMA_REG_TECN"]
});