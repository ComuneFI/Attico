angular.module('ngCacheBuster', [])
  .config(['$httpProvider', function($httpProvider) {
    return $httpProvider.interceptors.push('httpRequestInterceptorCacheBuster');
  }])
    .provider('httpRequestInterceptorCacheBuster', function() {
	
	this.matchlist = [/.*partials.*/, /.*views.*/ ];
	this.buildTS = "";
	this.logRequests = false;
	
	//Default to whitelist (i.e. block all except matches)
	this.black=false; 
	
	//Select blacklist or whitelist, default to whitelist
	this.setMatchlist = function(list,black,buildTS) {
	    this.black = typeof black != 'undefined' ? black : false
	    this.matchlist = list;
		this.buildTS = buildTS;
	};
	

	this.setLogRequests = function(logRequests) {
	    this.logRequests = logRequests;
	};
	
	this.$get = ['$q', '$log','$rootScope', function($q, $log, $rootScope) {
	    var matchlist = this.matchlist;
	    var logRequests = this.logRequests;
	    var black = this.black;
        if (logRequests) {
            $log.log("Blacklist? ",black);
        }
	    return {
		'request': function(config) {
		    //Blacklist by default, match with whitelist
		    var busted= !black; 
		    
		    for(var i=0; i< matchlist.length; i++){
			if(config.url.match(matchlist[i])) {
			    busted=black; break;
			}
		    }
		    
			let isResource = false;
			if (!busted) {
				isResource = config.url && config.url.startsWith('scripts/') && (config.url.endsWith(".js") || config.url.endsWith(".html"));
				if(!isResource){
					isResource = config.url && config.url.startsWith('i18n/') && config.url.endsWith(".json");
				}
			}

		    //Bust if the URL was on blacklist or not on whitelist
		    if (busted) {
			var d = new Date();
			config.url = config.url.replace(/[?|&]cacheBuster=\d+/,'');
			//Some url's allready have '?' attached
			config.url+=config.url.indexOf('?') === -1 ? '?' : '&'
			config.url += 'cacheBuster=' + d.getTime();
		    }else if(isResource && $rootScope && $rootScope.configurationParams &&  $rootScope.configurationParams.buildTS){
				config.url = config.url.replace(/[?|&]cacheBuster=\d+/,'');
				config.url+=config.url.indexOf('?') === -1 ? '?' : '&'
				config.url += 'cacheBuster=' + $rootScope.configurationParams.buildTS;
			}
		    
		    if (logRequests) {
			var log='request.url =' + config.url
			busted ? $log.warn(log) : $log.info(log)
		    }

		    return config || $q.when(config);
		}
	    }
	}];
    });


