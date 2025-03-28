/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD151Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, $q, validateService, getParameter, sysInfoService, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD151Controller";
		
		//參數
		$scope.getParameters = function() {
			getParameter.XML(["PRD.FCI_CURRENCY", "SOT.FCI_PM_PRIVILEGE_ID"], function(totas) {
				if (totas) {
					$scope.FCI_CURRENCY = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
					$scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'] = totas.data[totas.key.indexOf('SOT.FCI_PM_PRIVILEGE_ID')];
					debugger
					//權限ID//是否為FCI PM角色
					$scope.privilegeId = sysInfoService.getPriID();
					var findFCIPMRole = $filter('filter')($scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'], {DATA: $scope.privilegeId[0]});
					$scope.inputVO.isFCIPMRole = (findFCIPMRole != null && findFCIPMRole != undefined && findFCIPMRole.length > 0) ? true : false;
				}
			});
		}
        
		//取得報價起訖時間
		$scope.getTimeParams = function() {
			$scope.sendRecv("PRD281", "inquireParam", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {},
				function(tota, isError) {
					if (!isError) {
						debugger
						$scope.inputVO.PRICE_START_TIME = tota[0].body.P_START_HOUR + ":" + tota[0].body.P_START_MIN;
						$scope.inputVO.PRICE_END_TIME = tota[0].body.P_END_HOUR + ":" + tota[0].body.P_END_MIN;
						
						var today = new Date();
						var today_year = today.getFullYear();  //西元年份 
						var today_month = today.getMonth()+1;  //一年中的第幾月 
						var today_date = today.getDate();      //一月份中的第幾天
						var startTime = new Date(today_year, today_month-1, today_date, tota[0].body.P_START_HOUR, tota[0].body.P_START_MIN, "00");
						var endTime = new Date(today_year, today_month-1, today_date, tota[0].body.P_END_HOUR, tota[0].body.P_END_MIN, "00");
						var now  = new Date();
						
						if ( now.valueOf() > endTime.valueOf() || now.valueOf() < startTime.valueOf()) {
							$scope.inputVO.PriceExpired = true;
						} else {
							$scope.inputVO.PriceExpired = false;
						}
					}
			});
		}
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.LASTUPDATE = undefined;
			$scope.inputVO.PRICE_REMARK = "";
			$scope.getParameters();
			$scope.getTimeParams();
			
			$scope.inputVO.allowPurchaseYN = "Y";
			$scope.inputVO.kycLV = $scope.kycLV;
			$scope.inputVO.fromSOT = false;
			if($scope.fromSOT && $scope.fromSOT == "Y") { //下單頁面過來的SOT413
				$scope.inputVO.fromSOT = true;
			}
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();		
				
		// inquire
		$scope.inquire = function() {
			$scope.sendRecv("PRD151", "inquire", "com.systex.jbranch.app.server.fps.prd151.PRD151InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.inputVO.FCIPricingTodayYN = "N"; //無法提供報價
	                			return;
	                		} else {
	                			debugger
	                			$scope.paramList = tota[0].body.resultList;
	                			$scope.outputVO = tota[0].body;
	                			$scope.inputVO.DR_LASTUPDATE = tota[0].body.resultList[0].DR_LASTUPDATE;
	                			$scope.inputVO.PRICE_REMARK = tota[0].body.resultList[0].PRICE_REMARK;
	                			$scope.inputVO.FCIPricingTodayYN = "Y"; //今日有提供報價
	                		}
						}
			});
		};
		$scope.inquire();
		
		$scope.openDepRate = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD151/PRD151_DEPRATE.html',
				className: 'PRD151_DEPRATE',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};
		
		$scope.clearRateInfo = function() {
			$scope.inputVO.C_CURR_ID = "";
			$scope.inputVO.C_MON = "";
			$scope.inputVO.monType = undefined;
			$scope.inputVO.C_PRD_PROFEE = undefined;
			$scope.inputVO.C_MIN_PRD_PROFEE = undefined;
			$scope.inputVO.C_RISKCATE_ID = "";
			$scope.inputVO.C_TARGET_CURR_ID = "";
			$scope.inputVO.C_BASE_AMT = undefined;
			$scope.inputVO.C_UNIT_AMT = undefined;
			$scope.inputVO.C_STRIKE_PRICE = undefined;
			$scope.inputVO.C_TARGET_CNAME = "";
			$scope.inputVO.RM_PROFEE = undefined;
		}
		
		$scope.getRateInfo = function(row, idx) {
			debugger
			$scope.inputVO.C_CURR_ID = row.CURR_ID;
			$scope.inputVO.C_MON = idx + "個月";
			$scope.inputVO.monType = idx;
			$scope.inputVO.C_PRD_PROFEE = eval("row.PRD_PROFEE_RATE_" + idx);
			$scope.inputVO.C_MIN_PRD_PROFEE = eval("row.MIN_PROFEE_RATE_" + idx);
			$scope.inputVO.C_RISKCATE_ID = row.RISKCATE_ID;
			$scope.inputVO.C_TARGET_CURR_ID = row.TARGET_CURR_ID;
			$scope.inputVO.C_BASE_AMT = row.BASE_AMT;
			$scope.inputVO.C_UNIT_AMT = row.UNIT_AMT;
			$scope.inputVO.C_STRIKE_PRICE = row.STRIKE_PRICE;
			$scope.inputVO.C_TARGET_CNAME = row.TARGET_CNAME;
			$scope.prdProfeeChange();
		}
		
		$scope.prdProfeeChange = function() {
			if($scope.inputVO.C_CURR_ID == undefined || $scope.inputVO.C_CURR_ID == null || $scope.inputVO.C_CURR_ID == "") {
				$scope.showErrorMsg("請先選擇幣別天期");
				return;
			}
			
			if($scope.inputVO.C_PRD_PROFEE == undefined || $scope.inputVO.C_PRD_PROFEE == null || $scope.inputVO.C_PRD_PROFEE == "") {
				return;
			}
			debugger
			$scope.inputVO.allowPurchaseYN = "Y";
			if(!$scope.inputVO.isFCIPMRole && $scope.inputVO.C_PRD_PROFEE < $scope.inputVO.C_MIN_PRD_PROFEE) {
				//下單若非PM角色需檢核，若自行輸入的產品收益率(年化)<最低收益率，則跳出錯誤視窗” 產品條件超出權限，請洽PM”
				$scope.showErrorMsg("產品條件超出權限，請洽PM");
				$scope.inputVO.allowPurchaseYN = "N";
//				$scope.clearRateInfo();
				return;
			}
			
			$scope.sendRecv("PRD151", "getRMProfee", "com.systex.jbranch.app.server.fps.prd151.PRD151InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
	                		debugger
	                		if(tota[0].body.C_RM_PROFEE != null && tota[0].body.C_RM_PROFEE != undefined && tota[0].body.MINUF_MON != null && tota[0].body.MINUF_MON != undefined) {
	                			$scope.inputVO.RM_PROFEE = tota[0].body.C_RM_PROFEE;
	                			$scope.inputVO.MINUF_MON = tota[0].body.MINUF_MON;
	                			if(!$scope.inputVO.isFCIPMRole && $scope.inputVO.RM_PROFEE < $scope.inputVO.MINUF_MON) {
	                				$scope.showErrorMsg("產品條件超出權限，請洽PM");
	                				$scope.inputVO.allowPurchaseYN = "N";
	                				return;
	                			}
	                		} else {
	                			$scope.showErrorMsg("產品條件超出權限，請洽PM");
                				$scope.inputVO.allowPurchaseYN = "N";
                				return;
	                		}
						}
			});
		}
		
		//回到下單頁面SOT413
		$scope.gobackSOT = function() {
			$timeout(function() { 
				$scope.closeThisDialog($scope.inputVO); 
			}, 1000);
			
		}
});