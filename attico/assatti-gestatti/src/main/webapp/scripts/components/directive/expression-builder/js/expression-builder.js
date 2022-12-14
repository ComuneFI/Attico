angular.module('expression-builder', [ ])

	.controller('ExpressionBuilderController', function($scope, $log){
		
		$scope.salvaCondizione=function(){
			$log.debug($scope.conditions);
			$scope.conditionStrings = angular.toJson($scope.conditions);
		};
		
		$scope.$watch('conditions', function() {
	    	   $scope.salvaCondizione();
	    }, true);
		
		$scope.addCondition=function(){
			$scope.conditions.push({});
		}
		
		$scope.addGroup=function(){
			$scope.conditions.push({conditions:[]});
		}
		
		$scope.remove=function(c){
			var idx = $scope.conditions.indexOf(c);
			
			$scope.removeAt(idx);
		}
		
		$scope.removeAt=function(idx){
			$scope.conditions.splice(idx,1);
		}
		
		$scope.getLeftOperands=function($viewValue){
			return ($scope.leftOperandProvider)
				? $scope.leftOperandProvider($viewValue)
				: [];				
		}
		
		$scope.getRightOperands=function($viewValue){
			return ($scope.rightOperandProvider)
				? $scope.rightOperandProvider($viewValue)
				: [];		
		}
	})
	
	.directive('expressionBuilder', function($compile){
		
		return {
			restrict:'EA',
			controller:'ExpressionBuilderController',			
			templateUrl: 'scripts/components/directive/expression-builder/templates/expressionbuilder.html',
			scope: {
				
				conditionStrings: '=',
				conditions: '=',
				booleanOperators: '=?',
				comparisonOperators: '=?',
				leftOperands: '=?',
				leftOperandProvider: '&',
				rightOperands: '=?',
				rightOperandProvider: '&'				
			},
			compile: function (element) {
				var contents = element.contents().remove();
				var contentsLinker;

				return function (scope, iElement) {
					if (angular.isUndefined(contentsLinker)) {
						contentsLinker = $compile(contents);
					}

					contentsLinker(scope, function (clonedElement) {
						iElement.append(clonedElement);
					});
					
					if(scope.booleanOperators === undefined)
						scope.booleanOperators = ['AND', 'OR' ];
					
					if(scope.comparisonOperators === undefined)
						scope.comparisonOperators = [
							{value:'==',label:'Uguale a' },
							{value:'.length > 0',label:'Vuoto'}
					 	];
					
					if(scope.left)
					
					if(scope.leftOperandProvider)
						scope.leftOperandProvider = scope.leftOperandProvider()
					
					if(scope.rightOperandProvider)
						scope.rightOperandProvider = scope.rightOperandProvider()
				};
			}
		};
	})
	