/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD240_ETF_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD240_ETF_EDITController";
		
		// combobox
		getParameter.XML(["PRD.MKT_TIER3"], function(totas) {
			if (totas) {
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
				$scope.MKT_TIER3 = _.filter($scope.MKT_TIER3, function(o) { return !(o.DATA == '033' || o.DATA == '999'); });
			}
		});
		
		// date picker
		$scope.multi_sDateOptions = {};
		$scope.multi_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.multi_sDateOptions.maxDate = $scope.inputVO.multi_eDate;
			$scope.multi_eDateOptions.minDate = $scope.inputVO.multi_sDate;
		};
		// date picker end
		
		$scope.checkID = function() {
			var edit = $scope.isUpdate ? 'Y' : 'N';
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD240", "etf_checkID", "com.systex.jbranch.app.server.fps.prd240.PRD240InputVO", {'prd_id':$scope.inputVO.prd_id,'status':edit},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.etf_name = tota[0].body.etf_name;
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
				etf_name: $scope.row.ETF_CNAME,
				yield: $scope.row.CNR_YIELD,
				cnr_mult: $scope.row.CNR_MULTIPLE,
				inv_target: $scope.row.INV_TARGET,
				stock_bond_type: $scope.row.STOCK_BOND_TYPE,
				etf_project: $scope.row.PROJECT,
				etf_customer_level: $scope.row.CUSTOMER_LEVEL 
            };
			if($scope.row.MULTIPLE_SDATE)
            	$scope.inputVO.multi_sDate = $scope.toJsDate($scope.row.MULTIPLE_SDATE);
            if($scope.row.MULTIPLE_EDATE)
            	$scope.inputVO.multi_eDate = $scope.toJsDate($scope.row.MULTIPLE_EDATE);
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
			if($scope.isUpdate) {
				$scope.sendRecv("PRD240", "etf_editData", "com.systex.jbranch.app.server.fps.prd240.PRD240InputVO", $scope.inputVO,
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
//				$scope.sendRecv("PRD240", "addData", "com.systex.jbranch.app.server.fps.prd240.PRD240InputVO", $scope.inputVO,
//						function(tota, isError) {
//							if (isError) {
//		                		$scope.showErrorMsg(tota[0].body.msgData);
//		                	}
//		                	if (tota.length > 0) {
//		                		$scope.showSuccessMsg('ehl_01_common_004');
//		                		$scope.closeThisDialog('successful');
//		                	};
//				});
			}
		};
		
//		$scope.editTBPRD_ETF_Tags = function() {
//			if(!$scope.inputVO.prd_id){
//				$scope.showErrorMsg("尚未輸入ETF代碼");
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
//				$scope.sendRecv("PRD240", "updateTBPRD_ETF_Tags", "com.systex.jbranch.app.server.fps.prd240.PRD240InputVO", $scope.inputVO,
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