/**================================================================================================
 @Description 分行人員與客戶資金往來異常報表
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS422Controller', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
        
	$controller('BaseController', {$scope: $scope});
    $controller('PMSRegionController', {$scope: $scope});

    $controller('CommonUtil', {$scope: $scope});
    $scope.controllerName = 'PMS422Controller';
    $scope.modelName = 'com.systex.jbranch.app.server.fps.pms422.PMS422InputVO';

    // filter
	getParameter.XML(["PMS.PMS422_SOURCE_OF_DEMAND", "PMS.NOTE_STATUS", "PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.PMS422_SOURCE_OF_DEMAND'] = totas.data[totas.key.indexOf('PMS.PMS422_SOURCE_OF_DEMAND')];
			$scope.mappingSet['PMS.NOTE_STATUS'] = totas.data[totas.key.indexOf('PMS.NOTE_STATUS')];
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	// ===
	
    /** 查詢初始化 **/
    $scope.inquireInit = () => {
        $scope.data = [];
        $scope.outputVO = {};
    };

    /** 初始化 **/
    $scope.init = () => {
        /** 初始日曆 **/
        $scope.summary = {};
        $scope.initDateOptions($scope.summary);

        /** InputVO 初始，預設日期為系統日 **/
        let defaultDate = new Date();
        
        var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 1);
		min_mon.setHours(0, 0, 0, 0);
		
        $scope.inputVO = {
            start: min_mon,
            end: defaultDate,
            memLoginFlag: String(sysInfoService.getMemLoginFlag()),
            
            /** 連動組織會用到的參數 **/
            region_center_id: undefined,
            branch_area_id: undefined,
            branch_nbr: undefined,
            
            reportDate: $filter('date')(defaultDate, 'yyyyMM'), 
            empID: '',
            noteStatus: undefined,
            outCustID: ''
        };
        
        $scope.RegionController_getORG($scope.inputVO);
        $scope.inquireInit();
        
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS422'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;
				
				$scope.uhrmRCList = [];
				$scope.uhrmOPList = [];

				if (null != tota[0].body.uhrmORGList) {
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					});	
					
					$scope.inputVO.uhrmRC = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.uhrmOP = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
			}						
		});
    };
    
    $scope.init();

    /** 資料處理 **/
    $scope.processCols = each => {
        /** 添加 User 可編輯的 Input 欄位，進而判斷與原欄位差異者，更新資料 **/
        each.EX_FLAG_INPUT = each.EX_FLAG;
        each.NOTE_INPUT  = each.NOTE;
        each.NOTE_INPUT2 = each.NOTE2;
        each.NOTE_INPUT3 = each.NOTE3;
        
        /** 將資料日期 String 型態轉為 Date 型態 **/
        each.CREATETIME = $scope.toJsDate(each.CREATETIME);
        each.SNAP_DATE = $scope.toJsDate(each.SNAP_DATE);
        
        /** 本日無異動的資料需要 Disabled **/
        each.noChange = each.EX_FLAG == 'X';
    };
    
    $scope.tabNumber = '0';
    
    $scope.retuenTabNumber = function(tabNumber) {			
		$scope.tabNumber = tabNumber;
	}

    /**
     * 查詢資料:RM與客戶往來
     */
    $scope.query = () => {
        $scope.sendRecv("PMS422", "queryData", $scope.modelName, $scope.inputVO, (tota, isError) => {
            if (!isError) {
                $scope.data = tota[0].body.resultList;
                $scope.outputVO = tota[0].body;

                if (!$scope.data.length)
                    $scope.showMsg("ehl_01_common_009");
            }
        });
    };
    
    /**
     * 查詢資料:RM與客戶往來又轉入關聯戶
     */
    $scope.query_V2 = () => {
        $scope.sendRecv("PMS422", "queryDataV2", $scope.modelName, $scope.inputVO, (tota, isError) => {
            if (!isError) {
                $scope.data = tota[0].body.resultList;
                $scope.outputVO = tota[0].body;

                if (!$scope.data.length)
                    $scope.showMsg("ehl_01_common_009");

                //$scope.data.forEach($scope.processCols);
            }
        });
    };

    /** 點擊查看明細 **/
    $scope.detail = row => {
        ngDialog.open({
            template:'assets/txn/PMS422/PMS422_DETAIL.html',
            className:'PMS422',
            controller:['$scope',function($scope) {
                $scope.inputVO = {
                    snapDate: row.SNAP_DATE,
                    emp_id: row.ID
                }
            }]
        });
    };
    
    $scope.openReview = function(type, row, logPri) {
    	var page = '';
		switch (logPri) {
			case 'BRH':
				page = 'PMS422';
				
				break;
			case 'UHRM':
				page = 'PMS422U';
				
				break;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/' + page + '/' + page + '_REVIEW.html',
			className: page,
			showClose: false,
            controller: ['$scope', function($scope) {
				$scope.row = row
			}]
		}).closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.showSuccessMsg('ehl_01_common_002');
				$scope.query_V2();
			}
		});
	};		

    /**
     * 檢查使用者編輯的資料是否有效
     */
    $scope.checkInvalid = data => {
        let invalid = data.map((each, index) => !($scope.allCompleted(each) || $scope.noValue(each))? index + 1: '')
            .filter(Boolean)
            .join('、');
        if (invalid) {
            $scope.showErrorMsg(`第 ${invalid} 筆，未勾選確認或未輸入欄位，不得儲存資料！`);
            return true;
        }
        return false;
    };

	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
	
    /** all input have value **/
    $scope.allCompleted = each => each.EX_FLAG_INPUT && each.NOTE_INPUT && each.NOTE_INPUT2 && each.NOTE_INPUT3 ;

    /** all input have no value **/
    $scope.noValue = each => !each.EX_FLAG_INPUT && !each.NOTE_INPUT && !each.NOTE_INPUT2 && !each.NOTE_INPUT3;

    /** 判斷是否有異動資料 **/
    $scope.hasEdited = each => each.EX_FLAG_INPUT != each.EX_FLAG || each.NOTE_INPUT != each.NOTE || each.NOTE_INPUT2 != each.NOTE2 || each.NOTE_INPUT3 != each.NOTE3 ;

    /** 將資料日期格式化為字串 yyyyMMdd 型式 **/
    $scope.formatSnapDate = each => each.SNAP_DATE = $filter('date')(each.SNAP_DATE, 'yyyyMMdd');
    /**
     * 儲存使用者編輯的資料
     *
     * * 主管檢核說明（Ａ）、初判異常轉法遵部調查（Ｂ）
     *      1. Ａ、B 兩者皆有或皆無才能執行儲存功能。有任何一項有值，其他無值將會出現警示訊息。
     *      2. A、B 有任一異動則執行更新邏輯。
     *
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		//var rowIndex;
		angular.forEach($scope.data, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				//debugger
				if (!row.NOTE2 || 
					!row.NOTE3 || 
					!row.EX_FLAG ||
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
			$scope.showErrorMsg("已勾選的查證方式、資金來源/帳戶關係、具體原因/用途、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};

        $scope.sendRecv("PMS422", "save", $scope.modelName, {editedList: $scope.updateList},
            (_, isError) => {
                if (!isError) {
                    $scope.showSuccessMsg('ehl_01_common_025');
//                    $scope.query();
                }
            });
    };

    // 匯出 Csv
    $scope.exportRPT = () => $scope.sendRecv("PMS422", "export", $scope.modelName, $scope.inputVO, () => {});
    
    // 匯出 Csv
    $scope.exportRPT_V2 = () => $scope.sendRecv("PMS422", "exportV2", $scope.modelName, $scope.inputVO, () => {});
});
