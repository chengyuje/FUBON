/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS360Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS360Controller";
		
		/***===年月===***/
	   	$scope.yMonth = function(){
			var NowDate = new Date();
			var yr = NowDate.getFullYear();
			var mm = NowDate.getMonth() + 2;
			var strmm = '';
			var xm = '';
			$scope.mappingSet['time'] = [];
			for (var i = 0; i < 12; i++) {
				mm = mm - 1;
				if (mm == 0) {
					mm = 12;
					yr = yr - 1;
				}
				if (mm < 10)
					strmm = '0' + mm;
				else
					strmm = mm;
				var targetDay = '';
				if(mm == NowDate.getMonth() + 1){
					var yesterday = NowDate.getDate()-1;
					if(yesterday < 10){
						targetDay = '0' + yesterday;
					}else{
						targetDay = yesterday;
					}
				}else{
					targetDay = new Date(yr,mm,0).getDate();
				}
				$scope.mappingSet['time'].push({
					LABEL : yr + '/' + strmm,
					DATA : new Date(yr + '/' + strmm + '/' + targetDay)
				});
			} 
			
			var nowYr = NowDate.getFullYear();
			var identify = $scope.mappingSet['time'].filter((time) => time.LABEL.substr(0,4)==nowYr);
			
			if(identify.length==0){
				$scope.mappingSet['time'] =angular.copy($scope.mappingSet['time']);		
			}else{
				$scope.mappingSet['time'] =angular.copy(identify);		
			}
						
		};
		$scope.yMonth();
		
		$scope.init = function(){
			$scope.inputVO = {										
					reportDate: '',
					region_center_id: '',
					branch_area_id: '' ,
					branch_nbr: '',
					ao_code: '',
					sCreDate: '',
					emp_id:''
        	};	
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];				
		}
		
		// date picker end
	   //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if($scope.inputVO.sCreDate != ''){
            	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
            	$scope.RegionController_getORG($scope.inputVO);
        	}else{
        		$scope.inputVO.reportDate = '';
        	}
        };
        
        /** 資料查詢 **/
		$scope.query = function(){	
			if($scope.inputVO.reportDate == '' || $scope.inputVO.reportDate == undefined){
	    		$scope.showErrorMsg('請選擇月份');
        		return;
			}
			
			$scope.sendRecv("PMS360", "queryData", "com.systex.jbranch.app.server.fps.pms360.PMS360InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;	
							return;
						}						
			});
		};
		
		/** 資料匯出 **/
		$scope.exportRPT = function(){
			$scope.outputVO.type = $scope.flag;
			$scope.sendRecv("PMS360", "export", "com.systex.jbranch.app.server.fps.pms360.PMS360OutputVO", $scope.outputVO,
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
