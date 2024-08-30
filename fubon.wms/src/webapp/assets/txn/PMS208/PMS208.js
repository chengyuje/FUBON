/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS208Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS208Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
			// filter
	
			//***初始查詢***//
			$scope.init = function(){
				$scope.inputVO = {    
	        			sTime:undefined,    //日期
	        			ao_code  :'',        //理專
	        			branch_nbr  :'',        //分行
						region_center_id  :'',        //業務處
						branch_area_id      :'',        //營運區
						emp_id:'',
						type :'MTD',
	        		    				
	        	};
				
				$scope.paramList = [];
				$scope.paramList2 = [];
			};
	        $scope.init();
	        //有管餘時間
	        $scope.model = {};
	    	$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
			
			
			//選取月份下拉選單 --> 重新設定可視範圍
	        $scope.dateChange = function(){
	        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sTime,'yyyyMMdd');
	        	$scope.RegionController_getORG($scope.inputVO);
	        };
	        
		  	$scope.mappingSet['seture'] = [];

			$scope.mappingSet['seture'].push({
				DATA : 'MTD',
				LABEL : 'MTD'
			}, {
				DATA : 'YTD',
				LABEL : 'YTD'
			});
        

	    /****==主查詢==****/
        $scope.inquire = function(){
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料日期/查詢類型)');
        		return;
        	}		
			$scope.sendRecv("PMS208", "queryData", "com.systex.jbranch.app.server.fps.pms208.PMS208InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList2 = [];
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList2 = tota[0].body.resultList;							
//							$scope.paramList = tota[0].body.csvList;
							$scope.outputVO = tota[0].body;							   
							return;
						}
			});
		};
		
		
		/****==匯出==****/
		$scope.export = function() {
			
			$scope.sendRecv("PMS208", "export",
					"com.systex.jbranch.app.server.fps.pms208.PMS208OutputVO",
					{'resultList' : $scope.paramList2,
					 'type' : $scope.inputVO.type},function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		}
    
    
		
});
