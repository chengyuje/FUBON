/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM151_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM151_DETAILController";		
		
		$scope.colorlist = [];
		
		//查詢
		$scope.initial = function (){
			$scope.inputVO.emp_id = $scope.emp;
			var lastMonthDate = new Date(); 
	        $scope.DateList = [];
			//近12個月
			for(var i = 0; i < 12; i++) {
				//1-9月前面補0 ==> 01-09月
				if((lastMonthDate.getMonth()+1) < 10){
					$scope.DateList.push( {DATE :(lastMonthDate.getFullYear()+ "/0" + (lastMonthDate.getMonth()+1))} );
				}else{
					$scope.DateList.push( {DATE :(lastMonthDate.getFullYear()+ "/" + (lastMonthDate.getMonth()+1))} );
				}								
				lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);
			}

		
			//取得近一年理專達成狀況
			if($scope.set =='1'){
				$scope.sendRecv("CRM151", "detailb","com.systex.jbranch.app.server.fps.crm151.CRM151InputVO",$scope.inputVO, 
						function(tota, isError) {
							if(!isError){
			        	    	$scope.result = tota[0].body.resultList;
			        	    	$scope.AO_data();
			        	    }else{
			        	    	return;
			        	    }
				});
			}
			//取得近一年分行達成狀況
			if($scope.set=='2'){
				$scope.sendRecv("PMS000", "getUnAchBranchList", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
						function(tota, isError) {
			        	    if(!isError){
			        	    	$scope.result = tota[0].body.resultList;
			        	    	$scope.BR_data();
			        	    }else{
			        	    	return;
			        	    }
						});
				
			}
		};
		
		$scope.initial();
		
		
		//整理AO資料
		$scope.AO_data = function (){ 
			//目標達成、實際達成資料
			$scope.paramlist = [];
			var GOAL1 = 0 ,GOAL2 = 0 ,GOAL3 = 0 ,GOAL4 = 0 ,GOAL5 = 0 ,GOAL6 = 0 ,GOAL7 = 0 ,GOAL8 = 0 ,GOAL9 = 0 ,GOAL10 = 0 ,GOAL11 = 0 ,GOAL12 = 0 ;
			var fTOTAL = 0 ,FEE1 = 0 ,FEE2 = 0 ,FEE3 = 0 ,FEE4 = 0 ,FEE5 = 0 ,FEE6 = 0 ,FEE7 = 0 ,FEE8 = 0 ,FEE9 = 0 ,FEE10 = 0 ,FEE11 = 0 ,FEE12 = 0 ;		
			var COLOR1 = 0 ,COLOR2 = 0 ,COLOR3 = 0 ,COLOR4 = 0 ,COLOR5 = 0 ,COLOR6 = 0 ,COLOR7 = 0 ,COLOR8 = 0 ,COLOR9 = 0 ,COLOR10 = 0 ,COLOR11 = 0 ,COLOR12 = 0 ;
					
			//抓對應資料
			for(var i = 0; i < $scope.result.length; i++) {	
				//近一年12個月實際、達成									
				switch ($scope.result[i].DATA_DATE) {			
					case $scope.DateList[0].DATE.replace("/",""):
						GOAL1 = $scope.result[i].MTD_SUM_GOAL;
						FEE1 = $scope.result[i].MTD_SUM_FEE;
						COLOR1 = (GOAL1 <= FEE1 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[1].DATE.replace("/",""):
						GOAL2 = $scope.result[i].MTD_SUM_GOAL;
						FEE2 = $scope.result[i].MTD_SUM_FEE;
						COLOR2 = (GOAL2 <= FEE2 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[2].DATE.replace("/",""):
						GOAL3 = $scope.result[i].MTD_SUM_GOAL;
						FEE3 = $scope.result[i].MTD_SUM_FEE;
						COLOR3 = (GOAL3 <= FEE3 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[3].DATE.replace("/",""):
						GOAL4 = $scope.result[i].MTD_SUM_GOAL;
						FEE4 = $scope.result[i].MTD_SUM_FEE;
						COLOR4 = (GOAL4 <= FEE4 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[4].DATE.replace("/",""):
						GOAL5 = $scope.result[i].MTD_SUM_GOAL;
						FEE5 = $scope.result[i].MTD_SUM_FEE;
						COLOR5 = (GOAL5 <= FEE5 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[5].DATE.replace("/",""):
						GOAL6 = $scope.result[i].MTD_SUM_GOAL;
						FEE6 = $scope.result[i].MTD_SUM_FEE;
						COLOR6 = (GOAL6 <= FEE6 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[6].DATE.replace("/",""):
						GOAL7 = $scope.result[i].MTD_SUM_GOAL;
						FEE7 = $scope.result[i].MTD_SUM_FEE;
						COLOR7 = (GOAL7 <= FEE7 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[7].DATE.replace("/",""):
						GOAL8 = $scope.result[i].MTD_SUM_GOAL;
						FEE8 = $scope.result[i].MTD_SUM_FEE;
						COLOR8 = (GOAL8 <= FEE8 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[8].DATE.replace("/",""):
						GOAL9 = $scope.result[i].MTD_SUM_GOAL;
						FEE9 = $scope.result[i].MTD_SUM_FEE;
						COLOR9 = (GOAL9 <= FEE9 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[9].DATE.replace("/",""):
						GOAL10 = $scope.result[i].MTD_SUM_GOAL;
						FEE10 = $scope.result[i].MTD_SUM_FEE;
						COLOR10 = (GOAL10 <= FEE10 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[10].DATE.replace("/",""):
						GOAL11 = $scope.result[i].MTD_SUM_GOAL;
						FEE11 = $scope.result[i].MTD_SUM_FEE;
						COLOR11 = (GOAL11 <= FEE11 ? "color:blue" : "color:red");
					break;
					case $scope.DateList[11].DATE.replace("/",""):
						GOAL12 = $scope.result[i].MTD_SUM_GOAL;
						FEE12 = $scope.result[i].MTD_SUM_FEE;
						COLOR12 = (GOAL12 <= FEE12 ? "color:blue" : "color:red");
					break;
				}			
    		}
			
			$scope.paramlist.push({
				EMP_NAME : $scope.result[0].EMP_NAME ,
				BRANCH : $scope.result[0].BRANCH_NAME ,
				AREA : $scope.result[0].BRANCH_AREA_NAME ,
				GOAL1 : GOAL1 ,	FEE1 : FEE1 , COLOR1 : COLOR1 ,
				GOAL2 : GOAL2 ,	FEE2 : FEE2 , COLOR2 : COLOR2 ,
				GOAL3 : GOAL3 ,	FEE3 : FEE3 , COLOR3 : COLOR3 ,
				GOAL4 : GOAL4 ,	FEE4 : FEE4 , COLOR4 : COLOR4 ,
				GOAL5 : GOAL5 ,	FEE5 : FEE5 , COLOR5 : COLOR5 ,
				GOAL6 : GOAL6 ,	FEE6 : FEE6 , COLOR6 : COLOR6 ,
				GOAL7 : GOAL7 ,	FEE7 : FEE7 , COLOR7 : COLOR7 ,
				GOAL8 : GOAL8 ,	FEE8 : FEE8 , COLOR8 : COLOR8 ,
				GOAL9 : GOAL9 ,	FEE9 : FEE9 , COLOR9 : COLOR9 ,
				GOAL10 : GOAL10 , FEE10 : FEE10 , COLOR10 : COLOR10 ,
				GOAL11 : GOAL11 , FEE11 : FEE11 , COLOR11 : COLOR11 ,
				GOAL12 : GOAL12 , FEE12 : FEE12 , COLOR12 : COLOR12 ,
				fTOTAL : $scope.result[0].AVG  
			});	
			return;
			
		}
		
		//===============================================================================
		
		//整理BR資料
		$scope.BR_data = function (){ 				
			
			//目標達成、實際達成資料
			$scope.paramlist = [];
			$scope.BR_check = '';
			var GOAL1 = 0 ,GOAL2 = 0 ,GOAL3 = 0 ,GOAL4 = 0 ,GOAL5 = 0 ,GOAL6 = 0 ,GOAL7 = 0 ,GOAL8 = 0 ,GOAL9 = 0 ,GOAL10 = 0 ,GOAL11 = 0 ,GOAL12 = 0 ;
			var fTOTAL = 0 ,FEE1 = 0 ,FEE2 = 0 ,FEE3 = 0 ,FEE4 = 0 ,FEE5 = 0 ,FEE6 = 0 ,FEE7 = 0 ,FEE8 = 0 ,FEE9 = 0 ,FEE10 = 0 ,FEE11 = 0 ,FEE12 = 0 ;
			var COLOR1 = 0 ,COLOR2 = 0 ,COLOR3 = 0 ,COLOR4 = 0 ,COLOR5 = 0 ,COLOR6 = 0 ,COLOR7 = 0 ,COLOR8 = 0 ,COLOR9 = 0 ,COLOR10 = 0 ,COLOR11 = 0 ,COLOR12 = 0 ;
			
			//抓對應資料
			for(var i = 0; i < $scope.result.length; i++) {	
				//第一次判斷
				if($scope.BR_check == ''){
					$scope.BR_check = $scope.result[i].BRANCH_NBR;		
					GOAL1 = 0 ,GOAL2 = 0 ,GOAL3 = 0 ,GOAL4 = 0 ,GOAL5 = 0 ,GOAL6 = 0 ,GOAL7 = 0 ,GOAL8 = 0 ,GOAL9 = 0 ,GOAL10 = 0 ,GOAL11 = 0 ,GOAL12 = 0 ;
					fTOTAL = 0 ,FEE1 = 0 ,FEE2 = 0 ,FEE3 = 0 ,FEE4 = 0 ,FEE5 = 0 ,FEE6 = 0 ,FEE7 = 0 ,FEE8 = 0 ,FEE9 = 0 ,FEE10 = 0 ,FEE11 = 0 ,FEE12 = 0 ;
					COLOR1 = 0 ,COLOR2 = 0 ,COLOR3 = 0 ,COLOR4 = 0 ,COLOR5 = 0 ,COLOR6 = 0 ,COLOR7 = 0 ,COLOR8 = 0 ,COLOR9 = 0 ,COLOR10 = 0 ,COLOR11 = 0 ,COLOR12 = 0 ;
					
				}
				//不同理專=先加總push再換成其他理專並將GOAL、FEE歸0
				if($scope.result[i].BRANCH_NBR != $scope.BR_check){	
					fTOTAL = (FEE1 + FEE2 + FEE3 + FEE4 + FEE5 + FEE6 + FEE7 + FEE8 + FEE9 + FEE10 + FEE11 + FEE12)/12;
					$scope.paramlist.push({
						BRANCH : $scope.result[i-1].BRANCH_NAME ,
						AREA : $scope.result[i-1].BRANCH_AREA_NAME ,
						GOAL1 : GOAL1 ,	FEE1 : FEE1 , COLOR1 : COLOR1 ,
						GOAL2 : GOAL2 ,	FEE2 : FEE2 , COLOR2 : COLOR2 ,
						GOAL3 : GOAL3 ,	FEE3 : FEE3 , COLOR3 : COLOR3 ,
						GOAL4 : GOAL4 ,	FEE4 : FEE4 , COLOR4 : COLOR4 ,
						GOAL5 : GOAL5 ,	FEE5 : FEE5 , COLOR5 : COLOR5 ,
						GOAL6 : GOAL6 ,	FEE6 : FEE6 , COLOR6 : COLOR6 ,
						GOAL7 : GOAL7 ,	FEE7 : FEE7 , COLOR7 : COLOR7 ,
						GOAL8 : GOAL8 ,	FEE8 : FEE8 , COLOR8 : COLOR8 ,
						GOAL9 : GOAL9 ,	FEE9 : FEE9 , COLOR9 : COLOR9 ,
						GOAL10 : GOAL10 , FEE10 : FEE10 , COLOR10 : COLOR10 ,
						GOAL11 : GOAL11 , FEE11 : FEE11 , COLOR11 : COLOR11 ,
						GOAL12 : GOAL12 , FEE12 : FEE12 , COLOR12 : COLOR12 ,
						fTOTAL : fTOTAL 
					});	
					$scope.BR_check = $scope.result[i].BRANCH_NBR;
					GOAL1 = 0 ,GOAL2 = 0 ,GOAL3 = 0 ,GOAL4 = 0 ,GOAL5 = 0 ,GOAL6 = 0 ,GOAL7 = 0 ,GOAL8 = 0 ,GOAL9 = 0 ,GOAL10 = 0 ,GOAL11 = 0 ,GOAL12 = 0 ;
					fTOTAL = 0 ,FEE1 = 0 ,FEE2 = 0 ,FEE3 = 0 ,FEE4 = 0 ,FEE5 = 0 ,FEE6 = 0 ,FEE7 = 0 ,FEE8 = 0 ,FEE9 = 0 ,FEE10 = 0 ,FEE11 = 0 ,FEE12 = 0 ;
					COLOR1 = 0 ,COLOR2 = 0 ,COLOR3 = 0 ,COLOR4 = 0 ,COLOR5 = 0 ,COLOR6 = 0 ,COLOR7 = 0 ,COLOR8 = 0 ,COLOR9 = 0 ,COLOR10 = 0 ,COLOR11 = 0 ,COLOR12 = 0 ;
					
				}	
				//近一年12個月實際、達成									
				switch ($scope.result[i].DATA_YEARMON) {			
					case $scope.DateList[0].DATE.replace("/",""):
						GOAL1 = $scope.result[i].GOAL;
						FEE1 = $scope.result[i].FEE;
						COLOR1 = (GOAL1 <= FEE1 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[1].DATE.replace("/",""):
						GOAL2 = $scope.result[i].GOAL;
						FEE2 = $scope.result[i].FEE;
						COLOR2 = (GOAL2 <= FEE2 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[2].DATE.replace("/",""):
						GOAL3 = $scope.result[i].GOAL;
						FEE3 = $scope.result[i].FEE;
						COLOR3 = (GOAL3 <= FEE3 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[3].DATE.replace("/",""):
						GOAL4 = $scope.result[i].GOAL;
						FEE4 = $scope.result[i].FEE;
						COLOR4 = (GOAL4 <= FEE4 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[4].DATE.replace("/",""):
						GOAL5 = $scope.result[i].GOAL;
						FEE5 = $scope.result[i].FEE;
						COLOR5 = (GOAL5 <= FEE5 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[5].DATE.replace("/",""):
						GOAL6 = $scope.result[i].GOAL;
						FEE6 = $scope.result[i].FEE;
						COLOR6 = (GOAL6 <= FEE6 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[6].DATE.replace("/",""):
						GOAL7 = $scope.result[i].GOAL;
						FEE7 = $scope.result[i].FEE;
						COLOR7 = (GOAL7 <= FEE7 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[7].DATE.replace("/",""):
						GOAL8 = $scope.result[i].GOAL;
						FEE8 = $scope.result[i].FEE;
						COLOR8 = (GOAL8 <= FEE8 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[8].DATE.replace("/",""):
						GOAL9 = $scope.result[i].GOAL;
						FEE9 = $scope.result[i].FEE;
						COLOR9 = (GOAL9 <= FEE9 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[9].DATE.replace("/",""):
						GOAL10 = $scope.result[i].GOAL;
						FEE10 = $scope.result[i].FEE;
						COLOR10 = (GOAL10 <= FEE10 ? "color:blue" : "color:red");
						break;
					case $scope.DateList[10].DATE.replace("/",""):
						GOAL11 = $scope.result[i].GOAL;
						FEE11 = $scope.result[i].FEE;
						COLOR11 = (GOAL11 <= FEE11 ? "color:blue" : "color:red");
					break;
					case $scope.DateList[11].DATE.replace("/",""):
						GOAL12 = $scope.result[i].GOAL;
						FEE12 = $scope.result[i].FEE;
						COLOR12 = (GOAL12 <= FEE12 ? "color:blue" : "color:red");
					break;
				}
				//最後理專=push後結束
				if($scope.result.length == (i+1)){
					fTOTAL = (FEE1 + FEE2 + FEE3 + FEE4 + FEE5 + FEE6 + FEE7 + FEE8 + FEE9 + FEE10 + FEE11 + FEE12)/12;
					$scope.paramlist.push({
						BRANCH : $scope.result[i].BRANCH_NAME ,
						AREA : $scope.result[i].BRANCH_AREA_NAME ,
						GOAL1 : GOAL1 ,	FEE1 : FEE1 , COLOR1 : COLOR1 ,
						GOAL2 : GOAL2 ,	FEE2 : FEE2 , COLOR2 : COLOR2 ,
						GOAL3 : GOAL3 ,	FEE3 : FEE3 , COLOR3 : COLOR3 ,
						GOAL4 : GOAL4 ,	FEE4 : FEE4 , COLOR4 : COLOR4 ,
						GOAL5 : GOAL5 ,	FEE5 : FEE5 , COLOR5 : COLOR5 ,
						GOAL6 : GOAL6 ,	FEE6 : FEE6 , COLOR6 : COLOR6 ,
						GOAL7 : GOAL7 ,	FEE7 : FEE7 , COLOR7 : COLOR7 ,
						GOAL8 : GOAL8 ,	FEE8 : FEE8 , COLOR8 : COLOR8 ,
						GOAL9 : GOAL9 ,	FEE9 : FEE9 , COLOR9 : COLOR9 ,
						GOAL10 : GOAL10 , FEE10 : FEE10 , COLOR10 : COLOR10 ,
						GOAL11 : GOAL11 , FEE11 : FEE11 , COLOR11 : COLOR11 ,
						GOAL12 : GOAL12 , FEE12 : FEE12 , COLOR12 : COLOR12 ,
						fTOTAL : fTOTAL 
					});	
					return;
				}			
    		}
		}
});