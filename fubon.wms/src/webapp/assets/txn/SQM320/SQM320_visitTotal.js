
'use strict';
eSoafApp.controller('SQM320_visitTotalController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, $timeout) {
	
	$controller('BaseController', {$scope: $scope});
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.role = sysInfoService.getPriID();
	
	$scope.controllerName = "SQM320_visitTotal";
	$scope.loginBranchID = projInfoService.getBranchID();
	
	//資料查詢
	$scope.getVisitTotal = function() {	
		$scope.sendRecv("SQM320", "getVisitTotal", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.outputVO={};
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
				return;
			}						
		});
		 
	};
	 

	$scope.init = function(){
		var NowDate = new Date();
		
		//設定回傳時間
		$scope.inputVO.reportDate = NowDate;
		$scope.inputVO.memLoginFlag = String(sysInfoService.getMemLoginFlag());

		if ($scope.memLoginFlag.toLowerCase().startsWith('uhrm')) {
			$scope.inputVO.yearQtr = $scope.yearQtr;
			$scope.inputVO.branch_nbr = $scope.branch_nbr;
			$scope.inputVO.memLoginFlag = $scope.memLoginFlag;
			$scope.inputVO.uhrmOP = $scope.uhrmOP;
			$scope.inputVO.uhrmRC = $scope.uhrmRC;
			
			$scope.getVisitTotal();
		} else {
			//可視範圍  觸發 
			$scope.RegionController_getORG($scope.inputVO).then(function(data) {
				$scope.inputVO.yearQtr = $scope.yearQtr;
				$scope.inputVO.branch_nbr = $scope.branch_nbr;
				$scope.inputVO.memLoginFlag = $scope.memLoginFlag;
			
				$scope.getVisitTotal();
			});
		}
		
		$scope.inputVO.funcPage = "SQM320";
		if ($scope.inputVO.memLoginFlag.toLowerCase().startsWith('uhrm') && $scope.inputVO.memLoginFlag != 'UHRM') {
			$scope.inputVO.funcPage += "U";
		} 
	}	
		 
	$scope.init(); 
	 
	// 全行下載
	$scope.exportAll = function() {	
		$scope.sendRecv("SQM320", "exportAll", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {					
		});
	};
	
	$scope.goSQM320 = function (type, emp_id, reviewStatus) {
		var lineData = {'type':type, 'emp_id':emp_id, 'reviewStatus': reviewStatus};
		$scope.closeThisDialog(lineData);
	};
	
	//(手動)覆核
	$scope.review = function(empID){
		$scope.inputVO.emp_id = empID;
		$scope.sendRecv("SQM320", "review", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.reviewerList == null) {
					 $scope.showErrorMsg("無覆核人員，請洽資訊人員處理");
				 }else{
					 $scope.outputVO.reviewerList = tota[0].body.reviewerList;
					 $scope.saveAndSend(); 	
				 }
				 	
				return;
			 }	
		});
	}
	
	//清除覆核
	$scope.reset = function(empID){
		$scope.inputVO.emp_id = empID;
		$scope.sendRecv("SQM320", "reset", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				 $scope.showSuccessMsg("清除成功");
				 $scope.getVisitTotal();
				 return;
			 }	
		});
	}
	
	// 資料儲存並送出
	$scope.saveAndSend = function() {
		var reviewer = $scope.outputVO.reviewerList;
		var emp_id = $scope.inputVO.emp_id;
		var yearQtr = $scope.inputVO.yearQtr;
		
		var dialog = ngDialog.open({
			template : 'assets/txn/' + $scope.inputVO.funcPage + '/' + $scope.inputVO.funcPage + '_send.html',
			className : $scope.inputVO.funcPage,
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.reviewerList = reviewer;
				$scope.emp_id = emp_id;
				$scope.yearQtr = yearQtr;
			} ]
		});
		
		dialog.closePromise.then(function (data) {
			if(data.value != 'cancel') {			
				$scope.getVisitTotal();
			}
		});
		
	};
});