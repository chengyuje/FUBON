/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM160Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM160Controller";
		
		//filter
		var vo = {'param_type': 'CAM.RE_STATUS', 'desc': false};
        if(!projInfoService.mappingSet['CAM.RE_STATUS']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.RE_STATUS'] = totas[0].body.result;
        			$scope.mappingSet['CAM.RE_STATUS'] = projInfoService.mappingSet['CAM.RE_STATUS'];
        		}
        	});
        } else {
        	$scope.mappingSet['CAM.RE_STATUS'] = projInfoService.mappingSet['CAM.RE_STATUS'];
        }
        
        $scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
        $scope.importStartDateOptions = {
    		maxDate: $scope.inputVO.importEDate || $scope.maxDate,
    		minDate: $scope.minDate
		};
		$scope.importEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.importSDate || $scope.minDate
		};
		$scope.limitDate = function() {
			$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
			$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
		};
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate,
			minDate: $scope.minDate
		};
		$scope.startDateOptions2 = {
			maxDate: $scope.inputVO.eDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate
		};
		$scope.endDateOptions2 = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.eDate
		};
		$scope.limitDate2 = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.inputVO.sDate2;
			$scope.startDateOptions2.minDate = $scope.inputVO.sDate;
			
			$scope.endDateOptions.minDate = $scope.inputVO.sDate;
			$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
			
			$scope.endDateOptions2.minDate = $scope.inputVO.eDate;
		};
		
		$scope.hisStartDateOptions = {
			maxDate: $scope.inputVO.his_eDate,
			minDate: $scope.minDate
		};
		$scope.hisStartDate2Options = {
			maxDate: $scope.inputVO.his_eDate,
			minDate: $scope.minDate
		};
		$scope.hisEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.his_sDate
		};
		$scope.hisEndDate2Options = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.his_eDate
		};
		$scope.limitDate_his = function() {
			$scope.hisStartDateOptions.maxDate = $scope.inputVO.his_eDate || $scope.inputVO.his_sDate2;
			$scope.hisStartDate2Options.minDate = $scope.inputVO.his_sDate;
			
			$scope.hisEndDateOptions.minDate = $scope.inputVO.his_sDate;
			$scope.hisEndDateOptions.maxDate = $scope.inputVO.his_eDate2;
			
			$scope.hisEndDate2Options.minDate = $scope.inputVO.his_eDate;
		};
		
        $scope.his_delStartDateOptions = {
        	maxDate: $scope.inputVO.his_delEDate,
        	minDate: $scope.minDate
		};
		$scope.his_delEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.his_delSDate
		};
		$scope.limitDate_hisDel = function() {
			$scope.his_delStartDateOptions.maxDate = $scope.inputVO.his_delEDate || $scope.maxDate;
			$scope.his_delEndDateOptions.minDate = $scope.inputVO.his_delSDate || $scope.minDate;
		};
		
		$scope.data = $scope.connector('get','CAM160_INPUTVO') || {};
		$scope.connector('set','CAM160_INPUTVO', null);

        $scope.init = function(){
			$scope.inputVO = {
					campID: $scope.data.campID,
					importSDate: $scope.data.importSDate,
					importEDate: $scope.data.importEDate,
					campName: $scope.data.campName,
					sDate: $scope.data.sDate,
					eDate: $scope.data.eDate,
					eDate2: $scope.data.eDate2, 
					
					his_campID: $scope.inputVO.his_campID,
					his_campName: $scope.inputVO.his_campName, 
					his_modifier: $scope.inputVO.his_modifier, 
					his_sDate: $scope.inputVO.his_sDate,
					his_sDate2: $scope.inputVO.his_sDate2,
					his_eDate: $scope.inputVO.his_eDate,
					his_eDate2: $scope.inputVO.his_eDate2,
					his_delSDate: $scope.inputVO.his_delSDate,
					his_delEDate: $scope.inputVO.his_delEDate
        	};

			$scope.limitDate();
			$scope.limitDate2();
			$scope.paramList = [];
		};
        $scope.init();
        
        $scope.init_his = function(){
        	$scope.hisList=[];
			$scope.inputVO = {
					campID: $scope.inputVO.campID,
					importSDate: $scope.inputVO.importSDate,
					importEDate: $scope.inputVO.importSDate,
					campName: $scope.inputVO.campName,
					sDate: $scope.inputVO.sDate,
					eDate: $scope.inputVO.eDate,
					eDate2: $scope.inputVO.eDate2,
					
					his_campID: '',
					his_campName: '',
					his_modifier: '',
					his_sDate: undefined,
					his_sDate2: undefined,
					his_eDate: undefined,
					his_eDate2: undefined,
					his_delSDate: undefined, 
					his_delEDate: undefined
        	};
			
			$scope.limitDate_his();
			$scope.limitDate_hisDel();
		};
        $scope.init_his();
		
        // 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        	$scope.hisList = [];
        }
        $scope.inquireInit();
        
        $scope.inquire = function(){
        	console.log('inquire');
			$scope.sendRecv("CAM160", "queryData", "com.systex.jbranch.app.server.fps.cam160.CAM160InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row){
								row.set = [];
								row.set.push({LABEL: "一般移除", DATA: "01"});
								row.set.push({LABEL: "強制移除", DATA: "02"});
								if (row.PA_MODIFY_YN == 'Y')
									row.set.push({LABEL: "文件話術修改", DATA: "03"});
								
								if (row.MODIFY_YN == 'Y' && row.SFA_PARA_ID != undefined)
									row.set.push({LABEL: "活動設定修改", DATA: "04"});
							});
							return;
						}
			});
		};

		//2016/12/05; Sebastian; 若由他頁導回,則用帶回查詢值做查詢
		console.log("SCODE.DATA: "+!_.isEmpty($scope.data))
		if(!_.isEmpty($scope.data)){
			$scope.init();
			$scope.inquire();
		}

		// 初始分頁資訊 inquireInit_his();inquire_his()
        $scope.inquireInit_his = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        	$scope.hisList = [];
        }
        $scope.inquireInit_his();
        
        $scope.inquire_his = function(){
        	console.log('inquire_his');
			$scope.sendRecv("CAM160", "queryHisData", "com.systex.jbranch.app.server.fps.cam160.CAM160InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList_his.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.hisList = tota[0].body.resultList_his;
							$scope.hisOutputVO = tota[0].body;
							return;
						}
			});
		};
		
		// 活動名單移除及修改 -功能
		$scope.action = function(row) {
			if(row.cmbAction) {
				$scope.connector('set','CAM140PAGE', "CAM160");
				
				if(row.cmbAction == "01") { //01 一般移除 removeLead
					var dialog = ngDialog.open({
						template: 'assets/txn/CAM160/CAM160_DEL.html',
						className: 'CAM160_DEL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.type = 'M1';
		                	$scope.campID = row.CAMPAIGN_ID;
		                	$scope.stepID = row.STEP_ID;
		                	$scope.reason = row.RV_REASON;
		                }]
					}).closePromise.then(function (data) {
						 $scope.inquire();
					});
				} else if(row.cmbAction == "02") { //02 強制移除
					var dialog = ngDialog.open({
						template: 'assets/txn/CAM160/CAM160_DEL.html',
						className: 'CAM160_DEL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.type = 'M2';
		                	$scope.campID = row.CAMPAIGN_ID;
		                	$scope.stepID = row.STEP_ID;
		                	$scope.reason = row.RV_REASON;
		                }]
					}).closePromise.then(function (data) {
						 $scope.inquire();
					});
				} else if(row.cmbAction == "03") { //03 文件話術修改
					$scope.connector('set','CAM140', "updateImp");
	        		$scope.connector('set','IMP_SEQNO', row.SEQNO);
	        		$scope.connector('set','CAM160_INPUTVO', $scope.inputVO);
	        		$rootScope.menuItemInfo.url = "assets/txn/CAM140/CAM140.html";
				} else { //04 活動設定修改
					$scope.connector('set','CAM140', "update");
	        		$scope.connector('set','CAM140EDIT', row.SFA_PARA_ID);
	        		$scope.connector('set','IMP_SEQNO', row.SEQNO);
	        		$scope.connector('set','CAM160_INPUTVO', $scope.inputVO);
	        		$rootScope.menuItemInfo.url = "assets/txn/CAM140/CAM140.html";
				}
				
				row.cmbAction = "";
			}
		};
		
		$scope.hisAction = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM160/CAM160_LEADS.html',
				className: 'CAM160_LEADS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.campID = row.CAMPAIGN_ID;
                	$scope.stepID = row.STEP_ID;
                	$scope.campName = row.CAMPAIGN_NAME;
                }]
			})
		}
});
