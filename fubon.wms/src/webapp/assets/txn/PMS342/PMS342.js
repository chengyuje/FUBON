/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS342Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS342Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.dateChange = function(){
        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
        	$scope.RegionController_getORG($scope.inputVO);
		};
		
        getParameter.XML(['COMMON.YES_NO'], function(totas) {
			if (totas) {
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});

		
		$scope.ChangeAo_code = function(){			
			//理專 3碼 => 給AO_CODE
			if($scope.inputVO.emp_id.length == '3'){
				$scope.inputVO.ao_code = $scope.inputVO.emp_id;
			//請選擇 空 => 給初始的EMP_ID
			}else if($scope.inputVO.emp_id == ''){
				$scope.inputVO.ao_code = '';
				$scope.inputVO.emp_id = $scope.old_emp_id;
			//初始進入 非3碼 => 抓初始的EMP_ID
			}else{
				$scope.old_emp_id = $scope.inputVO.emp_id;
			}
		}
		
		$scope.init = function(){
			$scope.inputVO = {					
					sCreDate: undefined,
					eCreDate: undefined,					
					region_center_id: '',
					branch_area_id: '' ,
					branch_nbr: '',
					emp_id: ''	
        	};
			$scope.curDate = new Date();
			$scope.paramList = [];
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
	
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
		    //#0000375: 報表留存時間 三個月  實際操作是90天
			minDate: new Date(new Date().setDate(new Date().getDate() - 90))
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
		// date picker end
				
		$scope.query = function(){
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:資料統計日期為必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PMS342", "queryData", "com.systex.jbranch.app.server.fps.pms342.PMS342InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
//							angular.forEach($scope.paramList, function(row, index, objs){
//								row.CUST_ID=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10); //隱藏身分證  四碼
//								if(row.EMP_ID!=null){
//									row.EMP_NAME=row.AO_CODE + "-" + row.EMP_NAME;
//								}else{
//									row.EMP_NAME=""
//								}
//							});	
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
//			angular.forEach($scope.outputVO.totalListt, function(row, index, objs){
//				row.CUST_ID=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);  //隱藏身分證  四碼
//			});	
			$scope.sendRecv("PMS342", "export", "com.systex.jbranch.app.server.fps.pms342.PMS342OutputVO", $scope.outputVO,
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
