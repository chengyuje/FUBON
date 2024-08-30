/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS715Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS715Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			$scope.inputVO = {
					sTime            : '',
					yearMon          : '',
					yearMonOp        : '',
					VERSION          : '',
					role             : '',
					openFlag         : '',
					subProjectSeqId  :''
			}; 
			var voHead = {'param_type': 'FUBONSYS.HEADMGR_ROLE', 'desc': false};
        	$scope.requestComboBox(voHead, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE'] = totas[0].body.result;
        			$scope.role = 0;
        			for(var key in projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.role = 1;
        	    		}
        	    	}
        	    	$scope.inputVO.role = $scope.role;
        		}
	    	});
            $scope.paramList = [];
            $scope.subProjectSeqId = [];
            $scope.inquireRoleList = [];
            $scope.showList = [];
            $scope.showList2 = [];
            $scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			var FCvo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
        	$scope.requestComboBox(FCvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
			var FCHvo = {'param_type': 'FUBONSYS.FCH_ROLE', 'desc': false};
        	$scope.requestComboBox(FCHvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FCH_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FCH_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FCH_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCHFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
        	if($scope.FCFlag=='Y'||$scope.FCHFlag=='Y'){
        		$scope.inputVO.empHistFlag = 'Y'
        	};
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.EMP_LIST = [];
		}
		$scope.inquireInit();
    	//選取月份下拉選單 --> 重新設定可視範圍
		$scope.dateChange = function() {
			$scope.inquireInit();
        	if(''==$scope.inputVO.sTime){
        		$scope.init();
        		return;
        	}
        	//設定回傳時間
        	$scope.inputVO.reportDate = $scope.inputVO.sTime;  
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);

        	$scope.sendRecv("PMS715", "getOpenFlag", "com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	$scope.openFlag = totas[0].body.openFlagList[0].OPEN_FLAG;
					}
			);
        }
    	
        $scope.isq = function(){
    		var NowDate = new Date();
    		var yr = NowDate.getFullYear();
    		var mm = NowDate.getMonth() + 1;
    		var strmm = '';
    		var xm = '';
    		$scope.mappingSet['timeE'] = [];
    		for (var i = 0; i < 12; i++) {
    			mm = mm - 1;
    			if (mm == 0) {
    				mm = 12;
    				yr = yr - 1;
    			}
    			if (mm < 10)
    				strmm = '0' + mm;
    			else
    				strmm = mm;
    			$scope.mappingSet['timeE'].push({
    				LABEL : yr + '/' + strmm,
    				DATA : yr + '' + strmm
    			});
    		} 
    	};    	
    	$scope.isq();
    	
		/**
		 * 查詢
		 */
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS715", "inquireRole", "com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.inquireRoleList.length == 0) {
					    		$scope.showMsg('該月份暫無考核指標');
	                			return;
	                		} 
							$scope.inquireRoleList = tota[0].body.inquireRoleList;
							for(var i = 0; i < $scope.inquireRoleList.length; i++){								
								$scope.subProjectSeqId[i] = $scope.inquireRoleList[i].SUB_PROJECT_SEQ_ID;
								var a = i*4;
								var b = i*4 + 1;
								var c = i*4 + 2;
								var d = i*4 + 3;
								$scope.showList[a] = [i,'實際數'];
								$scope.showList[b] = [i,'目標數'];
								$scope.showList[c] = [i,'達成率'];
								$scope.showList[d] = [i,'得分'];
							}
							//2017/06/07  分頁的條件
							$scope.inputVO.currentPageIndex=tota[0].body.currentPageIndex;
							$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId.join(",");
							$scope.sendRecv("PMS715", "inquire", "com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
									function(tota, isError) {
										if (!isError) {
											$scope.paramList = [];
											if(tota[0].body.resultList.length == 0) {
												$scope.paramList = [];	
												$scope.showMsg("ehl_01_common_009");
					                			return;
					                		}
											$scope.paramList = tota[0].body.resultList;
											$scope.csvList = tota[0].body.csvList;
											$scope.confirmFlag = true;
											for(var i = 0; i < $scope.paramList.length; i++){		
												if($scope.paramList[i].A_AO_CODE == undefined){
													$scope.confirmFlag = false;
												}
											}
											$scope.outputVO = tota[0].body;	
											angular.forEach($scope.paramList, function(row, index, objs) {
												//==銷量
												if($scope.inquireRoleList.length!=0  && $scope.inquireRoleList.length!=undefined){ 	//銷量不為0
													var valueAll = '';
													for(var x=0; x<$scope.inquireRoleList.length;x++){
														if(row['REAL_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID]==null){
															valueAll+= '0,';
														}else{
															valueAll+= row['REAL_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID] + ',';
														}
														if(row['TARGET_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID]==null){
															valueAll+= '0,';
														}else{
															valueAll+= row['TARGET_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID] + ',';
														}
														if(row['RATE_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID]==null){
															valueAll+= '0%,';
														}else{
															valueAll+= row['RATE_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID] + ',';
														}
														if(row['SCORE_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID]==null){
															valueAll+= '0,';
														}else{
															valueAll+= row['SCORE_'+$scope.inquireRoleList[x].SUB_PROJECT_SEQ_ID] + ',';
														}
													}
												}
												row.listValue = valueAll;
											})
										}else{
											$scope.showBtn = 'none';
										}	
							});
							//2017/06/07  註解影響
//							$scope.outputVO = tota[0].body;
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			});
			
		};
		
		/**「，」分割資料**/ 
		$scope.getListByComma = function(value){
			if (value != "" && value != undefined) {
				var values = value.substring(0,value.length-1);
				return values.split(',');
			}
		}
		
		/**
		 * 儲存
		 */
		$scope.saveChange1 = function() {			
			var openFlag = $("input[name='openFlag']:checked").val();
			$scope.inputVO.openFlag = openFlag;
			$scope.inputVO.execFlag = 1;
			if ($scope.inputVO.sTime == null || $scope.inputVO.sTime =="") {
				$scope.showMsg("請選擇:*月份！");
				return;
			}
			if (openFlag == undefined) {
				$scope.showMsg("開放查詢:必須按照");
				return;
			}
			$scope.sendRecv("PMS715", "saveChange", "com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
					function(tota, isError) {
					if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
						$scope.showMsg("ehl_01_common_010");
					}else{
						$scope.showErrorMsg(tota[0].body.errorMessage);
					}
			});
		}
		$scope.saveChange2 = function() {
			$scope.inputVO.execFlag = 2;
			if ($scope.inputVO.yearMonOp == null || $scope.inputVO.yearMonOp =="") {
				$scope.showMsg("請選擇:重新計算月份！");
				return;
			}
			$scope.sendRecv("PMS715", "saveChange", "com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
					function(tota, isError) {
					if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
						$scope.showMsg("ehl_01_common_010");
					}else{
						$scope.showErrorMsg(tota[0].body.errorMessage);
					}
			});
		}
		/**
		 * 匯出
		 */
    	$scope.exportRPT = function(){
			$scope.sendRecv("PMS715", "export",	"com.systex.jbranch.app.server.fps.pms715.PMS715InputVO", $scope.inputVO,
				    function(tota, isError) {
						if (!isError) {
							return;
						}
					});
			};
});
