/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM124_ADD_ONSITEController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM124_ADD_ONSITEController";
		
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		
		//get first date of week
		function dateInWeek(month, year) {
		    return new Date(year + "-" + month + "-01").getDay()
		}
		
		//get days of month
		function daysInMonth(month, year) {
		    return new Date(year, month, 0).getDate();
		}

		$scope.inputVO.emp_id = sysInfoService.getUserID();
		$scope.inputVO.date = $scope.viewDate;
		
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
				//put null date
				if (i < nulldate){
					$scope.dateList.push({
						date : '', 
						holiday : false,
						period_a : false,
						period_b : false,
						seq_a : '',
						seq_b : '',
						bnr_id_a : '',
						bnr_id_b : '',
						show_a : false,
						show_b : false});
					
				//put date
				} else if (i < (daysInMonth($scope.inputVO.month, $scope.inputVO.year) + nulldate)) {
					$scope.dateList.push({
						date : i - nulldate + 1, 
						holiday : false,
						period_a : false,
						period_b : false,
						seq_a : '',
						seq_b : '',
						bnr_id_a : '',
						bnr_id_b : '',
						show_a : false,
						show_b : false});
					
				//put null date
				} else {
					$scope.dateList.push({
						date : '', 
						holiday : false,
						period_a : false,
						period_b : false,
						seq_a : '',
						seq_b : '',
						bnr_id_a : '',
						bnr_id_b : '',
						show_a : false,
						show_b : false});
				}
			}
			
			
			//put db data
			$scope.init = function(){
				$scope.sendRecv("CRM124", "initial_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'date':newViewDate,'emp_id':projInfoService.getUserID(),'branch_id':projInfoService.getBranchID()},
					function(tota, isError) {
		                if (isError) {
		                	$scope.showErrorMsg(tota[0].body.msgData);
		                }
	                	if (tota.length > 0) {
	                		
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
	            				}else {
	            					$scope.dateList[j].seq_b = tota[0].body.resultList[i].SEQ;
	            					$scope.dateList[j].bnr_id_b = tota[0].body.resultList[i].ONSITE_BRH;
	            					$scope.dateList[j].period_b = true;
	            				}
	            			}
	                	};
					}
				);
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
						}
			)};
			$scope.holiday();
			
		}
		$scope.change($scope.viewDate.getMonth(), $scope.viewDate.getFullYear(), $scope.viewDate);
		

		var month = $scope.viewDate.getMonth();
		var year = $scope.viewDate.getFullYear();
		var newViewDate = $scope.viewDate;
		
		//上個月
		$scope.decrement = function() {
			if (month > 0){
				month--;
			}else {
				month = 11;
				year--;
			}
			newViewDate = new Date(new Date(newViewDate).setMonth(newViewDate.getMonth()-1));
			$scope.change(month, year, newViewDate);
			$scope.inputVO.date = newViewDate;
		}
		
		//下個月
		$scope.increment = function() {
			if (month < 11){
				month++;
			}else {
				month = 0;
				year++;
			}
			newViewDate = new Date(new Date(newViewDate).setMonth(newViewDate.getMonth()+1));
			$scope.change(month, year, newViewDate);
			$scope.inputVO.date = newViewDate;
		}
		
		//儲存
        $scope.save = function() {
        	var list = $scope.dateList.filter(function(obj){
	    		return (obj.date != '' && (obj.bnr_id_a != '' || obj.bnr_id_b != ''));
	    	});
        	if(list.length == 0){
        		return;
        	}
        	$scope.inputVO.dateList = list;
        	
    		$scope.sendRecv("CRM124", "add_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
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
        };
		
        
        
});
