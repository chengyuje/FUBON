/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS410Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS410Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		// 設定資料統計月份需要以系統日往前幾月份
		$scope.inputVO.month = 24;
		$scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", $scope.inputVO,
					   function(totas, isError) {
				             	if (totas.length > 0) {
				               		$scope.ymList = totas[0].body.ymList;
				               	};
					   }
			);
		}
		$scope.initLoad();
		
		 //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dataMonthChange = function(){
        	if(!$scope.inputVO.dataMonth){
        		return;
        	}
        	$scope.inputVO.reportDate = $scope.inputVO.dataMonth;
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
       
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.check=false;
			$scope.inputVO = {					
					dataMonth: '',					
					reportDate: '',
					region_center_id  :'',  //區域中心
					branch_area_id      :'',//營運區	
					branch_nbr  :'',        //分行
					ao_code: ''			//理專
        	};
			$scope.paramList = [];
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
        		return;
        	}
			$scope.sendRecv("PMS410", "queryData", "com.systex.jbranch.app.server.fps.pms410.PMS410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.as=[];
							$scope.paramList = tota[0].body.resultList;
//						    alert(JSON.stringify($scope.paramList))
						/*0002230*/
							var a1=0;
							var a2=0;
							var a3=0;
							var a4=0;
							var a5=0;
							var a6=0;
							var a7=0;
							var a8=0;
							var a9=0;
							var a10=0;
							var a11=0;
							var a12=0;
							
							
							angular.forEach(tota[0].body.resultList2,function(row, index, objs){
								 a1+=row.TOTAL_STC_C1_PCTG;
								 a2+=row.TOTAL_STC_C1_NOP; 
								 a3+=row.TOTAL_STC_C2_PCTG;
								 a4+=row.TOTAL_STC_C2_NOP;
								 a5+=row.TOTAL_STC_C3_PCTG;
								 a6+=row.TOTAL_STC_C3_NOP;
								 a7+=row.TOTAL_STC_C4_PCTG;
								 a8+=row.TOTAL_STC_C4_NOP;
								 a9+=row.TOTAL_STC_C5_PCTG;
								 a10+=row.TOTAL_STC_C5_NOP;
								 a11+=row.TOTAL_STC_TOTAL_KYC;
								 a12+=row.TOTAL_STC_TOTAL_168;
								 });
							$scope.as[1]=a1;
							$scope.as[2]=a2;
							$scope.as[3]=a3;
							$scope.as[4]=a4;
							$scope.as[5]=a5;
							$scope.as[6]=a6;
							$scope.as[7]=a7;
							$scope.as[8]=a8;
							$scope.as[9]=a9;
							$scope.as[10]=a10;
							$scope.as[11]=a11;
							$scope.as[12]=a12;
							$scope.totalData = tota[0].body.totalList;	
							$scope.check=tota[0].body.SHOW;
//							alert(JSON.stringify(tota[0].body.SHOW))
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS410", "export", "com.systex.jbranch.app.server.fps.pms410.PMS410InputVO", $scope.inputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};		
});
