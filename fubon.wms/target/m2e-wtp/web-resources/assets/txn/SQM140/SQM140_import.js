/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM140_importController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM140_importController";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
//	$scope.QTN_LIST = [{'LABEL':'投資/保險', 'DATA': 'WMS01'},{'LABEL':'理專', 'DATA': 'WMS02'},{'LABEL':'開戶', 'DATA': 'WMS03'},{'LABEL':'櫃檯', 'DATA': 'WMS04'},{'LABEL':'簡訊', 'DATA': 'WMS05'}];
	getParameter.XML(["SQM.QTN_TYPE"], function(totas) {
		if (totas) {
			$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
		}
	});
	/*** 可示範圍  JACKY共用版  START ***/
	
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	//設定回傳時間
    	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.eCreDate,'yyyyMMdd');
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };
    
    $scope.curDate = new Date();
    
    $scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};	
	
    $scope.init = function(){
		$scope.inputVO = {
				sCreDate: $scope.curDate,
				eCreDate: $scope.curDate,
				branch_nbr: '',	   //分行	
				qth_type: '',
				emp_id: '',					
				cust_id: ''					
    	};
		
		$scope.dateChange();
    };
    
    $scope.init();
	//資料查詢
	$scope.query = function() {	
		
		$scope.sendRecv("SQM140", "queryImportData", "com.systex.jbranch.app.server.fps.sqm140.SQM140ImportInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.totalList.length == 0) {
							$scope.paramList = [];
							$scope.totalData = [];
							$scope.outputVO={};
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.totalData = tota[0].body.totalList;
						angular.forEach($scope.totalData, function(row, index, objs){
							row.ROWNUM=index+1;
//							if(row.CUST_ID != null){
//								row.CUST_IDS=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
//							}
						});
						$scope.outputVO = tota[0].body;		
						return;
					}						
		});
		
	};
	
	// date picker
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
	
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	
	
	$scope.select_all = function() {
    	if($scope.inputVO.select_all == 'Y' && $scope.paramList != undefined){
			angular.forEach($scope.paramList, function(row, index, objs){
				row.choice = 'Y';
			});
    	}
    	if($scope.inputVO.select_all == 'N' && $scope.paramList != undefined){
			angular.forEach($scope.paramList, function(row, index, objs){
				row.choice = 'N';
			});
    	}
    }
    
    $scope.select_false = function(choice){
    	if(choice == 'N'){
    		$scope.inputVO.select_all = 'N';
    	}
    }
	
	//資料匯入
	$scope.import = function() {	
		$scope.importList = [];
		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.choice == 'Y'){
				$scope.importList.push($scope.paramList[index]);	
				
			}
		});
		
		$scope.inputVO.importList = $scope.importList;
		$scope.sendRecv("SQM140","importData","com.systex.jbranch.app.server.fps.sqm140.SQM140ImportInputVO",
    			$scope.inputVO,function(tota,isError){
        	if (isError) {
        		$scope.showErrorMsg(tota[0].body.msgData);
        	}
        	if (tota.length > 0) {
        		$scope.showSuccessMsg('ehl_01_common_001');
//        		$scope.closeThisDialog('successful');
        		$scope.query();
        	};
		});
	};

});