/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS713Controller', function(sysInfoService, $scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS713Controller";
	$scope.init = function() {
		$scope.inputVO = {
				empId : ''
		}
		$scope.resultList = [];
		$scope.csvList = [];
	};
	$scope.init();
	$scope.inquireInit = function(){
		$scope.resultList = [];
		$scope.csvList = [];
	}
	$scope.inquireInit();

	
	// 报表年月初始化方法
	$scope.yMonth = function(){
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() +2;
		var strmm = '';
		var xm = '';
		$scope.mappingSet['timeE'] = [];
		for (var i = 0; i < 12; i++) {
			mm = mm - 1;
			if (mm == 0) {
				mm = 12;
				yr = yr - 1;
			}
			if (mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm;
			$scope.mappingSet['timeE'].push({
				LABEL : yr + '/' + strmm,
				DATA : yr + '' + strmm
			});
		} 
	};
	$scope.yMonth();
	/**
	 * 查詢
	 */
	$scope.inquire = function(){
		if($scope.inputVO.yearMon==''||$scope.inputVO.yearMon==undefined)
		{
    		$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		$scope.sendRecv("PMS713", "query", "com.systex.jbranch.app.server.fps.pms713.PMS713InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {

					$scope.showMsg("ehl_01_common_009");
//        			return;
        		}
				$scope.resultList = [];
				$scope.csvList = [];
				$scope.resultList = tota[0].body.resultList;
				$scope.csvList = tota[0].body.csvList;
				$scope.outputVO = tota[0].body;
				return;
			}else{
				$scope.showBtn = 'none';
			}	
		});
	};
	/**
	 * 匯出
	 */
	$scope.downLoad = function() {
		$scope.sendRecv("PMS713", "downLoad", "com.systex.jbranch.app.server.fps.pms713.PMS713OutputVO", {
			 'csvList': $scope.csvList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	}
	
	/**
	 * 下載
	 */
	$scope.downLoad1 = function() {
		$scope.sendRecv("PMS713", "downLoad1", "com.systex.jbranch.app.server.fps.pms713.PMS713OutputVO", {
			 'csvList': $scope.csvList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	}

    
  //調用存儲過程存excel表
	   $scope.callStored = function() {
			$scope.sendRecv("PMS713", "callStored", "com.systex.jbranch.app.server.fps.pms713.PMS713InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.inquire();
							$scope.showMsg("ehl_01_common_010");
							
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
	    };
    
	/**
	 * 上傳
	 */
	$scope.upload = function(name,rname) {
		if($scope.inputVO.yearMon==''||$scope.inputVO.yearMon==undefined)
		{
    		$scope.showMsg('欄位檢核錯誤:資料月份欄位必填');
    		return;
    	}
		$scope.inputVO.fileName = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$scope.inputVO.userId = projInfoService.getUserID();
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '120px'}).then(function() {
			$scope.sendRecv("PMS713", "upload", "com.systex.jbranch.app.server.fps.pms713.PMS713InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					return;
				}else 
				{
					$scope.callStored();
				}
				
			});
		});
	};
	$scope.del = function(row) {
		$scope.inputVO.delEmpId = row.EMP_ID;
		$confirm({text: '是否刪除' + row.EMP_ID + '的資料'}, {size: '120px'}).then(function() {
			$scope.sendRecv("PMS713", "del", "com.systex.jbranch.app.server.fps.pms713.PMS713InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.inquire();
					$scope.showMsg("ehl_01_common_003");
				}
			});
		});
	}
});
