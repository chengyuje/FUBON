/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR002_EditController',
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR002_EditController";
        
        $scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true;
        	}
            $scope.row = $scope.row || {};
            
        	$scope.inputVO = {
        			priID: $scope.row.PRIVILEGEID,
        			priName: $scope.row.NAME
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
        	console.log('save='+$scope.row.PRIVILEGEID);
        	$scope.inputVO.type="Creat";
        	if($scope.row.PRIVILEGEID != undefined){
        		$scope.inputVO.type="Update";
        	}
        	$scope.sendRecv("CMMGR002", "addGroupName", "com.systex.jbranch.app.server.fps.cmmgr002.CMMGR002InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.closeThisDialog('successful');
	                 };
	            }
        	);
        };
        
        $scope.del = function(){
    		$scope.sendRecv("CMMGR002", "deleteGroupName", "com.systex.jbranch.app.server.fps.cmmgr002.CMMGR002InputVO", $scope.inputVO,
	    		function(totas, isError) {
    				if (isError) {
		               	$scope.showErrorMsgInDialog(totas.body.msgData);
		            	return;
    				}
    				if (totas.length > 0) {
    					$scope.closeThisDialog('successful');
    				};
    			}
	        );
        };
        
    }
);
