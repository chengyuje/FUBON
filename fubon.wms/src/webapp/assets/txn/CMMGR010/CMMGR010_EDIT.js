/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR010_EditController', 
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR010_EditController";
        
        $scope.tmpstr = unfaker('%20%20%20%20%20%20%20p%3F%3F%3F%3F%3Fa%3F%3F%3F%3F%3F%3Fs%3F%3F%3F%3F%3F%3F%3Fs%23%23w%21o%20%20%20%20%20%20rd');
        
        $scope.init = function(){
            $scope.row = $scope.row || {};
            
        	$scope.inputVO = {
        			hostid: $scope.row.HOSTID,
        			ip: $scope.row.IP,
        			port: $scope.row.PORT,
        			username: $scope.row.USERNAME,
        			password: $scope.row.PASSWORD,
        			currentpageindex: $scope.row.CURRENTPAGEINDEX,
        			pagecnt: $scope.row.PAGECNT
            };
        };
        
        $scope.init();
        
        $scope.save = function(){
        	
        	if($scope.parameterTypeEditForm.$valid == false){
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$("#errorMessage").empty();
        		$("#errorMessage").append("欄位檢核錯誤");
        		return;
        	}
        	console.log('save='+$scope.row.HOSTID);
        	$scope.inputVO.type = 'Creat';
        	if($scope.row.HOSTID != undefined){
        		$scope.inputVO.type = 'Update';
        	}
        	$scope.sendRecv("CMMGR010", "operation", "com.systex.jbranch.app.server.fps.cmmgr010.CMMGR010InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showMsg('儲存成功');
		       			$scope.closeThisDialog('successful');
	                 };
	            }
        	);
        }
        
        $scope.del = function(){

    		$scope.inputVO.type = 'Delete';
    		$scope.sendRecv("CMMGR010", "operation", "com.systex.jbranch.app.server.fps.cmmgr010.CMMGR010InputVO", $scope.inputVO,
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
        }
    }
);
