/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM812Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM812Controller";
		
		//初始化
		$scope.init = function(){
//			$scope.inputVO.cust_id = 'N120913984';  //test
//			$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire = function(){
			$scope.sendRecv("CRM812", "inquire", "com.systex.jbranch.app.server.fps.crm812.CRM812InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//						$scope.showMsg("ehl_01_common_009");
                		return;
					}
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					return;
				}
		)};
		$scope.inquire();
		
		
		//轉STR YYYYMMDD TO DATE
		$scope.transDate = function(row) {
			var pattern = /(\d{4})(\d{2})(\d{2})/;
			return new Date(row.replace(pattern, '$1-$2-$3'));
		}
});
		