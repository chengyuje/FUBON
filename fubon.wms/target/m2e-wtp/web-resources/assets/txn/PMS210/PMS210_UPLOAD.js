/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_UPLOADController";

		$scope.init = function() {
			$scope.inputVO = {
				userID  :projInfoService.getUserID()
			};
		};
		$scope.init();
	    
	  //import xls
		$scope.importfile = function() {
			$scope.inputVO.yearMon = $scope.yearMon;
			// 處理bsPrize的上傳功能
			if ($scope.from == 'bsPrize') {
				$scope.sendRecv("PMS210", "addBsPrize","com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",$scope.inputVO, 
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callBsPrize';
								$scope.callStored();
							}
						});
			}
			
			//處理spMar的上傳功能
			if($scope.from == 'spMar'){
				$scope.sendRecv("PMS210", "addSpMar", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callSpMar';
								$scope.callStored();
							}
						});
			}
			
			//處理bsAum的上傳功能
			if($scope.from == 'bsAum'){
				$scope.sendRecv("PMS210", "addBsAum", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callBsAum';
								$scope.callStored();
							}
						});
			}
			
			//處理spBouns的上傳功能
			if($scope.from == 'spBouns'){
				$scope.sendRecv("PMS210", "addSpBouns", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callSpBouns';
								$scope.callStored();
							}
						});
			}
			
			//處理spSalBen的上傳功能
			if($scope.from == 'spSalBen'){
				$scope.sendRecv("PMS210", "addSpSalBen", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callSpSalBen';
								$scope.callStored();
							}
						});
			}
			
			//處理newBounty的上傳功能
			if($scope.from == 'newBounty'){
				$scope.sendRecv("PMS210", "addNewBounty", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callNewBounty';
								$scope.callStored();
							}
						});
			}
			
			//處理conNum的上傳功能
			if($scope.from == 'conNum'){
				$scope.sendRecv("PMS210", "addConNum", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								return;
							}else{
								$scope.workFlow = 'callConNum';
								$scope.callStored();
							}
						});
			}
			
	};
	  //文件名
	    $scope.uploadFinshed = function(name,rname) {
        		$scope.inputVO.fileName = name;
            //	$scope.inputVO.realfileName = rname;
        };
        
        
        //有數據傳入按鈕才生效
        $scope.checkName = function(){
        	var check = false;
        	if($scope.inputVO.fileName != undefined && $scope.inputVO.fileName != ''){
        		check = true;
        	}
        	return check;
        }
        
        //調用存儲過程
        $scope.callStored = function(){
        	$scope.sendRecv("PMS210",$scope.workFlow, "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
        }
	    
});