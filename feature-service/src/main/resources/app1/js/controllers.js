'use strict';

/* Controllers */

var mrLaunchApp = angular.module('mrLaunchApp', []);

mrLaunchApp.controller('LaunchCtrl', function($scope) {
    $scope.master = {};

    $scope.update = function(launchParameter) {
        //console("Hello ");
        $scope.master = angular.copy(launchParameter);
    };

    $scope.reset = function() {
        $scope.launchParameter = angular.copy($scope.master);
    };

    $scope.reset();

});