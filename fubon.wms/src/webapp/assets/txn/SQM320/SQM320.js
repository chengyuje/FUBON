'use strict';
eSoafApp.controller('SQM320Controller', function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter, $timeout) {
	$controller('BaseController', {$scope: $scope});

	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.controllerName = "SQM320Controller";
	
	getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "SQM.CUST_STATUS_LIST", "SQM.REVIEW_STATUS_LIST"], function(totas) {
		if (totas) {
			$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
			$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			$scope.CUST_STATUS_LIST = totas.data[totas.key.indexOf('SQM.CUST_STATUS_LIST')];
			$scope.REVIEW_STATUS_LIST = totas.data[totas.key.indexOf('SQM.REVIEW_STATUS_LIST')];
		}
	});
	
	$scope.loginBranchID = projInfoService.getBranchID();
	$scope.loginID = projInfoService.getUserID();
	$scope.roleID = projInfoService.getRoleID();
	
	$scope.getORG = function(){
		var NowDate = new Date();
    	//設定回傳時間
    	$scope.inputVO.reportDate = NowDate;
    	//可視範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO).then(function(data){
    		if($scope.loginBranchID != '000') {
    			$scope.inputVO.branch_nbr = $scope.loginBranchID;
    			$scope.ChangeBranch();
    			$timeout(function(){	
    				//確認如果是連動來的，請自動查詢待處理事項。
    				if('Y' == $scope.connector('get','LinkFlag_SQM320')){
    					$scope.inputVO.sCreDate = null;
    					$scope.inputVO.eCreDate = null;
    					$scope.inputVO.emp_id = '';
    					$scope.inputVO.review_status = "02";
    					$scope.query();
    					$scope.connector('set','LinkFlag_SQM320','');
    				}
    				
    				$scope.openVisitTotal();             	
    			}, 1000);
    		} else {
    			//確認如果是連動來的，請自動查詢待處理事項。
				if('Y' == $scope.connector('get','LinkFlag_SQM320')){
					$scope.inputVO.sCreDate = null;
					$scope.inputVO.eCreDate = null;
					$scope.inputVO.emp_id = '';
					$scope.inputVO.review_status = "02";
					$scope.query();
					$scope.connector('set','LinkFlag_SQM320','');
				}
    		}
    	});	
	}
    
    $scope.init = function(){
    	var LastDate = new Date();
    	LastDate.setMonth(LastDate.getMonth() - 1, 1);
	    var strLastMon='';
	    var strQtr='';
	    $scope.mappingSet['timeE'] = [];
	    
	    //資料日期區間限制為兩年內資料
	    for (var i = 0; i <= 24; i++) {
	    	strLastMon = LastDate.getMonth();
	    	strLastMon++;
	    	if (strLastMon < 10 ) {
	    		strLastMon = '0' + strLastMon;
	    	} else {
	    		strLastMon = '' + strLastMon;
	    	}

	    	switch (strLastMon) {
				case '03':
					strQtr = 'Q1';
					$scope.mappingSet['timeE'].push({
			    		LABEL: LastDate.getFullYear() + '/' + strQtr,
			    		DATA: LastDate.getFullYear() + '' + strLastMon
			    	}); 
					break;
				case '06':
					strQtr = 'Q2';
					$scope.mappingSet['timeE'].push({
			    		LABEL: LastDate.getFullYear() + '/' + strQtr,
			    		DATA: LastDate.getFullYear() + '' + strLastMon
			    	}); 
					break;	
				case '09':
					strQtr = 'Q3';
					$scope.mappingSet['timeE'].push({
			    		LABEL: LastDate.getFullYear() + '/' + strQtr,
			    		DATA: LastDate.getFullYear() + '' + strLastMon
			    	}); 
					break;	
				case '12':
					strQtr = 'Q4';
					$scope.mappingSet['timeE'].push({
			    		LABEL: LastDate.getFullYear() + '/' + strQtr,
			    		DATA: LastDate.getFullYear() + '' + strLastMon
			    	}); 
					break;
	    	}
	    	
	    	//每一筆減一個月，倒回去取前六個月內日期區間
	    	LastDate.setMonth(LastDate.getMonth() - 1);
		}
		
		$scope.inputVO = {
				region_center_id: '',   //區域中心
				branch_area_id: '' ,    //營運區
				branch_nbr: '',       //分行	
				yearQtr: $scope.mappingSet['timeE'][0].DATA,
				cust_status: '',
				review_status: '',
				memLoginFlag: String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.rptDate = '';
		$scope.totalData = [];
		$scope.paramList = [];
		$scope.outputVO={totalList:[]};	
		$scope.getORG();
		
		$scope.inputVO.funcPage = "SQM320";
		if ($scope.inputVO.memLoginFlag.toLowerCase().startsWith('uhrm') && $scope.inputVO.memLoginFlag != 'UHRM') {
			$scope.inputVO.funcPage += "U";
		} 
		
		// 單號 : 5614
		// role_id 為 A146 || A164 才可覆核
		$scope.isSupervisor = false;
		if ($scope.inputVO.memLoginFlag.toLowerCase().startsWith('uhrm') && $scope.inputVO.memLoginFlag != 'UHRM') {
			switch ($scope.roleID) {
				case "R013":
					$scope.isSupervisor = true;
					break;
				default:
					$scope.isSupervisor = false;
				break;
			}
		} else {
			switch ($scope.roleID) {
				case "A164":
				case "A146":
				case "A301":
					$scope.isSupervisor = true;
					break;
				default:
					$scope.isSupervisor = false;
				break;
			}
		}
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
		if($scope.inputVO.yearQtr == undefined || $scope.inputVO.yearQtr == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
    		return;
		}
		
		$scope.sendRecv("SQM320", "queryData", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.totalData = [];
					$scope.outputVO = {};
					$scope.showMsg("ehl_01_common_009");
					
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.REVIEW_FLAG = '';						

				angular.forEach($scope.paramList, function(row, index, objs){
					// 單號 : 5478
					// 可覆核的條件
					if(row.REVIEW_DATE == null && row.REVIEWER_FLAG == 'Y' 
						&& row.AUDITOR != $scope.loginID && row.AUDITOR != null
						&& row.AUDITED == 'Y'){
						 
						if ($scope.isSupervisor) {
							$scope.REVIEW_FLAG = 'Y';
						} else{
							$scope.REVIEW_FLAG = '';
						}
					}
				});	
				$scope.totalData = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;	
				
				return;
			}						
		});
	};
	
	$scope.openVisitTotal = function(){
    	if($scope.inputVO.yearQtr == undefined || $scope.inputVO.yearQtr == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:年季必要輸入欄位');
    		return;
		}
		
    	var yearQtr = $scope.inputVO.yearQtr;
		var branch_nbr = $scope.inputVO.branch_nbr;
		var memLoginFlag = $scope.inputVO.memLoginFlag;
		var uhrmRC = $scope.inputVO.uhrmRC;
		var uhrmOP = $scope.inputVO.uhrmOP;
		console.log("uhrmRC:" + $scope.inputVO.uhrmRC);
		console.log("uhrmOP:" + $scope.inputVO.uhrmOP)

		var dialog = ngDialog.open({
			template: 'assets/txn/' + $scope.inputVO.funcPage + '/' + $scope.inputVO.funcPage + '_visitTotal.html',
			className: $scope.inputVO.funcPage,
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.yearQtr = yearQtr,
				$scope.branch_nbr = branch_nbr,
				$scope.memLoginFlag = memLoginFlag,
            	$scope.uhrmRC = uhrmRC,
            	$scope.uhrmOP = uhrmOP
            }]
		});
		
		dialog.closePromise.then(function (data) {
			if(data.value != 'cancel') {			
				switch (data.value.type) {
					case "emp_id":
						$scope.inputVO.review_status = "";	//查該理專
						$scope.inputVO.cust_status = "";	//查一般客戶+特殊客戶
						break;
					case "audited":
						$scope.inputVO.review_status = (data.value.reviewStatus == "Y" ? "03" : "02");	//已訪查/已覆核
						$scope.inputVO.cust_status = "";	//查一般客戶+特殊客戶
						break;
					case "sp_cust_yn":
						$scope.inputVO.review_status = (data.value.reviewStatus == "Y" ? "03" : "02");	//已訪查/已覆核
						$scope.inputVO.cust_status = "02";	// 查特殊客戶
						break;
				}
				
				$scope.inputVO.emp_id = data.value.emp_id;
				
				$scope.query();
			}
		});
    };
    
    $scope.openReview = function(type, row) {
    	var yearQtr = $scope.inputVO.yearQtr;
		var branch_nbr = $scope.inputVO.branch_nbr;
		var auditor = row.AUDITOR;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/' + $scope.inputVO.funcPage + '/' + $scope.inputVO.funcPage + '_review.html',
			className: $scope.inputVO.funcPage,
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.type = type,
				$scope.row = row,
				$scope.auditor = auditor
            }]
		});
		
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.showSuccessMsg('ehl_01_common_002');
				$scope.query();
			}
		});
	};		
	
	$scope.select_all = function(){
		if ($scope.inputVO.select_all) {
			angular.forEach($scope.paramList, function(row){
				row.choice = true;
			});
		} else{
			angular.forEach($scope.paramList, function(row){
				row.choice = false;
			});
		}
	}
    
    $scope.select_false = function(choice){
    	if(choice == false){
			$scope.inputVO.select_all = false;
    	}
    }
    
    //資料覆核
	$scope.review = function() {
		$scope.reviewList = [];
		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.choice == true){
				$scope.reviewList.push($scope.paramList[index]);
			}
		});
		
		$scope.inputVO.reviewList = $scope.reviewList;
		$scope.sendRecv("SQM320", "reviewData", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO,function(tota,isError){
        	if (isError) {
        		$scope.showErrorMsg(tota[0].body.msgData);
        	}
        	if (tota.length > 0) {
        		$scope.showSuccessMsg('ehl_01_common_001');
        		$scope.query();
        	};
		});
	};
	
	//匯出
	$scope.exportRPTNew = function(){
		if ($scope.inputVO.yearQtr == undefined || $scope.inputVO.yearQtr == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:匯出動作，年季必要輸入欄位');
    		return;
    	}
		
		if (!($scope.inputVO.memLoginFlag.toLowerCase().startsWith('uhrm') && $scope.inputVO.memLoginFlag != 'UHRM')) {
			if($scope.inputVO.branch_nbr == undefined || $scope.inputVO.branch_nbr == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:匯出動作，分行必要輸入欄位');
        		return;
        	}
		} 
    	
		$scope.sendRecv("SQM320", "exportRPTNew", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			
			if (tota.length > 0) {
        		if(tota[0].body.resultList == null) {
					$scope.showMsg("匯出成功!");
        			return;
        		}
        	};
		});
	};
	
	//匯出舊報表
	$scope.exportRPT = function(){
		if($scope.inputVO.yearQtr == undefined || $scope.inputVO.yearQtr == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:匯出動作，年季必要輸入欄位');
    		return;
    	}
		
		if (!($scope.inputVO.memLoginFlag.toLowerCase().startsWith('uhrm') && $scope.inputVO.memLoginFlag != 'UHRM')) {
        	if($scope.inputVO.branch_nbr == undefined || $scope.inputVO.branch_nbr == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:匯出動作，分行必要輸入欄位');
        		return;
        	}
		}
		
		$scope.sendRecv("SQM320", "exportRPT", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			if (tota.length > 0) {
        		if(tota[0].body.resultList == null) {
					$scope.showMsg("匯出成功!");
        			return;
        		}
        	};
		});
	};

    $scope.del = function(row, type){ 
		$scope.deleteList = [];
		$scope.deleteList.push(row);
		$scope.inputVO.deleteList = $scope.deleteList;
		$scope.inputVO.emp_id = row.EMP_ID;
		$scope.inputVO.up_kyc_yn = row.UP_KYC_YN;
		$scope.inputVO.delType = type;
		
		var varText = (type == 'D' ? '是否刪除此筆客戶資料？' : '是否清除該客戶訪查資料？');
		
		$confirm({text: varText, text1: "客戶ID : "  + row.CUST_ID, text2: "客戶姓名 :  " + row.CUST_NAME}, {size: 'sm'}).then(function() {
           	$scope.sendRecv("SQM320", "deleteData", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError){
        		if(isError){
        			$scope.showErrorMsg(tota[0].body.msgData);
        		}
        		
                if (tota.length > 0) {
                	switch (type) {
	                	case "D":
	                    	$scope.showSuccessMsg('ehl_01_common_003');
	                		break;
	                	case "R":
	                		$scope.showSuccessMsg('ehl_01_common_023');
	                		break;
                	}
                	
                	$scope.inputVO.deleteList = [];
            		$scope.inputVO.emp_id = '';
            		$scope.inputVO.up_kyc_yn = '';
            		$scope.inputVO.delType = '';
                	
                	$scope.query();
                };
        	});
		});	
	};
	
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
	};
});
