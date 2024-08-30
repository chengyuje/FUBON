/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS363Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS363Controller";
		
		
		$scope.mappingSet['ReportType'] = [];
		$scope.mappingSet['ReportType'].push({LABEL : 'EIP降等預估報表', DATA : '1' }, {LABEL : 'OS潛力升等報表', DATA : '2' });
		
		/***===年月===***/
	   	$scope.yMonth = function(){
			var NowDate = new Date();
			var yr = NowDate.getFullYear();
			var mm = NowDate.getMonth();	// 月份下拉：顯示當月減2個月。（#0283）
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
			
//			var nowYr = NowDate.getFullYear();
//			var identify = $scope.mappingSet['time'].filter((time) => time.LABEL.substr(0,4)==nowYr);
//			
//			if(identify.length==0){
//				$scope.mappingSet['time'] =angular.copy($scope.mappingSet['time']);		
//			}else{
//				$scope.mappingSet['time'] =angular.copy(identify);		
//			}
						
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
					emp_id:'',
					ReportType:''
        	};
			$scope.showMemo = false;
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];			
			$scope.outputVO = [];
		}
		
		// date picker end
	   //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if($scope.inputVO.sCreDate != ''){
            	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMM');
            	$scope.RegionController_getORG($scope.inputVO);
        	}else{
        		$scope.inputVO.reportDate = '';
        	}			
        };
        
        /** 資料查詢 **/
		$scope.query = function(){	
			if($scope.inputVO.reportDate == '' || $scope.inputVO.reportDate == undefined){
	    		$scope.showErrorMsg('請選擇日期');
        		return;
			}
			
			$scope.sendRecv("PMS363", "queryData", "com.systex.jbranch.app.server.fps.pms363.PMS363InputVO", $scope.inputVO,
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
			
			$scope.showMemo = true;
			if ($scope.inputVO.sCreDate != undefined || $scope.inputVO.sCreDate != '') {
				$scope.baseDate = $scope.inputVO.sCreDate;
				var year = $scope.baseDate.getFullYear();
				var mon = $scope.baseDate.getMonth() + 1;
				
				// 近6個月貢獻度資料區間：
				if(mon - 6 < 0) {
					year--;
					mon = 12 + mon - 6 + 1;
				} else {
					mon = mon - 6 + 1;
				}
				if (mon < 10)
					$scope.months_six = year + '0' + mon;
				else
					$scope.months_six = year + '' + mon;
				
				// 近9個月貢獻度資料區間：
				if(mon - 3 < 0) {
					year--;
					mon = 12 + mon - 3;
				} else {
					mon = mon - 3;
				}
				if (mon < 10)
					$scope.months_nine = year + '0' + mon;
				else
					$scope.months_nine = year + '' + mon;
				
				// 近11個月貢獻度資料區間：
				if(mon - 2 < 0) {
					year--;
					mon = 12 + mon - 2;
				} else {
					mon = mon - 2;
				}
				if (mon < 10)
					$scope.months_eleven = year + '0' + mon;
				else
					$scope.months_eleven = year + '' + mon;
			}
		};
		
		/** 資料匯出 **/
		/*
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS363", "export", "com.systex.jbranch.app.server.fps.pms363.PMS363OutputVO", $scope.outputVO,
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
		*/
		
});
