/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS402Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS402Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
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
        
     
        /*** 可示範圍  JACKY共用版  START ***/
        $scope.role_id = projInfoService.getRoleID();
        
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate=='')
        		$scope.inputVO.sCreDate='197001';    //如果請選擇   初始1970年     
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate+"20";    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/
        

        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.inputVO = {		
					sCreDate:'',			 //年月
					region_center_id: '',   //區域中心
					branch_area_id: '' ,   //營運區
					branch_nbr: '',	     //分行	  			
					ao_code: ''          //理專
        	};
			$scope.curDate = new Date();
			$scope.outputVO={totalList:[]};
			$scope.paramList = [];
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};
		}
		$scope.inquireInit();
		
		
		$scope.save = function () {	
			$scope.sendRecv("PMS402", "save", "com.systex.jbranch.app.server.fps.pms402.PMS402InputVO", {'list':$scope.paramList, 'list2':$scope.originalList},
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            		return;
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.inputVO.currentPageIndex=$scope.outputVO.currentPageIndex || 0;
		            		$scope.query();
		            	};		
			});
		};
		
		$scope.query = function(){
			
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
        		return;
        	}
			
			$scope.sendRecv("PMS402", "queryData", "com.systex.jbranch.app.server.fps.pms402.PMS402InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
						
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.IDS=row.ID.substring(0, 4)+"****"+row.ID.substring(8, 10);
								
								//作業主管能做的資料 by 2017/11/29 willis 需求變更
								if($scope.role_id =='A150' || $scope.role_id == 'ABRF'){
									if(row.ROLE_FLAG == 'BR'){
										row.DISABLE_ROLE_FLAG = 'Y';
									}
								}
								//分行主管能做的資料 by 2017/11/29 willis 需求變更
								if($scope.role_id =='A161' || $scope.role_id == 'A149' || $scope.role_id =='ABRU' || $scope.role_id =='A308'){
									if(row.ROLE_FLAG == 'OP'){
										row.DISABLE_ROLE_FLAG = 'Y';
									}
								}
							});	
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;	
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			angular.forEach($scope.outputVO.totalList, function(row, index, objs){
				angular.forEach($scope.mappingSet['FPS.CURRENCY'] , function(row2, index2, objs2){
					if(row2.DATA==row.CRCY_TYPE)
						row.CRCY_TYPE=row2.LABEL;
				});
				row.ID=row.ID.substring(0, 4)+"****"+row.ID.substring(8, 10);  //隱藏身分正  後四碼
			}); 
			
		
			$scope.sendRecv("PMS402", "export", "com.systex.jbranch.app.server.fps.pms402.PMS402OutputVO", $scope.outputVO,
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
