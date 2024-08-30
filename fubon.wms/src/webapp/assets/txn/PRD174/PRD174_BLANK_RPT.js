/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD174_BLANK_RPTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD174_BLANK_RPTController";

		$scope.init = function() {
			$scope.inputVO.INSPRD_ID = "";
			$scope.inputVO.INSPRD_NAME = "";
			$scope.inputVO.INSPRD_ANNUAL = "";
		};
		$scope.init();
        
        //主約險種資料查詢
		$scope.Ins_query = function(){
			var INSPRD_ID=$scope.inputVO.INSPRD_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT910/IOT910.html',
				className: 'IOT910',
				controller:['$scope',function($scope){
					$scope.FB_COM_YN = "Y";
					if(INSPRD_ID != undefined){
						$scope.INSPRD_ID = INSPRD_ID.toUpperCase();
					}else{
						$scope.INSPRD_ID = INSPRD_ID;
					}
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.check_INSPRD_ID = data.value.INSPRD_ID;
				$scope.inputVO.INSPRD_ID = data.value.INSPRD_ID;
				$scope.inputVO.INSPRD_NAME = data.value.INSPRD_NAME;
				$scope.inputVO.CURR_CD = data.value.CURR_CD;
				$scope.inputVO.INSPRD_ANNUAL = data.value.INSPRD_ANNUAL;
			});
		}
		
		//確認險種代號是否與險種名稱一致
		$scope.checkCNCTData = function() {
			if($scope.check_INSPRD_ID != $scope.inputVO.INSPRD_ID) {
				$scope.inputVO.INSPRD_NAME = '';
				$scope.inputVO.INSPRD_ANNUAL = '';
			}
		}
		
		//產生報表
        $scope.genReport = function() {
        	if($scope.inputVO.INSPRD_NAME != null && $scope.inputVO.INSPRD_NAME != undefined && $scope.inputVO.INSPRD_NAME != "") {
        		$scope.printList = [];
    			
    			$scope.printList.push({
    				INSPRD_ID: $scope.inputVO.INSPRD_ID,
    				INSPRD_NAME: $scope.inputVO.INSPRD_NAME,
    				CURR_CD : $scope.inputVO.CURR_CD,
    				INSPRD_ANNUAL: $scope.inputVO.INSPRD_ANNUAL
    				});
    			$scope.sendRecv("PRD174", "printBlankRpt", "com.systex.jbranch.app.server.fps.prd174.PRD174InputVO", {'list':$scope.printList}, 
    				function(tota, isError) {
    					if (!isError) {				
    						return;
    					}
    	   			});
        	}
        }
});