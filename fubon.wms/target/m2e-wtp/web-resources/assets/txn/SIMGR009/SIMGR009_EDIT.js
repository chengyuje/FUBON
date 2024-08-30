/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('SIMGR009EditController', 
    function($scope, $controller, socketService, alerts) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SIMGR009EditController";
        $scope.init = function(){
        	$scope.isNew = $scope.row.PARAM_CODE == undefined;
        	$scope.isUpdate = $scope.isNew == false;
            
        	$scope.inputVO = {
            		paramType: $scope.row.PARAM_TYPE,
            		ptypeName: $scope.row.PTYPE_NAME,
            		ptypeDesc: $scope.row.PTYPE_DESC,
            		roleMaintain: $scope.row.ROLE_MAINTAIN,
            		ptypeRange: $scope.row.PTYPE_RANGE,
            		ptypeBuss: $scope.row.PTYPE_BUSS,
            		worksType: $scope.row.WORKS_TYPE,
            		csvType: $scope.row.CSV_TYPE,
            		validateType: $scope.row.VALIDATE_TYPE,
            		maxInput: $scope.row.MAX_INPUT || 0,
            		validateRange: $scope.row.VALIDATE_RANGE
            };
//        	console.log('inputVO='+JSON.stringify($scope.inputVO));
        };
        
        $scope.init();
        
        $scope.save = function(){
        	
        	$scope.parameterTypeEditForm.PARAM_CODE.$setValidity('exist', true);
        	
        	if($scope.parameterTypeEditForm.$valid == false){
        		
        		return;
        	}
//        	console.log('$scope.row.PARAM_CODE='+$scope.row.PARAM_CODE);
//        	console.log('fun='+$scope.paramCodeExist($scope.row.PARAM_CODE));
        	if($scope.isNew && $scope.paramCodeExist($scope.row.PARAM_CODE)){
        		$scope.parameterTypeEditForm.PARAM_CODE.$setValidity('exist', false);
        		return;
        	}
//        	console.log('isNew='+$scope.isNew + ',isUpdate='+$scope.isUpdate);
        	$scope.row.PARAM_STATUS = $scope.row.PARAM_STATUS || 0;
        	if($scope.row.PARAM_STATUS == '0'){
        		
        		$scope.row.PARAM_STATUS = '2';//修改
        		if($scope.isNew){
        			$scope.row.PARAM_STATUS = '3';//新增
        		}
        	}
        	
        	$scope.closeThisDialog($scope.row);
        }
        
        $scope.paramCodeExist = function(paramCode){
//        	console.log('paramCode='+paramCode);
        	var isExist = false;
        	angular.forEach($scope.adgParameter, function(row){
//        		console.log('row.PARAM_CODE='+row.PARAM_CODE);
        		if(isExist == false && row.PARAM_CODE == paramCode){
//        			console.log('equal');
        			isExist = true;
        		}
        	});
        	return isExist;
        }
    }
);
