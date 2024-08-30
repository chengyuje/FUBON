/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS358_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS358_DETAILController";
		$scope.init = function(){
			$scope.inputVO = {
				strDate: $scope.row.DATA_DATE,
				branch_nbr: $scope.row.BRANCH_NBR,
				ao_code: $scope.row.AO_CODE,
				reportType: $scope.row.RTYPE,
				cust_type: $scope.row.CUST_TYPE
        	};
			console.log($scope.row);
			if($scope.showType == ""){
				$scope.inputVO.cust_type = ""; //如果是點選分行則顯示理財戶+Mass戶
			}
			$scope.inputVO2 = $scope.inputVO;
			$scope.outputVO2= $scope.outputVO;
			$scope.yearmon = $scope.row.DATA_DATE;
			$scope.sumFlag = false;
			$scope.rowtype1=$scope.rowtype;
        };
        $scope.init();
        
        /*** 查詢資料 ***/
		$scope.query = function(){

			$scope.sumFlag = false;
			var method = "";
			
			if($scope.type == 'AO'){
				method = "queryAODetail";
			}else {
				method = "queryBRDetail";
			}
			
			$scope.inputVO2.reportType = $scope.inputVO.reportType;
			$scope.sumWEEK_BAL = 0;
			$scope.sumLMON_BAL = 0;
			$scope.sumCD_DIFF = 0;
			$scope.sendRecv("PMS358", method, "com.systex.jbranch.app.server.fps.pms358.PMS358InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if($scope.type == 'AO'){
								if(tota[0].body.aoDetail.length == 0) {
									$scope.showMsg("ehl_01_common_009");
									return;
								}														
								$scope.paramList = tota[0].body.aoDetail;								
							}
							else{
								if(tota[0].body.brDetail.length == 0) {
									$scope.showMsg("ehl_01_common_009");
									return;
								}														
								$scope.paramList = tota[0].body.brDetail;
							}
							//資料日期加斜線:週報YY//MM/YY
							if($scope.inputVO.reportType == 'week'){
								$scope.BAL_NAME = "本週台定餘額";				
								angular.forEach($scope.paramList, function(row, index, objs){
									row.DATA_DATE = row.DATA_DATE.substring(0, 4) + "/" + row.DATA_DATE.substring(4,6) + "/" + row.DATA_DATE.substring(6,8); //日期加斜線
								});	
							}
							//資料日期加斜線:週報YY//MM
							if($scope.inputVO.reportType == 'month'){
								$scope.BAL_NAME = "本月台定餘額";
								angular.forEach($scope.paramList, function(row, index, objs){
									row.DATA_DATE = $scope.yearmon;
								});		
							}
							
							if($scope.type == 'BR'){		
								angular.forEach($scope.paramList, function(row, index, objs){
									//空AO_CODE : MASS戶
									if(row.AO_CODE == null || row.AO_CODE == '' || row.AO_CODE == '000'){
										row.AO_CODE = '000';
										row.EMP_NAME = 'MASS戶'
									//AO_CODE+EMP_NAME
									}
								});		
							}
							
//							//身分證遮蔽:只有理專會使用到
//							if($scope.type == 'AO'){
//								angular.forEach($scope.paramList, function(row, index, objs){
//									row.CUST_ID = row.CUST_ID.substring(0, 4)+"***"+row.CUST_ID.substring(7, 10); //隱藏身分證  三碼
//								});	
//							}
							$scope.sumFlag = true;
							if($scope.rowtype1 !='MASS戶'){
								for(var i = 0; i < $scope.paramList.length; i++){
									if($scope.paramList[i].CUST_TYPE == 'MASS戶'){
										continue;
									}
									$scope.paramList[i].reportType = $scope.inputVO.reportType;
									$scope.sumWEEK_BAL += $scope.paramList[i].WEEK_BAL;
									$scope.sumLMON_BAL += $scope.paramList[i].LMON_BAL;	
									$scope.sumCD_DIFF += $scope.paramList[i].CD_DIFF;
								}
							} 
							
							$scope.outputVO = tota[0].body;		
							return;
						}						
			});
		};
		$scope.query();
		$scope.queryAODeatil = function(index){
			for(var i = 0 ; i < $scope.paramList.length ; i++){
				if($scope.inputVO2.ao_code == undefined || $scope.paramList[i].ROWNUM == index+1){
					$scope.inputVO2.ao_code = $scope.paramList[i].AO_CODE;
				}
			}
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
	
							$scope.sumFlag = true;
							for(var i = 0; i < $scope.paramList2.length; i++){
								$scope.sumWEEK_BAL2 += $scope.paramList2[i].WEEK_BAL;
								$scope.sumLMON_BAL2 += $scope.paramList2[i].LMON_BAL;	
							}
							$scope.outputVO2 = tota2[0].body;	
							$scope.outputVO3 = JSON.stringify($scope.paramList2.length)/10;
//							alert(JSON.stringify($scope.paramList2.length)/10);
							return;
						}						
			});
		};
		$scope.showDetail = function(row, type){ 
			var reportType=$scope.inputVO.reportType;
        	var dialog = ngDialog.open({
				template: 'assets/txn/PMS358/PMS358_AO_DETAIL.html',
				className: 'PMS358_AO_DETAIL',					
                controller: ['$scope', function($scope) {
                	console.log(row);
                	$scope.row = row;
                	$scope.type = type;
                	$scope.reportType=reportType;
                }]
            });
        };
});
