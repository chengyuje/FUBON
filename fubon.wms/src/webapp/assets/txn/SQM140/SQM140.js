/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM140Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM140Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.function_LIST = [{'LABEL':'編輯', 'DATA': 'edit'},{'LABEL':'刪除', 'DATA': 'del'}];
		$scope.deduction_initial_LIST = [{'LABEL':'未簽核', 'DATA': 'null'},{'LABEL':'扣分', 'DATA': 'Y'},{'LABEL':'不扣分', 'DATA': 'N'}];
		
		getParameter.XML(["SQM.QTN_TYPE"], function(totas) {
			if (totas) {
				$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
			}
		});
        /*** 可示範圍  JACKY共用版  START ***/
		
		// date picker
		var currDate = new Date(2018, 8, 1, 0, 0, 0); //只能選擇20180901以後資料
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: currDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: currDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			if ($scope.inputVO.eCreDate) {
				let y = $scope.inputVO.eCreDate.getFullYear() - 1;
				let m = $scope.inputVO.eCreDate.getMonth();
				let d = $scope.inputVO.eCreDate.getDate();
				$scope.bgn_sDateOptions.minDate = new Date(y, m, d);
			}
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || currDate;
			if ($scope.inputVO.sCreDate) {
				let y = $scope.inputVO.sCreDate.getFullYear() + 1;
				let m = $scope.inputVO.sCreDate.getMonth();
				let d = $scope.inputVO.sCreDate.getDate();
				$scope.bgn_eDateOptions.maxDate = new Date(y, m, d);
			}
		};
		// date picker end

        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.eCreDate,'yyyyMMdd');
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);
        };
		
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate: new Date(new Date().getFullYear(),new Date().getMonth(),1),
					eCreDate: new Date(),
					region_center_id: '',   //區域中心
					branch_area_id: '' ,    //營運區
					branch_nbr: '',				//分行				
        	};
			$scope.rptDate = '';
			$scope.totalData = [];
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};
			$scope.limitDate();
			$scope.dateChange();
		
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;			
		
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
			$scope.outputVO={totalList:[]};
		}
		$scope.inquireInit();
		
		//資料查詢
		$scope.query = function() {	
			
			$scope.rptDate = $filter('date')($scope.inputVO.sCreDate, 'yyyy/MM/dd')+ '~' +$filter('date')($scope.inputVO.eCreDate, 'yyyy/MM/dd'); 			
			$scope.sendRecv("SQM140", "queryData", "com.systex.jbranch.app.server.fps.sqm140.SQM140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.outputVO={};
								$scope.showMsg("ehl_01_common_009");
								
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.totalList);
							$scope.paramList = tota[0].body.totalList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;		
							return;
						}						
			});
		};
		
		$scope.edit = function(row){
			var dialog = ngDialog.open({
				template: 'assets/txn/SQM140/SQM140_edit.html',
				className: 'SQM140',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                	$scope.qtnType = row.QTN_TYPE;
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.query();
				}
			});
        };
        
        $scope.del = function(row){      					
					$scope.delList = [];
					$scope.delList.push(row);
					$scope.inputVO.delList = $scope.delList;
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
	               	$scope.sendRecv("SQM140","deleteData","com.systex.jbranch.app.server.fps.sqm140.SQM140InputVO",
	            			$scope.inputVO,function(tota,isError){
	            			if(isError){
	            				$scope.showErrorMsg(tota[0].body.msgData);
	            			}
	                       	if (tota.length > 0) {
	                    		$scope.showSuccessMsg('ehl_01_common_003');
	                    		$scope.query();
	                    	};
	            	});
				});	
        };
		
        
		//放行
		$scope.ho_check = function() {
			$scope.checkList = [];
			angular.forEach($scope.paramList, function(row, index, objs){
				if(row.choice == 'Y'){
					$scope.checkList.push($scope.paramList[index]);	
					
				}
			});
			
			$scope.inputVO.checkList = $scope.checkList;
			$scope.sendRecv("SQM140","ho_check","com.systex.jbranch.app.server.fps.sqm140.SQM140InputVO",
        			$scope.inputVO,function(tota,isError){
        			if(isError){
        				$scope.showErrorMsg(tota[0].body.msgData);
        			}
                   	if (tota.length > 0) {
                		$scope.showSuccessMsg('ehl_01_common_002');
                		$scope.query();
                	};
        	});
		};
		
		
		//扣分
		$scope.deduction = function() {	
			$scope.checkList = [];
			var err_flag = false;
			angular.forEach($scope.paramList, function(row, index, objs){
				if(row.choice == 'Y' && !err_flag){
					if(row.CASE_NO == null || row.CASE_NO == ''){
						err_flag = true;
					}
					$scope.checkList.push($scope.paramList[index]);	
					
				}
			});
			
			if(err_flag){
				$scope.showErrorMsg("案件未建立，無法進行扣分。");
				return;
			}
			
			$scope.inputVO.checkList = $scope.checkList;
			$scope.sendRecv("SQM140","deduction","com.systex.jbranch.app.server.fps.sqm140.SQM140InputVO",
        			$scope.inputVO,function(tota,isError){
        			if(isError){
        				$scope.showErrorMsg(tota[0].body.msgData);
        			}
                   	if (tota.length > 0) {
                		$scope.showSuccessMsg('ehl_01_common_002');
                		$scope.query();
                	};
        	});
		};
		
		//不扣分
		$scope.no_deduction = function() {	
			$scope.checkList = [];
			var err_flag = false;
			angular.forEach($scope.paramList, function(row, index, objs){
				if(row.choice == 'Y' && !err_flag){
					if(row.CASE_NO == null || row.CASE_NO == ''){
						err_flag = true;
					}
					$scope.checkList.push($scope.paramList[index]);	
					
				}
			});
			
			if(err_flag){
				$scope.showErrorMsg("案件未建立，無法進行不扣分。");
				return;
			}
			
			$scope.inputVO.checkList = $scope.checkList;
			$scope.sendRecv("SQM140","no_deduction","com.systex.jbranch.app.server.fps.sqm140.SQM140InputVO",
        			$scope.inputVO,function(tota,isError){
        			if(isError){
        				$scope.showErrorMsg(tota[0].body.msgData);
        			}
                   	if (tota.length > 0) {
                		$scope.showSuccessMsg('ehl_01_common_002');
                		$scope.query();
                	};
        	});
		};
				
		//跳轉分行畫面
        $scope.importRPT =function() {
        	var dialog = ngDialog.open({
				template: 'assets/txn/SQM140/SQM140_import.html',
				className: 'SQM140',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
//				if(data.value === 'successful'){
					$scope.query();
//				}
			});
        };
		
        
        $scope.select_all = function() {
        	if($scope.inputVO.select_all == 'Y' && $scope.paramList != undefined){
    			angular.forEach($scope.paramList, function(row, index, objs){
    				row.choice = 'Y';
    			});
        	}
        	if($scope.inputVO.select_all == 'N' && $scope.paramList != undefined){
    			angular.forEach($scope.paramList, function(row, index, objs){
    				row.choice = 'N';
    			});
        	}
        }
        
        $scope.select_false = function(choice){
        	if(choice == 'N'){
        		$scope.inputVO.select_all = 'N';
        	}
        }
        
      //匯出彙整報告
    	$scope.collectionExportRPT = function(){
    		if($scope.totalData.length == 0) {
    			$scope.showMsg("ehl_01_common_009");
        		return;
        	}
			$scope.sendRecv("SQM140", "collectionExportRPT", "com.systex.jbranch.app.server.fps.sqm140.SQM140OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            		return;
		            	}
						else{
		            		if(tota[0].body == null) {
	                			$scope.showMsg("查無不扣分資料匯出。");
	                			return;
	                		}
		            		else{
		            			$scope.showSuccessMsg('下載成功');
		            			return;
		            		}
		            		
		            	}
	                	
			});
		};
		
	  //匯出備查報告
    	$scope.exportRPT = function(){
    		if($scope.totalData.length == 0) {
    			$scope.showMsg("ehl_01_common_009");
        		return;
        	}
			$scope.sendRecv("SQM140", "exportRPT", "com.systex.jbranch.app.server.fps.sqm140.SQM140OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            		return;
		            	}else{
//		            		if(tota[0].body == null) {
//	                			$scope.showMsg("無核決扣分資料匯出。");
//	                			return;
//	                		}
//		            		else{
		            			$scope.showSuccessMsg('下載成功');
		            			return;
//		            		}
		            		
		            	}
                		
	                	
			});
		};
		
});
