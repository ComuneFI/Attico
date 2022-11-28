'use strict';

angular.module('cifra2gestattiApp')
    .directive('hasAnyRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInAnyRole(roles);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');

                if (roles.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('hasRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInAnyRole(role);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    role = attrs.hasRole.replace(/\s+/g, '').split(',');

                if (role.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('notHasRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                        	setHidden();
                        }

                        result = Principal.isInAnyRole(role);
                        if (result) {
                        	setHidden();
                        } else {
                        	setVisible();
                        }
                    },
                    role = attrs.notHasRole.replace(/\s+/g, '').split(',');

                if (role.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
 .directive('hasRuolo', ['ProfiloAccount','Principal','$log', '$rootScope', function (ProfiloAccount, Principal, $log, $rootScope) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {

                var setVisible = function (el) {
                		el.removeClass('hidden');
                    },
                    setHidden = function (el) {
                    	el.addClass('hidden');
                    },
                    defineVisibility = function (ruoli, el, reset) {
                        var result;
                        $rootScope.abilitato = false;
                        if (reset) {
                        	setHidden(el);
                        }
                        
                        if(ruoli === null || ruoli.length === 0 ){
                        	result = true;
 	                        setVisible(el);
                        }else{
                        	$rootScope.abilitato = false;
                        	if(angular.isDefined($rootScope.activeprofilos)){
                        		result = ProfiloAccount.isInAnyRole(ruoli);

                        		if(!result){
                        			result = Principal.isInAnyRole(ruoli);
                        		}

                        		if (result === true) {
	                        	    setVisible(el);
	                        	} else {
	                        	    setHidden(el);
	                        	}
                        	}else{
                        		if(!scope.retry){
                        			scope.retry = [];
                        		}
                        		scope.retry.push({ruoli: ruoli, el: el});
                        		scope.clearWatch = $rootScope.$watch('activeprofilos', function(value) {
                        			if(value){
                        				retryVisibility();
                        			}
                        		});
                        	}
                        }
                    },
                    retryVisibility = function(){
                    	if(scope.clearWatch){
                    		scope.clearWatch();
                    		scope.clearWatch = null;
                    	}
                    	if(scope.retry){
                    		angular.forEach(scope.retry, function(obj){
                    			defineVisibility(obj.ruoli, obj.el);
                    		});
                    		delete scope.retry;
                    	}
                    },
                    roles = attrs.hasRuolo.replace(/\s+/g, '').split(',');

                if (roles.length > 0) {
                    defineVisibility(roles, element, true);
                }
            }

        };
    }])
    .directive('hasRuoloInAllProfiles', ['ProfiloAccount','Principal','$log', '$rootScope', function (ProfiloAccount, Principal, $log, $rootScope) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {

                var setVisible = function (el) {
                        el.removeClass('hidden');
                    },
                    setHidden = function (el) {
                        el.addClass('hidden');
                    },
                    defineVisibility = function (ruoli, el, reset) {
                        var result;
                        $rootScope.abilitato = false;
                        if (reset) {
                        	setHidden(el);
                        }
                        
                        if(ruoli === null || ruoli.length === 0 ){
                        	result = true;
 	                        setVisible(el);
                        }else{
                        	$rootScope.abilitato = false;
                        	result = Principal.isInAnyRole(ruoli);
                        	if(result === true){
                        		setVisible(el);
                        	}else{
                        		if(angular.isDefined($rootScope.activeprofilos)){
			                        result = ProfiloAccount.isAnyProfilesInRole(ruoli);
		
			                        result.then(function(hasRuolo){
			                        	result = hasRuolo;
				                        if(hasRuolo === false){
				                           result = Principal.isInAnyRole(ruoli);
				                        }
				
				                        if (result === true) {
				                            setVisible(el);
				                        } else {
				                            setHidden(el);
				                        }
			                        });
                        		}else{
                        			if(!scope.retry){
                        				scope.retry = [];
                        			}
                        			scope.retry.push({ruoli: ruoli, el: el});
                        			scope.clearWatch = $rootScope.$watch('activeprofilos', function(value) {
                        				if(value){
                        					retryVisibility();
                        				}
                        			});
                        		}
                        	}
                        }
                    },
                    retryVisibility = function(){
                    	if(scope.clearWatch){
                    		scope.clearWatch();
                    		scope.clearWatch = null;
                    	}
                    	if(scope.retry){
                    		angular.forEach(scope.retry, function(obj){
                    			defineVisibility(obj.ruoli, obj.el);
                    		});
                    		delete scope.retry;
                    	}
                    },
                    roles = attrs.hasRuoloInAllProfiles.replace(/\s+/g, '').split(',');

                if (roles.length > 0) {
                    defineVisibility(roles, element, true);
                }
            }

        };
    }]);
