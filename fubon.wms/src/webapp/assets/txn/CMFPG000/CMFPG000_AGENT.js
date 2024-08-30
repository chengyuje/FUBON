/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMFPG000_AgentController', ["$rootScope" , "$scope", "$controller", "socketService", "projInfoService", "sysInfoService",
    function($rootScope , $scope, $controller, socketService, projInfoService , sysInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMFPG000_AgentController";
        
        $scope.loginSourceToken = sessionStorage.CMFPG000_MOBILE_LOGIN ? 'mobile' : undefined;
        
		var index = $scope.userList.map(function(e) { return e.ROLE_NAME;}).indexOf(projInfoService.getRoleName());
		index = index == -1 ? 0 : index;
        $scope.inputVO = {
        	select: $scope.userList[index]
    	};
        
        $scope.foucsButton = function() {
        	$('#savebut').focus();
        };
        
        $scope.save = function() {
        	if(!$scope.inputVO.select){
        		$scope.showErrorMsgInDialog('請選擇角色');
        		return;
        	}
        	
//        	alert(JSON.stringify($scope.inputVO.select));
        	$scope.sendRecv("CMFPG000", "tlron", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO", {
			 	'iptAppUsername':$scope.inputVO.select.EMP_ID,
    			'iptAppUserRole':$scope.inputVO.select.ROLE_ID,  
    			'iptAppUserDeptID':$scope.inputVO.select.DEPT_ID, 
			    'iptAppUserRegioinCenterID':$scope.inputVO.select.REGION_CENTER_ID, 
			    'iptAppUserRegioinCenterName':$scope.inputVO.select.REGION_CENTER_NAME, 
			    'iptAppUserBranchAreaID':$scope.inputVO.select.BRANCH_AREA_ID, 
			    'iptAppUserBranchAreaName':$scope.inputVO.select.BRANCH_AREA_NAME, 
			    'iptAppUserBranchID':$scope.inputVO.select.BRANCH_NBR, 
			    'iptAppUserBranchName':$scope.inputVO.select.BRANCH_NAME, 
			    'iptAppUserIsPrimaryRole':$scope.inputVO.select.IS_PRIMARY_ROLE,
			    'loginSourceToken':$scope.loginSourceToken,
			    'currentUserId':sysInfoService.getCurrentUserId()
		     }, 
		     function(tota, isError) {
				console.log('AGENT-tlron tota=' + tota + ', isError=' + isError);
				if (isError) {
					$scope.showErrorMsg(tota[0].body.msgData);
				} else {
					projInfoService.LoginInfo = tota[0].body;
					console.log(JSON.stringify(projInfoService.LoginInfo));
					debugger;
					$scope.closeThisDialog('successful');
				}
			});
        };
        
}]);