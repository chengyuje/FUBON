/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR001_EditController',
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR001_EditController";
        
        $scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true
        	}
            $scope.row = $scope.row || {};
            
        	$scope.inputVO = {
        			roleID: $scope.row.ROLEID,
        			roleName: $scope.row.NAME,
        			approvalLevel: $scope.row.EXTEND1,
        			extend3: $scope.row.EXTEND3,
        			mainPage: $scope.row.EXTEND2
            };
        	console.log('inputVO='+JSON.stringify($scope.inputVO));
        };
        
        $scope.init();
        
        $scope.save = function(){
        	if($scope.parameterTypeEditForm.$invalid){
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.isUpdate){
        		console.log('save='+$scope.row.ROLEID);
            	$scope.sendRecv("CMMGR001", "updateRole", "com.systex.jbranch.app.server.fps.CMMGR001InputVO", $scope.inputVO,
        			function(totas, isError) {
    	                if (isError) {
    	                	$scope.showErrorMsgInDialog(totas.body.msgData);
    	                    return;
    	                }
    	                if (totas.length > 0) {
    	                	$scope.showMsg('儲存成功');
    	                	$scope.closeThisDialog('successful');
    	                }
    	            }
            	);
        	} else {
        		$scope.sendRecv("CMMGR001", "createRole", "com.systex.jbranch.app.server.fps.CMMGR001InputVO", $scope.inputVO,
            		function(totas, isError) {
        				if (isError) {
        					$scope.showErrorMsgInDialog(totas.body.msgData);
        	                   return;
        				}
        				if (totas.length > 0) {
        					$scope.showMsg('新增成功');
        					$scope.closeThisDialog('successful');
        				}
        			}
        		);
        	}
        }
        
        $scope.del = function(){
        	if ($scope.row.ROLEID == undefined){
        		$scope.showErrorMsgInDialog('無法刪除');
        		return;
        	}
    		$scope.sendRecv("CMMGR001", "deleteRole", "com.systex.jbranch.app.server.fps.CMMGR001InputVO", $scope.inputVO,
	    		function(totas, isError) {
    				if (isError) {
		               	$scope.showErrorMsgInDialog(totas.body.msgData);
		            	return;
    				}
    				if (totas.length > 0) {
    					$scope.showMsg('刪除成功');
    					$scope.closeThisDialog('successful');
    				};
    			}
	        );
        };
        
    }
);
