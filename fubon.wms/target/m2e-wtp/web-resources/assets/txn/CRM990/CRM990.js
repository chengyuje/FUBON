/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM990Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM990Controller";
	
	$scope.loginID = sysInfoService.getUserID();
	$scope.role = sysInfoService.getPriID();
	$scope.inputVO = {};

	// filter
	getParameter.XML(["CRM.COMPLAIN_GRADE"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMPLAIN_GRADE'] = totas.data[totas.key.indexOf('CRM.COMPLAIN_GRADE')];
		}
	});
	
	// date picker
	$scope.s_createtimeOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
	};
	$scope.e_createtimeOptions = {
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
		$scope.s_createtimeOptions.maxDate = $scope.inputVO.e_createtime || $scope.maxDate;
		$scope.e_createtimeOptions.minDate = $scope.inputVO.s_createtime || $scope.minDate;
	};
	
	//非初始查詢註記
	$scope.changeFlag = function() {
		$scope.inputVO.intiQuery = false;
	}
	
	//查詢
	$scope.query = function() {
		$scope.pageControlVO.allChoice = false;
		$scope.pageControlVO.pageChoice = false;
		$scope.resultList = [];
		$scope.outputVO = [];
		
		if($scope.inputVO.cust_id != ''){
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();	//轉大寫
		}
		
		$scope.sendRecv("CRM990", "query", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");	//查無資料
        			return;
        		}
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
	}
	
	//初始化
	$scope.init = function(){
		$scope.pageControlVO = [];
		$scope.pageControlVO.allChoice = false;
		$scope.pageControlVO.pageChoice = false;
		
		//設定分行下拉
		$scope.BRANCH_LIST = [];
		$scope.availBranch = projInfoService.getAvailBranch();
		
		angular.forEach($scope.availBranch, function(row) {
			$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		if($scope.role >= '038'){
			$scope.BRANCH_LIST.push({LABEL: '客服部', DATA: '806'});				
		}
		
		// 20211206 modify by ocean #0798: WMS-CR-20211115-01_新增客戶關懷中心角色權限及調整內控品管科角色權限 
		if($scope.role == '071' || $scope.role == '072'){
	    	$scope.BRANCH_LIST.push({LABEL: '客戶關懷中心', DATA: '01782'});
	    	$scope.BRANCH_LIST.push({LABEL: '個金分行業務管理部', DATA: '175'});
	    }
		
		if($scope.role == '041' || $scope.role == '042'){	//個金分行業務管理部_分行內控品管科經辦、科長 
	    	$scope.BRANCH_LIST.push({LABEL: '個金分行業務管理部', DATA: '175'});
	    }
		$scope.BRANCH_LIST = _.sortBy($scope.BRANCH_LIST, ['DATA']);
		
		if($scope.BRANCH_LIST.length == 1){
			$scope.inputVO.branch_nbr = $scope.BRANCH_LIST[0].DATA;
		} else {
			$scope.inputVO.branch_nbr = undefined;
		}
		
		//控制凍結視窗 (總行科長：凍結視窗多一列放行功能)
		if($scope.role == '042' || $scope.role == '044' || $scope.role == '046'){
			$scope.freezeControl = 6;
		} else {
			$scope.freezeControl = 5;
		}
		
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.inputVO.cust_id = '';
		$scope.inputVO.s_createtime = undefined;
		$scope.inputVO.e_createtime = undefined;
		$scope.inputVO.handle_step = undefined;
		$scope.inputVO.intiQuery = true;
		$scope.inputVO.pri_id = $scope.role[0];
		$scope.query();
	}
	$scope.init();
	
	//清除
	$scope.inquireInit = function(){
		//客戶統編
		$scope.inputVO.cust_id = '';
		//客戶姓名
		$scope.inputVO.cust_name = '';
		//分行別
		if($scope.availBranch.length == 1){
			$scope.inputVO.branch_nbr = $scope.availBranch[0].BRANCH_NBR;
		} else {
			$scope.inputVO.branch_nbr = undefined;
		}
		//日期起迄
		$scope.inputVO.s_createtime = undefined;
		$scope.inputVO.e_createtime = undefined;
		//狀態
		$scope.inputVO.handle_step = undefined;
	}
	
	//本頁全選
	$scope.page = function() {
    	if ($scope.pageControlVO.pageChoice) {
    		//未結案且處理進度已到總行科長(A1)才可選取
    		angular.forEach($scope.paramList, function(row) {
    			if(row.HANDLE_STEP != 'E' && row.HANDLE_STEP == 'A1')
    				row.SELECTED = true;
			});
    	} else {
    		angular.forEach($scope.paramList, function(row) {
    			row.SELECTED = false;
			});
    	}
    };
    
    //全選
    $scope.all = function() {
    	if ($scope.pageControlVO.allChoice) {
    		$scope.pageControlVO.pageChoice = true;

    		//未結案且處理進度已到總行科長(A1)才可選取
    		angular.forEach($scope.resultList, function(row) {
    			if(row.HANDLE_STEP != 'E' && row.HANDLE_STEP == 'A1')
    				row.SELECTED = true;
			});
    	} else {
    		$scope.pageControlVO.pageChoice = false;
    		angular.forEach($scope.resultList, function(row) {
    			row.SELECTED = false;
			});
    	}
    };
    
    //放行
    $scope.release = function() {
    	var data = $scope.resultList.filter(function(row) {
			return (row.SELECTED == true);
    	})
    	if(!data.length) {
    		$scope.showErrorMsg('無案件可覆核');
        } else {
        	var release_ids = [];
	    	angular.forEach($scope.resultList, function(row){
	    		if (row.SELECTED == true) {
	    			release_ids.push(row.COMPLAIN_LIST_ID);
	    		}
	    	});
        	
        	$confirm({text : '共有 ' + data.length + ' 筆客訴案件準備覆核，確認送出?'}, {size : 'sm'}).then(function() {
        		$scope.sendRecv("CRM990", "release", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'release_ids' : release_ids}, function(tota, isError) {
    				if (!isError) {
    					$scope.showMsg("ehl_01_cam150_001");	//放行成功
    					$scope.query();
    				}
    			});
    		});
        }
    }
	
	//新增
	$scope.add = function() {
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM990/CRM990_MAINTAIN.html',
			className: 'CRM990_MAINTAIN',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.newComplainSource = true;
			}]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful' || data.value === 'cancel' ){
				$scope.query();
			}
		});
	}
	
	//編輯 or 查詢客訴詳情
	$scope.maintain = function(row, act) {
		//取客訴詳情
		$scope.sendRecv("CRM990", "getDetail", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'complain_list_id': row.COMPLAIN_LIST_ID}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.errorMsg) {
					$scope.showErrorMsg(tota[0].body.errorMsg);
					return;
				} else {
					if(tota[0].body.resultList.length > 0) {
						var resultList = tota[0].body.resultList[0];
						var dialog = ngDialog.open({
							template: 'assets/txn/CRM990/CRM990_MAINTAIN.html',
							className: 'CRM990_MAINTAIN',
							showClose: false,
							controller: ['$scope', function($scope) {
								$scope.resultList = resultList;
								$scope.act = act;
								$scope.role_id = row.ROLE_ID;
								$scope.newComplainSource = false;
							}]
						});
						dialog.closePromise.then(function (data) {
							if(data.value === 'successful' || data.value === 'cancel' ){
								if (act == 'edit')
									$scope.query();
							}
						});
					}								
				}
			}
		});
	}
	
	//查詢客訴軌跡
	$scope.showComplainFlow = function(row) {
		var complain_list_id = row.COMPLAIN_LIST_ID;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM990/CRM990_FLOW.html',
			className: 'CRM990_FLOW',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.complain_list_id = complain_list_id;
			}]
		});
	}
	
	//列印
	$scope.exportPdf = function(row) {
		$scope.sendRecv("CRM990", "exportPdf", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'complain_list_id': row.COMPLAIN_LIST_ID}, function(tota, isError) {
			if (!isError) {
				
			}
		});
	}
	
	//列印備查簿
	$scope.exportResult = function() {
		$scope.sendRecv("CRM990", "exportResult", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
			}
		});
	}
	
	//取消結案
	$scope.revive = function(row) {
		$confirm({text: '是否取消結案此筆資料？ '}, {size: 'sm'}).then(function() {
			$scope.sendRecv("CRM990", "revive", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'complain_list_id' : row.COMPLAIN_LIST_ID}, function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('ehl_01_common_023');		//執行成功
					$scope.query();
				}
			});
		});
	}
});