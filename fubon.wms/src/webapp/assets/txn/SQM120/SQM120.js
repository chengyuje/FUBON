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
		
		 // date picker
		var currDate = new Date(2018, 8, 1, 0, 0, 0);
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: currDate
		};
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: currDate
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
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || currDate;
		};
		// date picker end

		$scope.init = function(){
			debugger;
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
			$scope.limitDate();
			$scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	    	$scope.RegionController_setName($scope.region).then(function(data) {
	    		debugger
	    		//檢核需不需要預設員編	
	    		if ($scope.EMP_LIST.length >= 3)
	    			$scope.inputVO.emp_id = '';
	    	});
	    	console.log("$scope.inputVO2 ", $scope.inputVO); 	
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
                	
});
