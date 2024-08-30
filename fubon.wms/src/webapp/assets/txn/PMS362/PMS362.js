/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS362Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS362Controller";
		
		$scope.mappingSet['srchType'] = [];
        $scope.mappingSet['srchType'].push({LABEL:'Step1:分行收益核實版', DATA:'BR_PROFIT'}, {LABEL:'Step2:分行銷量核實版', DATA:'BR_SALES'}, {LABEL:'Step3:專員業績戰報資料', DATA:'AO_PROFIT'});
        $scope.srchType =  $scope.mappingSet['srchType'];

        
        
        $scope.init = function(){			
			$scope.inputVO = {
					srchType: '',
					sTimes : undefined,									
					eTimes : undefined,
        	};
			$scope.outputVO = [];
			$scope.resultList = [];
		};
	
		$scope.init();
				
		//選擇上傳檔案
		$scope.uploadFile = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS362/PMS362_EXPORT.html',
				className: 'PMS362_EXPORT',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value.success == 'success'){
					$scope.init();
					$scope.inputVO.srchType = data.value.srchType;
//					$scope.inputVO.sTimes = data.value.time;
//					$scope.inputVO.time = data.value.time;
					$scope.queryData();
				}
			});
		};
		
		
		/** date picker **/
		/**
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eTimes || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sTimes || $scope.minDate;
		};
		 **/
		
		
		//下載範例
		$scope.downloadExm = function() {			
			//資料類型未選擇
			if($scope.inputVO.srchType == undefined || $scope.inputVO.srchType == ''){
				$scope.showErrorMsg('請選擇資料類型');
				return;
			}
			
			$scope.sendRecv("PMS362", "getExample", "com.systex.jbranch.app.server.fps.pms362.PMS362InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg("下載成功");
						return;
					}
			});
		};
		
		/** 查詢資料 **/
		$scope.queryData = function(active){
			
			//未選擇資料類型
			if($scope.inputVO.srchType == '' || $scope.inputVO.srchType == undefined){
				$scope.showErrorMsg('須選擇資料類型');
				return;
			}
			/*
			//沒選擇結束年月，視為選擇當下年月前一個月
			if($scope.inputVO.eTimes == '' || $scope.inputVO.eTimes == undefined){
				$scope.inputVO.eTimes = String(new Date().getFullYear())+String(new Date().getMonth());
			}
			*/
			$scope.sendRecv("PMS362", "queryData", "com.systex.jbranch.app.server.fps.pms362.PMS362InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.outputVO = tota[0].body;														
							$scope.resultList = tota[0].body.resultList;
							if($scope.resultList.length == 0){
								$scope.showMsg('ehl_01_common_009');
								return;
							}
							return;
						}						
			});
	};

});
