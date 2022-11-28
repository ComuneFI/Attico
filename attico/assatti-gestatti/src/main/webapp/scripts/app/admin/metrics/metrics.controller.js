'use strict';

angular.module('cifra2gestattiApp')
    .controller('MetricsController', function ($scope, $rootScope, MonitoringService) {
        $scope.metrics = {};
        $scope.updatingMetrics = true;
        $scope.separator = '.';

        $scope.refresh = function () {
            $scope.updatingMetrics = true;
            MonitoringService.getMetrics().then(function (promise) {
                $scope.metrics = promise;
				MonitoringService.checkHealth().then(function (response) {
	                $scope.healthData = $scope.transformHealthData(response);
	                $scope.updatingMetrics = false;
	            }, function (response) {
	                $scope.healthData =  $scope.transformHealthData(response.data);
	                $scope.updatingMetrics = false;
	            });
            }, function (promise) {
                $scope.metrics = promise.data;
                $scope.updatingMetrics = false;
            });
        };

        $scope.$watch('metrics', function (newValue) {
            $scope.servicesStats = {};
            $scope.cachesStats = {};
            angular.forEach(newValue.timers, function (value, key) {
                if (key.indexOf('web.rest') !== -1 || key.indexOf('service') !== -1) {
                    $scope.servicesStats[key] = value;
                }

                if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf('.');
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf('.');
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
            });
        });

        $scope.refresh();

        $scope.refreshThreadDumpData = function () {
            MonitoringService.threadDump().then(function (data) {
                $scope.threadDump = data;

                $scope.threadDumpRunnable = 0;
                $scope.threadDumpWaiting = 0;
                $scope.threadDumpTimedWaiting = 0;
                $scope.threadDumpBlocked = 0;

                angular.forEach(data, function (value) {
                    if (value.threadState === 'RUNNABLE') {
                        $scope.threadDumpRunnable += 1;
                    } else if (value.threadState === 'WAITING') {
                        $scope.threadDumpWaiting += 1;
                    } else if (value.threadState === 'TIMED_WAITING') {
                        $scope.threadDumpTimedWaiting += 1;
                    } else if (value.threadState === 'BLOCKED') {
                        $scope.threadDumpBlocked += 1;
                    }
                });

                $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
                    $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

            });
        };

        $scope.getLabelClass = function (statusState) {
            if (statusState === 'RUNNABLE') {
                return 'label-success';
            } else if (statusState === 'WAITING') {
                return 'label-info';
            } else if (statusState === 'TIMED_WAITING') {
                return 'label-warning';
            } else if (statusState === 'BLOCKED') {
                return 'label-danger';
            }else if (statusState === 'UP') {
                return 'label-success';
            } else {
                return 'label-danger';
            }
        };


        $scope.transformHealthData = function (data) {
            var response = [];
            $scope.flattenHealthData(response, null, data);
            return response;
        };

        $scope.flattenHealthData = function (result, path, data) {
            angular.forEach(data, function (value, key) {
                if ($scope.isHealthObject(value)) {
                    if ($scope.hasSubSystem(value)) {
                        $scope.addHealthObject(result, false, value, $scope.getModuleName(path, key));
                        $scope.flattenHealthData(result, $scope.getModuleName(path, key), value);
                    } else {
                        $scope.addHealthObject(result, true, value, $scope.getModuleName(path, key));
                    }
                }
            });
            return result;
        };

        $scope.getModuleName = function (path, name) {
            var result;
            if (path && name) {
                result = path + $scope.separator + name;
            }  else if (path) {
                result = path;
            } else if (name) {
                result = name;
            } else {
                result = '';
            }
            return result;
        };


        $scope.showHealth = function (health) {
            $scope.currentHealth = health;
            $('#showHealthModal').modal('show');
        };

        $scope.addHealthObject = function (result, isLeaf, healthObject, name) {

            var healthData = {
                'name': name
            };
            var details = {};
            var hasDetails = false;

            angular.forEach(healthObject, function (value, key) {
                if (key === 'status' || key === 'error') {
                    healthData[key] = value;
                } else {
                    if (!$scope.isHealthObject(value)) {
                        details[key] = value;
                        hasDetails = true;
                    }
                }
            });

            // Add the of the details
            if (hasDetails) {
                angular.extend(healthData, { 'details': details});
            }

            // Only add nodes if they provide additional information
            if (isLeaf || hasDetails || healthData.error) {
                result.push(healthData);
            }
            return healthData;
        };

        $scope.hasSubSystem = function (healthObject) {
            var result = false;
            angular.forEach(healthObject, function (value) {
                if (value && value.status) {
                    result = true;
                }
            });
            return result;
        };

        $scope.isHealthObject = function (healthObject) {
            var result = false;
            angular.forEach(healthObject, function (value, key) {
                if (key === 'status') {
                    result = true;
                }
            });
            return result;
        };

        $scope.baseName = function (name) {
            if (name) {
              var split = name.split('.');
              return split[0];
            }
        };

        $scope.subSystemName = function (name) {
            if (name) {
              var split = name.split('.');
              split.splice(0, 1);
              var remainder = split.join('.');
              return remainder ? ' - ' + remainder : '';
            }
        };
    });
