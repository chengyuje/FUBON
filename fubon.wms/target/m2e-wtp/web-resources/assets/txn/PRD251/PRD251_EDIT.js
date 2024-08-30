/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD251_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD251_EDITController";
		
		$scope.checkID = function() {
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD251", "checkID", "com.systex.jbranch.app.server.fps.prd251.PRD251InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.bond_name = tota[0].body.bond_name;
								$scope.canEdit = tota[0].body.canEdit;
								$scope.errMsg = tota[0].body.errorMsg;
								return;
							}
				});
			}
		};
		
		$scope.init = function() {
			if($scope.row){
        		$scope.isUpdate = true
        	}
			$scope.row = $scope.row || {};
			$scope.inputVO = {
				prd_id: $scope.row.PRD_ID,
				bond_name: $scope.row.BOND_CNAME_A,
				cust_id: $scope.row.CUST_ID,
				limit_price: $scope.row.LIMITED_PRICE,
				channel_fee: $scope.row.CHANNEL_FEE
            };
			if($scope.isUpdate)
            	$scope.checkID();
		};
		$scope.init();
		
		$scope.doFloat = function() {
			$scope.inputVO.limit_price = parseFloat($scope.inputVO.limit_price);
			$scope.inputVO.channel_fee = parseFloat($scope.inputVO.channel_fee);
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg($scope.errMsg, [' ']);
        		return;
			}
			if($scope.isUpdate) {
//				$scope.sendRecv("PRD251", "editData", "com.systex.jbranch.app.server.fps.prd251.PRD251InputVO", $scope.inputVO,
//						function(tota, isError) {
//							if (isError) {
//		                		$scope.showErrorMsg(tota[0].body.msgData);
//		                	}
//		                	if (tota.length > 0) {
//		                		$scope.showSuccessMsg('ehl_01_common_004');
//		                		$scope.closeThisDialog('successful');
//		                	};
//				});
			} else {
				$scope.sendRecv("PRD251", "addData", "com.systex.jbranch.app.server.fps.prd251.PRD251InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_004');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			}
		};
		
		
		
});