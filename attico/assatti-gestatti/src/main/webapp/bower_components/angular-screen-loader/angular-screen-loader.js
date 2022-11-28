/**
 * - Custom Spinner Loader 
 *   -------------------------
 *   Gianluca Stefanelli 
 *   Links SPA
 */

angular.module('angularScreenLoader',[])
.factory('httpLoadingInterceptor', ['$q', '$rootScope', function ($q, $rootScope) {
    var reqIteration = 0;
    return {
        request: function (config) {
            if(reqIteration === 0){
          		$rootScope.$broadcast('globalLoadingStart');
            }
            reqIteration++;
            return config || $q.when(config);
        },
        response : function(config){
          reqIteration--;
          if(!reqIteration){
          	$rootScope.$broadcast('globalLoadingEnd');
          }
          return config || $q.when(config);
        },
        responseError: function (response) {
            if (!(--reqIteration)) {
            	$rootScope.$broadcast('globalLoadingEnd');
            }
            return $q.reject(response);
        }
    };
}])
.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('httpLoadingInterceptor');
}])
.directive('screenLoader', function(){
  return {
    restrict: 'E',
    replace: true,
    template: '<div><style>.ui-loader-background {width:100%;height:100%;top:0;padding: 0;margin: 0;background: rgba(0, 0, 0, 0.3);display:none;position: fixed;z-index:9999999;background-image: url("assets/images/spinner.gif");background-position: center;background-repeat: no-repeat;}</style><div class="ui-loader-background"> </div></div>',
    link:function(scope,element){
     
     // angular.element(element).addClass('ui-loader-background');
      $('html').append("<div class='ui-loader-background'> </div>");
      scope.$on('globalLoadingStart',function(){
        $('.ui-loader-background').show()
        //angular.element(element).toggleClass('ui-loader-background');
      });
      
      scope.$on('globalLoadingEnd',function(){
        $('.ui-loader-background').hide()
        //angular.element(element).toggleClass('ui-loader-background');
      });
    }
  }
})