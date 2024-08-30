/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM990_NEXTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM990_NEXTController";

		/*--------------------------------------------------------------------------------------------
		 * 初始化
		 * --------------------------------------------------------------------------------------------
		 */
		$scope.init = function() {
			$scope.sendRecv("CRM990", "getPriID", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.priID = tota[0].body.privilege_id;
//							alert($scope.priID);
						}
			});
			
			if($scope.preInputVO){
				$scope.inputVO = angular.copy($scope.preInputVO);
				
				//預設下一層對應層級主管
				$scope.sendRecv("CRM990", "getNextEmp", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'complain_list_id': $scope.inputVO.complain_list_id},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.next_emp_id = '';
								if(tota[0].body.resultList.length > 0){
									$scope.inputVO.next_emp_id = tota[0].body.resultList[0].EMP_ID;
								}
							}
				});
				
				//取得下一流程人員資訊
				$scope.sendRecv("CRM990", "getRevList", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'complain_list_id': $scope.inputVO.complain_list_id},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['nextEmpList'] = [];
								$scope.nextEmpList = [];
								if(tota[0].body.resultList.length > 0){
									$scope.nextEmpList = tota[0].body.resultList;
									angular.forEach(tota[0].body.resultList, function(row) {
										$scope.mappingSet['nextEmpList'].push({LABEL: row.ROLE_NAME + "-" + row.EMP_NAME, DATA: row.EMP_ID});														        			
					    			});
								}
							}
				});
				
//				//取得上一級處理人員
//				$scope.sendRecv("CRM990", "checkFoword", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
//					{'complain_list_id': $scope.inputVO.complain_list_id},
//						function(tota, isError) {
//							if (!isError) {
//								if(tota[0].body.resultList.length > 0){
//									$scope.inputVO.pri_dept_name = tota[0].body.resultList[0].PRI_DEPT_NAME;
//									$scope.inputVO.pri_job_title_name = tota[0].body.resultList[0].PRI_JOB_TITLE_NAME;
//									$scope.inputVO.pri_emp_name = tota[0].body.resultList[0].PRI_EMP_NAME;
//									$scope.inputVO.pri_emp_id = tota[0].body.resultList[0].PRI_EMP_ID;
//								}
//							}
//				});
				
				//取得目前處理人員
				$scope.sendRecv("CRM990", "getFlow", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'complain_list_id': $scope.inputVO.complain_list_id},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0){
									$scope.inputVO.dept_name = tota[0].body.resultList[0].DEPT_NAME;
									$scope.inputVO.job_title_name = tota[0].body.resultList[0].JOB_TITLE_NAME;
									$scope.inputVO.emp_name = tota[0].body.resultList[0].EMP_NAME;
									$scope.inputVO.emp_id = tota[0].body.resultList[0].EMP_ID;
								}
							}
				});
			}
			
		}
		$scope.init();
		
		//更新狀態(處理進度) & 客訴流程明細檔
		$scope.saveSubmit = function() {
			//查詢是否為跨級別進件
			$scope.openConfirm = false;
			if($scope.inputVO.next_emp_id != undefined && $scope.inputVO.next_emp_id != ''){
				angular.forEach($scope.nextEmpList, function(row) {
					if($scope.inputVO.next_emp_id == row.EMP_ID){
						$scope.next_pri_id = row.PRIVILEGEID;
					}
    			});
				
				if($scope.inputVO.next_emp_id != undefined){
					switch($scope.priID) {
						case '006':
						case '009':
							if($scope.inputVO.case_type == 'OP'){
								if($scope.next_pri_id != '010' && $scope.next_pri_id != '011'){
									$scope.openConfirm = true;
								}
							} else {
								if($scope.next_pri_id != '012'){
									$scope.openConfirm = true;
								}
							}
							break;
						case '010':
						case '011':
							if($scope.next_pri_id != '012'){
								$scope.openConfirm = true;
							}
							break;
						case '012':
							if($scope.next_pri_id != '013'){
								$scope.openConfirm = true;
							}
							break;
						case '038':
						case '039':
						case '040':
							if($scope.inputVO.branch_nbr != '806'){
								if($scope.inputVO.case_type == 'OP'){	//作業客訴
									if($scope.next_pri_id != '006'){
										$scope.openConfirm = true;
									}
								} else {
									if($scope.next_pri_id != '010' || $scope.next_pri_id != '011'){
										$scope.openConfirm = true;
									}
								}								
							}
							break;
					}
				}
			} else {
				$scope.showErrorMsg('ehl_01_common_022');	//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
				return;
			}
			
			if($scope.openConfirm == true){
				$confirm({text: '此為跨級別覆核，是否確認執行？ '}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CRM990", "saveSubmit", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
							{'complain_list_id': $scope.inputVO.complain_list_id, 
							 'next_emp_id': $scope.inputVO.next_emp_id,
							 'grade': $scope.inputVO.grade,
							 'case_type': $scope.inputVO.case_type},
								function(tota, isError) {
									if (!isError) {
										$scope.showMsg("ehl_01_common_023");	//執行成功
										$scope.closeThisDialog('successful');
									}
					});
				});
			} else {
				$scope.sendRecv("CRM990", "saveSubmit", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
						{'complain_list_id': $scope.inputVO.complain_list_id, 
						 'next_emp_id': $scope.inputVO.next_emp_id,
						 'grade': $scope.inputVO.grade,
						 'case_type': $scope.inputVO.case_type},
							function(tota, isError) {
								if (!isError) {
									$scope.showMsg("ehl_01_common_023");	//執行成功
									$scope.closeThisDialog('successful');
								}
				});
			}
			
		}
});