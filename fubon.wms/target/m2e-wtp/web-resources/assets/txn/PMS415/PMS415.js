/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS415Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS415Controller";
		
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
/**==============================================初始化========================================================**/
				
		$scope.init = function(){
			$scope.inputVO = {					
					applyDateS: undefined,
					applyDateE: undefined,	
					region_center_id: '',
					branch_area_id: '' ,
					branch_nbr: '',
					ao_code: '',
					cust_id: '',
					ins_id: ''

        	};
			$scope.curDate = new Date();
			$scope.paramList = [];
			$scope.inputVO.reportDate =  new Date();
			$scope.RegionController_getORG($scope.inputVO);
		};
			
		$scope.init();

		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();	
/**==============================================初始化========================================================**/
		
		
		$scope.dateChange = function(){
			if($scope.inputVO.applyDateS == undefined && $scope.inputVO.applyDateE != undefined){
				$scope.inputVO.reportDate = $filter('date')($scope.inputVO.applyDateE,'yyyyMMdd');
				$scope.RegionController_getORG($scope.inputVO);
			}else{
	        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.applyDateS,'yyyyMMdd');
	        	$scope.RegionController_getORG($scope.inputVO);
			}
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.applyDateE || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.applyDateS || $scope.minDate;
		};
		// date picker end
		
		// 日期相差的天數
		function getDiffDays(applyDateS, applyDateE){
			var diffSeconds = applyDateE.getTime() - applyDateS.getTime(); // 時間差的毫秒数
			var diffDays = Math.floor(diffSeconds/(24*3600*1000));// 轉成相差天數
			return diffDays;
		};
				
		$scope.query = function(){
			var curDate = new Date();
			var pre5Date = new Date(curDate.getTime() - 5 * 24*60*60*1000);
			if($scope.inputVO.applyDateS == undefined && $scope.inputVO.applyDateE == undefined) {
				$scope.inputVO.applyDateS = pre5Date;
				$scope.inputVO.applyDateE = curDate;
				$scope.dateChange();
				$scope.limitDate();
        	}
			
			if($scope.inputVO.applyDateS == undefined || $scope.inputVO.applyDateS == ''
				|| $scope.inputVO.applyDateE == undefined || $scope.inputVO.applyDateE == '') {
				$scope.showErrorMsg('欄位檢核錯誤:要保申請日區間為必要輸入欄位');
				return;
			}
			
			
			if (getDiffDays($scope.inputVO.applyDateS, $scope.inputVO.applyDateE) > 31){
				$scope.inputVO.applyDateE = undefined;
				$scope.showErrorMsg('日期起迄區間不得超過31天');
				return;
			}
			
			if($scope.inputVO.applyDateE < $scope.inputVO.applyDateS){
				//$scope.inputVO.applyDateS = $scope.inputVO.applyDateE;				
				//$scope.dateChange();
				$scope.showErrorMsg('迄日不可小於起日');
				return;				
			}
			$scope.sendRecv("PMS415", "queryData", "com.systex.jbranch.app.server.fps.pms415.PMS415InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.CUST_ID = row.CUST_ID.substring(0, 4) + "****" + row.CUST_ID.substring(8, 10); //隱藏身分證  四碼
							});	
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){	
			$scope.sendRecv("PMS415", "export", "com.systex.jbranch.app.server.fps.pms415.PMS415OutputVO", $scope.outputVO,
					function(tota, isError) {						
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
});

