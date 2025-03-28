/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT200Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT200Controller";
		
		//狀態
		getParameter.XML(["IOT.MAPP_STATUS",'IOT.MAIN_STATUS'],function(totas){
			if(totas){
				$scope.mappingSet['IOT.MAPP_STATUS'] = totas.data[0].concat(totas.data[1]);	
			}
		});
		
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
		
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.priID == 'UHRM002') {
							$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
						} else {
							$scope.inputVO.uEmpID = '';
						}
					}
		});
		
		$scope.area_list = projInfoService.getAvailArea();
		$scope.bra_list = projInfoService.getAvailBranch();
		$scope.ao_code = sysInfoService.getAoCode();
				
        /***以下連動營運區.分行別***/		
		//分行資訊
		$scope.areaChange = function() {			
			$scope.inputVO.branchID = '';
			$scope.mappingSet['branch'] = [];
			angular.forEach($scope.bra_list, function(row, index, objs){				
				if(row.BRANCH_AREA_ID == $scope.inputVO.areaID)			
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
			if($scope.mappingSet['branch'].length == 1)
				$scope.inputVO.branchID = $scope.bra_list[0].BRANCH_NBR;
        };
				
      //營運區資訊
		$scope.regionChange = function() {			
			$scope.mappingSet['op'] = [];
			$scope.inputVO.areaID = '';
			angular.forEach($scope.area_list, function(row, index, objs){				
				$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});								
			});			
			if($scope.mappingSet['op'].length == 1){				
				$scope.inputVO.areaID = $scope.area_list[0].BRANCH_AREA_ID;							
			}
			$scope.areaChange();
        };
        
        var curDate = new Date();
        /**取工作日**/
		$scope.getBusDt = function(){
			$scope.sendRecv("IOT170", "getBusDate", "com.systex.jbranch.app.server.fps.iot170.IOT170InputVO", {},
					function(tota, isError) {
						if (!isError) {																							
							$scope.busDate = tota[0].body.busDate[0].BUS_DATE;	
							$scope.inputVO.sApplyDate = $scope.toJsDate($scope.busDate);
							$scope.inputVO.eApplyDate = curDate;
							$scope.outputVO = tota[0].body;														
							return;
						}						
			});
		}
		$scope.getBusDt();
				
        /**** 下拉連動END ****/
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};					
		
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eApplyDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sApplyDate || $scope.minDate;
		};
		// date picker end	
        
        $scope.init = function(){			
			$scope.inputVO = {
					areaID: '',
					sApplyDate: undefined,										
					eApplyDate: undefined,
					branchID: '',
					custID: '',
					caseID: '',
					insuredID: '',
					status: '',
					insPrdID: ''
        	};			 
			$scope.outputVO = [];
			$scope.resultList = [];
			
			if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
				$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'IOT200'}, function(tota, isError) {
					if (!isError) {
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
				
				$scope.inputVO.region_center_id = $scope.inputVO.uhrmRC;
				$scope.inputVO.branch_area_id = $scope.inputVO.uhrmOP;
			} else {
				$scope.regionChange();
			}
			
			$scope.limitDate();
		};	
		$scope.init();
		
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'caseID':
				$scope.inputVO.caseID = toUppercase_text;
				break;
			case 'custID':
				$scope.inputVO.custID = toUppercase_text;
				break;
			case 'insuredID':
				$scope.inputVO.insuredID = toUppercase_text;
				break;
			case 'insPrdID':
				$scope.inputVO.insPrdID = toUppercase_text;
				break;
			default:
				break;
			}
		}			
		
		//選擇上傳檔案
		$scope.uploadFile = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT200/IOT200_EXPORT.html',
				className: 'IOT200_EXPORT',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value === 'successful') {
					$scope.queryData();
	  			}
			});
		};
		
		//下載範例
		$scope.downloadExm = function() {
			$scope.sendRecv("IOT200", "getExample", "com.systex.jbranch.app.server.fps.iot200.IOT200InputVO", {},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		/** 查詢資料 **/
		$scope.queryData = function(active){
			
			if($scope.inputVO.sApplyDate != undefined || $scope.inputVO.eApplyDate != undefined || $scope.inputVO.caseID != '' || 
					$scope.inputVO.insuredID != '' || $scope.inputVO.insPrdID != '' || $scope.inputVO.custID != '' ){
				//日期查詢有一天未選擇
				if($scope.inputVO.sApplyDate == undefined || $scope.inputVO.eApplyDate == undefined){
					$scope.showErrorMsg('申請日期查詢須全選');
					return;
				}
				
				if($scope.inputVO.sApplyDate != undefined && $scope.inputVO.eApplyDate != undefined){
					
					if($scope.inputVO.eApplyDate < $scope.inputVO.sApplyDate){
						$scope.showErrorMsg('ehl_02_common_003');
						return;
					}
					//日期起迄區間不得超過31天
					var days = (new Date($scope.inputVO.eApplyDate) - new Date( $scope.inputVO.sApplyDate))/86400000;
					if(days > 31){
						$scope.showErrorMsg('日期起迄區間不得超過31天');
						return;
					}

				}
			}
			
			if($scope.inputVO.sApplyDate == undefined && $scope.inputVO.eApplyDate == undefined && $scope.inputVO.caseID == '' &&  $scope.inputVO.custID == '' &&
					$scope.inputVO.insuredID == '' && $scope.inputVO.insPrdID == '' ){
				$scope.showErrorMsg('ehl_01_common_022');
				return;
			}
		
			$scope.sendRecv("IOT200", "queryData", "com.systex.jbranch.app.server.fps.iot200.IOT200InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.outputVO = tota[0].body;														
							$scope.resultList = tota[0].body.resultList;
							if($scope.resultList.length == 0){
								$scope.showErrorMsg('ehl_01_common_009');
								return;
							}
							return;
						}						
			});
	};

});
