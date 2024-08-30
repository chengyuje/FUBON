/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS422_REVIEWController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS422_REVIEWController";
	
    // filter
	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	// ===
	
	$scope.init = function(){
    	$scope.inputVO.actStartDate = $scope.row.ACT_START_DATE;
    	$scope.inputVO.actEndDate = $scope.row.ACT_END_DATE;
    	$scope.inputVO.outCustID1 = $scope.row.OUT_CUST_ID1;
    	$scope.inputVO.inCustID1 = $scope.row.IN_CUST_ID1;
    	$scope.inputVO.inCustID2 = $scope.row.IN_CUST_ID2;
    	
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS422'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
    };
    
    $scope.init();
    
    $scope.updateFlagR = function (row){
		row.UPDATE_FLAG_R = 'Y';			
	}
    
	//資料查詢
	$scope.queryR = function() {	
		$scope.sendRecv("PMS422", "queryDetailV2", "com.systex.jbranch.app.server.fps.pms422.PMS422InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.paramList = tota[0].body.dtlList;
				$scope.totalData = tota[0].body.dtlList;
				$scope.outputVO = tota[0].body;		
				return;
			}						
		});
	};
	
	$scope.queryR();
	
	//資料儲存
	$scope.saveR = function() {	
		
		$scope.updateListR = [];
		var checkData = true;

		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG_R == 'Y') {
				//debugger
				if (!row.NOTE2 || 
					!row.NOTE3 || 
					!row.HR_ATTR ||
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE) || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					//rowIndex = row.SEQ;
					checkData = false; 
					return;
				};
				$scope.updateListR.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的查證方式、資金來源/帳戶關係、具體原因/用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
        $scope.sendRecv("PMS422", "saveR", "com.systex.jbranch.app.server.fps.pms422.PMS422InputVO", {editedList: $scope.updateListR}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
//                $scope.queryR();
            }
        });
	};
	
});