/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT960Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT960Controller";
		
		$scope.init = function() {
			debugger
//			$scope.inputVO = $scope.inputVO;	
			
			$scope.inputVO.totalInvAmtP1 = 0;
			$scope.inputVO.totalInvAmtP2 = 0;
			$scope.inputVO.totalInvAmtP3 = 0;
			$scope.inputVO.totalInvAmtP4 = 0;
			
			$scope.inputVO.custID = $scope.inputVO.CUST_ID;
			
			//測試資料
//			$scope.inputVO.custID = "A100401786";//$scope.inputVO.CUST_ID;
//			$scope.inputVO.custID = "A100401786";
//			$scope.inputVO.CUST_ID = "A100401786";
//			$scope.inputVO.INSPRD_ID = 'VAIF';
//			$scope.inputVO.INSPRD_NAME =  '外幣計價富貴醫生變額年金保險';
//			$scope.inputVO.CURR_CD = 'USD';
//			$scope.inputVO.REAL_PREMIUM = 100000;
//			$scope.inputVO.CUST_RISK = "C3";
			
			//取得客戶資料
			$scope.getCustData();
		}
        
		$scope.inquireInit = function() {
			$scope.outputVO = {};
			$scope.inputVO.fp032675Data = null;
			$scope.inputVO.custRiskChkVal = null;
			$scope.inputVO.wmshaiaData = null;
			$scope.inputVO.dataDate = null;
			$scope.inputVO.trialData = null;
			$scope.inputVO.PROD_CURR_RATE = 1;
			
			$scope.inputVO.currentRiskVal = 1; //客戶現在風險屬性值
        	$scope.inputVO.allowRiskVal = 4; //客戶可越級風險屬性值
		};
		
		//檢查是否配置比例相加為100
		$scope.checkPercentage = function() {
			if($scope.inputVO.INVESTList.length > 0) {
				var sum = 0;
				for(var a = 0; a<$scope.inputVO.INVESTList.length; a++){
					if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != undefined 
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != null
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
						if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
							if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO < 5) {
								return false
								break;
							} else {
								sum += Number($scope.inputVO.INVESTList[a].ALLOCATION_RATIO);
							}
						}
					}
				}
				if(sum != 100) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
		
		//依標的清單中配置比例計算申購金額及折台金額
		$scope.calInvListAmt = function() {
			angular.forEach($scope.inputVO.INVESTList, function(row) {
				if(row.ALLOCATION_RATIO != undefined && row.ALLOCATION_RATIO != null && row.ALLOCATION_RATIO != '') {
					row.ORI_AMT = ($scope.inputVO.REAL_PREMIUM * parseInt(row.ALLOCATION_RATIO)) / 100; //原幣金額
					row.TWD_AMT = row.ORI_AMT * $scope.inputVO.PROD_CURR_RATE; //折台金額
				}
			});
		}
		
		//取得標的清單
		$scope.getInvestList = function() {
			$scope.inputVO.INVESTList = [];
			debugger
			$scope.sendRecv("IOT920","Get_List","com.systex.jbranch.app.server.fps.iot920.InsFundListInputVO", $scope.inputVO, 
				function(tota,isError){
					if(!isError) {
						$scope.inputVO.INVESTList = tota[0].body.INVESTList;
						//依標的清單中配置比例計算申購金額及折台金額
						$scope.calInvListAmt();
						//已有標的清單，計算此次投入金額折台
						$scope.calTotalInvAmt();
					}
				});
		}
		
		//取得客戶註記資料
		//取得目前風險檢核值
		$scope.getCustData = function() {
			$scope.inquireInit();
			
			if ($scope.inputVO.custID != undefined && $scope.inputVO.custID != null && $scope.inputVO.custID != '') {
				$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
				$scope.sendRecv("IOT960", "getCustData", "com.systex.jbranch.app.server.fps.iot960.IOT960InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.inputVO.fp032675Data = tota[0].body.fp032675Data; //電文客戶註記資料
							$scope.inputVO.custRiskChkVal = tota[0].body.custRiskChkVal; //客戶風險檢核值
							$scope.inputVO.wmshaiaData = tota[0].body.wmshaiaData; //投組風險檢核值資料
							$scope.inputVO.dataDate = $filter('date')(new Date(),'yyyy-MM-dd HH:mm:ss');//取當日日期
							$scope.inputVO.currentRiskVal = parseInt($scope.inputVO.CUST_RISK.substring(1)); //客戶現在風險屬性值
							$scope.inputVO.PROD_CURR_RATE = tota[0].body.prodCurrRate; //險種幣別折台匯率
							
//							if($scope.inputVO.fp032675Data.custRemarks == "Y") { //特定客戶只可越一級
								$scope.inputVO.allowRiskVal = $scope.inputVO.currentRiskVal < 4 ? $scope.inputVO.currentRiskVal + 1 : 4; //客戶可越級風險屬性值
//							}
							
							//可選擇的越級適配P值
							$scope.mappingSet['SENIOR_OVER_PVAL'] = [];
							for(var i=1; i<=4; i++) {
								if($scope.inputVO.currentRiskVal < i && $scope.inputVO.allowRiskVal >= i) {
									$scope.mappingSet['SENIOR_OVER_PVAL'].push({LABEL: "P"+i, DATA: "P"+i});
								}
							}
							$scope.inputVO.SENIOR_OVER_PVAL = "P" + $scope.inputVO.allowRiskVal;
							
							//取得標的清單
							if(!$scope.inputVO.INVESTList || $scope.inputVO.INVESTList.length <= 0) {
								$scope.getInvestList();
							} else {
								//依標的清單中配置比例計算申購金額及折台金額
								$scope.calInvListAmt();
								//已有標的清單，計算此次投入金額折台
								$scope.calTotalInvAmt();
							}
								
							return;
						}
				});
			}
		}
		
		//輸入比例後，計算申購金額及折台金額
		$scope.calAmt = function(row) {
			debugger
			if(row.ALLOCATION_RATIO == null || row.ALLOCATION_RATIO == undefined || row.ALLOCATION_RATIO == "") {
				row.ORI_AMT = undefined;
				row.TWD_AMT = undefined;
			} else {
				row.ORI_AMT = ($scope.inputVO.REAL_PREMIUM * parseInt(row.ALLOCATION_RATIO)) / 100; //原幣金額
				row.TWD_AMT = row.ORI_AMT * $scope.inputVO.PROD_CURR_RATE; //折台金額
			}
			//計算此次投入金額折台
			$scope.calTotalInvAmt();
		}
		
		//計算各P值此次投入金額折台總計
		$scope.calTotalInvAmt = function() {
			$scope.inputVO.totalInvAmtP1 = 0;
			$scope.inputVO.totalInvAmtP2 = 0;
			$scope.inputVO.totalInvAmtP3 = 0;
			$scope.inputVO.totalInvAmtP4 = 0;
			
			angular.forEach($scope.inputVO.INVESTList, function(row) {
				debugger
				if(row.ALLOCATION_RATIO != undefined && row.ALLOCATION_RATIO != null && row.ALLOCATION_RATIO != '') {
					debugger
					if(row.PRD_RISK == "P1") $scope.inputVO.totalInvAmtP1 += row.TWD_AMT;
					if(row.PRD_RISK == "P2") $scope.inputVO.totalInvAmtP2 += row.TWD_AMT;
					if(row.PRD_RISK == "P3") $scope.inputVO.totalInvAmtP3 += row.TWD_AMT;
					if(row.PRD_RISK == "P4") $scope.inputVO.totalInvAmtP4 += row.TWD_AMT;
				}
			});
		}
		
		//確定，回傳配置比例資料
		$scope.returnInvListData = function() {
			debugger
			//檢查是否配置比例相加為100
			if(!$scope.checkPercentage()) {
				$scope.showErrorMsg("ehl_01_iot120_008");
				return;
			} else if($scope.inputVO.SENIOR_OVER_PVAL != undefined && $scope.inputVO.SENIOR_OVER_PVAL != null && $scope.inputVO.SENIOR_OVER_PVAL != "") {
				//有越級適配
				//越級投組適配金額檢核
				var overPvalAmt = eval("$scope.inputVO.totalInvAmt" + $scope.inputVO.SENIOR_OVER_PVAL); //越級P值投入金額
				var overPvalMaxAmt = eval("$scope.inputVO.wmshaiaData.AMT_LEFT_" + $scope.inputVO.SENIOR_OVER_PVAL.substring(1)); //越級P值最大可申購金額
				$scope.inputVO.OVER_PVAL_AMT = overPvalAmt;
				$scope.inputVO.OVER_PVAL_MAX_AMT = overPvalMaxAmt;
				if(overPvalAmt > 0) {
					//有越級適配投入金額
					if(overPvalAmt > overPvalMaxAmt) {
						$scope.showErrorMsgInDialog("投組適配金額 > 最大可申購金額，請調整配置比例");
						return;
					}
					//進行越級投組適配計算
					$scope.inputVO.custRemarks = $scope.inputVO.fp032675Data.custRemarks;
					$scope.inputVO.overPvalAmt = overPvalAmt;
					$scope.sendRecv("IOT960", "trialCalculate", "com.systex.jbranch.app.server.fps.iot960.IOT960InputVO", $scope.inputVO,
			        		function(tota, isError) {
								if (!isError) {
									//取得越級投組適配試算計算
									if(tota[0].body.wmshaiaData != null ) {
										if(tota[0].body.wmshaiaData.VALIDATE_YN == "Y") { //可否交易
											//紀錄最後一次發查高資產電文的序號
											$scope.inputVO.WMSHAIA_SEQ = tota[0].body.wmshaiaData.SEQ; //高資產投組適配電文序號
											//越級適配通過，回傳資料
											$scope.closeThisDialog($scope.inputVO);
											return;
										} else {
											//越級適配不通過
											$scope.showErrorMsgInDialog("客戶投資組合風險權值已超標，請調整配置比例，或至「投組適配試算功能」重新試算");
											return;
										}
									}
									$scope.showErrorMsgInDialog("越級投組適配計算有誤，請重新調整");
									return;
								}
			        });
				} else {
					//沒有越級適配，回傳資料
					$scope.closeThisDialog($scope.inputVO);
					return;
				}
			} else {
				//沒有越級適配，回傳資料
				$scope.closeThisDialog($scope.inputVO);
				return;
			}
		}
		
		//初始化
		$scope.init();
		
});
