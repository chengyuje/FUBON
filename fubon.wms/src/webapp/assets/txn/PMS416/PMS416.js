'use strict';
eSoafApp.controller('PMS416Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS416Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});

	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	
	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth()+1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 13; i++){
    	mm = mm -1;
    	if(mm == 0){
    		mm = 12;
    		yr = yr-1;
    	}
    	if(mm < 10)
    		strmm = '0' + mm;
    	else
    		strmm = mm;        		
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA: yr + '' + strmm
    	});        
    }

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
	   if($scope.inputVO.sCreDate != ''){
	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   } else {   
		   $scope.inputVO.sCreDate = '201701';
		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;    
		   $scope.inputVO.sCreDate = '';
	   } 
    	$scope.inputVO.dataMonth = $scope.inputVO.sCreDate; 
    }; 
    
	$scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());
		
		$scope.inputVO = {					
			sCreDate         : undefined,				
			rc_id            : '',
			op_id            : '' ,	
			region_center_id : '',
			branch_nbr       : '' ,
			branch_area_id   : '',
			aoCode           : '',
			exportList       :[], 
			dataMon          : '',
			memLoginFlag     : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.curDate = new Date();
		$scope.dateChange();
	    
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;

        $scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.test);
        
        $scope.sendRecv("PMS416", "chkMaintenance", "com.systex.jbranch.app.server.fps.pms416.PMS416InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.originalList = [];
	}
	$scope.inquireInit();
	
	$scope.query = function(){
		if($scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == null) {
    		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
    		return;
    	}
		$scope.inputVO.dataMon = angular.copy($scope.inputVO.sCreDate);
		
		$scope.sendRecv("PMS416", "queryData", "com.systex.jbranch.app.server.fps.pms416.PMS416InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.resultList = tota[0].body.resultList;
				$scope.exportList =	tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}						
		});
	};
	
	$scope.exportRPT = function(){			
		angular.copy($scope.exportList,$scope.inputVO.exportList);
		$scope.sendRecv("PMS416", "export", "com.systex.jbranch.app.server.fps.pms416.PMS416InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			if (tota.length > 0) {
        		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
	
	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
	
	/** 添加 User 可編輯的 Input 欄位，進而判斷與原欄位差異者，更新資料 **/
    $scope.addInputCol = each => {
        each.HR_ATTR_INPUT = each.HR_ATTR;
        each.NOTE_INPUT = each.NOTE;
        each.NOTE2_INPUT = each.NOTE2;
    };
    
	/**
     * 儲存使用者編輯的資料
     *
     * * 主管確認欄位（Ａ）、專員代為操作（Ｂ）、備註欄（Ｃ）。
     *      1. Ａ、B、C 三者皆有或皆無才能執行儲存功能。有任何一項有值，其他無值將會出現警示訊息。
     *      2. A、B、C 三者有任一異動則執行更新邏輯。
     *
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		
		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					!row.NOTE2 ||
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y')) // // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					checkData = false; 
					return;
				};
				
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的專員未有勸誘客戶提高風險屬性、查證方式(若為其它，請補充查證方式；若為電訪，請填寫12碼電訪錄音編號)、具體原因/用途，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS416", "save", 'com.systex.jbranch.app.server.fps.pms416.PMS416InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
            }
        });
    };
});
