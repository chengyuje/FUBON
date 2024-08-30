/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3103Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "CRM3103Controller";
		
		//權限: 043, 044 總行分行管理科人員
		$scope.privilegeId = projInfoService.getPriID()[0];
		$scope.isHeadMgr = ($scope.privilegeId == '043' || $scope.privilegeId == '044');
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate != '' && $scope.inputVO.sCreDate != null && $scope.inputVO.sCreDate != undefined){
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	} else {
        		let defaultDate = new Date().setDate(new Date().getDate() - 14);
		        let defaultEndDate = new Date();
        		$scope.inputVO.reportDate = defaultDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		// combobox
    	getParameter.XML(["FUBONSYS.FC_ROLE", "FUBONSYS.BMMGR_ROLE", "FUBONSYS.ARMGR_ROLE", "CRM.TRS_PRJ_ROT_STEP_STATUS", "CRM.TRS_PRJ_ROT_STEP1_BMMGR"], function(totas) {
    		if (totas) {
    			$scope.mappingSet['FUBONSYS.BMMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.BMMGR_ROLE')];
    			$scope.mappingSet['FUBONSYS.FC_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.FC_ROLE')];
    			$scope.mappingSet['FUBONSYS.ARMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.ARMGR_ROLE')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STEP_STATUS'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STEP_STATUS')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STEP1_BMMGR'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STEP1_BMMGR')]; //第一階段可覆核主管角色
    			
    			debugger
    			//是否為理專角色
    			var findfc = $filter('filter')($scope.mappingSet['FUBONSYS.FC_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isFC = (findfc != null && findfc.length > 0) ? true : false;
				
				//是否為第一階段分行主管角色
				var findbmmS1 = $filter('filter')($scope.mappingSet['CRM.TRS_PRJ_ROT_STEP1_BMMGR'], {DATA: sysInfoService.getRoleID()});
				$scope.isBMMGRStep1 = (findbmmS1 != null && findbmmS1.length > 0) ? true : false;
				
				//是否為分行主管角色
				var findbmm = $filter('filter')($scope.mappingSet['FUBONSYS.BMMGR_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isBMMGR = (findbmm != null && findbmm.length > 0) ? true : false;
    			
				//是否為處主管角色
				var findarm = $filter('filter')($scope.mappingSet['FUBONSYS.ARMGR_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isARMGR = (findarm != null && findarm.length > 0) ? true : false;
    		}
    	});
    	
		//Project combobox
		$scope.getAllPRJ = function() {
			$scope.sendRecv("CRM3103", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",{},
					function(tota, isError) {
					   if(!isError){
						   	$scope.PROJNAME = [];
		                	angular.forEach(tota[0].body.allPRJ, function(row) {
		                		$scope.PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
		                	});
					   }
				});
		};
		$scope.getAllPRJ();
		//
		
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.edate;
			$scope.eDateOptions.minDate = $scope.inputVO.sdate;
		};
		//
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.data = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.init = function() {
			$scope.checkVO = {};
			
			/** InputVO 初始，預設日期為系統日 **/
            let defaultDate = new Date().setDate(new Date().getDate() - 14);
            let defaultEndDate = new Date();
            $scope.inputVO = {
            	/** 連動組織會用到的參數 **/
	            region_center_id	: undefined,
	            branch_area_id		: undefined,
	            branch_nbr			: undefined,
	            reportDate			: $filter('date')(defaultDate, 'yyyyMM'),
	            custId      : ''
            };
			$scope.dateChange();
			$scope.inquireInit();
		};
		$scope.init();
		
		//查詢
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.inputVO.bra_nbr = $scope.inputVO.branch_nbr;
			$scope.sendRecv("CRM3103", "inquire", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",$scope.inputVO,
				function(tota, isError) {
					if(!isError) {
						if (tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
						}
						else {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.resultList, function(row) {
								//核心客戶比例
								row.GO_CUST_PERC = (row.GO_CUST_CNT/row.CUST_CNT)*100;
								//是否有勾選:預設為未勾選 
								row.IS_SELECTED = "N";
								//是否可勾選:預設為不可
								row.selectedDisabled = true;
								
								row.func = [];
								row.func.push({LABEL: "檢視", DATA: "1"});
								debugger
								var isEditable = false;
								if($scope.isFC) { //理專
									isEditable = RegExp('[1456]').test(row.STEP_STATUS);
								} else if($scope.isBMMGR) { //分行主管
									 if($scope.isBMMGRStep1) { //第一階段可覆核分行主管
										 isEditable = RegExp('[26]').test(row.STEP_STATUS);
									 } else {
										 isEditable = RegExp('[6]').test(row.STEP_STATUS);
									 }
								} else if($scope.isARMGR) { //處主管
									isEditable = (row.STEP_STATUS == "3");
									if(row.STEP_STATUS == "3") {
										//處長覆核中，則可勾選
										row.selectedDisabled = false;
									}
								} else if($scope.isHeadMgr) {
									isEditable = RegExp('[67]').test(row.STEP_STATUS);
									//分行人員管理科可刪除理專
									row.func.push({LABEL: "刪除", DATA: "3"});
								}
								if(isEditable && row.EDITABLE_YN == "Y") { //可編輯
									row.func.push({LABEL: "修改", DATA: "2"});
								}
							});
		               	}
				   }
			});
		};
		
		//明細資料：檢視或修改
		$scope.doFunc = function(row) {
			if(row.acttype != "1" && row.acttype != "2" && row.acttype != "3")
				return;
			if(row.acttype == "3") {
				//刪除
				$confirm({text: '您確定刪除此筆記錄？'}, {size: 'sm'}).then(function () {
					var param = {};
					param.saveType = "5";
					param.emp_id = row.EMP_ID;
					param.bra_nbr = row.BRANCH_NBR;
					param.PRJ_ID = row.PRJ_ID;
                    $scope.sendRecv("CRM3103", "save", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO", param,
                    	function (totas, isError) {
                            if (isError) {
                                $scope.showErrorMsg(totas[0].body.msgData);
                            }
                            if (totas.length > 0) {
                                $scope.showSuccessMsg('ehl_01_common_003'); // 刪除成功
                                $scope.inquireInit();
                                $scope.inquire();
                            }
                    });
                });
			} else {
				//檢視或修改
				var isFC = $scope.isFC;
				var isBMMGR = $scope.isBMMGR;
				var isBMMGRStep1 = $scope.isBMMGRStep1;
				var isARMGR = $scope.isARMGR;
				var isHeadMgr = $scope.isHeadMgr;
				
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM3103/CRM3103_DETAIL.html',
					className: 'CRM3103_DETAIL',
					showClose: false,
					controller: ['$scope', function($scope) {
						$scope.row = row;
						$scope.row.isFC = isFC;
						$scope.row.isBMMGR = isBMMGR;
						$scope.row.isBMMGRStep1 = isBMMGRStep1;
						$scope.row.isARMGR = isARMGR;
						$scope.row.isHeadMgr = isHeadMgr;
					}]
				});
				dialog.closePromise.then(function(data) {
					$scope.inquire();
				});
			}
		}
		
		//匯出
		$scope.exportData = function(){
			$scope.inputVO.bra_nbr = $scope.inputVO.branch_nbr;
			$scope.sendRecv("CRM3103","export","com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO", 
					{'list': $scope.resultList, 'printAllData': false, 'PRJ_ID': $scope.inputVO.PRJ_ID}, function(tota, isError) {
						if (isError) {							
							return;            		
		            	}
					});			
		};
		
		//匯出全部資料
		$scope.exportAllData = function(){
			$scope.inputVO.bra_nbr = $scope.inputVO.branch_nbr;
			$scope.sendRecv("CRM3103","export","com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO", 
					{'list': $scope.resultList, 'printAllData': true, 'PRJ_ID': $scope.inputVO.PRJ_ID}, function(tota, isError) {
						if (isError) {							
							return;            		
		            	}
					});			
		};
		
		//本頁全選
		$scope.checkrow = function() {
	    	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.data, function(row){
        			if(row.STEP_STATUS == "3") {
						//處長覆核中，才可勾選
        				row.IS_SELECTED = "Y";
					} else {
						row.IS_SELECTED = "N";
					}
    			});
        	} else {
        		angular.forEach($scope.data, function(row){
        			row.IS_SELECTED = "N";
    			});
        	}
        };
        
        //全選
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.resultList, function(row){
        			if(row.STEP_STATUS == "3") {
						//處長覆核中，才可勾選
        				row.IS_SELECTED = "Y";
					} else {
						row.IS_SELECTED = "N";
					}
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
        			row.IS_SELECTED = "N";
    			});
        	}
        };
        
        $scope.save = function() {
        	$scope.inputVO.bra_nbr = $scope.inputVO.branch_nbr;
        	$scope.sendRecv("CRM3103", "save", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",$scope.inputVO,
        		function(tota, isError) {
					if(!isError) {
					} else {
//						$scope.init();
//						$scope.inquireDetail(); //有錯誤重新整理
					}
			});
        	$scope.inquire();
        }
        
        //處主管核可
        $scope.approval = function() {
        	$scope.inputVO.custList = [];
        	$scope.inputVO.custList = $filter('filter')($scope.resultList, {IS_SELECTED: "Y"});
        	$scope.inputVO.saveType = "7";
        	
        	$scope.save();
        }
        
        //處主管退回
        $scope.deny = function() {
        	$scope.inputVO.custList = [];
        	$scope.inputVO.custList = $filter('filter')($scope.resultList, {IS_SELECTED: "Y"});
        	$scope.inputVO.saveType = "8";
        	
        	$scope.save();
        }
});