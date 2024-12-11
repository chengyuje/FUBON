/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM331Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('CRM210Controller', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		//
		$scope.controllerName = "CRM331Controller";
		
		// combobox
		$scope.pri_id = String(sysInfoService.getPriID());
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "CRM.CUST_GENDER", "CRM.TRS_APL_REASON", "COMMON.YES_NO", "TBCRM_CUST_NOTE.TAKE_CARE_MATCH_YN", "CRM.CRM331_CHG_FRQ", "CRM.331_MAX_QUERY_COUNT"], function(totas) {
			if (totas) {
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.CUST_GENDER = totas.data[totas.key.indexOf('CRM.CUST_GENDER')];
				// 理專取PARAM_CODE為A開頭的選項；主管則取全部
				$scope.TRS_APL_REASON = [];
				if($scope.pri_id == '002' || $scope.pri_id == '003') {
					angular.forEach(totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')], function(row) {
						if (row.DATA.substring(0, 1) == 'A')
							$scope.TRS_APL_REASON.push(row);
					});
				} else
					$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				$scope.COMYN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.TCMYN = totas.data[totas.key.indexOf('TBCRM_CUST_NOTE.TAKE_CARE_MATCH_YN')];
				$scope.CRM331_CHG_FRQ = totas.data[totas.key.indexOf('CRM.CRM331_CHG_FRQ')];
				$scope.MAX_QUERY_COUNT = totas.data[totas.key.indexOf('CRM.331_MAX_QUERY_COUNT')];
			}
		}); 
		//get處理方式
        if($scope.pri_id != '045')
        	$scope.mappingSet['process_mode'] = [{LABEL: '加入待移轉清單', DATA: '1'}];
        else
        	$scope.mappingSet['process_mode'] = [{LABEL: '強制AO移轉', DATA: '2'}];
		//
        
        // date picker
        $scope.sDateOptions = {};
		$scope.eDateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate;
		};
		// date picker end
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.checkVO = {};
			$scope.regionOBJ = ['Y', $scope.inputVO, "ao_03", "REGION_LIST", "ao_04", "AREA_LIST", "ao_05", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.regionOBJ);
		};
		$scope.init();
		$scope.init2 = function() {
			$scope.inputVO2 = {};
			$scope.regionOBJ2 = ['N', $scope.inputVO2, "ao_03", "REGION_LIST2", "ao_04", "AREA_LIST2", "new_branch_nbr", "BRANCH_LIST2", "new_ao_code", "AO_LIST2", "emp_id", "EMP_LIST2"];
	        $scope.RegionController_setName($scope.regionOBJ2).then(function(data) {
	        	$scope.BRANCH_LIST_OTHER = angular.copy($scope.BRANCH_LIST2);
//	        	$scope.AO_LIST_OTHER = angular.copy($scope.AO_LIST2);
	        });
		};
		$scope.init2();
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
		}
		$scope.inquireInit();
		//
		
		// 2017/7/19 主管要同查詢
		if($scope.pri_id != '045') {
			// inputVO.ao_05 change 會call
			$scope.ChangeOther = function() {
				if($scope.inputVO.ao_05) {
					$scope.BRANCH_LIST_OTHER = [{'LABEL': $filter('mapping')($scope.inputVO.ao_05, $scope.BRANCH_LIST),'DATA': $scope.inputVO.ao_05}];
					$scope.inputVO2.new_branch_nbr = $scope.inputVO.ao_05;
				} else {
					$scope.BRANCH_LIST_OTHER = angular.copy($scope.BRANCH_LIST);
					$scope.inputVO2.new_branch_nbr = "";
				}
			};
			
			$scope.getAolist1 = function() {
				$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.ao_03,'branch_area_id': $scope.inputVO.ao_04,'branch_nbr': $scope.inputVO.ao_05},
						function(tota, isError) {
							if (!isError) {
								$scope.ao_code_common = [];
								$scope.inputVO.ao_code = '';
								$scope.ao_code_common.push({LABEL: "空CODE", DATA: "0" , TYPE: "0"});
								
								angular.forEach(tota[0].body.ao_list, function(row) {
									if(row.TYPE == '1')
		    							$scope.ao_code_common.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
		    						else if(row.TYPE == '2')
		    							$scope.ao_code_common.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
		    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
		    						else if(row.TYPE == '3')
		    							$scope.ao_code_common.push({'LABEL': (row.EMP_NAME+'(維護Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
								});
								
								$scope.ao_code_common = $filter('orderBy')($scope.ao_code_common, 'TYPE', false);
							}
						}
					);
			};
			$scope.getAolist1();
			
			// inputVO2.new_branch_nbr change 會call
			$scope.getAolist = function() {
				if($scope.inputVO.ao_05 && $scope.inputVO.ao_05 != $scope.inputVO2.new_branch_nbr)
					return;
				$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO2.ao_03,'branch_area_id': $scope.inputVO2.ao_04,'branch_nbr': $scope.inputVO2.new_branch_nbr},
						function(tota, isError) {
							if (!isError) {
								$scope.AO_LIST_OTHER = [];
								angular.forEach(tota[0].body.ao_list, function(row) {
									if(row.TYPE == '1')
		    							$scope.AO_LIST_OTHER.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
		    						else if(row.TYPE == '2')
		    							$scope.AO_LIST_OTHER.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
		    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
		    						else if(row.TYPE == '3')
		    							$scope.AO_LIST_OTHER.push({'LABEL': (row.EMP_NAME+'(維護Code)'),'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
								});
								
								$scope.AO_LIST_OTHER = $filter('orderBy')($scope.AO_LIST_OTHER, 'TYPE', false);
								// 手動連動 新查出來的分行裏面沒這個AO
								if ($scope.inputVO2.new_ao_code) {
									if(!_.find(tota[0].body.ao_list, function(o) { return o.AO_CODE == $scope.inputVO2.new_ao_code; }))
										$scope.inputVO2.new_ao_code = "";
								}
							}
						}
					);
			};
			$scope.getAolist1();
			
			if($scope.inputVO2.new_branch_nbr){$scope.getAolist()};
			
			// inputVO2.new_ao_code change will call, 手動連動 copy region
			$scope.changeAolist = function() {
				if ($scope.inputVO2.new_ao_code) {
					var msg = $filter('i18n')('ehl_02_common_009'); //維護CODE的客戶將不計績，請再次確認是否認養或移入哦！
		    		
					if($filter('filter')($scope.AO_LIST_OTHER, {'DATA': $scope.inputVO2.new_ao_code})[0].TYPE == '3') {
	        			$confirm({text: msg , title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
	    					
	    				});
	        		}
	        		
					$scope.inputVO2.new_branch_nbr = $filter('filter')($scope["TOTAL_AO_LIST"], {AO_CODE: $scope.inputVO2.new_ao_code})[0].BRANCH_NBR;
					$scope.inputVO2.new_ao_type = $filter('filter')($scope["TOTAL_AO_LIST"], {AO_CODE: $scope.inputVO2.new_ao_code})[0].TYPE;
					
				}
			};
		}
		
		$scope.clearOTH = function() {
	    	if($scope.inputVO2.apl_reason != "99")
	    		$scope.inputVO2.apl_reason_oth = "";
	    };
	    
	    $scope.checkrow = function() {
	    	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.data, function(row){
        			if (row.UEMP_ID != null) {
        				
        			} else {
        				row.SELECTED = true;
        			}
    			});
        	} else {
        		angular.forEach($scope.data, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.resultList, function(row){
        			if (row.UEMP_ID != null) {
        				
        			} else {
        				row.SELECTED = true;
        			}
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
		
		$scope.inquire = function() {
			// old code from AO_LIST have AO_LIST 
			$scope.inputVO.aolist = $scope.AO_LIST;
			// follow carley
			$scope.inputVO.branch_list = [];
			angular.forEach($scope.BRANCH_LIST, function(row) {
				if(row.DATA != "" && row.DATA != "0"){
					$scope.inputVO.branch_list.push({LABEL: row.LABEL, DATA: row.DATA});					
				}
			});
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			if($scope.inputVO.re_ao_code)
				$scope.inputVO.re_ao_code = $scope.inputVO.re_ao_code.toUpperCase();

			//分行必輸
			if($scope.inputVO.ao_05 == undefined || $scope.inputVO.ao_05 == null || $scope.inputVO.ao_05 == "") {
				$scope.showErrorMsg('請輸入分行後再做查詢');
        		return;
			}
			
			$scope.sendRecv("CRM331", "inquire", "com.systex.jbranch.app.server.fps.crm331.CRM331InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					debugger
					var maxcntAry = $filter('filter')($scope.MAX_QUERY_COUNT, {DATA: "1"});
					var maxCount = (maxcntAry != null && maxcntAry.length > 0) ? maxcntAry[0].LABEL : 1000; //找不到參數，預設查詢筆數上限為1000
					if(tota[0].body.resultList.length > maxCount) {
						$scope.showWarningMsg("查詢結果超過筆數上限，只顯示前" + maxCount + "筆資料");
					}
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					return;
				}
			});
	    };
	    
	    $scope.$watch('EMP_LIST', function(newValue, oldValue) {
	    	if ($scope.AO_LIST.length > 0 && $scope.EMP_LIST.length > 0) {
	    		$scope.RM_LIST = [];
//	    		$scope.rmList = [];
	    		for (var i = 0; i < $scope.EMP_LIST.length; i++) {
	    			if ($scope.EMP_LIST[i].AO_CODE != undefined) {
	    				$scope.RM_LIST.push({LABEL: $scope.EMP_LIST[i].LABEL, DATA: $scope.EMP_LIST[i].EMP_ID});
//	    				$scope.rmList.push($scope.EMP_LIST[i].EMP_ID);
	    			}
	    		}
//	    		if ($scope.RM_LIST.includes(sysInfoService.getUserID())) {
//	    			$scope.inputVO.rm_id = sysInfoService.getUserID();
//	    		}
	    	}
		});
	      
	   
        //Confirm
		$scope.confirm = function(row) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			var ans = $scope.resultList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		$scope.showErrorMsg('未勾選客戶');
        		return;
        	}
        	// 總行執行強制AO移轉,不用限定一定要同分行
        	if($scope.inputVO2.process_mode != '2') {
        		var check = false;
            	angular.forEach(ans, function(row) {
    				if(row.CUST_02 && row.CUST_02 != $scope.inputVO2.new_branch_nbr)
    					check = true;
    			});
            	if(check) {
            		$scope.showErrorMsg('整批移轉客戶僅能做同分行互移，請重新調整');
            		return;
            	}
        	}
        	// 2017/6/23 移入前和移入後相同Code,應不可申請移入
        	// 2017/10/25 法金法人戶空CODE登錄無法由系統登錄，需由人工登錄。
        	// mantis 4209 : {2018/01/29 調整 "個人戶" 可以 pass 只需擋法金 "法人戶" row.CUST_ID.length < 10}
        	var check1 = false;
        	var check2 = false;
        	angular.forEach(ans, function(row) {
				if(row.AO_CODE == $scope.inputVO2.new_ao_code)
					check1 = true;
				if(!row.AO_CODE && row.CO_ACCT_YN == 'Y' && row.CUST_ID.length < 10)
					check2 = true;
			});
        	if(check1) {
        		$scope.showErrorMsg('移入前和移入後相同Code，應不可申請移入');
        		return;
        	}
        	if(check2) {
        		$scope.showErrorMsg('法金法人戶空CODE登錄無法由系統登錄，需由人工登錄');
        		return;
        	}
        	//
        	$scope.inputVO2.confirm_list = ans;
        	
        	// 2017/6/23 follow crm341
        	$scope.sendRecv("CRM331", "checkadd", "com.systex.jbranch.app.server.fps.crm331.CRM331InputVO", $scope.inputVO2,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.resultList2 == 'ERR1') {
								$scope.showErrorMsg("目前無設定"+ tota[0].body.role_name + "的總客戶數上限");
							} else if (tota[0].body.resultList2 == 'ERR2') {
								var cctext = "超過"+ tota[0].body.role_name + "的總客戶數上限，是否繼續?";
								$confirm({text: cctext}, {size: 'sm'}).then(function() {
									$scope.sendRecv("CRM331", "confirm", "com.systex.jbranch.app.server.fps.crm331.CRM331InputVO", $scope.inputVO2,
											function(tota, isError) {
												if (isError) {
													$scope.showErrorMsgInDialog(tota.body.msgData);
													return;
												}
												if (tota.length > 0) {
													$scope.showSuccessMsg("儲存成功");
								                	$scope.inquireInit();
								                	$scope.inquire();
								                };
									});
								});
							} else if (tota[0].body.resultList2 == 'ERR3') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", 已在待覆核流程中");
							} else if (tota[0].body.resultList2 == 'ERR4') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", FC客戶不可移入FCH");
							} else if (tota[0].body.resultList2 == 'ERR5') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", FCH計績 Code不可轉入FCH維護Code");
							} else if (tota[0].body.resultList2 == 'ERR6') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", 公司戶與公司負責人掛Code需一致");
							} else if (tota[0].body.resultList2 == 'ERR7') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", 該客戶屬十保監控客戶未滿六個月需客戶同意書才可移回原理專");
							} else if (tota[0].body.resultList2 == 'ERR8') { //#2225:主CODE客戶不可移轉至維護CODE
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", 主CODE客戶不可移轉至維護CODE");
							} else if (tota[0].body.resultList2 == 'ERR9') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "為NS戶，無法申請客戶移入，請洽分行內控品管科。");
							} else if (tota[0].body.resultList2 == 'ERR10') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "為已換手經營客戶未滿6個月移轉回原個金RM，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」");
							} else if (tota[0].body.resultList2 == 'ERR11') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "在ON CODE排除名單中，不可做移轉");
							} else if (tota[0].body.resultList2 == 'ERR12') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "輪調專案客戶移回原RM，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」");
							} else if (tota[0].body.resultList2 == 'ERR13') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "輪調RM名單上傳後，執行前名單中RM/客戶不可做移入申請");
							} else if (tota[0].body.resultList2 == 'ERR14') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + "輪調RM名單上傳後，執行前名單中RM/客戶不可做移入申請");
							} else if (tota[0].body.resultList2 == 'ERR15') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", RM輪調後，帶走核心客戶，一年內不得再帶走該RM轄下原分行客戶");
							} else if (tota[0].body.resultList2 == 'ERR16') {
								$scope.showErrorMsg("客戶:" + tota[0].body.resultList + ", 不可將分行客戶移入私銀CODE");
							} else {
								$scope.sendRecv("CRM331", "confirm", "com.systex.jbranch.app.server.fps.crm331.CRM331InputVO", $scope.inputVO2,
										function(tota, isError) {
											if (isError) {
												$scope.showErrorMsgInDialog(tota.body.msgData);
												return;
											}
											if (tota.length > 0) {
												$scope.showSuccessMsg("儲存成功");
							                	$scope.inquireInit();
							                	$scope.inquire();
							                };
								});
							}
						}
			});
		};
		
		$scope.goCRM610 = function(row) {
	    	$scope.CRM_CUSTVO = {
				CUST_ID :  row.CUST_ID,
				CUST_NAME :row.CUST_NAME
			}
			$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO);
	    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			$scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
	    };
		
		
});