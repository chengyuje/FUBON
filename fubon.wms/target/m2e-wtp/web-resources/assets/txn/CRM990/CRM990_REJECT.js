/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM990_REJECTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM990_REJECTController";
		
		//字數長度計算
		$scope.checkLenght = function(type, cloum, limit) {

			var text = $scope.inputVO[type];
			if (!text) {
				$scope[cloum] = limit;
			} else {
				var len = text.length;
				$scope[cloum] = parseInt(limit)-parseInt(len);
			}
		}
		
		//初始化
		$scope.init = function() {
			$scope.inputVO.reject_type = ''
			$scope.inputVO.reason = '';
			$scope.checkLenght('backReason', "lenght1", "60");
			if($scope.preInputVO){
				$scope.inputVO = angular.copy($scope.preInputVO);
				
//				//取得上一級處理人員
//				$scope.sendRecv("CRM990", "checkFoword", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
//					{'complain_list_id': $scope.inputVO.complain_list_id
//					 'grade': $scope.inputVO.grade},
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
				
				//取第一級處理人員
				if($scope.step == 'first'){
					$scope.sendRecv("CRM990", "getFirstFlow", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
							{'complain_list_id': $scope.inputVO.complain_list_id},
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.resultList.length > 0){
											$scope.inputVO.first_dept_name = tota[0].body.resultList[0].FIRST_DEPT_NAME;
											$scope.inputVO.first_job_title_name = tota[0].body.resultList[0].FIRST_JOB_TITLE_NAME;
											$scope.inputVO.first_emp_id = tota[0].body.resultList[0].FIRST_EMP_ID;
											$scope.inputVO.first_emp_name = tota[0].body.resultList[0].FIRST_EMP_NAME;
										}
									}
						});
				}
			}
		}
		$scope.init();
		
		//更新狀態(處理進度) & 客訴流程明細檔
		$scope.saveSubmit = function() {
			if($scope.step == 'one'){
				//退回上一級
				$scope.inputVO.next_emp_id = $scope.inputVO.pri_emp_id;
				$scope.inputVO.reject_type = 'one';
				
			} else if($scope.step == 'first'){
				//退回第一級
				$scope.inputVO.next_emp_id = $scope.inputVO.first_emp_id;
				$scope.inputVO.reject_type = 'first';
			}
			
			$scope.sendRecv("CRM990", "saveSubmit", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'complain_list_id': $scope.inputVO.complain_list_id, 
					 'next_emp_id': $scope.inputVO.next_emp_id, 
					 'reject_type': $scope.inputVO.reject_type,
					 'reason': $scope.inputVO.reason},
						function(tota, isError) {
							if (!isError) {
//								$scope.showMsg("ehl_01_common_023");	//執行成功
								$scope.closeThisDialog('successful');
							}
				});
		}

});