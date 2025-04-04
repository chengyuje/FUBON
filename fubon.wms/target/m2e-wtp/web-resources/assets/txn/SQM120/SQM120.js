/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM120Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM120Controller";
		
		// 繼承共用的組織連動選單
		$controller('RegionController', {$scope: $scope});
		$scope.function_LIST = [{'LABEL':'編輯', 'DATA': 'edit'},{'LABEL':'刪除', 'DATA': 'del'}];
		$scope.STATUS_LIST = [{'LABEL':'未處理', 'DATA': '0'},{'LABEL':'結案扣分', 'DATA': '1'},{'LABEL':'結案不扣分', 'DATA': '2'},{'LABEL':'處理中', 'DATA': '3'},{'LABEL':'退件', 'DATA': '4'}];
		getParameter.XML(["SQM.QTN_TYPE","FUBONSYS.HEADMGR_ROLE"], function(totas) {
			if (totas) {
				$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
				$scope.HEADMGR_ROLE = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')];
				$scope.role_id = projInfoService.getRoleID();
				$scope.user_id = projInfoService.getUserID();
				$scope.headmgr_flag = false;//判斷是否為總行人員flag
				angular.forEach($scope.HEADMGR_ROLE, function(row, index, objs){
					if(row.DATA == $scope.role_id ){
						$scope.headmgr_flag = true;
						$scope.inputVO.owner_emp_id = $scope.user_id;
						return;	
					}
    			});
			}
		});		
		
		//取得UHRM人員清單
		$scope.getUHRMList = function() {
			debugger
			$scope.sendRecv("SQM110", "getUHRMList", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", {"branch_area_id":$scope.inputVO.branch_area_id}, 
				function(tota, isError) {
					debugger
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						$scope.inputVO.emp_id = tota[0].body.uEmpID;
					}
			});
		};
		
		 // date picker
		var currDate = new Date(2018, 8, 1, 0, 0, 0);
		$scope.optionsInit = function() {
			$scope.bgn_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: currDate
			};
			$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: currDate
			};
		}
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			if ($scope.inputVO.eCreDate) {
				let y = $scope.inputVO.eCreDate.getFullYear() - 1;
				let m = $scope.inputVO.eCreDate.getMonth();
				let d = $scope.inputVO.eCreDate.getDate();
				$scope.bgn_sDateOptions.minDate = new Date(y, m, d);
			}
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || currDate;
			if ($scope.inputVO.sCreDate) {
				let y = $scope.inputVO.sCreDate.getFullYear() + 1;
				let m = $scope.inputVO.sCreDate.getMonth();
				let d = $scope.inputVO.sCreDate.getDate();
				$scope.bgn_eDateOptions.maxDate = new Date(y, m, d);
			}
		};
		// date picker end

		$scope.init = function(){
			$scope.inputVO = {
					sCreDate: new Date(new Date().getFullYear(),new Date().getMonth(),1),
					eCreDate: new Date(),
					region_center_id: '',   //區域中心
					branch_area_id: '' ,    //營運區
        	};
			$scope.rptDate = '';
			$scope.totalData = [];
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};	
			$scope.optionsInit();
			$scope.limitDate();
			debugger
			
			$scope.inputVO.mgrUHRMAreaYN = "N";
			$scope.isUHRMArea = false;
			$scope.pri_id = projInfoService.getPriID();
			$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
			//是否為業務處長
			$scope.inputVO.isRegionMgr = ($scope.pri_id[0] == "013");
			//是否為私銀角色
			$scope.isUHRM = $scope.memLoginFlag.startsWith('UHRM') ? true : false;
			if($scope.isUHRM) {
				$scope.getUHRMList();
			}
			
			$scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	    	$scope.RegionController_setName($scope.region).then(function(data) {
	    		debugger
	    		//檢核需不需要預設員編	
	    		if ($scope.EMP_LIST.length >= 3) $scope.inputVO.emp_id = '';
	    	}); 	
		
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
			$scope.outputVO={totalList:[]};
		}
		$scope.inquireInit();	
				
		//資料查詢
		$scope.query = function() {	
			$scope.sendRecv("SQM120", "queryData", "com.systex.jbranch.app.server.fps.sqm120.SQM120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.outputVO={};
								$scope.showMsg("ehl_01_common_009");
								
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.totalList);
							$scope.paramList = tota[0].body.totalList;
//							alert(JSON.stringify($scope.paramList));
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;		
							return;
						}						
			});
		};
		
		//確認如果是連動來的，請自動查詢待處理事項。
		if('Y' == $scope.connector('get','LinkFlag_SQM120')){
			$scope.inputVO.sCreDate = null;
			$scope.inputVO.eCreDate = null;
			$scope.inputVO.emp_id = '';
			$scope.inputVO.owner_emp_id = projInfoService.getUserID();
			$scope.query();
			$scope.connector('set','LinkFlag_SQM120','');
		}
		
		$scope.edit = function(row){
			if(row.NO_IMPROVE_FLAG == 'N'){
				var dialog = ngDialog.open({
					template: 'assets/txn/SQM120/SQM120_edit.html',
					className: 'SQM120',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.qtnType = row.QTN_TYPE;
	                	$scope.row = row;
	                }]
				});
				dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.query();
					}
				});
			}else if(row.NO_IMPROVE_FLAG == 'Y'){
				var dialog = ngDialog.open({
					template: 'assets/txn/SQM120/SQM120_contents.html',
					className: 'SQM120',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                }]
				});
				dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.query();
					}
				});
			}
			
        };
        
        $scope.printPdf = function(row) {
			$scope.inputVO.case_no = row.CASE_NO;
			$scope.sendRecv("SQM120", "download", "com.systex.jbranch.app.server.fps.sqm120.SQM120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('下載成功');
							$scope.closeThisDialog('successful');
						}						
			});
			
		};
		
		 $scope.queryFlowDetail = function(row){
				var dialog = ngDialog.open({
					template: 'assets/txn/SQM120/SQM120_flowDetail.html',
					className: 'SQM120',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.case_no = row.CASE_NO;
	                }]
				});
				dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.query();
					}
				});
	      };
	      
	      $scope.checkIsUHRMArea = function() {
	  		$scope.inputVO.brhList = [];
	  		angular.forEach($scope.BRANCH_LIST, function(row, index, objs){
	  			if(row.DATA != "" && row.DATA != "0"){
	  				$scope.inputVO.brhList.push({LABEL: row.LABEL, DATA: row.DATA});					
	  			}
	  		});
	  		debugger
	  		$scope.inputVO.mgrUHRMAreaYN = "N";
	  		$scope.isUHRMArea = false;
	  		//總行或業務處長有選營運區
	  		if(($scope.headmgr_flag || $scope.inputVO.isRegionMgr) && $scope.inputVO.branch_area_id != undefined
	  				&& $scope.inputVO.branch_area_id != null && $scope.inputVO.branch_area_id != "") {
	  			if($scope.inputVO.brhList.length > 0 && $scope.inputVO.brhList[0].DATA.length > 3) {
	  				//若選到私銀區，或沒有分行下拉選單資料
	  				$scope.inputVO.mgrUHRMAreaYN = "Y"; //總行或業務處長選私銀區
	  				$scope.isUHRMArea = true;
	  				$scope.getUHRMList();
	  				$scope.inputVO.branch_nbr = ""; //私銀區不顯示分行
	  			}
	  		}
	  	}
                	
});
