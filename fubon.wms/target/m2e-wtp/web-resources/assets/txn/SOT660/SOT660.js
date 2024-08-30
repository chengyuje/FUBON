/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT660Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT660Controller";
		
		getParameter.XML(["SOT.SOT660_CUR_TYPE"], function(totas) {
			if (totas) {
				//幣別
			    $scope.mappingSet['SOT.SOT660_CUR_TYPE'] = totas.data[totas.key.indexOf('SOT.SOT660_CUR_TYPE')];
			}
		});
      	
        $scope.init = function(){
        	$scope.inputVO = {};
        	$scope.inputVO.custID = '';
		};
		$scope.init();
        
		$scope.inquireInit = function() {
			$scope.outputVO = {};
			$scope.inputVO.fp032675Data = null;
			$scope.inputVO.custKYCData = null;
			$scope.inputVO.custRiskChkVal = null;
			$scope.inputVO.hnwcData = null;
			$scope.inputVO.wmshaiaData = null;
			$scope.inputVO.dataDate = null;
			$scope.inputVO.trialData = null;
			
			$scope.inputVO.currentRiskVal = 1; //客戶現在風險屬性值
        	$scope.inputVO.allowRiskVal = 4; //客戶可越級風險屬性值
        	
			$scope.inputVO.CUR11 = "TWD";
        	$scope.inputVO.CUR12 = "TWD";
        	$scope.inputVO.CUR13 = "TWD";
        	$scope.inputVO.CUR14 = "TWD";
        	$scope.inputVO.CUR15 = "TWD";
        	$scope.inputVO.CUR21 = "TWD";
        	$scope.inputVO.CUR22 = "TWD";
        	$scope.inputVO.CUR23 = "TWD";
        	$scope.inputVO.CUR24 = "TWD";
        	$scope.inputVO.CUR25 = "TWD";
        	$scope.inputVO.CUR31 = "TWD";
        	$scope.inputVO.CUR32 = "TWD";
        	$scope.inputVO.CUR33 = "TWD";
        	$scope.inputVO.CUR34 = "TWD";
        	$scope.inputVO.CUR35 = "TWD";
        	$scope.inputVO.CUR41 = "TWD";
        	$scope.inputVO.CUR42 = "TWD";
        	$scope.inputVO.CUR43 = "TWD";
        	$scope.inputVO.CUR44 = "TWD";
        	$scope.inputVO.CUR45 = "TWD";
        	
        	for(var i = 1; i <= 4; i++) {
        		for(var j = 1; j <= 5; j++) {
        			var obj = eval("$scope.inputVO.AMT" + i + j);
        			obj = undefined;
        		}
        	}
//        	$scope.inputVO.AMT11 = undefined;
//        	$scope.inputVO.AMT21 = undefined;
//        	$scope.inputVO.AMT31 = undefined;
//        	$scope.inputVO.AMT41 = undefined;
		};
		$scope.inquireInit();	
		
		//取得客戶註記資料
		//取得客戶KYC資料
		//高資產客戶資料
		//取得目前風險檢核值
		$scope.getCustData = function() {
			var custId = $scope.inputVO.custID;
			$scope.init();
			$scope.inquireInit();
			$scope.inputVO.custID = custId;
			
			if ($scope.inputVO.custID != undefined && $scope.inputVO.custID != null && $scope.inputVO.custID != '') {
				$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
				$scope.sendRecv("SOT660", "getCustData", "com.systex.jbranch.app.server.fps.sot660.SOT660InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.inputVO.fp032675Data = tota[0].body.fp032675Data; //電文客戶註記資料
							$scope.inputVO.custKYCData = tota[0].body.custKYCData; //客戶資料(KYC)
							$scope.inputVO.custRiskChkVal = tota[0].body.custRiskChkVal; //客戶風險檢核值
							$scope.inputVO.hnwcData = tota[0].body.hnwcData; //客戶高資產註記資料
							$scope.inputVO.wmshaiaData = tota[0].body.wmshaiaData; //投組風險檢核值資料
							$scope.inputVO.dataDate = $filter('date')(new Date(),'yyyy-MM-dd HH:mm:ss');//取當日日期
							$scope.inputVO.currentRiskVal = parseInt($scope.inputVO.custKYCData.kycLevel.substring(1)); //客戶現在風險屬性值
							
							$scope.inputVO.AMT11 = $scope.inputVO.wmshaiaData.AMT_LEFT_1; //P1台幣最大可申購金額
							$scope.inputVO.AMT21 = $scope.inputVO.wmshaiaData.AMT_LEFT_2; //P2台幣最大可申購金額
							$scope.inputVO.AMT31 = $scope.inputVO.wmshaiaData.AMT_LEFT_3; //P3台幣最大可申購金額
							$scope.inputVO.AMT41 = $scope.inputVO.wmshaiaData.AMT_LEFT_4; //P4台幣最大可申購金額
							//客戶可越一級風險屬性值
							$scope.inputVO.allowRiskVal = $scope.inputVO.currentRiskVal < 4 ? $scope.inputVO.currentRiskVal + 1 : 4; 
														
							var today = new Date();
							var nextmonth = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate());
							$scope.inputVO.kycDateColor = ($scope.toJsDate($scope.inputVO.custKYCData.kycDueDate) <= nextmonth) ? "color:red" : "color:black";
							$scope.inputVO.proDateColor = ($scope.toJsDate($scope.inputVO.fp032675Data.custProDate) <= nextmonth) ? "color:red" : "color:black";
							$scope.inputVO.hnwcDateColor = ($scope.toJsDate($scope.inputVO.hnwcData.dueDate) <= nextmonth) ? "color:red" : "color:black";
							
							return;
						} else {
							$scope.init();
							$scope.inquireInit();
						}
				});
			} else {
//				$scope.showErrorMsg("請輸入客戶ID");
			}
		}
		
		//進行投組適配試算
		$scope.trialCalculate = function() {
			$scope.inputVO.trialData = null;
			debugger
			$scope.inputVO.custRemarks = $scope.inputVO.hnwcData.spFlag;
			$scope.inputVO.kycLevel = $scope.inputVO.custKYCData.kycLevel;
			$scope.inputVO.prodRisk = "P" + $scope.inputVO.allowRiskVal.toString();
			
			//金額檢核
			$scope.chkAmt();
			
			$scope.sendRecv("SOT660", "trialCalculate", "com.systex.jbranch.app.server.fps.sot660.SOT660InputVO", $scope.inputVO,
        		function(tota, isError) {
					if (!isError) {
						if(tota[0].body.wmshaiaData == null) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
	                	}
						debugger
						//取得投組適配試算結果
						if(tota[0].body.wmshaiaData != null ) {
							$scope.inputVO.trialData = tota[0].body.wmshaiaData;							
							$scope.inputVO.trialData.MESSAGE1 = "越級投資比例";
							if($scope.inputVO.trialData.VALIDATE_YN == "Y") { //可否交易
								$scope.inputVO.trialData.MESSAGE = "<= 30%：投組適配，可承作該投資試算交易內容。";
							} else {
								$scope.inputVO.trialData.MESSAGE = "> 30%：投組不適配，無法承作該投資試算交易內容。";
							}
						}
						
						return;
					}
        	});
		}
		
		//取得最大可申購金額幣別相對應金額
		$scope.getCurAmt = function(idx) {
			var objCur = "TWD";
			var objAmt = 0;
			
			debugger
			switch(idx) {
			case '1':
				objCur = $scope.inputVO.CUR11;
				objAmt = $scope.inputVO.wmshaiaData.AMT_LEFT_1; //送P1原台幣最大可申購金額
				break;
			case '2':
				objCur = $scope.inputVO.CUR21;
				objAmt = $scope.inputVO.wmshaiaData.AMT_LEFT_2; //送P2原台幣最大可申購金額
				break;
			case '3':
				objCur = $scope.inputVO.CUR31;
				objAmt = $scope.inputVO.wmshaiaData.AMT_LEFT_3; //送P3原台幣最大可申購金額
				break;
			case '4':
				objCur = $scope.inputVO.CUR41;
				objAmt = $scope.inputVO.wmshaiaData.AMT_LEFT_4; //送P4原台幣最大可申購金額
				break;
			}
			
			$scope.sendRecv("SOT660", "getCurAmt", "com.systex.jbranch.app.server.fps.sot660.SOT660InputVO", {"CUR12": objCur, "AMT12": objAmt},
	        		function(tota, isError) {
						if (!isError) {
							debugger
							if(idx == '1') $scope.inputVO.AMT11 = tota[0].body.curAmt;
							if(idx == '2') $scope.inputVO.AMT21 = tota[0].body.curAmt;
							if(idx == '3') $scope.inputVO.AMT31 = tota[0].body.curAmt;
							if(idx == '4') $scope.inputVO.AMT41 = tota[0].body.curAmt;
						}
		    });
		}
		
		//金額欄位若清空，則為undefined
		//否則到後端會JSON格式錯誤
		$scope.chkAmt = function() {
			if($scope.inputVO.AMT12 == "") $scope.inputVO.AMT12 = undefined;
			if($scope.inputVO.AMT13 == "") $scope.inputVO.AMT13 = undefined;
			if($scope.inputVO.AMT14 == "") $scope.inputVO.AMT14 = undefined;
			if($scope.inputVO.AMT15 == "") $scope.inputVO.AMT15 = undefined;
			
			if($scope.inputVO.AMT22 == "") $scope.inputVO.AMT22 = undefined;
			if($scope.inputVO.AMT23 == "") $scope.inputVO.AMT23 = undefined;
			if($scope.inputVO.AMT24 == "") $scope.inputVO.AMT24 = undefined;
			if($scope.inputVO.AMT25 == "") $scope.inputVO.AMT25 = undefined;
			
			if($scope.inputVO.AMT32 == "") $scope.inputVO.AMT32 = undefined;
			if($scope.inputVO.AMT33 == "") $scope.inputVO.AMT33 = undefined;
			if($scope.inputVO.AMT34 == "") $scope.inputVO.AMT34 = undefined;
			if($scope.inputVO.AMT35 == "") $scope.inputVO.AMT35 = undefined;
			
			if($scope.inputVO.AMT42 == "") $scope.inputVO.AMT42 = undefined;
			if($scope.inputVO.AMT43 == "") $scope.inputVO.AMT43 = undefined;
			if($scope.inputVO.AMT44 == "") $scope.inputVO.AMT44 = undefined;
			if($scope.inputVO.AMT45 == "") $scope.inputVO.AMT45 = undefined;
		}
});
