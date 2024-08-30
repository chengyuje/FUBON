/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS324_parameterController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) 
	{
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS324_parameterController";
		/*$scope.init = function()
		{
			
		};
		$scope.init();*/
		
		//初始化頁面，先查詢
		$scope.queryParm = function(){
			$scope.sendRecv("PMS324", "queryParm", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.parmList == null
									|| tota[0].body.parmList.length == 0){
								$scope.inputVO.loan_cont = '';
								$scope.inputVO.eiss_cnt = '';
								return;
							}else{
								$scope.inputVO.loan_cont = tota[0].body.parmList[0].LOAN_REF_CONT;
								$scope.inputVO.eiss_cnt = tota[0].body.parmList[0].EISS_CNT;
							}
						}
			});
		}
		$scope.queryParm();
		
	    //晉級門檻參數設定
		$scope.importparameter = function() 
		{
			$scope.inputVO.yearMon = $scope.yearMon;
			if($scope.inputVO.loan_cont == '' || $scope.inputVO.eiss_cnt == ''||$scope.inputVO.loan_cont == 0 || $scope.inputVO.eiss_cnt == 0)
			{
				$scope.showMsg('請檢核房貸轉介件數或信用卡卡數的輸入');
				return;
			}
			$scope.sendRecv("PMS324", "addParameter", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", $scope.inputVO,
					function(tota, isError) 
					{
						if (isError) 
						{
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
							$scope.showMsg("ehl_01_common_006");
					});
	    };
});