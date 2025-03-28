/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM211Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, crmService) {
		$controller('BaseController', {$scope: $scope});

		$controller('CRM210Controller', {$scope: $scope});
		$scope.controllerName = "CRM211Controller";
		
		crmService.getForbiddenList();
		
		$scope.priID = String(sysInfoService.getPriID());
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.priID == 'UHRM002') {
							$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
						} else {
							$scope.inputVO.uEmpID = '';
						}
					}
		});
		
		$scope.init = function() {
			$scope.inputVO.ao_code = '';
			$scope.inputVO.cust_id = '';
			$scope.inputVO.cust_name = '';
			
			$scope.ao_code = sysInfoService.getAoCode();
			$scope.role = sysInfoService.getRoleID();

			// AO CODE
			// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管 change svn:log
//			if ($scope.priID == '002') {
//				$scope.sendRecv("CRM211", "getAOCodeList", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO, function(tota, isError) {
//					if (!isError) {
//						if(tota[0].body.msgData != undefined){
//							$scope.showErrorMsg(tota[0].body.msgData);
//						}
//					}
//					
//					if (tota.length > 0) {
//						$scope.aolist = tota[0].body.resultList;
//						
//						$scope.mappingSet['ao_list'] = tota[0].body.resultList;
//						
//						$scope.inputVO.aolist = [];
//						
//						angular.forEach($scope.aolist, function(row, index, objs){
//							switch (row.AO_CODE) {
//								case "Diamond Team":
//									break;
//								default :
//									$scope.inputVO.aolist.push({"AO_CODE":row.AO_CODE});
//									break;
//							}							
//						});
//					}
//				});
//			} else {
				 $scope.sendRecv("CRM211", "getAOCode", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO, function(tota, isError) {
					 if (!isError) {
						 if(tota[0].body.msgData != undefined){
							 $scope.showErrorMsg(tota[0].body.msgData);
						 }
					 }
					 if (tota.length > 0) {
						 $scope.aolist = _.sortBy(tota[0].body.resultList, ['AO_CODE']);
						 $scope.mappingSet['ao_list'] = [];
						 $scope.inputVO.aolist = [];
						
						 if ($scope.ao_code != '' && $scope.ao_code != undefined) {		//有AO_CODE
							 if($scope.ao_code.length > 1){		//有兩個以上AO_CODE的理專
								 angular.forEach($scope.aolist, function(row, index, objs){
									 angular.forEach($scope.ao_code, function(row2, index2, objs2){
										 if(row.AO_CODE == row2){
											 $scope.mappingSet['ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME + '(' + row.CODE_TYPE_NAME + ')', DATA: row.AO_CODE});
											 $scope.inputVO.aolist.push({"AO_CODE":row.AO_CODE});
										 }
									 });
								 });
							 }else if($scope.ao_code.length == 1){		//只有一個AO_CODE的理專
								 angular.forEach($scope.aolist, function(row, index, objs){
									 $scope.mappingSet['ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME + '(' + row.CODE_TYPE_NAME + ')', DATA: row.AO_CODE});
									 $scope.inputVO.aolist.push({"AO_CODE":row.AO_CODE});
								 });
								 $scope.inputVO.ao_code = String(sysInfoService.getAoCode());
							 }
						 }else{		//無AO_CODE
							 angular.forEach($scope.aolist, function(row, index, objs){
								 $scope.mappingSet['ao_list'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME + '(' + row.CODE_TYPE_NAME + ')', DATA: row.AO_CODE});
								 $scope.inputVO.aolist.push({"AO_CODE":row.AO_CODE});
							 });
						 }   		
					 };
				 });
//			}
		}
		$scope.init();				
		
		$scope.inquire = function() {
			if($scope.inputVO.cust_id != ''){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();	
				
				if(crmService.checkCustId($rootScope.forbiddenData,$scope.inputVO.cust_id)) {
					$scope.showErrorMsg("ehl_01_CRM_002");
	    			return;
				}
			}
			//手收貢獻度檢查
			if($scope.inputVO.manage_05_Date != undefined){
				if($scope.inputVO.manage_06_fee_s == undefined || $scope.inputVO.manage_06_fee_e == undefined){
					$scope.showMsg("請輸入完整手續費收入");
					return;
				}
				if($scope.inputVO.manage_06_fee_s >= $scope.inputVO.manage_06_fee_e){
					$scope.showMsg("手續費收入由小到大");
					return;
				}
			}
			$scope.inputVO.role = $scope.role;
			$scope.sendRecv("CRM211", "inquire", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						debugger;
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.obj.resultList = tota[0].body.resultList;
						$scope.obj.outputVO = tota[0].body;
						
						$scope.obj.resultList = crmService.filterList($rootScope.forbiddenData,$scope.obj.resultList);
					}
			});
	    };
	 	    
		$scope.initQuery = function(){
			$scope.sendRecv("CRM211", "initQuery", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					if(tota[0].body.showMsg){
						$scope.showErrorMsg(tota[0].body.errorMsg);
					}
					
				}					
			});
		};
		$scope.initQuery();
});
