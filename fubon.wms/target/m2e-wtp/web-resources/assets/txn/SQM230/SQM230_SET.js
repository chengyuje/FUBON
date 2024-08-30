/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM230_SETController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM230_SETController";
		
		//初始化
		$scope.init = function(){
			$scope.resultList = [];
			$scope.inputVO.emp_dp_4    = null;
			$scope.inputVO.im_sup_dp_4 = null;
			$scope.inputVO.so_sup_dp_4 = null;
			$scope.inputVO.emp_dp_5    = null;
			$scope.inputVO.im_sup_dp_5 = null;
			$scope.inputVO.so_sup_dp_5 = null;
			
			$scope.sendRecv("SQM230", "inquireScore", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", {},
						function(tota, isError) {
							if (!isError) {
//								alert(JSON.stringify(tota[0].body.resultList));
								$scope.resultList = tota[0].body.resultList;
								if($scope.resultList.length > 0){
									angular.forEach($scope.resultList, function(row) {
										if(row.CS_TYPE == '4'){
											$scope.inputVO.emp_dp_4    = row.EMP_DP;
											$scope.inputVO.im_sup_dp_4 = row.IM_SUP_DP;
											$scope.inputVO.so_sup_dp_4 = row.SO_SUP_DP;
											
										} else if (row.CS_TYPE == '5'){
											$scope.inputVO.emp_dp_5    = row.EMP_DP;
											$scope.inputVO.im_sup_dp_5 = row.IM_SUP_DP;
											$scope.inputVO.so_sup_dp_5 = row.SO_SUP_DP;
										}
									});
								}
							}
			});
		}
		
		$scope.init();
		
		$scope.saveScore = function(){
			$scope.sendRecv("SQM230", "saveScore", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
							$scope.closeThisDialog('successful');
						}
		});
		}
});