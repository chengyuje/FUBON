/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS233_UPPIXController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "FPS233_UPPIXController";
	
	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
		
		};
		$scope.List = []; // 存放新增修改的
		$scope.resultList = []; // 存放前四筆List
		$scope.paramList = [{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '核心',
			'PRD_CTYPE_ID' : '6143','PRD_CTYPE_NAME' : '瑞銀中國股票基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		},{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '衛星',
			'PRD_CTYPE_ID' : '6144','PRD_CTYPE_NAME' : '中國入息組合基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		}];
		
		$scope.paramListBody = [{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '核心',
			'PRD_CTYPE_ID' : '6143','PRD_CTYPE_NAME' : '瑞銀中國股票基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		},{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '衛星',
			'PRD_CTYPE_ID' : '6144','PRD_CTYPE_NAME' : '中國入息組合基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		},{
			'PRID':'2',
			'MARKET' : '全球股票',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXX345XXX',
			'PRD_CTYPE' : '核心',
			'PRD_CTYPE_ID' : '6145','PRD_CTYPE_NAME' : '台灣基金股票',
			'PRD_CTYPE_1Y' : '35',
			'PRD_CTYPE_3Y' : '10',
			'PRD_CTYPE_3M' : '30',
			'PRD_CTYPE_6M' : '15',
			'YPER' : '25',
			
		},{
			'PRID':'2',
			'MARKET' : '全球股票',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XX11111X',
			'PRD_CTYPE' : '衛星',
			'PRD_CTYPE_ID' : '6146','PRD_CTYPE_NAME' : '優質海外股票',
			'PRD_CTYPE_1Y' : '55',
			'PRD_CTYPE_3Y' : '60',
			'PRD_CTYPE_3M' : '80',
			'PRD_CTYPE_6M' : '15',
			'YPER' : '25',
			
		},{
			'PRID':'2',
			'MARKET' : '新興市場',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXX345XXX',
			'PRD_CTYPE' : '核心',
			'PRD_CTYPE_ID' : '6147','PRD_CTYPE_NAME' : '正道基金會',
			'PRD_CTYPE_1Y' : '35',
			'PRD_CTYPE_3Y' : '10',
			'PRD_CTYPE_3M' : '30',
			'PRD_CTYPE_6M' : '15',
			'YPER' : '25',
			
		},{
			'PRID':'2',
			'MARKET' : '新興市場',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XX11111X',
			'PRD_CTYPE' : '衛星',
			'PRD_CTYPE_ID' : '6148','PRD_CTYPE_NAME' : '優質股基金會',
			'PRD_CTYPE_1Y' : '55',
			'PRD_CTYPE_3Y' : '60',
			'PRD_CTYPE_3M' : '80',
			'PRD_CTYPE_6M' : '15',
			'YPER' : '25',
			
		}];
		
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
	
	
	
	
	$scope.inquire = function() {
		if($scope.inputVO.flag!='Y' && $scope.inputVO.flag2!='Y'){
			$scope.showErrorMsg("本月主推基金,至少擇一輸入!!!");
			return;
		}
			
		$scope.paramList = [{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '核心',
			'PRD_CTYPE_ID' : '6143','PRD_CTYPE_NAME' : '瑞銀中國股票基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		},{
			'PRID':'1',
			'MARKET' : '大中華',
			'WEATHER9' : '1',
			'WEATHER8' : '1',
			'WEATHER7' : '1',
			'NMARKET' : 'XXXXXXXXXX',
			'PRD_CTYPE' : '衛星',
			'PRD_CTYPE_ID' : '6144','PRD_CTYPE_NAME' : '中國入息組合基金',
			'PRD_CTYPE_1Y' : '15',
			'PRD_CTYPE_3Y' : '40',
			'PRD_CTYPE_3M' : '10',
			'PRD_CTYPE_6M' : '25',
			'YPER' : '25',
			
		}];
		
		if(!($scope.inputVO.flag=='Y' && $scope.inputVO.flag2=='Y')){
			if($scope.inputVO.flag=='Y')
				$scope.flags='核心';
			else
				$scope.flags='衛星';
//			$filter('filter')($scope.paramList,$scope.flags, true);
			$filter('filter')($scope.friends, $scope.search, true);

		}
			
		

	}

	
	
	
	$scope.saveleft=function(){
		$scope.closeThisDialog('close');
	}
});
