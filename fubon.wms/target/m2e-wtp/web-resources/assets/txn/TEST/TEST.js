'use strict';
eSoafApp.controller('TESTController', 
    function($rootScope, $scope, sysInfoService, $controller, ngDialog) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "TESTController";
        
        $scope.text = [];
        $scope.flag = false;
        $scope.init = function(){
        	$scope.text = [
        	               {A:"E001",B:"A商品",C:10},
        	               {A:"E002",B:"B商品",C:"-0.001"},
        	               {A:"E005",B:"F商品",C:-109.09},
        	               {A:"E006",B:"G商品",C:11},
        	               {A:"E007",B:"H商品",C:3319}
        	               ]
        }
        $scope.init();
        
        $scope.sw = function(){
        	if($scope.flag){
        		$scope.text = [
            	               {A:"E001",B:"A商品",C:10},
            	               {A:"E002",B:"B商品",C:"-0.001"},
            	               {A:"E005",B:"F商品",C:-109.09},
            	               {A:"E006",B:"G商品",C:11},
            	               {A:"E007",B:"H商品",C:3319}
            	               ]
        	}else{
        		$scope.text = [
            	               {A:"E001",B:"A商品",C:-120,D:"-0.022"},
            	               {A:"E002",B:"B商品",C:"90.001"},
            	               {A:"E005",B:"F商品",C:1209.209},
            	               {A:"E006",B:"G商品",C:8811},
            	               {A:"E007",B:"H商品",C:-123000}
            	               ]
        	}   
        	$scope.flag = $scope.flag?false:true;
        }
        
    }
);