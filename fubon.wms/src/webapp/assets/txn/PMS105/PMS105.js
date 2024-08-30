/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS105Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS105Controller";
	
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<13; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	if($scope.inputVO.sCreDate)
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
		};
		$scope.init();

	
        $scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
//		$scope.toJsDate = new Date();
//		// date picker
//		$scope.bgn_sDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.minDate
//		};
//		$scope.bgn_eDateOptions = {
//				maxDate: $scope.maxDate,
//				minDate: $scope.minDate
//			};
//		// config
//		$scope.model = {};
//		$scope.open = function($event, elementOpened) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope.model[elementOpened] = !$scope.model[elementOpened];
//		};
//		$scope.limitDate = function() {
//			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
//			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
//		};
		// date picker end
		
		
		  //***合計function***//
		$scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
		}
        
		$scope.inquire = function(){		
				if($scope.parameterTypeEditForm.$invalid){				
					$scope.showMsg('ehl_01_common_022');
	        		return;
	        	}
			$scope.sendRecv("PMS105", "inquire", "com.systex.jbranch.app.server.fps.pms105.PMS105InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
						
							
							//取得分頁數值   用來做分頁
							$scope.ya = tota[0].body.currentPageIndex;
							$scope.ya1 = tota[0].body.totalPage;
							//拿總計
							//計算最後頁總計   2017/05/18
							if($scope.ya==$scope.ya1-1){
								$scope.sendRecv("PMS105", "inquire2", "com.systex.jbranch.app.server.fps.pms105.PMS105InputVO",$scope.inputVO ,
										function(tota, isError) {
											if (!isError) {
												$scope.paramList2 = tota[0].body.resultList2;
												
											}
								});
							}
							return;
						}
			});
		};
		
	
	
		
		$scope.detail = function (row,ind) {
			row.set=ind;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS105/PMS105_DETAIL.html',
				className: 'PMS105_DETAIL',
				showClose: false,				
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
				
					$scope.inquireInit();
					$scope.inquire();
				
				}
			});
		};
		
	
	
		
});
