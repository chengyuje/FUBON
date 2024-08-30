/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210_setShortTranController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210_setShortTranController";
		
		$scope.select = function() {
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.inputVO.userID = projInfoService.getUserID();
			$scope.sendRecv("PMS210", "queryShortTran",
				"com.systex.jbranch.app.server.fps.pms210.PMS210InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if(null==tota[0].body.shortTranList || tota[0].body.shortTranList.length<=0){
							//$scope.showMsg("無資料");
						}else{
							$scope.shortTranList = tota[0].body.shortTranList[0]; 
							//回顯查詢結果
							$scope.inputVO.TSDate = new Date($scope.shortTranList.TSDATE);
							$scope.inputVO.TEDate = new Date($scope.shortTranList.TEDATE);
							if(null!=$scope.shortTranList.NSDATE && ''!=$scope.shortTranList.NSDATE
									&& null!=$scope.shortTranList.NEDATE && ''!=$scope.shortTranList.NEDATE){
								$scope.inputVO.NSDate = new Date($scope.shortTranList.NSDATE);
								$scope.inputVO.NEDate = new Date($scope.shortTranList.NEDATE);
							}
							$scope.inputVO.tradeS = $scope.shortTranList.TRADES;
							$scope.inputVO.tradeE = $scope.shortTranList.TRADEE;
							return;
						}
					}
				});
		}
		$scope.select();
		/******時間控制******/
		// date picker
		$scope.ivgStartDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.ivgEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {		
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.ivgStartDateOptions.maxDate = $scope.inputVO.EDate || $scope.maxDate;
			$scope.ivgEndDateOptions.minDate = $scope.inputVO.SDate || $scope.minDate;
		};
		
		/** 清除非常態交易 **/
		$scope.cleanNDate = function(){
			$scope.inputVO.NEDate = '';
			$scope.inputVO.NSDate = '';
		}
		/** 儲存 **/
		$scope.save = function(){
			if(null==$scope.inputVO.TSDate || ''==$scope.inputVO.TSDate
					||null==$scope.inputVO.TEDate || ''==$scope.inputVO.TEDate){
				$scope.showMsg("請選擇日期");
				return;
			}else if($scope.inputVO.TSDate > $scope.inputVO.TEDate){
				$scope.showMsg("日期錯誤");				
				return;
			}
			if(null!=$scope.inputVO.NSDate && ''!=$scope.inputVO.NSDate
					&& (null==$scope.inputVO.NEDate || ''==$scope.inputVO.NEDate)){
				$scope.showMsg("請選擇非常態交易結束日期");
				return;
			}else if(null!=$scope.inputVO.NEDate && ''!=$scope.inputVO.NEDate
					&& (null==$scope.inputVO.NSDate || ''==$scope.inputVO.NSDate)){
				$scope.showMsg("請選擇非常態交易開始日期");
				return;
			}else if($scope.inputVO.NSDate > $scope.inputVO.NEDate){
				$scope.showMsg("非常態交易日期錯誤");				
				return;
			}
			
			if(null==$scope.inputVO.tradeS || ''==$scope.inputVO.tradeS
					||null==$scope.inputVO.tradeE || ''==$scope.inputVO.tradeE){
				$scope.showMsg("請輸入交易天數");								
				return;
			}else if($scope.inputVO.tradeS > $scope.inputVO.tradeE){
				$scope.showMsg("交易天數錯誤");				
				return;
			}else if($scope.inputVO.tradeE > 31){
				$scope.showMsg("交易天數錯誤");				
				return;
			}
			$scope.inputVO.yearMon = $scope.resultList.YEARMON;
			$scope.sendRecv("PMS210", "saveShortTran", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
								$scope.showMsg("ehl_01_common_002"); //修改成功
						}
			});
		}
		
});