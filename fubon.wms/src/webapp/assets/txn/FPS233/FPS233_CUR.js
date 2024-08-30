/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS233_CURController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "FPS233_CURController";
	
	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
			/**
			 * 潛力金流條件種類 含息報酬率 台幣庫存金額
			 */
			ROI1 : '', // 基金
			ROI2 : '', // ETF
			ROI3 : '', // SI/SN
			ROI4 : '', // 海外債
			AMT_TWD1 : '', // 基金
			AMT_TWD2 : '', // ETF
			AMT_TWD3 : '', // SI/SN
			AMT_TWD4 : '', // 海外債
			TYPE : '',
			ROI : '',
			AMT_TWD : '',
			TER_FEE_YEAR : '', // 險種年期
			INS_NBR : '', // 險種代號
			INS_NAME : '', // 險種名稱
			INS : '',//頁面險種代號
			YEAR : ''//頁面險種年期
		};
		$scope.List = []; // 存放新增修改的
		$scope.resultList = []; // 存放前四筆List
		$scope.paramList = []; // 存放最後List
		$scope.insList = [];//暫存保險資料List
	};
	$scope.init();
	
	//檢查是否大於零
	$scope.check=function(data){
	   data=data.replace(/[^\d\.]/g,'');
	   
	   if(data==0&&data!='') 
		  $scope.showErrorMsg("數值不得零及負數，請重新輸入");
	   return data;
	   
   }
	
	
	$scope.inquire = function() {}
	$scope.inquire();

	//保險加入
	$scope.saveins = function() {}
	
	//保險刪除
	$scope.delins = function(row) {
		//刪除前端資料，不更動後端資料庫
		for(var i = 0; i < $scope.insList.length; i++) {
			if($scope.insList[i].INS_NBR == row.INS_NBR){
				$scope.insList.splice(i,1);
				return;
			}
		}
	
	}
	
	/**彈跳視窗打開FPS233_CUR
	 * $scope.dialog = '1'
	 * 以此判斷是否關閉彈跳視窗
	 */
	
	
	$scope.saveleft=function(){
		$scope.showSuccessMsg("儲存成功");
		$scope.closeThisDialog('close');
	}
});
