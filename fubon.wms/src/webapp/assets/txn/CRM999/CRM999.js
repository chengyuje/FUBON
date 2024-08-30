/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM999Controller',
		function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q, getParameter, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM999Controller";
	
	//初始化 

	$scope.inputVO = {};
	$scope.pageControlVO = {};
	$scope.initQueryStatus = true;
	/*
	 * 載入初始相關資訊
	 */
	$scope.start = function() {
		
		$scope.sendRecv("CRM999", "start", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO = angular.copy(tota[0].body.paramVO);
						$scope.isEnabledUser = true;
					} else { //20171030add 登入者資料錯誤，無法使用客訴功能
						$scope.isEnabledUser = false ;
					}
				});
	}
	
	$scope.init = function() {
		//查詢條件
		$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
        $scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
		
        //載入初始資訊
        $scope.start();
        
	};
	$scope.init();

	// combo box datasource
	$scope.mappingSet['status'] = [];
	$scope.mappingSet['status'].push(
			{LABEL : '金服主管處理',DATA : '1'},
			{LABEL : '區督導處理',DATA : '2'},
			{LABEL : '處長',DATA : '3'},
			{LABEL : '結案',DATA : '4'},
			{LABEL : '總行建立',DATA : 'A'},
//			{LABEL : '總行科長',DATA : 'A1'},{LABEL : '總行部長',DATA : 'A2'},
	);
	
	// branch
	$scope.bra_list = projInfoService.getAvailBranch();
	$scope.BRANCH_LIST = [];
	angular.forEach(projInfoService.getAvailBranch(), function(row) {
		if($scope.inputVO.branch_area_id) {
			if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id)			
				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		} else
			$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
	});
	
	// date picker
	// 日期起訖
	$scope.sDateOptions = {};
	$scope.eDateOptions = {};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.endDate;
		$scope.eDateOptions.minDate = $scope.inputVO.startDate;
	};
	
	/**
	 * 清除
	 */
	$scope.inquireInit = function() {
		//查詢結果
    	$scope.paramList = [];
		$scope.resultList = [];
		$scope.outputVO = {};
	};
	$scope.inquireInit();
	
	/**
	 * 清除
	 */
	$scope.inquireInit1 = function() {
		//查詢條件
		$scope.inputVO.custId = undefined;
		$scope.inputVO.branchId = undefined;
		$scope.inputVO.startDate = undefined;
		$scope.inputVO.endDate = undefined;
		$scope.inputVO.status = undefined;
	};
	
	/**
	 * 查詢
	 */
	$scope.query = function() {
		$scope.sendRecv("CRM999", "query", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						var result = tota[0].body.resultList1;
						if (result && result.length) {
							if ($scope.initQueryStatus) {
								//初始查詢只要帶出屬於自己的案例就行
								$scope.resultList = result.filter((e)=>e.MYCASE=='Y');
								$scope.initQueryStatus = false;
							} else {
								$scope.resultList = result;
							}
							$scope.outputVO = tota[0].body;
						} else {
							if ($scope.initQueryStatus) {
								$scope.showMsg('查無待覆核資料');
								$scope.initQueryStatus = false;
							} else {
								//查無資料
								$scope.showMsg('ehl_01_common_009');
							}
							$scope.inquireInit();
						}
					}
		});
		$scope.clearOrder();
	};
	
	$scope.clearOrder = function() {
		$scope.inputVO.column = null;
	}
	
	$scope.query();
	
	
	/**
	 * 新增
	 */
	$scope.add = function() {
		$scope.sendRecv("CRM999", "add", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						var paramVO = angular.copy(tota[0].body.paramVO);
						if (paramVO) {
							var dialog = ngDialog.open({
								template: 'assets/txn/CRM999/CRM999_edit.html',
								className: 'CRM999_edit',
								showClose: false,
								controller: ['$scope', function($scope) {
									
									$scope.preInputVO = paramVO;
								}]
							});
							dialog.closePromise.then(function (data) {
								if(data.value === 'successful'){
									$scope.query();
								}
							});
						}
					}
		});
	};
	
	/**
	 * 修改
	 */
	$scope.update = function(row) {
		//將$scope.inputVO copy一份當作參數update，必須將其設定complain_list_id(備份設定就行，$scope.inputVO原始不動)
		var tempVO = angular.copy($scope.inputVO);
		tempVO.complainListId = row.COMPLAIN_LIST_ID;
		
		$scope.sendRecv("CRM999", "update", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", tempVO,
				function(tota, isError) {
			if (!isError) {
				if(tota[0].body.errorMsg) {
					$scope.showErrorMsg(tota[0].body.errorMsg);
					return;
				} else {
					//將資訊傳至dialog
					var paramVO = angular.copy(tota[0].body.paramVO);
					if (paramVO) {
						var dialog = ngDialog.open({
							template: 'assets/txn/CRM999/CRM999_edit.html',
							className: 'CRM999_edit',
							showClose: false,
							controller: ['$scope', function($scope) {
								
								$scope.preInputVO = paramVO;
							}]
						});
						dialog.closePromise.then(function (data) {
							if(data.value === 'successful'){
								$scope.query();
							}
						});
						
					}
					
					
				}
			}
		});
		
	}
	
	/**
	 * 列印備查簿
	 */
	$scope.exportMemo = function() {
		$scope.sendRecv("CRM999", "expMemo", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
			if (!isError) {
			}
		});
		
	}
	
	/**
	 * 列印
	 */
	$scope.exportPdf = function(row) {
		$scope.sendRecv("CRM999", "expPdf", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", 
				{complainListId : row.COMPLAIN_LIST_ID},
				function(tota, isError) {
			if (!isError) {

			}
		});
	}
	
	/**
	 * 列印Excel
	 */
	$scope.exportExcel = function() {
		$scope.sendRecv("CRM999", "expExcel", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
			if (!isError) {

			}
		});
	}
	
	
	/*
	 * 取消結案	Ray	201307150187-00	開放總行可取消結案案件
	 */
	$scope.cancel = function(row) {
		$confirm({text : '確定要取消結案?'}, {size : 'sm'}).then(function() {
			$scope.sendRecv("CRM999", "cancel", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO",
					{complainListId : row.COMPLAIN_LIST_ID}, 
					function(tota, isError) {
				if (!isError) {
					var errMsg = tota[0].body.errorMsg;  //取得回傳錯誤訊息
					if (!errMsg) {
						$scope.showMsg("客訴資料表取消結案成功！");
						$scope.query();
					} else {
						$scope.showErrorMsg(errMsg);
					}
				}
			});
		});
	}
	
	
	$scope.showComplainFlow = function(row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM999/CRM999_showComplainFlow.html',
			className: 'CRM999_showComplainFlow',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.row = row;
			}]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.closeThisDialog('successful');
			}
		});
	}
	
	$scope.pageOn = function() {
    	if ($scope.pageControlVO.pageChoice) {
    		angular.forEach($scope.choiceLogic($scope.paramList), function(row){
				row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.choiceLogic($scope.paramList), function(row){
				row.SELECTED = false;
			});
    	}
    }
	
    $scope.allOn = function() {
    	if ($scope.pageControlVO.allChoice) {
    		angular.forEach($scope.choiceLogic($scope.resultList), function(row){
				row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.choiceLogic($scope.resultList), function(row){
				row.SELECTED = false;
			});
    	}
    }
    
    //for disabled 
    $scope.checkLogic = function(row) {
    	if (row.STEP_FOR_UI == 'A2') {
    		return false;
    	}
    	if (row.GRADE != 3 && row.STEP_FOR_UI == '3') {
    		return false;
    	}
    	return true;
    }
    
    $scope.choiceLogic = function(list) {
    	var data = list.filter(function(row) {
    		//最後一階以及第三階(等級1、2)
			return (row.MYCASE == 'Y' && row.STEP_FOR_UI == 'A2') || (row.MYCASE == 'Y' && row.STEP_FOR_UI == '3' && row.GRADE != 3);
    	});
    	return data;
    }
    
    // 換頁全選按鈕初始化
    $scope.$watchCollection('paramList', function(newNames, oldNames) {
    	
    	$scope.pageControlVO.pageChoice = false;
    })
    
    $scope.send = function() {
    	
    	var data = $scope.resultList.filter(function(row) {
			return (row.SELECTED == true);
    	})
    	if(!data.length) {
    		$scope.showErrorMsg('無案件可覆核');
        } else {
        	var str = '';
        	var index = 1;
        	data.map((e)=>{
        		str += e.COMPLAIN_LIST_ID ;
        		if (data.length > index) str += ',';
        		index++;
        	}) 
        	
        	$confirm({text : '共有 ' + data.length + ' 筆客訴案件準備覆核，確認送出?'}, {size : 'sm'}).then(function() {
    			$scope.sendRecv("CRM999", "sendBatchComplains", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO",
    					{complainListId : str, handleStep : data[0].STEP_FOR_UI}, 
    					function(tota, isError) {
    				if (!isError) {
    					
    					var msg = tota[0].body.msg;  //取得回傳訊息
    					if (msg) {
    						$scope.showMsg(msg);
    						$scope.query();
    					}
    				}
    			});
    		});
        }
    	

    }
});