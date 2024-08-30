/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS420Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS420Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<6; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
     
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate!=''){
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/			
		
		$scope.init = function(){
			$scope.inputVO = {	
					sCreDate :'',
					dataMonth: '',					
					rc_id: '',
					op_id: '' ,
					br_id: '',
					emp_id: ''
        	};
			$scope.curDate = new Date();
			$scope.outputVO={};
			$scope.paramList = [];
			//把鎖定清掉
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			if($scope.inputVO.sCreDate==''||$scope.inputVO.sCreDate==undefined){
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
        		return;
        	}
			$scope.sendRecv("PMS420", "queryData", "com.systex.jbranch.app.server.fps.pms420.PMS420InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger;
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList=[];
								return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		// 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID   : row.CUST_ID,
    				CUST_NAME : row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		};
		
		$scope.initQuery = function(){
			$scope.sendRecv("PMS420", "initQuery", "com.systex.jbranch.app.server.fps.pms420.PMS420InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					debugger;
					if(tota[0].body.countYN == 'N') {
						$scope.showMsg("上個月自取對帳單名單批次尚未執行完畢，請再往前一個月查詢。");
            		}
				}						
			});
		};
		$scope.initQuery();
});
