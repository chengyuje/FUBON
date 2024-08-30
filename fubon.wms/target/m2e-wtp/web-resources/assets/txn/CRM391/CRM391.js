/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM391Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM391Controller";
		
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();

		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST_TEMP'] = tota[0].body.uhrmList;
						$scope.mappingSet['UHRM_LIST'] = [];
						angular.forEach($scope.mappingSet['UHRM_LIST_TEMP'], function(row){
							$scope.mappingSet['UHRM_LIST'].push({LABEL : row.LABEL, DATA : row.UHRM_CODE});
                		});
					}
		});
		
		// combobox
		getParameter.XML(["CRM.TRS_APL_REASON", "CRM.TRS_CALL_REVIEW_TYPE", "CRM.TRS_TYPE", "CRM.TRS_PROCESS_STATUS"], function(totas) {
			if (totas) {
				$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				$scope.TRS_CALL_REVIEW_TYPE = totas.data[totas.key.indexOf('CRM.TRS_CALL_REVIEW_TYPE')];
				$scope.TRS_TYPE = totas.data[totas.key.indexOf('CRM.TRS_TYPE')];
				$scope.TRS_PROCESS_STATUS = totas.data[totas.key.indexOf('CRM.TRS_PROCESS_STATUS')];
			}
		});
		// 分派狀態
		$scope.mappingSet['process'] = [];
		$scope.mappingSet['process'].push({LABEL : '已分派',DATA : '1'},{LABEL : '未分派',DATA : '2'});
		
		// init
//		$scope.pri_id = projInfoService.getPriID()[0];
//		var tempid = ['009','010','011'];var tempid2 = ['001','002','003'];
//		$scope.requireBrh = (tempid.indexOf($scope.pri_id) >= 0 || tempid2.indexOf($scope.pri_id) >= 0) ? true : false;
//		$scope.requireAo = tempid2.indexOf($scope.pri_id) >= 0 ? true : false;
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
	        $scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// bra
		$scope.bra_list = projInfoService.getAvailBranch();
		$scope.BRANCH_LIST = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row) {
			if($scope.inputVO.branch_area_id) {
				if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id)			
					$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			} else
				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		// aocode
		$scope.ao_code = projInfoService.getAoCode();
		
		$scope.ORG_AO_CODE = [];
		$scope.getAolist = function() {
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO.org_ao_brh},
					function(tota, isError) {
						if (!isError) {
							$scope.ORG_AO_CODE = [];
							$scope.inputVO.org_ao_code = "";
							if($scope.ao_code.length > 0) {
								if($scope.ao_code.length > 1 ) {
		    						angular.forEach($scope.ao_code, function(row) {
		    							$scope.ORG_AO_CODE.push({LABEL: projInfoService.getUserName(), DATA: row});
		    						});	
		    					}
		    					else {
		    						$scope.ORG_AO_CODE.push({LABEL: projInfoService.getUserName(), DATA: $scope.ao_code[0]});
		    					}
							}
							else {
								angular.forEach(tota[0].body.ao_list, function(row) {
									$scope.ORG_AO_CODE.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
								});
							}
							return;
						}
			});
		};
		$scope.getAolist();
		$scope.NEW_AO_CODE = [];
		$scope.getAolist2 = function() {
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO.new_ao_brh},
					function(tota, isError) {
						if (!isError) {
							$scope.NEW_AO_CODE = [];
							$scope.inputVO.new_ao_code = "";
							if($scope.ao_code.length > 0) {
								if($scope.ao_code.length > 1 ) {
		    						angular.forEach($scope.ao_code, function(row) {
		    							$scope.NEW_AO_CODE.push({LABEL: projInfoService.getUserName(), DATA: row});
		    						});	
		    					}
		    					else {
		    						$scope.NEW_AO_CODE.push({LABEL: projInfoService.getUserName(), DATA: $scope.ao_code[0]});
		    					}
							}
							else {
								angular.forEach(tota[0].body.ao_list, function(row) {
									$scope.NEW_AO_CODE.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
								});
							}
							return;
						}
			});
		};
		$scope.getAolist2();
		//
		
		// date picker
		// 理專送出時間
		$scope.apl_sDateOptions = {};
		$scope.apl_eDateOptions = {};
		// 分行主管送出時間
		$scope.apl_mgr_sDateOptions = {};
		$scope.apl_mgr_eDateOptions = {};
		// 督導主管覆核時間
		$scope.op_mgr_sDateOptions = {};
		$scope.op_mgr_eDateOptions = {};
		// 區域主管覆核時間
		$scope.dc_mgr_sDateOptions = {};
		$scope.dc_mgr_eDateOptions = {};
		// 總行覆核時間
		$scope.hq_mgr_sDateOptions = {};
		$scope.hq_mgr_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.apl_sDateOptions.maxDate = $scope.inputVO.apl_eDate;
			$scope.apl_eDateOptions.minDate = $scope.inputVO.apl_sDate;
		};
		$scope.limitDate2 = function() {
			$scope.apl_mgr_sDateOptions.maxDate = $scope.inputVO.apl_mgr_eDate;
			$scope.apl_mgr_eDateOptions.minDate = $scope.inputVO.apl_mgr_sDate;
		};
		$scope.limitDate3 = function() {
			$scope.op_mgr_sDateOptions.maxDate = $scope.inputVO.op_mgr_eDate;
			$scope.op_mgr_eDateOptions.minDate = $scope.inputVO.op_mgr_sDate;
		};
		$scope.limitDate4 = function() {
			$scope.dc_mgr_sDateOptions.maxDate = $scope.inputVO.dc_mgr_eDate;
			$scope.dc_mgr_eDateOptions.minDate = $scope.inputVO.dc_mgr_sDate;
		};
		$scope.limitDate5 = function() {
			$scope.hq_mgr_sDateOptions.maxDate = $scope.inputVO.hq_mgr_eDate;
			$scope.hq_mgr_eDateOptions.minDate = $scope.inputVO.hq_mgr_sDate;
		};
		// date picker end
		
		// inquire
		$scope.inquire = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("CRM391", "inquire", "com.systex.jbranch.app.server.fps.crm391.CRM391InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.download = function(row) {
			//資況表客戶同意書
			$scope.sendRecv("CRM371", "download", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'seq': row.TRS_SEQ},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
			//十保客戶指定聲明書
			$scope.sendRecv("CRM371", "download2", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'seq': row.TRS_SEQ},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
		};
		
		// old code?
		var now = new Date();
		$scope.passParams = $scope.connector('get','passParams');
