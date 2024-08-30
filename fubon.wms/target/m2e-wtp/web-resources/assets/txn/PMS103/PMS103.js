'use strict';
eSoafApp.controller('PMS103Controller', function($rootScope, $scope, $controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService, getParameter,$filter) {
	
	$scope.controllerName = "PMS103Controller";
	
	$controller('BaseController', {$scope: $scope});
	$controller('PPAPController', {$scope: $scope});
	
	// 繼承共用的組織連動選單
	$controller('RegionController', {$scope: $scope});
	$controller('PMSRegionController', {$scope: $scope});

	/***初始化***/
    $scope.init = function() {
    	var sTimeMM = (new Date().getMonth() + 1) < 10 ? '0' + (new Date().getMonth() + 1) : (new Date().getMonth() + 1);
    	
    	$scope.inputVO = {					
			cust_id          : '',
			emp_id           : '',
			
			region_center_id : '', 	 	//區域中心
			branch_area_id   : '',    	//營運區
			branch_nbr       : '',    	//分行
			ao_code          : '',    	//理專
			reportDate       : null,
			sTime            : new Date().getFullYear() + '' + sTimeMM
    	};

    	$scope.inputVO.reportDate = new Date();  //20190122 by Willis 修正BUG，這裡請放有效的日期，要不然會掛。
    	$scope.RegionController_getORG($scope.inputVO);  
//    	$scope.typeRequired = false;
//    	$scope.useMonth = true;
    	
    	$scope.sendRecv("PMS103", "getYmList", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.ymList = tota[0].body.ymList;
				if ($scope.ymList != null && $scope.ymList.length > 0) {
		           $scope.currentYM = tota[0].body.currentYM;
		           
		           //#0000375: 報表留存時間 未來三個月&過去三個月
		           $scope.ymList.splice(0,2);
		           $scope.ymList.splice(7);
		        }
			}
	    });
	};
	
    $scope.init();
	
    /** 查詢 **/
    $scope.inquire = function() {
    	if ($scope.inputVO.sTime == null || $scope.inputVO.sTime == undefined || $scope.inputVO.sTime == '')	{
    		$scope.showErrorMsg('欄位檢核錯誤:必輸條件(*)必須輸入！！');
    		return;
    	}

		$scope.sendRecv("PMS103", "queryData", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.paramList = [];
				
				//沒有銷售目標 就為0
				if (tota[0].body.tarList.length != 0)	
					$scope.SALETARGET = tota[0].body.tarList[0].SALETARGET;
				
				if (tota[0].body.resultList.length == 0) {	
					//總合計歸0
					$scope.paramList = [];
					$scope.outputVO = {};
					$scope.SUM_PLANAMT = 0;
					$scope.SUM_EST_AMT = 0;
					$scope.SUM_EST_EARNINGS = 0;
					$scope.showMsg("ehl_01_common_009");
					
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;	//結果							
				$scope.outputVO = tota[0].body;
				$scope.PLAN_AMT = 0;
				$scope.EST_AMT = 0;                       	//分頁合計
				$scope.EST_EARNINGS = 0;                    //分頁合計
				
				//查詢完一次
				$scope.totalFlag = false;
				
				//金流金額
				for (var i = 0; i < $scope.paramList.length; i++) {
					if ($scope.paramList[i].PLAN_AMT == null) {
						$scope.paramList[i].PLAN_AMT = 0;
					}
					
					$scope.EST_AMT += $scope.paramList[i].EST_AMT;
			        $scope.EST_EARNINGS += $scope.paramList[i].EST_EARNINGS;

			        $scope.inputVO.CUST_ID = '';
				}
				
				angular.forEach($scope.paramList, function(row, index, objs){
					row.CUST_IDS = row.CUST_ID.substring(0, 4) + "***" + row.CUST_ID.substring(7, 10);
				});	
				
				return;
			}
		});	
    }
	
	//filter
	getParameter.XML(["PMS.SALE_PLAN_PTYPE", "PMS.SALE_PLAN_SRC"], function(totas) {
		if (totas) {				
			$scope.mappingSet['PMS.SALE_PLAN_PTYPE'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_PTYPE')];
			$scope.mappingSet['PMS.SALE_PLAN_SRC'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_SRC')];
		}
	});
	
    /*** crm211導頁面 ***/
    $scope.crm211 = function(){
    	$rootScope.menuItemInfo.url = "assets/txn/CRM211/CRM211.html";	
    }
    
    $scope.goCRM211 = function () {
		var custID = $scope.inputVO.custID;
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM211/CRM211_ROUTE.html',
			className: 'CRM211',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.txnName = "我的客戶查詢";
        		$scope.routeURL = 'assets/txn/CRM211/CRM211.html';
            }]
		}).closePromise.then(function (data) {
			$scope.inquire();
		});
	};
        
    /*** 總計 ***/
	$scope.getSUM = function(row) {
		$scope.EST_AMT = 0;                            
		$scope.EST_EARNINGS = 0;                       
		
		for(var i = 0; i < row.length; i++) {
			$scope.EST_AMT += row[i].EST_AMT ;
			$scope.EST_EARNINGS += row[i].EST_EARNINGS ;
		}
	}	
	
    /*** 刪除 ***/
    $scope.del = function (row) { 
    	switch (sysInfoService.getPriID()) {
	    	case "009":		// 業務主管
	    	case "011": 	// 分行主管
	    	case "001": 	// AFC
	    	case "002": 	// FC
	    	case "003": 	// FCH
	    	case "004":		// 消金PS
	    	case "004AO": 	// 個金AO
	    		break;
	    	default:
	    		$scope.showMsg("非理專身分/理專一級主管身分無法使用刪除銷售計劃");
	    		return;
	    		break;
    	}
    	
    	var nowYYYYMM = new Date().getFullYear() + "" + ((new Date().getMonth() + 1) < 10 ? '0' + (new Date().getMonth() + 1) : (new Date().getMonth() + 1));

		if (row.C_YYYYMM.trim() < nowYYYYMM) {
	        $scope.showErrorMsg('無法刪除當月之前銷售計劃月份資料');
        	return;
	    }

    	$confirm({text: '請確定是否刪除此筆資料？'}, {size: 'sm'}).then(function() {
	    	$scope.sendRecv("PMS103", "delRes", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", {'CUST_ID': row.CUST_ID, 'SEQ': row.SEQ, 'ao_code': row.AO_CODE}, function(tota, isError) {
    			if (!isError) {
    				$scope.inquire();
    				$scope.showMsg("ehl_01_common_003");
    			}
    		});
    	});
    }
});
