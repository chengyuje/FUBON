/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM431_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM431_DETAILController";
		
		// filter
		getParameter.XML(["CRM.SINGLE_TYPE", "CRM.VIP_DEGREE", "CRM.NF_TRUST_CURR_TYPE", "CRM.ETF_TRUST_CURR_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.SINGLE_TYPE'] = totas.data[totas.key.indexOf('CRM.SINGLE_TYPE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.NF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.NF_TRUST_CURR_TYPE')];
				$scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.ETF_TRUST_CURR_TYPE')];
			}
		});
	    //
    	
    	$scope.mappingSet['CRM.APPLY.CAT'] = [];
    	$scope.mappingSet['CRM.APPLY.CAT'].push({LABEL: '期間議價', DATA: '1'},
    											{LABEL: '單次議價', DATA: '2'});

		$scope.init = function(){
			$scope.actionFlag = false;
			$scope.detailList = [];
			$scope.data1 = [];
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {
				apply_seq:		'',
				cust_id:		'',
				apply_cat:		'',
				comments:		'',
				highest_auth_lv:'',
				acceptList:		[],
				auth_status:	'',
				apply_status:	''
			}
			
			if($scope.isUpdate){
				$scope.inputVO.apply_cat = $scope.row.APPLY_CAT;
				$scope.inputVO.apply_seq = $scope.row.APPLY_SEQ;
				$scope.inputVO.cust_id = $scope.row.CUST_ID;
				$scope.inputVO.apply_status = $scope.row.APPLY_STATUS;
				$scope.inputVO.apply_type = $scope.row.APPLY_TYPE;
			}
			
//			申請狀態 
//			V：送檢核中
//			0：暫存
//			1：待覆核
//			2：已授權
//			3：已終止
//			4：終止待覆核
//			5：終止後申請失敗"
			if($scope.row.APPLY_STATUS == '4'){		
				//終止原因
				$scope.sendRecv("CRM431", "getTerminateReason", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", {"apply_seq":$scope.row.APPLY_SEQ},
						function(totas, isError) {
							if (totas[0].body.resultList.length > 0) {
								$scope.BRG_REASON = totas[0].body.resultList[0].TERMINATE_REASON;
							}
						}
				);
			}
		};
		$scope.init();

		$scope.inquire = function() {
			$scope.sendRecv("CRM431", "queryDTL", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
 					function(totas, isError) {
 						if (isError) {
 							
 						}
						if (totas[0].body.detailList.length > 0) {
							$scope.detailList.push(totas[0].body.detailList[0]);
	                		$scope.outputVO1 = totas[0].body;
                		
                			$scope.apply_type = $scope.detailList[0].APPLY_TYPE;
                			$scope.cust_id = $scope.detailList[0].CUST_ID;
                			$scope.cust_name = $scope.detailList[0].CUST_NAME;
			       			$scope.vip_degree = $scope.detailList[0].VIP_DEGREE;
                			$scope.con_degree = $scope.detailList[0].CON_DEGREE;
                			$scope.apply_date = $scope.detailList[0].APPLY_DATE;
                			$scope.aum_amt = $scope.detailList[0].AUM_AMT;
                			$scope.highest_auth_lv = $scope.detailList[0].HIGHEST_AUTH_LV;
                			$scope.highest_auth_lv_name = $scope.detailList[0].HIGHEST_AUTH_LV_NAME;
                			$scope.y_profee = $scope.detailList[0].Y_PROFEE;
                			$scope.apply_status = $scope.detailList[0].APPLY_STATUS;
                			$scope.inputVO.comments = $scope.detailList[0].COMMENTS;
                			if($scope.apply_status=='1'||$scope.apply_status=='4'){
                				$scope.apply_btn = true;
                			}else{
                				$scope.apply_btn = false;
                			}
                			console.log("apply_status="+$scope.apply_status);
						}
			});
		};
		$scope.inquire();
		
		$scope.action = function(type) {
			$scope.actionFlag = true;	//防止同時發送兩筆電文至400主機，於點選完覆核後鎖定覆核按鈕(#4144)
			
//			if($scope.inputVO.apply_cat == '1'){	//期間議價才需要判斷是否已超過期間議價迄日
//				
//				$scope.inputVO.auth_date_end = $scope.detailList[0].BRG_END_DATE;
//				
//				var endDate = new Date($scope.detailList[0].BRG_END_DATE);
//				var now = new Date();
//				var todayAtMidn = new Date(now.getFullYear(), now.getMonth(), now.getDate());
//				
//				if(endDate < todayAtMidn){
//					$scope.showMsg("ehl_02_CRM431_001");
//					return;
//				}
//			}
			
			$scope.inputVO.auth_date_end = new Date($scope.detailList[0].BRG_END_DATE);
			$scope.inputVO.actionType = type;
			
			$scope.inputVO.con_degree = $scope.con_degree;
			$scope.inputVO.prod_type = "";
			if (($scope.inputVO.apply_cat == '1' && $scope.inputVO.apply_type == '1') ||
					($scope.inputVO.apply_cat == '2' && 
							($scope.inputVO.apply_type == '1' || $scope.inputVO.apply_type == '2'))	) { // 單筆 & 基金 or 期間 & 基金議價
				$scope.inputVO.prod_type = 1;
			} else {
				$scope.inputVO.prod_type = 2;
			}
			
			$confirm({text: (type == 'accept' ? '是否同意' : ''),text13: (type == 'accept' ? '' : '是否退回')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CRM431", "review", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
						function(tota, isError) {
					if(!isError){
						if (isError) {
							$scope.actionFlag = false;
							$scope.showErrorMsg(tota[0].body.msgData);
							$scope.showMsg("ehl_01_common_007");
						}
						if(tota.length > 0) {
							$scope.closeThisDialog('successful');
							return;
						}
					}
				});
			});		
		};
});