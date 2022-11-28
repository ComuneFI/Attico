'use strict';

angular.module('cifra2gestattiApp')
    .factory('Navigatore', function ($rootScope,$location,$window, $http, $state, $translate,$log) {
       
        return {
            navigate: function(event, toState, toParams, fromState, fromParams) {
                var titleKey = 'global.title';
                var link = '/';

                $rootScope.navigatoretitle = [];
                 $rootScope.previousStateName = fromState.name;
                 $rootScope.previousStateParams = fromParams;

                if (toState.key) {
                    titleKey = toState.key;
                }
                if(titleKey !== 'global.title'){
                    $translate().then(function (title) {
                       $rootScope.navigatoretitle.unshift({title:title,titleKey:titleKey,link:link});
                    });
                }
                // Set the page title key to the one configured in state or use default one
                if (toState.data.pageTitle) {
                    titleKey = toState.data.pageTitle;
                    $log.debug(toState);
                }

                if(titleKey !== 'global.title'){
                    $translate(titleKey).then(function (title) {
                        if(titleKey !== 'CIFRA 2'){
                          $rootScope.navigatoretitle.unshift({title:title,titleKey:titleKey,link:link});
                        }
                    });
                }

                return $rootScope.navigatoretitle;
            }
        };
    });