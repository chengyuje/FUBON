/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD280_SI_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD280_SI_EDITController";
		
		// date picker
		$scope.buy_DateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD280", "checkID", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.cname = tota[0].body.cname;
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
					fixed: $scope.row.FIXED_DIVIDEND_RATE,
					fix_Date: $scope.row.FIXED_RATE_DURATION,
					floating: $scope.row.FLOATING_DIVIDEND_RATE,
					exchange: $scope.row.CURRENCY_EXCHANGE,
					target: $scope.row.INVESTMENT_TARGETS,
					cnr_yield: $scope.row.CNR_YIELD,
					rate_return: $scope.row.RATE_OF_RETURN,
					performance_review: $scope.row.PERFORMANCE_REVIEW,
					stock_bond_type: $scope.row.STOCK_BOND_TYPE,
					si_project: $scope.row.PROJECT,
					si_customer_level: $scope.row.CUSTOMER_LEVEL
            };
			if($scope.row.START_DATE_OF_BUYBACK)
            	$scope.inputVO.buy_Date = $scope.toJsDate($scope.row.START_DATE_OF_BUYBACK);
			if($scope.isUpdate)
            	$scope.checkID();
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid || 
					$scope.inputVO.stock_bond_type=='' || 
					$scope.inputVO.stock_bond_type==undefined) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg($scope.errMsg, [' ']);
        		return;
			}
			if($scope.inputVO.target) {
				$scope.inputVO.target = $scope.inputVO.target.replace(/(\r\n|\n|\r)/gm,"");
				var temp = $scope.inputVO.target.split(";");
				if(temp.length > 20) {
					$scope.showErrorMsg('欄位檢核錯誤:連結標的');
	        		return;
				}
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD280", "editData", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_004');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			} else {
				$scope.sendRecv("PRD280", "addData", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", $scope.inputVO,
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
		
//		$scope.editTBPRD_SI_Tags = function() {
//			if(!$scope.inputVO.prd_id){
//				$scope.showErrorMsg("尚未輸入SI代碼");
//				return;
//			}
//			
//			$confirm({
//	            title:"提醒視窗",
//	            text:"是否更新紅字標籤?",
//	            ok: "確定更新",
//	            cancel: "取消"
//			})
//			.then(function(){
//				$scope.sendRecv("PRD280", "updateTBPRD_SI_Tags", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", $scope.inputVO,
//				function(tota, isError) {
//					if (isError) {
//						$scope.showErrorMsg("更新專案&客群代碼失敗:");
//                		$scope.showErrorMsg(tota[0].body.msgData);
//                	} else {
//                		$scope.showSuccessMsg("更新專案&客群代碼成功");
//                		$scope.closeThisDialog('successful');
//                	}
//					
//		});
//			}); 
//			
//		};		
		
});