/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM124_ADD_REMINDController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM124_ADD_REMINDController";
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		$scope.mappingSet['TASK_SOURCE'] = [];
		angular.forEach(projInfoService.mappingSet['CAM.TASK_SOURCE'], function(row){
			if ((row.DATA).substr(0, 1) == 'A') {
				$scope.mappingSet['TASK_SOURCE'].push({LABEL: row.LABEL, DATA: row.DATA});
			}
		});
		
		$scope.init = function() {
			$scope.inputVO = {
					task_date: undefined,
					task_source: '',
					task_source_oth: '',
					task_stime: '',
					task_etime: '',
					task_title: '',
					task_emp_id: '',
					task_cust_id: '',
					salesplan_seq: '',
					task_memo: ''
			}
			
			$scope.id_check = true;
			
			$scope.task_stime = new Date(1970, 0, 1, 8, 0);
			$scope.task_etime = new Date(1970, 0, 1, 17, 0);
			
		}
		$scope.init();
		
		$scope.aoinquire = function() {
			$scope.sendRecv("CRM124", "aoinquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.aoList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.aoList = tota[0].body.aoList;
						$scope.mappingSet['AO_LIST'] = [];
						angular.forEach($scope.aoList, function(row){
							$scope.mappingSet['AO_LIST'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
						});
						return;
					}
			});
		}
		$scope.aoinquire();
		
		//非客戶陪訪選項=>清空客戶 銷售計劃
		$scope.initCustID = function(){
			if($scope.inputVO.task_source != 'A2'){
				$scope.id_check = true;
				$scope.inputVO.task_cust_id = '';
				$scope.inquire_salesplan();
			}else{
				$scope.id_check = false;
			}
		}
		
		//銷售計劃選單
		$scope.inquire_salesplan = function() {
			if($scope.inputVO.task_source == 'A2'){
				//AO_CODE跟CUST_ID有資料才去做查詢帶銷售計劃選單
				if($scope.inputVO.task_ao_code != '' && $scope.inputVO.task_cust_id != ''){
					
//					$scope.inputVO.task_ao_code = _.filter($scope.aoList, { EMP_ID: $scope.inputVO.task_emp_id})[0].AO_CODE;
					$scope.sendRecv("CRM124", "inquire_salesplan", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								//先清空銷售計劃選單，AO_CODE跟CUST_ID帶不出銷售計劃就不做事，有銷售計劃就帶入
								$scope.mappingSet['salesplanList'] = [];
								if(tota[0].body.salesplanList.length == 0) {
									$scope.showMsg("無可陪訪的銷售計劃資料");
									return;
								}else{
									$scope.salesplanshow = 'show';
									$scope.salesplanList = tota[0].body.salesplanList;
									angular.forEach($scope.salesplanList, function(row){
										$scope.mappingSet['salesplanList'].push({LABEL: row.TITLE , DATA: row});
									});
								}
							}
					});
				}
				//沒資料就清空下拉選單
				else{
					$scope.mappingSet['salesplanList'] = [];
					//客戶陪訪選項and客戶ID為空=>上鎖
					if($scope.inputVO.task_cust_id == ''){
						$scope.inputVO.lock = 'yes' ;
					}else{
						$scope.inputVO.lock = 'no' ;
					}
				}
			}
		}
		
		//銷售計劃選擇後帶入時間
		$scope.change_salesplan = function(row) {
			if (row == null || row == undefined) {
				$scope.sales = false;
			} else {
				$scope.sales = true;
				$scope.task_stime = $scope.toJsDate(row.MEETING_DATE_S);
				$scope.task_etime = $scope.toJsDate(row.MEETING_DATE_E);
				$scope.inputVO.task_date = $scope.toJsDate(row.MEETING_DATE_S);
				$scope.inputVO.task_date.setHours(0,0,0,0);
			}
		}
		//客戶ID檢核	
		$scope.custCheck = function(row) {
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", {'cust_id':row},
					function(tota110, isError) {	
				if(tota110[0].body.resultList.length == 0) {
					$scope.showErrorMsg("客戶ID錯誤，請重新填寫");
					$scope.id_check = false;
					return;
				}else{
					$scope.id_check = true;
				}
			});
		}
		
		//儲存
		$scope.save = function() {
			//檢核條件:
			
			if ($scope.inputVO.task_date == undefined || $scope.inputVO.task_source == '' ||
				$scope.task_stime == null || $scope.task_etime == null ||
				$scope.task_stime == undefined || $scope.task_etime == undefined ||
				$scope.inputVO.task_title == '' || $scope.inputVO.task_ao_code == '' ||
				$scope.salesplan_seq == ''){
				
				$scope.showErrorMsg('欄位檢核錯誤:必填欄位不得為空');
	            return;
			}
			//============================時間===========================
			if ($scope.task_stime.getHours() < 10) {
				var s_hour = '0' + $scope.task_stime.getHours();
			} else {
				var s_hour = '' + $scope.task_stime.getHours();
			}
			if ($scope.task_stime.getMinutes() < 10) {
				var s_minutes = '0' + $scope.task_stime.getMinutes();
			} else {
				var s_minutes = '' + $scope.task_stime.getMinutes();
			}
			if ($scope.task_etime.getHours() < 10) {
				var e_hour = '0' + $scope.task_etime.getHours();
			} else {
				var e_hour = '' + $scope.task_etime.getHours();
			}
			if ($scope.task_etime.getMinutes() < 10) {
				var e_minutes = '0' + $scope.task_etime.getMinutes();
			} else {
				var e_minutes = '' + $scope.task_etime.getMinutes();
			}
			$scope.inputVO.task_stime = s_hour + s_minutes;
			$scope.inputVO.task_etime = e_hour + e_minutes;
			//========================================================
			
			if ($scope.salesplan_seq != undefined && $scope.salesplan_seq != null) {
				$scope.inputVO.salesplan_seq = $scope.salesplan_seq.SEQ;
				$scope.inputVO.visit_purpose = $scope.salesplan_seq.VISIT_PURPOSE;
				$scope.inputVO.visit_purpose_other = $scope.salesplan_seq.VISIT_PURPOSE_OTHER;
				$scope.inputVO.key_issue = $scope.salesplan_seq.KEY_ISSUE;
			}
			$scope.sendRecv("CRM124", "add_remind", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
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
});
