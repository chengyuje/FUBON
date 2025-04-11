'use strict';
eSoafApp.controller('CRM210Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$controller('PPAPController', {$scope: $scope});
	$scope.controllerName = "CRM210Controller";
	
	$scope.mappingSet['branchsDesc'] = [];
	angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
		$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
	});

	/** 抓出所有分行 **/
	$scope.mappingSet['branchsDesc_all'] = [];		
	$scope.sendRecv("CRM210", "getAllBranch", "com.systex.jbranch.app.server.fps.crm210.CRM210InputVO", {}, function(totas, isError) {
		if (isError) {
			$scope.showErrorMsgInDialog(totas.body.msgData);
			return;
        }
        if (totas.length > 0) {
        	angular.forEach(totas[0].body.resultList, function(row, index, objs){
    			$scope.mappingSet['branchsDesc_all'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
    		});
        };
    });
	
	// filter
	getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "CRM.FAMILY_DEGREE", "TBCRM_CUST_NOTE.TAKE_CARE_MATCH_YN", "CRM.ACT_PRD_NUMS", "CRM.NONSEARCH_ID", "CRM.EXPERIENCE_LEVEL"], function(totas) {
		if (totas) {
			$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];											/** 貢獻度等級 **/
			$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];											/** 理財會員等級 **/
			$scope.mappingSet['CRM.FAMILY_DEGREE'] = totas.data[totas.key.indexOf('CRM.FAMILY_DEGREE')];									/** 家庭會員等級 **/
			$scope.mappingSet['TBCRM_CUST_NOTE.TAKE_CARE_MATCH_YN'] = totas.data[totas.key.indexOf('TBCRM_CUST_NOTE.TAKE_CARE_MATCH_YN')];	/** 經營頻次符合度 **/
			$scope.mappingSet['CRM.ACT_PRD_NUMS'] = totas.data[totas.key.indexOf('CRM.ACT_PRD_NUMS')];										/** 客戶活躍度1~9 **/
			$scope.mappingSet['CRM.NONSEARCH_ID'] = totas.data[totas.key.indexOf("CRM.NONSEARCH_ID")];
			$scope.mappingSet['CRM.EXPERIENCE_LEVEL'] = totas.data[totas.key.indexOf('CRM.EXPERIENCE_LEVEL')];
		}
	});
    //
	
	// gender
	$scope.mappingSet['GENDER'] = [];
	$scope.mappingSet['GENDER'].push({LABEL : '男',DATA : '1'},{LABEL : '女',DATA : '2'});
	
	// yn
	$scope.mappingSet['YN'] = [];
	$scope.mappingSet['YN'].push({LABEL : '是',DATA : 'Y'},{LABEL : '否',DATA : 'N'});
	
	// OP_FREQ
	$scope.mappingSet['OP_FREQ'] = [];
	$scope.mappingSet['OP_FREQ'].push({LABEL : '符合',DATA : 'Y'},{LABEL : '未符合',DATA : 'N'});
	
	//COMPARE
	$scope.mappingSet['COMPARE'] = [];
	$scope.mappingSet['COMPARE'].push({LABEL : '>',DATA : 'B'},{LABEL : '<',DATA : 'S'},{LABEL : '=',DATA : 'E'});
	
	//手收貢獻度(查詢區間)
	$scope.mappingSet['timeE'] = [];
	$scope.mappingSet['timeE'].push({LABEL : '近三個月',DATA : 'Three'},{LABEL : '近六個月',DATA : 'Six'},{LABEL : '近一年',DATA : 'OneYear'});
	
	$scope.advanced = false;

	$scope.groupData = $scope.connector('get', 'CRM251_ROW');
	$scope.group_source = $scope.connector('get', 'CRM251_PAGE');
	$scope.connector('set', "CRM251_ROW", null);
	$scope.connector('set', "CRM251_PAGE", null);
	
	//datapicker
	$scope.manage_03_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.manage_03_eDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.cust_05_sDateOptions = {
		minMode: "year"
	};
	
	$scope.cust_05_eDateOptions = {
		minMode: "year"
	};
	
	$scope.atr_03_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.atr_03_eDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	
	$scope.campaign_dateOptions = {
		maxDate: $scope.maxDate,
		minDate: new Date()
	};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.cust_05_sDateOptions.maxDate = $scope.inputVO.cust_05_eDate || $scope.maxDate;
		$scope.cust_05_eDateOptions.minDate = $scope.inputVO.cust_05_sDate || $scope.minDate;
	};
	$scope.limitDate2 = function() {
		$scope.atr_03_sDateOptions.maxDate = $scope.inputVO.atr_03_eDate || $scope.maxDate;
		$scope.atr_03_eDateOptions.minDate = $scope.inputVO.atr_03_sDate || $scope.minDate;
	};
	$scope.limitDate3 = function() {
		$scope.manage_03_sDateOptions.maxDate = $scope.inputVO.manage_03_eDate || $scope.maxDate;
		$scope.manage_03_eDateOptions.minDate = $scope.inputVO.manage_03_sDate || $scope.minDate;
	};
	
	//group
    $scope.getGroup = function() {
    	if ($scope.groupData) {
    		$scope.mappingSet['GroupList'] = [];
    		$scope.mappingSet['GroupList'].push({LABEL: $scope.groupData.GROUP_NAME, DATA: $scope.groupData.GROUP_ID});
    		$scope.inputVO.group = $scope.groupData.GROUP_ID;
    		$scope.inputVO.group_name = $scope.groupData.GROUP_NAME;
    	} else {
    		$scope.inputVO.ao_code = String(sysInfoService.getAoCode());
    		$scope.sendRecv("CRM230", "getGroupList", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO, function(totas, isError) {
            	if (isError) {
            		$scope.showErrorMsg(totas[0].body.msgData);
            	}
            	if (totas.length > 0) {
            		$scope.mappingSet['GroupList'] = [];
            		angular.forEach(totas[0].body.resultList, function(row, index, objs){
        				$scope.mappingSet['GroupList'].push({LABEL: row.GROUP_NAME, DATA: row.GROUP_ID});
        			});
            	};
			});	
    	}
    }
    $scope.getGroup();
    
    // 2018/3/29
    $scope.getGroupName = function() {
    	if($scope.inputVO.group) {
    		var index = $scope.mappingSet['GroupList'].map(function(e) { return e.DATA; }).indexOf($scope.inputVO.group);
    		$scope.inputVO.group_name = $scope.mappingSet['GroupList'][index].LABEL;
    	}
    };

	// init
	$scope.init_common = function(){
		$scope.inputVO = {};
		$scope.obj = {};
		$scope.getGroup();
		$scope.ao_code = sysInfoService.getAoCode();
		
		//AO CODE
	    $scope.sendRecv("CRM211", "getAOCode", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.msgData != undefined){
					$scope.showErrorMsg(tota[0].body.msgData);
				}
			}
			
			if (tota.length > 0) {
				$scope.aolist = _.sortBy(tota[0].body.resultList, ['AO_CODE']);
				$scope.mappingSet['new_ao_list'] = [];
				
				if ($scope.ao_code != '' && $scope.ao_code != undefined) {		//有AO_CODE
        			if($scope.ao_code.length > 1){		//有兩個以上AO_CODE的理專
        				angular.forEach($scope.aolist, function(row, index, objs){
        					angular.forEach($scope.ao_code, function(row2, index2, objs2){
        						if(row.AO_CODE == row2){
        							$scope.mappingSet['new_ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME, DATA: row.AO_CODE});
        						}
        					});
            			});
        			} else if ($scope.ao_code.length == 1) {		//只有一個AO_CODE的理專
                		angular.forEach($scope.aolist, function(row, index, objs) {
            				$scope.mappingSet['new_ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME, DATA: row.AO_CODE});
            			});
        				$scope.inputVO.campaign_ao_code = String(sysInfoService.getAoCode());		//指派至新理專
        			}
    			} else {		//無AO_CODE
    				angular.forEach($scope.aolist, function(row, index, objs) {
    					$scope.mappingSet['new_ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME, DATA: row.AO_CODE});
    				});
    			}   		
        	};
		});	
	};
	$scope.init_common();
	
	$scope.inquireInit_common = function(){
		$scope.obj.outputVO = [];
		$scope.obj.resultList = [];
		$scope.obj.data = [];	
	}
	$scope.inquireInit_common();
			
	//進階畫面開關
	$scope.show_advance = function() {
		if ($scope.advanced == false) {
			$scope.advanced = true;
		} else {
			$scope.advanced = false;
		}
	};
    
	$scope.checkrow = function() {
    	if ($scope.obj.clickAll) {
    		angular.forEach($scope.obj.data, function(row) {
				row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.obj.data, function(row) {
				row.SELECTED = false;
			});
    	}
    };
    
    $scope.checkrow2 = function() {
    	if ($scope.obj.clickAll2) {
    		angular.forEach($scope.obj.resultList, function(row) {
				row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.obj.resultList, function(row) {
				row.SELECTED = false;
			});
    	}
    };

	$scope.checkAdd = function() {
    	var ans = _.uniqBy($scope.obj.resultList.filter(function(obj){
    		return (obj.SELECTED == true);
    	}),"CUST_ID");
    	
    	if (ans.length == 0) {
    		return;
    	}

    	$scope.inputVO.grouplist = ans;
    	$scope.sendRecv("CRM230", "group_check", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO,  function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			
			if (totas[0].body.resultList.length == 0) {
				$scope.showErrorMsg('勾選客戶中有非歸屬理專自建的群組名單');
				return;
			} else {
				$scope.sendRecv("CRM230", "group_join", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO,  function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
        			
	                if (totas.length > 0) {
	                	$scope.showMsg('加入成功');
	                	
	                	if ($scope.groupData) {
	                		// 原路徑
	                    	var path = $rootScope.menuItemInfo.txnPath;
	                    	path.pop();
	                    	$rootScope.GeneratePage({'txnName':"CRM251",'txnId':"CRM251",'txnPath':path});
	                	}	
	                };
			    });
			}
    	});
	};
	
	$scope.add = function(row) {
    	var dialog = ngDialog.open({
			template: 'assets/txn/PMS109/PMS109.html',
			className: 'PMS109',
			showClose: false,
			 controller: ['$scope', function($scope) {
                	$scope.cust_id = row.CUST_ID;
                	$scope.cust_name = row.CUST_NAME;
                	$scope.src_type = 'CRM210';
             }]
		});
    };
	
    $scope.add_campaign = function() {
    	var ans = _.uniqBy($scope.obj.resultList.filter(function(obj){
    		return (obj.SELECTED == true);
    	}),"CUST_ID");
    	
    	if (ans.length == 0) {
    		$scope.showErrorMsgInDialog("請選取客戶");
    		return;
    	}
    	$scope.inputVO.campaign_custlist = ans;
    	$scope.inputVO.source = 'crm210';
    	$scope.sendRecv("CRM230", "add_campaign", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
            }
            if (totas.length > 0) {
            	$scope.showMsg('加入成功');
            };
    	});
    };
	
    $scope.goCRM610 = function(row){ 	
    	$scope.noQuery = false;
    	
    	angular.forEach($scope.mappingSet['CRM.NONSEARCH_ID'], function(rowrow) {
    		if(row.CUST_ID === rowrow.DATA ){
    			$scope.showErrorMsg("ehl_01_CRM_001");
    			$scope.noQuery = true;
    			return;
    		}
		});		
		
		if($scope.noQuery) {
			return;
		}
		
    	$scope.CRM_CUSTVO = {
				CUST_ID :  row.CUST_ID,
				CUST_NAME :row.CUST_NAME
		}
		$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO);
    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
		$scope.connector("set","CRM610URL",path);
		
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM610/CRM610.html',
			className: 'CRM610',
			showClose: false
		});
    };	    
		
    $scope.toggleRadio = function toggleRadio() {
    	$scope.inputVO.branchID = $scope.inputVO.ao_05;

    	$scope.mappingSet['campEmpList'] = [];
    	
    	$scope.sendRecv("CAM220", "getEmpList", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.empList = tota[0].body.resultList;
				
				$scope.mappingSet['campEmpList'] = [];
				
				angular.forEach($scope.empList, function(row, index, objs){
					var label = row.EMP_ID + "-" + row.EMP_NAME + "(" + row.TOTAL_CONTACT_CUST + ")";
					$scope.mappingSet['campEmpList'].push({LABEL: label, DATA: row.EMP_ID});
				});
			}
		});
    }
});