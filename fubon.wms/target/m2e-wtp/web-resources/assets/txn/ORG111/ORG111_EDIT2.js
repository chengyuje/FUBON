/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('ORG111_EDIT2Controller', 
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "ORG111_EDIT2Controller";

    	var connect = $scope.connector('get','ORG110_tnsfData');
    	
        $scope.init = function(){      	
        	if($scope.row){
        		$scope.isUpdate = true
        	} 	
        	$scope.inputVO = {
        			EMP_ID:$scope.row.EMP_ID,
        			EMP_NAME:$scope.row.EMP_NAME,
        			ROLE_ID:'',
            		paramType: $scope.row.PARAM_TYPE,   	
            };
        	if($scope.row.IS_PRIMARY_ROLE === "Y"){
        		$scope.inputVO.isPrimaryRole = "N";
        	}
        };
        
        $scope.init();
              
		$scope.bran = function(){
			$scope.sendRecv("ORG111", "empRo", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", {'EMP_ID':$scope.row.EMP_ID},
				function(totas, isError) {
	            	if (isError) {
	            		$scope.showErrorMsg(totas[0].body.msgData);
	            	}
	            	if (totas.length > 0) {
	            		$scope.mappingSet['ro'] = [];
	            		angular.forEach(totas[0].body.rolist, function(row, index, objs){	            				            			
	            			$scope.mappingSet['ro'].push({LABEL: row.NAME, DATA: row.ROLE_ID});
	        			});
	            	}
			});
	    }
		
		$scope.bran();
		
        $scope.save = function(){
        	$scope.sendRecv("ORG111", "addRole", "com.systex.jbranch.app.server.fps.org111.ORG111OrgRoleVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('新增角色成功');
		       			$scope.closeThisDialog('successful');
	                }
        	});
        }       
});
