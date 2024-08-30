/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM211_GIFTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM211_GIFTController";
		
		//初始化
		$scope.init = function() {
			
			$scope.giftList = [];
			$scope.outputVO = [];
			//查詢所有禮贈品
			$scope.sendRecv("MGM211", "getGiftList", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.giftList = tota[0].body.giftList;
							$scope.outputVO = tota[0].body;
							
							if($scope.editGiftList.length > 0){	//修改
								angular.forEach($scope.giftList, function(row){
									angular.forEach($scope.editGiftList, function(editRow){
										if(row.GIFT_SEQ == editRow.GIFT_SEQ){
											row.ADD = true;
											row.EXCH_UNI = editRow.EXCH_UNI;
										}
									});
								});
							}
							
						}
			});
			
			
		}
		$scope.init();
		
		//儲存適用贈品
		$scope.saveGift = function() {
			$scope.connector('set', 'MGM211_giftList', null);
			$scope.errFlag = false;
			$scope.inputVO.giftList = [];
			
			angular.forEach($scope.giftList, function(row){
	    		if (row.ADD == true) {
	    			//未輸入兌換點數/回饋金
	    			if(row.EXCH_UNI == undefined || row.EXCH_UNI == ''){
	    				$scope.inputVO.giftList = [];
	    				if($scope.act_type == 'M'){
	    					$scope.showErrorMsg("請輸入每單位贈品兌換點數。");
	    					$scope.errFlag = true;
	    					return;
	    				} else if ($scope.act_type == 'V'){
	    					$scope.showErrorMsg("請輸入每單位回饋金。");
	    					$scope.errFlag = true;
	    					return;
	    				}
	    			}
	    			$scope.inputVO.giftList.push(row);
	    		}
	    	});
			
			if($scope.inputVO.giftList.length > 0){
				$scope.connector('set', 'MGM211_giftList', $scope.inputVO.giftList);
				$scope.closeThisDialog('successful');
			} else {
				if(!$scope.errFlag){
					$scope.showErrorMsg("請選取適用贈品。");
					return;					
				}
			}
			
			
		}
		
});
