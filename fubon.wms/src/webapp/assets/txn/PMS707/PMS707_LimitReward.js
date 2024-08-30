'use strict';
eSoafApp.controller('PMS707_LimitRewardController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS707_LimitRewardController";

	$scope.init = function() {
		$scope.inputVO = {
				yearMon :$scope.yearMon,
				userId  :projInfoService.getUserID()
		};
		$scope.resultList = [];
	};
	$scope.init();
	
	/** 查詢 * */
	$scope.queryLimitReward = function() {
		$scope.sendRecv("PMS707", "queryLimitReward",
				"com.systex.jbranch.app.server.fps.pms707.PMS707InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList == null
								|| tota[0].body.resultList.length == 0) {
							$scope.resultList = [];
							return;
						}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
				});
	}
	$scope.queryLimitReward();
	
	/** 初始化獲取所有的參數源：案件來源，消金商品1，消金商品2 **/
	var vo = {'param_type': 'PMS.LOAN_CASE_SOURCE', 'desc': false};
    if(!projInfoService.mappingSet['PMS.LOAN_CASE_SOURCE']) {
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['PMS.LOAN_CASE_SOURCE'] = totas[0].body.result;
    			$scope.mappingSet['PMS.LOAN_CASE_SOURCE'] = projInfoService.mappingSet['PMS.LOAN_CASE_SOURCE'];        		
    		}
    	});
    } else
    	$scope.mappingSet['PMS.LOAN_CASE_SOURCE'] = projInfoService.mappingSet['PMS.LOAN_CASE_SOURCE'];
    
    var vo = {'param_type': 'PMS.LOAN_PROD_TYPE_1', 'desc': false};
    if(!projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_1']) {
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_1'] = totas[0].body.result;
    			$scope.mappingSet['PMS.LOAN_PROD_TYPE_1'] = projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_1'];        		
    		}
    	});
    } else
    	$scope.mappingSet['PMS.LOAN_PROD_TYPE_1'] = projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_1'];
    
    var vo = {'param_type': 'PMS.LOAN_PROD_TYPE_2', 'desc': false};
    if(!projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_2']) {
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_2'] = totas[0].body.result;
    			$scope.mappingSet['PMS.LOAN_PROD_TYPE_2'] = projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_2'];        		
    		}
    	});
    } else
    	$scope.mappingSet['PMS.LOAN_PROD_TYPE_2'] = projInfoService.mappingSet['PMS.LOAN_PROD_TYPE_2'];
	
	//將相應行的數據移出resultList
	$scope.del = function(delIndex){
		$scope.resultList.splice(delIndex,1);
	}
    
  //將輸入添加到resultList并顯示
	$scope.add = function(){
		var obj = new Object();
		obj.YEARMON = $scope.yearMon;
		//參數源轉換
		if($scope.CASE_SOURCE=="01"){$scope.CASE_SOURCE = "轉介"};
		if($scope.CASE_SOURCE=="02"){$scope.CASE_SOURCE = "非轉介"};
		if($scope.PROD_TYPE_1=="01"){$scope.PROD_TYPE_1 = "房貸"};
		if($scope.PROD_TYPE_1=="02"){$scope.PROD_TYPE_1 = "信貸"};
		if($scope.PROD_TYPE_2=="01"){$scope.PROD_TYPE_2 = "中長期"};
		if($scope.PROD_TYPE_2=="02"){$scope.PROD_TYPE_2 = "額度式"};
		obj.CASE_SOURCE = $scope.CASE_SOURCE;
		obj.PROD_TYPE_1 = $scope.PROD_TYPE_1;
		obj.PROD_TYPE_2 = $scope.PROD_TYPE_2;
		obj.BEGIN_MON = $scope.BEGIN_MON;
		obj.END_MON = $scope.END_MON;
		obj.BONUS = $scope.BONUS;
		obj.BONUS_DIS = $scope.BONUS_DIS;
		if (null == obj.CASE_SOURCE || "" == obj.CASE_SOURCE
			|| null == obj.PROD_TYPE_1 || "" == obj.PROD_TYPE_1
			|| null == obj.PROD_TYPE_2 || "" == obj.PROD_TYPE_2
			|| null == obj.BEGIN_MON || "" == obj.BEGIN_MON
			|| null == obj.END_MON || "" == obj.END_MON
			|| null == obj.BONUS || "" == obj.BONUS
			|| null == obj.BONUS_DIS || "" == obj.BONUS_DIS) {
			//$scope.showMsg("ehl_01_common_022");	//欄位檢核錯誤：必要輸入欄位
			$scope.showMsg("欄位檢核錯誤:請重新核准必填欄位信息");
			return;
		}
		//校驗主鍵是否唯一
		for(var i in $scope.resultList){
			if($scope.yearMon == $scope.resultList[i].YEARMON&&
				$scope.CASE_SOURCE == $scope.resultList[i].CASE_SOURCE&&
				$scope.PROD_TYPE_1 == $scope.resultList[i].PROD_TYPE_1&&
				$scope.PROD_TYPE_2 == $scope.resultList[i].PROD_TYPE_2&&
				$scope.BEGIN_MON == $scope.resultList[i].BEGIN_MON){
				//$scope.showMsg("ehl_01_common_016");	//資料已存在
				$scope.showMsg("違反主鍵唯一性約束");
				return;
			}
		}
		
		//校驗數值輸入最大值
		if(!($scope.BEGIN_MON>=0&&$scope.BEGIN_MON<=9999999999999999.99)){
			$scope.showMsg("欄位檢核錯誤:請重新核准撥款額度級距(起)的大小");
			return;
		}
		if(!($scope.END_MON>=0&&$scope.END_MON<=9999999999999999.99)){
				$scope.showMsg("欄位檢核錯誤:請重新核准撥款額度級距(迄)的大小");
				return;
		}
		if(!($scope.BONUS>=0&&$scope.BONUS<=9999999999999999.99)){
				$scope.showMsg("欄位檢核錯誤:請重新核准撥款對應獎金大小");
				return;
		}
		if(!($scope.BONUS_DIS>=0&&$scope.BONUS_DIS<=9.99999)){
				$scope.showMsg("欄位檢核錯誤:請重新核准未以標準利率承作獎金折扣的大小");
				return;
		}
		
		//校驗撥款額度級距
		if(Number($scope.BEGIN_MON) >= Number($scope.END_MON) ) {
			$scope.showMsg("撥款額度級距(起) >= 撥款額度級距(迄)");
			return;	//若撥款額度級距(起) >= 撥款額度級距(迄)單位：百萬 
		}
		
		for(var j = 0; j < $scope.resultList.length ; j++){
			if($scope.yearMon==$scope.resultList[j].YEARMON&&
				$scope.CASE_SOURCE==$scope.resultList[j].CASE_SOURCE&&
				$scope.PROD_TYPE_1==$scope.resultList[j].PROD_TYPE_1&&
				$scope.PROD_TYPE_2==$scope.resultList[j].PROD_TYPE_2){
				var nextBEGIN_MON = Number($scope.resultList[j].BEGIN_MON);
				var nextEND_MON = Number($scope.resultList[j].END_MON);
				if(($scope.BEGIN_MON < nextEND_MON) && ($scope.END_MON >= nextEND_MON)){
					$scope.showMsg("撥款額度級距區間重複");
					return;		//有重複數據
				}
				if(($scope.BEGIN_MON > nextBEGIN_MON) && ($scope.BEGIN_MON <= nextEND_MON)){
					$scope.showMsg("撥款額度級距區間重複");
					return;		//有重複數據
				}
				if($scope.BEGIN_MON == nextBEGIN_MON){
					$scope.showMsg("撥款額度級距區間重複");
					return;		//有重複數據
				}
			}
			
		}
		$scope.resultList.push(obj);
		$scope.CASE_SOURCE = null;
		$scope.PROD_TYPE_1 = null;
		$scope.PROD_TYPE_2 = null;
		$scope.BEGIN_MON = null;
		$scope.END_MON = null;
		$scope.BONUS = null;
		$scope.BONUS_DIS = null;
	}
	
	//儲存數據
	$scope.save= function(){
		$scope.inputVO.inputList = $scope.resultList;
		$scope.sendRecv("PMS707", "addLimitReward", "com.systex.jbranch.app.server.fps.pms707.PMS707InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
							$scope.showMsg("ehl_01_common_002");
					}else{
							$scope.showMsg("ehl_01_common_007");
					}
		});
	}
});