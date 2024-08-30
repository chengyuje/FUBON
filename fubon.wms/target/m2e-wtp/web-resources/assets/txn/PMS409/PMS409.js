/* 
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS409Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS409Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        console.log(mm);
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<36; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
		// combobox
		getParameter.XML(["KYC.OUT_ACCESS", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.OUT_ACCESS = totas.data[totas.key.indexOf('KYC.OUT_ACCESS')];
				$scope.YES_NO = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});     
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		/*** 可視範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可是範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可視範圍  JACKY共用版  END***/
		
		$scope.init = function(){
			$scope.inputVO = {					
				sCreDate        : '',	//年月		
				region_center_id: '',   //區域中心
				branch_area_id  : '',   //營運區
				branch_nbr      : '',   //分行
				ao_code         : '', 
				memLoginFlag    : String(sysInfoService.getMemLoginFlag())
        	};			
			$scope.curDate = new Date();
			$scope.outputVO={totalList:[]};
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
			$scope.outputVO = {totalList:[]};
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「資料統計月份」');
        		return;
        	}

			$scope.sendRecv("PMS409", "queryData", "com.systex.jbranch.app.server.fps.pms409.PMS409InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		
		$scope.save = function () {			
			$scope.sendRecv("PMS409", "save", "com.systex.jbranch.app.server.fps.pms409.PMS409InputVO", {'list':$scope.paramList, 'list2':$scope.originalList},
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            		return;
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.query();
		            	};		
			});
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS409", "export", "com.systex.jbranch.app.server.fps.pms409.PMS409OutputVO", $scope.outputVO,
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
