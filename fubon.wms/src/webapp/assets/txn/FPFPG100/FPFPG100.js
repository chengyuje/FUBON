'use strict';
eSoafApp.controller('FPFPG100Controller', 
    function($rootScope, $scope, sysInfoService, $controller, ngDialog) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "FPFPG100Controller";
        
        /** Initialize **/
        $scope.init = function(){        	
        	$scope.box = {'state':'1', 'size':'48%'};
        }
        $scope.init();
        
        // 行事曆
        $scope.magTAG1= "assets/txn/FPCAL100/FPCAL100.html";
        
        /** Setting Box Size **/
        $scope.Scaling = function(){        	
        	switch($scope.box.state){
        		case '1':
        			$scope.box.state='2';
        			$scope.box.size='100%';
        			break;
        		case '2':
        			$scope.box.state='1';
        			$scope.box.size='48%';
        			break;
        		default:
        			$scope.box.state='1';
        			$scope.box.size='48%';
        	};        	
        }

    }
);