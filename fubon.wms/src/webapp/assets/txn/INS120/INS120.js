/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS120Controller', function(sysInfoService,$rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $timeout, $filter ,$q) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "INS120Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
		
	/**==============================================初始化========================================================**/

	$scope.initial= function(){
		debugger;
		var deferred = $q.defer();
		$scope.resultList = [];
		var fromINS100 = $scope.connector('get',"INS100_Date");
		$scope.inputVO = {				
				branchId     		: '',
				aoCode         		: '',
				custId				: '',
				custName			: '',
				agreeSdate 			: undefined,
				agreeEdate 			: undefined
				
		}

		if(fromINS100){
			$scope.inputVO.custId = fromINS100.CUST_ID;
			$scope.inputVO.custName = fromINS100.CUST_NAME;
			$scope.inputVO.agreeSdate = Date.parse(fromINS100.REPORT_DATE);
			$scope.inputVO.agreeEdate = Date.parse(fromINS100.REPORT_DATE);
			deferred.resolve("success");
		}
		//組織連動 // 增加角色判斷，除了理專外的預設不用給 ao code
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branchId", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        $scope.pri = String(sysInfoService.getPriID());
		if ($scope.pri == '006' || $scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011' || $scope.pri == '012' || 
			$scope.pri == 'O001' || $scope.pri == '013' || $scope.pri == '045' || $scope.pri == '046' || $scope.pri == '055' || $scope.pri == '056' ||
			$scope.pri == '014' || $scope.pri == '015' || $scope.pri == '023' || $scope.pri == '024'	
		) {
			$scope.inputVO.aoCode = '';
		}
		$scope.csvBtn = false;
        debugger;
        return deferred.promise;
	};
	$scope.initial().then(
			function(data){
				if($scope.connector('get',"INS100_Date")){
					$scope.connector('set',"INS100_Date",undefined);
					$scope.queryData();
				}
			});	
	
	//取客戶姓名
	$scope.getcustName = function(){
		$scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
		$scope.sendRecv("INS120", "getcustName", "com.systex.jbranch.app.server.fps.ins120.INS120InputVO", {custId: $scope.inputVO.custId},
				function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
				return;
			}
			
			if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
				$scope.showMsg("ehl_01_common_009");
				$scope.inputVO.custName = '';
				return;
			}else{
				$scope.inputVO.custName = tota[0].body.resultList[0].CUST_NAME;				
			}
		});
	};
	
	/***查詢結果***/	
	$scope.queryData = function(){
		$scope.csvBtn = true;
		$scope.sendRecv("INS120", "queryData", "com.systex.jbranch.app.server.fps.ins120.INS120InputVO", $scope.inputVO,
				function(tota, isError) {
			debugger;
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
				return;
			}
			
			if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
				$scope.resultList = [];
				$scope.data = [];
				$scope.showMsg("ehl_01_common_009");
				return;
			}else{
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;	
//				$scope.showMsg("ehl_01_common_023");//執行成功
//				angular.forEach($scope.resultList, function(row, index, objs){
//										
//				});
			}
		});
	};
	
	//匯出
	$scope.exportCSV = function(){
		$scope.sendRecv("INS120","exportCSV","com.systex.jbranch.app.server.fps.ins120.INS120InputVO",	{outputVO: $scope.outputVO},
       			function(tota,isError){
    			if (!isError) {

    			}
    	});		
	};
			
	//列印
	$scope.print = function(row){
		$scope.sendRecv("INS120","print","com.systex.jbranch.app.server.fps.ins120.INS120InputVO",	{agree_keyno: row.AGREE_KEYNO},
       			function(tota,isError){
    			if (isError) {
    				$scope.showErrorMsg(tota[0].body.msgData);
    			} else {
    				$scope.showMsg("ehl_01_common_023");//執行成功
    			}
    	});		
	};
	
	$scope.reFindAo = function() {
		$scope.AVAIL_AO_CODE = projInfoService.getAoCode();
		if($scope.inputVO.branchId == "") {
			$scope["AO_LIST"] = [];
			$scope.inputVO.aoCode = '';
			return;
		}
		var deferred = $q.defer();
    	$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': undefined,
    																									   'branch_area_id': undefined,
    																									   'branch_nbr': $scope.inputVO.branchId },
			function(totas, isError) {
    			$scope["TOTAL_AO_LIST"] = totas[0].body.ao_list;
    			
    			$scope["AO_LIST"] = [];
				if($scope.AVAIL_AO_CODE.length > 0) {
					if($scope.AVAIL_AO_CODE.length > 1 ) {
						angular.forEach($scope["TOTAL_AO_LIST"], function(row) {
							if($scope.AVAIL_AO_CODE.indexOf(row.AO_CODE) > -1)
								$scope["AO_LIST"].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
						});	
					}
					else {
						$scope["AO_LIST"].push({LABEL: projInfoService.getUserName(), DATA: $scope.AVAIL_AO_CODE[0]});
					}
				}
				else {
					angular.forEach($scope["TOTAL_AO_LIST"], function(row) {
						$scope["AO_LIST"].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
					});
				}
				deferred.resolve();
		 	}
		);
    	return deferred.promise;
	};
	
	
				
    /****======================================================== date picker =======================================================****/
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
  		minDate: $scope.minDate
    };

    $scope.bgn_eDateOptions = {
		maxDate: $scope.maxDate,
  		minDate: $scope.minDate
    };

    $scope.model = {};
	
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
		
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.agreeEdate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.agreeSdate || $scope.minDate;		
	};
    /**** date picker end ****/
});