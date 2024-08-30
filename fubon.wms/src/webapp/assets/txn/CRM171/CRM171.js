/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM171Controller',	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm,  $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM171Controller";
	
		$scope.sumRecTot = 0;
		$scope.sumCust_id = 0;
		$scope.paramList=[];
		$scope.cust_id = '';
		$scope.init = function (){
			$scope.inputVO = {};
		}
		$scope.init();
		
		$scope.cust_id = $scope.connector('get','CRM110_CUST_ID');
		
//		$scope.cust_id = $scope.CRM_CUSTVO.CUST_ID;
//		console.log("asdadasadsfas!!!!!!!!!!!!!!!!!!");
//		console.log(JSON.stringify($scope.cust_id));
//		console.log(JSON.stringify(sysInfoService));

		
		$scope.inputVO.TYPE = 'N';   //N-未轉銷、Y-已轉銷
		$scope.inputVO.FLAG = 'Y';
//		 {'emp_id':$scope.inputVO.EMP_ID, 'CUST_ID':$scope.inputVO.CUST_ID, 'TYPE':$scope.inputVO.TYPE,'FLAG':$scope.inputVO.FLAG}

	
		//判斷為工作首頁OR客戶首頁
		if($scope.cust_id != undefined ){
			$scope.inputVO.CUST_ID = $scope.cust_id;
			
			$scope.sendRecv("CRM171", "getCustCFData", "com.systex.jbranch.app.server.fps.crm171.CRM171InputVO",$scope.inputVO ,
					function(tota, isError) {
						if (!isError) {

							if(tota[0].body.resultList!=null && tota[0].body.resultList.length > 0) {
								$scope.paramList1 = tota[0].body.resultList;
								$scope.outputVO1  = tota[0].body;
								//合計function
								for(var i = 0; i < $scope.paramList1.length; i++){
										$scope.sumRecTot1 += $scope.paramList1[i].REC_CF_TOTAL;
								}
								for(var i = 0; i < $scope.paramList1.length; i++){
									$scope.sumRecTot2 += $scope.paramList1[i].EXP_CF_TOTAL;
								}
								$scope.sumRecTot3 = $scope.paramList1[0].REC_CF_TOTAL;
								$scope.sumCust_id1 = $scope.paramList1[0].REC_CF_PLAN;
								$scope.sumCust_id2 = ($scope.paramList1[0].REC_CF_TOTAL) - ($scope.paramList1[0].REC_CF_PLAN);

//								console.log("客首");				
//								console.log(JSON.stringify($scope.sumCust_id2));				
							}
						}
			});
		}else{
			$scope.inputVO.EMP_ID = String(sysInfoService.getUserID());
			$scope.inputVO.AO_CODE = String(sysInfoService.getAoCode());
			$scope.inputVO.CUST_NBR = String(sysInfoService.getBranchID());
			$scope.inputVO.CUST_BRANCH = String(sysInfoService.getAreaID());
			$scope.inputVO.CUST_REGION = String(sysInfoService.getRegionID());
//			$scope.inputVO.REGION_NAME = String(sysInfoService.getBranchName());
			
			//目前輔銷科長沒有getAvailArea
			if(sysInfoService.getPriID() != "015" && sysInfoService.getPriID() != "024"){
				$scope.inputVO.REGION_NAME = String(sysInfoService.getAvailArea()[0].BRANCH_AREA_NAME);
			}
			

			
//			console.log("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//			console.log(JSON.stringify(sysInfoService.getBranchID()));
//			console.log(JSON.stringify($scope.inputVO));
			
			var d = new Date();
			var y = d.getFullYear();
			var m = ((d.getMonth()+1)<10? '0': '') + (d.getMonth()+1);
			var ym = y+m;
			//BRANCH_AREA_NAME
//			alert(JSON.stringify(sysInfoService.getRegionID()));
//			alert(JSON.stringify($scope.d)+"/"+JSON.stringify($scope.m));
//			alert(JSON.stringify($scope.inputVO.CUST_BRANCH));

			
			$scope.sendRecv("CRM171", "getCFData", "com.systex.jbranch.app.server.fps.crm171.CRM171InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {

							if(tota[0].body.resultList!=null && tota[0].body.resultList.length > 0) {
								$scope.paramList = tota[0].body.resultList;
								$scope.outputVO  = tota[0].body;
								//合計function
								for(var i = 0; i < $scope.paramList.length; i++){
										$scope.sumRecTot += $scope.paramList[i].REC_CF_TOTAL;
								}
//								$scope.sumCust_id = $scope.paramList.length;
								$scope.sumCust_id = $scope.paramList[0].CUST_CNT_UNPLAN;
//								console.log("工首= "+JSON.stringify(tota[0].body));
							}
						}
			});
		}
		
		$scope.goPMS101 = function(){
			$scope.connector('set','CRM171_lastMonthYN', 'Y' ); //時間
			$scope.connector('set','CRM171_TYPE', 'Y');  //未轉銷
			if($scope.cust_id != undefined ){
				$scope.connector('set','CRM171_CUSTID', $scope.inputVO.CUST_ID);
				$scope.connector('set','CRM110_CUST_ID', $scope.inputVO.cust_id);
	        	var path = "assets/txn/PMS101/PMS101.html";
				$scope.connector("set","CRM610URL",path);
			}else{
				$scope.connector('set','CRM171_lastMonthYN', ym);
				$scope.connector('set','CRM171_TYPE', $scope.inputVO.TYPE);
				$scope.connector('set','CRM171_CUSTID', '');
				$scope.connector('set','CRM171_AO_CODE', $scope.inputVO.AO_CODE);
				//
				$scope.connector('set','CRM171_BRANCH', $scope.inputVO.CUST_NBR);
//				$scope.connector('set','CRM171_REGIONNAME', $scope.inputVO.REGION_NAME);
				$scope.connector('set','CRM171_REGIONID',$scope.inputVO.CUST_REGION);

				$rootScope.menuItemInfo.url = "assets/txn/PMS101/PMS101.html";
			}
			
		};

	
    });
