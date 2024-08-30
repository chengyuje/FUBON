/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS358_AO_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS358_AO_DETAILController";
		$scope.init = function(){
			$scope.inputVO = {
				strDate: $scope.row.DATA_DATE,
				branch_nbr: $scope.row.BRANCH_NBR,
				ao_code: $scope.row.AO_CODE,
				reportType: $scope.reportType,
				cust_type: $scope.row.CUST_TYPE
        	};
			$scope.inputVO2 = $scope.inputVO;
			$scope.outputVO2= $scope.outputVO;
			$scope.yearmon = $scope.row.DATA_DATE;
			$scope.sumFlag = false;
        };
        $scope.init();
        /*** 查詢資料 ***/

		$scope.queryAODeatil = function(index){
			console.log($scope.inputVO2);
//			for(var i = 0 ; i < $scope.paramList.length ; i++){
//				if($scope.inputVO2.ao_code == undefined || $scope.paramList[i].ROWNUM == index+1){
//					$scope.inputVO2.ao_code = $scope.paramList[i].AO_CODE;
//				}
//			}
			$scope.sumWEEK_BAL2 = 0;
			$scope.sumLMON_BAL2 = 0;
			$scope.sendRecv("PMS358", "queryAODetail" , "com.systex.jbranch.app.server.fps.pms358.PMS358InputVO", $scope.inputVO2,
					function(tota2, isError) {
						if (!isError) {
								if(tota2[0].body.aoDetail.length == 0) {
									$scope.showMsg("ehl_01_common_009");
									return;
								}														
								$scope.paramList2 = tota2[0].body.aoDetail;								
							
							//資料日期加斜線:週報YY//MM/YY
							if($scope.inputVO.reportType == 'week'){
								$scope.BAL_NAME = "本週台定餘額";				
								angular.forEach($scope.paramList2, function(row2, index, objs){
									row2.DATA_DATE = row2.DATA_DATE.substring(0, 4) + "/" + row2.DATA_DATE.substring(4,6) + "/" + row2.DATA_DATE.substring(6,8); //日期加斜線
								});	
							}
							//資料日期加斜線:週報YY//MM
							if($scope.inputVO2.reportType == 'month'){
								$scope.BAL_NAME = "本月台定餘額";
								angular.forEach($scope.paramList2, function(row2, index, objs){
									row2.DATA_DATE = $scope.yearmon;
								});		
							}
							
							if($scope.type == 'BR'){		
								angular.forEach($scope.paramList2, function(row2, index, objs){
									//空AO_CODE : MASS戶
									if(row2.AO_CODE == null || row2.AO_CODE == '' || row2.AO_CODE == '000'){
										row2.AO_CODE = '000';
										row2.EMP_NAME = 'MASS戶'
									//AO_CODE+EMP_NAME
									}
								});		
							}
							
//							//身分證遮蔽:只有理專會使用到
//							if($scope.type == 'AO'){
//								angular.forEach($scope.paramList2, function(row2, index, objs){
//									row2.CUST_ID = row2.CUST_ID.substring(0, 4)+"***"+row2.CUST_ID.substring(7, 10); //隱藏身分證  三碼
//								});	
//							}
//	
							$scope.sumFlag = true;
							for(var i = 0; i < $scope.paramList2.length; i++){
								$scope.sumWEEK_BAL2 += $scope.paramList2[i].WEEK_BAL;
								$scope.sumLMON_BAL2 += $scope.paramList2[i].LMON_BAL;	
							}
							console.log($scope.paramList2);
							$scope.outputVO2 = tota2[0].body;	
							$scope.outputVO3 = JSON.stringify($scope.paramList2.length)/10;
//							alert(JSON.stringify($scope.paramList2.length)/10);
							return;
						}						
			});
		};
		$scope.queryAODeatil();
});
