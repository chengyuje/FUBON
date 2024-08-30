/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS408Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS408Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.CHECK_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.CHECK_TYPE'] = totas.data[totas.key.indexOf('PMS.CHECK_TYPE')];
		}
	});
    //
	
	$scope.initLoad = function(){
		$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {}, function(totas, isError) {
         	if (totas.length > 0) {
           		$scope.ymList = totas[0].body.ymList;
          
         	};
		});
	}
	$scope.initLoad();
    
	//選取月份 --> 重新設定可視範圍
	$scope.dateChange = function(){
    	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
    	$scope.RegionController_getORG($scope.inputVO);
	};
    
    /***ORG COMBOBOX END***/
    
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};				
	
	$scope.init = function(){
		$scope.inputVO = {
				sCreDate: '', 
				endDate: '',
				reportDate: '',
				region_center_id  :'',  //區域中心
				branch_area_id      :'',//營運區	
				branch_nbr  :'',        //分行
				ao_code: '',			//理專
				dataList:[],             //上送更新資料
				memLoginFlag: String(sysInfoService.getMemLoginFlag())
    	};
		$scope.chkDateLimit = 'Y';
		$scope.outputVO={totalList:[]};
		$scope.paramList=[];
	};
	$scope.init();
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.paramList = [];
		$scope.originalList = [];
	}
	$scope.inquireInit();
	
	// date picker
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	
	// date picker end

	$scope.query = function(){
		var date = new Date();
		if($scope.inputVO.endDate == ''){
			$scope.inputVO.endDate = date;
		}

		if($scope.inputVO.sCreDate == ''){
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
    		return;
    	}
		
		$scope.sendRecv("PMS408", "queryData", "com.systex.jbranch.app.server.fps.pms408.PMS408InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList =[];
					$scope.totalData = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.originalList = angular.copy(tota[0].body.resultList);
				$scope.paramList = tota[0].body.resultList;
				
				angular.forEach($scope.paramList, function(row, index, objs){
					if(row.DATA_DATE < "20170825"){
						$scope.chkDateLimit = 'N';
					}
					// 20200629 Steven, 將此行Mark以解決冷靜期資料問題.
					// if(row.CUST_RISK_BEF == row.CUST_RISK_AFR &&(row.CUST_RISK_BEF != null &&  row.CUST_RISK_AFR != null)){
					// 		$scope.paramList.splice(index ,1 );
					// }
				});
				
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;				
				return;
			}
		});
	};

	$scope.save = function () {

		//判斷有無修改
		var checkData = true;
		var checkFlag = true;
		var chkRecordFlag = true;
		angular.forEach($scope.originalList, function(rowo, indexo, objso) {
			angular.forEach($scope.paramList, function(row, index, objs) {
				if (indexo == index) {
					//增加HR_ATTR有異動判斷，問題單:4154 BY 20180124-Willis
					if (chkRecordFlag) {
						switch (row.NOTE_TYPE) {
							case "I":
							case "A":
//								if ((rowo.RECORD_SEQ != row.RECORD_SEQ) && row.RECORD_SEQ.length != 12 && row.RECORD_YN == 'Y') {
//									chkRecordFlag = false;
//								}
								break;
							default:
								break;
						}
					}
					
					if ((rowo.HR_ATTR == null ? '' : rowo.HR_ATTR) != (row.HR_ATTR == null ? '' : row.HR_ATTR) ||
						(rowo.NOTE2 == null ? '' : rowo.NOTE2) != (row.NOTE2 == null ? '' : row.NOTE2) || 
						(rowo.NOTE == null ? '' : rowo.NOTE) != (row.NOTE == null ? '' : row.NOTE) ||
						(rowo.NOTE_TYPE == null ? '' : rowo.NOTE_TYPE) != (row.NOTE_TYPE == null || row.NOTE_TYPE == '' || row.NOTE_TYPE == undefined ? '' : row.NOTE_TYPE) 
//						rowo.RECORD_SEQ != row.RECORD_SEQ ||
						) {
						
						checkFlag = false; 
						
						
						if (!checkFlag && 
							(!row.HR_ATTR || 
							 (!row.NOTE_TYPE || (row.NOTE_TYPE == 'O' && !row.NOTE)) || 
							 !row.NOTE2) ) {
							
							checkData = false; 
							return;
						};
							
						return;
					}
				}
			});
		});
		
		if (!chkRecordFlag) {
			$scope.showMsg('查證方式若為電訪/系統查詢及電訪客戶，請輸入電訪錄音編號，電訪錄音編號需滿12碼。');
			return;
		}
		
		if(!checkData){
			$scope.showErrorMsg("專員沒有勸誘客戶提高風險屬性、查證方式、檢核說明，都必須輸入。");
    		return;
		};
		
		if (checkFlag) {
			$scope.showMsg('資料沒有做任何修改。');
			return;
		}
		
		$scope.sendRecv("PMS408", "save", "com.systex.jbranch.app.server.fps.pms408.PMS408InputVO", {'list':$scope.paramList, 'list2':$scope.originalList}, function(tota, isError) {
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);
        	}
			
        	if (tota.length > 0) {
        		$scope.showMsg('儲存成功');
        	};		
		});
	};
	
	
	$scope.exportRPT = function(rptVersion){
		$scope.inputVO.rptVersion = rptVersion;
		$scope.sendRecv("PMS408", "export", "com.systex.jbranch.app.server.fps.pms408.PMS408InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			
			if (tota.length > 0) {
        		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
	        	}
	        }
		});
	};
	
	$scope.remind = function () {
		$confirm({
          title: "提醒",
          text: '通報異常-專員勸誘客戶提高KYC情事'
        }, {
          size: '200px'
        }).then(function() {});
	}
	
    $scope.openDetail = function(row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS408/PMS408_DETAIL.html',
			className: 'PMS408_DETAIL',
			showClose: false,
            controller: ['$scope', function($scope) {
				$scope.row = row
            }]
		});
	};		
});
