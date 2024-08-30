/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
/**輔銷行事曆會議記錄 陪訪紀錄已移除 目前為駐點分行設定**/
eSoafApp.controller('CRM1241Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1241Controller";
		
		//get first date of week
		function dateInWeek(month, year) {
			return new Date(year + "-" + month + "-01").getDay()
		}
		
		//get days of month
		function daysInMonth(month, year) {
			return new Date(year, month, 0).getDate();
		}
		$scope.viewDate = new Date();
		$scope.emp_id = projInfoService.getUserID();
		$scope.inputVO.emp_id = $scope.emp_id;
		$scope.inputVO.date = $scope.viewDate;
		
		if (projInfoService.getRoleID() == "FA9" || projInfoService.getRoleID() == "IA9"){
			$scope.type = 1;
			//輔銷人員選單
			$scope.sendRecv("CRM124", "emp_inquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {},
				function(tota, isError) {
					if (!isError) {
						var emp_id_list = [];
						emp_id_list = tota[0].body.resultList;			
						$scope.mappingSet['emp_id'] = [];
						angular.forEach(emp_id_list, function(row, index, objs){				
							$scope.mappingSet['emp_id'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
						});	
						return;
					}
				});
		}
		//輔銷科長查詢
		$scope.faia_query = function () {
			/*		最初進入	type = undefined	emp = userid
					請選擇	type = 1			emp = userid
					FA/IA	type = 2			emp = inputemp	*/
			if ($scope.inputVO.emp_id == "" || $scope.inputVO.emp_id == undefined) {
				$scope.inputVO.faia_type = "1";
				$scope.inputVO.emp_id = "";
			} else {
				$scope.inputVO.faia_type = "2";
				$scope.emp_id = $scope.inputVO.emp_id;
			}	
			$scope.inputVO.date = $scope.viewDate
			
			
			$scope.holiday();
			$scope.change($scope.viewDate.getMonth(), $scope.viewDate.getFullYear(), $scope.viewDate);		
		}
		
		
		//換頁執行
		$scope.change = function(month, year, newViewDate) {
			//get year
			$scope.inputVO.year = String(year);
	
			//get month
			if (String(month + 1).length == 1) {
				$scope.inputVO.month = '0' + String(month + 1);
			}else {
				$scope.inputVO.month = String(month + 1);
			}
			
			//dateList
			$scope.dateList = [];
			
			var nulldate = dateInWeek($scope.inputVO.month, $scope.inputVO.year);
			
			for (var i = 0 ; i < 42; i++) {
				var dateC = '';
				//put null date
				if (i < nulldate){
					dateC = '';
				//put date
				} else if (i < (daysInMonth($scope.inputVO.month, $scope.inputVO.year) + nulldate)) {
					dateC =  i - nulldate + 1; 
				//put null date
				} else {
					dateC = '';
				}
				$scope.dateList.push({
					date : dateC, 
					holiday : false,
					period_a : false,
					period_b : false,
					seq_a : '',
					seq_b : '',
					bnr_id_a : '',
					bnr_id_b : '',
					show_a : false,
					show_b : false
				});
			}
			
			
			//put db data
			$scope.init = function(){
				$scope.viewDate = new Date();
				$scope.sendRecv("CRM124", "initial_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'date':newViewDate,'emp_id':$scope.emp_id,'branch_id':projInfoService.getBranchID(),'faia_type':$scope.inputVO.faia_type},
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
						}
						if (tota.length > 0) {
							/**分行選單 -- 輔銷科長轄下分行跟轄下輔銷人員對不上的問題導致選單必須額外抓**/
							$scope.mappingSet['branchsDesc'] = [];
							if ($scope.type == 1) {
								angular.forEach(tota[0].body.braList, function(row, index, objs){
									$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
								});
							}
							else{
								angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
									$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
								});
							}
		
		
							var event = [];
							angular.forEach(tota[0].body.resultList, function(row, index, objs){
								event.push($scope.toJsDate(row.ONSITE_DATE).getDate());
							});
		
							for (var i = 0 ; i < event.length ; i++) {
								var j = event[i] + nulldate - 1;
								if (tota[0].body.resultList[i].ONSITE_PERIOD == 1) {
									$scope.dateList[j].seq_a = tota[0].body.resultList[i].SEQ;
									$scope.dateList[j].bnr_id_a = tota[0].body.resultList[i].ONSITE_BRH;
									$scope.dateList[j].period_a = true;
									$scope.dateList[j].status_a = tota[0].body.resultList[i].STATUS;
								}else {
									$scope.dateList[j].seq_b = tota[0].body.resultList[i].SEQ;
									$scope.dateList[j].bnr_id_b = tota[0].body.resultList[i].ONSITE_BRH;
									$scope.dateList[j].period_b = true;
									$scope.dateList[j].status_b = tota[0].body.resultList[i].STATUS;
								}
							}
						//console.log(tota[0].body.resultList	)
						//console.log($scope.dateList)
						};
				});
			};
			$scope.init();
	
			//holiday
			$scope.holiday = function(){
				$scope.sendRecv("CRM124", "holiday", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.holidayList = tota[0].body.holidayList;
						var hol = [];
						for (var i = 0 ; i < $scope.holidayList.length ; i++) {
							hol.push($scope.toJsDate($scope.holidayList[i].HOL_DATE).getDate());
						}
						
						for (var i = 0 ; i < hol.length ; i++) {
							var j = hol[i] + nulldate - 1;
							$scope.dateList[j].holiday = true;
							$scope.dateList[j].holiday = true;
						}
				});
			}
			$scope.holiday();
	
		}
		$scope.change($scope.viewDate.getMonth(), $scope.viewDate.getFullYear(), $scope.viewDate);
		
		//切換上下月用的資料，只到月，因此日期固定唯1
		var month = $scope.viewDate.getMonth();
		var year = $scope.viewDate.getFullYear();
		var newViewDate = new Date($scope.viewDate);
		newViewDate.setDate(1);
		//上個月
		$scope.decrement = function() {
			if (month > 0){
				month = month - 1 ;
			}else {
				month = 11;
				year = year - 1;
			}
			newViewDate.setMonth(newViewDate.getMonth()-1);
			$scope.inputVO.date = newViewDate;
			$scope.change(month, year, newViewDate);	
			
		}
		
		//下個月
		$scope.increment = function() {
			if (month < 11){
				month = month + 1;
			}else {
				month = 0;
				year = year + 1;
			}
			newViewDate.setMonth(newViewDate.getMonth()+1);
			$scope.inputVO.date = newViewDate;
			$scope.change(month, year, newViewDate);	
			
		}
		
		//儲存
		$scope.save = function() {
			var list = $scope.dateList.filter(function(obj){
				return (obj.date != '' && (obj.bnr_id_a != '' || obj.bnr_id_b != ''));
			});
			if(list.length == 0){
				return;
			}
			//避免新增未選駐點行
			angular.forEach(list, function(row, index, objs){
				if(row.bnr_id_a == '' || row.bnr_id_b == ''){
					return;
				}
			});
			
			$scope.inputVO.dateList = list;
	
			$scope.sendRecv("CRM124", "add_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if (tota.length > 0) {
						$scope.showSuccessMsg('ehl_01_common_004');
					};
					$scope.change(month, year, newViewDate);
			});
		}
		
		// edit_onsite 編輯駐點行
		$scope.edit_onsite = function (SEQ) {	
			$scope.sendRecv("CRM124", "initial_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'seq':SEQ},
				function(tota, isError) {
					var row = tota[0].body.resultList[0];
					//--STATUS--	輔銷科長:進入變更駐點行覆核頁面 輔銷人員:僅顯示 "待覆核中"
					if (row.STATUS == '2') {
						//輔銷科長
						if (projInfoService.getRoleID() == "FA9" || projInfoService.getRoleID() == "IA9"){
							$rootScope.menuItemInfo.url = "assets/txn/CRM1243/CRM1243.html";
							return;
							//輔銷人員
						}else{
							$scope.showMsg("待覆核中");
							return;
						}		     		
					}
		
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM124/CRM124_EDIT_ONSITE.html',
						className: 'CRM124_EDIT_ONSITE',
						showClose: false,
						controller: ['$scope', function($scope) {
							$scope.row = row;
							$scope.emp_id = $scope.emp_id;
						}]
					});
					dialog.closePromise.then(function (data) {
						if(data.value === 'successful'){
							$scope.change(month, year, newViewDate);
						}
					});	
			});
	
		}
});
