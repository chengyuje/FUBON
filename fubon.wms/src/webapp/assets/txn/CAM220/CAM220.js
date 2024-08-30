/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM220Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM220Controller";
		
		// filter
		getParameter.XML(["CAM.LEAD_STATUS", "CAM.LEAD_TYPE", "CAM.CHANNEL_CODE", "CRM.CON_DEGREE", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CAM.LEAD_STATUS'] = totas.data[totas.key.indexOf('CAM.LEAD_STATUS')];
				$scope.mappingSet['CAM.LEAD_TYPE'] = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
				$scope.mappingSet['CAM.CHANNEL_CODE'] = totas.data[totas.key.indexOf('CAM.CHANNEL_CODE')];
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
	    //
		
		$scope.tabType = $scope.connector('get','tab');
		if (typeof($scope.tabType) === 'undefined') {
			$scope.tabType = 'tab1';
		}
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		
        $scope.init = function(){
			$scope.inputVO = {
					branchID: sysInfoService.getBranchID() == "000" ? "" : sysInfoService.getBranchID(),
					campIDList: [], 
					stepIDList: [],
					channel: '002',
					
					custIDList:$scope.inputVO.custIDList,
					empIDList: $scope.inputVO.empIDList,
					aoCodeList: $scope.inputVO.aoCodeList,
					vipDegree: $scope.inputVO.vipDegree,
					conDegree: $scope.inputVO.conDegree,
					
					empID: $scope.inputVO.empID, // 分派處理人員
					yesORno: $scope.inputVO.yesORno, //是 否改派
					custID: $scope.inputVO.custID, 
					custName: $scope.inputVO.custName, 
					campName: $scope.inputVO.campName, 
					leadStatus: $scope.inputVO.leadStatus, 
					sDate: $scope.inputVO.sDate,
					eDate: $scope.inputVO.eDate
        	};

			$scope.limitDate();
			// 2017/9/6
			$scope.checkVO = {};
		};
        $scope.init();
        
        $scope.initByTab2 = function() {
			$scope.inputVO = {
					branchID: $scope.inputVO.branchID,
					campIDList: $scope.inputVO.campIDList, 
					stepIDList: $scope.inputVO.stepIDList,
					channel: $scope.inputVO.channel,
					
					custIDList:[],
					empIDList: [],
					aoCodeList: [],
					vipDegree: '',
					conDegree: '',
					
					empID: $scope.inputVO.empID, // 分派處理人員
					yesORno: $scope.inputVO.yesORno, //是 否改派
					custID: $scope.inputVO.custID, 
					custName: $scope.inputVO.custName, 
					campName: $scope.inputVO.campName, 
					leadStatus: $scope.inputVO.leadStatus, 
					sDate: $scope.inputVO.sDate,
					eDate: $scope.inputVO.eDate
        	};
			$scope.limitDate();
			$scope.assBranchListByCust = [];
			$scope.assBranchListByCustOutputVO = {};
		};
        $scope.initByTab2();
        
        $scope.init_his = function() {
        	$scope.inputVO = {
					branchID: $scope.inputVO.branchID,
					campIDList: $scope.inputVO.campIDList, 
					stepIDList: $scope.inputVO.stepIDList,
					channel: $scope.inputVO.channel,
					
					assCustID: $scope.inputVO.assCustID,
					assCustName: $scope.inputVO.assCustName,
					custIDList: $scope.inputVO.custIDList,
					empIDList: $scope.inputVO.empIDList,
					aoCodeList: $scope.inputVO.aoCodeList,
					vipDegree: $scope.inputVO.vipDegree,
					conDegree: $scope.inputVO.conDegree,
					
					empID: '', // 分派處理人員
					yesORno: '', //是 否改派
					custID: '', 
					custName: '', 
					campName: '', 
					leadStatus: '', 
					sDate: undefined,
					eDate: undefined,
        	};
        	$scope.limitDate();
        	$scope.logList = [];
        	$scope.logListOutputVO = {};
        }
        $scope.init_his();

        $scope.bra_list = projInfoService.getAvailBranch();
        $scope.branch_area_id = "null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID();
		// 取得分行列表
		$scope.genBranch = function() {
			$scope.mappingSet['branchsDesc'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row) {
				if($scope.branch_area_id) {
					if(row.BRANCH_AREA_ID == $scope.branch_area_id)
						$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				} else
					$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
        };
        
        // 該分行所有人員
        $scope.getEmpList = function(type) {
        	var channel_T = $scope.inputVO.channel;
        	if (type == 'tab3')
        		channel_T = '';
        	
			$scope.sendRecv("CAM220", "getEmpList", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", {branchID: $scope.inputVO.branchID, 
																											   channel: channel_T},
        			function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['empList'] = tota[0].body.resultList;
        				}
        			}
			);
        };

        // 取得該分行待分派列表
        $scope.getAssBranchList = function() {
        	// 2017/8/3 russle add 必填
        	if(!$scope.inputVO.branchID) {
        		$scope.showErrorMsg('請選擇分行');
        		return;
        	}
        	$scope.assBranchList = [];
        	$scope.sendRecv("CAM220", "getAssBranchList", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.assBranchList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.assBranchList = tota[0].body.assBranchList;
							$scope.assBranchListCount = tota[0].body.leadsCounts;
							$scope.assBranchListOutputVO = tota[0].body;
							return;
						}
        	});
        };
        
        // 取得該分行待分派列表  BY CUSTOMER
        $scope.getAssBranchListByCust = function() {
        	// 2017/8/3 russle add 必填
        	if(!$scope.inputVO.branchID) {
        		$scope.showErrorMsg('請選擇分行');
        		return;
        	}
        	// toUpperCase
			if($scope.inputVO.assCustID)
				$scope.inputVO.assCustID = $scope.inputVO.assCustID.toUpperCase();
        	
        	$scope.assBranchListByCust = [];
        	$scope.sendRecv("CAM220", "getAssBranchListByCust", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.assBranchListByCust.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.assBranchListByCust = refreshList(tota[0].body.assBranchListByCust);
							$scope.assBranchListByCustOutputVO = tota[0].body;
							$scope.inputVO.custIDList = [];
						}
        	});
        };
        
        function refreshList(list) {
			var ans = [];
			for(var i = 0; i < list.length; i++) {
				generateList(ans,list[i]);
			}
			return ans;
		}
		function generateList(ansRow,row) {
			var obj = {};
			var exist = false;
			for(var i = 0; i < ansRow.length; i++) {
				if(row["CUST_ID"] == ansRow[i]["CUST_ID"]) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				obj["CUST_ID"] = row["CUST_ID"];
				obj["CUST_NAME"] = row["CUST_NAME"];
				obj["BRANCH_ID"] = row["BRANCH_ID"];
				obj["BRANCH_NAME"] = row["BRANCH_NAME"];
				obj["HIS_AO_CODE"] = row["HIS_AO_CODE"];
				obj["HIS_PS"] = row["HIS_PS"];
				obj["VIP_DEGREE"] = row["VIP_DEGREE"];
				obj["CON_DEGREE"] = row["CON_DEGREE"];
				obj["AUM_AMT"] = row["AUM_AMT"];
				obj["SUBITEM"] = [];
				var obj2 = {};
				obj2["CUST_ID"] = row["CUST_ID"];
				obj2["CUST_NAME"] = row["CUST_NAME"];
				obj2["BRANCH_ID"] = row["BRANCH_ID"];
				obj2["BRANCH_NAME"] = row["BRANCH_NAME"];
				obj2["CAMPAIGN_NAME"] = row["CAMPAIGN_NAME"];
				obj2["CAMPAIGN_DESC"] = row["CAMPAIGN_DESC"];
				obj2["LEAD_TYPE"] = row["LEAD_TYPE"];
				obj2["CUST_AO_CODE"] = row["CUST_AO_CODE"];
				obj2["CUST_AO_NAME"] = row["CUST_AO_NAME"];
				obj2["FIRST_CHANNEL"] = row["FIRST_CHANNEL"];
				obj2["END_DATE"] = row["END_DATE"];
				obj2["HIS_AO_CODE"] = row["HIS_AO_CODE"];
				obj2["HIS_PS"] = row["HIS_PS"];
				obj2["VIP_DEGREE"] = row["VIP_DEGREE"];
				obj2["CON_DEGREE"] = row["CON_DEGREE"];
				obj2["AUM_AMT"] = row["AUM_AMT"];
				obj["SUBITEM"].push(obj2);
				ansRow.push(obj);
				return;
			}
			var obj2 = {};
			obj2["CUST_ID"] = row["CUST_ID"];
			obj2["CUST_NAME"] = row["CUST_NAME"];
			obj2["BRANCH_ID"] = row["BRANCH_ID"];
			obj2["BRANCH_NAME"] = row["BRANCH_NAME"];
			obj2["CAMPAIGN_NAME"] = row["CAMPAIGN_NAME"];
			obj2["CAMPAIGN_DESC"] = row["CAMPAIGN_DESC"];
			obj2["LEAD_TYPE"] = row["LEAD_TYPE"];
			obj2["CUST_AO_CODE"] = row["CUST_AO_CODE"];
			obj2["CUST_AO_NAME"] = row["CUST_AO_NAME"];
			obj2["FIRST_CHANNEL"] = row["FIRST_CHANNEL"];
			obj2["END_DATE"] = row["END_DATE"];
			obj2["HIS_AO_CODE"] = row["HIS_AO_CODE"];
			obj2["HIS_PS"] = row["HIS_PS"];
			obj2["VIP_DEGREE"] = row["VIP_DEGREE"];
			obj2["CON_DEGREE"] = row["CON_DEGREE"];
			obj2["AUM_AMT"] = row["AUM_AMT"];
			var old = ansRow.slice(-1).pop();
			old["SUBITEM"].push(obj2);
		}
        
        //分派部隊
		var vo = {'param_type': 'CAM.DISPATCH_CHANNEL', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['CAM.DISPATCH_CHANNEL'] = totas[0].body.result;
    			$scope.mappingSet['CAM.DISPATCH_CHANNEL'] = projInfoService.mappingSet['CAM.DISPATCH_CHANNEL'];
    			
    		}
    	});
        
        // 多選
        $scope.checkRow = function() {
        	if ($scope.inputVO.campIDList.length > 0) {
        		angular.forEach($scope.assBranchList, function(row){
        			var idx = $scope.inputVO.campIDList.indexOf(row.CAMPAIGN_ID);
        			$scope.inputVO.campIDList.splice(idx, 1);
        			$scope.inputVO.stepIDList.splice(idx, 1);
    			});
        	} else {
            	$scope.inputVO.campIDList = [];
    			$scope.inputVO.stepIDList = [];
    			
        		angular.forEach($scope.assBranchList, function(row){
        			$scope.inputVO.campIDList.push(row.CAMPAIGN_ID);
        			$scope.inputVO.stepIDList.push(row.STEP_ID);
        			
    			});
        	}
        };
        
        $scope.toggleSelection = function toggleSelection(campID, stepID) {
        	var idx;
        	var campID_idx = $scope.inputVO.campIDList.indexOf(campID);
        	var stepID_idx = $scope.inputVO.stepIDList.indexOf(stepID);
        	if (campID_idx == stepID_idx) {
        		idx = campID_idx;
        	}
        	if (idx > -1) {
        		$scope.inputVO.campIDList.splice(idx, 1);
        		$scope.inputVO.stepIDList.splice(idx, 1);
        	} else {
        		$scope.inputVO.campIDList.push(campID);
        		$scope.inputVO.stepIDList.push(stepID);
        	}
        };
        
        // 員編多選
        $scope.checkTab2Row = function() {
        	$scope.inputVO.empIDList = [];
			$scope.inputVO.aoCodeList = [];
			if($scope.checkVO.clickEmpAll) {
				angular.forEach($scope.resultList, function(row) {
        			$scope.inputVO.empIDList.push(row.EMP_ID);
        			$scope.inputVO.aoCodeList.push(row.AO_CODE);
    			});
			}
        };
        
        $scope.toggleSelection2 = function(row) {
        	var empID_idx = $scope.inputVO.empIDList.indexOf(row.EMP_ID);
        	var aoCode_idx = $scope.inputVO.aoCodeList.indexOf(row.AO_CODE);
        	if (empID_idx > -1)
    			$scope.inputVO.empIDList.splice(empID_idx, 1);
        	else
        		$scope.inputVO.empIDList.push(row.EMP_ID);
        	if (aoCode_idx > -1)
        		$scope.inputVO.aoCodeList.splice(aoCode_idx, 1);
        	else
        		$scope.inputVO.aoCodeList.push(row.AO_CODE);
        };
        
        // 客戶多選
        $scope.checkRowTab2 = function() {
        	$scope.inputVO.custIDList = [];
			if($scope.checkVO.clickAllTab2) {
				angular.forEach($scope.assBranchListByCust, function(row) {
					$scope.inputVO.custIDList.push(row.CUST_ID);
    			});
			}
        };
        
        $scope.toggleSelectionTab2 = function(custID) {
        	var idx = $scope.inputVO.custIDList.indexOf(custID);
        	if (idx > -1) {
        		$scope.inputVO.custIDList.splice(idx, 1);
        	} else {
        		$scope.inputVO.custIDList.push(custID);
        	}
        };
        
        // 單選
        $scope.toggleRadio = function toggleRadio(data) {
        	if (data)
        		$scope.inputVO.channel = data;
        	
        	$scope.sendRecv("CAM220", "getEmpList", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.checkVO.clickEmpAll = false;
							$scope.inputVO.empIDList = [];
							$scope.inputVO.aoCodeList = [];
						}
        		}
        	);
        }
        if($scope.inputVO.branchID)
        	$scope.toggleRadio();

        // 輪流指派
        $scope.turnDispatchLead = function() {
        	// 2017/8/3 russle add 必填
        	if(!$scope.inputVO.branchID) {
        		$scope.showErrorMsg('請選擇分行');
        		return;
        	}
        	$scope.sendRecv("CAM220", "turnDispatchLead", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
    				function(totas, isError) {
		            	if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
		            	
		            	$scope.inputVO.campIDList = [];
		            	$scope.inputVO.stepIDList = [];
		            	$scope.inputVO.empIDList = [];
	        			$scope.inputVO.aoCodeList = [];
		            	
		            	if (totas.length > 0) {
		            		$scope.showSuccessMsg("ehl_01_cam220_001");
		            		$scope.clickAll = false;
		            		$scope.assBranchList = [];
		            		$scope.getAssBranchList();
		            	};
					}
			);
	    };
	    
	    // 指定分派
	    $scope.designDispatchLead = function() {
	    	// 2017/8/3 russle add 必填
        	if(!$scope.inputVO.branchID) {
        		$scope.showErrorMsg('請選擇分行');
        		return;
        	}
	    	$scope.sendRecv("CAM220", "designDispatchLead", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
    				function(totas, isError) {
		            	if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
		            	
		            	$scope.inputVO.campIDList = [];
		            	$scope.inputVO.stepIDList = [];
		            	$scope.inputVO.empIDList = [];
	        			$scope.inputVO.aoCodeList = [];
	        			
		            	if (totas.length > 0) {
		            		$scope.showSuccessMsg("ehl_01_cam220_002");
		            		$scope.clickAll = false;
		            		$scope.assBranchListByCust = [];
		            		$scope.assBranchListByCustOutputVO = {};
		            		$scope.getAssBranchListByCust();
		            		$scope.toggleRadio();
		            	};
					}
			);
	    }
	    
	    // 取得LOG
	    $scope.logList = [];
	    $scope.getLogList = function() {
	    	// 2017/8/3 russle add 必填
        	if(!$scope.inputVO.branchID) {
        		$scope.showErrorMsg('請選擇分行');
        		return;
        	}
			$scope.sendRecv("CAM220", "getLogList", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.logList.length == 0) {
        						$scope.showMsg("ehl_01_common_009");
        						return;
        					}
        					$scope.logList = tota[0].body.logList;
							$scope.logListOutputVO = tota[0].body;
        				}
        			}
			);
        };
        
        $scope.exportHistory = function() {
			if($scope.logList.length == 0)
				return;
			$scope.sendRecv("CAM220", "exportHistory", "com.systex.jbranch.app.server.fps.cam220.CAM220InputVO", {'history_list': $scope.logList},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
        
//        $scope.custConnectData = function(row) {
//        	$scope.connector('set','custID', row.CUST_ID);
//        	$scope.connector('set','custName', row.CUST_NAME);
//        	$scope.connector('set','tab', 'tab1');
//    		$rootScope.menuItemInfo.url = "assets/txn/CAM200/CAM200.html";
//        }
        
        $scope.custConnectData = function(row) {
        	var custID = row.CUST_ID;
        	var custName = row.CUST_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM220/CAM220_ROUTE.html',
				className: 'CAM200',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.txnName = "名單執行紀錄";
	        		$scope.routeURL = 'assets/txn/CAM200/CAM200.html';
	        		$scope.connector('set','custID', row.CUST_ID);
	            	$scope.connector('set','custName', row.CUST_NAME);
	                $scope.connector('set', 'tab', 'tab1');
	            }]
			}).closePromise.then(function (data) {
//				if(data.value != "cancel") {
					$scope.goAction('tab3');
//				}
			});
		};
        
        $scope.goAction = function(type) {
        	$scope.tabType = (typeof(type) !== 'undefined' ? type : $scope.tabType);
        	if ($scope.tabType == "tab1") {
        		 $scope.genBranch();
        		 if($scope.inputVO.branchID)
        			 $scope.getAssBranchList();
        	} else if ($scope.tabType == "tab2") {
        		$scope.genBranch();
//        		$scope.getEmpList();
//        		if($scope.inputVO.branchID)
//        			$scope.getAssBranchListByCust();
        	} else if ($scope.tabType == "tab3") {
        		$scope.genBranch();
        		$scope.getEmpList($scope.tabType);
//        		$scope.getClosedList();
        	}
        }
        $scope.goAction();
        
     // 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID :  row.CUST_ID,
    				CUST_NAME :row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
});