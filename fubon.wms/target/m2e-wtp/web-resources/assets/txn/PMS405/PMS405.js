/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS405Controller', function($scope, $controller,
		socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS405Controller";
	
	
	/***TEST ORG COMBOBOX START***/
    var org = [];
   
    //區域中心資訊
    $scope.genRegion = function(){
    	$scope.sendRecv("PMS401", "getOrgInfo", "com.systex.jbranch.app.server.fps.pms401.PMS401InputVO", $scope.inputVO,
				function(totas, isError) {          	
    				if(!isError){          					
    					org = totas[0].body.orgList;  //全部權限組織資訊        					
    					$scope.rcList = _.uniqBy(org, 'V_REGION_CENTER_ID');
    					$scope.mappingSet['region'] = [];
    					angular.forEach($scope.rcList, function(row, index, objs){
    						$scope.mappingSet['region'].push({LABEL: row.V_REGION_CENTER_NAME, DATA: row.V_REGION_CENTER_ID});
    						if(row.V_REGION_CENTER_ID == '0000000000')
    							$scope.mappingSet['region'].splice(index,1);
    					});    			
    					if($scope.rcList.length == 1)
    						$scope.inputVO.rc_id = $scope.rcList[0].V_REGION_CENTER_ID;
    				}        				        		
		});        	
    };
  
   //營運區資訊
    $scope.genArea = function() {        	
		$scope.inputVO.op_id = '';
		$scope.mappingSet['op'] = [];
		$scope.opList = _.uniqBy(org, 'V_BRANCH_AREA_ID');
		angular.forEach($scope.opList, function(row, index, objs){				
			if(row.V_REGION_CENTER_ID == $scope.inputVO.rc_id){
				if(row.V_BRANCH_AREA_ID == '0000000000')
					row.V_BRANCH_AREA_NAME = 'N/A';
				$scope.mappingSet['op'].push({LABEL: row.V_BRANCH_AREA_NAME, DATA: row.V_BRANCH_AREA_ID});				
				if(row.V_BRANCH_AREA_ID == '0000000000' && $scope.opList.length > 1)						
						$scope.mappingSet['op'].splice(index,1);					
			}
		});
		if($scope.opList.length == 1 && $scope.inputVO.rc_id != '')
			$scope.inputVO.op_id = $scope.opList[0].V_BRANCH_AREA_ID;
    };
    
    //分行資訊
    $scope.genBranch = function() {
		$scope.inputVO.br_id = '';
		$scope.mappingSet['branch'] = [];
		$scope.brList = _.uniqBy(org, 'V_BRANCH_NBR');
		angular.forEach($scope.brList, function(row, index, objs){				
			if(row.V_BRANCH_AREA_ID == $scope.inputVO.op_id){
				if(row.V_BRANCH_NBR == '0000000000')
					row.V_BRANCH_NAME = 'N/A';
				$scope.mappingSet['branch'].push({LABEL: row.V_BRANCH_NAME, DATA: row.V_BRANCH_NBR});
				if(row.V_BRANCH_NBR == '0000000000' && $scope.brList.length > 1)
					$scope.mappingSet['branch'].splice(index,1);
			}			
		});
		if($scope.brList.length == 1 && $scope.inputVO.op_id != '')
			$scope.inputVO.br_id = $scope.brList[0].V_BRANCH_NBR;
    };
    
    //理專資訊
    $scope.branchChange = function() {
		$scope.inputVO.emp_id = '';
		$scope.mappingSet['aoemp'] = [];    		  		
		$scope.empList = _.uniqBy(org, 'V_EMP_ID');
		console.log("emp="+JSON.stringify($scope.empList));
		angular.forEach($scope.empList, function(row, index, objs){
			if(row.V_BRANCH_NBR == $scope.inputVO.br_id)
				$scope.mappingSet['aoemp'].push({LABEL: row.V_AO_CODE + '-' +row.V_EMP_NAME, DATA: row.V_EMP_ID});	                 					                 					                 					                 			    					    				
		});			
		if($scope.empList.length == 1 && $scope.inputVO.br_id != '')
			$scope.inputVO.emp_id = $scope.empList[0].V_EMP_ID;
    };
    
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	$scope.inputVO.rc_id = '';
    	$scope.inputVO.op_id = '';
    	$scope.inputVO.br_id = '';
    	$scope.inputVO.emp_id = '';
    	$scope.genRegion();
    };
    
    /***ORG COMBOBOX END***/
	


//	// 區域資訊
//	$scope.genArea = function() {
//		$scope.mappingSet['OpInfo'] = [];
//
//		angular.forEach(projInfoService.getAvailArea(), function(row, index,
//				objs) {
//		
//			$scope.mappingSet['OpInfo'].push({
//				LABEL : row.BRANCH_AREA_NAME,
//				DATA : row.BRANCH_AREA_ID
//			});
//
//		});
//	};
//	$scope.genArea();
//
//	// 分行資訊
//	$scope.genBranch = function() {
//		$scope.mappingSet['BrInfo'] = [];
//		angular.forEach(projInfoService.getAvailBranch(), function(row, index,
//				objs) {
//			if (row.BRANCH_AREA_ID == $scope.inputVO.op_id)
//				$scope.mappingSet['BrInfo'].push({
//					LABEL : row.BRANCH_NAME,
//					DATA : row.BRANCH_NBR
//				});
//		});
//	};
//	// $scope.genBranch();
//	$scope.open = function($event, index) {
//		$event.preventDefault();
//		$event.stopPropagation();
//		$scope['opened' + index] = true;
//	};
//	$scope.limitDate = function() {
//		$scope.startMaxCreDate = $scope.inputVO.eCreDate || $scope.maxDate;
//		$scope.endMinCreDate = $scope.inputVO.sCreDate || $scope.minDate;
//	};

	$scope.init = function() {
		$scope.inputVO = {
			sCreDate : undefined,
			eCreDate : undefined,
			rc_id : '',
			op_id : '',
			br_id : '',
			aocode : '',
			emp_id:'',
			TRADE_DATE : undefined,
			TXN_ID : '',
			SUPERVISOR_FLAG : '',
			NOTE : ''

		};
		$scope.curDate = new Date();
		$scope.startMaxCreDate = $scope.maxDate;
		$scope.endMinCreDate = $scope.minDate;
		$scope.genRegion();
	};
	$scope.init();

	$scope.inquireInit = function() {
		$scope.initLimit();
		$scope.paramList = [];
	}
	$scope.inquireInit();
	
	
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
	// date picker end
	
	
	
	
	

	$scope.query = function() {
		$scope.sendRecv("PMS405", "queryData",
				"com.systex.jbranch.app.server.fps.pms405.PMS405InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList.length == 0) {
							$scope.paramList = [];
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					S
						return;
					}
				});
	};

	$scope.save = function() {
		$scope.sendRecv("PMS405", "save",
				"com.systex.jbranch.app.server.fps.pms405.PMS405InputVO", {
					'list' : $scope.paramList
				}, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota[0].body.msgData);
						return;
					}
					if (tota.length > 0) {
						$scope.showMsg('儲存成功');
						$scope.query();
					}
					;
				});
	};
	
	
	$scope.export = function() {
		$scope.sendRecv("PMS405", "export",
				"com.systex.jbranch.app.server.fps.pms405.PMS405OutputVO",
				{'list':$scope.paramList}, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						
						return;
					}
				});
	};

});
