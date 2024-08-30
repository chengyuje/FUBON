/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS423Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS423Controller";
		$controller('PMSRegionController', {$scope: $scope});
        
		$scope.mappingSet['PMS.REPORT_TYPE'] = [];
		// 報表類型固定5種：1.各項目標數(統計計績Code)、2.存款不含台定AUM增減明細、3.投保不含黃金存摺AUM增減明細、4.EIP客戶增減明細、5.LUM房貸增減明細
		$scope.mappingSet['PMS.REPORT_TYPE'].push(
			{LABEL: '各項目標數(統計計績Code)', 	DATA: '1'},
			{LABEL: '存款不含台定AUM增減明細', 	DATA: '2'},
			{LABEL: '投保不含黃金存摺AUM增減明細', 	DATA: '3'},
			{LABEL: 'EIP客戶增減明細', 			DATA: '4'},
			{LABEL: 'LUM房貸增減明細', 			DATA: '5'}
		);
		
		$scope.getDataDate = function(){
    		var NowDate = new Date();
    		var yr = NowDate.getFullYear();
    		var mm = NowDate.getMonth() + 1;
    		var strmm = '';
    		var xm = '';
    		$scope.mappingSet['PMS.DATA_DATE'] = [];
    		for (var i = 0; i < 12; i++) {
    			if (mm == 0) {
    				mm = 12;
    				yr = yr - 1;
    			}
    			if (mm < 10)
    				strmm = '0' + mm;
    			else
    				strmm = mm;
    			$scope.mappingSet['PMS.DATA_DATE'].push({
    				LABEL : yr + '/' + strmm,
    				DATA : yr + '' + strmm
    			});
    			mm--;
    		} 
    	};
    	$scope.getDataDate();
		
		$scope.init = function(){
			$scope.showFlag = undefined;
			$scope.resultList = [];
			$scope.outputVO = {};
			
            $scope.inputVO = {
            		data_date: '',
            		report_type: undefined,
	                /** 連動組織會用到的參數 **/
	                region_center_id: undefined,
	                branch_area_id: undefined,
	                branch_nbr: undefined,
	                emp_id: undefined,
	                reportDate: ''
            };
            
            //取本月最新日期
            $scope.sendRecv("PMS423", "getDate", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", {},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length > 0) {
							$scope.current_snap_date = tota[0].body.resultList[0].CURRENT_SNAP_DATE;
                		}
					}						
			});
		};
		$scope.init();
		
		$scope.inquireInit = function(isSelect){
			$scope.resultList = [];
			$scope.outputVO = {};
			if(isSelect){
				$scope.showFlag = $scope.inputVO.report_type;
				if(angular.isDefined($scope.inputVO.asc))delete $scope.inputVO.asc;
				if(angular.isDefined($scope.inputVO.column))delete $scope.inputVO.column;
			}
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			if($scope.inputVO.data_date == '' || $scope.inputVO.data_date == undefined ||
			   $scope.inputVO.report_type == '' || $scope.inputVO.report_type == undefined ){
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.showFlag = $scope.inputVO.report_type;
			$scope.sendRecv("PMS423", "query", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList=[];
								return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.upload = function(){
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS423/PMS423_UPLOAD.html',
				className: 'PMS423_UPLOAD',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value.success == 'success'){
//					$scope.init();
//					$scope.queryData();
				}
			});
		}
		
		$scope.export = function(){
			$scope.sendRecv("PMS423", "export", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", $scope.inputVO,
					function(tota, isError) {						
			});
		}
		
		$scope.goAnotherRpt = function (type, row) {
			$scope.inputVO.report_type = type;
			$scope.inputVO.branch_nbr = row.BRANCH_NBR;
			$scope.inputVO.emp_id = row.EMP_ID;
			$scope.query();
		};
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if ($scope.inputVO.data_date != '') {
            	$scope.inputVO.reportDate = $scope.inputVO.data_date;
            	$scope.RegionController_getORG($scope.inputVO);
        	} else {
        		$scope.inputVO.reportDate = '';
        	}			
        };
		
});
