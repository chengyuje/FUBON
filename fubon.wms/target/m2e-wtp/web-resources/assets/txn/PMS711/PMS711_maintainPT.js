'use strict';
eSoafApp.controller('PMS711_miantainPTController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_miantainPTController";
	//初始化
	$scope.init = function(){
		$scope.inputVO = {
				date_year  : '',
				personType : ''
				
		};
		$scope.flag = '1';
	}
	$scope.init();
	
	/** 查詢 * */
	$scope.query = function() {
		$scope.sendRecv("PMS711","queryPersonType","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.showList.length>0){
							$scope.showList = tota[0].body.showList;
							$scope.newCustRateList = $scope.showList;
							$scope.maxCode = $scope.showList[0].MAX_CODE;
						}
					}else{
						$scope.showMsg("ehl_01_common_024");		//執行失敗
					}
				});
	}
	$scope.query();
	
	//將輸入框中的內容加入newCustRateList中
	$scope.add = function() {
		var PARAM_CODE = $scope.PARAM_CODE;
		var PARAM_NAME = $scope.PARAM_NAME;
		var PARAM_DESC = $scope.PARAM_DESC;
		if (null == PARAM_CODE || "" == PARAM_CODE
				|| null == PARAM_NAME || "" == PARAM_NAME
				|| null == PARAM_DESC || "" == PARAM_DESC) {
			$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
			return;
		}
		for (var i = 0; i < $scope.newCustRateList.length; i++) {
			if ($scope.newCustRateList[i].PARAM_CODE == PARAM_CODE) {
				$scope.showMsg("ehl_01_common_016");  //資料已存在
				return;
			}
		}
		var a = new Object();
		a.PARAM_DESC = PARAM_DESC;
		a.PARAM_CODE = PARAM_CODE;
		a.PARAM_NAME = PARAM_NAME;
		$scope.newCustRateList.push(a);
		$scope.PARAM_CODE = null;
		$scope.PARAM_NAME = null;
		$scope.PARAM_DESC = null;
	}
	//刪除newCustRateList中對應的項
	$scope.del = function(delIndex){
		$scope.newCustRateList.splice(delIndex,1);
	}
	//將表單數據插入或更新到數據庫
	$scope.save = function() {
		console.log("123");
		$scope.inputVO.newCustRateList = $scope.newCustRateList;
		
		$scope.sendRecv("PMS711","updateMaintainPT","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO,function(tota, isError) {
				if(!isError){
					$scope.showMsg("ehl_01_common_002"); //修改成功
					$scope.query();
				}else{
					$scope.showMsg("ehl_01_common_024"); //執行失敗
				}
				return;
			});
	}

	
});