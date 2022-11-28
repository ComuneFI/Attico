/* globals $ */
'use strict';

angular.module('cifra2gestattiApp').
   directive('validitaForm', function() {
        return {
            restrict: 'E',
             replace: true,
             transclude: true,
                scope: {
                    ngModel : '=',
                    ngDisabled : '=',
             },
             templateUrl: 'scripts/components/form/validita-form.html'
        };
    });

angular.module('cifra2gestattiApp').
   directive('siNoSelect', function() {
        return {
            restrict: 'E',
             replace: true,
             require: '^ngModel',
             transclude: true,
                scope: {
                    ngModel : '=',
                    ngDisabled : '=',
                    ngChange: '&',
                    label:'=',
                    disallowclear:'='
             },
             templateUrl: 'scripts/components/form/sino-select.html',
            link: function postLink(scope, element, attrs) 
            {
            	scope.sinoList = [{valore:true,label:'SI'},{valore:false,label:'NO'}];

            	scope.updateModel = function(item)
                {
                     scope.ngModel = item;
                     scope.ngChange();
                }
            }
        };
    });
