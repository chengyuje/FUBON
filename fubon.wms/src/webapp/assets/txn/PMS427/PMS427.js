'use strict';
eSoafApp.controller('PMS427Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $compile) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS427Controller";;
	
	// 繼承共用的組織連動選單
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
	// ===
	
	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 13; i++){
    	mm = mm -1;
    	if(mm == 0){
    		mm = 12;
    		yr = yr-1;
    	}
    	if(mm < 10)
    		strmm = '0' + mm;
    	else
    		strmm = mm;        		
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA: yr + '' + strmm
    	});        
    }

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function() {
	   if($scope.inputVO.sCreDate != ''){
	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   } else {   
		   $scope.inputVO.sCreDate = '201701';
		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;    
		   $scope.inputVO.sCreDate = '';
	   } 
    	$scope.inputVO.dataMon = $scope.inputVO.sCreDate; 
    }; 
    
	// 初始化
	$scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());

		$scope.inputVO = {
			sCreDate			: '',
			region_center_id	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			exportList          : [], 
			memLoginFlag        : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.dateChange();
		
		$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
		$scope.RegionController_setName($scope.test);
		
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS427'}, function(tota, isError) {
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
	
	//資料查詢
	$scope.query = function() {	
		if ($scope.inputVO.sCreDate == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		
		$scope.sendRecv("PMS427", "query", "com.systex.jbranch.app.server.fps.pms427.PMS427InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
			}				
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.exportList = $scope.resultList;
		
		$scope.sendRecv("PMS427", "export", "com.systex.jbranch.app.server.fps.pms427.PMS427InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
	$scope.updateFlag = function (row){
		row.UPDATE_FLAG = 'Y';			
	}
    
    /** 添加 User 可編輯的 Input 欄位，進而判斷與原欄位差異者，更新資料 **/
    $scope.addInputCol = each => {
        each.HR_ATTR_INPUT = each.HR_ATTR;
        each.NOTE_INPUT = each.NOTE;
        each.NOTE2_INPUT = each.NOTE2;
        each.NOTE3_INPUT = each.NOTE3;
    };
    
    /**
     * 儲存使用者編輯的資料
     */
    $scope.save = () => {
		$scope.updateList = [];
		var checkData = true;
		
		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG == 'Y') {
				if (!row.HR_ATTR || 
					!row.NOTE2 ||
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
			$scope.showErrorMsg("已勾選的查證方式、帳戶關係。如客戶彼此不認識請了解客戶留存原因是否合理、初判異常轉法遵部調查，都必須輸入。");
    		return;
		};
		
		$scope.sendRecv("PMS427", "save", 'com.systex.jbranch.app.server.fps.pms427.PMS427InputVO', {list: $scope.updateList}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
                $scope.query();
            }
        });
    };
    
	$scope.getExample = function() {
		$scope.sendRecv("PMS427", "getExample", "com.systex.jbranch.app.server.fps.pms427.PMS427InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.updateAO84MList = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS427", "updateAO84MList", "com.systex.jbranch.app.server.fps.pms427.PMS427InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");

					return;
				} else {
				}
			});
		});
		
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updateAO84MList(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
});