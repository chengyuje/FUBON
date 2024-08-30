/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM120_sendController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM120_sendController";
	

    $scope.inputVO = angular.copy($scope.editInputVO);
    
    //送出人選項
	$scope.getSendRole = function() {	
		
		$scope.sendRecv("SQM120","getSendRole","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
				$scope.inputVO,function(tota,isError){
				if(tota[0].body.totalList.length == 0) {
					$scope.roleList = [];
					$scope.empData = [];
					$scope.outputVO={};
					$scope.showMsg("ehl_01_common_009");
					
	    			return;
	    		}
				$scope.roleList = tota[0].body.resultList;
				angular.forEach($scope.roleList, function(row, index, objs){
					if(index == 0){
						$scope.inputVO.owner_role_id = row.DATA;
					}
				});	
//				$scope.roleList.push(tota[0].body.resultList);
    			$scope.empData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;		
				return;
    	});
		
	};
	$scope.getSendRole();
	//
	$scope.roleChange = function() {		
		var role_id = $scope.inputVO.owner_role_id;
		$scope.empList = []; 
		angular.forEach($scope.empData, function(row, index, objs){
			if(role_id == row.ROLE_ID){
				var emp = {'LABEL':row.EMP_NAME,'DATA':row.EMP_ID};	
			    $scope.empList.push(emp);
			    if(index == 0){
					$scope.inputVO.owner_emp_id = row.EMP_ID;
				}    
			}
		});	
	};
    
	//送出
	$scope.send = function() {	
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		debugger
		$scope.sendRecv("SQM120","save","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
				$scope.inputVO,function(tota,isError){
    			if(isError){
    				$scope.showErrorMsg(tota[0].body.msgData);
    			}
               	if (tota.length > 0) {
//	        		$scope.showSuccessMsg('ehl_01_common_002');
	        		$scope.closeThisDialog('successful');
            	};
    	});
		
	};

});