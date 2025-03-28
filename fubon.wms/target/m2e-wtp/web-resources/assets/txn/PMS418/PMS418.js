/**================================================================================================
 @Description 理財戶同一 IP 交易警示報表 
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS418Controller', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
    
	$controller('BaseController', {$scope: $scope});
    $controller('PMSRegionController', {$scope: $scope});

    /** 載入 CommonUtil **/
    $controller('CommonUtil', {$scope: $scope});
    $scope.controllerName = 'PMS418Controller';
    $scope.modelName = 'com.systex.jbranch.app.server.fps.pms418.PMS418InputVO';

    // filter
	getParameter.XML(["PMS.CHECK_TYPE", "PMS.CUST_BASE_IP"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
			$scope.mappingSet['PMS.CUST_BASE_IP'] = totas.data[totas.key.indexOf('PMS.CUST_BASE_IP')];
		}
	});
    //
	
    /** 查詢初始化 **/
    $scope.inquireInit = () => {
        $scope.ipData = [];
        $scope.outputVO = {};
    };

    /** 初始化 **/
    $scope.init = () => {
        /** 初始日曆 **/
        $scope.summary = {};
        $scope.initDateOptions($scope.summary);

        /** InputVO 初始，預設日期為系統日前一日 **/
        let defaultDate = new Date();
        defaultDate.setDate(defaultDate.getDate() - 1);

        $scope.inputVO = {
            sCreDate        : undefined,
            eCreDate        : defaultDate,
            region_center_id: undefined,
            branch_area_id  : undefined,
            branch_nbr      : undefined,
            reportDate      : $filter('date')(defaultDate, 'yyyyMM'),
            memLoginFlag    : String(sysInfoService.getMemLoginFlag()),
            ipAddrSearch    : '',
            custIDSearch    : '',
            noteStatus      : undefined
        };
        $scope.RegionController_getORG($scope.inputVO);
        $scope.inquireInit();
        
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS418'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;
			}						
		});
    };
    $scope.init();
    
	$scope.updateFlag = function (row) {
		row.UPDATE_FLAG = 'Y';			
	}
	
    /** 查詢資料 **/
    $scope.query = function() {
    	$scope.sendRecv("PMS418", "queryData", $scope.modelName, $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.ipData = tota[0].body.resultList;
                $scope.outputVO = tota[0].body;

                if (!$scope.ipData.length)
                    $scope.showMsg("ehl_01_common_009");
			}						
    	});
    }; 
    
    /** 點擊查看明細 **/
    $scope.detail = row => {
        ngDialog.open({
            template:'assets/txn/PMS418/PMS418_DETAIL.html',
            className:'PMS418_DETAIL',
            controller:['$scope',function($scope) {
                $scope.inputVO = {
            		txnDay: row.TXN_DAY,
            		taskNM: row.TASK_NM,
            		ipAddr: row.IP_ADDR,
            		custID: row.CUST_ID
                }
            }]
        });
    };
    
    /**
     * 儲存使用者編輯的資料
     *
     * * 主管確認欄位（Ａ）、專員代為操作（Ｂ）、備註欄（Ｃ）。
     *      1. Ａ、B、C 三者皆有或皆無才能執行儲存功能。有任何一項有值，其他無值將會出現警示訊息。
     *      2. A、B、C 三者有任一異動則執行更新邏輯。
     *
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		
		angular.forEach($scope.ipData, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				if ((!row.CUST_BASE || // 客戶背景或關係需填寫
					 (row.CUST_BASE == 'O' && !row.NOTE2) // 客戶背景或關係若為其它，請補充查證方式
					) || 
					!row.NOTE3 || 
					!row.EMP_DLG ||
					(!row.NOTE_TYPE || // 查證方式需填寫
					 (row.NOTE_TYPE == 'O' && !row.NOTE) || // 查證方式若為其它，請補充查證方式
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && !row.RECORD_SEQ && row.RECORD_YN == 'Y') || // 查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號
					 ((row.NOTE_TYPE == 'I' || row.NOTE_TYPE == 'A') && row.RECORD_SEQ && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') // 查證方式若為電訪/系統查詢及電訪客戶，電訪錄音編號需滿12碼
					)) {
					checkData = false; 
					return;
				};
				
				$scope.updateList.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的查證方式、客戶背景或關係、具體原因或用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS418", "save", $scope.modelName, {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
//                $scope.query(); // 2022-04-29 : mark by ocean : WMS-CR-20220302-01_因應金檢查核及品保官會議，擬優化業管系統內控報表 : 分行填寫檢核說明並儲存成功後，頁面停留於原頁碼(不會重新查詢)
            }
        });
    };

    /** 匯出 Csv **/
    $scope.exportRPT = () => $scope.sendRecv("PMS418", "export", $scope.modelName, $scope.inputVO, () => {});
    
    /** 上傳排除IP **/
	$scope.updExcludeIP = function(){
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS418/PMS418_EXCLUDEIP.html',
			className: 'PMS418',
			showClose: false
		}).closePromise.then(function(data) {
			if(data.value.success == 'success'){
			}
		});
	}

	$scope.setDefaultVal = function (row) {
		if (row.NOTE3 == undefined || row.NOTE3 == null || row.NOTE3 == '') {
			switch(row.CUST_BASE) {
				case "M":
				case "PC":
				case "BS":
				case "G":
					row.NOTE3 = '經判斷為親屬關係，確認為客戶自主行為，非RM代操。';
					row.EMP_DLG = 'N';
					break;
				case "B":
					row.NOTE3 = '經判斷為公司及負責人關係，確認為客戶自主行為，非RM代操。';
					row.EMP_DLG = 'N';
					break;
				case "O":
					row.NOTE3 = '';
					break;
				default:
					row.NOTE3 = '';
					break;
			}
		}
	}
});