//		alert($scope.passParams);
		$scope.fromCRM181 = function() {
			if($scope.passParams != undefined) {
				var dataParams = $scope.passParams.replace(/=/g, ',').split(',');
//				alert(JSON.stringify(dataParams));
				//理專送出時間
				var dateParam = [];
				for(var i=0; dataParams.length > i ; i++){
					if(dataParams[i] == "FRQ_TYPE"){						
						dateParam = dataParams[i+1];
					}
				}
				//日
				if(dateParam == 'D') {
					$scope.inputVO.apl_sDate = now;
					$scope.inputVO.apl_eDate = now;
				}
				//周
				if(dateParam == 'W') {
					// 現在日期
					var myDate  = new Date(); 
					// 現在是星期幾，日為0、一為1、二為2、三為3、四為4、五為5、六為6
					var myDay  = myDate.getDay(); 
					// 設成日為7
					if(myDay == 0) {
					  myDay = 7;
					}					
					var myStartDate = new Date();
					var startDate = new Date(myStartDate.setDate(myStartDate.getDate() + (0-(myDay-1))));
					$scope.inputVO.apl_sDate = startDate;
					var myEndDate = new Date();
					var endDate = new Date(myEndDate.setDate(myEndDate.getDate() + (7-myDay)));
					$scope.inputVO.apl_eDate = endDate;		
				}
				//月
				if(dateParam == 'M') {
					if(dateParam == 'M') {
						var date = new Date(), y = date.getFullYear(), m = date.getMonth();
						$scope.inputVO.apl_sDate = new Date(y, m, 1);
						$scope.inputVO.apl_eDate = new Date(y, m + 1, 0);
					}
				}
				//分派狀態
				var statusParam = [];
				for(var i=0; dataParams.length > i ; i++){
					if(dataParams[i] == "PROCESS_STATUS"){						
						statusParam = dataParams[i+1];
					}
				}		
				if(statusParam == 'S'){
					$scope.inputVO.process = '1';//已分派
				} else {
					$scope.inputVO.process = '2';//未分派
				}
				
				$scope.inquire();
			}
		}
		$scope.fromCRM181();
				
		function getMonday(d) {
			d = new Date(d);
			var day = d.getDay(), diff = d.getDate() - day + (day == 0 ? -6:1);
			return new Date(d.setDate(diff));
		}
		
});