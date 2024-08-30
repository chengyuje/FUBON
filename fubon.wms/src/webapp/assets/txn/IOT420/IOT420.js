/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT420Controller', function($scope, $controller, getParameter, ngDialog) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT420Controller";
	
	//XML參數
	getParameter.XML(["CALLOUT.REVIEW_STATUS", "CALLOUT_STATUS", "CALLOUT.CALL_TYPE", "CALLOUT.TIME"],function(totas){
		if(totas){
			$scope.mappingSet['CALLOUT.REVIEW_STATUS'] = totas.data[totas.key.indexOf('CALLOUT.REVIEW_STATUS')];	// 檢視狀態
			$scope.mappingSet['CALLOUT_STATUS'] = totas.data[totas.key.indexOf('CALLOUT_STATUS')];					// 電訪狀態
			$scope.mappingSet['CALLOUT.CALL_TYPE'] = totas.data[totas.key.indexOf('CALLOUT.CALL_TYPE')];			// 電訪類型
			$scope.mappingSet['CALLOUT.TIME'] = totas.data[totas.key.indexOf('CALLOUT.TIME')];						// 方便連絡時段
		}
	});
	
	// 日期起迄
	$scope.s_apply_dateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    };
    $scope.e_apply_dateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    };
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.s_apply_dateOptions.maxDate = $scope.inputVO.e_apply_date || $scope.maxDate;
		$scope.e_apply_dateOptions.minDate = $scope.inputVO.s_apply_date || $scope.minDate;
	};
	// 日期起迄 END
	
	$scope.clean = function() {
		$scope.inputVO = {};
		$scope.pageControlVO = [];
		$scope.pageControlVO.allChoice = false;
		
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.s_apply_dateOptions.maxDate = undefined;
		$scope.e_apply_dateOptions.minDate = undefined;
	}
	
	$scope.init = function() {
		$scope.clean();
		// 控制凍結視窗
		$scope.freezeControl = 7;
		
		// 派件權限，必需要有在派件人員清單時，才可以點選派件
		$scope.showAssign = false;
		$scope.sendRecv("IOT420", "showAssign", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", {},
		function(tota, isError) {
			if (!isError) {				
				if (tota[0].body.showAssign != undefined) {
					$scope.showAssign = tota[0].body.showAssign;
//					alert($scope.showAssign);
				}
			}
		});
	}
	
    //全選
    $scope.all = function() {
    	if ($scope.pageControlVO.allChoice) {
    		angular.forEach($scope.resultList, function(row) {
    			row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.resultList, function(row) {
    			row.SELECTED = false;
			});
    	}
    };
	
	$scope.query = function() {
		$scope.pageControlVO.allChoice = false;
		$scope.resultList = [];
		$scope.outputVO = [];
		if ($scope.inputVO.cust_id != undefined) {
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();			
		}
		if ($scope.inputVO.case_id != undefined) {
			$scope.inputVO.case_id = $scope.inputVO.case_id.toUpperCase();			
		}
		$scope.sendRecv("IOT420", "inquire", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length > 0) {
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				} else {
					$scope.showMsg("ehl_01_common_009");	//查無資料
					return;
				}
			}
		});
	}
	
	// IOT400 電訪預約/取消申請作業
	$scope.openIOT400 = function(row) {
		var inputVO = row;
		var dialog = ngDialog.open({
			template: 'assets/txn/IOT400/IOT400.html',
			className: 'IOT400',
			controller:['$scope',function($scope){
				$scope.inputVO = inputVO;
				$scope.review = true;
				$scope.fromIOT420 = true;
			}]
		});
		dialog.closePromise.then(function(data){
			if (data.value === 'successful') {
				$scope.query();
			}
		});
	}
	
	// 派件
	$scope.assign = function() {
		var data = $scope.resultList.filter(function(row) {
			return (row.SELECTED == true);
    	});
		if(!data.length) {
    		$scope.showErrorMsg('請勾選一筆以上之案件!');
    		return;
        } else {
        	var assign_seq = [];
        	var flag = true;
	    	angular.forEach($scope.resultList, function(row){
	    		if (flag) {
	    			if (row.SELECTED == true) {
	    				if (row.REVIEW_STATUS == undefined || row.REVIEW_STATUS != 'OK') {
	    					flag = false;
	    					// 勾選一筆以上案件，其中有案件檢視狀態≠檢視完成時，則產生提醒訊息：
	    					//「24XXXXXXXX適合度檢核編碼 需檢視文件完成後，始能執行派件作業!」
	    					$scope.showErrorMsg(row.PREMATCH_SEQ + ' 適合度檢核編碼 需檢視文件完成後，始能執行派件作業!');
	    				}
	    				if (flag) {
	    					assign_seq.push(row.PREMATCH_SEQ);	    					
	    				}
	    			}
	    		}
	    	});
	    	if (!flag) {
	    		return;
	    	}
//	    	alert(JSON.stringify(assign_seq));
	    	var dialog = ngDialog.open({
				template: 'assets/txn/IOT420/IOT420_ASSIGN.html',
				className: 'IOT420',
				controller:['$scope',function($scope){
					$scope.assign_seq = assign_seq;
				}]
			});
			dialog.closePromise.then(function(data){
				if (data.value === 'successful') {
					$scope.query();
				}
			});
        }
	}
	
	$scope.save = function() {
		var data = $scope.resultList.filter(function(row) {
			return (row.SELECTED == true);
    	});
		if(!data.length) {
    		$scope.showErrorMsg('請勾選一筆以上之案件!');
    		return;
        }
		
		$scope.sendRecv("IOT420", "save", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", {'resultList': data},
		function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
				$scope.query();
			}
		});
	}
	
	// 匯出 Csv
    $scope.export = function() {
    	if ($scope.resultList.length == 0) {
    		$scope.showErrorMsg('查詢結果至少需有一筆');
    		return;
    	}
    	$scope.inputVO.resultList = $scope.resultList;
    	$scope.sendRecv("IOT420", "export", "com.systex.jbranch.app.server.fps.iot420.IOT420InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('匯出成功');
			}
		});
    }
    
	$scope.init();
});