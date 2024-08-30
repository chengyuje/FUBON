/**===============================================================================
								InitController
==================================================================================
 	    @Author: Angus, ArthurKO
   @description: Initialize Platform Control
    @LastUpdate: 
    2016/08/04 ArthurKO: Add Parameters.
    2016/07/26 ArthurKO: sorts codes.
    2016/05/30 ArthurKO: Add Watcher to dialog & server to send data.
================================================================================*/
'use strict';
eSoafApp.controller("InitController", ['$rootScope', '$scope', '$controller', 'projInfoService', 'socketService', 'ngDialog', '$location', '$interpolate', '$timeout', '$confirm', 'getParameter', 'sysInfoService', 
	function($rootScope, $scope, $controller, projInfoService, socketService, ngDialog, $location, $interpolate, $timeout, $confirm, getParameter, sysInfoService){
		$controller('webviewController', {$scope: $scope});
		
		/**============================================================================
		 *  	   @Type: 參數取得 & 設定
		 *  @Description: 平台參數的獲取與設定
		 *  			  platform parameters GET / SET
		 *=============================================================================*/	
    	/*-----------------------------------------------------------------------------
		 * @Description: 角色權限檢查
		 *               Role permission checks
		 *-----------------------------------------------------------------------------*/
		// switch role
		$scope.userList = projInfoService.getUserList();
		
		$scope.UserID = projInfoService.getUserID();
		$scope.ApplicationID = projInfoService.getApplicationID();
		$scope.BranchID = projInfoService.getBranchID();
		$scope.WsID = projInfoService.getWsID();
		$scope.RoleID = projInfoService.getRoleID();
//		$scope.PriID = projInfoService.getPriID();
//		console.log('$scope.UserID='+$scope.UserID);
		if(($scope.UserID && $scope.ApplicationID && $scope.BranchID && $scope.WsID && $scope.RoleID) === false){
			//send alert with unload event.
			$scope.$emit("e-unload-alert", 'Login');
			return;
		}
		
		/*-----------------------------------------------------------------------------
		 * @Description: 彈跳視窗計數器
		 *               Dialog Counter
		 *  @Parameters: [visit] hidden dialog, [total] all dialog
		 *-----------------------------------------------------------------------------*/
		$scope.$counter = $rootScope.$counter;
		
		/*-----------------------------------------------------------------------------
		 * @Description: 一般系統參數設定
		 *               General system parameters
		 *-----------------------------------------------------------------------------*/
		//Dialog
		projInfoService.setDialogCounts([{"LABEL":"SINGLE","DATA":2},{"LABEL":"TOTAL","DATA":10}]);
		//Navigator
		projInfoService.setNavigator(navigator.userAgent.match("Safari") ? true : false);
		
		/*-----------------------------------------------------------------------------
		 * @Description: 系統-單頁顯示筆數參數
		 *               Single page shows the number of parameters
		 *-----------------------------------------------------------------------------*/
		getParameter.XML('SYS.MAX_DISPLAY_ROWS', function(totas) {
			if (len(totas)) {
				projInfoService.setSinglePageRows(totas.data[0]);
			} else {
				projInfoService.setSinglePageRows([{"DATA":"DEFAULT","LABEL":"50"}]);
			}
		});
		
		/*-----------------------------------------------------------------------------
		 * @Description: 浮水印
		 *               water mark
		 *-----------------------------------------------------------------------------*/
		$scope.WATERMARK = [];
		angular.forEach(projInfoService.getWATERMARK(), function(row, index, objs){
			$scope.WATERMARK.push({LABEL: $scope.$eval($interpolate(row.LABEL))});
		});
//		console.log('WATERMARK='+JSON.stringify($scope.WATERMARK));
		
		/*-----------------------------------------------------------------------------
		 * @Description: 行事曆提醒機制相關參數
		 *               Calendar reminder system parameters set
		 *-----------------------------------------------------------------------------*/
		$rootScope.$calendar = {"flag":[],"list":[],"time":15,"stop":false};
		
		/*-----------------------------------------------------------------------------
		 * @Description: 上傳檔案相關參數
		 *               upload parameters
		 *  @Parameters: 檔案大小限制(單位:MB)(FILE_SIZE.LIMIT)
		 *-----------------------------------------------------------------------------*/
		projInfoService.setFileParameter([{"DATA":"DEFAULT","LABEL":"10"}]);
		
		/*-----------------------------------------------------------------------------
		 * @Description: 伺服器相關參數
		 *               server respons parameters
		 *  @Parameters: 伺服器回應時間限制(單位:mins)(TIMEOUT.LIMIT)
		 *-----------------------------------------------------------------------------*/
		getParameter.XML('TIMEOUT.LIMIT', function(totas) {
			if (len(totas)) {
				projInfoService.setServerParameter(totas.data[0]);
			} else {
				projInfoService.setServerParameter([{"DATA":"DEFAULT","LABEL":"2"}]);
			}	
		});
		
		/*-----------------------------------------------------------------------------
		 * @Description: 跑馬燈資料更新間距參數
		 *               Marquee info update parameters
		 *  @Parameters: 更新間距時間(單位:分鐘)(COMMON.MARQUEE)
		 *-----------------------------------------------------------------------------*/
		projInfoService.setMarqueeParameter([{"DATA":"PERIOD","LABEL":"15"}]);
		
		/**============================================================================
		 * 		   @Type: 監視器
		 *  @Description: 平台公用監視系統
		 *  			  platform watcher
		 *=============================================================================*/
		/*-----------------------------------------------------------------------------
		 * @Description: 平台捲軸監視器
		 *               scroll listener
		 *  @Parameters: total / visit
		 *-----------------------------------------------------------------------------*/
		$rootScope.$watchCollection('$counter.total', function(newCol, oldCol, scope) {
    		var total = $rootScope.$counter.total, counts = $rootScope.$counter.total.length;
    		if(counts == 0){
    			$("html,body").css("overflow","auto");
    		}else{
    			var chk = 0;
        		for(var i=0; i<counts; i+=1) {
        			if($(total[i].element).is(":hidden")) { 
        				chk+=1;
        			}
        		}
        		if(chk == counts){
        			$("html,body").css("overflow","auto");
        		}else{
        			$("html,body").css("overflow","hidden");
        		}
    		}
		});
    	$rootScope.$watchCollection('$counter.visit', function(newCol, oldCol, scope) {
    		$timeout(function() {
    			var total = $rootScope.$counter.total, counts = $rootScope.$counter.total.length;
        		if(counts == 0){
        			$("html,body").css("overflow","auto");
        		}else{
        			var chk = 0;
        			for(var i=0; i<counts; i+=1) {
            			if($(total[i].element).is(":hidden")) {
            				chk+=1;
            			}
            		}
            		if(chk == counts){
            			$("html,body").css("overflow","auto");
            		}else{
            			$("html,body").css("overflow","hidden");
            		}
        		}
    		},500);
		});
    	
    	
    	$scope.inputVO.AOlist = sysInfoService.getAoCode();
    	$scope.inputVO.priID = String(sysInfoService.getPriID());
    	if ($scope.inputVO.priID == "014" || $scope.inputVO.priID == "015" || 
    		$scope.inputVO.priID == "023" || $scope.inputVO.priID == "024" ||
    		$scope.inputVO.AOlist.length > 0){
    		//陪訪遭退回(輔銷人員、輔銷科長、理專)
    		$scope.inputVO.LoginID = sysInfoService.getUserID();
        	$scope.sendRecv("CRM1242", "cancel", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", $scope.inputVO,
        		function(tota, isError) {
        			if (!isError) {
        				$scope.resultList = tota[0].body.resultList;
        				if($scope.resultList != null && $scope.resultList.length > 0){
        					alert("您有"+$scope.resultList.length+"筆陪訪需求被退回")
        				}
        				return;
        			};
        	});
    	}
    	//
    	//輔銷駐點異動(輔銷人員)
    	if($scope.inputVO.priID == "014" || $scope.inputVO.priID == "023"){
    		$scope.inputVO.LoginID = sysInfoService.getUserID();
        	$scope.sendRecv("CRM1243", "change", "com.systex.jbranch.app.server.fps.crm1243.CRM1243InputVO", $scope.inputVO,
        		function(tota, isError) {
        			if (!isError) {
        				$scope.ListY = tota[0].body.resultList;
        				$scope.ListN = tota[0].body.resultList2;
        				if($scope.ListY != null && $scope.ListY.length > 0 && $scope.ListN != null && $scope.ListN.length > 0){
        					alert("您有"+$scope.ListY.length+"筆駐點變更已覆核\r\n" +
        						  "您有"+$scope.ListN.length+"筆駐點變更被退回");
        				}else if($scope.ListY != null && $scope.ListY.length > 0){
        					alert("您有"+$scope.ListY.length+"筆駐點變更已覆核");
        				}else if($scope.ListN != null && $scope.ListN.length > 0){
        					alert("您有"+$scope.ListN.length+"筆駐點變更被退回")
        				}
        				return;
        			};
        	});
    	}
    	
		/** alert when window onunload. */
		//inital
		window.onbeforeunload = function (e) {
			return "";      
		};
		//oncall
		$rootScope.$on("e-unload-alert",function(event, path) {
			// 2017/7/5
			if(path == 'MobilePlatform') {
				$rootScope.GeneratePage({'txnName':'HOME','txnId':'HOME','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'}]});
			}
			else {
				window.onbeforeunload = null;
				window.location = path + '.html';
			}
		});
		
		// override ngDialog closeThisDialog
		$scope.closeThisDialog = function(val) {
			if (val === 'cancel') {
				webViewParamObj.cancel();
				window.webkit.messageHandlers.resultCompleted.postMessage('cancel');
			}
		}
}]);