/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS343Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS343Controller";
		$controller('PMSRegionController', {$scope: $scope});
		$scope.mappingSet['rtype'] = [];
		console.log(projInfoService.getRoleName().includes('主管'));
		
		
		if(projInfoService.getBranchID() == '000' && projInfoService.getAreaID() == 'null' && projInfoService.getRegionID() !=' null'){ // 業務處處長或以上
			$scope.mappingSet['rtype'].push(
					{LABEL: 'AO 贖回量', DATA: 1},
					{LABEL: '分行贖回量', DATA: 2},
					{LABEL: '營運區贖回量', DATA: 3},
					{LABEL: '業務處贖回量', DATA: 4}
			);
		}
		else if(projInfoService.getBranchID() == '000' && projInfoService.getAreaID() != 'null'){ // 營運區督導
			$scope.mappingSet['rtype'].push(
					{LABEL: 'AO 贖回量', DATA: 1},
					{LABEL: '分行贖回量', DATA: 2},
					{LABEL: '營運區贖回量', DATA: 3},
			);
		}
		else if(projInfoService.getBranchID() != '000' && projInfoService.getRoleName().includes('主管')){ // 理專
			$scope.mappingSet['rtype'].push(
					{LABEL: 'AO 贖回量', DATA: 1},
					{LABEL: '分行贖回量', DATA: 2}
			);
		}
		else if(projInfoService.getBranchID() != '000'){ // 理專
			$scope.mappingSet['rtype'].push(
					{LABEL: 'AO 贖回量', DATA: 1}
			);
		}
		
		
		/***TEST ORG COMBOBOX START***/
        var org = [];
       
        //選取月份下拉選單 --> 重新設定可視範圍       
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	if($scope.inputVO.sCreDate!=''&&$scope.inputVO.sCreDate!=undefined){ 	
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
	    
        $scope.dateChange();
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};					
		
		$scope.init = function(){
			$scope.inputVO = {					
					sCreDate: '',										
					region_center_id: '',
					branch_area_id: '',
					branch_nbr: '',
					ao_code: '',
					rptType: 1
        	};
			$scope.outputVO={totalList:[]};
			$scope.curDate = new Date();
			$scope.typeName = '-AO 贖回量';
			
			$scope.dateChange();
		};
	
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};
			
			var today = new Date();
			var pastNinetyDay = today.setDate(today.getDate()-90);
			$scope.minDate = pastNinetyDay;
			debugger;
		}
		$scope.inquireInit();
		
		$scope.rptTypeChange = function(){
			$scope.mappingSet['op'] = [];
			$scope.mappingSet['branch'] = [];
			$scope.mappingSet['aoemp'] = [];
			
			$scope.inquireInit();
			
			if($scope.inputVO.rptType == 1)
				$scope.typeName = '-AO 贖回量';
			else if($scope.inputVO.rptType == 2)
				$scope.typeName = '-分行贖回量';
			else if($scope.inputVO.rptType == 3)
				$scope.typeName = '-營運區贖回量';
			else if($scope.inputVO.rptType == 4)
				$scope.typeName = '-業務處贖回量';	
			else
				$scope.typeName = '';
		};
		
		// date picker
		$scope.bgn_sDateOptions = {

			maxDate: $scope.maxDate,
			//#0000375: 報表留存時間 三個月  實際操作是90天
			minDate: new Date(new Date().setDate(new Date().getDate() - 90))
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		// date picker end						
		
		//查詢
		$scope.query = function(){
			if($scope.inputVO.rptType==''){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇報表類型');
        		return;
        	}
        	
			if($scope.inputVO.sCreDate==''){
				$scope.showErrorMsg('欄位檢核錯誤:日期為必填欄位');
        		return;	
			}
			
			$scope.sendRecv("PMS343", "queryData", "com.systex.jbranch.app.server.fps.pms343.PMS343InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
//							if(tota[0].body.resultList.length == 0) {
//								$scope.paramList=[];
//								$scope.showMsg("ehl_01_common_009");
//	                			return;
//	                		}
							console.log(tota[0].body);
//							$scope.paramList = tota[0].body.resultList;
							$scope.paramList = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							$scope.outputVO.rt = $scope.inputVO.rptType;							
							return;
						}						
			});
		};
		
		//匯出
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS343", "export", "com.systex.jbranch.app.server.fps.pms343.PMS343OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};		
});
