/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS354Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS354Controller";
		
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
	
		$scope.init = function(){
			$scope.inputVO = {										
					
        	};
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];	
		}
		$scope.inquireInit();	
		
        /*** 查詢資料 ***/
		$scope.query = function(){						
			$scope.sendRecv("PMS354", "queryData", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;								
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		$scope.query();
		
		/**「專案名稱」超連結**/
		$scope.getDetail = function(row){
//			alert("HAHA");
			console.log(row);
			$scope.connector('set', 'PMS354DETAIL', row.PRJ_SEQ);
			$scope.connector('set', 'PMS354DETAIL_title', row.PRJ_NAME);
			$scope.connector('set', 'PMS354DETAIL_START_DT', row.START_DT);
			$scope.connector('set', 'PMS354DETAIL_END_DT', row.END_DT);
    		$rootScope.menuItemInfo.url = "assets/txn/PMS354/PMS354_DETAIL.html";			
		}
});
