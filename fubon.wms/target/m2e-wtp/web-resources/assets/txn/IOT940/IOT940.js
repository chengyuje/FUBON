/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT940Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT940Controller";
		
		//取得檢核項目資料
		$scope.queryData = function() {
			$scope.sendRecv("IOT940","query","com.systex.jbranch.app.server.fps.iot940.IOT940InputVO", {'PREMATCH_SEQ': $scope.inputVO.PREMATCH_SEQ},
					function(tota,isError) {
						if(!isError) {
							if(tota[0].body.CHK_YN_LIST == null || tota[0].body.CHK_YN_LIST.length == 0) {
								if($scope.inputVO.CHKLIST_TYPE == "1") { 
									//若為理專檢核，且尚未填寫過資料，將人壽API傳入的不通過原因帶入
									$scope.inputVO.CHK_YN_LIST["197"].DATA = $scope.inputVO.AGENT_MEMO;
								}
								
								return;
							}
							
							debugger
							angular.forEach(tota[0].body.CHK_YN_LIST, function(row) {
								debugger
								if(row.CHK_CODE == "97") {
									$scope.inputVO.CHK_YN_LIST[row.CHK_STEP+row.CHK_CODE].DATA = row.NP_REASON;
								} else if(row.CHK_CODE == "98") {
									$scope.inputVO.CHK_YN_LIST[row.CHK_STEP+row.CHK_CODE].DATA = row.CHK_EMP;
								} else if(row.CHK_CODE == "99") {
									$scope.inputVO.CHK_YN_LIST[row.CHK_STEP+row.CHK_CODE].DATA = row.LASTUPDATE;
								} else {
									$scope.inputVO.CHK_YN_LIST[row.CHK_STEP+row.CHK_CODE].DATA = row.CHK_YN;
								}
							});
						}
				});
		}
		
		$scope.save = function() {
			debugger
			//檢查是否每一檢核項目都有勾選
			for (var j = 0; j < $scope.CHK_CODE_LIST.length; j++) {
				if($scope.CHK_CODE_LIST[j].DATA != "97" && $scope.CHK_CODE_LIST[j].DATA != "98" && $scope.CHK_CODE_LIST[j].DATA != "99") {
					var data = $scope.inputVO.CHK_YN_LIST[$scope.inputVO.CHKLIST_TYPE+$scope.CHK_CODE_LIST[j].DATA].DATA;
					if(data == undefined || data == null || data == "") {
						$scope.showErrorMsgInDialog("儲存時，每一檢核項目都需勾選");
						return;
					}
				}
			}
			
			//儲存
			$scope.sendRecv("IOT940","save","com.systex.jbranch.app.server.fps.iot940.IOT940InputVO", $scope.inputVO,
					function(tota,isError){
						if(!isError){
							$scope.showMsg("ehl_01_common_025");  
							$scope.queryData();
						}
				});
		}
       		
		$scope.init = function() {
			debugger
			$scope.MAPPEVideoList = null;
			
			$scope.inputVO.PREMATCH_SEQ = $scope.PREMATCH_SEQ; //購買檢核編號
			$scope.inputVO.CUST_ID = $scope.CUST_ID; //要保人ID
			$scope.inputVO.INS_ID = $scope.INS_ID; //保險文件編號
			$scope.inputVO.CHKLIST_TYPE = $scope.CHKLIST_TYPE; //檢核步驟
			$scope.inputVO.AGENT_MEMO = $scope.AGENT_MEMO; //(人壽API取得)理專補充說明：視訊投保不通過原因
					
			//初始化檢核項目資料
			$scope.inputVO.CHK_YN_LIST = {};
			for (var i = 0; i < $scope.CHK_STEP_LIST.length; i++) {
				for (var j = 0; j < $scope.CHK_CODE_LIST.length; j++) {
					$scope.inputVO.CHK_YN_LIST[$scope.CHK_STEP_LIST[i].DATA+$scope.CHK_CODE_LIST[j].DATA] = {};
					$scope.inputVO.CHK_YN_LIST[$scope.CHK_STEP_LIST[i].DATA+$scope.CHK_CODE_LIST[j].DATA].CHK_STEP = $scope.CHK_STEP_LIST[i].DATA;
					$scope.inputVO.CHK_YN_LIST[$scope.CHK_STEP_LIST[i].DATA+$scope.CHK_CODE_LIST[j].DATA].CHK_CODE = $scope.CHK_CODE_LIST[j].DATA;
					$scope.inputVO.CHK_YN_LIST[$scope.CHK_STEP_LIST[i].DATA+$scope.CHK_CODE_LIST[j].DATA].DATA = undefined;
				}
			}
			
			$scope.queryData();
		}
		$scope.init();
				
	}
);