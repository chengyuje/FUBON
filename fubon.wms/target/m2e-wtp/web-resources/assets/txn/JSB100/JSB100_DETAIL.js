/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('JSB100_DETAILController',
	function($rootScope, $scope, $controller, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "JSB100_DETAILController";
		
		getParameter.XML(["JSB.INS_EDIT_STATUS", "CRM.CRM239_CONTRACT_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['JSB.INS_EDIT_STATUS'] = totas.data[totas.key.indexOf('JSB.INS_EDIT_STATUS')];
				$scope.mappingSet['CRM.CONTRACT_ST'] = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
				
			}
		});
		
//		$scope.mappingSet['JSB.CONTRACT_STATUS'] = [];
//		$scope.mappingSet['JSB.CONTRACT_STATUS'].push({LABEL:"有效" , DATA: '01'}, 
//											 	  	  {LABEL:"失效" , DATA: '22'});
		
		// date picker
		// 參考日期不能 > 系統日期
		$scope.data_dateOptions = {
				maxDate: new Date()
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.init = function(){
			$scope.alreadySave = false;
			
			$scope.inputVO = {};
			$scope.inputVO.seq_no = $scope.row.SEQ_NO;
			$scope.inputVO.seq = $scope.row.SEQ;
			$scope.inputVO.acceptid = $scope.row.ACCEPTID;
			$scope.inputVO.status = $scope.row.STATUS;
			$scope.inputVO.policy_nbr = $scope.row.POLICY_NBR;
			$scope.inputVO.policy_simp_name = $scope.row.POLICY_SIMP_NAME;
			$scope.inputVO.policy_full_name = $scope.row.POLICY_FULL_NAME;
			$scope.inputVO.appl_name = $scope.row.APPL_NAME;
			$scope.inputVO.ins_name = $scope.row.INS_NAME;
			$scope.inputVO.cust_id = $scope.row.CUST_ID;
			$scope.inputVO.ins_id = $scope.row.INS_ID;
//			$scope.inputVO.proposer_birth = $scope.row.PROPOSER_BIRTH;
			$scope.inputVO.proposer_birth = $scope.toJsDate($scope.row.PROPOSER_BIRTH);
//			$scope.inputVO.insured_birth = $scope.row.INSURED_BIRTH;
			$scope.inputVO.insured_birth = $scope.toJsDate($scope.row.INSURED_BIRTH);
			$scope.inputVO.project_code = $scope.row.PROJECT_CODE;
			$scope.inputVO.pay_type = $scope.row.PAY_TYPE;
			$scope.inputVO.policy_assure_amt = $scope.row.POLICY_ASSURE_AMT;
			$scope.inputVO.unit_nbr = $scope.row.UNIT_NBR;
			$scope.inputVO.contract_status = $scope.row.CONTRACT_STATUS;
			$scope.inputVO.service_emp_id = $scope.row.SERVICE_EMP_ID;
			$scope.inputVO.data_date = $scope.toJsDate($scope.row.DATA_DATE);
			$scope.inputVO.update_reason = $scope.row.UPDATE_REASON;
			$scope.inputVO.user_update_date = $scope.toJsDate($scope.row.USER_UPDATE_DATE);
		}
		
		$scope.save = function() {
			$scope.alreadySave = false;
			$scope.sendRecv("JSB100", "save", "com.systex.jbranch.app.server.fps.jsb100.JSB100InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('ehl_01_common_025');	// 儲存成功
					$scope.alreadySave = true;
					$scope.row.STATUS = tota[0].body.STATUS;
					$scope.inputVO.seq_no = tota[0].body.SEQ_NO;
				}
			});
		}
		
		$scope.updateStatus = function(status) {
			$scope.inputVO.status = status;
			$scope.sendRecv("JSB100", "updateStatus", "com.systex.jbranch.app.server.fps.jsb100.JSB100InputVO", $scope.inputVO, 
			function(tota, isError) {
				if (!isError) {
					switch (status) {
						case 'C': {
							$scope.showSuccessMsg('成功取消修改');
							break;
						}
						case 'P': {
							$scope.showSuccessMsg('ehl_01_common_019');		// 已送出覆核
							break;
						}
						case 'R': {
							$scope.showSuccessMsg('ehl_01_common_020');		// 退回成功
							break;
						}
//						default: {}
					}
					$scope.closeThisDialog('successful');
				}
			});
		}
		
		$scope.approve = function() {
			$scope.sendRecv("JSB100", "approve", "com.systex.jbranch.app.server.fps.jsb100.JSB100InputVO", $scope.inputVO, 
			function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('覆核成功');
					$scope.closeThisDialog('successful');
				}
			});
		}
		
		$scope.init();
});