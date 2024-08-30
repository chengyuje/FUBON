/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM124_EDIT_REMINDController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM124_EDIT_REMINDController";
		
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
		
		var task_date_temp = undefined;
		var task_stime_temp = undefined;
		var task_etime_temp = undefined;
		
		$scope.inputVO.seq = $scope.row.SEQ_NO;
		
		$scope.initial = function() {
			$scope.sendRecv("CRM124", "edit_remind_inquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.edit_remind_List.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.edit_remind_List = tota[0].body.edit_remind_List;
						
						$scope.inputVO.task_date = $scope.toJsDate($scope.edit_remind_List[0].TASK_DATE);
						$scope.inputVO.task_source = $scope.edit_remind_List[0].TASK_SOURCE;
						$scope.inputVO.task_title = $scope.edit_remind_List[0].TASK_TITLE;
						$scope.inputVO.task_emp_id = $scope.edit_remind_List[0].EMP_ID;
						$scope.inputVO.task_cust_id = $scope.edit_remind_List[0].CUST_ID || '';
						$scope.inputVO.salesplan_seq = $scope.edit_remind_List[0].SALES_SEQ;
						$scope.inputVO.task_memo = $scope.edit_remind_List[0].TASK_MEMO;
						$scope.inputVO.task_stime = '';
						$scope.inputVO.task_etime = '';
						$scope.task_stime = new Date(1970, 0, 1, 8, 0);
						$scope.task_stime.setHours(Number($scope.edit_remind_List[0].TASK_STIME.substr(0,2)));
						$scope.task_stime.setMinutes(Number($scope.edit_remind_List[0].TASK_STIME.substr(2,4)));
						$scope.task_etime = new Date(1970, 0, 1, 8, 0);
						$scope.task_etime.setHours(Number($scope.edit_remind_List[0].TASK_ETIME.substr(0,2)));
						$scope.task_etime.setMinutes(Number($scope.edit_remind_List[0].TASK_ETIME.substr(2,4)));
						
						task_date_temp = $scope.inputVO.task_date;
						task_stime_temp = $scope.task_stime;
						task_etime_temp = $scope.task_etime;	
						$scope.aoinquire();
						return;
					}
				});
		}
		$scope.initial();
	
		
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
							$scope.mappingSet['AO_LIST'].push({LABEL: row.AO_CODE + '_' + row.EMP_NAME, DATA: row.EMP_ID});
						});
						$scope.inquire_salesplan();
						return;
					}
			});
		}

		
		//銷售計劃
		$scope.inquire_salesplan = function() {	
			//AO_CODE跟CUST_ID有資料才去做查詢帶銷售計劃選單
			if($scope.inputVO.task_emp_id != '' && $scope.inputVO.task_cust_id != ''){
				$scope.inputVO.task_ao_code = _.filter($scope.aoList, { EMP_ID: $scope.inputVO.task_emp_id})[0].AO_CODE;
				$scope.sendRecv("CRM124", "inquire_salesplan", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							//先清空銷售計劃選單，AO_CODE跟CUST_ID帶不出銷售計劃就不做事，有銷售計劃就帶入
							$scope.mappingSet['salesplanList'] = [];	
							if(tota[0].body.salesplanList.length == 0) {
	                			return;
	                		}else{  
	                			$scope.salesplanshow = 'show';
	                			$scope.salesplanList = tota[0].body.salesplanList;				
								angular.forEach($scope.salesplanList, function(row){
									$scope.mappingSet['salesplanList'].push({LABEL: row.TITLE , DATA: row});
								});
								
								//編輯剛進入 抓取銷售計劃	
									for(var i = 0; i < tota[0].body.salesplanList.length; i++) {	
										if(tota[0].body.salesplanList[i].SEQ == $scope.inputVO.salesplan_seq){
											$scope.salesplan_seq = tota[0].body.salesplanList[i];
											return;
										}
									}
	                		}
						}
				});
			//沒資料就清空下拉選單
			}else{
				$scope.mappingSet['salesplanList'] = [];
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
		
		
		//not done
		$scope.save = function() {

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
			
			if ($scope.inputVO.salesplan_seq != '' && $scope.inputVO.salesplan_seq != null) {
				$scope.inputVO.visit_purpose = $filter('filter')($scope.salesplanList, {SEQ : $scope.inputVO.salesplan_seq})[0].VISIT_PURPOSE;
				$scope.inputVO.visit_purpose_other = $filter('filter')($scope.salesplanList, {SEQ : $scope.inputVO.salesplan_seq})[0].VISIT_PURPOSE_OTHER;
				$scope.inputVO.key_issue = $filter('filter')($scope.salesplanList, {SEQ : $scope.inputVO.salesplan_seq})[0].MEETING_DATE_S.KEY_ISSUE;
			}
			

			$scope.sendRecv("CRM124", "edit_remind", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
				function(tota, isError) {
                	if (isError) {
                		$scope.showErrorMsg(tota[0].body.msgData);
                	}
                	if (tota.length > 0) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.closeThisDialog('successful');
                	};
				}
			);
		}
		
		
		$scope.deleteR = function() {
			$scope.sendRecv("CRM124", "delete_remind", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
				function(tota, isError) {
                	if (isError) {
                		$scope.showErrorMsg(tota[0].body.msgData);
                	}
                	if (tota.length > 0) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.closeThisDialog('successful');
                	};
				}
			);
		}
		
		
		
});
